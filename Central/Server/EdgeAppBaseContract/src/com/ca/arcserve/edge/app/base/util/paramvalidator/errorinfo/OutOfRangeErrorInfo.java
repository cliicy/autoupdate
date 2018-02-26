package com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo;

class OutOfRangeErrorInfo<T> extends BasicErrorInfo
{
	private T minValue;
	private T maxValue;
	private T actualValue;
	
	public T getMinValue()
	{
		return minValue;
	}
	
	public void setMinValue( T minValue )
	{
		this.minValue = minValue;
	}
	
	public T getMaxValue()
	{
		return maxValue;
	}
	
	public void setMaxValue( T maxValue )
	{
		this.maxValue = maxValue;
	}
	
	public T getActualValue()
	{
		return actualValue;
	}
	
	public void setActualValue( T actualValue )
	{
		this.actualValue = actualValue;
	}
	
}
