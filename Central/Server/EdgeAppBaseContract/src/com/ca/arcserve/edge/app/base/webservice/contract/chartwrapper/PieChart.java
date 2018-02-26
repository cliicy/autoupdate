package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartModel;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartSeries;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;
import com.ca.arcserve.edge.app.base.webservice.contract.model.DataPoint;

public class PieChart extends Chart {

	public PieChart(ChartConfig config) {
		super(config);
	}

	@Override
	public ChartType getChartType() {
		return ChartType.Pie;
	}

	@Override
	public String getChartAttribute(ChartModel chartModel) {
		return " startingAngle='120' enableRotation='0'  slicingDistance='15' radius3D='100' showLegend='1' legendBorderThickness='0' legendshadow='0' showLabels='0' showPercentValues='1' showPercentInTooltip='0' showborder='0' showshadow='0'";
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

		DataPoint resetDP = null; 
		/*
		 * issue 111095: Node backup status & vitualization protection status report pie chart is not visible
			it's  not cause by us but by a  very strange fusion chart issue;

   			if one value in chart xml data  set to some  big value ( more than 300 ? such as 699, 700, 2402 ..) and 
		  all of other value are 0; the chart will disappear in javascript mode!!! 
		 * not all value cause this issue, only some value cause, for example 2402 has issue but 2401, 2403 is OK!
		 * seems the algorithm pie chart used to divide the pie to different color partition has issue!

		 */

		//cannot directly sort on old data points; because the click event use this sequence to judge the selected point;
		List<DataPoint> toSort = new ArrayList<DataPoint>( series.getSeries(0).getDataPoints() );
		if( toSort.size() >0 ) {
			Collections.sort( toSort,  new Comparator<DataPoint>(){
				@Override
				public int compare(DataPoint o1, DataPoint o2) {
					if( o1.getValue() <o2.getValue() ) {  ///descend
						return 1;
					}
					else if( o1.getValue() >o2.getValue() ) {
						return -1; 
					}
					return 0;
				}
			});
			if(  toSort.get(0).getValue() > 299 && toSort.size() >1 && toSort.get(1).getValue()==0  ) {
				toSort.get(1).setValue(0.001);
				resetDP = toSort.get(1);
			}
		}

		int dpIndex = 0;
		for (DataPoint dp : series.getSeries(0).getDataPoints()) {
			if (dp == null || dp.isNullValue()) {
				continue;
			}

			builder.append("<set");
//			builder.append(this.addXmlAttribute("displayValue", dp.getLabel()));
			Object obj = null;
			if (this.config.isIntegerYAxisValue() && resetDP != dp ) {
				obj = (Integer) (int) dp.getValue();
			} else {
				obj = dp.getValue();
				
			}
			builder.append(this.addXmlAttribute("value", obj));
			builder.append(this.addXmlAttribute("label", dp.getLabel()));
			if (dp.getTooltip() != null && !dp.getTooltip().isEmpty()) {
				builder.append(this.addXmlAttribute("toolText", dp.getTooltip()));
			}

			String color = dp.getColor();
			if (color != null && !color.isEmpty()) {
				builder.append(this.addXmlAttribute("color", color));
			}

			builder.append(this.addLinkAttribute(0, dpIndex++));

			builder.append(" />");
		}

		return builder.toString();
	}

}
