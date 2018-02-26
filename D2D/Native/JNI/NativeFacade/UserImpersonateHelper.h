#pragma once

#include "comdef.h"

#define  D2DCKUSER_OK			0
#define  D2DCKUSER_FAIL			0xFFF00000
#define  D2DCK_E_VALIDARG		(D2DCKUSER_FAIL + 0x1)
#define  D2DCK_E_LOGON			(D2DCKUSER_FAIL + 0x2)
#define  D2DCK_E_GETTINFO		(D2DCKUSER_FAIL + 0x3)
#define  D2DCK_E_PRIVILEGE		(D2DCKUSER_FAIL + 0x4)
#define  D2DCK_E_OUTOFMEM		(D2DCKUSER_FAIL + 0x5)
#define  D2DCK_E_NOMAILBOX		(D2DCKUSER_FAIL + 0x6)
#define  D2DCK_E_NO_POST_03		(D2DCKUSER_FAIL + 0x7)
#define  D2DCK_E_FIND_POST_03	(D2DCKUSER_FAIL + 0x8)
#define  D2DCK_E_FAIL_10		(D2DCKUSER_FAIL + 0x9)
#define  D2DCK_E_FAIL_07		(D2DCKUSER_FAIL + 0x0A)
#define  D2DCK_E_IMPERSONATE	(D2DCKUSER_FAIL + 0x0B)

class CUserImpersonateHelper
{
public:
	CUserImpersonateHelper(LPCWSTR pwszUserNameWithDomain, LPCWSTR pwszPassword);
	HRESULT GetError();
	~CUserImpersonateHelper();

private:
	HRESULT GetDomainName(_bstr_t & strDCname);

private:
	HANDLE m_hToken;
	HRESULT m_errCode;
	bool m_isImp;
};

