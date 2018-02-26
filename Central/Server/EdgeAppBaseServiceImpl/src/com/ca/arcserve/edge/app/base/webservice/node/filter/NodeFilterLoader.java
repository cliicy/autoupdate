package com.ca.arcserve.edge.app.base.webservice.node.filter;

import java.util.LinkedList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.BitmapFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.CommonNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter.NodeFilterType;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public enum NodeFilterLoader {
	
	Common(NodeFilterType.Common) {
		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof CommonNodeFilter;
		}
		
		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			CommonNodeFilter filter = (CommonNodeFilter) nodeFilter;
			hostMgrDao.as_edge_host_getIdsByFilter(filter.getNodeNamePattern(), filter.getApplicationBitmap(), filter.getOsBitmap(), filter.getHostTypeBitmap(), ids);
		}
	},
	JobStatus(NodeFilterType.JobStatus) {
		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof BitmapFilter;
		}

		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			BitmapFilter filter = (BitmapFilter) nodeFilter;
			hostMgrDao.as_edge_host_getIdsByJobStatus(filter.getBitmap(), ids);
		}
	},
	PlanProtectionType(NodeFilterType.PlanProtectionType) {
		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof BitmapFilter;
		}

		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			BitmapFilter filter = (BitmapFilter) nodeFilter;
			hostMgrDao.as_edge_host_getIdsByProtectionType(filter.getBitmap(), ids);
		}
	},
	NodeStatus(NodeFilterType.NodeStatus) {
		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof BitmapFilter;
		}

		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			BitmapFilter filter = (BitmapFilter) nodeFilter;
			hostMgrDao.as_edge_host_getIdsByNodeStatus(filter.getBitmap(), ids);
		}
	},
	RemoteDeployStatus(NodeFilterType.RemoteDeployStatus) {
		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof BitmapFilter;
		}

		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			BitmapFilter filter = (BitmapFilter) nodeFilter;
			String deployParameter = nodeService.getRemoteDeployParam(filter.getBitmap());
			hostMgrDao.as_edge_host_getIdsByRemoteDeployStatus(filter.getBitmap(), deployParameter, ids);
		}
	},
	NotNullField(NodeFilterType.NotNullField) {
		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof BitmapFilter;
		}

		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			BitmapFilter filter = (BitmapFilter) nodeFilter;
			hostMgrDao.as_edge_host_getIdsByNotNullField(filter.getBitmap(), ids);
		}
	},
	LastBackupStatus(NodeFilterType.LastBackupStatus){

		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof BitmapFilter;
		}

		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			BitmapFilter filter = (BitmapFilter) nodeFilter;
			hostMgrDao.as_edge_host_getIdsByLastBackupStatus(filter.getBitmap(), ids);
		}
		
	},
	Gateway(NodeFilterType.GateWay){

		@Override
		protected boolean validate(NodeFilter filter) {
			return filter instanceof BitmapFilter;
		}

		@Override
		protected void load(NodeFilter nodeFilter, List<IntegerId> ids) {
			BitmapFilter filter = (BitmapFilter) nodeFilter;
			hostMgrDao.as_edge_host_getIdsBygateway(filter.getBitmap(), ids);
		}
		
	};
	
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private static NodeServiceImpl nodeService = new NodeServiceImpl();
	
	private NodeFilterType filterType;
	
	private NodeFilterLoader(NodeFilterType filterType) {
		this.filterType = filterType;
	}

	public static NodeFilterLoader create(NodeFilterType type) {
		for (NodeFilterLoader loader : values()) {
			if (loader.filterType == type) {
				return loader;
			}
		}
		
		return NodeFilterLoader.Common;
	}

	public NodeFilterResult load(NodeFilter filter) {
		if (filter.getType() != filterType || !filter.isEnabled() || !validate(filter)) {
			return NodeFilterResult.NotFiltered;
		} else {
			List<IntegerId> ids = new LinkedList<IntegerId>();
			load(filter, ids);
			return new NodeFilterResult(true, ids);
		}
	}
	
	protected abstract boolean validate(NodeFilter filter);
	protected abstract void load(NodeFilter nodeFilter, List<IntegerId> ids);

}
