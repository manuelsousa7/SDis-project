package org.binas.station.domain;

import org.binas.station.domain.exception.BadInitException;
import org.binas.station.domain.exception.NoBinaAvailException;
import org.binas.station.domain.exception.NoSlotAvailException;
import org.binas.station.ws.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Domain Root. */
public class Station {
	
	/** Creates and returns default coordinates. */
	private static final Coordinates DEFAULT_COORDINATES = new Coordinates(5, 5);
	private static final int DEFAULT_MAX_CAPACITY = 20;
	private static final int DEFAULT_BONUS = 0;
	
	/**HashMap that holds the info on the user's credits */
	private HashMap<String,Integer> clientCredits =  new HashMap<String,Integer>();
	/**HashMap holding the timesTamp of the last write to the client's credit*/
	private HashMap<String,Timestamp> clientTimestamp =  new HashMap<String,Timestamp>();
	
	
	/** Station identifier. */
	private String id;
	/** Station location coordinates. */
	private Coordinates coordinates;
	/** Maximum capacity of station. */
    private int maxCapacity;
	/** Bonus for returning bike at this station. */
    private int bonus;

	/**
	 * Global counter of Binas Gets. Uses lock-free thread-safe single variable.
	 * This means that multiple threads can update this variable concurrently with
	 * correct synchronization.
	 */
    private AtomicInteger totalGets = new AtomicInteger(0);
    /** Global counter of Binas Returns. Uses lock-free thread-safe single variable. */
    private AtomicInteger totalReturns = new AtomicInteger(0);
    /** Global with current number of free docks. Uses lock-free thread-safe single variable. */
    private AtomicInteger freeDocks = new AtomicInteger(0);

    // Singleton -------------------------------------------------------------

 	/** Private constructor prevents instantiation from other classes. */
 	private Station() {
 		//Initialization of default values
 		reset();
 	}

 	/**
 	 * SingletonHolder is loaded on the first execution of
 	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
 	 * not before.
 	 */
 	private static class SingletonHolder {
 		private static final Station INSTANCE = new Station();
 	}
 	
 	/** Synchronized locks object to configure initial values */
 	public synchronized void init(int x, int y, int capacity, int returnPrize) throws BadInitException {
 		if(x < 0 || x > 99 || y < 0 || y > 99 || capacity < 0 || returnPrize < 0)
 			throw new BadInitException();
		this.coordinates = new Coordinates(x, y);
 		this.maxCapacity = capacity;
 		this.bonus = returnPrize;
 	}
 	
	public synchronized void reset() {
 		freeDocks.set(0);
 		maxCapacity = DEFAULT_MAX_CAPACITY;
 		bonus = DEFAULT_BONUS;
		coordinates = DEFAULT_COORDINATES;
		clientCredits =  new HashMap<String,Integer>();
		clientTimestamp =  new HashMap<String,Timestamp>();
		
		totalGets.set(0);
		totalReturns.set(0);
	}
 	
 	public void setId(String id) {
 		this.id = id;
 	}

 	/** Synchronized locks object before attempting to return Bina */
	public synchronized int returnBina() throws NoSlotAvailException {
		if(getFreeDocks() == 0)
			throw new NoSlotAvailException();
		freeDocks.decrementAndGet();
		totalReturns.incrementAndGet();
		System.out.println(getBonus());
		return getBonus();
	}

	/** Synchronized locks object before attempting to get Bina */
	public synchronized void getBina() throws NoBinaAvailException {
		if(getFreeDocks() == getMaxCapacity())
			throw new NoBinaAvailException();
		freeDocks.incrementAndGet();
		totalGets.incrementAndGet();
	}
	
	public synchronized BalanceView getBalance(String email) throws UserNotExists_Exception,InvalidEmail_Exception{
		if(email == null || !checkEmail(email)){
			InvalidEmail faultInfo = new InvalidEmail();
			String message = "[ERROR] Invalid email " + email;
			throw new InvalidEmail_Exception(message, faultInfo);
		}
		Integer credit = this.clientCredits.get(email);
		if(credit==null) {
			UserNotExists faultInfo = new UserNotExists();
			String message = "[ERROR] No records found of user: "+email;
			throw new UserNotExists_Exception(message, faultInfo);
		}
		Timestamp lastWrite = this.clientTimestamp.get(email);
		
		BalanceView response = new BalanceView();
		response.setNewBalance(credit);
		response.setTimeStamp(lastWrite.toString());
		
		return response;
	}

	private Boolean checkEmail(String email){
		if(email.split("@").length != 2){ //Check if email contains only 1 @
			return false;
		}
		String[] emailList = email.split("@");
		if(emailList[0].length() == 0 || emailList[1].length() == 0){ //Check if email is not like @binas or binas@ ...
			return false;
		}
		if(emailList[0].charAt(emailList[0].length() - 1) == '.' || emailList[1].charAt(emailList[1].length() - 1) == '.'){  //Check if email is not like binas.@binas or binas@.binas ...
			return false;
		}
		if(emailList[0].charAt(0) == '.' || emailList[1].charAt(0) == '.'){  //Check if email is not like binas.@binas or binas@.binas ...
			return false;
		}
		if(!emailList[0].replace(".","").matches("^[a-zA-Z0-9]*$") || !emailList[1].replace(".","").matches("^[a-zA-Z0-9]*$")){  //Check if email is not like binas@b!na$ ...
			return false;
		}
		if(emailList[0].contains("..") || emailList[1].contains("..")){ //Check if email is binas@binas..binas ...
			return false;
		}

		return true;
	}


	private  Timestamp stringToTimeStamp(String time){
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = dateFormat.parse(time);
			Timestamp ts = new Timestamp(parsedDate.getTime());
			return ts;
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public synchronized BalanceView setBalance(String email, BalanceView balanceTag) throws InvalidCredit_Exception,InvalidEmail_Exception {
		if(balanceTag.getNewBalance() < 0){
			InvalidCredit faultInfo = new InvalidCredit();
			String message = "[ERROR] Invalid balance " + Integer.toString(balanceTag.getNewBalance());
			throw new InvalidCredit_Exception(message,faultInfo);
		}
		if(email == null || !checkEmail(email)){
			InvalidEmail faultInfo = new InvalidEmail();
			String message = "[ERROR] Invalid email " + email;
			throw new InvalidEmail_Exception(message, faultInfo);
		}
		Timestamp times = this.clientTimestamp.get(email);
		if(times != null){
			if(stringToTimeStamp(balanceTag.getTimeStamp()).after(times)){
				this.clientCredits.put(email,balanceTag.getNewBalance());
				this.clientTimestamp.put(email,stringToTimeStamp(balanceTag.getTimeStamp()));
			}
		} else {
				this.clientCredits.put(email,balanceTag.getNewBalance());
				this.clientTimestamp.put(email,stringToTimeStamp(balanceTag.getTimeStamp()));
		}
		BalanceView response = new BalanceView();
		response.setNewBalance(this.clientCredits.get(email));
		response.setTimeStamp(this.clientTimestamp.get(email).toString());

		System.out.println("Input: " + balanceTag.getTimeStamp() + " - " + balanceTag.getNewBalance());
		System.out.println("Response: " + response.getTimeStamp() + " - " + response.getNewBalance());

		return response;
	}

 	// Getters -------------------------------------------------------------
 	
 	public static synchronized Station getInstance() {
 		return SingletonHolder.INSTANCE;
 	}
    
    public String getId() {
    	return id;
    }
    
	public Coordinates getCoordinates() {
    	return coordinates;
    }
    
    /** Synchronized locks object before returning max capacity */
    public synchronized int getMaxCapacity() {
    	return maxCapacity;
    }
    
    public int getTotalGets() {
    	return totalGets.get();
    }

    public int getTotalReturns() {
    	return totalReturns.get();
    }

    public int getFreeDocks() {
    	return freeDocks.get();
    }
    
    /** Synchronized locks object before returning bonus */
    public synchronized int getBonus() {
    	return bonus;
    }
    
    /** Synchronized locks object before returning available Binas */
    public synchronized int getAvailableBinas() {
    	return maxCapacity - freeDocks.get();
    }
    	
}
