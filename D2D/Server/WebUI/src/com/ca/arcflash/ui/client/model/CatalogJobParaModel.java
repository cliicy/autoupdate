package com.ca.arcflash.ui.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CatalogJobParaModel extends BaseModelData
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -721113714608302050L;
	
	public String getBackupDestination()
	{
		return (String) get("backupDestination");
	}

	public void setBackupDestination(String backupDestination)
	{
		set("backupDestination", backupDestination);
	}

	public String getUserName()
	{
		return (String) get("userName");
	}

	public void setUserName(String userName)
	{
		set("userName", userName);
	}

	public String getPassword()
	{
		return (String) get("password");
	}

	public void setPassword(String password)
	{
		set("password", password);
	}

	public Long getSessionNumber()
	{
		return (Long) get("sessionNumber");
	}

	public void setSessionNumber(Long sessionNumber)
	{
		set("sessionNumber", sessionNumber);
	}

	public Long getSubSessionNumber()
	{
		return (Long) get("subSessionNumber");
	}

	public void setSubSessionNumber(Long subSessionNumber)
	{
		set("subSessionNumber", subSessionNumber);
	}

	public String getSessionGUID()
	{
		return (String) get("sessionGUID");
	}

	public void setSessionGUID(String sessionGUID)
	{
		set("sessionGUID", sessionGUID);
	}

	public String getEncryptionPassword()
	{
		return (String) get("encryptionPassword");
	}

	public void setEncryptionPassword(String encryptionPassword)
	{
		set("encryptionPassword", encryptionPassword);
	}
	
	public List<String> getGRTEdbList(){
		return (List<String>)get("grtEdbList");
	}
	
	public void setGRTEdbList(List<String> grtEdbList){
		set("grtEdbList", grtEdbList);
	}
	
	public String getVMInstanceUUID()
	{
		return (String) get("vmInstanceUUID");
	}

	public void setVMInstanceUUID(String vmInstanceUUID)
	{
		set("vmInstanceUUID", vmInstanceUUID);
	}
}
