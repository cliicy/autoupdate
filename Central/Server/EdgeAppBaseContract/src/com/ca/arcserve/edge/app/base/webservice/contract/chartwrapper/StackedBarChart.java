package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;

public class StackedBarChart extends BarChart {

	public StackedBarChart(ChartConfig config) {
		super(config);
	}

	@Override
	public ChartType getChartType() {
		return ChartType.StackedBar;
	}

}
