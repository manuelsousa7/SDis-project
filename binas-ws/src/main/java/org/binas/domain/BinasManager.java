package org.binas.domain;

import java.util.HashMap;

import org.binas.exceptions.ExceptionManager;
import org.binas.ws.*;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.cli.StationClient;

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

	private void activateUser(String email) throws InvalidEmail_Exception{
		if(email == null){
			ExceptionManager.invalidEmail(email);
		} else {
			Boolean isValidEmail = email.matches("^(.+)@(.+)$");
			if(!isValidEmail){
				ExceptionManager.invalidEmail(email);
			}
		}
		// return is UserView
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
	
	public void ReturnBina(String stationId,String email) throws InvalidStation_Exception, UserNotExists_Exception, NoBinaRented_Exception, FullStation_Exception {
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
	
	public void usersInit(int userInitialPoints) throws BadInit_Exception,InvalidEmail_Exception {
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
	
	// TODO

}
