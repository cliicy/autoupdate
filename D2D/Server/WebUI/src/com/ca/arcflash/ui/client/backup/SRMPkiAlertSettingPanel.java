package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.ErrorAlignedNumberField;
import com.ca.arcflash.ui.client.model.EmailAlertsModel;
import com.ca.arcflash.ui.client.model.SRMAlertSettingModel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.FieldSet;
public class SRMPkiAlertSettingPanel extends SimpleContainer {

	class UsageElement{
		String elementName;
		Widget element;
		String elementUnit;
		UsageElement(String name, Widget element, String unit) {
			elementName = name;
			this.element = element;
			elementUnit = unit;
		}
	}
	
	private ErrorAlignedNumberField cpuAlertUtilThreshold = new ErrorAlignedNumberField();
	
	private ErrorAlignedNumberField memoryAlertUtilThreshold = new ErrorAlignedNumberField();
	
	private ErrorAlignedNumberField diskAlertUtilThreshold = new ErrorAlignedNumberField();
	
	private ErrorAlignedNumberField networkAlertUtilThreshold = new ErrorAlignedNumberField();
	
	private SRMAlertSettingModel alertModel;
	private SRMAlertSettingModel oldAlertModel;
	
	public SRMPkiAlertSettingPanel() {
		this.init();
	}
	
	private void init() {
		this.add(createCommonSettingView());
	}
	
	protected Widget createCommonSettingView() {
		initMembers();
		
		FlexTable tableContainer = new FlexTable();
		tableContainer.setCellSpacing(4);
		tableContainer.setWidth("100%");
        
		String alertUtilThreshold = UIContext.Constants.srm_alertUtilThreshold();
		List<UsageElement> elements = new ArrayList<UsageElement>();
		elements.add(new UsageElement(alertUtilThreshold, this.cpuAlertUtilThreshold, UIContext.Constants.srm_alertUtilThresholdUnit()));
		
		Widget usage = createUsageFieldSet(UIContext.Constants.srm_cpuUsage(), elements);
		tableContainer.setWidget(0, 0, usage);
		
		elements.clear();
		elements.add(new UsageElement(alertUtilThreshold, this.memoryAlertUtilThreshold, UIContext.Constants.srm_alertUtilThresholdUnit()));
		
		usage = createUsageFieldSet(UIContext.Constants.srm_memoryUsage(), elements);
		tableContainer.setWidget(0, 1, usage);
		
		elements.clear();
		elements.add(new UsageElement(alertUtilThreshold, this.diskAlertUtilThreshold, UIContext.Constants.srm_diskAlertUtilThresholdUnit()));
		
		usage = createUsageFieldSet(UIContext.Constants.srm_diskThroughput(), elements);
		tableContainer.setWidget(1, 0, usage);
		
		elements.clear();
		elements.add(new UsageElement(alertUtilThreshold, this.networkAlertUtilThreshold, UIContext.Constants.srm_alertUtilThresholdUnit()));
		
		usage = createUsageFieldSet(UIContext.Constants.srm_networkIO(), elements);
		tableContainer.setWidget(1, 1, usage);
        
    	return tableContainer;
	}
	
	public void loadData(EmailAlertsModel settingModel) {
		if (alertModel == null) {
			alertModel = new SRMAlertSettingModel();
			alertModel.getDefaultValue();
		}
		
		if (settingModel.getCpuAlertUtilThreshold() != null)
			alertModel.setCputhreshold(settingModel.getCpuAlertUtilThreshold());
		else
			settingModel.setCpuAlertUtilThreshold(alertModel.getCputhreshold());
		
		if (settingModel.getMemoryAlertUtilThreshold() != null)
			alertModel.setMemorythreshold(settingModel.getMemoryAlertUtilThreshold());
		else
			settingModel.setMemoryAlertUtilThreshold(alertModel.getMemorythreshold());
		
		if (settingModel.getDiskAlertUtilThreshold() != null)
			alertModel.setDiskthreshold(settingModel.getDiskAlertUtilThreshold());
		else
			settingModel.setDiskAlertUtilThreshold(alertModel.getDiskthreshold());
		
		if (settingModel.getNetworkAlertUtilThreshold() != null)
			alertModel.setNetworkthreshold(settingModel.getNetworkAlertUtilThreshold());
		else
			settingModel.setNetworkAlertUtilThreshold(alertModel.getNetworkthreshold());
		
		if (settingModel.getEnableSrmPkiAlert() != null) {
			alertModel.setValidalert(settingModel.getEnableSrmPkiAlert());
			alertModel.setValidpkiutl(settingModel.getEnableSrmPkiAlert());
		}
		
		this.refreshData();
	}
	
	public void saveData(EmailAlertsModel settingModel) {

		if ( alertModel != null && verifySettingData() == null && isSettingChanged() ) {
			GetUIData();
			settingModel.setCpuAlertUtilThreshold(alertModel.getCputhreshold());
			settingModel.setMemoryAlertUtilThreshold(alertModel.getMemorythreshold());
			settingModel.setDiskAlertUtilThreshold(alertModel.getDiskthreshold());
			settingModel.setNetworkAlertUtilThreshold(alertModel.getNetworkthreshold());
		}
	}
	
	private void initMembers() {
		int NumberFieldWidth = 85;

		cpuAlertUtilThreshold.ensureDebugId("C9FEC226-64CF-42b6-8EB6-6E6A329B7930");
		this.cpuAlertUtilThreshold.setAllowNegative(false);
		this.cpuAlertUtilThreshold.setMaxValue(100);
		this.cpuAlertUtilThreshold.setMinValue(1);
		this.cpuAlertUtilThreshold.setWidth(NumberFieldWidth);
		cpuAlertUtilThreshold.setAllowDecimals(false);
		cpuAlertUtilThreshold.setAllowBlank(false);
		
		memoryAlertUtilThreshold.ensureDebugId("B15EFD44-67BA-4d66-9B68-6D225C9F3CB1");
		this.memoryAlertUtilThreshold.setAllowNegative(false);
		this.memoryAlertUtilThreshold.setMaxValue(100);
		this.memoryAlertUtilThreshold.setMinValue(1);
		this.memoryAlertUtilThreshold.setWidth(NumberFieldWidth);
		memoryAlertUtilThreshold.setAllowDecimals(false);
		memoryAlertUtilThreshold.setAllowBlank(false);
		
		diskAlertUtilThreshold.ensureDebugId("EC8669DD-50BC-42c3-B05E-65E844774232");
		this.diskAlertUtilThreshold.setAllowNegative(false);
		this.diskAlertUtilThreshold.setMaxValue(1000);
		this.diskAlertUtilThreshold.setMinValue(1);
		this.diskAlertUtilThreshold.setWidth(NumberFieldWidth);
		diskAlertUtilThreshold.setAllowDecimals(false);
		diskAlertUtilThreshold.setAllowBlank(false);
		
		networkAlertUtilThreshold.ensureDebugId("B1F6DFB2-C9E5-425f-A6E3-552FBAEE78B2");
		this.networkAlertUtilThreshold.setAllowNegative(false);
		this.networkAlertUtilThreshold.setMaxValue(100);
		this.networkAlertUtilThreshold.setMinValue(1);
		this.networkAlertUtilThreshold.setWidth(NumberFieldWidth);
		networkAlertUtilThreshold.setAllowDecimals(false);
		networkAlertUtilThreshold.setAllowBlank(false);
		
	}
	
	private FieldSet createUsageFieldSet(String usageName, List<UsageElement> elements) {
		if ( elements == null || elements.size() == 0 )
			return null;
		
		final FieldSet fs = new FieldSet();
		fs.ensureDebugId("A91A8BD1-D82A-4fe7-AEE2-FBDEFB4FE991");
    	fs.setHeadingHtml(usageName);
    	
    	FlexTable layout = new FlexTable();
    	layout.setCellSpacing(4);
    	layout.setWidth("100%");
    	fs.setWidget(layout);

		for ( int i = 0; i < elements.size(); i++ ) {
//			tableData.setWidth("50%");
			Label l=new Label(elements.get(i).elementName);
			//set the style of the labal, reference  com.ca.arcserve.edge.app.base.ui.client.common.Gxt3Factory.createText(String text)
			l.getElement().getStyle().setFontSize(12, Unit.PX);
			l.getElement().getStyle().setPaddingTop(2, Unit.PX);
			l.getElement().getStyle().setPaddingRight(3, Unit.PX);
			l.getElement().getStyle().setPaddingBottom(3, Unit.PX);
			l.getElement().getStyle().setPaddingLeft(0, Unit.PX);
			
			layout.setWidget(i, 0, l);
			layout.setWidget(i, 1, elements.get(i).element);
//			tableData.setWidth("5%");
			Label unit = new Label(elements.get(i).elementUnit);
			//set the style of the labal, reference  com.ca.arcserve.edge.app.base.ui.client.common.Gxt3Factory.createText(String text)
			unit.getElement().getStyle().setFontSize(12, Unit.PX);
			unit.getElement().getStyle().setPaddingTop(2, Unit.PX);
			unit.getElement().getStyle().setPaddingRight(3, Unit.PX);
			unit.getElement().getStyle().setPaddingBottom(3, Unit.PX);
			unit.getElement().getStyle().setPaddingLeft(0, Unit.PX);
			
			layout.setWidget(i, 2, unit);
		}
		return fs;
	}
	
	private void refreshData() {
		if ( alertModel == null ) {
			alertModel = new SRMAlertSettingModel();
			alertModel.getDefaultValue();
		}
		
		setUIData();
	}

	private void setUIData() {
		if ( this.alertModel != null ) {
/*			this.setEnablePki(this.alertModel.isValidpkiutl());
			this.setEnableAlert(this.alertModel.isValidalert());

		    this.setUseDefaultSetting(this.alertModel.isUseglobalpolicy());
		    this.setEnableSRM(this.alertModel.isValidsrm());
		    if ( this.enableSRM.getValue() ) {
				SRMPkiAlertSettingPanel.this.useDefaultSetting.enable();

		    	if ( this.useDefaultSetting.getValue() ) {
			    	this.disableControls();
			    } else {
			    	this.enableControls();
			    }
		    } else {
		    	SRMPkiAlertSettingPanel.this.useDefaultSetting.disable();
				SRMPkiAlertSettingPanel.this.disableControls();
		    }*/
			
			//this.setCpuSamplingInterval(this.alertModel.getCpuinterval());
			this.setCpuAlertUtilThreshold(this.alertModel.getCputhreshold());
			//this.setCpuConsecutiveSamplingAmount(this.alertModel.getCpusampleamount());
			//this.setCpuMaxAlertNumOneDay(this.alertModel.getCpumaxalertnum());
			
			//this.setMemorySamplingInterval(this.alertModel.getMemoryinterval());
			this.setMemoryAlertUtilThreshold(this.alertModel.getMemorythreshold());
			//this.setMemoryConsecutiveSamplingAmount(this.alertModel.getMemorysampleamount());
			//this.setMemoryMaxAlertNumOneDay(this.alertModel.getMemorymaxalertnum());
			
			//this.setDiskSamplingInterval(this.alertModel.getDiskinterval());
			this.setDiskAlertUtilThreshold(this.alertModel.getDiskthreshold());
			//this.setDiskConsecutiveSamplingAmount(this.alertModel.getDisksampleamount());
			//this.setDiskMaxAlertNumOneDay(this.alertModel.getDiskmaxalertnum());
			
			//this.setNetworkSamplingInterval(this.alertModel.getNetworkinterval());
			this.setNetworkAlertUtilThreshold(this.alertModel.getNetworkthreshold());
			//this.setNetworkConsecutiveSamplingAmount(this.alertModel.getNetworksampleamount());
			//this.setNetworkMaxAlertNumOneDay(this.alertModel.getNetworkmaxalertnum());
		}
	}
	
/*	private void enableControls() {
		this.cpuAlertUtilThreshold.enable();
		this.cpuConsecutiveSamplingAmount.enable();
		this.cpuMaxAlertNumOneDay.enable();
		this.cpuSamplingInterval.enable();
		
		this.memoryAlertUtilThreshold.enable();
		this.memoryConsecutiveSamplingAmount.enable();
		this.memoryMaxAlertNumOneDay.enable();
		this.memorySamplingInterval.enable();
		
		this.diskAlertUtilThreshold.enable();
		this.diskConsecutiveSamplingAmount.enable();
		this.diskMaxAlertNumOneDay.enable();
		this.diskSamplingInterval.enable();
		
		this.networkAlertUtilThreshold.enable();
		this.networkConsecutiveSamplingAmount.enable();
		this.networkMaxAlertNumOneDay.enable();
		this.networkSamplingInterval.enable();
		
		this.enableAlert.enable();
		this.enablePki.enable();
	}*/
	
/*	private void disableControls() {
		this.cpuAlertUtilThreshold.disable();
		this.cpuConsecutiveSamplingAmount.disable();
		this.cpuMaxAlertNumOneDay.disable();
		this.cpuSamplingInterval.disable();
		
		this.memoryAlertUtilThreshold.disable();
		this.memoryConsecutiveSamplingAmount.disable();
		this.memoryMaxAlertNumOneDay.disable();
		this.memorySamplingInterval.disable();
		
		this.diskAlertUtilThreshold.disable();
		this.diskConsecutiveSamplingAmount.disable();
		this.diskMaxAlertNumOneDay.disable();
		this.diskSamplingInterval.disable();
		
		this.networkAlertUtilThreshold.disable();
		this.networkConsecutiveSamplingAmount.disable();
		this.networkMaxAlertNumOneDay.disable();
		this.networkSamplingInterval.disable();

		this.enableAlert.disable();
		this.enablePki.disable();
	}*/
	
	private void GetUIData() {
		if ( this.alertModel == null ) {
			this.alertModel = new SRMAlertSettingModel();
			this.alertModel.getDefaultValue();
		}
		
/*	    this.alertModel.setUseglobalpolicy(this.getUseDefaultSetting());
	    this.alertModel.setValidsrm(this.getEnableSRM());
		

		this.alertModel.setValidpkiutl(this.getEnablePki());
		this.alertModel.setValidalert(this.getEnableAlert());*/
		
		//CPU usage
		//this.alertModel.setCpuinterval(this.getCpuSamplingInterval());
		this.alertModel.setCputhreshold(this.getCpuAlertUtilThreshold());
		//this.alertModel.setCpusampleamount(this.getCpuConsecutiveSamplingAmount());
		//this.alertModel.setCpumaxalertnum(this.getCpuMaxAlertNumOneDay());
		
		//Memory usage
		//this.alertModel.setMemoryinterval(this.getMemorySamplingInterval());
		this.alertModel.setMemorythreshold(this.getMemoryAlertUtilThreshold());
		//this.alertModel.setMemorysampleamount(this.getMemoryConsecutiveSamplingAmount());
		//this.alertModel.setMemorymaxalertnum(this.getMemoryMaxAlertNumOneDay());
		
		//Disk throughput
		//this.alertModel.setDiskinterval(this.getDiskSamplingInterval());
		this.alertModel.setDiskthreshold(this.getDiskAlertUtilThreshold());
		//this.alertModel.setDisksampleamount(this.getDiskConsecutiveSamplingAmount());
		//this.alertModel.setDiskmaxalertnum(this.getDiskMaxAlertNumOneDay());
		
		//Network I/O
		//this.alertModel.setNetworkinterval(this.getNetworkSamplingInterval());
		this.alertModel.setNetworkthreshold(this.getNetworkAlertUtilThreshold());
		//this.alertModel.setNetworksampleamount(this.getNetworkConsecutiveSamplingAmount());
		//this.alertModel.setNetworkmaxalertnum(this.getNetworkMaxAlertNumOneDay());
	}
/*
	public boolean getEnablePki() {
		return enablePki.getValue();
	}

	public void setEnablePki(boolean enablePki) {
		this.enablePki.setValue(enablePki);
	}
	
	public boolean getEnableAlert() {
		return enableAlert.getValue();
	}

	public void setEnableAlert(boolean enableAlert) {
		this.enableAlert.setValue(enableAlert);
	}
	
	public boolean getUseDefaultSetting() {
		return useDefaultSetting.getValue();
	}

	public void setUseDefaultSetting(boolean useDefaultSetting) {
		this.useDefaultSetting.enableEvents(false);
		this.useDefaultSetting.setValue(useDefaultSetting);
		this.useDefaultSetting.enableEvents(true);
	}
	
	public boolean getEnableSRM() {
		return enableSRM.getValue();
	}

	public void setEnableSRM(boolean enableSRM) {
		this.enableSRM.enableEvents(false);
		this.enableSRM.setValue(enableSRM);
		this.enableSRM.enableEvents(true);
	}*/
	
/*	public int getCpuSamplingInterval() {
		return cpuSamplingInterval.getValue().intValue();
	}
	
	public void setCpuSamplingInterval(int cpuSamplingInterval) {
		this.cpuSamplingInterval.setValue(cpuSamplingInterval);
	}
*/	
	public int getCpuAlertUtilThreshold() {
		return cpuAlertUtilThreshold.getValue().intValue();
	}
	
	public void setCpuAlertUtilThreshold(int cpuAlertUtilThreshold) {
		this.cpuAlertUtilThreshold.setValue(cpuAlertUtilThreshold);
	}
	
/*	public int getCpuConsecutiveSamplingAmount() {
		return cpuConsecutiveSamplingAmount.getValue().intValue();
	}
	
	public void setCpuConsecutiveSamplingAmount(int cpuConsecutiveSamplingAmount) {
		this.cpuConsecutiveSamplingAmount.setValue(cpuConsecutiveSamplingAmount);
	}
	
	public int getCpuMaxAlertNumOneDay() {
		return cpuMaxAlertNumOneDay.getValue().intValue();
	}
	
	public void setCpuMaxAlertNumOneDay(int cpuMaxAlertNumOneDay) {
		this.cpuMaxAlertNumOneDay.setValue(cpuMaxAlertNumOneDay);
	}
	
	public int getMemorySamplingInterval() {
		return memorySamplingInterval.getValue().intValue();
	}
	
	public void setMemorySamplingInterval(int memorySamplingInterval) {
		this.memorySamplingInterval.setValue(memorySamplingInterval);
	}
 */	
	
	public int getMemoryAlertUtilThreshold() {
		return memoryAlertUtilThreshold.getValue().intValue();
	}
	
	public void setMemoryAlertUtilThreshold(int memoryAlertUtilThreshold) {
		this.memoryAlertUtilThreshold.setValue(memoryAlertUtilThreshold);
	}
	
/*	public int getMemoryConsecutiveSamplingAmount() {
		return memoryConsecutiveSamplingAmount.getValue().intValue();
	}
	
	public void setMemoryConsecutiveSamplingAmount(int memoryConsecutiveSamplingAmount) {
		this.memoryConsecutiveSamplingAmount.setValue(memoryConsecutiveSamplingAmount);
	}
	
	public int getMemoryMaxAlertNumOneDay() {
		return memoryMaxAlertNumOneDay.getValue().intValue();
	}
	
	public void setMemoryMaxAlertNumOneDay(int memoryMaxAlertNumOneDay) {
		this.memoryMaxAlertNumOneDay.setValue(memoryMaxAlertNumOneDay);
	}
	
	public int getDiskSamplingInterval() {
		return diskSamplingInterval.getValue().intValue();
	}
	
	public void setDiskSamplingInterval(int diskSamplingInterval) {
		this.diskSamplingInterval.setValue(diskSamplingInterval);
	}
*/	
	public int getDiskAlertUtilThreshold() {
		return diskAlertUtilThreshold.getValue().intValue();
	}
	
	public void setDiskAlertUtilThreshold(int diskAlertUtilThreshold) {
		this.diskAlertUtilThreshold.setValue(diskAlertUtilThreshold);
	}
	
/*	public int getDiskConsecutiveSamplingAmount() {
		return diskConsecutiveSamplingAmount.getValue().intValue();
	}
	
	public void setDiskConsecutiveSamplingAmount(int diskConsecutiveSamplingAmount) {
		this.diskConsecutiveSamplingAmount.setValue(diskConsecutiveSamplingAmount);
	}
	
	public int getDiskMaxAlertNumOneDay() {
		return diskMaxAlertNumOneDay.getValue().intValue();
	}
	
	public void setDiskMaxAlertNumOneDay(int diskMaxAlertNumOneDay) {
		this.diskMaxAlertNumOneDay.setValue(diskMaxAlertNumOneDay);
	}
	
	public int getNetworkSamplingInterval() {
		return networkSamplingInterval.getValue().intValue();
	}
	
	public void setNetworkSamplingInterval(int networkSamplingInterval) {
		this.networkSamplingInterval.setValue(networkSamplingInterval);
	}*/
	
	public int getNetworkAlertUtilThreshold() {
		return networkAlertUtilThreshold.getValue().intValue();
	}
	
	public void setNetworkAlertUtilThreshold(int networkAlertUtilThreshold) {
		this.networkAlertUtilThreshold.setValue(networkAlertUtilThreshold);
	}
	
	/*public int getNetworkConsecutiveSamplingAmount() {
		return networkConsecutiveSamplingAmount.getValue().intValue();
	}
	
	public void setNetworkConsecutiveSamplingAmount(int networkConsecutiveSamplingAmount) {
		this.networkConsecutiveSamplingAmount.setValue(networkConsecutiveSamplingAmount);
	}
	
	public int getNetworkMaxAlertNumOneDay() {
		return networkMaxAlertNumOneDay.getValue().intValue();
	}
	
	public void setNetworkMaxAlertNumOneDay(int networkMaxAlertNumOneDay) {
		this.networkMaxAlertNumOneDay.setValue(networkMaxAlertNumOneDay);
	}*/
	
	private boolean isSettingChanged() {
		if (this.alertModel == null && this.oldAlertModel == null) {
			return true;
		}
		
		//Determine whether the data are changed by users
		if ( this.oldAlertModel == null ) {
			oldAlertModel = this.alertModel;
		}
		
		
		
		this.alertModel = null;
		this.GetUIData();//Get the SRMPkiAlerSetting from UI
		
		
		if ( this.alertModel.isValidsrm() != oldAlertModel.isValidsrm()
		  || this.alertModel.isUseglobalpolicy() != oldAlertModel.isUseglobalpolicy() ) {
			this.oldAlertModel = null;
			return true;
		}

		if (  this.alertModel.isValidsrm() != oldAlertModel.isValidsrm()
		  || this.alertModel.isUseglobalpolicy() != oldAlertModel.isUseglobalpolicy()
		  || this.alertModel.isValidalert() != oldAlertModel.isValidalert() 
		  || this.alertModel.isValidpkiutl() != oldAlertModel.isValidpkiutl()
		  || this.alertModel.getCpuinterval() != oldAlertModel.getCpuinterval()
		  || this.alertModel.getCpumaxalertnum() != oldAlertModel.getCpumaxalertnum()
		  || this.alertModel.getCpusampleamount() != oldAlertModel.getCpusampleamount()
		  || this.alertModel.getCputhreshold() != oldAlertModel.getCputhreshold()
		  || this.alertModel.getMemoryinterval() != oldAlertModel.getMemoryinterval()
		  || this.alertModel.getMemorymaxalertnum() != oldAlertModel.getMemorymaxalertnum()
		  || this.alertModel.getMemorysampleamount() != oldAlertModel.getMemorysampleamount()
		  || this.alertModel.getMemorythreshold() != oldAlertModel.getMemorythreshold()
		  || this.alertModel.getDiskinterval() != oldAlertModel.getDiskinterval()
		  || this.alertModel.getDiskmaxalertnum() != oldAlertModel.getDiskmaxalertnum()
		  || this.alertModel.getDisksampleamount() != oldAlertModel.getDisksampleamount()
		  || this.alertModel.getDiskthreshold() != oldAlertModel.getDiskthreshold()
		  || this.alertModel.getNetworkinterval() != oldAlertModel.getNetworkinterval()
		  || this.alertModel.getNetworkmaxalertnum() != oldAlertModel.getNetworkmaxalertnum()
		  || this.alertModel.getNetworksampleamount() != oldAlertModel.getNetworksampleamount()
		  || this.alertModel.getNetworkthreshold() != oldAlertModel.getNetworkthreshold() ) {
			this.oldAlertModel = null;
			return true;
		}
		
		this.oldAlertModel = null;
		return false;
	}

	public String verifySettingData() {		
		
		if (!this.cpuAlertUtilThreshold.validate() || this.cpuAlertUtilThreshold.getValue() == null) {
			return UIContext.Messages.srmErrorParameterInvalid(this.cpuAlertUtilThreshold.getMinValue(), 
					this.cpuAlertUtilThreshold.getMaxValue());
		}
		
		
		if (!this.memoryAlertUtilThreshold.validate() || this.memoryAlertUtilThreshold.getValue() == null) {
			return UIContext.Messages.srmErrorParameterInvalid(this.memoryAlertUtilThreshold.getMinValue(), 
					this.memoryAlertUtilThreshold.getMaxValue());
		}
		
		if (!this.diskAlertUtilThreshold.validate() || this.diskAlertUtilThreshold.getValue() == null) {
			return UIContext.Messages.srmErrorParameterInvalid(this.diskAlertUtilThreshold.getMinValue(), 
					this.diskAlertUtilThreshold.getMaxValue());
		}
		
		if (!this.networkAlertUtilThreshold.validate() || this.networkAlertUtilThreshold.getValue() == null) 	{
			return UIContext.Messages.srmErrorParameterInvalid(this.networkAlertUtilThreshold.getMinValue(), 
					this.networkAlertUtilThreshold.getMaxValue());
		}
			
		return null;
		
	}

}
