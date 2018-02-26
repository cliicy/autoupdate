package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeSchedulerDao {
	@StoredProcedure(name = "as_edge_emailreport_list")
	void as_edge_emailreport_list(int ID,
			@ResultSet List<EdgeScheduler_EmailReport> Reports);
			
	@StoredProcedure(name = "as_edge_emailreport_list_by_emailid")
	void as_edge_emailreport_list_by_emailid(int emailID,
			@ResultSet List<EdgeScheduler_EmailReport> Reports);			

	@StoredProcedure(name = "as_edge_schedule_list")
	void as_edge_schedule_list(int ID,
			@ResultSet List<EdgeScheduler_Schedule> Schedues);

	@StoredProcedure(name = "as_edge_schedules_list")
	void as_edge_schedules_list(@In(jdbcType = Types.NVARCHAR) String IDs,
			@ResultSet List<EdgeScheduler_Schedule> Schedues);
	
	@StoredProcedure(name = "as_edge_scheduleemail_list")
	void as_edge_scheduleemail_list(int ID,
			@ResultSet List<EdgeScheduler_ScheduleEmail> Emails);
			
	@StoredProcedure(name = "as_edge_scheduleemail_list_by_scheduleid")
	void as_edge_scheduleemail_list_by_scheduleid(int schID,
			@ResultSet List<EdgeScheduler_ScheduleEmail> Emails);
	
	@StoredProcedure(name = "as_edge_emailreport_update")
	void as_edge_emailreport_update(int EmailID, int ReportID, 
			@In(jdbcType = Types.NVARCHAR) String ReportParam);
	
	@StoredProcedure(name = "as_edge_emailreport_create")
	void as_edge_emailreport_create(int EmailID, int ReportID, 
			@In(jdbcType = Types.NVARCHAR) String ReportParam,
			@Out(jdbcType = Types.INTEGER) int[] id);

	@StoredProcedure(name = "as_edge_schedule_create")
    void as_edge_schedule_create(@In(jdbcType = Types.NVARCHAR) String name,
           @In(jdbcType = Types.NVARCHAR) String desc, int acttype,
           int schedtype, @In(jdbcType = Types.NVARCHAR) String schedparam,
           @In(jdbcType = Types.TIMESTAMP) Date actiontime,
           @In(jdbcType = Types.TIMESTAMP) Date repfrom, int reptype,
           @In(jdbcType = Types.NVARCHAR) String repparam,
           @In(jdbcType = Types.TIMESTAMP) Date lastactiontime,
           int actedtimes, int userid,
           @Out(jdbcType = Types.INTEGER) int[] id);
    
    @StoredProcedure(name = "as_edge_schedule_update_for_scheduler")
    void as_edge_schedule_update_for_scheduler(int ID,          
           @In(jdbcType = Types.TIMESTAMP) Date lastactiontime,
           int actedtimes);
    
    @StoredProcedure(name = "as_edge_schedule_update_for_ui")
    void as_edge_schedule_update_for_ui(int ID,
           @In(jdbcType = Types.NVARCHAR) String name,
           @In(jdbcType = Types.NVARCHAR) String desc, int acttype,
           int schedtype, @In(jdbcType = Types.NVARCHAR) String schedparam,
           @In(jdbcType = Types.TIMESTAMP) Date actiontime,
           @In(jdbcType = Types.TIMESTAMP) Date repfrom, int reptype,
           @In(jdbcType = Types.NVARCHAR) String repparam);

	@StoredProcedure(name = "as_edge_scheduleemail_create")
	void as_edge_scheduleemail_create(int ScheduleID,
			@In(jdbcType = Types.NVARCHAR) String SenderName,
			@In(jdbcType = Types.NVARCHAR) String FromAddress,
			@In(jdbcType = Types.NVARCHAR) String ToAddresses,
			@In(jdbcType = Types.NVARCHAR) String CCAddresses, int Priority,
			@In(jdbcType = Types.NVARCHAR) String MailSubject,
			@In(jdbcType = Types.NVARCHAR) String MailComment,
			int Attachment,
			@Out(jdbcType = Types.INTEGER) int[] id);
	
	@StoredProcedure(name = "as_edge_scheduleemail_update")
	void as_edge_scheduleemail_update(int ScheduleID,
			@In(jdbcType = Types.NVARCHAR) String SenderName,
			@In(jdbcType = Types.NVARCHAR) String FromAddress,
			@In(jdbcType = Types.NVARCHAR) String ToAddresses,
			@In(jdbcType = Types.NVARCHAR) String CCAddresses, int Priority,
			@In(jdbcType = Types.NVARCHAR) String MailSubject,
			@In(jdbcType = Types.NVARCHAR) String MailComment,
			int Attachment);

	@StoredProcedure(name = "as_edge_schedulelog_insert")
	void as_edge_schedulelog_insert(
	                              int UserID,   
	                              int Level,   
	                              @In(jdbcType = Types.NVARCHAR) String Source,   
	                              @In(jdbcType = Types.TIMESTAMP)Date Time,
	                              @In(jdbcType = Types.NVARCHAR) String Message);
	                              
	@StoredProcedure(name = "as_edge_emailreport_remove")
	void as_edge_emailreport_remove(int ID);
	
	@StoredProcedure(name = "as_edge_emailreport_remove_by_emailid")
	void as_edge_emailreport_remove_by_emailid(int emailID);

	@StoredProcedure(name = "as_edge_schedule_remove")
	void as_edge_schedule_remove(int ID);

	@StoredProcedure(name = "as_edge_scheduleemail_remove")
	void as_edge_scheduleemail_remove(int ID);
	
	@StoredProcedure(name = "as_edge_scheduleemail_remove_by_scheduleid")
	void as_edge_scheduleemail_remove_by_scheduleid(int scheduleID);
	
	@StoredProcedure(name = "as_edge_schedulelog_remove")
	void as_edge_schedulelog_remove(int ID);

	
}
