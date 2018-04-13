package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReturnBinaIT extends BaseIT {
	
	public static final String STATION_ID = "T06_Station1";
	public static final String STATION_ID2 = "T06_Station2";
	public static final String EMAIL = "testing1@text.com";
	public static final int USER_POINTS = 10;
	public static final int CAPACITY = 10;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(10);
		client.testInitStation(STATION_ID, 5, 5, CAPACITY, 1);
	}
	
	@Test
	public void success() throws UserNotExists_Exception, InvalidStation_Exception {
		try {
			client.rentBina(STATION_ID , EMAIL);
		} catch (AlreadyHasBina_Exception | InvalidStation_Exception | NoBinaAvail_Exception | NoCredit_Exception
				| UserNotExists_Exception e) {
			Assert.fail();
		}
		try {
			client.returnBina(STATION_ID, EMAIL);
		} catch (FullStation_Exception | InvalidStation_Exception | NoBinaRented_Exception
				| UserNotExists_Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertEquals(USER_POINTS,client.getCredit(EMAIL));
		Assert.assertEquals(client.getInfoStation(STATION_ID).getAvailableBinas(),CAPACITY);
	}
	
	@Test(expected = NoBinaRented_Exception.class)
	public void noBinaInUser() throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		client.returnBina(STATION_ID, EMAIL);
	}
	
	@Test(expected = FullStation_Exception.class)
	public void noSpace() throws BadInit_Exception, FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception{
		client.testInitStation(STATION_ID2, 0, 0, CAPACITY, 1);
		try {
			client.rentBina(STATION_ID , EMAIL);
		} catch (AlreadyHasBina_Exception | InvalidStation_Exception | NoBinaAvail_Exception | NoCredit_Exception
				| UserNotExists_Exception e) {
			Assert.fail();
		}
		client.returnBina(STATION_ID2, EMAIL);
	}
	
	@Test(expected = UserNotExists_Exception.class)
	public void invalidUser() throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		client.returnBina(STATION_ID, "invalid");
	}
	@Test(expected = UserNotExists_Exception.class)
	public void nullUser() throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		client.returnBina(STATION_ID, null);
	}
	@Test(expected = InvalidStation_Exception.class)
	public void invalidStation() throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		client.returnBina("invalid",EMAIL);
	}
	@Test(expected = InvalidStation_Exception.class)
	public void nullStation() throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		client.returnBina(null,EMAIL);
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}
}
