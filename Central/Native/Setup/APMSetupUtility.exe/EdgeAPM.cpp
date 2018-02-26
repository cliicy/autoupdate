#include "stdafx.h"
#include <atlbase.h>

#include "EdgeUtility.h"
#include "EdgeAPM.h"
#include "MSXMLParserWrapper.h"
#include "Utility.h"
#include "Golbals.h"
#include "IniFileWrapper.h"

bool g_bContinuousMode;

namespace EdgeAPM
{
	ServerInfoModel::ServerInfoModel()
	{
	}
	APMSettingModel::APMSettingModel():m_nServerType(APM_SERVERTYPE_CA), m_bProxy(FALSE), m_bProxyAuth(FALSE)
	{
	}
	DWORD createRequest(const wstring &strRequestId, int command, int nProduct, wstring &strRequest, int nmajorver, int minorver)
	{
		HXMLDOCUMENT hDoc = NULL;
		HXMLELEMENT  hMessage = NULL;
		HXMLELEMENT	 hHeader = NULL;
		HXMLELEMENT	 hBody = NULL;
		HXMLELEMENT	 hProduct = NULL;
		HXMLELEMENT  hRelease = NULL;
		HXMLELEMENT  hElement = NULL;
		HXMLATTRIBUTE hAttribute = NULL;
		WCHAR buf[1024];
		WCHAR number[10];
		WCHAR hostname[512] = {0};
		DWORD dwCount;
//		DWORD dwRet;
		//BOOL bRet;
		UINT iMajorVer = 16, iMinorVer = 0;
		WCHAR  			szInstructionXml[MAX_PATH];
		WCHAR  			szXmlData[MAX_PATH];

		memset(szInstructionXml, L'\0', MAX_PATH * sizeof(WCHAR));
		wcscpy(szInstructionXml, L"xml");
		memset(szXmlData, L'\0', MAX_PATH * sizeof(WCHAR));
		wcscpy(szXmlData, L"version=\"1.0\" encoding=\"UTF-8\"");


		CoInitialize(NULL);
		hDoc = JobCreateDocument();
		JobCreateProcessingInstruction(hDoc,szInstructionXml, szXmlData);
		hMessage = JobCreateElement(hDoc, L"Message", NULL);

		//create header
		hHeader = JobCreateElement(hDoc, L"Header", NULL);
		hElement = JobCreateElement(hDoc, L"Id", strRequestId.c_str());
		JobAddElementChild(hHeader, hElement);
		JobDestroyElement(hElement);


		DWORD dwLen = _countof(hostname);
		GetComputerName(hostname, &dwLen);						
		hElement = JobCreateElement(hDoc, L"Source", hostname);		
		JobAddElementChild(hHeader, hElement);
		JobDestroyElement(hElement);

		hElement = JobCreateElement(hDoc, L"Type", APM_REQUEST_TYPE_UICOMMAND);
		JobAddElementChild(hHeader, hElement);
		JobDestroyElement(hElement);		

		hElement = JobCreateElement(hDoc, L"Length", L"1000");
		JobAddElementChild(hHeader, hElement);
		JobDestroyElement(hElement);

		hElement = JobCreateElement(hDoc, L"Flags", L"1");
		JobAddElementChild(hHeader, hElement);
		JobDestroyElement(hElement);

		hElement = JobCreateElement(hDoc, L"SequenceNumber", L"1");
		JobAddElementChild(hHeader, hElement);
		JobDestroyElement(hElement);

		hElement = JobCreateElement(hDoc, L"Command", NULL);
		hAttribute = JobCreateAttribute(hDoc, L"Id", _itow(command, buf, 10));
		JobAddElementAttribute(hElement, hAttribute);
		JobDestroyAttribute(hAttribute);
		JobAddElementChild(hHeader, hElement);
		JobDestroyElement(hElement);

		JobAddElementChild(hMessage, hHeader);
		JobDestroyElement(hHeader);

		//create body
		hBody = JobCreateElement(hDoc, L"Body", NULL);
		hProduct = JobCreateElement(hDoc, L"Product", NULL);

		wstring strProduct = L"";
		switch( nProduct ){
			case APM_EDGE_CM:
				strProduct += L"CA ARCserve Edge CM";
				break;
			case APM_D2D:
				strProduct += L"CA ARCserve D2D";
				break;
		}
		hAttribute = JobCreateAttribute(hDoc, L"Name", strProduct.c_str());
		JobAddElementAttribute(hProduct, hAttribute);
		JobDestroyAttribute(hAttribute);

		hRelease = JobCreateElement(hDoc, L"Release", NULL);

		hAttribute = JobCreateAttribute(hDoc, L"MajorVersion", _itow(nmajorver, number, 10));
		JobAddElementAttribute(hRelease, hAttribute);
		JobDestroyAttribute(hAttribute);
		hAttribute = JobCreateAttribute(hDoc, L"MinorVersion", _itow(minorver, number, 10));
		JobAddElementAttribute(hRelease, hAttribute);
		JobDestroyAttribute(hAttribute);
		hAttribute = JobCreateAttribute(hDoc, L"ServicePack", L"");
		JobAddElementAttribute(hRelease, hAttribute);
		JobDestroyAttribute(hAttribute);

		JobAddElementChild(hProduct, hRelease);
		JobDestroyElement(hRelease);
		JobAddElementChild(hBody, hProduct);
		JobDestroyElement(hProduct);
		JobAddElementChild(hMessage, hBody);
		JobDestroyElement(hBody);

		JobSetDocumentElement(hDoc, hMessage);
		JobDestroyElement(hMessage);

		//recalculate the count of length
		JobGetDocumentSizeInWCHARs(hDoc, &dwCount);
		int length = wcslen(_itow(dwCount, number, 10))  + dwCount;
		hElement = JobGetElement(hDoc, L"/Message/Header/Length");
		if(hElement){
			JobSetElementValue(hElement, _itow(length, number, 10));
			JobDestroyElement(hElement);
		}

		JobSerializeDocumentToXML(hDoc, buf, &dwCount);
		//JobSaveXMLDocument(hDoc, L"d:\\request.xml");
		JobDestroyDocument(hDoc);

		CoUninitialize();
		
		strRequest = wstring(buf);
		return 0;
	}

	DWORD GetProductVersion( UINT &majorVer, UINT &minorVer )
	{
		WCHAR buf[256];
		DWORD res = 0;
		CRegKey reg;
		ULONG count = 256;

		res = reg.Open(HKEY_LOCAL_MACHINE, REG_EDGE_COMMON);
		if (res){
			WriteLog( L"fail to open registry %s", REG_EDGE_COMMON);
			return 2;
		}

		res = reg.QueryStringValue(L"Version", buf, &count);
		if(res){
			WriteLog( L"fail to read registry Version");
			return 2;
		}

		reg.Close();
		if( ConvertString2Version(buf, majorVer, minorVer) )
			return 1;
		return 0;
	}

	DWORD ProcessResponse(const wstring &strResponse, struct Response &response){
		HXMLDOCUMENT hDoc = NULL;
		HXMLELEMENT hType = NULL;
		HXMLELEMENT hId = NULL;
		HXMLELEMENT hErrorMessage = NULL;


		DWORD dwRet = 0;
		WCHAR buf[1024];
		DWORD dwCount;
		const int iBufSize = 1024;
		const wstring typPath= L"/Message/Header/Type";
		const wstring idPath = L"/Message/Header/Id";
		const wstring errorMsgPath= L"/Message/Header/ReturnMessage";

		CoInitialize(NULL);
		hDoc = JobCreateDocumentFromXMLStream(strResponse.c_str());
		if( NULL == hDoc ){
			CoUninitialize();
			return 1;
		}

		hType = JobGetElement(hDoc, (LPWSTR)typPath.c_str());
		if(hType){
			dwCount = _countof(buf);
			buf[0] = L'\0';
			JobGetElementValue(hType, buf, &dwCount);
			response.m_strType = wstring( buf );
		}

		hId = JobGetElement( hDoc, (LPWSTR)idPath.c_str());
		if( hId ){
			dwCount = _countof(buf);
			buf[0] = L'\0';
			JobGetElementValue(hId, buf, &dwCount);
			response.m_strId = wstring( buf );
		}

		hErrorMessage = JobGetElement(hDoc, (LPWSTR)errorMsgPath.c_str());
		if(hErrorMessage){
			dwCount = _countof(buf);
			buf[0] = L'\0';
			JobGetElementValue(hErrorMessage, buf, &dwCount);
			response.m_strError = wstring( buf );
		}else{
			dwRet = 2;
		}


		JobDestroyElement(hType);
		JobDestroyElement(hId);
		JobDestroyElement( hErrorMessage );
		JobDestroyDocument(hDoc);
		CoUninitialize();
		return dwRet;
	}

	DWORD createCheckUpdateRequest( const wstring &strRequestId, wstring &strRequest, int nProductId, int nmajorver, int minorver)
	{
		return createRequest( strRequestId, 101, nProductId, strRequest, nmajorver, minorver);
	}

	DWORD createTerminateRequest( const wstring &strRequestId, wstring &strRequest, int nProductId, int nmajorver, int minorver)
	{
		return createRequest( strRequestId, 199, nProductId, strRequest, nmajorver, minorver);
	}

	DWORD InstallPatch(const PackInfo &pack)
	{
		int nRet = 0;
		HANDLE hProcess = INVALID_HANDLE_VALUE;
		DWORD dwRet = 0;
		if( pack.m_iAvailabe != 1 || pack.m_iDownload != 1 )
			return 1;
		if (pack.m_iInstall != 1 ){
			wstring cmd = L"\"" + pack.m_strDownloadLocation + L"\"";

			if (g_bContinuousMode)
			{
				cmd += L" /s /v\"/s /m /c\"";
			}
			else
			{
				cmd += L" /s /v\"/s /m\"";
			}
				
			WriteLog(L"install patch : %s", cmd.c_str());
			dwRet = RunProcess( cmd.c_str(), hProcess );
			if( dwRet ){
				WriteLog( L"Fail to launch patch exe %s, %d", cmd.c_str(), dwRet);
				return dwRet;
			}
			if( WAIT_OBJECT_0 != WaitForSingleObject( hProcess, INFINITE ) ){
				WriteLog(L"Fail to wait for the install finish");
				return 1;
			}
		}
		return 0;
	}

	BOOL IsProductInstalled( int nProductId )
	{
		CRegKey reg;
		DWORD res = 0;
		wstring strReg;

		GetRegRootKeyByProductId( nProductId, strReg );
		res = reg.Open(HKEY_LOCAL_MACHINE, strReg.c_str());
		if(res)
			return FALSE;
		else
			return TRUE;
	}

	DWORD GetRegRootKeyByProductId( int nProductId, wstring &strRegKey )
	{
		DWORD dwRet = 0;
		switch( nProductId ){
			case APM_EDGE_COMMON:
				strRegKey = REG_EDGE_COMMON;
				break;
			case APM_EDGE_CM:
				strRegKey = REG_EDGE_CM;
				break;
			case APM_EDGE_VCM:
				strRegKey = REG_EDGE_VCM;
				break;
			case APM_EDGE_VSPHERE:
				strRegKey = REG_EDGE_VSPHERE;
				break;
			case APM_EDGE_REPORT:
				strRegKey = REG_EDGE_REPORT;
				break;
			default:
				strRegKey.clear();
				dwRet = 1;
				break;
		}

		return dwRet;
	}

	Response::Response(): m_iError(0){}

	ServerInfo::ServerInfo(): m_nPort(0){}

	APMSetting::APMSetting(): m_nServerType(0){}

	DWORD SetRestartServerAfterPatch( int nProductId, bool bValue, OUT bool &bOriginal )
	{
		wstring strValue;
		wstring strRegKey;
		CRegKey reg;
		LONG nRes = 0;
		WCHAR buf[256];
		const ULONG bufSize = 256;
		DWORD res = 0;
		ULONG count = 256;

		if( bValue ){
			strValue = L"True";
		}else{
			strValue = L"False";
		}

		GetRegRootKeyByProductId( nProductId, strRegKey );
		nRes= reg.Open(HKEY_LOCAL_MACHINE, strRegKey.c_str());
		if (nRes){
			WriteLog( L"fail to open registry %s", strRegKey.c_str());
			return GetLastError();
		}

		count = bufSize;
		nRes = reg.QueryStringValue(REG_EDGE_RestartServiceAfterPatch, buf, &count);
		if( nRes ){
			WriteLog( L"fail to read registry key %s", REG_EDGE_RestartServiceAfterPatch );
			return GetLastError();
		}
		if( _wcsicmp(buf, L"True") ){
			bOriginal = false;
		}else{
			bOriginal = true;
		}

		nRes= reg.SetStringValue(REG_EDGE_RestartServiceAfterPatch, strValue.c_str());
		if (nRes){
			WriteLog( L"fail to write registry key %s", REG_EDGE_RestartServiceAfterPatch);
			reg.Close();
			return GetLastError();
		}

		reg.Close();
		return 0;
	}


	DWORD GetPackInfoFromXmlFile( const wstring &strFilePath, PackInfo &packInfo )
	{
		HXMLDOCUMENT hDoc = NULL;
		HXMLELEMENTLIST hEleList = NULL;
		BOOL bRet;

		CoInitialize(NULL);

		hDoc = JobCreateDocumentFromXML(strFilePath.c_str());
		if( NULL == hDoc ){
			return 2;
		}
		hEleList = JobGetElementList(hDoc, L"/Product/Release/Package");

		HXMLELEMENT hPack = NULL;
		HXMLELEMENT hElement = NULL;
		HXMLATTRIBUTE hAttribute = NULL;
		WCHAR buf[10*1024];
		const int iBufSize = 10*1024;
		DWORD dwCount = 0;

		hPack = JobGetElementFromList(hEleList, 0);

		hAttribute = JobGetElementAttributeByName(hPack, L"Id");
		dwCount = iBufSize;
		bRet = JobGetAttributeValue(hAttribute, buf, &dwCount);
		JobDestroyAttribute(hAttribute);
		packInfo.m_strPackId = wstring(buf);
		buf[0] = L'\0';

		hAttribute = JobGetElementAttributeByName(hPack, L"PublishedDate");
		dwCount = iBufSize;
		JobGetAttributeValue(hAttribute, buf, &dwCount);
		JobDestroyAttribute(hAttribute);
		packInfo.m_strDate = wstring(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"Desc");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_strDesc = wstring(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"Downloadedlocation");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_strDownloadLocation = wstring(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"UpdateURL");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_strUpdateUrl = wstring(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"RebootRequired");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_iReboot = _wtoi(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"Size");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_iSize = _wtoi(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"UpdateVersionNumber");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_strUpdateVersionNumber = wstring(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"AvailableStatus");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_iAvailabe = _wtoi(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"DownloadStatus");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_iDownload = _wtoi(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"InstallStatus");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_iInstall = _wtoi(buf);
		buf[0] = L'\0';

		hElement =JobGetElementChildByName(hPack, L"ErrorMessage ");
		dwCount = iBufSize;
		JobGetElementValue(hElement, buf, &dwCount);
		JobDestroyElement(hElement);
		packInfo.m_strError = wstring(buf);
		buf[0] = L'\0';

		JobDestroyElement(hPack);

		JobDestroyElementList(hEleList);
		JobDestroyDocument(hDoc);
	
		CoUninitialize();

		return 0;
	}

	PackInfo::PackInfo(): m_iReboot(0), m_iSize(0), m_iAvailabe(0), m_iDownload(0), m_iInstall(0){}

	DWORD GetPackInfo( int nProductId, PackInfo &packInfo ){
		wstring path;
		DWORD dwRet = 0;

		dwRet = GetEdgeRootDir(path);
		if( dwRet ){
			WriteLog( L"Fail to obtain edge root dir." );
			return 2;
		}
		path += wstring(FOLDER_UPDATEMANAGER) + L"\\" + FOLDER_UPDATEMANAGER_ARCAPP + L"\\";
		switch( nProductId ){
		case APM_EDGE_COMMON:
			path += FOLDER_UPDATEMANAGER_COMMON;
			break;
		case APM_EDGE_CM:
			path += FOLDER_UPDATEMANAGER_CM;
			break;
		case APM_EDGE_VCM:
			path += FOLDER_UPDATEMANAGER_VCM;
			break;
		case APM_EDGE_VSPHERE:
			path += FOLDER_UPDATEMANAGER_VSphere;
			break;
		case APM_EDGE_REPORT:
			path += FOLDER_UPDATEMANAGER_REPORTING;
			break;
		}
		path += wstring(L"\\") + FILE_UPDATEMANAGER_STATUS;

		dwRet = GetPackInfoFromXmlFile(path, packInfo);
		if( dwRet ){
			WriteLog( L"Fail to get pack info from %s, ec=%d", path.c_str(), dwRet );
			return dwRet;
		}

		return dwRet;
	}

	DWORD GetProductPatchVersion( int nProductId, UINT& nPatchVersion )
	{
		DWORD dwRet = 0;
		wstring strVersion;
		
		dwRet = GetProductExtentionKey( nProductId, REG_EDGE_UpdateVersionNumber, strVersion );
		if( dwRet ){
			WriteLog( L"Fail to read patch version from registry." );
			return dwRet;
		}
		nPatchVersion = _wtoi( strVersion.c_str() );

		return 0;
	}
	/*
	DWORD TestDownloadServerConnection(const APMSetting &apmSetting)
	{
		DWORD dwRet = 0;
		
		wstring strSubDir;
		GetEdgeRootDir(strSubDir);
		strSubDir += wstring(FOLDER_UPDATEMANAGER) + L"\\" + FOLDER_UPDATEMANAGER_ARCAPP + L"\\";
		
		CoInitialize(NULL);
		wstring strUri = strSubDir + FILE_EDGE_PMClient_XML;
		HXMLDOCUMENT hDoc = JobCreateDocumentFromXML(strUri.c_str());
		HXMLELEMENT hElement = JobGetElement(hDoc, L"/client/product[@Name='CA ARCserve Edge Common']/downloadinfo/Protocol/pathonsource");
		WCHAR chUri[128] = {L'\0'};
		DWORD bufSize = _countof(chUri);
		JobGetElementValue(hElement, chUri, &bufSize);
		JobDestroyElement(hElement);
		hElement = JobGetElement(hDoc, L"/client/product[@Name='CA ARCserve Edge Common']/downloadinfo/Protocol/ServerName");
		WCHAR chCaServer[128] ={L'\0'};
		bufSize = _countof(chCaServer);
		JobGetElementValue(hElement, chCaServer, &bufSize);
		JobDestroyElement(hElement);
		JobDestroyDocument(hDoc);
		CoUninitialize();

		wstring strServerName;
		WCHAR chPort[10] = L"80";
		wstring strProxy;
		WCHAR chProxyPort[10] = L"";
		wstring strProxyUser;
		wstring	strProxyPassword;
		if( apmSetting.m_nServerType == APM_SERVERTYPE_CA ){
			strServerName = wstring(chCaServer);
			if( apmSetting.m_bProxy ){
				strProxy = apmSetting.m_proxyServer.m_strServer;
				_itow( apmSetting.m_proxyServer.m_nPort, chProxyPort, 10 );
				if( apmSetting.m_bProxyAuth ){
					strProxyUser = apmSetting.m_strProxyUsername;
					strProxyPassword = apmSetting.m_strProxyPwd;
				}
			}
		}else{
			strServerName = apmSetting.m_vecStaging[0].m_strServer;
			_itow( apmSetting.m_vecStaging[0].m_nPort, chPort, 10 );
		}


		wstring strDllPath = strSubDir + FILE_EdgePMCommandBase_dll;
		typedef INT (*PFunDownloadServerStatus)(TCHAR*, UINT, WCHAR*, WCHAR*, WCHAR*, WCHAR*, WCHAR*, WCHAR*);
		PFunDownloadServerStatus pfntestDownloadServerStatus = (PFunDownloadServerStatus)DynGetProcAddress( strDllPath, "IS_SERVER_AVBL2");
		if( pfntestDownloadServerStatus == NULL ){
			WriteLog( L"Failed to call IS_SERVER_AVBL2 in %s. ec=%d", strDllPath.c_str(), GetLastError());
			return 1;
		}

		WriteLog( L"Begin to test connection: server type:%d, server name:%s, server port:%s, proxy server:%s, proxy port:%s, proxy user:%s",
			apmSetting.m_nServerType,
			strServerName.c_str(),
			chPort,
			strProxy.c_str(),
			chProxyPort,
			strProxyUser.c_str());
		int nStatus = pfntestDownloadServerStatus(chUri, 
			apmSetting.m_nServerType,
			(TCHAR *)strServerName.c_str(),
			chPort,
			(TCHAR *)strProxy.c_str(),
			(TCHAR *)chProxyPort,
			(TCHAR *)strProxyUser.c_str(),
			(TCHAR *)strProxyPassword.c_str());
		if( nStatus == 0 )
			dwRet = 0;
		else{
			dwRet = 2;
		}

		//BOOL bRet = FreeLibrary(hDll);
		return dwRet;
	}*/
}