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
import java.util.Iterator;
import java.util.Set;

import pt.ulisboa.tecnico.sdis.kerby.*;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

public class BinasAuthorizationHandler implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    String serverPw = "xm7bhuSz";

    public static final String TICKET_HEADER = "clientTicketHeader";
    public static final String TICKET_NS = "http://ticket.com";
    public static final String AUTH_HEADER = "clientAuthHeader";
    public static final String AUTH_NS = "http://auth.com";

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

    private boolean processRequest(SOAPBody sb, String email) {
        String requestName = sb.getFirstChild().getLocalName();
        String requestEmail;
        if (requestName.equals("activateUser")) {
            requestEmail = sb.getChildNodes().item(0).getChildNodes().item(0).getTextContent();
        } else if (requestName.equals("rentBina") || requestName.equals("returnBina")) {
            requestEmail = sb.getChildNodes().item(0).getChildNodes().item(1).getTextContent();
        } else {
            return true;
        }

        return email.equals(requestEmail);
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
                System.out.println("[SERVER-VALIDATION] Receiving request from " + ticketName);

                System.out.println("[SERVER-VALIDATION] Generating Ks from server password");
                Key ks = SecurityHelper.generateKeyFromPassword(serverPw);

                System.out.println("[SERVER-VALIDATION] Opening ticket using Ks");
                Ticket ticket = new Ticket(cipheredTicket, ks);

                System.out.println("[SERVER-VALIDATION] Decripting auth using Kcs");
                Auth receivedAuth = new Auth(cipheredAuth, ticket.getKeyXY());

                System.out.println("[SERVER-VALIDATION] Validating auth");
                Date validityStart = ticket.getTime1();
                Date validityEnd = ticket.getTime2();
                Date requestDate = receivedAuth.getTimeRequest();
                String ticketClient = ticket.getX();
                String authClient = receivedAuth.getX();

                if((requestDate.before(validityEnd) || requestDate.after(validityStart))
                    && ticketClient.equals(authClient) && processRequest(sb, authClient)) {
                    System.out.println("[SERVER-VALIDATION] Valid Auth from client: " + authClient);
                    System.out.println("[SERVER-VALIDATION] Recieved 'Request from client'");
                    return true;
                }else {
                    System.out.println("[SERVER-VALIDATION-ERROR] Invalid Auth");
                    System.out.println();
                    throw new RuntimeException();
                }


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
