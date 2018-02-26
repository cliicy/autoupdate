package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class ExchangeDiscoveryModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3092436526347024781L;
		
	public static final int EXCH_DISC_TYPE_ORGANIZATION  = 1;
	public static final int EXCH_DISC_TYPE_SERVER  	  	 = 2;
	public static final int EXCH_DISC_TYPE_STORAGE_GROUP = 3;
	public static final int EXCH_DISC_TYPE_MBS_DB		 = 4;
	public static final int EXCH_DISC_TYPE_PUBLIC_FOLDER = 5;
	public static final int EXCH_DISC_TYPE_MAILBOX	 	 = 6;
		
	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}
	
	public Integer getType() {
		return (Integer) get("type");
	}

	public void setType(Integer type) {
		set("type", type);
	}
	
	public String getDN() {
		return get("dn");
	}

	public void setDN(String dn) {
		set("dn", dn);
	}
	
	public Integer getExVersion() {
		return (Integer) get("exVersion");
	}

	public void setExVersion(Integer exVersion) {
		set("exVersion", exVersion);
	}
	
	public String getPath()
	{
		return get("path");
	}
	
	public void setPath(String path)
	{
		set("path", path);
	}
	
	
	/**
	 *  methods for tree item
	 */
	
	public String getIcon()
	{
		String strIconStyleName = "";
		
		if (this.getType() != null)
		{
			int nType = this.getType().intValue();
	   	 	switch(nType)
	   	 	{
	   	 	case EXCH_DISC_TYPE_ORGANIZATION:
	   	 		strIconStyleName = "exchange_grt_organization_icon";
	   	 		break;
	   		case EXCH_DISC_TYPE_SERVER:
	   	 		strIconStyleName = "exchange_grt_server_icon";
	   	 		break;
	   		case EXCH_DISC_TYPE_STORAGE_GROUP:
	   	 		strIconStyleName = "exchange_grt_storage_group_icon";
	   	 		break;
	   		case EXCH_DISC_TYPE_MBS_DB:
	   	 		strIconStyleName = "exchange_grt_edb_icon";
	   	 		break;
	   		case EXCH_DISC_TYPE_PUBLIC_FOLDER:
	   	 		strIconStyleName = "exchange_grt_public_folder_icon";
	   	 		break;	   		
	   		case EXCH_DISC_TYPE_MAILBOX:
	   	 		strIconStyleName = "exchange_grt_mailbox_icon";
	   	 		break;
		   	 	default: 
		   	 	{
		   	 		strIconStyleName = "exchange_grt_folder_icon";
		   	 	}
	   	 		break;
	   	 	}
		}	
   	 	
   	 	return strIconStyleName;
	}
	
	public int compareTo(ExchangeDiscoveryModel other)
	{
		int ret = 0;
	  	if (other != null)
	  	{	    	
	  		if (this.getType() != null && other.getType() != null)
	  		{
	  			if (this.getType().longValue() != other.getType().longValue())
		  		{
		  			ret = this.getType().compareTo(other.getType());
		  		}
		  		else if (this.getName() != null && other.getName() != null)
		  		{
		  			ret = this.getName().compareTo(other.getName()); 
		  		}
	  		}	  		
	  	}
	  	return ret;
	}
	
	public boolean isValidDestination()
	{
		boolean bValid = false;
		
		switch(this.getType().intValue())
   	 	{
   	 	case EXCH_DISC_TYPE_ORGANIZATION:
   		case EXCH_DISC_TYPE_SERVER:
   		case EXCH_DISC_TYPE_STORAGE_GROUP:
   		case EXCH_DISC_TYPE_MBS_DB:
   			bValid = false;
   			break;
   		
   		case EXCH_DISC_TYPE_PUBLIC_FOLDER:
   		case EXCH_DISC_TYPE_MAILBOX:
   			bValid = true;
   			break;
	   	 default: 
	   		bValid = false;
	   		break;
   	 	}
		
		return bValid;
	}
}
