package com.ca.arcserve.edge.app.base.webservice.syncmonitor;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.alert.AlertManager;
import com.ca.arcserve.edge.app.base.webservice.contract.synchistory.EdgeSyncComponents;

public class EdgeSyncAlert implements IEdgeAlert {

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}


	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}


	/**
	 * @return the sendTime
	 */
	public Date getSendTime() {
		return sendTime;
	}


	/**
	 * @return the productType
	 */
	public EdgeSyncComponents getComponent() {
		return productTypeToComponent(productType);
	}

	/**
	 * @param productType the productType to set
	 */
	public void setComponent(EdgeSyncComponents component) {
		this.productType = componentToProductType(component);
	}

	/**
	 * @return the overDays
	 */
	public int getOverDays() {
		return overDays;
	}

	/**
	 * @param overDays the overDays to set
	 */
	public void setOverDays(int overDays) {
		this.overDays = overDays;
	}


	@Override
	public void RaiseAlert() {
		

		String productName = EdgeCMWebServiceMessages.getResource("EDGE_SYNC_ALERT_PRODUCT_NAME");
		if (productType == CommonEmailInformation.PRODUCT_TYPE.ARCServe) {
			productName = EdgeCMWebServiceMessages.getResource("EDGE_SYNC_ALERT_PRODUCT_NAME_ASBU");
		} else if (productType == CommonEmailInformation.PRODUCT_TYPE.ARCFlash) {
			productName = EdgeCMWebServiceMessages.getResource("EDGE_SYNC_ALERT_PRODUCT_NAME_D2D");
		}
		content = EdgeCMWebServiceMessages.getResource("EDGE_SYNC_ALERT_MESSAGE_TEMPLATE", productName, hostName, Integer.toString(overDays ) );

		sendTime = Calendar.getInstance().getTime();
		//m_idao.spsrmedgeAlertMessageInsert(hostName, eventType, content, content, sendTime, productType.getValue());
		AlertManager alertMgr = AlertManager.getInstance();
		CommonEmailInformation info = new CommonEmailInformation();
		info.setProtectedNode(hostName);
		info.setEventType( CommonEmailInformation.EVENT_TYPE.SYNC_ALERT.getValue() );
		info.setSubject(content);
		info.setContent(content);
		info.setSendTime(sendTime);
		info.setProductType(productType.getValue());
		alertMgr.SaveAlertInfo(info);
	}

	public EdgeSyncComponents productTypeToComponent(CommonEmailInformation.PRODUCT_TYPE productType) {
		if (productType == CommonEmailInformation.PRODUCT_TYPE.ARCServe) {
			return EdgeSyncComponents.ARCserve_Backup;
		} else if (productType == CommonEmailInformation.PRODUCT_TYPE.ARCFlash) {
			return EdgeSyncComponents.ARCserve_D2D;
		} else {
			return null;
		}
	}

	public CommonEmailInformation.PRODUCT_TYPE componentToProductType(EdgeSyncComponents component) {
		if (component == EdgeSyncComponents.ARCserve_Backup) {
			return CommonEmailInformation.PRODUCT_TYPE.ARCServe;
		} else if (component == EdgeSyncComponents.ARCserve_D2D) {
			return CommonEmailInformation.PRODUCT_TYPE.ARCFlash;
		} else {
			return null;
		}
	}

	//private static IEdgeSrmDao m_idao = DaoFactory.getDao(IEdgeSrmDao.class);
	private String hostName;
	private String content;
	private Date sendTime;
	private CommonEmailInformation.PRODUCT_TYPE productType;
	private int overDays;

}
