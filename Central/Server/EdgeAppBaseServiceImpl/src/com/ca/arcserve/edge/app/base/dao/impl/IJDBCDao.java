package com.ca.arcserve.edge.app.base.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import com.ca.arcserve.edge.app.base.dao.StoredProcedure;



/**
 * Dao for invoke stored procedures using a given data source.
 * the SQLException is translated into DaoException which extends from {@link Error}. So the DaoException is an unckecked exception.
 * <br/>
 * pay atention to:<br/>
 * 1. input parameters must be before output parameters. It doesn't support inout parameter<br/>
 * 
 * @author gonro07
 *
 */
interface IJDBCDao {
	/**
	 * if @In or @Out doesn't have the jdbcType attribute, its value should be this.
	 */
	public static final int DefaultJdbcType = -100; 
	/**
	 * 
	 * @param dataSource
	 */
	void setDataSource(DataSource dataSource);
	/**
	 * 
	 * @param storeProcedureName
	 * @param params
	 * @param resesClz 
	 * @param spAnno 
	 * @param reses, the result set containers
	 */
	void execute(String storeProcedureName,
			List<StoredProcedureParameter<?>> params, List<List<?>> reses, List<Class<?>> resesClz, StoredProcedure spAnno);
}
