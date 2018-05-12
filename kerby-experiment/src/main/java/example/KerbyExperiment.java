package example;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;


import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;


public class KerbyExperiment {

    public static void main(String[] args) throws Exception {
    	
    	String kerby = args[0];
    	String client = args[1];
    	String clientPw = args[2];
    	String server = args[3];
    	String serverPw = args[4];
    	
    	System.out.println("");
    	
    	System.out.println("[INFO] Kerby Url: " + kerby);
    	System.out.println("[INFO] Client Id: " + client);
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

        System.out.println();

        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[CLIENT-START] Experiment with Kerberos client-side processing START");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        
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
		
		System.out.println("[SERVER-INFO] Sending Ticket + Auth + Request = 'Request from client' to Server");
		
		System.out.println("[INFO] ------------------------------------------------------------------------");
		System.out.println("[CLIENT-END] Experiment with Kerberos client-side processing END");
		System.out.println("[INFO] ------------------------------------------------------------------------");
		
		System.out.println();
	
		System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[SERVER-START] Experiment with Kerberos server-side processing START");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        
        System.out.println("[SERVER-INFO] Generating Ks from server password");
        Key ks = SecurityHelper.generateKeyFromPassword(serverPw);
        
        System.out.println("[SERVER-INFO] Opening ticket using Ks");
        Ticket ticket = new Ticket(cipheredTicket, ks);
        
        System.out.println("[SERVER-INFO] Obtaining Kcs from ticket");
        clientServerKey = ticket.getKeyXY();
        
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
        	return;
        }
        
        System.out.println("[SERVER-INFO] Generating and encrypting Treq with Kcs");
        Date Tresp = new Date();
        Auth responseAuth = new Auth("", Tresp);
        CipheredView cipheredResponse = recievedAuth.cipher(clientServerKey);
        
        System.out.println("[SERVER-INFO] Sending 'Response from server'");
        
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[SERVER-END] Experiment with Kerberos server-side processing END");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println();
    }
}
