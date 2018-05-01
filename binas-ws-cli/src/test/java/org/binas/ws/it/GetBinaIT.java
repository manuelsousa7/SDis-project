package org.binas.ws.it;

import org.binas.ws.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class GetBinaIT extends BaseIT {
    private static final String USER_EMAIL = "testing1@text.com";
    private static final String stationID = "T06_Station1";

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
            client.returnBina(stationID, USER_EMAIL);
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
            client.testInit(2);
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

    @Test(expected = NoBinaAvail_Exception.class)
    public void noBinaAvail() throws NoBinaAvail_Exception{
        try {
            client.testInit(1);
            client.testInitStation(stationID, 5, 5, 0, 5);
            client.rentBina(stationID, USER_EMAIL);
        } catch (NoBinaAvail_Exception e) {
            try{
                Assert.assertTrue(client.getInfoStation(stationID).getAvailableBinas() == 0);
            } catch (InvalidStation_Exception is){
                Assert.fail();
            }
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
