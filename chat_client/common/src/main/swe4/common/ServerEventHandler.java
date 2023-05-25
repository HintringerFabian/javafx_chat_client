package main.swe4.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerEventHandler extends Remote {
	void hasConnection() throws RemoteException;
	void handleNewChatFromServer(Chat chat) throws RemoteException;
	void handleNewMessageFromServer(Chat chat, Message message) throws RemoteException;
}
