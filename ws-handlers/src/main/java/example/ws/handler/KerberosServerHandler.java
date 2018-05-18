package example.ws.handler;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.PrintStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import org.w3c.dom.NodeList;
import pt.ulisboa.tecnico.sdis.kerby.*;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    String server = "binas@T06.binas.org";
    String serverPw = "xm7bhuSz";

    public static final String SERVER_HEADER = "clientHeader";
    public static final String SERVER_HEADER_NS = "http://clientHeader.com";
    public static final String TICKET_HEADER = "clientTicketHeader";
    public static final String TICKET_NS = "http://ticket.com";
    public static final String AUTH_HEADER = "clientAuthHeader";
    public static final String AUTH_NS = "http://auth.com";

    Key clientServerKey = null;

    /**
     * Gets the header blocks that can be processed by this Handler instance. If
     * null, processes all.
     */
    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private void logSOAPMessage(SOAPMessageContext smc, PrintStream out) {}

    /** The handleFault method is invoked for fault message processing. */
    @Override
    public boolean handleFault(SOAPMessageContext smc) {
        logSOAPMessage(smc, System.out);
        return true;
    }

    /**
     * Called at the conclusion of a message exchange pattern just prior to the
     * JAX-WS runtime dispatching a message, fault or exception.
     */
    @Override
    public void close(MessageContext messageContext) {
        // nothing to clean up
    }

    /**
     * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
     * outgoing or incoming message. Write the SOAP message to the print stream. The
     * writeTo() method can throw SOAPException or IOException.
     */
    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            try {
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                System.out.println("[SERVER-INFO] Generating and encrypting Treq with Kcs");
                if (clientServerKey == null) {
                    System.out.println("clientServerKey is invalid!");
                    return true;
                }
                Date Tresp = new Date();
                Auth responseAuth = new Auth(server, Tresp);
                CipheredView cipheredResponse = responseAuth.cipher(clientServerKey);

                Name ticketName = se.createName(SERVER_HEADER, "e", SERVER_HEADER_NS);
                SOAPHeaderElement ticketElement = sh.addHeaderElement(ticketName);

                byte[] ticketBytes = cipheredResponse.getData();
                String cipherTicketText = printBase64Binary(ticketBytes);
                ticketElement.addTextNode(cipherTicketText);

            } catch (KerbyException e) {
                System.out.printf("Received kerby exception %s%n", e);
            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }
        }
        else {
            try {
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();
                SOAPBody sb = se.getBody();

                // check header
                if (sh == null) {
                    System.out.println("Body not found.");
                    return true;
                }

                if (sb == null) {
                    System.out.println("Body not found.");
                    return true;
                }

                Name ticketName = se.createName(TICKET_HEADER, "e", TICKET_NS);
                Iterator it = sh.getChildElements(ticketName);
                if (!it.hasNext()) {
                    System.out.printf("Ticket element %s not found.%n", TICKET_HEADER);
                    return true;
                }
                SOAPElement ticketElement = (SOAPElement) it.next();

                Name authName = se.createName(AUTH_HEADER, "e", AUTH_NS);
                it = sh.getChildElements(authName);
                if (!it.hasNext()) {
                    System.out.printf("Auth element %s not found.%n", AUTH_HEADER);
                    return true;
                }
                SOAPElement authElement = (SOAPElement) it.next();

                String ticketValue = ticketElement.getValue();
                String authValue = authElement.getValue();

                byte[] ticketBytes = parseBase64Binary(ticketValue);
                byte[] authBytes = parseBase64Binary(authValue);

                CipherClerk clerk = new CipherClerk();
                CipheredView cipheredTicket = new CipheredView();
                cipheredTicket.setData(ticketBytes);
                CipheredView cipheredAuth = new CipheredView();
                cipheredAuth.setData(authBytes);
                System.out.println("[SERVER-INFO] Receiving request from " + ticketName);

                System.out.println("[SERVER-INFO] Generating Ks from server password");
                Key ks = SecurityHelper.generateKeyFromPassword(serverPw);

                System.out.println("[SERVER-INFO] Opening ticket using Ks");
                Ticket ticket = new Ticket(cipheredTicket, ks);

                System.out.println("[SERVER-INFO] Decripting auth using Kcs");
                clientServerKey = ticket.getKeyXY();

            } catch (NoSuchAlgorithmException e) {
                System.out.printf("No such algorithm %s%n", e);
            } catch (InvalidKeySpecException e) {
                System.out.printf("Invalid key specification %s%n", e);
            } catch (KerbyException e) {
                System.out.printf("Received kerby exception %s%n", e);
            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }
        }
        return true;
    }

}
