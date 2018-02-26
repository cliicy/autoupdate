package com.ca.arcserve.edge.app.base.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalLink {
	ExternalLinkItem[] value();
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ExternalLinkItem{
		EdgeApplicationType applicationType();
		String resourceKey();
	}
}
