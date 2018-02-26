package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.util.Collection;

/**
 * internal util for collection
 * 
 * @author zhati04
 *
 */
public final class CollectionUtils {
	public static <T> boolean isEmpty(Collection<T> collection){
		return collection!=null && collection.size()==0;
	}
	
	public static <T> boolean isNotEmpty(Collection<T> collection){
		return !isEmpty(collection);
	}
}
