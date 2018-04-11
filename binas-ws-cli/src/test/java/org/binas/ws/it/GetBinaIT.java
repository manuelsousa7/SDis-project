package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GetBinaIT extends BaseIT {
    public static final String USER_EMAIL = "testing1@text.com";
    public static final String stationID = "T06_Station1";

    @Test
    public void success() {
        try {
            client.testInit(1);
            client.rentBina(stationID, USER_EMAIL);
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

    @Test (expected = InvalidStation_Exception.class)
    public void invalidStation() throws InvalidStation_Exception {
        try {
            client.testInit(1);
            client.rentBina("Wrong station", USER_EMAIL);
        }
        catch(InvalidStation_Exception ise) {
            throw ise;
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

    @Test (expected = UserNotExists_Exception.class)
    public void userNotExists() throws UserNotExists_Exception {
        try {
            client.testInit(1);
            client.rentBina(stationID, "WrongUser@Bad");
        }
        catch(UserNotExists_Exception unee) {
            throw unee;
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

    @Test (expected = NoCredit_Exception.class)
    public void noCredit() throws NoCredit_Exception {
        try {
            client.testInit(0);
            client.rentBina(stationID, USER_EMAIL);
        }
        catch(NoCredit_Exception nce) {
            throw nce;
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

    @Test (expected = AlreadyHasBina_Exception.class)
    public void alreadyRented() throws AlreadyHasBina_Exception {
        try {
            client.testInit(1);
            client.rentBina(stationID, USER_EMAIL);
            client.rentBina(stationID, USER_EMAIL);
        }
        catch(AlreadyHasBina_Exception ahbe) {
            throw ahbe;
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

    @Test (expected = NoBinaAvail_Exception.class)
    public void noBinaAvail() throws NoBinaAvail_Exception {
        try {
            //TODO Complete test
            client.testInit(1);
            client.rentBina(stationID, USER_EMAIL);
        }
        catch(NoBinaAvail_Exception nbae) {
            throw nbae;
        }
        catch (Exception e) {
            Assert.fail();
        }
    }
}
