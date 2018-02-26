package com.ca.arcserve.edge.app.base.webservice.gateway;

import com.ca.arcserve.edge.app.base.webservice.actioncenter.IActionOwner;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions.InvalidCategoryParamException;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;

public class SiteActionOwner implements IActionOwner
{

	@Override
	public String convertCategoryParamToString( Object categoryParam ) throws InvalidCategoryParamException
	{
		if (!(categoryParam instanceof SiteId))
			throw new InvalidCategoryParamException();
		
		SiteId siteId = (SiteId)categoryParam;
		return Integer.toString( siteId.getRecordId() );
	}

	@Override
	public Object parseCategoryParamString( String string ) throws InvalidCategoryParamException
	{
		try
		{
			int recordId = Integer.parseInt( string );
			return new SiteId( recordId );
		}
		catch (Exception e)
		{
			throw new InvalidCategoryParamException( e );
		}
	}

}
