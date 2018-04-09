package org.binas.ws;

import java.util.HashMap;

import org.binas.station.ws.CoordinatesView;
import org.binas.station.ws.cli.StationClient;
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
		
		HashMap<CoordinatesView, StationClient> connectedStations = new HashMap<CoordinatesView, StationClient>();
		
		if(uddiUrl != null) {
			Boolean hasMore = true;
			int currentStation = 1;
			while(hasMore) {
				StationClient client = null;
				String stationName = stationPrefix + currentStation;
				try {
					client = new StationClient(uddiUrl, stationName);
					System.out.printf("[INFO] Created client using UDDI at %s for server with name %s%n", uddiUrl, stationName);
					CoordinatesView coordinates = client.getInfo().getCoordinate();
					connectedStations.put(coordinates, client);
				}catch (Exception se) {
					hasMore = false;
				}
				currentStation += 1;
			}
			
		}
	}

}