package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.CancelJobParameter;

@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class ValuePair <K,V> implements Serializable{
	private static final long serialVersionUID = 9026803714020308453L;
	@XmlElements( {@XmlElement(name="Integer",type=Integer.class),@XmlElement(name="CancelJobParameter",type=CancelJobParameter.class)} )
	private K key;
	@XmlElements( {@XmlElement(name="Integer",type=Integer.class),@XmlElement(name="Long",type=Long.class),@XmlElement(name="String",type=String.class)} )
	private V value;
	
	public ValuePair(){
		
	}
	public ValuePair(K key, V val) {
		this.key = key;
		this.value = val;
	}
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public void setValue(V value) {
		this.value = value;
	}
}
