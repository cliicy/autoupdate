package com.ca.arcserve.edge.app.base.webservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiubo01
 * It is used for parameter of DAO interface to indicate that it need to convert the wild card.
 * Generally, the '*' will be replaced with '%', and the '?' will be replaced with '_'.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface WildcardConversion {

}
