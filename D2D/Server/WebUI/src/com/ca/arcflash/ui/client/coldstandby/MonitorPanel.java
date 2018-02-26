package com.ca.arcflash.ui.client.coldstandby;

import java.util.Date;

import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;

public class MonitorPanel extends LayoutContainer {
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	
	public static final int REFRESH_INTERVAL = 3000;
	private ProgressBar bar;
	private static Timer timer;
	private Label estimatedRemainTimeLabel = new Label();
	private int endsCounter = 0;
	private ContentPanel panel;
	private LoadingStatus jobStatus = new LoadingStatus();
	//Indicate whether the current MonitorPanel is visible. We do not use the method visible() because 
	//this panel is now in a TabItem and the method will return false when the panel shows and the enclosing 
	//item is not active. Note: when changing the visibility of the panel, isMonitorVisible must also be updated
	//correspondingly.
	private boolean isMonitorVisible;

	private Label currentSessionLabel;
	private Button detaiButton = new Button();
	private JobMonitorDetailWindow detailWindow;
	
	public void render(Element target, int index) {
		super.render(target, index);
		
	    ContentPanel topContainer = new ContentPanel();
	    topContainer.setCollapsible(true);
	    topContainer.ensureDebugId("f4776a21-38d1-49da-bcc0-81675dec6cdc");
	    topContainer.setHeadingHtml(UIContext.Constants.jobMonitorPanelTitle());
	    topContainer.setBodyStyle("background-color: white; padding: 6px;");
	    topContainer.setLayout(new RowLayout());
	    
		panel = new ContentPanel();
//		panel.setWidth("100%");
		panel.ensureDebugId("b75c8082-93b7-4ee3-b4bd-595afd03399e");
		panel.setBodyBorder(false);
		setPanelVisiblity(false);
		panel.setHeaderVisible(false);
		
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(4);
	    panel.setLayout(layout);
	    
	    TableData data = new TableData();
	    data.setColspan(4);
	    data.setMargin(2);
	    jobStatus.setStyleAttribute("margin", "2px, 2px, 5px, 2px");
	    jobStatus.setMsgLabelStyleName("jobMonitor_label");
	    panel.add(jobStatus,data);
	    
//	    jobStatus.setLoadingMsg("Virtual Conversion (Processing session 2 of total 68)");
	    jobStatus.setLoadingMsg(UIContext.Constants.virtualStandyNameTranslate());
	    
	    data = new TableData();
	    data.setColspan(4);
	    currentSessionLabel = new Label();
	    currentSessionLabel.setStyleName("jobMonitor_headingsub_label");
//	    currentSessionLabel.setText("Processing Session: S00000001021, Session Created at: 10/13/2010 6:17:27 PM");
	    panel.add(currentSessionLabel, data);
	    
	    data = new TableData();
	    data.setWidth("25%");
	    data.setStyle("margin: 20px, 15px, 2px, 2px;");
	    Label estLabel = new Label();
	    estLabel.setText(UIContext.Constants.jobMonitorEstimatedTime());//"Estimated Time Remaining: 00:16:45"
	    estLabel.setStyleName("jobMonitor_headingsub_label");
	    panel.add(estLabel, data);
	    
	    data = new TableData();
	    data.setWidth("15%");
	    data.setMargin(2);
	    data.setHorizontalAlign(HorizontalAlignment.LEFT);
	    estimatedRemainTimeLabel.setWidth("100%");
	    estimatedRemainTimeLabel.setStyleName("jobMonitor_value");
	    estimatedRemainTimeLabel.ensureDebugId("83ddb322-7d10-4284-a2c1-ef271b24b0a9");
	    estimatedRemainTimeLabel.setText(UIContext.Constants.NA());
	    panel.add(estimatedRemainTimeLabel,data);
	    
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
				this.setWidth(el().getParent().getWidth());
				return this;
			}
        };

        bar.ensureDebugId("1dcdbedc-76c3-4853-928f-aa06b0716850");
//	    bar.setWidth(550);
        bar.setWidth("100%");
	    bar.setHeight(20);
	    
	    data = new TableData();
	    data.setWidth("45%");
	    panel.add(bar,data);
	    
        data = new TableData();
        data.setWidth("15%");
        data.setPadding(5);
        detaiButton.setWidth(80);
	    detaiButton.setText(UIContext.Constants.jobMonitorDetail());
	    detaiButton.ensureDebugId("8ecca0dc-6f90-4622-827f-304f1940dadd");
	    detaiButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				detailWindow = new JobMonitorDetailWindow();
				detailWindow.setModal(true);
				detailWindow.show();
				
			}
	    	
	    });
	    panel.add(detaiButton, data);	    
	    
	    topContainer.add(panel);
	    add(topContainer);
	    
	    timer = new Timer() {
			public void run() {
				refresh(null);
			}
		};
		timer.schedule(REFRESH_INTERVAL);
		timer.scheduleRepeating(REFRESH_INTERVAL);
	}

	private void setPanelVisiblity(boolean visibility) {
		setVisible(visibility);
		isMonitorVisible = visibility;
	}
	
	public void refresh(Object data){
		String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
		service.getReplicaJobMonitor(vmInstanceUUID, new BaseAsyncCallback<RepJobMonitor>(){
			
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof BusinessLogicException){
					BusinessLogicException exception = (BusinessLogicException)caught;
					if(ColdStandbyManager.CONNECT_VCM_CLIENT_FAIL.equals(exception.getErrorCode())) {
						if(isMonitorVisible) {
							setPanelVisiblity(false);
						}
					}
				}
			}

			@Override
			public void onSuccess(RepJobMonitor result) {
				
				if(detailWindow != null && detailWindow.isVisible())
					detailWindow.refresh(result);
				
				if (result == null || result.getId()<0){
					if (isMonitorVisible)
						ColdStandbyManager.getInstance().fireReplicationJobFinishedEvent();
					
					setPanelVisiblity(false);
					return;
				}
				
				
				
				if (result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_EXIT){
					
					endsCounter++;
					if (endsCounter>=3){
						//GXT issue. Can't reset to 0.
						bar.updateProgress(0.0026,""); 
						setPanelVisiblity(false);
					}
					
					return;
				}
				
				endsCounter = 0;
				setPanelVisiblity(true);
				
				if (result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_SESSION_END || result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_SESSION_START){
					result.setRepElapsedTime(0);
					result.setRepStartTime(0);
					result.setRepStartNanoTime(0);
					result.setRepTotalSize(0);
					result.setRepTransedSize(0);
				}
					
				long totalSize = result.getRepTotalSize();
				long totalTransSize = result.getRepTransedSize();
				long transSizeAfterResume = result.getRepTransAfterResume();
				int numOfToRep = result.getToRepSessions().length;
				int totalSessionNumbers = result.getTotalSessionNumbers();
				if(totalSessionNumbers > 0)
					jobStatus.setLoadingMsg(VCMMessages.virtualConversionMonitorTitle(totalSessionNumbers - numOfToRep, totalSessionNumbers));//"Virtual Conversion (Processing session 2 of total 68)"
				else
					jobStatus.setLoadingMsg(UIContext.Constants.virtualStandyNameTranslate());
				
				String backupTime = UIContext.Constants.NA(); 
				if(result.getRepSessionBackupTime() > 0)
					backupTime = Utils.formatDateToServerTime(new Date(result.getRepSessionBackupTime()));
				currentSessionLabel.setText(UIContext.Messages
						.virtualConversionMonitorProcessing(backupTime)); //"Processing backup session created: 10/13/2010 6:17:27 PM"
				
				
				//Show estimatedRemainTime only if transfer throughput reach 1MB/min
				if (result.getRepElapsedTime()>1000 && totalSize>0){
					double remainSize = totalSize - totalTransSize;
					double remainTime = remainSize/(transSizeAfterResume / result.getRepElapsedTime());
					double transThroughput = (transSizeAfterResume/1024.0/1024.0) / (result.getRepElapsedTime()/1000.0/60.0);
					if(transThroughput > 1){
						estimatedRemainTimeLabel.setText(Utils.milseconds2String((long)remainTime));
					}else{
						estimatedRemainTimeLabel.setText(UIContext.Constants.NA());
					}
				}
				else{
					if(estimatedRemainTimeLabel.getText() == null || estimatedRemainTimeLabel.getText().indexOf(":") < 0 )
						estimatedRemainTimeLabel.setText(UIContext.Constants.NA());
				}
					
				String transferedSizeLabel = Utils.bytes2String(totalTransSize);
				String totalSizeLabel = Utils.bytes2String(totalSize);
				
				// to deal with the immediately started new job after first job fails. 
				//We need to refresh the process bar when it is just beginning
				/*if(result.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP || 
						result.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_TAKING_SNAPSHOT ||
						result.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS)
				{
					bar.updateProgress(0.0026,""); 
				}
				else*/ 
				if (totalSize>0){
					double percent = ((double)totalTransSize)/totalSize;
					if (percent>=1)
						bar.updateProgress(1, UIContext.Messages.jobMonitorProgressBarLabel(100, transferedSizeLabel, totalSizeLabel));
					else
						bar.updateProgress(percent, UIContext.Messages.jobMonitorProgressBarLabel((int)(percent*100), transferedSizeLabel, totalSizeLabel));
				}else
					bar.updateProgress(0, UIContext.Constants.NA());
				
			}
			
		});
	}
	
	public void refreshNextScheduleEvent(){
//		nextEventText.setText("There is no running job");
		/*homepageService.getNextScheduleEvent(new BaseAsyncCallback<NextScheduleEventModel>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(NextScheduleEventModel result) {
				if (result == null){
					nextEventText.setText(UIContext.Messages.homepageNextScheduledEvent(UIContext.Constants.NA(), ""));
					return;
				}
				
				nextEventText.setText(UIContext.Messages.homepageNextScheduledEvent(
						Utils.formatDateToServerTime(result.getDate()),
						Utils.backupType2String(result.getBackupType())+" "+UIContext.Constants.backup()
						));
			}
		});*/
	}
}
