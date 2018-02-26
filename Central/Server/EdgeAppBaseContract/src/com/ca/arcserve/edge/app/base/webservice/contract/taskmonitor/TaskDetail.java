package com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.ca.arcflash.rps.webservice.data.datastore.DataStoreStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskData;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;

//@XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class TaskDetail<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;

	private long code;
	private String message;
	@XmlElements( {@XmlElement(name="DeployTargetDetail",type=DeployTargetDetail.class),@XmlElement(name="DataStoreStatus",type=DataStoreStatus.class),
		   @XmlElement(name="DiscoveryHistory",type=DiscoveryHistory.class),@XmlElement(name="ActionTaskData",type=ActionTaskData.class)} )
	private  T rawData;

	public long getCode() {
		return code;
	}
	public void setCode(long code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public T getRawData() {
		return rawData;
	}
	public void setRawData( T rawData) {
		this.rawData = rawData;
	}
	
}
