package com.ca.arcserve.edge.app.base.webservice.d2ddatasync.rps;

import java.util.ArrayList;
import java.util.List;


import com.ca.arcserve.edge.app.base.appdaos.EdgeDaoCommonExecuter;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.DataBaseConnectionFactory;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.RpsJobInfo;

public class RpsJobInfoSynchronizer extends RpsBaseSynchronizer{
	
	private RpsJobInfo info = null;
	

	public RpsJobInfoSynchronizer(int branchid, RpsJobInfo info) {	
		this.branchid = branchid;
		this.info = info;
	}

	private List<Object> bindD2DJobInfo(RpsJobInfo jobInfo)
	{
		long rawDataSizeWritten = 0;

		if(jobInfo.getGDDInfo() != null){
			rawDataSizeWritten = jobInfo.getGDDInfo().getRawSize();
		}
				
		List<Object> para = new ArrayList<Object>();
		para.add(jobInfo.getJobID());	//job_id
		para.add(jobInfo.getStartSession()); //sess_id

		// sessGuid
		RpsJobInfo.SessionInfo info = jobInfo.getSessionInfo();
		List<RpsJobInfo.SessionInfo.Session> sessList = null;
		if (info != null) {
			sessList = info.getSession();
			if (sessList != null && !sessList.isEmpty())
				para.add(sessList.get(0).getGUID());
		}

		if (sessList == null) {
			para.add("");
		}
		
		para.add("");//uniqueID
		para.add(jobInfo.getJobName()); //display_name
		/**************************para.add(jobInfo.getJobType()); //bk_type ************************/
		para.add(jobInfo.getJobType()); //bk_type
		para.add(0); //compress_type /****************************miss*******************/
		para.add(jobInfo.getProcessedSize()); //data_sizeKB 
		para.add(0); //trans_data_sizeKB /***********************miss************************/
		para.add(0); //catalog_sizeKB /*************************miss************************/
		para.add(jobInfo.getJobStatus()); //status
		para.add(0); //dest_path_id /*************************miss************************/
		para.add(""); //backupDest /*************************miss************************/
		para.add(""); //recover_point /*************************miss************************/
		//[update_time] Getutcdate()
		para.add(0); //encrypt_type /*************************miss************************/
		para.add(""); //encrypt_password_hash /*************************miss************************/
		para.add(rawDataSizeWritten);//TotalRawDataSizeWritten
		para.add(-1);//BMRFlag
		
		//appType
		
		para.add(branchid);
		para.add(jobInfo.getJobGUID());
		
		return para;
	}
	
	private List<Object> bindRpsJobInfo(int jobid, RpsJobInfo jobInfo) {
		long compressedSize = 0;
		long compressRatio = 0;
		long compressPercetage = 0;
		if(jobInfo.getGDDInfo() != null){
			compressedSize = jobInfo.getGDDInfo().getCompressedSize();
			compressRatio = jobInfo.getGDDInfo().getCompressionRatio();
			compressPercetage = jobInfo.getGDDInfo().getCompressionPercentage();
		}
		
		List<Object> para = new ArrayList<Object>();
		para.add(jobid); //d2d_jobid
		para.add(compressedSize); //compressed_size_kb
		para.add(compressRatio); //compressed_ratio
		para.add(compressPercetage); //compressed_percetage
		para.add(jobInfo.getClientNode()); //source_node
		para.add(EdgeDaoCommonExecuter.getSafeString(jobInfo.getSourceDataStore())); //source_datastore_name
		para.add(EdgeDaoCommonExecuter.getSafeString(jobInfo.getSourceDataStoreUUID())); //source_datastore_uuid
		para.add(EdgeDaoCommonExecuter.getSafeString(jobInfo.getTargetDataStore())); //target_datastore_name
		para.add(EdgeDaoCommonExecuter.getSafeString(jobInfo.getTargetDataStoreUUID())); //target_datastore_uuid
		para.add(EdgeDaoCommonExecuter.getSafeString(jobInfo.getPolicyName())); //source_policy_name
		para.add(EdgeDaoCommonExecuter.getSafeString(jobInfo.getPolicyUUID())); //source_policy_uuid
		return para;
	}

	@Override
	protected int insertInfo() {
		// delete the job with same uuid
		deleteInfo();

		EdgeDaoCommonExecuter ede = DataBaseConnectionFactory.getInstance()
				.createEdgeDaoCommonExecuter();
		try {
			List<Object> para = bindD2DJobInfo(info);

			int jobid = ede.ExecuteDaoWithGenerateKey(
					f_sqlStmtPro.getProperty("sqlStmt_InsertD2DJob"),
					para);

			para = bindRpsJobInfo(jobid, info);
			ede.ExecuteDao(f_sqlStmtPro.getProperty("sqlStmt_InsertRpsJob"),
					para);
		} catch (Exception e) {
			logger.error("insertD2DJob: " + e.getMessage());
			ede.CloseDao();
		}

		return 0;
	}

	@Override
	protected int updateInfo() {
		return 0;
	}

	@Override
	protected int deleteInfo() {
		EdgeDaoCommonExecuter ede = DataBaseConnectionFactory.getInstance()
				.createEdgeDaoCommonExecuter();
		
		try {
			List<Object> para = new ArrayList<Object>();
			para.add(info.getJobGUID());
			para.add(branchid);
			
			String deleteJobStmt = f_sqlStmtPro
					.getProperty("sqlStmt_DeleteD2DJob");
			ede.ExecuteDao(deleteJobStmt, para);
		} catch (Exception e) {
			logger.error("deleteD2DJob: " + e.getMessage());
			ede.CloseDao();
		}
		return 0;
	}
}
