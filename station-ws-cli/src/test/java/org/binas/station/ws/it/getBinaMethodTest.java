package org.binas.station.ws.it;

import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class getBinaMethodTest extends BaseIT {
	
	private int reserved = 0;
	
	@Test
	public void success() {
		
		reserved = 1;
		
		//Getting info before getting Bina
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
	@Test
	public void noBinasAvailable() {
		StationView info = client.getInfo();
		int availableBinas = info.getAvailableBinas();
		reserved = 0;
		availableBinas +=1;
		while (availableBinas > 0) {
			try {
				client.getBina();
				availableBinas--;
				reserved ++;
			} catch (NoBinaAvail_Exception e) { 
				Assert.assertTrue(client.getInfo().getAvailableBinas()==0);
				return;
			}
		}
		Assert.fail();
	}
	@After
	public void cleanReservations() {
		client.testClear();
	}
	 
}
