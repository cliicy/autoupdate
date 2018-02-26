#pragma once
#include "jni.h"
#include "drcommonlib.h"
#include "dbglog.h"

namespace V2PWEBSRV
{
    typedef struct _SNAPSHOT
    {
        wstring strName;
        wstring strGuid;
        wstring strTime; //string time formart for this snapshot.
        FILETIME fTime;//digital time format for this snapshot.
        vector<wstring> vDiskPath;
        wstring strParentName;
        wstring strParentGuid;
    }SNAPSHOT, *PSNAPSHOT;

    typedef vector<SNAPSHOT> SNAPSHOT_LIST;

    typedef struct _VM_INFO
    {
        DWORD dwState;
        wstring strVMName;
        wstring strVMUUID;
        vector<wstring> vDiskPath;
		bool bInstantVM;
        //vector<SNAPSHOT> vSnapshot;
    }VM_INFO, *PVM_INFO;

    typedef vector<VM_INFO> VM_INFO_LIST;

    typedef struct _SRV_INFO
    {
        wstring strSrv; //name or ip address.
        wstring strUser; //user name.
        wstring strPwd; //password.
        wstring strProtocol;
        int iPort; //port if needed.
        _SRV_INFO()
        {
            strSrv = L"";
            strUser = L"";
            strPwd = L"";
            strProtocol = L"";
            iPort = 0;
        }
        ~_SRV_INFO()
        {
        }
        _SRV_INFO(const _SRV_INFO &info)
        {
            strSrv = info.strSrv;
            strUser = info.strUser;
            strPwd = info.strPwd;
            strProtocol = info.strProtocol;
            iPort = info.iPort;
        }

        _SRV_INFO &operator=(const _SRV_INFO &info)
        {
            this->strSrv = info.strSrv;
            this->strUser = info.strUser;
            this->strPwd = info.strPwd;
            this->strProtocol = info.strProtocol;
            this->iPort = info.iPort;
            return *this;
        }
    }SRV_INFO, *PSRV_INFO;

    typedef enum VM_STATE
    {
        STATE_UNKNOWN = 0,
        STATE_RUNNING,
        STATE_SHUTDOWN
    };

    class IVM
    {
    public:

        virtual ~IVM(){}

        virtual void Release() = 0;

        virtual DWORD ConnectSrv(const SRV_INFO &info) = 0;

        virtual void DisconnectSrv() = 0;

        virtual DWORD GetVMList(VM_INFO_LIST &vmList) = 0;

        virtual DWORD GetSnapshots(const VM_INFO &vm, SNAPSHOT_LIST &list) = 0;

        virtual DWORD TakeSnapshot(const VM_INFO &vm, const wstring &snapshotName,
            const wstring &strNotes, wstring &strSnapGuid) = 0;

        virtual DWORD GetVmSate(const wstring &strVmGuid, VM_STATE &state) = 0;

        virtual DWORD ShutdownVm(const wstring &strVmGuid) = 0;

        virtual DWORD GetMDataPort(wstring &strDataPort) = 0; //retrieve data port(4090) on Monitor.

        virtual DWORD GetAdrCfgFile(const wstring &strVMGuid, const wstring &strVMName, const wstring &strFile) = 0;//retrieve adrconfigure.xml for v2p from now. strFile is full file path.

		virtual DWORD GetIVMAdrCfgFile(const wstring &strVMGuid, const wstring &strVMName, const wstring &strFile) = 0;//retrieve adrconfigure.xml for ivm from now. strFile is full file path.

        virtual DWORD GetAdrCfgFileMonitee(const wstring &strVMGuid, const wstring &strVMName, const wstring &strFile) = 0;

		virtual DWORD GetAdrInfoCMonitee(const wstring &strVMGuid, const wstring &strVMName, const wstring &strFile) = 0;

        virtual BOOL CheckHypervRoleExist() = 0;//check whether the current server has hyperv role installed.

        virtual int GetSnapshotCount(const wstring &strVMGuid) = 0;

		virtual DWORD GetAdrInfoCFile(const wstring &strVMGuid, const wstring &strVMName, const wstring &strFile) = 0;//retrieve adrinfoc.drz for v2p from now. strFile is full file path.

		virtual DWORD DeleteSnapshotAsync(const wstring &strVMGuid, const wstring &strSnapGuid) = 0;
    };

    class IJvm
    {
    public:
        virtual ~IJvm(){}

        virtual void Release() = 0;

        virtual DWORD CreateJvm(void *pJvm) = 0;

        virtual void *GetJvmHandler() = 0;

        virtual void DestroyJvm() = 0;

        virtual JNIEnv *GetJniEnv() = 0;

        virtual DWORD AttachCurThread(JNIEnv **ppEnv) = 0;

        virtual DWORD DetachCurThread() = 0;
    };

};

DWORD WINAPI CreateIVM(V2PWEBSRV::IVM **ppVm, CDbgLog *pLog, JNIEnv *pEnv, BOOL bHyperV = TRUE);

DWORD WINAPI CreateIJvm(V2PWEBSRV::IJvm **pJvm, CDbgLog *pLog);

typedef DWORD (WINAPI *_pfn_CreateIJvm)(V2PWEBSRV::IJvm **pJvm, CDbgLog *pLog);
typedef DWORD (WINAPI *_pfn_CreateIVM)(V2PWEBSRV::IVM **ppVm, CDbgLog *pLog, JNIEnv *pEnv, BOOL bHyperV);
