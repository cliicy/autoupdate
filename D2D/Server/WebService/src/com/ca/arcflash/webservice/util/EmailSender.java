package com.ca.arcflash.webservice.util;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Proxy.Type;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.ReadOnlyFolderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.replication.CAProxy;
import com.ca.arcflash.rps.webservice.replication.CAProxySelector;
import com.ca.arcflash.rps.webservice.replication.HttpProxy;
import com.ca.arcflash.service.common.FlashSwitch;
import com.ca.arcflash.service.common.FlashSwitchDefine;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.nimsoft.NimsoftService;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.MailSSLSocketFactory;


/**
 * @Fixme because this class modifies the system properties when sending email, so it may influence other network behavior.
 * the best method is to design own mail socket factory to use independent proxy for mail socket and authenticator
 * @author Administrator
 *
 */
public class EmailSender {
	/**
	 * gmail use smtp tls port 25 , or use smtps port 465
	 *
	 */
//	public static final String MAILPassword = "";
//	public static void sendLiveNoProxy_test() {
//		EmailSender em = new EmailSender();
//		em.setContent("This is content");
//		em.setFromAddress("gongys@hotmail.com");
//		em.setEnableProxy(false);
//		em.setMailAuth(true);
//		em.setMailPassword(MAILPassword);
//		em.setMailUser("gongys@hotmail.com");
//		em.setRecipients(new String[] { "gong_ys2004@sohu.com" });
//		em.setSmptPort(25);
//		em.setSubject("this is from emailCA");
//		em.setSMTP("smtp.live.com");
//		em.setUseSsl(false);
//		em.setUseTls(true);
//		em.sendEmail(false);
//	}
//
//	public static void sendLiveWithProxy_test() {
//		EmailSender em = new EmailSender();
//		em.setContent("This is content");
//		em.setFromAddress("gongys@hotmail.com");
//		em.setEnableProxy(false);
//		em.setMailAuth(true);
//		em.setMailPassword(MAILPassword);
//		em.setMailUser("gongys@hotmail.com");
//		em.setRecipients(new String[] { "gong_ys2004@sohu.com" });
//		em.setSmptPort(25);
//		em.setSubject("this is from emailCA proxy");
//		em.setSMTP("smtp.live.com");
//		em.setProxyAddress("127.0.0.1");
//		em.setEnableProxy(true);
//		em.setProxyAuth(false);
//		em.setProxyPort(1080);
//		em.setProxyUsername("gongys");
//		em.setProxyPassword("123456");
//		em.setUseSsl(false);
//		em.setUseTls(true);
//		em.sendEmail(false);
//	}
//
//	/**
//	 * gmail use smtp tls port 25 , or use smtps port 465
//	 *
//	 */
//	public static void sendGoogleNoProxy_test() {
//		EmailSender em = new EmailSender();
//		em.setContent("This is content");
//		em.setFromAddress("gongys2004@gmail.com");
//		em.setEnableProxy(false);
//		em.setMailAuth(true);
//		em.setMailPassword(MAILPassword);
//		em.setMailUser("gongys2004@gmail.com");
//		em.setRecipients(new String[] { "gong_ys2004@sohu.com" });
//		em.setSmptPort(465);
//		em.setSubject("this is from emailCA");
//		em.setSMTP("smtp.gmail.com");
//		em.setUseSsl(true);
//		em.setUseTls(false);
//		em.sendEmail(false);
//	}
//
//	public static void sendGoogleWithProxy_test() {
//		EmailSender em = new EmailSender();
//		em.setContent("This is content");
//		em.setFromAddress("gongys2004@gmail.com");
//		em.setEnableProxy(false);
//		em.setMailAuth(true);
//		em.setMailPassword(MAILPassword);
//		em.setMailUser("gongys2004@gmail.com");
//		em.setRecipients(new String[] { "gong_ys2004@sohu.com" });
//		em.setSmptPort(465);
//		em.setSubject("this is from emailCA");
//		em.setSMTP("smtp.gmail.com");
//		em.setProxyAddress("127.0.0.1");
//		em.setEnableProxy(true);
//		em.setProxyAuth(false);
//		em.setProxyPort(1080);
//		em.setProxyUsername("gongys");
//		em.setProxyPassword("123456");
//		em.setUseSsl(true);
//		em.setUseTls(false);
//		em.sendEmail(false);
//	}
//
//	public static void sendYahooNoProxy_test() {
//		EmailSender em = new EmailSender();
//		em.setContent("This is content");
//		em.setFromAddress("gong_ys2004@yahoo.com.cn");
//		em.setEnableProxy(false);
//		em.setMailAuth(true);
//		em.setMailPassword(MAILPassword);
//		em.setMailUser("gong_ys2004");
//		em.setRecipients(new String[] { "gong_ys2004@sohu.com" });
//		em.setSmptPort(25);
//		em.setSubject("this is from emailCA");
//		em.setSMTP("smtp.mail.yahoo.com");
//
//		em.setUseSsl(false);
//		em.setUseTls(false);
//		em.sendEmail(false);
//	}
//
//	public static void sendGoogleWithProxyPw_test() {
//		EmailSender em = new EmailSender();
//		em.setContent("This is content");
//		em.setFromAddress("gongys2004@gmail.com");
//		em.setEnableProxy(false);
//		em.setMailAuth(true);
//		em.setMailPassword(MAILPassword);
//		em.setMailUser("gongys2004@gmail.com");
//		em.setRecipients(new String[] { "gong_ys2004@sohu.com" });
//		em.setSmptPort(465);
//		em.setSubject("this is from emailCA");
//		em.setSMTP("smtp.gmail.com");
//		em.setProxyAddress("127.0.0.1");
//		em.setEnableProxy(true);
//		em.setProxyAuth(true);
//		em.setProxyPort(1080);
//		em.setProxyUsername("gongys");
//		em.setProxyPassword("123456");
//		em.setUseSsl(true);
//		em.setUseTls(false);
//		em.sendEmail(false);
//	}
//
//	public static void main(String[] args) {
//		sendGoogleWithProxyPw_test();
//		 sendLiveNoProxy_test();
//		 sendLiveWithProxy_test();
//		 sendGoogleNoProxy_test();
//		 sendGoogleWithProxy_test();
//		 sendYahooNoProxy_test();
//		 sendYahooWithProxy();
//	}
//
//	private static void sendYahooWithProxy() {
//		EmailSender em = new EmailSender();
//		em.setContent("This is content");
//		em.setFromAddress("gong_ys2004@yahoo.com.cn");
//		em.setEnableProxy(false);
//		em.setMailAuth(true);
//		em.setMailPassword(MAILPassword);
//		em.setMailUser("gong_ys2004");
//		em.setRecipients(new String[] { "gong_ys2004@sohu.com" });
//		em.setSmptPort(25);
//		em.setSubject("this is from emailCA");
//		em.setSMTP("smtp.mail.yahoo.com");
//		em.setProxyAddress("127.0.0.1");
//		em.setEnableProxy(true);
//		em.setProxyAuth(false);
//		em.setProxyPort(1080);
//		em.setProxyUsername("gongys");
//		em.setProxyPassword("123456");
//		em.setUseSsl(false);
//		em.setUseTls(false);
//		em.sendEmail(false);
//	}

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(EmailSender.class);

	private boolean enableProxy;
	private String proxyAddress;
	private int proxyPort;
	private String proxyUsername;
	@NotPrintAttribute
	private String proxyPassword;
	private String subject;
	private String content;
	private String fromAddress;
	private String[] recipients;
	private String SMTP;

	/** Alert email enhance PR */
	@NotPrintAttribute
	private String mailPassword;
	private String URL;
	private boolean useSsl = false;
	private int smptPort = 25;
	private boolean useTls = false;
	private String mailUser = "";
	private boolean proxyAuth = false;
	private boolean mailAuth = false;
	private boolean highPriority = false;	

	/**
	 * Alert message promote to Edge
	 * */
	private long jobStatus = -1; ///default ;-1, cannot use 0 as defauly value; 0 means job status ->ACTIVE; this value actually means both job status (for job releated event) or Event type defined in CommonEmailInformation.EVENT_TYPE( no job event )
	private long productType = CommonEmailInformation.PRODUCT_TYPE.NONE.getValue();
	
	private long jobType = -1; ///-1 means no correlated job from com.ca.arcflash.webservice.constants.JobType;
	private String protectedNode;

	public void setJobStatus(long js) {
		jobStatus = js;
	}

	public long getJobStatus() {
		return jobStatus;
	}

	public void setProductType(long pt) {
		productType = pt;
	}

	public long getProductType() {
		return productType;
	}	
	
	public long getJobType() {
		return jobType;
	}

	public void setJobType(long jobType) {
		this.jobType = jobType;
	}
	
	public String getProtectedNode() {
		return protectedNode;
	}

	public void setProtectedNode(String protectedNode) {
		this.protectedNode = protectedNode;
	}
	
	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

//	/**
//	 * This function will check three types Edge Application, if this D2D is managed by any Application will return true.
//	 * in uulong; the management status is useless; so don't check manage status now; 
//	 * */
//	private boolean isRegisteredInEdge() {
//		boolean isRegister = false;
//		String UUID = CommonService.getInstance().getNodeUUID();
//
//		for (ApplicationType type : ApplicationType.values()) {
//			D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
//			EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(type);
//			if(edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty()) {
//				logger.debug("isRegisteredInEdge - no registion info! type:" + type.toString() + "\n");
//				continue;
//			}
//			
//			IEdgeD2DService proxy = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeCM4D2D.class);
//			if(proxy == null) {
//				logger.debug("isRegisteredInEdge - get edge service failed! type:" + type.toString() + "\n");
//				continue;
//			}
//
//			try {
//				 proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
//				 isRegister = true;
//			}catch(EdgeServiceFault e) {
//				logger.debug("isRegisteredInEdge - expection: " + e.getMessage() + "! type:" + type.toString() + "\n");
//				continue;
//			}
//			
////			try {
////				isManaged = proxy.isManagedByEdge(UUID);
////				if (isManaged) {
////					break;
////				}
////					
////			} catch (Exception e) {
////				logger.debug("isManagedByEdge - expection: " + e.getMessage() + "! type:" + type.toString() + "\n");
////				continue;
////			}
//		}
//		
//		return isRegister;
//	}
	
	private long convertSpecailJobStatusValue(long jobStatus) {
		if(jobStatus == Constants.BackupJob_PROC_EXIT) {
			return Constants.JOBSTATUS_FINISHED;
		} else if (jobStatus == Constants.JOBSTATUS_SKIPPED) {
			return Constants.JOBSTATUS_MISSED;
		} else {
			return jobStatus;
		}
	}
	
	private void promoteAlertMessageToEdgeServer() {
		List<CommonEmailInformation> ceiList = new ArrayList<CommonEmailInformation>();
		CommonEmailInformation cei = new CommonEmailInformation();
		cei.setEventType( convertSpecailJobStatusValue(jobStatus) | productType);
		cei.setProductType(productType);
		cei.setSendhost(ServiceContext.getInstance().getLocalMachineName());
		cei.setContent(content);
		cei.setSendViaEdge(false);
		cei.setSendTime(Calendar.getInstance().getTime());
		cei.setSubject(subject);
		cei.setJobType(jobType);
		if( !StringUtil.isEmptyOrNull(protectedNode) ) {
			cei.setProtectedNode(protectedNode);
		}
		else {
			cei.setProtectedNode( cei.getSendhost() );
		}
		ceiList.add(cei);
		
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		
		String edgeWSDL = edgeReg.GetEdgeWSDL();
		if ( edgeWSDL != null && edgeWSDL.length() > 0 ) {
			IEdgeD2DService proxy =  WebServiceFactory.getEdgeService(edgeWSDL,IEdgeCM4D2D.class);
			if(proxy == null) {
				logger.debug("promoteAlertMessageToEdgeServer - Failed to get proxy handle!!\n");
			} else {
				try {
					if (0 == proxy.validateUserByUUID(edgeReg.GetEdgeUUID())) {
						logger.info("Email -- start save alert with send_host = " + cei.getSendhost()+ " protectedNode: " + cei.getProtectedNode()  
								+" raw_event_type: " + cei.getEventType() +" time: " + cei.getSendTime().toString() );
						proxy.promoteEmailToEdge(ceiList);
					}
					
					logger.debug("promote alert message to edge server succeed!\n");
				} catch (EdgeServiceFault e) {
//					e.printStackTrace();
					logger.debug("promote alert message to edge server failed!\n" + e.getMessage());
				}
			}
		}
	}
	
	private void sendAlertToNimsoft(){
		if(NimsoftService.getInstance().isRegister())
			NimsoftService.getInstance().addAlertMessage(subject,highPriority);
	}


	public boolean isProxyAuth() {
		return proxyAuth;
	}

	public void setProxyAuth(boolean proxyAuth) {
		this.proxyAuth = proxyAuth;
	}

	public boolean isMailAuth() {
		return mailAuth;
	}

	public void setMailAuth(boolean mailAuth) {
		this.mailAuth = mailAuth;
	}

	public boolean isUseTls() {
		return useTls;
	}

	public void setUseTls(boolean useTls) {
		this.useTls = useTls;
	}

	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public boolean isUseSsl() {
		return useSsl;
	}

	public void setUseSsl(boolean useSsl) {
		this.useSsl = useSsl;
	}

	public int getSmptPort() {
		return smptPort;
	}

	public void setSmptPort(int smptPort) {
		this.smptPort = smptPort;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 *
	 * @param mailPassword
	 */
	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public void sendEmail(boolean isHtml) {
		NativeFacade nativeFacade = CommonService.getInstance().getNativeFacade();
		try {
            sendMail(isHtml);
		}catch(Exception e) {
            logger.error("Send email failed", e);
            nativeFacade.addLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,new String[]{WebServiceMessages.getResource("EmailSendFailure", e.getMessage()),"","","",""});
     	}
    	promoteAlertMessageToEdgeServer();
		sendAlertToNimsoft();
	}
	
	public void sendEmail( BackupEmail email, String subject, String content, 
			int jobStatus, boolean priority, long productType, long jobType, String protectedNode ) {
		setHighPriority(priority);
		setSubject(subject);
		/** for promoting alert message  to edge server */
		setJobStatus(jobStatus);
		setProductType(productType);
		setJobType(jobType);
		setProtectedNode(protectedNode);
		/** for email alert PR */
		setContent(content);
		//"To log on the server to make changes or fix job settings - click here. You can also submit a backup now"
		/** Alert email ehan PR */
		setUseSsl(email.isEnableSsl());
		setSmptPort(email.getSmtpPort());
		setMailPassword(email.getMailPassword());
		setMailUser(email.getMailUser());
		setUseTls(email.isEnableTls());
		setProxyAuth(email.isProxyAuth());
		setMailAuth(email.isMailAuth());

		setFromAddress(email.getFromAddress());
		setRecipients(email.getRecipientsAsArray());
		setSMTP(email.getSmtp());
		setEnableProxy(email.isEnableProxy());
		setProxyAddress(email.getProxyAddress());
		setProxyPort(email.getProxyPort());
		setProxyUsername(email.getProxyUsername());
		setProxyPassword(email.getProxyPassword());

		sendEmail(email.isEnableHTMLFormat());
	}
	
	private boolean isTimeoutEnable() {
		int defaultValue = 0;
		int value = FlashSwitch
		.getSwitchIntFromReg(
				FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE_DISABLE_TIMEOUT,
				defaultValue,
				FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE);
		
		if(value == 0)
			return true;
		return false;
	}
	
	private String DEFAULT_TIME_OUT_MS = "300000"; //30 seconds
	private String getTimeoutMS() {
		String defaultValue = DEFAULT_TIME_OUT_MS;
		String value = FlashSwitch
				.getSwitchStringFromReg(
						FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE_TIMEOUT_MS,
						defaultValue,
						FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE);
		return StringUtil.isEmptyOrNull(value) ? defaultValue : value;
	}
	
	private String getConnTimeoutMS() {
		String defaultValue = DEFAULT_TIME_OUT_MS;
		String value = FlashSwitch
				.getSwitchStringFromReg(
						FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE_CON_TIMEOUT_MS,
						defaultValue,
						FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE);
		return StringUtil.isEmptyOrNull(value) ? defaultValue : value;
	}
	
	private String getWriteTimeoutMS() {
		String defaultValue = DEFAULT_TIME_OUT_MS;
		String value = FlashSwitch
				.getSwitchStringFromReg(
						FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE_WRITE_TIMEOUT_MS,
						defaultValue,
						FlashSwitchDefine.EMailHelperModule.SWT_EMAIL_HELPER_MODULE);
		return StringUtil.isEmptyOrNull(value) ? defaultValue : value;
	}


	private boolean sendMail(boolean isHtml) throws Exception {
		logger.debug("sendEmail() - start");
		if (logger.isDebugEnabled()) {
			logger.debug(StringUtil.convertObject2String(this));
		}
		
		CAProxy caProxy = new CAProxy();
		HttpProxy proxy = new HttpProxy(proxyAddress, proxyPort, proxyUsername, proxyPassword);				
		proxy.setProxyRequiresAuth(proxyAuth);
		caProxy.setTargetHost(SMTP);
		caProxy.setProxyType(Type.SOCKS);
		caProxy.setHttpProxy(proxy);
		
		//System.setProperty("mail.mime.charset", "UTF-8");
		synchronized (EmailSender.class) {
			if (enableProxy) {
				/*Properties p = System.getProperties();
				p.setProperty("proxySet", "true");
				p.setProperty("socksProxyHost", proxyAddress);
				p.setProperty("socksProxyPort", String.valueOf(proxyPort));*/
				
				CAProxySelector.getInstance().registryProxy(caProxy);
				
				/*if (proxyAuth) {

					Authenticator a = new Authenticator(){
					@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							PasswordAuthentication p = new PasswordAuthentication(proxyUsername,proxyPassword.toCharArray());
							return p;
						}};
					Authenticator.setDefault(a);
				}*/
			}

			Session session = null;
			Properties props = new Properties();
			String prot = "smtp";

			if (this.isUseSsl()) {
      	prot = "smtps";
      	props.put("mail.smtp.ssl.trust", "*"); // trust all certificates
      }
			

			props.put("mail." + prot + ".host", SMTP);
			props.put("mail." + prot + ".port", "" + this.getSmptPort());
			props.put("mail." + prot + ".sendpartial", true);	
			if (isTimeoutEnable()) {
				props.put("mail." + prot + ".connectiontimeout", getConnTimeoutMS());
				props.put("mail." + prot + ".timeout", getTimeoutMS());
				props.put("mail." + prot + ".writetimeout", getWriteTimeoutMS());
			}

			if (this.isUseTls()){
				props.put("mail." + prot + ".starttls.enable", "true");
				props.put("mail.smtp.ssl.trust", "*");  // trust all certificates
			}
			
			if (mailAuth) {
				props.put("mail." + prot + ".auth", "true");

			}

			session = Session.getInstance(props, null);
		//	session.setDebug(true);

			// create a message
			Message msg = new MimeMessage(session);

			// set the from and to address
			InternetAddress addressFrom;
			try {
				addressFrom = new InternetAddress(fromAddress);
				msg.setFrom(addressFrom);

				InternetAddress[] addressTo = new InternetAddress[recipients.length];
				for (int i = 0; i < recipients.length; i++) {
					addressTo[i] = new InternetAddress(recipients[i]);
				}
				
				msg.setRecipients(Message.RecipientType.TO, addressTo);
				
				if(highPriority) {
					msg.setHeader("X-Priority","1");
					msg.setHeader("Priority","Urgent");
					msg.setHeader("Importance","high");
				}

				// Setting the Subject and Content Type
				if(!subject.isEmpty())
					subject.trim();
				msg.setSubject(MimeUtility.encodeText( subject,"utf-8","B"));

				logger.debug(content);

				MimeMultipart multipart = new MimeMultipart("alternative");
				MimeBodyPart tp = new MimeBodyPart();
				if (isHtml)
					tp.setText(content, "utf-8", "html");
				else
				{
					tp.setContent(content, "text/plain; charset=utf-8");
				}
				multipart.addBodyPart(tp);
				msg.setContent(multipart);
                msg.setSentDate(new Date());
				// Transport.send(msg);

				SMTPTransport t = (SMTPTransport) session.getTransport(prot);
				try {

					if (mailAuth)						
					try {
						t.connect(SMTP, this.getMailUser(), this.mailPassword);
					}
					/*fix issue 125465; this issue happens only if javamail 1.4.3 package is selected as reference library; if we use 1.4.0; this issue disappear
					   the reason is some mail server( such as mai.ca.com ) return a AUTH head ( this head is a flag means server need authentication) but the head has 
					   no value ( actually , a valid auth head should has values such as "login/plain text/md5" mark the username/password transport pattern ). 
					   mail 1.4.0 process this scenario as server doesn't actually support auth so it ignore authentication but mail 1.4.3 take it as a error
					   so, if this error happens; we manually ignore it and launch a no-auth connect.
					*/
					catch( AuthenticationFailedException e ) {
						String auth ="AUTH";		
						if( !t.supportsExtension(auth) ||t.getExtensionParameter(auth)==null|| t.getExtensionParameter(auth)=="" ) {
							props.put("mail." + prot + ".auth", "false");
							t.connect();
						}
						else {
							throw e;
						}
					}
					else
						t.connect();
					t.sendMessage(msg, msg.getAllRecipients());
				} finally {
					t.close();
				}

			} catch (AddressException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			} finally {
				/*if (enableProxy) {
					Properties p = System.getProperties();
					p.remove("proxySet");
					p.remove("socksProxyHost");
					p.remove("socksProxyPort");
					if(this.proxyAuth)
						Authenticator.setDefault(null);

//					p.remove("java.net.socks.username");
//					p.remove("java.net.socks.password");
				}*/
				
				CAProxySelector.getInstance().unRegistryProxy(caProxy);
			}
		}
		logger.debug("sendEmail() - end");
		return true;
	}
	
	public boolean sendTestMail(BackupEmail mailConf) throws ServiceException {
		try {
			StringBuilder sb = new StringBuilder(mailConf.getSubject());
			sb.append(" (");
			sb.append(InetAddress.getLocalHost().getHostName().toUpperCase());
			sb.append(")");
			setSubject(sb.toString());
			setContent(mailConf.getContent());
			setUseSsl(mailConf.isEnableSsl());
			setSmptPort(mailConf.getSmtpPort());
			setMailPassword(mailConf.getMailPassword());
			setMailUser(mailConf.getMailUser());
			setUseTls(mailConf.isEnableTls());
			setProxyAuth(mailConf.isProxyAuth());
			setMailAuth(mailConf.isMailAuth());
	
			setFromAddress(mailConf.getFromAddress());
			setRecipients(mailConf.getRecipientsAsArray());
			setSMTP(mailConf.getSmtp());
			setEnableProxy(mailConf.isEnableProxy());
			setProxyAddress(mailConf.getProxyAddress());
			setProxyPort(mailConf.getProxyPort());
			setProxyUsername(mailConf.getProxyUsername());
			setProxyPassword(mailConf.getProxyPassword());
		
			sendMail(false);
		}catch(Exception e) {
			String msg = "";
			if(e instanceof ReadOnlyFolderException) {
				msg = WebServiceMessages.getResource("sendMailFolderError");
			}else if(e instanceof AuthenticationFailedException) {
				msg = WebServiceMessages.getResource("sendMailUserError");
			}else if(e instanceof SendFailedException) {
				StringBuilder message = new StringBuilder();
				SendFailedException se = (SendFailedException)e;
				if(se.getNextException() != null && (se.getNextException() instanceof SMTPAddressFailedException)){
					if(se.getInvalidAddresses() != null
							&& se.getInvalidAddresses().length > 0){						
						for(Address ad : se.getInvalidAddresses()) {
							message.append(ad);
							message.append(",");
						}
						message.deleteCharAt(message.length() - 1);					
					}
					msg = WebServiceMessages.getResource("sendMailRecError", message.toString());
				}
			}else if(e instanceof AddressException) {
				msg = WebServiceMessages.getResource("sendMailFromError");
			}else if(e instanceof MessagingException) {
				MessagingException me = (MessagingException)e;
				if(me.getNextException() != null && (me.getNextException() instanceof UnknownHostException)) {
					msg = WebServiceMessages.getResource("sendMailHostError", mailConf.getSmtp());
				}else if(me.getNextException() != null && (me.getNextException() instanceof ConnectException)) {
					msg = WebServiceMessages.getResource("sendMailPortError", mailConf.getSmtp(), 
							String.valueOf(mailConf.getSmtpPort()));
				}else if(me.getNextException() != null && (me.getNextException() instanceof SocketException)) {
					if(mailConf.isEnableProxy()){
						msg = WebServiceMessages.getResource("sendMailProxyError", mailConf.getSmtp(), 
								String.valueOf(mailConf.getSmtpPort()),
								mailConf.getProxyAddress());
					}else
						msg = WebServiceMessages.getResource("sendMailPortError", mailConf.getSmtp(), 
								String.valueOf(mailConf.getSmtpPort()));
				}
			}
			if(msg.isEmpty()) {
				msg = e.getLocalizedMessage();
			}
			throw new ServiceException(msg, FlashServiceErrorCode.Common_SendTestMail_Failure);
		}
		
		return true;
	}
	
	public String getSMTP() {
		return SMTP;
	}

	public void setSMTP(String sMTP) {
		SMTP = sMTP;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}

	public String getProxyAddress() {
		return proxyAddress;
	}

	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}


	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}
}
