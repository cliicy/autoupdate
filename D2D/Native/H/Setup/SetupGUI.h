// SetupGUI.h : Defines the interface for the DLL.
//

#ifndef _SETUPGUI
#define _SETUPGUI

#include <setupinfo.h>


#ifdef __cplusplus
extern "C"
{
#endif

#define WM_USER_SETUPGUI (WM_USER+100)

#ifdef _SETUPGUI_DLL
void __declspec(dllexport) Install(LPSETUP_INFO lpSetupInfo);
void __declspec(dllexport) Uninstall(LPSETUP_INFO lpSetupInfo);
#else
void __declspec(dllimport) Install(LPSETUP_INFO lpSetupInfo);
void __declspec(dllimport) Uninstall(LPSETUP_INFO lpSetupInfo);
#endif


#ifdef __cplusplus
}
#endif

#endif

