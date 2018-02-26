package com.ca.arcflash.ui.client.backup;

public class BackupContext {
	
	private static boolean isFileCopyEnable;
	
	private static ArchiveSourceSettings archiveSourceSettings;
	
	public static void destory(){
		archiveSourceSettings = null;
	}

	public static boolean isFileCopyEnable() {
		return isFileCopyEnable;
	}

	public static void setFileCopyEnable(boolean isFileCopyEnable) {
		BackupContext.isFileCopyEnable = isFileCopyEnable;
	}

	public static ArchiveSourceSettings getArchiveSourceSettings() {
		return archiveSourceSettings;
	}

	public static void setArchiveSourceSettings(
			ArchiveSourceSettings archiveSourceSettings) {
		BackupContext.archiveSourceSettings = archiveSourceSettings;
	}
	
}
