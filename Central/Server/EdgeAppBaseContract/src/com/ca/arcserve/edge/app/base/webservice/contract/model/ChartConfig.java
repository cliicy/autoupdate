package com.ca.arcserve.edge.app.base.webservice.contract.model;

public class ChartConfig {
	
	public enum LegendPositoin {
		Right,
		Bottom;
	}
	
	private boolean showValue = false;
	private int scollVisibleCount;
	private String yAxisLabel;
	private String xAxisLabel;
	private boolean showLegend;
	private LegendPositoin legendPosition;
	private Double yAxisMaxValue;
	private Double yAxisMinValue;
	private boolean drawAnchors = true;
	private int anchorAlpha = 100;
	private Boolean formatNumberScale = false;
	private Boolean formatNumber = false;
	private int decimals = 2;
	private boolean connectNullData = true;
	private boolean rotateLabels = true;
	
	private boolean scrollToEnd = true;
	private boolean adjustDiv = true;
	
	private boolean needResetMaxYAxisValue = true;
	private boolean checkRepeatedLabel = false;
	
	private boolean integerYAxisValue = false;
	private int minChartHeight = 200;
	private int scrollVisibleHeight = 200;
	
	private String defaultNumberScale = "";
	private String numberScaleValue = "";
	private String numberScaleUnit = "";
	private boolean plotGradient = true; 
	
	private String caption = "";
	private boolean showPercentageValues = false;
	private boolean showLabels = false;
	private String plotBorderColor="";
	private int startingAngle=0;
	private boolean enableSmartLabel=false;
	private boolean showToolTip=false;
	private boolean defaultAnimation=true;
	
	private int labelStep =1;

	public boolean isPlotGradient() {
		return plotGradient;
	}

	public void setPlotGradient(boolean _plotGradient) {
		this.plotGradient = _plotGradient;
	}

	public boolean isShowValue()
	{
		return showValue;
	}

	public void setShowValue( boolean showValue )
	{
		this.showValue = showValue;
	}

	public boolean isIntegerYAxisValue() {
		return integerYAxisValue;
	}

	public void setIntegerYAxisValue(boolean integerYAxisValue) {
		this.integerYAxisValue = integerYAxisValue;
	}

	public boolean isAdjustDiv() {
		return adjustDiv;
	}

	public void setAdjustDiv(boolean adjustDiv) {
		this.adjustDiv = adjustDiv;
	}

	public boolean isConnectNullData() {
		return connectNullData;
	}

	public void setConnectNullData(boolean connectNullData) {
		this.connectNullData = connectNullData;
	}

	public boolean isScrollToEnd() {
		return scrollToEnd;
	}

	public void setScrollToEnd(boolean scrollToEnd) {
		this.scrollToEnd = scrollToEnd;
	}

	public boolean isCheckRepeatedLabel() {
		return checkRepeatedLabel;
	}

	public void setCheckRepeatedLabel(boolean checkRepeatedLabel) {
		this.checkRepeatedLabel = checkRepeatedLabel;
	}

	public boolean isNeedResetMaxYAxisValue() {
		return needResetMaxYAxisValue;
	}

	public void setNeedResetMaxYAxisValue(boolean needResetMaxYAxisValue) {
		this.needResetMaxYAxisValue = needResetMaxYAxisValue;
	}

	public int getDecimals() {
		return decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	public Boolean getFormatNumber() {
		return formatNumber;
	}

	public void setFormatNumber(Boolean formatNumber) {
		this.formatNumber = formatNumber;
	}

	public Boolean getFormatNumberScale() {
		return formatNumberScale;
	}

	public void setFormatNumberScale(Boolean formatNumberScale) {
		this.formatNumberScale = formatNumberScale;
	}

	public int getAnchorAlpha() {
		return anchorAlpha;
	}

	public void setAnchorAlpha(int anchorAlpha) {
		this.anchorAlpha = anchorAlpha;
	}

	public boolean isDrawAnchors() {
		return drawAnchors;
	}

	public void setDrawAnchors(boolean drawAnchors) {
		this.drawAnchors = drawAnchors;
	}

	public Double getYAxisMaxValue() {
		return yAxisMaxValue;
	}

	public void setYAxisMaxValue(Double yAxisMaxValue) {
		this.yAxisMaxValue = yAxisMaxValue;
	}

	public Double getYAxisMinValue() {
		return yAxisMinValue;
	}

	public void setYAxisMinValue(Double yAxisMinValue) {
		this.yAxisMinValue = yAxisMinValue;
	}

	public LegendPositoin getLegendPosition() {
		return legendPosition;
	}

	public void setLegendPosition(LegendPositoin legendPosition) {
		this.legendPosition = legendPosition;
	}

	public ChartConfig() {
		scollVisibleCount = 30;
		yAxisLabel = "";
		xAxisLabel = "";
		showLegend = false;
	}
	
	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public int getScollVisibleCount() {
		return scollVisibleCount;
	}
	public void setScollVisibleCount(int scollVisibleCount) {
		this.scollVisibleCount = scollVisibleCount;
	}
	public String getyAxisLabel() {
		return yAxisLabel;
	}
	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	public String getxAxisLabel() {
		return xAxisLabel;
	}
	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}
	public boolean isRotateLabels() {
		return rotateLabels;
	}

	public void setRotateLabels(boolean rotateLabels) {
		this.rotateLabels = rotateLabels;
	}

	public int getMinChartHeight() {
		return minChartHeight;
	}
	public void setMinChartHeight(int minChartHeight) {
		this.minChartHeight = minChartHeight;
	}
	
	public int getScrollVisibleHeight() {
		return scrollVisibleHeight;
	}
	public void setScrollVisibleHeight(int scrollVisibleHeight) {
		this.scrollVisibleHeight = scrollVisibleHeight;
	}

	public String getDefaultNumberScale()
	{
		return defaultNumberScale;
	}

	public void setDefaultNumberScale( String defaultNumberScale )
	{
		this.defaultNumberScale = defaultNumberScale;
	}

	public String getNumberScaleValue()
	{
		return numberScaleValue;
	}

	public void setNumberScaleValue( String numberScaleValue )
	{
		this.numberScaleValue = numberScaleValue;
	}

	public String getNumberScaleUnit()
	{
		return numberScaleUnit;
	}

	public void setNumberScaleUnit( String numberScaleUnit )
	{
		this.numberScaleUnit = numberScaleUnit;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public boolean isShowPercentageValues() {
		return showPercentageValues;
	}

	public void setShowPercentageValues(boolean showPercentageValues) {
		this.showPercentageValues = showPercentageValues;
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public String getPlotBorderColor() {
		return plotBorderColor;
	}

	public void setPlotBorderColor(String plotBorderColor) {
		this.plotBorderColor = plotBorderColor;
	}

	public int getStartingAngle() {
		return startingAngle;
	}

	public void setStartingAngle(int startingAngle) {
		this.startingAngle = startingAngle;
	}

	public boolean isEnableSmartLabel() {
		return enableSmartLabel;
	}

	public void setEnableSmartLabel(boolean enableSmartLabel) {
		this.enableSmartLabel = enableSmartLabel;
	}

	public boolean isShowToolTip() {
		return showToolTip;
	}

	public void setShowToolTip(boolean showToolTip) {
		this.showToolTip = showToolTip;
	}

	public boolean isDefaultAnimation() {
		return defaultAnimation;
	}

	public void setDefaultAnimation(boolean defaultAnimation) {
		this.defaultAnimation = defaultAnimation;
	}
	public int getLabelStep() {
		return labelStep;
	}

	public void setLabelStep(int labelStep) {
		this.labelStep = labelStep;
	}
}
