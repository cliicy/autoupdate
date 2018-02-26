package com.ca.arcserve.edge.app.base.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * This mandatory annotation  is used to annotate one ResultSet output by stored procedure.<br/>
 * For stored procedure that outputs multiple ResultSets, use the same number of List<T> arguments annotated by this annotation in the method.
 * <br/>For example:
 * <pre>
 * void listPersonsMachines(int pageIndex,int pageSize,@Out int[] rowAccount,@ResultSet List&lt;Person&gt; persons,@ResultSet List&lt;Machine&gt; machines);
* </pre>
 * @author gonro07
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ResultSet {
}
