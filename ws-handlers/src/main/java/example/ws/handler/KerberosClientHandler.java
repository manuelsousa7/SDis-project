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
import java.util.*;

import pt.ulisboa.tecnico.sdis.kerby.*;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class KerberosClientHandler  implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    private String kerby = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
    private static String clientPw = "ySudhFL";
    private String server = "binas@T06.binas.org";

    private static int ticketDuration = 30;

    private SessionKeyAndTicketView requestedTicket = null;
    private Date timeLimit = null;

    private CipheredView cipheredSessionKey = null;
    private CipheredView cipheredTicket = null;

    private Date sentTimeRequest = null;

    private Key kc = null;
    private Key clientServerKey = null;

    private static final String TICKET_HEADER = "clientTicketHeader";
    private static final String TICKET_NS = "http://ticket.com";
    private static final String AUTH_HEADER = "clientAuthHeader";
    private static final String AUTH_NS = "http://auth.com";
    private static final String TREQ_HEADER = "clientHeader";
    private static final String TREQ_HEADER_NS = "http://clientHeader.com";

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

    public static void setPassword(String newPassword) {
        clientPw = newPassword;
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

                if (kc == null) {
                    System.out.println("[CLIENT-INFO] Generating Kc from client password");
                    kc = SecurityHelper.generateKeyFromPassword(clientPw);
                }
                else {
                    System.out.println("[CLIENT-INFO] Using generated Kc");
                }

                SecureRandom randomGenerator = new SecureRandom();
                if (requestedTicket == null || timeLimit.before(new Date())) {
                    System.out.println("[CLIENT-INFO] Requesting ticket from Kerby");
                    requestedTicket = cli.requestTicket(userEmail, server, randomGenerator.nextLong(), ticketDuration);
                    timeLimit = new Date();
                    timeLimit.setTime(timeLimit.getTime()+( (ticketDuration - 2)*1000) );
                    System.out.println("[CLIENT-INFO] Time limit:" + timeLimit);

                    cipheredSessionKey = requestedTicket.getSessionKey();
                    cipheredTicket = requestedTicket.getTicket();
                    System.out.println("[CLIENT-INFO] Obtaining sessionKey and Kcs using Kc");
                    SessionKey sessionKey = new SessionKey(cipheredSessionKey, kc);
                    clientServerKey = sessionKey.getKeyXY();
                    smc.put("kcs",clientServerKey);
                }
                else {
                    System.out.println("[CLIENT-INFO] Already own valid ticket");
                }


                if (clientServerKey == null) {

                }


                System.out.println("[CLIENT-INFO] Generating auth ciphered with Kcs");
                sentTimeRequest = new Date();
                Auth auth = new Auth(userEmail, sentTimeRequest);
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

                System.out.println("[INFO] Kerby Url: " + kerby);
                System.out.println("[INFO] Client email: " + userEmail);
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
            try {
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
                    System.out.println("Body not found.");
                    return true;
                }

                System.out.println("[CLIENT-RECEIVE] Opening header with Ciphered Response");

                Name treqName = se.createName(TREQ_HEADER, "e", TREQ_HEADER_NS);
                Iterator it = sh.getChildElements(treqName);
                if (!it.hasNext()) {
                    System.out.printf("TREQ element %s not found.%n", TREQ_HEADER);
                    return true;
                }
                SOAPElement treqElement = (SOAPElement) it.next();
                String treqValue = treqElement.getValue();
                byte[] treqBytes = parseBase64Binary(treqValue);

                CipheredView cipheredResponse = new CipheredView();
                cipheredResponse.setData(treqBytes);

                Auth receivedAuth = new Auth(cipheredResponse, clientServerKey);
                Date timeRequest = receivedAuth.getTimeRequest();

                System.out.println("[CLIENT-RECEIVE] Success opening ciphered response");

                if (timeRequest.equals(sentTimeRequest)) {
                    System.out.println("[CLIENT-RECEIVE] Valid date");
                    return true;
                }
                else {
                    System.out.println("[CLIENT-RECEIVE] ERROR: Invalid date");
                    throw new RuntimeException();
                }
            } catch (KerbyException e) {
                System.out.printf("Received Kerby Exception %s%n", e);
            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }
        }
        return true;
    }
}
