package com.ca.arcserve.edge.app.base.webservice.test;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.ca.arcserve.edge.app.asbu.webservice.ASBUServiceImpl;
import com.ca.arcserve.edge.app.asbu.webservice.IASBUService;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ABFuncAuthMode;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUDeviceInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaGroupInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaPool;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUMediaPoolSet;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.ASBUServerInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ArcserveConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;


/**
 * arcserve backup server destination test case
 * 
 * @author zhati04
 */
public class ASBUServiceTestCase extends AbstractTestCase{
	private static final Logger logger = Logger.getLogger(ASBUServiceTestCase.class);
	private static final IASBUService nodeService = new ASBUServiceImpl();
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	
	@Test
	public void testAddASBUServers(){
		try {
			nodeService.createOrUpdateASBUServers(getConnectInfo(), hostName);
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testDeleteDomain(){
		try {
			nodeService.deleteASBUDomain(domainId);
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testGetASBUServerList(){
		List<ASBUServerInfo> servers;
		try {
			servers = nodeService.getASBUServerList(gatewayId, domainId);
			for(ASBUServerInfo server : servers){
				logger.debug("server host name "+server.getHostName());
				logger.debug("domain name is "+server.getDomainName());
			}
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	@Test
	public void testGetASBUMediaGroupList(){
		List<ASBUServerInfo> servers = null;
		try {
			servers = nodeService.getASBUServerList(gatewayId, domainId);
		} catch (EdgeServiceFault e1) {
			e1.printStackTrace();
		}
		if(CollectionUtils.isNotEmpty(servers)){
			List<ASBUMediaGroupInfo> groups;
			try {
				groups = nodeService.getASBUMediaGroupList(domainId);
				for(ASBUMediaGroupInfo group : groups){
					logger.debug("group id is "+group.getId());
					logger.debug("group name is "+group.getName());
					logger.debug("group type is "+group.getType());
				}
				
			} catch (EdgeServiceFault e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	@Test
	public void testGetASBUMediaPoolList(){
		List<ASBUServerInfo> servers = null;
		try {
			servers = nodeService.getASBUServerList(gatewayId, domainId);
		} catch (EdgeServiceFault e1) {
			e1.printStackTrace();
		}
		if(CollectionUtils.isNotEmpty(servers)){
			List<ASBUMediaGroupInfo> groups;
			try {
				groups = nodeService.getASBUMediaGroupList(domainId);
				for(ASBUMediaGroupInfo group : groups){
					logger.debug("group id is "+group.getId());
					logger.debug("group name is "+group.getName());
					logger.debug("group type is "+group.getType());
					List<ASBUMediaPoolSet> mediaPoolSetList = nodeService.getASBUMediaPoolSet(domainId, group.getName());
					for(ASBUMediaPoolSet poolSet : mediaPoolSetList){
						logger.debug("media pool set name id is "+poolSet.getName());
						for(ASBUMediaPool pool : poolSet.getMediaPools()){
							logger.debug("media pool name is "+pool.getName());
							logger.debug("media pool value is "+pool.getValue());
							logger.debug("media pool selected is "+pool.getName());
							logger.debug("media pool type is "+pool.getType());
						}
					}
				}
				
			} catch (EdgeServiceFault e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	@Test
	public void testGetASBUDeviceList(){
		List<ASBUServerInfo> servers = null;
		try {
			servers = nodeService.getASBUServerList(gatewayId, domainId);
		} catch (EdgeServiceFault e1) {
			e1.printStackTrace();
		}
		if(CollectionUtils.isNotEmpty(servers)){
			List<ASBUMediaGroupInfo> groups;
			try {
				groups = nodeService.getASBUMediaGroupList(domainId);
				for(ASBUMediaGroupInfo group : groups){
					logger.debug("group id is "+group.getId());
					logger.debug("group name is "+group.getName());
					logger.debug("group type is "+group.getType());
					List<ASBUDeviceInformation> deviceList = nodeService.getASBUDeviceList(domainId, group.getNumber());
					for(ASBUDeviceInformation device : deviceList){
						logger.debug("device scsiID"+device.getScsiID());
					}
				}
				
			} catch (EdgeServiceFault e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	private ArcserveConnectInfo getConnectInfo() {
		ArcserveConnectInfo connectInfo = new ArcserveConnectInfo();
		connectInfo.setProtocol(Protocol.Http);
		connectInfo.setPort(8018);
		connectInfo.setCauser("caroot");
		connectInfo.setCapasswd("caroot");
		try {
			connectInfo.setGatewayEntity(gatewayService.getGatewayById(gatewayId));
		} catch (EdgeServiceFault e) {
			e.printStackTrace();
		}
		connectInfo.setAuthmode(ABFuncAuthMode.AR_CSERVE);
		return connectInfo;
	}
}
