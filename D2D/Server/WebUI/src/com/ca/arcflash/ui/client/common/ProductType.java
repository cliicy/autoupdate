package com.ca.arcflash.ui.client.common;

public enum ProductType {
	D2D(0),D2D_ON_DEMAND(1);
	private Integer value;

	ProductType(Integer v){
		this.value=v;
	}
	public Integer getValue(){
		return value;
	}
}
