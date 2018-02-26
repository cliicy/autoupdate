package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.GDBServerType;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;

public class RemoteNodeInfo implements Serializable {
	private static final long serialVersionUID = -2830641683733908689L;
	private boolean SQLServerInstalled = false;
	private boolean ExchangeInstalled = false;
	private boolean D2DInstalled = false;
	private boolean ARCserveBackInstalled = false;
	private boolean D2DODInstalled = false;
	private String D2DMajorVersion;
	private String D2DMinorVersion;
	private String updateVersionNumber;
	private String D2DBuildNumber;
	private Protocol D2DProtocol = Protocol.UnKnown;
	private int D2DPortNumber = 8014;
	private String D2DUUID;
	private String ARCserveVersion;
	private int ARCservePortNumber = 6054;
	private Protocol ARCserveProtocol = Protocol.UnKnown;
	private ABFuncServerType ARCserveType =ABFuncServerType.UN_KNOWN;
	private GDBServerType gdbType = GDBServerType.GDB_UNKNOW;
	private String hostEdgeServer = null;
	private boolean RPSInstalled = false;
	
	private boolean ConsoleInstalled = false;
	private Protocol ConsoleProtocol = Protocol.UnKnown;
	private int ConsolePortNumber = 8015;
	private String ConsoleUUID;
	
	public String getHostEdgeServer() {
		return hostEdgeServer;
	}
	public void setHostEdgeServer(String hostEdgeServer) {
		this.hostEdgeServer = hostEdgeServer;
	}
	public GDBServerType getGdbType() {
		return gdbType;
	}
	public void setGdbType(GDBServerType gdbType) {
		this.gdbType = gdbType;
	}
	private String osVersion;
	private String osDescription;
	private String osType;//issue143314 <zhaji22>
	
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getOsDescription() {
		return osDescription;
	}
	public void setOsDescription(String osDescription) {
		this.osDescription = osDescription;
	}
	public String getARCserveVersion() {
		return ARCserveVersion;
	}
	public void setARCserveVersion(String aRCserveVersion) {
		ARCserveVersion = aRCserveVersion;
	}
	public ABFuncServerType getARCserveType() {
		return ARCserveType;
	}
	public void setARCserveType(
			ABFuncServerType aRCserveType) {
		ARCserveType = aRCserveType;
	}
	public boolean isD2DInstalled() {
		return D2DInstalled;
	}
	public void setD2DInstalled(boolean d2dInstalled) {
		D2DInstalled = d2dInstalled;
	}
	public boolean isARCserveBackInstalled() {
		return ARCserveBackInstalled;
	}
	public void setARCserveBackInstalled(boolean aRCserveBackInstalled) {
		ARCserveBackInstalled = aRCserveBackInstalled;
	}	
	public boolean isD2DODInstalled() {
		return D2DODInstalled;
	}
	public void setD2DODInstalled(boolean d2dodInstalled) {
		D2DODInstalled = d2dodInstalled;
	}
	public String getD2DMajorVersion() {
		return D2DMajorVersion;
	}
	public void setD2DMajorVersion(String d2dMajorVersion) {
		D2DMajorVersion = d2dMajorVersion;
	}
	public String getD2DMinorVersion() {
		return D2DMinorVersion;
	}
	public void setD2DMinorVersion(String d2dMinorVersion) {
		D2DMinorVersion = d2dMinorVersion;
	}
	public String getUpdateVersionNumber() {
		return updateVersionNumber;
	}
	public void setUpdateVersionNumber(String updateVersionNumber) {
		this.updateVersionNumber = updateVersionNumber;
	}
	public String getD2DBuildNumber() {
		return D2DBuildNumber;
	}
	public void setD2DBuildNumber(String d2dBuildNumber) {
		D2DBuildNumber = d2dBuildNumber;
	}
	public Protocol getD2DProtocol() {
		return D2DProtocol;
	}
	public void setD2DProtocol(Protocol d2dProtocol) {
		D2DProtocol = d2dProtocol;
	}
	public int getD2DPortNumber() {
		return D2DPortNumber;
	}
	public void setD2DPortNumber(int d2dPortNumber) {
		D2DPortNumber = d2dPortNumber;
	}
	public String getD2DUUID() {
		return D2DUUID;
	}
	public void setD2DUUID(String d2duuid) {
		D2DUUID = d2duuid;
	}
	public boolean isSQLServerInstalled() {
		return SQLServerInstalled;
	}
	public void setSQLServerInstalled(boolean sQLServerInstalled) {
		SQLServerInstalled = sQLServerInstalled;
	}
	public boolean isExchangeInstalled() {
		return ExchangeInstalled;
	}
	public void setExchangeInstalled(boolean exchangeInstalled) {
		ExchangeInstalled = exchangeInstalled;
	}
	public int getARCservePortNumber() {
		return ARCservePortNumber;
	}
	public void setARCservePortNumber(int aRCservePortNumber) {
		ARCservePortNumber = aRCservePortNumber;
	}
	public Protocol getARCserveProtocol() {
		return ARCserveProtocol;
	}
	public void setARCserveProtocol(Protocol aRCserveProtocol) {
		ARCserveProtocol = aRCserveProtocol;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public boolean isRPSInstalled() {
		return RPSInstalled;
	}
	public void setRPSInstalled(boolean rPSInstalled) {
		RPSInstalled = rPSInstalled;
	}
	
	public boolean isConsoleInstalled(){
		boolean val = ConsoleInstalled;
		return ConsoleInstalled;
	}
	public void setConsoleInstalled(boolean consoleInstalled){
		ConsoleInstalled = consoleInstalled;
	}
	public Protocol getConsoleProtocol() {
		return ConsoleProtocol;
	}
	public void setConsoleProtocol(Protocol consoleProtocol) {
		ConsoleProtocol = consoleProtocol;
	}
	public int getConsolePortNumber() {
		return ConsolePortNumber;
	}
	public void setConsolePortNumber(int consolePortNumber) {
		ConsolePortNumber = consolePortNumber;
	}
	public String getConsoleUUID() {
		return ConsoleUUID;
	}
	public String getConsoleUUID(boolean isNeededToDecrypt) {
		/*if(!isNeededToDecrypt)
			return ConsoleUUID;
		return BaseWSJNI.AFDecryptString(ConsoleUUID);*/
		
		return getConsoleUUID();
		
	}
	public void setConsoleUUID(String Consoleuuid) {
		ConsoleUUID = Consoleuuid;
	}
}
