package org.binas.exceptions;

import org.binas.ws.*;

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
	
    public static void noBinaAvail() throws NoBinaAvail_Exception{
        String message = "[ERROR] There is no Bina available";
        NoBinaAvail faultInfo = new NoBinaAvail();
        throw new NoBinaAvail_Exception(message, faultInfo );
    }

    public static void noCreditException() throws NoCredit_Exception{
        String message = "[ERROR] User has no sufficient credit";
        NoCredit faultInfo = new NoCredit();
        throw new NoCredit_Exception(message, faultInfo );
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

	public static void badInit() throws BadInit_Exception{
		String message = "[ERROR] Bad init values";
		BadInit faultInfo = new BadInit();
		throw new BadInit_Exception(message, faultInfo);

	}

	public static void invalidEmail(String email) throws InvalidEmail_Exception{
		String message = "[ERROR] Email " + email + " is not in correct format";
		InvalidEmail faultInfo = new InvalidEmail();
		throw new InvalidEmail_Exception(message, faultInfo);
	}

	public static void emailExists(String email) throws EmailExists_Exception {
		String message = "[ERROR] No email found for email " + email;
		EmailExists faultInfo = new EmailExists();
		throw new EmailExists_Exception(message, faultInfo);
	}
}
