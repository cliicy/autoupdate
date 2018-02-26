package com.ca.arcflash.ui.client;

import com.ca.arcflash.ui.client.common.Account;
import com.ca.arcflash.ui.client.common.IRPSRefreshable;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.icons.FlashImageBundle;
import com.ca.arcflash.ui.client.homepage.D2DHomePageTab;
import com.ca.arcflash.ui.client.homepage.HomepagePanel;
import com.ca.arcflash.ui.client.homepage.ManagedByEdgeContainer;
import com.ca.arcflash.ui.client.i18n.DataFormat;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.ExternalLinksModel;
import com.ca.arcflash.ui.client.model.RolePrivilegeModel;
import com.ca.arcflash.ui.client.model.VersionInfoModel;
import com.ca.arcflash.ui.client.vsphere.homepage.VSphereHomePageTab;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class UIContext {
	public static FlashUIConstants Constants = GWT.create(FlashUIConstants.class);
	public static FlashUIMessages Messages = GWT.create(FlashUIMessages.class);
	public static DataFormat dataFormat = GWT.create(DataFormat.class);
	public static ExternalLinksModel externalLinks;
	public static final FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
	public static HomepagePanel homepagePanel;
	public static D2DHomePageTab d2dHomepagePanel;
	public static ManagedByEdgeContainer managedByEdgeContainer;
	public static VersionInfoModel serverVersionInfo;
	public static String loginUser;
	public static boolean hasBLILic = false;
	//public static boolean isAdvSchedule = false;
	public static IRefreshable hostPage;
	public static IRPSRefreshable rpsHostPage;
	public static IRefreshable recentBackupPanel;
	public static BackupVMModel backupVM;
	public static int uiType;
	public static VSphereHomePageTab vSphereHomepagePanel;
	public static String productNameD2D = UIContext.Constants.productNameD2D();
	public static String companyName = "Arcserve";
	public static String productNamevSphere = Constants.productNamevSphere();
	public static String productNameVCM = Constants.productNameVCM();
	public static String productNameRPS = Constants.productNameRPS();
	public static String productNameASBU = Constants.productNameASBU();
	public static String productShortNameRPS = Constants.productShortNameRPS();
	public static String productShortNameD2D = Constants.productShortNameD2D();
//	public static final long maxRPLimitDEFAULT = 1344;
	public static final long maxRPLimitDEFAULT = 1440;
	public static final int maxRecoveryPointLimit = 1440;	
	public static long maxRPLimit = maxRPLimitDEFAULT;	
	public static int maxBackupsForArchiveJob = 700;
	public static int minBackupsForArchiveJob = 1;
	
	public static final long maxBSLimitDEFAULT = 100;
	public static long maxBSLimit = maxBSLimitDEFAULT;		

	public static long minFileVersions = 1;
	public static long maxFileVersions = 100;
	
//	public static int DEFAULT_MAX_PERIOD_RETAIN_COUNT_DAILY = 30;
//	public static int DEFAULT_MAX_PERIOD_RETAIN_COUNT_WEEKLY = 30;
//	public static int DEFAULT_MAX_PERIOD_RETAIN_COUNT_MONTHLY= 36;
//	public static int maxRecoveryPointLimit = (int)maxRPLimit + DEFAULT_MAX_PERIOD_RETAIN_COUNT_DAILY
//			+ DEFAULT_MAX_PERIOD_RETAIN_COUNT_WEEKLY + DEFAULT_MAX_PERIOD_RETAIN_COUNT_MONTHLY;
	public static int DEFAULT_MAX_PERIOD_RETAIN_COUNT_DAILY = maxRecoveryPointLimit;
	public static int DEFAULT_MAX_PERIOD_RETAIN_COUNT_WEEKLY = maxRecoveryPointLimit;
	public static int DEFAULT_MAX_PERIOD_RETAIN_COUNT_MONTHLY= maxRecoveryPointLimit;

	public static long minRetentionTime = 1;
	
	public static boolean isLaunchedForEdgePolicy = false;
	public static int MIN_WIDTH = 90;
	
	public static String hostName = ""; //the edge server host name from dns.
	
	public static String cloudBucketD2DArchiveLabel = "d2dfilecopy-"; //for r16
	
	public static String cloudBucketD2DF2CLabel = "d2dfc-v2-";
	
	public static String cloudBucketD2DLabel = "d2d-filecopy-"; // for r16.5
	
	public static String cloudBucketARCserveLabel = "arcserve-"; // for r17
	
	public static CustomizationModel customizedModel = getDefaultCustomizationModel();	
	
	public static final String AMAZON_URL = "s3.amazonaws.com";
	
	public static final String AZURE_URL = "https://blob.core.windows.net";	
	
	public static final String Fujitsu_URL = "https://blob.core.windows.net";
	
	public static final String CACLOUD_SEPARATOR="#";
	
	public static final int MaxStagingServerCount=5;
	
	public static boolean isRemoteVCM = false;
	
	private static Account globalAccount=new Account();
	private static ArchiveDestinationModel archiveDestModel=null;
	
	public static boolean isExchangeGRTFuncEnabled = false;
	
	public static RolePrivilegeModel RolePrivilege=null;
	
	
//	public static HashMap<String, String[]> remoteUserMap=null;
	//new HashMap<String, String[]>(); //cache remote server infomation
	private UIContext() {
	}
	
	public static CustomizationModel getDefaultCustomizationModel() {
		
		CustomizationModel customizationModel = new CustomizationModel(); 
		
		if(customizationModel.get("FileCopyToCloud")==null)
		{		
			customizationModel.set("FileCopyToCloud", true);
		}
		if(customizationModel.get("FileCopy")==null)
		{
			customizationModel.set("FileCopy", true);
		}
		if(customizationModel.get("FileArchive")==null)
		{
			customizationModel.set("FileArchive", true);
		}
		
		return customizationModel;

	}
	
	public static String getGlobalDefaultUser() {
		return globalAccount.getQualityUsername();
	}

	public static void setGlobalDefaultUser(String globalDefaultUser) {
		globalAccount.setQualityUsername(globalDefaultUser);
	}

	public static void setGlobalDefaultPassword(String globalDefaultPassword) {
		globalAccount.setPassword(globalDefaultPassword);
	}

	public static boolean hasGlobalDefaultPassword() {
		return !Util.isEmptyString(globalAccount.getPassword());
	}

	public static String getGlobalDefaultPassword() {
		return globalAccount.getPassword();
	}
	
	public static String escapeHTML(String inputString){		
		return SafeHtmlUtils.htmlEscapeAllowEntities(inputString);	
	}
	
	public static ArchiveDestinationModel getCurrentArchiveDestination(){
		return archiveDestModel;
	};
	
	public static void setCurrentArchiveDestination(ArchiveDestinationModel in_archiveDestModel){
		archiveDestModel=in_archiveDestModel;
	};
	
}
