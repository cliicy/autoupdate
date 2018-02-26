package com.ca.arcserve.edge.app.base.webservice.node.filter;

import java.util.LinkedList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;

public enum NodeSortLoader {
	
	Hostname(NodeSortCol.hostname) {
		@Override
		protected void load(String sortColumnName, boolean asc, List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds_by_nodename(asc, ids);
		}
	},
	
	Policyname(NodeSortCol.policy){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds(sortColumnName, asc, ids);
			
		}
		
	},
	
	Vmname(NodeSortCol.vmname){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds_by_vmname(asc, ids);
			
		}
		
	},
	
	Hypervisor(NodeSortCol.vcenter){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds_by_hypervisor(asc, ids);
			
		}
		
	},
	
	Os(NodeSortCol.os){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds(sortColumnName, asc, ids);
			
		}
		
	},
	
	Nodedescription(NodeSortCol.nodeDescription){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds(sortColumnName, asc, ids);
			
		}
		
	},
	
	VerifyStatus(NodeSortCol.verifyStatus){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds(sortColumnName, asc, ids);
			
		}
		
	},
	
	LastD2DBackupResult(NodeSortCol.lastBackupResult){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds(sortColumnName, asc, ids);
			
		}
		
	},
	
	LastD2DBackupTime(NodeSortCol.lastBackupTime){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds(sortColumnName, asc, ids);
			
		}
		
	},
	
	Converter(NodeSortCol.converter){

		@Override
		protected void load(String sortColumnName, boolean asc,
				List<IntegerId> ids) {
			hostMgrDao.as_edge_host_getSortedIds(sortColumnName, asc, ids);
			
		}
		
	};
	
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	private NodeSortCol sortColumn;
	
	private NodeSortLoader(NodeSortCol sortColumn) {
		this.sortColumn = sortColumn;
	}

	public static NodeSortLoader create(NodeSortCol sortColumn) {
		switch (sortColumn) {
		case hostname:
			return Hostname;
		case policy:
			return Policyname;
		case verifyStatus:
			return VerifyStatus;
		case vmname:
			return Vmname;
		case vcenter:
			return Hypervisor;
		case converter:
			return Converter;
		case lastBackupResult:
			return LastD2DBackupResult;
		case lastBackupTime:
			return LastD2DBackupTime;
		case os:
			return Os;
		case nodeDescription:
			return Nodedescription;
		default:
			return Hostname;
		}
	}
	
	protected abstract void load(String sortColumnName, boolean asc, List<IntegerId> ids);

	public List<Integer> load(boolean asc) {
		List<IntegerId> ids = new LinkedList<IntegerId>();
		load(sortColumn.value(), asc, ids);
		
		List<Integer> result = new LinkedList<Integer>();
		for (IntegerId id : ids) {
			result.add(id.getId());
		}
		
		return result;
	}

}
