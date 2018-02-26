package com.ca.arcserve.edge.app.base.util.paramvalidator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates that a parameter should not be empty. Empty is not null. For a
 * string, it means the string has no meaningful characters. Strings only
 * consists of spaces maybe treated as an empty string. This depends on the
 * business logic behind the API. For a collection, this means the collection
 * contains no element. Based on the business logic, the meaning for empty
 * can be things other than these two cases.
 * 
 * @author panbo01
 *
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER } )
public @interface NotEmpty
{

}
