#ifndef CA_D2DWIN8_UNIVERSAL_INTERFACE_idefUI__H
#define CA_D2DWIN8_UNIVERSAL_INTERFACE_idefUI__H
#pragma  once

#ifdef  D2DUI_EXPORTS
#define D2DUI_EXPORT_FUNC __declspec(dllexport)
#else
#define D2DUI_EXPORT_FUNC __declspec(dllimport)
#endif

extern "C"  D2DUI_EXPORT_FUNC HRESULT  WINAPI CreateInterface(GUID iid , void ** ppv);
extern "C"  D2DUI_EXPORT_FUNC HRESULT  WINAPI ReleaseInterface(void * pv);

#endif//CA_D2DWIN8_UNIVERSAL_INTERFACE_idefUI__H