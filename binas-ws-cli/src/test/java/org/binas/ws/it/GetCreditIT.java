package org.binas.ws.it;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.UserNotExists_Exception;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GetCreditIT extends BaseIT {
	private static final String USER_EMAIL = "testing1@text.com";
	private static final int INITIAL_CREDIT = 10;
	
	@Before
	public void setUp() {
		try {
			client.testInit(10);
		} catch (BadInit_Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void success() {
		try {
			int credit = client.getCredit(USER_EMAIL);
			Assert.assertEquals(credit, INITIAL_CREDIT);
		} catch (UserNotExists_Exception e) {
			Assert.fail();
		}
		
	}
	
	@Test(expected = UserNotExists_Exception.class )
	public void wrongUsername() throws UserNotExists_Exception {
		try {
			client.getCredit("none");
		} catch (UserNotExists_Exception e) {
			throw e;
		}
		Assert.fail();
	}
	
	@Test(expected = UserNotExists_Exception.class )
	public void nullUsername() throws UserNotExists_Exception {
		try {
			client.getCredit(null);
		} catch (UserNotExists_Exception e) {
			throw e;
		}
		Assert.fail();
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}
}
