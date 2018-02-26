package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.common.DeadLockInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailServerSetting.EmailService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;

public interface IEdgeSettingDao{
	@StoredProcedure(name = "as_edge_asdatasync_setting_get")
	void as_edge_asdatasync_setting_get(int branchid,
			@ResultSet List<EdgeASDataSyncSetting> Settings);

	@StoredProcedure(name = "as_edge_asdatasync_setting_list")
	void as_edge_asdatasync_setting_list(
			@ResultSet List<EdgeASDataSyncSetting> Settings);

	@StoredProcedure(name = "as_edge_d2ddatasync_setting_get")
	void as_edge_d2ddatasync_setting_get(int branchid,
			@ResultSet List<EdgeD2DDataSyncSetting> Settings);

	@StoredProcedure(name = "as_edge_d2ddatasync_setting_list")
	void as_edge_d2ddatasync_setting_list(
			@ResultSet List<EdgeD2DDataSyncSetting> Settings);

	@StoredProcedure(name = "as_edge_srmprobing_setting_get")
	void as_edge_srmprobing_setting_get(
			@ResultSet List<EdgeSRMProbingSetting> Settings);
	
	@StoredProcedure(name = "as_edge_nodeDelete_setting_get")
	void as_edge_nodedeleteprobing_setting_get(
			@ResultSet List<EdgeNodeDeleteProbingSetting> Settings);
	
	@StoredProcedure(name = "as_edge_it_managment_setting_get")
	void as_edge_it_managment_setting_get(
			@ResultSet List<ITManagementModel> Settings);
	
	@StoredProcedure(name = "as_edge_srm_archive_setting_get")
	void as_edge_srm_archive_setting_get(
			int type, int action,
			@ResultSet List<EdgeSRMArchiveSetting> Settings);

	@StoredProcedure(name = "as_edge_discovery_setting_get")
	void as_edge_discovery_setting_get(int SettingType,
			@ResultSet List<EdgeDiscoverySetting> Settings,@ResultSet List<EdgeScheduler_Schedule> Schedues);

	@StoredProcedure(name = "as_edge_asdatasync_setting_set")
	void as_edge_asdatasync_setting_set(
			@In(jdbcType = Types.NVARCHAR) String SyncFilepath, int RetryTimes,
			int RetryInterval, int Status, int ScheduleID,
			@In(jdbcType = Types.NVARCHAR) String Name,
			@In(jdbcType = Types.NVARCHAR) String Description, int ActionType,
			int ScheduleType,
			@In(jdbcType = Types.NVARCHAR) String ScheduleParam,
			@In(jdbcType = Types.TIMESTAMP) Date ActionTime,
			@In(jdbcType = Types.TIMESTAMP) Date RepeatFrom, int RepeatType,
			@In(jdbcType = Types.NVARCHAR) String RepeatParam, int branchid);

	@StoredProcedure(name = "as_edge_d2ddatasync_setting_set")
	void as_edge_d2ddatasync_setting_set(int RetryTimes, int RetryInterval,
			int ScheduleID, int branchid);

	@StoredProcedure(name = "as_edge_srmprobing_setting_set")
	void as_edge_srmprobing_setting_set(@In(jdbcType = Types.NVARCHAR) String ProbeFilter, int ThreadCount,
			int Timeout, int RetryTimes, int RetryInterval, int Status, int ScheduleID,
			@In(jdbcType = Types.NVARCHAR) String Name,
			@In(jdbcType = Types.NVARCHAR) String Description, int ActionType,
			int ScheduleType,
			@In(jdbcType = Types.NVARCHAR) String ScheduleParam,
			@In(jdbcType = Types.TIMESTAMP) Date ActionTime,
			@In(jdbcType = Types.TIMESTAMP) Date RepeatFrom, int RepeatType,
			@In(jdbcType = Types.NVARCHAR) String RepeatParam);
	
	@StoredProcedure(name = "as_edge_nodeDelete_setting_set")
	void as_edge_nodeDelete_setting_set(@In(jdbcType = Types.NVARCHAR) String ProbeFilter, int ThreadCount,
			int Timeout, int RetryTimes, int RetryInterval, int Status, int ScheduleID,
			@In(jdbcType = Types.NVARCHAR) String Name,
			@In(jdbcType = Types.NVARCHAR) String Description, int ActionType,
			int ScheduleType,
			@In(jdbcType = Types.NVARCHAR) String ScheduleParam,
			@In(jdbcType = Types.TIMESTAMP) Date ActionTime,
			@In(jdbcType = Types.TIMESTAMP) Date RepeatFrom, int RepeatType,
			@In(jdbcType = Types.NVARCHAR) String RepeatParam);
	
	@StoredProcedure(name = "as_edge_discovery_setting_set")
	int as_edge_discovery_setting_set(
			@In(jdbcType = Types.NVARCHAR) String xmlContent,@In(jdbcType = Types.NVARCHAR) String Name,
			@In(jdbcType = Types.NVARCHAR) String Description, int ActionType,
			int ScheduleType,
			@In(jdbcType = Types.NVARCHAR) String ScheduleParam,
			@In(jdbcType = Types.TIMESTAMP) Date ActionTime,
			@In(jdbcType = Types.TIMESTAMP) Date RepeatFrom, int RepeatType,
			@In(jdbcType = Types.NVARCHAR) String RepeatParam, int SettingType);
	
	@StoredProcedure(name = "as_edge_it_management_setting_set")
	int as_edge_it_management_setting_set(
			int status,
			@In(jdbcType = Types.NVARCHAR) String Name,
			@In(jdbcType = Types.NVARCHAR) String Description, int ActionType,
			int ScheduleType,
			@In(jdbcType = Types.NVARCHAR) String ScheduleParam,
			@In(jdbcType = Types.TIMESTAMP) Date ActionTime,
			@In(jdbcType = Types.TIMESTAMP) Date RepeatFrom, int RepeatType,
			@In(jdbcType = Types.NVARCHAR) String RepeatParam);
	
	@StoredProcedure(name = "as_edge_email_setting_get")
	void as_edge_email_server_setting_get(
			@In(jdbcType = Types.NVARCHAR) String mail_server,
			int port,
			@ResultSet List<EmailServerSetting> serverSettings);
	
//	void as_edge_email_server_setting_set(
//			EmailService mail_server,
//			@In(jdbcType = Types.NVARCHAR) String smtp,
//			int port,
//			short auth_flag,
//			@In(jdbcType = Types.NVARCHAR) String user_name,
//			@In(jdbcType = Types.NVARCHAR) String user_password,
//			short ssl_flag,
//			short tls_flag,
//			short proxy_flag,
//			@In(jdbcType = Types.NVARCHAR) String proxy_server,
//			int proxy_port,
//			short proxy_auth_flag,
//			@In(jdbcType = Types.NVARCHAR) String proxy_user_name,
//			@In(jdbcType = Types.NVARCHAR) String proxy_user_password,
//			EdgeDaoCommonExecuter ede);
	
	@StoredProcedure(name = "as_edge_email_setting_update")
	void as_edge_email_server_setting_set(
			EmailService mail_server,
			@In(jdbcType = Types.NVARCHAR) String smtp,
			int port,
			short auth_flag,
			@In(jdbcType = Types.NVARCHAR) String user_name,
			@In(jdbcType = Types.NVARCHAR) String user_password,
			short ssl_flag,
			short tls_flag,
			short proxy_flag,
			@In(jdbcType = Types.NVARCHAR) String proxy_server,
			int proxy_port,
			short proxy_auth_flag,
			@In(jdbcType = Types.NVARCHAR) String proxy_user_name,
			@In(jdbcType = Types.NVARCHAR) String proxy_user_password);
	
	@StoredProcedure(name = "as_edge_email_info_get")
	void as_edge_email_template_setting_get(
			int featureId,
			@ResultSet List<EmailTemplateSetting> settingList);
	
//	void as_edge_email_template_setting_set(
//			@In(jdbcType = Types.NVARCHAR) String from,
//			@In(jdbcType = Types.NVARCHAR) String recipients,
//			@In(jdbcType = Types.NVARCHAR) String subject,
//			short html_flag,
//			int featureId,
//			EdgeDaoCommonExecuter ede);
	
	@StoredProcedure(name = "as_edge_email_info_add")
	void as_edge_email_template_setting_set(
			@In(jdbcType = Types.NVARCHAR) String from,
			@In(jdbcType = Types.NVARCHAR) String recipients,
			@In(jdbcType = Types.NVARCHAR) String subject,
			short html_flag,
			int featureId);
	
	
	@StoredProcedure(name = "as_edge_saveDeployD2DSettings")
	void saveDeployD2DSettings(
		int port,
		@In(jdbcType = Types.NVARCHAR) String installPath,
		int allowInstallDriver,
		int rebootType,
		int protocol,
		int productType,
		@In(jdbcType = Types.TIMESTAMP) Date rebootTime
		);
	
	@StoredProcedure(name = "as_edge_getDeployD2DSettings")
	void getDeployD2DSettings(
		@ResultSet List<EdgeDeployD2DSettings> settingsList
		);
	
	@StoredProcedure(name = "as_edge_configuration_getById")
	void as_edge_configuration_getById(int paramId, 
			@Out String[] paramValue);
	
	@StoredProcedure(name = "as_edge_configuration_delete")
	void as_edge_configuration_delete(int paramId);
	
	@StoredProcedure(name = "as_edge_configuration_insertOrUpdate")
	void as_edge_configuration_insertOrUpdate(int paramId, String paramKey, String paramValue);
	
	@StoredProcedure(name = "as_edge_getDeadLockInfo")
	void as_edge_getDeadLockInfo(@ResultSet List<DeadLockInfo> deadLockInfos);
}
