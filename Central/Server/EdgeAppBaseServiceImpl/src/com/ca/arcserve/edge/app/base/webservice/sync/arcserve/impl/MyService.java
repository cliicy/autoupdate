package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcserve.edge.app.base.appdaos.EdgeArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfBranchSiteInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ArrayOfstring;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.ISyncService;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncFileType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.client.SyncTranInfo;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class MyService implements IMySyncService{

	public ISyncService stock;
	int utcOffset = 0;
	private String arcGuid = null;
	
	public MyService (String ServiceUrl, EdgeArcserveConnectInfo info) {
		URL url = null;
		Calendar d = Calendar.getInstance();
		utcOffset = (d .get(Calendar.ZONE_OFFSET) + d .get(Calendar.DST_OFFSET)) / (60 * 1000);
		
		try {
			if (ServiceUrl == null) {
				url = new URL(
						"http://localhost:9999/SyncService/metadata");
			} else {
				url = new URL(ServiceUrl);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			ConfigurationOperator.debugMessage(e.getMessage(), e);
		}

		SyncServiceImpl service1 =   new SyncServiceImpl(url, new QName(
				"http://tempuri.org/", "SyncServiceImpl"));
		stock = service1.getBasicHttpBindingISyncService();
				
		ABFuncAuthMode arcserveAuthMode = ABFuncAuthMode.values()[info
				.getAuthmode()];
		arcGuid = stock.connectARCserve(info.getCauser(), info.getCapasswd(),
				arcserveAuthMode);			
	}

	public void transferData(Holder<SyncFileType> syncFileInfo,
			Holder<byte[]> buffer) {
		try
		{
			stock.transferData(arcGuid, syncFileInfo, buffer);
		}catch (SOAPFaultException sofe) 
		{	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		}
	}

	public void incrementalSyncDataTransfer(Holder<SyncTranInfo> syncFileInfo,
			Holder<byte[]> transferDataResult) {
		if(syncFileInfo != null && syncFileInfo.value != null)
			syncFileInfo.value.setUTCOffset(utcOffset);
		try
		{			
			stock.incrementalSyncDataTransfer(arcGuid, syncFileInfo, transferDataResult);
		}
		catch (SOAPFaultException sofe) 
		{	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		}
	}
	
	public Boolean unRegisterBranchServer(
	        String branchServeName) {
		
		boolean bRet = false;		
		try
		{
			bRet = stock.unRegisterBranchServer(arcGuid, branchServeName);
		}		
		catch (SOAPFaultException sofe) 
		{	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		}
		return bRet; 
	}
	
	 public ArrayOfBranchSiteInfo enumBranchServer(){
		ArrayOfBranchSiteInfo aryInfo = null;
		try
		{
			aryInfo = stock.enumBranchServer(arcGuid);
		}
		catch (SOAPFaultException sofe) 
		{	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		}
		return aryInfo; 
	 }
	 
	 public ArrayOfstring getSyncFileList()
	 {
		 ArrayOfstring aryStr = null;
		 try
		 {
			 aryStr = stock.getSyncFileList(arcGuid);
		 }
		 catch (SOAPFaultException sofe) 
		 {	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		 }
		 return aryStr; 
	 }
	 
	public Integer syncIncrementalEnd(SyncTranInfo syncInfo, Long lastID)
	{
		Integer iRet = 0;
		if(syncInfo != null)
			syncInfo.setUTCOffset(utcOffset);
		try
		{
			iRet = stock.syncIncrementalEnd(arcGuid, syncInfo, lastID);
		}
		 catch (SOAPFaultException sofe) 
		 {	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		 }
		 return iRet; 
		
	}

	public void syncGDBDatabase(Integer edgeHostid, Integer branchid,
			Holder<Integer> timeoffset, Holder<Integer> result) {
		if(timeoffset != null)
			timeoffset.value = utcOffset;
		try
		{
			stock.syncGDBDatabase(arcGuid, edgeHostid, branchid, timeoffset, result);
		}		
		 catch (SOAPFaultException sofe) 
		 {	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		 }		  
	}

	public ArrayOfstring syncFileList(Integer edgeHostid, Integer branchid) {
		ArrayOfstring aryStr = null;
		try
		{
			aryStr = stock.syncFileList(arcGuid, edgeHostid, branchid);			
		}
		catch (SOAPFaultException sofe) 
		 {	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		 }
		return aryStr;
	}

	public void transferDataWithBase64(Holder<SyncFileType> syncFileInfo,
			Holder<String> transferDataWithBase64Result) {
		try
		{
			stock.transferDataWithBase64(arcGuid, syncFileInfo,
					transferDataWithBase64Result);
		}
		catch (SOAPFaultException sofe) 
		 {	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		 }
	}
	public void syncFileEnd(String fileName)
	{
		try
		{
			stock.syncFileEnd(arcGuid, fileName);
		}
		catch (SOAPFaultException sofe) 
		 {	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		 }
	}

	public void fullDumpDataBase(Holder<Integer> timeoffset,
			Holder<Integer> fullDumpDataBaseResult) {
		// TODO Auto-generated method stub
		if(timeoffset != null)
			timeoffset.value = utcOffset;		
		try
		{
			stock.fullDumpDataBase(arcGuid, timeoffset, fullDumpDataBaseResult);
		}
		catch (SOAPFaultException sofe) 
		 {	
			ConfigurationOperator.debugMessage(sofe.getMessage(), sofe);
		 }
	}
}
