/* molve01: Reviewed*/
#pragma once
#include <wincrypt.h>
#include "DbgLog.h"

class CCryptography
{
public:
	CCryptography(void);
public:
	virtual ~CCryptography(void);
public:
	BOOL VerifyEmbeddedSignature(LPCWSTR pwszSourceFile);
	BOOL IsCertificateOrganizationNameValid( LPCWSTR file );
protected:
	CDbgLog m_log;
};
