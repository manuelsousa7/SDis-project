package org.binas.station.ws.it;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class GetBinaMethodTest extends BaseIT {
	
	@Test
	public void success() {
		StationView info = client.getInfo();
		
		try {
			client.getBina();
		} catch (NoBinaAvail_Exception e) {
			Assert.fail();
		}
		int expected = info.getAvailableBinas() -1;
		int actual = client.getInfo().getAvailableBinas();
		Assert.assertEquals(expected,actual);
		
		
	}
	@Test(expected = NoBinaAvail_Exception.class)
	public void noBinasAvailable() throws BadInit_Exception, NoBinaAvail_Exception {
		client.testInit(0, 0, 0, 10);
		client.getBina();
	}
	@After
	public void cleanReservations() {
		client.testClear();
	}
	 
}
