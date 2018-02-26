package com.ca.arcserve.edge.app.base.webservice.common;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import sun.util.calendar.ZoneInfo;

import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.IEdgeCommonService;
import com.ca.arcserve.edge.app.base.webservice.appliance.ChangeASBULinuxInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ApplianceUtils;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeAppInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgePreferenceConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ExternalLinks;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.externalLink.ExternalLinkCreator;
import com.ca.arcserve.edge.app.base.webservice.d2dapm.ApmUtility;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class EdgeCommonServiceImpl implements IEdgeCommonService {
	
	private static final Logger logger = Logger.getLogger(EdgeCommonServiceImpl.class);
	
	private static Map<String, ExternalLinks> links =  Collections.synchronizedMap( new HashMap<String, ExternalLinks>() );
	@Override
	public EdgeVersionInfo getVersionInformation() throws EdgeServiceFault {
		
		// It's no need to throw exceptions from following method, but I don't
		// want to change the interface so that we still have the flexibility
		// to throw exceptions once we need.
		//
		// In fact, the version info should be construct only once since it
		// will not change during the running of the application. It should
		// called when application starting and then keep it in a static
		// variable, later invocations don't need to handle the exception.
		//
		// Bo, Pang (panbo01)
		// 2015-11-06
		
		return getVersionInformation2();
	}
		
	public static EdgeVersionInfo getVersionInformation2() {
		EdgeVersionInfo versionInfo = new EdgeVersionInfo();
		
		TimeZone tz = Calendar.getInstance().getTimeZone();
		
		ZoneInfo zone = (ZoneInfo) TimeZone.getDefault();
//		String TZname = zone.getDisplayName();
		String dltLongName = zone.getDisplayName(true, ZoneInfo.LONG, Locale.getDefault());
		String stdLongName = zone.getDisplayName(false, ZoneInfo.LONG, Locale.getDefault());
		String dltShortName = zone.getDisplayName(true, ZoneInfo.SHORT, Locale.getDefault());
		String stdShortName = zone.getDisplayName(false, ZoneInfo.SHORT, Locale.getDefault());
		
		long[] transitions = null ;
		try {
			Field field = ZoneInfo.class.getDeclaredField("transitions");
			field.setAccessible(true);
			transitions = (long[])field.get(zone);
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} 
		long[] GWTtransitions = null;
		String TZid = tz.getID();
		int tzoffset =  tz.getRawOffset()/60000;
		if(transitions != null){
			int count=0;
			for(int i=0;i<transitions.length;i++){
				long l = transitions[i];		
				if(l>0){
					count++;
				}
			}
			GWTtransitions = new long[count*2];
			int j=0;
			for(int i=0;i<transitions.length;i++){
				long l = transitions[i];
				if(l>0){
					String hexStr=Long.toHexString(l);
					//System.out.println(hexStr);
					String change = hexStr.substring(hexStr.length()-3,hexStr.length());
					hexStr = hexStr.substring(0, hexStr.length()-3);
					//System.out.println(hexStr);
					l = Long.parseLong(hexStr, 16)/1000/3600;
					GWTtransitions[j]=l;
					j++;
					long tag = Long.parseLong(change, 16);
					if(tag>0){
						GWTtransitions[j] = 60;
						if(TZid.equalsIgnoreCase("America/Santo_Domingo")) GWTtransitions[j] = 30;
					}else{
						GWTtransitions[j] = 0;
					}
					j++;
				}
			}
		}
			
		String TZjson = "{\"transitions\":" + (GWTtransitions == null ? "[]" : Arrays.toString(GWTtransitions))
				+ ", \"names\": [\"" + stdShortName + "\", \"" + stdLongName + "\", \"" + dltShortName + "\", \""
				+ dltLongName + "\"], \"id\": \"" + TZid + "\", \"std_offset\": " + tzoffset + "}";

		versionInfo.setTimeZoneID(TZjson);
		int offSet = tz.getOffset(System.currentTimeMillis());
		
		if(tz.inDaylightTime(new Date(System.currentTimeMillis())))
		{
			offSet=tz.getRawOffset();
		}
		versionInfo.setTimeZoneOffset(offSet);
		versionInfo.setLanguage( Locale.getDefault().getLanguage() );
		versionInfo.setCountry( Locale.getDefault().getCountry() );
		versionInfo.setTimeZoneDisplayname (tz.getDisplayName());
		versionInfo.setDSTSavings(tz.getDSTSavings());

		try{
			versionInfo.setVersionString( CommonUtil.getVersionString() );
			versionInfo.setMajorVersion( CommonUtil.getMajorVersion() );
			versionInfo.setMinorVersion( CommonUtil.getMinorVersion() );
			versionInfo.setBuildNumber( CommonUtil.getBuildNumber() );
			versionInfo.setUpdateNumber(CommonUtil.getApplicationUpdateVersionNumber());
			versionInfo.setUpdateBuildNumber(CommonUtil.getApplicationUpdateBuildNumber());
			versionInfo.setRequiredD2DMajorVersion( CommonUtil.getRequiredD2DMajorVersion() );
			versionInfo.setRequiredD2DMinorVersion( CommonUtil.getRequiredD2DMinorVersion() );
			versionInfo.setRequiredD2DVersion( CommonUtil.getRequireD2DVersion() );
			versionInfo.setRequiredD2DVersionString( CommonUtil.getRequiredD2DVersionString() );
		}catch(Throwable e){
			logger.error("Read registry error", e);
		}
//		versionInfo.setDateFormatEdge(getDateFormatEdge());

		return versionInfo;
	}

//	private DateFormatEdge getDateFormatEdge() {
//		DateFormatEdge d = new DateFormatEdge();
//		d.setTimeDateFormat(EdgeCMWebServiceMessages.getResource("timeDateFormat"));
//		d.setTimeDateFormat1(EdgeCMWebServiceMessages.getResource("timeDateFormat1"));
//		d.setTimeDateFormat2(EdgeCMWebServiceMessages.getResource("timeDateFormat2"));
//		d.setFullTimeDateFormat1(EdgeCMWebServiceMessages.getResource("fullTimeDateFormat"));
//		d.setTimeDateDayInWeekFormat(EdgeCMWebServiceMessages.getResource("timeDateDayInWeekFormat"));
//		d.setDateTooltipFormat(EdgeCMWebServiceMessages.getResource("dateTooltipFormat"));
//		d.setDateTooltipFormat2(EdgeCMWebServiceMessages.getResource("dateTooltipFormat2"));
//		d.setDateTooltipFormat3(EdgeCMWebServiceMessages.getResource("dateTooltipFormat3"));
//		d.setTimeFormat(EdgeCMWebServiceMessages.getResource("timeFormat"));
//		d.setDayTooltipFormat(EdgeCMWebServiceMessages.getResource("dayTooltipFormat"));
//		d.setTimeTooltipFormat(EdgeCMWebServiceMessages.getResource("timeTooltipFormat"));
//		
//		return d;
//	}

	@Override
	public EdgeAppInfo getAppInformation() throws EdgeServiceFault {
		EdgeAppInfo appInfo = new EdgeAppInfo();
		
		appInfo.setVersion(getVersionInformation());
		appInfo.setEnableImportRemoteNodesFromFile(CommonUtil.isEnableImportRemoteNodesFromFile());
		
		return appInfo;
	}

	@Override
	public EdgePreferenceConfigInfo getPreferenceInformation()
			throws EdgeServiceFault {
		EdgePreferenceConfigInfo pfCfg = new EdgePreferenceConfigInfo();
		try {
			String configurationFolder = CommonUtil.getAppRootKey(EdgeWebServiceContext.getApplicationType());
			String newsFeed = CommonUtil.getApplicationExtentionKey(configurationFolder,WindowsRegistry.VALUE_NAME_APP_NEWSFEED);
			String socialNetworking = CommonUtil.getApplicationExtentionKey(configurationFolder,WindowsRegistry.VALUE_NAME_APP_SOCIALNETWORKING);
			String videoTag = CommonUtil.getApplicationExtentionKey(configurationFolder,WindowsRegistry.VALUE_NAME_APP_VIDEO);
			if(newsFeed==null){
				newsFeed="1";
			}
			if(socialNetworking==null){
				socialNetworking="1";
			}
			if(videoTag==null){
				videoTag="1";
			}
			pfCfg.setNewsFeed(newsFeed);
            pfCfg.setSocialNetworking(socialNetworking);
            pfCfg.setVideoTag(videoTag);
            pfCfg.setShowNodeDeleteConfigureUI(false);
            String isShow = CommonUtil.getApplicationExtentionKey(EdgeApplicationType.CentralManagement, WindowsRegistry.VALUE_NAME_ShowDeleteNodeUI);
            if( "1".equals(isShow) )
            	pfCfg.setShowNodeDeleteConfigureUI(true);
            
		} catch (Exception e) {
			logger.debug("getPreferenceConfiguration failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e
					.fillInStackTrace());
			throw esf;
		}
		return pfCfg;
	}

	@Override
	public PatchInfo getD2DPatchInformation() throws EdgeServiceFault {

		try {
			PatchInfo patchInfo = ApmUtility.getD2DPatchInfo();
			if (patchInfo != null) {
				int major = CommonUtil.getRequiredD2DMajorVersion();
				int minor = CommonUtil.getRequiredD2DMinorVersion();
				int buildNumber = CommonUtil.getRequiredD2DBuildNumber();
				if (patchInfo.getMajorversion() < major 
						|| (patchInfo.getMajorversion() == major && patchInfo.getMinorVersion() < minor)
						|| (patchInfo.getMajorversion() == major && patchInfo.getMinorVersion() == minor && patchInfo.getBuildNumber() < buildNumber) ) {
					patchInfo.setMajorversion(major);
					patchInfo.setMinorVersion(minor);
					patchInfo.setBuildNumber(buildNumber);
				}
			}
			
			return patchInfo;
		} catch (Exception e) {
			logger.error("getD2DPatchInformation failed:", e);
			String msg = e.getLocalizedMessage();
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.Configuration_FailedReadCfg, msg);
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, e.fillInStackTrace());
			throw esf;
		}
	}
	@Override
	public ExternalLinks getExternalLinksForInternal(String language, String country) throws EdgeServiceFault {
		String localeString = language + "_" + country;
		return getExternalLinksFromMap(localeString, "ExternalLinks.properties");
	}
	
	@SuppressWarnings("static-access")
	private ExternalLinks getExternalLinksFromMap(String localeString, String propertyFilename) {
		if (!this.links.containsKey(localeString)) {
			String configFolder = CommonUtil.getConfigurationFolder(EdgeWebServiceContext.getApplicationType());
			ExternalLinks link = loadExternalLinks(configFolder + propertyFilename, localeString);
			this.links.put(localeString, link);
		}
		
		return this.links.get(localeString);
	}
	private static ExternalLinks loadExternalLinks(String path, String localeString ){
		String baseUrl = "";
		Properties properties = new Properties();
		FileInputStream fis = null; 
	    try {
			fis = new FileInputStream(path);
	        properties.load(fis);
	        baseUrl = properties.getProperty("baseUrl");
	    } catch (Exception e) {
	    	logger.equals(e);
	    } finally {
	    	try {
	    		if(fis != null) fis.close();
	    	}catch(Throwable t) {}
	    }
	    
		ExternalLinks model = new ExternalLinks();
		Field[] fields = ExternalLinks.class.getDeclaredFields();
		ExternalLinkCreator linkCreator = new ExternalLinkCreator( localeString );
        for (Field field:fields){
        	if(Modifier.isStatic(field.getModifiers()))	//bypass serialVersionUID
        		continue;
        	field.setAccessible(true);
        	String url = linkCreator.create(baseUrl, 
        			EdgeWebServiceContext.getApplicationType(), 
        			field.getName());
        	try {
        		field.set(model, url);
        	} catch (Exception e) {
    	    	logger.error(e);
    	    }
        }
        
        return model;
	}
	
	@Override
	public Boolean IsApplianceNotConfig(String domain, String userName,
			String password) throws EdgeServiceFault {
		if(!ApplianceUtils.isAppliance())
			return false;
		try {
			if(ApplianceUtils.applianceConfigFinished())
				return false;
		} catch (Exception e) {
			logger.error("Analysis ApplianceProperties failed. May be the ApplianceDefaultSetting have not isConfigFinished flag",e);
			return false;
		}
		String d_username = userName;
		if(!StringUtil.isEmptyOrNull(domain)){
			d_username = domain+"\\"+userName;
		}
		
		//updateConsoleRegistryAccount(domain,userName,password);
	
		//updateAgentRegistryAccount(d_username,password);
		
		try {
			String host = EdgeCommonUtil.getLocalFqdnName();
			EdgeExecutors.getCachedPool().submit(new ChangeASBULinuxInformation(host, d_username, password));
		} catch (Exception e) {
			logger.error("Try to change ASBU and linux information failed.",e);
		}
		return true;
	}
	
	public void updateConsoleRegistryAccount(String c_domain, String c_userName, String c_password){
		EdgeAccount edgeAccount = new EdgeAccount();
		edgeAccount.setDomain(c_domain);
		edgeAccount.setUserName(c_userName);
		edgeAccount.setPassword(c_password);
		try {
			NativeFacade nativeFacade = new com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl();
			nativeFacade.saveEdgeAccount(edgeAccount);
		} catch (EdgeServiceFault e1) {
			logger.error("change edge account failed", e1); 
		}
	}
	
	private void updateAgentRegistryAccount(String domain_userName, String c_password){
		Account d2daccount = new Account();
		d2daccount.setUserName(domain_userName);
		d2daccount.setPassword(c_password);
		NativeFacadeImpl d2dNativeFacade = new NativeFacadeImpl();
		try {
			d2dNativeFacade.validateAdminAccount(d2daccount);
			d2dNativeFacade.saveAdminAccount(d2daccount);
		} catch (ServiceException e) {
			logger.error("change agent registry password failed.", e); 
		}
	}
	
	@Override
	public void ApplianceFactoryReset(boolean preserve, boolean autoReboot)throws EdgeServiceFault {
		boolean result = com.ca.arcserve.edge.app.base.webservice.appliance.ApplianceFactoryReset.applianceFactoryReset(preserve, autoReboot);
		if(!result){
			String msg = "Appliance Factory Reset failed! Plesse manual reset by useing command \"powershell.exe .\\arcserve_factoryreset.ps1  -preserve_data <true|false> -auto_reboot <true|false>\" in cmd under path:"+ CommonUtil.BaseEdgeInstallPath + "Appliance";
			EdgeServiceFaultBean bean = new EdgeServiceFaultBean(
					EdgeServiceErrorCode.FactoryReset_Failed, msg);
			bean.setMessageParameters(new Object[]{CommonUtil.BaseEdgeInstallPath});
			EdgeServiceFault esf = new EdgeServiceFault(msg, bean, null);
			throw esf;
		}
	}
	
	@Override
	public String getConsoleHostName(){
		return EdgeCommonUtil.getLocalFqdnName();
	}
}
