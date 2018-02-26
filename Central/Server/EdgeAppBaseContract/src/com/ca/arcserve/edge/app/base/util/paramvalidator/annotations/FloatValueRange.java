package com.ca.arcserve.edge.app.base.util.paramvalidator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates that a parameter should not be in the specified range. This is
 * for float-point values, include float, double etc.
 * 
 * @author panbo01
 *
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER } )
public @interface FloatValueRange
{
	double minValue() default Double.MIN_VALUE;
	double maxValue() default Double.MAX_VALUE;
}
