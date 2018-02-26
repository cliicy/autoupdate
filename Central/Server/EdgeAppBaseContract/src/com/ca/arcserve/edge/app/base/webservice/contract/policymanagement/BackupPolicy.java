package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "BackupPolicy", namespace = "http://webservice.edge.arcserve.ca.com/policymanagement/")
public class BackupPolicy implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int			id;
	private String		name;
	private int 		nodeId;
	private String		policyGuid;
	private String		policyXML;
	private int			type;				// 1: BackupAndArchiving
											// 2: VCM
											// 3: VMBackup
	private int			contentFlag;		// bit 1 -> Backup
											// bit 2 -> Archiving
											// bit 3 -> Virtual Conversion
											// bit 4 -> VM Backup
											// bit 5 -> Preferences
	private String		version;			// edge version?
	private Date		creationTime;
	private Date		modificationTime;
	private int			policyProductType;
	
	//////////////////////////////////////////////////////////////////////////
	
	public BackupPolicy()
	{
		this.id					= -1;
		this.name				= "";
		this.policyXML			= "";
		this.type				= PolicyTypes.BackupAndArchiving;
		this.contentFlag		= 0;
		this.version			= "";
		this.creationTime		= new Date();
		this.modificationTime	= this.creationTime;
		this.policyProductType	= -1;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public String getPolicyXML() {
		return policyXML;
	}
	
	public void setPolicyXML(String policyXML) {
		this.policyXML = policyXML;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public int getType()
	{
		return type;
	}
	
	public void setType( int type )
	{
		this.type = type;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public int getContentFlag()
	{
		return contentFlag;
	}

	public void setContentFlag( int contentFlag )
	{
		this.contentFlag = contentFlag;
	}

	//////////////////////////////////////////////////////////////////////////
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	//////////////////////////////////////////////////////////////////////////
	
	public Date getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime( Date creationTime )
	{
		this.creationTime = creationTime;
	}

	//////////////////////////////////////////////////////////////////////////
	
	public Date getModificationTime()
	{
		return modificationTime;
	}

	public void setModificationTime( Date modificationTime )
	{
		this.modificationTime = modificationTime;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getPolicyGuid() {
		return policyGuid;
	}

	public void setPolicyGuid(String policyGuid) {
		this.policyGuid = policyGuid;
	}

	public int getPolicyProductType() {
		return policyProductType;
	}

	public void setPolicyProductType(int policyProductType) {
		this.policyProductType = policyProductType;
	}
}
