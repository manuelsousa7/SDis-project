package example.ws.handler;

import org.w3c.dom.NodeList;

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
import java.util.Date;
import java.util.Set;

import pt.ulisboa.tecnico.sdis.kerby.*;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    String server = "binas@T06.binas.org";
    String serverPw = "xm7bhuSz";

    public static final String SERVER_HEADER = "clientHeader";
    public static final String SERVER_NS = "http://clientHeader.com";
    public static final String AUTH_HEADER = "http://clientAuthHeader.com";
    public static final String AUTH_NS = "urn:autn";

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
        if (!outbound) {
            try {
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPBody sb = se.getBody();

                // check header
                if (sb == null) {
                    System.out.println("Body not found.");
                    return true;
                }

                NodeList nodes = sb.getChildNodes();
                CipherClerk clerk = new CipherClerk();
                CipheredView cipheredTicket = clerk.cipherFromXMLNode(nodes.item(0));
                CipheredView cipheredAuth = clerk.cipherFromXMLNode(nodes.item(1));
                String clientName = nodes.item(1).getNodeName();
                System.out.println("[SERVER-VALIDATION] Receiving request from " + clientName);

                System.out.println("[SERVER-VALIDATION] Generating Ks from server password");
                Key ks = SecurityHelper.generateKeyFromPassword(serverPw);

                System.out.println("[SERVER-VALIDATION] Opening ticket using Ks");
                Ticket ticket = new Ticket(cipheredTicket, ks);

                System.out.println("[SERVER-VALIDATION] Decripting auth using Kcs");
                Auth recievedAuth = new Auth(cipheredAuth, ticket.getKeyXY());

                System.out.println("[SERVER-VALIDATION] Validating auth");
                Date validityStart = ticket.getTime1();
                Date validityEnd = ticket.getTime2();
                Date requestDate = recievedAuth.getTimeRequest();
                String ticketClient = ticket.getX();
                String authClient = recievedAuth.getX();

                if((requestDate.before(validityEnd) || requestDate.after(validityStart))
                    && ticketClient.equals(authClient) && authClient.equals(clientName)) {
                    System.out.println("[SERVER-VALIDATION] Valid Auth from client: " + authClient);
                    System.out.println("[SERVER-VALIDATION] Recieved 'Request from client'");
                    return true;
                }else {
                    System.out.println("[SERVER-VALIDATION-ERROR] Invalid Auth");
                    System.out.println();
                    return false;
                }


            } catch (JAXBException e) {
                System.out.printf("JAXB Exception %s%n", e);
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
