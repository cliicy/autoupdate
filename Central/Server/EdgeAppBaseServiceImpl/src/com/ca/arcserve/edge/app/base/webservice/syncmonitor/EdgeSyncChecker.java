package com.ca.arcserve.edge.app.base.webservice.syncmonitor;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.LogUtility;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.email.EmailTemplateFeature;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.SyncStatus;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.EdgeSyncHistory;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;


public class EdgeSyncChecker  {
	
	public EdgeSyncChecker(IEdgeSyncMonitorConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public void bindHost(EdgeHost host, Set<EdgeSyncComponents> components) {
		this.host = host;
		this.components = components;
	}
	
	
	/**
	 * @return the lastSendAlertTime
	 */
	public Calendar getLastSendAlertTime() {
		return lastSendAlertTime;
	}

	/**
	 * @param lastSendAlertTime the lastPingTime to set
	 */
	public void setLastSendAlertTime(Calendar lastSendAlertTime) {
		this.lastSendAlertTime = lastSendAlertTime;
	}

	
	public void checkStatus() {
		// try to ping the target host, if failed, return directly
		/*if (!pingIt()) {
			if (isOverDay(lastPingTime)) {
				for (EdgeSyncComponents component : components) {
					raiseAlert(component);
				}
			}
		} else {*/
			
			_log.debug("[EdgeSyncChecker] ping host succeed : " + host.getRhostname());
			
			// if succeed, update last ping succeed time point,
			// and continue to check sync history
			//lastPingTime = checkingPoint;
			
			for (EdgeSyncComponents component : components) {
				checkSyncHistory(component);
			}
		/*}*/
		
	}
	
	public boolean isOverDay(Calendar lastUpdate) {
		Calendar c = Calendar.getInstance();
		//checkingPoint = c;
		c.add(Calendar.SECOND, 0-(configuration.getVerifyPeriod()*24*3600));
		
		long m1 = c.getTimeInMillis();
		long m2 = lastUpdate.getTimeInMillis();
		
		long diff = m1 - m2;
		overDays = (int) (diff / (24 * 60 * 60 * 1000)) + configuration.getVerifyPeriod();
		
		_log.debug("[EdgeSyncChecker] compare date " + c.getTime().toLocaleString() + " to " + lastUpdate.getTime().toLocaleString());
		
		return c.getTime().after(lastUpdate.getTime()); 
	}
	
	public void checkSyncHistory(EdgeSyncComponents component) {
		
		try {
			// select out the sync history from database
			EdgeSyncHistory syncHistory = new EdgeSyncHistory();
			int branchId = host.getRhostid();
			long[] lastCacheId = new long[1];
			SyncStatus[] status = new SyncStatus[1];
			Date[] lastUpdate = new Date[1];
			int returnCode = syncHistory.GetSyncHistoryStatusEx(component,
					branchId, lastCacheId, status, lastUpdate);
			Calendar c = Calendar.getInstance();

			switch (returnCode) {
			case 0: // Read sync history succeed.
				c.setTime(lastUpdate[0]);
				// if the last sync failed time older than verify days
				if (isOverDay(c)) {
					raiseAlert(component);
				}
				break;
			case 1:
				// Log error
				_log.debug("[EdgeSyncChecker] get sync history failed."
						+ component.toString() + "," + branchId);
				break;

			case 100:
				// Log no record, is reachable, but over the verify days
				c.setTime(host.getLastupdated());
				if (isOverDay(c)) {
					raiseAlert(component);
				}
				break;

			default:
				// Log unknown error
				_log.debug("[EdgeSyncChecker] unknown error happened when query sync history, code:"
						+ returnCode);
				break;
			}
		} catch (Exception e) {
			_log.debug("[EdgeSyncChecker] exception throw in the checking," + e.getMessage());
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void raiseAlert(EdgeSyncComponents component) {
		Calendar current = Calendar.getInstance();
		if (lastSendAlertTime == null 
			|| !(lastSendAlertTime.getTime().getYear() == current.getTime().getYear()
				&& lastSendAlertTime.getTime().getMonth() == current.getTime().getMonth()
				&& lastSendAlertTime.getTime().getDate() == current.getTime().getDate())) {
		

			EdgeSyncAlert alert = new EdgeSyncAlert();
			alert.setHostName(host.getRhostname());
			alert.setOverDays(overDays);
			alert.setComponent(component);
			alert.RaiseAlert();
			sendEmail(alert);
			
			lastSendAlertTime = current;
		}
	}
	
	public void sendEmail(EdgeSyncAlert alert) {
		if (configuration.getSyncAlertEmailSendFlag()) {
			EdgeEmailService emailService = EdgeEmailService.GetInstance();
			
			String syncAlertSubject ="";
			try {
				ConfigurationServiceImpl configurationServiceImpl = new ConfigurationServiceImpl();
				EmailTemplateSetting emailTemplate = configurationServiceImpl.getEmailTemplateSetting( EmailTemplateFeature.D2DPolicy);
				if (emailTemplate==null) {
					syncAlertSubject =  EdgeCMWebServiceMessages.getResource("EDGEMAIL_ALERT_SUBJECT");
				} else {
					syncAlertSubject =  emailTemplate.getSubject();
				}
				syncAlertSubject = syncAlertSubject + ": " + EdgeCMWebServiceMessages.getMessage("EDGEMAIL_SYNC_SUBJECT");
			} catch (EdgeServiceFault e) {
				_log.error("faield set sync alert subject");
			}
			
			
			emailService.SendMailWithGlobalSetting(alert.getHostName(),///hostname is same as protected node in CommonEmailInformation
					syncAlertSubject, alert.getContent(), EmailTemplateFeature.Report);
			_log.debug("[EdgeSyncChecker] send the sync failed alert via email.");
		}
	}
	
	
/*	public boolean pingIt() {
		boolean pingResult = false;
		final int defaultPingTimeout = 3000;
		
		try {
			if (host.getIpaddress().equalsIgnoreCase(EdgeHost.DEF_IPADDRESS)) { 
				InetAddress address = InetAddress.getByName(host.getRhostname());
				pingResult = address.isReachable(defaultPingTimeout);
			}
			
			if (!pingResult) {
				InetAddress address = InetAddress.getByName(host.getIpaddress());
				pingResult = address.isReachable(defaultPingTimeout);
			}
			
			if (!pingResult) {
				_log.debug("[EdgeSyncChecker] ping host failed : " + host.getRhostname());
			}

			return pingResult;
		} catch (IOException e) {
			_log.debug("[EdgeSyncChecker] ping host " + host.getRhostname() + " exception. " + e.getMessage());
			return pingResult;
		}
	}
	*/
	
	private IEdgeSyncMonitorConfiguration configuration = null;
	private EdgeHost host = null;
	private Set<EdgeSyncComponents> components = null;
	private Calendar lastSendAlertTime = null;
	//private Calendar checkingPoint = null;
	private int overDays = 0;
	private static Logger _log = Logger.getLogger(EdgeSyncChecker.class);
		
}
