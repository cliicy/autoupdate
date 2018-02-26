package com.ca.arcserve.edge.app.base.appdaos;

import javax.xml.bind.JAXBException;

import com.ca.arcserve.edge.app.base.util.CommonUtil;




public class EdgeDiscoverySetting {
	private String XMLContent;
	private int scheduleid;
	private int settingType;

	public int getScheduleid() {
		return scheduleid;
	}

	public void setScheduleid(int scheduleid) {
		this.scheduleid = scheduleid;
	}

	public String getXMLContent() {
		return XMLContent;
	}

	public void setXMLContent(String xMLContent) {
		XMLContent = xMLContent;
	}
	public static EdgeDiscoverySettingModel getModel(String xMLContent) throws JAXBException{
		return CommonUtil.unmarshal(xMLContent, EdgeDiscoverySettingModel.class);
	}
	public static String getString(EdgeDiscoverySettingModel setting) throws JAXBException{
		return CommonUtil.marshal(setting);
	}

	public int getSettingType() {
		return settingType;
	}

	public void setSettingType(int settingType) {
		this.settingType = settingType;
	}
}
