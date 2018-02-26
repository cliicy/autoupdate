#pragma  once

#include "ErrorStore.h"

#define ERRORSTORE_INIT	\
	DWORD ERRORSTOR_code = 0,ERRORSTOR_MsgLength=1024;TCHAR ERRORSTOR_Msg[1024]={0};InitErrorMessage(1600);

#define ERRORSTORE_CHECKERROR(env,clz,ret) \
	if(ret!=0) \
	{\
		if(GetErrorMessage(&ERRORSTOR_code,ERRORSTOR_Msg,&ERRORSTOR_MsgLength)==0)\
		{\
			ThrowWSJNIExceptionEx(env, clz, WCHARToJString(env,ERRORSTOR_Msg), ERRORSTOR_code);\
		}else\
		{\
			ThrowWSJNIException(env, clz, env->NewStringUTF("DataStore Manager Error"), (jint)lRet);\
		}\
	} 