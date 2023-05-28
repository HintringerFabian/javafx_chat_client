package main.swe4.common;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Action extends Serializable {
	void perform() throws RemoteException;
}
