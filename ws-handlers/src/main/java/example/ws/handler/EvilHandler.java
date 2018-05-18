package example.ws.handler;

import org.w3c.dom.Node;
import pt.ulisboa.tecnico.sdis.kerby.*;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClientException;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.PrintStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Set;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class EvilHandler implements SOAPHandler<SOAPMessageContext> {

    /** Date formatter used for outputting time stamp in ISO 8601 format. */
    String kerby = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
    String fakeClient = "evilPotato@T06.binas.org";
    String server = "binas@T06.binas.org";

    public static final String CLIENT_BODY = "clientBody";
    public static final String CLIENT_BODY_NS = "http://clientBody.com";


    private void changeRequest(SOAPBody sb) {
        String requestName = sb.getFirstChild().getLocalName();
        if (requestName.equals("activateUser")) {
            Node textNode = sb.getChildNodes().item(0).getChildNodes().item(0);
            textNode.setTextContent("victimEmail@victim.com");
        } else if (requestName.equals("rentBina") || requestName.equals("returnBina")) {
            Node textNode = sb.getChildNodes().item(0).getChildNodes().item(1);
            textNode.setTextContent("victimEmail@victim.com");
        }
        return;
    }


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

                SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader sh = se.getHeader();
                SOAPBody sb = se.getBody();

                if (sb == null) {
                    sb = se.addBody();
                }

                changeRequest(sb);

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
