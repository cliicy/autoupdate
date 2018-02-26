// V2PNativeFacadeSrv.cpp : Implementation of WinMain


#include "stdafx.h"
#include "resource.h"
#include "V2PNativeFacadeSrv_i.h"
#include "V2PSrvDebugLog.h"
#include "CNativeCmdCtrl.h"
#include "ExceptionDump.h"

using namespace ATL;


class CV2PNativeFacadeSrvModule : public ATL::CAtlExeModuleT< CV2PNativeFacadeSrvModule >
{
public :
	DECLARE_LIBID(LIBID_V2PNativeFacadeSrvLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_V2PNATIVEFACADESRV, "{B05AA742-28AA-499F-86FB-68586B96BA74}")
	};

CV2PNativeFacadeSrvModule _AtlModule;



//
extern "C" int WINAPI _tWinMain(HINSTANCE /*hInstance*/, HINSTANCE /*hPrevInstance*/, 
								LPTSTR /*lpCmdLine*/, int nShowCmd)
{
	int nRet = 0;
	do 
	{
        ExceptionDump::SetExceptionHandler(FALSE);
		HRESULT hr = CCNativeCmdCtrl::InitV2PNativeSrv();
		if (FAILED(hr))
		{
			D2DDEBUGLOG(LL_ERR, hr, __WFUNCTION__, L"Failed to init V2P Native Server.");
			break;
		}
		nRet = _AtlModule.WinMain(nShowCmd);

		hr = CCNativeCmdCtrl::UnInitV2PNativeSrv();
		if (FAILED(hr))
		{
			D2DDEBUGLOG(LL_ERR, hr, __WFUNCTION__, L"Failed to uninit V2P Native Server.");
			break;
		}

	} while (FALSE);
	

	return nRet;
}

