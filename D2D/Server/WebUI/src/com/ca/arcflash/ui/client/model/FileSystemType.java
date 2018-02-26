package com.ca.arcflash.ui.client.model;

import com.ca.arcflash.ui.client.UIContext;

public final class FileSystemType {
	public static final int EFST_UNKNOWN = 0;   // Unknown Type
	public static final int EFST_RAW = 1;           // Raw volume.
	public static final int EFST_NTFS = 2;
	public static final int EFST_FAT16 = 3;
	public static final int EFST_FAT32 = 4;
	public static final int EFST_EXFAT = 5;
	public static final int EFST_TEXFAT = 6;
	public static final int EFST_HPFS = 7;
	public static final int EFST_REFS = 8;
	
	public static String getDisplayName(int type) {
		switch (type) {
		case EFST_RAW:
			return "RAW";
			
		case EFST_NTFS:
			return "NTFS";
			
		case EFST_FAT16:
			return "FAT";
			
		case EFST_FAT32:
			return "FAT32";
			
		case EFST_EXFAT:
			return "EXFAT";
			
		case EFST_TEXFAT:
			return "TEXFAT";
			
		case EFST_HPFS:
			return "HPFS";
			
		case EFST_REFS:
			return "ReFS";

		default:
			return UIContext.Constants.unknown();
		}
	}
}
