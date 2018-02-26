#include "StdAfx.h"
#include "Native_ASAG_CRC.h"
#define CRC32_POLYNOMIAL		0XEDB88320L
VOID Native_ASAG_CRC::BuildCRCTable()
{
	INT i, j;
	DWORD crc;

	for (i = 0; i <= 255; i++)
	{
		crc = i;
		for (j = 8; j > 0; j--)
		{
			if (crc & 1 )
				crc = (crc >> 1 ) ^ CRC32_POLYNOMIAL;
			else
				crc >>= 1;
		}
		CRCTable[ i ] = crc;
	}
}

VOID Native_ASAG_CRC::CSSCheckSum(PCHAR buffer, DWORD count)
{
	PUCHAR p;
	DWORD crc = chkSum;
	DWORD temp1, temp2;

	if(!CRCTableInitialized)
	{
		BuildCRCTable();
		CRCTableInitialized = TRUE;
	}

	p = (PUCHAR) buffer;
	while (count-- != 0)
	{
		temp1 = (crc >> 8) & 0x00FFFFFFL;
		temp2 = CRCTable[ ( crc ^ *p++) & 0xff ];
		crc = temp1 ^ temp2;
	}
	chkSum=crc;
	return;
}

VOID Native_ASAG_CRC::CSSCheckSum(PCHAR buffer, DWORD count, DWORD *pdwCRC)
{
	PUCHAR p;
	DWORD crc = count/**pdwCRC*/;
	DWORD temp1, temp2;

	if(!CRCTableInitialized)
	{
		BuildCRCTable();
		CRCTableInitialized = TRUE;
	}

	p = (PUCHAR) buffer;
	while (count-- != 0)
	{
		temp1 = (crc >> 8) & 0x00FFFFFFL;
		temp2 = CRCTable[ ( crc ^ *p++) & 0xff ];
		crc = temp1 ^ temp2;
	}
	*pdwCRC=crc;
	return;
}