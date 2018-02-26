package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BackupSummaryEventListener;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class MergePanel extends ContentPanel 
	implements IRefreshable, BackupSummaryEventListener {
	
	protected HomepageServiceAsync service = GWT.create(HomepageService.class);
	
	private LabelField statusLabel;
	protected Button actionBtn;
	
	protected SelectionListener<ButtonEvent> actionListener;
	protected MergeStatusModel currentStatus;

	protected boolean toResume = false;
	
	public MergePanel(MergeStatusModel model) {
		this.setHeaderVisible(false);
		currentStatus = model;
		TableLayout tl = new TableLayout();
		tl.setColumns(3);
		tl.setWidth("100%");	
//		tl.setCellSpacing(2);
		this.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("93%");
//		this.add(setupLeftContainer(), td);
		setupLeftContainer();
				
		actionBtn = new Button(UIContext.Constants.mergeJobPanelActionButtonResume());
		actionBtn.setMinWidth(80);
		actionBtn.ensureDebugId("9B25D2E8-478C-4a1e-8190-981124982A9A");
		actionBtn.setAutoWidth(true);
		Utils.addToolTip(actionBtn, UIContext.Constants.mergeJobPanelActionButtonResumeToolTip());
//		actionBtn.setEnabled(false);
		actionBtn.addSelectionListener(defineActionListener());
		refreshButton(model);
		td = new TableData();
		td.setPadding(5);
		td.setWidth("7%");
		this.add(actionBtn, td);
	}
	
	protected void setupLeftContainer() {
		TableData td = new TableData();
		td.setWidth("15%");
		
		LabelField jobLabel = new LabelField();
		jobLabel.addStyleName("jobMonitor_value");
		jobLabel.setStyleAttribute("margin-left", "10px");
		jobLabel.setValue(UIContext.Constants.mergeJob());
		add(jobLabel, td);
		
		statusLabel = new LabelField();
		statusLabel.addStyleName("jobMonitor_value");
		statusLabel.setStyleAttribute("padding-left", "0");
		statusLabel.setValue(UIContext.Constants.mergeJobPanelStatusNoJob());
		td = new TableData();
		td.setWidth("78%");
		add(statusLabel, td);
//		return lc;
	}

	
	protected SelectionListener<ButtonEvent> defineActionListener() {
		final BaseAsyncCallback<Integer> resumeCallback = 
			new BaseAsyncCallback<Integer> (){

				@Override
				public void onFailure(Throwable caught) {
					actionBtn.setEnabled(true);
					toResume = false;
					super.onFailure(caught);
				}
				
				@Override
                public void onSuccess(Integer result) {
//					actionBtn.setEnabled(true);
                }
			
		};
		actionListener = new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				actionBtn.setEnabled(false);
				if(currentStatus != null) {
					resumeMerge(resumeCallback);
					toResume = true;
				}
			}
		};
		
		return actionListener;
	}
	
	protected void resumeMerge(BaseAsyncCallback<Integer> callback) {
		service.resumeMerge(null, callback);
	}
	
	protected void refreshButton(MergeStatusModel status) {
		if(status.isRecoverySet() != null && status.isRecoverySet()){
			actionBtn.hide();
			return;
		}
		if(status.canResume())
			actionBtn.setEnabled(!toResume);
		else
			actionBtn.setEnabled(false);
	}
	
	protected void refreshProgress(Integer status) {
		switch(status.intValue()) {
		case MergeStatusModel.RUNNING:
		case MergeStatusModel.PAUSING:
				toResume = false;
//				statusLabel.setText(Utils.getMergePhaseMessage(currentJobMonitor));
				break;
		case MergeStatusModel.FAILED:
		case MergeStatusModel.NOTRUNNING:
		case MergeStatusModel.TO_RUN:
//				toPause = false;
				statusLabel.setValue(Utils.getMergeStatusMessage(MergeStatusModel.FAILED));
				break;
		case MergeStatusModel.PAUSED:
		case MergeStatusModel.PAUSED_NO_SCHEDULE:
//				toPause = false;	
				statusLabel.setValue(Utils.getMergeStatusMessage(status));
				break;
		case MergeStatusModel.PAUSED_MANUALLY:
//				toPause = false;
				statusLabel.setValue(Utils.getMergeStatusMessage(MergeStatusModel.PAUSED_MANUALLY));
				break;
		}
	}
	
	public void refreshUI(MergeStatusModel status) {
		currentStatus = status;
		refreshButton(currentStatus);
		refreshProgress(currentStatus.getStatus());
		/*if(status.isInSchedule())
			refreshProgress(currentStatus.getStatus());
		else
			refreshProgress(MergeStatusModel.PAUSED_NO_SCHEDULE);*/
	}

	@Override
    public void refresh(Object data, int changeSource) {
		
    }

	@Override
    public void refresh(Object data) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void backupSummaryUpdated(BackupInformationSummaryModel model) {
		//No use now
		if(model == null)
			return;
		else if(model.getRecoveryPointCount() != null && model.getRetentionCount() != null
				&& model.getRecoveryPointCount() > model.getRetentionCount()) {
			
		}
    }
	
	
/*	@Override
	protected void onLoad() {
		super.onLoad();
		toResume = false;
	}
	
	@Override
	protected void onUnload() {
		super.onUnload();
		actionBtn.setEnabled(true);
	}
	
	@Override
	protected void onHide() {
		super.onHide();
		toResume = false;
	}
	
	@Override
	protected void onShow() {
		super.onShow();
		actionBtn.setEnabled(true);
//		statusLabel.setText(Utils.getMergeStatusMessage(MergeStatusModel.FAILED));
	}*/
}
