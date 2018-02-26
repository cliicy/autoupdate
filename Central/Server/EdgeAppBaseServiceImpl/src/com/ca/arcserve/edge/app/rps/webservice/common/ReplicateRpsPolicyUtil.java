package com.ca.arcserve.edge.app.rps.webservice.common;


import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.RPSPolicyWrapper;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsPolicyDao;

public class ReplicateRpsPolicyUtil {
	private final static Logger logger = Logger.getLogger(ReplicateRpsPolicyUtil.class);
	private static Object policyMarshObject = new Object();
	private static IRpsPolicyDao policyDao = DaoFactory.getDao(IRpsPolicyDao.class);
	private static IRpsDataStoreDao dsDao = DaoFactory.getDao(IRpsDataStoreDao.class);
	private static IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	public static void buildSiteName(RPSPolicyWrapper rpsPolicy) {
		if(rpsPolicy.getSiteId()>0){
			try {
				GatewayEntity gateway = gatewayService.getGatewayById(new GatewayId(rpsPolicy.getSiteId()));
				rpsPolicy.setSiteName(gateway.getName());
			} catch (EdgeServiceFault e) {
				logger.error("convertPlanXml gatewayService.getGatewayById id="+rpsPolicy.getSiteId(),e);
			}
		}else {
			try {
				logger.debug("convertPlanXml gatewayService getLocalGateway");
				GatewayEntity gateway = gatewayService.getLocalGateway();
				if(gateway==null)
					return;
				rpsPolicy.setSiteId(gateway.getId().getRecordId());
				rpsPolicy.setSiteName(gateway.getName());
			} catch (Exception e) {
				logger.error("convertPlanXml gatewayService.getGatewayById rpsPolicy.getSiteId() "+rpsPolicy.getSiteId(),e);
			}
		}
	}	

}
