package swe4.client.controller;

import swe4.common.communication.ServerEventHandler;
import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ServerRequestHandler implements ServerEventHandler, Serializable {
	private transient ChatViewController chatViewController;
	private transient LoginRegisterController loginRegisterController;

	public void setChatViewController(ChatViewController chatViewController) {
		this.chatViewController = chatViewController;
	}

	@Override
	public void hasConnection() {
		// needs to exist for the server to be able to call the method
	}

	@Override
	public void handleNewChatFromServer(Chat chat) throws RemoteException {
		chatViewController.handleNewChatFromServer(chat);
	}

	@Override
	public void handleNewMessageFromServer(Chat chat, Message message) throws RemoteException {
		chatViewController.handleNewMessageFromServer(chat, message);
	}

	@Override
	public void handleNotificationFromServer(String notification) {
		chatViewController.handleNotificationFromServer(notification);
	}

	@Override
	public void handleBanFromServer(Chat chat) {
		chatViewController.handleBanFromServer(chat);
	}

}
