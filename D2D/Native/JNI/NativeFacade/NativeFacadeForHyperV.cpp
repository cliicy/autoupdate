#include "stdafx.h"
#include "com_ca_ha_webservice_jni_HyperVJNI.h"
#include "HypervInterface.h"
#include "utils.h"
#include "HaCommonFunc.h"
#include "JNIConv.h"
#include "HyperVRepCallBack.h"
#include "HAVhdUtility.h"
#include "..\..\H\afDiskImageInterface.h"
#include "..\..\Virtualization\VirtualStandby\HaUtility\ComEnvironment.h"
#include "vdserr.h"
#include "..\..\Virtualization\Common\HypervisorMgr\IHypervisor.h"

#include <atlstr.h>
#include <string>

using namespace std;
using namespace HyperVManipulation;
using namespace HaVhdUtility;

char _InvalidVMGuidMsg[] = "Invalid vm guid";

extern CDbgLog logObj;

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_OpenHypervHandle
(JNIEnv *env, jclass klass, jstring serverName, jstring user, jstring pwd)
{
	wstring wstrServer = Utility_JStringToWCHAR(env, serverName);
	wstring wstrUser = Utility_JStringToWCHAR( env, user );
	wstring wstrPwd = Utility_JStringToWCHAR(env, pwd);

	IHypervOperation *pHyperv = NULL;
	DWORD dwImp = AFImpersonate();

	do{
		pHyperv = OpenHyperVOperation( wstrServer, wstrUser, wstrPwd );
		if( NULL == pHyperv )
		{
			string msg = "fail to connect hyper-v server";
			ThrowHyperVException( env, klass, env->NewStringUTF(msg.c_str()), 0);		
			break;
		}
	}while (FALSE);
	
	if (ERROR_SUCCESS == dwImp)
	{
		AFRevertToSelf();
	}
	
	return (jlong)pHyperv;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_CloseHypervHandle
(JNIEnv *env, jclass klass, jlong handle)
{
	DelIHyperVOperation( (IHypervOperation*)handle );
}

JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_CreateVM
(JNIEnv *env, jclass klass, jlong handle, jstring friendName, jstring vmLocation, jint vmGeneration)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrFriendlyName = Utility_JStringToWCHAR(env, friendName);
	wstring wstrVMLocation = Utility_JStringToWCHAR(env, vmLocation);
	wstring wstrVmGuid;

	MSVM_VIRTUAL_SYSTEM_SUB_TYPE vmGenerationType;
	switch( vmGeneration)
	{
	case 1:
		vmGenerationType = MSVM_GENERATION_1;
		break;
	case 2:
		vmGenerationType = MSVM_GENERATION_2;
		break;
	default:
		vmGenerationType = MSVM_GENERATION_1;
		break;
	}
	int dwResult = pHyperv->_CreateVmEx(wstrFriendlyName, wstrVMLocation, vmGenerationType, wstrVmGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in CreateVm."), dwResult);
		return NULL;
	}
	jstring jVmGuid = WCHARToJString(env, wstrVmGuid.c_str());
	return jVmGuid;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DestroyVM
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	int dwResult = pHyperv->_DestroyVm(wstrVmGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DestroyVm."), dwResult);
	}
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_PowerOnVM
(JNIEnv *env, jclass klass, jlong handle, jstring guid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrGuid = Utility_JStringToWCHAR(env, guid);

	pVm = pHyperv->GetVmByGuid(wstrGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return -1;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->PowerOn();

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in PowerOnVm."), dwResult);
	}
	return (jint)dwResult;
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_ShutdownVM
(JNIEnv *env, jclass klass, jlong handle, jstring guid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrGuid = Utility_JStringToWCHAR(env, guid);

	pVm = pHyperv->GetVmByGuid(wstrGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return -1;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->Shutdown();
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in PowerOnVm."), dwResult);
	}
	return (jint)dwResult;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetVMMemorySize
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jint size)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->SetMemorySize(size);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetVmMemorySize."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetVMLogicalProcessorNum
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jint number)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->SetLogicalProcessorNum(number);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetVmLogicalProcessorNum."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_ModifyAttachedScsiHardDisk
(JNIEnv * env, jclass klass, jlong handle, jstring strVmGuid, jstring strDiskPath, jboolean bDeleteOriginalFile, jstring controlName, jint position)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, strVmGuid);
	wstring wstrDiskPath = Utility_JStringToWCHAR(env, strDiskPath);
	wstring wstrControlName = Utility_JStringToWCHAR(env, controlName);

/*	if ( position > 1 || position < 0 )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Invalid ide control position"), 0);
		return;
	}*/
	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->ModifyScsiDisk(wstrControlName, position, wstrDiskPath,bDeleteOriginalFile);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in ModifyAttachedScsiHardDisk."), dwResult);
	}
}


JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_ModifyAttachedIdeHardDisk
(JNIEnv * env, jclass klass, jlong handle, jstring strVmGuid, jstring strDiskPath, jboolean bDeleteOriginalFile, jint controlIdx, jint position)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, strVmGuid);
	wstring wstrDiskPath = Utility_JStringToWCHAR(env, strDiskPath);

	if ( controlIdx > 1 || controlIdx < 0 )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Invalid ide control index"), 0);
		return;
	}
	if ( position > 1 || position < 0 )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Invalid ide control position"), 0);
		return;
	}
	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->ModifyIdeDisk((IDE_CONTROLLER_INDEX)controlIdx, position, wstrDiskPath,bDeleteOriginalFile);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in ModifyAttachedIdeHardDisk."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AttachDVDDiskToVMIde
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring imgFilePath, jint controlIdx, jint position, jboolean attachISO)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrImgFilePath = Utility_JStringToWCHAR(env, imgFilePath);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->AttachImageDvd((IDE_CONTROLLER_INDEX)controlIdx, position, wstrImgFilePath, attachISO);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AttachDVDDiskToVMIde."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AttachDVDDiskToVMSCSI
	(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring imgFilePath, jstring strScsiName, jint position, jboolean attachISO)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrImgFilePath = Utility_JStringToWCHAR(env, imgFilePath);
	wstring wstrScsiName = Utility_JStringToWCHAR(env, strScsiName);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
	CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); 

	int dwResult = pVm->AttachImageDvdToScsi(wstrScsiName, position, wstrImgFilePath, attachISO);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AttachDVDDiskToVMSCSI."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AdjustVirtualCDDriveLetter
(JNIEnv *env, jclass klass, jstring systemDir, jint nIDEController, jint nLocation)
{
	wstring systemDirWChar = Utility_JStringToWCHAR(env, systemDir);

	int dwResult = AdjustVirtualCDDriveLetter_HyperV(systemDirWChar, nIDEController, nLocation);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AdjustVirtualCDDriveLetter."), dwResult);
	}	
}


JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_TakeVmSnapshot
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring snapshotName, jstring snapshotNotes)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrSnapshotName = Utility_JStringToWCHAR(env, snapshotName);
	wstring wstrSnapshotNotes = Utility_JStringToWCHAR(env, snapshotNotes);
	wstring wstrSnapshotGuid;
	jstring jSnapshotGuid;

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

    ///* <huvfe01>2012-12-10 #VM operational status check if vm is operating normally */
    ///* vector<LONG> vOperationalStatus;
    //DWORD dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
    //if (0 == vOperationalStatus.size())
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_TakeVmSnapshot(), Get operational status failed."), dwRet);
    //return NULL;
    //}

    //if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_TakeVmSnapshot(), operational status is not OK."), -1);
    //return NULL;
    //}*/

    static CONST DWORD dwWaitInterval = 1000;
    static CONST DWORD dwLoopmax = 60;
    if (!CheckHyperVmSnapshotApplicableWithRetry((VOID *)pVm, dwWaitInterval, dwLoopmax))
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_TakeVmSnapshot(), VM is not Editable."), -1);
        return NULL;
    }

    pVm->WaitForMergingDisksDone(true);

	vector<LONG> vOperationalStatus;
	DWORD dwRet = pVm->Get_MsvmOperationalStatusEx(vOperationalStatus);
	if (MSVM_OPERATIONAL_STATUS_MAX_INDEX == vOperationalStatus.size()) //<huvfe01>2012-12-26 for issue#151425
	{
		if (MSVM_OPERATIONAL_STATUS_MERGING_DISKS == vOperationalStatus[1])
		{
			ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_TakeVmSnapshot(), VM is merging disks, cannot take snapshot."), -1);
			return NULL;
		}
	}
    
	int dwResult = pVm->TakeSnapshot(wstrSnapshotName,wstrSnapshotNotes,wstrSnapshotGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in TakeVmSnapshot."), dwResult);
		return NULL;
	}
	jSnapshotGuid = WCHARToJString(env, wstrSnapshotGuid.c_str());

	return jSnapshotGuid;
}

JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_MountVHD
(JNIEnv *env, jclass klass, jlong handle, jstring vhdFile,jlong offset)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVhdFile = Utility_JStringToWCHAR(env, vhdFile);
	wstring wstrresult;
	jstring jResult;
		
	int dwResult = pHyperv->MountVHD(wstrVhdFile,offset,wstrresult);

	if(dwResult!=0)
	{
		if (dwResult == VDS_E_INVALID_DISK)
		{
			ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in MountVHD."), INVALID_DYNAMIC_DISK);
			return NULL;
		}
		else
		{
			ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in MountVHD."), dwResult);
			return NULL;
		}
	}

	jResult = WCHARToJString(env, wstrresult.c_str());

	return jResult;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_UnmountVHD
(JNIEnv *env, jclass klass, jlong handle, jstring vhdFile)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVhdFile = Utility_JStringToWCHAR(env, vhdFile);

	int dwResult = pHyperv->UnmountVHD(wstrVhdFile);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in UnmountVHD."), dwResult);
	}
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_OpenHypervMountHandle
(JNIEnv *env, jclass klass, jstring serverName, jstring user, jstring pwd)
{
	wstring wstrServer = Utility_JStringToWCHAR(env, serverName);
	wstring wstrUser = Utility_JStringToWCHAR(env, user);
	wstring wstrPwd = Utility_JStringToWCHAR(env, pwd);

	IMount* pMountHandle = NULL;
	DWORD dwImp = AFImpersonate();

	DWORD dwRet = 0;
	do{
		HMODULE hd = ::LoadLibraryW(L"HypervisorMgr.dll");
		if (!hd)
		{
			dwRet = ::GetLastError();
			logObj.LogW(LL_ERR, dwRet, L"Failed to load HypervisorMgr.dll!");
			break;
		}

		typedef IHypervisor* (WINAPI *PFN_CreateVSphereAPI)(EHypervisorType platform);
		PFN_CreateVSphereAPI pfnCreateVSphereAPI = (PFN_CreateVSphereAPI)GetProcAddress(hd, "GetHypervisorMgr");
		if (!pfnCreateVSphereAPI)
		{
			dwRet = ::GetLastError();
			logObj.LogW(LL_ERR, dwRet, L"Failed to get API GetHypervisorMgr!");
			break;
		}

		IHypervisor* hypervisor = pfnCreateVSphereAPI(Hypervisor_HyperV_t);
		if (!hypervisor)
		{
			dwRet = -1;
			logObj.LogW(LL_ERR, dwRet, L"Failed to get hypervisor api %d!", Hypervisor_HyperV_t);
			break;
		}

		pMountHandle = hypervisor->GetIMount();
		delete hypervisor;

		if (pMountHandle)
		{
			wstring wstrServer = Utility_JStringToWCHAR(env, serverName);
			wstring wstrUser = Utility_JStringToWCHAR(env, user);
			wstring wstrPwd = Utility_JStringToWCHAR(env, pwd);

			HypervisorServer server;
			server.server = wstrServer;
			server.username = wstrUser;
			server.password = wstrPwd;
			if (!pMountHandle->Connect(server, L""))
			{
				CErrorCodeMessage error;
				pMountHandle->GetErrorCodeMessage(error);
				dwRet = error.errCode;
				logObj.LogW(LL_ERR, dwRet, L"fail to connect hyper-v server, %s.", error.errMessage.c_str());

				delete pMountHandle;
				pMountHandle = NULL;
			}
		}
	} while (FALSE);

	if (NULL == pMountHandle)
	{
		string msg = "fail to connect hyper-v server";
		ThrowHyperVException(env, klass, env->NewStringUTF(msg.c_str()), dwRet);
	}

	if (ERROR_SUCCESS == dwImp)
	{
		AFRevertToSelf();
	}

	return (jlong)pMountHandle;
}

void AddItemToVMMountDiskVector(JNIEnv * pEnv, jobject item, LPARAM lParam)
{
	jclass classHyperVMountDisk = pEnv->GetObjectClass(item);
	jmethodID midgetDiskFilePath = pEnv->GetMethodID(classHyperVMountDisk, "getDiskFilePath", "()Ljava/lang/String;");
	jmethodID midgetDiskParentFilePath = pEnv->GetMethodID(classHyperVMountDisk, "getDiskParentFilePath", "()Ljava/lang/String;");

	jmethodID midgetRootVolumePartitionOffset = pEnv->GetMethodID(classHyperVMountDisk, "getRootVolumePartitionOffset", "()J");
	jmethodID midgetSysVolumePartitionOffset = pEnv->GetMethodID(classHyperVMountDisk, "getSysVolumePartitionOffset", "()J");

	VMMountDisk mntDisk;
	mntDisk.diskFilePath = JStringToWString(pEnv, (jstring)pEnv->CallObjectMethod(item, midgetDiskFilePath));
	mntDisk.diskParentFilePath = JStringToWString(pEnv, (jstring)pEnv->CallObjectMethod(item, midgetDiskParentFilePath));

	mntDisk.rootVolumePartitionOffset = pEnv->CallLongMethod(item, midgetRootVolumePartitionOffset);
	mntDisk.sysVolumePartitionOffset = pEnv->CallLongMethod(item, midgetSysVolumePartitionOffset);
	vector<VMMountDisk> * pVMMountDiskVector = (vector<VMMountDisk> *)lParam;
	pVMMountDiskVector->push_back(mntDisk);
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_MountVHDEx
(JNIEnv *env, jclass klass, jlong handle, jobject jobj)
{
	IMount* pMountHandle = ((IMount *)handle);

	jclass class_HyperVMountParam = env->FindClass("com/ca/ha/webservice/jni/HyperVMountParam");
	jfieldID field_rootVolumeSystemDirectory = env->GetFieldID(class_HyperVMountParam, "rootVolumeSystemDirectory", "Ljava/lang/String;");
	jfieldID field_diskList = env->GetFieldID(class_HyperVMountParam, "diskList", "Ljava/util/List;");

	MountDiskParameter mountPara;
	mountPara.rootVolumeSystemDirectory = GetStringFromField(env, &jobj, field_rootVolumeSystemDirectory);
	GoThroughJavaList(env, env->GetObjectField(jobj, field_diskList), AddItemToVMMountDiskVector, (LPARAM)&mountPara.disks);

	mountPara.volLinks.rootVolPath = L"";
	mountPara.volLinks.sysVolPath = L"";

	DWORD dwRet = 0;
	bool ret = pMountHandle->MountDisk(mountPara);
	if (!ret)
	{
		CErrorCodeMessage error;
		pMountHandle->GetErrorCodeMessage(error);

		dwRet = error.errCode;
		logObj.LogW(LL_ERR, dwRet, L"fail to mount vhd files, %s.", error.errMessage.c_str());
		if (dwRet == VDS_E_INVALID_DISK)
		{
			ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in MountVHDEx."), INVALID_DYNAMIC_DISK);
		}
		else
		{
			ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in MountVHDEx."), dwRet);
		}
	}
	else
	{
		if (!mountPara.volLinks.rootVolPath.empty())
		{
			jmethodID mid_HyperVMountParam_setRootVolPath = env->GetMethodID(class_HyperVMountParam, "setRootVolPath", "(Ljava/lang/String;)V");
			env->CallVoidMethod(jobj, mid_HyperVMountParam_setRootVolPath, WCHARToJString(env, mountPara.volLinks.rootVolPath.c_str()));
		}
		if (!mountPara.volLinks.sysVolPath.empty())
		{
			jmethodID mid_HyperVMountParam_setSysVolPath = env->GetMethodID(class_HyperVMountParam, "setSysVolPath", "(Ljava/lang/String;)V");
			env->CallVoidMethod(jobj, mid_HyperVMountParam_setSysVolPath, WCHARToJString(env, mountPara.volLinks.sysVolPath.c_str()));
		}
	}

	if (class_HyperVMountParam != NULL){
		env->DeleteLocalRef(class_HyperVMountParam);
	}

	return dwRet;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_UnmountVHDEx
(JNIEnv *env, jclass klass, jlong handle)
{
	IMount* pMountHandle = ((IMount *)handle);
	pMountHandle->UnMountDisk();
	pMountHandle->UpdateDiskSignature();
}
JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_CloseHypervMountHandle
(JNIEnv *env, jclass klass, jlong handle)
{
	IMount* pMountHandle = ((IMount *)handle);
	if (pMountHandle)
	{
		delete pMountHandle;
		pMountHandle = NULL;
	}
}
JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_InjectIDEDriver
(JNIEnv *env, jclass klass, jstring systemDir)
{
	wstring wstrSystemDir = Utility_JStringToWCHAR(env, systemDir);
	int dwResult = InjectIDEDriver(wstrSystemDir);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in InjectIDEDriver."), dwResult);
	}

	dwResult = InjectSCSIDriver(wstrSystemDir);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in InjectSCSIDriver."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_InjectBCDFile
	(JNIEnv *env, jclass klass, jstring EFISystemVolume, jstring sessionFolder)
{
	wstring wstrEFISystemVolume = Utility_JStringToWCHAR(env, EFISystemVolume);
	wstring wstrSessionFolder = Utility_JStringToWCHAR(env, sessionFolder);
	int dwResult = InjectBCDFile(wstrEFISystemVolume, wstrSessionFolder);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in InjectBCDFile."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_EnableVirtualPlatformServices
	(JNIEnv *env, jclass klass, jstring systemDir, jint platform_type, jboolean bEnable)
{
	wstring wstrSystemDir = Utility_JStringToWCHAR(env, systemDir);
	int dwResult = RVCM_EnableVirtualPlatformServices(wstrSystemDir, (UINT32)platform_type, bEnable);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in EnableVirtualPlatformServices."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_ChangeProductionHostName
(JNIEnv *env, jclass klass, jstring systemDir, jstring newName)
{
	wstring wstrSystemDir = Utility_JStringToWCHAR(env, systemDir);
	wstring wstrNewName = Utility_JStringToWCHAR(env, newName);

	int dwResult = ChangeProductionHostName(wstrSystemDir, wstrNewName);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in ChangeProductionHostName."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_PrepareD2DForFailover
(JNIEnv *env, jclass klass, jstring systemDir, jstring vmType, jstring key, jstring value){
	wstring wstrSystemDir = Utility_JStringToWCHAR(env, systemDir);
	wstring wstrVmType = Utility_JStringToWCHAR(env, vmType);
	wstring wstrKey = Utility_JStringToWCHAR(env, key);
	wstring wstrValue = Utility_JStringToWCHAR(env, value);

	int dwResult = PrepareD2DForFailover(wstrSystemDir, wstrVmType);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in PrepareD2DForFailover."), dwResult);
		return;
	}

	dwResult = InjectScript(wstrSystemDir,wstrKey,wstrValue);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in InjectScript."), dwResult);
	}
}
JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DisableShutDownEventTracker
(JNIEnv *env, jclass klass, jstring systemDir)
{
	wstring wstrSystemDir = Utility_JStringToWCHAR(env, systemDir);
	int dwResult = DisableShutDownEventTracker(wstrSystemDir);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DisableShutDownEvent."), dwResult);
	}	
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetD2DSrvStart
(JNIEnv *env, jclass klass, jstring systemDir, jstring d2dSrv){
	wstring wstrSystemDir = Utility_JStringToWCHAR(env, systemDir);
	wstring wstrD2dSrv = Utility_JStringToWCHAR(env, d2dSrv);

	int dwResult = SetD2DServiceStart(wstrSystemDir, wstrD2dSrv);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetD2DSrvStart."), dwResult);
	}	
}


JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AddPrivateNetwork
(JNIEnv *env, jclass klass, jlong handle, jstring networkName)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrNetworkName = Utility_JStringToWCHAR(env, networkName);
	wstring wstrNetworkGuid;
	jstring jNetworkGuid;

	int dwResult = pHyperv->AddPrivateNetwork(wstrNetworkName, wstrNetworkGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AddPrivateNetwork."), dwResult);
	}
	jNetworkGuid= WCHARToJString(env, wstrNetworkGuid.c_str());

	return jNetworkGuid;
}


JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AddInternalNetwork
(JNIEnv *env, jclass klass, jlong handle, jstring networkName)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrNetworkName = Utility_JStringToWCHAR(env, networkName);
	wstring wstrNetworkGuid;
	jstring jNetworkGuid;

	int dwResult = pHyperv->AddInternalNetwork(wstrNetworkName, wstrNetworkGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AddInternalNetwork."), dwResult);
	}
	jNetworkGuid = WCHARToJString(env, wstrNetworkGuid.c_str());

	return jNetworkGuid;
}

JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AddExternalNetwork
(JNIEnv *env, jclass klass, jlong handle, jstring networkName, jstring physicalAdapter)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrNetworkName = Utility_JStringToWCHAR(env, networkName);
	wstring wstrPhysicalAdapter = Utility_JStringToWCHAR(env, physicalAdapter);
	wstring wstrNetworkGuid;
	jstring jNetworkGuid;

	int dwResult = pHyperv->AddExternalNetwork(wstrNetworkName,wstrPhysicalAdapter, wstrNetworkGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AddExternalNetwork."), dwResult);
	}
	jNetworkGuid = WCHARToJString(env, wstrNetworkGuid.c_str());

	return jNetworkGuid;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AddLegacyNetworkAdapterToVm
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring name, jstring mac, jstring virtualSwitchGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrName = Utility_JStringToWCHAR(env, name);
	wstring wstrMac = Utility_JStringToWCHAR(env, mac);
	wstring wstrVirtualSwitchGuid = Utility_JStringToWCHAR(env, virtualSwitchGuid);
	HyperVManipulation::IVirtualMachine *pVm = NULL;

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if(pVm == NULL)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("invalid virtual machine GUID."), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	CONST DWORD WAIT_INTERVAL = 1000 * 60;
	CONST LONG LOOP_COUNT_MAX = 5;

	int dwResult = -1;
	//LONG State;
	//LONG HealthState;
	//for (INT ii = 0; ii < LOOP_COUNT_MAX; ++ ii)
	//{
	//	State = spVm->GetState(); //<sonmi01>2012-11-29 #edit vm in a editable VM state
	//	HealthState = spVm->GetHealthState();

	//	if (IsHypervVMEditable(State, HealthState))
	//	{
	//		dwResult = pVm->AddLegacyNetworkAdapter(wstrName,wstrMac, wstrVirtualSwitchGuid);
	//		break;
	//	}
	//	else
	//	{
	//		Sleep(WAIT_INTERVAL);
	//	}
	//}

    if (CheckHyperVMEditableWithRetry((VOID *)pVm, WAIT_INTERVAL, LOOP_COUNT_MAX))
    {
        dwResult = pVm->AddLegacyNetworkAdapter(wstrName,wstrMac, wstrVirtualSwitchGuid);
    }

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AddLegacyNetworkAdapterToVm."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AddNetworkAdapterToVm
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring name, jstring mac, jstring virtualSwitchGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrName = Utility_JStringToWCHAR(env, name);
	wstring wstrMac = Utility_JStringToWCHAR(env, mac);
	wstring wstrVirtualSwitchGuid = Utility_JStringToWCHAR(env, virtualSwitchGuid);
	HyperVManipulation::IVirtualMachine *pVm = NULL;

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if(pVm == NULL)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("invalid virtual machine GUID."), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	CONST DWORD WAIT_INTERVAL = 1000 * 60;
	CONST LONG LOOP_COUNT_MAX = 5;

	int dwResult = -1;
	//LONG State;
	//LONG HealthState;
	//for (INT ii = 0; ii < LOOP_COUNT_MAX; ++ ii)
	//{
	//	State = spVm->GetState(); //<sonmi01>2012-11-29 #edit vm in a editable VM state
	//	HealthState = spVm->GetHealthState();

	//	if (IsHypervVMEditable(State, HealthState))
	//	{
	//		dwResult = pVm->AddNetworkAdapter(wstrName,wstrMac, wstrVirtualSwitchGuid);
	//		break;
	//	}
	//	else
	//	{
	//		Sleep(WAIT_INTERVAL);
	//	}
	//}

    if (CheckHyperVMEditableWithRetry((VOID *)pVm, WAIT_INTERVAL, LOOP_COUNT_MAX))
    {
        dwResult = pVm->AddNetworkAdapter(wstrName,wstrMac, wstrVirtualSwitchGuid);
    }

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AddNetworkAdapterToVm."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_EnableHostDHCP
(JNIEnv *env, jclass klass, jstring adapterName)
{
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	int dwResult = EnableHostDHCP(wstrAdapterName);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in EnableHostDHCP."), dwResult);
	}
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_InstallIntegrationService
(JNIEnv *env, jclass klass,jboolean quiet, jboolean restart)
{
	HaUtility::ComEnvironment::Instance().Start();
	int dwResult = InstallIntegrationService(quiet,restart);
	HaUtility::ComEnvironment::Instance().Shutdown();
	return dwResult;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DestroyVm
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	int dwResult = pHyperv->_DestroyVm(wstrVmGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DestroyVm."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetVmBootOrder
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;	
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->SetBootOrder();
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetVmBootOrder."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetVmStartupAction
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jint action)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->SetStartupAction((AUTO_STARTUP_ACTION)action);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetVmStartupAction."), dwResult);
	}
}


JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_ChangeVmFriendlyName
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring name)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrName = Utility_JStringToWCHAR(env, name);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->ChangeFriendlyName(wstrName);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in ChangeVmFriendlyName."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AttachIdeHardDiskToVm
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring diskPath, jint controlIdx, jint position)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrDiskPath = Utility_JStringToWCHAR(env, diskPath);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->AttachIdeDisk((IDE_CONTROLLER_INDEX)controlIdx, position, wstrDiskPath);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AttachIdeHardDiskToVm."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DeleteVmLastSnapshot
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

    ///* <huvfe01>2012-12-10 #VM operational status check if vm is operating normally */
    ///*  vector<LONG> vOperationalStatus;
    //DWORD dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
    //if (0 == vOperationalStatus.size())
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_DeleteVmLastSnapshot(), Get operational status failed."), dwRet);
    //return;
    //}

    //if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_DeleteVmLastSnapshot(), operational status is not OK."), -1);
    //return;
    //}*/

    static CONST DWORD dwWaitInterval = 1000;
    static CONST DWORD dwLoopmax = 60;
    if (!CheckHyperVMEditableWithRetry((VOID *)pVm, dwWaitInterval, dwLoopmax))
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_DeleteVmLastSnapshot(), VM is not Editable."), -1);
        return;
    }

	int dwResult = pVm->DeleteLastSnapshot();
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DeleteVmLastSnapshot."), dwResult);
	}

    pVm->WaitForMergingDisksDone(false); //<huvfe01>2012-12-26 for issue#151425

    return;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DeleteVmSnapshot
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring snapshotGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrSnapshotGuid = Utility_JStringToWCHAR(env, snapshotGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

    ///* <huvfe01>2012-12-10 #VM operational status check if vm is operating normally */
    ///* vector<LONG> vOperationalStatus;
    //DWORD dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
    //if (0 == vOperationalStatus.size())
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_DeleteVmSnapshot(), Get operational status failed."), dwRet);
    //return;
    //}

    //if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_DeleteVmSnapshot(), operational status is not OK."), -1);
    //return;
    //}*/

    static CONST DWORD dwWaitInterval = 1000;
    static CONST DWORD dwLoopmax = 60;
    if (!CheckHyperVMEditableWithRetry((VOID *)pVm, dwWaitInterval, dwLoopmax))
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_DeleteVmSnapshot(), VM is not Editable."), -1);
        return;
    }

	int dwResult = pVm->DeleteSnapshot(wstrSnapshotGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DeleteVmSnapshot."), dwResult);
	}

    pVm->WaitForMergingDisksDone(false); //<huvfe01>2012-12-26 for issue#151425

    return;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DeleteVmSnapshotAsync
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring snapshotGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrSnapshotGuid = Utility_JStringToWCHAR(env, snapshotGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

    static CONST DWORD dwWaitInterval = 1000;
    static CONST DWORD dwLoopmax = 60;
    if (!CheckHyperVMEditableWithRetry((VOID *)pVm, dwWaitInterval, dwLoopmax))
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_DeleteVmSnapshotAsync(), VM is not Editable."), -1);
        return;
    }

	int dwResult = pVm->DeleteSnapshot(wstrSnapshotGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DeleteVmSnapshotAsync."), dwResult);
	}

    return;
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_BeginDeleteSnapshot
  (JNIEnv *env, jclass klass, jstring vmGuid, jstring s1Guid, jstring s1GuidBootable, jstring s2Guid)
{
	HANDLE handle;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrS1Guid = Utility_JStringToWCHAR(env, s1Guid);
	wstring wstrS1GuidBootable = Utility_JStringToWCHAR(env, s1GuidBootable);
	wstring wstrS2Guid = Utility_JStringToWCHAR(env, s2Guid);

	int dwResult = BeginDeleteSnapshot_HyperV(wstrVmGuid.c_str(), wstrS1Guid.c_str(), wstrS1GuidBootable.c_str(), wstrS2Guid.c_str(), &handle);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in BeginDeleteSnapshot_HyperV."), dwResult);
	}

	return (jint)handle;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_EndDeleteSnapshot
  (JNIEnv *env, jclass klass, jlong handle, jboolean suc)
{
	HANDLE phCtx = (HANDLE)handle;

	int dwResult = EndDeleteSnapshot_HyperV(phCtx, suc);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in EndDeleteSnapshot_HyperV."), dwResult);
	}
}

JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_RevertToLastVmSnapshot
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring snapshotGuid;
	jstring jret;

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

    ///* <huvfe01>2012-12-10 #VM operational status check if vm is operating normally */
    ///* vector<LONG> vOperationalStatus;
    //DWORD dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
    //if (0 == vOperationalStatus.size())
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_RevertToLastVmSnapshot(), Get operational status failed."), dwRet);
    //return NULL;
    //}
    //if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_RevertToLastVmSnapshot(), operational status is not OK."), -1);
    //return NULL;
    //}*/

    static CONST DWORD dwWaitInterval = 1000;
    static CONST DWORD dwLoopmax = 60;
    if (!CheckHyperVMEditableWithRetry((VOID *)pVm, dwWaitInterval, dwLoopmax))
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_RevertToLastVmSnapshot(), VM is not Editable."), -1);
        return NULL;
    }

	int dwResult = pVm->RevertToLastSnapshot(snapshotGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in RevertToLastVmSnapshot."), dwResult);
		return NULL;
	}

	jret = WCHARToJString( env, snapshotGuid.c_str() );
	return jret;
}


JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_RevertToVmSnapshot
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring snapshotGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrSnapshotGuid = Utility_JStringToWCHAR(env, snapshotGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

    ///* <huvfe01>2012-12-10 #VM operational status check if vm is operating normally */
    ///* vector<LONG> vOperationalStatus;
    //DWORD dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
    //if (0 == vOperationalStatus.size())
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_RevertToVmSnapshot(), Get operational status failed."), dwRet);
    //return;
    //}
    //if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_RevertToVmSnapshot(), operational status is not OK."), -1);
    //return;
    //}*/

    static CONST DWORD dwWaitInterval = 1000;
    static CONST DWORD dwLoopmax = 60;
    if (!CheckHyperVMEditableWithRetry((VOID *)pVm, dwWaitInterval, dwLoopmax))
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_RevertToVmSnapshot(), VM is not Editable."), -1);
        return;
    }

    /* check if already linked to the specific Guid. */
    wstring strLastSnapshotGuid;
    DWORD dwRet = pVm->GetLastSnapshotGuid(strLastSnapshotGuid);
    if (0 == wstrSnapshotGuid.compare(strLastSnapshotGuid.c_str()))
    {
        return;
    }

	int dwResult = pVm->RevertToSnapshot(wstrSnapshotGuid);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in RevertToVmSnapshot."), dwResult);
	}
}


JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_CreateChildVHD
(JNIEnv *env, jclass klass, jlong handle, jstring vhdFile, jstring child)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVhdFile = Utility_JStringToWCHAR(env, vhdFile);
	wstring wstrChild = Utility_JStringToWCHAR(env, child);

	int dwResult = pHyperv->CreateChildVHD(wstrVhdFile,wstrChild);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in CreateChildVHD."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_CreateIsoImage
(JNIEnv *env, jclass klass, jstring folder, jstring volumeName, jstring imageFile)
{
	HaUtility::ComEnvironment::Instance().Start();

	wstring wstrFolder = Utility_JStringToWCHAR(env, folder);
	wstring wstrVolumeName = Utility_JStringToWCHAR(env, volumeName);
	wstring wstrImageFile = Utility_JStringToWCHAR(env, imageFile);

	int dwResult = CreateIsoImage(wstrFolder,wstrVolumeName,wstrImageFile);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in CreateIsoImage."), dwResult);
	}

	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_EnableHostStatic
(JNIEnv *env, jclass klass, jstring adapterName, jobject IPAddress, jobject mask)
{
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	vector<wstring> ipVector;
	JStrListToCVector(env,klass,IPAddress,ipVector);
	vector<wstring> maskVector; 
	JStrListToCVector(env,klass,mask,maskVector);

	int dwResult = EnableHostStatic(wstrAdapterName,ipVector,maskVector);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in EnableHostStatic."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_EnableHostDNS
(JNIEnv *env, jclass klass, jstring adapterName)
{
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	int dwResult = EnableHostDNS(wstrAdapterName);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in EnableHostDNS."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetHostDNSDomain
(JNIEnv *env, jclass klass, jstring adapterName, jstring DNSDomain)
{
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	wstring wstrDNSDomain = Utility_JStringToWCHAR(env, DNSDomain);
	int dwResult = SetHostDNSDomain(wstrAdapterName,wstrDNSDomain);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetHostDNSDomain."), dwResult);
	}	
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetHostDNSServerSearchOrder
(JNIEnv *env, jclass klass, jstring adapterName, jobject DNSServerSearchOrder)
{
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	vector<wstring> serverSearchOder;
	JStrListToCVector(env,klass,DNSServerSearchOrder,serverSearchOder);
	int dwResult = SetHostDNSServerSearchOrder(wstrAdapterName,serverSearchOder);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetHostDNSServerSearchOrder."), dwResult);
	}
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetHostDNSSuffixSearchOrder
(JNIEnv *env, jclass klass, jstring adapterName, jobject DNSDomainSuffixSearchOrder)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	vector<wstring> suffixSearchOder;
	JStrListToCVector(env,klass,DNSDomainSuffixSearchOrder,suffixSearchOder);
	int dwResult = SetHostDNSSuffixSearchOrder(wstrAdapterName,suffixSearchOder);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetHostDNSSuffixSearchOrder."), dwResult);
	}
	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetHostDynamicDNSRegistration
(JNIEnv *env, jclass klass, jstring adapterName, 
 jboolean fullDNSRegistrationEnabled, 
 jboolean domainDNSRegistrationEnabled)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	int dwResult = SetHostDynamicDNSRegistration(wstrAdapterName,fullDNSRegistrationEnabled,domainDNSRegistrationEnabled);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetHostDynamicDNSRegistration."), dwResult);
	}
	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetHostGateways
(JNIEnv *env, jclass klass, jstring adapterName, jobject gateway, jobject costMetric)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	vector<wstring> vecGateway; 
	JStrListToCVector(env,klass,gateway,vecGateway);
	vector<unsigned short> vecCostMetric;
	JIntListToCVector(env,klass,costMetric,vecCostMetric);
	int dwResult = SetHostGateways(wstrAdapterName,vecGateway,vecCostMetric);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetHostGateways."), dwResult);
	}
	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_SetHostWINSServer
(JNIEnv *env, jclass klass, jstring adapterName, 
 jstring WINSPrimaryServer, jstring WINSSecondaryServer)
{
	HaUtility::ComEnvironment::Instance().Start();
	wstring wstrAdapterName = Utility_JStringToWCHAR(env, adapterName);
	wstring wstrWINSPrimaryServer = Utility_JStringToWCHAR(env, WINSPrimaryServer);
	wstring wstrWINSSecondaryServer = Utility_JStringToWCHAR(env, WINSSecondaryServer);
	int dwResult = SetHostWINSServer(wstrAdapterName,wstrWINSPrimaryServer,wstrWINSSecondaryServer);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in SetHostWINSServer."), dwResult);
	}
	HaUtility::ComEnvironment::Instance().Shutdown();
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetSystemDiskSignature
(JNIEnv *env, jclass klass)
{
	HaUtility::ComEnvironment::Instance().Start();
	DWORD systemSig;
	int dwResult = GetSystemDiskSignature(systemSig);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetSystemDiskSignature."), dwResult);
	}
	HaUtility::ComEnvironment::Instance().Shutdown();
	return (jlong)systemSig;
}


JNIEXPORT jobject JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetFreePhysicalNetworkAdapter
(JNIEnv *env, jclass klass, jlong handle)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	vector<IExternalEthernetPort*> vEthernet;
	vector<IExternalEthernetPort*>::iterator itr;
	HyperVObjectMap mapEthernet;
	DWORD dwResult = 0;
	jobject jobj = NULL;

	dwResult = pHyperv->GetFreePhysicalNetworkAdapter(vEthernet);
	if( dwResult )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetFreePhysicalNetworkAdapter."), dwResult);
		return jobj;
	}

	itr = vEthernet.begin();
	while( itr != vEthernet.end() )
	{
		wstring name = (*itr)->GetName();
		wstring guid = (*itr)->GetGuid();
		mapEthernet.insert(pair<wstring,wstring>(guid,name));
		itr++;
	}

	jobj = CMapToJMap( env, klass, mapEthernet );

	itr= vEthernet.begin();
	while( itr != vEthernet.end() )
	{
		DelIExternalEthernetPort( *(itr) );
		itr++;
	}
	return jobj;
}


JNIEXPORT jobject JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetVmSnapshots
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	vector<ISnapshot*> vSnapshots;
	vector<ISnapshot*>::iterator itr;
	HyperVObjectMap mapSnapshots;
	DWORD dwResult;
	jobject jobj;
	
	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13
	
	dwResult = pVm->GetSnapshotList(vSnapshots);
	if( dwResult )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetVmSnapshots"), dwResult);
		return NULL;
	}
	
	itr = vSnapshots.begin();
	while( itr != vSnapshots.end() )
	{
		wstring name = (*itr)->GetName();
		wstring guid = (*itr)->GetGuid();
		mapSnapshots.insert(pair<wstring,wstring>(guid,name));
		itr++;
	}
	jobj = CMapToJMap( env, klass, mapSnapshots ); 

	itr = vSnapshots.begin();
	while( itr != vSnapshots.end() )
	{
		DelISnapshot(*itr);
		itr++;
	}
	return jobj;

}

JNIEXPORT jobject JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetHostAdapterList
(JNIEnv *env, jclass klass)
{
	try{
		HyperVObjectMap	adapterList = GetHostAdapterList();
		return CMapToJMap(env,klass,adapterList);
	}
	catch(...){
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetHostAdapterList."), 1);
		return NULL;
	}
}

JNIEXPORT jobject JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetVmList
(JNIEnv *env, jclass klass, jlong handle)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	vector<HyperVManipulation::IVirtualMachine*> vVms;
	vector<HyperVManipulation::IVirtualMachine*>::iterator itr;
	HyperVObjectMap mapVms;
	DWORD dwResult;
	jobject jobj;

	dwResult = pHyperv->_GetVmList(vVms);
	if( dwResult )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetVmList"), dwResult);
		return NULL;
	}

	itr = vVms.begin();
	while( itr != vVms.end() )
	{
		wstring name = (*itr)->GetName();
		wstring guid = (*itr)->GetGuid();
		mapVms.insert(pair<wstring,wstring>(guid,name));
		itr++;
	}
	jobj = CMapToJMap( env, klass, mapVms ); 

	itr = vVms.begin();
	while( itr != vVms.end() )
	{
		DelIVirtualMachine(*itr);
		itr++;
	}
	return jobj;
}

JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetVmNotes
(JNIEnv *env, jclass jcls, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	wstring vmNotes(L"");
	if (pVm != NULL)
		vmNotes = pVm->GetNotes();

	jstring retValue = WCHARToJString(env, vmNotes);
	return retValue;
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetVmState
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	return pVm->GetState();
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_checkHyperVVMToolVersion
(JNIEnv *env, jclass jcls, jlong handle, jstring ivmUUID)
{
	IHypervOperation *pHyperv = (IHypervOperation*)handle;
	HyperVManipulation::IVirtualMachine* pvm = NULL;
	pvm = pHyperv->GetVmByGuid(JStringToWCHAR(env, ivmUUID));

	long IntegrationServiceStatus = 0;
	wstring IntegrationServiceVersion;
	CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pvm);
	if (pvm)
		IntegrationServiceStatus = pvm->GetIntegrationServicesStatus(IntegrationServiceVersion);
	else
	{
		ThrowHyperVException(env, jcls, env->NewStringUTF("VM UUID is invalid."), -1);
	}
	
	return IntegrationServiceStatus;
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_getHyperVIPAddresses
(JNIEnv *env, jclass jcls, jlong handle, jstring ivmUUID, jobject ips)
{
	IHypervOperation *pHyperv = (IHypervOperation*)handle;
	HyperVManipulation::IVirtualMachine* pvm = NULL;
	pvm = pHyperv->GetVmByGuid(JStringToWCHAR(env, ivmUUID));

	CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pvm);
	vector<wstring> ipAddresses;
	if (pvm)
		pvm->GetIPAddress(ipAddresses);
	else
	{
		ThrowHyperVException(env, jcls, env->NewStringUTF("VM UUID is invalid."), -1);
	}
	
	jclass listClass = env->FindClass("java/util/List");
	jmethodID addMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

	for (vector<std::wstring>::iterator i = ipAddresses.begin(); i != ipAddresses.end(); i++)
	{
		env->CallBooleanMethod(ips, addMethod, WCHARToJString(env, i->c_str()));
	}

	return 0;
}

JNIEXPORT jboolean JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_IsVMOperatingNormally 
    (JNIEnv *env, jclass klass, jlong handle, jstring vmGuid) //<huvfe01>2012-12-7 #VM operational status
{
    IHypervOperation *pHyperv = ((IHypervOperation *)handle);
    HyperVManipulation::IVirtualMachine *pVm = NULL;
    wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
    vector<LONG> vOperationalStatus;

    pVm = pHyperv->GetVmByGuid(wstrVmGuid);
    if( NULL == pVm )
    {
        ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
        return false;
    }
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm);
    
    vOperationalStatus.clear();
    DWORD dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
    if (0 == vOperationalStatus.size())
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("Get operational status failed."), dwRet);
        return false;
    }

    if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
    {
        return false;
    }

    return true;
}

JNIEXPORT jboolean JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_IsVmInStorageOperationalStatus
(JNIEnv *env, jclass kclass, jlong handle, jstring vmGuid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	vector<LONG> vOperationalStatus;
	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if (NULL == pVm)
	{
		ThrowHyperVException(env, kclass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return false;
	}
	CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm);
	vOperationalStatus.clear();
	DWORD dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
	if (0 == vOperationalStatus.size())
	{
		ThrowHyperVException(env, kclass, env->NewStringUTF("Get operational status failed."), dwRet);
		return false;
	}
	if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
	{
		return false;
	}
	if (vOperationalStatus.size() > 1)
	{
		MSVM_OPERATIONAL_STATUS_INDEX_1 operationalStatus = (MSVM_OPERATIONAL_STATUS_INDEX_1)vOperationalStatus[1];
		if (operationalStatus == MSVM_OPERATIONAL_STATUS_MIGRATING_VM ||
			operationalStatus == MSVM_OPERATIONAL_STATUS_EXPORTING_VM ||
			operationalStatus == MSVM_OPERATIONAL_STATUS_MOVING_STORAGE_2012_VM ||
			operationalStatus == MSVM_OPERATIONAL_STATUS_MOVING_STORAGE_2012R2_VM)
		{
			return true;
		}
	}
	return false;
}
JNIEXPORT jobject JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetVirutalNetworkList
(JNIEnv *env, jclass klass, jlong handle)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	vector<IVirtualSwitch*> vSwitches;
	vector<IVirtualSwitch*>::iterator itr;
	HyperVObjectMap mapSwitches;
	DWORD dwResult = 0;
	jobject jobj;

	dwResult = pHyperv->GetVirutalSwitchList(vSwitches);
	if (dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetVirutalNetworkList."), dwResult);
		return NULL;
	}
	
	itr=vSwitches.begin();
	while( itr != vSwitches.end() )
	{
		wstring name = (*itr)->GetName();
		wstring guid = (*itr)->GetGuid();
		mapSwitches.insert(pair<wstring, wstring>(guid, name));
		DelIVirtualSwitch(*itr);
		itr++;
	}

	return CMapToJMap(env,klass,mapSwitches);
	
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetAttachedDiskImage
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jobject retArr)
{	
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	vector<wstring> vImages;
	DWORD dwResult = 0;


	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	dwResult = pVm->GetDiskImages( vImages );
	if (dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in GetAttachedDiskImage."), dwResult);
		return dwResult;
	}
	
	AddVecString2List(env,&retArr,vImages);
	return dwResult;
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetSnapshotVhds
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jstring snapshotGuid, jobject retArr)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	ISnapshot *pSnapshot = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrSnapshotGuid = Utility_JStringToWCHAR(env, snapshotGuid);
	vector<wstring> vImages;
	DWORD dwResult = 0;

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	dwResult = pVm->GetSnapshotByGuid(wstrSnapshotGuid, pSnapshot);
	if( 0 != dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Invalid snapshot guid"), 0);
		return NULL;
	}
    CAutoPtr<ISnapshot> spSnapshot(pSnapshot); //baide02 for mem leak 2011-04-13
	
	dwResult = pSnapshot->GetDiskImages(vImages);
	if( 0 != dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get vhds for snapshot"), 0);
		return NULL;
	}

	AddVecString2List(env,&retArr,vImages);
	return dwResult;
}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_startTransServer(JNIEnv * env, jclass klass,jint hatransserverport){
	HyperVRepCallBack * callback = new HyperVRepCallBack();
	callback->env = env;
    BOOL bRestartServer = TRUE; //<huvfe01>2012-11-10 for defect#126017
	long iret = Native_HADT_S_StartServer(hatransserverport, callback, bRestartServer); //<huvfe01>2012-11-10 for defect#126017
	delete callback;
	callback = NULL;
	if(iret == -1000) {
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs when getting HADT_S_StartServer."), -100);
	}
	return;


}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_stopTransServer(JNIEnv * env, jclass klass){
	long iret = Native_HADT_S_StopServer();
	if(iret == -1000) {
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs when getting HADT_S_StopServer."), -100);
	}
	return;


}


JNIEXPORT jboolean JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_isDifferencingDisk(JNIEnv * env, jclass klass, jstring filevhd){
	wstring wstrFilevhd = Utility_JStringToWCHAR(env, filevhd);
	IVHDFile* pvhdfile = NULL;
	int iret = CreateVHDFile(wstrFilevhd.c_str(), NULL, &pvhdfile);
	if(iret!=0)
	{
		CComPtr<ISeparateVhdx> spISeparateVhdx; 
		iret = CreateInstanceCSeparateVhdx(wstrFilevhd.c_str(), &spISeparateVhdx, TRUE); //<sonmi01>2013-10-23 ###???
		if (iret)
		{
			ThrowHyperVException(env, klass, env->NewStringUTF("Failed to create vhd or vhdx file object to get vhd type"), iret);
			return false;
		}

		if (spISeparateVhdx)
		{
			ULONG disktype =  spISeparateVhdx->GetDiskStorageType();
			return disktype == ISeparateVhdx::VHDX_DISK_STORAGE_TYPE_DIFF;
		}	 

		return false;
	}
	iret = pvhdfile->GetVHDType();
	pvhdfile->Release();
	
	if(4==iret) {

		return true;
	}
	return false;
}

JNIEXPORT  jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_modifyParentAbsolutePath(JNIEnv * env, jclass klass,jstring filevhd, jstring preDisk){
	wstring wstrFilevhd = Utility_JStringToWCHAR(env, filevhd);
	wstring wstrPreDisk= Utility_JStringToWCHAR(env, preDisk);
	IVHDFile* pvhdfile = NULL;
	int iret = CreateVHDFileEx(wstrFilevhd.c_str(), GENERIC_READ | GENERIC_WRITE, 
        FILE_SHARE_READ|FILE_SHARE_WRITE, NULL, &pvhdfile);
	if(iret!=0)
	{
		if(pvhdfile!=NULL)
			pvhdfile->Release();

		CreateInstanceCDiskImageVhdxParams params =
		{
			NULL,//LPCTSTR parentVhdxFileName;
			wstrFilevhd.c_str(),//LPCTSTR vhdxFileName;
			GENERIC_READ,//DWORD DesiredAccess;
			-1,//DWORD CreationDisposition;
			0,//LONGLONG VirtualDiskSize;
			0,//LONG LogicalSectorSize;
			0,//LONG PhysicalSectorSize;
			0,//LONG BlockSize;
			VHDX_DISK_TYPE_ENUM::DYNAMIC
		};
		CComPtr<IDiskImageVirtual> spIDiskRW;
		HRESULT hr = CreateInstanceCDiskImageVhdx(params, &spIDiskRW, TRUE);
		if (SUCCEEDED(hr))
		{
			return true; // VHDX no need to set.
		}
	
		ThrowHyperVException(env, klass, env->NewStringUTF("Failed to create vhd file object to change parent path"), iret);

		return false;
	}

	iret = pvhdfile->SetParentPath(WIN_ABSOLUTE,wstrPreDisk.c_str());
	if(iret!=0){
		if(pvhdfile!=NULL)
			pvhdfile->Release();
		ThrowHyperVException(env, klass, env->NewStringUTF("Failed to set Parent path"), iret);
		return iret;
	}
	pvhdfile->Release();
	return iret;
}

JNIEXPORT  jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_getParentAbsolutePath(JNIEnv * env, jclass klass,jstring filevhd)
{
	wstring wstrFilevhd = Utility_JStringToWCHAR(env, filevhd);
	IVHDFile* pvhdfile = NULL;
	int iret = CreateVHDFile(wstrFilevhd.c_str(), NULL, &pvhdfile);
	if(iret!=0)
	{
		CreateInstanceCDiskImageVhdxParams params =
		{
			NULL,//LPCTSTR parentVhdxFileName;
			wstrFilevhd.c_str(),//LPCTSTR vhdxFileName;
			GENERIC_READ,//DWORD DesiredAccess;
			-1,//DWORD CreationDisposition;
			0,//LONGLONG VirtualDiskSize;
			0,//LONG LogicalSectorSize;
			0,//LONG PhysicalSectorSize;
			0,//LONG BlockSize;
			VHDX_DISK_TYPE_ENUM::DYNAMIC
		};
		CComPtr<IDiskImageVirtual> spIDiskRW;
		HRESULT hr = CreateInstanceCDiskImageVhdx(params, &spIDiskRW, TRUE);
		if (SUCCEEDED(hr))
		{
			/*wchar_t *path = NULL;
			iret = spIDiskRW->GetParentPath(WIN_ABSOLUTE,&path);
			jstring jret = WCHARToJString( env, path );
			if(path!=NULL)
			::VirtualFree(path, 0, MEM_RELEASE);
			return jret;*/
			
			return filevhd; // Currently return current path without add the new interface.
		}
					
		if(pvhdfile!=NULL)
			pvhdfile->Release();
		ThrowHyperVException(env, klass, env->NewStringUTF("Failed to create vhd file object to get parent path"), iret);

		return NULL;
	}
	wchar_t *path = NULL;
	iret = pvhdfile->GetParentPath(WIN_ABSOLUTE,&path);
	if(iret!=0){
		if(pvhdfile!=NULL)
			pvhdfile->Release();
		if(path!=NULL)
			::VirtualFree(path, 0, MEM_RELEASE);
		ThrowHyperVException(env, klass, env->NewStringUTF("Failed to get Parent path"), iret);
		return NULL;
	}
	pvhdfile->Release();
	if(path == NULL){

		ThrowHyperVException(env, klass, env->NewStringUTF("Failed to get Parent path"), iret);
		return NULL;
	}
	jstring jret = WCHARToJString( env, path );
	if(path!=NULL)
		::VirtualFree(path, 0, MEM_RELEASE);
	return jret;
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_ListScsiControls(JNIEnv * env, jclass klass,jlong handle,jstring vmGuid, jobject retArr){
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	vector<wstring> vScsi;
	DWORD dwResult = 0;


	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	dwResult = pVm->ListScsiControls( vScsi );
	if (dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in ListScsiControls."), dwResult);
		return dwResult;
	}

	AddVecString2List(env,&retArr,vScsi);
	return dwResult;	
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_AddScsiControl(JNIEnv * env, jclass klass,jlong handle,jstring vmGuid, jstring controller){
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring wstrController = Utility_JStringToWCHAR(env, controller);
	DWORD dwResult = 0;


	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	dwResult = pVm->AddScsiControl(wstrController);
	if (dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in AddScsiControl."), dwResult);
		return dwResult;
	}


	return dwResult;	
}

JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetMaxCPUSForHypervVm(JNIEnv *env, jclass klass, jlong handle)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	DWORD dwResult = 0;
	unsigned int cpu = 0;

	dwResult = pHyperv->GetMaxCPUSForVm(cpu);
	if(dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get max cpus for hyper-v vm."), dwResult);
		return -1;
	}

	return cpu;
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetMaxRAMForHypervVm(JNIEnv *env, jclass klass, jlong handle)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	DWORD dwResult = 0;
	unsigned long long ram = 0;

	dwResult = pHyperv->GetMaxRAMForVm(ram);
	if(dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get max RAM for hyper-v vm."), dwResult);
		return -1;
	}

	return ram;
}


JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetHyperVSystemInfo(JNIEnv *env, jclass klass, jlong handle, jobject hyperVSystemInfo)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	DWORD dwResult = 0;
	unsigned int uiCpuCount = 0;
	unsigned long long llTotalPhysicalMemory = 0;
	unsigned long long llAvailablePhysicalMemory = 0;

    dwResult = pHyperv->GetMaxCPUSForVm(uiCpuCount);
	if(dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get max cpu for hyper-v vm."), dwResult);
		return -1;
	}
	
	dwResult = pHyperv->GetMaxRAMForVm(llTotalPhysicalMemory);
	if(dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get max RAM for hyper-v vm."), dwResult);
		return -1;
	}

	dwResult = pHyperv->GetAvailableRAMForVm(llAvailablePhysicalMemory);
	if(dwResult)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get avaiable RAM for hyper-v vm."), dwResult);
		return -1;
	}

	jclass class_hyperVSysInfo = env->GetObjectClass(hyperVSystemInfo);

	jmethodID setCpuCount = env->GetMethodID(class_hyperVSysInfo, "setCpuCount", "(I)V");
	jmethodID setTotalPhysicalMemory = env->GetMethodID(class_hyperVSysInfo, "setTotalPhysicalMemory", "(J)V");
	jmethodID setAaviablePhysicalMemory = env->GetMethodID(class_hyperVSysInfo, "setAvailablePhysicalMemory", "(J)V");

	env->CallVoidMethod(hyperVSystemInfo, setCpuCount, (jint)uiCpuCount);
	env->CallVoidMethod(hyperVSystemInfo, setTotalPhysicalMemory, (jlong)llTotalPhysicalMemory);
	env->CallVoidMethod(hyperVSystemInfo, setAaviablePhysicalMemory, (jlong)llAvailablePhysicalMemory);

	return dwResult;
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DetachIdeDisk
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jint controlIdx, jint position)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return -1;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->DetachIdeDisk((IDE_CONTROLLER_INDEX)controlIdx, position);
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DetachIdeHardDiskToVm."), dwResult);
		return -1;
	}
	return dwResult;
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_DetachAllDisks(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid) 
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return -1;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = pVm->DetachAllDisks();
	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in DetachAllDisks."), dwResult);
		return -1;
	}
	return dwResult;
}

JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_MergeHyperVSnapshots
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid, jint method, jobject jSnapshotList)
{
	DWORD				dwRet = 0;
	IHypervOperation*	pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine*	pVm = NULL;
	wstring				wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	vector<wstring>		vSnapshotList;

	JStrListToCVector(env, klass, jSnapshotList, vSnapshotList);

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if (NULL == pVm) {
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return -1;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

    ///* <huvfe01>2012-12-10 #VM operational status check if vm is operating normally */
    ///*vector<LONG> vOperationalStatus;
    //dwRet = pVm->Get_MsvmOperationalStatus(vOperationalStatus);
    //if (0 == vOperationalStatus.size())
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_MergeHyperVSnapshots(), Get operational status failed."), dwRet);
    //return -1;
    //}

    //if (!IsOK_MSVM_OperationalStatus(vOperationalStatus))
    //{
    //ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_MergeHyperVSnapshots(), operational status is not OK."), -1);
    //return -1;
    //}*/

    static CONST DWORD dwWaitInterval = 1000;
    static CONST DWORD dwLoopmax = 60;
    if (!CheckHyperVMEditableWithRetry((VOID *)pVm, dwWaitInterval, dwLoopmax))
    {
        ThrowHyperVException(env, klass, env->NewStringUTF("::HyperVJNI_MergeHyperVSnapshots(), VM is not Editable."), -1);
        return -1;
    }

	// TODO, chefr03
	dwRet = pVm->MergeSnapshots(method, vSnapshotList);
	if (dwRet != 0) {
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in MergeSnapshots."), dwRet);
		return -1;
	}

    pVm->WaitForMergingDisksDone(false); //<huvfe01>2012-12-26 for issue#151425

	return	dwRet;
}

JNIEXPORT jobject JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetLastSnapshot
(JNIEnv *env, jclass klass, jlong handle, jstring vmGuid)
{
	DWORD				dwRet = 0;
	IHypervOperation*	pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine*	pVm = NULL;
	wstring				wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring				wstrSnapshotGuid;
	jstring				jstrSnapshotGuid;

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if (NULL == pVm) {
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	dwRet = pVm->GetLastSnapshotGuid(wstrSnapshotGuid);
	if (dwRet){
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get last snapshot guid"), 0);
		return NULL;
	}

	if( wstrSnapshotGuid.empty() )
		jstrSnapshotGuid = NULL;
	else
		jstrSnapshotGuid = WCHARToJString(env, wstrSnapshotGuid);
	return jstrSnapshotGuid;
}

JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_GetLastErrorMessage
(JNIEnv *env, jclass klass)
{
	wstring strError = GetHyperVMgrLastError();
	jstring jstrRet = WCHARToJString(env, strError);
	return jstrRet;
}

/*VCM-HyperV*/
JNIEXPORT jlong JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_MountVHDGetWinSysBootVol
(JNIEnv *env, jclass klss,jlong handle, jstring vhdFile, jobject bootVolumePaths, jobject systemVolumePaths)
{

	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring wstrVhdFile = Utility_JStringToWCHAR(env, vhdFile);
	vector<wstring> vBootVolumes;
	wstring wstrSystemVolume;

	DWORD dwResult = 0;

	dwResult = pHyperv->MountVHD_GetWinSysBootVol(wstrVhdFile,vBootVolumes,wstrSystemVolume);

	AddVecString2List(env,&bootVolumePaths,vBootVolumes);
	if(!wstrSystemVolume.empty()){
		AddstringToList(env,klss,(wchar_t*)wstrSystemVolume.c_str(), &systemVolumePaths);
	}
	
	return dwResult;

}

JNIEXPORT jstring JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_getVHDDirForSpecificSnapshot
(JNIEnv *env, jclass klass,jlong handle, jstring vmGuid, jstring snapshotGuid)
{
	DWORD				dwRet = 0;
	IHypervOperation*	pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine*	pVm = NULL;
	ISnapshot*          pSnapshot = NULL;
	wstring				wstrVmGuid = Utility_JStringToWCHAR(env, vmGuid);
	wstring				wstrSnapshotGuid = Utility_JStringToWCHAR(env,snapshotGuid);
	wstring				wstrSnapshotFolder;
	jstring				jstrSnapshotFolder;

	pVm = pHyperv->GetVmByGuid(wstrVmGuid);
	if (NULL == pVm) {
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get IVirtualMachine"), 0);
		return NULL;
	}
    CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	dwRet = pVm->GetSnapshotByGuid(wstrSnapshotGuid, pSnapshot);
	if (pSnapshot == NULL){
		ThrowHyperVException(env, klass, env->NewStringUTF("fail to get ISnapshot object"), 0);
		return NULL;
	}
    CAutoPtr<ISnapshot> snSnap(pSnapshot); //baide02 for mem leak 2011-04-13

	pSnapshot->GetDiskFolder(wstrSnapshotFolder);

	if( wstrSnapshotFolder.empty() )
		jstrSnapshotFolder = NULL;
	else
		jstrSnapshotFolder = WCHARToJString(env, wstrSnapshotFolder);

	return jstrSnapshotFolder;

}

JNIEXPORT void JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_getHyperVDestInfo
	(JNIEnv *env, jclass clz, jobject pathList, jobject retObj)
{
	// Get hyper-v version
	CWinVer winVer;
	WCHAR osVersion[10] = {0};
	swprintf(osVersion, 10, L"%d.%d", winVer.GetMajVer(), winVer.GetMinVer());
	
	jclass Class_HyperVDestinationInfo = env->FindClass("com/ca/arcflash/webservice/data/HyperVDestinationInfo");
	jclass listClass = env->FindClass("java/util/List");
	jfieldID list_field = env->GetFieldID(Class_HyperVDestinationInfo, "isInvalidPathList", "Ljava/util/List;");
	jobject returnList = env->GetObjectField(retObj, list_field);
	jmethodID add_method = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

	jclass list_class = env->GetObjectClass(pathList);
	jmethodID size = env->GetMethodID(list_class, "size", "()I");
	jmethodID get_method = env->GetMethodID(list_class, "get", "(I)Ljava/lang/Object;");

	Proc_HADT_GetHypervVMPathType func = (Proc_HADT_GetHypervVMPathType)DynGetProcAddress(L"HATransClientProxy.dll", "HADT_GetHypervVMPathType");


	jint listSize = env->CallIntMethod(pathList, size);
	for(int i = 0; i < listSize; i++)
	{
		jstring path = (jstring)env->CallObjectMethod(pathList, get_method, i);

		jclass longCls = env->FindClass("java/lang/Long");
		jmethodID mid_Constructor = env->GetMethodID(longCls,"<init>","(J)V");
		jobject obj_value = env->NewObject(longCls,mid_Constructor,(jlong)func(JStringToWCHAR(env, path)));
		env->CallBooleanMethod(returnList, add_method,obj_value);
		if (longCls != NULL) env->DeleteLocalRef(longCls);	
	}

	jmethodID setOS = env->GetMethodID(Class_HyperVDestinationInfo, "setVersion", "(Ljava/lang/String;)V");
	jfieldID PathValidation = env->GetFieldID(Class_HyperVDestinationInfo, "isInvalidPathList", "Ljava/util/List;");
	env->CallVoidMethod(retObj, setOS, WCHARToJString(env, osVersion));
	env->SetObjectField(retObj, PathValidation, returnList);

	if (Class_HyperVDestinationInfo != NULL) env->DeleteLocalRef(Class_HyperVDestinationInfo);	
	if (listClass != NULL) env->DeleteLocalRef(listClass);
	if (list_class != NULL) env->DeleteLocalRef(list_class);	

	return;
}

//<sonmi01>2012-10-19 #for JNI easy to use
JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_RemoveAllVMNetworks
	(JNIEnv *env, jclass klass, jlong handle, jstring guid)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	HyperVManipulation::IVirtualMachine *pVm = NULL;
	wstring wstrGuid = Utility_JStringToWCHAR(env, guid);

	pVm = pHyperv->GetVmByGuid(wstrGuid);
	if( NULL == pVm )
	{
		ThrowHyperVException(env, klass, env->NewStringUTF(_InvalidVMGuidMsg), 0);
		return -1;
	}
	CAutoPtr<HyperVManipulation::IVirtualMachine> spVm(pVm); //baide02 for mem leak 2011-04-13

	int dwResult = RemoveAllHyperVVMNetworks(spVm.m_p);

	if(dwResult!=0)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("Error occurs in RemoveAllHyperVVMNetworks."), dwResult);
	}
	return (jint)dwResult;
}


#if 0 //<sonmi01>2014-1-23 ###???
//<sonmi01>2014-1-22 ###???
namespace {

	static BOOL AFIsNumber( TCHAR ch )
	{
		return (ch >= TEXT('0') && ch <= TEXT('9'));
	}

	static INT AFToNumber( TCHAR ch )
	{
		return (INT)(ch - TEXT('0'));
	}

	static VOID AFExtractNumbers( LPCTSTR pStr, INT Length, vector<INT> & Numbers )
	{
		INT Index = 0;
		while (Index < Length)
		{
			while (Index < Length && !AFIsNumber(pStr[Index]))
			{
				++Index;
			}

			INT Number = 0;
			BOOL bFoundNumber = FALSE;
			while (Index < Length && AFIsNumber(pStr[Index]))
			{
				bFoundNumber = TRUE;
				Number = Number * 10 + AFToNumber(pStr[Index]);
				++Index;
			}

			if (bFoundNumber)
			{
				Numbers.push_back(Number);
			}
		}
	}
}

//<sonmi01>2014-1-22 ###???
// 0 - original hyper-v server version is equal to target ...
//<0 - less
//>0 - greater - print warning
JNIEXPORT jint JNICALL Java_com_ca_ha_webservice_jni_HyperVJNI_CompareHyperVVersion
	(JNIEnv *env, jclass klass, jlong handle, jstring sessionRootPath, jint sessionNumber)
{
	IHypervOperation *pHyperv = ((IHypervOperation *)handle);
	wstring TargetHyperVServerVesion = pHyperv->GetHyperVServerOsVersion();


	wstring wstrsessionRootPath = Utility_JStringToWCHAR(env, sessionRootPath);
	IHypervPersistInfo* pPersistVMDataInfo = NULL;
	int nRet = AFCreateInstanceHyperVPersistInfo(&pPersistVMDataInfo);
	if (nRet)
	{
		ThrowHyperVException(env, klass, env->NewStringUTF("cannot AFCreateInstanceHyperVPersistInfo."), nRet);
	}

	nRet = pPersistVMDataInfo->UnSerialize(wstrsessionRootPath.c_str(), sessionNumber);
	if (nRet)
	{
		pPersistVMDataInfo->Release(); pPersistVMDataInfo = NULL;
		ThrowHyperVException(env, klass, env->NewStringUTF("cannot UnSerialize HyperV persist info."), nRet);
	}

	wstring OriginalHyperVServerVesion = pPersistVMDataInfo->GetHyperVVersion();

	vector<INT> OriginalVersionNumbers;
	vector<INT> TargetVersionNumbers;
	AFExtractNumbers( OriginalHyperVServerVesion.c_str(), OriginalHyperVServerVesion.size(), OriginalVersionNumbers);
	AFExtractNumbers( TargetHyperVServerVesion.c_str(), TargetHyperVServerVesion.size(), TargetVersionNumbers );

	LONG OriginalVersion = MAKELONG((OriginalVersionNumbers.size() >= 2? OriginalVersionNumbers[1] : 0), (OriginalVersionNumbers.size() >= 1? OriginalVersionNumbers[0] : 0));
	LONG TargetVersion = MAKELONG((TargetVersionNumbers.size() >= 2? TargetVersionNumbers[1] : 0), (TargetVersionNumbers.size() >= 1? TargetVersionNumbers[0] : 0));

	if (pPersistVMDataInfo)
	{
		pPersistVMDataInfo->Release();
		pPersistVMDataInfo = NULL;
	}
	
	return (OriginalVersion - TargetVersion);
}
#endif