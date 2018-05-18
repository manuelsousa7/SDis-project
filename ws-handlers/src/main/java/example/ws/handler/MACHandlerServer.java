package example.ws.handler;

import org.binas.security.domain.SecurityMagic;
import org.binas.security.exception.SecurityMagicException;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.PrintStream;
import java.security.Key;
import java.util.Iterator;
import java.util.Set;

public class MACHandlerServer implements SOAPHandler<SOAPMessageContext> {

    public static final String MAC_HEADER = "MACHeader";
    public static final String MAC_NS = "http://macheader.com";

    private static Key kcs = null;

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

                if (sh == null) {
                    sh = se.addHeader();
                }
                if (sb == null) {
                    sb = se.addBody();
                }
                if ( kcs == null){
                    return true;
                }
                SecurityMagic sm = new SecurityMagic(sb.getTextContent(),kcs);

                Name mac = se.createName(MAC_HEADER, "m", MAC_NS);
                SOAPHeaderElement ticketElement = sh.addHeaderElement(mac);
                System.out.println(sm.getMAC64());
                System.out.println(sm.getMAC());
                ticketElement.addTextNode(sm.getMAC64());

            } catch (Exception e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }
        } else {
            try{
                kcs = (Key) smc.get("kcs");
                if (kcs == null) {
                    System.out.println("clientServerKey is invalid!");
                    return true;
                }
                else{
                    SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
                    SOAPHeader sh = se.getHeader();
                    SOAPBody sb = se.getBody();

                    if (sh == null) {
                        sh = se.addHeader();
                    }
                    if (sb == null) {
                        sb = se.addBody();
                    }
                    Name mac = se.createName(MAC_HEADER, "m", MAC_NS);
                    Iterator it = sh.getChildElements(mac);
                    if (!it.hasNext()) {
                        System.out.printf("Auth element %s not found.%n", MAC_HEADER);
                        return true;
                    }
                    SOAPElement macElement = (SOAPElement) it.next();
                    String macB64Soap = macElement.getValue();
                    String bodySoap = sb.getTextContent();

                    SecurityMagic sm = new SecurityMagic(bodySoap,kcs);
                    if(!sm.getMAC64().equals(macB64Soap)){
                        System.out.println("MAC Verification: UNSUCESS!");
                        System.out.println("Expected MAC: " + sm.getMAC64());
                        System.out.println("MAC Received: " + macB64Soap);
                        throw new SecurityMagicException("You have been hacked");
                    } else {
                        System.out.println("MAC Verification: SUCESS!");
                    }
                }
            }catch (SecurityMagicException sme) {
                throw sme;
            } catch (Exception e) {
                System.out.printf("No such algorithm %s%n");
            }
        }
        return true;
    }
}
