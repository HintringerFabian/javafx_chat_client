package main.swe4.server;

import main.swe4.common.Database;
import main.swe4.database.FakeDatabase;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
	public static void main(String[] args) throws RemoteException, MalformedURLException {

		// TODO think of logic:
		// - how many clients are connected?
		// - which ports are used?
		// - how to handle multiple clients?

		var port = Registry.REGISTRY_PORT;
		var host = "localhost";
		var serverUrlAndPort = "rmi://" + host + ":" + port + "/ChatServer";

		System.setProperty("java.rmi.server.hostname", host);

		Database database = FakeDatabase.getInstance();
		var remoteDatabase = UnicastRemoteObject.exportObject(database, port);

		LocateRegistry.createRegistry(port);
		Naming.rebind(serverUrlAndPort, remoteDatabase);

		System.out.println("Server started at: " + serverUrlAndPort);
	}
}
