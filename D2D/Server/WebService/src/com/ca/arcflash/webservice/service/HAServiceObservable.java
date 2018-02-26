package com.ca.arcflash.webservice.service;

import java.util.Observable;

public class HAServiceObservable extends Observable {
	public synchronized void notifyObserversWithChanged(Object arg) {
		setChanged();
		notifyObservers(arg);
	}

}
