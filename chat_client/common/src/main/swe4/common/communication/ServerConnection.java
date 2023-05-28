package main.swe4.common.communication;

import main.swe4.common.datamodel.User;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerConnection extends Remote, Serializable {
	void registerClient(ServerRequestHandler client, User user) throws RemoteException;
}
