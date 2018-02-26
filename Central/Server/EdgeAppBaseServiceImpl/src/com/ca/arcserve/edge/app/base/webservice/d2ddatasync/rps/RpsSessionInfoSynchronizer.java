package com.ca.arcserve.edge.app.base.webservice.d2ddatasync.rps;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.DataBaseConnectionFactory;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.SessionInfo;

public class RpsSessionInfoSynchronizer extends RpsBaseSynchronizer {

	List<SessionInfo> sessionInfo = null;
	
	public RpsSessionInfoSynchronizer(int branchid, List<SessionInfo> sessionInfo) {
		this.branchid = branchid; 
		this.sessionInfo = sessionInfo;
	}
	
	private List<Object> bindInsertSession(SessionInfo sessInfo) {
				
		List<Object> para = new ArrayList<Object>();
		
		para.add(sessInfo.getBackupDetail().getID().longValue()); //sub_sessid
		para.add(0); //job_internal_id
		para.add(sessInfo.getBackupDetail().getBackupType()); //type
		para.add(0); //flags
		para.add(""); //display_name
		para.add(""); //mount_point
		para.add(sessInfo.getBackupDetail().getSessGuid()); //guid
		para.add(0); //vol_dat_sizeB
		para.add(""); //catalog_file
		para.add(0); //IsBootVolume
		para.add(0); //IsSystemVolume
		para.add(branchid); //branchid
		para.add(sessInfo.getBackupDetail().getNodeName()); //source_node
		
		return para;
	}
	
	@Override
	protected int insertInfo() {
		EdgeDaoCommonExecuter ede = DataBaseConnectionFactory.getInstance()
				.createEdgeDaoCommonExecuter();
		try {
			for (int i = 0; i < sessionInfo.size(); i++) {
				List<Object> para = bindInsertSession(sessionInfo.get(i));
				ede.ExecuteDao(
						f_sqlStmtPro.getProperty("sqlStmt_InsertRpsSession"),
						para);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e);
		} finally {
			ede.CloseDao();
		}
		return 0;
	}
	
	private List<Object> bindUpdateSession(SessionInfo sessInfo) {
										
		List<Object> para = new ArrayList<Object>();
		
		para.add(sessInfo.getBackupDetail().getID()); //sub_sessid
		para.add(0); //job_internal_id
		para.add(sessInfo.getBackupDetail().getBackupType()); //type
		para.add(0); //flags
		para.add(""); //display_name
		para.add(""); //mount_point
		para.add(0); //vol_dat_sizeB
		para.add(""); //catalog_file
		para.add(0); //IsBootVolume
		para.add(0); //IsSystemVolume
		para.add(sessInfo.getBackupDetail().getNodeName()); //source_node
		para.add(sessInfo.getBackupDetail().getSessGuid()); //guid
		para.add(branchid); //branchid
		
		return para;
	}
	
	@Override
	protected int updateInfo() {
		EdgeDaoCommonExecuter ede = DataBaseConnectionFactory.getInstance()
				.createEdgeDaoCommonExecuter();
		try {
			for (int i = 0; i < sessionInfo.size(); i++) {
				List<Object> para = bindUpdateSession(sessionInfo.get(i));
				ede.ExecuteDao(
						f_sqlStmtPro.getProperty("sqlStmt_UpdateRpsSession"),
						para);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e);
		} finally {
			ede.CloseDao();
		}
		return 0;
	}

	@Override
	protected int deleteInfo() {
		EdgeDaoCommonExecuter ede = DataBaseConnectionFactory.getInstance()
				.createEdgeDaoCommonExecuter();
		try {
			for (int i = 0; i < sessionInfo.size(); i++) {
				List<Object> para = new ArrayList<Object>();
				para.add(sessionInfo.get(i).getBackupDetail().getSessGuid());
				para.add(branchid);
				ede.ExecuteDao(
						f_sqlStmtPro.getProperty("sqlStmt_DeleteRpsSession"),
						para);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e);
		} finally {
			ede.CloseDao();
		}
		return 0;
	}

}
