package com.ca.arcflash.webservice.jni.model;

import java.util.Comparator;

public class JFileComparator implements Comparator<JFileInfo>{

	@Override
	public int compare(JFileInfo o1, JFileInfo o2) {
		return o1.getStrName().compareToIgnoreCase(o2.getStrName());
	}

}
