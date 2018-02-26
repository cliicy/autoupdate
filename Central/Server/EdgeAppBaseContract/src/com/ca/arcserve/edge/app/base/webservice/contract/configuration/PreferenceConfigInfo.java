package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo.AuthenticationType;

public class PreferenceConfigInfo implements Serializable {
	private String newsFeedConfigInfo;
	private String socialNetworkingConfigInfo;
	private String videoTag;
	private int pageSize;
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getVideoTag() {
		return videoTag;
	}
	public void setVideoTag(String videoTag) {
		this.videoTag = videoTag;
	}
	public String getNewsFeedConfigInfo() {
		return newsFeedConfigInfo;
	}
	public void setNewsFeedConfigInfo(String newsFeedConfigInfo) {
		this.newsFeedConfigInfo = newsFeedConfigInfo;
	}
	public String getSocialNetworkingConfigInfo() {
		return socialNetworkingConfigInfo;
	}
	public void setSocialNetworkingConfigInfo(String socialNetworkingConfigInfo) {
		this.socialNetworkingConfigInfo = socialNetworkingConfigInfo;
	}
	
	@Override
	public boolean equals(Object otherObject){
		if( this == otherObject )
			return true;
		if( null == otherObject ){
			return false;
		}
		if( getClass() != otherObject.getClass() ){
			return false;
		}
		
		PreferenceConfigInfo other = (PreferenceConfigInfo)otherObject;

		return StringUtil.isEqual(newsFeedConfigInfo, other.newsFeedConfigInfo) &&
				StringUtil.isEqual(socialNetworkingConfigInfo, other.socialNetworkingConfigInfo) && 
				StringUtil.isEqual(videoTag, other.videoTag);
	}

}
