package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class StatusPieChartPanel extends LayoutContainer implements IRefreshable{
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
	private boolean isAdobeFlashInstalled = false;
	private static final int chartWidth = 375;
	private static final int chartHighth = 223; 
	private static final String successColor = "199437";
//	private static final String cancelColor = "fcfc00";
	private static final String failureColor = "a00000";
//	private static final String crashColor = "f27c1a";
	private static String flashDownloadUrl = UIContext.externalLinks.getFlashURL();
	
	private static final String chartXMLDataBegin = "<graph  showNames='1' decimalPrecision='0' formatNumberScale='0' pieRadius='90'> ";
	private static final String chartXMLDataEnd = "</graph>";
	private static final String chartXMLDataSet = "<set name='SETNAME' value='COUNT' color='COLOR' />";
	private static final String chartHTML = new StringBuilder()
	.append("<html>")
	.append("  <body bgcolor=\"#ffffff\" >")
	.append("    <OBJECT classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" width=\"").append(chartWidth).append("\" height=\"").append(chartHighth).append("\" id=\"Pie3D\">")
	.append("      <param name=\"movie\" value=\"FusionCharts/FCF_Pie3D.swf\" />")
	.append("      <param name=\"FlashVars\" ")
	.append("             value=\"&dataXML=ChartXMLData&chartWidth=").append(chartWidth).append("&chartHeight=").append(chartHighth).append("\">")
	.append("      <param name=wmode value=transparent>")
	.append("      <param name=\"quality\" value=\"high\" />")
	.append("      <embed src=\"../FusionCharts/FCF_Pie3D.swf\" ")
	.append("             flashVars=\"&dataXML=ChartXMLData&chartWidth=").append(chartWidth).append("&chartHeight=").append(chartHighth).append("\"")
	.append("             quality=\"high\" wmode=transparent width=\"").append(chartWidth).append("\" height=\"").append(chartHighth).append("\" name=\"Pie3D\" ")
	.append("             type=\"application/x-shockwave-flash\" />")
	.append("    </OBJECT>")
	.append("  </body>")
	.append("</html>").toString();
	
//	private String chartHTML = new StringBuilder()
//	.append("<html>")
//	.append("<head><script language=\"JavaScript\" src=\"../FusionCharts/FusionCharts.js\"></script></head>")
//	.append("  <body bgcolor=\"#ffffff\" >")
//	.append("    <div id=\"chartdiv\" align=\"center\"></div>")
//	.append("    <script type=\"text/javascript\">")
//	.append("      var myChart = new FusionCharts(\"../FusionCharts/FCF_pie3D.swf\", \"myChartId\", \"").append(chartWidth).append("\", \"").append(chartHighth).append("\"); ")
//	.append("      myChart.setDataXML(\"").append(chartContent).append("\");")
//	.append("      myChart.render(\"chartdiv\");")
//	.append("    </script>")
//	.append("  </body>")
//	.append("</html>").toString();
	
	private HTML fusionChart;
	private AbstractImagePrototype imagePrototype;
	private LabelField noDataToshow;
	private LoadingStatus status;
	
	native public static String getAdobeFlashMajorVersion()/*-{
		
		try { 
    		try { 
      			var axo = new ActiveXObject('ShockwaveFlash.ShockwaveFlash.6'); 
      			try{ 
          			axo.AllowScriptAccess = 'always'; 
      			}catch(e) {
          			return '6,0,0'; 
      			} 
    		} catch(e) {} 
    		return new ActiveXObject('ShockwaveFlash.ShockwaveFlash').GetVariable('$version').replace(/\D+/g, ',').match(/^,?(.+),?$/)[1]; 
  		} catch(e) { 
    		try { 
      			if(navigator.mimeTypes["application/x-shockwave-flash"].enabledPlugin){ 
        			return (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]).description.replace(/\D+/g, ",").match(/^,?(.+),?$/)[1]; 
      			}
      		} catch(e) {} 
  		} 
  		
  		return '0,0,0';
	}-*/;
	
	public void render(Element target, int index) {
		super.render(target, index);
	    
		final ContentPanel panel = new ContentPanel();
		panel.setHeight(310);
		panel.setHeadingHtml(UIContext.Constants.homepageStatusPieChartHeader());

		RowLayout layout = new RowLayout();
		layout.setOrientation(Orientation.VERTICAL);
		panel.setLayout(layout);
	    status = new LoadingStatus();
	    panel.add(status);
		add(panel);
		try {
			HTML text = getFlashInstallReminder();
			text.setStyleName("FlashInstaller");
			
			if(text != null) {
				panel.add(text);
				// fix 18921497 
				status.hideIndicator();
				return;
			}
		} catch (Exception e) {

		}
	    
	    Text descriptionText = new Text(UIContext.Constants.homepageStatusPieChartDescription());
	    descriptionText.setStyleName("home_chart_description");
	    panel.add(descriptionText);
		
		isAdobeFlashInstalled = true;

		panel.add(getChartColorIndicator());
		
		panel.add(getFusionChart3D());
		
		noDataToshow = new LabelField(UIContext.Constants.homepageStatusPieCharNoDataDisplay());
		RowData data = new RowData();
		Margins margin = new Margins(20, 80, 50, 70);
		data.setMargins(margin);
		panel.add(noDataToshow, data);
		
	    service.getBackupInforamtionSummary(new BaseAsyncCallback<BackupInformationSummaryModel>(){
			@Override
			public void onSuccess(BackupInformationSummaryModel result) {
				status.hideIndicator();
				refresh(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				status.hideIndicator();
				super.onFailure(caught);
			}
		});
	}

	public static HTML getFlashInstallReminder() {
		String flashMajorVersionString = getAdobeFlashMajorVersion();
		int majorVersion = Integer.parseInt(flashMajorVersionString.split(",")[0]);
		HTML text = null;
		if (majorVersion < 10){
			String posiURL = "<a href=\"" + flashDownloadUrl + "\" target=\"_blank\" style=\"home_chart_description\">" + UIContext.Constants.homepageStatusPieChartAdobeFlashHere() + "</a>";
			text = new HTML(UIContext.Constants.homepageStatusPieChartAdobeFlashNotInstalled() + "&nbsp; " 
					               + UIContext.Messages.homepagePieChartInstallFlash(posiURL));
			text.setStyleName("home_chart_description");
		}
		return text;
	}
	
	private Widget getChartColorIndicator() 
	{
	    LayoutContainer container = new LayoutContainer();
	    TableLayout layout = new TableLayout();
	    layout.setColumns(5);
		container.setLayout(layout);
		container.setStyleAttribute("padding", "8px");
		container.setStyleAttribute("padding-top", "6px");
		
		addColorIndicator(container, "images/StatusPieChart_Legend_Finish.png", UIContext.Constants.backupStatusFinished());
		LabelField splitter = new LabelField();
		splitter.setValue("");
		splitter.setWidth(50);
		container.add(splitter);
//		addColorIndicator(container, "images/StatusPieChart_Legend_Cancel.png", UIContext.Constants.backupStatusCanceled());
		addColorIndicator(container, "images/StatusPieChart_Legend_Failed.png", UIContext.Constants.backupStatusFailed());
//		addColorIndicator(container, "images/StatusPieChart_Legend_Crashed.png", UIContext.Constants.backupStatusCrashed());
		
		return container;
	}
	private Widget addColorIndicator(LayoutContainer container, String imageURL,
			String statusName) {
		Image indicImage;
		if(imagePrototype == null)
		{
			imagePrototype = IconHelper.create(imageURL, 16, 16);
			indicImage = imagePrototype.createImage();
		}
		else
		{
			indicImage = imagePrototype.createImage();
			indicImage.setUrl(imageURL);
		}
		TableData data = new TableData();
		data.setPadding(4);
		container.add(indicImage, data);

		LabelField successText = new LabelField();
		successText.setStyleName("home_chart_description");
		successText.setValue(statusName);
		data = new TableData();
		data.setHorizontalAlign(HorizontalAlignment.LEFT);
		container.add(successText, data);

		return container;
	}

	private LayoutContainer getFusionChart3D() {
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new FitLayout());
		container.setStyleAttribute("padding-top", "6px");
		fusionChart = new HTML("<html><body bgcolor=\"#ffffff\"></body></html>", true);
		container.add(fusionChart);
		return container;
	}
	
	@Override
	public void refresh(Object data, int changeSource) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void refresh(Object data) {
		if (!isAdobeFlashInstalled)
			return;
		
		BackupInformationSummaryModel result = (BackupInformationSummaryModel)data;
		String concretData = chartXMLDataBegin;
		boolean hasData = false;
		if (result!=null){
			if(result.getTotalSuccessfulCount() > 0)
			{
				concretData += getXMLDataSet(UIContext.Constants.backupStatusFinished(),
						 result.getTotalSuccessfulCount(), successColor);
				hasData = true;
			}
			if(result.getTotalFailedCount() > 0)
			{
				concretData += getXMLDataSet(UIContext.Constants.backupStatusFailed(),
	                     result.getTotalFailedCount(), failureColor);
				hasData = true;
			}
//			if(result.getTotalCanceledCount() > 0)
//			{
//				concretData += getXMLDataSet(UIContext.Constants.backupStatusCanceled(),
//						result.getTotalCanceledCount(), cancelColor);
//			}
//			if(result.getTotalCrashedCount() > 0)
//			{
//				concretData +=  getXMLDataSet(UIContext.Constants.backupStatusCrashed(),
//			              result.getTotalCrashedCount(), crashColor);
//			}
				
			concretData +=chartXMLDataEnd;	
				
		}
		if(hasData) {
			String chartHTMLString = chartHTML.replaceAll("ChartXMLData", concretData);
			fusionChart.setHTML(chartHTMLString);
			fusionChart.setVisible(true);
			noDataToshow.setVisible(false);
		}
		else
		{
			fusionChart.setVisible(false);
			noDataToshow.setVisible(true);
		}
	}

	private String getXMLDataSet(String statusName,
			Integer count, String color) {
		return chartXMLDataSet.replace("SETNAME", statusName)
                              .replace("COUNT", count+"")
                              .replace("COLOR", color);
	}
}
