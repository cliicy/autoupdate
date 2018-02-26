package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

public class EdgePreferenceConfigInfo implements Serializable{
	
	private String newsFeed;
	private String socialNetworking;
	private String videoTag;	
	private boolean showNodeDeleteConfigureUI;
	
	public String getNewsFeed() {
		return newsFeed;
	}
	public void setNewsFeed(String newsFeed) {
		this.newsFeed = newsFeed;
	}
	public String getSocialNetworking() {
		return socialNetworking;
	}
	public void setSocialNetworking(String socialNetworking) {
		this.socialNetworking = socialNetworking;
	}
	public void setShowNodeDeleteConfigureUI(boolean showNodeDeleteConfigureUI) {
		this.showNodeDeleteConfigureUI = showNodeDeleteConfigureUI;
	}
	public boolean isShowNodeDeleteConfigureUI() {
		return showNodeDeleteConfigureUI;
	}
	public String getVideoTag() {
		return videoTag;
	}
	public void setVideoTag(String videoTag) {
		this.videoTag = videoTag;
	}
}
