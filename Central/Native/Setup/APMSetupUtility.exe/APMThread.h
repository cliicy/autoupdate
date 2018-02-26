#pragma once

#include <string>
#include <vector>
#include "APMSetupUtilityDlg.h"

using namespace std;

extern bool g_bContinuousMode;

struct UpdateMessage{
	int			m_iProductId;
	wstring		m_strError;
};
DWORD WINAPI RunApm(__in  LPVOID lpParameter);

DWORD getProductsToDownloadPatch( OUT int arrProductId[], IN OUT int &nSize );

DWORD getProductsToInstallPatch( OUT int arrProductId[], IN OUT int &nSize );

DWORD getFirstXmlDoc( IN const wstring &strSource, OUT wstring &strFirstXml );

DWORD checkResponseComplete( IN const wstring &strSource );

wstring generateRequestId();

//DWORD getLastXmlDoc( IN const wstring &strSource, OUT wstring &strLastXml );

wstring generateMessgeForProduct( int nProductId, const wstring &strMessage );

wstring loadString( UINT resourceId );

wstring generateMessage( const vector<UpdateMessage> &vecUpdateMessage );



