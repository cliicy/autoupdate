package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;

public class GatewayUpdatesInfo
{
	private Version consoleVersion;
	private List<UpdateDownloadFile> updateFiles = new ArrayList<>();
	private String gmPackagePath;
	private String updatePackagePath;
	private String pathOfInstallUpdatesExe;

	public Version getConsoleVersion()
	{
		return consoleVersion;
	}

	public void setConsoleVersion( Version consoleVersion )
	{
		this.consoleVersion = consoleVersion;
	}

	public List<UpdateDownloadFile> getUpdateFiles()
	{
		return updateFiles;
	}

	public void setUpdateFiles( List<UpdateDownloadFile> updateFiles )
	{
		this.updateFiles = updateFiles;
	}

	public String getGmPackagePath()
	{
		return gmPackagePath;
	}

	public void setGmPackagePath( String gmPackagePath )
	{
		this.gmPackagePath = gmPackagePath;
	}

	public String getUpdatePackagePath()
	{
		return updatePackagePath;
	}

	public void setUpdatePackagePath( String updatePackagePath )
	{
		this.updatePackagePath = updatePackagePath;
	}

	public String getPathOfInstallUpdatesExe()
	{
		return pathOfInstallUpdatesExe;
	}

	public void setPathOfInstallUpdatesExe( String pathOfInstallUpdatesExe )
	{
		this.pathOfInstallUpdatesExe = pathOfInstallUpdatesExe;
	}

}
