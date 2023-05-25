package main.swe4.client.controller;

import java.rmi.RemoteException;

public interface DatabaseAction {
	void perform() throws RemoteException;
}
