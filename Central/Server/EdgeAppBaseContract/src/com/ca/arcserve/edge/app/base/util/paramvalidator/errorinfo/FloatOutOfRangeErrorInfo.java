package com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo;

public class FloatOutOfRangeErrorInfo extends OutOfRangeErrorInfo<Double>
{
	public FloatOutOfRangeErrorInfo()
	{
		this.setMinValue( Double.MIN_VALUE );
		this.setMaxValue( Double.MAX_VALUE );
		this.setActualValue( 0.0 );
	}
}
