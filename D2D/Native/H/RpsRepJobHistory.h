#ifndef _RPS_REP_JOBHISTORY_H_
#define _RPS_REP_JOBHISTORY_H_

#include "MergeJobHistory.h"
#include "RpsRepJobScript.h"
#include <string>
using std::wstring;

#if 0
struct RepJobHistoryXml
{
    unsigned long   Version;

    unsigned long   JobType;
    unsigned long   JobID;
    unsigned long   JobFlag;            //corresponding to JobEndFlag in jobscript
    wstring         JobName;
    wstring         PolicyGuid;

    //source
    wstring         D2DNodeName;
    wstring         D2DNodeGuid;
    wstring         SrcRoot;
    unsigned long   SessNo;

    //dest
    wstring         DestHostName;
    wstring         DesRoot;

    //protocol details
    wstring         Protocol;
    unsigned long   Port;

    //status details
    wstring         StartTime;
    wstring         EndTime;
    wstring         JobStatus;
    __int64         TotalSize;
    __int64         SentSize;
};
#endif // if 0

// According to Yonghui (lijyo02), currently GUI and Java side use the same structure
// with Merge job, and directly unserialize the xml to their inner structure.
// So we need to write to the same xml format with intermerge job.
struct RepJobHistoryXml
{
    MergeJobHistoryXml Common;
};


DWORD WINAPI RPSWriteRepJobHistoryXml(RepJobHistoryXml & HistoryXml/*, LPCWSTR szXmlFileName*/);
DWORD WINAPI RPSReadRepJobHistoryXml(LPCWSTR pszXmlFileName, RepJobHistoryXml & HistoryXml);

VOID WINAPI RPSRepInitJobScriptToHistoryXml(const RPSRepJobScript & Script, RepJobHistoryXml & RepHistoryXml);
VOID WINAPI RPSRepSetStartTime(RepJobHistoryXml & RepHistoryXml);
VOID WINAPI RPSRepSetEndTimeAndJobStatus(RepJobHistoryXml & RepHistoryXml, LPCTSTR pszJobStatus);






#endif // _RPS_REP_JOBHISTORY_H_