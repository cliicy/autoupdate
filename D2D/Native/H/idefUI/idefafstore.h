#ifndef CA_D2DWIN8_UNIVERSAL_INTERFACE_idefAFStore__H
#define CA_D2DWIN8_UNIVERSAL_INTERFACE_idefAFStore__H
#pragma  once//idefAFStore.h

#include "idefunknown.h"
#include <comdef.h>
#include "..\AFStorInterface.h"

CA_DEFINE_IID(IUIAFStor, "{4A12E736-A073-4e6a-943C-ADF26C42E517}");
class IUIAFStor : public IUnknown
{
public:
	virtual DWORD __stdcall iCreateReclaimObject(LPCWSTR pszSessionFolder, LPCWSTR pszTmpFolder, IAFReclaim** ppReclaim)=0;
	virtual CREATE_SESS_ERR __stdcall iCreateDevObject(LPCWSTR pszRootFolder, IAFStorDev** ppDevObj)=0;
};
#endif//CA_D2DWIN8_UNIVERSAL_INTERFACE_idefUI__H