#include <Windows.h>
#include "ErrorStore.h"

typedef struct _Error_Message
{
	WORD bUsed;//zero memory allocate
	WORD reserved;
	DWORD ErrorCode;
	DWORD MessageLength;//in byte
	DWORD dwMessageBufferSize;
	byte Message[1];

}Error_Message;

__declspec( thread )  Error_Message *l_err= 0;


void InitErrorMessage(DWORD dwsize)
{
	l_err = (Error_Message*)malloc(sizeof(Error_Message) + dwsize);
	memset(l_err,0,sizeof(Error_Message) + dwsize);
	l_err->dwMessageBufferSize = dwsize + 1;
	return;
}

void SetErrorMessage(DWORD Errcode,wchar_t*pMsg)
{
	l_err->bUsed = TRUE;
	l_err->ErrorCode = Errcode;
	l_err->MessageLength = (1 + (DWORD)wcslen(pMsg))*(DWORD)sizeof(wchar_t);
	memcpy_s(&l_err->Message, l_err->dwMessageBufferSize, pMsg,l_err->MessageLength);
	return;
}


DWORD GetErrorMessage(DWORD *pErrcode,void*pBuf, DWORD *pdwBufLen)
{
	DWORD ret = 0;
	DWORD flagCouldFree = 1;
	if (l_err->bUsed)
	{
		ret = 0;
		*pErrcode = l_err->ErrorCode;
		if (*pdwBufLen>=l_err->MessageLength)
		{
			memcpy(pBuf,&l_err->Message,l_err->MessageLength);
		}else
		{
			ret = GETERRORMESSAGE_MOREINFO;
			flagCouldFree = 0;
		}
		*pdwBufLen=l_err->MessageLength;
	}else
	{
		ret = GETERRORMESSAGE_NO_INFO;
	}
	if (flagCouldFree ==1)
	{
		free(l_err);
		l_err = 0;
	}
	return ret;
}