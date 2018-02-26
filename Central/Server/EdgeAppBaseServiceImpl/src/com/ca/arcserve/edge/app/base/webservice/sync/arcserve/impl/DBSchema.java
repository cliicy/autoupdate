package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Dictionary;
import java.util.Hashtable;

import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;

class DBSchema {
	
	static final String columnNameTag = "COLUMN_NAME";
	static final String columnTypeTag = "DATA_TYPE";

	static Dictionary<String, Integer> EnumDBSchema(IDBConnecter con, String TableName) {
		Dictionary<String, Integer> dc = new Hashtable<String, Integer>();
		
		try {
			DatabaseMetaData dm = con.GetMedaData();
			ResultSet rs = dm.getColumns(null, "dbo", TableName, "%");
			while(rs.next())
			{				
				dc.put(rs.getString(columnNameTag), rs.getInt(columnTypeTag));
			}
			rs.close();
		} catch (Exception e) {
			ConfigurationOperator.errorMessage(DBSchema.class+e.getMessage(), e);
		}
		
		return dc;
	}
}
