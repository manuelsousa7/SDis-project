package example.ws.handler;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.PrintStream;
import java.security.Key;
import java.util.Date;
import java.util.Set;

public class KerberosServerHandler implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    //private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String serverPw = "xm7bhuSz";

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
        if (!outbound) {
            CipheredView cipheredTicket = (CipheredView) smc.get(KerberosClientHandler.ticketCiphered);
            CipheredView cipheredAuth = (CipheredView) smc.get(KerberosClientHandler.authCiphered);

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
        }
    }

}
