package com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo;

import java.lang.annotation.Annotation;

public class BasicErrorInfo
{
	private Annotation ruleAnnotation;
	
	public BasicErrorInfo()
	{
		this( null );
	}
	
	public BasicErrorInfo( Annotation ruleAnnotation )
	{
		this.ruleAnnotation = ruleAnnotation;
	}

	public Annotation getRuleAnnotation()
	{
		return ruleAnnotation;
	}

	public void setRuleAnnotation( Annotation ruleAnnotation )
	{
		this.ruleAnnotation = ruleAnnotation;
	}
}
