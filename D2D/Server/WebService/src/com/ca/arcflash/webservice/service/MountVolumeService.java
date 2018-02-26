package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jni.common.JMountRecoveryPointParams;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.restore.MountedRecoveryPointItem;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.jni.model.JMountedRecoveryPointItem;
import com.ca.arcflash.webservice.service.internal.MountedRecoveryPointItemConverter;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class MountVolumeService extends BaseService {
	private static final String STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME	=	"Volume";
	private static final Logger logger = Logger.getLogger(MountVolumeService.class);
	private static final MountVolumeService instance = new MountVolumeService();
	private static final MountedRecoveryPointItemConverter converter = new MountedRecoveryPointItemConverter();
	
	private MountVolumeService(){}

	public static MountVolumeService getInstance(){
		return instance;
	}
	
	private List<MountedRecoveryPointItem> getAllMountedItems(){
		List<MountedRecoveryPointItem>	mountedItems = new ArrayList<MountedRecoveryPointItem>();
		for (JMountedRecoveryPointItem jMountedItem : getNativeFacade()
				.getAllMountedRecoveryPointItems()) {
			if (isSessionMountToTempDir(jMountedItem.getMountPath())
					|| isSessionMountToWindowsTempDir(jMountedItem
							.getMountPath()))
				continue;

			mountedItems.add(converter.convert(jMountedItem));
		}
		return mountedItems;
	}
	
	private List<MountedRecoveryPointItem> getMountedItems(String dest,String domain, String user, String pwd, String subPath){
		List<MountedRecoveryPointItem>	mountedItems = new ArrayList<MountedRecoveryPointItem>();
		for (JMountedRecoveryPointItem jMountedItem : getNativeFacade().getMountedRecoveryPointItems(dest, domain, user, pwd, subPath)){
			if (isSessionMountToTempDir(jMountedItem.getMountPath())
					|| isSessionMountToWindowsTempDir(jMountedItem
							.getMountPath()))
				continue;

			mountedItems.add(converter.convert(jMountedItem));
		}
		return mountedItems;
	}
	
	public MountedRecoveryPointItem[] getAllMountedRecoveryPointItems(){
		List<MountedRecoveryPointItem> items = getAllMountedItems();
		return items.toArray(new MountedRecoveryPointItem[0]);
	}
	
	private boolean isSessionGuidEqual(String rpSessionGuid, String mountedSessionGuid){
		String newMountedSessionGuid = String.format("{%s}", mountedSessionGuid);
		return rpSessionGuid.equalsIgnoreCase(newMountedSessionGuid);
	}
	
	public MountedRecoveryPointItem[] getMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath, String sessionGUID)
	 	throws ServiceException{
		List<MountedRecoveryPointItem> items = new ArrayList<MountedRecoveryPointItem>();
		List<MountedRecoveryPointItem> newAddLists = new ArrayList<MountedRecoveryPointItem>();
		//get the mounted item
		for (MountedRecoveryPointItem moutItem: getMountedItems(dest,domain,user,pwd,subPath)) {
			if(isSessionGuidEqual(sessionGUID, moutItem.getSessionGuid()))
				items.add(moutItem);
		}
		//get the unmounted volume item, remove the application item
		for (RecoveryPointItem rpItem: RestoreService.getInstance().getRecoveryPointItems(dest, domain, user, pwd, subPath)) {
			if(items.size() == 0){
				if(rpItem.getVolumeOrAppType().equals(STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME))
					newAddLists.add(convertToMountedItem(rpItem));
			}
			else{
				boolean isFound = false;
				for (MountedRecoveryPointItem mpItem : items) {
					if(mpItem.getVolumeGuid().equals(rpItem.getGuid())){
						isFound = true;
						break;
					}
				}
				if( (!isFound) && (rpItem.getVolumeOrAppType().equals(STRING_RECOVERYPOINT_ITEM_TYPE_VOLUME)))
					newAddLists.add(convertToMountedItem(rpItem));
			}
			
		}
		items.addAll(newAddLists);
		
		return items.toArray(new MountedRecoveryPointItem[0]);
	}
	
	private MountedRecoveryPointItem convertToMountedItem(RecoveryPointItem rpItem){
		MountedRecoveryPointItem newMPItem = new MountedRecoveryPointItem();
		newMPItem.setMountDiskSignature(0);
		newMPItem.setMountPath("");
		newMPItem.setMountFlag(0);
		newMPItem.setReadOnly(true);
		newMPItem.setRecoveryPointDate(new Date(0));
		newMPItem.setRecoveryPointPath("");
		newMPItem.setSessionID(0);
		newMPItem.setTimeZoneOffset(0);
		newMPItem.setVolumeGuid(rpItem.getGuid());
		newMPItem.setVolumePath(converter.removeSlash(rpItem.getDisplayName()));
		newMPItem.setVolumeSize(rpItem.getVolDataSizeB());
		return newMPItem;
	}
	
	public String[] getAvailableMountDriveLetters(){
		List<String> avaliableDriveLetters = getNativeFacade().getAvailableMountDriveLetters();
		
		Collections.sort(avaliableDriveLetters, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		
		return avaliableDriveLetters.toArray(new String[0]);
	}
	
	@Deprecated
	public long mountRecoveryPointItem(String dest,String domain, String user, String pwd, String subPath, 
			String volGUID,int encryptionType,String encryptPassword, String mountPath){
		String msgLog = StringUtil.enFormat("Enter mountRecoveryPointItem with dest[%s] domain[%s] user[%s] subpath[%s] volGUID[%s] encryptionType[%d] mountPath[%s]",
				dest,domain,user,subPath, volGUID,encryptionType,mountPath);
		logger.debug(msgLog);
		checkD2DTempMountPath(mountPath);
		long result = getNativeFacade().mountRecoveryPointItem(dest, domain, user, pwd, subPath, volGUID, encryptionType, encryptPassword, mountPath);
		if(result!=0){
			if(result ==  0x0E0000080L){
				List<JMountedRecoveryPointItem> allMountedInfos = getNativeFacade().getMntInfoForVolume(dest, domain, user, pwd, subPath, volGUID);
				List<JMountedRecoveryPointItem> mountedInfos = new ArrayList<JMountedRecoveryPointItem>();
				for (JMountedRecoveryPointItem jRPItem : allMountedInfos) {
					if(isSessionMountToTempDir(jRPItem.getMountPath()) || isSessionMountToWindowsTempDir(jRPItem.getMountPath()))
						continue;
					mountedInfos.add(jRPItem);
				}
				if(mountedInfos.size() == 0){
					String logMsg = String.format("Failed to AFGetMntInfoForVolume with dest[%s],domain[%s],user[%s],subPath[%s],volGUID[%s]",
							dest, domain, user, subPath, volGUID);
					logger.error(logMsg);
					String allMsg = WebServiceMessages.getResource("mountSessionFailed", new Object[]{mountPath,""});
					throw AxisFault.fromAxisFault(allMsg, FlashServiceErrorCode.Common_MountVolume_Failure);
				}
				else{
					JMountedRecoveryPointItem jItem = mountedInfos.get(0);
					String allMsg = WebServiceMessages.getResource("mountVolumeFailed", new Object[]{mountPath, converter.removeSlash(jItem.getVolumePath()),
							converter.removeSlash(jItem.getMountPath())});
					throw AxisFault.fromAxisFault(allMsg, FlashServiceErrorCode.Common_MountVolume_Failure);
				}
			}
			else{
				String errorMsg = getNativeFacade().getErrorMsg(result);
				String allMsg = WebServiceMessages.getResource("mountSessionFailed", new Object[]{mountPath,errorMsg});
				throw AxisFault.fromAxisFault(allMsg, FlashServiceErrorCode.Common_General_Message);
			}
			
		}
		
		logger.debug("Leave mountRecoveryPointItem");
		return result;
	}
		
	public long mountRecoveryPointItem( JMountRecoveryPointParams jMntParams ){
		String msgLog = StringUtil.enFormat("Enter mountRecoveryPointItem with rps[%s], dsName[%s], dest[%s] domain[%s] user[%s] subpath[%s] volGUID[%s] encryptionType[%d] mountPath[%s]",
				jMntParams.getRpsHostname(), jMntParams.getDatastoreName(), jMntParams.getDest(), jMntParams.getDomain(), jMntParams.getUser(), 
				jMntParams.getSubPath(), jMntParams.getVolGUID(), jMntParams.getEncryptionType(), jMntParams.getMountPath() );
		logger.debug(msgLog);
		checkD2DTempMountPath(jMntParams.getMountPath() );
		long result = getNativeFacade().mountRecoveryPointItem( jMntParams );
		if(result!=0){
			if(result ==  0x0E0000080L){
				List<JMountedRecoveryPointItem> allMountedInfos = getNativeFacade().getMntInfoForVolume( jMntParams.getDest(), 
						jMntParams.getDomain(), jMntParams.getUser(), jMntParams.getPwd(), jMntParams.getSubPath(), jMntParams.getVolGUID() );
				List<JMountedRecoveryPointItem> mountedInfos = new ArrayList<JMountedRecoveryPointItem>();
				for (JMountedRecoveryPointItem jRPItem : allMountedInfos) {
					if(isSessionMountToTempDir(jRPItem.getMountPath()) || isSessionMountToWindowsTempDir(jRPItem.getMountPath()))
						continue;
					mountedInfos.add(jRPItem);
				}
				if(mountedInfos.size() == 0){
					String logMsg = String.format("Failed to AFGetMntInfoForVolume with dest[%s],domain[%s],user[%s],subPath[%s],volGUID[%s]",
							jMntParams.getDest(), jMntParams.getDomain(), jMntParams.getUser(), jMntParams.getSubPath(), jMntParams.getVolGUID() );
					logger.error(logMsg);
					String allMsg = WebServiceMessages.getResource("mountSessionFailed", new Object[]{ jMntParams.getMountPath(),""});
					throw AxisFault.fromAxisFault(allMsg, FlashServiceErrorCode.Common_MountVolume_Failure);
				}
				else{
					JMountedRecoveryPointItem jItem = mountedInfos.get(0);
					String allMsg = WebServiceMessages.getResource("mountVolumeFailed", new Object[]{jMntParams.getMountPath(), converter.removeSlash(jItem.getVolumePath()),
							converter.removeSlash(jItem.getMountPath())});
					throw AxisFault.fromAxisFault(allMsg, FlashServiceErrorCode.Common_MountVolume_Failure);
				}
			}
			else{
				String errorMsg = getNativeFacade().getErrorMsg(result);
				String allMsg = WebServiceMessages.getResource("mountSessionFailed", new Object[]{jMntParams.getMountPath(),errorMsg});
				throw AxisFault.fromAxisFault(allMsg, FlashServiceErrorCode.Common_General_Message);
			}
			
		}
		
		logger.debug("Leave mountRecoveryPointItem");
		return result;
	}
	
	public long disMountRecoveryPointItem(String mountPath, int mountDiskSignature){
		String msg = StringUtil.enFormat("Enter disMountRecoveryPointItem with mountPath[%s] mountDiskSignature[%d]", mountPath, mountDiskSignature);
		logger.debug(msg);
		long result = getNativeFacade().disMountRecoveryPointItem(mountPath, mountDiskSignature);
		if(result!=0){
			String errors = getNativeFacade().getErrorMsg(result);
			String allMsg = WebServiceMessages.getResource("dismountSessionFailed", new Object[]{mountPath,errors});
			throw AxisFault.fromAxisFault(allMsg, FlashServiceErrorCode.Common_General_Message);
		}
		logger.debug("Leave disMountRecoveryPointItem");
		return result;
	}
	
	private boolean isSessionMountToTempDir(String mountPath){
		String path = CommonUtil.D2DInstallPath;
		if(path.endsWith("\\")){
			path = path + "BIN\\temp";
		}
		else{
			path = path +"\\BIN\\temp";
		}
		
		if(mountPath.toLowerCase().startsWith(path.toLowerCase())){
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean isSessionMountToWindowsTempDir(String mountPath){
		String windowsTempDir = getNativeFacade().getWindowsTempDir();
		if(windowsTempDir.endsWith("\\")){
			windowsTempDir = windowsTempDir.substring(0,windowsTempDir.length()-1);
		}
		
		if(mountPath.toLowerCase().startsWith(windowsTempDir.toLowerCase())){
			return true;
		}
		else{
			return false;
		}
	}
	private void checkD2DTempMountPath(String mountPath){
		if(isSessionMountToTempDir(mountPath) || isSessionMountToWindowsTempDir(mountPath)){
			String msg = WebServiceMessages.getResource("mountSessionToTempDir", mountPath);
			throw AxisFault.fromAxisFault(msg, FlashServiceErrorCode.Common_General_Message);
		}
	}
}
