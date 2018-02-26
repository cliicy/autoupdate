package com.ca.arcflash.ui.server;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.IDirectWebServiceImpl;
import com.ca.arcflash.common.MessageFormatEx;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.rps.webservice.RPSWebServiceClientProxy;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.server.servlet.ContextListener;
import com.ca.arcflash.ui.server.servlet.SessionConstants;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.data.VersionInfo;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

public class BaseServiceImpl extends RemoteServiceServlet {

	private static final Logger logger = Logger
			.getLogger(BaseServiceImpl.class);

	/**
	 *
	 */
	private static final long serialVersionUID = -8976248413988600354L;
	private static WebServiceClientProxy localWebServiceClient;
	private static Map<String, String> serverLocaleMap = new HashMap<String, String>();
	
	private static String productNameD2D = ContextListener.ProductNameD2D;
	private static  BusinessLogicException staticInnerError=new BusinessLogicException();
	private static  BusinessLogicException jvmOutOutMemoryError=new BusinessLogicException();
	static {

		staticInnerError.setErrorCode(FlashServiceErrorCode.Common_ErrorOccursInService);
		jvmOutOutMemoryError.setErrorCode(FlashServiceErrorCode.Common_JVMOutOfMemoryError);
		try {
			staticInnerError.setDisplayMessage(
					ResourcesReader.getResource("ServiceError_" + FlashServiceErrorCode.Common_ErrorOccursInService,
							Locale.getDefault().toLanguageTag()));
			jvmOutOutMemoryError.setDisplayMessage(
					ResourcesReader.getResource("ServiceError_" + FlashServiceErrorCode.Common_JVMOutOfMemoryError,
							Locale.getDefault().toLanguageTag()));
		} catch (Throwable e) {
			// Never run this line
			staticInnerError
					.setDisplayMessage("Unknown error code:" + FlashServiceErrorCode.Common_ErrorOccursInService);
			jvmOutOutMemoryError.setDisplayMessage("");
		}
		
		
	}
	
	protected static WebServiceClientProxy getLocalWebServiceClient() {
		return localWebServiceClient;
	}

	protected static void setLocalWebServiceClient(
			WebServiceClientProxy localWSClient) {
		localWebServiceClient = localWSClient;
	}

	protected String getServerLocale() {
		logger.debug("getServerLocale begin");
		// Firstly get web service side locale from session.
		String locale = "";
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			locale = (String) session
					.getAttribute(SessionConstants.SRING_LOCALE);

			if (locale == null) {
				logger.info("locale:" + locale + ", in Thread:"
						+ Thread.currentThread().getId() + "||"
						+ Thread.currentThread().getName());

				// Secondly try to get web service side locale from
				WebServiceClientProxy client = (WebServiceClientProxy) session
						.getAttribute(SessionConstants.SERVICE_CLIENT);

				if (client != null) {
					VersionInfo vi = client.getService().getVersionInfo();
					String wsLocale = vi.getLocale();
					logger.info("Get locale from WS wsLocale:" + wsLocale);

					if ("ja".equals(wsLocale)) {
						locale = "ja_JP";
					} else if ("fr".equals(wsLocale)) {
						locale = "fr_FR";
					} else if ("de".equals(wsLocale)) {
						locale = "de_DE";
					} else if("pt".equals(wsLocale)){
						locale = "pt_BR";
					} else if ("es".equals(wsLocale)){
						locale = "es_ES";
					} else if ("it".equals(wsLocale)){
						locale = "it_IT";
					} else if ("zh".equals(wsLocale)){
						String country = vi.getCountry();
						if(country.equalsIgnoreCase("CN") || country.equalsIgnoreCase("SG"))
							locale = "zh_CN";
						else
							locale = "zh_TW";
					}else {
						locale = "en";
					}

					serverLocaleMap.put(this.getThreadLocalRequest().getLocalName(), locale);

					logger.info("Get locale from WS:" + locale);
				}
				// get locale from WebService side again
			}
		} catch (Exception ex) {
			logger.error("ex:", ex);
		}

		// Thirdly get web service side locale from cached map if session is
		// invalidated in another concurrent thread just before get locale from
		// session..
		if (locale == null || locale.trim().length() == 0) {
			logger.info("locale:" + locale + ", in Thread:"
					+ Thread.currentThread().getId() + "||"
					+ Thread.currentThread().getName());
			String localName = this.getThreadLocalRequest().getLocalName();
			locale = serverLocaleMap.get(localName);
			logger.info("localName:" + localName);
			logger.info("cached locale:" + locale);
		}

		// Lastly, the last resort, using English as default.
		if (locale == null || locale.trim().length() == 0) {
			locale = "en";
			logger.info("using default locale:" + locale);
		}
		logger.debug("getServerLocale end, Final locale:" + locale);

		return locale;
	}
	protected String getServerLocaleForDiagnosticLogCollection() {
		logger.debug("getServerLocaleForDiagnosticLogCollection begin");
			Locale formatLocale = Locale.getDefault(Category.FORMAT);
			String country = formatLocale.getCountry();
			String language = formatLocale.getLanguage();
			
			if ("ja".equals(language)) {
				language = "ja_JP";
			} else if ("fr".equals(language)) {
				language = "fr_FR";
			} else if ("de".equals(language)) {
				language = "de_DE";
			} else if("pt".equals(language)){
				language = "pt_BR";
			} else if ("es".equals(language)){
				language = "es_ES";
			} else if ("it".equals(language)){
				language = "it_IT";
			} else if ("zh".equals(language)){
				if(country.equalsIgnoreCase("CN") || country.equalsIgnoreCase("SG"))
					language = "zh_CN";
				else
					language = "zh_TW";
			}else {
				language = "en";
			}
			return language;
	}

	protected void proccessAxisFaultException(HttpServletRequest request,WebServiceException arg_exception, boolean clearSession)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException{
		if(this.getThreadLocalRequest()==null){
			super.perThreadRequest.set(request);
		}
		proccessAxisFaultException(arg_exception,clearSession);
	}
	
	protected void proccessAxisFaultException(WebServiceException arg_exception, boolean clearSession)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try{
			logger.debug("proccessAxisFaultException(AxisFault) enter exception:",
					arg_exception);

			HttpServletRequest request = this.getThreadLocalRequest();
			HttpSession session = request.getSession();
			
			if (arg_exception.getCause()!=null &&
					(
					arg_exception.getCause() instanceof ConnectException
					|| arg_exception.getCause() instanceof SocketException
					|| arg_exception.getCause() instanceof UnknownHostException
					)
				)
			{	
				String locale = this.getServerLocale();
				if (clearSession) {
					session.invalidate();
				}
				logger.info("session.invalidate() in Thread:"
						+ Thread.currentThread().getId() + "||"
						+ Thread.currentThread().getName());
				throw new ServiceConnectException(
						FlashServiceErrorCode.Common_CantConnectService,
						MessageFormatEx.format(ResourcesReader.getResource("ServiceError_"
								+ FlashServiceErrorCode.Common_CantConnectService,
								locale), getProductName()));
			}else if (arg_exception.getCause()!=null &&  arg_exception.getCause() instanceof SocketTimeoutException) {
				logger.debug("SocketTimeoutException");
				throw generateException(FlashServiceErrorCode.Common_ServiceRequestTimeout);
			}
			if (arg_exception instanceof SOAPFaultException) {	
				SOAPFaultException exception = (SOAPFaultException) arg_exception;	
				if(exception.getFault() != null){
					String errorCode = exception.getFault().getFaultCodeAsQName().getLocalPart();
					String errorMsg = exception.getFault().getFaultString();
					if(errorCode != null){
						if (errorCode.equalsIgnoreCase("Client") && errorMsg != null
								&& errorMsg.contains("Unable to create StAX reader or writer")) {
							// server will throw this exception when request timeout
							logger.error(exception);
							for (StackTraceElement element : exception.getStackTrace()) {
								// print stack trace to know which method always time out
								logger.error("     at " + element);
							}
							throw generateException(FlashServiceErrorCode.Common_ServiceRequestTimeout);
						}
						if (errorCode.equalsIgnoreCase(AxisFault.JVMOOME_CODE)){
							logger.error("A jvm error occured on webservice",exception);
							throw jvmOutOutMemoryError;
						}
					}
				}	
				if(arg_exception instanceof ServerSOAPFaultException) {
					ServerSOAPFaultException se = (ServerSOAPFaultException)arg_exception;
					if(se.getFault() != null && se.getFault().getFaultCodeAsQName() != null && se.getMessage() != null) {  
						String m = se.getMessage();
						logger.debug(m);
						String mesg = m.replace("Client received SOAP Fault from server: ", "");
						String showMesg = mesg.replace(" Please see the server log to find more detail regarding exact cause of the failure.", "");
						String errorCode = se.getFault().getFaultCodeAsQName().getLocalPart();					
						if(!StringUtil.isEmptyOrNull(errorCode) && errorCode.equals("4294967302"))
						{
							if (clearSession) {							
								session.invalidate();
							}
						}
						throw new BusinessLogicException(errorCode,showMesg);
					}
					
				}
				
				if (exception.getFault() != null
						&& exception.getFault().getFaultCodeAsQName() != null
						//wanqi06
						&& exception.getFault().getFaultString() != null) {
					logger.warn("SOAPFaultException:"+exception.getFault().getFaultCodeAsQName().getLocalPart());
					throw new BusinessLogicException(exception.getFault().getFaultCodeAsQName().getLocalPart(), exception.getFault().getFaultString());
				}
			}
			BusinessLogicException ex = generateException(FlashServiceErrorCode.Common_ErrorOccursInService);

			logger.debug("proccessAxisFaultException(AxisFault) exit BusinessLogicException:",ex);

			throw ex;
		}catch(Error e){
			throw staticInnerError;
		}
	}
	
	protected void proccessAxisFaultException(WebServiceException arg_exception)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		proccessAxisFaultException(arg_exception, true);
	}
	
	protected void proccessAxisFaultException(HttpServletRequest request,WebServiceException arg_exception)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		proccessAxisFaultException(request,arg_exception,true);
	}
	

	protected BusinessLogicException generateException(String errorCode) {
		logger.debug("generateException(String) enter  errorCode:" + errorCode);

		// HttpSession session = this.getThreadLocalRequest().getSession();
		//
		// logger.debug("generateException.session:" + session);
		//
		// String locale = "";
		// try {
		// locale = (String) session
		// .getAttribute(SessionConstants.SRING_LOCALE);
		// } catch (Exception ex) {
		// logger.debug("generateException.locale ex:", ex);
		// locale = serverLocaleMap.get(this.getThreadLocalRequest()
		// .getLocalName());
		// }

		String locale;
		if(errorCode.compareToIgnoreCase(FlashServiceErrorCode.DiagConfig_ERR_ValidateDestFailed) == 0)
		{
			locale = this.getServerLocaleForDiagnosticLogCollection();
		}
		else
			locale = this.getServerLocale();

		logger.debug("generateException.locale:" + locale);

		String errorMessage = null;

		try{
			errorMessage = ResourcesReader.getResource("ServiceError_" + errorCode, locale);
		}catch(Exception e){
			logger.error(e);
			errorMessage="Unknown error code:"+errorCode;
		}
		BusinessLogicException ex = new BusinessLogicException(errorCode, errorMessage);

		logger.debug("generateException(String) exit BusinessLogicException:",
				ex);

		return ex;
	}

	protected WebServiceClientProxy getServiceClient() {
		return (WebServiceClientProxy) this.getThreadLocalRequest().getSession()
				.getAttribute(SessionConstants.SERVICE_CLIENT);
	}

	protected RPSWebServiceClientProxy getServiceClient_RpsService() {
		return (RPSWebServiceClientProxy) this.getThreadLocalRequest().getSession()
				.getAttribute(SessionConstants.SERVICE_CLIENT_RPS_SERVICE);
	}

	protected void setServiceClient(WebServiceClientProxy client) {
		HttpSession session = this.getThreadLocalRequest().getSession();
		serverLocaleMap.put(this.getThreadLocalRequest().getLocalName(),
				(String) session.getAttribute(SessionConstants.SRING_LOCALE));
		this.getThreadLocalRequest().getSession().setAttribute(
				SessionConstants.SERVICE_CLIENT, client);
		if(client.getService() instanceof IDirectWebServiceImpl){
			((IDirectWebServiceImpl)client.getService()).setSession(session);
		}
	}

	protected void setServiceClient(RPSWebServiceClientProxy client) {
		HttpSession session = this.getThreadLocalRequest().getSession();
		serverLocaleMap.put(this.getThreadLocalRequest().getLocalName(),
				(String) session.getAttribute(SessionConstants.SRING_LOCALE));
		this.getThreadLocalRequest().getSession().setAttribute(
				SessionConstants.SERVICE_CLIENT_RPS_SERVICE, client);
		if(client.getServiceForCPM() instanceof IDirectWebServiceImpl){
			((IDirectWebServiceImpl)client.getServiceForCPM()).setSession(session);
		}
	}

	protected void setServiceClient(HttpServletRequest req, WebServiceClientProxy client)    ///D2D Lite Integration
    {
		HttpSession session = req.getSession();
		serverLocaleMap.put(req.getLocalName(),
				(String) session.getAttribute(SessionConstants.SRING_LOCALE));
		req.getSession().setAttribute(
				SessionConstants.SERVICE_CLIENT, client);
		if(client.getService() instanceof IDirectWebServiceImpl){
			((IDirectWebServiceImpl)client.getService()).setSession(session);
		}
	}

	public synchronized static boolean isShowSocialNW() {
		boolean isShowSocialNW = true;
		logger.debug("isShowSocialNW:" + isShowSocialNW);
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String showSocialNW = registry.getValue(handle, "ShowSocialNW");
			registry.closeKey(handle);
			logger.debug("REGISTRY_ROOT:" + CommonRegistryKey.getD2DRegistryRoot());
			logger.debug("registry String KEY: ShowSocialNW, value:"
					+ showSocialNW);
			logger.debug("disableShowSocialNWStringVal:"
					+ disableShowSocialNWStringVal);
			if (StringUtil.isEmptyOrNull(showSocialNW))
				isShowSocialNW = true;
			else if (showSocialNW.equals(disableShowSocialNWStringVal)) {
				isShowSocialNW = false;
			} else {
				isShowSocialNW = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("isShowSocialNW:" + isShowSocialNW);
		return isShowSocialNW;
	}

	public static final String disableShowSocialNWStringVal = "0";
	public static final String useCASupportVideoSource = "0";

	public synchronized static Boolean isVideoSourceYouTube() {
		boolean isVideoSourceYouTube = true;
		logger.debug("isVideoSourceYouTube:" + isVideoSourceYouTube);
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String useYouTube = registry.getValue(handle, "UseVideos");
			registry.closeKey(handle);
			logger.debug("REGISTRY_ROOT:" + CommonRegistryKey.getD2DRegistryRoot());
			logger.debug("registry String KEY: VideoSourceYouTube, value:"
					+ useYouTube);

			if (StringUtil.isEmptyOrNull(useYouTube))
				return true;
			else if (useYouTube.equals(useCASupportVideoSource)) {
				isVideoSourceYouTube = false;
			} else {
				isVideoSourceYouTube = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("isVideoSourceYouTube:" + isVideoSourceYouTube);
		return isVideoSourceYouTube;
	}

	public synchronized static void setVideoSourceYouTube(boolean value) {
		logger.debug("setVideoSourceYouTube start");
		WindowsRegistry registry = new WindowsRegistry();
		int handle = 0;
		try
		{
			handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String keyValue = "1";
			if (!value) {
				keyValue = "0";
			}
			registry.setValue(handle, "VideoSourceYouTube", keyValue);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		} finally {
			if(handle != 0) {
				try {
					registry.closeKey(handle);
				}catch(Exception e) {};
			}
		}
		logger.debug("setVideoSourceYouTube end");
	}

	protected Date string2Date(String source) {
		StringTokenizer token = new StringTokenizer(source, "/-: ");
		Calendar cal = Calendar.getInstance();
		try {
			int year = Integer.parseInt(token.nextToken());
			int month = Integer.parseInt(token.nextToken()) - 1;
			int date = Integer.parseInt(token.nextToken());
			int hourOfDay = Integer.parseInt(token.nextToken());
			int minute = Integer.parseInt(token.nextToken());
			int second = Integer.parseInt(token.nextToken());
			cal.set(year, month, date, hourOfDay, minute, second);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException("Wrong date time format: "
					+ source + ", " + e.getMessage());
		}
		return cal.getTime();
	}


	public synchronized static long getMaxRecPointLimit() {
		long maxRPLimit = -1;
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String strMax = registry.getValue(handle, "MaxRPLimit");
			registry.closeKey(handle);
			logger.debug("REGISTRY_ROOT:" + CommonRegistryKey.getD2DRegistryRoot());
			logger.debug("registry String KEY: MaxRPLimit, value:"
					+ strMax);

			if (!StringUtil.isEmptyOrNull(strMax)){
				maxRPLimit = Long.parseLong(strMax);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("maxRPLimit:" + maxRPLimit);
		return maxRPLimit;
	}

	protected WebServiceClientProxy getMoniteeServiceClient() {
		return (WebServiceClientProxy) this.getThreadLocalRequest().getSession()
				.getAttribute(SessionConstants.VCM_MONITEE_CLIENT);
	}

	protected void setMoniteeServiceClient(WebServiceClientProxy client) {
		this.getThreadLocalRequest().getSession().setAttribute(
				SessionConstants.VCM_MONITEE_CLIENT, client);
	}

	protected void setCurrentMonitee(ARCFlashNode monitee) {
		this.getThreadLocalRequest().getSession(true).setAttribute(SessionConstants.SRING_SELCSMONITEE, monitee);
	}

	protected ARCFlashNode getCurrentMonitee() {
		return (ARCFlashNode)this.getThreadLocalRequest().getSession(true).getAttribute(SessionConstants.SRING_SELCSMONITEE);
	}

	//cold standby setting:Add the API to support browse the monitor server volumes
	protected WebServiceClientProxy getMonitorClientProxy() {
		return (WebServiceClientProxy) this.getThreadLocalRequest().getSession()
			.getAttribute(SessionConstants.STRING_MONITORPROXY);
	}
	//Coldstandby API end
	
	protected String getProductName() {
		if(productNameD2D == null || productNameD2D.isEmpty()) {
//			if(FlashWebServiceContext.getApplicationType() == FlashApplicationType.RPS){
//				productNameD2D = ResourcesReader.getResource("ProductNameRPS", this.getServerLocale());
//			}else
				productNameD2D = ResourcesReader.getResource("ProductNameD2D", this.getServerLocale());
		}
		return productNameD2D;
	}
	
	protected BusinessLogicException generateException(String errorCode, String messageID, Object ...params) {
		String locale = this.getServerLocale();

		logger.debug("generateException.locale:" + locale);

		String errorMessage = null;

		try{
			errorMessage = ResourcesReader.getResource(messageID, locale);
			errorMessage = MessageFormatEx.format(errorMessage, params);
		}catch(Exception e){
			logger.error(e);
		}
		BusinessLogicException ex = new BusinessLogicException(errorCode, errorMessage);

		logger.debug("generateException(String) exit BusinessLogicException:",
				ex);

		return ex;
	}
}
