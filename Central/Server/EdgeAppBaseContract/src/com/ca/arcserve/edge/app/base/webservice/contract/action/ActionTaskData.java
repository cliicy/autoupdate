package com.ca.arcserve.edge.app.base.webservice.contract.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;

@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(ValuePair.class)
public class ActionTaskData<T extends Serializable> implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@XmlElementWrapper(name="totalEntities")
	@XmlElements( {@XmlElement(name="Integer",type=Integer.class)} )
	private List<T> totalEntities = new ArrayList<T>();
	
	@XmlElementWrapper(name="successfullEntities")
	@XmlElements( {@XmlElement(name="Integer",type=Integer.class)} )
	private List<T> successfullEntities = new ArrayList<T>();
	
	private List<ValuePair<T, String>> wanningEntities = new ArrayList<ValuePair<T, String>>();
	
	private List<ValuePair<T, Long>> failedEntities = new ArrayList<ValuePair<T, Long>>();
	
	private String additionInfo;

	public List<T> getTotalEntities() {
		return totalEntities;
	}
	public void setTotalEntities(List<T> totalEntities) {
		this.totalEntities = totalEntities;
	}
	
	public List<T> getSuccessfullEntities() {
		return successfullEntities;
	}
	public void setSuccessfullEntities(List<T> successfullEntities) {
		this.successfullEntities = successfullEntities;
	}
	
	public List<ValuePair<T, String>> getWanningEntities() {
		return wanningEntities;
	}
	public void setWanningEntities(List<ValuePair<T, String>> wanningEntities) {
		this.wanningEntities = wanningEntities;
	}
	public List<ValuePair<T, Long>> getFailedEntities() {
		return failedEntities;
	}
	public void setFailedEntities(List<ValuePair<T, Long>> failedEntities) {
		this.failedEntities = failedEntities;
	}
	public String getAdditionInfo() {
		return additionInfo;
	}
	public void setAdditionInfo(String additionInfo) {
		this.additionInfo = additionInfo;
	}
}
