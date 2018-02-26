package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveConstantsModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5910425368953036984L;

	public static final int OPERATOR_LESSTHAN = 0;
	public static final int OPERATOR_GREATERTHAN = 1;
	public static final int OPERATOR_BETWEEN = 2;
	
	public static final String OPERATOR_LESSTHAN_STRING = "0";
	public static final String OPERATOR_GREATERTHAN_STRING = "1";
	public static final String OPERATOR_BETWEEN_STRING = "2";
	
	public static final int FILTER_PATTERN_FILE = 0;
	public static final int FILTER_PATTERN_FOLDER = 1;
	
	public static final String FILTER_PATTERN_FILE_STRING = "0";
	public static final String FILTER_PATTERN_FOLDER_STRING = "1";
	
	public static final int FILTER_TYPE_INCLUDE = 0;
	public static final int FILTER_TYPE_EXCLUDE = 1;
	
	public static final String FILTER_TYPE_INCLUDE_STRING = "0";
	public static final String FILTER_TYPE_EXCLUDE_STRING = "1";
}
