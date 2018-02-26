package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeD2DDao {
	/**
	 * 
	 * @param afguid, the D2D's uuid
	 * @param buildNumber
	 * @param majorVersion
	 * @param minorVersion
	 * @param backupConfigurationXML
	 * @return
	 */
	@StoredProcedure
	int reportBackupConfigurationXML(String afguid,String buildNumber,String majorVersion,String minorVersion,String backupConfigurationXML);
	/**
	 * 
	 * @param afguid
	 * @param buildNumber
	 * @param majorVersion
	 * @param minorVersion
	 * @return the BackupConfiguration XML
	 */
	@StoredProcedure
	int getBackupConfigurationXML(String afguid,String buildNumber,String majorVersion,String minorVersion,@Out String[] backupXML);
}
