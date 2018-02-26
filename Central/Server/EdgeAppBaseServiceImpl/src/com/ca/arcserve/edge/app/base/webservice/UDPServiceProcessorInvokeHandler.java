package com.ca.arcserve.edge.app.base.webservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.paramvalidator.ParameterInfo;
import com.ca.arcserve.edge.app.base.util.paramvalidator.ParameterValidationErrorHandler;
import com.ca.arcserve.edge.app.base.util.paramvalidator.ParameterValidationErrorType;
import com.ca.arcserve.edge.app.base.util.paramvalidator.ParameterValidator;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.BasicErrorInfo;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.FloatOutOfRangeErrorInfo;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.IntegerOutOfRangeErrorInfo;

public class UDPServiceProcessorInvokeHandler implements InvocationHandler
{
	private UDPServiceImpl serviceImpl;
	private UDPServiceProcessor requestProcessor;
	
	private ParameterValidator parameterValidator = ParameterValidator.createInstance(
		new ParameterValidationErrorHandler()
		{
			@Override
			public void onError( ParameterValidationErrorType errorType,
				ParameterInfo paramInfo, BasicErrorInfo errorInfo ) throws Exception
			{
				EdgeServiceFault edgeServiceFault = null;
				
				switch (errorType)
				{
				case NotNull:
					{
						edgeServiceFault = EdgeServiceFault.getFault(
							EdgeServiceErrorCode.Common_Service_BadParameter_IsNull,
							new Object[] { paramInfo.getParameterIndex(), getClassName( paramInfo.getParameterType() ) },
							generateParameterInfoForLog( paramInfo ) + " is null." );
						break;
					}
					
				case NotEmpty:
					{
						edgeServiceFault = EdgeServiceFault.getFault(
							EdgeServiceErrorCode.Common_Service_BadParameter_IsEmpty,
							new Object[] { paramInfo.getParameterIndex(), getClassName( paramInfo.getParameterType() ) },
							generateParameterInfoForLog( paramInfo ) + " is empty." );
						break;
					}
					
				case IntegerOutOfRange:
					{
						IntegerOutOfRangeErrorInfo specificErrorInfo = errorType.castErrorInfo( errorInfo );
						
						edgeServiceFault = EdgeServiceFault.getFault(
							EdgeServiceErrorCode.Common_Service_BadParameter_OutOfRange,
							new Object[] { paramInfo.getParameterIndex(), getClassName( paramInfo.getParameterType() ),
								specificErrorInfo.getMinValue(), specificErrorInfo.getMaxValue(),
								specificErrorInfo.getActualValue() },
							generateParameterInfoForLog( paramInfo ) + " is out of range. " +
								generateValidRangeString( specificErrorInfo.getMinValue(), specificErrorInfo.getMaxValue() ) +
								" The actual value is " + specificErrorInfo.getActualValue() + "." );
						
						break;
					}
					
				case FloatOutOfRange:
					{
						FloatOutOfRangeErrorInfo specificErrorInfo = errorType.castErrorInfo( errorInfo );
						
						edgeServiceFault = EdgeServiceFault.getFault(
							EdgeServiceErrorCode.Common_Service_BadParameter_OutOfRange,
							new Object[] { paramInfo.getParameterIndex(), getClassName( paramInfo.getParameterType() ),
								specificErrorInfo.getMinValue(), specificErrorInfo.getMaxValue(),
								specificErrorInfo.getActualValue() },
							generateParameterInfoForLog( paramInfo ) + " is out of range. " +
								generateValidRangeString( specificErrorInfo.getMinValue(), specificErrorInfo.getMaxValue() ) +
								" The actual value is " + specificErrorInfo.getActualValue() + "." );
						
						break;
					}
				}
				
				if (edgeServiceFault != null)
					throw UDPServiceFaultUtilities.edgeServiceFault2UdpServiceFault(
						edgeServiceFault );
			}
		}
	);
	
	public UDPServiceProcessorInvokeHandler(
		UDPServiceImpl serviceImpl, UDPServiceProcessor requestProcessor )
	{
		this.serviceImpl = serviceImpl;
		this.requestProcessor = requestProcessor;
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] arguments )
		throws Throwable
	{
		try
		{
			this.parameterValidator.validate( proxy, method, arguments );
			
			this.requestProcessor.setWebServiceContext(
				this.serviceImpl.getWebServiceContext() );
			
			return method.invoke( requestProcessor, arguments );
		}
		catch (InvocationTargetException e)
		{
			throw e.getTargetException();
		}
	}
	
	private String generateParameterInfoForLog( ParameterInfo parameterInfo )
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append( parameterInfo.getMethod().getName() );
		strBuilder.append( ": " );
		strBuilder.append( "parameter" + parameterInfo.getParameterIndex() );
		strBuilder.append( " (" + getClassName( parameterInfo.getParameterType() ) + ")" );
		return strBuilder.toString();
	}
	
	private String generateValidRangeString( long minValue, long maxValue )
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append( "Parameter value " );
		
		if ((minValue == Long.MIN_VALUE) && (maxValue == Long.MAX_VALUE))
			strBuilder.append( "could be any value" );
		
		if (minValue == Long.MIN_VALUE)
		{
			strBuilder.append( "should be less then " + maxValue );
		}
		else if (maxValue == Long.MAX_VALUE)
		{
			strBuilder.append( "should be greater then " + minValue );
		}
		else
		{
			strBuilder.append( "should be in [" + minValue + ", " + maxValue + "]" );
		}
		
		strBuilder.append( "." );
		return strBuilder.toString();
	}
	
	private String generateValidRangeString( double minValue, double maxValue )
	{
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append( "Parameter value " );
		
		if ((minValue == Double.MIN_VALUE) && (maxValue == Double.MAX_VALUE))
			strBuilder.append( "could be any value" );
		
		if (minValue == Double.MIN_VALUE)
		{
			strBuilder.append( "should be less then " + maxValue );
		}
		else if (maxValue == Double.MAX_VALUE)
		{
			strBuilder.append( "should be greater then " + minValue );
		}
		else
		{
			strBuilder.append( "should be in [" + minValue + ", " + maxValue + "]" );
		}
		
		strBuilder.append( "." );
		return strBuilder.toString();
	}
	
	private String getClassName( Class<?> clazz )
	{
		return clazz.getSimpleName();
	}
}
