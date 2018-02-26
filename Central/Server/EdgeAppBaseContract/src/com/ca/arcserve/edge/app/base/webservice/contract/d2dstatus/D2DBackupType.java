package com.ca.arcserve.edge.app.base.webservice.contract.d2dstatus;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "D2DBackupType", namespace = "http://webservice.edge.arcserve.ca.com/d2dstatus/")
public enum D2DBackupType
{
	Unknown,
	FullBackup,
	IncrementalBackup,
	ResyncBackup,
}