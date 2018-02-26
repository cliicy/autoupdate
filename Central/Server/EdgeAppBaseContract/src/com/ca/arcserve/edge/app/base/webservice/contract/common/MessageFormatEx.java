package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

class RegexCfgProperties extends Properties {
	private static final long serialVersionUID = -8054152127370940614L;
	private ArrayList<String> mRegexCfgVals = new ArrayList<String>();
    
    public List<String> getRegexCfgVals(){
    	return mRegexCfgVals;
    }
    
    @Override
    public Object put(Object key, Object value) {
    	mRegexCfgVals.add((String)value);
        return super.put(key, value);
    }
}

class RegexCfgInfo{
	public RegexCfgInfo(String sMatchReg, String sReplaceRegString) {
		mMatchReg = sMatchReg;
		mReplaceRegex = sReplaceRegString;
	}
	String mMatchReg;
	String mReplaceRegex;
}

public class MessageFormatEx {
	private static final Logger logger = Logger.getLogger(MessageFormatEx.class);
	static String REGEX_CFG_NAME = "formatMessageRegexCfg.properties";

	public static String format(String pattern, Object ... arguments){
		if (arguments != null && arguments.length > 0) {
			List<RegexCfgInfo> regexInfoList = getRegexCfgInfo();
			String newPattern = pattern;
			if (null != regexInfoList) {
				for (RegexCfgInfo curCfgInfo : regexInfoList) {
					newPattern = newPattern.replaceAll(curCfgInfo.mMatchReg, curCfgInfo.mReplaceRegex);
				}
			}
			return MessageFormat.format(newPattern, arguments);
		} else {
			return pattern;
		}
	}
	
	public static List<RegexCfgInfo> getRegexCfgInfo(){
		RegexCfgProperties pps = new RegexCfgProperties();
		InputStream in;
		try {
			in = new BufferedInputStream(MessageFormatEx.class.getResourceAsStream(REGEX_CFG_NAME));
			pps.load(in);
		} catch (Throwable e) {
			logger.error(e);
			return null;
		}

		List<RegexCfgInfo> newCfgInfos = new ArrayList<RegexCfgInfo>();
		List<String> listCfgInfo = pps.getRegexCfgVals();
		for (int i = 0; i < listCfgInfo.size();) {
			String sMatchReg = listCfgInfo.get(i++);
			if (i >= listCfgInfo.size()) {
				logger.error("format of regexCfg is wrong.");
				break;
			}

			String sReplaceRegString = listCfgInfo.get(i++);
			newCfgInfos.add(new RegexCfgInfo(sMatchReg, sReplaceRegString));
		}

		return newCfgInfos;
	}
}
