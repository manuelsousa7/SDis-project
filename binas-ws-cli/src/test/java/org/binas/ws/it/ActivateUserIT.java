package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ActivateUserIT extends BaseIT {
	public static final String USER_EMAIL = "testing1@text.com";
	public static final Integer INITIAL_CREDIT = 10;
	
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
			UserView uv = client.activateUser(USER_EMAIL);
			Assert.assertEquals(uv.getCredit(), INITIAL_CREDIT);
			Assert.assertEquals(uv.getEmail(), USER_EMAIL);
		} catch (Exception e) {
			Assert.fail();
		}
		
	}
	
	@Test(expected = InvalidEmail_Exception.class )
	public void nullName() throws InvalidEmail_Exception {
		try {
			client.activateUser(null);
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = EmailExists_Exception.class )
	public void userAlreadyExists() throws EmailExists_Exception {
		try {
			client.activateUser(USER_EMAIL);
			client.activateUser(USER_EMAIL);
		} catch (EmailExists_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@After
	public void tearDown() {
		client.testClear();
	}
}
