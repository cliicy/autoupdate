package com.ca.arcserve.edge.app.base.webservice.node.hypervisor;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHypervisorDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;

public class DefaultHypervisorSpecifier extends HypervisorSpecifier {
	
	private IEdgeHypervisorDao hypervisorDao = DaoFactory.getDao(IEdgeHypervisorDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);

	@Override
	protected LicenseMachineType getMachineType() {
		return LicenseMachineType.Other;
	}

	@Override
	protected List<Result> doSpecify(Hypervisor hypervisor, List<EdgeHost> hosts) {
		List<Result> results = new ArrayList<Result>();
		
		for (EdgeHost host : hosts) {
			hypervisorDao.as_edge_hypervisor_vm_update(host.getRhostid(), hypervisor.getServerName(), hypervisor.getSocketCount());
			removeLicense(host);
			results.add(new Result(LogAddEntity.create(Severity.Information, host.getRhostid(),
					EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_OtherSucceed", hypervisor.getServerName()))));
		}
		
		return results;
	}

	@Override
	protected String getSpecifyBeginMessage(Hypervisor hypervisor) {
		return EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_OtherBegin", hypervisor.getServerName());
	}

	@Override
	protected String getSpecifyEndMessage(Hypervisor hypervisor) {
		return EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_OtherEnd", hypervisor.getServerName());
	}

	@Override
	protected void testConnection(Hypervisor hypervisor) throws EdgeServiceFault {
		List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();
		esxDao.as_edge_esx_getByName(hypervisor.getGatewayId().getRecordId(), hypervisor.getServerName(), esxList);
		if (!esxList.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_OtherHypervisorIsEsx, "The specified hypervisor is an existing vCenter/ESX server.");
		}
		
		List<EdgeHyperV> hyperVList = new ArrayList<EdgeHyperV>();
		hyperVDao.as_edge_hyperv_getByName(hypervisor.getGatewayId().getRecordId(), hypervisor.getServerName(), hyperVList);
		if (!hyperVList.isEmpty()) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_OtherHypervisorIsHyperV, "The specified hypervisor is an existing Hyper-V server.");
		}
	}

}
