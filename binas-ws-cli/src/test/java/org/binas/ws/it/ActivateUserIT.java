package org.binas.ws.it;

import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.UserView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ActivateUserIT extends BaseIT {
	private static final String VALID_EMAIL = "testing2@text.com";
	private static final Integer INITIAL_USER_CREDIT = 10;


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
	public void validEmail()  {
		try {
			client.activateUser("teste@binas");
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void validEmailWithDotsBeforeAt() {
		try {
			client.activateUser("teste.teste@binas");
		} catch (Exception e) {
			Assert.fail();
		}
	}


	@Test
	public void validEmailWithDotsAfterAt() throws InvalidEmail_Exception {
		try {
			client.activateUser("teste@binas.binas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void validEmailWithNumbers() throws InvalidEmail_Exception {
		try {
			client.activateUser("4te4ste1@bin4as.bi4nas4");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
	}


	@Test
	public void validEmailOnlyNumbers() throws InvalidEmail_Exception {
		try {
			client.activateUser("123123213.123.123.123.123.123312123312@1231232112312313231223.12123312.123312.342432344234234");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void oneCharacterBeforeAndAfterAt() {
		try {
			client.activateUser("a@b");
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void validBeforeAtEmptyAfterAt() throws InvalidEmail_Exception {
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
	public void validAfterAtEmptyBeforeAt() throws InvalidEmail_Exception {
		try {
			client.activateUser("@teste");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidDotAfterAt() throws InvalidEmail_Exception {
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
	public void invalidDotAfterAt2() throws InvalidEmail_Exception {
		try {
			client.activateUser("teste@.binas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidDotBeforeAt() throws InvalidEmail_Exception {
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
	public void invalidDotBeforeAt2() throws InvalidEmail_Exception {
		try {
			client.activateUser(".teste@binas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void emailNoAt() throws InvalidEmail_Exception {
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
	public void dotAfterAndBeforeAt() throws InvalidEmail_Exception {
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
	public void invalidCharactersBeforeAt() throws InvalidEmail_Exception {
		try {
			client.activateUser("asdsadsad$sa@asdasdsadas.asdasdsadadas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void invalidCharactersAfterAt() throws InvalidEmail_Exception {
		try {
			client.activateUser("asdsadsadsa@asdasds$adas.asdasd$sadada");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}


	@Test(expected = InvalidEmail_Exception.class )
	public void consecutiveDotsBeforeAt() throws InvalidEmail_Exception {
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
	public void consecutiveDotsAfterAt() throws InvalidEmail_Exception {
		try {
			client.activateUser("snjadknkasdsajkandsksa@asdasdasdas..dsa.csadasdas");
		} catch (InvalidEmail_Exception e) {
			throw e;
		} catch (Exception e) {
			Assert.fail();
		}
		Assert.fail();
	}

	@Test(expected = InvalidEmail_Exception.class )
	public void emptyString() throws InvalidEmail_Exception {
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
	public void at() throws InvalidEmail_Exception {
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
	public void twoAts() throws InvalidEmail_Exception {
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
	public void onlyDot() throws InvalidEmail_Exception {
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
	public void space() throws InvalidEmail_Exception {
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
