package com.ca.arcserve.edge.app.base.util.paramvalidator;

import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.BasicErrorInfo;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.FloatOutOfRangeErrorInfo;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.IntegerOutOfRangeErrorInfo;

public enum ParameterValidationErrorType
{
	NotNull( BasicErrorInfo.class ),
	NotEmpty( BasicErrorInfo.class ),
	IntegerOutOfRange( IntegerOutOfRangeErrorInfo.class ),
	FloatOutOfRange( FloatOutOfRangeErrorInfo.class );
	
	private Class<?> errorInfoType;
	
	ParameterValidationErrorType( Class<?> errorInfoType )
	{
		this.errorInfoType = errorInfoType;
	}
	
	public <T> T castErrorInfo( BasicErrorInfo errorInfo )
	{
		assert errorInfo.getClass() == this.errorInfoType : "";
		return (T) this.errorInfoType.cast( errorInfo );
	}
}
