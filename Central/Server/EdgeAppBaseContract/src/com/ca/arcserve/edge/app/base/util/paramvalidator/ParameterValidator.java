package com.ca.arcserve.edge.app.base.util.paramvalidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;

import java_cup.internal_error;

import com.ca.arcserve.edge.app.base.util.paramvalidator.annotations.FloatValueRange;
import com.ca.arcserve.edge.app.base.util.paramvalidator.annotations.NotEmpty;
import com.ca.arcserve.edge.app.base.util.paramvalidator.annotations.NotNull;
import com.ca.arcserve.edge.app.base.util.paramvalidator.annotations.IntegerValueRange;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.BasicErrorInfo;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.FloatOutOfRangeErrorInfo;
import com.ca.arcserve.edge.app.base.util.paramvalidator.errorinfo.IntegerOutOfRangeErrorInfo;

public class ParameterValidator
{
	private ParameterValidationErrorHandler validationErrorHandler;
	
	public static ParameterValidator createInstance( ParameterValidationErrorHandler validationErrorHandler )
	{
		ParameterValidator instance = new ParameterValidator();
		instance.validationErrorHandler = validationErrorHandler;
		return instance;
	}
	
	private ParameterValidator()
	{
	}
	
	public void validate( Object object, Method method, Object[] parameters ) throws Exception
	{
		int paramIndex = 0;
		
		Annotation[][] paramAnnotations = method.getParameterAnnotations();
		Class<?>[] paramTypes = method.getParameterTypes();
		
		for (Annotation[] annotations : paramAnnotations)
		{
			for (Annotation annotation : annotations)
			{
				Object parameter = parameters[paramIndex];
				Class<?> paramType = paramTypes[paramIndex];
				
				if (annotation instanceof NotNull)
				{
					if (isObjectNull( parameter ))
					{
						this.validationErrorHandler.onError(
							ParameterValidationErrorType.NotNull,
							new ParameterInfo( method, paramIndex, paramType ),
							new BasicErrorInfo( annotation ) );
					}
				}
				else if (annotation instanceof NotEmpty)
				{
					if (isObjectNull( parameter ))
					{
						this.validationErrorHandler.onError(
							ParameterValidationErrorType.NotNull,
							new ParameterInfo( method, paramIndex, paramType ),
							new BasicErrorInfo( annotation ) );
					}
					
					if (isObjectEmpty( parameter ))
					{
						this.validationErrorHandler.onError(
							ParameterValidationErrorType.NotEmpty,
							new ParameterInfo( method, paramIndex, paramType ),
							new BasicErrorInfo( annotation ) );
					}
				}
				else if (annotation instanceof IntegerValueRange)
				{
					if (!isIntegerType( paramType ))
						continue;
					
					IntegerValueRange valueRange = (IntegerValueRange) annotation;
					long actualValue = getIntegerValue( parameter );
					
					if (isIntegerOutOfRange( valueRange.minValue(), valueRange.maxValue(), actualValue ))
					{
						IntegerOutOfRangeErrorInfo errorInfo = new IntegerOutOfRangeErrorInfo();
						errorInfo.setRuleAnnotation( annotation );
						errorInfo.setMinValue( valueRange.minValue() );
						errorInfo.setMaxValue( valueRange.maxValue() );
						errorInfo.setActualValue( actualValue );
						
						this.validationErrorHandler.onError(
							ParameterValidationErrorType.IntegerOutOfRange,
							new ParameterInfo( method, paramIndex, paramType ),
							errorInfo );
					}
				}
				else if (annotation instanceof FloatValueRange)
				{
					if (!isFloatType( paramType ))
						continue;
					
					FloatValueRange valueRange = (FloatValueRange) annotation;
					double actualValue = getFloatValue( parameter );
					
					if (isFloatOutOfRange( valueRange.minValue(), valueRange.maxValue(), actualValue ))
					{
						FloatOutOfRangeErrorInfo errorInfo = new FloatOutOfRangeErrorInfo();
						errorInfo.setRuleAnnotation( annotation );
						errorInfo.setMinValue( valueRange.minValue() );
						errorInfo.setMaxValue( valueRange.maxValue() );
						errorInfo.setActualValue( actualValue );
						
						this.validationErrorHandler.onError(
							ParameterValidationErrorType.FloatOutOfRange,
							new ParameterInfo( method, paramIndex, paramType ),
							errorInfo );
					}
				}
			}
			
			paramIndex ++;
		}
	}
	
	private boolean isObjectNull( Object object )
	{
		return (object == null);
	}
	
	private boolean isObjectEmpty( Object object )
	{
		assert object != null : "object is null";
		
		if (object == null)
			return false;
		
		if (object instanceof String)
		{
			return ((String) object).trim().isEmpty();
		}
		else if (object instanceof Collection<?>)
		{
			return ((Collection<?>) object).isEmpty();
		}
		else if (object.getClass().isArray())
		{
			return (Array.getLength( object ) == 0);
		}
		return false;
	}
	
	private boolean isIntegerOutOfRange( long minValue, long maxValue, long actualValue )
	{
		return ((actualValue < minValue) || (actualValue > maxValue));
	}
	
	private boolean isFloatOutOfRange( double minValue, double maxValue, double actualValue )
	{
		return ((actualValue < minValue) || (actualValue > maxValue));
	}
	
	private boolean isIntegerType( Class<?> type )
	{
		return ((type == int.class) || (type == Integer.class) ||
			(type == long.class) || (type == Long.class));
	}
	
	private boolean isFloatType( Class<?> type )
	{
		return ((type == float.class) || (type == Float.class) ||
			(type == double.class) || (type == Double.class));
	}
	
	private long getIntegerValue( Object object )
	{
		if (object.getClass() == int.class)
			return new Long( (int) object );
		else if (object.getClass() == Integer.class)
			return ((Integer) object).longValue();
		else if (object.getClass() == long.class)
			return (long) object;
		else if (object.getClass() == Long.class)
			return (Long) object;
		
		return 0;
	}
	
	private double getFloatValue( Object object )
	{
		if (object.getClass() == float.class)
			return new Double( (float) object );
		else if (object.getClass() == Float.class)
			return ((Float) object).doubleValue();
		else if (object.getClass() == double.class)
			return (double) object;
		else if (object.getClass() == Double.class)
			return (Double) object;
		
		return 0;
	}
}
