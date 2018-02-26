package com.ca.arcflash.ui.client.homepage;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DataStoreInfoModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


public class DataStoreDetailWindow  extends Window {
	private DataStoreInfoModel dataStoreInfoModel;
	LayoutContainer container;
	FieldSet basicFS;
	FieldSet dedupFS;
	private static DataStoreDetailWindow window = null;
	
	public static void show(DataStoreInfoModel dsim, String dataStoreDisplayName)
	{
		if (window == null) {
		    window = new DataStoreDetailWindow(dsim, dataStoreDisplayName);
		    window.setModal(true);
		    window.show();
		}
	}

	private DataStoreDetailWindow(DataStoreInfoModel dsim, String dataStoreDisplayName) {
		this.dataStoreInfoModel = dsim;
		this.setLayout(new FitLayout());
		container = new LayoutContainer();
		
		basicFS = new FieldSet();
		basicFS.addStyleName("homepage_summary_fieldset");
		basicFS.setHeadingHtml("Basic Storage");
		
		dedupFS = new FieldSet();
		dedupFS.addStyleName("homepage_summary_fieldset");
		dedupFS.setHeadingHtml("Deduplication Storage");
		
		this.setHeadingHtml(dataStoreDisplayName);
		addButtons();
		add(this.container);
		
		refresh();
	}
	
	private void appendTDChart(StringBuffer buffer, int percent, String image, String title){
		buffer.append("<td ");
		buffer.append(" title=\"");
		buffer.append(title);
		buffer.append("\" width=\"");
		buffer.append(percent);
		buffer.append("%\" style=\"background-image: url(./");
		buffer.append(image);
		buffer.append(");\"/>");
	}
	
	private void addButtons() {			
		Button closeButton = new Button();
		closeButton.setText(UIContext.Constants.close());		
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					window.hide();
					window = null;
				}
			});	
		
		this.addButton(closeButton);		
	}
	
	private void addPath(LayoutContainer layoutContainer, String name, String value) {
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		LayoutContainer lc = new LayoutContainer();
		lc.setLayout(layout);
		lc.setStyleAttribute("padding-bottom", "6px");
		layoutContainer.add(lc);
		
		Label destination = new Label();
	    destination.setStyleName("homepage_summary_fieldset_label");
	    destination.setText(name);
	    Label destinationValue = new Label();
	    destinationValue.setStyleName("homepage_summary_legengLabel");
		destinationValue.setText(value);
		
		lc.add(destination);	
		lc.add(destinationValue);
	}
	
	private void addChart(LayoutContainer layoutContainer, double total, long backupSpace, long freeSpace) {
        double backup = ((double)(backupSpace))/total;
        double free = ((double)freeSpace)/total;
        double others = ((double)(total - backupSpace - freeSpace))/total;

        GWT.log("Other Percent:"+String.valueOf(others), null);
        int backupPercent = (int)(backup*100);
        int freePercent = (int)(free*100);
        int othersPercent = (int)(others*100);

        StringBuffer buffer = new StringBuffer();
        buffer.append("<table width=\"85%\" height=\"15\" style=\"border:1px solid #000000; margin: 0px;\" CELLPADDING=0 CELLSPACING=0>");
        buffer.append("<tr>");

		appendTDChart(buffer,backupPercent, "images/legend_incremental.png", "");
		appendTDChart(buffer,othersPercent, "images/legend_others.png", "");
	    appendTDChart(buffer,freePercent, "images/legend_freeSpace.png", "");

        buffer.append("</tr></table>");

        HTML destinationHtml = new HTML();
        destinationHtml.setHTML(buffer.toString());
        
        layoutContainer.add(destinationHtml);
	}
	
	private void addPicAndValue(LayoutContainer layoutContainer, double total, long backupSpace, long freeSpace) {
		TableLayout layout = new TableLayout();
		layout.setColumns(6);
		LayoutContainer lc = new LayoutContainer();
		lc.setLayout(layout);
		lc.setStyleAttribute("padding-top", "6px");
		lc.setStyleAttribute("padding-bottom", "15px");
		layoutContainer.add(lc);
		
		AbstractImagePrototype imagePrototype = IconHelper.create("images/legend_incremental.png", 16,16);
		Image backupLegendImage = imagePrototype.createImage();
	    Label legendBackupText = new Label();
	    legendBackupText.setStyleName("homepage_summary_legengLabel");
	    
	    Image othersLegendImage = imagePrototype.createImage();
	    othersLegendImage.setUrl("images/legend_others.png");
	    Label legendOthersText = new Label();
	    legendOthersText.setStyleName("homepage_summary_legengLabel");
	    
	    Image freeLegendImage = imagePrototype.createImage();
	    freeLegendImage.setUrl("images/legend_freeSpace.png");
	    Label legendFreeText = new Label();
	    legendFreeText.setStyleName("homepage_summary_legengLabel");
		
        legendBackupText.setText(UIContext.Messages.homepageSummaryLegendBackup(Utils.bytes2String(backupSpace)));
        legendOthersText.setText(UIContext.Messages.homepageSummaryLegendOthers(Utils.bytes2String((long)(total - backupSpace - freeSpace))));
        legendFreeText.setText(UIContext.Messages.homepageSummaryLegendFree(Utils.bytes2String(freeSpace)));
		
		lc.add(backupLegendImage);
		lc.add(legendBackupText);	    
		lc.add(othersLegendImage);	    
		lc.add(legendOthersText);	    
		lc.add(freeLegendImage);	    
		lc.add(legendFreeText);
	}
	
	private void addBasic() {
		container.add(basicFS);		
		addPath(basicFS, UIContext.Constants.homepageSummaryDataStorePath(), dataStoreInfoModel.getDataStorePath());
		addChart(basicFS, dataStoreInfoModel.getTotalSize(), dataStoreInfoModel.getDirSize(), dataStoreInfoModel.getFreeSize());
		addPicAndValue(basicFS, dataStoreInfoModel.getTotalSize(), dataStoreInfoModel.getDirSize(), dataStoreInfoModel.getFreeSize());
	}
	
	private void addDedup() {
		container.add(dedupFS);		
		addPath(dedupFS, UIContext.Constants.homepageSummaryDataStoreIndexPath(), dataStoreInfoModel.getIndexPath());
		addChart(dedupFS, dataStoreInfoModel.getIndexTotalSize(), dataStoreInfoModel.getIndexDirSize(), dataStoreInfoModel.getIndexFreeSize());
		addPicAndValue(dedupFS, dataStoreInfoModel.getIndexTotalSize(), dataStoreInfoModel.getIndexDirSize(), dataStoreInfoModel.getIndexFreeSize());
		addPath(dedupFS, UIContext.Constants.homepageSummaryDataStoreDataPath(), dataStoreInfoModel.getDataPath());
		addChart(dedupFS, dataStoreInfoModel.getDataTotalSize(), dataStoreInfoModel.getDataDirSize(), dataStoreInfoModel.getDataFreeSize());
		addPicAndValue(dedupFS, dataStoreInfoModel.getDataTotalSize(), dataStoreInfoModel.getDataDirSize(), dataStoreInfoModel.getDataFreeSize());
		addPath(dedupFS, UIContext.Constants.homepageSummaryDataStoreHashPath(), dataStoreInfoModel.getHashPath());
		addChart(dedupFS, dataStoreInfoModel.getHashTotalSize(), dataStoreInfoModel.getHashDirSize(), dataStoreInfoModel.getHashFreeSize());
		addPicAndValue(dedupFS, dataStoreInfoModel.getHashTotalSize(), dataStoreInfoModel.getHashDirSize(), dataStoreInfoModel.getHashFreeSize());
	}
	
	private void refresh() {
		if (!dataStoreInfoModel.isDedupe()) {
			this.setWidth(700);
			this.setHeight(300);
		}
		else {
			this.setWidth(700);
			this.setHeight(500);
		}	
		
		if (!dataStoreInfoModel.getDataStorePath().equals(""))
            addBasic();

		if (dataStoreInfoModel.isDedupe())
		    addDedup();
		
        container.layout();
	}
}
