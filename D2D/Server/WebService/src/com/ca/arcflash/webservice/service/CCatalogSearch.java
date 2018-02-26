/**
 * 
 */
package com.ca.arcflash.webservice.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.catalog.SearchResult;
import com.ca.arcflash.webservice.data.catalog.SearchSessionItem;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JSearchResult;

/**
 * @author pidma02
 *
 */
public class CCatalogSearch extends CSearch {

	private static final Logger logger = Logger.getLogger(CCatalogSearch.class);
	
	/**
	 * @param si
	 * @param sessionPath
	 * @param searchDir
	 * @param caseSensitive
	 * @param includeSubDir
	 * @param pattern
	 * @return 
	 */
	public CCatalogSearch(RecoveryPoint si, String sessionPath,
			String searchDir, boolean caseSensitive, boolean includeSubDir,
			String pattern, NativeFacade nf) {
		super(si, sessionPath, searchDir, caseSensitive, includeSubDir,
				pattern);
		
		try {
			this.setNativeFacade(nf);
			initializeSearch();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public int initializeSearch()
	{
		int iRet = 0;
		
		logger.debug("CCatalogSearch: InitializeSearch - start");
		
		try{
			RecoveryPoint si = super.getSearchItem();
			
			String catalogPath = StringUtil.enFormat("%s\\catalog\\S%010d", super.getSessionPath(), si.getSessionID());
			SearchContext localContext = getNativeFacade().openSearchCatalogEx(catalogPath, super.getSearchDir(), super.isCaseSensitive(), super.isIncludeSubDir(), super.getPattern());
			this.setContext(localContext);
			
		}catch(Throwable e){
			e.printStackTrace();
			iRet = -1;
		}
		logger.debug("CCatalogSearch:InitializeSearch - end. returning "+iRet);
		return iRet;
	}
	
	@SuppressWarnings("null")
	@Override
	public SearchResult searchNext()
	{
		SearchResult result = null;
		SearchContext context = getContext();
		
		JSearchResult sr = null;
		try {
			result = new SearchResult();
			result.setNextKind(1);

			sr = getNativeFacade().searchNextEx(context);
		} catch (Throwable e) {
			logger.error("Failed to search next ");
		}

		if(sr != null)
		{
			result.setCurrent(sr.getCurrent());
			result.setFound(sr.getFound());
			List<CatalogItem> itemList = new LinkedList<CatalogItem>();
			for (JCatalogDetail detail : sr.getDetail()) {
				itemList.add(convert2CatalogItem(detail, true));
			}
			result.setDetail(itemList.toArray(new CatalogItem[0]));
			
			if(sr.hasNext())
				result.setNextKind(context.getCurrKind());
			else
				result.setNextKind(SearchContext.KIND_END);
			
			if (logger.isDebugEnabled()){
				logger.debug(StringUtil.convertObject2String(result));
				logger.debug(StringUtil.convertArray2String(result.getDetail()));
			}
		}
		logger.debug("searchNext() - end");
		
		return result;
	}
	
	@Override
	public void closeSearch() {
		try{
			getNativeFacade().closeSearchCatalog(getContext());
		}catch(Throwable e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void closeSearchCatalog() {
		
		try{
			getNativeFacade().closeSearchCatalog(getContext());
		}catch(Throwable e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
