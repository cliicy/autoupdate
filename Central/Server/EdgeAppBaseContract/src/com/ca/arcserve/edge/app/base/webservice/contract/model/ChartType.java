package com.ca.arcserve.edge.app.base.webservice.contract.model;

public enum ChartType {
	
	None(0x0000, ""),
	Pie(0x0001, "FusionCharts/Charts/Pie2D.swf"),
	Bar(0x0002, "FusionCharts/Charts/ScrollColumn2D.swf"),
	StackedBar(0x0003, "FusionCharts/Charts/ScrollStackedColumn2D.swf"),
	ScatterLine(0x0004, "FusionCharts/Charts/ScrollLine2D.swf"),
	Doughnut(0x006,"FusionCharts/Charts/Doughnut2D.swf"),
	Area2D(0x0007,"FusionCharts/Charts/MSArea.swf"),
	Combi2D(0x0008,"FusionCharts/Charts/MSArea.swf"),
	AllChart(0x1000, "");
	
	private int value;
	private String swfPath;
	
	private ChartType(int value, String swfPath) {
		this.value = value;
		this.swfPath = swfPath;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getSwfPath() {
		return this.swfPath;
	}
	
	public static ChartType parseInt(int val) {
		switch(val) {
		case 0x0000: return None;
		case 0x0001: return Pie;
		case 0x0002: return Bar;
		case 0x0003: return StackedBar;
		case 0x0004: return ScatterLine;
		case 0x0006: return Doughnut;
		case 0x0007: return Area2D;
		case 0x0008: return Combi2D;
		case 0x1000: return AllChart;
		default: return null;
		}
	}
	
}
