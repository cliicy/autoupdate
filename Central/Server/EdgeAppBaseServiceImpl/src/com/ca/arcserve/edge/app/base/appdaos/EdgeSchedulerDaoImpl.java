package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.DaoException;

public class EdgeSchedulerDaoImpl implements IEdgeSchedulerDao {
/*	private void BeginTrans(Connection conn) {
		try {
			conn.setAutoCommit(false);
			conn.setSavepoint();
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(),e);
		}
	}
	
	private void CommitTrans(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			throw new DaoException(e.getMessage(),e);
		}
	}*/
	
	@Override
	public void as_edge_emailreport_create(int EmailID, int ReportID,
			String ReportParam, int[] id) {
		// TODO Auto-generated method stub
		String sqlStr = "INSERT INTO as_edge_emailreport (EmailID, ReportID, ReportParam) "
					+	"VALUES(" + EmailID + ", " + ReportID + ",?) ";
		String sqlStrGetID = "SELECT id from as_edge_emailreport where EmailID = " + EmailID;
	
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
	
		try {
			ede.BeginTrans();
			pa.add(new String(ReportParam));
			ede.ExecuteDao(sqlStr, pa);
			
			List<Integer> result = new ArrayList<Integer>();
			ede.ExecuteDao(sqlStrGetID, null, result, 0);
			ede.CommitTrans();
			
			if(!result.isEmpty())
				id[0] = result.get(0);
		}catch(Exception e){
			ede.RollbackTrans();
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_emailreport_list(int ID,
			List<EdgeScheduler_EmailReport> Reports) {
		// TODO Auto-generated method stub
		String sqlStr ="";
		if(ID == 0)
			sqlStr = "SELECT ID,EmailID,ReportID,ReportParam FROM as_edge_emailreport ORDER BY ID";
		else
			sqlStr = "SELECT ID,EmailID,ReportID,ReportParam FROM as_edge_emailreport WHERE ID = " + ID + " ORDER BY ID";
	
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		try {
			ede.ExecuteDao(sqlStr, null, EdgeScheduler_EmailReport.class, Reports);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_emailreport_list_by_emailid(int emailID,
			List<EdgeScheduler_EmailReport> Reports) {
		// TODO Auto-generated method stub
		String sqlStr = "";
		if(emailID == 0)
			sqlStr = "SELECT ID,EmailID,ReportID,ReportParam FROM as_edge_emailreport ORDER BY ID";
		else
			sqlStr = "SELECT ID,EmailID,ReportID,ReportParam FROM as_edge_emailreport WHERE EmailID = " + emailID + " ORDER BY ID";
	
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		try {
			ede.ExecuteDao(sqlStr, null, EdgeScheduler_EmailReport.class, Reports);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_emailreport_remove(int ID) {
		// TODO Auto-generated method stub
		String sqlStr = "DELETE FROM as_edge_emailreport WHERE ID = " + ID;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		try {
			ede.ExecuteDao(sqlStr, null);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_emailreport_remove_by_emailid(int emailID) {
		// TODO Auto-generated method stub
		String sqlStr = "DELETE FROM as_edge_emailreport WHERE EmailID = " + emailID;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		try {
			ede.ExecuteDao(sqlStr, null);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_emailreport_update(int EmailID, int ReportID,
			String ReportParam) {
		// TODO Auto-generated method stub
		String sqlStrQuery  = "SELECT ID from as_edge_emailreport WHERE EmailID = " + EmailID;
		String sqlStrUpdate = "	UPDATE as_edge_emailreport "
			+"	   SET ReportID = " + ReportID + " "
			+"	      ,ReportParam = ? "
			+"	 WHERE EmailID = " + EmailID + " ";
		String sqlStrInsert = "	INSERT INTO as_edge_emailreport "
			+"	           (EmailID "
			+"	           ,ReportID "
			+"	           ,ReportParam) "
			+"	     VALUES (" + EmailID + ", " + ReportID + ", ?) ";
		
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		try {
			List<Integer> result = new ArrayList<Integer>();
			ede.BeginTrans();
			pa.add(new String(ReportParam));
			ede.ExecuteDao(sqlStrQuery, null, result, 0);
			if(result.isEmpty())
				ede.ExecuteDao(sqlStrInsert, pa);
			else
				ede.ExecuteDao(sqlStrUpdate, pa);
			ede.CommitTrans();
		}catch(Exception e){
			ede.RollbackTrans();
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedule_create(String name, String desc, int acttype,
			int schedtype, String schedparam, Date actiontime, Date repfrom,
			int reptype, String repparam, Date lastactiontime, int actedtimes,
			int userid, int[] id) {
		// TODO Auto-generated method stub
		String sqlStrLockTable = "lock table as_edge_schedule in exclusive mode";
		String sqlStrInsert = "INSERT INTO as_edge_schedule (Name,Description,ActionType,ScheduleType, "
					+	"ScheduleParam,ActionTime,RepeatFrom,RepeatType,RepeatParam,LastActionTime, "
					+	"ActedTimes,UserID,CreatedAt,LastModifiedAt) "
					+	"VALUES(?, ?, " + acttype + ", " + schedtype + ", ?, "
					+	"?, ?, " + reptype + ", ?, ?, " + actedtimes + ", " + userid + ", "
					+	"GetCurrentUTCTime(), GetCurrentUTCTime())";
		String sqlStrGetID = "SELECT max(id) from as_edge_schedule";
	
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		try {
			ede.BeginTrans();
			ede.ExecuteDao(sqlStrLockTable, null);
			
			pa.add(new String(name));
			pa.add(new String(desc));
			pa.add(new String(schedparam));
			pa.add(new Timestamp(actiontime.getTime()));
			pa.add(new Timestamp(repfrom.getTime()));
			pa.add(new String(repparam));
			pa.add(new Timestamp(lastactiontime.getTime()));
			
			ede.ExecuteDao(sqlStrInsert, pa);
			
			List<Integer> result = new ArrayList<Integer>();
			ede.ExecuteDao(sqlStrGetID, null, result, 0);
			if(!result.isEmpty())
				id[0] = result.get(0);
			ede.CommitTrans();
		}catch(Exception e){
			ede.RollbackTrans();
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedule_list(int ID,
			List<EdgeScheduler_Schedule> Schedues) {
		// TODO Auto-generated method stub
		String sqlStr ="";
		if(ID == 0) {
			sqlStr = "SELECT ID,Name,Description,ActionType,ScheduleType,ScheduleParam "
				+	 ",ActionTime,RepeatFrom,RepeatType,RepeatParam,LastActionTime "
				+	 ",ActedTimes,UserID,CreatedAt,LastModifiedAt "
				+	 "FROM as_edge_schedule ORDER BY ID ";
		}
		else {
			sqlStr = "SELECT ID,Name,Description,ActionType,ScheduleType,ScheduleParam "
				+	 ",ActionTime,RepeatFrom,RepeatType,RepeatParam,LastActionTime "
				+	 ",ActedTimes,UserID,CreatedAt,LastModifiedAt "
				+	 "FROM as_edge_schedule WHERE ID = " + ID + " ORDER BY ID";
		}
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			ede.ExecuteDao(sqlStr, null, EdgeScheduler_Schedule.class, Schedues);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedule_remove(int ID) {
		// TODO Auto-generated method stub
		String sqlStr = "DELETE FROM as_edge_schedule WHERE ID = " +ID;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			ede.ExecuteDao(sqlStr, null);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedule_update_for_scheduler(int ID,
			Date lastactiontime, int actedtimes) {
		// TODO Auto-generated method stub
		String sqlStr = "UPDATE as_edge_schedule SET LastActionTime = ?, ActedTimes = " 
					+ 	actedtimes + " WHERE ID = " + ID;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		
		try {
			pa.add(new Timestamp(lastactiontime.getTime()));
			ede.ExecuteDao(sqlStr, pa);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedule_update_for_ui(int ID, String name,
			String desc, int acttype, int schedtype, String schedparam,
			Date actiontime, Date repfrom, int reptype, String repparam) {
		// TODO Auto-generated method stub
		String sqlStr = "UPDATE as_edge_schedule SET Name = ?, Description = ?, "
				+		"ActionType = " + acttype + ", "
				+		"ScheduleType = " + schedtype + ", "  
				+		"ScheduleParam = ?, ActionTime = ?, "
				+		"RepeatFrom = ?, RepeatType = " + reptype + ", "
				+		"RepeatParam = ? WHERE ID = " + ID;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		
		try {
			pa.add(new String(name));
			pa.add(new String(desc));
			pa.add(new String(schedparam));
			pa.add(new Timestamp(actiontime.getTime()));
			pa.add(new Timestamp(repfrom.getTime()));
			pa.add(new String(repparam));
			ede.ExecuteDao(sqlStr, pa);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_scheduleemail_create(int ScheduleID, String SenderName,
			String FromAddress, String ToAddresses, String CCAddresses,
			int Priority, String MailSubject, String MailComment, int Attachment, int[] id) {
		// TODO Auto-generated method stub
		String sqlStrLockTable = "lock table as_edge_scheduleemail in exclusive mode"; 
		String sqlStrInsert = "INSERT INTO as_edge_scheduleemail (ScheduleID, SenderName, FromAddress, "
					+	"ToAddresses,CCAddresses, Priority, MailSubject, MailComment, Attachment) "
					+	"VALUES "
					+	"(" + ScheduleID + ", ?, ?, ?, ?, "
					+	Priority + ", ?, ?, ?) ";
		String sqlStrGetID = "SELECT max(id) from as_edge_scheduleemail";
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		try {
			ede.BeginTrans();
			ede.ExecuteDao(sqlStrLockTable, null);
			
			pa.add(new String(SenderName));
			pa.add(new String(FromAddress));
			pa.add(new String(ToAddresses));
			pa.add(new String(CCAddresses));
			pa.add(new String(MailSubject));
			pa.add(new String(MailComment));
			pa.add(Attachment);
			
			ede.ExecuteDao(sqlStrInsert, pa);
			
			List<Integer> result = new ArrayList<Integer>();
			ede.ExecuteDao(sqlStrGetID, null, result, 0);
			ede.CommitTrans();
			
			if(!result.isEmpty()){
				id[0] = result.get(0);
			}
		}catch(Exception e){
			ede.RollbackTrans();
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_scheduleemail_list(int ID,
			List<EdgeScheduler_ScheduleEmail> Emails) {
		// TODO Auto-generated method stub
		String sqlStr = "";
		if(ID == 0) {
			sqlStr = "SELECT ID,ScheduleID,SenderName,FromAddress,ToAddresses, "
				+	 "CCAddresses ,Priority,MailSubject,MailComment,Attachment "
				+	 "FROM as_edge_scheduleemail ORDER BY ID ";
		}
		else {
			sqlStr = "SELECT ID,ScheduleID,SenderName,FromAddress,ToAddresses "
				+	 ",CCAddresses,Priority,MailSubject,MailComment,Attachment "
				+	 "FROM as_edge_scheduleemail WHERE ID = " + ID + " ORDER BY ID";
		}
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			ede.ExecuteDao(sqlStr, null, EdgeScheduler_ScheduleEmail.class, Emails);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_scheduleemail_list_by_scheduleid(int schID,
			List<EdgeScheduler_ScheduleEmail> Emails) {
		// TODO Auto-generated method stub
		String sqlStr = "";
		if(schID == 0) {
			sqlStr = "SELECT ID,ScheduleID,SenderName,FromAddress,ToAddresses, "
				+	 "CCAddresses,Priority,MailSubject,MailComment,Attachment "
				+	 "FROM as_edge_scheduleemail ORDER BY ID ";
		}
		else {
			sqlStr = "SELECT ID,ScheduleID,SenderName,FromAddress,ToAddresses, "
				+	 "CCAddresses,Priority,MailSubject,MailComment,Attachment "
				+	 "FROM as_edge_scheduleemail WHERE ScheduleID = " + schID + " ORDER BY ID";
		}
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			ede.ExecuteDao(sqlStr, null, EdgeScheduler_ScheduleEmail.class, Emails);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_scheduleemail_remove(int ID) {
		// TODO Auto-generated method stub
		String sqlStr = "DELETE FROM as_edge_scheduleemail WHERE ID = " + ID;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			ede.ExecuteDao(sqlStr, null);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_scheduleemail_remove_by_scheduleid(int scheduleID) {
		// TODO Auto-generated method stub
		String sqlStr = "DELETE FROM as_edge_scheduleemail WHERE ScheduleID = " + scheduleID;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			ede.ExecuteDao(sqlStr, null);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_scheduleemail_update(int ScheduleID, String SenderName,
			String FromAddress, String ToAddresses, String CCAddresses,
			int Priority, String MailSubject, String MailComment, int Attachment) {
		// TODO Auto-generated method stub
		String sqlStrQuery  = "SELECT id from as_edge_scheduleemail "
					+"			WHERE ScheduleID = " + ScheduleID;
		String sqlStrUpdate = "UPDATE as_edge_scheduleemail "
					+"	   SET ScheduleID = " + ScheduleID + " "
					+"	      ,SenderName = ? "
					+"	      ,FromAddress = ? "
					+"	      ,ToAddresses = ? "
					+"	      ,CCAddresses = ? "
					+"	      ,Priority = " + Priority + " "
					+"	      ,MailSubject = ? "
					+"	      ,MailComment = ? "
					+"	      ,Attachment  = ? "
					+"	 WHERE ScheduleID = " + ScheduleID;
		String sqlStrInsert = "INSERT INTO as_edge_scheduleemail "
					+"	           (ScheduleID "
					+"	           ,SenderName "
					+"	           ,FromAddress "
					+"	           ,ToAddresses "
					+"	           ,CCAddresses "
					+"	           ,Priority "
					+"	           ,MailSubject "
					+"	           ,MailComment "
					+"	           ,Attachment) "
					+"	     VALUES "
					+"	           (" + ScheduleID + " "
					+"	           ,? "
					+"	           ,? "
					+"	           ,? "
					+"	           ,? "
					+"	           ," + Priority + " "
					+"	           ,? "
					+"	           ,? "
					+"	           ,?) ";
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa	 = new ArrayList<Object>();
		
		try {
			List<Integer> result = new ArrayList<Integer>();
			pa.add(new String(SenderName));
			pa.add(new String(FromAddress));
			pa.add(new String(ToAddresses));
			pa.add(new String(CCAddresses));
			pa.add(new String(MailSubject));
			pa.add(new String(MailComment));
			pa.add(Attachment);
			
			ede.BeginTrans();
			ede.ExecuteDao(sqlStrQuery, null, result, 0);
			if(result.isEmpty()){
				ede.ExecuteDao(sqlStrInsert, pa);
			}else{
				ede.ExecuteDao(sqlStrUpdate, pa);
			}
			ede.CommitTrans();
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedulelog_insert(int UserID, int Level,
			String Source, Date Time, String Message) {
		// TODO Auto-generated method stub
		String sqlStr = "INSERT INTO as_edge_schedulelog "
					+	"(UserID,Level,Source,Time,Message) "
					+	"VALUES "
					+	"(" + UserID + "," + Level + ",?,?,?)";
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa	 = new ArrayList<Object>();
	
		try {
			pa.add(new String(Source));
			pa.add(new Timestamp(Time.getTime()));
			pa.add(new String(Message));
			
			ede.ExecuteDao(sqlStr, pa);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedulelog_remove(int ID) {
		// TODO Auto-generated method stub
		String sqlStr = "DELETE FROM as_edge_schedulelog WHERE ID = " + ID;
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		try {
			ede.ExecuteDao(sqlStr, null);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public void as_edge_schedules_list(String IDs,
			List<EdgeScheduler_Schedule> Schedues) {
		// TODO Auto-generated method stub
		String sqlStr = "SELECT ID,Name,Description,ActionType "
			  		+	",ScheduleType,ScheduleParam,ActionTime "
			  		+	",RepeatFrom,RepeatType,RepeatParam,LastActionTime "
			  		+	",ActedTimes,UserID,CreatedAt,LastModifiedAt "
			  		+	"FROM as_edge_schedule WHERE ID in " 
			  		+ IDs;
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			ede.ExecuteDao(sqlStr, null, EdgeScheduler_Schedule.class, Schedues);
		}catch(Exception e){
			throw new DaoException(e.getMessage(),e);
		} finally {
			ede.CloseDao();
		}
	}

}
