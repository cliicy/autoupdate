package com.ca.arcserve.edge.app.base.webservice.license;

public class EdgeLicenseUtil {
	public static boolean hasFeature(long required_feature, long check_feature) {
		return (required_feature & check_feature)!=0;
	}
	
	public static long addFeature(long current_feature, long need_feature){
		return current_feature | need_feature;
	}

	public static long removeFeature(long current_feature, long remove_feature){
		return current_feature & (~ remove_feature);
	}
}
