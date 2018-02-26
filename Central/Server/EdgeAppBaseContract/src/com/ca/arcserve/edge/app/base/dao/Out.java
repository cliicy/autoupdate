package com.ca.arcserve.edge.app.base.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * This mandatory annotation  is used to annotate one Out parameter of stored procedure.<br/>
 * the argument must be one of
 * <ol>
 * 	<li>String[]
 * </li>
 *  	<li>boolean|Boolean[]
 * </li>
 *  	<li>byte|Byte[]
 * </li>
 * 	<li>short|Short[]
 * </li>
 *  	<li>int|Integer[]
 * </li>
 *   	<li>long|Long[]
 * </li>
 *  	<li>float|Float[]
 * </li>
 *  	<li>double|Double[]
 * </li>
 * 	<li>java.util.Date[]
 * </li>
 * </ol> 
 * For {@link #jdbcType()}, please see {@link In}
 *  For example:
 * <pre>
 * 	void listPersons(int pageIndex,int pageSize,@out int[] rowCount,@out(jdbcType=Types.SMALLINT) int[] returnID);
 * </pre>
 * @author gonro07
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Out {
	int jdbcType()  default -100;
}
