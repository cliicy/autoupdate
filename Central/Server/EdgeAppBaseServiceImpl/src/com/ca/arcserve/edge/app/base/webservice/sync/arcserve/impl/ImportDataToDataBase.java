package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ASBUSyncUtil;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

public class ImportDataToDataBase implements ImportTaskBase {

	private String m_sqlStmt;
	private String m_dataFilePath;
	private String m_tableName;
	private int[] RowTypes;
	private long m_branchID;
	private SyncFileQueueItem _item = null;
	private ASBUJobInfo _jobinfo = null;

	public void run() {
		SyncASBUActivityLog _log = SyncASBUActivityLog.GetInstance(_jobinfo);
		if (_item.isLastFileFlag())
		{
			ASBUSyncUtil syncUtil = ASBUSyncUtil.getASBUSyncUtil(_item.getRhostid());
			syncUtil.UpdateFullSyncStatus(SyncStatus.FINISHED);
			_log.WriteInformation(_item.getBranchName(), SyncActivityLogMsg.getSyncfullsuccessfulMsg());
			return;
		}

		/*
		 * _log.WriteInformation(_item.getBranchName(), String.format(
		 * SyncActivityLogMsg.getSyncimportfileintodatabaseMsg(), _item
		 * .getFileName()));
		 */

		ConfigurationOperator.debugMessage(String.format(SyncActivityLogMsg
				.getSyncimportfileintodatabaseMsg(), _item.getFileName()));

		m_tableName = _item.getFileName().substring(0,
				_item.getFileName().lastIndexOf('.'));
		if (m_tableName.compareToIgnoreCase("aspathname") == 0)
			m_tableName = "aspathnamew";

		this.m_sqlStmt = "INSERT INTO "
				+ ConfigurationOperator._PrefixTableName + m_tableName;
		this.m_tableName = ConfigurationOperator._PrefixTableName + m_tableName;
		this.m_dataFilePath = _item.getFolder() + _item.getFileName();
		this.m_branchID = _item.getRhostid();

		try {
			ImportData(m_branchID);
		} catch (SyncDB_Exception e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		}
		
		File file = new File(m_dataFilePath);
		if(file.exists())
			file.delete();
	}

	public void SetConfiguration(ASBUJobInfo jobinfo, SyncFileQueueItem item) {
		_item = item;
		_jobinfo = jobinfo;
	}

	public Boolean ImportData(long branchID) throws SyncDB_Exception {
		Boolean bFirstRow = true;
		Boolean bSecondRow = true;
		IDBConnecter conn = DBConnecterFactory.getConnectInstance();
		PreparedStatement preStmt = null;
		String strLine = null;
		int batchLine = 0;

		try {
			File inputFile = new File(this.m_dataFilePath);
			if (inputFile.length() == 0)
				return true;

			BufferedReader inFileBufferedReader = null;
			try {
				conn.Connect();

				inFileBufferedReader = new BufferedReader(
						new InputStreamReader(new GZIPInputStream(
								new FileInputStream(inputFile))));

				while ((strLine = inFileBufferedReader.readLine()) != null) {
					SyncDataDeSerializer deSerializer = new SyncDataDeSerializer();
					deSerializer.DeSerializationRow(strLine);
					try {
						if (bFirstRow) {
							GetSqlStatement(deSerializer);
							bFirstRow = false;
							preStmt = conn.CreatePreparedStmt(this.m_sqlStmt);
							conn.setAutoCommit(false);
							preStmt.clearBatch();
						} else if (!bFirstRow && bSecondRow) {
							SetRowTypesWithDeSerializer(deSerializer);
							bSecondRow = false;
						} else {
							SetBatchRow(branchID, deSerializer, preStmt);
							preStmt.addBatch();
							batchLine++;
							if (batchLine >= 50) {
								preStmt.executeBatch();
								preStmt.clearBatch();
								batchLine = 0;
							}
						}
					} catch (SQLException e1) {
						ConfigurationOperator.errorMessage(e1.getMessage(), e1);
						throw new SyncDB_Exception(e1.getErrorCode(),
								this.m_sqlStmt + e1.getMessage());
					}

				}
				try {
					if (batchLine < 50) {
						preStmt.executeBatch();
						preStmt.clearBatch();
						conn.commit();
						conn.Disconnect();
					}
				} catch (SQLException e1) {
					ConfigurationOperator.errorMessage(e1.getMessage(), e1);
					throw new SyncDB_Exception(e1.getErrorCode(),
							this.m_sqlStmt + e1.getMessage());
				}
			} finally {
				conn.Disconnect();
				inFileBufferedReader.close();
			}
			inputFile.delete();
		} catch (IOException e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
			throw new SyncDB_Exception(e.getMessage());
		}

		return true;
	}

	private void SetBatchRow(long branchID, SyncDataDeSerializer deSerializer,
			PreparedStatement preStmt) throws SQLException {
		Iterator<DeSerializeItem> e = deSerializer.m_dataList.iterator();
		int nIndex = 1;
		preStmt.setLong(nIndex, branchID);
		while (e.hasNext()) {
			nIndex++;
			DeSerializeItem item = e.next();

			switch (item.m_typeFlag) {
			case SerializationFlag.TC_BOOL:
				preStmt.setBoolean(nIndex, (Boolean) item.m_data);
				break;
			case SerializationFlag.TC_BYTE_ARRAY:
				preStmt.setBytes(nIndex, (byte[]) item.m_data);
				break;
			case SerializationFlag.TC_DATE:
				preStmt.setTimestamp(nIndex, (Timestamp) item.m_data,
						ConfigurationOperator.getCwithoutdaylightsaves());
				break;
			case SerializationFlag.TC_DOUBLE:
				preStmt.setDouble(nIndex, (Double) item.m_data);
				break;
			case SerializationFlag.TC_FLOAT:
				preStmt.setFloat(nIndex, (Float) item.m_data);
				break;
			case SerializationFlag.TC_INT:
				preStmt.setInt(nIndex, ((Integer) item.m_data).intValue());
				break;
			case SerializationFlag.TC_LONG:
				preStmt.setLong(nIndex, ((Long) item.m_data).longValue());
				break;
			case SerializationFlag.TC_NULL:
				preStmt.setNull(nIndex, RowTypes[nIndex - 2]);
				break;
			case SerializationFlag.TC_STRING:
				preStmt.setString(nIndex, (String) item.m_data);
				break;
			case SerializationFlag.TC_NSTRING:
				preStmt.setNString(nIndex, (String) item.m_data);
				break;
			default:
				continue;
			}
		}
	}

	private void GetSqlStatement(SyncDataDeSerializer deSerializer) {
		String stmtColumn = " ( branchid";
		String stmtValues = " VALUES ( ?";

		Iterator<DeSerializeItem> e = deSerializer.m_dataList.iterator();
		while (e.hasNext()) {
			stmtColumn += ", [" + e.next().m_data.toString() + "] ";
			stmtValues += ", ?";
		}
		stmtColumn += ") ";
		stmtValues += ") ";
		this.m_sqlStmt += stmtColumn + stmtValues;
	}

	private void SetRowTypesWithDeSerializer(SyncDataDeSerializer deSerializer) {
		RowTypes = new int[deSerializer.m_rowCount];
		Iterator<DeSerializeItem> e = deSerializer.m_dataList.iterator();
		int nIndex = 0;
		while (e.hasNext()) {
			DeSerializeItem item = e.next();
			switch (((Integer) item.m_data).intValue()) {
			case SerializationFlag.TC_BOOL:
				RowTypes[nIndex] = Types.BOOLEAN;
				break;
			case SerializationFlag.TC_BYTE_ARRAY:
				RowTypes[nIndex] = Types.BINARY;
				break;
			case SerializationFlag.TC_DATE:
				RowTypes[nIndex] = Types.TIMESTAMP;
				break;
			case SerializationFlag.TC_DOUBLE:
				RowTypes[nIndex] = Types.DOUBLE;
				break;
			case SerializationFlag.TC_FLOAT:
				RowTypes[nIndex] = Types.FLOAT;
				break;
			case SerializationFlag.TC_INT:
				RowTypes[nIndex] = Types.INTEGER;
				break;
			case SerializationFlag.TC_LONG:
				RowTypes[nIndex] = Types.BIGINT;
				break;
			case SerializationFlag.TC_STRING:
				RowTypes[nIndex] = Types.NVARCHAR;
				break;
			case SerializationFlag.TC_UNKNOWN:
				RowTypes[nIndex] = Integer.MAX_VALUE;
				break;
			default:
				RowTypes[nIndex] = Types.NULL;
			}
			nIndex++;
		}
	}

	/*protected void Debug_PrintValue(String logPath, DeSerializeItem item) {
		if (outFileBufferedWritter == null) {
			try {
				outFileBufferedWritter = new BufferedWriter(new FileWriter(
						logPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (item.m_typeFlag == SerializationFlag.TC_INT) {
				outFileBufferedWritter.write(((Integer) item.m_data).toString()
						+ "\t");

			} else if (item.m_typeFlag == SerializationFlag.TC_LONG) {
				outFileBufferedWritter.write(((Long) item.m_data).toString()
						+ "\t");
			} else if (item.m_typeFlag == SerializationFlag.TC_STRING) {
				outFileBufferedWritter.write((String) item.m_data + "\t");
			} else if (item.m_typeFlag == SerializationFlag.TC_DATE) {
				outFileBufferedWritter.write(((Timestamp) item.m_data)
						.toString()
						+ "\t");
			} else if (item.m_typeFlag == SerializationFlag.TC_BOOL) {
				outFileBufferedWritter.write(((Boolean) item.m_data).toString()
						+ "\t");
			} else if (item.m_typeFlag == SerializationFlag.TC_BYTE_ARRAY) {
				outFileBufferedWritter.write(((byte[]) item.m_data).toString()
						+ "\t");
			} else if (item.m_typeFlag == SerializationFlag.TC_DOUBLE) {
				outFileBufferedWritter.write(((Double) item.m_data).toString()
						+ "\t");
			} else if (item.m_typeFlag == SerializationFlag.TC_FLOAT) {
				outFileBufferedWritter.write(((Float) item.m_data).toString()
						+ "\t");
			} else if (item.m_typeFlag == SerializationFlag.TC_NULL) {
				outFileBufferedWritter.write("NULL" + "\t");
			} else
				return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void Debug_PrintNewLine(String logPath) {
		if (outFileBufferedWritter == null) {
			try {
				outFileBufferedWritter = new BufferedWriter(new FileWriter(
						logPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (outFileBufferedWritter != null)
			try {
				outFileBufferedWritter.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@SuppressWarnings("unused")
	private void Debug_PrintString(String logPath, String strMessage) {
		if (outFileBufferedWritter == null) {
			try {
				outFileBufferedWritter = new BufferedWriter(new FileWriter(
						logPath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (outFileBufferedWritter != null)
			try {
				outFileBufferedWritter.write(strMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Debug_PrintNewLine(logPath);
	}
*/
}
