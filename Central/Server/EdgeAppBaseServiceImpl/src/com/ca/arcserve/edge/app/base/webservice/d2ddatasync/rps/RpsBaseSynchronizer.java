package com.ca.arcserve.edge.app.base.webservice.d2ddatasync.rps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class RpsBaseSynchronizer {
	protected volatile static Properties f_sqlStmtPro = new Properties();
	protected static final Logger logger = Logger.getLogger(RpsBaseSynchronizer.class);
	protected int branchid = 0; 
	
	protected RpsBaseSynchronizer(){
		initSqlStmtProperties();
	}
	
	private static void initSqlStmtProperties() {
		if (f_sqlStmtPro.isEmpty()) {

			synchronized (f_sqlStmtPro) {
				if (!f_sqlStmtPro.isEmpty())
					return;

				InputStream is = RpsBaseSynchronizer.class
						.getResourceAsStream("d2dsync_rps_sql.properties");
				try {
					f_sqlStmtPro.load(is);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("RpsJobInfoSynchronizer:" + e.getMessage());
				}
			}
		}
	}

	public static Properties getSqlStmtPro() {
		initSqlStmtProperties();
		return f_sqlStmtPro;
	}
	
	public int doSync(String operation) {
		if (operation.compareToIgnoreCase("DELETE") == 0)
			return deleteInfo();
		else if (operation.compareToIgnoreCase("ADD") == 0)
			return insertInfo();
		else if (operation.compareToIgnoreCase("MODIFY") == 0)
			return updateInfo();
		return 0;
	}
	
	abstract protected int insertInfo(); 
	abstract protected int updateInfo();
	abstract protected int deleteInfo();
}
