/**
 * 
 */
package com.ca.arcflash.webservice.service;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.catalog.SearchResult;
import com.ca.arcflash.webservice.data.catalog.SearchSessionItem;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JCatalogDetail;

/**
 * @author pidma02
 *
 */

public abstract class CSearch {

	/**
	 * @param si
	 * @param sessionPath
	 * @param searchDir
	 * @param caseSensitive
	 * @param includeSubDir
	 * @param pattern
	 */
	public CSearch(RecoveryPoint si, String sessionPath,
			String searchDir, boolean caseSensitive, boolean includeSubDir,
			String pattern) {
		this.searchItem = si;
		this.sessionPath = sessionPath;
		this.searchDir = searchDir;
		this.caseSensitive = caseSensitive;
		this.includeSubDir = includeSubDir;
		this.pattern = pattern;
	}

	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private RecoveryPoint searchItem;
	
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

	@XmlElement(namespace = "http://service.webservice.arcflash.ca.com/xsd")
	private SearchContext context;

	private NativeFacade nativeFacade;

	public RecoveryPoint getSearchItem() {
		return searchItem;
	}

	public void setSearchItem(RecoveryPoint searchItem) {
		this.searchItem = searchItem;
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

	public SearchContext getContext() {
		return context;
	}

	public void setContext(SearchContext context) {
		this.context = context;
	}

	public void updateContext(SearchContext context){
		if(this.context == null){
			this.context = new SearchContext();
			this.context.setContextID(context.getContextID());
		}
		
		this.context.setCurrKind(context.getCurrKind());
		this.context.setExcludeFileSystem(context.isExcludeFileSystem());
		this.context.setSearchkind(context.getSearchkind());
		this.context.setTag(context.getTag());
	}
	
	
	public NativeFacade getNativeFacade() {
		return nativeFacade;
	}

	public void setNativeFacade(NativeFacade nativeFacade) {
		this.nativeFacade = nativeFacade;
	}
	
	public void closeSearch()
	{
		
	}
	
	public SearchResult searchNext() throws ServiceException
	{
		SearchResult sr = null;
		
		return sr;
	}
	
	public CatalogItem convert2CatalogItem(JCatalogDetail detail, boolean useCatalog){
		CatalogItem item = new CatalogItem();
		item.setName(detail.getDisplayName());
		item.setComponentName(detail.getLongName());
		item.setId(detail.getLongNameID());
		item.setType(detail.getDataType());
		item.setSize((detail.getFileSizeHigh()<<32)+detail.getFileSize());
		item.setDate(detail.getFileDate() == 0 ? null : new Date(detail.getFileDate()));
		item.setPath(detail.getPath());
		item.setSessionNumber(detail.getSessionNumber());
		item.setSubSessionNumber(detail.getSubSessionNumber());
		item.setChildrenCount(detail.getChildrenCount());
		item.setUsePathID(useCatalog);
		item.setFullSessNum(detail.getFullSessNum());
		item.setEncryptInfo(detail.getEncryptInfo());
		item.setBackupDest(detail.getBackupDest());
		item.setJobName(detail.getJobName());
		item.setBackupTime(detail.getBackupTime() == 0 ? null : new Date(detail.getBackupTime()));
		item.setPwdHash(detail.getPwdHash());
		item.setSessionGuid(detail.getSessionGuid());
		item.setFullSessionGuid(detail.getFullSessionGuid());
		item.setVolAttr(detail.getVolAttr());
		item.setDriverLeterAttr(detail.getDriverLeterAttr());
		return item;
	}

}//end CSearch
