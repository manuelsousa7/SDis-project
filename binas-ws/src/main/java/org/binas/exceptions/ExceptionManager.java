package org.binas.exceptions;

import org.binas.ws.FullStation;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidStation;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaRented;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.AlreadyHasBina;
import org.binas.ws.AlreadyHasBina_Exception;
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

	public static void alreadyHasBina() throws AlreadyHasBina_Exception {
		String message = "[ERROR] User already has a bina rented";
		AlreadyHasBina faultInfo = new AlreadyHasBina();
		throw new AlreadyHasBina_Exception(message, faultInfo );
	}

	public static void fullStation() throws FullStation_Exception{
		String message = "[ERROR] Station Full";
		FullStation faultInfo = new FullStation();
		throw new FullStation_Exception(message, faultInfo );
	}

	public static void emptyStation()/* throws EmptyStation_Exception*/{
		/*String message = "[ERROR] Station is Empty";
		EmptyStation faultInfo = new EmptyStation();
		throw new EmptyStation_Exception(message, faultInfo );*/
	}
}
