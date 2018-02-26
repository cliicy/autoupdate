package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.UIContext;

public final class VolumeStatus {
	public static final int EVS_UNKNOWN = 0;             // Unknown Type
	public static final int EVS_HEALTHY = 1;
	public static final int EVS_FAILED = 2;
	public static final int EVS_FAILED_REDUNDANCY = 3;
	public static final int EVS_FORMATTING = 4;
	public static final int EVS_REGENERATING = 5;
	public static final int EVS_RESYNCHING = 6;
	
	public static String getDisplayName(int type) {
		switch (type) {
		case EVS_HEALTHY:
			return UIContext.Constants.volumeStatusHealthy();
			
		case EVS_FAILED:
			return UIContext.Constants.volumeStatusFailed();
			
		case EVS_FAILED_REDUNDANCY:
			return UIContext.Constants.volumeTypeDynamic();
			
		case EVS_FORMATTING:
			return UIContext.Constants.volumeStatusRedundancy();
			
		case EVS_REGENERATING:
			return UIContext.Constants.volumeStatusRegenerating();
			
		case EVS_RESYNCHING:
			return UIContext.Constants.volumeStatusRecynching();

		default:
			return UIContext.Constants.unknown();
		}
	}

}
