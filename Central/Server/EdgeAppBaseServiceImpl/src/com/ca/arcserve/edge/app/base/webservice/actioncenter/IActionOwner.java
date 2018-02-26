package com.ca.arcserve.edge.app.base.webservice.actioncenter;

import com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions.InvalidCategoryParamException;

public interface IActionOwner
{
	String convertCategoryParamToString( Object categoryParam ) throws InvalidCategoryParamException;
	Object parseCategoryParamString( String string ) throws InvalidCategoryParamException;
}
