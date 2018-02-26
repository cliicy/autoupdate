package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ExchangeOptionModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4702436246123699594L;

	public Boolean isDisMoundAndMountDB() {
		return get("disMoundAndMountDB");
	}

	public void setDisMoundAndMountDB(Boolean disMoundAndMountDB) {
		set("disMoundAndMountDB", disMoundAndMountDB);
	}

	public Boolean isReplayLogOnDB() {
		return get("replayLogOnDB");
	}

	public void setReplayLogOnDB(Boolean replayLogOnDB) {
		set("replayLogOnDB", replayLogOnDB);
	}
}
