package com.ca.arcserve.edge.app.base.util.paramvalidator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates that a parameter should not be null.
 * 
 * @author panbo01
 *
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER } )
public @interface NotNull
{

}
