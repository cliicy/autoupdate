package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class NodeManageResult implements Serializable{
	private static final long serialVersionUID = 1L;
	private NodeManagedStatusByConsole managedStatus;
	private String mnanagedConsoleName;
	
	public NodeManagedStatusByConsole getManagedStatus() {
		return managedStatus;
	}
	public void setManagedStatus(NodeManagedStatusByConsole managedStatus) {
		this.managedStatus = managedStatus;
	}
	public String getMnanagedConsoleName() {
		return mnanagedConsoleName;
	}
	public void setMnanagedConsoleName(String mnanagedConsoleName) {
		this.mnanagedConsoleName = mnanagedConsoleName;
	}


	public enum NodeManagedStatusByConsole{
		NotBeManaged(0),
		ManagedByCurrentConsle(1),
		ManagedByAnotherConsole(2);
		private final int value;

		NodeManagedStatusByConsole(int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
		public static NodeManagedStatusByConsole parse(int value){
			if(value == NotBeManaged.getValue()){
				return NotBeManaged;
			}else if (value == ManagedByCurrentConsle.getValue()) {
				return ManagedByCurrentConsle;
			}else {
				return ManagedByAnotherConsole;
			}
		}
	}
}
