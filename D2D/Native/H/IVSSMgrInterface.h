#pragma once

#ifdef VSSWRAPPERDLL_EXPORTS
#define VSSWRAPPERDLL_API __declspec(dllexport)
#else
#define VSSWRAPPERDLL_API __declspec(dllimport)
#endif

#include "VSS Include\ws03\vss.h"
#include <vector>
#include <string>
using namespace std;

//ZZ: Specify calculating writer size in VSSWrapperDll instead of backup module.
#define __CALCULATE_WRITER_COMP_SIZE_IN_VSSWRAP__

//mutga01: HW Snapshot: Moved the definition to IVSSMgrInterface.h
struct BLIVolume {
	LPWSTR lpwVolume;
	BOOL bIncremental;
	//BOOL bVolumeGUIDName; //<sonmi01>2009-7-16 ###???
    ULONGLONG ullClusterCnt;  //ZZ: Record number of cluster on volume
	//mutga01: HW snapshot changes
	BOOL bSnapshotable; //mark if a volume is snapshotable or not
};

struct BLIBackupVolumes {
	DWORD dwVolumeCount;
	BLIVolume backupVols[1];
};
    
class CDataTransmitter;
namespace vsswrapper
{
    typedef long VSSRET;
    typedef unsigned long ulong;

	class CRestoreFileElement;
	class CComponentInfo;
	class CRestoreFileElement;
    class CSnapShotProp;
    typedef CSnapShotProp* CSnapShotPropPtr;
    typedef std::vector<CSnapShotProp> SnapShotPropVector;
    typedef std::vector<CSnapShotPropPtr> SnapShotPropPtrVector;

    typedef std::vector<wstring> WStrVector;

    class CFileElement;
    typedef CFileElement* CFileElementPtr;
    typedef std::vector<CFileElement> FileElementVector;
    typedef std::vector<CFileElementPtr> FileElementPtrVector;

	//mutga01: HW snapshot changes
	class CVSSProviderProp;
	typedef std::vector<CVSSProviderProp*>	vectorVSSProviderData;

	//ZZ: When add or modify application version, it MUST change eApplicationVersion in \Native\FlashCore\MonadMgrDll\MWInvokeImpl.cs
	typedef enum eApplicationVersion
	{
		EAV_UNKNOWN = 0,
		EAV_SQL_UNKNOWN,
		EAV_SQL_2K5,
		EAV_SQL_2K8,
		EAV_EXCHANGE_UNKNOWN,
		EAV_EXCHANGE_2K3,
		EAV_EXCHANGE_2K7,
		EAV_EXCHANGE_E14,  //ZZ: Exchange 2010
		EAV_EXCHANGE_E15,  //ZZ: Exchange 2013
        EAV_EXCHANGE_2016
	}E_APP_VER;

    typedef enum eDeleteSnapshotMethod
    {
        DSM_DEL_SNAPSHOT = 0,
        DSM_DEL_SNAPSHOT_SET,
        DSM_DEL_ALL_SNAPSHOT,
        DSM_DEL_ALL_SNAPSHOT_VOL
    }E_DELSNAP_METHOD;

	struct ExchangeDBDesciptor
	{
	public:
		BOOL	IsPublicDatabase;
		wstring strDBName;
		wstring strDBGUID;
		BOOL	IsMountedStatus;
	public:
		ExchangeDBDesciptor()
		{
			IsPublicDatabase = FALSE;
			strDBGUID = L"";
			strDBName = L"";
		}
	};

    class IBrowseMgrInterface
    {
    public:
        virtual VSSRET BrowseAppInformation(
            wstring& wsAppInfo, 
            bool bSaveAsXML) = 0;

        virtual VSSRET BrowseVolumeInformation(            
            wstring& wsVolumeInfo, 
            bool bSaveAsXML) = 0;

        virtual VSSRET GetExchangeVersion(E_APP_VER& eAppVer) = 0;

        virtual VSSRET IsExchPublicFolder(const WCHAR* pwzDBNameGuid, bool& bIsPublicFolder) = 0;
		
		virtual VSSRET GetExchangeDatabaseOnLocalHost(const wstring& strserverName, vector<ExchangeDBDesciptor>& v_strDBName) = 0;
    };

    class IVSSBackupMgrInterface
    {
    public:
        virtual VSSRET Initialize(
            CDataTransmitter* pDataTransmitter = NULL,
            DWORD dwContext = VSS_CTX_BACKUP, 
            VSS_BACKUP_TYPE eBackupType = VSS_BT_COPY,
            bool bPersistent = false,
			const WCHAR* bcdXmlFilePath = NULL) = 0;

        virtual VSSRET BrowseAppInformation(
            wstring& wsAppInfo, 
            bool bSaveAsXML) = 0;

        virtual VSSRET CreateSnapshotSet(
            WStrVector vecVolumeLIst, 
            wstring& wsSnapshotSetID) = 0;
		
		//mutga01: HW snapshot changes
		virtual vector<wstring> FilterOutSnapshotsFromVolumeList(
			WStrVector& vecVolumes,
			BLIBackupVolumes *pVolumes) = 0;

        //pSourceObjectId
        virtual VSSRET DeleteSnapshots(
            VSS_ID* pSourceObjectId, 
            ulong ulObjIDCnt, 
            E_DELSNAP_METHOD eDelSnapMethod, 
            bool bForceDelete,
            ulong* pulDelSnapCnt,
            ulong* pulNonDelSnapCnt,
            WStrVector* pvecSnapshotsDeleted,
            WStrVector* pvecSnapshotsNonDeleted) = 0;

        virtual VSSRET GetBackupFilesForWriter(
            const WCHAR* pWriterIDorName,
            FileElementVector& vecFileList,
            const WCHAR* pwzInstanceNameOrID = NULL) = 0;

        virtual VSSRET GetBackupFilesForWriter(
            const WCHAR* pWriterIDorName,
            WStrVector& vecFileList,
            const WCHAR* pwzInstanceNameOrID = NULL) = 0;

        virtual SnapShotPropPtrVector& GetSnapshotPropVector() = 0;

        virtual bool IsComponentInSnapshot(
            const WCHAR* pwzWriterNameOrID,
            const WCHAR* pwzUniquePath,  //Unique Path = Logical path\Component name.
			wstring& sUniquePathForDisplay,
            wstring* pwsVolNotBackup = NULL,
            const WCHAR* pwzInstanceNameOrID = NULL) = 0; 

        virtual VSSRET SetBackupCompleteStatus(
            bool bBKSuccess) = 0;

        virtual VSSRET GetWriterCompSize(
            ULONGLONG* pullTotalSize,
            const WCHAR* pwzWriterIDorName,               
            const WCHAR* pwzCompIndentity = NULL,           //ZZ: Unique Path = Logical path\Component name.
            const WCHAR* pwzInstanceNameOrID = NULL) = 0;   //ZZ: Instance name

        virtual vector<CSnapShotProp> QuerySnapshotSet(VSS_ID snapshotSetID) = 0;

		//virtual ~IVSSBackupMgrInterface() {};
		virtual VSSRET Release() = 0; //<sonmi01>2012-11-27 #vss mem and handle leak

		//mutga01: HW snapshot changes
		virtual VSSRET BrowseAvailableProviders() = 0;
		virtual vectorVSSProviderData& GetProviderInfo() = 0;
		virtual VSSRET ReleaseProviderInfo() = 0;
		virtual void SetProviderInfo(vectorVSSProviderData& vecProviderInfo) = 0;
		virtual bool  IsSnapshotableVolume(WCHAR* pszVolumeID) = 0;
		virtual bool  IsHWSnapshotUsed() = 0;
		virtual void  DisableHWSnapshot() = 0;
		virtual void  EnableHWSnapshot() = 0;
		virtual bool IsTrnansportableSnapshotUsed() = 0;
		virtual void DisableTransportableSnapshot() = 0;
		virtual void EnableTransportableSnapshot() = 0;
		virtual VSSRET 	ImportSnapShot() = 0;
		virtual BOOL IsVolumeSupportedByProviders(const WCHAR* pszVolumes) = 0;
    };

	class IVSSBackupMgrInterface2
	{
	public:
		virtual VSSRET SetIncludedComponents(const WStrVector& vecIncludedComponents) = 0;
		virtual VSSRET SetSupportWriters(const WStrVector& vecWriters) = 0;
		virtual VSSRET ExposeSnapshot(VSS_ID snapshotId, WCHAR* pszRootPath, LONG lAttribute, WCHAR* szExpose, WCHAR** pszExposed) = 0;
		virtual VSSRET SetComponentBackupStatus(const WCHAR* pszComponent, bool bSucceed) = 0;
		virtual VSSRET SetBackupComplete() = 0;
		virtual VSSRET GetBCD(wstring& strBCD) = 0;
		virtual VSSRET GetBackupFilesForComponent(const wstring& strWriterId, const WCHAR* pszComponent, WStrVector& vecBackupFiles) = 0;
		virtual VSSRET AddVolumeProviderMap(const wstring& strVolume, const VSS_ID& idProvider) = 0;
	};

    class IVSSRestoreAppOptions;
    class IVSSRestoreMgrInterface
    {
    public:                       
        virtual VSSRET Initialize(IVSSRestoreAppOptions* pRestoreAppOption = NULL) = 0;
        virtual VSSRET DoRestore() = 0;
		virtual long   GetSkippedVssComponentCount() = 0;
    };

    class IVSSWriterInfoInterface
    {
    public:
        virtual const WCHAR* GetWriterName() = 0;
        virtual const WCHAR* GetWriterID() = 0;
        virtual const WCHAR* GetInstanceName() = 0;
        virtual const WCHAR* GetInstanceID() = 0;
        virtual const WCHAR* GetWriterMetadata() = 0;

        virtual VSSRET GetBackupFiles(FileElementVector& vecBackupFiles) = 0;
        virtual VSSRET GetBackupFiles(WStrVector& vecBackupFiles) = 0;
		virtual VSSRET GetBackupFilesForComponent(const WCHAR* pszComponent, WStrVector& vecBackupFiles) = 0;

		virtual VSSRET GetVSSComponent(WStrVector& vecComponents) = 0;

        virtual void Release() = 0;
    };

    typedef IVSSWriterInfoInterface* IWriterInfoPtr;
    typedef std::vector<IWriterInfoPtr> IWriterInfoPtrVector;

	
    class IVSSWriterInfoMgrInterface
    {
    public:
        virtual VSSRET QueryWriters(IWriterInfoPtrVector& vecWriterIter) = 0;

        virtual VSSRET QueryWriters(
            IWriterInfoPtrVector& vecWriterIter,
            const WCHAR* pwzWriterNameORID, 
            const WCHAR* pwzInstanceName = NULL) = 0;

        //virtual VSSRET ReleaseWriters(IWriterInfoPtrVector& vecWriterIter) = 0;
    };

    class CSnapShotProp
    {
    public:
        CSnapShotProp() {}
        CSnapShotProp(VSS_ID guidSnapshotID, VSS_ID guidSnapshotSetID, VSS_SNAPSHOT_PROP* prop)
            : m_SnapshotId(guidSnapshotID),
            m_SnapshotSetId(guidSnapshotSetID)
        {
            if(prop)
                *this = *prop;
        }

        CSnapShotProp& operator = (const VSS_SNAPSHOT_PROP& prop)
        {
            m_SnapshotId = prop.m_SnapshotId;
            m_SnapshotSetId = prop.m_SnapshotSetId;
            m_lSnapshotsCount = prop.m_lSnapshotsCount;
            if(prop.m_pwszSnapshotDeviceObject) m_pwszSnapshotDeviceObject = prop.m_pwszSnapshotDeviceObject;
            if(prop.m_pwszOriginalVolumeName) m_pwszOriginalVolumeName = prop.m_pwszOriginalVolumeName;
            if(prop.m_pwszOriginatingMachine) m_pwszOriginatingMachine = prop.m_pwszOriginatingMachine;
            if(prop.m_pwszServiceMachine) m_pwszServiceMachine = prop.m_pwszServiceMachine;
            if(prop.m_pwszExposedName) m_pwszExposedName = prop.m_pwszExposedName;
            if(prop.m_pwszExposedPath) m_pwszExposedPath = prop.m_pwszExposedPath;
            m_ProviderId = prop.m_ProviderId;
            m_lSnapshotAttributes = prop.m_lSnapshotAttributes;
            m_tsCreationTimestamp = prop.m_tsCreationTimestamp;
            m_eStatus = prop.m_eStatus;		
            return *this;
        }

        VSS_ID m_SnapshotId;
        VSS_ID m_SnapshotSetId;
        LONG m_lSnapshotsCount;
        wstring m_pwszSnapshotDeviceObject;
        wstring m_pwszOriginalVolumeName;
        wstring m_pwszOriginatingMachine;
        wstring m_pwszServiceMachine;
        wstring m_pwszExposedName;
        wstring m_pwszExposedPath;
        VSS_ID m_ProviderId;
        LONG m_lSnapshotAttributes;
        VSS_TIMESTAMP m_tsCreationTimestamp;
        VSS_SNAPSHOT_STATE m_eStatus;
    };
	//mutga01: HW snapshot changes
	class CVSSProviderProp
	{
	public:
		CVSSProviderProp() {}
		CVSSProviderProp( VSS_PROVIDER_PROP nv  ) 
		{
			memcpy( &m_ProviderProperties, &nv, sizeof( m_ProviderProperties ) );
		}

		const VSS_PROVIDER_PROP		GetProperties()						{ return m_ProviderProperties;}
		const VSS_ID				GetProviderID()						{ return m_ProviderProperties.m_ProviderId; }
		const VSS_PWSZ				GetProviderName()					{ return m_ProviderProperties.m_pwszProviderName; }
		const VSS_PROVIDER_TYPE		GetProviderType()					{ return m_ProviderProperties.m_eProviderType; }
		const VSS_PWSZ				GetProviderVersion()				{ return m_ProviderProperties.m_pwszProviderVersion; }
		const VSS_ID				GetProviderVersionID()				{ return m_ProviderProperties.m_ProviderVersionId; }
		const CLSID					GetClassID()						{ return m_ProviderProperties.m_ClassId; }
	private:
		VSS_PROVIDER_PROP			m_ProviderProperties;
	};

    class CFileElement
    {
    public:
        CFileElement() : m_bIsRecursive(false) {}
        wstring m_wsFilePath;
        wstring m_wsExpandedPath;
        wstring m_wsFileDesc;
        bool    m_bIsRecursive;
    };

	class IRedirectEventUpdate
	{
	public:
		virtual void OnStartSnapshotSetDelay(ULONG ulInterval) = 0;
		virtual ~IRedirectEventUpdate(){}
	};

#ifdef __cplusplus
    extern "C"
    {
#endif
        // Browse all writers' structure.
        typedef long (*BROWSEINFORMATIONFUNC)(wchar_t**, unsigned long*, bool);
        typedef void (*RELEASEBROWSEINFOFUNC)(wchar_t**);

        VSSWRAPPERDLL_API long BrowseAppInfo(wchar_t** ppBrowseInfo, unsigned long* pdwBrowseInfoSize, bool bSaveAsFile);
        VSSWRAPPERDLL_API long BrowseVolumeInfo(wchar_t** ppBrowseInfo, unsigned long* pdwBrowseInfoSize, bool bSaveAsFile);
        VSSWRAPPERDLL_API void ReleaseBrowseInfo(wchar_t** ppPonterToBeReleased);

        VSSWRAPPERDLL_API IVSSBackupMgrInterface* GetVSSBackupMgr();
        typedef IVSSBackupMgrInterface* (*GETVSSBACKUPMGRFUNC)(void);

        VSSWRAPPERDLL_API void ReleaseBackupMgr(IVSSBackupMgrInterface** ppVssBackupMgr);
        typedef void (*RELEASEBACKUPMGRFUNC)(IVSSBackupMgrInterface**);

        VSSWRAPPERDLL_API IVSSRestoreMgrInterface* GetVSSRestoreMgr();
        typedef IVSSRestoreMgrInterface* (*GETVSSRESTOREMGRFUNC)(void);

        VSSWRAPPERDLL_API void ReleaseRestoreMgr(IVSSRestoreMgrInterface** ppVssRestoreMgr);
        typedef void (*RELEASERESTOREMGRFUNC)(IVSSRestoreMgrInterface**);

        VSSWRAPPERDLL_API IBrowseMgrInterface* GetBrowseMgr();
        typedef IBrowseMgrInterface* (*GETBROWSEMGRFUNC)(void);

        VSSWRAPPERDLL_API void ReleaseBrowseMgr(IBrowseMgrInterface** ppVssBrowseMgr);
        typedef void (*RELEASEBROWSEMGRFUNC)(IBrowseMgrInterface**);

        VSSWRAPPERDLL_API IVSSWriterInfoMgrInterface* GetWriterInfoMgr();
        typedef IVSSWriterInfoMgrInterface* (*GETWRITERINFOMGRFUNC)(void);

        VSSWRAPPERDLL_API void ReleaseWriterInfoMgr(IVSSWriterInfoMgrInterface** ppWriterInfoMgr);
        typedef void (*RELEASEWRITERINFOMGRFUNC)(IVSSWriterInfoMgrInterface**);

        // this API not use the singleton, that is to say,
        // each call will return a different object
        VSSWRAPPERDLL_API IVSSBackupMgrInterface* GetVSSBackupMgr2();
        typedef IVSSBackupMgrInterface* (*GETVSSBACKUPMGR2FUNC)(void);

        VSSWRAPPERDLL_API void ReleaseBackupMgr2(IVSSBackupMgrInterface** ppVssBackupMgr);
        typedef void (*RELEASEBACKUPMGR2FUNC)(IVSSBackupMgrInterface**);

        // this API not use the singleton, that is to say,
        // each call will return a different object
        VSSWRAPPERDLL_API IVSSBackupMgrInterface2* GetVSSBackupMgrForWriter(IRedirectEventUpdate* pHandler);
        typedef IVSSBackupMgrInterface* (*GETVSSBACKUPMGRFORWRITERFUNC)(void);

        VSSWRAPPERDLL_API void ReleaseBackupMgrForWriter(IVSSBackupMgrInterface2** ppVssBackupMgr);
        typedef void (*RELEASEBACKUPMGRFORWRITERFUNC)(IVSSBackupMgrInterface**);

		VSSWRAPPERDLL_API std::wstring* GetString(WCHAR *);
		typedef std::wstring* (*GETSTRING)(WCHAR *);

		VSSWRAPPERDLL_API std::vector<CComponentInfo>* GetComponentVector();
		typedef std::vector<CComponentInfo>* (*GETCOMPONENTVECTOR)();

		VSSWRAPPERDLL_API void PushIntoComponentVector(std::vector<CComponentInfo>* pVecComponentInfo, CComponentInfo &compInfo);
		typedef void (*PUSHINTOCOMPONENTVECTOR)(std::vector<CComponentInfo>* pVecComponentInfo, CComponentInfo &compInfo);

		VSSWRAPPERDLL_API void ReleaseComponentInfo(std::vector<CComponentInfo>* pVecComponentInfo);
		typedef void (*RELEASECOMPONENTINFO)(std::vector<CComponentInfo>* pVecComponentInfo);

		VSSWRAPPERDLL_API DWORD GetComponentVectorSize(std::vector<CComponentInfo>* pVecComponentInfo);
		typedef DWORD (*GETCOMPONENTVECTORSIZE)(std::vector<CComponentInfo>* pVecComponentInfo);

		VSSWRAPPERDLL_API CComponentInfo& GetComponentFromVector(std::vector<CComponentInfo>* pVecComponentInfo, UINT iLocation);
		typedef CComponentInfo& (*GETCOMPONENTFROMVECTOR)(std::vector<CComponentInfo>* pVecComponentInfo, UINT iLocation);

		VSSWRAPPERDLL_API DWORD GetRestoreElementsSize(std::vector<CRestoreFileElement> *pVecFiles);
		typedef DWORD (*GETRESTOREELEMENTSSIZE)(std::vector<CRestoreFileElement> *pVecFiles);

		VSSWRAPPERDLL_API DWORD GetRestoreElementList(std::vector<CRestoreFileElement> *pVecFiles, CRestoreFileElement **pRestoreFilesList);
		typedef DWORD (*GETRESTOREELEMENTLIST)(std::vector<CRestoreFileElement> *pVecFiles, CRestoreFileElement **pRestoreFilesList);
	
#ifdef __cplusplus
    };
#endif
}


