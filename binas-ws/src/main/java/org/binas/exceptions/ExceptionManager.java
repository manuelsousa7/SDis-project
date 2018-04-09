package org.binas.exceptions;

import org.binas.ws.InvalidStation;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaRented;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.UserNotExists;
import org.binas.ws.UserNotExists_Exception;

public abstract class ExceptionManager {
	
	public static void stationNotFound(String stationID) throws InvalidStation_Exception{
		String message = "[ERROR] No station found for id " + stationID;
		InvalidStation faultInfo = new InvalidStation();
		throw new InvalidStation_Exception(message, faultInfo);
	}
	
	public static void userNotFound(String email) throws UserNotExists_Exception  {
		String message = "[ERROR] No user found with email " + email;
		UserNotExists faultInfo = new UserNotExists();
		throw new UserNotExists_Exception(message, faultInfo );
	}
	
	public static void noBinaRented() throws NoBinaRented_Exception{
		String message = "[ERROR] User has no bina rented";
		NoBinaRented faultInfo = new NoBinaRented();
		throw new NoBinaRented_Exception(message, faultInfo );
	}
}
