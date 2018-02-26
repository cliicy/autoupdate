/**
 * 
 */
package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.plan;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeVCMConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;

/**
 * @author lijwe02
 * 
 */
public class VSBTaskUtils {
	private static IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);

	public static D2DConnectInfo getD2DConnectInfo(EdgeVCMConnectInfo hostConnectInfo) {
		if(hostConnectInfo==null)
			return null;
		return getD2DConnectInfo(hostConnectInfo.toHostConnectInfo());
	}

	public static D2DConnectInfo getD2DConnectInfo(HostConnectInfo hostConnectInfo) {
		if (hostConnectInfo == null) {
			return null;
		}
		D2DConnectInfo d2dConnectInfo = PolicyManagementServiceImpl.getInstance().new D2DConnectInfo();
		int converterHostId = hostConnectInfo.getHostId();
		if (converterHostId > 0) {
			// Load converter information according to the converter host id
			List<EdgeConnectInfo> infos = new ArrayList<EdgeConnectInfo>();
			connectInfoDao.as_edge_connect_info_list(converterHostId, infos);
			if (infos.size() > 0) {
				EdgeConnectInfo info = infos.get(0);
				d2dConnectInfo.setHostId(converterHostId);
				d2dConnectInfo.setHostName(hostConnectInfo.getHostName());
				d2dConnectInfo.setPort(info.getPort());
				d2dConnectInfo.setProtocol(info.getProtocol() == Protocol.Https.ordinal() ? "HTTPS" : "HTTP");
				d2dConnectInfo.setUsername(info.getUsername());
				d2dConnectInfo.setPassword(info.getPassword());
				d2dConnectInfo.setDomain("");
				d2dConnectInfo.setUuid(info.getUuid());
				d2dConnectInfo.setAuthUuid(info.getAuthUuid());
				d2dConnectInfo.setVcmConverterType(hostConnectInfo.getConverterType());
				d2dConnectInfo.setGatewayId(hostConnectInfo.getGatewayId());
				return d2dConnectInfo;
			}
		}
		d2dConnectInfo.setHostName(hostConnectInfo.getHostName());
		d2dConnectInfo.setPort(hostConnectInfo.getPort());
		d2dConnectInfo.setProtocol(hostConnectInfo.getProtocol() == Protocol.Https ? "HTTPS" : "HTTP");
		d2dConnectInfo.setUsername(hostConnectInfo.getUserName());
		d2dConnectInfo.setPassword(hostConnectInfo.getPassword());
		d2dConnectInfo.setDomain("");
		d2dConnectInfo.setUuid(hostConnectInfo.getUuid());
		d2dConnectInfo.setVcmConverterType(hostConnectInfo.getConverterType());
		d2dConnectInfo.setGatewayId(hostConnectInfo.getGatewayId());
		
		return d2dConnectInfo;
	}

}
