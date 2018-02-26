package com.ca.arcserve.edge.app.base.webservice.email;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
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
	private boolean turnOnHtmlMode = false;
	private String[] recipients;
	private String[] recipientsCC;
	private String SMTP;
	private int 	priority = 1; // 0: high, 1: normal, 2: low

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
	private MimeMultipart multipart = null;
	
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

	public boolean sendEmail(boolean isHtml){
		boolean sendStatus = true;
		try {
			sendMail(isHtml);
		}catch(Exception e) {
			logger.error("Send Email error");
			sendStatus = false;
		}
		return sendStatus;
	}
	
	public boolean sendMail(boolean isHtml) throws Exception {
		synchronized (EmailSender.class) {
			if (enableProxy) {
				Properties p = System.getProperties();
				p.setProperty("proxySet", "true");
				p.setProperty("socksProxyHost", proxyAddress);
				p.setProperty("socksProxyPort", String.valueOf(proxyPort));
				if (proxyAuth) {

					Authenticator a = new Authenticator(){
					@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							PasswordAuthentication p = new PasswordAuthentication(proxyUsername,proxyPassword.toCharArray());
							return p;
						}};
					Authenticator.setDefault(a);
//					p.setProperty("java.net.socks.username", proxyUsername);
//					p.setProperty("java.net.socks.password", proxyPassword);
				}
//				else {
//					p.remove("java.net.socks.username");
//					p.remove("java.net.socks.password");
//				}
			}

			Session session = null;
			Properties props = new Properties();
			String prot = "smtp";

			if (this.isUseSsl())
				prot = "smtps";

			props.put("mail." + prot + ".host", SMTP);
			props.put("mail." + prot + ".port", "" + this.getSmptPort());
			props.put("mail." + prot + ".sendpartial", "true");

			if (this.isUseTls()){
				props.put("mail." + prot + ".starttls.enable", "true");
				MailSSLSocketFactory sf = new MailSSLSocketFactory(); 
				sf.setTrustAllHosts(true); 
				props.put("mail.smtp.ssl.socketFactory", sf); 
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

				if ((recipients != null) && (recipients[0].compareTo("")!= 0))
				{
					InternetAddress[] addressTo = new InternetAddress[recipients.length];
					for (int i = 0; i < recipients.length; i++) {
						addressTo[i] = new InternetAddress(recipients[i]);
					}
					msg.setRecipients(Message.RecipientType.TO, addressTo);
				}

				if ((recipientsCC != null) && (recipientsCC[0].compareTo("")!= 0))
				{
					InternetAddress[] addressCC = new InternetAddress[recipientsCC.length];
					for (int i = 0; i < recipientsCC.length; i++) {
						addressCC[i] = new InternetAddress(recipientsCC[i]);
					}
					msg.setRecipients(Message.RecipientType.CC, addressCC);
				}
				
				if (priority == 0) {	// High
					 msg.setHeader("Importance", "high");
				}
				else if (priority == 2) { // Low
				   	msg.setHeader("Importance", "low");
				}

				// Setting the Subject and Content Type
//				subject = EdgeCMWebServiceMessages.getMessage("EDGEMAIL_SUBJECT_WITH_DATETIME", subject, format.format(new Date()));
				
				msg.setSubject(MimeUtility.encodeText( subject,"utf-8","B"));

				logger.debug(content);

				if (this.multipart == null){
					multipart = new MimeMultipart("alternative");
					MimeBodyPart tp = new MimeBodyPart();
					if (turnOnHtmlMode) // It is for dashboard email scheduler to enable html mode always otherwise all contents might be corrupted.
					{
						tp.setText(content, "utf-8", "html");
						turnOnHtmlMode = false; // resetting...
					}
					else
					{
						if (isHtml)
							tp.setText(content, "utf-8", "html");
						else
						{
							tp.setContent(content, "text/plain; charset=utf-8");
						}
					}
					multipart.addBodyPart(tp);
				}
				//else multipart includes content and image
				msg.setContent(multipart);
				msg.setSentDate(new Date());

				// Transport.send(msg);

				SMTPTransport t = (SMTPTransport) session.getTransport(prot);
				try {
					
					if (mailAuth) {
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
					}
					else
						t.connect();
					t.sendMessage(msg, msg.getAllRecipients());
				} finally {
					t.close();
				}

			} catch (AddressException e) {
				logger.error("sendEmail()", e);
				throw e;
			} catch (Exception e) {
				logger.error("sendEmail()", e);
				throw e;
			}finally {
				if (enableProxy) {
					Properties p = System.getProperties();
					p.remove("proxySet");
					p.remove("socksProxyHost");
					p.remove("socksProxyPort");
					if(this.proxyAuth)
						Authenticator.setDefault(null);
	
	//				p.remove("java.net.socks.username");
	//				p.remove("java.net.socks.password");
				}
			}
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
	
	public String[] getRecipientsCC() {
		return recipientsCC;
	}

	public void setRecipientsCC (String[] recipientsCC) {
		this.recipientsCC = recipientsCC;
	}
	
	public void setTurnOnHtmlMode (boolean turnOnHtmlMode)
	{
		this.turnOnHtmlMode = turnOnHtmlMode;
	}
	
	public boolean getTurnOnHtmlMode ()
	{
		return turnOnHtmlMode;
	}
	
	public void setEmailPriority (int priority)
	{
		this.priority = priority;
	}
	
	public int getEmailPriority ()
	{
		return priority;
	}

	public void setMultipart(MimeMultipart multipart) {
		this.multipart = multipart;
	}

	public MimeMultipart getMultipart() {
		return multipart;
	}
	public static void main(String[] args){
		boolean isSSL = false;
		boolean isStartTls = true;
		final String username = "xxx@gmail.com";
		final String password = "xxx";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		if(isSSL){
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.port", "465");
		}
		if(isStartTls){
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.port", "587");
		}
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new javax.mail.PasswordAuthentication(username,password);
				}
			});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("from@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
			message.setSubject("Testing Subject" + (isSSL ? " SSL" : " TSL"));
			message.setText("Dear Mail Crawler," + "\n\n No spam to my email, please!");
			Transport.send(message);
			System.out.println("Done");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
