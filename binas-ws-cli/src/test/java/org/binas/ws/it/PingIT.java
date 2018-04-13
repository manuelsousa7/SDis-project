package org.binas.ws.it;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test suite
 */
public class PingIT extends BaseIT {

    private static final String INPUT = "input";


    @Test
    public void emptyInput() {
        Assert.assertNotNull(client.testPing(""));
    }

    @Test
    public void nullInput() {
        Assert.assertNotNull(client.testPing(null));
    }

    @Test
    public void pingEmptyTest() {
        Assert.assertNotNull(client.testPing(INPUT));
    }

}
