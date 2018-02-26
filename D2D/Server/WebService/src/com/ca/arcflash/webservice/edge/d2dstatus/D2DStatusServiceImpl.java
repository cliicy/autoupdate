package com.ca.arcflash.webservice.edge.d2dstatus;

import com.ca.arcflash.webservice.edge.d2dstatus.D2DStatusCollectorFactory.StatusInfoType;
import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;

public class D2DStatusServiceImpl implements ID2DStatusService
{
	private static D2DStatusServiceImpl instance = null;
	
	//////////////////////////////////////////////////////////////////////////

	public static synchronized D2DStatusServiceImpl getInstance()
	{
		if (instance == null)
			instance = new D2DStatusServiceImpl();
		
		return instance;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public D2DStatusInfo getD2DStatusInfo()
	{
		ID2DStatusCollector statusCollector =
			D2DStatusCollectorFactory.getInstance().getCollector( StatusInfoType.D2DStatus );
		return statusCollector.getStatusInfo( null );
	}
	
	//////////////////////////////////////////////////////////////////////////

	@Override
	public D2DStatusInfo getVCMStatusInfo( String uuid )
	{
		ID2DStatusCollector statusCollector =
			D2DStatusCollectorFactory.getInstance().getCollector( StatusInfoType.VCMStatus );
		return statusCollector.getStatusInfo( uuid );
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public D2DStatusInfo getVSphereVMStatusInfo( String vmInstanceUuid )
	{
		ID2DStatusCollector statusCollector =
			D2DStatusCollectorFactory.getInstance().getCollector( StatusInfoType.vSphereStatus );
		return statusCollector.getStatusInfo( vmInstanceUuid );
	}

}
