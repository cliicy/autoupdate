package com.ca.arcserve.edge.app.base.dao.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

public class BeanProcessor {

	/**
	 * Special array value used by <code>mapColumnsToProperties</code> that
	 * indicates there is no bean property that matches a column from a
	 * <code>ResultSet</code>.
	 */
	protected static final int PROPERTY_NOT_FOUND = -1;

	/**
	 * Set a bean's primitive properties to these defaults when SQL NULL is
	 * returned. These are the same as the defaults that ResultSet get* methods
	 * return in the event of a NULL column.
	 */
	private static final Map<Class<?>, Object> Defaults_For_Primitive = new HashMap<Class<?>, Object>();

	static {

		Defaults_For_Primitive.put(Boolean.TYPE, Boolean.FALSE);
		Defaults_For_Primitive.put(Character.TYPE, '\u0000');

		Defaults_For_Primitive.put(Byte.TYPE, (Byte) ((byte) 0));
		Defaults_For_Primitive.put(Short.TYPE, (Short) ((short) 0));
		Defaults_For_Primitive.put(Integer.TYPE, 0);
		Defaults_For_Primitive.put(Long.TYPE, (Long) (0L));
		Defaults_For_Primitive.put(Float.TYPE, (Float) (float) (0));
		Defaults_For_Primitive.put(Double.TYPE, (Double) (double) (0));

	}
	
	public BeanProcessor() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type, List results,StoredProcedure spAnno)
			throws SQLException {
		if (!rs.next()) {
			return results;
		}

		PropertyDescriptor[] props = null;
		{
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(type);
				props = beanInfo.getPropertyDescriptors();
			} catch (IntrospectionException e) {
				throw new SQLException("Bean introspection failed: "
						+ e.getMessage());
			}

		}

		ResultSetMetaData metaData = rs.getMetaData();
		int[] map = this.columnsToPropertiesMap(metaData, props);

		while (true) {
			T bean = this.setupBean(rs, type, props, map, metaData,spAnno.UTC(),spAnno.Derby());
			results.add(bean);
			if (!rs.next())
				break;
		}

		return results;
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> toBeanList(ResultSet rs, Class<T> type, List results)
			throws SQLException {
		return toBeanList(rs,type,results,new StoredProcedure(){

			@Override
			public boolean UTC() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public String name() {
				
				return null;
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean Derby() {
				// TODO Auto-generated method stub
				return true;
			}
			
		});
	}
	private <T> T setupBean(ResultSet rs, Class<T> type,
			PropertyDescriptor[] props, int[] columnToProperty,
			ResultSetMetaData rsmd,boolean UTC,boolean Derby) throws SQLException {

		T bean = null;
		{
			try {
				bean = type.newInstance();

			} catch (Exception e) {
				throw new SQLException("Cannot create " + type.getName() + ": "
						+ e.getMessage());

			}
		}
		for (int i = 1; i < columnToProperty.length; i++) {

			if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
				continue;
			}

			PropertyDescriptor prop = props[columnToProperty[i]];
			Class<?> propType = prop.getPropertyType();
			EncryptSave annotation = prop.getReadMethod().getAnnotation(
					EncryptSave.class);
			Object value = this.getColumnValue(rs, i, propType, rsmd
					.getColumnType(i), annotation != null,UTC,Derby);

			if (propType != null && value == null
					&& (propType.isPrimitive() || propType.isEnum())) {
				value = Defaults_For_Primitive.get(propType);
			}
			// call setter of the property
			{
				Method setter = prop.getWriteMethod();
				if (setter != null)
					try {
						setter.invoke(bean, new Object[] { value });
					} catch (Exception e) {
						if (!(e instanceof SQLException))
							throw new SQLException("Cannot set "
									+ prop.getName() + ": " + e.getMessage());

					}
			}
		}

		return bean;
	}

	private int[] columnsToPropertiesMap(ResultSetMetaData metadata,
			PropertyDescriptor[] props) throws SQLException {

		int cols = metadata.getColumnCount();
		int columnToProperty[] = new int[cols + 1];
		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

		for (int col = 1; col <= cols; col++) {
			String columnName = metadata.getColumnLabel(col);
			if (null == columnName || 0 == columnName.trim().length()) {
				columnName = metadata.getColumnName(col);
			}
			for (int i = 0; i < props.length; i++) {

				if (columnName.equalsIgnoreCase(props[i].getName())) {
					columnToProperty[col] = i;
					break;
				}
			}
		}

		return columnToProperty;
	}

	private Object getColumnValue(ResultSet rs, int index, Class<?> propType,
			int jdbcType, boolean crypt,boolean UTC, boolean Derby) throws SQLException {

		if (!propType.isPrimitive() && rs.getObject(index) == null) {
			return null;
		}

		if (propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
			return (rs.getBoolean(index));

		} else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
			return (rs.getLong(index));

		} else if (propType.equals(Double.TYPE)
				|| propType.equals(Double.class)) {
			return (rs.getDouble(index));

		} else if (propType.equals(Integer.TYPE)
				|| propType.equals(Integer.class)) {
			return (rs.getInt(index));

		} else if (propType.equals(Float.TYPE) || propType.equals(Float.class)) {
			return (rs.getFloat(index));

		} else if (propType.equals(Short.TYPE) || propType.equals(Short.class)) {
			return (rs.getShort(index));

		} else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
			return (rs.getByte(index));

		} else if (propType.isEnum()) {
			int ordinal = (rs.getByte(index));
			int enumSize = propType.getEnumConstants().length;
			if (ordinal >= enumSize || ordinal < 0)
				throw new DaoException("invalid Enum ordinal:" + ordinal
						+ " for " + propType.getName());
			return propType.getEnumConstants()[ordinal];

		} else if (propType.equals(Date.class)) {
			switch (jdbcType) {
			case Types.TIME:
				Time t = null;
				if(Derby)
					t = rs.getTime(index);
				else
					t = rs.getTime(index, DaoUtils.getCalWithoutDST());
				if(UTC)
				return DaoUtils.fromUTC((Date)t);
				else
					return t;

			case Types.TIMESTAMP:
				Timestamp ts = null;
				if(Derby)
					ts = rs.getTimestamp(index);
				else
					ts = rs.getTimestamp(index,DaoUtils.getCalWithoutDST());
				if(UTC)
				return DaoUtils.fromUTC((Date)ts);
				else return ts;
			case Types.DATE:
				java.sql.Date d = null;
				if(Derby)
					d = rs.getDate(index);
				else
					d = rs.getDate(index, DaoUtils.getCalWithoutDST());
				if(UTC)
				return DaoUtils.fromUTC((Date)d);
				else
					return d;
			default:
				return rs.getObject(index);
			}

		} else if (propType.equals(String.class)) {
			String value = rs.getString(index);
			if (crypt && value != null)
				return DaoFactory.getEncrypt().decryptString(value);
			else
				return value;

		} else {
			return rs.getObject(index);
		}

	}

}
