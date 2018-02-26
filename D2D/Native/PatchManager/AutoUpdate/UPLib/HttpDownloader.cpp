#include "StdAfx.h"
#include "HttpDownloader.h"
// --------------------------------------
#define RETURN_IF_CANCELED(ret) \
	if( m_pJobMonitor && m_pJobMonitor->IsJobCanceled() )\
	{\
		return  ret;\
	}\

#define BREAK_IF_CANCELED() \
	if( m_pJobMonitor && m_pJobMonitor->IsJobCanceled() )\
	{\
		break;\
	}\
// --------------------------------------


CHttpDownloader::CHttpDownloader(ARCUPDATE_SERVER_INFO* pSvrInfo, IUpdateJobMonitor* pJobMonitor)
	: m_pJobMonitor(pJobMonitor)
	, m_strUrlOfFile(L"")
	, m_strDstFile(L"")
	, m_httpURL(L"")
	, m_hSession(NULL)
	, m_hConnect(NULL)
	, m_hRequest(NULL)
	, m_ullDownloadedSize(0)
{
	ZeroMemory(&m_ieConfig, sizeof(m_ieConfig));
	ZeroMemory(&m_proxy, sizeof(m_proxy));
	m_proxy.dwAccessType = WINHTTP_ACCESS_TYPE_NO_PROXY;

	ZeroMemory(&m_updateSvrInfo, sizeof(m_updateSvrInfo));
	if (pSvrInfo)
		m_updateSvrInfo = (*pSvrInfo);
}

CHttpDownloader::~CHttpDownloader(void)
{
	Reset();
}

DWORD CHttpDownloader::DownloadFile( const wstring& strUrlOfFile, const wstring& strDstFile )
{
	m_ullDownloadedSize = 0;
	DWORD dwRet = 0;
	m_strDstFile = strDstFile;
	m_strUrlOfFile = STRUTILS::construct_url(strUrlOfFile.c_str());

	wstring srcFileURL = STRUTILS::construct_url(L"http://%s:%d/%s", m_updateSvrInfo.downloadServer, m_updateSvrInfo.nServerPort, m_strUrlOfFile.c_str());
	m_log.LogW(LL_INF, 0, L"%s: Start to download file. SrcFile[%s] DstFile[%s]", __WFUNCTION__, srcFileURL.c_str(), strDstFile.c_str());
	
	_dumpHttpSettings();	

	// return if canceled
	RETURN_IF_CANCELED(0)
	_closeHttpHandles();
		
	dwRet = openHttpSession();
	if(dwRet!=0)
	{
		return dwRet;
	}

	// return if canceled
	RETURN_IF_CANCELED(0)
	dwRet = connectHttpSvr();
	if(dwRet!=0)
	{
		return dwRet;
	}

	// return if canceled
	RETURN_IF_CANCELED(0)
	// return if canceled
	dwRet = createHttpRequest();
	if(dwRet!=0)
	{
		return dwRet;
	}

	// return if canceled
	RETURN_IF_CANCELED(0)
	dwRet = processHttpRequest();
	if(dwRet!=HTTP_STATUS_OK)
	{
		return dwRet;
	}

	// return if canceled
	RETURN_IF_CANCELED(0)
	dwRet = httpReadData();

	return dwRet;
}

DWORD CHttpDownloader::TestHttpConnection( const wstring& strUrlOfFile )
{
	m_strUrlOfFile = STRUTILS::construct_url(strUrlOfFile.c_str());
	DWORD dwRet = 0;
	m_log.LogW(LL_INF, 0, L"%s: Start to test connection. Server[%s] Port[%d] URL[%s]", __WFUNCTION__, 
		m_updateSvrInfo.downloadServer, m_updateSvrInfo.nServerPort, strUrlOfFile.c_str());
	//
	// test if if the specified server is available
	//
	if (m_updateSvrInfo.serverType == ARCUPDATE_SERVER_DEFAULT)
	{
		if (m_updateSvrInfo.bDefaultIEProxy) // using IE default settings
		{
			dwRet = TestWithIEConfig( );
		}
		else // test with customized proxy
		{
			m_strUsername = m_updateSvrInfo.proxyUserName;
			m_strPassword = m_updateSvrInfo.proxyPassword;
			
			m_proxy.dwAccessType = WINHTTP_ACCESS_TYPE_NAMED_PROXY;
			wstring strT = STRUTILS::fstr(L"%s:%d", m_updateSvrInfo.proxyServerName, m_updateSvrInfo.proxyServerPort);
			m_proxy.lpszProxy = (LPWSTR)GlobalAlloc(GMEM_ZEROINIT, (strT.length()+1)*sizeof(WCHAR) );
			ZeroMemory( m_proxy.lpszProxy, (strT.length()+1)*sizeof(WCHAR) );
			wcscpy_s( m_proxy.lpszProxy, strT.length()+1, strT.c_str() );
			
			dwRet = TestWithProxy( );
		}
	}
	else
	{
		dwRet = TestStageServer( );
	}
	return dwRet;
}

DWORD CHttpDownloader::TestWithProxy()
{
	DWORD dwRet = 0;
	m_log.LogW(LL_INF, 0, L"%s: Start to test connection with customized proxy.", __WFUNCTION__ );		
	dwRet = openHttpSession();
	if(dwRet!=0)
		return dwRet;

	// return if canceled
	RETURN_IF_CANCELED(0)
	dwRet = connectHttpSvr();
	if(dwRet!=0)
		return dwRet;

	// return if canceled
	RETURN_IF_CANCELED(0)
	dwRet = createHttpRequest();
	if(dwRet!=0)
		return dwRet;

	// return if canceled
	RETURN_IF_CANCELED(0);
	dwRet = processHttpRequest();
	return dwRet;
}

DWORD CHttpDownloader::TestWithIEConfig( )
{
	m_log.LogW(LL_DET, 0, L"%s: Start to test connection with default IE config.", __WFUNCTION__ );
	
	int ieOptions[4] = {-1, -1, -1, -1};
	DWORD dwRet = 0;
	if(WinHttpGetIEProxyConfigForCurrentUser(&m_ieConfig)== FALSE)
	{
		dwRet = GetLastError();
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to WinHttpGetIEProxyConfigForCurrentUser.", __WFUNCTION__ );
		ieOptions[3] = IECFG_NO_PROXY;;
	}
	else
	{
		if( m_ieConfig.fAutoDetect )
			ieOptions[0] = IECFG_AUTO_DETECT;
		if( m_ieConfig.lpszAutoConfigUrl )
			ieOptions[1] = IECFG_USE_CONFIG_SCRIPT;
		if( m_ieConfig.lpszProxy )
			ieOptions[2] = IECFG_USE_PROXY;
		ieOptions[3] = IECFG_NO_PROXY;
	}

	// return if canceled
	RETURN_IF_CANCELED(0);

	for( int i=0; i<4; i++ )
	{
		_closeHttpHandles( ); // close all handles firstly
		_httpFree( m_proxy );
		// break if canceled
		BREAK_IF_CANCELED()

		if( ieOptions[i]==-1 )
			continue;

		m_hSession = WinHttpOpen( HTTP_CLIENT_NAME, WINHTTP_ACCESS_TYPE_NO_PROXY, WINHTTP_NO_PROXY_NAME, WINHTTP_NO_PROXY_BYPASS, 0); 
		if( m_hSession==NULL )
		{
			dwRet = GetLastError();
			//_handle_http_error( dwRet );
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpOpen.", __WFUNCTION__ );
			break;
		}

		// break if canceled
		BREAK_IF_CANCELED()
		dwRet = openHttpSessionWithIEConfig( (IECONFIG_OPTIONS)(ieOptions[i]) );
		if( dwRet != 0 )
			continue;

		// break if canceled
		BREAK_IF_CANCELED()
		dwRet = connectHttpSvr( );
		if( dwRet != 0 )
			continue;

		// break if canceled
		BREAK_IF_CANCELED()
		dwRet = createHttpRequest();
		if( dwRet!=0 )
			continue;

		// break if canceled
		BREAK_IF_CANCELED()
		dwRet = processHttpRequest();
		if( dwRet!=HTTP_STATUS_OK &&	   // connection is okay
			dwRet!=HTTP_STATUS_NOT_FOUND ) // server is available but URL is not found
		{
			continue;
		}

		break;
	}
	return dwRet;
}

DWORD CHttpDownloader::openHttpSessionWithIEConfig( IECONFIG_OPTIONS ieOption )
{
	DWORD dwRet = 0;
	m_log.LogW(LL_DET, 0, L"%s: IE Option: %d", __WFUNCTION__, ieOption );

	WINHTTP_AUTOPROXY_OPTIONS	autoProxyOptions;
	ZeroMemory( &autoProxyOptions, sizeof(WINHTTP_AUTOPROXY_OPTIONS) );
	ZeroMemory( &m_proxy, sizeof(WINHTTP_PROXY_INFO) );
	m_proxy.dwAccessType = WINHTTP_ACCESS_TYPE_NO_PROXY;

	m_httpURL = STRUTILS::construct_url(L"http://%s:%d/%s", m_updateSvrInfo.downloadServer, m_updateSvrInfo.nServerPort, m_strUrlOfFile.c_str());
	switch(ieOption)
	{
	case IECFG_USE_CONFIG_SCRIPT:
		{
			autoProxyOptions.dwFlags = WINHTTP_AUTOPROXY_CONFIG_URL;
			autoProxyOptions.lpszAutoConfigUrl = m_ieConfig.lpszAutoConfigUrl;
			autoProxyOptions.fAutoLogonIfChallenged = TRUE;
			if( !WinHttpGetProxyForUrl( m_hSession, m_httpURL.c_str(), &autoProxyOptions, &m_proxy ) )
			{
				dwRet = GetLastError();
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpGetProxyForUrl, URL:%s", __WFUNCTION__, m_httpURL.c_str() );
				return dwRet;
			}
			if(!WinHttpSetOption(m_hSession,WINHTTP_OPTION_PROXY, &m_proxy, sizeof(m_proxy)) )
			{
				dwRet = GetLastError(); 
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption", __WFUNCTION__ );
				return dwRet; 
			}
			return 0;
		}
	case IECFG_AUTO_DETECT:
		{
			autoProxyOptions.dwFlags = WINHTTP_AUTOPROXY_AUTO_DETECT;
			autoProxyOptions.dwAutoDetectFlags = WINHTTP_AUTO_DETECT_TYPE_DHCP |WINHTTP_AUTO_DETECT_TYPE_DNS_A;
			autoProxyOptions.fAutoLogonIfChallenged = TRUE;
			if( !WinHttpGetProxyForUrl( m_hSession, m_httpURL.c_str(), &autoProxyOptions, &m_proxy ) )
			{
				dwRet = GetLastError();
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpGetProxyForUrl, URL:%s", __WFUNCTION__, m_httpURL.c_str() );
				return dwRet;
			}
			if( !WinHttpSetOption(m_hSession, WINHTTP_OPTION_PROXY, &m_proxy, sizeof(m_proxy) ) )
			{
				dwRet = GetLastError(); 
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption", __WFUNCTION__ );
				return dwRet; 
			}
			return 0;
		}
	case IECFG_USE_PROXY:
		{
			WINHTTP_PROXY_INFO temp;
			temp.dwAccessType = WINHTTP_ACCESS_TYPE_NAMED_PROXY;
			temp.lpszProxy = m_ieConfig.lpszProxy;
			temp.lpszProxyBypass = m_ieConfig.lpszProxyBypass;
			_copyHttpProxy( m_proxy, temp );
			if( !WinHttpSetOption(m_hSession, WINHTTP_OPTION_PROXY, &m_proxy,sizeof(m_proxy)) )
			{
				dwRet = GetLastError(); 
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption", __WFUNCTION__ );
				return dwRet;
			}			
			return 0;
		}
	case IECFG_NO_PROXY:
		return 0;
	}
	return 0;
}

DWORD CHttpDownloader::TestStageServer( )
{
	DWORD dwRet = 0;
	m_hSession = WinHttpOpen( HTTP_CLIENT_NAME, WINHTTP_ACCESS_TYPE_NO_PROXY, WINHTTP_NO_PROXY_NAME, WINHTTP_NO_PROXY_BYPASS, 0); 
	if( m_hSession==NULL )
	{
		dwRet = GetLastError();
		//_handle_http_error( dwRet );
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpOpen.", __WFUNCTION__ );
		return dwRet;
	}

	// return if canceled
	RETURN_IF_CANCELED(0);
	m_hConnect = WinHttpConnect( m_hSession, m_updateSvrInfo.downloadServer, m_updateSvrInfo.nServerPort, 0 );
	if( m_hConnect==NULL )
	{
		dwRet = GetLastError();
		//_handle_http_error( dwRet );
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpConnect. Svr[%s] Port[%d]", __WFUNCTION__, m_updateSvrInfo.downloadServer, m_updateSvrInfo.nServerPort);
		return dwRet;
	}

	// return if canceled
	RETURN_IF_CANCELED(0);
	m_hRequest  = WinHttpOpenRequest( m_hConnect, L"GET", m_strUrlOfFile.c_str(),  
                                      NULL /* NULL means HTTP 1.1 */, 
                                      WINHTTP_NO_REFERER, 
                                      WINHTTP_DEFAULT_ACCEPT_TYPES, 
                                      WINHTTP_FLAG_REFRESH);
	if( m_hRequest==NULL )
	{
		dwRet = GetLastError();
		//_handle_http_error( dwRet );
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpOpenRequest URL: %s", __WFUNCTION__, m_strUrlOfFile.c_str() );
		return dwRet;
	}

	// return if canceled
	RETURN_IF_CANCELED(0);
	dwRet = processHttpRequest();
	return dwRet;
}

void CHttpDownloader::Reset()
{
	_closeHttpHandles();
	_httpFree( m_ieConfig );
	_httpFree( m_proxy );
}

void CHttpDownloader::Release()
{
	delete this;
}

DWORD CHttpDownloader::openHttpSession( )
{
	DWORD dwRet = 0;

	if (m_updateSvrInfo.serverType == ARCUPDATE_SERVER_DEFAULT && !m_updateSvrInfo.bDefaultIEProxy)
	{
		m_hSession = WinHttpOpen( HTTP_CLIENT_NAME, WINHTTP_ACCESS_TYPE_DEFAULT_PROXY, WINHTTP_NO_PROXY_NAME, WINHTTP_NO_PROXY_BYPASS, 0);
		if(m_hSession==NULL)
		{
			dwRet = GetLastError();
			//_handle_http_error( dwRet );
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpOpen", __WFUNCTION__ );
			return dwRet;
		}

		if( !WinHttpSetOption(m_hSession,WINHTTP_OPTION_PROXY, &m_proxy, sizeof(m_proxy)) )
		{
			dwRet = GetLastError();
			//_handle_http_error( dwRet );
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption [WINHTTP_OPTION_PROXY] ", __WFUNCTION__ );
			return dwRet;
		}
	}
	else
	{
		m_hSession = WinHttpOpen( HTTP_CLIENT_NAME, WINHTTP_ACCESS_TYPE_NO_PROXY, WINHTTP_NO_PROXY_NAME, WINHTTP_NO_PROXY_BYPASS, 0);
		if(m_hSession==NULL)
		{
			dwRet = GetLastError();
			//_handle_http_error( dwRet );
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpOpen", __WFUNCTION__ );
			return dwRet;
		}

		if( m_proxy.dwAccessType!=WINHTTP_ACCESS_TYPE_NO_PROXY )
		{
			if( !WinHttpSetOption(m_hSession,WINHTTP_OPTION_PROXY, &m_proxy, sizeof(m_proxy)) )
			{
				dwRet = GetLastError();
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption [WINHTTP_OPTION_PROXY] ", __WFUNCTION__ );
				return dwRet;
			}
		}
	}
	return dwRet;
}

DWORD CHttpDownloader::connectHttpSvr( )
{
	DWORD dwRet = 0;
	m_hConnect = WinHttpConnect(m_hSession, m_updateSvrInfo.downloadServer, m_updateSvrInfo.nServerPort, 0);
	if( m_hConnect==NULL )
	{
		dwRet = GetLastError();
		//_handle_http_error( dwRet );
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpConnect. Svr[%s] Port[%d]", __WFUNCTION__, m_updateSvrInfo.downloadServer, m_updateSvrInfo.nServerPort);
		return dwRet;
	}
	return dwRet;
}

DWORD CHttpDownloader::createHttpRequest( )
{
	DWORD dwRet = 0;
	m_hRequest = WinHttpOpenRequest( m_hConnect, L"GET", m_strUrlOfFile.c_str(),
		NULL, WINHTTP_NO_REFERER,  
		WINHTTP_DEFAULT_ACCEPT_TYPES,  
		WINHTTP_FLAG_REFRESH );

	if( m_hRequest==NULL )
	{
		dwRet = GetLastError();
		//_handle_http_error( dwRet );
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpOpenRequest. URL[%s]", __WFUNCTION__, m_strUrlOfFile.c_str() );
		return dwRet;
	}

	m_log.LogW(LL_DET, 0, L"%s: Proxy.AccessType=%d",	__WFUNCTION__, m_proxy.dwAccessType );
	m_log.LogW(LL_DET, 0, L"%s: Proxy.Proxy=%s",		__WFUNCTION__, m_proxy.lpszProxy==NULL ? L"<NULL>" : m_proxy.lpszProxy );
	m_log.LogW(LL_DET, 0, L"%s: Proxy.ProxyBypass=%s",	__WFUNCTION__, m_proxy.lpszProxyBypass==NULL ? L"<NULL>" : m_proxy.lpszProxyBypass );
	if( m_proxy.dwAccessType!=WINHTTP_ACCESS_TYPE_NO_PROXY)
	{
		if( !WinHttpSetOption(m_hRequest,WINHTTP_OPTION_PROXY, &m_proxy, sizeof(m_proxy)) )
		{
			dwRet = GetLastError();
			//_handle_http_error( dwRet );
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption [WINHTTP_OPTION_PROXY] ", __WFUNCTION__ );
			return dwRet;
		}

		if( !m_strUsername.empty() )
		{
			if( !WinHttpSetOption( m_hRequest, WINHTTP_OPTION_PROXY_USERNAME,(LPVOID)m_strUsername.c_str(), (DWORD)( m_strUsername.length() ) ) )
			{
				dwRet = GetLastError();
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption. [WINHTTP_OPTION_PROXY_USERNAME]", __WFUNCTION__ );
				return dwRet;
			}
		}

		if( !m_strPassword.empty() )
		{
			if (!WinHttpSetOption(m_hRequest, WINHTTP_OPTION_PROXY_PASSWORD, (LPVOID)m_strPassword.c_str(), (DWORD)(m_strPassword.length()) ) )
			{
				dwRet = GetLastError();
				//_handle_http_error( dwRet );
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to call WinHttpSetOption. [WINHTTP_OPTION_PROXY_PASSWORD]", __WFUNCTION__ );
				return dwRet;
			}
		}
	}
	return dwRet;
}

DWORD CHttpDownloader::processHttpRequest( )
{
	DWORD dwStatusCode = 0;
	DWORD dwSupportedSchemes;
	DWORD dwFirstScheme;
	DWORD dwSelectedScheme;
	DWORD dwTarget;
	DWORD dwLastStatus = 0;
	DWORD dwSize = sizeof(DWORD);
	BOOL  bResults = FALSE;
	BOOL  bDone = FALSE;
	DWORD dwProxyAuthScheme = 0;
	DWORD dwLastError = 0;
	int   nTry=1;
	while( !bDone )
	{
		//  If a proxy authentication challenge was responded to, reset 
		//  those credentials before each SendRequest, because the proxy  
		//  may require re-authentication after responding to a 401 or to 
		//  a redirect. If you don't, you can get into a 407-401-407-401
		//  loop.
		if( dwProxyAuthScheme != 0 )
		{
			bResults = WinHttpSetCredentials( m_hRequest, 
											WINHTTP_AUTH_TARGET_PROXY, 
											dwProxyAuthScheme, 
											m_strUsername.c_str(),
											m_strPassword.c_str(),
											NULL );
			if( !bResults )
			{
				dwLastError = GetLastError();
				m_log.LogW(LL_ERR, dwLastError, L"%s: Failed to call WinHttpSetCredentials. UserName=%s dwProxyAuthScheme=%d", __WFUNCTION__, m_strUsername.c_str(), dwProxyAuthScheme );
			}
		}
		

		// Send a request.
		BREAK_IF_CANCELED();
		bResults = WinHttpSendRequest( m_hRequest,
										WINHTTP_NO_ADDITIONAL_HEADERS,
										0,
										WINHTTP_NO_REQUEST_DATA,
										0, 
										0, 
										0 );

		if( !bResults )
		{
			dwLastError = GetLastError();
			m_log.LogW(LL_ERR, dwLastError, L"%s: Failed to call WinHttpSendRequest (WINHTTP_NO_ADDITIONAL_HEADERS)", __WFUNCTION__ );
		}

		// End the request.
		BREAK_IF_CANCELED();
		if( bResults )
		{
			bResults = WinHttpReceiveResponse( m_hRequest, NULL );
			if( !bResults )
			{
				dwLastError = GetLastError();
				m_log.LogW(LL_ERR, dwLastError, L"%s: Failed to call WinHttpReceiveResponse.", __WFUNCTION__ );
			}
		}

		// Resend the request in case of 
		// ERROR_WINHTTP_RESEND_REQUEST error.
		if( !bResults && GetLastError( ) == ERROR_WINHTTP_RESEND_REQUEST)
		{
			dwLastError = 0;
			continue;
		}

		// Check the status code.
		BREAK_IF_CANCELED();
		if( bResults ) 
		{
			bResults = WinHttpQueryHeaders( m_hRequest, 
											WINHTTP_QUERY_STATUS_CODE | WINHTTP_QUERY_FLAG_NUMBER,
											NULL, 
											&dwStatusCode, 
											&dwSize, 
											NULL );
			if( !bResults )
			{
				dwLastError = GetLastError();
				m_log.LogW(LL_ERR, dwLastError, L"%s: Failed to call WinHttpQueryHeaders. dwStatusCode=%d", __WFUNCTION__, dwStatusCode );				
			}
		}


		if( bResults )
		{
			m_log.LogW(LL_ERR, GetLastError(), L"%s: WinHttpQueryHeaders return status code %d", __WFUNCTION__, dwStatusCode );
			switch( dwStatusCode )
			{
			case HTTP_STATUS_OK: 
				// The resource was successfully retrieved.
				// You can use WinHttpReadData to read the contents 
				// of the server's response.
				bDone = TRUE;
				break;

			case HTTP_STATUS_DENIED:
				// The server requires authentication.
				m_log.LogW(LL_ERR, GetLastError(), L"%s: The server requires authentication. Sending credentials", __WFUNCTION__ );
				// Obtain the supported and preferred schemes.
				bResults = WinHttpQueryAuthSchemes( m_hRequest, 
													&dwSupportedSchemes, 
													&dwFirstScheme, 
													&dwTarget );
				if( !bResults )
				{
					dwLastError = GetLastError();
					m_log.LogW(LL_ERR, dwLastError, L"%s: Failed to call WinHttpQueryAuthSchemes", __WFUNCTION__ );
				}
				// Set the credentials before re-sending the request.
				if( bResults )
				{
					dwSelectedScheme = chooseAuthScheme( dwSupportedSchemes );
					if( dwSelectedScheme == 0 )
					{
						bDone = TRUE;
					}
					else
					{
						bResults = WinHttpSetCredentials( m_hRequest, dwTarget, 
														  dwSelectedScheme,
														  m_strUsername.c_str(),
														  m_strPassword.c_str(),
														  NULL );
						if( !bResults )
						{
							dwLastError = GetLastError();
							m_log.LogW(LL_ERR, dwLastError, L"%s: Failed to call WinHttpSetCredentials. UserName=%s dwSelectedScheme=%d", __WFUNCTION__, m_strUsername.c_str(), dwSelectedScheme );
						}

					}
				}

				// If the same credentials are requested twice, abort the
				// request.  For simplicity, this sample does not check for
				// a repeated sequence of status codes.
				if( dwLastStatus == 401 )
					bDone = TRUE;
				break;

			case HTTP_STATUS_PROXY_AUTH_REQ:
				// The proxy requires authentication.
				m_log.LogW(LL_ERR, GetLastError(), L"%s: The server requires authentication. Sending credentials", __WFUNCTION__ );

				// Obtain the supported and preferred schemes.
				bResults = WinHttpQueryAuthSchemes( m_hRequest, 
													&dwSupportedSchemes, 
													&dwFirstScheme, 
													&dwTarget );
				if( !bResults )
				{
					dwLastError = GetLastError();
					m_log.LogW(LL_ERR, dwLastError, L"%s: Failed to call WinHttpQueryAuthSchemes", __WFUNCTION__ );
				}

				// Set the credentials before re-sending the request.
				if( bResults )
					dwProxyAuthScheme = chooseAuthScheme(dwSupportedSchemes);
				// If the same credentials are requested twice, abort the
				// request.  For simplicity, this sample does not check for
				// a repeated sequence of status codes.
				if( dwLastStatus == 407 && nTry>10 ) // // If the same credentials are requested, try 10 times
					bDone = TRUE;
				else
					nTry++;
				break;
			default:
				bDone = TRUE;
			}
		}

		// Keep track of the last status code.
		dwLastStatus = dwStatusCode;
		// If there are any errors, break out of the loop.
		if( !bResults ) 
			bDone = TRUE;
	}

	// Report any errors.
	if( !bResults )
	{
		//_handle_http_error( dwLastError );
		return dwLastError;
	}
	
	//switch(dwLastStatus)
	//{
	//case HTTP_STATUS_OK:
	//	return 0;
	//case HTTP_STATUS_NOT_FOUND:
	//	return ARCUPDATE_ERROR_NO_UPDATE_FOUND;
	//default:
	//	return dwLastStatus;
	//	//_handle_http_error( dwLastStatus );
	//}
	return dwLastStatus;
}

DWORD CHttpDownloader::chooseAuthScheme( DWORD dwSupportedSchemes )
{
    //  It is the server's responsibility only to accept authentication
    //  schemes that provide a sufficient level of security to protect
    //  the server's resources.
    //
    //  The client is also obligated only to use an authentication
    //  scheme that adequately protects its username and password.
    //  
    if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_NEGOTIATE )
        return WINHTTP_AUTH_SCHEME_NEGOTIATE;
    else if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_NTLM )
        return WINHTTP_AUTH_SCHEME_NTLM;
    else if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_PASSPORT )
        return WINHTTP_AUTH_SCHEME_PASSPORT;
    else if( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_DIGEST )
        return WINHTTP_AUTH_SCHEME_DIGEST;
    else if ( dwSupportedSchemes & WINHTTP_AUTH_SCHEME_BASIC)
        return WINHTTP_AUTH_SCHEME_BASIC;
    else
        return 0;
}

DWORD CHttpDownloader::httpReadData( )
{
	DWORD dwRet  = 0;
	m_log.LogW(LL_INF, 0, L"%s: Start to reading data through HTTP", __WFUNCTION__ );

	HANDLE hFile = ::CreateFile( m_strDstFile.c_str(), 
								 GENERIC_READ|GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, 
								 NULL, CREATE_ALWAYS, FILE_FLAG_BACKUP_SEMANTICS, NULL );
	if( hFile==INVALID_HANDLE_VALUE )
	{
		dwRet = GetLastError(); 
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create file for writing.[%s]", __WFUNCTION__, m_strDstFile.c_str() );
		return dwRet;
	}
	
	DWORD dwSize = 0;
	do
	{
		BREAK_IF_CANCELED()

		dwSize = 0;
		if( !WinHttpQueryDataAvailable( m_hRequest, &dwSize ) )
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to query available data from HTTP", __WFUNCTION__ );
			//_handle_http_error( dwRet );
			break;
		}

		if(dwSize==0)
			break;

		char* pBuf = new char[dwSize];
		if( !pBuf )
		{			
			dwRet = ERROR_OUTOFMEMORY;
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to allocate buffer with size %d", __WFUNCTION__, dwSize );
			break;
		}
		ZeroMemory( pBuf, dwSize);

		DWORD dwRead = 0;
		if( !WinHttpReadData( m_hRequest, (LPVOID)pBuf, dwSize, &dwRead ) )
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to read data through HTTP", __WFUNCTION__ );
			//_handle_http_error( dwRet );
			delete [] pBuf;
			break;
		}

		if( !WriteFile( hFile, pBuf, dwSize, &dwRead, NULL ) )
		{
			dwRet = GetLastError();
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to write data to local file.", __WFUNCTION__ );
			delete [] pBuf;
			break;
		}
		if (m_pJobMonitor)
			m_pJobMonitor->UpdateDownloadedSize(dwSize);			
		m_ullDownloadedSize += dwSize;
		delete [] pBuf;

	} while( dwSize > 0 );

	::CloseHandle(hFile);
	if( dwRet!=0 )
		::DeleteFile( m_strDstFile.c_str() );

	m_log.LogW(LL_INF, dwRet, L"%s: End to read data through HTTP", __WFUNCTION__ );
	return dwRet;
}

void CHttpDownloader::_dumpHttpSettings()
{
	m_log.LogMessageW(LL_DBG, 0, L"    Http Settings....." );
	if (m_updateSvrInfo.serverType == ARCUPDATE_SERVER_STAGE)
	{
		m_log.LogW(LL_DBG, 0, L"        Server Type: Staging Server");
		m_log.LogW(LL_DBG, 0, L"        Server Name: %s", m_updateSvrInfo.downloadServer);
		m_log.LogW(LL_DBG, 0, L"        Port       : %d", m_updateSvrInfo.nServerPort);
	}
	else
	{
		m_log.LogW(LL_DBG, 0, L"        Server Type: CA Server");

		if (m_updateSvrInfo.bDefaultIEProxy)
		{
			m_log.LogW(LL_DBG, 0, L"        Proxy	   : Default Proxy");
		}
		else
		{
			m_log.LogW(LL_DBG, 0, L"        Proxy Server: %s", m_updateSvrInfo.proxyServerName);
			m_log.LogW(LL_DBG, 0, L"        Proxy User : %s", m_updateSvrInfo.proxyUserName);
		}
	}
}

void CHttpDownloader::_httpFree( WINHTTP_CURRENT_USER_IE_PROXY_CONFIG& ieProxyCfg )
{
	if( ieProxyCfg.lpszAutoConfigUrl )  GlobalFree(ieProxyCfg.lpszAutoConfigUrl);
	if( ieProxyCfg.lpszProxy )			GlobalFree(ieProxyCfg.lpszProxy);
	if( ieProxyCfg.lpszProxyBypass )	GlobalFree(ieProxyCfg.lpszProxyBypass);
	ieProxyCfg.lpszAutoConfigUrl = NULL;
	ieProxyCfg.lpszProxy = NULL;
	ieProxyCfg.lpszProxyBypass = NULL;

}

void CHttpDownloader::_httpFree( WINHTTP_PROXY_INFO& proxy )
{
	if(proxy.lpszProxy) GlobalFree(proxy.lpszProxy);
	if(proxy.lpszProxyBypass) GlobalFree(proxy.lpszProxyBypass);
	proxy.lpszProxy = NULL;
	proxy.lpszProxyBypass = NULL;
}

void CHttpDownloader::_initHttpProxy( WINHTTP_PROXY_INFO& proxy )
{
	_httpFree( proxy );
	proxy.dwAccessType = WINHTTP_ACCESS_TYPE_NO_PROXY;
}

void CHttpDownloader::_copyHttpProxy( WINHTTP_PROXY_INFO& dstProxy, const WINHTTP_PROXY_INFO& srcProxy )
{
	_httpFree(dstProxy);
	dstProxy.dwAccessType = srcProxy.dwAccessType;
	if( srcProxy.lpszProxy )
	{
		DWORD dwSize = (DWORD)wcslen(srcProxy.lpszProxy) + 1;
		dstProxy.lpszProxy = (LPWSTR)GlobalAlloc(GMEM_ZEROINIT, dwSize*sizeof(WCHAR) );
		ZeroMemory( dstProxy.lpszProxy, dwSize*sizeof(WCHAR) );
		wcscpy_s( dstProxy.lpszProxy, dwSize+1, srcProxy.lpszProxy );
	}
	if( srcProxy.lpszProxyBypass)
	{
		DWORD dwSize = (DWORD)wcslen(srcProxy.lpszProxyBypass) + 1;
		dstProxy.lpszProxyBypass = (LPWSTR)GlobalAlloc(GMEM_ZEROINIT, dwSize*sizeof(WCHAR) );
		ZeroMemory( dstProxy.lpszProxyBypass, dwSize*sizeof(WCHAR) );
		wcscpy_s( dstProxy.lpszProxyBypass, dwSize+1, srcProxy.lpszProxyBypass );
	}
}

void CHttpDownloader::_closeHttpHandles( )
{
	if( m_hRequest ) WinHttpCloseHandle( m_hRequest );
	if( m_hConnect ) WinHttpCloseHandle( m_hConnect );
	if( m_hSession ) WinHttpCloseHandle( m_hSession );
	m_hRequest = NULL;
	m_hConnect = NULL;
	m_hSession = NULL;
}

DWORD CreateHttpDownloader(ARCUPDATE_SERVER_INFO* pSvrInfo, IUpdateJobMonitor* pJobMonitor, IDownloader** ppDownloader)
{
	(*ppDownloader) = static_cast<IDownloader*> (new CHttpDownloader(pSvrInfo, pJobMonitor));
	return 0;
}