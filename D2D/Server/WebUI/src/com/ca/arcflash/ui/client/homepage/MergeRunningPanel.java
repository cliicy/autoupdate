package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.MergeJobDetailWindow.IPauseAction;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.MergeJobMonitorModel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MergeRunningPanel extends ContentPanel {
	protected HomepageServiceAsync service = GWT.create(HomepageService.class);
	
	private LoadingStatus jobStatus;
	private LabelField estimateTimeLabel;
	private LabelField estimateTimeLabelField;
	private LabelField progressLabel;
	private ProgressBar bar;
	protected Button detailBtn;
	protected Button actionBtn;
	protected boolean toPause = false;
	private MergeJobDetailWindow detailWindow;
	private SelectionListener<ButtonEvent> detailListener;
	private MergeJobMonitorModel currentJobMonitor;
	private MergeStatusModel currentStatus;
	protected SelectionListener<ButtonEvent> actionListener;
	
	private String estimatedTime = UIContext.Constants.NA();

	public MergeRunningPanel(MergeStatusModel model) {
		this.setHeaderVisible(false);		
		currentStatus = model;
		TableLayout tl = new TableLayout();
		tl.setColumns(3);
		tl.setWidth("100%");	
		tl.setCellSpacing(2);
		this.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("86%");
		this.add(setupLeftContainer(), td);
				
		actionBtn = new Button(UIContext.Constants.mergeJobPanelActionButtonPause());
		actionBtn.setMinWidth(80);
		actionBtn.ensureDebugId("CF10728A-93D0-40db-B5C6-09AF49D90026");
		actionBtn.setAutoWidth(true);
		if(currentStatus != null && currentStatus.getStatus() != null){
			enableActionButton(currentStatus.getStatus());
		}
		Utils.addToolTip(actionBtn, UIContext.Constants.mergeJobPanelActionButtonPauseToolTip());
//		actionBtn.setEnabled(false);
		actionBtn.addSelectionListener(defineActionListener());
		td = new TableData();
		td.setWidth("7%");
		this.add(actionBtn, td);
		detailBtn = new Button(UIContext.Constants.mergeJobPanelDetailButton());
		detailBtn.setMinWidth(80);
		detailBtn.ensureDebugId("A17EE267-BB6C-41b9-BD3C-003FF1877554");
		detailBtn.setAutoWidth(true);
		detailBtn.addSelectionListener(defineDetailListener());
		Utils.addToolTip(detailBtn, UIContext.Constants.mergeJobPanelDetailButtonToolTip());
		td = new TableData();
		td.setWidth("7%");
		this.add(detailBtn, td);
//		detailBtn.setEnabled(false);	
	}
	
	protected LayoutContainer setupLeftContainer() {
		LayoutContainer mergeRunningContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(5);
		tl.setWidth("100%");
//		tl.setCellSpacing(2);
		mergeRunningContainer.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("18%");
		td.setMargin(2);
		jobStatus = new LoadingStatus();
		jobStatus.getLoadingLabelField().setStyleAttribute("font-size", "12px");
		jobStatus.getLoadingLabelField().setStyleAttribute("padding-left","0px");
		jobStatus.setLoadingMsg(UIContext.Constants.mergeJob());
		mergeRunningContainer.add(jobStatus, td);

		td = new TableData();
		td.setWidth("22%");
		estimateTimeLabelField = new LabelField();
		estimateTimeLabelField.setValue(UIContext.Constants.jobMonitorEstimatedTime());
		estimateTimeLabelField.setStyleName("jobMonitor_label");
		estimateTimeLabelField.setStyleAttribute("margin-left", "0px");
		mergeRunningContainer.add(estimateTimeLabelField, td);

		if(currentStatus.jobMonitor.isVHDMerge() != null && currentStatus.jobMonitor.isVHDMerge()) {
			estimateTimeLabelField.setValue(UIContext.Constants.jobMonitorElapsedTime());
		}
		
		td = new TableData();
		td.setWidth("10%");
		td.setMargin(2);
		estimateTimeLabel = new LabelField();
		estimateTimeLabel.setValue(UIContext.Constants.NA());
		estimateTimeLabel.setStyleName("jobMonitor_value");
		mergeRunningContainer.add(estimateTimeLabel, td);

		if(currentStatus.jobMonitor.isVHDMerge() != null
				&& currentStatus.jobMonitor.isVHDMerge()) {
			progressLabel = new LabelField();
			progressLabel.addStyleName("jobMonitor_value");
			td = new TableData();
			td.setWidth("50%");
			mergeRunningContainer.add(progressLabel, td);
		}else {
			bar = new ProgressBar() {
				@Override
				public void updateText(String text) {
					text = text != null ? text : "&#160;";
					if (rendered) {
						El inner = el().firstChild();
						El textBackElem = inner.childNode(1);// .firstChild();
						textBackElem.setInnerHtml(text);
					}
				}

				@Override
				public ProgressBar updateProgress(double value, String text) {
					super.updateProgress(value, text);
					String w = value * 100 + "%";
					if (rendered)
						// Set progress width to a relative value.
						el().firstChild().firstChild().setWidth(w);
					return this;
				}
			};
			td = new TableData();
			td.setWidth("50%");
			mergeRunningContainer.add(bar, td);
		}
		
		return mergeRunningContainer;
	}

	protected SelectionListener<ButtonEvent> defineActionListener() {
		final BaseAsyncCallback<Integer> pauseCallback = 
			new BaseAsyncCallback<Integer> (){

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Pause failure");
					actionBtn.setEnabled(true);
					toPause = false;
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
				toPause = true;
				final Button clickedBtn = ce.getButton();				
				Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>(){
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							pauseMerge(pauseCallback);
						}else {
							actionBtn.setEnabled(true);
							clickedBtn.setEnabled(true);
							toPause = false;
						}
					}
				};
				
				popupMessage(UIContext.Messages.mergejobPanelPauseButtonWarning(), 
							UIContext.Messages.messageBoxTitleWarning(Utils.getProductName()), 
							MessageBox.WARNING, MessageBox.YESNO, listener);
				}
			
		};
		
		return actionListener;
	}
	
	public void refreshUI(MergeStatusModel model) {
		if(model != null && model.jobMonitor != null 
				&& model.jobMonitor.getJobPhase() == MergeJobMonitorModel.JobPhase.EJP_MERGE_SESS.ordinal()
				&& (currentJobMonitor == null || currentJobMonitor.getJobPhase() != MergeJobMonitorModel.JobPhase.EJP_MERGE_SESS.ordinal())){
			refreshHostPage(IRefreshable.CS_MERGEJOB_STARTED);
		}else if(model != null && model.jobMonitor != null && model.jobMonitor.isVHDMerge() 
				&& currentJobMonitor != null && currentJobMonitor.getSessCntMerged() != model.jobMonitor.getSessCntMerged()){
			refreshHostPage(IRefreshable.CS_MERGEJOB_STARTED);
		}
		currentJobMonitor = model.jobMonitor;
		currentStatus = model;
		computeEstimatedTime(currentJobMonitor);
		
		refreshButton(model.getStatus());
		refreshProgress();
		
		if(detailWindow != null && detailWindow.isVisible()) {
			detailWindow.refreshUI(model);
		}
	}
	
	public MergeJobMonitorModel getCurrentJobMonitor() {
		return currentJobMonitor;
	}
	
	protected void refreshProgress(){
		if(currentJobMonitor != null) {
			if(bar != null){
				Utils.updateMergeProgress(bar, currentJobMonitor);
				estimateTimeLabel.setValue(getEstimatedTime());
			}else {
				estimateTimeLabel.setValue(Utils.milseconds2String(currentJobMonitor.getElapsedTime()));
				Utils.updateVHDMergeProgress(progressLabel, currentJobMonitor);				
			}
		}else {
			estimateTimeLabel.setValue(UIContext.Constants.NA());
			bar.updateProgress(0.0026,""); 
		}
	}
	
	protected void pauseMerge(AsyncCallback<Integer> callback) {
		service.pauseMerge(null, callback);
	}
	
	public void clear() {
		estimatedTime = UIContext.Constants.NA();
		currentJobMonitor = null;
		bar.reset();
		toPause = false;
	}
	
	public void onHide() {
		super.onHide();
		toPause = false;
//		actionBtn.setEnabled(true);
		if(UIContext.hostPage != null)
			UIContext.hostPage.refresh(null, IRefreshable.CS_MERGEJOB_FINISHED);
		if(detailWindow != null && detailWindow.isVisible()) {
			detailWindow.hide();
		}
	}
	
	@Override
	protected void onUnload() {
		super.onUnload();
		toPause = false;
//		actionBtn.setEnabled(true);
		refreshHostPage(IRefreshable.CS_MERGEJOB_FINISHED);
		if(detailWindow != null && detailWindow.isVisible()) {
			detailWindow.hide();
		}
	}
	
	protected void refreshHostPage(final int refreshSource) {		
		if(UIContext.hostPage != null){
			if(refreshSource == IRefreshable.CS_MERGEJOB_STARTED) {
				service.getBackupInforamtionSummary(new BaseAsyncCallback<BackupInformationSummaryModel>(){

					@Override
					public void onFailure(Throwable caught) {
						//do nothing
					}

					@Override
					public void onSuccess(BackupInformationSummaryModel result) {
						UIContext.hostPage.refresh(result, refreshSource);
					}	
				});
			}else {
				UIContext.hostPage.refresh(null, refreshSource);
			}
		}
	}
	
	protected void refreshButton(int status) {
		if(currentStatus.isRecoverySet() != null && currentStatus.isRecoverySet()) {
			actionBtn.hide();
			return;
		}
		
		enableActionButton(status);
		detailBtn.setEnabled(true);
	}
	
	private void enableActionButton(int status) {
		GWT.log("Merge job status is " + status);
		if(status == MergeStatusModel.PAUSING)
			actionBtn.setEnabled(false);
		else
			actionBtn.setEnabled(!toPause && true);
	}
	
	protected void computeEstimatedTime(MergeJobMonitorModel jobMonitor) {
		if(jobMonitor.getTimeRemain() != null && jobMonitor.getTimeRemain() > 0) {
			estimatedTime = Utils.milseconds2String(jobMonitor.getTimeRemain());
		}
	}
	
	public String getEstimatedTime() {
		return this.estimatedTime;
	}
	
	private SelectionListener<ButtonEvent> defineDetailListener() {
		detailListener = new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				detailWindow = new MergeJobDetailWindow(new PauseAction());
				detailWindow.show();
				detailWindow.refreshUI(currentStatus);
			}
		};
		
		return detailListener;
	}
	
	protected void popupMessage(String message, String title, 
			String icons, String buttons, Listener<MessageBoxEvent> listener){
		MessageBox box = new MessageBox();
		box.setButtons(buttons);
		box.setIcon(icons);
		box.setTitleHtml(title);
		box.setMessage(message);
		if(listener != null)
			box.addCallback(listener);
		box.show();
	}
	
	protected SelectionListener<ButtonEvent> getActionListener() {
		return actionListener;
	}
	
	protected class PauseAction implements IPauseAction {
		@Override
		public void pause(AsyncCallback<Integer> callback) {
			pauseMerge(callback);
		}
	}
}
