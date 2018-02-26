//////////////////////////////////////////////////////////////////////////
//Name: ExchangeGRTVM.h
//
#pragma once

#ifdef HBBUEXCHGRT_OLDSESS_SUPPORT
#include "..\Common\AFDiskParser\CommonDef.h"
#include "VMInfoParser.h"
#include "AFStorInterface.h"
#endif

#define EXCHANGE_BINARIES_FOLDER_IN_SESSION  L"VStore\\S%010d\\ExchBin"

//x is wstring type
#define MAKEPATH_WITH_SLASH(x) \
	if (x.length()>0)	if (x.at(x.length()-1) != L'\\') x+=L"\\";


#define AFSTORDLL	L"AFStor.dll"
#define OpenVHD		"OpenVHD"

#define SECTOR_SIZE_OLD			512
#define SECTOR_SIZE_NEW			4096


#ifdef HBBUEXCHGRT_OLDSESS_SUPPORT
typedef DWORD (*PFAFSTOREOPENVHD)(LPCWSTR pszFileName, D2D_ENCRYPTION_INFO * pEncryptionInfo, IAFVHD ** ppVhd, LPCWSTR lckFilePath);
#endif

const WCHAR swszExchRegPath2013[] = L"\\Microsoft\\ExchangeServer\\v15";	//in software branch
const WCHAR swszExchRegPath2010[] = L"\\Microsoft\\ExchangeServer\\v14";	//in software branch
const WCHAR swszExchRegPath2007[] = L"\\Microsoft\\Exchange\\v8.0";			//in software branch
const WCHAR swszExchRegPath2003[] = L"\\CurrentControlSet\\Services\\EXIFS";	//in system branch
const WCHAR swszExchRegSetup[] = L"Setup";
const WCHAR swszExchRegValueMsiInstallPath[] = L"MsiInstallPath";
const WCHAR swszExchBinFolder[] = L"Bin";

const WCHAR swszExchVolInfFileName[] = L"ExchVoInf.dat";

const WCHAR swszExchTempFolderSurfix[] = L"_VMExch";

//The binary list for the Exchange EDB parser
const WCHAR wsExchFileList[][128] = {
	L"Ese.dll",
	L"Eseutil.exe",
	L"Exchmem.dll",
	L"exosal.dll",
	L"Jcb.dll"
};

//binaries for E15 utility running
const WCHAR wsVMSysFileList[][128] = {
	L"msvcr110.dll",
	L"msvcp110.dll",
	L"msvcr100.dll",
	L"msvcp100.dll",
	L"msvcr90.dll",
	L"msvcm90.dll",
	L"msvcp90.dll"
};


enum enum_Windows_PLATFORM_Type
{
	WINDOWS_PLF_UNKNOWN			= 0,
	WINDOWS_PLF_AMD64			= 1,
	WINDOWS_PLF_X86				= 2
}; 

enum enum_Exch_Vol_Type
{
	EXCH_VOL_TYPE_UNKNOW	= 0,
	EXCH_VOL_TYPE_INST		=0x01,
	EXCH_VOL_TYPE_DB		=0x02
};


typedef struct _Exch_Vol_Inf
{
	GUID	volumeID;
	DWORD	dwDrvLetter;	//volume driver letter
	DWORD	dwExchVolType;	//enum_Exch_Vol_Type
	DWORD	dwFileSystem;
	DWORD	IsNtfsDedup;	//0 or 1
}EXCH_VOL_INF, *pEXCH_VOL_INF;


typedef struct _Exch_Vol_Inf_File
{
	DWORD	dwVolCount;
	vector<EXCH_VOL_INF> vExchVolInfList;
	WCHAR	szExchInstPath[260];
}EXCH_VOL_INF_FILE, *pEXCH_VOL_INF_FILE;



class CCommConverter
{
public:
	static std::wstring GuidToString(const GUID guid);

	//The format should be L"{XXXX---}"
	static GUID StringToGUID(const wstring wsGuid);
};

//////////////////////////////////////////////////////////////////////////
//* Class COffSessionDiskIO is used to parse special files from backup session.
//* In order to support old session for HBBU Exchange GRT (means with the old HBBU backup session, whose VM is 
//* Exchange Server installed, Exchange GRT can restore the mail from the old sessions), catalog job 
//* will use this class parsing Exchange binaries from the old session, and then using the binaries
//* to generate Exchange GRT catalog.
//*
//* The basic implementation is:
//*		1. Create a new class derived from AFDiskParser::IAFDiskIO, implement all interfaces;
//*		2. CVMInfoParser::SetDiskGroup(...)
//*		3. CVMInfoParser::ParseVMInformation
//*
//* Example:
//*		vector<AFDiskParser::IAFDiskIO*> vpDiskIO;
//*		COffSessionDiskIO* pDiskIO = new COffSessionDiskIO();
//*
//*		WCHAR wsParamD2DFile[] = L"E:\\UDPDedupDst\\c\\chegu02-ex15-2@qijfe01-2950[52aec8f4-1294-4975-427a-c520bcb5c0c2]\\VStore\\S0000000034\\disk0126184003.D2D";
//*		D2D_ENCRYPTION_INFO stParamEncInf = {0};
//*		WCHAR wsParamLckFile[] = L"";
//*		
//*		pDiskIO->InitVHDParser(wsParamD2DFile, NULL, wsParamLckFile);
//*		vpDiskIO.push_back(pDiskIO);
//*		
//*		CVMInfoParser ll_VMInfoParser;
//*		ll_VMInfoParser.SetDiskGroup(vpDiskIO);
//*		ll_VMInfoParser.ParseVMInformation();
//*
//////////////////////////////////////////////////////////////////////////

#ifdef HBBUEXCHGRT_OLDSESS_SUPPORT
class COffSessionDiskIO: public AFDiskParser::IAFDiskIO
{
private:
	wstring m_wsBkSessPath;
	ULONGLONG m_ullReadOffset;
	IAFVHD* m_pVHD;

public:
	COffSessionDiskIO();
	~COffSessionDiskIO();

private:
	DWORD CalDiskGeometryInfo(const ULONGLONG ullTotalSectors, DWORD& ulCylinders, DWORD& ulSectorsPerTrack);

public:
	void SetBackupSessionPath(const wstring wsNewPath) {m_wsBkSessPath = wsNewPath;}

	DWORD InitVHDParser(const WCHAR* pszSesPath, D2D_ENCRYPTION_INFO* pEncInf, const WCHAR* pszLckFile);
	BOOL Close();

	virtual LPCTSTR GetDescription() const {return L"";}
	virtual LPCTSTR GetARCPath();

	virtual ULONG GetSectorSize();
	virtual DWORD GetGeometry(PDISK_GEOMETRY pDiskGeometry);
	virtual AFDiskParser::E_DISK_BUS_TYPE GetDiskBusType();
	
	virtual ULONGLONG GetDiskSize();
	
	virtual DWORD Seek(ULONGLONG nNumberOfSector);
	virtual DWORD Read(void* pDest, ULONG nNumberOfSector = 1);
};
#endif

class CExchangeGRTVMManager
{
public:
	CExchangeGRTVMManager() {};
	~CExchangeGRTVMManager() {};

private:
	static CExchangeGRTVMManager g_ExchGRTVMManagerObj;

public:
	static CExchangeGRTVMManager GetInstance();

public:
	//////////////////////////////////////////////////////////////////////////
	//* From The registry entrance, checking the Exchange server version in the system
	//*
	DWORD GetStaticExchangeVersion(wstring strSoftwareEntry=L"SOFTWARE", wstring strSystemEntry=L"SYSTEM");

	//////////////////////////////////////////////////////////////////////////
	//* Get Exchange Server installation path in registry
	//*
	DWORD GetStaticExchangeInstallPath(WCHAR* pszInstallPath, DWORD dwPathBufSizeInChar, 
							wstring strSoftwareEntry=L"SOFTWARE", wstring strSystemEntry=L"SYSTEM");
	
	//////////////////////////////////////////////////////////////////////////
	//* Save VM volume information to ExchVolInf.dat file
	//
	DWORD SaveExchVolumeInfo(EXCH_VOL_INF_FILE exchVolFileInf, const WCHAR* szDestPath);


	//////////////////////////////////////////////////////////////////////////
	//* According to Exchange DB path, update the matching item ExchVolInf.dat file 
	//
	DWORD UpdateExchDBVol(const wstring wsExchDBPath, const wstring wsExchVolFilePath);


	//////////////////////////////////////////////////////////////////////////
	//* Get Exch information from vol file path
	//
	DWORD GetExchInstVolInf(const wstring wsExchVolFilePath, 
				EXCH_VOL_INF& exchInstVolInf, WCHAR* pszExchInstPath, UINT unPathBufSizeInChar);
	
	//////////////////////////////////////////////////////////////////////////
	//* Copy all the Exchange binaries from UDP temp folder to destination session folder
	//* The binaries is in the temp\VMBootVolGuid_surfix folder, so need the vmBootVolGUID
	//*
	DWORD CopyExchBinaryFromTempToSess(const WCHAR* szDstPath, DWORD dwSessNumber, wstring strVMUUID /*GUID vmBootVolGUID = GUID_NULL*/);

	//////////////////////////////////////////////////////////////////////////
	//* Get VMDK System WIndows operating system information
	//* Including Windows Version, and AMD64/X86 platform
	//*
	DWORD GetStaticWinInfo(const wstring strSoftwareEntry, DWORD& dwMajorVer, DWORD& dwMinorVer, DWORD& dwWinPltType);


	//////////////////////////////////////////////////////////////////////////
	//* Get VM Windows name
	//*
	DWORD GetStaticWinName(const wstring strSystemEntry, WCHAR* szComputerName, DWORD dwNameBufSizeInChar);

	//////////////////////////////////////////////////////////////////////////
	//* Checking Exchange installation status according to the input registry entrance
	//*
	BOOL IsExchangeInstalled(wstring strSoftwareEntry=L"SOFTWARE", wstring strSystemEntry=L"SYSTEM");


	//////////////////////////////////////////////////////////////////////////
	//Checking current Windows system support REFS file system or not.
	//
	BOOL IsCurSysSupportRefs();


	//////////////////////////////////////////////////////////////////////////
	//Checking whether current Windows system has installed NTFS dedup feature.
	//
	BOOL IsCurSysSupportNTFSDedup();

	//////////////////////////////////////////////////////////////////////////
	//by default, all the Exchange binaries will be deleted after copy them to backup sessions. 
	//but if there is a configuration switch, the files and the folder will not be removed.
	//Path: HKEY_LOCAL_MACHINE\SOFTWARE\CA\ARCserve Unified Data Protection\Engine\Exchange
	//Name: ReserveVMExchBinary
	//Value: >=1 should do not remove the binary, no the switch or other value will delete the binaries, this is default behavior.
	//
	BOOL IsReserveVMExchBinary();


	//////////////////////////////////////////////////////////////////////////
	//Check Exchange binaries in the VM is parsed succeed or not.
	//only check kernel files: ese.dll and eseutil.exe
	//Parameter: strVMUUID: the temp parsed Exchange binaries location.
	//
	BOOL IsExchBinaryParSucceed(wstring strVMUUID);
};


