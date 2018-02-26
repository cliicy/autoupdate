package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class LicInfoModel extends BaseModelData{
	private static final long serialVersionUID = 1L;
	
	public static final int LICENSE_SUC = 0;
	public static final int LICENSE_ERR = 1;       
	public static final int LICENSE_WAR = 2;
	
//	public static final int LICENSE_BASE_ERROR = 4;
//	public static final int LICENSE_BASE_ERR_SERVER = 5;
//	public static final int LICENSE_BASE_ERR_WORKSTATION = 6;
//	public static final int LICENSE_BASE_ERR_FOUNDTION_SERVER = 7;
//	public static final int LICENSE_BASE_ERR_SMALL_BUSINESS_SERVER = 8;
//	public static final int LICENSE_BASE_ERR_VIRTUAL_MACHINES = 9;
	
	public static final int LICENSE_BASE_SUCCESS           		= 0;            // license stats is ok
	public static final int LICENSE_BASE_ERR_WORKSTATION   		= 1;            // need workstation license
	public static final int LICENSE_BASE_ERR_STANDARD_SOCKET 	= 2;             // need STANDARD_Per_SOCKET license
	public static final int LICENSE_BASE_ERR_ADVANCED_SOCKET 	= 3;            // need ADVANCED_Per_SOCKET license
	public static final int LICENSE_BASE_WARN_TRIAL            	= 4;             // is using trial license  
	public static final int LICENSE_BASE_NCE            		= 16;           // no change edition
	
	public Integer getBaseLic() {
		return get("baseLic");
	}
	public void setBaseLic(Integer baseLic) {
		set("baseLic", baseLic);
	}
	public Integer getbLI() {
		return get("bLI");
	}
	public void setbLI(Integer bLI) {
		set("bLI", bLI);
	}
	public Integer getAllowBMR() {
		return get("allowBMR");
	}
	public void setAllowBMR(Integer allowBMR) {
		set("allowBMR", allowBMR);
	}
	public Integer getAllowBMRAlt() {
		return get("allowBMRAlt");
	}
	public void setAllowBMRAlt(Integer allowBMRAlt) {
		set("allowBMRAlt", allowBMRAlt);
	}
	public Integer getProtectSql() {
		return get("protectSql");
	}
	public void setProtectSql(Integer protectSql) {
		set("protectSql", protectSql);
	}
	public Integer getProtectExchange() {
		return get("protectExchange");
	}
	public void setProtectExchange(Integer protectExchange) {
		set("protectExchange", protectExchange);
	}
	public Integer getProtectHyperV() {
		return get("protectHyperV");
	}
	public void setProtectHyperV(Integer protectHyperV) {
		set("protectHyperV", protectHyperV);
	} 
	
	public Integer getDwEncryption() {
		return get("dwEncryption");
	}

	public void setDwEncryption(Integer dwEncryption) {
		set("dwEncryption", dwEncryption);
	}
	public Integer getDwScheduledExport() {
		return get("dwScheduledExport");
	}

	public void setDwScheduledExport(Integer dwScheduledExport) {
		set("dwScheduledExport", dwScheduledExport);
	}

	public Integer getDwExchangeDB() {
		return get("dwExchangeDB");
	}

	public void setDwExchangeDB(Integer dwExchangeDB) {
		set("dwExchangeDB", dwExchangeDB);
	}

	public Integer getDwExchangeGR() {
		return get("dwExchangeGR");
	}

	public void setDwExchangeGR(Integer dwExchangeGR) {
		set("dwExchangeGR", dwExchangeGR);
	}	
	
	public Integer getDwD2D2D() {
		return get("dwD2D2D");
	}

	public void setDwD2D2D(Integer dwD2D2D) {
		set("dwD2D2D", dwD2D2D);
	}	

}
