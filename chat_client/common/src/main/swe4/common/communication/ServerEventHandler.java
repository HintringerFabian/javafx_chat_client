package main.swe4.common.communication;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerEventHandler extends Remote, Serializable, ChatsMessagesHandler {
	void hasConnection() throws RemoteException;
}
