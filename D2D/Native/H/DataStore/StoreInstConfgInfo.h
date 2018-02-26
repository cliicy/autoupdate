#pragma once

#include <Windows.h>
#include <string>
#include <atlstr.h>
#include "DataStoreConfDef.h"

using namespace std;


#define SAFE_CLOSE_CONFIGFILE(hdsConfFile)  if (hdsConfFile != INVALID_HANDLE_VALUE) { CloseHandle(hdsConfFile); hdsConfFile = NULL; }

class CStoreInstConfgInfo
{
public:
	CStoreInstConfgInfo();
	CStoreInstConfgInfo(const wchar_t* pszCommStorePath);
	virtual ~CStoreInstConfgInfo(void);

public:
    long ExportKeyFile(const STORE_CONF& stStoreConf, const wchar_t* pszEncPath=NULL); //using common path when pszEncPath is NULL
	long WirteDataStoreConf(STORE_CONF& storeconf, DWORD dwDSCreateBuild, char* pszConfHash, STORECONF_OWNER* pCfgOwner, DWORD dwUsageTag = 1);
	long ReadDataStoreConf(PSTORE_CONF* ppStoreConf, STORECONF_HEADER* pCfgHeader = NULL, STORECONF_OWNER* pCfgOwner = NULL);
	long ReadDataStoreFolderUsage(DWORD& dwUsageTag);
	DWORD IsDataStoreconfExist(BOOL& bExist);
	long DeleteDataStoreConf();
	long DeleteBakDataStoreConf();
	long MakeBakDataStoreConfAsCurrent();
	long BackupCurrentDataStoreConf();
	static long ReleaseDataStoreConf(PSTORE_CONF pStoreConf);
	long MarkDataStoreUsageTag(DWORD dwUsageTag);

	long ReadDataStoreConfFromBuf(PSTORE_CONF* ppStoreConf, unsigned char *pCfgFileBuf, DWORD dwBufLen);

	long pack(PSTORE_CONF pstoreconf);

	BYTE* GetStream() {return m_pStream;}
	DWORD GetStreamSize() {return m_dwStreamSize;}

	void SetDSGUID(wstring strDSGUID) {m_strDSGUID = strDSGUID;}
	long GetOwnerShip(STORECONF_OWNER* pCfgOwner);
	long SetOwnerShip(const STORECONF_OWNER* pCfgOwner);  //If the owner content is empty, remove the ownership in the configuration file
	
	long GetDSHeader(STORECONF_HEADER &stDSHeader);

	static long ResetDSDedupHost(const byte *pbOrgDSConfigFileBuf, const DWORD dwInputBufSize, 
		const wchar_t *pwszNewHostNameOrIP, byte **pbNewDSConfigFileBuf, DWORD& dwNewBufSize);
	
private:
	long unPack(PSTORE_CONF pstoreconf,LPBYTE pStream, DWORD dwSize, DWORD dwBuild = 0);
	BOOL bIsShareFolder(const wstring& strFolder);
	long OpenStoreFile(DWORD dwMode,HANDLE& storeHandle);
	long CreateFolders(const wstring& strPath); 
    BOOL IsBakDataStoreConfExist();

private:
	static BOOL IslittleEnd();
	 long d4stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, DWORD dwdata);
	 long stream2d4(const BYTE * stream ,const DWORD streamLen, DWORD & dwIndex, DWORD & dwdata);
	 long d8stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, DWORD64 dwdata);
	 long stream2d8(const BYTE * stream ,const DWORD streamLen, DWORD & dwIndex, DWORD64 & dwdata);
	 long wstr2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, const WCHAR * pszData);
	 long stream2wstr(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, WCHAR ** ppszData, WORD & strsize);
	 long binary2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, const BYTE * pbzData, WORD dwsizeOfdata);
	 long stream2binary(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, BYTE ** ppbzData, WORD & dwsizeOfdata);

	 DWORD GetSize(PSTORE_CONF pStoreConf);
	 DWORD GetSize(PCOMM_STORECONF pCommConf);
	 DWORD GetSize(PDATASTORE_NGDD_CONF pDSNGDDConf);
	 DWORD GetSize(PDATASTORE_GDD_CONF pDSGDDConf);
	 DWORD GetSize(PDATASTORE_GDD_CONF_PRIMARYROLE pPrimConf);
	 DWORD GetSize(PDATASTORE_GDD_CONF_HASHROLE pHashConf);
	 DWORD GetSize(PDATASTORE_GDD_CONF_DATAROLE pDataConf);

	 union  d2
	 {
		 short n2;
		 WORD  w2;
		 BYTE data[2];
	 };

	 union d4
	 {
		 int n4;
		 DWORD w4;
		 long l4;
		 float f4;
		 BYTE data[4];
	 };

	 union d8
	 {
		 DWORD64 w8;
		 DATE	date8;
		 double	double8;
		 BYTE data[8];
	 };

private: 
	wstring m_strDSCommonPath;  //Data store common path
	LPBYTE  m_pStream;
	DWORD   m_dwStreamSize;

	wstring m_strDSGUID;
};



class CDataStoreConfigFactory
{
public:
    CDataStoreConfigFactory();
    ~CDataStoreConfigFactory(void);

    static wstring FormatGUID(const GUID& guid);
    static wstring GetDSPathFromSessPath(wstring& wsSessPath);
    static wstring GetDSPathFromFilePath(const wstring & wsFilename);
    static DWORD AchieveDataStoreConfig(wstring& wsDataStorePath, UCHAR *pDataStoreBuf, DWORD dwBufLen, PSTORE_CONF& pDataStoreConfig);
    static DWORD ReleaseDataStoreConfig(PSTORE_CONF pDataStoreConfig);

private:
    static VOID  DumpDataStoreConfig(PSTORE_CONF pDataStoreConfig);
};

