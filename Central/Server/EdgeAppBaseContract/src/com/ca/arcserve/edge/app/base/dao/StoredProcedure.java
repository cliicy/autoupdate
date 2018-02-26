package com.ca.arcserve.edge.app.base.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * This mandatory annotation  is used to annotate one method of an interface Dao is to call a given stored procedure.<br/>
 * The {@link #name()} attribute is optional to specify the stored procedure's name. 
 * If this attribute is missing, the to-be-called stored procedure will have the same name as the method.
 * @author gonro07
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface  StoredProcedure {
	String name() default "";
	boolean UTC() default true;
	boolean Derby() default false;
}
