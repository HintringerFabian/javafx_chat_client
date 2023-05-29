package main.swe4.common.communication;

import main.swe4.common.datamodel.Chat;
import main.swe4.common.datamodel.Message;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerEventHandler extends Remote, Serializable {
	void hasConnection() throws RemoteException;
	void handleNewChatFromServer(Chat chat) throws RemoteException;
	void handleNewMessageFromServer(Chat chat, Message message) throws RemoteException;
}
