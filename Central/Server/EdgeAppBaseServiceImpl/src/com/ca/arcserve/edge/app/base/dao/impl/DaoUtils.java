package com.ca.arcserve.edge.app.base.dao.impl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.In;

/**
 *
 * @author gonro07
 *
 */

public class DaoUtils {
	public static <T> List<StoredProcedureParameter<?>> getInsFromBean(
			T object, In anno,boolean encrypt, boolean wildcardConversion) {

		List<StoredProcedureParameter<?>> result = new ArrayList<StoredProcedureParameter<?>>();
		if(object == null){
			if(anno==null) throw new  DaoException(	"for null parameter, anno must be non null!");
			result.add(new NullSPParameter(anno.jdbcType()));
			return result;
		}
		if (object.getClass().equals(String.class)) {
			String value = (String) object;
			int jdbcType = StringSPParameter.JDBCTYPE;
			if(anno!=null && anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new StringSPParameter(value,jdbcType,encrypt,wildcardConversion));

		} else if (object.getClass().equals(Long.class)) {
			int jdbcType = LongSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			Long value = (Long) object;
			result.add(new LongSPParameter(value,jdbcType));

		} else if (object.getClass().equals(Integer.class)) {
			Integer value = (Integer) object;
			int jdbcType = IntegerSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new IntegerSPParameter(value,jdbcType));

		} else if (object.getClass().equals(Double.class)) {

			Double value = (Double) object;
			int jdbcType = DoubleSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new DoubleSPParameter(value,jdbcType));

		} else if (object.getClass().equals(Float.class)) {
			Float value = (Float) object;
			int jdbcType = FloatSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new FloatSPParameter(value,jdbcType));

		} else if (object.getClass().equals(Boolean.class)) {
			Boolean value = (Boolean) object;
			int jdbcType = BooleanSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new BooleanSPParameter(value,jdbcType));

		} else if (object.getClass().equals(Byte.class)) {
			Byte value = (Byte) object;
			int jdbcType = ByteSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new ByteSPParameter(value,jdbcType));

		} else if (object.getClass().equals(Short.class)) {
			Short value = (Short) object;
			int jdbcType = ShortSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new ShortSPParameter(value,jdbcType));

		} else if (object instanceof Date) {
			Date value = (Date) object;
			int jdbcType = DateSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			result.add(new DateSPParameter(value,jdbcType));

		} else if (object.getClass().isEnum()) {
			Enum<?> value = (Enum<?>) object;
			int jdbcType = ByteEnumSPParameter.JDBCTYPE;
			if(anno!=null&& anno.jdbcType()!=IJDBCDao.DefaultJdbcType)
				jdbcType = anno.jdbcType();
			byte value2 = 0;
			value2 = (byte)value.ordinal();
			result.add(new ByteEnumSPParameter(value2,jdbcType));
		}else {
			throw new DaoException(	"Illegal parameter type:"+object.getClass().getName());

		}
		return result;
	}


	public static StoredProcedureParameter<?> getOutProcedureParameter(Class<?> clz){
		StoredProcedureParameter<?> resultPP = null;
		if (clz.equals(String.class) ) {
			resultPP=(new StringSPParameter(null));
		} else if (clz.equals(Long.class) || clz.equals(Long.TYPE)) {
			resultPP=(new LongSPParameter(null));

		} else if (clz.equals(Integer.class)|| clz.equals(Integer.TYPE)) {
			resultPP=(new IntegerSPParameter(null));

		} else if (clz.equals(Double.class)|| clz.equals(Double.TYPE)) {
			resultPP=(new DoubleSPParameter(null));

		} else if (clz.equals(Float.class)|| clz.equals(Float.TYPE)) {
			resultPP=(new FloatSPParameter(null));

		} else if (clz.equals(Boolean.class)|| clz.equals(Boolean.TYPE)) {
			resultPP=(new BooleanSPParameter(null));

		} else if (clz.equals(Byte.class)|| clz.equals(Byte.TYPE)) {
			resultPP=(new ByteSPParameter(null));

		} else if (clz.equals(Short.class)|| clz.equals(Short.TYPE)) {
			resultPP=(new ShortSPParameter(null));

		} else if (clz.equals(Date.class)) {
			resultPP=(new DateSPParameter(null));

		} else if(clz.isEnum()){
			resultPP = new ByteEnumSPParameter(null);
		}
		if(resultPP!=null) resultPP.setInPara(false);
		return resultPP;
	}
	/**
	 * Close a <code>Connection</code>, avoid closing if null and hide any
	 * SQLExceptions that occur.
	 *
	 * @param conn
	 *            Connection to close.
	 */
	public static void closeQuietly(Connection conn) {
		try {
			close(conn);
		} catch (SQLException e) {
			// quiet
		}
	}

	/**
	 * Close a <code>Connection</code>, <code>Statement</code> and
	 * <code>ResultSet</code>. Avoid closing if null and hide any SQLExceptions
	 * that occur.
	 *
	 * @param conn
	 *            Connection to close.
	 * @param stmt
	 *            Statement to close.
	 * @param rs
	 *            ResultSet to close.
	 */
	public static void closeQuietly(Connection conn, Statement stmt,
			ResultSet rs) {

		try {
			closeQuietly(rs);
		} finally {
			try {
				closeQuietly(stmt);
			} finally {
				closeQuietly(conn);
			}
		}

	}

	/**
	 * Close a  <code>Statement</code> and
	 * <code>ResultSet</code>. Avoid closing if null and hide any SQLExceptions
	 * that occur.
	 *

	 * @param stmt
	 *            Statement to close.
	 * @param rs
	 *            ResultSet to close.
	 */
	public static void closeQuietly(Statement stmt,
			ResultSet rs) {

		try {
			closeQuietly(rs);
		} finally {
			closeQuietly(stmt);

		}

	}

	/**
	 * Close a <code>ResultSet</code>, avoid closing if null and hide any
	 * SQLExceptions that occur.
	 *
	 * @param rs
	 *            ResultSet to close.
	 */
	private static void closeQuietly(ResultSet rs) {
		try {
			close(rs);
		} catch (SQLException e) {
			// quiet
		}
	}

	/**
	 * Close a <code>Statement</code>, avoid closing if null and hide any
	 * SQLExceptions that occur.
	 *
	 * @param stmt
	 *            Statement to close.
	 */
	private static void closeQuietly(Statement stmt) {
		try {
			close(stmt);
		} catch (SQLException e) {
			// quiet
		}
	}

	/**
	 * Close a <code>Connection</code>, avoid closing if null.
	 *
	 * @param conn
	 *            Connection to close.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public static void close(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}

	/**
	 * Close a <code>ResultSet</code>, avoid closing if null.
	 *
	 * @param rs
	 *            ResultSet to close.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public static void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	/**
	 * Close a <code>Statement</code>, avoid closing if null.
	 *
	 * @param stmt
	 *            Statement to close.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public static void close(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}
	
	private static Calendar edgeCalWithoutDst = null;
	/**
	 * change local Date into UTC date
	 * @param localDate
	 * @return
	 */
	public static Date toUTC(Date localDate){
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(localDate.getTime() - java.util.TimeZone.getDefault().getRawOffset());

		Date utcDate = new Date(gc.getTimeInMillis());
		return utcDate;
	}
	
	public static Date fromUTC(Date utcDate){
		Calendar gc = Calendar.getInstance();
		gc.setTimeInMillis(utcDate.getTime() + java.util.TimeZone.getDefault().getRawOffset());

		Date localDate = new Date(gc.getTimeInMillis());
		return localDate;
	}
	
	public static Calendar getCalWithoutDST(){
		if (edgeCalWithoutDst == null) {
			edgeCalWithoutDst = Calendar.getInstance();
			TimeZone defaultTimeZone = TimeZone.getDefault();
			SimpleTimeZone value = new SimpleTimeZone(defaultTimeZone
					.getRawOffset(), defaultTimeZone.getID(), 0, 0, 0, 0, 0, 0,
					0, 0);
			edgeCalWithoutDst.setTimeZone(value);
		}
		return edgeCalWithoutDst;
	}
	
	public static String convertWildcard(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		
		value = value.replace("%", "\\%").replace("_", "\\_");
		value = value.replace('*', '%').replace('?', '_');
		
		if (!value.endsWith("%")) {
			value = value + "%";
		}
		
		return value;
	}

}
