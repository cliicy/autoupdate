package com.ca.arcserve.edge.app.base.dao.impl;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;



import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.ITransactionDao;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

class SimpleJDBCDao implements IJDBCDao, ITransactionDao {
	static ThreadLocal<Connection> connection = new ThreadLocal<Connection>();

	SimpleJDBCDao() {
	};

	protected DataSource ds = null;

	@Override
	public void setDataSource(DataSource dataSource) {
		this.ds = dataSource;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(String storeProcedureName,
			List<StoredProcedureParameter<?>> params, List<List<?>> reses,
			List<Class<?>> resesClz,StoredProcedure spAnno) {
		DataSource dataSource = this.ds;
		Connection conn = null;
		CallableStatement prepareCall = null;
		ResultSet resultSet = null;
		try {
			conn = connection.get();
			if (conn == null) {
				conn = dataSource.getConnection();
				conn.setAutoCommit(true);
			}
			prepareCall = conn.prepareCall(storeProcedureName);
			int index = 1;
			for (StoredProcedureParameter pp : params) {
				if (pp.isInPara()) {
					setIn(prepareCall, index, pp.getValue(), pp.getJdbcType());

				} else {
					//if (pp.getScale() == -1)
					{
						prepareCall.registerOutParameter(index, pp
								.getJdbcType());
					}
					/*else {
						prepareCall.registerOutParameter(index, pp
								.getJdbcType(), pp.getScale());
					}*/
				}
				index++;
			}

			int resultList = 0;
			prepareCall.execute();
			resultSet = prepareCall.getResultSet();
			while (resultSet == null
					&& !((prepareCall.getMoreResults() == false) && (prepareCall
							.getUpdateCount() == -1)))
				resultSet = prepareCall.getResultSet();
			int resultsize = resesClz.size();
			BeanProcessor pro = DaoFactory.getBeanProcessor();
			while (resultSet != null && resultList < resultsize) {
				// pro.toBeanList(rs, type)
				List<?> list = reses.get(resultList);
				pro.toBeanList(resultSet, resesClz.get(resultList), list,spAnno);
				resultList++;
				resultSet = null;
				while (resultSet == null
						&& !((prepareCall.getMoreResults() == false) && (prepareCall
								.getUpdateCount() == -1)))
					resultSet = prepareCall.getResultSet();
			}

			index = 1;
			for (StoredProcedureParameter pp : params) {
				if (pp.isInPara()) {
					index++;
					continue;
				} else {
					pp.setValue(getValue(prepareCall, index, pp.getJdbcType()));
					index++;
				}
			}

			return;

		} catch (SQLException se) {
			throw new DaoException(se.getMessage(), se);
		} finally {
			if (null != connection.get())
				DaoUtils.closeQuietly(prepareCall, resultSet);
			else
				DaoUtils.closeQuietly(conn, prepareCall, resultSet);
		}
	}

	protected void setIn(CallableStatement call, int index, Object value,
			int jdbcType) throws SQLException {
		switch (jdbcType) {
		case Types.BIT:
			if (value == null)
				call.setNull(index, Types.BIT);
			else
				call.setBoolean(index, (Boolean) value);
			break;
		case Types.BOOLEAN:
			if (value == null)
				call.setNull(index, Types.BOOLEAN);
			else
				call.setBoolean(index, (Boolean) value);
			break;
		case Types.TINYINT:
			if (value == null)
				call.setNull(index, Types.TINYINT);
			else
				call.setByte(index, (Byte) value);
			break;
		case Types.SMALLINT:
			if (value == null)
				call.setNull(index, Types.SMALLINT);
			else
				call.setShort(index, (Short) value);
			break;
		case Types.INTEGER:
			if (value == null)
				call.setNull(index, Types.INTEGER);
			else
				call.setInt(index, (Integer) value);
			break;
		case Types.BIGINT:
			if (value == null)
				call.setNull(index, Types.BIGINT);
			else
				call.setLong(index, (Long) value);
			break;
		case Types.FLOAT:
			if (value == null)
				call.setNull(index, Types.FLOAT);
			else
				call.setFloat(index, (Float) value);
			break;
		case Types.DOUBLE:
			if (value == null)
				call.setNull(index, Types.DOUBLE);
			else
				call.setDouble(index, (Double) value);
			break;
		case Types.VARCHAR:
			if (value == null)
				call.setNull(index, Types.VARCHAR);
			else
				call.setString(index, (String) value);
			break;
		case Types.CHAR:
			if (value == null)
				call.setNull(index, Types.CHAR);
			else
				call.setString(index, (String) value);
			break;
		case Types.NVARCHAR:
			if (value == null)
				call.setNull(index, Types.NVARCHAR);
			else
				call.setNString(index, (String) value);
			break;
		case Types.NCHAR:
			if (value == null)
				call.setNull(index, Types.NCHAR);
			else
				call.setNString(index, (String) value);
			break;

		case Types.DATE:
			if (value == null)
				call.setNull(index, Types.DATE);
			else {
				//value = EdgeCommonUtil.toUTC((java.util.Date) value);
				java.sql.Date d = new java.sql.Date(((java.util.Date) value)
						.getTime());
				call.setDate(index, d, DaoUtils.getCalWithoutDST());
			}
			break;
		case Types.TIME:
			if (value == null)
				call.setNull(index, Types.TIME);
			else {
				//value = EdgeCommonUtil.toUTC((java.util.Date) value);
				java.sql.Time time = new java.sql.Time(((java.util.Date) value)
						.getTime());
				call.setTime(index, time, DaoUtils.getCalWithoutDST());
			}
			break;
		case Types.TIMESTAMP:
			if (value == null)
				call.setNull(index, Types.TIMESTAMP);
			else {
				//value = EdgeCommonUtil.toUTC((java.util.Date) value);
				Timestamp ts = new java.sql.Timestamp(((java.util.Date) value)
						.getTime());
				call.setTimestamp(index, ts, DaoUtils.getCalWithoutDST());
			}
			break;
		case Types.SQLXML:
			if (value == null)
				call.setNull(index, Types.SQLXML);
			else {
				byte[] data;
				try {
					data = ((String) value).getBytes("utf-8");
					call.setBytes(index, data);
				} catch (UnsupportedEncodingException e) {
					throw new DaoException(e.getMessage());
				}

			}
			break;
		default:
			throw new DaoException("Invalid jdbcType:" + jdbcType);
		}

	}

	protected Object getValue(CallableStatement call, int index, int jdbcType)
			throws SQLException {
		Object results = null;
		switch (jdbcType) {
		case Types.BOOLEAN:
		case Types.BIT:
			results = call.getBoolean(index);
			break;
		case Types.TINYINT:
			results = call.getByte(index);
			break;
		case Types.SMALLINT:
			results = call.getShort(index);
			break;
		case Types.INTEGER:
			results = call.getInt(index);
			break;
		case Types.BIGINT:
			results = call.getLong(index);
			break;
		case Types.FLOAT:
			results = call.getFloat(index);
			break;
		case Types.DOUBLE:
			results = call.getDouble(index);
			break;
		case Types.VARCHAR:
		case Types.CHAR:
			results = call.getString(index);
			break;
		case Types.NVARCHAR:
		case Types.NCHAR:
			results = call.getNString(index);
			break;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			results = call.getTimestamp(index, DaoUtils.getCalWithoutDST());
			break;
		case Types.SQLXML:
			byte[] data = call.getBytes(index);
			if(data==null) return null;
			try {
				results = new String(data,"utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new DaoException(e.toString());
			}

			break;
		default:
			throw new DaoException("Invalid jdbcType:" + jdbcType);
		}
		return results;
	}

	@Override
	public void beginTrans() {
		Connection connection2 = null;

		try {
			connection2 = this.ds.getConnection();
			connection2.setAutoCommit(false);
		} catch (SQLException se) {
			throw new DaoException(se.getMessage(), se);
		}
		if (connection2 == null)
			throw new DaoException("Failed to get a connection from datasource");
		if (connection.get() != null) {
			throw new DaoException(
					"please call commitTrans or rollbackTrans before this invocation!");
		}
		connection.set(connection2);

	}

	@Override
	public void commitTrans() {
		Connection connection2 = connection.get();
		if (connection2 == null) {
			throw new DaoException(
					"please call beginTrans before this invocation!");
		}
		try {
			connection2.commit();

		} catch (SQLException se) {
			throw new DaoException(se.getMessage(), se);
		} finally {
			connection.set(null);
			DaoUtils.closeQuietly(connection2);
		}
	}

	@Override
	public void rollbackTrans() {
		Connection connection2 = connection.get();
		if (connection2 == null) {
			throw new DaoException(
					"please call beginTrans before this invocation!");
		}
		try {
			connection2.rollback();

		} catch (SQLException se) {
			throw new DaoException(se.getMessage(), se);
		} finally {
			connection.set(null);
			DaoUtils.closeQuietly(connection2);
		}

	}

	@Override
	public boolean isTransEnd() {
		return connection.get()==null;
	}

}
