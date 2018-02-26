package com.ca.arcserve.edge.app.base.webservice.jni;

import java.io.Serializable;
import java.util.List;

import com.ca.arcflash.webservice.jni.model.JHypervInfo;
import com.ca.arcflash.webservice.jni.model.JHypervPFCDataConsistencyStatus;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HypervProtectionType;

public interface IHyperVNativeFacade {
	
	List<JHypervVMInfo> GetVmList(String serverName, String user, String password, boolean onlyUnderThisHyperv) throws EdgeServiceFault;
	
	GetHyperVProtectionTypeResult getHyperVProtectionType(String serverName, String user, String password);
	
	JHypervPFCDataConsistencyStatus getHypervPFCDataConsistentStatus(String hostName,String userName, String password, String vmGuid, String vmUserName, String vmPassword);
	
	void testConnection(String host, String user, String password) throws EdgeServiceFault;

	JHypervVMInfo getHypervVMInfo(String hostName,String userName, String password, String vmInstanceUUID);
	
	public static class GetHyperVProtectionTypeResult implements Serializable
	{
		private static final long serialVersionUID = 3537612070190646671L;
		
		private long errorCode;
		private HypervProtectionType protectionType;
		private String additionInfo;
		
		public long getErrorCode()
		{
			return errorCode;
		}
		
		public void setErrorCode( long errorCode )
		{
			this.errorCode = errorCode;
		}

		public HypervProtectionType getProtectionType()
		{
			return protectionType;
		}

		public void setProtectionType( HypervProtectionType protectionType )
		{
			this.protectionType = protectionType;
		}

		public String getAdditionInfo() {
			return additionInfo;
		}

		public void setAdditionInfo(String additionInfo) {
			this.additionInfo = additionInfo;
		}
	}
	
	List<JHypervInfo> getHypervList(String serverName, String user, String password) throws EdgeServiceFault;
}
