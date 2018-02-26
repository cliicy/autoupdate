package com.ca.arcserve.edge.app.base.dao;
/**
 * you must pair beginTrans and commitTrans or beginTrans and rollbackTrans.
 * Any other groups will cause an exception.
 *
 * These groups should finished in one thread.
 * <pre>
 *  public interface IPersonDao extends ITransactionDao{...}
 *
 *
 *  ITransactionDao transDao = ...;
 *  try{
 *  	transDao.beginTrans();
 *  }catch(DaoException de0){
 *  	....
 *  	return;
 *  }
 *  try{
 *  	....
 *  	transDao.commitTrrans();
 *  }catch(DaoException de1){
 *  	...
 *  	try{
 *  		transDao.rollbackTrans();
 *  	}catch(DaoException de2){
 *  	}
 *  }finally{
 *     ...
 *  }
 * </pre>
 * @author gonro07
 *
 */
public interface ITransactionDao {
	void beginTrans();
	void commitTrans();
	void rollbackTrans();
	boolean isTransEnd();
}
