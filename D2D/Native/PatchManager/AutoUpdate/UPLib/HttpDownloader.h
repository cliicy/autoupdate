#pragma once
#include "UpLib.h"
#include <winhttp.h>
#include <string>
using namespace std;

enum IECONFIG_OPTIONS
{
	IECFG_AUTO_DETECT			= 1,	// "automatically detect settings".
	IECFG_USE_CONFIG_SCRIPT		= 2,	// "Use automatic proxy configuration"
	IECFG_USE_PROXY				= 3,	// "use a proxy server".
	IECFG_NO_PROXY				= 4,	// "No proxy being used"
};

class CHttpDownloader : public IDownloader
{
public:
	CHttpDownloader(ARCUPDATE_SERVER_INFO* pSvrInfo, IUpdateJobMonitor* pJobMonitor);

	virtual ~CHttpDownloader(void);

	virtual DWORD	DownloadFile( const wstring& strUrlOfFile, const wstring& strDstFile );

	virtual DWORD	TestHttpConnection( const wstring& strUrlOfFile );

	virtual void    Reset( );

	virtual void	Release();

	virtual ULONGLONG	GetDownloadedSize() { return m_ullDownloadedSize;  }

protected:
	DWORD	TestStageServer( );

	DWORD	TestWithIEConfig( );

	DWORD	TestWithProxy();

	DWORD	openHttpSessionWithIEConfig( IECONFIG_OPTIONS ieOption );
protected:
	

	DWORD	openHttpSession( );

	DWORD   connectHttpSvr( );

	DWORD	createHttpRequest( );

	DWORD	processHttpRequest( );

	DWORD	httpReadData( );

	DWORD	chooseAuthScheme( DWORD dwSupportedSchemes );
protected:
	void	_dumpHttpSettings();

	void	_httpFree( WINHTTP_CURRENT_USER_IE_PROXY_CONFIG& ieProxyCfg );

	void	_httpFree( WINHTTP_PROXY_INFO& proxy );

	void	_initHttpProxy( WINHTTP_PROXY_INFO& proxy );

	void	_copyHttpProxy( WINHTTP_PROXY_INFO& dstProxy, const WINHTTP_PROXY_INFO& srcProxy );

	void	_closeHttpHandles( );
protected:
	CDbgLog				m_log;
	IUpdateJobMonitor*	m_pJobMonitor;
protected:
	HINTERNET			m_hSession;
	HINTERNET			m_hConnect;
	HINTERNET			m_hRequest;
protected:
	WINHTTP_CURRENT_USER_IE_PROXY_CONFIG m_ieConfig; // the IE configurations
	WINHTTP_PROXY_INFO  m_proxy;		 // the http proxy info
	wstring				m_strUrlOfFile;	 // the URL to download
	wstring				m_strUsername;	 // the proxy user. If IE default is being used, it is the admin user name
	wstring				m_strPassword;	 // the proxy user. If IE default is being used, it is the admin password
	wstring             m_strDstFile;    // the local path to save the downloaded file
	wstring				m_httpURL;		  // the full URL.

protected:
	ARCUPDATE_SERVER_INFO     m_updateSvrInfo;

protected:
	ULONGLONG			m_ullDownloadedSize;
};

