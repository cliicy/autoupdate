package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.data.ad.ADAttribute;
import com.ca.arcflash.webservice.data.ad.ADNode;
import com.ca.arcflash.webservice.data.ad.ADNodeFilter;
import com.ca.arcflash.webservice.data.ad.ADPagingConfig;
import com.ca.arcflash.webservice.data.ad.ADPagingResult;
import com.ca.arcflash.webservice.data.job.rps.IJobDependency;
import com.ca.arcflash.webservice.data.job.rps.JobDependencySource;

public class ApplicationService extends BaseService implements IJobDependency {
	public static final String SPECIAL_CHAR_REGEX   = "[`~!@#$%^&()——+{}\\[\\]\\\\|:;\"',<>?/……]";
	public static final String SPLIT_POINT   		= "=";
	public static final String WILDCARDS			=	"*";
	public static final String WILDCARDS_REGEX		=	"[^\n]+";

	private static ApplicationService instance = new ApplicationService();

	private Logger logger = Logger.getLogger(ApplicationService.class);

	public static ApplicationService getInstance() {
		return instance;
	}

	@Override
	public boolean needRun(JobDependencySource source) {
		// TODO Auto-generated method stub
		return false;
	}

	public ADNode[] getADNodes(String destination, String userName,
			String password, long sessionNumber, long subSessionID,
			String encryptedPwd, long parentID) throws ServiceException {
		List<ADNode> list=this.getNativeFacade().getADNodes(destination, userName, password, sessionNumber, subSessionID, encryptedPwd, parentID);
		return list.toArray(new ADNode[0]);
	}

	public ADAttribute[] getADAttributes(String destination, String userName,
			String password, long sessionNumber, long subSessionID,
			String encryptedPwd, long nodeID) throws ServiceException {
		List<ADAttribute> list=this.getNativeFacade().getADAttributes(destination, userName, password, sessionNumber, subSessionID, encryptedPwd, nodeID);
		return list.toArray(new ADAttribute[0]);
	}

	public ADPagingResult getADPagingNodes(ADPagingConfig config,
			ADNodeFilter filter) throws ServiceException {
		ADNode[] nodes = getADNodes(config.getDestination(),
				config.getUserName(), config.getPassWord(),
				config.getSessionNumber(), config.getSubSessionID(),
				config.getEncryptedPwd(), config.getParentID(), filter);
		if (nodes == null || nodes.length == 0) {
			return null;
		}
		int totalCount = nodes.length;
		int pageCount = config.getCount();
		int startIndex = config.getStartIndex();

		if (startIndex + pageCount > totalCount) {
			pageCount = totalCount - startIndex;
		}
		List<ADNode> list = new ArrayList<ADNode>(pageCount);
		for (int i = startIndex; i < startIndex+pageCount; i++) {
			list.add(nodes[i]);
		}
		ADPagingResult result = new ADPagingResult();
		result.setStartIndex(startIndex);
		result.setTotalCount(totalCount);
		result.setData(list);
		return result;
	}
	
	private ADNode[] getADNodes(String destination, String userName,
			String password, long sessionNumber, long subSessionID,
			String encryptedPwd, long parentID, ADNodeFilter filter) throws ServiceException {
		List<ADNode> list=this.getNativeFacade().getADNodes(destination, userName, password, sessionNumber, subSessionID, encryptedPwd, parentID);
		if(filter==null||StringUtil.isEmptyOrNull(filter.getName())){
			return list.toArray(new ADNode[0]);
		}
		List<ADNode> result = new ArrayList<ADNode>();
		for(ADNode node : list){
			if(match(node.getName(), filter.getName())){
				result.add(node);
			}
		}
		return result.toArray(new ADNode[0]);
	}
	
	private boolean match(String input, String keyword){
		boolean result = false;
		try {
			String name=preprocess(input);
			if(containsSpecialChar(keyword)){
				result = false;
			}else if(!keyword.contains(WILDCARDS)){
				result = name.equalsIgnoreCase(keyword);
			}else{
				String regex = getRegex(keyword.toLowerCase());
				result = name.toLowerCase().matches(regex);
			}
		} catch (Exception e) {
			result = false;
		}
		logger.debug("match "+input +" "+ keyword +" return: "+result);
		return result;
	}
	
	private String preprocess(String input) {
		return input.substring(input.indexOf(SPLIT_POINT)+1);
	}

	/**
	 * get regular expression
	 * @param keyword
	 * @return
	 */
	private String getRegex(String keyword){
		return keyword.replace(WILDCARDS, WILDCARDS_REGEX);
	}
	
	private boolean containsSpecialChar(String string){
		String anotherString = string.replaceAll(SPECIAL_CHAR_REGEX, "");
		return !string.equals(anotherString);
	}
	
//	private String trimWildcards(String keyword){
//		while(keyword.startsWith(WILDCARDS)){
//			keyword = keyword.substring(1);
//		}
//		while(keyword.endsWith(WILDCARDS)){
//			keyword = keyword.substring(0,keyword.length()-1);
//		}
//		return keyword;
//	}

}
