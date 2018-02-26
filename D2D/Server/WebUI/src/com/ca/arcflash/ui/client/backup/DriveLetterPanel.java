package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.webservice.data.backup.BackupSources;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

public class DriveLetterPanel extends LayoutContainer {
	
	public static final String SystemVolume = BackupSources.SystemVolume;
	public static final String BootVolume = BackupSources.BootVolume;
	public static final String RecoveryVolume= BackupSources.RecoveryVolume;
	private List<CheckBox> boxList;
	private CheckBox systemVolumeCheckBox;
	private CheckBox bootVolumeCheckBox;
	private CheckBox recoveryVolumeCheckBox;
	private Listener<BaseEvent> listenerForCheckbox;
	private CheckBox selectAllBox;
	
	private CheckBox lastDisabledCheckBox;
	private final static char StartDriverLetter='A';
	
	public DriveLetterPanel(Listener<BaseEvent> listener)
	{
		this();
		this.listenerForCheckbox = listener;
	}
	
	public DriveLetterPanel(){
		boxList = new ArrayList<CheckBox>();
		
		selectAllBox = new CheckBox();
		selectAllBox.ensureDebugId("6FDC8A95-CB63-4474-A6A6-CA35AD19C4D9");
		selectAllBox.setBoxLabel(UIContext.Constants.selectUnselectAll());
		selectAllBox.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				setSelectAll(selectAllBox.getValue());
			}
			
		});
		
		this.add(selectAllBox);
		
		LayoutContainer contentContainer = new LayoutContainer(new FitLayout());
		contentContainer.setBorders(true);
		contentContainer.add(this.createContent(), new FitData(2, 4, 2, 4));
		this.add(contentContainer);
	}
	
	private void setSelectAll(boolean select) {
		for (CheckBox box : boxList) {
			if (box != lastDisabledCheckBox) {
				box.setValue(select);
			}
		}
		
		systemVolumeCheckBox.setValue(select);
		recoveryVolumeCheckBox.setValue(select);
		bootVolumeCheckBox.setValue(select);
	}
	
	private Widget createContent() {
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new ColumnLayout());
		
		String debugID = "5F64BE38-16AF-42d2-A836-41C8FA780BA2";
		
		for (char ch = StartDriverLetter; ch <= 'Z'; ++ch) {
			CheckBox box = new CheckBox();
			box.setBoxLabel(getDisplayName(ch));
			box.ensureDebugId(debugID + "_" + ch);
			box.addListener(Events.Change, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					onCheckBoxChange(be);
				}
				
			});
			
			boxList.add(box);
			container.add(box, new ColumnData(80));
		}
		
		systemVolumeCheckBox = new CheckBox();
		systemVolumeCheckBox.ensureDebugId("DD6CF875-A2B6-4f2b-88E2-87C4B3949FB2");
		systemVolumeCheckBox.setBoxLabel(UIContext.Constants.systemVolume());
		systemVolumeCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				onCheckBoxChange(be);
			}
			
		});
		container.add(systemVolumeCheckBox, new ColumnData(320));
		
		this.recoveryVolumeCheckBox = new CheckBox();
		recoveryVolumeCheckBox.ensureDebugId("123dE71B-9E21-4963-8E4E-85E7991w3e43");
		recoveryVolumeCheckBox.setBoxLabel(UIContext.Constants.recoveryVolume());
		recoveryVolumeCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				onCheckBoxChange(be);
			}
			
		});
		container.add(recoveryVolumeCheckBox, new ColumnData(320));
		
		bootVolumeCheckBox = new CheckBox();
		bootVolumeCheckBox.ensureDebugId("112CE71B-9E21-4963-8E4E-85E799056495");
		bootVolumeCheckBox.setBoxLabel(UIContext.Constants.bootVolume());
		bootVolumeCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				onCheckBoxChange(be);
			}
			
		});
		container.add(bootVolumeCheckBox, new ColumnData());
		
		return container;
	}
	
	protected void onCheckBoxChange(FieldEvent fe) {
		if (listenerForCheckbox != null) {
			listenerForCheckbox.handleEvent(fe);
		}
		
		for (CheckBox box : boxList) {
			if (box != lastDisabledCheckBox && !box.getValue()) {
				selectAllBox.setValue(false);
				return;
			}
		}
		
		selectAllBox.setValue(systemVolumeCheckBox.getValue() && bootVolumeCheckBox.getValue()&& recoveryVolumeCheckBox.getValue());
	}
	
	protected String getDisplayName(char driveLetter) {
		return driveLetter + ":";
	}
	
	protected String getVolumnName(String displayName) {
		return displayName;
	}
	
	public List<String> getValue() {
		List<String> volumns = new ArrayList<String>();
		
		for (CheckBox box : boxList) {
			if (box.getValue()) {
				volumns.add(getVolumnName(box.getBoxLabel()));
			}
		}
		
		if (systemVolumeCheckBox.getValue()) {
			volumns.add(SystemVolume);
		}
		
		if (bootVolumeCheckBox.getValue()) {
			volumns.add(BootVolume);
		}
		if( this.recoveryVolumeCheckBox.getValue()  ) {
			volumns.add( RecoveryVolume );
		}
		return volumns;
	}
	
	public void setValue(List<String> value) {
		setSelectAll(false);
		
		if (value == null) {
			return;
		}
		
		for (String driveLetter : value) {
			if (driveLetter == null) {
				continue;
			}
			
			if (driveLetter.equals(SystemVolume)) {
				systemVolumeCheckBox.setValue(true);
			} else if (driveLetter.equals(BootVolume)) {
				bootVolumeCheckBox.setValue(true);
			} else if( driveLetter.equals(RecoveryVolume) ){
				recoveryVolumeCheckBox.setValue(true);
			}
			else {
				int offset = driveLetter.toUpperCase().charAt(0) - StartDriverLetter;
				if (offset >= 0 && offset < boxList.size()) {
					boxList.get(offset).setValue(true);
				}
			}
		}
	}
	
	public void enableAllDrive() {
		if (lastDisabledCheckBox != null) {
			lastDisabledCheckBox.enable();
			selectAllBox.setValue(false);
			lastDisabledCheckBox = null;
		}
	}
	
	public void disableDrive(String driveLetter) {
		if (driveLetter == null || driveLetter.isEmpty()) {
			return;
		}
		
		int offset = driveLetter.toUpperCase().charAt(0) - StartDriverLetter;
		if (offset < 0 || offset >= boxList.size()) {
			return;
		}
		
		if (lastDisabledCheckBox != null) {
			lastDisabledCheckBox.enable();
		}
		
		lastDisabledCheckBox = boxList.get(offset);
		lastDisabledCheckBox.setValue(false);
		lastDisabledCheckBox.disable();
	}
	
	public boolean isAllSelected() {
		return selectAllBox.getValue();
	}
	
}
