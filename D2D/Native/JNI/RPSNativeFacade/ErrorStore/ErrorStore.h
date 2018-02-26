#pragma  once

#define GETERRORMESSAGE_MOREINFO 100
#define GETERRORMESSAGE_NO_INFO 1


#ifdef __cplusplus
extern "C"
{

#endif // __cplusplus


	void InitErrorMessage(DWORD dwsize);
	void SetErrorMessage(DWORD Errcode,wchar_t*pMsg);

	//return GETERRORMESSAGE_NO_INFO means no error. GETERRORMESSAGE_MOREINFO means pBuf is too small, while pdwBufLen holding the requested length. o means ok
	DWORD GetErrorMessage(DWORD *pErrcode,void*pBuf, DWORD *bufsizeInbyte);


#ifdef __cplusplus
}

#endif // __cplusplus