#include "stdafx.h"
#include "utils.h"
#include "HAClientProxy.h"
#include <jni.h>

#define TRY __try{
#define CATCH(procName) }__except(HandleSEH(L"HATransClientProxy.dll", procName, GetExceptionInformation(), GetExceptionCode()),EXCEPTION_EXECUTE_HANDLER){}
#define GET_PROC_ADDRESS(procName) DynGetProcAddress(L"HATransClientProxy.dll", procName)

DWORD WINAPI 
 Native_HADT_GetLastRepSessInfo(const ST_HASRV_INFO* pstSrvInfo, 
                            const wchar_t* pwszSessRoot,
							wchar_t** ppwszInfo){

	TRY
	Proc_HADT_GetLastRepSessInfo pfun = (Proc_HADT_GetLastRepSessInfo)GET_PROC_ADDRESS("HADT_GetLastRepSessInfo");

	return pfun( pstSrvInfo, pwszSessRoot,ppwszInfo);
	CATCH("HADT_GetLastRepSessInfo")
}
VOID WINAPI 
Native_HADT_FreeBuffer(void* p){
	TRY
	Proc_HADT_FreeBuffer pfun = (Proc_HADT_FreeBuffer)GET_PROC_ADDRESS("HADT_FreeBuffer");

	return pfun( p);
	CATCH("HADT_FreeBuffer")
}