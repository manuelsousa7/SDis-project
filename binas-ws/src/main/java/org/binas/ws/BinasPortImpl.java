package org.binas.ws;

import java.util.List;

import javax.jws.WebService;

import org.binas.domain.BinasManager;
import org.binas.station.ws.NoSlotAvail_Exception;

@WebService(endpointInterface = "org.binas.ws.BinasPortType",
wsdlLocation = "binas.1_0.wsdl",
name ="BinasWebService",
portName = "BinasPort",
targetNamespace="http://ws.binas.org/",
serviceName = "BinasService" )
public class BinasPortImpl implements BinasPortType {

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,
			NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		BinasManager.getInstance().ReturnBina(stationId, email);
	}

	@Override
	public String testPing(String inputMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void testClear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws BadInit_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		// TODO Auto-generated method stub
		
	}
}
