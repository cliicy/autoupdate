// V2PNativeFacade.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "windows.h"
#include "wchar.h"

#include "jni.h"
#include "V2PNativeFacade.h"
#include "NativeClassIntf.h"
#include "V2PNF_Log.h"

using namespace std;

#ifdef _MANAGED
#pragma managed(push, off)
#endif


static jint __JvmDetachCurrentThread();

#define PATH_SEPARATOR ';' /* define it to be ':' on Solaris */
#define USER_CLASSPATH "." /* where Prog.class is */

BOOL APIENTRY DllMain(HMODULE hModule,
	DWORD  ul_reason_for_call,
	LPVOID lpReserved
	)
{
	switch (ul_reason_for_call)
	{
	case  DLL_THREAD_DETACH:
	{
		if (V2PNFGlobals::Get_isSharedJVM()) //<sonmi01>2015-9-6 ###???
		{
			__JvmDetachCurrentThread();
		}
	}
	break;
	}
	return TRUE;
}


/***********************************************
https://community.oracle.com/thread/1553209

dynamically link to the GetCreatedJavaVMs() function in the jni library (hJVM is the result of an earlier call to LoadLibrary):
// type definitions for function addresses returned by loading jvm.dll
typedef jint (JNICALL GetCreatedJavaVMs_t)( JavaVM**, jsize, jsize* );
// function definitions for functions used in jvm.dll
GetCreatedJavaVMs_t *GetCreatedJavaVMs;

GetCreatedJavaVMs = (GetCreatedJavaVMs_t*)GetProcAddress( hJVM, "JNI_GetCreatedJavaVMs" );

return GetCreatedJavaVMs( jvm, bufLen, nVMs);
Get an appropriate instance on the JVM by called GetCreatedJavaVMs and pulling the first one.
Once we have that, call AttachCurrentThread to get a JNIEnv pointer that we can use for doing work:
JavaVM *jvm;
JNIEnv *jenv;

jint nSize = 1;
jint nVms;
jint nStatus = JAVAVM.GetCreatedJavaVMs( &jvm, nSize, &nVms );

jint res = jvm->AttachCurrentThread((void**) &jenv, NULL);

jclassStartup =
jenv->FindClass(sStartupClass); // do some work
When all done with the invocation, detach the jenv pointer from the current thread:
jvm->DetachCurrentThread();
Works like a charm.
************************************************/

/*************************************
http://stackoverflow.com/questions/9642506/jni-attach-detach-thread-memory-management

Several points about calling back into Java from native code:

AttachCurrentThread should only be called if jvm->GetEnv() returns a zero value.
It's usually a no-op if the thread is already attached, but you can save some overhead.

DetachCurrentThread should only be called if you called AttachCurrentThread.

Avoid the detach if you expect to be called on the same thread in the future.

Depending on your native code's threading behavior,
you may want to avoid the detach and instead store references to all native threads for disposal on termination
(if you even need to do that; you may be able to rely on application shutdown to clean up).

If you continually attach and detach native threads, the VM must continually associate (often the same) threads with Java objects.
Some VMs may re-use threads, or temporarily cache mappings to improve performance,
but you'll get better and more predictable behavior if you don't rely on the VM to do it for you.
*************************************/

static jint __JvmDetachCurrentThread()
{
	jint jRet = JNI_ERR;

	jsize nVMs = 0; //<sonmi01>2015-7-24 ###???
	jRet = JNI_GetCreatedJavaVMs(NULL, 0, &nVMs); // 1. just get the required array length
	if (JNI_OK == jRet && nVMs > 0)
	{
		JavaVM** buffer = new JavaVM*[nVMs];
		ZeroMemory(buffer, sizeof(JavaVM*) * nVMs);
		jRet = JNI_GetCreatedJavaVMs(buffer, nVMs, &nVMs); // 2. get the data
		for (jsize ii = 0; (JNI_OK == jRet && ii < nVMs); ++ii)
		{
			JavaVM* jvm = buffer[ii];
			if (jvm)
			{
				JNIEnv *env = nullptr;
				jRet = jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
				if (jRet == JNI_OK && nullptr != env)
				{
					jRet = jvm->AttachCurrentThread((void **)&env, NULL);
					if (JNI_OK == jRet)
					{
						jRet = jvm->DetachCurrentThread();
					}
				}
			}
		}

		delete[] buffer;
	}

	return jRet;
}

//<sonmi01>2015-5-28 #retry backup on CBT failure
//NativeClass* g_NativeClass = NULL;

V2PNF_API V2PNF_HANDLE V2PNativeFacadeInit(const char* jarList, BOOL bOldVer)
{
	INativeClass* nativeClass = CNativeClassFactory::CreateInstanceNativeClass();
	BOOL bRet = nativeClass->LibInit(jarList, bOldVer);
	if (!bRet)
	{
		nativeClass->Release();
		nativeClass = NULL;
	}

	return (V2PNF_HANDLE)nativeClass;
}

V2PNF_API V2PNF_HANDLE V2PNativeFacadeInit_Ex(void* pJVM, const char* jarList, BOOL bOldVer)
{
	INativeClass* nativeClass = CNativeClassFactory::CreateInstanceNativeClass();
	
	if(pJVM == NULL)
	{
		BOOL bRet = nativeClass->LibInit(jarList, bOldVer);
		if (!bRet)
		{
			nativeClass->Release();
			nativeClass = NULL;
		}
	}
	else
	{
		//g_INativeClass->jvm = (JavaVM*)pJVM;
		CNativeClassFactory::SetJVM(nativeClass, pJVM);
	}
	return (V2PNF_HANDLE)nativeClass;
}

V2PNF_API V2PNF_HANDLE V2PNativeFacadeInit_RPC(const char* jarList/* = NULL*/, BOOL bOldVer/* = FALSE*/)
{

	INativeClass* nativeClass = CNativeClassFactory::CreateInstanceNativeClassRPC();
	BOOL bRet = nativeClass->LibInit(jarList, bOldVer);
	if (!bRet)
	{
		nativeClass->Release();
		nativeClass = NULL;
	}
	
	return (V2PNF_HANDLE)nativeClass;
}

V2PNF_API void* getJVM(V2PNF_HANDLE pHandle)
{
	void* pJVM = NULL;
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		pJVM = CNativeClassFactory::GetJVM(nativeClass); //(void*)nativeClass->jvm;
	}
	return pJVM;
}

V2PNF_API void V2PNativeFacadeExit(V2PNF_HANDLE pHandle)
{
	//NativeClass* nativeClass = (NativeClass*)pHandle;
	//if(nativeClass != NULL)
	//{
	//	nativeClass->LibExit();
	//	delete nativeClass;
	//}
}

////<sonmi01>2015-5-28 #retry backup on CBT failure
//V2PNF_API void V2PNativeFacadeExitDestroyJvm(V2PNF_HANDLE pHandle)
//{
//	NativeClass* nativeClass = (NativeClass*)pHandle;
//	if (nativeClass != NULL)
//	{
//		nativeClass->LibExit();
//		delete nativeClass;
//	}
//}

V2PNF_API int connectToESX(WCHAR* esxServer, WCHAR* esxUser, WCHAR* esxPwd, WCHAR* esxPro, bool bIgnoreCert, long lPort, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->connectToESX(esxServer, esxUser, esxPwd, esxPro, bIgnoreCert, lPort);
	else
		return 1;
}

V2PNF_API int getVMServerType(V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMServerType();
	else
		return 0;
}

V2PNF_API int checkVMServerLicense(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->checkVMServerLicense(esxName, dcName);
	else
		return -1;
}

V2PNF_API BOOL checkVMServerInMaintainenceMode(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->checkVMServerInMaintainenceMode(esxName, dcName);
	else
		return FALSE;
}

V2PNF_API WCHAR* getESXVersion(WCHAR* esxName, WCHAR* dcName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getESXVersion(esxName, dcName);
	else
		return NULL;
}

V2PNF_API int getESXNumberOfProcessors(IN WCHAR* esxName, OUT UINT& numberOfLogicalProcessors, OUT UINT& numberOfProcessors, IN V2PNF_HANDLE pHandle )
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getESXNumberOfProcessors(esxName, numberOfLogicalProcessors, numberOfProcessors);
	else
		return -1;
}

V2PNF_API void disconnectESX(V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		nativeClass->disconnectESX();
	return;
}

V2PNF_API ESXNode* getEsxNodeList(int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getEsxNodeList(count);
	else
		return NULL;
}

V2PNF_API DataStore* getESXHostDataStoreList(ESXNode esxNode, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getESXHostDataStoreList(esxNode, count);
	else
		return NULL;
}

V2PNF_API VM_BasicInfo* getVMList(ESXNode esxNode, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMList(esxNode, count);
	else
		return NULL;
}

V2PNF_API VM_Info* getVMInfo(WCHAR* vmName, WCHAR *vmUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMInfo(vmName, vmUUID);
	else
		return NULL;
}

V2PNF_API int getVMInfoUnderDataCenter(OUT VM_Info* pVMInfo, IN const WCHAR* dcName, IN const WCHAR* vmName, IN V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
        return nativeClass->getVMInfoUnderDataCenter(vmName, dcName, pVMInfo);
	else
		return -1;
}

V2PNF_API BOOL checkResPool(ESXNode esxNode, WCHAR* resPool, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->checkResPool(esxNode, resPool);
	else
		return false;
}

V2PNF_API int checkDSBlockSize(ESXNode esxNode, WCHAR* dataStore, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->checkDSBlockSize(esxNode, dataStore);
	else
		return 0;
}

V2PNF_API WCHAR* takeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info *info, V2PNF_HANDLE pHandle)
{
	return takeSnapShotEx(vmName, vmUUID, snapshotName, info, true, pHandle);
}

//<huvfe01>2015/4/30 expose quiesce option
V2PNF_API WCHAR* takeSnapShotEx(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName, error_Info *info, bool bQuiesce, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->takeSnapShot(vmName, vmUUID, snapshotName, info, bQuiesce);
	else
		return NULL;
}

V2PNF_API BOOL setInstanceUUID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* newUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->setInstanceUUID(vmName, vmUUID, newUUID);
	else
		return false;
}

V2PNF_API WCHAR* checkandtakeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->checkandtakeSnapShot(vmName, vmUUID, snapshotName);
	else
		return NULL;
}

V2PNF_API WCHAR* getVMMoref(WCHAR* vmName, WCHAR* vmUUID,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMMoref(vmName, vmUUID);
	else
		return NULL;
}

V2PNF_API WCHAR* getVMVersion(WCHAR* vmName, WCHAR* vmUUID,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMVersion(vmName, vmUUID);
	else
		return NULL;
}

V2PNF_API BOOL revertSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		 return nativeClass->revertSnapShot(vmName, vmUUID, snapshotURL);
	else
		return FALSE;
}
V2PNF_API BOOL removeSnapShot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->removeSnapShot(vmName, vmUUID, snapshotURL);
	else
		return FALSE;
}

V2PNF_API BOOL removeSnapShotAsync(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->removeSnapShotAsync(vmName, vmUUID, snapshotURL);
	else
		return FALSE;
}

#if 1
V2PNF_API BOOL removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->removeSnapShotByName(vmName, vmUUID, snapshotName);
	else
		return FALSE;
}
#else
V2PNF_API BOOL removeSnapShotByName(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotName,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	BOOL ret = TRUE;
	int count = 0;
	if(nativeClass != NULL)
	{
		Snapshot_Info * info = getVMSnapshotList(vmName, vmUUID, &count, pHandle);
		for(int i = 0; i < count; ++i)
		{
			if( _wcsicmp(info->snapshotName, snapshotName) == 0)
			{
				ret &= nativeClass->removeSnapShotByName(vmName, vmUUID, snapshotName);
			}
		}
		return ret;
	}
	else
		return FALSE;
}
#endif

V2PNF_API BOOL revertSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		 return nativeClass->revertSnapShotByID(vmName, vmUUID, snapshotURL);
	else
		return FALSE;
}
V2PNF_API BOOL removeSnapShotByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->removeSnapShotByID(vmName, vmUUID, snapshotURL);
	else
		return FALSE;
}

V2PNF_API WCHAR* getsnapshotCTF(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getsnapshotCTF(vmName, vmUUID, snapshotURL);
	else
		return NULL;
}

V2PNF_API WCHAR* getparentSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getparentSnapshot(vmName, vmUUID, snapshotURL);
	else
		return NULL;
}

V2PNF_API BOOL powerOnVM(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->powerOnVM(vmName, vmUUID);
	else
		return FALSE;
}

V2PNF_API void powerOffVM(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		nativeClass->powerOffVM(vmName, vmUUID);
	return;
}

V2PNF_API int getVMPowerState(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMPowerState(vmName, vmUUID);
	else
		return -1;
}

V2PNF_API int getVMToolsState(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMToolsState(vmName, vmUUID);
	else
		return -1;
}

V2PNF_API Disk_Info* getVMDiskURLs(WCHAR* vmName, WCHAR* vmUUID, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMDiskURLs(vmName, vmUUID, count);
	else
		return NULL;
}

V2PNF_API Snapshot_Info* getVMSnapshotList(WCHAR* vmName, WCHAR* vmUUID, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMSnapshotList(vmName, vmUUID, count);
	else
		return NULL;
}

V2PNF_API Disk_Info* getSnapShotDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getSnapShotDiskInfo(vmName, vmUUID, snapshotURL, count);
	else
		return NULL;
}

V2PNF_API AdrDisk_Info* getSnapShotAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getSnapShotAdrDiskInfo(vmName, vmUUID, snapshotURL, count);
	else
		return NULL;
}

V2PNF_API AdrDisk_Info* getVMAdrDiskInfo(WCHAR* vmName, WCHAR* vmUUID, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->getVMAdrDiskInfo(vmName, vmUUID, count);
	else
		return NULL;
}

V2PNF_API Disk_Info* getSnapShotDiskInfoByID(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getSnapShotDiskInfoByID(vmName, vmUUID, snapshotURL, count);
	else
		return NULL;
}


V2PNF_API Disk_Info* generateDiskBitMapForSnapshot(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, int *count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->generateDiskBitMapForSnapshot(vmName, vmUUID, snapshotURL, count);
	else
		return NULL;
}

V2PNF_API BOOL getDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  diskURL, int deviceKey, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getDiskBitMap(vmName, vmUUID, diskURL, deviceKey);
	else
		return FALSE;
}

V2PNF_API void deleteDiskBitMap(WCHAR* vmName, WCHAR*  vmUUID, WCHAR*  snapshotURL, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		nativeClass->deleteDiskBitMap(vmName, vmUUID, snapshotURL);
}


V2PNF_API BOOL getFile(WCHAR* vmName, WCHAR* vmUUID, WCHAR* fileName, WCHAR* localPath, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getFile(vmName, vmUUID, fileName, localPath);
	else
		return FALSE;
}

V2PNF_API BOOL setFileStream(WCHAR* vmName, WCHAR* vmUUID, WCHAR* fileName,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->setFileStream(vmName, vmUUID, fileName);
	else
		return FALSE;

}

V2PNF_API int readFileStream(BYTE* pBuff, int length, int* bytesRead , __int64 offSet,  V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->readFileStream(pBuff, offSet, length, bytesRead);
	else
		return  -1;
}
 V2PNF_API WCHAR* getVmdkFilePath(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotURL, DWORD dwDiskSignaure,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVmdkFilePath(vmName, vmUUID, snapshotURL, dwDiskSignaure);
	else
		return NULL;
}
V2PNF_API BOOL removeAllSnapshots(WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->removeAllSnapshots(vmName, vmUUID);
	else
		return FALSE;
}

V2PNF_API BOOL removeSnapshotFromAppliance(WCHAR* vmName, WCHAR*  snapRef, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->removeSnapshotFromAppliance(vmName, snapRef);
	else
		return FALSE;
}
V2PNF_API int VM_GetDiskChangesBitmap(VM_Info* pvm_info,
                     Disk_Info* pDiskDetails,
                     wchar_t* snapshotId,
					 long sectorSize,
                     PLARGE_INTEGER pbitmapSize,
					 PLARGE_INTEGER pUsedSectorCount,
                     wchar_t* filePath,
					 V2PNF_HANDLE pHandle)
 //int getUsedDiskBlocks(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotId, WCHAR* diskChangeId, int DiskDeviceKey, WCHAR* filePath,int chunkSize, int sectorSize,
	//		V2PNF_HANDLE pHandle)
 {
	INativeClass* nativeClass = (INativeClass*)pHandle;
	long chunkSize = 8192;
	//sectorSize = 512;
	if(nativeClass != NULL)
		return nativeClass->getUsedDiskBlocks(pvm_info->vmName, pvm_info->vmInstUUID, snapshotId, pDiskDetails->chageID, pDiskDetails->deviceKey,pbitmapSize,pUsedSectorCount,filePath,chunkSize,sectorSize);
	else
		return NULL;
 }
V2PNF_API int VM_GetDiskBitmap(VM_Info* pvm_info,
                     Disk_Info* pDiskDetails,
                     wchar_t* snapshotId,
					 long sectorSize,
                     PLARGE_INTEGER pbitmapSize,
					 PLARGE_INTEGER pUsedSectorCount,
                     wchar_t* filePath,
					 V2PNF_HANDLE pHandle)
 //int getUsedDiskBlocks(WCHAR* vmName, WCHAR* vmUUID, WCHAR* snapshotId, WCHAR* diskChangeId, int DiskDeviceKey, WCHAR* filePath,int chunkSize, int sectorSize,
	//		V2PNF_HANDLE pHandle)
 {
	INativeClass* nativeClass = (INativeClass*)pHandle;
	long chunkSize = 8192;
	//sectorSize = 512;
	if(nativeClass != NULL)
		return nativeClass->getUsedDiskBlocks(pvm_info->vmName, pvm_info->vmInstUUID, snapshotId, /*pDiskDetails->chageID*/ L"*", pDiskDetails->deviceKey,pbitmapSize,pUsedSectorCount,filePath,chunkSize,sectorSize);
	else
		return NULL;
 }
V2PNF_API int VM_CheckAndEnableChangeTracking(VM_Info* vm_info,V2PNF_HANDLE pHandle)
 //int enableChangeBlockTracking(WCHAR* vmName, WCHAR* vmUUID,BOOL bEnable,V2PNF_HANDLE pHandle)
 {
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->checkAndEnableChangeBlockTracking(vm_info->vmName, vm_info->vmInstUUID);
	else
		return NULL;
 }
V2PNF_API int VM_EnableChangeTracking(VM_Info* vm_info,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->enableChangeBlockTracking(vm_info->vmName, vm_info->vmInstUUID, TRUE);
	else
		return NULL;
}
V2PNF_API int VM_EnableDiskUUID(VM_Info* vm_info,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->enableDiskUUIDForVM(vm_info->vmName, vm_info->vmInstUUID, TRUE);
	else
		return NULL;
}
V2PNF_API int VM_DisableChangeTracking(VM_Info* vm_info,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->enableChangeBlockTracking(vm_info->vmName, vm_info->vmInstUUID, FALSE);
	else
		return NULL;
}
V2PNF_API int VM_GetVSSwriterfiles(VM_Info* vm_info,wchar_t* snapshotId,wchar_t* pathToSave, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVSSwriterfiles(vm_info->vmName, vm_info->vmInstUUID, snapshotId, pathToSave );
	else
		return NULL;
 }

V2PNF_API int VM_SetVMnvramfiles(VM_Info* vm_info,wchar_t* nvRamFile, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->setVMNVRamFile(vm_info->vmName, vm_info->vmInstUUID, nvRamFile );
	else
		return NULL;
}

V2PNF_API WCHAR* VM_GetVMNVRAMFile(VM_Info* vm_info,wchar_t* pathToSave, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getVMNVRAMFile(vm_info->vmName, vm_info->vmInstUUID, pathToSave );
	else
		return NULL;
}

 V2PNF_API BOOL VM_RemoveSnapshot(VM_Info* vm_info,wchar_t* snapshotId,V2PNF_HANDLE pHandle)
 {
	return removeSnapShotByID(vm_info->vmName, vm_info->vmInstUUID, snapshotId, pHandle);
 }

 V2PNF_API BOOL VM_RemoveSnapshotByName(VM_Info* vm_info,wchar_t* snapshotName,V2PNF_HANDLE pHandle)
 {
	 return removeSnapShotByName(vm_info->vmName, vm_info->vmInstUUID, snapshotName, pHandle);
 }

 V2PNF_API BOOL VM_RevertSnapshot(VM_Info* vm_info,wchar_t* snapshotId,V2PNF_HANDLE pHandle)
 {
	return revertSnapShotByID(vm_info->vmName, vm_info->vmInstUUID, snapshotId, pHandle);
 }
 V2PNF_API error_Info VM_TakeSnapshot(VM_Info* vm_info, wchar_t* snapshotName, wchar_t* snapshotId, Disk_Info** ppdiskInfo, int *count,V2PNF_HANDLE pHandle)
 {
	return VM_TakeSnapshotEx(vm_info, snapshotName, snapshotId, ppdiskInfo, count, true, pHandle);
 }

 //<huvfe01>2015/4/30 expose quiesce option
 V2PNF_API error_Info VM_TakeSnapshotEx(VM_Info* vm_info, wchar_t* snapshotName, wchar_t* snapshotId,Disk_Info** ppdiskInfo, int *count, bool bQuiesce, V2PNF_HANDLE pHandle)
 {
	 error_Info info;
	 info.errorCode  = 1;
	 wcscpy_s(info.erroString, DISKURL_SIZE, L"");
	 WCHAR* tempSnapshotId = takeSnapShotEx(vm_info->vmName, vm_info->vmInstUUID, snapshotName, &info, bQuiesce, pHandle);
	 if ( tempSnapshotId )
	 {
		 wcscpy_s(snapshotId,PATH_SIZE,tempSnapshotId);
		 *ppdiskInfo = getSnapShotDiskInfoByID(vm_info->vmName, vm_info->vmInstUUID, snapshotId, count, pHandle);
	 }
	 return info;
 }

 V2PNF_API int VM_GetVMXSpec(VM_Info* vm_info, wchar_t* vmxSpec, V2PNF_HANDLE pHandle)
 {
	 if (vm_info)
	 {
		WCHAR* tempVMXpec = getVMMoref(vm_info->vmName, vm_info->vmInstUUID, pHandle);
		if (tempVMXpec)
		{
			wcscpy_s(vmxSpec,PATH_SIZE,tempVMXpec);			
		}
		else
			return -1;
	 }
	 else
		 return -1;
	 return 1;
 }

 V2PNF_API int VM_GetSnapshotConfigInfo(VM_Info* vm_info,wchar_t* snapshotId,wchar_t* pathToSave, V2PNF_HANDLE pHandle)
 {
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->getSnapshotConfigInfo(vm_info->vmName, vm_info->vmInstUUID, snapshotId, pathToSave );
	else
		return NULL;
 }
V2PNF_API error_Info VM_Create(wchar_t* configFilePath,
						wchar_t* vcName,
						wchar_t* esxHost,
						wchar_t* esxDC,
						wchar_t* vmResPool,
						wchar_t* vmNewName,
						wchar_t* datastoreOfVM,						
						int numDisksToCreate,
						wchar_t** diskUrlList, 
						wchar_t** diskDatastoreList,
						BOOL overwriteVM,
						BOOL recoverToOriginal,
						VM_Info* pVMInfo, //out						
						V2PNF_HANDLE pHandle)							  					
 {
	INativeClass* nativeClass = (INativeClass*)pHandle;
	error_Info info;
	info.errorCode  = -1;
	wcscpy_s(info.erroString, DISKURL_SIZE, L"");
	if(nativeClass != NULL)
		return nativeClass->createVMwareVirtualMachine(configFilePath,
														vcName,
														esxHost,
														esxDC,
														vmResPool,
														vmNewName,
														datastoreOfVM,
														numDisksToCreate,
														diskUrlList, 
														diskDatastoreList,														
														overwriteVM,
														recoverToOriginal,
														pVMInfo);
	else
		return info;
 }
V2PNF_API int VM_GetVMCurrentDiskURLs(VM_Info* vm_info,
									  Disk_Info** ppdiskInfo,
									  int *count,
									  V2PNF_HANDLE pHandle)
{
	
	if(pHandle != NULL && vm_info!=NULL)
	{
		*ppdiskInfo = getVMDiskURLs(vm_info->vmName, vm_info->vmInstUUID, count, pHandle);
		if (ppdiskInfo != NULL)
			return 1;
		else
			return -1;
	}
	else
		return -1;
}

V2PNF_API BOOL VM_PowerOn(VM_Info* vm_info, V2PNF_HANDLE pHandle)
{
	if(pHandle != NULL && vm_info!=NULL)
	{
		return powerOnVM(vm_info->vmName,vm_info->vmInstUUID,pHandle);
	}
	else
		return FALSE;
}

V2PNF_API void VM_PowerOff(VM_Info* vm_info, V2PNF_HANDLE pHandle)
{
	if(pHandle != NULL && vm_info!=NULL)
	{
		powerOffVM(vm_info->vmName,vm_info->vmInstUUID,pHandle);
	}
}
V2PNF_API int VM_Delete(VM_Info* vm_info,V2PNF_HANDLE pHandle)
{
	int rc = 0;
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		rc = nativeClass->deleteVM(vm_info->vmName,vm_info->vmInstUUID);
	}

	return rc;
}
V2PNF_API int VM_Rename(VM_Info* vm_info,wchar_t* vmNewname,V2PNF_HANDLE pHandle)
{
	int rc = 0;
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		rc = nativeClass->renameVM(vm_info->vmName,vm_info->vmInstUUID,vmNewname);
	}
	return rc;
}
V2PNF_API int VM_GetPowerState(VM_Info* vm_info,V2PNF_HANDLE pHandle)
{
	if(pHandle != NULL && vm_info!=NULL)
	{
		return getVMPowerState(vm_info->vmName,vm_info->vmInstUUID,pHandle);
	}
	else
		return -1;
}

V2PNF_API int VM_GetToolsState(VM_Info* vm_info,V2PNF_HANDLE pHandle)
{
	if(pHandle != NULL && vm_info!=NULL)
	{
		return getVMToolsState(vm_info->vmName,vm_info->vmInstUUID,pHandle);
	}
	else
		return -1;
}

V2PNF_API int VM_IsVMNameUsed(wchar_t* vmname,V2PNF_HANDLE pHandle)
{
	int rc = -1;
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		rc = nativeClass->isVMNameUsed(vmname);
	}

	return rc;
}

V2PNF_API void logUserEvent(WCHAR* vmName, WCHAR* vmUUID, WCHAR* eventMessage,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		nativeClass->logUserEvent(vmName,vmUUID,eventMessage);
}

V2PNF_API void deleteCTKFiles(WCHAR* esxName, WCHAR* dcName, WCHAR* vmName, WCHAR* vmUUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		nativeClass->deleteCTKFiles(esxName, dcName, vmName, vmUUID);
}	
V2PNF_API BOOL VM_HasSnapshot(VM_Info* vm_info,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		if(vm_info!=NULL)
			return nativeClass->VMHasSnapshot(vm_info->vmName,vm_info->vmInstUUID);
		else
			return -1;
	}
	else
		return -1;
}

//<sonmi01>2013-6-5 #vds support
V2PNF_API int SetvDSNetworkInfoEx(CONST VMNetworkAdapter_Info * pVMNetworkAdapter_Info, LONG Count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		return nativeClass->SetvDSNetworkInfoEx(pVMNetworkAdapter_Info, Count);		
	}
	else
		return -1;
}

//<sonmi01>2014-1-9 #87330: With upgrade to VDDK 5.5, we need to Check permissions when importing VM from VC
V2PNF_API BOOL hasSufficientPermission(V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
		return nativeClass->hasSufficientPermission();
	else
		return 0;
}

V2PNF_API VMDataStoreInfo* getVMDataStoreDetails(ESXNode nodeDetails, WCHAR* dsName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->getVMDataStoreDetails(nodeDetails, dsName);
	else
		return NULL;
}
V2PNF_API void rescanallHBA(ESXNode nodeDetails, BOOL rescanVC, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->rescanallHBA(nodeDetails, rescanVC);
	else
		return;
}
V2PNF_API WCHAR* addCloneDataStore(ESXNode nodeDetails, WCHAR* dsGUID, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->addCloneDataStore(nodeDetails, dsGUID);
	else
		return NULL;
}
V2PNF_API BOOL destroyandDeleteClone(ESXNode nodeDetails, WCHAR* dsName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->destroyandDeleteClone(nodeDetails, dsName);
	else
		return NULL;
}
V2PNF_API WCHAR* createApplianceVM(ESXNode nodeDetails, WCHAR* vmName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->createApplianceVM(nodeDetails, vmName);
	else
		return NULL;
}
V2PNF_API WCHAR* createStndAloneApplianceVM(ESXNode nodeDetails, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->createStndAloneApplianceVM(nodeDetails);
	else
		return NULL;
}
V2PNF_API BOOL deleteApplianceVM(ESXNode nodeDetails, WCHAR* vmName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->deleteApplianceVM(nodeDetails, vmName);
	else
		return NULL;
}
V2PNF_API WCHAR* attachDiskToVM(WCHAR* vmName, WCHAR* diskURL, WCHAR* esxName, WCHAR*  diskType, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->attachDiskToVM(vmName, diskURL, esxName, diskType);
	else
		return NULL;
}
V2PNF_API WCHAR* detachDiskFromVM(WCHAR* vmName, WCHAR* diskURL, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->detachDiskFromVM(vmName, diskURL);
	else
		return NULL;
}
V2PNF_API BOOL isESXinCluster(ESXNode nodeDetails, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->isESXinCluster(nodeDetails);
	else
		return FALSE;
}
V2PNF_API BOOL isESXunderVC(ESXNode nodeDetails, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->isESXunderVC(nodeDetails);
	else
		return FALSE;
}
V2PNF_API WCHAR* checkandtakeApplianceSnapShot(WCHAR* vmName, WCHAR* snapshotName, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
		return nativeClass->checkandtakeApplianceSnapShot(vmName, snapshotName);
	else
		return NULL;
}
V2PNF_API INT64 GetESXVFlashResource(wchar_t* esxHost, wchar_t* esxDC,V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		return nativeClass->GetESXVFlashResource(esxHost, esxDC);		
	}
	else
		return -1;
}

V2PNF_API INT64 GetVMFlashReadCache(wchar_t* configFilePath, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		return nativeClass->GetVMVFlashReadCache(configFilePath);		
	}
	else
		return -1;
}


V2PNF_API int connectVCloud(const V2PNF_HANDLE pHandle, const wchar_t* vcloudDirectorServerName, const wchar_t* username, const wchar_t* password,  const wchar_t* protocol/* = L"https"*/, const int port/* = 443*/, const bool ignoreCert/* = true*/)
{
	int result = 0;

	do 
	{
		INativeClass* nativeClass = (INativeClass*)pHandle;
		if(nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->connectVCloud(vcloudDirectorServerName, username, password, protocol, port, ignoreCert);

	} while (false);

	return result;	
}

V2PNF_API void disconnectVCloud(const V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if(nativeClass != NULL)
	{
		nativeClass->disconnectVCloud();
	}
}

V2PNF_API int saveVAppInfo(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, const wchar_t* vAppInfoFilePath)
{
	int result = 0;

	do 
	{
		INativeClass* nativeClass = (INativeClass*)pHandle;
		if(nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->saveVAppInfo(vAppId, vAppInfoFilePath);

	} while (false);

	return result;	
}

V2PNF_API int getVMListOfVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, VCloudVM_Info** vmList, int* vmCount)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getVMListOfVApp(vAppId, vmList, vmCount);

	} while (false);

	return result;
}

V2PNF_API int cleanupHotAddedDisksAndConsolidateSnapshot(const V2PNF_HANDLE pHandle, const wstring& esxHost, const vector<wstring>& proxyVMIPList, const vector<wstring>& proxyHotAddedDiskURLs, const wstring& protectedVMInstanceUUID)
{
	int result = FALSE;

	do 
	{
		INativeClass* nativeClass = (INativeClass*)pHandle;
		if(nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->cleanupHotAddedDisksAndConsolidateSnapshot(esxHost, proxyVMIPList, proxyHotAddedDiskURLs, protectedVMInstanceUUID);

	} while (false);

	return result;	
}

V2PNF_API VM_Info* getVMInfoByMoId(V2PNF_HANDLE pHandle, WCHAR* vmMoId)
{
	INativeClass* nativeClass = (INativeClass*) pHandle;
	if (nativeClass != NULL)
		return nativeClass->getVMInfoByMoId(vmMoId);
	else
		return NULL;
}

V2PNF_API int SetVMDiskInfo(CONST Disk_Info* pVMDiskInfo, LONG Count, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*) pHandle;
	if (nativeClass != NULL)
	{
		return nativeClass->SetVMDiskInfo(pVMDiskInfo, Count);
	}
	else
		return -1;
}

V2PNF_API VCloud_CreateVAppResult* createVApp(const V2PNF_HANDLE pHandle, const VCloud_CreateVAppParams* createVAppParams)
{
	VCloud_CreateVAppResult* result = NULL;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			break;
		}

		result = nativeClass->createVApp(createVAppParams);

	} while (false);

	return result;
}

V2PNF_API VCloud_ImportVMResult* importVM(const V2PNF_HANDLE pHandle, const VCloud_ImportVMParams* importVMParams)
{
	VCloud_ImportVMResult* result = NULL;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			break;
		}

		result = nativeClass->importVM(importVMParams);

	} while (false);

	return result;
}


V2PNF_API int deleteVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->deleteVApp(vAppId);

	} while (false);

	return result;
}


V2PNF_API int powerOnVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->powerOnVApp(vAppId);

	} while (false);

	return result;
}


V2PNF_API int powerOffVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->powerOffVApp(vAppId);

	} while (false);

	return result;
}




V2PNF_API int renameVAppEx(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, const wchar_t* suffix, const bool append, const bool renameVM)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->renameVAppEx(vAppId, suffix, append, renameVM);

	} while (false);

	return result;
}


V2PNF_API int getVAppListOfVDC(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, VCloudVApp_Info** vAppList, int* count)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getVAppListOfVDC(vdcId, vAppList, count);

	} while (false);

	return result;
}


V2PNF_API int getVApp(const V2PNF_HANDLE pHandle, const wchar_t* vAppId, VCloudVApp_Info* vAppInfo)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getVApp(vAppId, vAppInfo);

	} while (false);

	return result;
}


V2PNF_API int getVAppInOrg(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, wchar_t* vAppName, VCloudVApp_Info* vAppInfo)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getVAppInOrg(vdcId, vAppName, vAppInfo);

	} while (false);

	return result;
}

V2PNF_API int getESXHostListByDatastoreMoRef(const wchar_t* datastoreMoRef, vector<wstring>& esxHostList, V2PNF_HANDLE pHandle)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getESXHostListByDatastoreMoRef(datastoreMoRef, esxHostList);

	} while (false);

	return result;
}

V2PNF_API int getDatastore(const V2PNF_HANDLE pHandle, const wchar_t* datastoreId, VCloudDatastore_Info* datastoreInfo)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getDatastore(datastoreId, datastoreInfo);

	} while (false);

	return result;
}

V2PNF_API int getESXHostListOfVDC(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, VCloudESXHost_Info** esxHostList, int* count)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*) pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getESXHostListOfVDC(vdcId, esxHostList, count);

	} while (false);

	return result;
}

V2PNF_API int getStorageProfileListOfVDC(const V2PNF_HANDLE pHandle, const wchar_t* vdcId, VCloud_StorageProfile** storageProfileList, int* count)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*)pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->getStorageProfileListOfVDC(vdcId, storageProfileList, count);

	} while (false);

	return result;
}

V2PNF_API int verifyVCloudInfo(const V2PNF_HANDLE pHandle, VCloud_VerifyInfo* vCloudInfo)
{
	int result = 0;

	do
	{
		INativeClass* nativeClass = (INativeClass*)pHandle;
		if (nativeClass == NULL)
		{
			result = -1000;
			break;
		}

		result = nativeClass->verifyVCloudInfo(vCloudInfo);

	} while (false);

	return result;
}


V2PNF_API int setVMCpuMemory(int numCPU, int numCoresPerSocket, long memoryMB, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*) pHandle;
	if (nativeClass != NULL)
	{
		return nativeClass->setVMCpuMemory(numCPU, numCoresPerSocket, memoryMB);
	}
	else
		return -1;
}

V2PNF_API wstring getThumbprint(V2PNF_HANDLE pHandle)
{
	wstring result;

	INativeClass* nativeClass = (INativeClass*) pHandle;
	if (nativeClass != NULL)
	{
		result = nativeClass->getThumbprint();
	}

	return result;		
}

V2PNF_API int consolidateVMDisks(const wchar_t* vmUuid, V2PNF_HANDLE pHandle)
{
	INativeClass* nativeClass = (INativeClass*)pHandle;
	if (nativeClass != NULL)
	{
		return nativeClass->consolidateVMDisks(vmUuid);
	}
	else
		return -1;
}


/// VIX is replaced by vSphere  -- begin --
// function for VIX replace
V2PNF_HANDLE g_hV2PNFVIXReplace = NULL;
CRITICAL_SECTION g_csVIXReplace;
int g_nConnectedEsxServer = 0;
const char* g_pszVIXReplaceJar = "";
V2PNF_API const char * Vix_GetErrorText(VixMgr_Error err, const char *locale)
{
	return "";
}
V2PNF_API const char * Vix_GetLastErrorText(IN int iSess, OUT char* pszError, IN size_t ccError)
{
	do
	{
		if( iSess <= 0
			|| NULL == pszError
			|| ccError <= 0 )
		{
			break;
		}
		if( NULL == g_hV2PNFVIXReplace )
		{
			break;
		}
		*pszError = '\0';

		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		wchar_t szError[1024] = {0};
		int rc = nativeClass->VixVM_GetLastError( iSess, szError, _countof(szError ) );
		if(rc >= 0 )
		{
			::WideCharToMultiByte( CP_ACP, 0, szError, -1, pszError, ccError, NULL, NULL );
		}
	}while( 0 );

	return pszError;
}
V2PNF_API VixMgr_Handle VixHost_Connect(int nAPIVer, VixMgr_ServiceProvider nHostType, const char *pszHostName, int nHostPort, const char *pszUserName, const char *pszPassword,
	VixMgr_HostOptions nOptions, VixMgr_Handle hPropertyListHandle, VixEventProc *fnCallbackProc, void *pvClientData)
{
	int iHandle = VixMgr_INVALID_HANDLE;

	do
	{
		if( NULL == pszHostName
			|| NULL == pszUserName
			|| NULL == pszPassword )
		{
			break;
		}
		if( NULL == g_hV2PNFVIXReplace )
		{
			InitializeCriticalSection( &g_csVIXReplace );
		}
		EnterCriticalSection( &g_csVIXReplace );
		if( NULL == g_hV2PNFVIXReplace )
		{
			g_hV2PNFVIXReplace = V2PNativeFacadeInit( g_pszVIXReplaceJar, FALSE );
		}
		LeaveCriticalSection( &g_csVIXReplace );
		if (NULL == g_hV2PNFVIXReplace)
		{
			break;
		}

		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		iHandle = nativeClass->Vix_ConnectToHost(pszHostName, nHostPort, pszUserName, pszPassword, nOptions, hPropertyListHandle, fnCallbackProc, pvClientData );
		EnterCriticalSection( &g_csVIXReplace );
		if(iHandle > 0 )
		{
			g_nConnectedEsxServer ++;
		}
		LeaveCriticalSection( &g_csVIXReplace );
	}while( 0 );

	return iHandle;
}
V2PNF_API void VixHost_Disconnect(VixMgr_Handle hHostHandle)
{
	int iHandle = (int)hHostHandle;

	do
	{
		if( iHandle <= 0 )
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		nativeClass->Vix_DisconnectFromHost(hHostHandle);

		EnterCriticalSection( &g_csVIXReplace );
		if( NULL != g_hV2PNFVIXReplace && --g_nConnectedEsxServer <= 0 )
		{
			V2PNativeFacadeExit( g_hV2PNFVIXReplace );
			g_hV2PNFVIXReplace = NULL;
			LeaveCriticalSection( &g_csVIXReplace );
			DeleteCriticalSection( &g_csVIXReplace );
			break;
		}
		LeaveCriticalSection( &g_csVIXReplace );
	} while (0);
}
V2PNF_API int Vix_GetESXVersionVM(IN VixMgr_Handle hHostHandle, IN const char* pszVMName)
{
	int nVersion = 0;

	do
	{
		if (hHostHandle <= 0
			|| NULL == pszVMName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}

		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		nVersion = nativeClass->Vix_GetESXVersionVM(hHostHandle, pszVMName);
	}while( 0 );

	return nVersion;
}

V2PNF_API VixMgr_Handle VixVM_Open(IN VixMgr_Handle hHostHandle, IN const char *pszVMName, IN VixEventProc *fnCallbackProc, IN void *pvClientData)
{
	int rc = 0;// it is not a handle!

	do
	{
		if (hHostHandle <= 0
			|| NULL == pszVMName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}

		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_Open(hHostHandle, pszVMName);
	}while( 0 );

	return rc;
}
V2PNF_API void Vix_ReleaseHandle(VixMgr_Handle hHandle)
{
}
V2PNF_API VixMgr_Error Vix_GetProperties(VixMgr_Handle vmHandle, VixMgr_PropertyID firstPropertyID, ...)
{
	switch (firstPropertyID)
	{
		case VixMgr_PROPERTY_JOB_RESULT_HANDLE:
		{
			va_list args;
			va_start(args, firstPropertyID);
			VixMgr_Handle* phVixHandle = va_arg(args, VixMgr_Handle*);
			*phVixHandle = 0x7FFFFFFF;// return a fake handle
			va_end(args);
			break;
		}
		case VixMgr_PROPERTY_FOUND_ITEM_LOCATION:
		{
			va_list args;
			va_start(args, firstPropertyID);
			char** ppbBuf = va_arg(args, char**);
			va_end(args);

			*ppbBuf = (char*)malloc(1024);
			memset( *ppbBuf, 0, 1024 );
		}
		case VixMgr_PROPERTY_VM_NAME: // for find VM
		{
			va_list args;
			va_start(args, firstPropertyID);
			char** pszVMName = va_arg(args, char**);
			va_end(args);

			*pszVMName = strdup("VMName"); // return a fake VM name
		}
		case VixMgr_PROPERTY_JOB_RESULT_GUEST_OBJECT_EXISTS: // result of VixVM_FileExistsInGuest or VixVM_DirectoryExistsInGuest
		{
			va_list args;
			va_start(args, firstPropertyID);
			BOOL* pszFileExist = va_arg(args, BOOL*);
			va_end(args);

			INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
			*pszFileExist = nativeClass->VixVM_GetResultBOOL(vmHandle);
		}
	}
	return 0;
}

V2PNF_API void Vix_FreeBuffer(void *p)
{
	if (NULL != p)
	{
		free(p);
	}
}
V2PNF_API VixMgr_Handle VixHost_FindItems(VixMgr_Handle hostHandle, VixMgr_FindItemType searchType, VixMgr_Handle searchCriteria, UINT32 timeout, VixEventProc *callbackProc, void *clientData)
{
	return 0;
}
V2PNF_API VixMgr_Handle VixVM_LoginInGuest( IN VixMgr_Handle hHostHandle, IN const char *pszUserName, IN const char *pszPassword, IN int nOptions, IN VixEventProc *fnCallbackProc, void *pvClientData )
{
	int rc = 0;

	do
	{
		if (hHostHandle <= 0
			|| NULL == pszUserName
			|| NULL == pszPassword
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}

		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_LoginInGuest( hHostHandle, pszUserName, pszPassword, nOptions, fnCallbackProc, pvClientData);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_LogoutFromGuest(VixMgr_Handle vmHandle, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0)
		{
			break;
		}

		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		nativeClass->VixVM_LogoutFromGuest(vmHandle, NULL, NULL);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_RunProgramInGuest(IN VixMgr_Handle hVMHandle, IN const char *pszGuestProgramName, IN const char *pszCommandLineArgs,
	IN VixMgr_RunProgramOptions nOptions, IN VixMgr_Handle hPropertyListHandle, IN VixEventProc *fnCallbackProc, IN void *pvClientData)
{
	int rc = 0;

	do
	{
		if (hVMHandle <= 0
			|| NULL == pszGuestProgramName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_RunProgramInGuest(hVMHandle, pszGuestProgramName, pszCommandLineArgs, nOptions, hPropertyListHandle, fnCallbackProc, pvClientData);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_CopyFileFromHostToGuest(VixMgr_Handle vmHandle, const char *hostPathName, const char *guestPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == hostPathName
			|| NULL == guestPathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			rc = -1;
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_CopyFileFromHostToGuest(vmHandle, hostPathName, guestPathName, options, propertyListHandle, callbackProc, clientData);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_CopyFileFromGuestToHost(VixMgr_Handle vmHandle, const char *guestPathName, const char *hostPathName, int options, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == hostPathName
			|| NULL == guestPathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_CopyFileFromGuestToHost(vmHandle, guestPathName, hostPathName, options, propertyListHandle, callbackProc, clientData);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_DeleteFileInGuest(VixMgr_Handle vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == guestPathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_DeleteFileInGuest(vmHandle, guestPathName, callbackProc, clientData);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_FileExistsInGuest(VixMgr_Handle vmHandle, const char *guestPathName, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == guestPathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_FileExistsInGuest(vmHandle, guestPathName, callbackProc, clientData);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_ListDirectoryInGuest(VixMgr_Handle vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == pathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		PGUEST_FILE_INFO pFileInfo = nativeClass->VixVM_ListFileInGuest(vmHandle, pathName, 0, callbackProc, clientData);
		rc = nativeClass->VixVM_FilterOutFile(vmHandle, true );

		nativeClass->VixVM_setResultBOOL( vmHandle, rc > 0 ? TRUE : FALSE );
		rc = 1;
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_CreateDirectoryInGuest(VixMgr_Handle vmHandle, const char *pathName, VixMgr_Handle propertyListHandle, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == pathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_CreateDirectoryInGuest(vmHandle, pathName, propertyListHandle, callbackProc, clientData);

		nativeClass->VixVM_setResultBOOL( vmHandle, rc > 0 ? TRUE : FALSE );
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_DeleteDirectoryInGuest(VixMgr_Handle vmHandle, const char *pathName, int options, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == pathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_DeleteDirectoryInGuest(vmHandle, pathName, options, callbackProc, clientData);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_DirectoryExistsInGuest(VixMgr_Handle vmHandle, const char *pathName, VixEventProc *callbackProc, void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == pathName
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixVM_DirectoryExistsInGuest(vmHandle, pathName, callbackProc, clientData);
	} while (0);

	return rc;
}
V2PNF_API int VixJob_GetNumProperties(VixMgr_Handle vmHandle,int resultPropertyID)
{
	int rc = 0;
	do
	{
		if (vmHandle <= 0
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixJob_GetNumProperties(vmHandle, resultPropertyID);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Error VixJob_GetNthProperties(VixMgr_Handle vmHandle,int index,int propertyID,...)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		va_list args;
		va_start(args, propertyID);

		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixJob_GetNthProperties(vmHandle, index, propertyID, args);
		va_end(args);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Handle VixVM_ListProcessesInGuest(VixMgr_Handle vmHandle,int options,VixEventProc *callbackProc,void *clientData)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		PGUEST_PROCESS_INFO pProcessInfo = nativeClass->VixVM_ListProcessesInGuest(vmHandle, options, callbackProc, clientData);
		if (pProcessInfo != NULL)
		{
			rc = 1;
		}
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Error VixJob_Wait(VixMgr_Handle vmHandle, VixMgr_PropertyID firstPropertyID, ...)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		if (VixMgr_PROPERTY_VM_IS_RUNNING != firstPropertyID)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixJob_Wait(vmHandle, firstPropertyID);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Error VixJob_CheckCompletion(VixMgr_Handle vmHandle, bool *complete)
{
	int rc = 0;

	do
	{
		if (vmHandle <= 0
			|| NULL == g_hV2PNFVIXReplace)
		{
			break;
		}
		INativeClass* nativeClass = (INativeClass*)g_hV2PNFVIXReplace;
		rc = nativeClass->VixJob_CheckCompletion(vmHandle, complete);
	} while (0);

	return rc;
}
V2PNF_API VixMgr_Error VixJob_GetError(VixMgr_Handle vmHandle)
{
	return 0;
}
/// VIX is replaced by vSphere  -- end --

//<sonmi01>2015-9-6 ###???
V2PNF_API VOID V2PNFSet_isSharedJVM(BOOL isSharedJVM)
{
	V2PNFGlobals::Set_isSharedJVM(isSharedJVM);
}


#ifdef _MANAGED
#pragma managed(pop)
#endif

