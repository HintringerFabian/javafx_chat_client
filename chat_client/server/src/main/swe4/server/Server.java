package swe4.server;

import swe4.common.communication.ChatServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {


	public static void main(String[] args) throws RemoteException, MalformedURLException {

		var port = Registry.REGISTRY_PORT;
		var host = "localhost";
		var serverUrlAndPort = "rmi://" + host + ":" + port + "/ChatServer";

		System.setProperty("java.rmi.server.hostname", host);

		ChatServer server = new ChatDao();
		var remoteDatabase = UnicastRemoteObject.exportObject(server, port);

		LocateRegistry.createRegistry(port);
		Naming.rebind(serverUrlAndPort, remoteDatabase);

		System.out.println("Server started at: " + serverUrlAndPort);
	}
}