package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * The class is the model bean for the customization configuration file.
 * The key is the same as the corresponding element name
 * @author zhawe03
 *
 */
public class CustomizationModel extends BaseModelData {
	
	private static final long serialVersionUID = -5494756185889217532L;

	public String getCompanyName() { 
		return (String)get("CompanyName");
	}
	
	public void setCompanyName(String companyName) {
		set("CompanyName", companyName);
	}
	
	public String getProductName() {
		return (String)get("ProductNameD2D");
	}
	
	public void setProductName(String productName) {
		set("ProductNameD2D", productName);
	}
	
	public Boolean getShowVedios() {
		return (Boolean)get("Videos");
	}
	
	public void setShowVedios(Boolean vedios) { 
		set("Videos", vedios);
	}
	
	public Boolean getShowSupport() {
		return (Boolean)get("Support");
	}
	
	public void setShowSupport(Boolean support) {
		set("Support", support);
	}
	
	public Boolean getShowFeedback() {
		return (Boolean)get("Feedback");
	}
	
	public void setShowFeedback(Boolean feedback) {
		set("Feedback", feedback);
	}
	
	public Boolean getShowCommunity() {
//		return (Boolean)get("UserCommunity");
		return false;	// remove user community from UI
	}
	
	public void setShowCommunity(Boolean community) {
		set("UserCommunity", community);
	}
	
	public Boolean getShowAdvice() {
//		return (Boolean)get("ExpertAdvice");
		return false; // always return false since we are removing ExpoertAdvice in Oolong.
	}
	
	public void setShowAdvice(Boolean advice) {
		set("ExpertAdvice", advice);
	}
	
	public Boolean getShowTwitter() {
//		return (Boolean)get("twitter");
		return false; // remove twitter in Oolong
	}
	
	public void setShowTwitter(Boolean twitter) {
		set("twitter", twitter);
	}
	
	public Boolean getShowFacebook() {
//		return (Boolean)get("facebook");
		return false; // remove facebook in Oolong
	}
	
	public void setShowFacebook(Boolean facebook) {
		set("facebook", facebook);
	}
	
	public Boolean getShowBackup() {
		return (Boolean)get("Backup");
	}
	
	public void setShowBackup(Boolean backup) { 
		set("Backup", backup);
	}
	
	public Boolean getShowRestore() {
		return (Boolean)get("Restore");
	}
	
	public void setShowRestore(Boolean restore) {
		set("Restore", restore);
	}
	
	public Boolean getShowSettings() {
		return (Boolean)get("Settings");
	}
	
	public void setShowSettings(Boolean settings) {
		set("Settings", settings);
	}
	
	public Boolean getShowCopy() {
		return (Boolean)get("Copy");
	}
	
	public void setShowCopy(Boolean copy) {
		set("Copy", copy);
	}
	
	public Boolean getShowLog() {
		return (Boolean)get("Logs");
	}
	
	public void setShowLog(Boolean log) {
		set("Logs", log);
	}
	
	public Boolean getShowDeploy() {
		return (Boolean)get("Deploy");
	}
	
	public void setShowDeploy(Boolean deploy) {
		set("Deploy", deploy);
	}
	
	public Boolean getShowMountVolume() {
		return (Boolean)get("MountVolume");
	}
	
	public void setShowMountVolume(Boolean mountVolume) {
		set("MountVolume", mountVolume);
	}
	
	public Boolean getShowServerManage() {
		return (Boolean)get("ServerManage");
	}
	
	public void setShowServerManage(Boolean ServerManage) {
		set("ServerManage", ServerManage);
	}
	public Boolean getShowAllFeeds() {
//		return (Boolean)get("AllFeeds");
		return false;   // always return false for RSS feed on top of UI. We are removing it in Oolong
	}
	
	public void setShowAllFeeds(Boolean AllFeeds) {
		set("Deploy", AllFeeds);
	}
	
	public Boolean getShowRSSFeed() {
//		return (Boolean)get("RSSFeed");
		return false;	// always return false for RSS feed on top of UI. We are removing it in Oolong
	}
	
	public void setShowRSSFeed(Boolean RSSFeed) {
		set("Deploy", RSSFeed);
	}
	
	public void setShowSocialNetworkHeader(Boolean show) {
		set("snHeader", show);
	}
	
	public Boolean getShowSocialNetworkHeader() {
		Boolean bRet = (Boolean)get("snHeader");
		if(bRet == null) {
			bRet = false;
			if(getShowTwitter() == null || getShowTwitter()
					|| getShowFacebook() == null || getShowFacebook()) {
				bRet = true;
				setShowSocialNetworkHeader(bRet);
				return bRet;
			}
		}
		return bRet;
	}
	
	public void setShowSupportHeader(Boolean show) {
		set("supportHeader", show);
	}
	
	public Boolean getShowLiveChat() {
		return (Boolean)get("LiveChat");
	}
	
	public void setShowLiveChat(Boolean show) {
		set("LiveChat", show);
	}
	

	public void setShowKnowledgeCenter(Boolean show){
		set("KnowledgeCenter",show);
	}
	
	public Boolean getShowKnowledgeCenter(){
		return (Boolean)get("KnowledgeCenter");
	}
	
	public Boolean getShowSupportHeader() {
		Boolean bRet = (Boolean)get("supportHeader");
		if(bRet == null) {
			bRet = false;
			if(getShowVedios() == null || getShowVedios()
					|| getShowSupport() == null || getShowSupport()
					|| getShowFeedback() == null || getShowFeedback()
					//|| getShowAdvice() == null || getShowAdvice()|| getShowCommunity() == null || getShowCommunity()
					|| getShowKnowledgeCenter()||getShowKnowledgeCenter()==null
					|| getShowLiveChat() == null || getShowLiveChat()) {
				bRet = true;
				setShowSupportHeader(bRet);
				return bRet;
			}
		}
		return bRet;
	}
	
	//No Charged Edition
	public Boolean getShowNCE() {
		Boolean bRet = (Boolean)get("NCE");
		if(bRet == null) {
			return true;
		}
		return bRet;
	}
	
	public void setShowNCE(Boolean show) {
		set("NCE", show);
	}
}
