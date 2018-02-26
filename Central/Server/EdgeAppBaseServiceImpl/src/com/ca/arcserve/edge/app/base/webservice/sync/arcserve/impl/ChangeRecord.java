package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.util.LinkedList;
import java.util.List;

class ChangeRecord {
	
    public enum ChangeActionType {
		Insert, Delete, Update, InitInsert
	}
	
    public Integer ID;
	public String TableName;
	public ChangeActionType Type;
	public List<String> Columns = new LinkedList<String>();
	public List<String> Values = new LinkedList<String>();
}
