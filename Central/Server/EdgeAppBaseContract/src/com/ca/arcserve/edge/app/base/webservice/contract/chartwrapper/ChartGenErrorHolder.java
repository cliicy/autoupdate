package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChartGenErrorHolder implements Serializable {

	private static final long serialVersionUID = 1L;
	public static Integer DotNetError = 0x0001;
	public static Integer FlashError = 0x0002;
	public static String ChartGenHelpTopic = "";
	private HashMap<Integer, String> errors = new HashMap<Integer, String> ();

	public void setPrerequisiteErrorString( Integer code, String msg ) {
		errors.put(code, msg);
	}
	public List<String> getPreRequisiteErrorString() {
		List<String> msgs = new ArrayList<String>();
		msgs.addAll( errors.values() );	
		return msgs;
	}
	public boolean isPrerequisiteError() {
		return !errors.isEmpty();
	}
}
