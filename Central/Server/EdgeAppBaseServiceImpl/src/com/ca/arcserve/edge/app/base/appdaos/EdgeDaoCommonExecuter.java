package com.ca.arcserve.edge.app.base.appdaos;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.BeanProcessor;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoUtils;
import com.ca.arcserve.edge.app.base.db.impl.ConnectionManagerUtil;

public class EdgeDaoCommonExecuter {
	private static Logger log = Logger.getLogger(EdgeDaoCommonExecuter.class);
	private Connection connection = null;
	
	private boolean isConvertInputTime2UTC;

	public Connection getConnection() {
		return connection;
	}

	public EdgeDaoCommonExecuter(boolean convertInputTime2UTCFlag) {
		try {
			isConvertInputTime2UTC=convertInputTime2UTCFlag; //by default, it's true
			this.connection = ConnectionManagerUtil.getDs().getConnection();
		} catch (SQLException e) {
			log.error("------- Get connection object failed." + e.getMessage());
		}
	}
	
	public EdgeDaoCommonExecuter() {
		try {
			isConvertInputTime2UTC=true; //by default, it's true
			this.connection = ConnectionManagerUtil.getDs().getConnection();
		} catch (SQLException e) {
			log.error("------- Get connection object failed." + e.getMessage());
		}
	}

	public void BeginTrans() throws DaoException {
		try {
			connection.setAutoCommit(false);
			connection.setSavepoint();
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}

	public void CommitTrans() throws DaoException {
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}

	public void RollbackTrans() throws DaoException {
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}

	public <T> void ExecuteDao(String sqlCmd, List<Object> para, Class<T> type,
			List<T> result) throws DaoException {
		ExecuteDao(sqlCmd, para, type, result, 0);
	}

	public <T> void ExecuteDao(String sqlCmd, List<Object> para, Class<T> type,
			List<T> result, int count) throws DaoException {
		PreparedStatement createStatement = null;
		ResultSet rs = null;

		try {
			createStatement = connection.prepareStatement(sqlCmd);

			if (count != 0)
				createStatement.setMaxRows(count);

			BindParameterList(para, createStatement);
			rs = createStatement.executeQuery();
			BeanProcessor pro = DaoFactory.getBeanProcessor();
			pro.toBeanList(rs, type, result);
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			DaoUtils.closeQuietly(createStatement, rs);
		}
	}

	public void ExecuteDao(String sqlCmd, List<Object> para,
			List<Integer> result, int count) throws DaoException {
		PreparedStatement createStatement = null;
		ResultSet rs = null;

		try {
			createStatement = connection.prepareStatement(sqlCmd);

			if (count != 0)
				createStatement.setMaxRows(count);

			BindParameterList(para, createStatement);
			rs = createStatement.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			DaoUtils.closeQuietly(createStatement, rs);
		}
	}
	
	/**
	 *
	 * @param sqlCmd Update SQL statement
	 * @param para
	 * @param result returns the query result
	 * @throws DaoException
	 */
	public void ExecuteDao(String sqlCmd, List<Object> para,
			List<Long> result) throws DaoException {
		PreparedStatement createStatement = null;
		ResultSet rs = null;

		try {
			createStatement = connection.prepareStatement(sqlCmd);

			BindParameterList(para, createStatement);
			rs = createStatement.executeQuery();
			while (rs.next()) {
				result.add(rs.getLong(1));
			}
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			DaoUtils.closeQuietly(createStatement, rs);
		}
	}
	
	/**
	 *
	 * @param sqlCmd Update SQL statement
	 * @param para
	 * @param result returns the count of rows
	 * @throws DaoException
	 */
	public void ExecuteDaoUpdate(String sqlCmd, List<Object> para,
			int[] result) throws DaoException {
		PreparedStatement createStatement = null;
		ResultSet rs = null;

		try {
			createStatement = connection.prepareStatement(sqlCmd);
			BindParameterList(para, createStatement);
			result[0] = createStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			DaoUtils.closeQuietly(createStatement, rs);
		}
	}
	
	public <T> void ExecuteDaoTemplateResult(String sqlCmd, List<Object> para,
			List<T> result, Class<T> type, int count) throws DaoException {
		PreparedStatement createStatement = null;
		ResultSet rs = null;

		try {
			createStatement = connection.prepareStatement(sqlCmd);

			if (count != 0)
				createStatement.setMaxRows(count);

			BindParameterList(para, createStatement);
			rs = createStatement.executeQuery();
			Method m = null;
			try{
			if(type.equals(String.class)){
				m=ResultSet.class.getMethod("getString", int.class);
			}else if(type.equals(Timestamp.class)){
				m=ResultSet.class.getMethod("getTimestamp", int.class);
			}else if(type.equals(Integer.TYPE) || type.equals(Integer.class)){
				m=ResultSet.class.getMethod("getInt", int.class);
			}else{
				throw new IllegalArgumentException("unsupport this type");
			}
			}catch(NoSuchMethodException e){
				
			}
			while (rs.next()) {
				try {
					result.add((T)m.invoke(rs, 1));
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			DaoUtils.closeQuietly(createStatement, rs);
		}
	}
	
	public void ExecuteDaoStringResult(String sqlCmd, List<Object> para,
			List<String> result, int count) throws DaoException {
		ExecuteDaoTemplateResult(sqlCmd, para, result, String.class, count);
	}

	public static String getSafeString(String str){
		return str;
	}

	public void ExecuteDaoTimeStampResult(String sqlCmd, List<Object> para,
			List<Timestamp> result, int count) throws DaoException {
		ExecuteDaoTemplateResult(sqlCmd, para, result, Timestamp.class, count);
	}

	public void ExecuteDao(String sqlCmd, List<Object> para)
			throws DaoException {
		PreparedStatement createStatement = null;
		try {
			createStatement = connection.prepareStatement(sqlCmd);
			BindParameterList(para, createStatement);
			createStatement.execute();
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		}
	}

	public int ExecuteDaoWithGenerateKey(String sqlCmd, List<Object> para){
		PreparedStatement createStatement = null;
		ResultSet rs = null;
		try {
			createStatement = connection.prepareStatement(sqlCmd, Statement.RETURN_GENERATED_KEYS);
			BindParameterList(para, createStatement);
			createStatement.execute();
			rs = createStatement.getGeneratedKeys();
			if(rs != null && rs.next())
				return rs.getInt(1);
			return -1;
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			DaoUtils.closeQuietly(createStatement, rs);
		}
	}

	public void CloseDao() {

		try {
			DaoUtils.close(connection);
		} catch (SQLException e) {
			log.error("------ Close JavaDB Dao failed." + e.getMessage());
		}
	}

	protected void finalize() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			log.error("------ Rlease JavaDB connection failed."
					+ e.getMessage());
		}

	}

	private void BindParameterList(List<Object> para, PreparedStatement statement) throws SQLException{
		if(para != null && para.size() > 0){
			for (int i = 0; i < para.size(); i++) {

				//avoid null string
				if(para.get(i) == null) {
					statement.setNull(i+1, Types.VARCHAR);
					continue;
				}
				
				if(para.get(i) instanceof Timestamp){

					/*Calendar gc = Calendar.getInstance();
					gc.setTimeInMillis(((Timestamp)para.get(i)).getTime() - java.util.TimeZone.getDefault().getRawOffset());

					Timestamp utcTime = new Timestamp(gc.getTimeInMillis());
					para.set(i, utcTime);*/
					if(isConvertInputTime2UTC) {
						Date value = EdgeCommonUtil.toUTC((java.util.Date) para.get(i));
						Timestamp utcDate = new java.sql.Timestamp(value.getTime());
						para.set(i, utcDate);
					}
				} else if (para.get(i) instanceof Time) {
					if(isConvertInputTime2UTC) {
						Date value = EdgeCommonUtil.toUTC((java.sql.Time) para
								.get(i));
						Time utcTime = new java.sql.Time(value.getTime());
						para.set(i, utcTime);
					}
				} else if (para.get(i) instanceof java.util.Date) {
					if(isConvertInputTime2UTC) {
						Date value = EdgeCommonUtil.toUTC((java.util.Date) para
								.get(i));
						Date utcDate = new java.sql.Timestamp(value.getTime());
						para.set(i, utcDate);
					}
				}

				statement.setObject(i + 1, para.get(i));

			}
		}
	}
}
