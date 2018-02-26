package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class WinsForADR implements Serializable, BeanModelTag {

	private static final long serialVersionUID = 2850597176704133277L;

	private String key;
	private String wins = "";

	public WinsForADR() {
		key = "_" + System.currentTimeMillis();
	}

	public String getWins() {
		return wins;
	}

	public void setWins(String wins) {
		this.wins = wins;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WinsForADR that = (WinsForADR) obj;
		return (key == that.key || (key != null && key.equals(that.key)))
				&& (wins == that.wins || (wins != null && wins.equals(that.wins)));
	}

}
