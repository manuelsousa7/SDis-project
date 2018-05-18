package org.binas.ws.it;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SecurityIT extends BaseIT {
	private static final String VALID_EMAIL = "alice@T06.binas.org";
	private static final Integer INITIAL_USER_CREDIT = 10;

	@Before
	public void init(){
		try {
			client.testInitStation("T06_Station1", 0, 0, 10, 0);
		}
		catch (BadInit_Exception bie) {
			Assert.fail();
		}
	}

	@Test
	public void success() {
		try {
			client.activateUser(VALID_EMAIL);
			int credit = client.getCredit(VALID_EMAIL);
			client.rentBina("T06_Station1", VALID_EMAIL);
			client.returnBina("T06_Station1", VALID_EMAIL);
			int newCredit = client.getCredit(VALID_EMAIL);
			Assert.assertEquals(newCredit, credit-1);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@After
	public void tearDown() {
		client.testClear();
	}
}
