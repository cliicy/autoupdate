package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.filecopy.FileCopySchedulePanel;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class ArchiveScheduleSettings {
	ArchiveSettingsContent parentWindow;
	private LayoutContainer lcArchiveScheduleContainer;

	ToolTipConfig tipConfig = null;
	ToolTip tip = null;
	private FileCopySchedulePanel schedulePanel;

	public ArchiveScheduleSettings(ArchiveSettingsContent in_archiveSetingsWindow)
	{
		parentWindow = in_archiveSetingsWindow;
	}

	public LayoutContainer render()
	{
		lcArchiveScheduleContainer = new LayoutContainer();
		TableLayout tlArchivePageLayout = new TableLayout();
		tlArchivePageLayout.setWidth("95%");
		lcArchiveScheduleContainer.setLayout(tlArchivePageLayout);

		DisclosurePanel dp = Utils.getDisclosurePanel(UIContext.Constants.ArchiveSchedule());

		schedulePanel = new FileCopySchedulePanel(parentWindow.isArchiveTask());
		dp.add(schedulePanel);

		lcArchiveScheduleContainer.add(dp);

		lcArchiveScheduleContainer.layout();
		return lcArchiveScheduleContainer;
	}

	public void setEditable(boolean editable) {
		schedulePanel.setEnabled(editable);
	}


	public AdvanceSchedule getScheduleData(){
		return schedulePanel.getScheduleData();
	}

	public void setScheduleData(AdvanceSchedule model){
		schedulePanel.setScheduleData(model);	
	}

	public boolean validate(){
		return schedulePanel.validate();
	}
}
