#ifndef _CATALOG_JOBHISTORY_H_
#define _CATALOG_JOBHISTORY_H_

#include "MergeJobHistory.h"
#include "Catalogjobscript.h"
#include <string>
using std::wstring;


// According to Yonghui (lijyo02), currently GUI and Java side use the same structure
// with Merge job, and directly unserialize the xml to their inner structure.
// So we need to write to the same xml format with intermerge job.
struct CatJobHistoryXml
{
    MergeJobHistoryXml Common;
};


DWORD WINAPI CatWriteCatJobHistoryXml(CatJobHistoryXml & HistoryXml/*, LPCWSTR szXmlFileName*/);
DWORD WINAPI CatReadCatJobHistoryXml(LPCWSTR pszXmlFileName, CatJobHistoryXml & HistoryXml);

VOID WINAPI CatInitJobScriptToHistoryXml(const CatJobScript & Script, CatJobHistoryXml & CatHistoryXml);
VOID WINAPI CatSetStartTime(CatJobHistoryXml & CatHistoryXml);
VOID WINAPI CatSetEndTimeAndJobStatus(CatJobHistoryXml & CatHistoryXml, LPCTSTR pszJobStatus);






#endif // _RPS_REP_JOBHISTORY_H_