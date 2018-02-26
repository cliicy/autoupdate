package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.D2DSettingModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

public class ScheduleSummaryWindow extends Window {
	private ScheduleSummaryPanel advSchedulePanel;
	public ScheduleSummaryPanel getAdvSchedulePanel() {
		return advSchedulePanel;
	}

	private ScheduleSettingsSummaryPanel simplePanel;
	private CardLayout layout = new CardLayout();

	private Button expandAllButton;
	private Button collapseAllButton;
	
	private LayoutContainer container = new LayoutContainer();
	
	public ScheduleSummaryWindow() {
		this.setWidth(840);
		this.setHeight(600);
		this.setHeadingHtml(UIContext.Constants.scheduleSummary());
		this.setLayout(new FitLayout());
		layout.setDeferredRender(false);		
		container.setLayout(layout);
		advSchedulePanel = new ScheduleSummaryPanel();
		simplePanel = new ScheduleSettingsSummaryPanel();		
		container.add(new Html(""));
		container.add(advSchedulePanel);
		container.add(simplePanel);		
		this.add(container);
		addButtons();	
	}
	
	public void showWidget(Component panel){
		layout.setActiveItem(panel);
	}

	private void addButtons() {
		expandAllButton = new Button();
		expandAllButton.setText(UIContext.Constants.scheduleSummaryExpandAll());
		expandAllButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				advSchedulePanel.getTreeGrid().expandAll();
			}
		});

		this.addButton(expandAllButton);

		collapseAllButton = new Button();
		collapseAllButton.setText(UIContext.Constants.scheduleSummaryCollapseAll());
		collapseAllButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				advSchedulePanel.getTreeGrid().collapseAll();
			}
		});

		this.addButton(collapseAllButton);

		Button closeButton = new Button();
		closeButton.setText(UIContext.Constants.close());
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ScheduleSummaryWindow.this.hide();
			}
		});

		this.addButton(closeButton);

	}

	private void setButtonVisible(boolean isVisible) {
		if (isVisible) {
			expandAllButton.show();
			collapseAllButton.show();
		} else {
			expandAllButton.hide();
			collapseAllButton.hide();
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		container.mask(UIContext.Constants.loadingIndicatorText());
		final LoginServiceAsync service = GWT.create(LoginService.class);
		service.getD2DConfiguration(new BaseAsyncCallback<D2DSettingModel>() {
			public void onFailure(Throwable caught) {
				container.unmask();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(D2DSettingModel result) {				
				if (result != null && result.getBackupSettingsModel() != null
						&& result.getBackupSettingsModel().getBackupDataFormat() != null
						&& result.getBackupSettingsModel().getBackupDataFormat() == 0) {
					setButtonVisible(false);
					layout.setActiveItem(simplePanel);
					simplePanel.RefreshData(result.getBackupSettingsModel());
				} else {
					setButtonVisible(true);
					layout.setActiveItem(advSchedulePanel);
					advSchedulePanel.RefreshData(result.getBackupSettingsModel());
				}
				
				container.unmask();

			}
		});

	}

}