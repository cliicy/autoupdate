package com.ca.arcflash.ui.client.backup;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class D2DDestinationSettingsForEdge extends BackupDestinationSettings{

	private DriveLetterPanel driveLetterPanel;

	public D2DDestinationSettingsForEdge(BackupSettingsContentForEdge w) {
		super(w);
	}
	
	@Override
	protected void createPathSelectionPanel() {
		pathSelection = new PathSelectionPanel(this.parentWindow.isForEdge(), new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				updateNotificationForEdge();
				String newDest = pathSelection.getDestination();
				setDestChangedBackupType(newDest);
				refreshSkippedDiskIcon();
					
				// qiubo01: fix issue 20080853 in EDGE
				// If the path is local disk, disable the related drive letter.
				
				if (pathSelection.isLocalPath()) {
					driveLetterPanel.disableDrive(pathSelection.getDestination().substring(0, 1));
				} else {
					driveLetterPanel.enableAllDrive();
				}
			}
		});

	}
	
	@Override
	protected void loadVolumeTreeData(BackupSettingsModel model) {
		driveLetterPanel.setValue(model.getBackupVolumes().selectedVolumesList);
		if (PathSelectionPanel.isLocalPath(model.getDestination())) {
			driveLetterPanel.disableDrive(model.getDestination().substring(0, 1));
		}
			
		return;
	}
	
	@Override
	protected void getVolumeList(BackupVolumeModel volumeModel) {
		volumeModel.selectedVolumesList = driveLetterPanel.getValue();
	}
	
	@Override
	protected LayoutContainer initVolumesGridContainer() {
		LayoutContainer sourceVolumesContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("96%");
		layout.setColumns(1);
		sourceVolumesContainer.setLayout(layout);
		sourceVolumesContainer.setStyleName("sourceVolumesLayout");

		driveLetterPanel = new DriveLetterPanel(new Listener<BaseEvent>(){
				@Override
				public void handleEvent(BaseEvent be) {
					updateNotificationForEdge();
				}
		});
		driveLetterPanel.setVisible(false);
		TableData data = new TableData();
		data.setWidth("100%");
		sourceVolumesContainer.add(driveLetterPanel, data);
		
		return sourceVolumesContainer;
	}
	
	protected void volumesPane(boolean show) {
		driveLetterPanel.setVisible(show);		
	}
	
	private void updateNotificationForEdge()
	{
			warnLoadingStatus.hideIndicator();
			notificationSet.removeAll();
			int warningNoForEdge = 0;
			String dest = pathSelection.getDestinationTextField().getValue();
			if(dest!=null)
			{
				dest = dest.trim();
				if(dest.length()>1)
				{
					dest=dest.substring(0, 2);
					if(dest.matches("[c-zC-Z]:"))
					{
						if(fullBackupRadio.getValue())
						{
							addWaringIcon();
							notificationSet.add(new LabelField(UIContext.Messages.backupSettingsDestChainOneOnLocal(dest)));
							warningNoForEdge++;
						}else if(selectVolumsRadio.getValue())
						{
							List<String> backupSourceVolumesForEdge = driveLetterPanel.getValue();
							if(backupSourceVolumesForEdge!=null)
							{
								for(String aBackupSourceVolumeForEdge : backupSourceVolumesForEdge)
								{
									if(aBackupSourceVolumeForEdge.equalsIgnoreCase(dest))
									{
										addWaringIcon();
										notificationSet.add(new LabelField(UIContext.Messages.backupSettingsDestChainOneOnLocal(dest)));
										warningNoForEdge++;
										break;
									}
								}
							}
						}
					}
				}
			}
			if(selectVolumsRadio.getValue()&&driveLetterPanel.getValue()!=null)
			{
				if(driveLetterPanel.getValue().size() != 24)
				{
					addWaringIcon();
					notificationSet.add(new LabelField(UIContext.Messages
							.backupSettingsOnSystemVolumeNotSelect("C:")));
					warningNoForEdge++;
				}


			}
			updateNotificationPane(warningNoForEdge, 0);
		
	}	

	@Override
	protected void refreshAllDatas() {
		updateNotificationMsg();
	}
	
	@Override
	protected void updateNotificationMsg() {
		updateNotificationForEdge();
	}	

	
	protected String validateBackupSource() {
		String msgStr = null;
		BackupVolumeModel volumes = getBackupVolumes();
		if(!volumes.getIsFullMachine()) {
			if(volumes.selectedVolumesList == null || volumes.selectedVolumesList.size() == 0) {
				msgStr = UIContext.Constants.destinationSelectAtLeastOneVolume();
			}
			else if(volumes.selectedVolumesList.size() == 1 && currentBackupDestVolume != null
					&& currentBackupDestVolume.getName().equalsIgnoreCase(volumes.selectedVolumesList.get(0))) {
				msgStr = UIContext.Constants.destinationSelectAtLeastOneVolumeButDest();
			}
		}
		return msgStr;
	}

	@Override
	protected LayoutContainer renderDestRPS() {
		destRpsContainer = new BackupRPSDestSettingsPanelForEdge(this);
		destRpsContainer.setVisible(false);
		return destRpsContainer;
	}
}
