package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.StatusPieChartPanel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class BackupSourePieChartPanel extends LayoutContainer {
	final CommonServiceAsync service = GWT.create(CommonService.class);
	
	private long oneIncrementalSize;
	private long onefullBackupSize;
	
	private LabelField totalSourceSizeLabel;
	private LabelField fullBackupSizeLabel;
	private LabelField incrBackupSizeLabel;
	private LabelField totalBackupSizeLabel;
	private ComboBox<ModelData> spaceSavedBox, growthRateBox, ntfsDeduplicationRate;
	private ListStore<ModelData> spaceSavedBoxStore, growthRateBoxStore, ntfsDeduplicationRateStore;
	private HTML backupPieChart;
	private Long estimatedTotalBackupSize;
	private LabelField legendFreeText;
	private LabelField legendUsedText;
	private LabelField legendBackupText;
	private LayoutContainer legendAndPieChart;
	private static String chartXMLDataSet;
	private LayoutContainer noEnoughFreeSpace;
	private boolean isIstalledFlash;
	private int width = 360;
	private int height = 170;
	private LabelField noDataToshow;

	private LabelField spaceUsedByCurrentBackups;
	private DestinationCapacityModel destCapacityModel;
	
	private long selectedTotalSize;
	private int retentionCount;
	
	private String chartXMLDataBegin = "<graph  showNames='0' decimalPrecision='2' showValues='0' pieRadius='85'> ";
	
	private D2DDestinationSettings destinationSettings;
	
	public BackupSourePieChartPanel(D2DDestinationSettings destinationSettings) {
		this.destinationSettings = destinationSettings;
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.destinationEstimatedBackupDiscription());
		add(label);

		LayoutContainer backupSize = new LayoutContainer();
		TableLayout backupLayout = new TableLayout();
		backupLayout.setColumns(2);
		backupSize.setLayout(backupLayout);

		LayoutContainer backupSizeChart = initPieChartPane();
		backupSize.add(backupSizeChart);

		LayoutContainer backupSizeValues = initEstimatedSizeContainer();
		backupSize.add(backupSizeValues);
		add(backupSize);
	}
	
	private LayoutContainer initPieChartPane() {
		TableLayout layout = new TableLayout();
		layout.setCellSpacing(2);
		layout.setWidth("100%");
		layout.setColumns(4);

		LayoutContainer legendCont = new LayoutContainer();
		legendCont.setLayout(layout);

		AbstractImagePrototype imagePrototype = IconHelper.create("images/legend_incremental.png", 16,16);
		Image backupLegendImage = imagePrototype.createImage();
	    legendCont.add(backupLegendImage);

	    legendBackupText = new LabelField();
	    //legendBackupText.setWidth(100);

	    legendCont.add(legendBackupText);

	    Image othersLegendImage = imagePrototype.createImage();
	    othersLegendImage.setUrl("images/legend_others.png");
	    legendCont.add(othersLegendImage);

	    legendUsedText = new LabelField();
	    legendCont.add(legendUsedText);

	    Image freeLegendImage = imagePrototype.createImage();
	    freeLegendImage.setUrl("images/legend_freeSpace.png");
	    legendCont.add(freeLegendImage);

	    legendFreeText = new LabelField();
	    legendCont.add(legendFreeText);

	    legendAndPieChart = new LayoutContainer();
	    TableLayout tl = new TableLayout();
	    tl.setWidth("100%");
	    tl.setHeight("100%");
	    legendAndPieChart.setLayout(tl);
	    legendAndPieChart.setStyleName("backupdest_piechart");
	    legendAndPieChart.setStyleAttribute("padding-top", "2px");

	    TableData tableData = new TableData();
//	    tableData.setHeight("10%");
	    tableData.setVerticalAlign(VerticalAlignment.TOP);
		legendAndPieChart.add(legendCont, tableData);

		noEnoughFreeSpace = new LayoutContainer();
		noDataToshow = new LabelField();
		backupPieChart = new HTML("<html><body bgcolor=\"#ffffff\"></body></html>", true);
		HTML flashInstall = StatusPieChartPanel.getFlashInstallReminder();
		if(flashInstall != null) {
			tableData = new TableData();
//			tableData.setHeight("90%");
			tableData.setVerticalAlign(VerticalAlignment.TOP);
			legendAndPieChart.add(flashInstall, tableData);
		}
		else {
			isIstalledFlash = true;
			TableLayout freeSpaceTl = new TableLayout();
			freeSpaceTl.setCellPadding(2);
			freeSpaceTl.setCellSpacing(2);
			freeSpaceTl.setColumns(2);
			noEnoughFreeSpace.setLayout(freeSpaceTl);
			Image icon = getWaringIcon();
			noEnoughFreeSpace.add(icon);
			//Waring: there's no enough free space on the backup destination to accommodate all the estimated backup.
			noEnoughFreeSpace.add(noDataToshow);
			noEnoughFreeSpace.setVisible(false);

			tableData = new TableData();
			tableData.setHeight("50%");
			tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
			tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
			legendAndPieChart.add(noEnoughFreeSpace, tableData);

			legendAndPieChart.add(backupPieChart);

		}
		LayoutContainer infoContainer = new LayoutContainer();
		tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
	    tl.setWidth("100%");
	    tl.setHeight("100%");
	    infoContainer.setLayout(tl);
		Image image = AbstractImagePrototype.create(UIContext.IconBundle.logMsg()).createImage();
		infoContainer.add(image);

		spaceUsedByCurrentBackups = new LabelField();
		spaceUsedByCurrentBackups.setValue(UIContext.Messages.backupSettingsSpaceUsedByBackups("0 GB"));
		infoContainer.add(spaceUsedByCurrentBackups);
		legendAndPieChart.add(infoContainer);
	    return legendAndPieChart;
	}
	
	private LayoutContainer initEstimatedSizeContainer() {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		container.setLayout(layout);

		FieldSet estimatedValues = new FieldSet();
		estimatedValues.ensureDebugId("A672BB87-AEC4-411c-9CF5-FEF57EB6E20A");
		estimatedValues.setHeadingHtml(UIContext.Constants.destinationEstmatedValue());
		estimatedValues.setCheckboxToggle(false);
		estimatedValues.setCollapsible(false);

		layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);

		estimatedValues.setLayout(layout);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.destinationDataPressedSize());
		estimatedValues.add(label);

		spaceSavedBox = new BaseComboBox<ModelData>();
		spaceSavedBox.ensureDebugId("77134410-53F8-44f5-8ACC-62ACFCC66C3E");
		spaceSavedBox.setEditable(false);
		List<ModelData> values = new ArrayList<ModelData>();
		for (int i = 0; i < 100; i+= 10) {
			ModelData md = new BaseModelData();
			md.set("value", i);
			md.set("displayName",UIContext.Messages.percentage(String.valueOf(i)));
			values.add(md);
		}
		
		StoreSorter<ModelData> StoreSorter = new StoreSorter<ModelData>();
		spaceSavedBoxStore = new ListStore<ModelData>();
		spaceSavedBoxStore.setStoreSorter(StoreSorter);
		spaceSavedBoxStore.setDefaultSort("value", SortDir.ASC);
		spaceSavedBoxStore.add(values);
		spaceSavedBox.setStore(spaceSavedBoxStore);
		spaceSavedBox.setValue(values.get(1));
		spaceSavedBox.setDisplayField("displayName");
		Utils.addToolTip(spaceSavedBox, UIContext.Constants.destinationDataPressedSizeTips());
		spaceSavedBox.setWidth(75);
		spaceSavedBox.addSelectionChangedListener(getEstimatePercentListener());

		estimatedValues.setWidth(280);

		estimatedValues.add(spaceSavedBox);

		label = new LabelField();
		label.setValue(UIContext.Constants.destinationDataChangeRate());
		estimatedValues.add(label);

		growthRateBox = new BaseComboBox<ModelData>();
		growthRateBox.ensureDebugId("1E73466B-32E0-4a91-88CA-BF99FC07D62D");
		growthRateBox.setEditable(false);
		List<ModelData> growthRateList = new ArrayList<ModelData>();
		for (int i = 1; i < 6; i++) {
			ModelData md = new BaseModelData();
			md.set("value", i);
			md.set("displayName",UIContext.Messages.percentage(String.valueOf(i)));
			growthRateList.add(md);
		}
		for (int i = 10; i < 100; i+= 10) {
			ModelData md = new BaseModelData();
			md.set("value", i);
			md.set("displayName",UIContext.Messages.percentage(String.valueOf(i)));
			growthRateList.add(md);
		}

		StoreSorter<ModelData> StoreSorter1 = new StoreSorter<ModelData>();
		growthRateBoxStore = new ListStore<ModelData>();
		growthRateBoxStore.add(growthRateList);
		growthRateBox.setStore(growthRateBoxStore);
		spaceSavedBoxStore.setStoreSorter(StoreSorter1);
		growthRateBoxStore.setDefaultSort("value", SortDir.ASC);		
		growthRateBox.setValue(values.get(1));
		growthRateBox.setDisplayField("displayName");
		Utils.addToolTip(growthRateBox, UIContext.Constants.destinationDataChangeRateTips());
		growthRateBox.setWidth(75);
		growthRateBox.addSelectionChangedListener(getEstimatePercentListener());

		estimatedValues.add(growthRateBox);

		//NTFS Deduplication Rate
		label = new LabelField();
		label.setValue(UIContext.Constants.destinationNTFSDeduplicationRate());
		estimatedValues.add(label);

		ntfsDeduplicationRate = new BaseComboBox<ModelData>();	
		ntfsDeduplicationRate.setEditable(false);
		List<ModelData> ntfsDeduplicationRateList = new ArrayList<ModelData>();		
		for (int i = 0; i < 100; i+= 10) {
			ModelData md = new BaseModelData();
			md.set("value", i);
			md.set("displayName",UIContext.Messages.percentage(String.valueOf(i)));
			ntfsDeduplicationRateList.add(md);
		}
		
		ntfsDeduplicationRateStore = new ListStore<ModelData>();
		ntfsDeduplicationRateStore.add(ntfsDeduplicationRateList);
		ntfsDeduplicationRate.setStore(ntfsDeduplicationRateStore);		
		ntfsDeduplicationRateStore.setDefaultSort("value", SortDir.ASC);		
		ntfsDeduplicationRate.setValue(values.get(1));
		ntfsDeduplicationRate.setDisplayField("displayName");
		Utils.addToolTip(ntfsDeduplicationRate, UIContext.Constants.destinationNTFSDeduplicationRateTips());
		ntfsDeduplicationRate.setWidth(75);
		ntfsDeduplicationRate.addSelectionChangedListener(getEstimatePercentListener());

		estimatedValues.add(ntfsDeduplicationRate);		
		
		container.add(estimatedValues);

		FieldSet estimatedBackupValues = new FieldSet();
		estimatedBackupValues.ensureDebugId("C71DCFC8-5FFB-46fe-90BD-AC2FB0641EA9");
		estimatedBackupValues.setHeadingHtml(UIContext.Constants.destinationEstimatedBackupSize());
		estimatedBackupValues.setCheckboxToggle(false);
		estimatedBackupValues.setCollapsible(false);
		estimatedBackupValues.setWidth(280);

		layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);

		estimatedBackupValues.setLayout(layout);

		TableData fieldData = new TableData();
		fieldData.setWidth("76%");

		label = new LabelField();
		label.setValue(UIContext.Constants.destinationTotalSourceSize());
		estimatedBackupValues.add(label, fieldData);

		totalSourceSizeLabel = new LabelField();

		TableData valueData = new TableData();
		valueData.setWidth("24%");
		valueData.setHorizontalAlign(HorizontalAlignment.CENTER);
		estimatedBackupValues.add(totalSourceSizeLabel, valueData);

		label = new LabelField();
		label.setValue(UIContext.Constants.destinationCompressedFullBackupSize());

		fieldData = new TableData();
		fieldData.setWidth("76%");
		estimatedBackupValues.add(label, fieldData);

		fullBackupSizeLabel = new LabelField();
		estimatedBackupValues.add(fullBackupSizeLabel, valueData);

		label = new LabelField();
		label.setValue(UIContext.Constants.destinationCompressedIncrementalBackupSize());

		fieldData = new TableData();
		fieldData.setWidth("76%");
		estimatedBackupValues.add(label, fieldData);

		incrBackupSizeLabel = new LabelField();
		estimatedBackupValues.add(incrBackupSizeLabel, valueData);

		label = new LabelField();
		label.setValue(UIContext.Constants.destinationEstimatedTotalBackupSize());

		fieldData = new TableData();
		fieldData.setWidth("76%");
		estimatedBackupValues.add(label, fieldData);

		totalBackupSizeLabel = new LabelField();
		estimatedBackupValues.add(totalBackupSizeLabel,valueData);

		container.add(estimatedBackupValues);

		return container;
	}
	
	private SelectionChangedListener<ModelData> getEstimatePercentListener() {
		return new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(
					SelectionChangedEvent<ModelData> se) {
				updateEstimatedSize(selectedTotalSize, retentionCount);
				updateEstimatedPieChart();
			}
		};
	}
	
	public void updateEstimatedSize_old(long selectedVolSize, int retentionCount) {
		if(selectedVolSize <= 0)
			return;
		
		this.selectedTotalSize = selectedVolSize;
		this.retentionCount = retentionCount;
		
		String sourceSize = Utils.bytes2String(selectedVolSize);

		totalSourceSizeLabel.setValue(sourceSize);

		Integer percent = spaceSavedBox.getValue().<Integer> get("value");
		
		onefullBackupSize = getSize(percent, selectedVolSize, true);		
		fullBackupSizeLabel.setValue(Utils.bytes2String(onefullBackupSize));
		percent = growthRateBox.getValue().get("value");
		long incrBackup = 0;
		if(retentionCount > 1) {
			oneIncrementalSize = getSize(percent, onefullBackupSize, false);
			incrBackup = oneIncrementalSize * (retentionCount - 1);
		}	
	
		incrBackupSizeLabel.setValue(Utils.bytes2String(incrBackup));		
		estimatedTotalBackupSize = onefullBackupSize + incrBackup;		
		setTotalEstimatedBackupSize(estimatedTotalBackupSize);
	}
	
	
	public void updateEstimatedSize(long selectedVolSize, int retentionCount) {
		if(selectedVolSize <= 0)
			return;
		
		this.selectedTotalSize = selectedVolSize;
		this.retentionCount = retentionCount;
		
		long totalFullBackupSize = selectedVolSize;
	
		int savedAfterCompress = spaceSavedBox.getValue().<Integer> get("value");		
		int percent = growthRateBox.getValue().get("value");
		int dedupRate = ntfsDeduplicationRate.getValue().get("value");
		
		double compressRate = (100 - savedAfterCompress)/ 100d;
		double changeRate = percent/100d;		
		double dedupateRate = (100-dedupRate)/100d;
		
		//1. Total source size
		totalSourceSizeLabel.setValue( Utils.bytes2String(selectedVolSize));
		
		//2. Compressed Full Backup Size
		onefullBackupSize = (long) (totalFullBackupSize * compressRate);		
		fullBackupSizeLabel.setValue(Utils.bytes2String(onefullBackupSize));

		//3. Compressed Incremental Backup Size		
		long totalIncrementalBackupSize = 0;
		if(retentionCount-1 > 0) {// there are N-1 incr backup.
			long oneIncrementalSizeBeforeCompress = (long)(totalFullBackupSize * changeRate);
			totalIncrementalBackupSize = oneIncrementalSizeBeforeCompress * (retentionCount - 1);
			oneIncrementalSize = (long) (oneIncrementalSizeBeforeCompress*compressRate);
		}	
		incrBackupSizeLabel.setValue(Utils.bytes2String((long)(totalIncrementalBackupSize*compressRate)));
		
		//4. windows dedupe		
		long totalBackupSize = totalFullBackupSize + totalIncrementalBackupSize;
		estimatedTotalBackupSize = (long)(totalBackupSize*compressRate*dedupateRate);
		
		setTotalEstimatedBackupSize(estimatedTotalBackupSize);
	}
	
	public void updateEstimatedPieChart() {

//		if(totalFreeSize == null || destUsedSize == null || (totalFreeSize + destUsedSize) == 0
		if(destCapacityModel == null || destCapacityModel.getTotalVolumeSize() == 0
				 || estimatedTotalBackupSize == null || estimatedTotalBackupSize == 0) {
			long free = 0;
			long used = 0;
			long esti = 0;
			long backupSize = 0;
			if(estimatedTotalBackupSize == null || estimatedTotalBackupSize == 0) {
				noDataToshow.setValue(UIContext.Constants.destinationNoVolumesToBackup());
			}
			else
				esti = estimatedTotalBackupSize;

//			if(totalFreeSize == null || destUsedSize == null || (totalFreeSize + destUsedSize) == 0)
			if(destCapacityModel == null || destCapacityModel.getTotalVolumeSize() == 0)
				noDataToshow.setValue(UIContext.Constants.destinationCanNotGetDestinationInfo());
			else {
				backupSize = getBackupDataSize(destCapacityModel);
				free = destCapacityModel.getTotalFreeSize() + backupSize;
				used = destCapacityModel.getTotalVolumeSize() - free - backupSize;
			}

			updateLegendIconText(free, esti, used, backupSize);
			showPieChart(false);
			return;
		}
//		else
//			pieChartContainer.unmask();

		Long diskFreeSize = destCapacityModel.getTotalFreeSize();
		long backupSize = getBackupDataSize(destCapacityModel);
		long freeSizeUsedForBackup = diskFreeSize + backupSize;
		long totalFreeSize = freeSizeUsedForBackup - estimatedTotalBackupSize;
//		estimatedTotalBackupSize += backupSize;
		long destUsedSize = destCapacityModel.getTotalVolumeSize() - backupSize - diskFreeSize;

		updateLegendIconText(totalFreeSize,estimatedTotalBackupSize, destUsedSize, backupSize);

		if(!isIstalledFlash)
			return;

		StringBuilder concretData = new StringBuilder(chartXMLDataBegin);
		if(totalFreeSize < 0) {
			showPieChart(false);
			long num = 0;
			if(oneIncrementalSize > 0 && freeSizeUsedForBackup > onefullBackupSize)
				num = (freeSizeUsedForBackup - onefullBackupSize)/oneIncrementalSize + 1;
			noDataToshow.setValue(UIContext.Messages.destinationNoDiskEnoughSpaceAndEstiCount(Utils.bytes2GBString(-totalFreeSize), num));
			return;
		}

		showPieChart(true);
		String color = "97a4ab";//	125,136,143
		concretData.append(getXMLDataSet(UIContext.Constants.destinationFree(), totalFreeSize, color));

		color = "1d72b6"; //20 96 156
		concretData.append(getXMLDataSet(UIContext.Constants.destinationEstimatedBackup(), estimatedTotalBackupSize, color));

		color = "f6ad40";//"b5802c";//"f9b144";//249 177 68
		concretData.append(getXMLDataSet(UIContext.Constants.destinationUsed(), destUsedSize, color));

		String chartXMLDataEnd = "</graph>";
		concretData.append(chartXMLDataEnd);

		String chartHTML = new StringBuilder()
		.append("<html>")
		.append("  <body bgcolor=\"#ffffff\" >")
		.append("    <OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"").append(width).append("\" height=\"").append(height).append("\" id=\"Pie3D\">")
		.append("      <param name=\"movie\" value=\"FusionCharts/FCF_Pie3D.swf\" />")
		.append("      <param name=\"FlashVars\" ")
		.append("             value=\"&dataXML=ChartXMLData&chartWidth=").append(width).append("&chartHeight=").append(height).append("\">")
		.append("      <param name=wmode value=transparent>")
		.append("      <param name=\"quality\" value=\"high\" />")
		.append("      <embed src=\"../FusionCharts/FCF_Pie3D.swf\" ")
		.append("             flashVars=\"&dataXML=ChartXMLData&chartWidth=").append(width).append("&chartHeight=").append(height).append("\"")
		.append("             quality=\"high\" wmode=transparent width=\"").append(width).append("\" height=\"").append(height).append("\" name=\"Pie3D\" ")
		.append("             type=\"application/x-shockwave-flash\" />")
		.append("    </OBJECT>")
		.append("  </body>")
		.append("</html>").toString();
		String chartHTMLString = chartHTML.replaceAll("ChartXMLData", concretData.toString());
		backupPieChart.setHTML(chartHTMLString);
	}
	
	private void showPieChart(boolean showPieChart) {
		noEnoughFreeSpace.setVisible(!showPieChart);
		backupPieChart.setVisible(showPieChart);
	}

	public static String getXMLDataSet(String statusName,
			long size, String color) {
		String sizeStr = Utils.number.format(((double)size)/(1024*1024*1024));
		sizeStr = Utils.number.parse(sizeStr) + "";
		chartXMLDataSet = "<set name='SETNAME' value='COUNT' color='COLOR' />";
		return chartXMLDataSet.replace("SETNAME", statusName)
                              .replace("COUNT", sizeStr)
                              .replace("COLOR", color);
	}
	
	private void updateLegendIconText(long totalFreeSize, long estimatedSize, long destUsedSize, long usedBackupSize) {
		if(totalFreeSize < 0)
			totalFreeSize = 0;
		if(destUsedSize < 0)
			destUsedSize = 0;
		legendFreeText.setValue(UIContext.Messages.homepageSummaryLegendFree(Utils.bytes2GBString(totalFreeSize)));
		legendBackupText.setValue(UIContext.Messages.backupSettingsEstimatedBakupSize(Utils.bytes2GBString(estimatedSize)));
		legendUsedText.setValue(UIContext.Messages.backupSettingsUsedSize(Utils.bytes2GBString(destUsedSize)));
		spaceUsedByCurrentBackups.setValue(UIContext.Messages.backupSettingsSpaceUsedByBackups(Utils.bytes2GBString(usedBackupSize)));
	}
	
	private long getSize(int percent, long total, boolean minus100) {
		int size = percent;
		if(minus100)
			return total * (100 - size)/ 100;
		else
			return total * size / 100;
	}
	
	private void setTotalEstimatedBackupSize(long size) {
		totalBackupSizeLabel.setValue(Utils.bytes2String(size));
	}
	
	public Listener<BaseEvent> getRetentioncountListener() {
		return new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				retentionCount = destinationSettings.getRetentionCount();
				updateEstimatedSize(selectedTotalSize, retentionCount);
				updateEstimatedPieChart();
			}

		};
	}
	
	public SelectionChangedListener<SimpleComboValue<String>> setupCompressionChangeListener() {
		return new SelectionChangedListener<SimpleComboValue<String>>(){
			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				String selString = se.getSelectedItem().getValue();
				if (selString.compareTo(UIContext.Constants.settingsCompressionNone()) == 0)
				{
					spaceSavedBox.setValue(spaceSavedBoxStore.getAt(0));
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompreesionStandard()) == 0)
				{	
					spaceSavedBox.setValue(spaceSavedBoxStore.getAt(1));
				}
				else if (selString.compareTo(UIContext.Constants.settingsCompressionMax()) == 0)
				{	
					if(spaceSavedBox.getValue().<Integer> get("value") < 20)
					{
						spaceSavedBox.setValue(spaceSavedBoxStore.getAt(2));
					}
				}
			}			
		};		
	}
	
	private long getBackupDataSize(DestinationCapacityModel model) {
		if(model != null)
			return model.getFullBackupSize() + model.getIncrementalBackupSize() + model.getResyncBackupSize() + model.getCatalogSize();
		return 0;
	}
	
	public void loadDestDiskDetailsAndUpdateChart(PathSelectionPanel pathSelection) {

		final String dest = pathSelection.getDestination();
		destCapacityModel = null;
		if(isEmpty(dest) ||
				!PathSelectionPanel.isLocalPath(dest) && (isEmpty(pathSelection.getUsername()) || 
						isEmpty(pathSelection.getUsername()))) {
			updateEstimatedPieChart();
			return;
		}
		
		legendAndPieChart.mask(UIContext.Constants
				.destinationLoadingBackupDiskInfo());
		service.getDestCapacity(dest, "", pathSelection.getUsername(),
				pathSelection.getPassword(),
				new BaseAsyncCallback<DestinationCapacityModel>() {
					@Override
					public void onFailure(Throwable caught) {
						legendAndPieChart.unmask();
					}

					@Override
					public void onSuccess(DestinationCapacityModel destCapacity) {
						legendAndPieChart.unmask();

						destCapacityModel = destCapacity;
						updateEstimatedPieChart();
					}
		});
	}
	
	public void getLocalDistDetailsAndUpdateChart(long destTotalSize, long destFreeSize) {
		destCapacityModel = new DestinationCapacityModel();
		destCapacityModel.setTotalVolumeSize(destTotalSize);
		destCapacityModel.setTotalFreeSize(destFreeSize);
		/*
		 * destCapacityModel.setTotalVolumeSize(currentBackupDestVolume.getTotalSize
		 * ());
		 * destCapacityModel.setTotalFreeSize(currentBackupDestVolume.getFreeSize
		 * ());
		 */
		destCapacityModel.setIncrementalBackupSize(0l);
		destCapacityModel.setFullBackupSize(0l);
		destCapacityModel.setResyncBackupSize(0l);
		destCapacityModel.setCatalogSize(0L);

		updateEstimatedPieChart();
	}
	
	public void refreshData(BackupSettingsModel model) {
		if (model != null) {
			Integer spaceSavedAfterCompression = model.getSpaceSavedAfterCompression();
			if(spaceSavedAfterCompression != null && spaceSavedAfterCompression >= 0){
				if(spaceSavedBox.getStore().findModel("value", spaceSavedAfterCompression) == null){
					ModelData md = new BaseModelData();
					md.set("value", spaceSavedAfterCompression);
					md.set("displayName",UIContext.Messages.percentage(String.valueOf(spaceSavedAfterCompression)));
					spaceSavedBox.getStore().add(md);
					spaceSavedBox.getStore().sort("value", SortDir.ASC);
				}
				setSaveBox(spaceSavedAfterCompression.intValue());
			}
			else {
				if(isStandardCompression(model))
					setSaveBox(10);
				else if(isMaxCompression(model))
					setSaveBox(20);
			}

			Integer growthRate = model.getGrowthRate();
			if(growthRate != null && growthRate >= 0){
				
				if(growthRateBox.getStore().findModel("value", growthRate) == null){
					ModelData md = new BaseModelData();
					md.set("value", growthRate);
					md.set("displayName",UIContext.Messages.percentage(String.valueOf(growthRate)));
					growthRateBox.getStore().add(md);
					growthRateBox.getStore().sort("value", SortDir.ASC);
				}
				setGrowthRateBox(model.getGrowthRate());
			}
			
			Integer ntfsDeduplicationRateValue = model.getWindowsDeduplicationRate();
			if(ntfsDeduplicationRateValue != null && ntfsDeduplicationRateValue >= 0){
				
				if(ntfsDeduplicationRate.getStore().findModel("value", ntfsDeduplicationRateValue) == null){
					ModelData md = new BaseModelData();
					md.set("value", ntfsDeduplicationRateValue);
					md.set("displayName",UIContext.Messages.percentage(String.valueOf(ntfsDeduplicationRateValue)));
					ntfsDeduplicationRate.getStore().add(md);
					ntfsDeduplicationRate.getStore().sort("value", SortDir.ASC);
				}
				setNtfsDeduplicationRate(model.getWindowsDeduplicationRate());
			}
		}
	}
	
	public void saveData(BackupSettingsModel model) {
		int percent = spaceSavedBox.getValue().<Integer> get("value");
		model.setSpaceSavedAfterCompression(percent);
		percent = growthRateBox.getValue().<Integer> get("value");
		model.setGrowthRate(percent);
		percent = ntfsDeduplicationRate.getValue().<Integer> get("value");
		model.setWindowsDeduplicationRate(percent);
	}
	
	public void setEditable(boolean editable){
		spaceSavedBox.setEnabled(editable);
		growthRateBox.setEnabled(editable);
		ntfsDeduplicationRate.setEnabled(editable);
	}
	
	private boolean isMaxCompression(BackupSettingsModel model) {
		if(model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 9;
	}

	private boolean isStandardCompression(BackupSettingsModel model) {
		if(model == null || model.getCompressionLevel() == null)
			return false;

		return model.getCompressionLevel() == 1;
	}
	
	private void setSaveBox(int value)
	{
		for(int i=0;i<spaceSavedBoxStore.getCount();i++)
		{
			if(spaceSavedBoxStore.getAt(i).<Integer> get("value") == value)
			{
				spaceSavedBox.setValue(spaceSavedBoxStore.getAt(i));
				break;
			}
		}
	}

	private void setGrowthRateBox(int value)
	{
		for(int i=0;i<growthRateBoxStore.getCount();i++)
		{
			if(growthRateBoxStore.getAt(i).<Integer> get("value") == value)
			{
				growthRateBox.setValue(growthRateBoxStore.getAt(i));
				break;
			}
		}
	}
	
	private void setNtfsDeduplicationRate(int value)
	{
		for(int i=0;i<ntfsDeduplicationRateStore.getCount();i++)
		{
			if(ntfsDeduplicationRateStore.getAt(i).<Integer> get("value") == value)
			{
				ntfsDeduplicationRate.setValue(ntfsDeduplicationRateStore.getAt(i));
				break;
			}
		}
	}
	
	protected boolean isEmpty(final String remoteDest) {
		return remoteDest == null || remoteDest.length() == 0;
	}
	
	protected Image getWaringIcon() {
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		return warningImage;
	}	
}
