package com.ca.arcflash.ui.client.mount;

public class MountUtils {
	private static int MNT_UNKNOWN = 0;
	private static int MNT_MOUNTED = 0x01;
	private static int MNT_UNMOUNTED =0x02;
	private static int MNT_INVALID = 0x04;
	
	public static boolean isVolumeMounted(int mountFlag){
		return (mountFlag & MNT_MOUNTED) > 0;
	}
	
	public static boolean isDestinationAccess(int mountFlag){
		return (mountFlag & MNT_INVALID) == 0;
	}
}
