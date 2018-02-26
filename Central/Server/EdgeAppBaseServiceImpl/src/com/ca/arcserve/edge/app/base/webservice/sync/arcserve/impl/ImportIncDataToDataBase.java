package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.ChangeRecord.ChangeActionType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.keyschema.KeySchema;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.keyschema.KeySchemaUtil;


class ImportIncDataToDataBase {

	private String m_sqlStmt;
	private PreparedStatement m_preStmt = null;
	private long m_lastTransactionID = 0;
	private int m_branchID = 0;
	private long m_lastSuccessTranID = 0;
	private Timestamp m_timeBase = Timestamp.valueOf("1970-01-01 00:00:00");
	private Dictionary<String, Integer> m_DBSchema;
	private static KeySchema key = KeySchemaUtil.getUnmarshalKeySchema();
	private static final String specifyCommonColumn = "branchid";

	private String m_debugString;
	private ArrayList<String> m_uniqueColumns = null;

	public ImportIncDataToDataBase(int branchID) {
		m_branchID = branchID;
	}
	
	private void IniteTable(IDBConnecter conn, ChangeRecord records){
		m_DBSchema = DBSchema.EnumDBSchema(conn, records.TableName);
		m_uniqueColumns = null;
		if(records.Type == ChangeActionType.Delete)
			m_uniqueColumns = GetUniqueColumn(records.TableName);
			
	}

	private Boolean CreateSqlStmt(ChangeRecord records) {
		m_sqlStmt = "";
		String sqlCondition = "";
		String sqlColumns = "";
		Boolean bIsDelOp = false;
		
		if (records.Type == ChangeActionType.Insert
				|| records.Type == ChangeActionType.InitInsert) {
			m_sqlStmt = "INSERT INTO " + records.TableName;
			sqlCondition = " VALUES ( ?";
			sqlColumns = " ( branchid";
		} else if (records.Type == ChangeActionType.Delete) {
			bIsDelOp = true;
			m_sqlStmt = "DELETE FROM " + records.TableName;
			sqlCondition = " WHERE branchid = ?";
		} else {
			return false;
		}

		Iterator<String> itColumns = records.Columns.iterator();
		while (itColumns.hasNext()) {
			String column = itColumns.next();
			
			/*
			 * DB schema is getting from metadata
			 * if colType == null means the column is not eixst in Edge
			 * return true for ignore this column
			 * for example in GDB server astpses table have masterjobid which not exist in GDB table
			 * ignore this column
			 */
			if(m_DBSchema.get(column) == null)
				continue;
			
			if (!bIsDelOp) {
				sqlColumns += ", [" + column + "] ";
				sqlCondition += ", ?";
			} else {
				if (m_uniqueColumns == null || m_uniqueColumns.isEmpty()
						|| m_uniqueColumns.contains(column))
					sqlCondition += " AND " + column + "= ?";
			}
		}
		if (!bIsDelOp) {
			m_sqlStmt += sqlColumns + " ) " + sqlCondition + " ) ";
		} else {
			m_sqlStmt += sqlCondition;
		}
		
		return true;

		/*
		 * if (records.Type == ChangeActionType.Insert || records.Type ==
		 * ChangeActionType.InitInsert) { m_sqlStmt = "INSERT INTO " +
		 * records.TableName; sqlCondition = " VALUES ( ?"; sqlColumns =
		 * " ( branchid";
		 * 
		 * Iterator<String> itColumns = records.Columns.iterator(); while
		 * (itColumns.hasNext()) {
		 * 
		 * sqlColumns += ", [" + itColumns.next() + "] "; sqlCondition += ", ?";
		 * } m_sqlStmt += sqlColumns + " ) " + sqlCondition + " ) "; } else if
		 * (records.Type == ChangeActionType.Delete) { m_sqlStmt =
		 * "DELETE FROM " + records.TableName; String sqlCondition =
		 * " WHERE branchid = ?";
		 * 
		 * Iterator<String> itColumns = records.Columns.iterator(); while
		 * (itColumns.hasNext()) { String column = itColumns.next();
		 * if(m_uniqueColumns == null || m_uniqueColumns.isEmpty() ||
		 * m_uniqueColumns.contains(column)) sqlCondition += " AND " + column +
		 * "= ?"; } m_sqlStmt += sqlCondition; } else { m_sqlStmt = ""; } return
		 * true;
		 */
	}

	private Boolean SetPreparedStatement(IDBConnecter conn, ChangeRecord records)
			throws SyncDB_Exception {

		m_lastTransactionID = records.ID;

		/*if (m_lastTransactionID == 0) {
			// conn.setSavePoint();
		} else if (records.ID != m_lastTransactionID) {
			// ExecutePreparedStatement(conn);
			m_lastTransactionID = records.ID;
		}*/

		try {
			m_preStmt = conn.CreatePreparedStmt(m_sqlStmt);
			m_debugString = m_sqlStmt;

			Iterator<String> itValues = records.Values.iterator();
			Iterator<String> itColumns = records.Columns.iterator();
			int nIndex = 1;

			m_preStmt.setInt(nIndex, m_branchID);
			while (itValues.hasNext() && itColumns.hasNext()) {
				String strColumn = itColumns.next();
				String strValues = itValues.next();
				Integer colType = m_DBSchema.get(strColumn);
				/*
				 * DB schema is getting from metadata
				 * if colType == null means the column is not eixst in Edge
				 * return true for ignore this column
				 * for example in GDB server astpses table have masterjobid which not exist in GDB table
				 * ignore this column
				 */
				if(colType == null)
					continue;
				
				if(m_uniqueColumns != null && !m_uniqueColumns.isEmpty() && !m_uniqueColumns.contains(strColumn))
					continue;

				m_debugString += String.format("[%s:", strColumn);

				nIndex++;
				BindPreStmt(nIndex, colType, strColumn, strValues);
			}
			ConfigurationOperator.debugMessage(m_debugString);
			m_preStmt.execute();
			m_lastTransactionID = records.ID;
		} catch (SQLException e) {
			throw new SyncDB_Exception(e.getErrorCode(), this.m_sqlStmt
					+ e.getMessage());
		}

		return true;
	}

	private Boolean BindPreStmt(int nIndex, Integer colType, String Column, String strValue)
			throws SQLException {
		
		if (strValue == null) {
			m_preStmt.setNull(nIndex, colType);
			return true;
		}

		switch (colType) {
		case Types.ARRAY:
		case Types.CHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
			m_preStmt.setString(nIndex, strValue);
			m_debugString += String.format("%s]", strValue);
			break;
		case Types.BIGINT:
			m_preStmt.setLong(nIndex, Long.parseLong(strValue));
			m_debugString += String.format("%s]", strValue);
			break;
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			if(Column.compareToIgnoreCase("sessguid")==0)
			{
				UUID id = UUID.fromString(strValue);
				m_preStmt.setBytes(nIndex, UUIDToByte(id));
			}
			else
				m_preStmt.setBytes(nIndex, strValue.getBytes());
			m_debugString += String.format("%s]", strValue);
			break;
		case Types.BIT:
		case Types.TINYINT:
			m_preStmt.setByte(nIndex, Byte.parseByte(strValue));
			m_debugString += String.format("%s]", strValue);
			break;
		case Types.BOOLEAN:
			m_preStmt.setBoolean(nIndex, Boolean.parseBoolean(strValue));
			m_debugString += String.format("%s]", strValue);
			break;
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			m_preStmt.setTimestamp(nIndex, new Timestamp(Long
					.parseLong(strValue)
					+ m_timeBase.getTime()), ConfigurationOperator
					.getCwithoutdaylightsaves());
			m_debugString += String.format("%s]", new Timestamp(Long
					.parseLong(strValue)
					+ m_timeBase.getTime()).toString());
			break;
		case Types.DOUBLE:
			m_preStmt.setDouble(nIndex, Double.parseDouble(strValue));
			m_debugString += strValue;
			break;
		case Types.FLOAT:
		case Types.REAL:
			m_preStmt.setFloat(nIndex, Float.parseFloat(strValue));
			m_debugString += strValue;
			break;
		case Types.DECIMAL:
		case Types.INTEGER:
		case Types.NUMERIC:
			m_preStmt.setInt(nIndex, Integer.parseInt(strValue));
			m_debugString += strValue;
			break;
		case Types.SMALLINT:
			m_preStmt.setShort(nIndex, Short.parseShort(strValue));
			m_debugString += strValue;
			break;
		default:
			return false;
		}

		return true;
	}

	private Boolean ExecutePreparedStatement(IDBConnecter conn)
			throws SyncDB_Exception {
		if (m_lastTransactionID != 0) {
			m_lastSuccessTranID = m_lastTransactionID;
			m_lastTransactionID = 0;
			conn.commit();
			//conn.setSavePoint();
		}
		return true;
	}
	
	private byte[] UUIDToByte(UUID id){
		ByteBuffer buffer = ByteBuffer.allocate(16);
		buffer.putLong(id.getMostSignificantBits());
		buffer.putLong(id.getLeastSignificantBits());
		return reverseUUIDByteBuffer(buffer);
	}
	
	private byte[] reverseUUIDByteBuffer(ByteBuffer buffer){
		
		byte[] ret = buffer.array();
		
		reverseByte(ret, 0, 4);
		reverseByte(ret, 4, 2);
		reverseByte(ret, 6, 2);
		
		return ret;
	}
	
	private void reverseByte(byte[] buffer, int start, int len){
		int end = len/2;
		for(int i = 0; i < end; i++){
			int swap_start = start+i;
			int swap_end = start + len - i - 1 ;
			byte b = buffer[swap_start];
			buffer[swap_start] = buffer[swap_end];
			buffer[swap_end] = b;
		}
	}

	public Boolean ImportIncrementalData(byte[] xmlBuffer)
			throws SyncDB_Exception {
		IDBConnecter conn = DBConnecterFactory.getConnectInstance();
		String strLine = null;
		BufferedReader inFileBufferedReader = null;
		try {
			conn.Connect();

			if (m_preStmt == null)
				conn.setAutoCommit(true);

			if (xmlBuffer == null || xmlBuffer.length == 0)
				return true;

			try {
				inFileBufferedReader = new BufferedReader(
						new InputStreamReader(new GZIPInputStream(
								new ByteArrayInputStream(xmlBuffer))));

				while ((strLine = inFileBufferedReader.readLine()) != null) {
					SyncDataDeSerializer deSerializer = new SyncDataDeSerializer();
					ChangeRecord records = deSerializer
							.DeSerializationXmlRow(strLine);
					
					try {
						IniteTable(conn, records);
						CreateSqlStmt(records);
						SetPreparedStatement(conn, records);
					} catch (Exception e) {
						ConfigurationOperator.errorMessage(e.getMessage(), e);
					}
				}
				if (m_lastTransactionID != 0)
					ExecutePreparedStatement(conn);

			} finally {
				if (inFileBufferedReader != null)
					try {
						inFileBufferedReader.close();
					} catch (Exception e) {
						// ignore this
					}
			}
		} catch (Exception e) {
			throw new SyncDB_Exception(e.getMessage());
		} finally {
			conn.Disconnect();
		}

		return true;
	}

	public long getlastSuccessTranID() {
		return m_lastSuccessTranID;
	}

	private ArrayList<String> GetUniqueColumn(String objname)
	{
		//Get key column from keyschema.xml
		ArrayList<String> uniqueColumns = KeySchemaUtil.getUniqueColumn(key, objname);
		
		if(!uniqueColumns.isEmpty())
			uniqueColumns.add(specifyCommonColumn);
		
		return uniqueColumns;
		
		//Get unique column from unique index
		/*String[] keys = new String[1];
		IEdgeSyncDao iDao = DaoFactory.getDao(IEdgeSyncDao.class);
		iDao.as_edge_sync_get_unique_column(objname, keys);

		if(keys == null || keys[0] == null)
			return null;

		ArrayList<String> list = new ArrayList<String>();
		String[] columns = keys[0].split(",");

		for(int i = 0; i < columns.length; i++)
		{
			list.add(columns[i].trim());
		}

		return list;*/
	}

}
