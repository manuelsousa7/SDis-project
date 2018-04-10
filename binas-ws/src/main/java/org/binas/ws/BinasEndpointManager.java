package org.binas.ws;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

public class BinasEndpointManager {
	private final String wsURL;
	private final String uddiURL;
	private final String wsName;
	private boolean verbose = true;
	private static UDDINaming uddiNaming = null;
	private Endpoint endpoint = null;
	BinasPortImpl portImpl = new BinasPortImpl(this);
	
	public BinasEndpointManager(String uddiURL,String wsName,String wsURL) {
		this.wsURL = wsURL;
		this.uddiURL = uddiURL;
		this.wsName = wsName;
	}
	
	public void start() throws Exception {
		try {
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		publishToUDDI();
	}
	
	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}
	
	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
		unpublishFromUDDI();
	}
	
	public String getUddiURL() {
		return uddiURL;
	}

	public String getWsName() {
		return wsName;
	}
	public String getWsURL() {
		return wsURL;
	}
	
	/* UDDI */
	private void publishToUDDI() throws Exception {
		System.out.printf("Publishing '%s' to UDDI at %s%n",wsName, uddiURL);
		uddiNaming = new UDDINaming(uddiURL);
		uddiNaming.rebind(wsName, wsURL);
	}

	private void unpublishFromUDDI() throws Exception{
		if (uddiNaming!=null) {
			uddiNaming.unbind(wsName);
			System.out.printf("Deleted '%s' from UDDI%n", wsName);
		}
	}
	
}
