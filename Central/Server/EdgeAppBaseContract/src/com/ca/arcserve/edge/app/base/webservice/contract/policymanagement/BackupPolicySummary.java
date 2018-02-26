package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

import java.io.Serializable;
import java.util.Date;

public class BackupPolicySummary implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public class PolicyType
	{
		public static final int D2DPolicy		= 1;
		public static final int ARCservePolicy	= 2;
	}
	
	public class PolicyContentFlag
	{
		public static final int All					= 0xffffffff;
		public static final int Backup				= 0x00000001;
		public static final int Archiving			= 0x00000002;
		public static final int VirtualConversion	= 0x00000004;
		public static final int VMBackup			= 0x00000008;
		public static final int Preferences			= 0x00000010;
		public static final int ScheduledExport		= 0x00000020;
		public static final int Rps					= 0x00000040;
		public static final int Subscription		= 0x00000080;
	}
	
	private int		policyId;
	private String	policyName;
	private int		policyType;
	private int		contentFlag;
	private Date	creationTime;
	private int		usedCount;
	private int productType;
	
	public int getPolicyId()
	{
		return policyId;
	}
	
	public void setPolicyId( int policyId )
	{
		this.policyId = policyId;
	}
	
	public String getPolicyName()
	{
		return policyName;
	}
	
	public void setPolicyName( String policyName )
	{
		this.policyName = policyName;
	}

	public int getPolicyType()
	{
		return policyType;
	}
	
	public void setPolicyType( int policyType )
	{
		this.policyType = policyType;
	}
	
	public int getContentFlag()
	{
		return contentFlag;
	}

	public void setContentFlag( int contentFlag )
	{
		this.contentFlag = contentFlag;
	}

	public Date getCreationTime()
	{
		return creationTime;
	}
	
	public void setCreationTime( Date creationTime )
	{
		this.creationTime = creationTime;
	}
	
	public int getUsedCount()
	{
		return usedCount;
	}
	
	public void setUsedCount( int usedCount )
	{
		this.usedCount = usedCount;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}
}

