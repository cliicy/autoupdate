#ifndef D2D_CALLBACK_SIGNATURE__SigFord2dcallback_h
#define D2D_CALLBACK_SIGNATURE__SigFord2dcallback_h
#pragma once
//////////////////////////////////////////////////////////////////////////
//class D2DCallbackEntry
#define	CLS_SIG_CALLBACKENTRY	"com/ca/arcflash/webservice/callback/D2DCallbackEntry"

#define	FUN_ENTRY_CREATE	"<init>"
#define SIG_ENTRY_CREATE	"()V"

#define	FUN_ENTRY_mergeFailureCallback	"mergeFailureCallback"
#define SIG_ENTRY_mergeFailureCallback	"(Lcom/ca/arcflash/webservice/data/callback/MergeFailureInfo;)J"
//////////////////////////////////////////////////////////////////////////
// class MergeFailureInfo
#define  CLS_SIG_MERGEINFOR	"com/ca/arcflash/webservice/data/callback/MergeFailureInfo"

#define	FUN_MERGEINFO_CREATE	"<init>"
#define SIG_MERGEINFO_CREATE	"()V"

#define FUN_MINFO_getMergeStartSessionNumber	"getMergeStartSessionNumber"
#define SIG_MINFO_getMergeStartSessionNumber	"()J"

#define FUN_MINFO_setMergeStartSessionNumber	"setMergeStartSessionNumber"
#define SIG_MINFO_setMergeStartSessionNumber	"(J)V"

#define FUN_MINFO_getMergeEndSessionNumber		"getMergeEndSessionNumber"
#define SIG_MINFO_getMergeEndSessionNumber		"()J"

#define FUN_MINFO_setMergeEndSessionNumber		"setMergeEndSessionNumber"
#define SIG_MINFO_setMergeEndSessionNumber		"(J)V"

#define FUN_MINFO_getFailedStartSession			"getFailedStartSession"
#define SIG_MINFO_getFailedStartSession			"()J"

#define FUN_MINFO_setFailedStartSession			"setFailedStartSession"
#define SIG_MINFO_setFailedStartSession			"(J)V"

#define FUN_MINFO_getFailedEndSession			"getFailedEndSession"
#define SIG_MINFO_getFailedEndSession			"()J"

#define FUN_MINFO_setFailedEndSession			"setFailedEndSession"
#define SIG_MINFO_setFailedEndSession			"(J)V"

#define FUN_INFO_SETJOBID		"setJobID"
#define SIG_INFO_SETJOBID		"(J)V"

#define FUN_INFO_SETSOURCE		"setMergeSource"
#define SIG_INFO_SETSOURCE		"(J)V"

#define FUN_INFO_SET_VMGUID		"setVmInstanceUUID"
#define SIG_INFO_SET_VMGUID		"(Ljava/lang/String;)V"

//////////////////////////////////////////////////////////////////////////
#endif//D2D_CALLBACK_SIGNATURE__SigFord2dcallback_h