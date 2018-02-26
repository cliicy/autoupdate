#ifndef _CSLICAPI_H
#define _CSLICAPI_H

#ifdef __cplusplus		   	
  extern "C" {                     /* avoid name-mangling if used from C++ */
#endif /* __cplusplus */


#define CSLIC_PT_OK             0
#define CSLIC_INVALID_KEYLEN    1
#define CSLIC_INVALID_KEY       2
#define CSLIC_ERR_BAD_PARAM     3
#define CSLIC_ERR_FAILED        4

UINT WINAPI CSLicenseDecryptKey(LPSTR key, LPSTR serialNumber, long *userLevel,
                  long *licenseVersion, long *productCode, long *oemCode);

UINT WINAPI CSLicenseEncryptKey	(UCHAR    *key,
								UCHAR    *serialNumber,
								ULONG  uluserLevel,
								ULONG  ullicenseVersion,
								ULONG  ulproductCode,
								ULONG  uloemCode);


#ifdef __cplusplus
  }
#endif /* __cplusplus */

#endif //_ASVCTL_H
