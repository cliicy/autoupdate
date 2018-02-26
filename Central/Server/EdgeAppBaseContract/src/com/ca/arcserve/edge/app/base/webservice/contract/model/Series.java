package com.ca.arcserve.edge.app.base.webservice.contract.model;

import java.util.List;

public class Series {
	
	private static final int DefaultThickness = 2;
	private static final int HighlightThickness = 6;
	public static String AREA = "Area";
	public static String TRENDLINES = "TrendLines";
	private String name;
	private String color;
	private int lineThickness = DefaultThickness;
	private List<DataPoint> dataPoints;
	private boolean drawAnchors = true;
	private boolean includeInLegend = true;
	private String renderAs = "";
	
	public boolean isIncludeInLegend() {
		return includeInLegend;
	}
	public void setIncludeInLegend(boolean includeInLegend) {
		this.includeInLegend = includeInLegend;
	}
	public boolean isDrawAnchors() {
		return drawAnchors;
	}
	public void setDrawAnchors(boolean drawAnchors) {
		this.drawAnchors = drawAnchors;
	}
	public int getLineThickness() {
		return lineThickness;
	}
	public void setLineThickness(int lineThickness) {
		this.lineThickness = lineThickness;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	public String getRenderAs() {
		return renderAs;
	}
	public void setRenderAs(String renderAs) {
		this.renderAs = renderAs;
	}
	
	public List<DataPoint> getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(List<DataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}

	public void resetThickness() {
		this.lineThickness = DefaultThickness;
	}
	public void setToHighlightThickness() {
		this.lineThickness = HighlightThickness;
	}

	public void setToThin() {
		this.lineThickness = 1;
	}
	public void setToThick() {
		this.lineThickness = 2;
	}

	public int getNumDataPoints()
	{
		int num = 0;
		for( DataPoint dp : dataPoints)
		{
			if(dp.isNullValue()== false)
			{
				num++;
			}
		}
		return num; 
	}
}
