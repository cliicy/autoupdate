package com.ca.arcflash.webservice.edge.d2dstatus;

import com.ca.arcflash.webservice.edge.data.d2dstatus.D2DStatusInfo;

public interface ID2DStatusCollector
{
	D2DStatusInfo getStatusInfo( String uuid );
}

