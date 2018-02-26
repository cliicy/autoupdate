package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeASDataSyncSetting;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSettingDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class SyncEnvChecker {
	private static final Logger logger = Logger.getLogger(SyncEnvChecker.class);
	
	private String syncRootFolder = null;

	public int getGdbServerID() {
		return gdbServerID;
	}

	public void setGdbServerID(int gdbServerID) {
		this.gdbServerID = gdbServerID;
	}

	public int getBranchHostID() {
		return branchHostID;
	}

	public void setBranchHostID(int branchHostID) {
		this.branchHostID = branchHostID;
	}

	private int gdbServerID  = 0;
	private int branchHostID = 0;

	public SyncEnvChecker(int gdbServerID, int branchHostID) {
		this.gdbServerID  = gdbServerID;
		this.branchHostID = branchHostID;

		IEdgeSettingDao setDao = DaoFactory.getDao(IEdgeSettingDao.class);
		List<EdgeASDataSyncSetting> settings = new ArrayList<EdgeASDataSyncSetting>();
		setDao.as_edge_asdatasync_setting_get(this.getBranchHostID(), settings);
		if (settings.isEmpty())
			setDao.as_edge_asdatasync_setting_get(0, settings);

		String installPath = getEdgeInstallPath();

		if (settings.isEmpty()) {
			ConfigurationOperator.debugMessage(this.getClass().getName() + ": [" + gdbServerID + "," + branchHostID
					+ "] no any setting information in database.");
			syncRootFolder = installPath;
		} else {
			syncRootFolder = settings.get(0).getSyncFilepath();
		}

		// convert <EdgeHome>\ABC -> .\ABC
		syncRootFolder = syncRootFolder.replace(ConfigurationOperator._SyncRootPathPerfix,
				installPath == null ? "." : installPath);

		//sonle01 20101230, allow to alter arcserve sync temp folder via registry
		String alternativeSyncRootFolder = getAlternativeEdgeSyncFolder();
		if(alternativeSyncRootFolder != null && (!alternativeSyncRootFolder.isEmpty())) {
			syncRootFolder = alternativeSyncRootFolder;
			System.out.print("ARCserveSync: alternative sync folder is set\n");
		}
		else {
			System.out.print("ARCserveSync: alternative sync folder is not set\n");
		}
		
		StringBuilder sb = new StringBuilder(syncRootFolder);

		if (gdbServerID != 0) {
			sb.append(File.separatorChar);
			sb.append(gdbServerID);
		}
		sb.append(File.separatorChar);
		sb.append(branchHostID);
		sb.append(File.separatorChar); // .\ABC\1\1\
		syncRootFolder = sb.toString();
	}
	
	public boolean checkFolder() {
		File f = new File(syncRootFolder);
		return f.exists();
	}

	public boolean createFolder() {
		if (checkFolder()) {
			return true;
		} else {
			File f = new File(syncRootFolder);
			return f.mkdirs();
		}
	}

	public String getFolderPath() {
		return syncRootFolder;
	}

	public boolean RemoveBranchFolder() {
		File f = new File(syncRootFolder);
		return f.delete();
	}

	public boolean RemoveGDBFolder() {
		int pos = syncRootFolder.lastIndexOf(File.separatorChar);
		pos = syncRootFolder.lastIndexOf(File.separatorChar, pos-1);
		String gdbFolder = syncRootFolder.substring(0, pos-1);
		File f = new File(gdbFolder);
		return f.delete();
	}

	public String getEdgeInstallPath() {
		try {
			String edgeInstallPath = CommonUtil.BaseEdgeInstallPath;

			return edgeInstallPath;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String getAlternativeEdgeSyncFolder() {
		String thePath = CommonUtil.getApplicationExtentionKey(WindowsRegistry.VALUE_NAME_ARCserveSyncPath);
		
		return thePath;
	}
}
