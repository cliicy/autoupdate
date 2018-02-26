#pragma once

#include "AFDefine.h"
//#include "AFCoreFunction.h"
#include "IXmlParser.h"
#include "DbgLog.h"

namespace NS_VCM
{
	CONST TCHAR XML_PARSER_MODULE[] = L"AFXmlParser.dll";
	CONST TCHAR XML_FILE_NAME[] = L"VMDiskInfo.xml";
	CONST TCHAR XML_FILE_PREFIX[] = L"VMDiskInfo";

	typedef struct tagDiskXml
	{
		wstring attr_Size;
		wstring attr_BusType;
		wstring attr_PartitionStyle;
		wstring attr_DiskSignature;
		wstring attr_vmDiskUrl;
		wstring attr_diskUnitNumber;
		wstring attr_DiskGuid; //<sonmi01>2013-1-15 #VCM disk partition style, GPT disk GUID
		wstring attr_DiskPage83Id;

		VOID ToXmlNode(XML_NODE & DiskXmlNode) CONST;
	} DiskXml, *PDiskXml;

	typedef struct tagDiskPropertyXml
	{
		vector<DiskXml> Disks;

		VOID ToXmlNode(XML_NODE & DiskPropertyXmlNode) CONST;
	}DiskPropertyXml, *PDiskPropertyXml;

	typedef struct tagDiskLayoutXml
	{
		DiskPropertyXml DiskProperty;

		VOID ToXmlNode(XML_NODE & DiskLayoutXmlXmlNode) CONST;
	}DiskLayoutXml, *PDiskLayoutXml;

	typedef struct tagClientServerXml
	{
		DiskLayoutXml DiskLayout;

		VOID ToXmlNode(XML_NODE & ClientServerXmlNode) CONST;
	}ClientServerXml, *PClientServerXml;

	typedef struct tagBackupServerXml
	{
		VOID ToXmlNode(XML_NODE & BackupServerXmlNode) CONST;
	}BackupServerXml, *PBackupServerXml;

	typedef struct tagDrInfoXml
	{
		wstring attr_Version;
		wstring attr_BackupInfoExist;
		BackupServerXml BackupServer;
		ClientServerXml ClientServer;

		VOID ToXmlNode(XML_NODE & DrInfoXmlNode) CONST;
	}DrInfoXml, *PDrInfoXml;


	typedef DWORD (WINAPI *_pfn_CreateIXmlParser)(IXmlParser **ppIXMLParser);

	class AFVCMVMDiskInfo
	{
	public:
		AFVCMVMDiskInfo(CDbgLog *pLog);
		~AFVCMVMDiskInfo();
		DWORD UpdateVmInfoXml(const DrInfoXml &vcmDiskInfo, const wstring &strFile);

	private:
		void Log(DWORD errNo, int level, wchar_t *Str, ...);
		DWORD GetXmlParserApi();

	private:
		CDbgLog *m_pLog;
		HMODULE m_hXml;
		_pfn_CreateIXmlParser m_pfnCreateIXmlParser;
		IXmlParser *m_pXmlParser;
	};
}

//API....
DWORD WINAPI AFWriteVCMVMDiskXml(NS_VCM::PDrInfoXml pXml, LPCWSTR szXmlFileName);