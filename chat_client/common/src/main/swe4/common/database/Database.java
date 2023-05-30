package swe4.common.database;

import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;
import swe4.common.datamodel.User;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface Database extends Remote, Serializable {
	Chat getChat(String name) throws RemoteException;

	Map<String, Chat> getChats() throws RemoteException;

	List<Chat> getChatsFor(User user) throws RemoteException;

	void addChat(Chat chat) throws RemoteException;

	void removeChat(Chat chat) throws RemoteException;

	void addMessage(Chat chat, Message message) throws RemoteException;

	User getUser(String username) throws RemoteException;

	void banUser(Chat chat, User user) throws RemoteException;

	void unbanUser(Chat chat, User user) throws RemoteException;
	void addUser(Chat chat, User user) throws RemoteException;
}
