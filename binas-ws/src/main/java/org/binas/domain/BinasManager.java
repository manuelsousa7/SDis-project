package org.binas.domain;

import java.util.*;

import org.binas.exceptions.ExceptionManager;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.CoordinatesView;
import org.binas.ws.*;
//import org.binas.ws.EmptyStation_Exception;


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

    public ArrayList<StationClient> listStations(int k, CoordinatesView coordenadas) {
        ArrayList<StationClient> Stations = new ArrayList<StationClient>();
        SortedMap<Float, StationClient> Distances = new TreeMap<>();

        for (Map.Entry<String, StationClient> station : connectedStations.entrySet()) {
            CoordinatesView coord = station.getValue().getInfo().getCoordinate();
            float DistanceX = coord.getX() - coordenadas.getX();
            float DistanceY = coord.getY() - coordenadas.getY();
            Distances.put(Math.abs((float)Math.sqrt(DistanceX*DistanceX + DistanceY*DistanceY)), station.getValue());
        }

        int instanceCounter = 0;

        for (Map.Entry<Float, StationClient> distance : Distances.entrySet()) {
            StationClient newStation = distance.getValue();
            Stations.add(newStation);
            instanceCounter++;
            if (instanceCounter >= k) {
                break;
            }
        }

        return Stations;
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
