package com.ca.arcserve.edge.app.base.util.paramvalidator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates that a parameter should not be in the specified range. This is
 * for integer values, include int, long, Byte, Short etc.
 * 
 * @author panbo01
 *
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER } )
public @interface IntegerValueRange
{
	long minValue() default Long.MIN_VALUE;
	long maxValue() default Long.MAX_VALUE;
}
