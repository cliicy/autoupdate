package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartModel;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartSeries;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;
import com.ca.arcserve.edge.app.base.webservice.contract.model.DataPoint;
import com.ca.arcserve.edge.app.base.webservice.contract.model.Series;

public class ScatterLineChart extends Chart {

	public ScatterLineChart(ChartConfig config) {
		super(config);
	}

	@Override
	public ChartType getChartType() {
		return ChartType.ScatterLine;
	}

	@Override
	public String getChartAttribute(ChartModel chartModel) {
		int width = this.config.getScollVisibleCount();
		//temp solution for  113643: 10784_JPN_IO: truncation: Date is truncated in Backup Size Trend Report
		boolean onlyOneNode = false;
		if (chartModel != null && chartModel instanceof ChartSeries) {
			ChartSeries series  = (ChartSeries) chartModel;
			int maxCount = -1;
			if( series != null  ) {
				for (int i = 0; i < series.getSeriesCount(); ++i) {
					int count = series.getSeries(i).getDataPoints().size();
					if (maxCount == -1 || maxCount < count) {
						maxCount = count;
					}
				}
				onlyOneNode = maxCount<=1 ? true : false;
			}
		}
	
		
		String attr = "showValues='0' " + "rotateLabels=" + (this.config.isRotateLabels() && !onlyOneNode ? "'1' " : "'0' ")
				+ "connectNullData='" + (this.config.isConnectNullData() ? "1" : "0") + "' " + "numVisiblePlot='"
				+ width + "' " + "yAxisName='" + this.config.getyAxisLabel() + "' " + "showLegend='"
				+ (this.config.isShowLegend() ? "1" : "0") + "' formatNumber='"
				+ (this.config.getFormatNumber() ? "1" : "0") + "' formatNumberScale='"
				+ (this.config.getFormatNumberScale() ? "1" : "0") + "' "
				
				+ ( config.getDefaultNumberScale().isEmpty()? "" : "defaultNumberScale=' "+config.getDefaultNumberScale() +"' " )
				+ ( config.getNumberScaleValue().isEmpty()? "" : "numberScaleValue=' "+config.getNumberScaleValue() +"' " ) 
				+ ( config.getNumberScaleUnit().isEmpty()? "" : "numberScaleUnit=' "+config.getNumberScaleUnit() +"' " )
				+  " labelStep='"+ Integer.toString( config.getLabelStep() ) +"' " 
				;

		if (this.config.isShowLegend()) {
			attr = attr + " legendPosition='"
					+ (this.config.getLegendPosition() == ChartConfig.LegendPositoin.Right ? "RIGHT" : "BOTTOM") + "'";
		}

		if (config.getYAxisMinValue() != null && config.getYAxisMaxValue() != null) {
			String sval = this.config.getYAxisMaxValue().toString();
			if (this.config.isNeedResetMaxYAxisValue()) {
				int index = sval.indexOf(".");
				if (index != -1) {
					sval = Long.toString((Long.parseLong(sval.substring(0, index)) + 1));
				}
			}

			String smin = "";
			if (this.config.getYAxisMinValue() < 0) {
				smin = Double.toString(Math.abs(this.config.getYAxisMinValue()));

				int index = smin.indexOf(".");
				if (index != -1) {
					smin = Long.toString((Long.parseLong(smin.substring(0, index)) + 1));
				}
			} else
				smin = this.config.getYAxisMinValue().toString();

			attr = attr + " yAxisMinValue='" + (this.config.getYAxisMinValue() < 0 ? "-" + smin : smin)
					+ "' yAxisMaxValue='" + sval + "'";
		}
		
		attr = attr + " decimals='" + Integer.toString(this.config.getDecimals()) + "'";

		attr = attr + " drawAnchors='" + (this.config.isDrawAnchors() ? "1" : "0") + "' anchorAlpha='"
				+ this.config.getAnchorAlpha() + "'";

		attr = attr + " lineDashGap='10'";

		attr += " scrollToEnd='" + (this.config.isScrollToEnd() ? "1'" : "0'");

		return attr;
	}

	@Override
	public String getChartDataXML(ChartModel chartModel) {
		ChartSeries series = null;
		if (chartModel != null && chartModel instanceof ChartSeries) {
			series = (ChartSeries) chartModel;
		}
		if (series == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();

		int seriesCount = series.getSeriesCount();
		if (seriesCount == 0) {
			return builder.toString();
		}

		int maxCount = -1;
		int maxCountIndex = 0;
		for (int i = 0; i < seriesCount; ++i) {
			int count = series.getSeries(i).getDataPoints().size();
			if (maxCount == -1 || maxCount < count) {
				maxCount = count;
				maxCountIndex = i;
			}
		}

		builder.append("<categories>");
		for (DataPoint dp : series.getSeries(maxCountIndex).getDataPoints()) {
			builder.append("<category");
			builder.append(this.addXmlAttribute("label", dp.getLabel()));

			if (!dp.isShowLabel()) {
				builder.append(this.addXmlAttribute("showLabel", 0));
			}
			else {
				builder.append(this.addXmlAttribute("showLabel", 1));
			}

			builder.append(" />");
		}
		builder.append("</categories>");

		for (int sIndex = 0; sIndex < seriesCount; ++sIndex) {
			Series s = series.getSeries(sIndex);
			builder.append("<dataset");
			builder.append(this.addXmlAttribute("seriesName", s.getName()));

			String color = s.getColor();
			if (color != null) {
				builder.append(this.addXmlAttribute("color", s.getColor()));
			}

			int lineThickness = s.getLineThickness();
			if (lineThickness > 0) {
				builder.append(this.addXmlAttribute("lineThickness", lineThickness));
			}

			if (!s.isDrawAnchors()) {
				builder.append(this.addXmlAttribute("drawAnchors", "0"));
			}

			builder.append(this.addXmlAttribute("includeInLegend", s.isIncludeInLegend() ? "1" : "0"));

			builder.append(">");

			int dpIndex = 0;
			for (DataPoint dp : s.getDataPoints()) {
				builder.append("<set");

				if (!dp.isNullValue()) {
					builder.append(this.addXmlAttribute("value", dp.getValue()));

					String tooltip = dp.getTooltip();
					if (tooltip != null && !tooltip.isEmpty()) {
						builder.append(this.addXmlAttribute("toolText", tooltip));
					}

					if (dp.isDashed()) {
						builder.append(this.addXmlAttribute("dashed", 1));
					}

					builder.append(this.addXmlAttribute("anchorAlpha", dp.getAnchorAlpha()));
					builder.append(this.addXmlAttribute("anchorBorderThickness", dp.getAnchorBorderThickness()));

					if (dp.isCanBeClicked())
						builder.append(this.addLinkAttribute(sIndex, dpIndex++));
				}

				builder.append(" />");
			}

			builder.append("</dataset>");
		}

		return builder.toString();
	}

}
