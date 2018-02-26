package com.ca.arcserve.edge.app.base.webservice.contract.vcm;

import java.io.Serializable;
import java.util.List;

public class ImportVSphereVMResult implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int returnCode;
	private int importedNodes;
	private List<DuplicatedVM> duplicatedVMs;
	
	public int getReturnCode()
	{
		return returnCode;
	}

	public void setReturnCode( int returnCode )
	{
		this.returnCode = returnCode;
	}

	public int getImportedNodes()
	{
		return importedNodes;
	}
	
	public void setImportedNodes( int importedNodes )
	{
		this.importedNodes = importedNodes;
	}
	
	public List<DuplicatedVM> getDuplicatedVMs()
	{
		return duplicatedVMs;
	}
	
	public void setDuplicatedVMs( List<DuplicatedVM> duplicatedVMs )
	{
		this.duplicatedVMs = duplicatedVMs;
	}
}
