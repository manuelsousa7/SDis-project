package example.ws.handler;

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
import java.util.Iterator;
import java.util.Set;

import pt.ulisboa.tecnico.sdis.kerby.*;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    //private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String serverPw = "xm7bhuSz";

    public static final String CLIENT_HEADER = "client";
    public static final String CLIENT_NS = "urn:client";
    public static final String TICKET_HEADER = "clientTicket";
    public static final String TICKET_NS = "urn:ticket";
    public static final String AUTH_HEADER = "clientAuth";
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
        if (outbound) {

        }
        else {
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

                Name ticketName = se.createName(TICKET_HEADER, "e", TICKET_NS);
                String ticketValue = sh.getAttributeValue(ticketName);

                Name authName = se.createName(AUTH_HEADER, "e", AUTH_NS);
                String authValue = sh.getAttributeValue(authName);

                CipherClerk clerk = new CipherClerk();
                CipheredView cipheredTicket = clerk.cipherBuild(ticketValue);
                CipheredView cipheredAuth = clerk.cipherFromXMLNode(authValue);

                System.out.println("[SERVER-INFO] Generating Ks from server password");
                Key ks = SecurityHelper.generateKeyFromPassword(serverPw);

                System.out.println("[SERVER-INFO] Opening ticket using Ks");
                Ticket ticket = new Ticket(cipheredTicket, ks);

                System.out.println("[SERVER-INFO] Obtaining Kcs from ticket");
                Key clientServerKey = ticket.getKeyXY();

                System.out.println("[SERVER-INFO] Decripting auth using Kcs");
                Auth recievedAuth = new Auth(cipheredAuth, clientServerKey);

                System.out.println("[SERVER-INFO] Validating auth");
                Date validityStart = ticket.getTime1();
                Date validityEnd = ticket.getTime2();
                Date requestDate = recievedAuth.getTimeRequest();
                String ticketClient = ticket.getX();
                String authClient = recievedAuth.getX();

                if((requestDate.before(validityEnd) || requestDate.after(validityStart))
                        && ticketClient.equals(authClient)  ) {
                    System.out.println("[SERVER-INFO] Valid Auth from client: " + authClient);
                    System.out.println("[SERVER-INFO] Recieved 'Request from client'");
                }else {
                    System.out.println("[SERVER-ERROR] Invalid Auth");
                    System.out.println("[INFO] ------------------------------------------------------------------------");
                    System.out.println("[SERVER-END] Experiment with Kerberos server-side processing END");
                    System.out.println("[INFO] ------------------------------------------------------------------------");
                    System.out.println();
                    return true;
                }

                System.out.println("[SERVER-INFO] Generating and encrypting Treq with Kcs");
                Date Tresp = new Date();
                Auth responseAuth = new Auth("", Tresp);
                CipheredView cipheredResponse = recievedAuth.cipher(clientServerKey);

                return true;

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
    }

}
