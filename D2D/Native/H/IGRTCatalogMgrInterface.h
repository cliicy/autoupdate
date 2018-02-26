#pragma once
#include <string>
#include <vector>
using namespace std;

#define GRT_CATALOG_INDEX_FILENAME_FORMAT     L"GRT_%u.xml"
#define GRT_CATALOG_FILENAME_FORMAT           L"GRT%u.%u_%u.CAT"
#define GRT_CATALOG_FOLDER_NAME_FORMAT        L"Catalog\\S%010d\\GRT_%u"
#define EXCHANGE_BINARIES_FOLDER_NAME_FORMAT  L"VStore\\S%010d\\ExchBin"
#define SESSION_CATALOG_FOLDER_NAME_FORMAT    L"Catalog\\S%010d"
#define SESSION_DATA_FOLDER_NAME_FORMAT       L"VStore\\S%010d"
#define SESSION_DATA_FIXED_FOLDER_NAME        L"\\VStore\\"
#define SESSION_CATALOG_FIXED_FOLDER_NAME     L"\\Catalog\\"
#define GRT_CATALOG_FOLDER_NAME               L"\\GRT"
#define SESSION_INDEX_FOLDER_NAME             L"Index"
#define BACKUP_INFO_XML_NAME                  L"BackupInfo.XML"
#define EXCHANGE_SUBSESSION_GUID_TAG          L"Microsoft Exchange Writer"       
#define SQLSERVER_SUBSESSION_GUID_TAG         L"SqlServerWriter"
#define AD_SUBSESSION_GUID_TAG                L"NTDS"

class CDBCatalogInfo
{
public:

    CDBCatalogInfo() : m_bIsGRTCatalogCreated(false) {}
    CDBCatalogInfo(const CDBCatalogInfo& obj)
    {
        m_wsLogicalPath = obj.m_wsLogicalPath;
        m_wsDBName = obj.m_wsDBName;
        m_wsDisplayName = obj.m_wsDisplayName;
        m_wsEdbPath = obj.m_wsEdbPath;
        m_wsStmPath = obj.m_wsStmPath;
        m_wsSysPath = obj.m_wsSysPath;
        m_wsLogPath = obj.m_wsLogPath;
        m_wsCatalogPath = obj.m_wsCatalogPath;
        m_vecDBFileList = obj.m_vecDBFileList;
		m_bIsGRTCatalogCreated =obj.m_bIsGRTCatalogCreated;
    }

    wstring m_wsLogicalPath; // Database identity, format: logical path\database GUID
    wstring m_wsDBName;      // GUID for exchange and DB name for SQL.
    wstring m_wsDisplayName; // Display name for exchange database.  
    wstring m_wsEdbPath;     // Full path of exchange database files(edb files)
    wstring m_wsStmPath;     // Full path of exchange 2k3 stream file, empty when 2k7 and e14.
    wstring m_wsSysPath;     // Full path of exchange system files, including .chk file.
    wstring m_wsLogPath;     // Full path of exchange logs.
    wstring m_wsCatalogPath; // Full path of GRT catalog file.
    bool    m_bIsGRTCatalogCreated;  // If GRT catalog has been created.  
    vector<wstring> m_vecDBFileList;  // Used for SharePoint. A list for all database files, including mdf, ldf and ndf.
};

typedef vector<CDBCatalogInfo> DBCatalogInfoVector;
typedef vector<CDBCatalogInfo*> DBCatalogInfoPtrVector;

typedef enum _eDBCatalogType
{
    EDBCT_UNKNOWN = 0,
    EDBCT_EXCH,
    EDBCT_SP,
	EDBCT_AD,
}E_DBCATALOG_TYPE;

class CSubSessInfo
{
public:
    wstring m_wsMnt4Path;     // Longest mount point for specified path.
    wstring m_wsType;
    wstring m_wsDisplayName;
    wstring m_wsMnts;         // Mount point for the volume. use ';' to delimit.
    wstring m_wsGUID;
    wstring m_wsVolDataSizeB; // Volume data size in bytes.
    wstring m_wsSubSessNo;    // Sub session No.
    wstring m_wsCatalogFile;
    bool    m_bIsBootVol;     // Boot volume. only valid when type is volume.
    bool    m_bIsSysVol;      // System volume. only valid when type is volume.
};

// Get current backup destination according to session number and any backup destination path in backup destination link.
long WINAPI AFGetCurrentBackupDestination
    (
    wstring& wsCurBKDest,       // [out] Current backup destination path.
    const WCHAR* pwzBackupDest, // [in] Any backup destination in backup destination link.
    DWORD dwSessNum             // [in] A specified session number.
    );

// Get location of exchange binaries of GRT.
long WINAPI AFGetExchBinDirectory
    (
     wstring& wsExchBinPath,     // [out] Full path for exchange binaries.
     const WCHAR* pwzBackupDest, // [in] Current backup destination. 
     DWORD dwSessNum             // [in] Current session number. 
    );

// Get location of session data, including VHD(D2D) files, block2 ctf and backupindo.xml.
long WINAPI AFGetSessionDataFullPath
    (
     wstring& wsSessDataFullPath, // [out] Full path for session data.
     const WCHAR* pwzBackupDest,  // [in] Current backup destination. 
     DWORD dwSessNum              // [in] Current session number. 
    );

// Get location of session data, including volume catalog, application catalog and GRT catalog.
long WINAPI AFGetSessionCatalogFullPath
    (
     wstring& wsSessCatFullPath, // [out] Full path for session catalog.
     const WCHAR* pwzBackupDest, // [in] Current backup destination.
     DWORD dwSessNum             // [in] Current session number.
    );

// Get location of session index.
long WINAPI AFGetSessionIndexFullPath
    (
     wstring& wsSessIdxFullPath, // [out] Full path for session index.
     const WCHAR* pwzBackupDest, // [in] Current backup destination. 
     DWORD dwSessNum             // [in] Current session number. 
    );

// Get location of session data, including volume catalog, application catalog and GRT catalog.
long WINAPI AFGetSessionGRTCatalogFullPath
    (
     wstring& wsSessGRTCatFullPath, // [out] Full path for session catalog.
     const WCHAR* pwzBackupDest,    // [in] Current backup destination.
     DWORD dwSessNum,               // [in] Current session number. 
     DWORD dwSubSessNum,            // [in] Sub session number which contain the databases. 
     bool  bCreateNew = true        // [in] If create the full path when the GRT catalog path doesn't exist.
    );

// Save catalog mapping information to file.
long WINAPI AFSaveDatabaseCatalogInformation
    (
     DBCatalogInfoVector& vecDBCatalogInfo,        // [in] A vector contains all database catalog information to be saved.
     const WCHAR* pwzBackupDest,                   // [in] Current backup destination.
     DWORD dwSessNum,                              // [in] Current session number. 
     DWORD dwSubSessNum,                           // [in] Sub session number which contain the databases.
     bool  bCreateNew = true,                      // [in] If create the full path when the GRT catalog path doesn't exist.
     E_DBCATALOG_TYPE eDBCataLogType = EDBCT_EXCH  // [in] The GRT catalog file type.
    );

// Read catalog mapping information to file.
long WINAPI AFReadDatabaseCatalogInformation
    (
     DBCatalogInfoVector& vecDBCatalogInfo,        // [out] A vector contains all database catalog information has been read.
     const WCHAR* pwzBackupDest,                   // [in] Current backup destination. 
     DWORD dwSessNum,                              // [in] Current session number. 
     DWORD dwSubSessNum,                           // [in] Sub session number which contain the databases. 
     E_DBCATALOG_TYPE eDBCataLogType = EDBCT_EXCH  // [in] The GRT catalog file type.
    );

// Get sub session information for a volume mount point, if it is a full path, its mount point will be found automatically,
long WINAPI AFGetSubSessionInformation
    (
     CSubSessInfo& SubSessInfo,  // [out] Sub session information for the specified file full path.
     const WCHAR* pwzVolMnt,     // [in] A mount point for which find owner sub session information. If this is a full path, its mount point will be used.
     const WCHAR* pwzBackupDest, // [in] Current backup destination.
     DWORD dwSessNum             // [in] Current session number.
    );

// Get volume GUID for a volume mount point, if it is a full path, its mount point will be found automatically,
long WINAPI AFGetVolumeGUIDForPath
    (
     wstring& wsVolGUID,         // [out] Volume GUID for the specified file full path.
     const WCHAR* pwzVolMnt,     // [in] A mount point for which find owner sub session information. If this is a full path, its mount point will be used.
     const WCHAR* pwzBackupDest, // [in] Current backup destination. 
     DWORD dwSessNum             // [in] Current session number. 
    );

// Get database information according to sub session number and database logical path.
long WINAPI AFGetDatabaseCatalogInformation
    (
     CDBCatalogInfo& DBCatalogInfo, // [out] Catalog information for database, see definition of class CDBCatalogInfo. 
     const WCHAR* pwzDBIdentity,    // [in] Logical path\component name for specified database in writer metadata.
     const WCHAR* pwzBackupDest,    // [in] Current backup destination. 
     DWORD dwSessNum,               // [in] Current session number. 
     DWORD dwSubSessNum             // [in] Sub session number which contain the databases. 
    );

// Get database information according to sub session number and database edb path..
long WINAPI AFGetDatabaseCatalogInformationByEDBPath
    (
     CDBCatalogInfo& DBCatalogInfo, // [out] Catalog information for database, see definition of class CDBCatalogInfo. 
     const WCHAR* pwzEDBPath,       // [in] EDB full path for specified database.
     const WCHAR* pwzBackupDest,    // [in] Current backup destination.
     DWORD dwSessNum,               // [in] Current session number.
     DWORD dwSubSessNum             // [in] Sub session number which contain the databases. 
    );

// Get database information according to sub session number and database edb path..
long WINAPI AFGetExchDBGRTCatalogInformationByEDBPath
    (
     CDBCatalogInfo& DBCatalogInfo, // [out] Catalog information for database, see definition of class CDBCatalogInfo. 
     const WCHAR* pwzEDBPath,       // [in] EDB full path for specified database.
     const WCHAR* pwzSessPath       // [in] Current session path.
    );

// Get backup destination path, session number from session path, including catalog path.
long WINAPI AFParseSessionPath
    (
     const WCHAR* pwzSessPath,      // [in] Session path like D:\BK\W2K3-SQL08-01\VStore\S0000000001 or D:\BK\W2K3-SQL08-01\Catalog\S0000000001
     wstring* pwsBKDest,            // [out] Backup destination in session path. If equal to NULL, ignore this parameter
     DWORD* pdwSessNum              // [out] Session number in current session path, If equal to NULL, ignore this parameter
    );

// Get location of exchange binaries of GRT by session path.
long WINAPI AFGetExchBinDirectoryBySessPath
    (
     wstring& wsExchBinPath,     // [out] Full path for exchange binaries.
     const WCHAR* pwzSessPath    // [in] Current session path like D:\BK\W2K3-SQL08-01\VStore\S0000000001. 
    );

long WINAPI AFGetCatalogPathFromSessPath
    (
    wstring& wstrCatalogPath,    // [out] Catalog path for input session path.
    const WCHAR* pwzSessPath     // [in] Session path like D:\BK\W2K3-SQL08-01\VStore\S0000000001.
    );

long WINAPI AFGetSessPathFromCatalogPath
    (
    wstring& wsSessPath,            // [out] Session path for input session path.
    const WCHAR* pwzCatalogPath     // [in] Catalog path like D:\BK\W2K3-SQL08-01\VStore\S0000000001.
    );

// Get backup destination path, session number from session path, including catalog path.
long WINAPI AFParseGRTCatalogPath
    (
     const WCHAR* pwzGRTCatalogPath, // [in] Session path like D:\BK\W2K3-SQL08-01\VStore\S0000000001 or D:\BK\W2K3-SQL08-01\Catalog\S0000000001
     wstring* pwsBKDest,             // [out] Backup destination in session path. If equal to NULL, ignore this parameter
     DWORD* pdwSessNum,              // [out] Session number in current session path, If equal to NULL, ignore this parameter
     DWORD* pdwSubSessNum,           // [out] Sub session number for current Exhcange or SharePoint GRT catalog.
     CDBCatalogInfo* pDatabaseInfo   // [out] Database information for the GRT catalog.
     );

long WINAPI AFSetGlobalUserToken
	(
	HANDLE hUserToken
	);

long WINAPI AFClearGlobalUserToken
	(
	);

int WINAPI GenGRTCatalog(const wchar_t *pJobScriptPath, DWORD dwJobId);