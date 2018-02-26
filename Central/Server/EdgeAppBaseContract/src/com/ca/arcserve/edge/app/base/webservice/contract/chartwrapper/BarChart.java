package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import java.util.HashMap;
import java.util.Map;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartModel;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartSeries;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;
import com.ca.arcserve.edge.app.base.webservice.contract.model.DataPoint;
import com.ca.arcserve.edge.app.base.webservice.contract.model.Series;

public class BarChart extends Chart {

	public BarChart(ChartConfig config) {
		super(config);
	}

	@Override
	public ChartType getChartType() {
		return ChartType.Bar;
	}

	@Override
	public String getChartAttribute(ChartModel chartModel) {
		String attr =
				"showValues='" + (this.config.isShowValue() ? "1" : "0") + "' " +
				"rotateLabels=" + (this.config.isRotateLabels() ? "'1' " : "'0' ") +
				"numVisiblePlot='" + this.config.getScollVisibleCount() + 
				"' yAxisName='" + this.config.getyAxisLabel() +
				"' formatNumber='" + (this.config.getFormatNumber() ? "1" : "0") + 
				"' formatNumberScale='" + (this.config.getFormatNumberScale() ? "1" : "0") +
				"' adjustDiv='" + (this.config.isAdjustDiv() ? "1" : "0") + "'" +
				(this.config.getDefaultNumberScale().isEmpty() ? "" : " defaultNumberScale='" + this.config.getDefaultNumberScale() + "'") +
				(this.config.getNumberScaleValue().isEmpty() ? "" : " numberScaleValue='" + this.config.getNumberScaleValue() + "'") +
				(this.config.getNumberScaleUnit().isEmpty() ? "" : " numberScaleUnit='" + this.config.getNumberScaleUnit() + "'") +
				( this.config.isPlotGradient()? " plotgradientcolor=\"\"  ": "" )   ;
			
			if(config.getYAxisMinValue() != null && config.getYAxisMaxValue() != null) {
				String sval = this.config.getYAxisMaxValue().toString();
				if(this.config.isNeedResetMaxYAxisValue()) {
					int index = sval.indexOf(".");
					if(index != -1) {
						sval = Long.toString((Long.parseLong(sval.substring(0, index))));
					}
				}
				
				String minVal = this.config.getYAxisMinValue().toString();
				if(this.config.isIntegerYAxisValue()) {
					int index = minVal.indexOf(".");
					if(index != -1) {
						minVal = Long.toString((Long.parseLong(minVal.substring(0, index))));
					}
				}
				attr = attr + " yAxisMinValue='" + minVal + "' yAxisMaxValue='" +
					sval + "' ";
			}
			
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
