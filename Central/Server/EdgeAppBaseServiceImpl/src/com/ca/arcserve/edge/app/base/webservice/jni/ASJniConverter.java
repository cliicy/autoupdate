package com.ca.arcserve.edge.app.base.webservice.jni;

import com.ca.arcflash.service.jni.model.JActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;

public class ASJniConverter {
	public static ActivityLog D2DActivityLog2EdgeActivityLog( JActivityLog d2dLog ){
		if( null == d2dLog )
			return null;
		ActivityLog edgeLog = new ActivityLog();
		edgeLog.setMessage( d2dLog.getMessage() );
		switch ((int)d2dLog.getLevel()) {
		case 0:
		case 3:
			edgeLog.setSeverity(Severity.Information);
			break;
		case 1:
		case 4:
			edgeLog.setSeverity(Severity.Warning);
			break;
		case 2:
		case 5:
			edgeLog.setSeverity(Severity.Error);
			break;
		default:
			edgeLog.setSeverity(Severity.Information);
			break;
		}
		return edgeLog;
	}
}
