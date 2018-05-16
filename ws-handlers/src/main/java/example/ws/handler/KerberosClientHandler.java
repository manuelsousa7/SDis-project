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

public class KerberosClientHandler  implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    //private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String kerby = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
    String client = "alice@T06.binas.org";
    String clientPw = "ySudhFL";
    String server = "binas@T06.binas.org";

    public static final String CLIENT_HEADER = "client";
    public static final String CLIENT_NS = "urn:client";
    public static final String TICKET_HEADER = "clientTicket";
    public static final String TICKET_NS = "urn:ticket";
    public static final String AUTH_HEADER = "clientAuth";
    public static final String AUTH_NS = "urn:autn";

    public static final String TOKEN = "client-handler";

    public static final String userEmail = "invalid@email";

    //
    // Handler interface implementation
    //

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
                SessionKeyAndTicketView view = cli.requestTicket(client, server, randomGenerator.nextLong(), 30);

                System.out.println("[CLIENT-INFO] Generating Kc from client password");
                Key kc = SecurityHelper.generateKeyFromPassword(clientPw);

                CipheredView cipheredSessionKey = view.getSessionKey();
                CipheredView cipheredTicket = view.getTicket();

                System.out.println("[CLIENT-INFO] Obtaining sessionKey and Kcs using Kc");
                SessionKey sessionKey = new SessionKey(cipheredSessionKey, kc);
                Key clientServerKey = sessionKey.getKeyXY();

                System.out.println("[CLIENT-INFO] Generating auth ciphered with Kcs");
                Date timeRequest = new Date();
                Auth auth = new Auth(client, timeRequest);
                CipheredView cipheredAuth = auth.cipher(clientServerKey);

                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPBody sb = se.getBody();
                if (sb == null) {
                    sb = se.addBody();
                }

                // add header element (name, namespace prefix, namespace)
                Name clientName = se.createName(CLIENT_HEADER, "e", CLIENT_NS);
                SOAPBodyElement element = sb.addBodyElement(clientName);

                // add ticket and auth values
                CipherClerk clerk = new CipherClerk();
                org.w3c.dom.Node ticketNode = clerk.cipherToXMLNode(cipheredTicket, "clientTicket");
                Name ticketName = se.createName(TICKET_HEADER, "e", TICKET_NS);
                element.addAttribute(ticketName, ticketNode.getNodeValue());

                org.w3c.dom.Node authNode = clerk.cipherToXMLNode(cipheredAuth, client);
                Name authName = se.createName(AUTH_HEADER, "e", AUTH_NS);
                element.addAttribute(authName, authNode.getNodeValue());

                System.out.println("[INFO] Kerby Url: " + kerby);
                System.out.println("[INFO] Client email: " + client);
                System.out.println("[INFO] Server Url: " + server);
                System.out.println();

                return true;
            } catch (BadTicketRequest_Exception e) {
                System.out.printf("Bad ticket %s%n", e);
            } catch (NoSuchAlgorithmException e) {
                System.out.printf("No such algorithm %s%n", e);
            } catch (InvalidKeySpecException e) {
                System.out.printf("Invalid key specification %s%n", e);
            } catch (JAXBException e) {
                System.out.printf("JAXB Exception %s%n", e);
            } catch (KerbyClientException e) {
                System.out.printf("Received kerby client exception %s%n", e);
            } catch (KerbyException e) {
                System.out.printf("Received kerby exception %s%n", e);
            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }
            finally {
                return false;
            }
        }
        else {
            //TODO: ?
            return true;
        }
    }
}
