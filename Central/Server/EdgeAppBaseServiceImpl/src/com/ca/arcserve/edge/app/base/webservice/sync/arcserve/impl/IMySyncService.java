package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import javax.xml.ws.Holder;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfBranchSiteInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncTranInfo;

public interface IMySyncService {

	void transferData(Holder<SyncFileType> syncFileInfo,Holder<byte[]> buffer);
	void incrementalSyncDataTransfer(Holder<SyncTranInfo> syncFileInfo, Holder<byte[]> transferDataResult);
	Boolean unRegisterBranchServer(String branchServeName);
	ArrayOfBranchSiteInfo enumBranchServer();
	ArrayOfstring getSyncFileList();
	Integer syncIncrementalEnd(SyncTranInfo syncInfo, Long lastID);
	void syncGDBDatabase(Integer edgeHostid, Integer branchid,Holder<Integer> timeoffset, Holder<Integer> result);
	ArrayOfstring syncFileList(Integer edgeHostid, Integer branchid);
	void transferDataWithBase64(Holder<SyncFileType> syncFileInfo, Holder<String> transferDataWithBase64Result);
	void syncFileEnd(String fileName);
	void fullDumpDataBase(Holder<Integer> timeoffset,Holder<Integer> fullDumpDataBaseResult);			
}
