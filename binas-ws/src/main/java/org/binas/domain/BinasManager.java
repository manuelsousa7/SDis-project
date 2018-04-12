package org.binas.domain;

import com.google.common.collect.*;
import org.binas.exceptions.ExceptionManager;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.ws.*;

import java.util.*;

public class BinasManager {

	private HashMap<String, StationClient> connectedStations = new HashMap<String, StationClient>();
	private HashMap<String, User> users = new HashMap<String,User>();

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

	public StationView getInfoStation(String stationId) throws InvalidStation_Exception{
		StationClient station = getStation(stationId);
		StationView out = new StationView();
		out.setAvailableBinas(station.getInfo().getAvailableBinas());
		out.setFreeDocks(station.getInfo().getFreeDocks());
		out.setCapacity(station.getInfo().getCapacity());
		CoordinatesView coordinates = new CoordinatesView();
		coordinates.setX(station.getInfo().getCoordinate().getX());
		coordinates.setY(station.getInfo().getCoordinate().getY());
		out.setCoordinate(coordinates);
		out.setId(stationId);
		out.setTotalGets(station.getInfo().getTotalGets());
		out.setTotalReturns(station.getInfo().getTotalReturns());
		return out;
	}


	public synchronized UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception{
		if(email == null){
			ExceptionManager.invalidEmail(email);
		} else {
			Boolean isValidEmail = email.matches("^(.+)@(.+)$");
			if(!isValidEmail){
				ExceptionManager.invalidEmail(email);
			}
			if(users.containsKey(email)){
				ExceptionManager.emailExists(email);
			}
		}

		User user = new User(email,10);
		this.users.put(email,user);
		UserView uv = new UserView();
		uv.setHasBina(user.hasBina());
		uv.setCredit(user.getCredit());
		uv.setEmail(user.getEmail());
		return uv;
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
			} catch (Exception se) {
				hasMore = false;
			}
			currentStation += 1;
		}
	}

    public List<StationView> listStations(Integer k, CoordinatesView coordinates) {
        ArrayList<StationView> Stations = new ArrayList<StationView>();
        SetMultimap<Float, StationView> map = MultimapBuilder.hashKeys().hashSetValues().build();

        for (String station : connectedStations.keySet()) {
            try {
                CoordinatesView coord1 = getInfoStation(station).getCoordinate();
                float DistanceX1 = coord1.getX() - coordinates.getX();
                float DistanceY1 = coord1.getY() - coordinates.getY();

                float Distance1 = (float)Math.sqrt(DistanceX1*DistanceX1 + DistanceY1*DistanceY1);
                map.put(Distance1, getInfoStation(station));
            } catch (InvalidStation_Exception ise) {
                //Station is invalid
            }
        }

        for (StationView station : map.values()) {
            Stations.add(station);
            if (Stations.size() >= k) {
                break;
            }
        }

        return Stations;
    }
	
	public int getUserCredit(String email) throws UserNotExists_Exception {
		return getUserByEmail(email).getCredit();
	}

    public synchronized void getBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception, NoCredit_Exception, UserNotExists_Exception,NoBinaAvail_Exception {

        StationClient station = getStation(stationId);
        User user = getUserByEmail(email);

        if (user.getCredit() <= 0) {
            ExceptionManager.noCreditException();
        }

        if (user.hasBina()) {
            ExceptionManager.alreadyHasBina();
        }

        try {
			station.getBina();
		} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			ExceptionManager.noBinaAvail();
		}
        user.setHasBina(true);
        user.addBonus(-1);
    }
	
	public synchronized void returnBina(String stationId,String email) throws InvalidStation_Exception, UserNotExists_Exception, NoBinaRented_Exception, FullStation_Exception {
		StationClient station = getStation(stationId);
		User user = getUserByEmail(email);
		if (!user.hasBina()) {
			ExceptionManager.noBinaRented();
		}
		try {
			int bonus = station.returnBina();
			user.addBonus(bonus);
			user.setHasBina(false);
		} catch (NoSlotAvail_Exception e) {
			ExceptionManager.fullStation();
		}
	}
	
	public void testClear() {
		for (StationClient station : connectedStations.values()) {
			station.testClear();
		}
		users = new HashMap<String,User>();
	}
	
	public void usersInit(int userInitialPoints) throws BadInit_Exception {
		if(userInitialPoints<=0) ExceptionManager.badInit();
		String userEmail1 = "testing1@text.com";
		String userEmail2 = "testing2@text.com";
		String userEmail3 = "testing3@text.com";
		User user1 = new User(userEmail1,userInitialPoints);
		User user2 = new User(userEmail2,userInitialPoints);
		User user3 = new User(userEmail3,userInitialPoints);
		users.put(userEmail1, user1);
		users.put(userEmail2,user2);
		users.put(userEmail3,user3);
	}
	
	public void stationInit(String stationId, int x, int y, int capacity, int returnPrize)throws BadInit_Exception{
		try {
			connectedStations.get(stationId).testInit(x, y, capacity, returnPrize);
		} catch (org.binas.station.ws.BadInit_Exception e) {
			ExceptionManager.badInit();
		}
	}
	
	// TODO

}
