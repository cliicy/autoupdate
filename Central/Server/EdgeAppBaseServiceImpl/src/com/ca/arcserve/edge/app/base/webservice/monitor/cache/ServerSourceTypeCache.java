package com.ca.arcserve.edge.app.base.webservice.monitor.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.asbu.dao.IASBUDao;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.EdgeASBUServer;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail.SourceType;

public class ServerSourceTypeCache
{
	private static ServerSourceTypeCache instance = null;
	private Map<Integer, EdgeHost> serverMap;
	private IEdgeHostMgrDao hostMgrDao;
	private IASBUDao asbuDao;
	private static final Logger logger = Logger.getLogger(ServerSourceTypeCache.class);
	
	private ServerSourceTypeCache()
	{
		this.hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
		this.asbuDao = DaoFactory.getDao(IASBUDao.class);
		this.serverMap = new HashMap<Integer, EdgeHost>();
	}	

	public static synchronized ServerSourceTypeCache getInstance()
	{
		if (instance == null)
			instance = new ServerSourceTypeCache();
		
		return instance;
	}
	
	public synchronized void clear()
	{
		logger.debug("ServerSourceTypeCache clear enter");
		this.serverMap.clear();
		logger.debug("ServerSourceTypeCache clear end");
	}	

	public synchronized void reloadMap(){
		logger.debug("ServerSourceTypeCache reloadMap enter");
		this.serverMap.clear();
		List<EdgeHost> hostList = new ArrayList<EdgeHost>();
		//-- list ARCserve & D2D hosts & RPS 
		hostMgrDao.as_edge_host_list(-1, 1, hostList);
		for (EdgeHost host : hostList) {				
			this.serverMap.put( host.getRhostid(), host );
		}
		hostList.clear();
		//-- list LinuxD2D
		hostMgrDao.as_edge_host_list(-31, 1, hostList);
		for (EdgeHost host : hostList) {				
			this.serverMap.put( host.getRhostid(), host );
		}
		
		// list asbuServer
		List<EdgeASBUServer> asbuList = new ArrayList<EdgeASBUServer>();
		asbuDao.findAllServers(0,asbuList);
		for (EdgeASBUServer asbu : asbuList) {
			EdgeHost host = new EdgeHost();
			host.setRhostid(asbu.getHostId());
			host.setRhostname(asbu.getServerName());
			host.setProtectionTypeBitmap(0x00000100);
			this.serverMap.put( host.getRhostid(), host );
		}
		
		if(logger.isDebugEnabled()){	
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("ServerSourceTypeCache Map list start");
			for (Iterator<Integer> iterator = serverMap.keySet().iterator(); iterator.hasNext();) {
				Integer key = (Integer) iterator.next();
				EdgeHost host = serverMap.get(key);
				sBuilder.append("\n  hostId="+host.getRhostid()+" name="+host.getRhostname()+" Type="+host.getProtectionTypeBitmap());
			}
			sBuilder.append("ServerSourceTypeCache Map list end");
			logger.debug(sBuilder.toString());
		}
		logger.debug("ServerSourceTypeCache reloadMap end");
	}
	
	public synchronized EdgeHost getHostInfo( Integer hostId )
	{
		if (!this.serverMap.containsKey( hostId ))
		{
			this.reloadMap();
			if (this.serverMap.containsKey( hostId ))
				return this.serverMap.get( hostId );
			else
				return null;
		}		
		return this.serverMap.get( hostId );
	}
	
	public SourceType getHostSoureType( Integer hostId )
	{
		EdgeHost host = getHostInfo(hostId);
		if(host!=null){
			switch (host.getProtectionTypeBitmap()) {
			case 0x00000001:	//ProtectionType.WIN_D2D;
				return SourceType.D2D;
			case 0x00000004:	//ProtectionType.RPS;
				return SourceType.RPS;
			case 0x00000080:	//ProtectionType.LINUX_D2D_SERVER;
				return SourceType.LINUXD2D;
			case 0x00000100:	//ProtectionType.ASBUServer;
				return SourceType.ASBU;
			}
		}
		return SourceType.NO_TYPE;
	}
}
