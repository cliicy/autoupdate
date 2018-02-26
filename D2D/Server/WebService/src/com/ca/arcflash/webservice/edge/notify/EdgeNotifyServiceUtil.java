/**
 * 
 */
package com.ca.arcflash.webservice.edge.notify;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.listener.manager.ListenerManager;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DStatus;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo;
import com.ca.arcflash.webservice.data.listener.FlashListenerInfo.ListenerType;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dreg.PlanUtil;
import com.ca.arcflash.webservice.service.CommonService;

/**
 * @author lijwe02
 * 
 */
public class EdgeNotifyServiceUtil {
	private static final Logger logger = Logger.getLogger(EdgeNotifyServiceUtil.class);
	private static long SLEEP_INTERVAL= 60*1000L;

	public static void notifyD2DVersion() {
		Thread thread = new Thread() {

			@Override
			public void run() {
				boolean flag = false;
				while(!flag){
					logger.info("Start to notify d2d version...");
					flag = CommonService.getInstance().notifyD2DVersion();
					try {
						Thread.sleep(SLEEP_INTERVAL);
					} catch (InterruptedException e) {
						break;
					}
				}
				logger.info("notify d2d version finished.");
				//add edge as a listener
				D2DEdgeRegistration ereg = new D2DEdgeRegistration();
				EdgeRegInfo edgeRegInfo = ereg.getEdgeRegInfo(ApplicationType.CentralManagement);
				if(edgeRegInfo==null||StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeWSDL()) || StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeUUID())){
					//stand alone, not do any things.
				}else{//managed by Console
					logger.info("registering Console as listener...");
					FlashListenerInfo listener = FlashListenerInfo.createListenerInfo(ListenerType.CPM, edgeRegInfo.getEdgeWSDL(), edgeRegInfo.getEdgeUUID());
					ListenerManager.getInstance().addFlashListener(listener);
					D2DStatus d2dStatus = CommonService.getInstance().checkD2DStatusFromEdgeCM();
					logger.info("D2DStatus is "+d2dStatus.name());
					switch (d2dStatus) {
					case NodeDeleted:
						logger.info("clean registration info ...");
						PlanUtil.cleanRegInfo4CM();
					case NoPolicy:
						logger.info("clean plan info ...");
//						PlanUtil.cleanPlanInfo4CM();
						PlanUtil.cleanPlan();
					case Ok:
					case PolicyChanged:
					case StandAlone:
					default:
					}
				}
				
			}

		};
		thread.setName("NotificationThread");
		thread.start();
	}
}
