#include "StdAfx.h"
#include "HyperVRepCallBack.h"
#include "HAClientProxy.h"
#include "utils.h"
#include "JNIConv.h"
#include <jni.h>

#define TRY __try{
#define CATCH(procName) }__except(HandleSEH(L"HATransClientProxy.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS(procName) DynGetProcAddress(L"HATransClientProxy.dll", procName)

#define CATCH_SERVER(procName) }__except(HandleSEH(L"HATransServerProxy.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_SERVER_ADDRESS(procName) DynGetProcAddress(L"HATransServerProxy.dll", procName)
#define GET_PROC_CLIENT_ADDRESS(procName) DynGetProcAddress(L"HATransClientProxy.dll", procName)


HyperVRepCallBack::HyperVRepCallBack(void)
: env(false),vmuuid(L"")
{
}

HyperVRepCallBack::~HyperVRepCallBack(void)
{
}


int HyperVRepCallBack::GetVmHostName(const wchar_t* pwszVmGuid, wchar_t* pwszHostName,
									 int nCntIn, int* nCntOut){
	jstring vmguidID = WCHARToJString(env,pwszVmGuid);
	jclass cls = env->FindClass("com/ca/arcflash/webservice/replication/BaseTransReplicationCommand");
	jmethodID methodID=env->GetStaticMethodID(cls, "getHostNameForVmguid", "(Ljava/lang/String;)Ljava/lang/String;");
	jstring hostname = (jstring)env->CallStaticObjectMethod(cls, methodID, vmguidID);
	if(hostname ==NULL) {
		(*nCntOut) = 0;
		//NULL hostname;
		return 1;
	}
	wstring strName = Utility_JStringToWCHAR(env,hostname);
	const wchar_t * t = strName.c_str();
	int x = wcslen(t);
	//empty hostname
	if(x == 0) return 1;

	if(pwszHostName==NULL && nCntIn == 0) {
		(*nCntOut) = x+1;
		return 0;
	}
	
	//nCntIn is less than length of hostname
	if(nCntIn < x+1) 
		return 2;
	
	(*nCntOut) = x+1;
	wcscpy(pwszHostName,t);
	return 0;


}
long HyperVRepCallBack::ReportJobProgress (const wchar_t* pwszJobID, 
                                           unsigned __int64 uliTotal, 
                                           unsigned __int64 uliTrans)
{
/*	printf("ReportJobProgres(), uliTotal = %I64d, uliTrans = %I64d\n", 
		*(unsigned __int64*)(&uliTotal),
		*(unsigned __int64*)(&uliTrans));
*/

	jlong total = uliTotal;
	jlong trans = uliTrans;
	jstring jobID = WCHARToJString(env,pwszJobID);
	jstring jstrvmuuid = WCHARToJString(env,this->vmuuid);
	jclass cls = env->FindClass("com/ca/arcflash/webservice/replication/BaseTransReplicationCommand");
	jmethodID methodID=env->GetStaticMethodID(cls, "hyperVUpdateRepJobMonitorProgress", "(Ljava/lang/String;Ljava/lang/String;JJ)V");
	env->CallStaticVoidMethod(cls, methodID,jstrvmuuid,jobID,total, trans);	
	return 0;
}

long HyperVRepCallBack::ReportDiskProgress(const wchar_t* pwszJobID, unsigned long ulDiskSig,
        unsigned __int64 uliTotal, unsigned __int64 uliTrans)
{
	jlong total = uliTotal;
	jlong trans = uliTrans;
	jstring jobID = WCHARToJString(env,pwszJobID);
	jstring jstrvmuuid = WCHARToJString(env,this->vmuuid);
	jclass cls = env->FindClass("com/ca/arcflash/webservice/replication/BaseTransReplicationCommand");
	jmethodID methodID=env->GetStaticMethodID(cls, "hyperVUpdateRepJobMonitorProgress", "(Ljava/lang/String;Ljava/lang/String;JJ)V");
	env->CallStaticVoidMethod(cls, methodID,jstrvmuuid, jobID,total, trans);	
	return 0;
}


/*
@ulJobID[in]    - The unique ID of job
@ulMsgID[in]    - the message ID in the resource
@ppParams[in]   - pointer to array of zero terminated string, those string is the parameter of the message 
corresponding to ulMsgID
@ulParamCnt[in] - the number of strings in the array pointed by ppParams
*/
long HyperVRepCallBack::ReportMessage      (const wchar_t* pwszJobID, HADT_SEVERITY_LEVEL eLevel, unsigned long ulMsgID, 
											const wchar_t** ppParams, unsigned long ulParamCnt/*, void* pUserData*/)
{
	
	/*String pwszJobID, int eLevel, long ulMsgID, 
	        ArrayList<String> ppParams
*/

	jstring jobID = WCHARToJString(env,pwszJobID);
	jstring jstrvmuuid = WCHARToJString(env,this->vmuuid);
	jint level = eLevel;
	jlong msgID = ulMsgID;

	jclass  arrayList = env->FindClass("java/util/ArrayList");
	jmethodID arrayList_constructor = env->GetMethodID(arrayList, "<init>", "()V");
	jobject jArray = env->NewObject(arrayList, arrayList_constructor);
	const wchar_t** temp = ppParams;
	if(ulParamCnt>0){
		std::vector<std::wstring> vect;
		for(int i = 0;i<ulParamCnt;i++){
			
			wstring wstSessionGuid = *temp;
			temp++;
			vect.push_back(wstSessionGuid);
		}
		AddVecString2List(env,&jArray,vect);
	}

	jclass cls = env->FindClass("com/ca/arcflash/webservice/replication/BaseTransReplicationCommand");
	jmethodID methodID=env->GetStaticMethodID(cls, "hyperVUpdateRepJobMonitorReportMsg", "(Ljava/lang/String;Ljava/lang/String;IJLjava/util/ArrayList;)V");
	env->CallStaticVoidMethod(cls, methodID,jstrvmuuid,jobID,level,msgID, jArray);	

	return 0;
}

long HyperVRepCallBack::ReportDiskSanModeStatus(const wchar_t* pwszJobID,unsigned long ulDiskSig, int nStatus) 
{
	jclass cls = env->FindClass("com/ca/arcflash/webservice/replication/VMwareProxyConversionManager");
	jfieldID fieldid = env->GetStaticFieldID(cls,"FailRepDisks","Ljava/util/Map;");
	if(nStatus != 0)
	{
		jobject jobjFailRepDisks = env->GetStaticObjectField(cls,fieldid);
		cls = env->GetObjectClass(jobjFailRepDisks);
		jmethodID mapGetMethodID = env->GetMethodID(cls,"get","(Ljava/lang/Object;)Ljava/lang/Object;");
		jstring afguid = WCHARToJString(env,this->vmuuid);
		jobject diskCollection = env->CallObjectMethod(jobjFailRepDisks,mapGetMethodID,afguid);
		jlong diskSig = (jlong)ulDiskSig;
		if(nStatus == STAT_DISK_HOTADD_FAILED){
			nStatus = 4;
		}
		jint diskStatus = (jint)nStatus;
		jclass clsLong = env->FindClass("java/lang/Long");
		jclass clsInt = env->FindClass("java/lang/Integer");
		jmethodID methodidLong = env->GetMethodID(clsLong,"<init>","(J)V");
		jmethodID methodidInt = env->GetMethodID(clsInt,"<init>","(I)V");
		jobject jobjLong = env->NewObject(clsLong,methodidLong,diskSig);
		jobject jobjInt = env->NewObject(clsInt,methodidInt,diskStatus);
		jmethodID mapPutMethodID = env->GetMethodID(cls,"put","(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		env->CallObjectMethod(diskCollection,mapPutMethodID,jobjLong,jobjInt);
	}
	return 0;

}


DWORD WINAPI Native_HADT_StartReplicaJobEx(const HA_JOBSCRIPT* pstJobScript, 
                                   IHADTCallback* pCallback)
{
	TRY
	Proc_HADT_StartReplicaJobEx pfun = (Proc_HADT_StartReplicaJobEx)GET_PROC_ADDRESS("HADT_StartReplicaJobEx");
	return pfun(pstJobScript,pCallback);
	CATCH("HADT_StartReplicaJobEx")
}
DWORD WINAPI Native_HADT_S_StartServer(int nPort,IHASrvCallback* pCallback, BOOL bRestartServer){//<huvfe01>2012-11-10 for defect#126017
	TRY
	Proc_HADT_S_StartServer pfun = (Proc_HADT_S_StartServer)GET_PROC_SERVER_ADDRESS("HADT_S_StartServer");
	if(pfun == NULL) return -1000;
	return pfun(nPort, pCallback, bRestartServer);
	CATCH_SERVER("HADT_S_StartServer")
}
DWORD WINAPI Native_HADT_S_StopServer(){
	TRY
	Proc_HADT_S_StopServer pfun = (Proc_HADT_S_StopServer)GET_PROC_SERVER_ADDRESS("HADT_S_StopServer");
	if(pfun == NULL) return -1000;
	return pfun();
	CATCH_SERVER("HADT_S_StopServer")
}

DWORD WINAPI Native_HADT_GetDestSubRoot(const ST_HASRV_INFO* pstSrvInfo,
										const wchar_t* pwszProductNode,
										const HA_SRC_ITEM* pwszSrcItem,
										const wchar_t* pwszLastDesRoot,
										int nDesRootNum,
										const wchar_t** ppwszDesRootList,
										wchar_t* pwszBuf,
										int nLenInWord)
{
	TRY
		Proc_HADT_GetDestSubRoot pfun = (Proc_HADT_GetDestSubRoot)GET_PROC_CLIENT_ADDRESS("HADT_GetDestSubRoot");
		if(pfun == NULL) return -1;
		return pfun(pstSrvInfo,pwszProductNode,pwszSrcItem,pwszLastDesRoot,nDesRootNum,ppwszDesRootList,pwszBuf,nLenInWord);
	CATCH_SERVER("HADT_S_StopServer")
}

int ConvertVMDKConnParams(JNIEnv *env, jobject& connParams, VMDK_CONNECT_MORE_PARAMS& moreParams)
{
	jclass class_ConnParams = env->GetObjectClass(connParams);

	/*public void setCode(int code)
	{
	this.code = code;
	}
	*/
	jmethodID id_ConnParams_method = env->GetMethodID(class_ConnParams, "getCode", "()I");
	jint codeResult = env->CallIntMethod(connParams, id_ConnParams_method);
	moreParams.code = codeResult;
	/*
	public int getFlags()
	{
	return flags;
	}
	*/
	id_ConnParams_method = env->GetMethodID(class_ConnParams, "getFlags", "()I");
	jint flagsResult = env->CallIntMethod(connParams, id_ConnParams_method);
	moreParams.flags = flagsResult;

	/*
	public String getThumbprint()
	{
	return thumbprint;
	}
	*/
	id_ConnParams_method = env->GetMethodID(class_ConnParams, "getThumbprint", "()Ljava/lang/String;");
	jobject objResult = env->CallObjectMethod(connParams, id_ConnParams_method);
	moreParams.thumbprint = JStringToChar(env, (jstring)objResult);

	return 0;
}

int ConvertVMwareVolumeInfo(JNIEnv *env, jobject& bootVol, VMwareVolumeInfo& volumeInfo)
{
	jclass class_volumeInfo = env->GetObjectClass(bootVol);

	jmethodID id_volumeInfo_method = env->GetMethodID(class_volumeInfo, "getBootVolumeGUID", "()Ljava/lang/String;");
	jobject bootGUIDResult = env->CallObjectMethod(bootVol, id_volumeInfo_method);
	wchar_t* tempResult = JStringToWCHAR(env, (jstring)bootGUIDResult);
	if (tempResult)
	{
		wcscpy_s(volumeInfo.bootVolumeGUID, MAX_PATH, tempResult);
		free(tempResult);
	}

	id_volumeInfo_method = env->GetMethodID(class_volumeInfo, "getSystemVolumeGUID", "()Ljava/lang/String;");
	jobject systemGUIDResult = env->CallObjectMethod(bootVol, id_volumeInfo_method);
	tempResult = JStringToWCHAR(env, (jstring)systemGUIDResult);
	if (tempResult)
	{
		wcscpy_s(volumeInfo.systemVolumeGUID, MAX_PATH, tempResult);
		free(tempResult);
	}

	id_volumeInfo_method = env->GetMethodID(class_volumeInfo, "getBootVolumeWindowsSystemRootFolder", "()Ljava/lang/String;");
	jobject objResult = env->CallObjectMethod(bootVol, id_volumeInfo_method);
	tempResult = JStringToWCHAR(env, (jstring)objResult);
	if (tempResult)
	{
		wcscpy_s(volumeInfo.rootFolder, MAX_PATH, tempResult);
		free(tempResult);
		tempResult = NULL;
	}

	return 0;
}


int RepJobMonitor2RepJobScript(JNIEnv *env, jobject& jobMonitor, HA_JOBSCRIPT& jobScript)
{
	jclass class_JJobMonitor = env->GetObjectClass(jobMonitor);

	/*public long getUlJobType() {
		return ulJobType;
	}
	*/
	jmethodID id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlJobType", "()J");
	jlong longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulJobType = longResuslt;
	/*
	public String getPwszJobID() {
		return pwszJobID;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszJobID", "()Ljava/lang/String;");
	jobject objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszJobID = JStringToWCHAR(env,(jstring)objResult);

	/*
	public String getAfGuid() {
	return afGuid;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getAfGuid", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszVmInstID = JStringToWCHAR(env,(jstring)objResult);

	/*
	public long getUlProtocol() {
		return ulProtocol;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlProtocol", "()J");
	longResuslt  = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulProtocol = longResuslt;
	/*
	public String getPwszLocalUsername() {
		return pwszLocalUsername;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszLocalUsername", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszLocalUsername = JStringToWCHAR(env,(jstring)objResult);
	/*
	public String getPwszLocalPassword() {
		return pwszLocalPassword;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszLocalPassword", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszLocalPassword = JStringToWCHAR(env,(jstring)objResult);
	/*
	public String getPwszProductNode() {
		return pwszProductNode;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszProductNode", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszProductNode = JStringToWCHAR(env,(jstring)objResult);
	/*
	public String getPwszDesHostName() {
		return pwszDesHostName;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszDesHostName", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszDesHostName = JStringToWCHAR(env,(jstring)objResult);
	/*
	public String getPwszDesPort() {
		return pwszDesPort;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszDesPort", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszDesPort = JStringToWCHAR(env,(jstring)objResult);
	/*

	public String getPwszDesFolder() {
		return pwszDesFolder;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszDesFolder", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszDesFolder = JStringToWCHAR(env,(jstring)objResult);
	
	/*
	public String getPwszOldDesFolder() {
	return pwszOldDesFolder;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszOldDesFolder", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszLastDesRoot = JStringToWCHAR(env,(jstring)objResult);
	
	/*
	public String getPwszUserName() {
		return pwszUserName;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszUserName", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszUserName = JStringToWCHAR(env,(jstring)objResult);
	/*
	public String getPwszPassword() {
		return pwszPassword;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszPassword", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszPassword = JStringToWCHAR(env,(jstring)objResult);
	/*
	public long getUlSrcItemCnt() {
		return ulSrcItemCnt;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlSrcItemCnt", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulSrcItemCnt = longResuslt;
	
	/*
	public int getUlReplicaConvType() {
		return ulReplicaConvType;
	}
	*/

	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlReplicaConvType", "()I");
	jint conversion_type = env->CallIntMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulReplicaConvType = conversion_type;
	if(jobScript.ulReplicaConvType)
	{
		id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getVmwareRepModel", "()Lcom/ca/arcflash/webservice/jni/VMwareRepParameterModel;");
		objResult = env->CallObjectMethod(jobMonitor,id_JJobMonitor_method);
		jclass class_vmware_rep = env->GetObjectClass(objResult);
		
		/*
		getPwszEsxHostName()
		*/
		jmethodID methodID_vmware_rep = env->GetMethodID(class_vmware_rep,"getPwszEsxHostName","()Ljava/lang/String;");
		jobject object_value = env->CallObjectMethod(objResult,methodID_vmware_rep);
		jobScript.pwszEsxHostName = JStringToWCHAR(env,(jstring)object_value);

		/*
		getPwszEsxUserName()
		*/
		methodID_vmware_rep = env->GetMethodID(class_vmware_rep,"getPwszEsxUserName","()Ljava/lang/String;");
		object_value = env->CallObjectMethod(objResult,methodID_vmware_rep);
		jobScript.pwszEsxUserName = JStringToWCHAR(env,(jstring)object_value);

		methodID_vmware_rep = env->GetMethodID(class_vmware_rep,"getPwszEsxPassword","()Ljava/lang/String;");
		object_value = env->CallObjectMethod(objResult,methodID_vmware_rep);
		jobScript.pwszEsxPassword = JStringToWCHAR(env,(jstring)object_value);

		methodID_vmware_rep = env->GetMethodID(class_vmware_rep,"getPwszMoref","()Ljava/lang/String;");
		object_value = env->CallObjectMethod(objResult,methodID_vmware_rep);
		jobScript.pwszMoref = JStringToWCHAR(env,(jstring)object_value);

		methodID_vmware_rep = env->GetMethodID(class_vmware_rep,"getUlVDDKPort","()I");
		jint int_value = env->CallIntMethod(objResult,methodID_vmware_rep);
		jobScript.ulVDDKPort = int_value;

		//add by zhepa02 at 2015-05-25
		methodID_vmware_rep = env->GetMethodID(class_vmware_rep, "getpwszEsxThumbprint", "()Ljava/lang/String;");
		object_value = env->CallObjectMethod(objResult, methodID_vmware_rep);
		jobScript.pwszEsxThumbprint = JStringToWCHAR(env, (jstring)object_value);

		jobScript.ulDesVHDFormat = NULL;
		jobScript.ulCtlFlag = 4;

		methodID_vmware_rep = env->GetMethodID(class_vmware_rep,"getIsSAN","()I");
		int_value = env->CallIntMethod(objResult,methodID_vmware_rep);
		jobScript.ulSubType = int_value;

		methodID_vmware_rep = env->GetMethodID(class_vmware_rep,"getSnapshotUrl","()Ljava/lang/String;");
		object_value = env->CallObjectMethod(objResult,methodID_vmware_rep);
		jobScript.pwszSnapshotID = JStringToWCHAR(env,(jstring)object_value);
		

	}
	else
	{
		jobScript.pwszEsxHostName = NULL;
		jobScript.pwszEsxUserName = NULL;	
		jobScript.pwszEsxPassword  = NULL;
		jobScript.pwszMoref = NULL;
		jobScript.pwszSnapshotID = NULL;

	}
		

	/*
	public ArrayList<SourceItemModel> getpSrcItemList() {
		return pSrcItemList;
	}
	*/
	if(jobScript.ulSrcItemCnt> 0)
	{
		id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getpSrcItemList", "()Ljava/util/ArrayList;");
		objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
		jobScript.pSrcItemList = new HA_SRC_ITEM[jobScript.ulSrcItemCnt];
		memset(jobScript.pSrcItemList, 0, sizeof(HA_SRC_ITEM));
		HA_SRC_ITEM * base = jobScript.pSrcItemList;
		jclass class_list = env->GetObjectClass(objResult);
		jmethodID id_array_get = env->GetMethodID(class_list, "get", "(I)Ljava/lang/Object;");
		for(int i = 0; i< jobScript.ulSrcItemCnt; i++,base++){
			
			jobject src_item = env->CallObjectMethod(objResult,id_array_get,i);
			//jclass itemclass = env->FindClass("com/ca/arcflash/webservice/jni/SourceItemModel");
			jclass itemclass = env->GetObjectClass(src_item);
			/*
			public String getPwszPath() {
				return pwszPath;
			}
			*/
			jmethodID item_id_JJobMonitor_method = env->GetMethodID(itemclass, "getPwszPath", "()Ljava/lang/String;");
			jobject item_objResult = env->CallObjectMethod(src_item, item_id_JJobMonitor_method);
			base->pwszPath = JStringToWCHAR(env,(jstring)item_objResult);
				/*
			public String getPwszSFUsername() {
				return pwszSFUsername;
			}
				*/
			item_id_JJobMonitor_method = env->GetMethodID(itemclass, "getPwszSFUsername", "()Ljava/lang/String;");
			item_objResult = env->CallObjectMethod(src_item, item_id_JJobMonitor_method);
			base->pwszSFUsername = JStringToWCHAR(env,(jstring)item_objResult);
				/*
			public String getPwszSFPassword() {
				return pwszSFPassword;
			}
				*/
			item_id_JJobMonitor_method = env->GetMethodID(itemclass, "getPwszSFPassword", "()Ljava/lang/String;");
			item_objResult = env->CallObjectMethod(src_item, item_id_JJobMonitor_method);
			base->pwszSFPassword = JStringToWCHAR(env,(jstring)item_objResult);
				
				/*
					public int getDiskCount() {
						return diskCount;
					}
				*/
			item_id_JJobMonitor_method = env->GetMethodID(itemclass, "getDiskCount", "()I");
			longResuslt = env->CallIntMethod(src_item, item_id_JJobMonitor_method);
			base->ulD2DFileCnt = longResuslt;
			if (base->ulD2DFileCnt > 0)
			{
				jobScript.pSrcItemList[0].pD2DFileList = new HA_ITEM_FILE[base->ulD2DFileCnt];
				memset(jobScript.pSrcItemList[0].pD2DFileList,0,sizeof(HA_ITEM_FILE));
				jmethodID methodid_src_item = env->GetMethodID(itemclass,"getFiles","()Ljava/util/List;");
				jobject object_file_list = env->CallObjectMethod(src_item,methodid_src_item);
				jclass class_file_list = env->GetObjectClass(object_file_list);
				jmethodID methodid_list_get = env->GetMethodID(class_file_list,"get","(I)Ljava/lang/Object;");
				HA_ITEM_FILE* pFileItem = jobScript.pSrcItemList[0].pD2DFileList;
				for(int index = 0 ; index < base->ulD2DFileCnt; index++,pFileItem++){

					jobject object_file_item = env->CallObjectMethod(object_file_list,methodid_list_get,index);
					jclass class_file_item = env->GetObjectClass(object_file_item);

					jmethodID methodid_file_item = env->GetMethodID(class_file_item,"getFilePath","()Ljava/lang/String;");
					jobject object_ret_value = env->CallObjectMethod(object_file_item,methodid_file_item);
					pFileItem->pwszPath = JStringToWCHAR(env,(jstring)object_ret_value);

					methodid_file_item = env->GetMethodID(class_file_item,"getFileDestination","()Ljava/lang/String;");
					object_ret_value = env->CallObjectMethod(object_file_item,methodid_file_item);
					pFileItem->pwszDesRoot = JStringToWCHAR(env,(jstring)object_ret_value);

					methodid_file_item = env->GetMethodID(class_file_item,"getBlockSize","()J");
					jlong blockSize = env->CallLongMethod(object_file_item,methodid_file_item);
					pFileItem->ulVmfsBlockSize = blockSize;

					if(jobScript.ulReplicaConvType)
					{
						pFileItem->pwszDesRoot = _wcsdup(jobScript.pwszDesFolder);

						methodid_file_item = env->GetMethodID(class_file_item,"getFileVMDKUrl","()Ljava/lang/String;");
						object_ret_value = env->CallObjectMethod(object_file_item,methodid_file_item);
						pFileItem->pwszVMDKPath = JStringToWCHAR(env,(jstring)object_ret_value);
					}
					else
					{
						pFileItem->pwszVMDKPath = NULL;
					}
				}
			}
		}
	}
	/*

	public long getUlDesVHDFormat() {
		return ulDesVHDFormat;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlDesVHDFormat", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulDesVHDFormat = longResuslt;
	/*
	public long getUlThrottling() {
		return ulThrottling;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlThrottling", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulThrottling = longResuslt;
	/*
	public long getUlCtlFlag() {
		return ulCtlFlag;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlCtlFlag", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulCtlFlag = longResuslt;
	/*
	public boolean isbCompressOnWire() {
		return bCompressOnWire;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "isbCompressOnWire", "()Z");
	jboolean boolResult = env->CallBooleanMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.bCompressOnWire = boolResult;
	/*
	public boolean isbEncryptOnWire() {
		return bEncryptOnWire;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "isbEncryptOnWire", "()Z");
	boolResult = env->CallBooleanMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.bEncryptOnWire = boolResult;
	/*
	public boolean isbOverwriteExist() {
		return bOverwriteExist;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "isbOverwriteExist", "()Z");
	boolResult = env->CallBooleanMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.bOverwriteExist = boolResult;
	/*
	public String getPwszCryptPassword() {
		return pwszCryptPassword;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getPwszCryptPassword", "()Ljava/lang/String;");
	objResult = env->CallObjectMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.pwszCryptPassword = JStringToWCHAR(env,(jstring)objResult);

	//smart copy
	/*
	public boolean isbSmartCopy() {
	return bSmartCopy;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "isbSmartCopy", "()Z");
	boolResult = env->CallBooleanMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.bSmartCopy = boolResult;
		
	/*
	public long getUlScSessBegin() {
	return ulScSessBegin;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlScSessBegin", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulScSessBegin = longResuslt;

	/*
	public long getUlScSessEnd() {
	return ulScSessEnd;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getUlScSessEnd", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulScSessEnd = longResuslt;

	/*
	public long getBackupDescType() {
		return ulBackupDescType;
	}
	*/
	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getBackupDescType", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.ulBackupDescType = longResuslt;

	id_JJobMonitor_method = env->GetMethodID(class_JJobMonitor, "getJobID", "()J");
	longResuslt = env->CallLongMethod(jobMonitor, id_JJobMonitor_method);
	jobScript.lJobID = longResuslt;
	return 0;
}
#define Free_NULL(x) if(x!=NULL) {free(x); x = NULL;}
void clear_jobscript(HA_JOBSCRIPT& jobScript){
	if(jobScript.pSrcItemList != NULL){
		HA_SRC_ITEM*  base = jobScript.pSrcItemList;
		for(int i = 0; i< jobScript.ulSrcItemCnt; i++,base++){		
			Free_NULL(base->pwszPath);
			Free_NULL(base->pwszSFPassword);
			Free_NULL(base->pwszSFUsername);
			HA_ITEM_FILE* pItemFile = base->pD2DFileList;
			for (int index = 0; index < base->ulD2DFileCnt;index++,pItemFile++)
			{
				Free_NULL(pItemFile->pwszPath);
				Free_NULL(pItemFile->pwszDesRoot);
				Free_NULL(pItemFile->pwszVMDKPath);
			}
			delete[] base->pD2DFileList;

		}

		delete[] jobScript.pSrcItemList;
		jobScript.pSrcItemList = NULL;
	}
    Free_NULL(jobScript.pwszVmInstID);
	Free_NULL(jobScript.pwszCryptPassword);
	Free_NULL(jobScript.pwszDesFolder);
	Free_NULL(jobScript.pwszDesHostName);
	Free_NULL(jobScript.pwszDesPort);
	Free_NULL(jobScript.pwszJobID);
	Free_NULL(jobScript.pwszLocalPassword);
	Free_NULL(jobScript.pwszLocalUsername);
	Free_NULL(jobScript.pwszPassword);
	Free_NULL(jobScript.pwszProductNode);
	Free_NULL(jobScript.pwszUserName);
	Free_NULL(jobScript.pwszEsxHostName);
	Free_NULL(jobScript.pwszEsxUserName);
	Free_NULL(jobScript.pwszEsxPassword);
	Free_NULL(jobScript.pwszMoref);
    Free_NULL(jobScript.pwszSnapshotID);
    Free_NULL(jobScript.pwszLastDesRoot);
	
}