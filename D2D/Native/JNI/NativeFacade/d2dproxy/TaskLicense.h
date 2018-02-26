#pragma once 
#include "id2dproxydef.h"
#include "Basecodec.h"

class CTaskLicense :public ITaskItem
{
public:
	CTaskLicense(void);
	virtual ~CTaskLicense(void);
public:
	static CTaskLicense *  CreateInstance();
	CA_COMCOUNTERIMPL();
	CA_BEGIN_INTERFACE_MAP()
		CA_INTERFACE(ITaskItem)
	CA_END_INTERFACE_MAP()
public:
	//ITaskItem
	virtual HRESULT DoWork();
	//
	DWORD Init(PD2D_CHKLIC_LISTENTRY pLicEntry);
	DWORD GetCheckReslut(PD2D_CHKLIC_LISTENTRY pLicEntry);
protected:
	DWORD CheckLicense();
	DWORD CheckLicenseEx();
	BOOL IsUnderCPM();

protected:
	D2D_CHKLIC_LISTENTRY m_licEntry;
};