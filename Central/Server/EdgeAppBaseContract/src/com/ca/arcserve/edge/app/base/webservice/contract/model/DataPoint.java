package com.ca.arcserve.edge.app.base.webservice.contract.model;

public class DataPoint {
	
	private static final int DefaultAnchorAlpha = 50;
	private static final int DefaultBorderThickness = 1;
	
	private boolean showLabel;
	private boolean showValue;
	private String label;
	private boolean nullValue;
	private double value;
	private String tooltip;
	private String color;
	private boolean dashed;
	private int anchorAlpha = DefaultAnchorAlpha;
	private int anchorBorderThickness = DefaultBorderThickness;
	private boolean canBeClicked = true;
	private String displayValue = null;
	
	public boolean isCanBeClicked() {
		return canBeClicked;
	}

	public void setCanBeClicked(boolean canBeClicked) {
		this.canBeClicked = canBeClicked;
	}

	public void setToHightlightValues() {
		this.anchorAlpha = 100;
		this.anchorBorderThickness = 6;
	}
	
	public void setToNormalValues() {
		this.anchorAlpha = DefaultAnchorAlpha;
		this.anchorBorderThickness = DefaultBorderThickness;
	}
	
	public void setToNormalHide() {
		this.anchorAlpha = 0;
		this.anchorBorderThickness = DefaultBorderThickness;
	}
	public void setToNormalShow() {
		this.anchorAlpha = 100;
		this.anchorBorderThickness = DefaultBorderThickness;
	}
	public void setToHighlightShow() {
		this.anchorAlpha = 100;
		this.anchorBorderThickness = 2;
	}
	
	public int getAnchorAlpha() {
		return anchorAlpha;
	}
	public void setAnchorAlpha(int anchorAlpha) {
		this.anchorAlpha = anchorAlpha;
	}
	public int getAnchorBorderThickness() {
		return anchorBorderThickness;
	}
	public void setAnchorBorderThickness(int anchorBorderThickness) {
		this.anchorBorderThickness = anchorBorderThickness;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}
	public boolean isShowLabel() {
		return showLabel;
	}
	public boolean isShowValue() {
		return showValue;
	}
	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean isNullValue() {
		return nullValue;
	}
	public void setNullValue(boolean nullValue) {
		this.nullValue = nullValue;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public void setDashed(boolean dashed) {
		this.dashed = dashed;
	}
	public boolean isDashed() {
		return dashed;
	}

	public String getDisplayValue()
	{
		return displayValue;
	}

	public void setDisplayValue( String displayValue )
	{
		this.displayValue = displayValue;
	}
	
	

}
