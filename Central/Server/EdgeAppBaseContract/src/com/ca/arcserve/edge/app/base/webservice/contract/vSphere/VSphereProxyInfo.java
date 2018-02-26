package com.ca.arcserve.edge.app.base.webservice.contract.vSphere;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

@XmlAccessorType(XmlAccessType.FIELD)
public class VSphereProxyInfo implements Serializable {
	private static final long serialVersionUID = 3552002692116609777L;
	private int vmHostID;
	private int vSphereProxyId;
	private String vSphereProxyName;
	private String vSphereProxyUsername;
	private @NotPrintAttribute String vSphereProxyPassword;
	private Protocol vSphereProxyProtocol;
	private int vSphereProxyPort;
	private String vSphereProxyUuid;
	private GatewayId vSphereProxyGatewayId;

	

	public GatewayId getvSphereProxyGatewayId() {
		return vSphereProxyGatewayId;
	}

	public void setvSphereProxyGatewayId(GatewayId vSphereProxyGatewayId) {
		this.vSphereProxyGatewayId = vSphereProxyGatewayId;
	}

	public void setVmHostID(int vmHostID)
	{
		this.vmHostID = vmHostID;
	}
	
	public int getVmHostID()
	{
		return this.vmHostID;
	}

	public int getvSphereProxyId()
	{
		return vSphereProxyId;
	}
	
	public void setvSphereProxyId( int vSphereProxyId )
	{
		this.vSphereProxyId = vSphereProxyId;
	}
	
	public void setVSphereProxyName(String vSphereProxyName){
		this.vSphereProxyName = vSphereProxyName;
	}

	public String getVSphereProxyName(){
		return this.vSphereProxyName;
	}

	public void setVSphereProxyUsername(String vSphereProxyUsername){
		this.vSphereProxyUsername = vSphereProxyUsername;
	}

	public String getVSphereProxyUsername(){
		return this.vSphereProxyUsername;
	}

	public void setVSphereProxyPassword(String vSphereProxyPassword){
		this.vSphereProxyPassword = vSphereProxyPassword;
	}

	public String getVSphereProxyPassword(){
		return this.vSphereProxyPassword;
	}

	public void setVSphereProxyProtocol(Protocol vSphereProxyProtocol){
		this.vSphereProxyProtocol = vSphereProxyProtocol;
	}

	public Protocol getVSphereProxyProtocol(){
		return this.vSphereProxyProtocol;
	}

	public void setVSphereProxyPort(int vSphereProxyPort){
		this.vSphereProxyPort = vSphereProxyPort;
	}

	public int getvSphereProxyPort(){
		return this.vSphereProxyPort;
	}
	
	public String getVSphereProxyUuid() {
		return vSphereProxyUuid;
	}
	
	public void setVSphereProxyUuid(String vSphereProxyUuid) {
		this.vSphereProxyUuid = vSphereProxyUuid;
	}
}
