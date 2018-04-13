package org.binas.station.ws.it;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.StationView;
import org.junit.Assert;
import org.junit.Test;


public class GetInfoIT extends BaseIT {
	
	@Test
	public void emptyStation() {
		try {
			client.testInit(10, 10, 0, 1000);
			StationView info = client.getInfo();
			int actualCapacity = info.getCapacity();
			int actualX = info.getCoordinate().getX();
			int actualY = info.getCoordinate().getY();
			Assert.assertEquals(0, actualCapacity);
			Assert.assertEquals(10, actualX);
			Assert.assertEquals(10, actualY);
			
		} catch (BadInit_Exception e) {
			Assert.fail();
		}
	}
	@Test(expected = BadInit_Exception.class)
	public void invalidCapacity() throws BadInit_Exception {
		try {
			client.testInit(-10,-20, -10, 1000);
		} catch (BadInit_Exception e) {
			throw e;
		}
	}
	@Test
	public void goodStation() {
		try {
			client.testInit(50, 60, 20, 1000);
			StationView info = client.getInfo();
			int actualCapacity = info.getCapacity();
			int actualX = info.getCoordinate().getX();
			int actualY = info.getCoordinate().getY();
			Assert.assertEquals(20, actualCapacity);
			Assert.assertEquals(50, actualX);
			Assert.assertEquals(60, actualY);
			
		} catch (BadInit_Exception e) {
			Assert.fail();
		}
	}

}
