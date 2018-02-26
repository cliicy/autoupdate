#pragma once

#include "AFDefine.h"
#include "IXmlParser.h"

#include <dbglog.h>
#include "MergeJobHistory.h"

class CMergeJobHistory
{
private:
	CDbgLog *m_pLog;
	HMODULE m_hXml;

	typedef DWORD (WINAPI *_pfn_CreateIXmlParser)(IXmlParser **ppIXMLParser);
	_pfn_CreateIXmlParser m_pfnCreateIXmlParser;

	IXmlParser *m_pXmlParser;

public:
	CMergeJobHistory(CDbgLog *pLog);
	~CMergeJobHistory();

	DWORD WriteJobHistoryToXml(IN CONST MergeJobHistoryXml & HistoryXml, LPCWSTR pszXmlFile);
	DWORD ReadJobHistoryFromXml(OUT MergeJobHistoryXml & HistoryXml, LPCWSTR pszXmlFile);


private:
	void Log(DWORD errNo, int level, wchar_t *Str, ...);
	DWORD GetXmlParserApi();

public:
	DWORD GetMgergeHistoryPath(wstring &strDir, IN CONST MergeJobHistoryXml & HistoryXml);
	DWORD GetLastHistoryNumber(DWORD &dwNum, CONST wstring & strDir/*IN CONST MergeJobHistoryXml & HistoryXml*/); //<sonmi01>2011-3-16 edge vminfo.xml not synced #20121594;
	DWORD ComposeXmlFullFilePath(wstring & strFullXmlPath , CONST wstring &strDir, IN CONST MergeJobHistoryXml & HistoryXml);
	

public:
	static VOID JobHistoryToXmlNode(IN CONST MergeJobHistoryXml & HistoryXml, OUT XML_NODE & XmlNode, IN LPCWSTR pszRootNodeName);
	static VOID XmlNodeToJobHistory(OUT MergeJobHistoryXml & HistoryXml, IN CONST XML_NODE & XmlNode, IN LPCWSTR pszRootNodeName);
};
