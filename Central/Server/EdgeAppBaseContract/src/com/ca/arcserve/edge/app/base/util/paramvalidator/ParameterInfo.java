package com.ca.arcserve.edge.app.base.util.paramvalidator;

import java.lang.reflect.Method;

public class ParameterInfo
{
	private Method method;
	private int parameterIndex;
	private Class<?> parameterType;
	
	public ParameterInfo()
	{
		this( null, 0, null );
	}
	
	public ParameterInfo( Method method, int parameterIndex, Class<?> parameterType )
	{
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.parameterType = parameterType;
	}
	
	public Method getMethod()
	{
		return method;
	}
	
	public void setMethod( Method method )
	{
		this.method = method;
	}
	
	public int getParameterIndex()
	{
		return parameterIndex;
	}
	
	public void setParameterIndex( int parameterIndex )
	{
		this.parameterIndex = parameterIndex;
	}
	
	public Class<?> getParameterType()
	{
		return parameterType;
	}
	
	public void setParameterType( Class<?> parameterType )
	{
		this.parameterType = parameterType;
	}
}
