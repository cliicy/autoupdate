package com.ca.arcserve.edge.app.base.webservice.contract.chartwrapper;

import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.model.ChartType;

public class ChartFactory {

	private static ChartFactory instance = new ChartFactory();

	private ChartFactory() {
	}

	public static ChartFactory getInstance() {
		return instance;
	}

	public Chart create(ChartType type) {
		return this.create(type, new ChartConfig());
	}

	public Chart create(ChartType type, ChartConfig config) {
		if (config == null) {
			return create(type);
		}
		switch (type) {
		case Pie:
			return new PieChart(config);
		case Bar:
			return new BarChart(config);
		case StackedBar:
			return new StackedBarChart(config);
		case ScatterLine:
			return new ScatterLineChart(config);
		case Doughnut:
			return new DoughnutChart(config);
		case Area2D:
			return new AreaChart(config);
		case Combi2D:
			return new CombiChart(config);
		default:
			return null;
		}
	}
}
