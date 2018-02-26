package com.ca.arcflash.webservice.service.validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.service.common.WebServiceErrorMessages;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.common.VolumnMapAdapter;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.MergeDetailItem;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.ca.arcflash.webservice.data.ThrottleItem;
import com.ca.arcflash.webservice.data.PM.AutoUpdateSettings;
import com.ca.arcflash.webservice.data.PM.ProxySettings;
import com.ca.arcflash.webservice.data.PM.StagingServerSettings;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupEmail;
import com.ca.arcflash.webservice.data.backup.BackupRPSDestSetting;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.merge.RetentionPolicy;
import com.ca.arcflash.webservice.data.validator.ValidatorUtil;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class BackupConfigurationValidator {

	private static final Logger logger = Logger.getLogger(BackupConfigurationValidator.class);
	private static final int MAX_PORT = 65535;
	private static final int MIN_PORT = 0;
	public static final int WINDOWS_HOST_NAME_MAX_LENGTH = 16;

	// shaji02
	public static final int MIN_THROTTLING = 0;
	public static final int MAX_THROTTLING = 99999;
	public static final int MIN_RETENTIONCOUNT = 1;
	public static final int MAX_RETENTIONCOUNT = 1344;
	public static final int MAX_RETENTIONCOUNT_ADV = 1440;
	public static final int NO_COMPRESSION = 0;
	public static final int STANDRAD_COMPRESSION = 1;
	public static final int MAXIMUM_COMPRESSION = 9;
	public static final int NO_ENCRYPTION = 0;
	public static final int LIBTYPE = 1;
	public static final int ALGTYPE_128 = 1;
	public static final int ALGTYPE_192 = 2;
	public static final int ALGTYPE_256 = 3;
	public static final int MAX_ENCRYPTIONKEY_BIT = 23;

	public int validate(BackupConfiguration backupConfiguration) throws ServiceException {
		if (backupConfiguration == null)
			throw new ServiceException("", FlashServiceErrorCode.Common_NullParameter);

		validateBLIDriver();

		// shaji02
		if (backupConfiguration.getThrottling() < MIN_THROTTLING
				|| backupConfiguration.getThrottling() > MAX_THROTTLING)
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidThrottling);

		if (backupConfiguration.getCompressionLevel() != NO_COMPRESSION
				&& backupConfiguration.getCompressionLevel() != STANDRAD_COMPRESSION
				&& backupConfiguration.getCompressionLevel() != MAXIMUM_COMPRESSION)
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidCompressionLevel);

		if (backupConfiguration.isEnableEncryption()) {
			if (backupConfiguration.getEncryptionAlgorithm() != NO_ENCRYPTION
					&& backupConfiguration.getEncryptionAlgorithm() != ((LIBTYPE << 16) | ALGTYPE_128)
					&& backupConfiguration.getEncryptionAlgorithm() != ((LIBTYPE << 16) | ALGTYPE_192)
					&& backupConfiguration.getEncryptionAlgorithm() != ((LIBTYPE << 16) | ALGTYPE_256)) {
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidEncryptionAlgorithm);
			}
			if (backupConfiguration.getEncryptionAlgorithm() > NO_ENCRYPTION) {
				if (backupConfiguration.getEncryptionKey() == null) {
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidEncryptionKey);
				} else if (backupConfiguration.getEncryptionKey() == "") {
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidEncryptionKey);
				} else if (backupConfiguration.getEncryptionKey().length() > MAX_ENCRYPTIONKEY_BIT) {
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidLongEncryptionKey);
				}
			}
		}

		if (backupConfiguration.getCommandBeforeBackup() == null
				|| backupConfiguration.getCommandBeforeBackup().trim().isEmpty()) {
			if (backupConfiguration.isEnablePreExitCode()) {
				backupConfiguration.setEnablePreExitCode(false);
				backupConfiguration.setPreExitCode(0);
				logger.info("No Command for backup, so ignore/disable the preexit code and reset the exit code to default value: 0");
			}
		}

		if (backupConfiguration.getBackupDataFormat() < 0 || backupConfiguration.getBackupDataFormat() > 1) {
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ERR_INVALID_BackupDataFormat);
		}

		if (!backupConfiguration.isEnableEncryption()) {
			backupConfiguration.setEncryptionAlgorithm(0);
			if (backupConfiguration.isD2dOrRPSDestType()) {// Session password
															// reuse the field
															// encypt key when
															// dest is
															// DataStore. So
															// only dest is
															// local or share
															// folder, reset it.
				backupConfiguration.setEncryptionKey(null);
			}
			logger.info("Not enable encryption key, so ignore and resest EncryptionAlgorithm and EncryptionKey to default value");
		}

		if (!PurgeLog.validate(backupConfiguration.getPurgeExchangeLogDays())) {
			throw new ServiceException(CommonService.getInstance().getServiceError(
					FlashServiceErrorCode.BackupConfig_ERR_INVALID_PURGE_LOG_DAYS_Exchange, null),
					FlashServiceErrorCode.BackupConfig_ERR_INVALID_PURGE_LOG_DAYS_Exchange);
		}

		if (!PurgeLog.validate(backupConfiguration.getPurgeSQLLogDays())) {
			throw new ServiceException(CommonService.getInstance().getServiceError(
					FlashServiceErrorCode.BackupConfig_ERR_INVALID_PURGE_LOG_DAYS_SQL, null),
					FlashServiceErrorCode.BackupConfig_ERR_INVALID_PURGE_LOG_DAYS_SQL);
		}

		boolean isManagedByD2D = backupConfiguration.isD2dOrRPSDestType();
		if (isManagedByD2D) {
			boolean isAdvanced = backupConfiguration.getBackupDataFormat() > 0;
			
			int maxCnt = MAX_RETENTIONCOUNT;
			
			int retentionCnt = backupConfiguration.getRetentionCount();
			
			if(isAdvanced){
				maxCnt = MAX_RETENTIONCOUNT_ADV;
				retentionCnt = backupConfiguration.getAllRetentionCount();
			}
			
			this.validateRetentionPolicy(backupConfiguration.getRetentionPolicy(),
					retentionCnt, isAdvanced, maxCnt);
		}

		// remove license check on GUI
		// if (backupConfiguration.isEnableEncryption()) {
		// if(!CommonService.getInstance().checkLicense(CommonService.ENCRYPTION_LIC)){
		// String msg = WebServiceMessages.getResource("LicenseEncryption",
		// WebServiceMessages.getResource("BackupJob"));
		// throw new ServiceException(msg,
		// FlashServiceErrorCode.Common_License_Failure_Encryption);
		// }
		// }

		// if (!StringUtil.isExistingPath(backupConfiguration.getDestination()))
		// throw new ServiceException(
		// FlashServiceErrorCode.BackupConfig_IvalidDestinationPath);

		int pathMaxWithoutHostName = validateDestPath(backupConfiguration);

		if ((!isRemote(backupConfiguration.getDestination()))
				&& (BrowserService.getInstance().getVolumes(false, null, null, null).length == 1)) {
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_SingleVolumeLocalDestination);
		}

		validateBackupVolumes(backupConfiguration, false); // currently we dont'
															// validate the
															// source volume
															// exist or not
															// because it's hard
															// to CPM to design
															// proper plan fit
															// to all agents.

		ValidateEmailSettings(backupConfiguration.getEmail());

		// validate pre/post username/password
		if (!StringUtil.isEmptyOrNull(backupConfiguration.getPrePostUserName())) {
			if (StringUtil.isEmptyOrNull(backupConfiguration.getPrePostPassword())) {
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ERR_NullPostUserPassword);
			}
			String userName = backupConfiguration.getPrePostUserName();
			String domain = "";
			int idx = userName.indexOf('\\');
			if (idx > 0 && userName.length() > idx + 1) {
				domain = userName.substring(0, idx);
				userName = userName.substring(idx + 1);
			}
			int result = 0;
			try {
				result = BackupService.getInstance().getNativeFacade()
						.validateUser(userName, backupConfiguration.getPrePostPassword(), domain);
			} catch (Throwable e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
				result = 1;
			}

			// ValidateUser returns 0 for admin user, 2 for valid user, 1 for
			// invalid user
			if (result == 1) {
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidPrePostUsernamePassword);
			}
		}

		// Validate pre/post username/password is blank when command is set
		if (!StringUtil.isEmptyOrNull(backupConfiguration.getCommandAfterBackup())
				|| !StringUtil.isEmptyOrNull(backupConfiguration.getCommandAfterSnapshot())
				|| !StringUtil.isEmptyOrNull(backupConfiguration.getCommandBeforeBackup())) {
			if (StringUtil.isEmptyOrNull(backupConfiguration.getPrePostUserName())) {
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ERR_NullPostUsername);
			}
		}

//		if (!isManagedByD2D) {
//			if (StringUtil.isJustEmptyOrNull(backupConfiguration.getEncryptionKey())) {
//				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ERR_Empty_Session_Pwd);
//			}
//		}

		if (backupConfiguration.getGrowthRate() < 0) {
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_Invalid_ChangeRate);
			// Invalid change rate. The value should be no less than 0.
		}

		if (backupConfiguration.getSpaceSavedAfterCompression() < 0
				|| backupConfiguration.getSpaceSavedAfterCompression() > 100) {
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_Invalid_SpaceSavedAfterCompression);
			// Invalid space saved after compression. The value should be in
			// range of 0 and 100.
		}

		// Standard format - non-MPII format that before r17
		if (backupConfiguration.getBackupDataFormat() == 0) {
			if (backupConfiguration.getPreAllocationBackupSpace() < 0
					|| backupConfiguration.getPreAllocationBackupSpace() > 100) {
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_Invalid_PreAllocationBackupSpace);
				// Invalid pre-allocate space. The value should be in range of 0
				// and 100.
			}
		} else {
			backupConfiguration.setPreAllocationBackupSpace(10);
			logger.info("For MPII, not support PreAllocationBackupSpace, so the PreAllocationBackupSpace is ignored: "
					+ backupConfiguration.getPreAllocationBackupSpace()
					+ ", the value is reset to its default value 10");
		}

		// validate selfupdate configurations
		// validateSelfUpdateConfigurations(backupConfiguration.getUpdateSettings());

		return pathMaxWithoutHostName;
	}

	private void validateBackupVolumes(BackupConfiguration configuration, boolean isValidExisting)
			throws ServiceException {
		BackupVolumes bkpVolumes = configuration.getBackupVolumes();
		if (bkpVolumes != null && !bkpVolumes.isFullMachine()) {
			List<String> volumes = bkpVolumes.getVolumes();
			boolean isEmpty = false;
			if (volumes == null || volumes.isEmpty()) {
				isEmpty = true;
			} else {
				for (String volumName : volumes) {
					if (volumName == null || volumName.trim().isEmpty()) {
						isEmpty = true;
						break;
					}
				}
			}
			if (isEmpty)
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUP_VOLUMES);

			if (isValidExisting) {
				VolumnMapAdapter.convertBackupVolumes(configuration);

				ArrayList<String> list = new ArrayList<String>();
				Volume[] localVolumes = BrowserService.getInstance().getVolumes(true, null, null, null);
				for (String volumName : volumes) {
					String name = volumName;
					boolean isExist = false;
					for (Volume vol : localVolumes) {
						String lvname = vol.getName();
						if (lvname.endsWith("\\")) {
							lvname = lvname.substring(0, lvname.length() - 1);
						}
						if (volumName.equalsIgnoreCase(lvname)) {
							isExist = true;
							break;
						}
					}
					if (!isExist)
						list.add(name);
				}

				if (!list.isEmpty()) {
					Iterator<String> it = list.iterator();
					StringBuilder sb = new StringBuilder();
					for (;;) {
						String vol = it.next();
						sb.append(vol);
						if (!it.hasNext())
							break;
						sb.append(", ");
					}
					throw new ServiceException(sb.toString(),
							FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUP_VOLUMES_NOTExist);
				}
			}
		}
	}

	public int validate(VSphereBackupConfiguration backupConfiguration, BackupVM backupVM) throws ServiceException {
		if (backupConfiguration == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);

		boolean isManagedByD2D = backupConfiguration.isD2dOrRPSDestType();
		if (isManagedByD2D) {
			this.validateRetentionPolicy(backupConfiguration.getRetentionPolicy(),
					backupConfiguration.getRetentionCount(), false, MAX_RETENTIONCOUNT);
		}

		// if (!StringUtil.isExistingPath(backupConfiguration.getDestination()))
		// throw new ServiceException(
		// FlashServiceErrorCode.BackupConfig_IvalidDestinationPath);

		int pathMaxWithoutHostName = validateDestPath(backupVM);

		/*
		 * if ((!isRemote(backupVM.getDestination())) &&
		 * (BrowserService.getInstance().getVolumes(false, null, null,
		 * null).length == 1)) { throw new ServiceException(
		 * FlashServiceErrorCode.BackupConfig_SingleVolumeLocalDestination); }
		 */

		ValidateEmailSettings(backupConfiguration.getEmail());

		if (!isManagedByD2D) {
			if (backupConfiguration.isEnableEncryption() && StringUtil.isEmptyOrNull(backupConfiguration.getEncryptionKey())) {
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_Empty_Session_Pwd);
			}
		}

		// Username is blank, password is entered
		if ((backupConfiguration.getPrePostUserName() == null || backupConfiguration.getPrePostUserName().trim()
				.isEmpty())
				&& (backupConfiguration.getPrePostPassword() != null && !backupConfiguration.getPrePostPassword()
						.trim().isEmpty())) {
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_InvalidPrePostUsernamePassword);
		}

		// validate selfupdate configurations
		// validateSelfUpdateConfigurations(backupConfiguration.getUpdateSettings());
		return pathMaxWithoutHostName;
	}

	public int ValidateEmailSettings(BackupEmail emailSettings) throws ServiceException {
		if (emailSettings != null) { // email settings can use.

			if (emailSettings.isEnableSettings()) {
				if (!StringUtil.isValidEmailAddress(emailSettings.getFromAddress()))
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_IvalidEmailFromAddress);

				if (!validateEmail(emailSettings.getFromAddress()))
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_IvalidEmailFromAddress);

				if (emailSettings.getRecipients() == null || emailSettings.getRecipients().size() == 0)
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ToAddressRequired);

				String[] recipients = emailSettings.getRecipientsAsArray();
				for (String recipient : recipients) {
					if ((!StringUtil.isValidEmailAddress(recipient)) || !validateEmail(recipient))
						throw new ServiceException("", FlashServiceErrorCode.BackupConfig_IvalidEmailToAddress);
				}

				String mailServer = emailSettings.getSmtp();
				if (mailServer == null || mailServer.trim().length() == 0 || !validateServerName(mailServer)
						|| !validateMachineName(mailServer)) {
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidEmailServer);
				}
			}

			if (emailSettings.isEnableProxy()) {
				String proxyServer = emailSettings.getProxyAddress();
				if (proxyServer == null || proxyServer.trim().length() == 0 || !validateServerName(proxyServer)
						|| !validateMachineName(proxyServer)) {
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidEmailProxyServer);
				}

				int port = emailSettings.getProxyPort();
				if (port <= MIN_PORT || port > MAX_PORT) {
					// throw new
					// ServiceException(FlashServiceErrorCode.BackupConfig_InvalidProxyPort);
					throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidProxyPort);
				}

				if (emailSettings.isProxyAuth()) {
					String usr = emailSettings.getProxyUsername();
					if (usr == null || usr.trim().length() == 0) {
						throw new ServiceException("",
								FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUPEMAIL_ProxyUserNameCantEmpty);
					}

					String pwd = emailSettings.getProxyPassword();
					if (pwd == null || pwd.trim().length() == 0) {
						throw new ServiceException("",
								FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUPEMAIL_ProxyPwdCantEmpty);
					}

					usr = usr.trim();
					if (usr.equals(".") || !validateUserName(usr)) {
						throw new ServiceException("",
								FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUPEMAIL_ProxyUserNameIsInvalid);
					}
				}
			}

			if (emailSettings.isMailAuth()) {
				String usr = emailSettings.getMailUser();
				if (usr == null || usr.trim().length() == 0) {
					throw new ServiceException("",
							FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUPEMAIL_EmailAccountCantEmpty);
				}

				String pwd = emailSettings.getMailPassword();
				if (pwd == null || pwd.trim().length() == 0) {
					throw new ServiceException("",
							FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUPEMAIL_EmailAccountPwdCantEmpty);
				}
			}

			if (emailSettings.isEnableSpaceNotification()) {
				if (emailSettings.getSpaceMeasureNum() < 0) {
					throw new ServiceException("",
							FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUPEMAIL_FreeSpaceLessThanMB);
				}
				String unit = emailSettings.getSpaceMeasureUnit();
				if (unit == null || unit.trim().length() == 0
						|| !("MB".equalsIgnoreCase(unit.trim()) || "%".equals(unit.trim()))) {
					throw new ServiceException("",
							FlashServiceErrorCode.BackupConfig_ERR_INVALID_BACKUPEMAIL_FreeSpaceLessThanUnit);
				}
			}

			String errorCode = ValidatorUtil.validate(emailSettings);
			if (errorCode != null && errorCode.trim().length() > 0)
				throw new ServiceException("", errorCode);
		}
		return 0;
	}

	private boolean validateUserName(String usr) {
		// Validate the server name with patterns:	"@=+[]<>:;*" 	
		String usrNameReg = "[^@=\\+\\[\\];:\\*<>]+";
		return Pattern.matches(usrNameReg, usr);
	}

	private boolean validateMachineName(String mailServer) {
		String validHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
		return Pattern.matches(validHostnameRegex, mailServer);
	}

	public int validateSelfUpdateConfigurations(AutoUpdateSettings autoUpdateSettings) throws ServiceException {
		return validateSelfUpdateConfigurations(autoUpdateSettings, false);
	}

	public int validateSelfUpdateConfigurations(AutoUpdateSettings autoUpdateSettings, boolean isFromEdge)
			throws ServiceException {

		// validating staging server
		if (autoUpdateSettings.getServerType() == 1) {
			StagingServerSettings[] stagingServers = autoUpdateSettings.getStagingServers();

			int iStagingServersCount = stagingServers.length;
			for (int iIndex = 0; iIndex < iStagingServersCount; iIndex++) {
				String stagingServer = stagingServers[iIndex].getStagingServer();
				if (stagingServer == null || stagingServer.length() == 0 || !validateServerName(stagingServer)) {
					throw new ServiceException(FlashServiceErrorCode.AutoUpdateConfig_InvalidServerName);
				}
			}

			// Currently, if d2d and rps and central are installed on the same
			// machine, central policy cannot be deployed to D2D. Because
			// central policy will add itself in policy as staging server but
			// d2d does not allow itself to be staging server.
			// In this particular scenario, D2D should still be able to use
			// Central Staging server as auto update location even if central is
			// on the same host. Customers would not want to download same
			// update twice ¡§C once through D2D and once through Central.
			// One of our customers uses D2D / Central in a cruise ship and they
			// were very upset when multiple copies of their D2Ds download
			// update over their very slow and expensive VSAT links.
			//if (!isFromEdge) {
				for (StagingServerSettings stagingServer : autoUpdateSettings.getStagingServers()) {
					if (!stagingServer.isUsingConsoleAsStagingServer() && CommonService.getInstance().checkLocalHost(stagingServer.getStagingServer(), false)) {
						throw new ServiceException(FlashServiceErrorCode.AutoUpdateConfig_InvalidStagingServer);
					}
				}
			//}
		}

		// validating proxy settings
		ProxySettings proxyConfig = autoUpdateSettings.getproxySettings();
		if (proxyConfig != null ? proxyConfig.isUseProxy() : false) {
			String proxyServerName = proxyConfig.getProxyServerName();
			if (proxyServerName == null || proxyServerName.length() == 0 || !validateServerName(proxyServerName)) {
				throw new ServiceException(FlashServiceErrorCode.AutoUpdateConfig_InvalidServerName);
			}

			int proxyServerPort = proxyConfig.getProxyServerPort();
			if (proxyServerPort < MIN_PORT || proxyServerPort > MAX_PORT) {
				throw new ServiceException(FlashServiceErrorCode.AutoUpdateConfig_InvalidPort);
			}
		}
		// validate patchesroot path
		/*
		 * long maxPath = BackupService.getInstance().getPathMaxLength();
		 * if(bcSelfUpdateSettings.getPatchesRoot().length() > maxPath) { throw
		 * new ServiceException("" + maxPath,
		 * FlashServiceErrorCode.BackupConfig_ERR_FileNameTooLong); }
		 */
		return 0;
	}

	public boolean validateServerName(String mailServer) {
		// Validate the server name with patterns:
		// ^`~!@#\$\^&\*\(\)=\+\[\]{}\\\|;:'",<>/\?%
		String serverNameReg = "[^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?%]+";
		return Pattern.matches(serverNameReg, mailServer);
	}

	private boolean isRemote(String inputFolder) {
		return inputFolder != null && inputFolder.startsWith("\\\\");
	}

	private void validateNetworkPath(BackupConfiguration backupConfiguration) throws ServiceException {

		logger.debug("validateNetworkPath - start");
		if (backupConfiguration.getDestination() != null && !backupConfiguration.getDestination().isEmpty()
				&& isRemote(backupConfiguration.getDestination())) {

			logger.debug("validateNetworkPath - inside test case");

			String username = backupConfiguration.getUserName();
			String password = backupConfiguration.getPassword();

			if (username == null)
				username = "";
			if (password == null)
				password = "";

			FileFolderItem folder = BrowserService.getInstance().getFileFolder(backupConfiguration.getDestination(),
					username, password);
			if (folder == null) {
				logger.debug("validateNetworkPath - exception");
				// throw new
				// ServiceException(FlashServiceErrorCode.BackupConfig_IvalidRemoteDestinationPath);
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_IvalidRemoteDestinationPath,
						new Object[] { "validateNetworkPath - exception" });
			} else {
				logger.debug("validateNetworkPath - valid file/folder");
			}
		}
		logger.debug("validateNetworkPath - end");
	}

	private int validateDestPath(BackupConfiguration backupConfiguration) throws ServiceException {

		logger.debug("validateDestPath - start");
		String path = backupConfiguration.getDestination();

		String username = backupConfiguration.getUserName();
		String password = backupConfiguration.getPassword();
		if (path == null)
			path = "";

		if (username == null)
			username = "";
		if (password == null)
			password = "";

		// logger.debug("path" + path);
		// logger.debug("username" + username);
		String domain = "";
		int indx = username.indexOf('\\');
		if (indx > 0) {
			domain = username.substring(0, indx);
			username = username.substring(indx + 1);
		}

		long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
		// To make sure the path without appending host name does not exceed the
		// maximum length,
		// the validation is necessary because
		// getNativeFacade().checkFolderAccess in the following
		// may lead to the web service breakdown.
		if (path.length() > pathMaxWithoutHostName + 1) {
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}

		BrowserService.getInstance().getNativeFacade().validateDestUser(path, domain, username, password);

		logger.debug("validateDestPath - end");
		return (int) pathMaxWithoutHostName;
	}

	private int validateDestPath(BackupVM backupVM) throws ServiceException {

		logger.debug("validateDestPath - start");
		String path = backupVM.getDestination();

		String username = backupVM.getDesUsername();
		String password = backupVM.getDesPassword();
		if (path == null)
			path = "";

		if (username == null)
			username = "";
		if (password == null)
			password = "";

		// logger.debug("path" + path);
		// logger.debug("username" + username);
		String domain = "";
		int indx = username.indexOf('\\');
		if (indx > 0) {
			domain = username.substring(0, indx);
			username = username.substring(indx + 1);
		}

		long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
		// To make sure the path without appending host name does not exceed the
		// maximum length,
		// the validation is necessary because
		// getNativeFacade().checkFolderAccess in the following
		// may lead to the web service breakdown.
		if (path.length() > pathMaxWithoutHostName + 1) {
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}

		BrowserService.getInstance().getNativeFacade().validateDestUser(path, domain, username, password);

		logger.debug("validateDestPath - end");
		return (int) pathMaxWithoutHostName;
	}

	public void generatePathExeedLimitException(long pathMaxWithoutHostName) throws ServiceException {
		long pathMaxLength = pathMaxWithoutHostName - ServiceContext.getInstance().getLocalMachineName().length();
		throw new ServiceException("" + pathMaxLength, FlashServiceErrorCode.BackupConfig_ERR_FileNameTooLong);
	}

	private void generatePathExeedLimitException(long pathMaxWithoutHostName, BackupVM vm) throws ServiceException {
		String folder = vm.getVmName() + "@" + vm.getEsxServerName();
		long pathMaxLength = pathMaxWithoutHostName - folder.length();
		throw new ServiceException("" + pathMaxLength, FlashServiceErrorCode.BackupConfig_ERR_FileNameTooLong);
	}

	public boolean validateEmail(String email) {
		try {
			InternetAddress address = new InternetAddress(email);
			address.validate();
		} catch (AddressException e) {
			return false;
		}
		return true;
	}

	private void validateRetentionPolicy(RetentionPolicy policy, int retentionCount,
			boolean hasAdvancedFormatAndUsingAdvanced, int maxCount) throws ServiceException {

		if (hasAdvancedFormatAndUsingAdvanced) {
			if (policy != null) {
				if (policy.isUseBackupSet()) {
					policy.setUseBackupSet(false);
					logger.info("Advanced format don't support Backup set, it's ignored and set to default value: false.");
				}
				if (policy.isUseTimeRange()) {
					policy.setUseTimeRange(false);
					logger.info("Advanced format don't support TimeRange, it's ignored and set to default value: false.");
				}
			}
			validateRetentionCount(retentionCount, maxCount, hasAdvancedFormatAndUsingAdvanced);
			return;
		}

		if (policy != null && policy.isUseBackupSet()) {

			if (policy.getBackupSetCount() <= 0) {
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ERR_INVALID_MIN_RECOVERYSET);
			}

			if (policy.getBackupSetCount() > 100) {
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_ERR_INVALID_MAX_RECOVERYSET);
			}

			if (policy.isUseWeekly()) {
				int dayOfWeek = policy.getDayOfWeek();
				if (dayOfWeek < Calendar.SUNDAY || dayOfWeek > Calendar.SATURDAY) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_ScheduleDayofWeekOutOfRange);
				}
			} else {
				int dayOfMonth = policy.getDayOfMonth();
				if (dayOfMonth < 1 || dayOfMonth > 32) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_ScheduleDayofMonthOutOfRange);
				}
			}

		} else {

			validateRetentionCount(retentionCount, MAX_RETENTIONCOUNT, hasAdvancedFormatAndUsingAdvanced);

			if (policy != null && policy.isUseTimeRange()) {
				if (policy.getEndHour() > 23 || policy.getEndHour() < 0) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_InvalidTimeRangeEndHour);
				}

				if (policy.getEndMinutes() > 59 || policy.getEndMinutes() < 0) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_InvalidTimeRangeEndMinute);
				}

				if (policy.getStartHour() > 23 || policy.getStartHour() < 0) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_InvalidTimeRangeStartHour);
				}

				if (policy.getStartMinutes() > 59 || policy.getStartMinutes() < 0) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_InvalidTimeRangeStartMinute);
				}
			}

		}
	}

	private void validateRetentionCount(int retentionCount, int maxCount, boolean isAdv) throws ServiceException {
		if (retentionCount < MIN_RETENTIONCOUNT)
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidRetentionCount);

		if (retentionCount > maxCount){
			if(isAdv){
				throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidMaxRetentionCount_ADV);
			}
			throw new ServiceException("", FlashServiceErrorCode.BackupConfig_InvalidMaxRetentionCount);
		}
	}

	public boolean isCleanDestination(String destination, String userName, String password) {
		java.util.Calendar beginDate = java.util.Calendar.getInstance();
		beginDate.set(1970, 0, 1);
		java.util.Calendar endDate = java.util.Calendar.getInstance();
		endDate.set(2999, 11, 31);
		try {
			return RestoreService.getInstance().getRecoveryPoints(destination, "", userName, password,
					beginDate.getTime(), endDate.getTime(), false).length == 0;
		} catch (ServiceException se) {
			logger.debug("Failed to get recovery points");
			return true;
		}
	}

	public void validateRPSHost(String hostName, String userName, String password, int port) throws ServiceException {
		if (hostName == null) {
			throw new ServiceException(WebServiceMessages.getResource("backupRPSDestHostNameIsNull"),
					FlashServiceErrorCode.Common_NullParameter);
		}

		if (userName == null) {
			throw new ServiceException(WebServiceMessages.getResource("backupRPSDestUserNameIsNull"),
					FlashServiceErrorCode.Common_NullParameter);
		}

		if (password == null) {
			throw new ServiceException(WebServiceMessages.getResource("backupRPSDestPwdIsNull"),
					FlashServiceErrorCode.Common_NullParameter);
		}

		if (port <= MIN_PORT || port > MAX_PORT) {
			throw new ServiceException(WebServiceErrorMessages.getServiceError(
					FlashServiceErrorCode.BackupConfig_InvalidProxyPort, new Object[] {}),
					FlashServiceErrorCode.BackupConfig_InvalidProxyPort);
		}
	}

	public RpsPolicy4D2D validateRpsDestSetting(BackupConfiguration backupConfiguration) throws ServiceException {

		if (backupConfiguration == null)
			throw new ServiceException(WebServiceMessages.getResource("backupConfSettingIsNull"),
					FlashServiceErrorCode.Common_NullParameter);

		if (backupConfiguration.isD2dOrRPSDestType()) {
			return null;
		}

		BackupRPSDestSetting bckRpsDestSetting = backupConfiguration.getBackupRpsDestSetting();
		return validateBackupRPSSetting(bckRpsDestSetting);
	}

	public RpsPolicy4D2D validateRpsDestSetting(VSphereBackupConfiguration backupConfiguration) throws ServiceException {

		if (backupConfiguration == null)
			throw new ServiceException(WebServiceMessages.getResource("backupConfSettingIsNull"),
					FlashServiceErrorCode.Common_NullParameter);

		if (backupConfiguration.isD2dOrRPSDestType()) {
			return null;
		}

		BackupRPSDestSetting bckRpsDestSetting = backupConfiguration.getBackupRpsDestSetting();
		return validateBackupRPSSetting(bckRpsDestSetting);
	}

	private RpsPolicy4D2D validateBackupRPSSetting(BackupRPSDestSetting bckRpsDestSetting) throws ServiceException {
		logger.debug("Validate rps setting for backup");
		if (bckRpsDestSetting == null)
			throw new ServiceException(WebServiceMessages.getResource("backupRPSDestSettingIsNull"),
					FlashServiceErrorCode.Common_NullParameter);

		// check RPS destination setting
		RpsHost rpsHost = bckRpsDestSetting.getRpsHost();
		String rpsHostName = rpsHost.getRhostname();
		int port = rpsHost.getPort();
		validateRPSHost(rpsHostName, rpsHost.getUsername(), rpsHost.getPassword(), port);

		String protocol = "http";
		if (!rpsHost.isHttpProtocol())
			protocol = "https";

		// check policy whether exists and connect to rps, validate rps whether
		// exist
		// temp zhash05
		String rpsPolicyId = bckRpsDestSetting.getRPSPolicyUUID();
		RpsHost host = bckRpsDestSetting.getRpsHost();
		try {
			RpsPolicy4D2D[] policyList = SettingsService.instance().getRPSPolicyList(host.getRhostname(),
					host.getUsername(), host.getPassword(), port, protocol, host.getUuid());
			// check if policy existes
			if (policyList == null || policyList.length == 0) {
				Object[] parameters = new Object[1];
				parameters[0] = bckRpsDestSetting.getRPSPolicy();
				throw new ServiceException(WebServiceMessages.getResource("RPSPolicyIsNotExisted", host.getRhostname()),
						FlashServiceErrorCode.Common_General_Message);
			} else {
				boolean isExist = false;
				RpsPolicy4D2D usedPolicy = null;
				for (RpsPolicy4D2D policy : policyList) {
					if (policy.getPolicyUUID().equals(rpsPolicyId)) {
						isExist = true;
						usedPolicy = policy;
						break;
					}
				}
				if (!isExist) {
					Object[] parameters = new Object[1];
					parameters[0] = bckRpsDestSetting.getRPSPolicy();
					String message = WebServiceMessages.getResource("rpsPolicyNotExist",
							bckRpsDestSetting.getRPSPolicy(), bckRpsDestSetting.getRpsHost().getRhostname());
					throw new ServiceException(message, FlashServiceErrorCode.BackupConfig_ERR_Policy_Not_Exist);
				}

				if (usedPolicy != null) {
					bckRpsDestSetting.setRPSDataStore(usedPolicy.getDataStoreName());
					bckRpsDestSetting.setRPSDataStoreDisplayName(usedPolicy.getDataStoreDisplayName());
				}
				if (logger.isDebugEnabled())
					logger.debug(StringUtil.convertObject2String(usedPolicy));
				logger.debug("Validate rps setting for backup end.");
				return usedPolicy;
			}
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			Object[] parameters = new Object[1];
			parameters[0] = rpsHostName;
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_RPS_SERVER_NOT_REACHABLE, parameters);
		}
	}

	public void validateAdvanceSchedule(AdvanceSchedule advanceScheduleSetting) throws ServiceException {
		// if (advanceScheduleSetting.isEnabled()){
		PeriodSchedule ps = advanceScheduleSetting.getPeriodSchedule();
		if(ps!=null){
			EveryDaySchedule daily = ps.getDaySchedule();
			if(daily!=null){
				if(daily.getDayEnabled()==null)
					daily.setDayEnabled(new Boolean[]{true,true,true,true,true,true,true}); 
				if(daily.getDayEnabled().length!=7)
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_InvalidLengthOfWeekdaysForDaily);
				if(daily.getDayEnabled()[0]==null||daily.getDayEnabled()[1]==null||daily.getDayEnabled()[2]==null
						||daily.getDayEnabled()[3]==null||daily.getDayEnabled()[4]==null||daily.getDayEnabled()[5]==null
						||daily.getDayEnabled()[6]==null)
					throw new ServiceException("", FlashServiceErrorCode.Common_NullParameter); 
				if(daily.getDayEnabled()[0]==false&&daily.getDayEnabled()[1]==false&&daily.getDayEnabled()[2]==false
						&&daily.getDayEnabled()[3]==false&&daily.getDayEnabled()[4]==false
							&&daily.getDayEnabled()[5]==false&&daily.getDayEnabled()[6]==false)
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_InvalidWeekdaysForDailySchedule);
			}
		}
		
		List<DailyScheduleDetailItem> dailyLists = advanceScheduleSetting.getDailyScheduleDetailItems();
		if (dailyLists != null) {
			for (int i = dailyLists.size() - 1; i >= 0; i--) {
				DailyScheduleDetailItem daylyItem = dailyLists.get(i);
				int dayOfWeek = daylyItem.getDayofWeek();
				if (dayOfWeek < 1 || dayOfWeek > 7) {
					throw new ServiceException("", FlashServiceErrorCode.AdvanceSchedule_ScheduleDayofWeekOutOfRange);
				}
			}
			List<DailyScheduleDetailItem> newDaylyLists = removeDuplicateDayOfWeek(dailyLists);
			Collections.sort(newDaylyLists);
			advanceScheduleSetting.setDailyScheduleDetailItems(newDaylyLists);

			for (DailyScheduleDetailItem daylyItem : advanceScheduleSetting.getDailyScheduleDetailItems()) {
				ArrayList<ScheduleDetailItem> scheduleDetailLists = daylyItem.getScheduleDetailItems();
				ArrayList<ThrottleItem> throttleLists = daylyItem.getThrottleItems();
				ArrayList<MergeDetailItem> mergeDetailLists = daylyItem.getMergeDetailItems();
				// shaji02 validate backup schedule item count max in advance
				// schedule
				if ((scheduleDetailLists != null)
						&& (scheduleDetailLists.size() > Integer.parseInt(WebServiceMessages
								.getResource("scheduleMaxItemCount")))) {
					throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsMax"),
							FlashServiceErrorCode.AdvanceSchedule_ScheduleMaxItemCount);
				}
				// shaji02 validate throttle schedule item count max in advance
				// schedule
				if ((throttleLists != null)
						&& (throttleLists.size() > Integer.parseInt(WebServiceMessages
								.getResource("scheduleMaxThrottleCount")))) {
					throw new ServiceException(WebServiceMessages.getResource("scheduleThrottleItemIsMax"),
							FlashServiceErrorCode.AdvanceSchedule_ScheduleMaxThrottleCount);
				}
				// shaji02 validate merge schedule item count max in advance
				// schedule
				if ((mergeDetailLists != null)
						&& (mergeDetailLists.size() > Integer.parseInt(WebServiceMessages
								.getResource("scheduleMaxMergeCount")))) {
					throw new ServiceException(WebServiceMessages.getResource("scheduleMergeItemIsMax"),
							FlashServiceErrorCode.AdvanceSchedule_ScheduleMaxMergeCount);
				}
				// shaji02 validate start time/end time/repeat interval/minium
				// repeat interval for each backup schedule item
				if (scheduleDetailLists != null) {
					for (ScheduleDetailItem scheduleItem : scheduleDetailLists) {
						int startMinutes = scheduleItem.getStartTime().getHour() * 60
								+ scheduleItem.getStartTime().getMinute();
						if (scheduleItem.getStartTime().getHour() < 0 || scheduleItem.getStartTime().getHour() > 23
								|| scheduleItem.getStartTime().getMinute() < 0
								|| scheduleItem.getStartTime().getMinute() > 59) {
							throw new ServiceException(
									WebServiceMessages.getResource("scheduleItemStartTimeIsInvalid"),
									FlashServiceErrorCode.AdvanceSchedule_ScheduleItemStartTimeIsInvalid);
						}
						if (scheduleItem.getJobType() < 0 || scheduleItem.getJobType() > 2)
							throw new ServiceException(WebServiceMessages.getResource("scheduleItemJobTypeIsInvalid"),
									FlashServiceErrorCode.AdvanceSchedule_ScheduleItemJobTypeIsInvalid);
						if (scheduleItem.isRepeatEnabled()) {
							int endMinutes = scheduleItem.getEndTime().getHour() * 60
									+ scheduleItem.getEndTime().getMinute();
							int repeatMinutes = 0;
							
							// Liang.Shu - Jan 15, 2016 - allow the setting 0:00 ~ 0:00 (whole day) - if end time is 12:00AM, it means the end of the day, so set endMinute to 24 hour * 60 min/hour
							if (endMinutes == 0)
								endMinutes = 24 * 60;
							
							int diffMinutes = endMinutes - startMinutes;
							if (scheduleItem.getEndTime().getHour() < 0 || scheduleItem.getEndTime().getHour() > 23
									|| scheduleItem.getEndTime().getMinute() < 0
									|| scheduleItem.getEndTime().getMinute() > 59)
								throw new ServiceException(
										WebServiceMessages.getResource("scheduleItemEndTimeIsInvalid"),
										FlashServiceErrorCode.AdvanceSchedule_ScheduleItemEndTimeIsInvalid);
							if ((scheduleItem.getIntervalUnit() == 0) || (scheduleItem.getIntervalUnit() == 1)) {
								if (scheduleItem.getIntervalUnit() == 0)
									repeatMinutes = scheduleItem.getInterval();
								else
									repeatMinutes = scheduleItem.getInterval() * 60;
							} else {
								throw new ServiceException(
										WebServiceMessages.getResource("scheduleRepeatUnitIsInvalid"),
										FlashServiceErrorCode.AdvanceSchedule_ScheduleRepeatUnitIsInvalid);
							}

							if (diffMinutes < 15)
								throw new ServiceException(
										WebServiceMessages.getResource("scheduleDifferenceTimeNoLessThan"),
										FlashServiceErrorCode.AdvanceSchedule_ScheduleDifferenceTimeNoLessThan);
							if (repeatMinutes < 15)
								throw new ServiceException(
										WebServiceMessages.getResource("scheduleRepeatValueIsInvalid"),
										FlashServiceErrorCode.AdvanceSchedule_ScheduleRepeatValueIsInvalid);
							else if (repeatMinutes > diffMinutes) {
								throw new ServiceException(WebServiceMessages.getResource("scheduleRepeatValueIsBig"),
										FlashServiceErrorCode.AdvanceSchedule_ScheduleRepeatValueIsBig);
							}
						}
					}
					validateScheduleItemOverlap(scheduleDetailLists);
				}
				if (throttleLists != null) {
					for (ThrottleItem throttleItem : throttleLists) {
						int startThrottleMinutes = throttleItem.getStartTime().getHour() * 60
								+ throttleItem.getStartTime().getMinute();
						int endThrottleMinutes = throttleItem.getEndTime().getHour() * 60
								+ throttleItem.getEndTime().getMinute();
						long throttleValue = throttleItem.getThrottleValue();
						int diffThrottleMinutes = endThrottleMinutes - startThrottleMinutes;
						//00:00 - 00:00 means throttle range 00:00 - 24:00
						if(startThrottleMinutes==0 && endThrottleMinutes==0){
							diffThrottleMinutes = 24*60;
						}
						if (throttleItem.getStartTime().getHour() < 0 || throttleItem.getStartTime().getHour() > 23
								|| throttleItem.getStartTime().getMinute() < 0
								|| throttleItem.getStartTime().getMinute() > 59) {
							throw new ServiceException(WebServiceMessages.getResource("throttleStartTimeIsInvalid"),
									FlashServiceErrorCode.AdvanceSchedule_ThrottleStartTimeIsInvalid);
						}
						if (throttleItem.getEndTime().getHour() < 0 || throttleItem.getEndTime().getHour() > 23
								|| throttleItem.getEndTime().getMinute() < 0
								|| throttleItem.getEndTime().getMinute() > 59)
							throw new ServiceException(WebServiceMessages.getResource("throttleEndTimeIsInvalid"),
									FlashServiceErrorCode.AdvanceSchedule_ThrottleEndTimeIsInvalid);
						if (diffThrottleMinutes < 15)
							throw new ServiceException(
									WebServiceMessages.getResource("throttleDifferenceTimeNoLessThan"),
									FlashServiceErrorCode.AdvanceSchedule_ThrottleDifferenceTimeNoLessThan);
						if (throttleValue <= 0)
							throw new ServiceException(WebServiceMessages.getResource("throttleValueIsInvalid"),
									FlashServiceErrorCode.AdvanceSchedule_ThrottleValueIsInvalid);
					}
					validateThrottleOverlap(throttleLists);
				}
				if (mergeDetailLists != null) {
					for (MergeDetailItem mergeItem : mergeDetailLists) {
						int startMergeMinutes = mergeItem.getStartTime().getHour() * 60
								+ mergeItem.getStartTime().getMinute();
						int endMergeMinutes = mergeItem.getEndTime().getHour() * 60
								+ mergeItem.getEndTime().getMinute();
						int diffMergeMinutes = endMergeMinutes - startMergeMinutes;
						if (mergeItem.getStartTime().getHour() < 0 || mergeItem.getStartTime().getHour() > 23
								|| mergeItem.getStartTime().getMinute() < 0
								|| mergeItem.getStartTime().getMinute() > 59) {
							throw new ServiceException(WebServiceMessages.getResource("mergeStartTimeIsInvalid"),
									FlashServiceErrorCode.AdvanceSchedule_MergeStartTimeIsInvalid);
						}
						if (mergeItem.getEndTime().getHour() < 0 || mergeItem.getEndTime().getHour() > 23
								|| mergeItem.getEndTime().getMinute() < 0 || mergeItem.getEndTime().getMinute() > 59)
							throw new ServiceException(WebServiceMessages.getResource("mergeEndTimeIsInvalid"),
									FlashServiceErrorCode.AdvanceSchedule_MergeEndTimeIsInvalid);
						if (diffMergeMinutes < 15)
							throw new ServiceException(WebServiceMessages.getResource("mergeDifferenceTimeNoLessThan"),
									FlashServiceErrorCode.AdvanceSchedule_MergeDifferenceTimeNoLessThan);
					}
					validateMergeOverlap(mergeDetailLists);
				}
			}
		}
		// }
	}

	private List<DailyScheduleDetailItem> removeDuplicateDayOfWeek(List<DailyScheduleDetailItem> daylyLists)
			throws ServiceException {
		ArrayList<DailyScheduleDetailItem> newDaylyLists = new ArrayList<DailyScheduleDetailItem>();
		// The later duplicate one will override the previous one if same
		// dayofweek
		for (int i = daylyLists.size() - 1; i >= 0; i--) {
			DailyScheduleDetailItem dailyItem = daylyLists.get(i);
			int dayOfWeek = dailyItem.getDayofWeek();
			boolean isAdd = true;
			for (DailyScheduleDetailItem item : newDaylyLists) {
				if (item.getDayofWeek() == dayOfWeek) {
					isAdd = false;
					this.logger.info("Duplicate DayOfweek set, the later will override previous one:" + dayOfWeek);
					break;
				}
			}
			if (isAdd) {
				newDaylyLists.add(dailyItem);
			}

		}
		return newDaylyLists;
	}

	public void validateThrottleOverlap(ArrayList<ThrottleItem> throttleLists) throws ServiceException {
		if (throttleLists.size() > 1) {
			// validate two throttle items has overlap time
			for (int i = 0; i < throttleLists.size() - 1; i++) {
				for (int j = i + 1; j < throttleLists.size(); j++) {
					int startM1 = throttleLists.get(i).getStartTime().getHour() * 60
							+ throttleLists.get(i).getStartTime().getMinute();
					int startM2 = throttleLists.get(j).getStartTime().getHour() * 60
							+ throttleLists.get(j).getStartTime().getMinute();
					int endM1 = throttleLists.get(i).getEndTime().getHour() * 60
							+ throttleLists.get(i).getEndTime().getMinute();
					int endM2 = throttleLists.get(j).getEndTime().getHour() * 60
							+ throttleLists.get(j).getEndTime().getMinute();
					int result1 = endM1 - startM2;
					int result2 = startM1 - endM2;
					if (result1 <= 0)
						continue;
					else if (result2 >= 0)
						continue;
					else
						throw new ServiceException(WebServiceMessages.getResource("scheduleThrottleItemOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleThrottleItemOverLap);
				}
			}
		}
	}

	public void validateMergeOverlap(ArrayList<MergeDetailItem> mergeDetailLists) throws ServiceException {
		if (mergeDetailLists.size() > 1) {
			// validate two merge items has overlap time
			for (int i = 0; i < mergeDetailLists.size() - 1; i++) {
				for (int j = i + 1; j < mergeDetailLists.size(); j++) {
					int startM1 = mergeDetailLists.get(i).getStartTime().getHour() * 60
							+ mergeDetailLists.get(i).getStartTime().getMinute();
					int startM2 = mergeDetailLists.get(j).getStartTime().getHour() * 60
							+ mergeDetailLists.get(j).getStartTime().getMinute();
					int endM1 = mergeDetailLists.get(i).getEndTime().getHour() * 60
							+ mergeDetailLists.get(i).getEndTime().getMinute();
					int endM2 = mergeDetailLists.get(j).getEndTime().getHour() * 60
							+ mergeDetailLists.get(j).getEndTime().getMinute();
					int result1 = endM1 - startM2;
					int result2 = startM1 - endM2;
					if (result1 <= 0)
						continue;
					else if (result2 >= 0)
						continue;
					else
						throw new ServiceException(WebServiceMessages.getResource("scheduleMergeItemOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleMergeItemOverLap);
				}
			}
		}
	}

	public void validateScheduleItemOverlap(ArrayList<ScheduleDetailItem> scheduleDetailLists) throws ServiceException {
		ArrayList<ScheduleDetailItem> scheduleNoRepeatListForFull = new ArrayList<ScheduleDetailItem>();
		ArrayList<ScheduleDetailItem> scheduleNoRepeatListForInc = new ArrayList<ScheduleDetailItem>();
		ArrayList<ScheduleDetailItem> scheduleNoRepeatListForVer = new ArrayList<ScheduleDetailItem>();
		ArrayList<ScheduleDetailItem> scheduleRepeatListForFull = new ArrayList<ScheduleDetailItem>();
		ArrayList<ScheduleDetailItem> scheduleRepeatListForInc = new ArrayList<ScheduleDetailItem>();
		ArrayList<ScheduleDetailItem> scheduleRepeatListForVer = new ArrayList<ScheduleDetailItem>();
		for (ScheduleDetailItem scheduleItem : scheduleDetailLists) {
			if (!scheduleItem.isRepeatEnabled()) {
				if (scheduleItem.getJobType() == 0) {
					scheduleNoRepeatListForFull.add(scheduleItem);
				} else if (scheduleItem.getJobType() == 1) {
					scheduleNoRepeatListForInc.add(scheduleItem);
				} else {
					scheduleNoRepeatListForVer.add(scheduleItem);
				}
			} else {
				if (scheduleItem.getJobType() == 0) {
					scheduleRepeatListForFull.add(scheduleItem);
				} else if (scheduleItem.getJobType() == 1) {
					scheduleRepeatListForInc.add(scheduleItem);
				} else {
					scheduleRepeatListForVer.add(scheduleItem);
				}
			}
		}
		if (scheduleNoRepeatListForFull.size() > 0 && scheduleRepeatListForFull.size() > 0) {
			for (int i = 0; i < scheduleNoRepeatListForFull.size(); i++) {
				int compareMinute = scheduleNoRepeatListForFull.get(i).getStartTime().getHour() * 60
						+ scheduleNoRepeatListForFull.get(i).getStartTime().getMinute();
				// validate full backup schedule without repeat has overlap with
				// the schedule with repeat enable
				for (int j = 0; j < scheduleRepeatListForFull.size(); j++) {
					int noRepeatStartMinutes = scheduleRepeatListForFull.get(j).getStartTime().getHour() * 60
							+ scheduleRepeatListForFull.get(j).getStartTime().getMinute();
					int noRepeatEndMinutes = scheduleRepeatListForFull.get(j).getEndTime().getHour() * 60
							+ scheduleRepeatListForFull.get(j).getEndTime().getMinute();
					if (noRepeatStartMinutes <= compareMinute && compareMinute <= noRepeatEndMinutes) {
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemIsOverLap);
					}
				}
			}
		}
		if (scheduleNoRepeatListForFull.size() > 1) {
			for (int i = 0; i < scheduleNoRepeatListForFull.size() - 1; i++) {
				int compareMinute = scheduleNoRepeatListForFull.get(i).getStartTime().getHour() * 60
						+ scheduleNoRepeatListForFull.get(i).getStartTime().getMinute();
				// validate full backup schedule without repeat has the same
				// start time
				for (int j = i + 1; j < scheduleNoRepeatListForFull.size(); j++) {
					if (compareMinute == scheduleNoRepeatListForFull.get(j).getStartTime().getHour() * 60
							+ scheduleNoRepeatListForFull.get(j).getStartTime().getMinute()) {
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemNoRepeatSameStartTime"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemNoRepeatSameStartTime);
					}
				}
			}
		}

		if (scheduleNoRepeatListForInc.size() > 0 && scheduleRepeatListForInc.size() > 0) {
			for (int i = 0; i < scheduleNoRepeatListForInc.size(); i++) {
				int compareMinute = scheduleNoRepeatListForInc.get(i).getStartTime().getHour() * 60
						+ scheduleNoRepeatListForInc.get(i).getStartTime().getMinute();
				// validate incremental backup schedule without repeat has
				// overlap with the schedule with repeat enable
				for (int j = 0; j < scheduleRepeatListForInc.size(); j++) {
					int noRepeatStartMinutes = scheduleRepeatListForInc.get(j).getStartTime().getHour() * 60
							+ scheduleRepeatListForInc.get(j).getStartTime().getMinute();
					int noRepeatEndMinutes = scheduleRepeatListForInc.get(j).getEndTime().getHour() * 60
							+ scheduleRepeatListForInc.get(j).getEndTime().getMinute();
					if (noRepeatStartMinutes <= compareMinute && compareMinute <= noRepeatEndMinutes) {
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemIsOverLap);
					}
				}
			}
		}
		if (scheduleNoRepeatListForInc.size() > 1) {
			for (int i = 0; i < scheduleNoRepeatListForInc.size() - 1; i++) {
				int compareMinute = scheduleNoRepeatListForInc.get(i).getStartTime().getHour() * 60
						+ scheduleNoRepeatListForInc.get(i).getStartTime().getMinute();
				// validate incremental backup schedule without repeat has the
				// same start time
				for (int j = i + 1; j < scheduleNoRepeatListForInc.size(); j++) {
					if (compareMinute == scheduleNoRepeatListForInc.get(j).getStartTime().getHour() * 60
							+ scheduleNoRepeatListForInc.get(j).getStartTime().getMinute()) {
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemNoRepeatSameStartTime"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemNoRepeatSameStartTime);
					}
				}
			}
		}
		if (scheduleNoRepeatListForVer.size() > 0 && scheduleRepeatListForVer.size() > 0) {
			for (int i = 0; i < scheduleNoRepeatListForVer.size(); i++) {
				int compareMinute = scheduleNoRepeatListForVer.get(i).getStartTime().getHour() * 60
						+ scheduleNoRepeatListForVer.get(i).getStartTime().getMinute();
				// validate verify backup schedule without repeat has overlap
				// with the schedule with repeat enable
				for (int j = 0; j < scheduleRepeatListForVer.size(); j++) {
					int noRepeatStartMinutes = scheduleRepeatListForVer.get(j).getStartTime().getHour() * 60
							+ scheduleRepeatListForVer.get(j).getStartTime().getMinute();
					int noRepeatEndMinutes = scheduleRepeatListForVer.get(j).getEndTime().getHour() * 60
							+ scheduleRepeatListForVer.get(j).getEndTime().getMinute();
					if (noRepeatStartMinutes <= compareMinute && compareMinute <= noRepeatEndMinutes) {
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemIsOverLap);
					}
				}
			}
		}
		if (scheduleNoRepeatListForVer.size() > 1) {
			for (int i = 0; i < scheduleNoRepeatListForVer.size() - 1; i++) {
				int compareMinute = scheduleNoRepeatListForVer.get(i).getStartTime().getHour() * 60
						+ scheduleNoRepeatListForVer.get(i).getStartTime().getMinute();
				// validate verify backup schedule without repeat has the same
				// start time
				for (int j = i + 1; j < scheduleNoRepeatListForVer.size(); j++) {
					if (compareMinute == scheduleNoRepeatListForVer.get(j).getStartTime().getHour() * 60
							+ scheduleNoRepeatListForVer.get(j).getStartTime().getMinute()) {
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemNoRepeatSameStartTime"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemNoRepeatSameStartTime);
					}
				}
			}
		}
		if (scheduleRepeatListForFull.size() > 1) {
			// validate full backup schedule with repeat has overlap with the
			// schedule with repeat enable
			for (int i = 0; i < scheduleRepeatListForFull.size() - 1; i++) {
				for (int j = i + 1; j < scheduleRepeatListForFull.size(); j++) {
					int startM1 = scheduleRepeatListForFull.get(i).getStartTime().getHour() * 60
							+ scheduleRepeatListForFull.get(i).getStartTime().getMinute();
					int startM2 = scheduleRepeatListForFull.get(j).getStartTime().getHour() * 60
							+ scheduleRepeatListForFull.get(j).getStartTime().getMinute();
					int endM1 = scheduleRepeatListForFull.get(i).getEndTime().getHour() * 60
							+ scheduleRepeatListForFull.get(i).getEndTime().getMinute();
					int endM2 = scheduleRepeatListForFull.get(j).getEndTime().getHour() * 60
							+ scheduleRepeatListForFull.get(j).getEndTime().getMinute();
					int result1 = endM1 - startM2;
					int result2 = startM1 - endM2;
					if (result1 <= 0)
						continue;
					else if (result2 >= 0)
						continue;
					else
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemIsOverLap);
				}
			}
		}
		if (scheduleRepeatListForInc.size() > 1) {
			// validate incremental backup schedule with repeat has overlap with
			// the schedule with repeat enable
			for (int i = 0; i < scheduleRepeatListForInc.size() - 1; i++) {
				for (int j = i + 1; j < scheduleRepeatListForInc.size(); j++) {
					int startM1 = scheduleRepeatListForInc.get(i).getStartTime().getHour() * 60
							+ scheduleRepeatListForInc.get(i).getStartTime().getMinute();
					int startM2 = scheduleRepeatListForInc.get(j).getStartTime().getHour() * 60
							+ scheduleRepeatListForInc.get(j).getStartTime().getMinute();
					int endM1 = scheduleRepeatListForInc.get(i).getEndTime().getHour() * 60
							+ scheduleRepeatListForInc.get(i).getEndTime().getMinute();
					int endM2 = scheduleRepeatListForInc.get(j).getEndTime().getHour() * 60
							+ scheduleRepeatListForInc.get(j).getEndTime().getMinute();
					int result1 = endM1 - startM2;
					int result2 = startM1 - endM2;
					if (result1 <= 0)
						continue;
					else if (result2 >= 0)
						continue;
					else
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemIsOverLap);
				}
			}
		}
		if (scheduleRepeatListForVer.size() > 1) {
			// validate verify backup schedule with repeat has overlap with the
			// schedule with repeat enable
			for (int i = 0; i < scheduleRepeatListForVer.size() - 1; i++) {
				for (int j = i + 1; j < scheduleRepeatListForVer.size(); j++) {
					int startM1 = scheduleRepeatListForVer.get(i).getStartTime().getHour() * 60
							+ scheduleRepeatListForVer.get(i).getStartTime().getMinute();
					int startM2 = scheduleRepeatListForVer.get(j).getStartTime().getHour() * 60
							+ scheduleRepeatListForVer.get(j).getStartTime().getMinute();
					int endM1 = scheduleRepeatListForVer.get(i).getEndTime().getHour() * 60
							+ scheduleRepeatListForVer.get(i).getEndTime().getMinute();
					int endM2 = scheduleRepeatListForVer.get(j).getEndTime().getHour() * 60
							+ scheduleRepeatListForVer.get(j).getEndTime().getMinute();
					int result1 = endM1 - startM2;
					int result2 = startM1 - endM2;
					if (result1 <= 0)
						continue;
					else if (result2 >= 0)
						continue;
					else
						throw new ServiceException(WebServiceMessages.getResource("scheduleItemIsOverLap"),
								FlashServiceErrorCode.AdvanceSchedule_ScheduleItemIsOverLap);
				}
			}
		}
	}

	private void validateBLIDriver() throws ServiceException {
		try {
			if (!CommonService.getInstance().isDriverInstalled()) {
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_NO_BLI_DRIVER);
			} else if (!CommonService.getInstance().isRestartedAfterDriver()) {
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_NOT_BLI_DRIVER_RESTART);
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			logger.error("Failed to check BLI driver", e);
		}
	}
}
