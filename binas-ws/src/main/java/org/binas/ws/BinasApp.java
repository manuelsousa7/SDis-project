package org.binas.ws;

import org.binas.domain.BinasManager;
import org.binas.station.ws.cli.StationClientApp;

public class BinasApp {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + StationClientApp.class.getName() + "uddiURL wsName");
			return;
		}
		
		String uddiUrl = null;
		String stationPrefix = null;
		uddiUrl = args[0];
		stationPrefix= args[1];
		
		System.out.println(BinasApp.class.getSimpleName() + " running");
		BinasManager manager = BinasManager.getInstance();
		if(uddiUrl != null) {
			manager.PopulateStations(uddiUrl, stationPrefix);			
		}
	}

}