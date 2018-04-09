package org.binas.domain;

import java.util.HashMap;

import org.binas.station.ws.CoordinatesView;
import org.binas.station.ws.cli.StationClient;

public class BinasManager {
	
	HashMap<CoordinatesView, StationClient> connectedStations = new HashMap<CoordinatesView, StationClient>();
	HashMap<String, User> users = new HashMap<>();

	private BinasManager() {
	}

	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public User getUserByEmail(String email) {
	    return users.get(email);
    }
	
	public void PopulateStations(String uddiUrl,String stationPrefix) {
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
	
	// TODO

}
