package example.ws.handler;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import pt.ulisboa.tecnico.sdis.kerby.*;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class KerberosClientHandler  implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    String kerby = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
    String client = "alice@T06.binas.org";
    String clientPw = "ySudhFL";
    String server = "binas@T06.binas.org";

    SessionKeyAndTicketView requestedTicket = null;

    public static final String CLIENT_HEADER = "clientHeader";
    public static final String CLIENT_HEADER_NS = "http://clientHeader.com";
    public static final String CLIENT_BODY = "clientBody";
    public static final String CLIENT_BODY_NS = "http://clientBody.com";
    public static final String TICKET_HEADER = "clientTicketHeader";
    public static final String TICKET_NS = "http://ticket.com";
    public static final String AUTH_HEADER = "clientAuthHeader";
    public static final String AUTH_NS = "http://auth.com";

    public static String userEmail = "invalid@email";

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
                KerbyClient cli = new KerbyClient(kerby);
                System.out.println("[CLIENT-INFO] Connection to Kerby Successfull");

                System.out.println("[CLIENT-INFO] Requesting ticket from Kerby");
                SecureRandom randomGenerator = new SecureRandom();
                requestedTicket = cli.requestTicket(client, server, randomGenerator.nextLong(), 30);

                System.out.println("[CLIENT-INFO] Generating Kc from client password");
                Key kc = SecurityHelper.generateKeyFromPassword(clientPw);

                CipheredView cipheredSessionKey = requestedTicket.getSessionKey();
                CipheredView cipheredTicket = requestedTicket.getTicket();

                System.out.println("[CLIENT-INFO] Obtaining sessionKey and Kcs using Kc");
                SessionKey sessionKey = new SessionKey(cipheredSessionKey, kc);
                Key clientServerKey = sessionKey.getKeyXY();

                System.out.println("[CLIENT-INFO] Generating auth ciphered with Kcs");
                Date timeRequest = new Date();
                Auth auth = new Auth(client, timeRequest);
                CipheredView cipheredAuth = auth.cipher(clientServerKey);

                //----------------------------------------------------------------------

                SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader sh = se.getHeader();
                SOAPBody sb = se.getBody();

                if (sh == null) {
                    sh = se.addHeader();
                }
                if (sb == null) {
                    sb = se.addBody();
                }

                CipherClerk clerk = new CipherClerk();

                Name ticketName = se.createName(TICKET_HEADER, "e", TICKET_NS);
                SOAPHeaderElement ticketElement = sh.addHeaderElement(ticketName);

                byte[] ticketBytes = cipheredTicket.getData();
                String cipherTicketText = printBase64Binary(ticketBytes);
                ticketElement.addTextNode(cipherTicketText);

                Name authName = se.createName(AUTH_HEADER, "e", AUTH_NS);
                SOAPHeaderElement authElement = sh.addHeaderElement(authName);

                byte[] authBytes = cipheredAuth.getData();
                String cipherAuthText = printBase64Binary(authBytes);
                authElement.addTextNode(cipherAuthText);

                Name clientName = se.createName(CLIENT_BODY, "e", CLIENT_BODY_NS);
                SOAPBodyElement bodyElement = sb.addBodyElement(clientName);
                bodyElement.addTextNode(userEmail);

                System.out.println("[INFO] Kerby Url: " + kerby);
                System.out.println("[INFO] Client email: " + client);
                System.out.println("[INFO] Server Url: " + server);
                System.out.println();

            } catch (BadTicketRequest_Exception e) {
                System.out.printf("Bad ticket %s%n", e);
            } catch (NoSuchAlgorithmException e) {
                System.out.printf("No such algorithm %s%n", e);
            } catch (InvalidKeySpecException e) {
                System.out.printf("Invalid key specification %s%n", e);
            } catch (KerbyClientException e) {
                System.out.printf("Received kerby client exception %s%n", e);
            } catch (KerbyException e) {
                System.out.printf("Received kerby exception %s%n", e.getMessage());
            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }
        }
        else {
            //TODO: ?
        }
        return true;
    }
}
