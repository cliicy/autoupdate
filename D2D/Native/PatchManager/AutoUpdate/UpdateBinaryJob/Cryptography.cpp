/* molve01: Reviewed*/
#include "stdafx.h"
#include <stdio.h>
#include "Cryptography.h"
#include <Softpub.h>
#include <wintrust.h>
#include "imagehlp.h"
#include "Wincrypt.h"

#pragma comment (lib, "wintrust")
#pragma comment (lib, "imagehlp") 
#pragma comment (lib, "Crypt32")

/////////////////////////////////////////////////////////
/*molve01 reviewed*/
CCryptography::CCryptography(void)
{
}
/*molve01 reviewed*/
CCryptography::~CCryptography(void)
{
}

/*molve01 reviewed*/
BOOL CCryptography::VerifyEmbeddedSignature(LPCWSTR pwszSourceFile)
{
	m_log.LogW(LL_INF, 0, L"%s: Start to Verifying Embedded Signature of file [%s]", __WFUNCTION__, pwszSourceFile );

	LONG lStatus = 0;
	DWORD dwLastError = 0;

	// Initialize the WINTRUST_FILE_INFO structure.
	WINTRUST_FILE_INFO FileData;
	::ZeroMemory(&FileData, sizeof(FileData));
	FileData.cbStruct = sizeof(WINTRUST_FILE_INFO);
	FileData.pcwszFilePath = pwszSourceFile;
	FileData.hFile = NULL;
	FileData.pgKnownSubject = NULL;

	/*
	WVTPolicyGUID specifies the policy to apply on the file
	WINTRUST_ACTION_GENERIC_VERIFY_V2 policy checks:

	1) The certificate used to sign the file chains up to a root 
	certificate located in the trusted root certificate store. This 
	implies that the identity of the publisher has been verified by 
	a certification authority.

	2) In cases where user interface is displayed (which this example
	does not do), WinVerifyTrust will check for whether the  
	end entity certificate is stored in the trusted publisher store,  
	implying that the user trusts content from this publisher.

	3) The end entity certificate has sufficient permission to sign 
	code, as indicated by the presence of a code signing EKU or no 
	EKU.
	*/

	GUID WVTPolicyGUID = WINTRUST_ACTION_GENERIC_VERIFY_V2;
	WINTRUST_DATA WinTrustData;

	// Initialize the WinVerifyTrust input data structure.

	// Default all fields to 0.
	memset(&WinTrustData, 0, sizeof(WinTrustData));

	WinTrustData.cbStruct = sizeof(WinTrustData);

	// Use default code signing EKU.
	WinTrustData.pPolicyCallbackData = NULL;

	// No data to pass to SIP.
	WinTrustData.pSIPClientData = NULL;

	// Disable WVT UI.
	WinTrustData.dwUIChoice = WTD_UI_NONE;

	// No revocation checking.
	WinTrustData.fdwRevocationChecks = WTD_REVOKE_NONE; 

	// Verify an embedded signature on a file.
	WinTrustData.dwUnionChoice = WTD_CHOICE_FILE;

	// Default verification.
	WinTrustData.dwStateAction = 0;

	// Not applicable for default verification of embedded signature.
	WinTrustData.hWVTStateData = NULL;

	// Not used.
	WinTrustData.pwszURLReference = NULL;

	// Default.
	WinTrustData.dwProvFlags = WTD_SAFER_FLAG;

	// This is not applicable if there is no UI because it changes 
	// the UI to accommodate running applications instead of 
	// installing applications.
	WinTrustData.dwUIContext = 0;

	// Set pFile.
	WinTrustData.pFile = &FileData;

	// WinVerifyTrust verifies signatures as specified by the GUID 
	// and Wintrust_Data.
	lStatus = WinVerifyTrust( NULL, &WVTPolicyGUID, &WinTrustData );
	
	m_log.LogW(LL_INF, 0, L"%s: WinVerifyTrust returned [%d]", __WFUNCTION__, lStatus );
	BOOL bRet = FALSE;
	switch (lStatus) 
	{
	case ERROR_SUCCESS:
		/*
		Signed file:
		- Hash that represents the subject is trusted.

		- Trusted publisher without any verification errors.

		- UI was disabled in dwUIChoice. No publisher or 
		time stamp chain errors.

		- UI was enabled in dwUIChoice and the user clicked 
		"Yes" when asked to install and run the signed 
		subject.
		*/
		m_log.LogW(LL_INF, lStatus, L"%s: The file is signed and the signature was verified.", __WFUNCTION__);
		bRet = TRUE;
		WinTrustData.dwStateAction = WTD_STATEACTION_CLOSE;       
		WinVerifyTrust(0, &WVTPolicyGUID, &WinTrustData);
		break;

	case TRUST_E_NOSIGNATURE:
		// The file was not signed or had a signature 
		// that was not valid.

		// Get the reason for no signature.
		dwLastError = GetLastError();
		if (TRUST_E_NOSIGNATURE == dwLastError ||
			TRUST_E_SUBJECT_FORM_UNKNOWN == dwLastError ||
			TRUST_E_PROVIDER_UNKNOWN == dwLastError) 
		{
			// The file was not signed.
			m_log.LogW(LL_INF, lStatus, L"%s: The file is not signed.", __WFUNCTION__);
		} 
		else 
		{
			// The signature was not valid or there was an error 
			// opening the file.
			m_log.LogW(LL_INF, lStatus, L"%s: An unknown error occurred trying to verify the signature of this file.", __WFUNCTION__);
		}
		bRet = FALSE;
		break;

	case TRUST_E_EXPLICIT_DISTRUST:
		// The hash that represents the subject or the publisher 
		// is not allowed by the admin or user.
		m_log.LogW(LL_INF, lStatus, L"%s: The signature is present, but specifically disallowed", __WFUNCTION__);
		bRet = FALSE;           
		break;

	case TRUST_E_SUBJECT_NOT_TRUSTED:
		// The user clicked "No" when asked to install and run.
		m_log.LogW(LL_INF, lStatus, L"%s: The signature is present for file %s but not trusted.", __WFUNCTION__);
		bRet = FALSE;
		break;

	case CRYPT_E_SECURITY_SETTINGS:
		/*
		The hash that represents the subject or the publisher 
		was not explicitly trusted by the admin and the 
		admin policy has disabled user trust. No signature, 
		publisher or time stamp errors.
		*/
		m_log.LogW(LL_INF, lStatus, L"%s: The hash representing the subject or the publisher wasn't explicitly trusted by the admin and admin policy has disabled user trust. No signature, publisher or timestamp errors.", __WFUNCTION__);
		bRet = FALSE;
		break;

	case CERT_E_EXPIRED:
		// The UI was disabled in dwUIChoice or the admin policy 
		// has disabled user trust. lStatus contains the 
		// publisher or time stamp chain error.
		m_log.LogW(LL_INF, lStatus, L"%s: A required certificate is not within its validity period when verifying against the current system clock or the timestamp in the signed file.", __WFUNCTION__);
		bRet = FALSE;
		break;
	default:
		m_log.LogW(LL_INF, lStatus, L"%s: Digital signature validation failed. Please contact Arcserve Technical support.", __WFUNCTION__);
		bRet = FALSE;
		break;
	}

	return bRet;
}
/*molve01 reviewed*/
BOOL CCryptography::IsCertificateOrganizationNameValid(LPCWSTR file)
{
	m_log.LogW(LL_INF, 0, L"%s: Start to verify Certificate Organisation of file %s", __WFUNCTION__, file );
	if(!PATHUTILS::is_file_exist(file))
	{
		m_log.LogW(LL_INF, 0, L"%s: File does not exist.", __WFUNCTION__ );
		return FALSE;
	}
	
	HANDLE dll_handle = CreateFile(file, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL|FILE_FLAG_RANDOM_ACCESS, NULL);
	if (dll_handle == INVALID_HANDLE_VALUE)
	{
		m_log.LogW(LL_INF, GetLastError(), L"%s: Failed to open file", __WFUNCTION__ );
		return FALSE;
	}

	DWORD cert_count;
	if (!ImageEnumerateCertificates(dll_handle, CERT_SECTION_TYPE_ANY, &cert_count, NULL, 0)) 
	{
		m_log.LogW(LL_INF, GetLastError(), L"%s: Failed ImageEnumerateCertificates of this file", __WFUNCTION__ );
		CloseHandle(dll_handle);
		return FALSE;
	}
	// there should only be one certificate associated with this dll, ours
	if (cert_count != 1)
	{
		m_log.LogW(LL_INF, cert_count, L"%s: there should only be one certificate associated with this file", __WFUNCTION__ );
		CloseHandle(dll_handle);
		return FALSE;
	}
	// first get the certificate header to get the proper size for certificate structure
	WIN_CERTIFICATE certHead;
	certHead.dwLength = 0;
	certHead.wRevision = WIN_CERT_REVISION_1_0;

	if (!ImageGetCertificateHeader(dll_handle, 0, &certHead)) {
		// problem getting certificate information, return failure
		m_log.LogW(LL_INF, GetLastError(), L"%s: Failed ImageGetCertificateHeader of this file", __WFUNCTION__ );
		CloseHandle(dll_handle);
		return FALSE;
	}

	// allocate memory for certificate
	DWORD cert_len = certHead.dwLength;
	char* cert = new char[sizeof(WIN_CERTIFICATE) + cert_len];
	if(cert == NULL)
	{
		m_log.LogW(LL_INF, GetLastError(), L"%s: Failed to allocate buffer", __WFUNCTION__ );
		CloseHandle(dll_handle);
		return FALSE;
	}
	WIN_CERTIFICATE *cert_p = (WIN_CERTIFICATE*)cert;
	cert_p->dwLength = cert_len;
	cert_p->wRevision = WIN_CERT_REVISION_1_0;

	if (!ImageGetCertificateData(dll_handle, 0, cert_p, &cert_len)) {
		// problem getting certificate, return failure
		m_log.LogW(LL_INF, GetLastError(), L"%s: Failed ImageGetCertificateData of this file", __WFUNCTION__ );
		CloseHandle(dll_handle);
		delete [] cert;
		cert = NULL;
		return false;
	}
	WORD CertificateType=cert_p->wCertificateType; //WIN_CERT_TYPE_PKCS_SIGNED_DATA 

	// done with file handle, close it
	//
	// extract the certificate used to sign the dll. Since the signature certificate is a PKCS#7 SignedData object,
	// we can verify the signature of the object which will have the side-effect of creating the certificate context
	// of the signing certificate.
	DWORD decodesize = 0;
	PCCERT_CONTEXT pCertContext;
	CRYPT_VERIFY_MESSAGE_PARA vPara;
	memset(&vPara, 0, sizeof(vPara));
	vPara.cbSize = sizeof(vPara);
	vPara.dwMsgAndCertEncodingType = X509_ASN_ENCODING | PKCS_7_ASN_ENCODING;
	//pCertContext=CertCreateCertificateContext(X509_ASN_ENCODING,cert_p->bCertificate,cert_p->dwLength);
	//DWORD encode = pCertContext->dwCertEncodingType;
	if (!CryptVerifyMessageSignature(&vPara, 0, cert_p->bCertificate, cert_p->dwLength, NULL, &decodesize, &pCertContext)) {
		m_log.LogW(LL_INF, GetLastError(), L"%s: Failed CryptVerifyMessageSignature of this file", __WFUNCTION__ );
		delete [] cert;
		cert = NULL;
		CloseHandle(dll_handle);
		return false;
	}

	// If the certificate that signed the dll matches ours, we're in business!
	// Compare the subject name, do not compare the entire certificate or the public key as these will change each
	// year as the certificates are renewed.
	DWORD subjectSize = CertGetNameString(pCertContext, CERT_NAME_SIMPLE_DISPLAY_TYPE, 0, NULL, NULL, 0);

	WCHAR * subjectName = (WCHAR *)malloc(sizeof(WCHAR) * subjectSize);
	if(subjectName == NULL)
	{
		m_log.LogW(LL_INF, GetLastError(), L"%s: Failed to allocate buffer 2", __WFUNCTION__ );
		delete [] cert;
		cert = NULL;
		CloseHandle(dll_handle);
		return FALSE;
	}
	CertGetNameString(pCertContext, CERT_NAME_SIMPLE_DISPLAY_TYPE, 0, NULL, subjectName, subjectSize);
	BOOL bValidSignature = TRUE;
	wstring strSig = UPUTILS::GetUpdateSignature(); // signature can be configured in updatecfg.ini
	if( !strSig.empty() )
	{
		if(wcscmp(subjectName, strSig.c_str())!=0)
			bValidSignature = FALSE;
	}
	else
	{
		if( wcscmp(subjectName, L"CA")!=0 && wcscmp(subjectName, L"CA, Inc.")!=0 && wcscmp(subjectName, L"Arcserve (USA) LLC")!=0) 
			bValidSignature = FALSE;
	}
	if( !bValidSignature )
	{
		m_log.LogW(LL_INF, GetLastError(), L"%s: Signature '%s' is invalid.", __WFUNCTION__, subjectName );
		free(subjectName);
		subjectName = NULL;
		delete [] cert;
		cert = NULL;
		CloseHandle(dll_handle);
		return false;
	}


	CloseHandle(dll_handle);
	dll_handle = NULL;
	// clean up
	free(subjectName);
	subjectName = NULL;
	delete [] cert;
	cert = NULL;
	CertFreeCertificateContext(pCertContext);
	return true;
}
