#pragma once

#include <vector>
#include <string>

typedef struct _ARCFLASH_LIC_INFO ARCFLASH_LIC_INFO, *PARCFLASH_LIC_INFO;
namespace vsswrapper
{
    typedef enum eRestoreMethod
    {
        ERM_REPLACE = 0,                        //ZZ: Replace file to destination folder.
        ERM_REPLACE_AFTER_REBOOT = 0x00000001,  //ZZ: Places the file in a temporary directory and uses MoveFileEx to move the file into the correct position after reboot.
		ERM_SKIPPED = 0x00000002                //ZZ: Some files should not be restored .
    }ERESTOREMETHOD;

    typedef enum
    {
        ERS_UNKNOWN = 0,
        ERS_FAILED,
        ERS_SUCCEED,
        ERS_SKIPPED,
        ERS_CANCELED
    }E_RESTORE_STATUS;

    typedef struct _stRestoreStatusStat
    {
    public:
        DWORD dwUnknownCount;
        DWORD dwFailedCount;
        DWORD dwSucceedCount;
        DWORD dwSkippedCount;
        DWORD dwCanceledCount;
    }ST_RESTORE_STATUS_STAT, *PST_RESTORE_STATUS_STAT;

    class CComponentInfo
    {
    public:
        CComponentInfo() : pwzName(NULL), pwzLogicalPath(NULL), pwzCompRestName(NULL), pwzCompRestPath(NULL) {}
        PWCHAR pwzName;
        PWCHAR pwzLogicalPath;
		PWCHAR pwzCompRestName;			// new component name if any
		PWCHAR pwzCompRestPath;			// new component path if any
    };

    class CRestoreFileElement
    {
    public:
        CRestoreFileElement() : pwzSrcFilePath(NULL), pwzDstFilePath(NULL), ulRestoreMethod(ERM_REPLACE), ulRestoreStatus(ERS_UNKNOWN){}
        PWCHAR pwzSrcFilePath;
        PWCHAR pwzDstFilePath;
        unsigned long ulRestoreMethod;
        unsigned long ulRestoreStatus;   //ZZ: [2014/03/30 14:11] Indicate if file is restored successfully. Refer to E_RESTORE_STATUS
    };

    class IVSSRestoreAppOptions
    {
    public:
        // Author			: zhoyu03
        // Function name	: SetError
        /// Description	    : This operation allows BSVSS to set an error value in the backup process.
        // Return type		: virtual void 
        // Argument         : HRESULT VssErrorNumber
        //                    WCHAR* errorMessage
        virtual void SetError(HRESULT VssErrorNumber, WCHAR* errorMessage) = 0;

        // Author			: zhoyu03
        // Function name	: StopService
        /// Description	    : Stops the service on the machine. Returns true if the 
        ///					: service stopped.
        // Return type		: virtual bool 
        // Argument         : WCHAR* serviceName
        virtual bool StopService(WCHAR* serviceName) = 0;

        // Author			: zhoyu03
        // Function name	: StartService
        /// Description	    : Starts the named service
        // Return type		: virtual bool 
        // Argument         : TCHAR* serviceName
        virtual bool StartService(WCHAR* serviceName) = 0;

        // Author			: zhoyu03
        // Function name	: SetNeedsReboot
        /// Description	    : Informs the application that a restore needs the 
        ///					: machine to be rebooted.
        // Return type		: virtual void 
        // Argument         : bool reboot
        virtual void SetNeedsReboot(bool reboot) = 0;
 
        // Author			: zhoyu03
        // Function name	: GetBCD
        /// Description	    : Return BCD content string.
        // Return type		: virtual PWCHAR. NULL means error.
        // Argument         : void
        virtual PWCHAR GetBCD() = 0;

        // Author			: zhoyu03
        // Function name	: GetWriterMetadata
        /// Description	    : Return writer metadata content string.
        // Return type		: virtual PWCHAR. NULL means error.
        // Argument         : void
        virtual PWCHAR GetWriterMetadata() = 0;

        // Author			: zhoyu03
        // Function name	: GetComponentsForRestore
        /// Description	    : Return components information which will be restored, If this list is empty
        ///                   all component in this writer will be restored.
        // Return type		: virtual bool.
        // Argument         : std::vector<CComponentInfo>& vecComponents
        virtual bool GetComponentsForRestore(std::vector<CComponentInfo> **ppvecComponents) = 0;

        // Author			: zhoyu03
        // Function name	: GetNewTargetForRestore
        /// Description	    : Get new target for restore, returning NULL means user chooses to restore to
        //                    original location.
        // Return type		: virtual PWCHAR.
        // Argument         : void
        virtual PWCHAR GetNewTargetForRestore() = 0;

        // Author			: zhoyu03
        // Function name	: RestoreAppFiles
        /// Description	    : Restore files in BLI session.
        // Return type		: virtual bool.
        // Argument         : std::vector<CRestoreFileElement> vecFiles
        virtual bool RestoreAppFiles(std::vector<CRestoreFileElement> *pvecFiles) = 0;

        // Author			: zhoyu03
        // Function name	: DismountDatabaseAutomatically
        /// Description	    : Some writers(e.g Echange) can't dismount database when restore, therefore we 
        //                    should do it before restore. If this function return true, our program will dismount
        //                    and mount database automatically, otherwise, user should do do this manually.
        // Return type		: virtual bool.
        // Argument         : void
        virtual bool DismountDatabaseAutomatically() = 0;

        // Author			: zhoyu03
        // Function name	: GetRestoreOption
        /// Description	    : Get restore option which indicaing restore to original or alternate location.
        // Return type		: virtual ULONG.
        // Argument         : void
        virtual ULONG GetRestoreOption() = 0;

        // Author			: zhoyu03
        // Function name	: GetRestoreJobID
        /// Description	    : Get restore job ID for activity log.
        // Return type		: virtual ULONG.
        // Argument         : void
        virtual ULONG GetRestoreJobID() = 0;

        // Author			: zhoyu03
        // Function name	: GetLicenseInfo
        /// Description	    : Get license information.
        // Return type		: virtual PARCFLASH_LIC_INFO.
        // Argument         : void
        virtual PARCFLASH_LIC_INFO GetLicenseInfo() = 0;

        // Author			: zhoyu03
        // Function name	: GetAdminAccount
        /// Description	    : Get current user account information.
        // Return type		: virtual ULONG.
        // Argument         : void
        virtual ULONG GetAdminAccount(std::wstring& wsUser, std::wstring& wsPwd) = 0;

        // Author			: zhoyu03
        // Function name	: UpdateJobMonitor
        /// Description	    : Update current phase in VSSWrapperDll.
        // Return type		: virtual ULONG.
        // Argument         : ULONG
        virtual ULONG UpdateJobMonitor(ULONG ulPhaseFlag) = 0;

		// For calling VMJobLogActivity
		virtual ULONG GetLogUniqueID(std::wstring& sLogUniqueID) = 0;
    };
}
