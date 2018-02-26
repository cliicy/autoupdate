package com.ca.arcserve.edge.app.base.webservice.monitor;

import com.ca.arcflash.listener.service.event.FlashEvent.Source;
import com.ca.arcserve.edge.app.base.webservice.monitor.model.ASBUJobMonitor;

/**
 * Job monitor factory
 * 
 * @author zhati04
 *
 */
public class JobMonitorFactory {
	public static JobMonitor getJobMonitor(Source source){
		if(source == Source.ASBU){
			return new ASBUJobMonitor();
		}
		throw new RuntimeException("cannot handle this source: " + source);
	}
}
