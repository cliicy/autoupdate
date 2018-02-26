/**
 *
 */
package com.ca.arcserve.edge.app.base.webservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gonro07
 * It is used for a field's gettter  or parameter of Dao to indicate it should be Encrypted before insertion into DB
 * and Dycrypted once retrieved from DB
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.PARAMETER})
public @interface EncryptSave {

}
