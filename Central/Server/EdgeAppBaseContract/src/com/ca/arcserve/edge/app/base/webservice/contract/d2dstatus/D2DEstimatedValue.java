package com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "D2DEstimatedValue", namespace = "http://webservice.edge.arcserve.ca.com/d2dstatus/")
public enum D2DEstimatedValue {
	Unknown,
	FLASE,
	TRUE,
}
