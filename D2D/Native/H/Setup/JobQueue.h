/////////////////////////////////////////////////////////////////////////////
// JobQueue.h		CJobQueue Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _JOBQUEUE
#define _JOBQUEUE

#include <asdefs.h>
#include <qsi.h>
#include <asqjob.h>

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CJobQueue
#else
class __declspec(dllimport) CJobQueue
#endif
{
private:
    BOOL    m_bExists;

public:
    // Constructor
    CJobQueue();

    // Attributes
    BOOL Exists() {return m_bExists;}

    static BOOL  QueueExists();
    static BOOL  CreateQueue(LPTSTR lpHomeDir);
    static BOOL  DeleteQueue();
    static BOOL  GetQueueJobs(LPTSTR pszMachine, ULONG ulQueueID, PQJOBITEM pQJobItemList, ULONG ulBufSize, PULONG pnJobItems, PQUEUEINFO pQueueInfoRet);
    static ULONG GetQueueJobStatus(ULONG ulTaskID, ULONG ulJobControlFlags);
    static BOOL  AddQueueJob(LPTSTR lpszServerName, LPTSTR lpszQueue, USHORT usLevel, PTCHAR pBuf, ULONG ulBufSize, PULONG pulJobID);
    static BOOL  DeleteQueueJob(LPTSTR lpszServerName, ULONG ulQueueID, ULONG ulJobID);
    static BOOL  GetDefaultQueue(LPTSTR lpszServerName, LPTSTR lpszDefaultQueue, PULONG pulDefaultQueueID);
	static CString GetPath();
};
#endif
