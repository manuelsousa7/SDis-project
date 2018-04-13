package org.binas.ws.it;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ListStationsIT extends BaseIT {
    private static final String[] stationIDs = {  "T06_Station1"
                                                ,"T06_Station2"
                                                ,"T06_Station3"};

    @Test
    public void success() {
        try {
            client.testInitStation(stationIDs[0], 3, 3, 2, 5);
            client.testInitStation(stationIDs[1], 5, 5, 2, 5);
            client.testInitStation(stationIDs[2], 1, 1, 2, 5);

            CoordinatesView coord = new CoordinatesView();
            coord.setX(6);
            coord.setY(6);

            List<StationView> station =  client.listStations(1, coord);

            Assert.assertEquals(station.size(), 1);
            Assert.assertEquals(stationIDs[1], station.get(0).getId());
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void anotherSuccess() {
        try {
            client.testInitStation(stationIDs[0], 3, 3, 2, 5);
            client.testInitStation(stationIDs[1], 5, 5, 2, 5);
            client.testInitStation(stationIDs[2], 1, 1, 2, 5);

            CoordinatesView coord = new CoordinatesView();
            coord.setX(4);
            coord.setY(4);

            List<StationView> station =  client.listStations(2, coord);

            Assert.assertEquals(station.size(), 2);
            Assert.assertEquals(station.get(0).getId(), stationIDs[1]);
            Assert.assertEquals(station.get(1).getId(), stationIDs[0]);
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

   @Test
    public void yetAnotherSuccess() {
        try {
            client.testInitStation(stationIDs[0], 1, 1, 2, 5);
            client.testInitStation(stationIDs[1], 3, 3, 2, 5);
            client.testInitStation(stationIDs[2], 5, 5, 2, 5);

            CoordinatesView coord = new CoordinatesView();
            coord.setX(3);
            coord.setY(3);

            List<StationView> station =  client.listStations(5, coord);

            Assert.assertEquals(3, station.size());
            Assert.assertEquals(station.get(0).getId(), stationIDs[1]);
            Assert.assertEquals(station.get(1).getId(), stationIDs[2]);
            Assert.assertEquals(station.get(2).getId(), stationIDs[0]);
        }
        catch (Exception e) {
            Assert.fail();
        }
    }

    public void tearDown() {
        client.testClear();
    }

}
