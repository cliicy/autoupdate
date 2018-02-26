#include "stdafx.h"
#include <windows.h>
#include "GRTMntManager.h"
#include "D2DSwitch.h"
#include "com_ca_arcflash_webservice_jni_WSJNI.h"
#include "JNIConv.h"
#include "Log.h"
#include "iAppBrowse.h"

extern CDbgLog logObj;

DWORD AddAppChild2ListForAD(JNIEnv *env, jobject retList, PAGRT_ITEMS pvItem);

JNIEXPORT jlong JNICALL Java_com_ca_arcflash_webservice_jni_WSJNI_getGRTItem(JNIEnv *env, jclass jclz, 
																			 jstring jstrRootPath, jstring jstrUser, jstring jstrPSW,  
																			 jlong jlSessNo, jstring jstrEncrypPSW, jlong jlSubSessNo, 
																			 jlong appType, jlong dataType, jlong parentID, jobject retList)
{
	DWORD dwRet =0;

	wchar_t* pwszDest  = JStringToWCHAR(env, jstrRootPath);
	wchar_t* pwszUser  = JStringToWCHAR(env, jstrUser);
	wchar_t* pwszPwd   = JStringToWCHAR(env, jstrPSW);
	wchar_t* pwszEnPwd = JStringToWCHAR(env, jstrEncrypPSW);

	AGRT_PARENT Parent = {0};
	AGRT_ITEMS pvItem = {0};
	D2D_ENCRYPTION_INFO stEncry= {0};

	Parent.session.pEncryptInfo = & stEncry;
	do 
	{
		Parent.size = sizeof(AGRT_PARENT);
		Parent.AppType = APP_GRT_AD;
		Parent.DataType= (APP_DATA_TYPE)dataType;
		Parent.dwSubSession = (DWORD) jlSubSessNo;
		Parent.session.dwSessNo		= jlSessNo;
		Parent.session.lpRootFolder = pwszDest;
		Parent.session.lpUserName   = pwszUser;
		Parent.session.lpPassword   = pwszPwd;
		if(pwszEnPwd)
		{ 
			wcsncpy(Parent.session.pEncryptInfo->szSessionPassword, pwszEnPwd, AF_SESSPWD_MAXLEN_IN_CHARACTER);
		}

		Parent.parent.AD_Parent.dwDNT = (DWORD) parentID;

		dwRet = AppGetItems(&Parent, &pvItem);

		if(dwRet)
		{
			logObj.LogW(LL_WAR, 0, L"Failed to AppGetItems(%d)...", dwRet);
			break;
		}

		if(APP_GRT_AD ==Parent.AppType)
		{
			dwRet = AddAppChild2ListForAD(env, retList, &pvItem);
		}
		
	} while (0);

	if(pvItem.nItemNum)
	{
		AppRleaseItems(&pvItem);
	}

	if (pwszDest!=NULL)
	{ 
		free(pwszDest);
	}
	if (pwszUser != NULL)
	{ 
		free(pwszUser);
	}
	if (pwszPwd != NULL)
	{ 
		free(pwszPwd);
	}
	if (pwszEnPwd != NULL)
	{ 
		free(pwszEnPwd);
	}

	return (jlong) dwRet;
}
  
#define  cls_app_item				"com/ca/arcflash/webservice/jni/model/JGRTItem"
#define  FUN_AppItem_setId			"setId"
#define  SIG_AppItem_setId			"(J)V"

#define	FUN_JGRTITEM_CREATE			"<init>"
#define SIG_JGRTITEM_CREATE			"()V"

#define  FUN_AppItem_setGroup		"setGroup"
#define  SIG_AppItem_setGroup		"(J)V"

#define  FUN_AppItem_setFlags		"setFlags"
#define  SIG_AppItem_setFlags		"(J)V"

#define  FUN_AppItem_setName		"setName"
#define  SIG_AppItem_setName		"(Ljava/lang/String;)V"

#define  FUN_AppItem_setValue		"setValue"
#define  SIG_AppItem_setValue		"(Ljava/lang/String;)V"

DWORD AddAppChild2ListForAD(JNIEnv *env, jobject retList, PAGRT_ITEMS pvItem)
{
	DWORD dwRet =0;

	jclass clsList;
	jmethodID id_list_add;
	jclass clsAppItem;
	jmethodID id_create;

	do 
	{
		clsList =  env->GetObjectClass(retList);
		if(NULL ==clsList)
		{
			logObj.LogA(LL_WAR, 0, "Failed to env->GetObjectClass(retList)");
			dwRet =2;
			break;
		}

		id_list_add = env->GetMethodID(clsList, "add", "(Ljava/lang/Object;)Z");
		if(NULL == id_list_add)
		{
			logObj.LogA(LL_WAR, 0, "Failed to find MethodID of Add in AddAppChild2List");
			dwRet =2;
			break;;
		}

		clsAppItem = env->FindClass(cls_app_item);
		if(NULL==clsAppItem)
		{
			logObj.LogA(LL_WAR, 0, "Failed to find class %s", cls_app_item);
			dwRet =2;
			break;
		}

		id_create = env->GetMethodID(clsAppItem, FUN_JGRTITEM_CREATE,  SIG_JGRTITEM_CREATE);
		if(NULL == id_create)
		{
			logObj.LogA(LL_WAR, 0, "Failed to find MethodID of JGRTItem in AddAppChild2List");
			dwRet =2;
			break;;
		}

		for(int i=0 ; i <  (pvItem->nItemNum) ; i++)
		{
			jobject objItem = env->NewObject(clsAppItem, id_create);
			if(NULL ==objItem)
			{
				logObj.LogA(LL_INF, 0, "Failed to create instance of class %s", cls_app_item);
				dwRet = E_FAIL;
				break;
			}

			BOOL isOK = FALSE;
			do 
			{
				jmethodID funid;

				//Set DNT
				funid = env->GetMethodID(clsAppItem, FUN_AppItem_setId, SIG_AppItem_setId);
				if(NULL==funid)
				{
					logObj.LogA(LL_WAR, 0, "Failed to find MethodID(%s)", FUN_AppItem_setId);
					break;
				}
				env->CallVoidMethod(objItem, funid, (jlong)pvItem->pvItems[i].item.ADItem.dwDnt);

				//Set Name
				funid = env->GetMethodID(clsAppItem, FUN_AppItem_setName, SIG_AppItem_setName);
				if(NULL==funid)
				{
					logObj.LogA(LL_WAR, 0, "Failed to find MethodID(%s)", FUN_AppItem_setName);
					break;
				}

				if( (0 < pvItem->pvItems[i].item.ADItem.dwNameLen) && pvItem->pvItems[i].item.ADItem.pszName)
				{
					jstring jstrItem = WCHARToJString(env, pvItem->pvItems[i].item.ADItem.pszName);
					env->CallVoidMethod(objItem, funid,  jstrItem);
					if ( jstrItem!=NULL)
					{
						env->DeleteLocalRef(jstrItem);
					}
				}

				//Set Value
				funid = env->GetMethodID(clsAppItem, FUN_AppItem_setValue, SIG_AppItem_setValue);
				if(NULL==funid)
				{
					logObj.LogA(LL_WAR, 0, "Failed to find MethodID(%s)", FUN_AppItem_setValue);
					break;
				}

				if( (0 <pvItem->pvItems[i].item.ADItem.dwValueLen) && pvItem->pvItems[i].item.ADItem.pszValue)
				{
					jstring jstrItem = WCHARToJString(env, pvItem->pvItems[i].item.ADItem.pszValue);
					env->CallVoidMethod(objItem, funid,  jstrItem);
					if ( jstrItem!=NULL)
					{
						env->DeleteLocalRef(jstrItem);
					}
				} 

				// Set Node type( used for showing icon)
				funid = env->GetMethodID(clsAppItem, FUN_AppItem_setGroup, SIG_AppItem_setGroup);
				if(NULL==funid)
				{
					logObj.LogA(LL_WAR, 0, "Failed to find MethodID(%s)", FUN_AppItem_setGroup);
					break;
				}
				  
				env->CallVoidMethod(objItem, funid, (jlong)pvItem->pvItems[i].item.ADItem.dwItemType);
				 
				// Set Node Flags
				// FUN_AppItem_setFlags
				funid = env->GetMethodID(clsAppItem, FUN_AppItem_setFlags, SIG_AppItem_setFlags);
				if(NULL==funid)
				{
					logObj.LogA(LL_WAR, 0, "Failed to find MethodID(%s)", FUN_AppItem_setFlags);
					break;
				}
				  
				env->CallVoidMethod(objItem, funid, (jlong)pvItem->pvItems[i].item.ADItem.dwFlags);

				isOK = 1;

			} while (0);
			
			if(isOK)
			{
				env->CallBooleanMethod(retList, id_list_add, objItem);
			}
			
			if (objItem != NULL) 
			{
				env->DeleteLocalRef(objItem);
			}
		}

	} while (0);

	if (clsAppItem != NULL)
	{
		env->DeleteLocalRef(clsAppItem);
	}

	return dwRet;
}