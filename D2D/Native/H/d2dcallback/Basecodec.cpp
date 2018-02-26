#include "Basecodec.h"
 
#include <tchar.h>
#include <windows.h>
#include <atlbase.h>
#include <atlstr.h>

#include <algorithm>



#define  iCHK_RET(x, y)  if(FALSE==x) return y
BOOL AtomCodec::d2stream (BYTE * stream, const DWORD dwbufLen, DWORD & dwIndex, WORD wdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+2) )
	{
		return FALSE;
	}


	d2 w2d = {0};
	w2d.w2 = wdata;

	for(int i=0; i < 2; i++)
	{
		stream[dwIndex] = w2d.data[i];
		dwIndex++;
	}

	return TRUE;
}

BOOL AtomCodec::stream2d2(const BYTE * stream ,const DWORD dwbufLen, DWORD & dwIndex, WORD & wdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+2) )
	{
		return FALSE;
	}

	d2 d2w = {0};

	for(int i=0; i < 2; i++)
	{
		d2w.data[i]=stream[dwIndex]; 
		dwIndex++;
	}

	wdata =d2w.w2;

	return TRUE;
}




BOOL AtomCodec::d4stream(BYTE * stream, const DWORD dwbufLen, DWORD & dwIndex, DWORD dwdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+4) )
	{
		return FALSE;
	}


	d4 w4d = {0};
	w4d.w4 = dwdata;

	for(int i=0; i < 4; i++)
	{
		stream[dwIndex] = w4d.data[i];
		dwIndex++;
	}

	return TRUE;
}

BOOL AtomCodec::stream2d4(const BYTE * stream ,const DWORD dwbufLen, DWORD & dwIndex, DWORD32 & dwdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+4) )
	{
		return FALSE;
	}

	d4 d2w4 = {0};

	for(int i=0; i < 4; i++)
	{
		d2w4.data[i]=stream[dwIndex]; 
		dwIndex++;
	}

	dwdata =d2w4.w4;

	return TRUE;
}



BOOL AtomCodec::l4stream(BYTE * stream, const DWORD dwbufLen, DWORD & dwIndex, long data)
{
	if(NULL==stream || dwbufLen < (dwIndex+4) )
	{
		return FALSE;
	}


	d4 w4d = {0};
	w4d.l4 = data;

	for(int i=0; i < 4; i++)
	{
		stream[dwIndex] = w4d.data[i];
		dwIndex++;
	}

	return TRUE;
}

BOOL AtomCodec::stream2l4(const BYTE * stream ,const DWORD dwbufLen, DWORD & dwIndex, long & data)
{
	if(NULL==stream || dwbufLen < (dwIndex+4) )
	{
		return FALSE;
	}

	d4 d2w4 = {0};

	for(int i=0; i < 4; i++)
	{
		d2w4.data[i]=stream[dwIndex]; 
		dwIndex++;
	}

	data =d2w4.l4;

	return TRUE;
}


BOOL AtomCodec::f4stream(BYTE * stream, const DWORD dwbufLen, DWORD & dwIndex, float dwdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+4) )
	{
		return FALSE;
	}

	d4 w4d = {0};
	w4d.f4 = dwdata;

	for(int i=0; i < 4; i++)
	{
		stream[dwIndex] = w4d.data[i];
		dwIndex++;
	}

	return TRUE;
}

BOOL AtomCodec::stream2f4(const BYTE * stream ,const DWORD dwbufLen, DWORD & dwIndex, float & dwdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+4) )
	{
		return FALSE;
	}

	d4 d2w4 = {0};

	for(int i=0; i < 4; i++)
	{
		d2w4.data[i]=stream[dwIndex]; 
		dwIndex++;
	}

	dwdata =d2w4.f4;

	return TRUE;
}


BOOL AtomCodec::d8stream(BYTE * stream, const DWORD dwbufLen, DWORD & dwIndex, DWORD64 dwdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+8) )
	{
		return FALSE;
	}


	d8 w8d = {0};
	w8d.w8 = dwdata;

	for(int i=0; i < 8; i++)
	{
		stream[dwIndex] = w8d.data[i];
		dwIndex++;
	}

	return TRUE;
}

BOOL AtomCodec::stream2d8(const BYTE * stream ,const DWORD dwbufLen, DWORD & dwIndex, DWORD64 & dwdata)
{
	if(NULL==stream || dwbufLen < (dwIndex+8) )
	{
		return FALSE;
	}

	d8 d2w8 = {0};

	for(int i=0; i < 8; i++)
	{
		d2w8.data[i]=stream[dwIndex]; 
		dwIndex++;
	}

	dwdata =d2w8.w8;

	return TRUE;
}

BOOL AtomCodec::wstr2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, const WCHAR * pszData)
{
	if(NULL==stream || streamLen < (dwIndex+2) )
	{
		return FALSE;
	}

	if(NULL==pszData )
	{
		stream[dwIndex++] =0;
		stream[dwIndex++] =0;
	}
	else
	{
		WORD nlen = lstrlenW((WCHAR *)pszData);
		if(streamLen <( dwIndex+nlen*sizeof(WCHAR)+2 ) )
		{
			stream[dwIndex++] =0;
			stream[dwIndex++] =0;
		}
		else
		{
			d2 unsize;
			unsize.w2 =nlen;
			stream[dwIndex++] =unsize.data[0];
			stream[dwIndex++] =unsize.data[1];
			memcpy(stream+dwIndex, (BYTE*) pszData,nlen* sizeof(WCHAR) );
			dwIndex += nlen* sizeof(WCHAR  );
		}
	}

	return TRUE;
}

BOOL AtomCodec::stream2wstr(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, WCHAR ** pszData, WORD & strsize)
{
	if(NULL==stream || streamLen <(dwIndex+2) )
	{
		return FALSE;
	}

	// get the string length
	WORD nLen =0;
	d2 unsize;
	strsize = 0;

	unsize.data[0] =stream[dwIndex++];
	unsize.data[1] =stream[dwIndex++];
	nLen = unsize.w2;

	if(0 == nLen)
	{
		*pszData = NULL;
	}
	else
	{
		WCHAR * pTemp = new WCHAR[nLen+1];
		if(NULL==pTemp) 
		{
			return FALSE;
		}

		memset(pTemp, 0, (nLen+1) *sizeof(WCHAR) );
		memcpy(pTemp, stream+dwIndex, (nLen) *sizeof(WCHAR)  );
		dwIndex+= (nLen) *sizeof(WCHAR);

		*pszData = pTemp;
		strsize = (nLen) *sizeof(WCHAR);
	}

	return	TRUE;
}

BOOL AtomCodec::binary2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, const BYTE * pbzData, WORD dwsizeOfdata)
{
	if(NULL==stream ||streamLen < (dwIndex+ dwsizeOfdata+2) )
	{
		return FALSE;
	}

	if(NULL==pbzData||0 ==dwsizeOfdata)
	{
		stream[dwIndex++] =0;
		stream[dwIndex++] =0;
	}
	else
	{
		d2 unsize;
		unsize.w2 =dwsizeOfdata;
		stream[dwIndex++] =unsize.data[0];
		stream[dwIndex++] =unsize.data[1];
		memcpy(stream+dwIndex, pbzData,  dwsizeOfdata );
		dwIndex += dwsizeOfdata;
	}

	return TRUE;

}

BOOL AtomCodec::stream2binary(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, BYTE ** ppbzData, WORD & dwsizeOfdata)
{
	if(NULL==stream || streamLen < (dwIndex+ 2)||NULL ==ppbzData )
	{
		return FALSE;
	}

	d2 unsize;
	dwsizeOfdata = 0;
	unsize.data[0] =stream[dwIndex++];
	unsize.data[1] =stream[dwIndex++];

	dwsizeOfdata = unsize.w2;


	if(0 == dwsizeOfdata)
	{
		*ppbzData = NULL;
	}
	else
	{

		BYTE * pTemp = new BYTE[dwsizeOfdata+2];
		if(NULL==pTemp) 
		{
			return FALSE;
		}

		memset(pTemp, 0, (dwsizeOfdata+2));
		memcpy(pTemp, stream+dwIndex, dwsizeOfdata);
		dwIndex += dwsizeOfdata;

		*ppbzData = pTemp;
		dwsizeOfdata =dwsizeOfdata;

	}

	return TRUE;
}

BOOL AtomCodec::header2stream(BYTE * stream, const DWORD streamLen, DWORD & dwIndex, PD2DIPC_MSG_HEADER pHeader)
{
	DWORD HeadSize = sizeof(D2DIPC_MSG_HEADER);

	if(NULL ==pHeader ||NULL==stream || streamLen < (dwIndex+  HeadSize)) 
	{
		return FALSE;
	}

	memcpy(stream, pHeader, HeadSize );

	dwIndex+= HeadSize;

	return TRUE;
}

BOOL AtomCodec::stream2header(const BYTE * stream, const DWORD streamLen, DWORD & dwIndex, PD2DIPC_MSG_HEADER pHeader)
{
	DWORD HeadSize = sizeof(D2DIPC_MSG_HEADER);

	if(NULL ==pHeader ||NULL==stream || streamLen < (dwIndex+  HeadSize)) 
	{
		return FALSE;
	}

	memcpy(pHeader,stream, HeadSize );

	dwIndex+= HeadSize;

	return TRUE;
}
//////////////////////////////////////////////////////////////////////////

DWORD CMDcodec::PackHeader(DWORD CMDid, BYTE * & pStream, DWORD & dwLenStream)
{
	D2DIPC_MSG_HEADER header = {0};
	header.version  =IPC_CALLBACK_VER_1;
	header.cmdid     = CMDid;
	header.oricmdid  = 0;
	header.signature = D2D_PXY_CMD_SIGNATURE;

	DWORD dwTempSize = 0;
	dwTempSize  = sizeof(D2DIPC_MSG_HEADER);
	dwTempSize += 128;	// pad

	BYTE * pTempbuf =NULL;
	pTempbuf = new BYTE[dwTempSize];
	if(NULL ==pTempbuf)
	{
		return E_OUTOFMEMORY;
	}
	memset(pTempbuf, 0, dwTempSize);

	DWORD dwIndex =0;
	DWORD dwRet =0;
	//Header
	iCHK_RET(AtomCodec::header2stream(pTempbuf, dwTempSize, dwIndex, &header),E_INVALIDARG);

	if(0==dwRet)
	{
		pStream = pTempbuf;
		dwLenStream = dwIndex;

	}
	else
	{
		delete pTempbuf;
		pTempbuf = NULL;
	} 

	return dwRet;
}

 
DWORD CMDcodec::UnpackHeader(BYTE * pStream, DWORD dwLenStream ,PD2DIPC_MSG_HEADER pHeader)
{
	if(NULL ==pStream || NULL==pHeader )
	{
		return E_INVALIDARG;
	}

	HRESULT hr = S_OK;
	DWORD dwIndex =0;
	// header
	iCHK_RET(AtomCodec::stream2header(pStream, dwLenStream, dwIndex, pHeader), E_INVALIDARG);

	return hr;
}

DWORD CMDcodec::UnpackResponse(BYTE * pStream, DWORD dwLenStream ,PD2DIPC_MSG_HEADER pHeader)
{
	return UnpackHeader(pStream, dwLenStream ,pHeader);
}

DWORD CMDcodec::PackResponse(DWORD dwRetVal, DWORD CMDid,DWORD oriCMDid, BYTE * & pStream, DWORD & dwLenStream)
{
	D2DIPC_MSG_HEADER header = {0};
	header.version = IPC_CALLBACK_VER_1;
	header.cmdid   = CMDid;
	header.oricmdid  = oriCMDid;
	header.signature = D2D_PXY_CMD_RES_SIGNATURE;
	header.retval =dwRetVal;

	DWORD dwTempSize = 0;
	dwTempSize  = sizeof(D2DIPC_MSG_HEADER);
	dwTempSize += 128;	// pad

	BYTE * pTempbuf =NULL;
	pTempbuf = new BYTE[dwTempSize];
	if(NULL ==pTempbuf)
	{
		return E_OUTOFMEMORY;
	}

	DWORD dwIndex =0;
	DWORD dwRet =0;
	//Header
	iCHK_RET(AtomCodec::header2stream(pTempbuf, dwTempSize, dwIndex, &header),E_INVALIDARG);

	if(0==dwRet)
	{
		pStream = pTempbuf;
		dwLenStream = dwIndex;

	}
	else
	{
		delete pTempbuf;
		pTempbuf = NULL;
	} 

	return dwRet;

}

DWORD CMDcodec::PackResponse(PD2DIPC_MSG_HEADER pHeader , BYTE * & pStream, DWORD & dwLenStream)
{
	DWORD dwTempSize = 0;
	dwTempSize  = sizeof(D2DIPC_MSG_HEADER);
	dwTempSize += 128;	// pad

	BYTE * pTempbuf =NULL;
	pTempbuf = new BYTE[dwTempSize];
	if(NULL ==pTempbuf)
	{
		return E_OUTOFMEMORY;
	}

	DWORD dwIndex =0;
	DWORD dwRet =0;
	//Header
	iCHK_RET(AtomCodec::header2stream(pTempbuf, dwTempSize, dwIndex, pHeader),E_INVALIDARG);

	if(0==dwRet)
	{
		pStream = pTempbuf;
		dwLenStream = dwIndex;

	}
	else
	{
		delete pTempbuf;
		pTempbuf = NULL;
	} 

	return dwRet;
}

DWORD CMDcodec::PackMerge(PD2DIPC_MERGE_INFO pstInfo, BYTE * & pStream, DWORD & dwLenStream)
{
	if(NULL==pstInfo)
	{
		return E_INVALIDARG;
	}

	DWORD dwTempSize = 0;
	dwTempSize += sizeof(D2DIPC_MSG_HEADER);
	dwTempSize += sizeof(D2DIPC_MERGE_INFO);
	dwTempSize += 128;	// pad

	D2DIPC_MSG_HEADER header = {0};
	header.version  =IPC_CALLBACK_VER_1;
	header.cmdid     = CMD_SEND_MERGE_MALI;
	header.oricmdid  = 0;
	header.signature = D2D_PXY_CMD_SIGNATURE;

	BYTE * pTempbuf =NULL;
	pTempbuf = new BYTE[dwTempSize];
	if(NULL ==pTempbuf)
	{
		return E_OUTOFMEMORY;
	}

	DWORD dwIndex =0;
	DWORD dwRet =0;

	do 
	{
		//Header
		iCHK_RET(AtomCodec::header2stream(pTempbuf, dwTempSize, dwIndex, &header), E_INVALIDARG);
		//Merge information
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwRangeStart ),	E_INVALIDARG );
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwRangeEnd ),	E_INVALIDARG );
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwFailedStart ), E_INVALIDARG );
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwFailedEnd ),	E_INVALIDARG );
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwJobID ),		E_INVALIDARG );
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwRetCode ),		E_INVALIDARG );
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwSource ),		E_INVALIDARG );
		iCHK_RET(AtomCodec::wstr2stream(pTempbuf,dwTempSize,dwIndex, pstInfo->VMInstance),  E_INVALIDARG );

	} while (0);

	if(0==dwRet)
	{
		pStream = pTempbuf;
		dwLenStream = dwIndex;

	}
	else
	{
		delete pTempbuf;
		pTempbuf = NULL;
	}
 
	return dwRet;
 
}

DWORD CMDcodec::UnpackMerge(BYTE * pStream, DWORD dwLenStream,PD2DIPC_MERGE_INFO pMergeinfo)
{
	if(NULL==pStream ||0==dwLenStream || NULL==pMergeinfo)
	{
		return E_INVALIDARG;
	}

	D2DIPC_MSG_HEADER header = {0};
	HRESULT hr = S_OK;
	DWORD dwIndex =0;

	do 
	{
		// header
		iCHK_RET(AtomCodec::stream2header(pStream, dwLenStream, dwIndex, &header), E_INVALIDARG);
		// Merge information
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pMergeinfo->dwRangeStart),	E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pMergeinfo->dwRangeEnd),	E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pMergeinfo->dwFailedStart), E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pMergeinfo->dwFailedEnd),	E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pMergeinfo->dwJobID),		E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pMergeinfo->dwRetCode),		E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pMergeinfo->dwSource),		E_INVALIDARG);

		WCHAR * pszbuf = NULL;
		WORD dwLen =0;
		iCHK_RET(AtomCodec::stream2wstr(pStream,dwLenStream, dwIndex, &pszbuf, dwLen),	E_INVALIDARG);
		if(pszbuf && dwLen>0)
		{
			wcsncpy_s(pMergeinfo->VMInstance, _countof(pMergeinfo->VMInstance), pszbuf,min(dwLen, 128) );
		}

		hr = S_OK;
	} while (0);

	return hr;
}
DWORD CMDcodec::PackVMINFO(PD2DIPC_VMBACKUP_INFO pstInfo, BYTE * & pStream, DWORD & dwLenStream)
{
	if(NULL==pstInfo)
	{
		return E_INVALIDARG;
	}

	DWORD dwTempSize = 0;
	dwTempSize += sizeof(D2DIPC_MSG_HEADER);
	dwTempSize += sizeof(D2DIPC_VMBACKUP_INFO);
	dwTempSize += 128;	// pad

	D2DIPC_MSG_HEADER header = {0};
	header.version  =IPC_CALLBACK_VER_1;
	header.cmdid     = CMD_BACKUP_VM_FAILED;
	header.oricmdid  = 0;
	header.signature = D2D_PXY_CMD_SIGNATURE;

	BYTE * pTempbuf =NULL;
	pTempbuf = new BYTE[dwTempSize];
	if(NULL ==pTempbuf)
	{
		return E_OUTOFMEMORY;
	}

	DWORD dwIndex =0;
	DWORD dwRet =0;

	do 
	{
		//Header
		iCHK_RET(AtomCodec::header2stream(pTempbuf, dwTempSize, dwIndex, &header), E_INVALIDARG);
		//Merge information
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwJobID ),	E_INVALIDARG );
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex,pstInfo->dwRetCode ),	E_INVALIDARG );

	} while (0);

	if(0==dwRet)
	{
		pStream = pTempbuf;
		dwLenStream = dwIndex;

	}
	else
	{
		delete pTempbuf;
		pTempbuf = NULL;
	}

	return dwRet;
}

DWORD CMDcodec::UnpackVMINFO(BYTE * pStream, DWORD dwLenStream,PD2DIPC_VMBACKUP_INFO pVMinfo)
{
	if(NULL==pStream ||0==dwLenStream || NULL==pVMinfo)
	{
		return E_INVALIDARG;
	}

	D2DIPC_MSG_HEADER header = {0};
	HRESULT hr = S_OK;
	DWORD dwIndex =0;

	do 
	{
		// header
		iCHK_RET(AtomCodec::stream2header(pStream, dwLenStream, dwIndex, &header), E_INVALIDARG);
		// Merge information
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pVMinfo->dwJobID), E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pVMinfo->dwRetCode), E_INVALIDARG);
		hr = S_OK;
	} while (0);

	return hr;
}


DWORD CMDcodec::PackLicList(PD2DIPC_MSG_HEADER pHeader, PD2D_CHKLIC_LISTENTRY pListEntry, BYTE * & pStream, DWORD & dwLenStream)
{
	if(NULL==pHeader || NULL ==pListEntry ||0==pListEntry->listsize)
	{
		return E_INVALIDARG;
	}

	DWORD dwTempSize = 0;
	dwTempSize  = sizeof(D2DIPC_MSG_HEADER);
	dwTempSize += sizeof(D2D_CHKLIC_ITEM) * pListEntry->listsize;
	dwTempSize += 128;	// pad
	pHeader->version = IPC_CALLBACK_VER_1;
	
	BYTE * pTempbuf =NULL;
	pTempbuf = new BYTE[dwTempSize];
	if(NULL ==pTempbuf)
	{
		return E_OUTOFMEMORY;
	}

	DWORD dwIndex =0;
	DWORD dwRet =0;

	do 
	{
		//Header
		iCHK_RET(AtomCodec::header2stream(pTempbuf, dwTempSize, dwIndex, pHeader), E_INVALIDARG);
		//list entry
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->size),		E_INVALIDARG);
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->version),	E_INVALIDARG);
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->processId),  E_INVALIDARG);

		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->Mask),		E_INVALIDARG);
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->SocketNum),  E_INVALIDARG);
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->LicID),		E_INVALIDARG);

		iCHK_RET(AtomCodec::d8stream(pTempbuf,dwTempSize, dwIndex, pListEntry->flags),		E_INVALIDARG);
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->JobID),		E_INVALIDARG);
		iCHK_RET(AtomCodec::d4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->listsize),	E_INVALIDARG);

		if(TRUE==IsBadReadPtr(pListEntry->pvItemList, sizeof( sizeof(D2D_CHKLIC_ITEM) * pListEntry->listsize ) ) )
		{
			dwRet = E_INVALIDARG;
			break;
		}
		for(DWORD i=0; i < pListEntry->listsize; i++)
		{
			iCHK_RET(AtomCodec::l4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->pvItemList[i].CompntID),	 E_INVALIDARG);
			iCHK_RET(AtomCodec::l4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->pvItemList[i].isRevered), E_INVALIDARG);
			iCHK_RET(AtomCodec::l4stream(pTempbuf,dwTempSize, dwIndex, pListEntry->pvItemList[i].retVal),	 E_INVALIDARG);
		}
		
	} while(0);

	if(0==dwRet)
	{
		pStream = pTempbuf;
		dwLenStream = dwIndex;

	}
	else
	{
		delete pTempbuf;
		pTempbuf = NULL;
	}
	 
	return dwRet;
}

DWORD CMDcodec::UnpackLicList(BYTE * pStream, DWORD dwLenStream ,PD2DIPC_MSG_HEADER pHeader,  PD2D_CHKLIC_LISTENTRY pListEntry)
{
	if(NULL ==pStream || NULL==pHeader || NULL==pListEntry )
	{
		return E_INVALIDARG;
	}

	HRESULT hr = S_OK;
	DWORD dwIndex =0;
	 
	do 
	{
		// header
		iCHK_RET(AtomCodec::stream2header(pStream, dwLenStream, dwIndex, pHeader), E_INVALIDARG);
		//list entry
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->size),		E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->version),	E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->processId),	E_INVALIDARG);

		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->Mask),		E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->SocketNum),	E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->LicID),	E_INVALIDARG);

		iCHK_RET(AtomCodec::stream2d8(pStream,dwLenStream, dwIndex, pListEntry->flags),		E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->JobID),		E_INVALIDARG);
		iCHK_RET(AtomCodec::stream2d4(pStream,dwLenStream, dwIndex, pListEntry->listsize ), E_INVALIDARG);

		if(0 < pListEntry->listsize)
		{
			pListEntry->pvItemList = new D2D_CHKLIC_ITEM[pListEntry->listsize];

			for(DWORD i=0;i < pListEntry->listsize; i ++)
			{ 
				long lRet =0;
				iCHK_RET(AtomCodec::stream2l4(pStream,dwLenStream, dwIndex, pListEntry->pvItemList[i].CompntID),  E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2l4(pStream,dwLenStream, dwIndex, lRet), E_INVALIDARG);
				pListEntry->pvItemList[i].isRevered = lRet;
				iCHK_RET(AtomCodec::stream2l4(pStream,dwLenStream, dwIndex, pListEntry->pvItemList[i].retVal), E_INVALIDARG);
 
			}
		}

		hr =0;

	} while(0);


	return hr;
}

DWORD CMDcodec::ReleaseLicList(PD2D_CHKLIC_LISTENTRY pListEntry)
{
	if(pListEntry)
	{
		if(pListEntry->pvItemList)
		{
			delete pListEntry->pvItemList;
			pListEntry->pvItemList = NULL;
		}
	}

	return 0;
}

//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm
DWORD CMDcodec::Pack_D2D_CPP_VM_JOB_CONTEXT_LIST(/*PD2DIPC_MSG_HEADER pHeader, */PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry, BYTE * & pStream, DWORD & dwLenStream)
{
	if (/*NULL == pHeader ||*/ NULL == pListEntry || 0 == pListEntry->listsize)
	{
		return E_INVALIDARG;
	}

	DWORD dwTempSize = 0;
	dwTempSize = sizeof(D2DIPC_MSG_HEADER);
	dwTempSize += sizeof(pListEntry->listsize);
	dwTempSize += sizeof(D2D_CPP_VM_JOB_CONTEXT_ITEM) * pListEntry->listsize;
	dwTempSize += 128;	// pad

	D2DIPC_MSG_HEADER header = { 0 };
	header.version = IPC_CALLBACK_VER_1;
	header.cmdid = CMD_CHILD_VM_JOB_CONTEXT;
	header.oricmdid = 0;
	header.signature = D2D_PXY_CMD_SIGNATURE;

	BYTE * pTempbuf = NULL;
	pTempbuf = new BYTE[dwTempSize];
	if (NULL == pTempbuf)
	{
		return E_OUTOFMEMORY;
	}

	DWORD dwIndex = 0;
	DWORD dwRet = 0;

	do
	{
		//Header
		iCHK_RET(AtomCodec::header2stream(pTempbuf, dwTempSize, dwIndex, &header), E_INVALIDARG);
		//list entry
		iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->listsize), E_INVALIDARG);
		if (TRUE == IsBadReadPtr(pListEntry->items, sizeof(sizeof(D2D_CPP_VM_JOB_CONTEXT_ITEM) * pListEntry->listsize)))
		{
			dwRet = E_INVALIDARG;
			break;
		}

		for (DWORD i = 0; i < pListEntry->listsize; i++)
		{
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwJobId), E_INVALIDARG);
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwQueueType), E_INVALIDARG);
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwJobType), E_INVALIDARG);
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwProcessId), E_INVALIDARG);
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwJMShmId), E_INVALIDARG);
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwLauncher), E_INVALIDARG);
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwPriority), E_INVALIDARG);
			iCHK_RET(AtomCodec::d4stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].dwMasterJobId), E_INVALIDARG);
			iCHK_RET(AtomCodec::wstr2stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].executerInstanceUUID), E_INVALIDARG);
			iCHK_RET(AtomCodec::wstr2stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].launcherInstanceUUID), E_INVALIDARG);
			iCHK_RET(AtomCodec::wstr2stream(pTempbuf, dwTempSize, dwIndex, pListEntry->items[i].generatedDestination), E_INVALIDARG);
		}

	} while (0);


	if (0 == dwRet)
	{
		pStream = pTempbuf;
		dwLenStream = dwIndex;

	}
	else
	{
		delete pTempbuf;
		pTempbuf = NULL;
	}
}

DWORD CMDcodec::Unpack_D2D_CPP_VM_JOB_CONTEXT_LIST( /*PD2DIPC_MSG_HEADER pHeader, */PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry, BYTE * & pStream, DWORD & dwLenStream)
{
	if (NULL == pStream /*|| NULL == pHeader*/ || NULL == pListEntry)
	{
		return E_INVALIDARG;
	}

	HRESULT hr = S_OK;
	DWORD dwIndex = 0;

	do
	{
		// header
		D2DIPC_MSG_HEADER Header = { 0 };
		iCHK_RET(AtomCodec::stream2header(pStream, dwLenStream, dwIndex, &Header), E_INVALIDARG);
		//list entry
		iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->listsize), E_INVALIDARG);

		if (0 < pListEntry->listsize)
		{
			pListEntry->items = new D2D_CPP_VM_JOB_CONTEXT_ITEM[pListEntry->listsize];

			for (DWORD i = 0; i < pListEntry->listsize; i++)
			{
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwJobId), E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwQueueType), E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwJobType), E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwProcessId), E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwJMShmId), E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwLauncher), E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwPriority), E_INVALIDARG);
				iCHK_RET(AtomCodec::stream2d4(pStream, dwLenStream, dwIndex, pListEntry->items[i].dwMasterJobId), E_INVALIDARG);
				
				{
					WCHAR * pszTemp = NULL;
					WORD sizeTemp = 0;
					iCHK_RET(AtomCodec::stream2wstr(pStream, dwLenStream, dwIndex, &pszTemp, sizeTemp), E_INVALIDARG);
					if (pszTemp && sizeTemp)
					{
						wcsncpy_s(pListEntry->items[i].executerInstanceUUID, _countof(pListEntry->items[i].executerInstanceUUID), pszTemp, min(sizeTemp / sizeof(WCHAR), _countof(pListEntry->items[i].executerInstanceUUID)));
						delete[] pszTemp; pszTemp = NULL;
					}
				}

				{
					WCHAR * pszTemp = NULL;
					WORD sizeTemp = 0;
					iCHK_RET(AtomCodec::stream2wstr(pStream, dwLenStream, dwIndex, &pszTemp, sizeTemp), E_INVALIDARG);
					if (pszTemp && sizeTemp)
					{
						wcsncpy_s(pListEntry->items[i].launcherInstanceUUID, _countof(pListEntry->items[i].launcherInstanceUUID), pszTemp, min(sizeTemp / sizeof(WCHAR), _countof(pListEntry->items[i].launcherInstanceUUID)));
						delete[] pszTemp; pszTemp = NULL;
					}
				}


				{
					WCHAR * pszTemp = NULL;
					WORD sizeTemp = 0;
					iCHK_RET(AtomCodec::stream2wstr(pStream, dwLenStream, dwIndex, &pszTemp, sizeTemp), E_INVALIDARG);
					if (pszTemp && sizeTemp)
					{
						wcsncpy_s(pListEntry->items[i].generatedDestination, _countof(pListEntry->items[i].generatedDestination), pszTemp, min(sizeTemp / sizeof(WCHAR), _countof(pListEntry->items[i].generatedDestination)));
						delete[] pszTemp; pszTemp = NULL;
					}
				}
			}
		}

		hr = 0;

	} while (0);


	return hr;
}

DWORD CMDcodec::Release_D2D_CPP_VM_JOB_CONTEXT_LIST(PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry)
{
	if (pListEntry)
	{
		if (pListEntry->items)
		{
			delete[] pListEntry->items;
			pListEntry->items = NULL;
		}
	}

	return 0;
}

DWORD CMDcodec::test_D2D_CPP_VM_JOB_CONTEXT_LIST()
{
	D2D_CPP_VM_JOB_CONTEXT_LIST var1;
	var1.listsize = 128;
	var1.items = new D2D_CPP_VM_JOB_CONTEXT_ITEM[var1.listsize];
	for (DWORD32 ii = 0; ii < var1.listsize; ++ ii)
	{
		D2D_CPP_VM_JOB_CONTEXT_ITEM & item = var1.items[ii];
		item.dwJobId = ii;//The job id = ii;
		item.dwQueueType = ii;//Job queue type  = ii;
		item.dwJobType = ii;//the job type = ii;
		item.dwProcessId = ii;
		item.dwJMShmId = ii;//same as job id = ii;
		item.dwLauncher = ii; 
		item.dwPriority = ii;//the job priority = ii;
		item.dwMasterJobId = ii; // specific fo = ii;
		std::fill_n(item.executerInstanceUUID, _countof(item.executerInstanceUUID) - 1, L'a' + ii);
		std::fill_n(item.launcherInstanceUUID, _countof(item.launcherInstanceUUID) - 1, L'a' + ii);
		std::fill_n(item.generatedDestination, _countof(item.generatedDestination) - 1, L'a' + ii);
	}

	//////////////////////////////////////////////////////////////////////////
	BYTE * pStream = NULL;
	DWORD dwLenStream = 0;
	Pack_D2D_CPP_VM_JOB_CONTEXT_LIST(&var1, pStream, dwLenStream);

	//////////////////////////////////////////////////////////////////////////
	D2D_CPP_VM_JOB_CONTEXT_LIST var2;
	Unpack_D2D_CPP_VM_JOB_CONTEXT_LIST(&var2, pStream, dwLenStream);

	//////////////////////////////////////////////////////////////////////////
	ATLASSERT(var1.listsize == var2.listsize);
	if (var1.listsize != var2.listsize)
	{
		DebugBreak();
	}
	for (decltype(var1.listsize) ii = 0; ii < var1.listsize; ++ ii)
	{
		ATLASSERT(0 == memcmp(&var1.items[ii], &var2.items[ii], sizeof(D2D_CPP_VM_JOB_CONTEXT_ITEM)));
		if (0 != memcmp(&var1.items[ii], &var2.items[ii], sizeof(D2D_CPP_VM_JOB_CONTEXT_ITEM)))
		{
			DebugBreak();
		}
	}

	//////////////////////////////////////////////////////////////////////////
	Release_D2D_CPP_VM_JOB_CONTEXT_LIST(&var2);
	Release_D2D_CPP_VM_JOB_CONTEXT_LIST(&var1);

	delete[] pStream; pStream = NULL;

	return 0;
}
