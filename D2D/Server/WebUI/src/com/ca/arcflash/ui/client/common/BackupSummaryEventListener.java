package com.ca.arcflash.ui.client.common;

import java.util.EventListener;

import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;

public interface BackupSummaryEventListener extends EventListener {
	void backupSummaryUpdated(BackupInformationSummaryModel model);
}
