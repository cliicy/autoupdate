package com.ca.arcserve.edge.app.base.webservice.node.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ca.arcserve.edge.app.base.appdaos.IntegerId;

public class NodeFilterResult {
	
	public static final NodeFilterResult NotFiltered = new NodeFilterResult(false, null);
	
	private boolean filtered;
	private Set<Integer> filteredHostIds;
	
	public NodeFilterResult(boolean filtered, List<IntegerId> ids) {
		this.filtered = filtered;
		filteredHostIds = new HashSet<Integer>();
		
		if (ids != null) {
			for (IntegerId id : ids) {
				filteredHostIds.add(id.getId());
			}
		}
	}
	
	public void intersect(NodeFilterResult another) {
		if (!another.isFiltered()) {
			return;
		} else if (filtered) {
			filteredHostIds.retainAll(another.getFilteredHostIds());
		} else {
			filtered = true;
			filteredHostIds.clear();
			filteredHostIds.addAll(another.getFilteredHostIds());
		}
	}
	
	public boolean isFiltered() {
		return filtered;
	}
	
	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public Set<Integer> getFilteredHostIds() {
		return filteredHostIds;
	}

	public void setFilteredHostIds(Set<Integer> filteredHostIds) {
		this.filteredHostIds = filteredHostIds;
	}

}
