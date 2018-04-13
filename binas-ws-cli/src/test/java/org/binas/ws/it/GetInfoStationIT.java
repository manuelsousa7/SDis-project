package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GetInfoStationIT extends BaseIT {
	private static final String STATION_ID = "T06_Station1";
	private static final int CAPACITY = 10;
	private static final int COORDINATE_X = 5;
	private static final int COORDINATE_Y = 10;
	private static final String VALID_EMAIL = "testing1@text.com";
	private static final String VALID_EMAIL_2 = "testing2@text.com";
	private StationView st = null;

	@Before
	public void setUp() {
		try {
			client.testInit(10);
			client.testInitStation(STATION_ID, 5, 10, CAPACITY, 1);
			this.st = client.getInfoStation(STATION_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void success() {
		try {
			Assert.assertEquals(this.st.getCapacity(), CAPACITY);
			Assert.assertEquals(this.st.getAvailableBinas(), CAPACITY);
			Assert.assertEquals(this.st.getCoordinate().getX(), new Integer(COORDINATE_X));
			Assert.assertEquals(this.st.getCoordinate().getY(), new Integer(COORDINATE_Y));
			Assert.assertEquals(this.st.getId(), STATION_ID);
			Assert.assertEquals(st.getTotalReturns(),0);
			Assert.assertEquals(st.getTotalGets(),0);
		} catch (Exception e) {
			Assert.fail();
		}
		
	}

	@Test
	public void rentBina() {
		try {
			client.rentBina(STATION_ID,VALID_EMAIL);
			StationView st = client.getInfoStation(STATION_ID);
			Assert.assertEquals(st.getAvailableBinas(), CAPACITY - 1);
			Assert.assertEquals(st.getTotalGets(),1);
			Assert.assertEquals(st.getFreeDocks(),1);
			client.rentBina(STATION_ID,VALID_EMAIL_2);
			StationView st2 = client.getInfoStation(STATION_ID);
			Assert.assertEquals(st2.getAvailableBinas(), CAPACITY - 2);
			Assert.assertEquals(st2.getTotalGets(),2);
			Assert.assertEquals(st2.getFreeDocks(),2);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void rentAndReturnBina() {
		try {
			client.rentBina(STATION_ID,VALID_EMAIL);
			client.returnBina(STATION_ID,VALID_EMAIL);
			StationView st = client.getInfoStation(STATION_ID);
			Assert.assertEquals(st.getAvailableBinas(), CAPACITY);
			Assert.assertEquals(st.getTotalGets(),1);
			Assert.assertEquals(st.getTotalReturns(),1);
			Assert.assertEquals(st.getFreeDocks(),0);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test(expected = InvalidStation_Exception.class )
	public void nullName() throws InvalidStation_Exception {
		try {
			client.getInfoStation(null);
		} catch (InvalidStation_Exception e) {
			throw e;
		}
		Assert.fail();
	}

	@Test(expected = InvalidStation_Exception.class )
	public void invalidName() throws InvalidStation_Exception {
		try {
			client.getInfoStation("INVALID_ID");
		} catch (InvalidStation_Exception e) {
			throw e;
		}
		Assert.fail();
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}
}
