package com.ca.arcflash.ui.client.common;


import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.Label;
import com.google.gwt.user.client.ui.Image;



public class LoadingStatus extends ContentPanel{ 
 
	public static final String ICON_LOADING = "images/gxt/icons/grid-loading.gif";
	private Image image = new Image(ICON_LOADING);// IconHelper.create(ICON_LOADING,
	private Label indicatorMsgLabel;
	public void addto(LayoutContainer con,int colSpan){
		TableData statusData = new TableData();
		statusData.setColspan(colSpan);
		statusData.setVerticalAlign(VerticalAlignment.MIDDLE);
		statusData.setHorizontalAlign(HorizontalAlignment.LEFT);
		statusData.setWidth("100%");
		this.setWidth("100%");
		con.add(this,statusData);
	}
    public LoadingStatus() { 
	    this.setBodyBorder(false);
	    this.setHeaderVisible(false);
		TableLayout layout2 = new TableLayout();
		layout2.setWidth("100%");
		layout2.setColumns(2);
		this.setLayout(layout2);
	    
	    TableData dataImage = new TableData();
	    dataImage.setWidth("0");
	    dataImage.setHorizontalAlign(HorizontalAlignment.RIGHT);
	    this.add(image,dataImage);
	    
	    TableData dataLabel = new TableData();
	    dataLabel.setWidth("100%");
	    dataLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
	    indicatorMsgLabel = new Label(UIContext.Constants.loadingIndicatorText());
	    indicatorMsgLabel.addStyleName("homepage_summary_loading_label");
	    indicatorMsgLabel.setWidth("100%");
	    this.add(indicatorMsgLabel,dataLabel); 
    } 

    public void setLoadingMsg(String msg) {
    	indicatorMsgLabel.setHtml(msg);
    }
    
    public void setMsgLabelStyleName(String styleName) {
    	indicatorMsgLabel.setStyleName(styleName);
    }

    public void showIndicator() { 
        this.setVisible(true); 
    } 


    public void hideIndicator() { 
    	 this.setVisible(false); 
    } 

    public Label getLoadingLabelField()
    {
    	return indicatorMsgLabel;
    }
} 