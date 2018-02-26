#ifndef _RPTERROR_H_
#define _RPTERROR_H_


#ifdef __cplusplus
extern "C"
{
#endif

#ifdef _SETUPGUI_DLL
BOOL __declspec(dllexport) WarningContinue(UINT nWarningID, CException* pException, ...);
void __declspec(dllexport) Warning(UINT nWarningID, CException* pException, ...);
#else
BOOL __declspec(dllimport) WarningContinue(UINT nWarningID, CException* pException, ...);
void __declspec(dllimport) Warning(UINT nWarningID, CException* pException, ...);
#endif

#ifdef __cplusplus
}
#endif
#endif	// _RPTERROR_H_
