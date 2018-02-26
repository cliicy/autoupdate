package com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo;

public class IntegerOutOfRangeErrorInfo extends OutOfRangeErrorInfo<Long>
{
	public IntegerOutOfRangeErrorInfo()
	{
		this.setMinValue( Long.MIN_VALUE );
		this.setMaxValue( Long.MAX_VALUE );
		this.setActualValue( 0L );
	}
}
