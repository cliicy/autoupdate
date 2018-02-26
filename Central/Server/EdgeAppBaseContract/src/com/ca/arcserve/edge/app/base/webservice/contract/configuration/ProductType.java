/**
 * Created on Aug 14, 2011 11:01:27 PM
 */
package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryApplication;

/**
 * @author Administrator
 * 
 */
public class ProductType {
	public static final int D2D = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D.getValue();
	public static final int D2DOD = DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2DOD.getValue();
	public static final String ProductD2D = "0";
	public static final String ProductD2DOD = "1";
	public static final String ProductRPS = "2";
}
