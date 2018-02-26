package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartModel;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;

public abstract class Chart {

	protected ChartConfig config;

	private String chartId;

	private ChartModel currentChartModel;
	private String currentChartData;
	private static int globalChartId;
	protected CategoryGenerator categoryGenerator = null;
	private int chartWidth;
	private int chartHeight;

	protected Chart(ChartConfig config) {
		this.config = config == null ? new ChartConfig() : config;
		++globalChartId;
		this.chartId = "chart_" + globalChartId;
	}

	public abstract ChartType getChartType();

	public abstract String getChartAttribute(ChartModel chartModel);

	public abstract String getChartDataXML(ChartModel chartModel);

	public void update(ChartModel chartModel) {
		this.currentChartModel = chartModel;
		StringBuilder builder = new StringBuilder();
		
		String commonChartAttribute = 
			"animation='0' " +
			"showAboutMenuItem='0' " +
			"showPrintMenuItem='0' " +
			"bgColor='#ffffff' " +
			"tooltipbgcolor='#000000' toolTipBgAlpha='70' tooltipcolor='#ffffff'";
		
		String exportAttribute =
			// To fix (20155965) : To support Export chart message
			"showExportDialog='1' "+
			//"exportDialogMessage='" + ComponentDashboard.getUiconstants().chartExportDialogText()  + "' " +
			
			"exportEnabled='1' " +
			"exportShowMenuItem='0' " +
			"exportAtClient='0' " +
			"exportAction='save' " +
			"exportFileName='DashboardChart' " +
		//	"exportHandler='" + ChartManager.getExportHandler() + "' " +
			"exportCallback='onChartExportedStub'";
		
		builder.append("<chart " + commonChartAttribute + " " + exportAttribute);

		String miscChartAttribute = this.getChartAttribute(chartModel);
		if (miscChartAttribute != null) {
			builder.append(" " + miscChartAttribute);
		}

		builder.append(">");

		String chartData = this.getChartDataXML(chartModel);
		if (chartData != null) {
			builder.append(chartData);
		}

		builder.append("</chart>");
		this.currentChartData = builder.toString();

	}

	protected String addXmlAttribute(String name, Object value) {
		return " " + name + "='" + value + "'";
	}

	protected String addLinkAttribute(int seriesIndex, int dataPointIndex) {
		return " link=\"javascript:onChartClickedStub('" + this.chartId + "'," + seriesIndex + "," + dataPointIndex + ")\"";
	}

	public String getChartData() {
		return this.currentChartData;
	}

	public ChartModel getCurrentChartModel() {
		return this.currentChartModel;
	}

	public ChartConfig getConfig() {
		return this.config;
	}

	public CategoryGenerator getCategoryGenerator() {
		return categoryGenerator;
	}

	public void setCategoryGenerator(CategoryGenerator categoryGenerator) {
		this.categoryGenerator = categoryGenerator;
	}

	public int getChartWidth() {
		return chartWidth;
	}

	public void setChartWidth(int chartWidth) {
		this.chartWidth = chartWidth;
	}

	public int getChartHeight() {
		return chartHeight;
	}

	public void setChartHeight(int chartHeight) {
		this.chartHeight = chartHeight;
	}

}