package com.ca.arcflash.ui.client.homepage;

import java.util.Date;
import java.util.LinkedList;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.JobMonitorHistoryItemModel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.webservice.constants.JobType;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Label;

public class JobMonitorDetailWindow extends BaseJobMonitorDetailWindow {

	// private static final int HEIGHT = 550;
	private static final int HEIGHT = 600;
	private static final int WIDTH = 570;

	private boolean firstFresh = true;

	private Label currentSessionLabel = new Label();
	private Label startSessionLabel = new Label();
	private Label endSessionLabel = new Label();
	private Label sourceRPSServerLabel = new Label();
	private Label destinationRPSServerLabel = new Label();
	private Label sourceDatastoreNameLabel = new Label();
	private Label targetDatastoreNameLabel = new Label();
	private Label savedBandWidthLabel = new Label();

	// private Image cancelImage = new
	// Image("images/default/shared/loading-balls.gif");

	private FlexTable progressTable = new FlexTable();
	// For GRT catalog
	// private Label currentFolderLabel = new Label();
	private Label totalFolderLabel = new Label();
	private Label processedFolderLabel = new Label();

	protected HTML chartHtml = new HTML();
	protected long yaxiMaxValue = DEFAULT_YAXIS_MAX;
	protected static final int chartWidth = 450;
	protected static final int chartHighth = 300;
	protected static final String chartHTML1 = new StringBuilder()
			.append("<html>")
			.append("  <body bgcolor=\"#ffffff\">")
			.append("<div style=\"border:1px solid;\">")
			.append("    <OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"")
			.append(chartWidth)
			.append("\" height=\"")
			.append(chartHighth)
			.append("\" id=\"MSLineChart\">")
			.append("      <param name=\"movie\" value=\"FusionCharts/MSLine.swf\" />")
			.append("      <param name=\"FlashVars\" ")
			.append("             value=\"&dataXML=").toString();
	protected static final String chartHTML2 = new StringBuilder()
			.append("&chartWidth=").append(chartWidth).append("&chartHeight=")
			.append(chartHighth).append("\">")
			.append("      <param name=wmode value=transparent>")
			.append("      <param name=\"quality\" value=\"high\" />")
			.append("      <embed src=\"../FusionCharts/MSLine.swf\" ")
			.append("             flashVars=\"&dataXML=").toString();
	protected static final String chartHTML3 = new StringBuilder()
			.append("&chartWidth=").append(chartWidth).append("&chartHeight=")
			.append(chartHighth).append("\"")
			.append("             quality=\"high\" wmode=transparent width=\"")
			.append(chartWidth).append("\" height=\"").append(chartHighth)
			.append("\" name=\"MSLineChart\" ")
			.append("             type=\"application/x-shockwave-flash\" />")
			.append("    </OBJECT>").append("</div>").append("  </body>")
			.append("</html>").toString();
	protected static final long DEFAULT_YAXIS_MAX = 10;
	protected static final String CHARTXMLDataEnd = "</chart>";
	protected static final int HISTORY_TIME = 180;
	protected static final int HISTORY_ITEM_COUNT = HISTORY_TIME
			/ REFRESH_INTERVAL + 1;
	protected LinkedList<JobMonitorHistoryItemModel> historyDeque = new LinkedList<JobMonitorHistoryItemModel>();
	protected long jobID;

	public JobMonitorDetailWindow(String jobType, long jobID) {
		this.jobType = jobType;
		this.jobID = jobID;
		thisWindow = this;
		this.setResizable(false);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);

		setBodyStyle("background-color: white;");
		setScrollMode(Scroll.AUTOY);

		setLayout(new RowLayout(Orientation.VERTICAL));

		// add four parts.
		add(setupTitlePanel()); // title
		add(setupProgressPanel()); // progress
		long type = Long.parseLong(jobType);
		if (type == JobMonitorModel.JOBTYPE_RPS_REPLICATE
				|| type == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
				|| type == JobType.JOBTYPE_RPS_DATA_SEEDING
				|| type == JobType.JOBTYPE_RPS_DATA_SEEDING_IN) {
			this.setHeight(600);
		}
		if (!(type == JobMonitorModel.JOBTYPE_CATALOG_FS
				|| type == JobMonitorModel.JOBTYPE_CATALOG_GRT
				// || type == JobMonitorModel.JOBTYPE_ARCHIVE
				|| type == JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC
				|| type == JobMonitorModel.JOBTYPE_ARCHIVEPURGE
				|| type == JobMonitorModel.JOBTYPE_ARCHIVERESTORE || type == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND)) {

			add(setupThoughputPanel()); // throughput
		}
		// if((type!=JobMonitorModel.JOBTYPE_ARCHIVE) &&
		// (type!=JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC)

		// add(setupButtonPanel()); // button
		setupButtonPanel();

		getJobMonitorHistory();

		startJobMonitorTimer();
	}

	protected void getJobMonitorHistory() {
		service.getJobMonitorHistory(new BaseAsyncCallback<JobMonitorHistoryItemModel[]>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(JobMonitorHistoryItemModel[] result) {
				historyDeque.clear();
				for (int i = 0; i < result.length; i++) {
					addJobMonitorHistory(result[i]);
				}
				firstFresh = false;
			}
		});
	}

	@Override
	public void refresh(Object data) {
		service.getJobMonitor(jobType, jobID,
				new BaseAsyncCallback<JobMonitorModel>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						// if (thisWindow.isMasked())
						// thisWindow.unmask();
					}

					@Override
					public void onSuccess(JobMonitorModel result) {
						jobMonitorModel = result;
						PupulateUI(result);
						// if (thisWindow.isMasked())
						// thisWindow.unmask();
						if (!firstFresh && result != null
								&& result.getSessionID().longValue() > 0)
							addJobMonitorHistory(result);
					}
				});
	}

	private void addJobMonitorHistory(JobMonitorHistoryItemModel model) {
		if (historyDeque.size() >= HISTORY_ITEM_COUNT) {
			historyDeque.poll();
		}
		historyDeque.add(model);
	}

	private void addJobMonitorHistory(JobMonitorModel model) {
		if (historyDeque.size() >= HISTORY_ITEM_COUNT) {
			historyDeque.poll();
		}
		JobMonitorHistoryItemModel historyModel = new JobMonitorHistoryItemModel();
		historyModel.setSessionID(model.getSessionID());
		historyModel.setReadSpeed(model.getReadSpeed());
		historyModel.setWriteSpeed(model.getWriteSpeed());
		historyDeque.add(historyModel);
	}

	protected void PupulateUI(JobMonitorModel jobMonitorModel) {
		this.jobMonitorModel = jobMonitorModel;
		if (jobMonitorModel == null
				|| (jobMonitorModel.getSessionID().longValue() <= 0
						&& jobMonitorModel.getJobType() != JobMonitorModel.JOBTYPE_BACKUP
						&& jobMonitorModel.getJobType() != JobMonitorModel.JOBTYPE_RPS_REPLICATE
						&& jobMonitorModel.getJobType() != JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
						&& jobMonitorModel.getJobType() != JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING && jobMonitorModel
						.getJobType() != JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN)) {
			cancelButton.disable();
			// cancelImage.setVisible(false);
			thisWindow.hide();
			return;
		}

		// dynamic set the window heading
		if (getHeadingHtml() == null || getHeadingHtml().trim().length() <= 0) {
			String heading = null;
			if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_BACKUP)
				heading = UIContext.Constants.jobMonitorBackupWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RESTORE)
				heading = UIContext.Constants.JobMonitorRestoreWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_COPY)
				heading = UIContext.Constants.jobMonitorExportWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND)
				heading = UIContext.Constants.JobMonitorCatalogWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVE)
				heading = UIContext.Constants.JobMonitorFileCopyWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_FILEARCHIVE)
				heading = UIContext.Constants.JobMonitorFileArchiveWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_FILECOPYDELETE)
				heading = UIContext.Constants.JobMonitorFileCopyDeleteWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC)
				heading = UIContext.Constants.JobMonitorFileCopyCatalogWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVEPURGE)
				heading = UIContext.Constants.JobMonitorFileCopyPurgeWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVERESTORE)
				heading = UIContext.Constants.JobMonitorFileCopyRestoreWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND)
				heading = UIContext.Constants.JobMonitorReplicationWindow();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
				heading = UIContext.Constants.JobMonitorJumpstartWindow();
			} else if (jobMonitorModel.getJobType() == JobType.JOBTYPE_BMR) {
				heading = UIContext.Constants.jobMonitorBMRWindow();
			}
			setHeadingHtml(heading);
		}

		if (titleFlexTable.getWidget(0, 0) == null) {
			if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_BACKUP)
				titleImage = AbstractImagePrototype.create(
						IconBundle.tasks_backup()).createImage();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RESTORE
					|| jobMonitorModel.getJobType() == JobType.JOBTYPE_BMR)
				titleImage = AbstractImagePrototype.create(
						IconBundle.tasks_restore()).createImage();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_COPY)
				titleImage = AbstractImagePrototype.create(
						IconBundle.tasks_recovery()).createImage();
			else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND)
				// TODO
				titleImage = AbstractImagePrototype.create(
						IconBundle.tasks_recovery()).createImage();
			titleFlexTable.setWidget(0, 0, titleImage);
		}
		// For progress table
		long readSpeed = jobMonitorModel.getReadSpeed();

		switch (Integer.parseInt(jobType)) {
		case JobMonitorModel.JOBTYPE_ARCHIVE:// File Copy
		case JobMonitorModel.JOBTYPE_ARCHIVEPURGE:// Archive Purge
		case JobMonitorModel.JOBTYPE_FILEARCHIVE:// File Archive
		case JobMonitorModel.JOBTYPE_FILECOPYDELETE:// File Copy Delete
			phasevalueLable.setHtml(Utils
					.ArchivejobMonitorPhase2String(jobMonitorModel
							.getJobPhase()));
			break;
		case JobMonitorModel.JOBTYPE_RPS_REPLICATE:
			phasevalueLable.setHtml(Utils.replicationJobMonitorPhase2String(
					jobMonitorModel.getJobPhase(),
					JobMonitorModel.JOBTYPE_RPS_REPLICATE));
			break;
		case JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND:
			phasevalueLable.setHtml(Utils.replicationJobMonitorPhase2String(
					jobMonitorModel.getJobPhase(),
					JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND));
			break;
		case JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING:
			phasevalueLable.setHtml(Utils.replicationJobMonitorPhase2String(
					jobMonitorModel.getJobPhase(),
					JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING));
			break;
		case JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN:
			phasevalueLable.setHtml(Utils.replicationJobMonitorPhase2String(
					jobMonitorModel.getJobPhase(),
					JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN));
			break;
		case JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC:// Archive Catalog
															// Sync Job
			phasevalueLable.setHtml(Utils
					.ArchiveCataogjobMonitorPhase2String(jobMonitorModel
							.getJobPhase()));
			break;
		case JobMonitorModel.JOBTYPE_BMR:
			phasevalueLable.setHtml(Utils.bmrJobMonitorPhase2String(
					jobMonitorModel.getJobPhase(),
					jobMonitorModel.getCurrentProcessDiskName()));
			break;
		case JobMonitorModel.JOBTYPE_ARCHIVERESTORE:// Archive Restore
		default:
			String phase = null;
			if (jobMonitorModel.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_MERGINGSESSION) {
				phase = UIContext.Messages.jobMonitorPhaseBackupMergeSessions(
						jobMonitorModel.getUlMergedSession(),
						jobMonitorModel.getUlTotalMergedSessions());
			} else if (jobMonitorModel.getJobPhase() == JobMonitorModel.LINUX_PHASE_BACKUP_VOLUME) {
				phase = UIContext.Messages
						.linuxJobMonitorBackupVolume(jobMonitorModel
								.getCurrentProcessDiskName());
			} else if (jobMonitorModel.getJobPhase() == JobMonitorModel.LINUX_PHASE_RESTORE_VOLUME) {
				phase = UIContext.Messages
						.linuxJobMonitorRestoreVolume(jobMonitorModel
								.getCurrentProcessDiskName());
			} else {
				phase = Utils.jobMonitorPhase2String(jobMonitorModel
						.getJobPhase());
			}
			if (phase != null)
				phasevalueLable.setHtml(phase);
			break;
		}

		jobNameLabel.setHtml(Utils.jobMonitorType2String(jobMonitorModel));
		String vol = Utils.getCurrentVolumn(jobMonitorModel);
		volumeLabel.setHtml(vol);
		if (jobMonitorModel.getJobType() == JobType.JOBTYPE_BMR) {
			// the start time is sent from backend module, in milliseconds
			startTimeLabel.setHtml(Utils.formatDateToServerTime(new Date(
					jobMonitorModel.getStartTime())));
		} else {
			// the start time is sent from backend module, in milliseconds
			startTimeLabel.setHtml(Utils.formatDateToServerTime(new Date(
					jobMonitorModel.getBackupStartTime())));
		}
		elapsedLabel.setHtml(Utils.milseconds2String(jobMonitorModel
				.getElapsedTime()));

		computeCompressionAndDedupe(jobMonitorModel);

		encryptionLabel.setHtml(Utils
				.jobMonitorEncrytionAlgorithm2String(jobMonitorModel
						.getEncInfoStatus()));
		
		destinationTypeLabel.setHtml(jobMonitorModel.getDestinationType());
		destinationPathLabel.setHtml(jobMonitorModel.getDestinationPath());

		if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP
				|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS
				|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND) {
			// Utils.updateProgress(progressLabel, jobMonitorModel);
			progressLabel.setHtml(Utils.getCatalogProgress(jobMonitorModel));
			progressTable.getRowFormatter().setVisible(4, false);
			// elapsedLabel.setText(Utils.getCatalogEclipsedTime(String.valueOf(jobMonitorModel.getBackupStartTime())));
		} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_BACKUP) {
			progressBar.setVisible(true);
			Utils.updateProgress(progressBar, jobMonitorModel);
			estimateLabel.setHtml(Utils.getRemainTime(jobMonitorModel));
			if(!Utils.isEmptyOrNull(jobMonitorModel.getRpsServerName())){				
				destinationRPSServerLabel.setHtml(jobMonitorModel.getRpsServerName());			
				progressTable.getRowFormatter().setVisible(12, true);
			}
			if(!Utils.isEmptyOrNull(jobMonitorModel.getRpsDataStoreName())){	
				targetDatastoreNameLabel.setHtml(jobMonitorModel.getRpsDataStoreName());
				progressTable.getRowFormatter().setVisible(13, true);
			}			
		} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT) {
			totalFolderLabel.setHtml(String.valueOf(jobMonitorModel
					.getGRTTotalFolder()));
			processedFolderLabel.setHtml(String.valueOf(jobMonitorModel
					.getGRTProcessedFolder()));
			progressLabel.setHtml(Utils.getCatalogProgress(jobMonitorModel));
			progressTable.getRowFormatter().setVisible(2, false);
		} else {
			if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_BMR) {
				progressBar.setVisible(true);
				Utils.updateProgress(progressBar, jobMonitorModel);
			} else {
				if (jobMonitorModel.getEstimateBytesJob() <= 0) {
					progressBar.setVisible(false);
				} else {
					progressBar.setVisible(true);
					Utils.updateProgress(progressBar, jobMonitorModel);
				}
			}
			estimateLabel.setHtml(Utils.getRemainTime(jobMonitorModel));
		}
		// For ThroughPut table
		if (throghtTable != null) {
			if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_BACKUP) {
				throghtTable.getRowFormatter().setVisible(0, true);
				progressTable.getRowFormatter().setVisible(6, true);
				progressTable.getRowFormatter().setVisible(7, true);
				progressTable.getRowFormatter().setVisible(8, true);
				progressTable.getRowFormatter().setVisible(9, true);
				progressTable.getRowFormatter().setVisible(10, true);
				progressTable.getRowFormatter().setVisible(11, true);
				if(!Utils.isEmptyOrNull(jobMonitorModel.getRpsServerName()))	
					progressTable.getRowFormatter().setVisible(12, true);
				if(!Utils.isEmptyOrNull(jobMonitorModel.getRpsDataStoreName()))
					progressTable.getRowFormatter().setVisible(13, true);			
			} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RESTORE
					|| jobMonitorModel.getJobType() == JobType.JOBTYPE_BMR) {
				throghtTable.getRowFormatter().setVisible(0, false);
				if (jobMonitorModel.getJobType() == JobType.JOBTYPE_BMR) {
					progressTable.getRowFormatter().setVisible(5, false);
				}
				progressTable.getRowFormatter().setVisible(6, false);
				progressTable.getRowFormatter().setVisible(7, false);
				progressTable.getRowFormatter().setVisible(8, false);
			} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_COPY) {
				throghtTable.getRowFormatter().setVisible(0, false);
				progressTable.getRowFormatter().setVisible(10, true);
				progressTable.getRowFormatter().setVisible(8, true);
				progressTable.getRowFormatter().setVisible(6, true);
			} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVE
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_FILEARCHIVE
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_FILECOPYDELETE) {
				throghtTable.getRowFormatter().setVisible(0, false);// disabling
																	// write
																	// speed no
																	// limit
																	// field
				progressTable.getRowFormatter().setVisible(6, false);// disabling
																		// the
																		// space
																		// saved
																		// due
																		// to
																		// compression
				progressTable.getRowFormatter().setVisible(8, true);
				progressTable.getRowFormatter().setVisible(6, true);
				//progressTable.getRowFormatter().setVisible(22, true);
				//progressTable.getRowFormatter().setVisible(23, true);
			} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
				currentSessionLabel.setHtml(String.valueOf(jobMonitorModel
						.getSessionID()));
				startSessionLabel.setHtml(String.valueOf(jobMonitorModel
						.getUlBeginSessID()));
				endSessionLabel.setHtml(String.valueOf(jobMonitorModel
						.getUlEndSessID()));
				throghtTable.getRowFormatter().setVisible(0, true);
				progressTable.getRowFormatter().setVisible(12, true);
				progressTable.getRowFormatter().setVisible(13, true);
				progressTable.getRowFormatter().setVisible(14, true);
				sourceRPSServerLabel.setHtml(jobMonitorModel.getSrcRPS());
				destinationRPSServerLabel.setHtml(jobMonitorModel.getDestRPS());
				sourceDatastoreNameLabel.setHtml(jobMonitorModel
						.getSrcDataStore());
				targetDatastoreNameLabel.setHtml(jobMonitorModel
						.getDestDataStore());
				if (0 == jobMonitorModel.getReplicationSavedBandWidth()) {
					savedBandWidthLabel.setHtml(UIContext.Constants.NA());
				} else {
					savedBandWidthLabel.setHtml(jobMonitorModel
							.getReplicationSavedBandWidth() + "%");
				}
				progressTable.getRowFormatter().setVisible(15, true);
				progressTable.getRowFormatter().setVisible(16, true);
				progressTable.getRowFormatter().setVisible(17, true);
				progressTable.getRowFormatter().setVisible(18, true);
				progressTable.getRowFormatter().setVisible(19, true);
				progressTable.getRowFormatter().setVisible(20, true);
				progressTable.getRowFormatter().setVisible(21, true);
			}

			if (jobMonitorModel.getThrottling() == -1) { // if the value is -1,
															// so we don't get
															// the throttling.
				writeSpeedLimitLabel.setHtml(UIContext.Constants.NA());
			} else if (jobMonitorModel.getThrottling() != 0) {
				if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE
						|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
						|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING
						|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
					writeSpeedLimitLabel
							.setHtml(getThroughoutString(jobMonitorModel
									.getThrottling()));
				} else {
					writeSpeedLimitLabel.setHtml(UIContext.Messages
							.jobMonitorThroughout(new Long(jobMonitorModel
									.getThrottling()).toString()));
				}
			} else { // if we don't set the limit, show the value "No limit"
				writeSpeedLimitLabel.setHtml(UIContext.Constants
						.jobMonitorNoLimit());
			}
			writeThrouhputLabel.setHtml(UIContext.Messages
					.jobMonitorThroughout(new Long(jobMonitorModel
							.getWriteSpeed()).toString()));
			if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
				// need put writespeed into readThroughputLabel for replication
				// in job monitor
				readThroughputLabel.setHtml(getThroughoutString(jobMonitorModel
						.getWriteSpeed())
						+ "("
						+ getThroughoutString(jobMonitorModel.getLogicSpeed())
						+ ")");
			} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_REPLICATE
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING) {
				readThroughputLabel.setHtml(getThroughoutString(readSpeed)
						+ "("
						+ getThroughoutString(jobMonitorModel.getLogicSpeed())
						+ ")");
			} else {
				readThroughputLabel.setHtml(UIContext.Messages
						.jobMonitorThroughout(new Long(readSpeed).toString()));
			}
		}

		if (cancelled
				|| jobMonitorModel.getJobPhase() == JobMonitorModel.PHASE_CANCELING
				|| jobMonitorModel.getJobStatus() == JobMonitorModel.JOBSTATUS_CANCELLED) {
			cancelButton.disable();
			// cancelImage.setVisible(true);
		} else
			cancelButton.enable();

		// disabling the cancel button for the file copy catalog sync job
		if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC
				|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_ARCHIVE
				|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_FILEARCHIVE) {
			if (jobMonitorModel.getJobPhase() == JobMonitorModel.ARCHIVE_PHASE_CATALOG_UPDATE) {
				cancelButton.disable();
			}
		}

		if (Utils.isJobDone(jobMonitorModel.getJobType(), jobMonitorModel
				.getJobPhase(), jobMonitorModel.getJobStatus().intValue())) {
			thisWindow.hide();
			cancelButton.disable();
			// cancelImage.setVisible(false);
		}

		// chartHtml.setHTML(produceChartHtml());
	}

	// 1000Kbps = 1Mbps
	private String getThroughoutString(long speed) {
		if (speed >= 1000) {
			//String speedString = String.valueOf(speed / 1000.0);
			//String speedString = String.format("%.1f",speed / 1000.0); //use String.fromat will cause GXT compile error
			// Ticket 77941 round to one decimal			
			NumberFormat formatter = NumberFormat.getFormat("0.0");
			String speedString = formatter.format(speed / 1000.0);
			if (speedString.indexOf(".") > 0) {
				speedString = speedString.replaceAll("0+?$", "");
				speedString = speedString.replaceAll("[.]$", "");
			}
			return UIContext.Messages.jobMonitorThroughoutMbps(speedString);
		} else {
			return UIContext.Messages.jobMonitorThroughoutKbps(new Long(speed)
					.toString());
		}
	}

	private Widget setupProgressPanel() {
		ContentPanel progressPanel = new ContentPanel();
		progressPanel.setHeadingHtml(UIContext.Constants
				.jobMonitorProgressTitle());
		progressPanel.setAnimCollapse(true);
		// progressPanel.setWidth("100%");
		progressPanel.setCollapsible(true);
		progressPanel.expand();
		progressPanel.setBorders(false);
		progressPanel.setBodyBorder(false);
		progressPanel.setScrollMode(Scroll.AUTO);
		progressPanel.setStyleAttribute("padding", "5px");
		progressPanel.addListener(Events.Collapse,
				new Listener<ComponentEvent>() {
					@Override
					public void handleEvent(ComponentEvent be) {
						thisWindow.setHeight(thisWindow.getHeight() - 200);
					}
				});
		progressPanel.addListener(Events.Expand,
				new Listener<ComponentEvent>() {
					@Override
					public void handleEvent(ComponentEvent be) {
						thisWindow.setHeight(thisWindow.getHeight() + 200);
					}
				});

		progressTable.setWidth("510px");
		progressTable.setCellPadding(4);
		progressTable.setCellSpacing(4);
		progressTable.getColumnFormatter().setWidth(0, "270px");
		// progressTable.getColumnFormatter().setWidth(1, "60%");
		// progressTable.getColumnFormatter().setStyleName(1,
		// "jobmonitor_window_value_column");
		Label label;
		Label currVolLable;

		progressTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		progressTable.setWidget(0, 0, setupJobPhasePanel());
		progressTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		progressTable.getCellFormatter().setStyleName(1, 0,
				"jobMonitor_progress");

		int type = Integer.parseInt(jobType);

		if (type == JobMonitorModel.JOBTYPE_CATALOG_APP
				|| type == JobMonitorModel.JOBTYPE_CATALOG_FS
				|| type == JobMonitorModel.JOBTYPE_CATALOG_GRT
				|| type == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND) {
			ContentPanel panel = new ContentPanel();
			panel.setWidth("100%");
			panel.setBodyBorder(true);
			panel.setHeaderVisible(false);
			progressLabel = new Label();
			progressLabel.setWidth("510");
			progressLabel.setStyleName("jobMonitor_value");
			panel.add(progressLabel);
			progressTable.setWidget(1, 0, panel);
		} else {
			progressBar = new ProgressBar() {
				@Override
				public void updateText(String text) {
					text = text != null ? text : "&#160;";
					if (rendered) {
						El inner = el().firstChild();
						El textBackElem = inner.childNode(1);// .firstChild();
						textBackElem.setInnerHtml(text);
					}
				}
			};
			progressBar.setWidth(510);
			progressBar.setVisible(false);
			progressTable.setWidget(1, 0, progressBar);
		}

		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorStartTime());
		LayoutContainer lc2 = new LayoutContainer();
		lc2.add(label);
		// lc2.setWidth(220);
		progressTable.setWidget(2, 0, lc2);
		startTimeLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(2, 1, startTimeLabel);

		label = new Label();
		label.setStyleName("jobMonitor_label");

		if (type == JobMonitorModel.JOBTYPE_CATALOG_GRT) {
			processedFolderLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(3, 1, processedFolderLabel);
			label.setHtml(UIContext.Constants.jobMonitorGRTProcessedFolder());
		} else {
			elapsedLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(3, 1, elapsedLabel);
			label.setHtml(UIContext.Constants.jobMonitorLabelElapsedTime());
		}
		progressTable.setWidget(3, 0, label);

		label = new Label();
		label.setStyleName("jobMonitor_label");
		if (type == JobMonitorModel.JOBTYPE_CATALOG_GRT) {
			label.setHtml(UIContext.Constants.jobMonitorGRTTotalFolder());
			this.totalFolderLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(4, 1, totalFolderLabel);
		} else {
			label.setHtml(UIContext.Constants.jobMonitorEstimatedTime());
			estimateLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(4, 1, estimateLabel);
		}
		progressTable.setWidget(4, 0, label);

		currVolLable = new Label();
		currVolLable.setStyleName("jobMonitor_label");

		if ((type != JobMonitorModel.JOBTYPE_ARCHIVE)
				&& (type != JobMonitorModel.JOBTYPE_FILEARCHIVE)
				&& (type != JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC)
				&& (type != JobMonitorModel.JOBTYPE_ARCHIVEPURGE)
				&& (type != JobMonitorModel.JOBTYPE_ARCHIVERESTORE)
				&& (type != JobMonitorModel.JOBTYPE_RPS_REPLICATE)
				&& (type != JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND)
				&& (type != JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING)
				&& (type != JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN)
				&& (type != JobMonitorModel.JOBTYPE_COPY)) {

			if (type == JobMonitorModel.JOBTYPE_CATALOG_GRT)
				currVolLable.setHtml(UIContext.Constants
						.jobMonitorGRTCurrentFolder());
			else
				currVolLable.setHtml(UIContext.Constants
						.jobMonitorVolumeLabel());

			// currVolLable.setWidth(220);
			progressTable.setWidget(5, 0, currVolLable);
			volumeLabel.setStyleName("jobMonitor_value");
			// volumeLabel.setWidth(200);
			volumeLabel.setStyleAttribute("white-space", "pre-wrap");
			LayoutContainer lc = new LayoutContainer();
			lc.setWidth(230);
			lc.add(volumeLabel);
			progressTable.setWidget(5, 1, lc);

		}

		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorEncryptionStatus());
		progressTable.setWidget(6, 0, label);
		encryptionLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(6, 1, encryptionLabel);
		progressTable.getRowFormatter().setVisible(6, false);

		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorDedupe());
		progressTable.setWidget(7, 0, label);
		dedupeEnabled.setStyleName("jobMonitor_value");
		progressTable.setWidget(7, 1, dedupeEnabled);
		progressTable.getRowFormatter().setVisible(7, false);

		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorCompressLevel());
		progressTable.setWidget(8, 0, label);
		compressLevel.setStyleName("jobMonitor_value");
		progressTable.setWidget(8, 1, compressLevel);
		progressTable.getRowFormatter().setVisible(8, false);

		label = new Label();
		// label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorDedupeRate());
		progressTable
				.setWidget(9, 0, setLayoutStyle(label, "jobMonitor_label"));
		dedupeRateLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(9, 1, dedupeRateLabel);
		progressTable.getRowFormatter().setVisible(9, false);

		label = new Label();
		// label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorCompressRate());
		progressTable.setWidget(10, 0,
				setLayoutStyle(label, "jobMonitor_label"));
		compressRateLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(10, 1, compressRateLabel);
		progressTable.getRowFormatter().setVisible(10, false);

		label = new Label();
		label.setHtml(UIContext.Constants.jobMonitorOverDataReduction());
		LayoutContainer container = new LayoutContainer(new RowLayout());
		container.setStyleName("jobMonitor_label");
		container.add(label);
		progressTable.setWidget(11, 0, container);
		// totalSpaceLabel.setStyleName("jobMonitor_value");
		// progressTable.setWidget(11, 1, totalSpaceLabel);
		overallReduction = new LayoutContainer(new RowLayout());
		overallReduction.setStyleName("jobMonitor_value");
		totalSpaceLabel.setStyleAttribute("padding-right", "5px");
		overallReduction.add(totalSpaceLabel);
		detailIcon = new Image(UIContext.IconBundle.jobmonitor_detail());
		detailIcon.setVisible(false);
		overallReduction.add(detailIcon);
		progressTable.setWidget(11, 1, overallReduction);

		progressTable.getRowFormatter().setVisible(11, false);
		if(type == JobMonitorModel.JOBTYPE_BACKUP){
			Label descServerLabel = new Label();
			descServerLabel.setStyleName("jobMonitor_label");
			descServerLabel.setHtml(UIContext.Constants.destinationRPSServer());
			progressTable.setWidget(12, 0, descServerLabel);
			destinationRPSServerLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(12, 1, destinationRPSServerLabel);
			progressTable.getRowFormatter().setVisible(12, false);

			Label targetDSNameLabel = new Label();
			targetDSNameLabel.setStyleName("jobMonitor_label");
			targetDSNameLabel.setHtml(UIContext.Constants.targetDatastoreName());
			progressTable.setWidget(13, 0, targetDSNameLabel);
			LayoutContainer targetDSContainer = new LayoutContainer(new RowLayout());
			targetDSContainer.setWidth(220);
			targetDSContainer.setStyleName("jobMonitor_value");
			targetDSContainer.setStyleAttribute("word-wrap", "break-word");
			targetDSContainer.add(targetDatastoreNameLabel);
			progressTable.setWidget(13, 1, targetDSContainer);
			progressTable.getRowFormatter().setVisible(13, false);
		}else
		if (type == JobMonitorModel.JOBTYPE_RPS_REPLICATE
				|| type == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
				|| type == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING
				|| type == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
			Label currentSession = new Label();
			currentSession.setStyleName("jobMonitor_label");
			currentSession.setHtml(UIContext.Constants.currentSession());
			currentSessionLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(12, 0, currentSession);
			progressTable.setWidget(12, 1, currentSessionLabel);
			progressTable.getRowFormatter().setVisible(12, false);

			Label startSession = new Label();
			startSession.setStyleName("jobMonitor_label");
			startSession.setHtml(UIContext.Constants.startSession());
			startSessionLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(13, 0, startSession);
			progressTable.setWidget(13, 1, startSessionLabel);
			progressTable.getRowFormatter().setVisible(13, false);

			Label endSession = new Label();
			endSession.setStyleName("jobMonitor_label");
			endSession.setHtml(UIContext.Constants.endSession());
			endSessionLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(14, 0, endSession);
			progressTable.setWidget(14, 1, endSessionLabel);
			progressTable.getRowFormatter().setVisible(14, false);

			Label compressLabel = new Label();
			compressLabel.setStyleName("jobMonitor_label");
			compressLabel
					.setHtml(UIContext.Constants.jobMonitorCompressLevel());
			progressTable.setWidget(15, 0, compressLabel);
			compressLevel.setStyleName("jobMonitor_value");
			progressTable.setWidget(15, 1, compressLevel);
			progressTable.getRowFormatter().setVisible(15, false);

			Label encryLabel = new Label();
			encryLabel.setStyleName("jobMonitor_label");
			encryLabel
					.setHtml(UIContext.Constants.jobMonitorEncryptionStatus());
			progressTable.setWidget(16, 0, encryLabel);
			encryptionLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(16, 1, encryptionLabel);
			progressTable.getRowFormatter().setVisible(16, false);

			Label savedBandWidth = new Label();
			savedBandWidth.setStyleName("jobMonitor_label");
			savedBandWidth.setHtml(UIContext.Constants
					.jobMonitorRepSavedBandWidth());
			progressTable.setWidget(17, 0, savedBandWidth);
			savedBandWidthLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(17, 1, savedBandWidthLabel);
			progressTable.getRowFormatter().setVisible(17, false);

			Label sourceServerLabel = new Label();
			sourceServerLabel.setStyleName("jobMonitor_label");
			sourceServerLabel.setHtml(UIContext.Constants.sourceRPSServer());
			progressTable.setWidget(18, 0, sourceServerLabel);
			sourceRPSServerLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(18, 1, sourceRPSServerLabel);
			progressTable.getRowFormatter().setVisible(18, false);

			Label sourceDSNameLabel = new Label();
			sourceDSNameLabel.setStyleName("jobMonitor_label");
			sourceDSNameLabel
					.setHtml(UIContext.Constants.sourceDatastoreName());
			progressTable.setWidget(19, 0, sourceDSNameLabel);
			LayoutContainer sourceDSContainer = new LayoutContainer(
					new RowLayout());
			sourceDSContainer.setWidth(220);
			sourceDSContainer.setStyleName("jobMonitor_value");
			sourceDSContainer.setStyleAttribute("word-wrap", "break-word");
			sourceDSContainer.add(sourceDatastoreNameLabel);
			progressTable.setWidget(19, 1, sourceDSContainer);
			progressTable.getRowFormatter().setVisible(19, false);

			Label descServerLabel = new Label();
			descServerLabel.setStyleName("jobMonitor_label");
			descServerLabel.setHtml(UIContext.Constants.destinationRPSServer());
			progressTable.setWidget(20, 0, descServerLabel);
			destinationRPSServerLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(20, 1, destinationRPSServerLabel);
			progressTable.getRowFormatter().setVisible(20, false);

			Label targetDSNameLabel = new Label();
			targetDSNameLabel.setStyleName("jobMonitor_label");
			targetDSNameLabel
					.setHtml(UIContext.Constants.targetDatastoreName());
			progressTable.setWidget(21, 0, targetDSNameLabel);
			LayoutContainer targetDSContainer = new LayoutContainer(
					new RowLayout());
			targetDSContainer.setWidth(220);
			targetDSContainer.setStyleName("jobMonitor_value");
			targetDSContainer.setStyleAttribute("word-wrap", "break-word");
			targetDSContainer.add(targetDatastoreNameLabel);
			progressTable.setWidget(21, 1, targetDSContainer);
			progressTable.getRowFormatter().setVisible(21, false);
		}
		
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorDestinationType());
		progressTable.setWidget(22, 0, label);
		destinationTypeLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(22, 1, destinationTypeLabel);
		progressTable.getRowFormatter().setVisible(22, false);
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorDestinationPath());
		progressTable.setWidget(23, 0, label);
		destinationPathLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(23, 1, destinationPathLabel);
		progressTable.getRowFormatter().setVisible(23, false);
		
		progressPanel.add(progressTable);
		return progressPanel;
	}

	// Fix the issue:20184255
	private LayoutContainer setLayoutStyle(Label label, String styleName) {
		LayoutContainer labelContainer = new LayoutContainer();
		labelContainer.setStyleName(styleName);
		labelContainer.add(label);
		return labelContainer;
	}

	protected Widget setupChartHTML() {
		HTML reminder = StatusPieChartPanel.getFlashInstallReminder();
		if (reminder != null)
			return reminder;
		chartHtml.setHTML(produceChartHtml());
		return chartHtml;
	}

	protected String produceCategoryXML() {
		DateTimeFormat df = DateTimeFormat.getFormat("H:mm:ss");
		StringBuilder stringBuilder = new StringBuilder();
		int i = 0;
		DateWrapper startTime;
		stringBuilder.append("<categories >");
		int intervalCount = 60 / REFRESH_INTERVAL;

		startTime = new DateWrapper();
		startTime = startTime.addSeconds(-180);

		for (i = 0; i < HISTORY_ITEM_COUNT; i++) {
			stringBuilder.append("<category label='");
			stringBuilder.append(df.format(startTime.asDate()));
			stringBuilder.append("' ");

			if (i % intervalCount != 0) {
				stringBuilder.append("showLabel='0' />");
			} else {
				stringBuilder.append("showLabel='1' />");
				stringBuilder
						.append("<vLine color='c6c6c6' thickness='2' linePosition='0' alpha='20'/>");
			}
			startTime = startTime.addSeconds(REFRESH_INTERVAL);
		}

		stringBuilder.append("</categories>");
		return stringBuilder.toString();
	}

	protected String produceDatasetXML() {
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();
		stringBuilder
				.append("<dataset seriesName='Write Throughput Speed' color='6AB87D' lineThickness='2'>");
		stringBuilder2
				.append("<dataset seriesName='Read Throughput Speed' color='C05B5B' lineThickness='2'>");

		if (historyDeque.size() < HISTORY_ITEM_COUNT) {
			int count = HISTORY_ITEM_COUNT - historyDeque.size();
			for (int i = 0; i < count; i++) {
				stringBuilder.append("<set value=''/>");
				stringBuilder2.append("<set value=''/>");
			}
		}

		for (JobMonitorHistoryItemModel model : historyDeque) {
			stringBuilder.append("<set value='").append(model.getWriteSpeed())
					.append("'");
			stringBuilder.append(" />");

			stringBuilder2.append("<set value='").append(model.getReadSpeed())
					.append("'");
			stringBuilder2.append(" />");
		}

		stringBuilder.append("</dataset>");
		stringBuilder2.append("</dataset>");
		return stringBuilder.toString() + stringBuilder2.toString();
	}

	protected String produceChartXMLDataBegin() {
		if (historyDeque.isEmpty())
			yaxiMaxValue = DEFAULT_YAXIS_MAX;
		else {
			yaxiMaxValue = 0;
			for (JobMonitorHistoryItemModel item : historyDeque) {
				if (yaxiMaxValue < item.getReadSpeed())
					yaxiMaxValue = item.getReadSpeed();
				if (yaxiMaxValue < item.getWriteSpeed())
					yaxiMaxValue = item.getWriteSpeed();
			}
			if (yaxiMaxValue < DEFAULT_YAXIS_MAX)
				yaxiMaxValue = DEFAULT_YAXIS_MAX;
			else
				yaxiMaxValue += yaxiMaxValue * 0.05;
		}

		return "<chart lineThickness='1' showValues='0' formatNumberScale='0' adjustDiv='0' "
				+ "showAlternateHGridColor='0' alternateHGridAlpha='5' alternateHGridColor='CC3300'  "
				+ "defaultAnimation='0' yAxisMinValue='0' yAxisMaxValue='"
				+ yaxiMaxValue
				+ "' "
				+ "yAxisName='"
				+ UIContext.Constants.jobMonitorChartYaxisTitle()
				+ "' drawAnchors='0' showShadow='0' "
				+ "showLegend='0' showLabels='1' chartRightMargin='30' bgColor='ffffff' showBorder='0' numDivLines='4' showYAxisValues='1'>";
	}

	protected String produceChartXML() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(produceChartXMLDataBegin())
				.append(produceCategoryXML()).append(produceDatasetXML())
				.append(CHARTXMLDataEnd);

		return stringBuilder.toString();
	}

	protected String produceChartHtml() {
		String chartXML = produceChartXML();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(chartHTML1).append(chartXML).append(chartHTML2)
				.append(chartXML).append(chartHTML3);

		return stringBuilder.toString();
	}

	@Override
	protected void onHelp() {
		HelpTopics
				.showHelpURL(UIContext.externalLinks.getJobMonitorPanelHelp());
	}
}
