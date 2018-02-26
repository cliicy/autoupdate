package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartModel;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartSeries;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;
import com.ca.arcserve.edge.app.base.webservice.contract.model.DataPoint;

public class DoughnutChart extends Chart {

	protected DoughnutChart(ChartConfig config) {
		super(config);
	}

	@Override
	public ChartType getChartType() {
		return ChartType.Doughnut;
	}
	
	@Override
	public String getChartAttribute(ChartModel chartModel) {
		String attr = "caption='"+this.config.getCaption()+"'"
				+" startingAngle='"+this.config.getStartingAngle()+"'"
				+" enableSmartLabel="+(this.config.isEnableSmartLabel()?"'1'":"'0'")
				+" showToolTip="+(this.config.isShowToolTip()?"'1'":"'0'")
				+" defaultAnimation="+(this.config.isDefaultAnimation()?"'1'":"'0'")
				+" showPercentageValues="+(this.config.isShowPercentageValues()?"'1'":"'0'")
				+" showValues="+(this.config.isShowValue()?"'1'":"'0'")
				+" plotBorderColor='"+this.config.getPlotBorderColor()+"'"
				+" showLegend="+(this.config.isShowLegend()?"'1'":"'0'");
		attr += "  enableRotation=\"0\"  slicingDistance=\"0\"  radius3D=\"100\" ";
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

		for (DataPoint dp : series.getSeries(0).getDataPoints()) {
			if (dp == null || dp.isNullValue()) {
				continue;
			}

			builder.append("<set");

			Object obj = null;
			if (this.config.isIntegerYAxisValue()) {
				obj = (Integer) (int) dp.getValue();
			} else {
				obj = dp.getValue();
			}
			builder.append(this.addXmlAttribute("value", obj));
			
			String label = dp.getLabel();
			if(label != null && !label.isEmpty()){
				builder.append(this.addXmlAttribute("label", label));
			}
			
			String color = dp.getColor();
			if (color != null && !color.isEmpty()) {
				builder.append(this.addXmlAttribute("color", color));
			}
			
			int alpha = dp.getAnchorAlpha();
			builder.append(this.addXmlAttribute("alpha", alpha));
			
			int showLabel = dp.isShowLabel()?1:0;
			builder.append(this.addXmlAttribute("showLable", showLabel));
			
			int showValue = dp.isShowValue()?1:0;
			builder.append(this.addXmlAttribute("showValue", showValue));
			
			builder.append(" />");
		}

		return builder.toString();
	}

}
