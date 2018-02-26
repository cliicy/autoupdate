package com.ca.arcflash.ui.client.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.icons.FlashImageBundle;
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
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class MergeJobDetailWindow extends Window {	
	
	private LabelField phaseValue;	
	private LabelField startTimeValue;
	private LabelField elapsedTimeValue;
	private LabelField currentSessionValue;
	private LabelField totalSessionValue;
	private LabelField sessionsMergedValue;
	private LabelField remainTimeValue;
	private ContentPanel phasePanel;
	private LayoutContainer detailContainer;
	private LabelField progressLabel;
	//
	private LabelField remainTimeLabel;
	private LabelField currentSession;
	private LabelField sessionsMerged;
	private LabelField runningServerNameLabel;
	private LabelField runningServerName;
	
	private LabelField mergeTotalRangeToMerge;
	private LabelField mergeCurrentRange;
	
	private ProgressBar progress;
	private Button pauseButton;
	protected boolean toPause = false;
//	private MergeRunningPanel panel;

	private final int width = 500;
	private final int height = 500;
	//event listener for the pause button, HBBU and D2D call different API to pause
	//merge job, so we need the user of this window to pass in the listener.
//	private SelectionListener<ButtonEvent> pauseListener;
	private IPauseAction pauseAction = null;
	
	public MergeJobDetailWindow(IPauseAction pauseAction) {
//		this.pauseListener = pauseListener;
		this.pauseAction = pauseAction;
		
		this.setHeight(height);
		this.setWidth(width);
		this.setResizable(false);
		this.setHeadingHtml(UIContext.Constants.mergeDetailHeading());
		setBodyStyle("background-color: white;");	
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setWidth("100%");
//		tl.setHeight("100%");
		tl.setCellSpacing(2);
		setLayout(tl);

		FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);

		Image titleImage = AbstractImagePrototype.create(IconBundle.tasks_backup()).createImage();
		TableData td = new TableData();
		td.setColspan(1);
		td.setPadding(5);
		td.setWidth("15%");
		this.add(titleImage, td);

		LabelField jobNameLabel = new LabelField(UIContext.Constants
		        .mergeJobPanelTitle());
		jobNameLabel.setStyleName("jobmonitor_window_jobname");
		td = new TableData();
		td.setWidth("80%");
		add(jobNameLabel, td);
		
		addProgressPanel();
		addButtons();
	}
	
	private void addProgressPanel() {
		phasePanel = new ContentPanel();
		phasePanel.setCollapsible(true);
		phasePanel.setHeight(450);
		phasePanel.setHeadingHtml(UIContext.Constants.jobMonitorProgressTitle());
		phasePanel.setLayoutOnChange(true);
		TableLayout tl = new TableLayout();
		tl.setColumns(3);
		tl.setWidth("100%");
		tl.setCellPadding(4);
		tl.setCellSpacing(2);
		phasePanel.setLayout(tl);
		
		LabelField phase = new LabelField(UIContext.Constants.jobMonitorLabelPhase());
		phase.setStyleName("jobMonitor_label");
		TableData td2 = new TableData();
		td2.setWidth("15%");
		phasePanel.add(phase, td2);
		
		phaseValue = new LabelField();
		phaseValue.setStyleName("jobMonitor_value");		
		td2 = new TableData();
		td2.setWidth("70%");
		phasePanel.add(phaseValue, td2);
		
		pauseButton = new Button();
		pauseButton.ensureDebugId("01672BC5-CDCB-4449-A01E-F9289144AB2A");
		pauseButton.setText(UIContext.Constants.mergeJobPanelActionButtonPause());
		pauseButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				pauseButton.setEnabled(false);
			}
		});
		pauseButton.addSelectionListener(this.setupPauseListener());
		pauseButton.setMinWidth(80);
		pauseButton.setAutoWidth(true);
		td2 = new TableData();
		td2.setWidth("15%");
		phasePanel.add(pauseButton, td2);
		
		//progress bar
		this.progress = new ProgressBar(){
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
            	if(rendered)
            	//Set progress width to a relative value.
            	el().firstChild().firstChild().setWidth(w);
				return this;
			}
		};		
		TableData td = new TableData();
		td.setWidth("97%");
		td.setPadding(10);
		td.setColspan(3);
		phasePanel.add(progress, td);
		
		progressLabel = new LabelField();
		progressLabel.addStyleName("jobMonitor_value");
		progressLabel.setStyleAttribute("padding-left", "8px");
		td = new TableData();
		td.setWidth("97%");
		td.setPadding(10);
		td.setColspan(3);
		phasePanel.add(progressLabel, td);
		
		this.addDetails(phasePanel);
		
		td = new TableData();
		td.setColspan(2);
		td.setHeight("100%");
		this.add(phasePanel, td);
	}
	
	private void addDetails(ContentPanel phasePanel) {
		detailContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setWidth("100%");
//		tl.setCellPadding(4);
		tl.setCellSpacing(2);
		detailContainer.setLayout(tl);
		
		//startTime
		LabelField startTime = new LabelField(UIContext.Constants.jobMonitorStartTime());
		startTime.setStyleName("jobMonitor_label");
		TableData td = new TableData();
		td.setWidth("60%");
		detailContainer.add(startTime, td);
		
		startTimeValue = new LabelField();
		startTimeValue.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(startTimeValue, td);
		//elpased Time
		LabelField elapsedTime = new LabelField(UIContext.Constants.jobMonitorElapsedTime());
		elapsedTime.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(elapsedTime, td);
		
		elapsedTimeValue = new LabelField();
		elapsedTimeValue.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(elapsedTimeValue, td);
		//remain time
		remainTimeLabel = new LabelField(UIContext.Constants.jobMonitorEstimatedTime());
		remainTimeLabel.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(remainTimeLabel, td);
		
		remainTimeValue = new LabelField();
		remainTimeValue.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(remainTimeValue, td);
		//currentSession
		currentSession = new LabelField(UIContext.Constants.jobMonitorCurrentSessionMerging());
		currentSession.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(currentSession, td);
		
		currentSessionValue = new LabelField();
		currentSessionValue.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(currentSessionValue, td);
		//sessions merged
		sessionsMerged = new LabelField(UIContext.Constants.jobMonitorSessionsMerged());
		sessionsMerged.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(sessionsMerged, td);
		
		sessionsMergedValue = new LabelField();
		sessionsMergedValue.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(sessionsMergedValue, td);
		//total session
		LabelField totalSessions = new LabelField(UIContext.Constants.jobMonitorTotalSessionsToMerge());
		totalSessions.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(totalSessions, td);
		
		totalSessionValue = new LabelField();
		totalSessionValue.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(totalSessionValue, td);
		
		//total merge range
		LabelField mergeTotalRangeToMergeLabel = new LabelField(UIContext.Constants.jobMonitorMergeTotalRangeToMerge());
		mergeTotalRangeToMergeLabel.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(mergeTotalRangeToMergeLabel, td);
		
		mergeTotalRangeToMerge = new LabelField();
		mergeTotalRangeToMerge.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(mergeTotalRangeToMerge, td);
		
		//current range
		LabelField mergeCurrentRangeLabel = new LabelField(UIContext.Constants.jobMonitorMergeCurrentRange());
		mergeCurrentRangeLabel.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(mergeCurrentRangeLabel, td);
		
		mergeCurrentRange = new LabelField();
		mergeCurrentRange.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(mergeCurrentRange, td);		
		
		// RPS name
		runningServerNameLabel = new LabelField("Running Server Name");
		runningServerNameLabel.setVisible(false);
		runningServerNameLabel.setStyleName("jobMonitor_label");
		td = new TableData();
		td.setWidth("60%");
		detailContainer.add(runningServerNameLabel, td);
		
		runningServerName = new LabelField();
		runningServerName.setVisible(false);
		runningServerName.setStyleName("jobMonitor_value");		
		td = new TableData();
		td.setWidth("40%");
		detailContainer.add(runningServerName, td);	
		
		td = new TableData();
		td.setColspan(3);
		phasePanel.add(detailContainer, td);
	}

	private SelectionListener<ButtonEvent> setupPauseListener() {
		final BaseAsyncCallback<Integer> pauseCallback = 
			new BaseAsyncCallback<Integer> (){

				@Override
				public void onFailure(Throwable caught) {
					pauseButton.setEnabled(true);
					toPause = false;
					super.onFailure(caught);
				}

				@Override
                public void onSuccess(Integer result) {

                }
			
		};
		
		SelectionListener<ButtonEvent> actionListener = new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				pauseButton.setEnabled(false);
				toPause = true;
				final Button clickedBtn = ce.getButton();				
				Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>(){
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							pauseAction.pause(pauseCallback);
						}else {
							pauseButton.setEnabled(true);
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
	
	private void addButtons() {
		Button closeBtn = new Button(UIContext.Constants.close());
		closeBtn.ensureDebugId("D584372E-78AD-4b8b-B54F-520D7E2CDC53");
		closeBtn.setMinWidth(80);
		closeBtn.setAutoWidth(true);
		closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		this.addButton(closeBtn);

		Button helpBtn = new Button(UIContext.Constants.help());
		helpBtn.ensureDebugId("0FB4E7BA-D0B9-4a93-AAC5-C82CB84C971F");
		helpBtn.setMinWidth(80);
		helpBtn.setAutoWidth(true);
		helpBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onHelp();
			}
		});
		this.addButton(helpBtn);
	}
	
	protected void onHelp() {
		HelpTopics.showHelpURL(UIContext.externalLinks.getJobMonitorPanelHelp());
	}
	
	public void refreshUI(MergeStatusModel statusmodel) {
		if(statusmodel == null)
			return;
		if(statusmodel.isRecoverySet() == null || !statusmodel.isRecoverySet()) {
			if(statusmodel.getStatus() == MergeStatusModel.PAUSING)
				pauseButton.setEnabled(false);
			else
				pauseButton.setEnabled(!toPause && true);
		}else {
			pauseButton.hide();
		}
		
		MergeJobMonitorModel model = statusmodel.jobMonitor;
		if(model != null) {
			phaseValue.setValue(Utils.getMergePhaseMessage(model));
			
			if(model.isVHDMerge() != null && model.isVHDMerge()) {
				if(progress != null){
					phasePanel.remove(progress);
					detailContainer.remove(remainTimeLabel);
					detailContainer.remove(remainTimeValue);
					progress = null;
				}
				Utils.updateVHDMergeProgress(progressLabel, model);
			}else {
				if(progressLabel != null) {
					phasePanel.remove(progressLabel);
					detailContainer.remove(currentSession);
					detailContainer.remove(currentSessionValue);
					detailContainer.remove(sessionsMerged);
					detailContainer.remove(sessionsMergedValue);
					progressLabel = null;
				}
				Utils.updateMergeProgress(progress, model);
			}
			
			if(model.getStartTime() != null && model.getStartTime() > 0){
				startTimeValue.setValue(Utils.formatDateToServerTime(new Date(model.getStartTime())));
			}
			
			if(model.getElapsedTime() != null && model.getElapsedTime() > 0) {
				elapsedTimeValue.setValue(Utils.milseconds2String(model.getElapsedTime()));
			}
			
			if(model.getTimeRemain() != null && model.getTimeRemain() > 0) {
				remainTimeValue.setValue(Utils.milseconds2String(model.getTimeRemain()));
			}else {
				remainTimeValue.setValue(UIContext.Constants.NA());
			}
			
			if(model.getCurSess2Merge() != null && model.getCurSess2Merge() > 0) {
				currentSessionValue.setValue(String.valueOf(model.getCurSess2Merge().longValue()));
			}else {
				currentSessionValue.setValue(UIContext.Constants.NA());
			}
			
			if(model.getSessCnt2Merge() != null && model.getSessCnt2Merge() > 0) {
				totalSessionValue.setValue(String.valueOf(model.getSessCnt2Merge().intValue()));
			}else {
				totalSessionValue.setValue(UIContext.Constants.NA());
			}
			
			if(model.getSessCntMerged() != null && model.getSessCntMerged() >= 0) {
				sessionsMergedValue.setValue(String.valueOf(model.getSessCntMerged().intValue()));
			}else {
				sessionsMergedValue.setValue(UIContext.Constants.NA());
			}
			
			
			if(model.getSessRangeCnt() != null && model.getSessRangeCnt() > 0) {
				this.mergeTotalRangeToMerge.setValue(String.valueOf(model.getSessRangeCnt().intValue()));
			}else {
				mergeTotalRangeToMerge.setValue(UIContext.Constants.NA());
			}
			
			if(model.getCurrentMergeRangeStart() != null && model.getCurrentMergeRangeStart() > 0 && model.getCurrentMergeRangeEnd() != null && model.getCurrentMergeRangeEnd() > 0) {
				this.mergeCurrentRange.setValue(model.getCurrentMergeRangeStart().intValue() + "-" + model.getCurrentMergeRangeEnd().intValue());
			}else {
				mergeCurrentRange.setValue(UIContext.Constants.NA());
			}	
			if (model.getServerNodeName() != null && model.getServerNodeName().length() > 0) {				
				runningServerNameLabel.setVisible(true);
				runningServerName.setVisible(true);
				runningServerName.setValue(model.getServerNodeName());
			}
			
			phasePanel.layout();
			this.layout();
			if(model.getJobPhase() == MergeJobMonitorModel.JobPhase.EJP_PROC_EXIT.ordinal()){
				this.hide();
			}
		}
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
	
	public static interface IPauseAction {
		void pause(AsyncCallback<Integer> callback);
	}
}
