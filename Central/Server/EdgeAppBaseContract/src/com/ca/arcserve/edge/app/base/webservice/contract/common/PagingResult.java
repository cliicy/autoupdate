package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.ShareFolderDestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredNode;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveredVM;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;

@XmlSeeAlso(value = {DiscoveredNode.class, DiscoveredVM.class, NodeEntity.class, ShareFolderDestinationInfo.class, GatewayEntity.class, ArchiveCloudDestInfo.class})
public class PagingResult<T> implements Serializable {

	private static final long serialVersionUID = -851030004091617306L;
	
	private int startIndex;
	private int totalCount;
	private List<T> data = new ArrayList<T>();
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	
	public static <T> PagingResult<T> create(PagingConfig config, List<T> allData) {
		if (config.getStartIndex() < 0) {
			config.setStartIndex(0);
		}
		
		if (config.getCount() < 0) {
			config.setCount(0);
		}
		
		PagingResult<T> result = new PagingResult<T>();
		result.setTotalCount(allData.size());
		result.setStartIndex(config.getStartIndex());
		
		int endIndex = config.getStartIndex() + config.getCount();
		for (int i = config.getStartIndex(); i < endIndex && i < allData.size(); ++i) {
			result.getData().add(allData.get(i));
		}
		
		return result;
	}

}
