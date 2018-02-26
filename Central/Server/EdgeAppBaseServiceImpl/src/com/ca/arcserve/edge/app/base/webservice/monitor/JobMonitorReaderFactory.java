package com.ca.arcserve.edge.app.base.webservice.monitor;

import com.ca.arcserve.edge.app.base.webservice.monitor.model.JobDetail.SourceType;



public class JobMonitorReaderFactory {
	public static JobMonitorReader getReader(SourceType source){
		if(source == SourceType.ASBU){
			return ASBUJobMonitorReader.getInstance();
		}else if (source == SourceType.D2D) {
			return D2DJobMonitorReader.getInstance();
		}else if (source == SourceType.RPS) {
			return RPSJobMonitorReader.getInstance();
		}else if (source == SourceType.LINUXD2D) {
			return LinuxD2DJobMonitorReader.getInstance();
		}
		return null;
	}
}
