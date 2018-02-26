package com.ca.arcserve.edge.app.base.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.CallableStatement;
/**
 * This optional annotation is used to annotate one In parameter of stored procedure.<br/>
 * By default, the parameters of an interface Dao are input parameters.
 * <br/>
 * {@link #jdbcType()} attribute  is used to modify the default mapping between one of nine Java Types and JDBC type:
 * 			<ol>
 * 				<li>String<->Types.NVARCHAR</li>
 * 				<li>boolean|Boolean<-->Types.BOOLEAN</li>
 * 				<li>byte|Byte<-->Types.TINYINT</li>  
 * 				<li>short|Short<-->Types.SMALLINT</li>
 * 				<li>int|Integer<-->Types.INTEGER</li>
 * 				<li>long|Long<-->Types.BIGINT</li>
 * 				<li>double|Double<-->Types.DOUBLE</li>  
 * 				<li>float|Float<-->Types.FLOAT</li>
 *				<li>java.util.Date<-->Types.TIMESTAMP</li>
 *				<li>Enum<-->Types.Byte</li>
 * 			</ol>
 * For example:
 * <pre>
 * 	void listPersons(int pageIndex,int(jdbcType=Types.INTEGER) pageSize);
 * </pre>
 * 
 * About Enum support Limitations:
 * <ol>
 * <li>
 * 	The ordinal 0 should not have meaning, because the module will convert NULL db value into 0 according to {@link CallableStatement#getByte(int)} function
 * </li>
 * <li> 
 * We use Byte to support ordinal, so no more than 127 Enum contants are supported in one Enum type
 * </li>
 * </ol>

 * @author gonro07
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface In {
	int jdbcType()  default -100;
}
