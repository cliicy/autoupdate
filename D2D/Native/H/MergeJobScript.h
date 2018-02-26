#pragma once

#include <Windows.h>
#include <tchar.h>
#include <malloc.h>

#include "AFJob.h"
#include "RPSRepJobScript.h"

//#define AF_JOBTYPE_INTER_MERGE				16	//<sonmi01>2011-8-5 Merge job script
//#define AF_JOBTYPE_INTER_MERGE_STRING       L"Intermediate Merge Job"

#define AF_MERGE_FLAG_MAKEUP_JOB    (ULONG)(1 << 0)     // means this is a makeup job. the xml is:

struct MergeJobScript
{
	ULONG Version;
	ULONG JobID;
    ULONG Flag;

	ULONG JobType;
	WCHAR * JobTypeString;
	WCHAR * JobName;
		
	WCHAR * D2DNodeName;
	WCHAR * D2DNodeGUID;
	WCHAR * PolicyGUID; //<sonmi01>2011-8-5 Merge job script
	WCHAR * pRPSName;
    WCHAR * MakeupJsPath;   // If is makeup job,  this means the real makeup jobscript file path.
	
	WCHAR * DestinationRootpath;
	WCHAR * DestinationUserName;
	WCHAR * DestinationPassword;

	ULONG BeginSession;
	ULONG EndSession;

	WCHAR * SourceDataStore;
	WCHAR * SourceDataStoreName;
	WCHAR * TargetDataStore;
	WCHAR * TargetDataStoreName;

	GDDInformation gInfo;
	//RPSDsAttr SrcDs;            // source data store
    RPSDsAttr DesDs;            // destination data store
	WCHAR * PolicyName;
    ULONG   MaxMackupRetryTimes; //max retry count for makeup jobs

	// only used by afcoreinterface to fee allocated memory, please make sure JNI allocating memory use malloc(...)
	VOID FreeMembers()
	{
#define MJS_FREE_MEM(x) {if(x) {free(x); x = NULL;}}

		MJS_FREE_MEM(JobTypeString);
		MJS_FREE_MEM(JobName);
		MJS_FREE_MEM(D2DNodeName);
		MJS_FREE_MEM(D2DNodeGUID);
		MJS_FREE_MEM(PolicyGUID);
        MJS_FREE_MEM(pRPSName);
        MJS_FREE_MEM(MakeupJsPath);
		MJS_FREE_MEM(DestinationRootpath);
		MJS_FREE_MEM(DestinationUserName);
		MJS_FREE_MEM(DestinationPassword);
		MJS_FREE_MEM(SourceDataStore);
		MJS_FREE_MEM(SourceDataStoreName);
		MJS_FREE_MEM(TargetDataStore);
		MJS_FREE_MEM(TargetDataStoreName);
		MJS_FREE_MEM(PolicyName);
	}

    static void DeepCopy(const MergeJobScript& src, MergeJobScript& des)
    {
        des.Version             = src.Version;
        des.JobID               = src.JobID;
        des.Flag                = src.Flag;

        des.JobType             = src.JobType;
        des.JobTypeString       = _wcsdup(src.JobTypeString);
        des.JobName             = _wcsdup(src.JobName);
    		
        des.D2DNodeName         = _wcsdup(src.D2DNodeName);
        des.D2DNodeGUID         = _wcsdup(src.D2DNodeGUID);
        des.PolicyGUID          = _wcsdup(src.PolicyGUID);
		des.PolicyName			= _wcsdup(src.PolicyName);
        des.pRPSName            = _wcsdup(src.pRPSName);
        des.MakeupJsPath        = _wcsdup(src.MakeupJsPath);
    	
        des.DestinationRootpath = _wcsdup(src.DestinationRootpath);
        des.DestinationUserName = _wcsdup(src.DestinationUserName);
        des.DestinationPassword = _wcsdup(src.DestinationPassword);

        des.BeginSession         = src.BeginSession;
        des.EndSession           = src.EndSession;
		des.SourceDataStore     = _wcsdup(src.SourceDataStore);
		des.SourceDataStoreName = _wcsdup(src.SourceDataStoreName);
		des.TargetDataStore     = _wcsdup(src.TargetDataStore);
		des.TargetDataStoreName = _wcsdup(src.TargetDataStoreName);
		des.DesDs.DSName        = _wcsdup(src.DesDs.DSName);
		des.DesDs.DisplayName   = _wcsdup(src.DesDs.DisplayName);
		des.gInfo.RawSize = src.gInfo.RawSize;
		des.gInfo.CompressedSize = src.gInfo.CompressedSize;
		des.gInfo.CompressRatio = src.gInfo.CompressRatio;
		des.gInfo.CompressPercentage = src.gInfo.CompressPercentage;

        des.MaxMackupRetryTimes = src.MaxMackupRetryTimes;
    }
};
