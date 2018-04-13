package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ActivateUserIT extends BaseIT {
	public static final String VALID_EMAIL = "testing2@text.com";
	public static final Integer INITIAL_USER_CREDIT = 10;


	@Test
	public void success() {
		try {
			UserView uv = client.activateUser(VALID_EMAIL);
			Assert.assertEquals(uv.getCredit(), INITIAL_USER_CREDIT);
			Assert.assertEquals(uv.getEmail(), VALID_EMAIL);
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


	@Test
	public void validEmail1()  {
		try {
			client.activateUser("teste@binas");
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void validEmail2() {
		try {
			client.activateUser("teste.teste@binas");
		} catch (Exception e) {
			Assert.fail();
		}
	}


	@Test
	public void validEmail3() throws InvalidEmail_Exception {
		try {
			client.activateUser("teste@binas.binas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void validEmail4() throws InvalidEmail_Exception {
		try {
			client.activateUser("4te4ste1@bin4as.bi4nas4");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
	}


	@Test
	public void validEmail5() throws InvalidEmail_Exception {
		try {
			client.activateUser("123123213.123.123.123.123.123312123312@1231232112312313231223.12123312.123312.342432344234234");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void validEmail6() {
		try {
			client.activateUser("a@b");
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail1() throws InvalidEmail_Exception {
		try {
			client.activateUser("teste@");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail2() throws InvalidEmail_Exception {
		try {
			client.activateUser("teste@binas.");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail3() throws InvalidEmail_Exception {
		try {
			client.activateUser("teste.@binas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail4() throws InvalidEmail_Exception {
		try {
			client.activateUser("email");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail5() throws InvalidEmail_Exception {
		try {
			client.activateUser(".@.");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail6() throws InvalidEmail_Exception {
		try {
			client.activateUser("asdsadsad$sa@asdasdsadas.asdasds$adadas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail7() throws InvalidEmail_Exception {
		try {
			client.activateUser("snjadknkasdsa..jkandsksa@asdasdasdasdsa.csadasdas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail8() throws InvalidEmail_Exception {
		try {
			client.activateUser("");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail9() throws InvalidEmail_Exception {
		try {
			client.activateUser("@binas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail10() throws InvalidEmail_Exception {
		try {
			client.activateUser("binas@");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail12() throws InvalidEmail_Exception {
		try {
			client.activateUser("@");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail13() throws InvalidEmail_Exception {
		try {
			client.activateUser("a@a@a");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail14() throws InvalidEmail_Exception {
		try {
			client.activateUser(".");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void invalidEmail15() throws InvalidEmail_Exception {
		try {
			client.activateUser(" ");
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
			client.activateUser(VALID_EMAIL);
			client.activateUser(VALID_EMAIL);
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
