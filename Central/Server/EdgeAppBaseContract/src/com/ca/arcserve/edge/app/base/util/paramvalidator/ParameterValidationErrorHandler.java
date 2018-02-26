package com.ca.arcserve.edge.app.base.util.paramvalidator;

import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.BasicErrorInfo;

public interface ParameterValidationErrorHandler
{
	void onError( ParameterValidationErrorType errorType,
		ParameterInfo paramInfo, BasicErrorInfo errorInfo ) throws Exception;
}
