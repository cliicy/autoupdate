/**
 * 
 */
package com.ca.arcflash.webservice.service;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.DataFormatUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.jni.CommonNativeInstance;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.catalog.SearchResult;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JMountPoint;
import com.ca.arcflash.webservice.jni.model.JSearchResult;
import com.ca.arcflash.webservice.util.WebServiceMessages;

/**
 * @author pidma02
 *
 */
public class CMountSearch extends CSearch {

	private static final Logger logger = Logger.getLogger(CMountSearch.class);
	JMountPoint mountPoint = null;
	//boolean bCloseCalled = false;
	int currVol = 0;
	private String userName = "";
	private String password = "";
	private String searchVolume = "";
	private String volDisplayName = "";
	private String encryptedPwd;
	
	/**
	 * @param si
	 * @param sessionPath
	 * @param searchDir
	 * @param caseSensitive
	 * @param includeSubDir
	 * @param pattern
	 */
	public CMountSearch(RecoveryPoint si, String sessionPath,
			String searchDir, boolean caseSensitive, boolean includeSubDir,
			String pattern, NativeFacade nf,
			String userName, String password, String encryptedPwd) throws ServiceException {
		super(si, sessionPath, searchDir, caseSensitive, includeSubDir,
				pattern);
		this.setNativeFacade(nf);
		this.userName = userName;
		this.password = password;
		this.encryptedPwd = encryptedPwd;
		
		if((searchDir != null) && (searchDir.length() > 0))
		{
			searchVolume = searchDir.toLowerCase();
			searchVolume = searchVolume.trim();
		}
		
		initializeSearch();
	}
	
	public void initializeSearch() throws ServiceException 
	{
		logger.debug("InitializeSearch - start");
		
		try{
			
			volDisplayName = super.getSearchItem().getItems().get(currVol).getDisplayName().toLowerCase();
			
			//pidma02 initialise search here..
			//pidma02: if search dir is mentioned wtih volume
			//then check if the volume we are supposed to mount
			//is the volume we are supposed to search in?
			if((searchVolume.length() > 0) &&
			   (volDisplayName.length() > 0))
			{
				int i = searchVolume.indexOf(volDisplayName);
				if( (i == 0) && //this means we found the volDisplayName in the beginning itself
					((searchVolume.length() == volDisplayName.length()) ||
					 ((searchVolume.length() > volDisplayName.length()) && 
					  (searchVolume.charAt(volDisplayName.length()) == '\\'))))
				{
					logger.debug("Volume Display Name is "+volDisplayName+" which is equal to "+searchVolume+" hence searching in it");
				}
				else
				{
					mountPoint = null;
					logger.debug("Volume Display Name is "+volDisplayName+" which is not equal to "+searchVolume+" hence skipping searching in it");
					
					logger.debug("InitializeSearch - end");
					return;
				}
			}//end if
			
			RecoveryPoint si = super.getSearchItem();
			String volumeGUID = si.getItemsArray()[currVol].getGuid();
			String dest = super.getSessionPath();
	
			logger.debug("Volume with volumeguid  " + volumeGUID + " has not been mounted.");
			mountPoint = BrowserService.getInstance().MountVolumeExForSearch(userName, password, 
					volumeGUID, si.getSessionID(), this.getSessionPath(), encryptedPwd);
			
			//mountPoint = getNativeFacade().MountVolume(userName, password, this.getSessionPath(), si.getSessionID(), volumeGUID);
			if(mountPoint != null)
			{
				long contextid = getNativeFacade().SearchMountPoint(mountPoint.getMountID(), dest, si.getItemsArray()[currVol].getDisplayName(), this.getSearchDir(),this.isCaseSensitive(), this.isIncludeSubDir(), this.getPattern());
				
				SearchContext context = new SearchContext();
				context.setContextID(contextid);
				this.setContext(context);
			}
			else
				logger.info("Volume with volumeguid " + volumeGUID + " has been newly mounted as " + mountPoint.getMountID());
		}catch(ServiceException se) {
			logger.error("Initialize search failed ", se);
			if(se.getMessage() != null && !se.getMessage().isEmpty()){
				SimpleDateFormat dataFormat = new SimpleDateFormat(CommonNativeInstance.getICommonNative().getDateTimeFormat().getTimeDateFormat(), 
						DataFormatUtil.getDateFormatLocale()); 
				String time = dataFormat.format(super.getSearchItem().getTime());
				String message = WebServiceMessages.getResource("restoreSearchFailed", time, se.getMessage());
				throw new ServiceException(message, FlashServiceErrorCode.Common_General_Message);
			}else
				throw se;
		}catch(Throwable e){
			logger.error("InitializeSearch error ", e);
		}
		
		logger.debug("InitializeSearch - end");
	}
	
	@Override
	public SearchResult searchNext() throws ServiceException 
	{
		SearchResult result = new SearchResult();
		logger.debug("searchNext - start");
		
		try{

			result.setNextKind(1);

			JSearchResult sr = null;
			
			if(mountPoint != null)
				sr = getNativeFacade().FindNextSearchItems(getContext());
			
			while((sr == null) || (sr.getFound() < 1))
			{
				//check if we have to search in any other volume in the current
				//session
				if((currVol + 1) < super.getSearchItem().getItems().size())
				{
					currVol++;
					
					//pidma02: is Application session? skip it if it is..
					if(super.getSearchItem().getItems().get(currVol).getVolumeOrAppType().equals("Application") )
					{	
						logger.debug("Skipping Application Sub Session");
						continue;
					}
					else
					{
						//if no more items 
						closeSearch();
				
						initializeSearch();
			
						if(mountPoint != null)
						{
							sr = getNativeFacade().FindNextSearchItems(getContext());
							result.setCurrent(sr.getCurrent());
							result.setFound(sr.getFound());
						}
					}
				}
				else
				{
					//No results.. set appropriate values so that
					//UI can properly handle it.
					result.setNextKind(SearchContext.KIND_END);
					result.setCurrent(-1);
					break;
				}
			}//while
			if(sr != null) {
				List<CatalogItem> itemList = new LinkedList<CatalogItem>();
				for (JCatalogDetail detail : sr.getDetail()) {
					
					RecoveryPoint si = super.getSearchItem();
					
					RecoveryPointItem rpi = si.getItemsArray()[currVol];
					
					//Set the encryption information.. will be 0 if no encryption
					detail.setEncryptInfo(si.getEncryptType());
					detail.setPwdHash(si.getEncryptPasswordHash());
					
					detail.setPath(detail.getLongName());
					detail.setLongName(detail.getDisplayName());
					
					detail.setJobName(si.getName());
					detail.setBackupDest(super.getSessionPath());
					detail.setBackupTime(si.getTime().getTime());
					detail.setSessionGuid(si.getSessionGuid());
					detail.setSessionNumber((int) si.getSessionID());
					detail.setSubSessionNumber((int)rpi.getSubSessionID());
					detail.setPwdHash(si.getEncryptPasswordHash());
					detail.setSessType(si.getBackupType());
					detail.setVolAttr(rpi.getVolAttr());
					
					itemList.add(convert2CatalogItem(detail, true));
				}
				result.setDetail(itemList.toArray(new CatalogItem[0]));

				if (logger.isDebugEnabled()){
					logger.debug(StringUtil.convertObject2String(result));
					logger.debug(StringUtil.convertArray2String(result.getDetail()));
				}
			}
		}catch(ServiceException se) {
			logger.error("Error in search next", se);
			throw se;
		}catch(Throwable e){
			logger.error("Error in search next", e);			
			//No results.. set appropriate values so that
			//UI can properly handle it.
			result.setNextKind(SearchContext.KIND_END);
			result.setCurrent(-1);
	
		}
		
		logger.debug("searchNext - stop");
		return result;
	}

	@Override
	public void closeSearch() {
		
		try{
			
			//pidma02: we should call closeSearch for each mount
			//with the below if condition, we call only once per session
			//where as there could be multiple volumes within a session 
			//hence multiple mounts...
			//if(this.bCloseCalled == false)
			if(mountPoint != null)
			{
				//CLose the search here..
				getNativeFacade().FindCloseSearchItems(getContext());
				
				//Unmount the volume here..
				//getNativeFacade().UnMountVolume(mountPoint);
				BrowserService.getInstance().UnMountVolume(mountPoint, false, true);
				mountPoint = null;
			}
		}catch(Throwable e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			mountPoint = null;
		}
	}

}
