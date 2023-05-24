package main.swe4.common;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public interface Database extends Remote, Serializable {
	Chat getChat(String name) throws RemoteException;
	Map<String, Chat> getChats() throws RemoteException;
	ArrayList<Chat> getChatsFor(User user) throws RemoteException;
	void addChat(Chat chat) throws RemoteException;
	void removeChat(Chat chat) throws RemoteException;
	User getUser(String username) throws RemoteException;
}
