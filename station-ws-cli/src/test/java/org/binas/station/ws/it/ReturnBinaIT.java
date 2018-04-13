package org.binas.station.ws.it;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ReturnBinaIT extends BaseIT {
	private static final int STATION_BONUS = 1000;
	private static final int CAPACITY = 1;

	@Before
	public void setup(){
		try {
			client.testInit(10, 10, CAPACITY, STATION_BONUS);
		} catch (BadInit_Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void validReturn() {
		try {
			client.getBina();
			int bonus = client.returnBina();
			StationView uv = client.getInfo();
			Assert.assertEquals(bonus,STATION_BONUS);
			Assert.assertEquals(uv.getTotalReturns(),1);
			Assert.assertEquals(uv.getAvailableBinas(),CAPACITY);
			Assert.assertEquals(uv.getTotalGets(),1);
		} catch (Exception e) {
			Assert.fail();
		}
	}

    @Test(expected = NoSlotAvail_Exception.class)
    public void noSlotAvailable() throws NoSlotAvail_Exception {
        try {
            client.returnBina();
        } catch (NoSlotAvail_Exception e) {
            throw e;
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
