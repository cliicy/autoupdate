package com.ca.arcserve.edge.app.base.webservice.node.filter;

import java.util.LinkedList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;

public enum NodeGroupFilterLoader {
	
	Instance(NodeGroup.Default);	// Refactor to singleton-N if need to split the big SQL of node group filter
	
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	private int groupType;
	
	private NodeGroupFilterLoader(int groupType) {
		this.groupType = groupType;
	}
	
	public static NodeGroupFilterLoader create(NodeGroup group) {
		for (NodeGroupFilterLoader loader : values()) {
			if (loader.groupType == group.getType()) {
				return loader;
			}
		}
		
		return Instance;
	}
	
	public NodeFilterResult load(NodeGroup group) {
		List<IntegerId> ids = new LinkedList<IntegerId>();
		hostMgrDao.as_edge_host_getIdsByGroup(group.getType(), group.getId(), ids);
		return new NodeFilterResult(true, ids);
	}

}
