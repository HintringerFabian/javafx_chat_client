package main.swe4.server;

import java.rmi.RemoteException;

public interface ClientAction {
	void perform() throws RemoteException;
}
