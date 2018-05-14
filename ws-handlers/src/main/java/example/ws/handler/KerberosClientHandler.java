package example.ws.handler;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

public class KerberosClientHandler  implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    //private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String kerby = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
    String client = "alice@T06.binas.org";
    String clientPw = "ySudhFL";
    String server = "binas@T06.binas.org";

    public static final String userEmail = "invalid@email";
    public static final String keyAndTicket = "client.session.ticket.property";
    public static final String ticketCiphered = "client.ciphered.ticket.property";
    public static final String authCiphered = "client.ciphered.auth.property";

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

    /**
     * The handleMessage method is invoked for normal processing of inbound and
     * outbound messages.
     */
    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        logSOAPMessage(smc, System.out);
        return true;
    }

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
    private void logSOAPMessage(SOAPMessageContext smc, PrintStream out) {

        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            // outbound message
            try {
                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                // add header element (name, namespace prefix, namespace)
                Name name = se.createName("myHeader", "d", "http://demo");
                SOAPHeaderElement element = sh.addHeaderElement(name);

                System.out.println("[INFO] Kerby Url: " + kerby);
                System.out.println("[INFO] Client email: " + client);
                System.out.println("[INFO] Server Url: " + server);
                System.out.println();

                // load configuration properties
                try {
                    InputStream inputStream = KerbyExperiment.class.getClassLoader().getResourceAsStream("config.properties");
                    // variant for non-static methods:
                    // InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");

                    Properties properties = new Properties();
                    properties.load(inputStream);

                    System.out.printf("Loaded %d properties%n", properties.size());

                } catch (IOException e) {
                    System.out.printf("Failed to load configuration: %s%n", e);
                }

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

                smc.put(keyAndTicket, view);
                smc.put(ticketCiphered, cipheredTicket);
                smc.put(authCiphered, cipheredAuth);
            } catch (SOAPException e) {
                System.out.printf("Failed to add SOAP header because of %s%n", e);
            }
        }
        else {
            //TODO: ?
        }
    }
}
