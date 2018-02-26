package com.ca.arcflash.webservice.edge.d2dstatus;

import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.D2DStatusCollector;
import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.VCMStatusCollector;
import com.ca.arcflash.webservice.edge.d2dstatus.statuscollectors.VSphereStatusCollector;

public class D2DStatusCollectorFactory
{
	public enum StatusInfoType
	{
		D2DStatus,
		VCMStatus,
		vSphereStatus,
	}
	
	private static D2DStatusCollectorFactory instance = null;
	
	public static synchronized D2DStatusCollectorFactory getInstance()
	{
		if (instance == null)
			instance = new D2DStatusCollectorFactory();
		
		return instance;
	}
	
	public ID2DStatusCollector getCollector( StatusInfoType statusInfoType )
	{
		switch (statusInfoType)
		{
		case D2DStatus:
			return D2DStatusCollector.getInstance();
			
		case VCMStatus:
			return VCMStatusCollector.getInstance();
			
		case vSphereStatus:
			return VSphereStatusCollector.getInstance();
		}
		
		return null;
	}
}
