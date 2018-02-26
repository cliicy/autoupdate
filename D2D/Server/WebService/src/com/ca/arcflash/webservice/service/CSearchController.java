/**
 * 
 */
package com.ca.arcflash.webservice.service;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.catalog.SearchResult;
import com.ca.arcflash.webservice.data.catalog.SearchSessionItem;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.jni.NativeFacade;

/**
 * @author pidma02
 *
 */
public class CSearchController {

	private static final Logger logger = Logger.getLogger(CSearchController.class);
	
	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private RecoveryPoint[] searchItemList;
	
	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private long numberOfSearchSessions;
	
	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private long currentSearchSession;

	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private CSearch currSearchObj = null;

	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private String sessionPath;

	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private String searchDir;

	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private boolean caseSensitive;

	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private boolean includeSubDir;
	
	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private String pattern;
	
	private String userName;
	
	private String password;

	private NativeFacade nativeFacade;
	
	private SearchContext context;
	
	private Map<String, String> pwdsByHash = new HashMap<String, String>();
	
	/**
	 * @param sessionItemsList
	 * @param numberOfSearchSessions
	 * @param sessionPath
	 * @param searchDir
	 * @param caseSensitive
	 * @param includeSubDir
	 * @param pattern
	 */
	public CSearchController(RecoveryPoint[] sessionItemsList,
			long numberOfSearchSessions, String sessionPath, String searchDir,
			boolean caseSensitive, boolean includeSubDir, String pattern, NativeFacade nF, 
			String userName, String password, String[] encryptedHashKey, String[] encryptedPwd) throws ServiceException {
		
		logger.debug("session path:"+sessionPath);
		logger.debug("searchDir:"+searchDir);
		logger.debug("caseSensitive:"+caseSensitive);
		logger.debug("includeSubDir:"+includeSubDir);
		logger.debug("pattern:"+pattern);
		logger.debug("encryptedHash:" + encryptedHashKey);
		logger.debug("encryptedPwd: " + encryptedPwd);
		
		this.searchItemList = sessionItemsList;
		this.numberOfSearchSessions = numberOfSearchSessions;
		this.sessionPath = sessionPath;
		this.searchDir = searchDir;
		this.caseSensitive = caseSensitive;
		this.includeSubDir = includeSubDir;
		this.pattern = pattern;
		this.nativeFacade = nF;
		this.currentSearchSession = 0;
		this.userName = userName;
		this.password = password;
		if(encryptedHashKey != null) {
			for(int i = 0; i < encryptedHashKey.length; i ++) {
				pwdsByHash.put(encryptedHashKey[i], encryptedPwd[i]);
			}
		}
		
		CreateSearchObject();
	}
	
	private void CreateSearchObject() throws ServiceException
	{
		logger.debug("CreateSearchObject() - start");

		RecoveryPoint si = this.searchItemList[(int) currentSearchSession];
		
		
		if(currSearchObj != null)
		{
			currSearchObj.closeSearch();
			currSearchObj = null;
		}
		
		if(si.getFsCatalogStatus() == 1)//catalog status is finished..
		{
			currSearchObj = new CCatalogSearch(si, this.sessionPath, this.searchDir, 
					this.caseSensitive, this.includeSubDir, this.pattern, nativeFacade);
		}
		else
		{
			currSearchObj = new CMountSearch(si, this.sessionPath, this.searchDir, 
					this.caseSensitive, this.includeSubDir, this.pattern, 
					nativeFacade, userName, password, pwdsByHash.get(si.getEncryptPasswordHash()));
		}
		
		logger.debug("CreateSearchObject() - end");
	}
	
	public SearchResult searchNext() throws ServiceException
	{
		SearchResult sr = null;
		logger.debug("CSearchController::searchNext - start");
		
		try{
			currSearchObj.updateContext(context);
			sr = currSearchObj.searchNext();
			
			while( sr.getNextKind() == SearchContext.KIND_END)
			{
				logger.debug("CUrrent session "+currentSearchSession+" Numberof sessions"+numberOfSearchSessions);
				
				//this means the search in current session has ended.
				//currentSearchSession will be 0 based index so we should compare with +1 always
				if((currentSearchSession+1) < numberOfSearchSessions)
				{
					currentSearchSession++;
					
					CreateSearchObject();
					if(currSearchObj != null){
						currSearchObj.updateContext(context);
						sr = currSearchObj.searchNext();
					}
				}
				else
				{
					//All sessions have been searched for..
					break;
				}
			}//end while
		}catch(ServiceException se){
			throw se;
		}catch(Throwable e){
			logger.error("Failed to search next ",  e);
		}
		
		logger.debug("CSearchController::searchNext - stop");
		logger.debug("Returning search result "+sr.getNextKind()+ " "+sr.getCurrent());
		return sr;
	}
	
	public void closeSearch()
	{
		logger.debug("CSearchController::searchNext - start");
		
		try{
			
			currSearchObj.closeSearch();
//			
//			CCatalogSearch cs = CCatalogSearch.getInstance();
//			if(cs != null)
//				cs.closeSearchCatalog();
		}catch(Throwable e){
			e.printStackTrace();
		}
		
		logger.debug("CSearchController::closeSearch - stop");
	}
	
	public RecoveryPoint[] getSearchItemList() {
		return searchItemList;
	}

	public void setSearchItemList(RecoveryPoint[] searchItemList) {
		this.searchItemList = searchItemList;
	}

	public long getNumberOfSearchSessions() {
		return numberOfSearchSessions;
	}

	public void setNumberOfSearchSessions(long numberOfSearchSessions) {
		this.numberOfSearchSessions = numberOfSearchSessions;
	}

	public long getCurrentSearchSession() {
		return currentSearchSession;
	}

	public void setCurrentSearchSession(long currentSearchSession) {
		this.currentSearchSession = currentSearchSession;
	}

	public CSearch getCurrSearchObj() {
		return currSearchObj;
	}

	public void setCurrSearchObj(CSearch currSearchObj) {
		this.currSearchObj = currSearchObj;
	}

	public String getSessionPath() {
		return sessionPath;
	}

	public void setSessionPath(String sessionPath) {
		this.sessionPath = sessionPath;
	}

	public String getSearchDir() {
		return searchDir;
	}

	public void setSearchDir(String searchDir) {
		this.searchDir = searchDir;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isIncludeSubDir() {
		return includeSubDir;
	}

	public void setIncludeSubDir(boolean includeSubDir) {
		this.includeSubDir = includeSubDir;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public NativeFacade getNativeFacade() {
		return nativeFacade;
	}

	public void setNativeFacade(NativeFacade nativeFacade) {
		this.nativeFacade = nativeFacade;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(SearchContext context) {
		this.context = context;
	}

	/**
	 * @return the context
	 */
	public SearchContext getContext() {
		return context;
	}

}//end CSearchController
