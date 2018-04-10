package org.binas.ws.it;

import org.binas.ws.UserNotExists_Exception;
import org.junit.Assert;
import org.junit.Test;

public class GetCreditIT extends BaseIT {
	public static final String USER_EMAIL = "testing1@text.com";
	public static final int INITIAL_CREDIT = 10;
	
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
}
