package org.binas.domain;

import java.util.HashMap;

import org.binas.exceptions.ExceptionManager;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.ws.FullStation_Exception;
//import org.binas.ws.EmptyStation_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.UserNotExists_Exception;

public class BinasManager {

	private HashMap<String, StationClient> connectedStations = new HashMap<String, StationClient>();
	private HashMap<String, User> users = new HashMap<>();

	private BinasManager() {
	}

	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private User getUserByEmail(String email) throws UserNotExists_Exception {
		
		User user = users.get(email);
		if(user==null) {
			ExceptionManager.userNotFound(email);
		}
		return user;
    }

	private StationClient getStation(String stationId) throws InvalidStation_Exception {
		StationClient station = this.connectedStations.get(stationId);
		if(station==null) {
			ExceptionManager.stationNotFound(stationId);
		}
		return station;
	}
	
	public void PopulateStations(String uddiUrl,String stationPrefix) {
		Boolean hasMore = true;
		int currentStation = 1;
		while(hasMore) {
			StationClient station = null;
			String stationName = stationPrefix + currentStation;
			try {
				station = new StationClient(uddiUrl, stationName);
				System.out.printf("[INFO] Created client using UDDI at %s for server with name %s%n", uddiUrl, stationName);
				String stationId = station.getInfo().getId();
				connectedStations.put(stationId, station);
			}catch (Exception se) {
				hasMore = false;
			}
			currentStation += 1;
		}
	}
	
	public int getUserCredit(String email) throws UserNotExists_Exception {
		return getUserByEmail(email).getCredit();
	}

	public void getBina(String stationId,String email) throws InvalidStation_Exception,
																UserNotExists_Exception,
																AlreadyHasBina_Exception/*,
																EmptyStation_Exception*/ {
		StationClient station = getStation(stationId);
		User user = getUserByEmail(email);
		if (user.hasBina()) {
			ExceptionManager.alreadyHasBina();
		}
		try {
			station.getBina();
		} catch (NoBinaAvail_Exception e) {
			ExceptionManager.emptyStation();
		}
	}
	
	public void ReturnBina(String stationId,String email) throws InvalidStation_Exception, UserNotExists_Exception, NoBinaRented_Exception, FullStation_Exception {
		StationClient station = getStation(stationId);
		User user = getUserByEmail(email);
		if (!user.hasBina()) {
			ExceptionManager.noBinaRented();
		}
		try {
			station.returnBina();
		} catch (NoSlotAvail_Exception e) {
			ExceptionManager.fullStation();
		}
	}
	
	// TODO

}
