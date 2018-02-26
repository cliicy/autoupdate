package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;


import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;

/*import java.sql.Savepoint;
 *import java.sql.CallableStatement;
 *import java.sql.Statement;
 */

public interface IDBConnecter {
	void Connect() throws SyncDB_Exception; 
	/*Statement CreateStmt() throws SyncDB_Exception;
	CallableStatement CreateCallableStmt(String s) throws SyncDB_Exception;*/
	PreparedStatement CreatePreparedStmt(String s) throws SyncDB_Exception;
	void Disconnect() throws SyncDB_Exception;
	void setAutoCommit(boolean b) throws SyncDB_Exception;
	/*void setCommit() throws SyncDB_Exception;
	void rollBack() throws SyncDB_Exception;
	Savepoint setSavePoint() throws SyncDB_Exception;*/
	void commit() throws SyncDB_Exception;
	DatabaseMetaData GetMedaData() throws SyncDB_Exception;
}
