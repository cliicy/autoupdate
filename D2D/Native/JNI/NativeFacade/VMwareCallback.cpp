#include "StdAfx.h"
#include "HAClientProxy.h"
#include "utils.h"
#include "JNIConv.h"
#include "jni.h"
#include "VMwareCallback.h"

VMwareCallback::VMwareCallback(JNIEnv *env,wstring vmuuid)
{
	this->cls = env->FindClass("com/ca/arcflash/webservice/replication/BaseTransReplicationCommand");
	this->mid_HyperVUpdateRepJobMonitorProgress = env->GetStaticMethodID(cls, "hyperVUpdateRepJobMonitorProgress", "(Ljava/lang/String;Ljava/lang/String;JJ)V");
	this->mid_HyperVUpdateRepJobMonitorReportMsg = env->GetStaticMethodID(cls, "hyperVUpdateRepJobMonitorReportMsg", "(Ljava/lang/String;Ljava/lang/String;IJLjava/util/ArrayList;)V");
	this->vmuuid = vmuuid;
	env->GetJavaVM(&this->jvm);
}

VMwareCallback::~VMwareCallback(void)
{
}


long VMwareCallback::ReportJobProgress (const wchar_t* pwszJobID, 
                                        unsigned __int64 uliTotal, 
                                        unsigned __int64 uliTrans)
{

	JNIEnv *env = NULL;
	this->jvm->AttachCurrentThread((void**)&env,NULL);

	jlong total = uliTotal;
	jlong trans = uliTrans;
	jstring jobID = WCHARToJString(env,pwszJobID);
	jstring jstrvmuuid = WCHARToJString(env,this->vmuuid);

	env->CallStaticVoidMethod(this->cls, this->mid_HyperVUpdateRepJobMonitorProgress,jstrvmuuid, jobID,total, trans);

	this->jvm->DetachCurrentThread();
	return 0;

}

long VMwareCallback::ReportDiskProgress(const wchar_t* pwszJobID, unsigned long ulDiskSig,
        unsigned __int64 uliTotal, unsigned __int64 uliTrans)
{
	return 0;
}



/*
@ulJobID[in]    - The unique ID of job
@ulMsgID[in]    - the message ID in the resource
@ppParams[in]   - pointer to array of zero terminated string, those string is the parameter of the message 
corresponding to ulMsgID
@ulParamCnt[in] - the number of strings in the array pointed by ppParams
*/
long VMwareCallback::ReportMessage      (const wchar_t* pwszJobID, HADT_SEVERITY_LEVEL eLevel, unsigned long ulMsgID, 
											const wchar_t** ppParams, unsigned long ulParamCnt/*, void* pUserData*/)
{

	JNIEnv *env;
	this->jvm->AttachCurrentThread((void**)&env,NULL);

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

	env->CallStaticVoidMethod(this->cls, this->mid_HyperVUpdateRepJobMonitorReportMsg,jstrvmuuid, jobID,level,msgID, jArray);	

	this->jvm->DetachCurrentThread();

	return 0;
}

long VMwareCallback::ReportDiskSanModeStatus(const wchar_t* pwszJobID,unsigned long ulDiskSig, int nStatus)
{
	return 0;
}
