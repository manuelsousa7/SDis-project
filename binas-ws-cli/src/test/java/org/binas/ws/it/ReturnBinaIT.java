package org.binas.ws.it;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReturnBinaIT extends BaseIT {
	
	public static final String STATION_ID = "T06_Station1";
	public static final String EMAIL = "testing1@text.com";
	public static final int USER_POINTS = 10;
	
	@Before
	public void setUp() throws BadInit_Exception {
		client.testInit(10);
		client.testInitStation(STATION_ID, 5, 5, 10, 1);
	}
	
	@Test
	public void success() throws UserNotExists_Exception {
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
	}
}
