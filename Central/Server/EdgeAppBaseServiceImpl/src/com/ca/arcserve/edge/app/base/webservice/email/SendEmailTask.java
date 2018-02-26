package com.ca.arcserve.edge.app.base.webservice.email;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.schedulers.IEdgeTaskItem;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.SyncASBUActivityLog;

public class SendEmailTask implements IEdgeTaskItem{
	private static Logger _log = Logger.getLogger(SendEmailTask.class);
	private static String Class_Name = SendEmailTask.class.getSimpleName();
	EmailSender mSender = null;
	boolean mIsHtml = false;
	String mHost = null;
	Module runningModule = Module.Common;
	
	public Module getRunningModule() {
		return runningModule;
	}

	public void setRunningModule(Module runningModule) {
		this.runningModule = runningModule;
	}

	public void setSender(EmailSender sender) {
		mSender = sender;
	}
	
	public void setHtmlFlag(boolean flag) {
		mIsHtml = flag;
	}
	
	public void setSendHost(String host) {
		mHost = host;
	}

	
	private IEmailPostProcessor processor =null;
	public void setPostProcessor( IEmailPostProcessor _processor	) {
		processor = _processor;
	}
	public static interface IEmailPostProcessor {
		public boolean postProcess();
	}
	@Override
	public void run() {
		try {
			if (mSender != null && mSender.sendEmail(mIsHtml)) {
				_log.info(Class_Name + ": Send mail from " + mHost + " succeed.");
				//_activityLog.WriteInformation(getHostName(),"Email sent successfully.");
				//_activityLog.WriteInformation(getHostName(), EdgeCMWebServiceMessages.getResource("EDGEMAIL_PassToSend")); // To avoid overhead log data. (remove general message)
			} else {
				_log.info(Class_Name + ": Send mail from " + mHost + " failed.");
				//_activityLog.WriteWarning(getHostName(),"Failed to send email. Please check your authentication or email addresses.");
				EdgeEmailService.GetInstance().writeEmailActivityLog(
						getHostName(), 
						EdgeCMWebServiceMessages.getResource("EDGEMAIL_FailedToSend"), 
						Severity.Warning, runningModule, 0);
			}
		} catch(Exception e) {
			_log.debug( Class_Name + ": " + e.getMessage());
		}
		finally {
			if( this.processor!=null ){
				processor.postProcess();
				///you should never reuse a SendEmailTask object and postProcessor
				processor = null;
			}
		}
	}
	
	public String getHostName()
	{
		String hostname="";
		try {
		    InetAddress addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		} catch (UnknownHostException e) {

		}
		return hostname.toUpperCase();
	}

}
