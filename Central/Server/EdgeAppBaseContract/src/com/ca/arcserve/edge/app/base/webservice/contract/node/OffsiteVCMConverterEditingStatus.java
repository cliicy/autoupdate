package com.ca.arcserve.edge.app.base.webservice.contract.node;

public enum OffsiteVCMConverterEditingStatus
{
	NoChange,
	NeedInformation,
	InvalidPort,
	Modified,
	Updating,
	UpdatedOK,
	UpdatedFailed_Unreachable,
	UpdatedFailed_UnsatisfiedVersion,
	UpdatedFailed_LoginFailed,
	Cancelled,
}
