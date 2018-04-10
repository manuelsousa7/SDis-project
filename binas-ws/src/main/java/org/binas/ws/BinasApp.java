package org.binas.ws;

import org.binas.domain.BinasManager;
import org.binas.station.ws.cli.StationClientApp;

public class BinasApp {

	public static void main(String[] args) throws Exception {
		if (args.length != 4) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + StationClientApp.class.getName() + "uddiURL wsName");
			return;
		}
		String uddiUrl = args[0];
		String stationPrefix = args[1];
		String wsName = args[2];
		String wsURL = args[3];
				
		BinasManager manager = BinasManager.getInstance();
		manager.PopulateStations(uddiUrl, stationPrefix);
		BinasEndpointManager endpoint = new BinasEndpointManager(uddiUrl, wsName, wsURL);
		System.out.println(BinasApp.class.getSimpleName() + " running");
		try {
			endpoint.start();
			endpoint.awaitConnections();
		}finally {
			endpoint.stop();
		}
	}

}