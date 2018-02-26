//////////////////////////////////////////////////////////////////////////
// Name: AgPkiStruct.h
// 

#pragma once


//PKI Alert Policy structure
typedef struct _PKIAlertStruct
{
	int nCPUInterval;
	int nCPUThreshold;
	int nCPUSamplingAmount;
	int nCPUMaxAlertNum;

	int nMemoryInterval;
	int nMemoryThreshold;
	int nMemorySamplingAmount;
	int nMemoryMaxAlertNum;

	int nDiskInterval;
	int nDiskThreshold;
	int nDiskSamplingAmount;
	int nDiskMaxAlertNum;

	int nNetworkInterval;
	int nNetworkThreshold;
	int nNetworkSamplingAmount;
	int nNetworkMaxAlertNum;
}PKIAlertStruct, *pPKIAlertStruct;


typedef struct _SRMValidation
{
	int nSRMEnabled;  //SRM_VALIDATION_ENABLE: SRM enable; SRM_VALIDATION_DISABLE or others: SRM disable
	int nAlertEnabled;  //SRM_VALIDATION_ENABLE: PKI alert enable; SRM_VALIDATION_DISABLE or others: PKI alert disable
	int nPKIUtlEnabled;  //SRM_VALIDATION_ENABLE: PKI Utilization enable; SRM_VALIDATION_DISABLE or others: PKI Utilization disable
}SRMValidation, *pSRMValidation;


typedef struct _SRMPKIInfo
{
	PKIAlertStruct stAlertPolicyStruct;  //PKI alert policy

	int nUsingGlobalPolicy; //PKI alert policy using global policy?  ASDBPKIAlert_USEGLOBALPOLICY.
	int nAppliedStatus; //Status records alert policy and SRM validation applied result.
						//ASDBPKIAlertApplicedStatus
	int nAppliedCode; //applied code, 0: applied successfully; -1: not apply; others: error code.
	ULONG lUpdateTime; //policy applied time

	SRMValidation stSRMValidStruct; //SRM validation status
}SRMPKIInfo, *pSRMPKIInfo;


enum  SRMValidationType 
{
	SRM_VALIDATION_DISABLE =0,
	SRM_VALIDATION_ENABLE
};

enum AlertType{
	TYPE_ALL = 0,
	TYPE_CPU,
	TYPE_MEMORY,
	TYPE_DISK,
	TYPE_NETWORK
};

enum ConfigureType{
	CONF_TYPE_INTERVAL = 1,
	CONF_TYPE_THRESHOLD,
	CONF_TYPE_SAMPLINGAMOUNT,
	CONF_TYPE_MAXALERTNUM
};


//////////////////////////////////////////////////////////////////////////
typedef struct _SRMCfgStructExt
{
	PKIAlertStruct		stPkiAlertInfo;
	unsigned long		ulServerPolicyUpdateTime;
}SRM_CONFIGURE_STRUCT_EXT, *pSRM_CONFIGURE_STRUCT_EXT;

enum SRM_VALIDATION_COMMAND {
	SRM_NONE = 0,
	SRM_TOTAL_DISABLE,
	SRM_ALERT_ENABLE,
	SRM_ALERT_DISABLE,
	SRM_PKI_UTL_ENABLE,
	SRM_PKI_UTL_DISABLE
};
