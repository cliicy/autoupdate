package com.ca.arcflash.ui.client.backup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleSettingsSummaryPanel extends LayoutContainer {

	private LabelField dateField;
	private LabelField timeField;	
	private LabelField incText = new LabelField();
	private LabelField fullText = new LabelField();
	private LabelField resyncText = new LabelField();

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		RowLayout layout = new RowLayout();
		this.setLayout(layout);
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsSchedule());
		label.addStyleName("restoreWizardTitle");
		this.add(label);

		render(this);

	}

	public void render(LayoutContainer container) {
		DisclosurePanel disSettingsPanel;
		disSettingsPanel = Utils.getDisclosurePanel(UIContext.Constants.startDateAndTime());

		LayoutContainer timeContainer = new LayoutContainer();
		timeContainer.add(getTimeWidget());
		timeContainer.add(new Html("<HR>"));
		disSettingsPanel.add(timeContainer);
		container.add(disSettingsPanel);

		String incrementalScheduleDescription = UIContext.Messages
				.scheduleLabelIncrementalDescription(UIContext.productNameD2D);
		LayoutContainer incContainer = new LayoutContainer();
		incContainer.setLayout(new RowLayout());
		incContainer.add(new LabelField(incrementalScheduleDescription));
		incContainer.add(incText);
		
		String fullScheduleDescription = UIContext.Messages.scheduleLabelFullDescription(UIContext.productNameD2D);
		LayoutContainer fullContainer = new LayoutContainer();
		fullContainer.setLayout(new RowLayout());
		fullContainer.add(new LabelField(fullScheduleDescription));
		fullContainer.add(fullText);
		
		String resyncDescription = UIContext.Messages.scheduleLabelResyncDescription(UIContext.productNameD2D);
		LayoutContainer resyncContainer = new LayoutContainer();
		resyncContainer.setLayout(new RowLayout());
		resyncContainer.add(new LabelField(resyncDescription));
		resyncContainer.add(resyncText);
		
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.scheduleLabelIncrementalBackup());
		LayoutContainer lc = new LayoutContainer();
		lc.add(incContainer);
		lc.add(new Html("<HR>"));
		panel.add(lc);
		container.add(panel);

		panel = Utils.getDisclosurePanel(UIContext.Constants.scheduleLabelFullBackup());		
		lc = new LayoutContainer();
		lc.add(fullContainer);
		lc.add(new Html("<HR>"));
		panel.add(lc);
		container.add(panel);

		panel = Utils.getDisclosurePanel(UIContext.Constants.scheduleLabelResyncBackup());
		panel.add(resyncContainer);
		container.add(panel);
	}

	private Widget getTimeWidget() {
		LayoutContainer startTimePanel = new LayoutContainer();
		TableLayout tabLayout = new TableLayout();
		tabLayout.setWidth("90%");
		tabLayout.setCellPadding(1);
		tabLayout.setCellSpacing(1);
		tabLayout.setColumns(4);
		startTimePanel.setLayout(tabLayout);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.startDate());
		label.addStyleName("StartDateSetting");

		dateField = new LabelField();
		TableData tabData = new TableData();
		tabData.setWidth("15%");
		tabData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		startTimePanel.add(label, tabData);

		tabData = new TableData();
		tabData.setWidth("35%");
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePanel.add(dateField, tabData);

		label = new LabelField();
		label.setValue(UIContext.Constants.startTime());
		label.addStyleName("StartTimeSetting");

		tabData = new TableData();
		tabData.setWidth("15%");
		tabData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		startTimePanel.add(label, tabData);

		timeField = new LabelField();
		tabData = new TableData();
		tabData.setWidth("35%");
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		startTimePanel.add(timeField, tabData);
		return startTimePanel;
	}

	public void RefreshData(BackupSettingsModel model) {

		try {
			if (model.getBackupStartTime() > 0) {
				Date backupStartTime = new Date(model.getBackupStartTime());
				if (model.getStartTimezoneOffset() != null) {
					backupStartTime = Utils.serverTimeToLocalTime(backupStartTime, model.getStartTimezoneOffset());
				}
				this.dateField.setValue(FormatUtil.getShortDateFormat().format(backupStartTime));
				this.timeField.setValue(FormatUtil.getShortTimeFormat().format(backupStartTime));
			}

			if (model.incrementalSchedule != null && model.incrementalSchedule.isEnabled()) {
				incText.setValue(UIContext.Messages.repeatEvery(model.incrementalSchedule.getInterval(),
						unitToString(model.incrementalSchedule.getIntervalUnit())));
			} else {
				incText.setValue(UIContext.Constants.scheduleLabelNever());
			}
			
			if (model.fullSchedule != null && model.fullSchedule.isEnabled()) {
				fullText.setValue(UIContext.Messages.repeatEvery(model.fullSchedule.getInterval(),
						unitToString(model.fullSchedule.getIntervalUnit())));
			} else {
				fullText.setValue(UIContext.Constants.scheduleLabelNever());
			}
			
			
			if (model.resyncSchedule != null && model.resyncSchedule.isEnabled()) {
				resyncText.setValue(UIContext.Messages.repeatEvery(model.resyncSchedule.getInterval(),
						unitToString(model.resyncSchedule.getIntervalUnit())));
			} else {
				resyncText.setValue((UIContext.Constants.scheduleLabelNever()));
			}

		} catch (Exception e) {

		}
	}

	private String unitToString(int unit) {
		switch (unit) {
		case ScheduleSubSettings.Minute_Unit:
			return UIContext.Constants.minutes();
		case ScheduleSubSettings.Hour_Unit:
			return UIContext.Constants.hours();
		case ScheduleSubSettings.Day_Unit:
			return UIContext.Constants.days();
		}

		return "";
	}
}
