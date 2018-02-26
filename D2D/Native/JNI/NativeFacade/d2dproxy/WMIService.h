#ifndef _WMISERVICE_H_
#define _WMISERVICE_H_
#include <Wbemidl.h>
#include <comdef.h>
#include <string>

# pragma comment(lib, "wbemuuid.lib")

using namespace std;
//struct IWbemLocator;
//struct IWbemServices;
class CWMIService
{
public:
    CWMIService(void);
    ~CWMIService(void);

	BOOL				Initialize( LPCWSTR wsNameSpace );
	void				Uninitialize( );

    static HRESULT	    GetLastError( );
    static void		    SetLastError( HRESULT hrLastError );
    static std::wstring GetWMIErrorMsg( HRESULT hErr );

    VOID                ConvertBSTR2Wstring(VARIANT& vtBSTR, wstring& wsSTR){if (vtBSTR.vt == VT_BSTR) wsSTR=_bstr_t(V_BSTR(&vtBSTR));}
    //BOOL                GetAdapterGUIDByName( wstring& wsName, vector<wstring>& vGUIDList );
    BOOL                DisableTSO( wstring& wsName );

protected:
    IWbemLocator*  m_pLoc;
    IWbemServices* m_pSvc;
	BOOL           m_bInit;
	BOOL           m_bInitCom;
	static HRESULT m_hrLastError;
};

#endif