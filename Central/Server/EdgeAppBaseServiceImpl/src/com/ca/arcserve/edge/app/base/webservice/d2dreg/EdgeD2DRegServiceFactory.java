package com.ca.arcserve.edge.app.base.webservice.d2dreg;

import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DRegService;

public class EdgeD2DRegServiceFactory {
	public static IEdgeD2DRegService create() {
		return new EdgeD2DRegServiceImpl();
	}
}
