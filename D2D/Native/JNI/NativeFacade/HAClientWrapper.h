#pragma once
#include "HAClientProxy.h"
DWORD WINAPI 
 Native_HADT_GetLastRepSessInfo(const ST_HASRV_INFO* pstSrvInfo, 
                            const wchar_t* pwszSessRoot,
							wchar_t** ppwszInfo);
VOID WINAPI 
 Native_HADT_FreeBuffer(void* p);