/**
 * @(#)CommonUtil.java 8/19/2011
 * Copyright 2011 CA Technologies, Inc. All rights reserved.
 */
package com.ca.arcserve.edge.app.rps.webservice.common;

import com.ca.arcflash.rps.webservice.IRPSRegisterService;
import com.ca.arcflash.rps.webservice.registration.RPSRegInfo;
import com.ca.arcserve.edge.app.base.util.CommonUtil;

/**
 * Class<code>CommonUtil</code> is responsible for providing common utility function to
 * another modules using.
 *
 * @author lijbi02
 * @version 1.0 08/19/2011
 * @since JDK1.6
 */
public class RpsCommonUtil {
	
	public static  Boolean isTheRPSServerManagedByCurrentApp(IRPSRegisterService regSrv) {
		if (regSrv == null) return Boolean.FALSE;
		
		RPSRegInfo regInfo = regSrv.getEdgeRpsRegInfo();
		if (regInfo == null) return Boolean.FALSE;
		
		return regInfo.getRpsAppUUID().equalsIgnoreCase(
				CommonUtil.retrieveCurrentAppUUID());
	}
}
