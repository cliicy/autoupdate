package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import java.util.HashMap;
import java.util.Map;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartModel;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartSeries;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;
import com.ca.arcserve.edge.app.base.webservice.contract.model.DataPoint;
import com.ca.arcserve.edge.app.base.webservice.contract.model.Series;

public class AreaChart extends BarChart {
	public AreaChart(ChartConfig config) {
		super(config);
	}
	
	@Override
	public ChartType getChartType() {
		return ChartType.Area2D;
	}
	
	@Override
	public String getChartAttribute(ChartModel chartModel) {
		String attr = super.getChartAttribute(chartModel);
		attr += " xAxisName='" + this.config.getxAxisLabel() + "'" + 
				" drawanchors='" + (this.config.isDrawAnchors() ? "1" : "0") + "'" +
				" anchorSides='10' anchorRadius='5' anchorBorderThickness='1'" + 
				" anchorAlpha='" + this.config.getAnchorAlpha() + "'" +
				" showCanvasBorder='1' canvasBorderThickness='1' canvasBorderAlpha='50' canvasBorderColor='#aaaaaa'"
				+ " showBorder='0' showAlternateHGridColor='0'"
				+ " showLegend='1' legendBorderThickness='0' legendshadow='0'"
				+" divlineisdashed='1' ";// numDivLines ='4' yAxisValueDecimals = '3'";
//				+ " divLineDashed='1' divLineDashLen='2' divLineDashGap='2'" ;
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

		builder.append("<categories>");
		if (categoryGenerator != null) {
			builder.append(categoryGenerator.genCategoryString());
		} else if (this.config.isCheckRepeatedLabel()) {
			Map<String, Boolean> addedCheckMap = new HashMap<String, Boolean>();
			for (DataPoint dp : series.getSeries(0).getDataPoints()) {
				builder.append("<category");
				if (!addedCheckMap.containsKey((dp.getLabel()))) {
					builder.append(this.addXmlAttribute("label", dp.getLabel()));
					addedCheckMap.put(dp.getLabel(), true);
				}
				builder.append(" />");
			}
		} else {
			for (DataPoint dp : series.getSeries(0).getDataPoints()) {
				builder.append("<category");
				builder.append(this.addXmlAttribute("label", dp.getLabel()));
				builder.append(" />");
			}
		}
		builder.append("</categories>");

		for (int sIndex = 0; sIndex < seriesCount; ++sIndex) {
			Series s = series.getSeries(sIndex);
			builder.append("<dataset");
			builder.append(this.addXmlAttribute("seriesName", s.getName()));

			String color = s.getColor();
			if (color != null) {
				String scolor = s.getColor();
				scolor = scolor.replaceAll("#", "");
				builder.append(this.addXmlAttribute("color", scolor));
				color = scolor;
			}

			builder.append(this.addXmlAttribute("includeInLegend", s.isIncludeInLegend() ? "1" : "0"));

			builder.append(">");

			int dpIndex = 0;
			for (DataPoint dp : s.getDataPoints()) {
				builder.append("<set");

				if (!dp.isNullValue()) {
					Object obj = null;
					if (this.config.isIntegerYAxisValue()) {
						obj = (Integer) (int) dp.getValue();
					} else {
						obj = dp.getValue();
					}
					builder.append(this.addXmlAttribute("value", obj));

					String displayValue = dp.getDisplayValue();
					if (displayValue != null)
						builder.append(this.addXmlAttribute("displayValue", displayValue));

					String tooltip = dp.getTooltip();
					if (tooltip != null && !tooltip.isEmpty()) {
						builder.append(this.addXmlAttribute("toolText", tooltip));
					}

					builder.append(this.addXmlAttribute("anchorBorderColor", color));
					builder.append(this.addXmlAttribute("anchorBgColor", color));
					
					if (dp.isCanBeClicked())
						builder.append(this.addLinkAttribute(sIndex, dpIndex));

					++dpIndex;
				}

				builder.append(" />");
			}

			builder.append("</dataset>");
		}

		return builder.toString();
	}

}
