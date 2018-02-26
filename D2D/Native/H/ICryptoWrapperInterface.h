#pragma once
#include "CryptoDefine.h"
#include <Windows.h>
#include <WinCrypt.h>
#include <string>
#include <vector>
using namespace std;

#ifndef CRYPTOWRAPPERDLL_API
#ifdef CRYPTOWRAPPERDLL_EXPORTS
#define CRYPTOWRAPPERDLL_API __declspec(dllexport)
#else
#define CRYPTOWRAPPERDLL_API __declspec(dllimport)
#endif
#endif 

typedef enum _eEncLibType
{
    ELT_NOENCRYTION = 0,   //ZZ: Not encryption.
    ELT_MSCRYPTO,          //ZZ: (Default) Encryption using library provided by MS. CAPI or CNG.
    ELT_ETPKI              //ZZ: Encryption using library provided by CA. Not supported now.
}E_ENCLIB_TYPE;

typedef enum _eEncAlgorithmType
{
    EEAT_UNKNOWN = 0,      //ZZ: Unknown algorithm, Not encryption.
    EEAT_AES_128BIT,       //ZZ: (Default) AES-128, Fips only require key size 128, 192 and 256 of AES,
    EEAT_AES_192BIT,       //ZZ: AES-192
    EEAT_AES_256BIT,       //ZZ: AES-256
    EEAT_3DES,             //ZZ: Triple-DES. Not supported now.
    EEAT_RSA,              //ZZ: Asymmetric encryption algorithm- RSA
    EEAT_RSA_SIGN          //ZZ: Asymmetric signature algorithm.
}E_ENCALG_TYPE;

typedef enum _eKeyBLOBType
{
    EKBT_UNKNOWN = 0,         //ZZ: Unknown key BLOB type
    EKBT_PLAIN_SESSION_KEY,   //ZZ: Key BLOB exported from symmetric key in plain text.
    EKBT_PUBLIC_KEY,          //ZZ: Public key BLOB exported from asymmetric key
    EKBT_PRIVATE_KEY,         //ZZ: Private key BLOB exported from asymmetric key
	EKBT_PUBLIC_KEY_X509,     //ZZ: Public key BLOB in X509 format exported from asymmetric key
	EKBT_PRIVATE_KEY_X509,    //ZZ: Private key BLOB in X509 format exported from asymmetric key
	EKBT_PUBLIC_KEY_X509_PEM, //ZZ: Public key BLOB in X509 format exported from asymmetric key
	EKBT_PRIVATE_KEY_X509_PEM //ZZ: Private key BLOB in X509 format exported from asymmetric key
}E_KEYBLOB_TYPE;

typedef enum _eCryptoFlags
{
    ECF_NO_FLAGS = 0,                  //ZZ: No special flags specified. Default behavior will be done.
    ECF_INTEROP_LEGACY = 0x00000001,   //ZZ: For some asymmetric algorithm, data encrypted by CNG or CAPIKI is big endian, while CAPI is little endian. need reverse data before decryption.
    ECF_PADDING_DATA = 0x00000002,     //ZZ: CBC encryption algorithm, such as ARS, request data is aligned by block size, Without this flags caller need make sure this requirement,
    ECF_STORE_RESULT = 0x00000004,     //ZZ: If the encrypt/decrypt result should be stored internally
    ECF_PWD_BY_FIXED_STR = 0x00000008, //ZZ: Encrypt/decrypt data using input password appended by fixed string predefined.
    ECF_PWD_BY_OS_INFO = 0x00000010,   //ZZ: Encrypt/decrypt data using input password appended by operation system information.
    ECF_PWD_CALC_BY_HASH = 0x00000020  //ZZ: Encrypt/decrypt data using input password appended by some information after hash.
}E_CRYPTO_FLAGS;

typedef enum _eHashAlgorithmType
{
    EHAT_UNKNOWN = 0,      //ZZ: Unknown hash algorithm.
    EHAT_MD5,              //ZZ: (Default) MD5 algorithm.
    EHAT_SHA1,             //ZZ: SHA-1 algorithm (160 bits).
    EHAT_SHA_256,          //ZZ: Belong to SHA-2 Supported on XP-SP3 and later  
    EHAT_SHA_384,          //ZZ: Belong to SHA-2 Supported on XP-SP3 and later  
    EHAT_SHA_512           //ZZ: Belong to SHA-2 Supported on XP-SP3 and later
}E_HASHALG_TYPE;

typedef enum _eCryptoAPIType
{
    ECAT_UNKNOWN = 0,      //ZZ: Unknown API type. Should not exist.
    ECAT_BY_OS,            //ZZ: (Default) Decided by OS version. Using CNG on win7 and w2k8 r2, while using CAPI on others.
    ECAT_CAPI,             //ZZ: Cryptography API to encrypt/decrypt.
    ECAT_CNG,              //ZZ: Next Generation Cryptography API(CNG) to encrypt/decrypt.
    ECAT_CNG_NCRYPT,       //ZZ: Use ncrypt function for asymmetric algorithm, such as RSA. This default type for RSA in CNG.
	ECAT_CAPKI_RSA         //ZZ: Use capki function for asymmetric algorithm, such as RSA. This default type for RSA in CAPKI.
}E_CRYPTOAPI_TYPE;

typedef enum _eIVUsageStatus
{
    EIU_UNKNOWN = 0,        //ZZ: Unknown status. Should not exist.
    EIU_NOT_USE,            //ZZ: (Default) Do not use initialization vector.
    EIU_USE_IV              //ZZ: Use default initialization vector.
}E_IV_USAGE;

typedef enum _eInterfaceType
{
    EIT_UNKNOWN = 0,        //ZZ: Unknown type. Should not exist.
    EIT_CRYPTO = 0x01,      //ZZ: Create instance for encryption. Only with this flag, instance can be used for encryption
    EIT_HASH = 0x02,        //ZZ: Create hash instance. Only with flag, initialization for cryptography will not be performed.
    EIT_CRYPRO_HASH = (EIT_CRYPTO | EIT_HASH) //ZZ: (Default) Instance can be used for enctyption and hash.
}E_INTER_TYPE;

typedef enum _eStrOption
{
    ESEO_BASE64               = CRYPT_STRING_BASE64,
    ESEO_BASE64_HEADER        = CRYPT_STRING_BASE64HEADER,
    ESEO_BASE64_REQUESTHEADER = CRYPT_STRING_BASE64REQUESTHEADER,
    ESEO_HEX                  = CRYPT_STRING_HEX,    //ZZ: Convert hex bin to string and start new line when a multiple of 16 bytes.
    ESEO_HEX_ASCII            = CRYPT_STRING_HEXASCII,
    ESEO_HEX_ADDR             = CRYPT_STRING_HEXADDR,
    ESEO_HEX_ASCII_ADDR       = CRYPT_STRING_HEXASCIIADDR,
    ESEO_HEX_RAW              = CRYPT_STRING_HEXRAW, //ZZ: Ignored on w2k3 and xp. Can be bitwise or with other option.
    ESEO_STR_NOCRLF           = CRYPT_STRING_NOCRLF, //ZZ: Ignored on w2k3 and xp. Can be bitwise or with other option.
    ESEO_STR_NOCR             = CRYPT_STRING_NOCR,
    ESEO_BASE64_ANY           = CRYPT_STRING_BASE64_ANY, //ZZ: Try such order: CRYPT_STRING_BASE64HEADER, CRYPT_STRING_BASE64
    ESEO_HEX_ANY              = CRYPT_STRING_HEX_ANY //ZZ: Try such order: CRYPT_STRING_HEXADDR, CRYPT_STRING_HEXASCIIADDR, CRYPT_STRING_HEX, CRYPT_STRING_HEXRAW, CRYPT_STRING_HEXASCII  
}E_STR_OPT;

typedef enum _eQueryKeySetCtrl
{
    EQKC_ALL = 0,
    EQKC_D2D_ONLY,
    EQKC_D2D_EXCLUDED
}E_QUERY_KEYSET_CTRL;

typedef enum _eCSPTYPE
{
    EST_UNKNOWN = 0,
    EST_BASE_CSP,
    EST_EXTENDED_CSP
}E_CSP_TYPE;

typedef enum _eKeyDataType
{
    EKDT_KEY_FILE = 0,
    EKDT_KEY_FILE_HDR,
    EKDT_PUBLIC_KEY,
    EKDT_PRIVATE_KEY,
	EKDT_BASIC_INFO,
    EKDT_EXTENDED_INFO
}E_KEY_DATA_TYPE;

typedef enum 
{
    ESKFC_NONE = 0,
    ESKFC_FORCE_SAVE_WHEN_INSTANCE_RELEASE = 0x00000001,
    ESKFC_UPGRADE_TO_CURRENT_VERSION = 0x00000002,
    ESKFC_REPLACE_WHEN_EXIST = 0x00000004,
    ESKFC_RENAME_TO_ORIG_WHEN_EXIST = 0x00000008,
    ESKFC_RENAME_NEW_X509_WHEN_EXIST = 0x00000010
}E_SAVE_KEY_FILE_CTRL;

#define CRYPTOWRAP_SIGNATURE_KEY_FILE_HEADER               0x59454B5A
#define CRYPTOWRAP_SIGNATURE_PUBLIC_KEY_HEADER             0x4B55505A
#define CRYPTOWRAP_SIGNATURE_PRIVATE_KEY_HEADER            0x4B52505A
#define CRYPTOWRAP_SIGNATURE_ENC_DATA_HEADER               0x5441445A
#define CRYPTOWRAP_SIGNATURE_BASIC_INFO_HEADER             0x4B58455A
#define CRYPTOWRAP_SIGNATURE_EXTENED_INFO_HEADER           0x4958455A
#define CRYPTOWRAP_SIGNATURE_EXTENED_INFO_SYS_INFO         0x5359535A

#define MAX_CRYPTOWRAP_KEY_HEADER_SIZE_IN_BYTES            128
#define CRYPTOWRAP_EX_INFO_SIZE(ExInfoHdr)                 (FIELD_OFFSET(ST_EX_SUB_HDR, pbExInfoPtr) + (ExInfoHdr)->unFixedInfo.stFixedInfo.stSubHeader.dwDataSize)
#define CRYPTOWRAP_EX_INFO_RAW_SIZE(ExInfoHdr)             (FIELD_OFFSET(ST_EX_SUB_HDR, pbExInfoPtr) + (ExInfoHdr)->unFixedInfo.stFixedInfo.dwRawDataSize)

#define CRYPTOWRAP_EX_SYS_INFO_VERSION_TUNGSTEN            0x00010000

const DWORD g_dwKeyBLOBMagicNumber = 0x4259454B; //ZZ: ¡®BYEK¡¯ 0x4B455942
const WORD g_wPlainKeyBLOBVersion = 0x00001; //ZZ: 0x0001

typedef struct _stEncKeySubHeader
{
	DWORD dwSIgnature;
	DWORD dwHdrCheckSum;
	DWORD dwDataCheckSum;
	DWORD dwDataSize;
}ST_ENC_KEY_SUB_HDR, *PST_ENC_KEY_SUB_HDR;

typedef struct _stExSubHeader
{
	union _unFixedInfo
	{
		struct _stFixedInfo
		{
			ST_ENC_KEY_SUB_HDR stSubHeader;
			ULONGLONG          ullCreateTime;
			DWORD              dwInfoSignature;
			DWORD              dwInfoEncode;
			DWORD              dwRawDataSize;        //ZZ: Data size before being encrypted.
		} stFixedInfo;
		BYTE pbReserved[MAX_CRYPTOWRAP_KEY_HEADER_SIZE_IN_BYTES];
	} unFixedInfo;

	BYTE pbExInfoPtr[1];
}ST_EX_SUB_HDR, *PST_EX_SUB_HDR;

typedef struct _stSysInfo
{
	DWORD dwSysInfoVersion;
	DWORD dwCryptoWrapVersion;
	DWORD dwCryptoWrapBuild;
	DWORD dwHdrCheckSum;
	DWORD dwDataCheckSum;
	DWORD dwOSVersion;
	DWORD dwOSBuild;
	DWORD dwOSInstallTime;
	DWORD dwBootVolumeClusterSize;
	DWORD dwBootVolumeSectorSize;
	DWORD dwExSysInfoSize;
	BYTE  pbExSysInfoBuf[1];
}ST_SYS_INFO, *PST_SYS_INFO;

typedef struct _stPlainKeyData
{
    union
    {
        BYTE pbKeyBLOBHdr[8];
        BLOBHEADER stCAPIBLOBHdr;
        struct { DWORD dwMagic; DWORD dwVersion; } stCNGKeyBLOBHdr;
    };

    ULONG dwKeyBlobSize;
    BYTE pbKeyBLOB[1];
}ST_PLAIN_KEY_DATA, *PST_PLAIN_KEY_DATA;

typedef struct _stPlainKeyBLOB
{
    DWORD dwMagicNumber;  //ZZ: g_dwKeyBLOBMagicNumber
    WORD wVersion;        //ZZ: g_wPlainKeyBLOBVersion
    BYTE cEncLibType;     //ZZ: Possible values defined in E_ENCLIB_TYPE.
    BYTE cHashAlgType;    //ZZ: Possible values defined in E_HASHALG_TYPE.
    BYTE cEncAPIType;     //ZZ: Possible values defined in E_CRYPTOAPI_TYPE.
    BYTE cEncAlgType;     //ZZ: Possible values defined in E_ENCALG_TYPE.
    BYTE cInterfaceType;  //ZZ: Possible values defined in E_INTER_TYPE.
    BYTE cIVUsage;        //ZZ: Possible values defined in E_IV_USAGE.
    ST_PLAIN_KEY_DATA stKeyData;
}ST_PLAIN_KEY_BLOB, *PST_PLAIN_KEY_BLOB; 

class CCryptoParam
{
public:
    CCryptoParam() 
        : m_dwHashAlgType(EHAT_UNKNOWN),
          m_dwEncAlgType(EEAT_UNKNOWN),
          m_dwEncLibType(ELT_NOENCRYTION),
          m_dwEncAPIType(ECAT_UNKNOWN),
          m_dwInterfaceType(EIT_CRYPRO_HASH),
          m_dwIVUsage(EIU_UNKNOWN)
    {
    }

    void Initialize()
    {
        m_dwHashAlgType = EHAT_UNKNOWN;
        m_dwEncAlgType = EEAT_UNKNOWN;
        m_dwEncLibType = ELT_NOENCRYTION;
        m_dwEncAPIType = ECAT_UNKNOWN;
        m_dwInterfaceType = EIT_CRYPRO_HASH;
        m_dwIVUsage = EIU_UNKNOWN;
    }

    bool operator == (const CCryptoParam& obj)
    {
        if((m_dwHashAlgType == obj.m_dwHashAlgType) &&
           (m_dwEncAlgType == obj.m_dwEncAlgType) && 
           (m_dwEncLibType == obj.m_dwEncLibType) &&
           (m_dwEncAPIType == obj.m_dwEncAPIType) &&
           (m_wsPassword == obj.m_wsPassword) &&
           (m_dwInterfaceType == obj.m_dwInterfaceType) &&
           (m_dwIVUsage == obj.m_dwIVUsage))
           return true;
        return false;
    }

    DWORD   m_dwEncLibType;     //ZZ: Possible values defined in E_ENCLIB_TYPE.
    DWORD   m_dwHashAlgType;    //ZZ: Possible values defined in E_HASHALG_TYPE.
    DWORD   m_dwEncAPIType;     //ZZ: Possible values defined in E_CRYPTOAPI_TYPE.
    DWORD   m_dwEncAlgType;     //ZZ: Possible values defined in E_ENCALG_TYPE.
    DWORD   m_dwInterfaceType;  //ZZ: Possible values defined in E_INTER_TYPE.
    DWORD   m_dwIVUsage;        //ZZ: Possible values defined in E_IV_USAGE.
    wstring m_wsPassword;       //ZZ: Password string used for generating encryption key. Can be empty.
};

class CRYPTOWRAPPERDLL_API CByteBuf
{
public:
    CByteBuf(DWORD dwBufSize = 0, PBYTE pbDataBuf = NULL) : m_pByteBuf(NULL), m_dwBufSize(0), m_dwBufAllocSize(0) { AllocMem(dwBufSize, pbDataBuf); }
    CByteBuf(const CByteBuf& ByteBuf) : m_pByteBuf(NULL), m_dwBufSize(0), m_dwBufAllocSize(0) { *this = ByteBuf; }
    ~CByteBuf() { AllocMem(0); }

    CByteBuf& operator = (const CByteBuf& ByteBuf)
    {
        if(this != &ByteBuf)
        {
            AllocMem(ByteBuf.m_dwBufSize);
            memcpy(m_pByteBuf, ByteBuf.m_pByteBuf, m_dwBufSize);
        }
        return *this;
    }

    bool IsValid() { return (m_pByteBuf && (0 != m_dwBufSize)); }

    void AllocMem(DWORD dwBufSize, PBYTE pbDataBuf = NULL)
    {
        if(0 != dwBufSize)
        {
            if((NULL == m_pByteBuf) || (dwBufSize > m_dwBufAllocSize))
            {
                PBYTE pbTemp = new BYTE[dwBufSize];
                if(pbTemp)
                {
                    if(m_pByteBuf)
                        delete []m_pByteBuf;
                    m_pByteBuf = pbTemp;
                    m_dwBufAllocSize = dwBufSize;
                }
            }            

            if (m_pByteBuf)
            {
                m_dwBufSize = dwBufSize;
                memset(m_pByteBuf, 0, m_dwBufSize);
                if (pbDataBuf)
                    memcpy(m_pByteBuf, pbDataBuf, m_dwBufSize);
            }
        }
        else if(m_pByteBuf)
        {
            delete []m_pByteBuf;
            m_pByteBuf = NULL;
            m_dwBufSize = 0;
            m_dwBufAllocSize = 0;
        }
    }

    BYTE* m_pByteBuf;
    DWORD m_dwBufSize;
    DWORD m_dwBufAllocSize;
};

class ICryptoWrapperInterface
{
public:
    /**
     * Release current instance.
     */
    virtual void Release() = 0;

    /**
     * Virtual destructor to make sure decried class release correctly.
     */
    virtual ~ICryptoWrapperInterface() {}

    /**
     * Get the instance or reference count for this kind of crypto instance.
     */
    virtual long GetInstanceCount(OUT DWORD& dwInstRefCount) = 0;

    /**
     * Each instance will be assigned a GUID for identity. This API will return a GUID string.
     * If the pguidInstance is set, the FUID object will be returned.
     */
    virtual const WCHAR* InstanceGUID(GUID* pguidInstance = NULL) = 0;

    /**
     * Return the crypto API used actually for implement. 
     */
    virtual E_CRYPTOAPI_TYPE CryptoAPIType() = 0;

    /**
     * Get parameter used for current encryption instance.
     * @Return return a copy of current parameter used for this instance.
     */
    virtual CCryptoParam CryptoParam() const = 0;

    /**
     * Initialize encryption instance, including create object and create encryption key(if required).
     * @Param  pCryptoParam  Specify parameters focus on, others will be filled using default value. If it is NUll,
     *                       all parameters will be used as default value.
     * @Param  pKeyBLOBBuf   Specify key BLOB buffer. If this parameter is NUll, a new encryption key will be created,
     * @Param  dwKeyBLOBSize Specify key BLOB buffer size. Only valid when pKeyBLOBBuf is specified,
     * @Param  eKeyBLOBType  Specify key BLOB type,
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long Initialize(
        IN     const CCryptoParam* pCryptoParam,
        IN OPT BYTE*               pKeyBLOBBuf = NULL,
        IN OPT DWORD               dwKeyBLOBSize = 0,
        IN OPT E_KEYBLOB_TYPE      eKeyBLOBType = EKBT_UNKNOWN
        ) = 0;

    /**
     * Get block size of specified algorithm.
     * @Param  dwBlockSize Return block size of encryption algorithm.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long GetAlgorithmBlockSize(OUT DWORD& dwBlockSize) = 0;

    /**
     * Encrypt data using algorithm specified when initialization.
     * This function will be failed with D2DCRYPTO_E_MORE_DATA and return required data buffer size when
     * the buffer size for encryption is not large enough. Caller must make sure data size to be encrypted.
     * is a multiple of block size, otherwise encryption will be failed.
     * @Param  pPlainbataBuf       Input data buffer to be encrypted, Must NOT be NULL. 
     * @Param  dwPlainDataBufLen   Input data buffer size(in byte) to be encrypted. Must NOT be zero.
     * @Param  pbCipherDataBuf     Encrypted data buffer. If equal to NULL, pdwCipherDataBufLen stores required size.
     * @Param  pdwCipherDataBufLen Must NOT be NULL, Input: encrypted data buffer size(in byte), if not large enough 
     *                             return required size; Output: required buffer size or process data size actually.   
     * @Param  bPadding            [true] means allow algorithm to append padding for input data, will cause encrypted 
     *                             data large than original data. [false] means not allow algorithm to append padding. 
     *                             Caller should make sure original data size is a multiple of block size.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     * @Remark For RSA algorithm, the maximum input data size is limited by algorithm key size, which equal to key 
     *         size in bytes if no padding specified, while equal to key size minus 11 if padding by BCRYPT_PAD_PKCS1¡£
     *         If input data size is greater than maximum size or size is not equal to key size if without padding
     *         this API will return NTE_INVALID_PARAMETER(0x80090027).
     *         There are 2 methods to encrypt huge size greater than maximum size.
     *         1. Encrypt data using a random symmetric key, and encrypt symmetric key by RSA.
     *         2. Split data into multiple piece with limited maximum size and encrypt each one by RSA and combine results.
     */
    virtual long EncryptData(
        IN     const BYTE* pPlainbataBuf,
        IN     DWORD       dwPlainDataBufLen,
        OUT    BYTE*       pbCipherDataBuf,
        IN OUT DWORD*      pdwCipherDataBufLen,
        IN OPT bool        bPadding = false,
        IN OPT DWORD       dwCryptoFlags = ECF_NO_FLAGS
        ) = 0;

    /**
     * Decrypt data using algorithm specified when initialization.
     * This function will be failed with D2DCRYPTO_E_MORE_DATA and return required data buffer size when
     * the buffer size for decryption is not large enough. Caller must make sure data size to be encrypted.
     * is a multiple of block size, otherwise encryption will be failed.
     * @Param  pbCipherDataBuf     Input data buffer to be decrypted, Must NOT be NULL. 
     * @Param  dwCipherDataBufLen  Input data buffer size(in byte) to be decrypted. Must NOT be zero.
     * @Param  pPlainbataBuf       Decrypted data buffer. If equal to NULL, pdwPlainDataBufLen stores required size.
     * @Param  pdwPlainDataBufLen  Must NOT be NULL, Input: decrypted data buffer size(in byte), if not large enough 
     *                             return required size; Output: required buffer size or process data size actually.   
     * @Param  bPadding            [true] means algorithm will trim data as padding format(PKCS#5). This may cause
     *                             unexpected loss of data if algorithm doesn't append padding. [false] means not 
     *                             allow algorithm to trim padding. 
     ^ @Param  bInterOPData        To interoperate encrypted data by RSA between CNG and CAPI, we should reverse data byte order
     *                             because this 2 algorithms use opposite byte order.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long DecryptData(
        IN     const BYTE* pbCipherDataBuf,
        IN     DWORD       dwCipherDataBufLen, 
        OUT    BYTE*       pPlainbataBuf, 
        IN OUT DWORD*      pdwPlainDataBufLen,
        IN OPT bool        bPadding = false,
        IN OPT DWORD       dwCryptoFlags = ECF_NO_FLAGS
        ) = 0;

    /**
     * Get size of hash value.
     * @Param  dwHashSize       Return size of hash value.
     * @Param  dwHashAlgType    Reserved. Modify it only when you exactly know if CSP created support it.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long GetHashValueSize(
        OUT DWORD&      dwHashSize,
        IN OPT DWORD    dwHashAlgType = EHAT_UNKNOWN
        ) = 0;

    /**
     * Get hash value for input data buffer.
     * @Param  pPlainbataBuf      Input data buffer to be calculated for hash, Must NOT be NULL. 
     * @Param  dwPlainDataBufLen  Input data buffer size(in byte) .Must NOT be zero.
     * @Param  pHashbataBuf       Data buffer for hash value. If equal to NULL, pdwHashDataBufLen stores required size.
     * @Param  pdwHashDataBufLen  Must NOT be NULL, Input: hash value buffer size(in byte), if not large enough return 
     * @Param  dwHashAlgType      Reserved. Modify it only when you exactly know if CSP created support it.
     *                            required size; Output: required buffer size or process data size actually.   
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long GetHashData(
        IN     const BYTE* pPlainbataBuf,
        IN     DWORD       dwPlainDataBufLen, 
        OUT    BYTE*       pHashbataBuf,
        IN OUT DWORD*      pdwHashDataBufLen,
        IN OPT DWORD       dwHashAlgType = EHAT_UNKNOWN
        ) = 0;

    /**
     * Get hash value for input data buffer.
     * The hash value will be convert to a string being composed of HEX characters. 
     * @Param  pPlainbataBuf      Input data buffer to be calculated for hash, Must NOT be NULL. 
     * @Param  dwPlainDataBufLen  Input data buffer size(in byte) .Must NOT be zero.
     * @Param  wsHashStr          Store hash value string.
     * @Param  dwHashAlgType      Reserved. Modify it only when you exactly know if CSP created support it.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long GetHashData(
        IN  const BYTE* pPlainbataBuf,
        IN  DWORD       dwPlainDataBufLen, 
        OUT wstring&    wsHashStr,
        IN OPT DWORD    dwHashAlgType = EHAT_UNKNOWN
        ) = 0;

    /**
     * Get key BLOB depend on required type.
     * @Param  pKeyBLOBBuf       A buffer to receive key BLOB.
     * @Param  pdwKeyBLOBBufSize Key BLOB buffer size. If this size is not enough or pKeyBLOBBuf is empty,
     *                           this parameter will receive required buffer size.
     * @Param  dwKeyBLOBType     Key BLOB type. Refer to E_KEYBLOB_TYPE
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long GetKeyBLOB(
        IN     BYTE* pKeyBLOBBuf, 
        IN OUT DWORD* pdwKeyBLOBBufSize, 
        IN     DWORD dwKeyBLOBType) = 0;

    /**
     * Get key BLOB depend on required type.
     * @Param  wsKeyBLOBStr      A string buffer to receive key BLOB after encoding by base 64.
     * @Param  dwKeyBLOBType     Key BLOB type. Refer to E_KEYBLOB_TYPE
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long GetKeyBLOB(
        IN wstring& wsKeyBLOBStr, 
        IN DWORD dwKeyBLOBType) = 0;
};

class IKeyFileMgrInterface
{
public:
    /**
     * Release this instance.
     */
    virtual void Release() = 0;

    /**
     * Virtual destructor to make sure decried class release correctly.
     */
    virtual ~IKeyFileMgrInterface() {}

    /**
     * Generate asymmetric key for following calling
     * @Param  pwzKeyFilePWD     The password used to encrypt private key into key file. If this value is empty, private is plain text.
     * @Param  pwzKeyFilePath    Specify full path of key file. If this value is empty, the path specified when create instance will be used.
     * @Param  pCryptoParam      Specify a crypto configuration. This parameter should be ignored currently.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long CreateCryptokey(
        IN OPT const WCHAR*  pwzKeyFilePWD = NULL, 
        IN OPT const WCHAR*  pwzKeyFilePath = NULL, 
        IN OPT CCryptoParam* pCryptoParam = NULL
        ) = 0;

    /**
     * Initialize crypto key using key data store in key file. 
     * @Param  pwzKeyFilePWD  The password used to decrypt private key from key file. If this value is empty and key file is encrypted, this API return error.
     * @Param  pwzKeyFilePath Specify full path of key file. If this value is empty, the path specified when create instance will be used.
     * @Param  bLoadKeyFile   If load key file again in this function. If this is false, you should make sure LoadKeyFile has been called.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long AttachCryptoKey(
        IN OPT const WCHAR* pwzKeyFilePWD = NULL, 
        IN OPT const WCHAR* pwzKeyFilePath = NULL,
        IN OPT bool         bLoadKeyFile = false
        ) = 0;

    /**
     * Initialize crypto key using key data store in key file. 
     * @Param  pwzKeyFilePWD  The password used to decrypt private key from key file. If this value is empty and key file is encrypted, this API return error.
     * @Param  pbKeyFileDataBuf     Key file data to be attached.
     * @Param  dwKeyFileDataBufSize Size of key file data to be attached
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long LoadCryptoKey(
        IN OPT const WCHAR* pwzKeyFilePWD = NULL,
        IN OPT const PBYTE  pbKeyFileDataBuf = NULL,
        IN OPT DWORD        dwKeyFileDataBufSize = 0
        ) = 0;

    /**
     * Encrypt data using asymmetric key in key file. This function will be failed with D2DCRYPTO_E_MORE_DATA and return required data buffer size
     * when the buffer size for encryption is not large enough. 
     * @Param  pbBuffer2Enc    Input data buffer to be encrypted, Must NOT be NULL. 
     * @Param  dwBuf2EncSize   Input data buffer size(in byte) to be encrypted. Must NOT be zero. Currently this size is limited to 256 bytes when ECF_PADDING_DATA
     *                         is not specified in dwFlags, while 245 when ECF_PADDING_DATA is specified in dwFlags because RSA key size is set as 2048 bits
     * @Param  pbEncDataBuf    Encrypted data buffer. If equal to NULL, pdwEncDataSize stores required size. Can be ignored
     * @Param  pdwEncDataSize  Encrypted data buffer size(in byte), if not large enough it receives required size. Can be ignored
     * @Param  pwsEncDataStr   Specify a string to receive encrypt data in base64 format. Can be ignored
     * @Param  dwFlags         Some flags combination, such as if data is padding, Refer to E_CRYPTO_FLAGS, If ECF_STORE_RESULT is specified, 
     *                         result will be saved into key file. If ECF_PADDING_DATA is not specified, caller should make sure input buffer size is 
     *                         a multiple of algorithm key size
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long EncryptData(        
        IN      PBYTE    pbBuffer2Enc,
        IN      DWORD    dwBuf2EncSize,
        OUT OPT PBYTE    pbEncDataBuf = NULL,
        OUT OPT DWORD*   pdwEncDataSize = NULL,
        OUT OPT wstring* pwsEncDataStr = NULL,
        IN OPT  DWORD    dwFlags = ECF_PADDING_DATA | ECF_STORE_RESULT
        ) = 0;

    /**
     * Decrypt data using asymmetric key in key file. This function will be failed with D2DCRYPTO_E_MORE_DATA and return required data buffer size. 
     * When there is no input data buffer or string, this API will decrypt data saved in key file.
     * when the buffer size for decryption is not large enough.
     * @Param  pbBuffer2Dec    Input data buffer to be decrypted. 
     * @Param  dwBuf2DecSize   Input data buffer size(in byte) to be decrypted. 
     * @Param  pwzDataStr2Dec  Input a encrypted data string in base64 format to decrypt. 
     * @Param  pbDecDataBuf    Decrypted data buffer. If equal to NULL, pdwEncDataSize stores required size.
     * @Param  pdwDecDataSize  Decrypted data buffer size(in byte), if not large enough it receives required size.
     * @Param  dwFlags         Some flags combination, such as if data is padding, Refer to E_CRYPTO_FLAGS, If ECF_STORE_RESULT is specified, 
     *                         result will be saved into key file. If ECF_PADDING_DATA is not specified, caller should make sure input buffer size is 
     *                         a multiple of algorithm key size
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long DecryptData(        
        IN OPT PBYTE        pbBuffer2Dec,
        IN OPT DWORD        dwBuf2DecSize,
        IN OPT const WCHAR* pwzDataStr2Dec,
        OUT    PBYTE        pbDecDataBuf,
        OUT    DWORD*       pdwDecDataSize,
        IN OPT DWORD        dwFlags = ECF_PADDING_DATA
        ) = 0;

    /**
     * Load specified Key file. 
     * @Param  pwzKeyFilePath  Specify full path of key file. When this parameter is not used or empty. path specified when create instance will be used.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long LoadKeyFile(
        IN OPT const WCHAR* pwzKeyFilePath = NULL
        ) = 0;

    /**
     * Load specified Key file data buffer. 
     * @Param  pbKeyDataBuf     Specify key file data buffer.
     * @Param  dwKeyDataBufSize Specify size of key file data buffer.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long LoadKeyFileData(
        IN const PBYTE pbKeyDataBuf,
        IN DWORD       dwKeyDataBufSize
        ) = 0;

    /**
     * Specify a new password to encrypt key file. If the key file is not encrypted originally, it will be encrypt using pwzNewPassword.
     * @Param  pwzNewPassword  Specify new password to encrypt key file. If this parameter is empty or NULL, key file will be plain text.
     * @Param  pwzOldPassword  Specify original password of key file. If this parameter is NULL or empty, password specified when create/attach key
     *                         will be used if the key file has been encrypted.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long SetKeyFilePWD(
        IN     const WCHAR* pwzNewPassword, 
        IN OPT const WCHAR* pwzOldPassword = NULL
        ) = 0;

    /**
     * Save key information to specified file.
     * @Param  pwzKeyFilePath  Specify new password to encrypt key file. If this parameter is empty or NULL, key file will be plain text.
     * @Param  dwSaveCtrl      Specify save control option. Refer to E_SAVE_KEY_FILE_CTRL.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long SaveKeyFile(
        IN OPT const WCHAR* pwzKeyFilePath = NULL,
        IN OPT DWORD dwSaveCtrl = ESKFC_REPLACE_WHEN_EXIST
        ) = 0;

    /**
     * Export key file data to buffer
     * @Param  pbKeyDataBuf      Specify key file data buffer, It can be ignored to receive required buffer size.
     * @Param  dwKeyDataBufSize  Specify size of file data buffer. If this size is not large enough or pbKeyDataBuf is NULL, it receive required size.
     * @Param  dwKeyDataType     Specify type of data to be exported. And this value can also be used to specify some signature to dump.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    virtual long ExportKeyFileData(
        IN const PBYTE      pbKeyDataBuf,
        IN DWORD&           dwKeyDataBufSize,
        IN DWORD            dwKeyDataType = EKDT_KEY_FILE,
		IN OPT const WCHAR* pwzKeyFilePWD = NULL
        ) = 0;

	/**
	* Add extended information to key file
	* @Param  dwInfoSignature  Specify extended information signature. This is can be caller-defined value.
	* @Param  pbExInfoBuf      Specify extended information buffer
	* @Param  dwExInfoBufSize  Specify extended information buffer size.
	* @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
	*/
	virtual long AddExtendedInfo(
		IN DWORD       dwInfoSignature,
		IN const PBYTE pbExInfoBuf,
		IN DWORD       dwExInfoBufSize
		) = 0;
};

/**
 * To use MSXNL for dealing with configuration file, I call CoInitialize and CoUnInitialize in 
 * constructor and destructor. Be careful, there is an restriction for COM initialization in 
 * APARTMENT mode: [the first thread in the application that calls CoInitialize(0) or 
 * CoInitializeEx(COINIT_APARTMENTTHREADED) must be the last thread to call CoUninitialize(). 
 * If the call sequence is not in this order, then subsequent calls to CoInitialize on the STA 
 * will fail and the application will not work.]. When you use this crypto library please make
 * sure such things:
 * 1. Make sure main thread call CoInitialize before creating crypto instance.
 * 2. (OR) Make sure the first thread create crypto instance exits lastly if #1 thing is missed.
 */
class CRYPTOWRAPPERDLL_API ICryptoWrapFactory
{
public:
    /**
     * Static member method to create a instance for encryption and hash.
     * @Param  CryptoInfo      Input parameter to indicate behavior of instance.
     * @Param  CreateNewAlways If create a new instance or reuse exist instance. Currently it will create a new always.
     * @Param  plErrCode       Error code for indicating error reason. Defined in D2DCryptoError.h
     * @Return If succeed return the instance pointer created. If any error return NULL. 
     */
    static ICryptoWrapperInterface* CryptoWrapInstance(
        IN      CCryptoParam& CryptoInfo, 
        IN OPT  bool          bCreateNewAlways = true, 
        OUT OPT long*         plErrCode = NULL
        );

    /**
     * Static member method to create a instance for encryption and hash.
     * @Param  CryptoInfo       Input parameter to indicate behavior of instance.
     * @Param  CreateNewAlways  If create a new instance or reuse exist instance. Currently it will create a new always.
     * @Param  plErrCode        Error code for indicating error reason. Defined in D2DCryptoError.h
     * @Param  pKeyBLOBBuf      Specify key BLOB buffer. If this parameter is NUll, a new encryption key will be created,
     * @Param  dwKeyBLOBSize    Specify key BLOB buffer size. Only valid when pKeyBLOBBuf is specified,
     * @Param  eKeyBLOBType     Specify key BLOB type,
     * @Param  bInitAfterCreate If call initialize function after this instance is created.
     * @Return If succeed return the instance pointer created. If any error return NULL. 
     */
    static ICryptoWrapperInterface* CryptoWrapInstanceEx(
        IN      CCryptoParam&  CryptoInfo, 
        IN OPT  BYTE*          pKeyBLOBBuf = NULL,
        IN OPT  DWORD          dwKeyBLOBSize = 0,
        IN OPT  E_KEYBLOB_TYPE eKeyBLOBType = EKBT_UNKNOWN,
        IN OPT  bool           bCreateNewAlways = true, 
        OUT OPT long*          plErrCode = NULL,
        IN OPT  bool           bInitAfterCreate = true
        );

    /**
     * Static member method to release exist crypto instance.
     * @Param  ppCryptoWrapInterface Pointer to instance pointer. After release this pointer is set to NULL
     * @Return void
     */
    static void ReleaseCryptoWrapInstance(
        IN ICryptoWrapperInterface** ppCryptoWrapInterface
        );

    /**
     * Static member method to create a key file manager instance for managing key file.
     * @Param  pwzKeyFilePath    Specify full path of key file. If this parameter is NULL, you should specify a valid path in following calling.
     * @Param  pwzKeyFilePWD     Specify password to encrypt key file.
     * @Param  plErrCode         Error code for indicating error reason. Defined in D2DCryptoError.h
     * @Param  dwSaveControl     Specify save control option. Refer to E_SAVE_KEY_FILE_CTRL.
     * @Return If succeed return the instance pointer created. If any error return NULL. 
     */
    static IKeyFileMgrInterface* CryptoKeyFileMgrInstance(
        IN OPT  const WCHAR* pwzKeyFilePath = NULL, 
        IN OPT  const WCHAR* pwzKeyFilePWD = NULL,
        OUT OPT long*        plErrCode = NULL,
        IN OPT  DWORD        dwSaveControl = ESKFC_NONE
        );

    /**
     * Static member method to release exist key file manager instance.
     * @Param  ppCryptoKeyFileMgr Pointer to key file manager instance pointer. After release this pointer is set to NULL
     * @Return void
     */
    static void ReleaseCryptoKeyFileMgrInstance(
        IN IKeyFileMgrInterface** ppCryptoKeyFileMgr
        );

    /**
     * Static member method to convert a unicode string to binary.
     * The unicode string should be as such format: base64(both contain header and not), HEX string.
     * @Param  pbBinary     Binary buffer for converted string, If equal to NULL, pdwBinSize return required size. 
     * @Param  pdwBinSize   Binary buffer size. Must NOT be NULL. if this size is not large enough, return required size.
     * @Param  pwzString    Pointer to unicode string to convert. Must NOT be NULL
     * @Param  dwStrSize    In CHARACTERS, not including terminating null character.
     * @Param  dwFlags      Flags for indicating how to convert, defined in enumerate type E_STR_OPT
     * @Param  pdwSkipSize  Return How many characters have been skipped to find real data. (skip header~)
     * @Param  pdwFlagsUsed Flags used for converting. if dwFlags is ESEO_BASE64_ANY or ESEO_HEX_ANY, this parameter will
     *                      return the flag used actually.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     * @see    E_STR_OPT
     */
    static long WString2Binary(
        OUT     PBYTE        pbBinary,
        IN OUT  DWORD*       pdwBinSize,
        IN      const WCHAR* pwzString,
        IN      DWORD        dwStrSize,
        IN OPT  DWORD        dwFlags = ESEO_BASE64_ANY,
        OUT OPT DWORD*       pdwSkipSize = NULL,
        OUT OPT DWORD*       pdwFlagsUsed = NULL
        );

    /**
     * Static member method to convert a binary to unicode string.
     * The unicode string should be as such format: base64(both contain header and not), HEX string.
     * @Param  pwzString  Pointer to unicode string after converting. If equal to NULL, pdwStrSize return required size.
     * @Param  pdwStrSize Must NOT be NULL. In CHARACTERS, including terminating null character, to indicate string length 
     *                    after converting. If it is not large enough, return D2DCRYPTO_E_MORE_DATA and pdwStrSize store required size.
     * @Param  pbBinary   Binary buffer to be converted, Must NOT be NULL. 
     * @Param  dwBinSize  Binary buffer size. Must NOT be zero.
     * @Param  dwFlags    Flags for indicating how to convert, defined in enumerate type E_STR_OPT
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     * @see    E_STR_OPT
     */
    static long Binary2StringW(
        OUT    WCHAR*      pwzString,
        IN OUT DWORD*      pdwStrSize,
        IN     const PBYTE pbBinary,
        IN     DWORD       dwBinSize,
        IN OPT DWORD       dwFlags = ESEO_BASE64
        );

    /**
     * Enumerate key container in specified CSP based on input encryption parameter.
     * @Param  vecContainerName Receive all key container names.
     * @Param  CryptoInfo       Input crypto information which decide the CSP.
     * @Param  eQueryCtrl       Which kind of container will be returned.
     * @Param  bSkipSelf        If skip the container name for current instance.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long EnumKeyContainerInCSP(
        OUT    std::vector<std::wstring>& vecContainerName,
        IN OPT E_QUERY_KEYSET_CTRL        eQueryCtrl = EQKC_D2D_ONLY,
        IN OPT bool                       bSkipSelf = true,
        IN OPT CCryptoParam*              pCryptoInfo = NULL
        );

    /**
     * Remove key container in specified CSP based on input vector or results searched.
     * @Param  pvecContainerName All container in vector will be remove, If it is NULL, all results searched will be removed.
     * @Param  CryptoInfo       Input crypto information which decide the CSP.
     * @Param  eQueryCtrl       Which kind of container will be returned.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long RemoveKeyContainerInCSP(
        IN OPT std::vector<std::wstring>* pvecContainerName = NULL,
        IN OPT E_QUERY_KEYSET_CTRL        eQueryCtrl = EQKC_D2D_ONLY,
        IN OPT CCryptoParam*              pCryptoInfo = NULL
        );

    /**
     * Create empty key container in specified CSP based on input encryption parameter.
     * @Param  pwzContainerName Container name will be created.
     * @Param  bTryRandomName   When it is unable to create specified key container, if try random name.
     * @Param  CryptoInfo       Input crypto information which decide the CSP.
     * @Param  pwsActualContianerName The actual container name created.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long CreateKeyContainerInCSP(
        IN OPT  const WCHAR*  pwzContainerName = NULL,
        IN OPT  bool          bTryRandomName = true,
        IN OPT  CCryptoParam* pCryptoInfo = NULL,
        OUT OPT wstring*      pwsActualContianerName = NULL
        );

    /**
     * Validate if a key BLOB is correct based on CSP type, including data size and BLOB length.
     * @Param  pbKeyBLOBBuf   Key BLOB buffer.
     * @Param  dwKeyBLOBSize  Key BLOB buffer size.
     * @Param  eKeyBLOBType   What kind of key BLOB.
     * @Param  pwsKeyBLOBDesc Description of key BLOB if it is valid.
     * @Param  dwCSPType      CSP type. Currently we only support key BLOB format in base provider and extended provider.
     * @Return If valid key BLOB return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long ValidateKeyBLOB(
        IN     PBYTE          pbKeyBLOBBuf, 
        IN     DWORD          dwKeyBLOBSize, 
        IN     E_KEYBLOB_TYPE eKeyBLOBType,
        IN OPT wstring*       pwsKeyBLOBDesc,
        IN OPT E_CSP_TYPE     dwCSPType = EST_BASE_CSP);

    /** 
     * Encrypt data using input password The encrypted data, result may be a string in base64 format or binary.
     * @Param  pbBuffer2Enc   Data buffer to encrypt.
     * @Param  dwBuf2EncSize  Size of data buffer to encrypt.
     * @Param  pwzPassword    Password to encrypt data or private key.
     * @Param  pwsEncDataStr  A string to receive encrypted data after base64 coding.
     * @Param  pbEncDataBuf   Data buffer to receive data encrypted. If equal to NULL, pdwEncDataSize stores required size.
     * @Param  pdwEncDataSize Specify data buffer size to receive encrypt data. It can only be ignored and set to NULL when pwsEncDataStr is used.
     * @Param  dwFlags        Specify if data will be padding when encrypt to following CBC algorithm requirement.
     * @Return If valid key BLOB return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long EncryptData(
        IN         PBYTE        pbBuffer2Enc,
        IN         DWORD        dwBuf2EncSize,
        IN         const WCHAR* pwzPassword,
        OUT OPT    wstring*     pwsEncDataStr,
        OUT OPT    PBYTE        pbEncDataBuf = NULL,
        IN OUT OPT DWORD*       pdwEncDataSize = NULL,
        IN OPT     DWORD        dwFlags = ECF_PADDING_DATA);

    /** 
     * Decrypt data using input password. Input encrypted data may be a string in base64 format or a data buffer.
     * @Param  pbBuffer2Dec   Data buffer to decrypt.
     * @Param  dwBuf2DecSize  Size of data buffer size to decrypt.
     * @Param  pwsEncStr2Dec  A string in base64 format to decrypt.
     * @Param  pwzPassword    Password to decrypt data or saved private key.
     * @Param  pbDecDataBuf   Data buffer to receive decrypted data. If it is NULL, pdwDecDataSize stores required size.
     * @Param  pdwDecDataSize Size of buffer to receive decrypted data. It can only be ignored when pwsDecDataStr is not empty.
     * @Param  dwFlags        Specify if data will be padding when encrypt to following CBC algorithm requirement.
     * @Return If valid key BLOB return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long DecryptData(
        IN OPT     PBYTE        pbBuffer2Dec,
        IN OPT     DWORD        dwBuf2DecSize,
        IN OPT     const WCHAR* pwsEncStr2Dec,
        IN         const WCHAR* pwzPassword,
        OUT OPT    PBYTE        pbDecDataBuf = NULL,
        IN OUT OPT DWORD*       pdwDecDataSize = NULL,
        IN OPT     DWORD        dwFlags = ECF_PADDING_DATA);

    /**
     * Get hash value for input data buffer.
     * The hash value will be convert to a string being composed of HEX characters. 
     * @Param  pPlainbataBuf      Input data buffer to be calculated for hash, Must NOT be NULL. 
     * @Param  dwPlainDataBufLen  Input data buffer size(in byte) .Must NOT be zero.
     * @Param  wsHashStr          Store hash value string.
     * @Param  dwHashAlgType      Reserved. Modify it only when you exactly know if CSP created support it.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long GetHashData(
        IN  const BYTE* pPlainbataBuf,
        IN  DWORD       dwPlainDataBufLen, 
        OUT wstring&    wsHashStr,
        IN OPT DWORD    dwHashAlgType = EHAT_SHA1);

    /**
     * Save a data buffer into key file. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
     * @Param  pbDataBuffer   Specify data buffer to be encrypted and saved into key file path.
     * @Param  dwDataBufSize  Specify data buffer size, in bytes.
     * @Param  pwzKeyFilePath Specify a valid and accessible full path which used to store key and data.
     * @Param  pwzPassword    Specify a password to encrypt private key. If this parameter is empty. all data is plain text.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyFileSaveData(
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,         
        IN     const WCHAR* pwzKeyFilePath, 
        IN OPT const WCHAR* pwzPassword);


    /**
     * Save a data buffer into key file data buffer. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
     * @Param  pbKeyFileData Specify key file data buffer object to receive key file data..
     * @Param  pbDataBuffer  Specify data buffer to be encrypted and saved into key file path.
     * @Param  dwDataBufSize Specify data buffer size, in bytes.
     * @Param  pwzPassword   Specify a password to encrypt private key. If this parameter is empty. all data is plain text.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyDataSaveData(
        IN OUT CByteBuf&    pbKeyFileData,
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,
        IN OPT const WCHAR* pwzPassword);

    /**
     * Read decrypted data from key file. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
     * and dwDataBufSize will receive required buffer size.
     * @Param  pbDataBuffer   Specify data buffer which receives decrypted data. Set NULL to get required size.
     * @Param  dwDataBufSize  Specify data buffer size and receive decrypted size. Return required size if size is not enough or pbDataBuffer is NULL, 
     * @Param  pwzKeyFilePath Specify full path of key file. Caller should make sure this file can be read.
     * @Param  pwzPassword    Specify password used to decrypt private key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyFileReadData(
        OUT    PBYTE        pbDataBuffer,          
        OUT    DWORD&       dwDataBufSize,        
        IN     const WCHAR* pwzKeyFilePath, 
        IN OPT const WCHAR* pwzPassword);

    /**
     * Read decrypted data from key file data buffer. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
     * and dwDataBufSize will receive required buffer size.
     * @Param  pbDataBuf        Specify a key data buffer object to receive decrypted data.   
     * @Param  pbKeyDataBuf     Specify a key data buffer from where data will be read.
     * @Param  dwKeyDataBufSize Specify a key data buffer size to read.
     * @Param  pwzPassword      Specify password used to decrypt private key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyDataReadData(
        OUT    CByteBuf&    pbDataBuf,     
        IN     const PBYTE  pbKeyDataBuf,          
        IN     DWORD        dwKeyDataBufSize,
        IN OPT const WCHAR* pwzPassword);

    /**
     * Encrypt data and replace it into key file. 
     * Replace data saved in current key file. This data will be encrypted using public key stored in key file
     * @Param  pbDataBuffer   Specify data buffer to replace the data in key file.
     * @Param  dwDataBufSize  Specify data buffer size.
     * @Param  pwzKeyFilePath Specify full path of key file. Caller should make sure this file can be read.
     * @Param  pwzPassword    Not used now. should be set as NULL.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyFileUpdateData(
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,         
        IN     const WCHAR* pwzKeyFilePath,  
        IN OPT const WCHAR* pwzPassword);

    /**
     * Encrypt data and replace it into key file data buffer 
     * Replace data saved in current key file. This data will be encrypted using public key stored in key file
     * @Param  pbKeyFileData    Specify a key file data buffer object which will be update by encrypted data.
     * @Param  pbDataBuffer     Specify data buffer to replace the data in key file data buffer.
     * @Param  dwDataBufSize    Specify data buffer size.
     * @Param  pwzPassword      Not used now. should be set as NULL.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyDataUpdateData(
        OUT    CByteBuf&    pbKeyFileData,
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,         
        IN OPT const WCHAR* pwzPassword);

    /**
     * Decrypt data stored in key file using original password and encrypt them using new password. If current key file is not encrypted data will 
     * be encrypted. If new password is NULL or empty. the data will be decrypted and saved into key file in plain text format.
     * @Param  pwzKeyFilePath Specify full path of key file. Caller should make sure this file can be read. 
     * @Param  pwzNewPassword Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
     * @Param  pwzCurPassword Specify original password to decrypt private key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyFileUpdatePassword(
        IN     const WCHAR* pwzKeyFilePath, 
        IN OPT const WCHAR* pwzNewPassword, 
        IN OPT const WCHAR* pwzCurPassword);

    /**
     * Decrypt data stored in key file data buffer using original password and encrypt them using new password. If current key file data buffer is not
     * encrypted data will be encrypted. If new password is NULL or empty. the data will be decrypted and saved into key file in plain text format.
     * @Param  pbKeyFileData    Specify a key data buffer object to which key file data wille be updated.
     * @Param  pwzNewPassword   Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
     * @Param  pwzCurPassword   Specify original password to decrypt private key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    static long KeyDataUpdatePassword(
        IN OUT CByteBuf&    pbKeyFileData,
        IN OPT const WCHAR* pwzNewPassword, 
        IN OPT const WCHAR* pwzCurPassword);

    /**
     * Derive key data based on same algorithm as what CAPI and CNG use. 
     * @Param  pbKeyData       Specify buffer to store key data. It can be NULL when query key data size
     * @Param  pulKeyDataSize  Specify buffer size of key data, if the size is not big enough or pbKeyData is NULL, this value store required size.
     * @Param  pbBaseDataBuf   Specify base data to generate key data, usually it is something like password.
     * @Param  ulBaseDataSize  Specify base data size, in bytes.
     * @Param  ulHashType      Specify hash algorithm used to hash base data. Refer to E_HASHALG_TYPE.
     * @Param  ulAlgType       Specify encryption algorithm. Refer to E_ENCALG_TYPE.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h. If the key data buffer size is not big enough
     *         D2DCRYPTO_E_MORE_DATA will be returned.
     */
    static long DeriveKey(
        OUT    PBYTE pbKeyData, 
        IN OUT ULONG* pulKeyDataSize, 
        IN     PBYTE pbBaseDataBuf, 
        IN     ULONG ulBaseDataSize, 
        IN     ULONG ulHashType,
        IN     ULONG ulAlgType);

	/**
	* Convert exported key format from MS to X509 which is used by ETPKI.
	* @Param  pbOutKeyData       Specify buffer to store key data after being converted. It can be NULL when query key data size
	* @Param  pdwOutKeyDataSize  Specify buffer size of key data, if the size is not big enough or pbKeyData is NULL, this value store required size.
	* @Param  pbInKeyData        Specify original key to convert in MS format.
	* @Param  dwInKeyDataSize    Specify size of original key to convert in MS format.
	* @Param  eKeyBLOBType       Specify original key data type, refer to E_KEYBLOB_TYPE.
	* @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h. If the key data buffer size is not big enough
	*         D2DCRYPTO_E_MORE_DATA will be returned.
	*/
	static long	ConvertKeyFormatMSToX509(
		OUT    PBYTE  pbOutKeyData,
		IN OUT DWORD* pdwOutKeyDataSize,
		IN     PBYTE  pbInKeyData,
		IN     DWORD  dwInKeyDataSize,
		IN     E_KEYBLOB_TYPE eKeyBLOBType);
	
	/**
	* Convert exported key format from X509 which is used by ETPKI to MS.
	* @Param  pbOutKeyData       Specify buffer to store key data after being converted. It can be NULL when query key data size
	* @Param  pdwOutKeyDataSize  Specify buffer size of key data, if the size is not big enough or pbKeyData is NULL, this value store required size.
	* @Param  pbInKeyData        Specify original key to convert in X509 format.
	* @Param  dwInKeyDataSize    Specify size of original key to convert in X509 format.
	* @Param  eKeyBLOBType       Specify original key data type, refer to E_KEYBLOB_TYPE.
	* @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h. If the key data buffer size is not big enough
	*         D2DCRYPTO_E_MORE_DATA will be returned.
	*/
	static long	ConvertKeyFormatX509ToMS(
		OUT    PBYTE  pbOutKeyData,
		IN OUT DWORD* pdwOutKeyDataSize,
		IN     PBYTE  pbInKeyData,
		IN     DWORD  dwInKeyDataSize,
		IN     E_KEYBLOB_TYPE eKeyBLOBType);
};

//ZZ: Defined for calling crypto API by function address.
#ifdef __cplusplus
extern "C"
{
#endif
    /**
     * Static member method to create a instance for encryption and hash.
     * @Param  CryptoInfo      Input parameter to indicate behavior of instance.
     * @Param  CreateNewAlways If create a new instance or reuse exist instance. Currently it will create a new always.
     * @Param  plErrCode       Error code for indicating error reason. Defined in D2DCryptoError.h
     * @Return If succeed return the instance pointer created. If any error return NULL. 
     */
    CRYPTOWRAPPERDLL_API ICryptoWrapperInterface* AFENCCryptoWrapInstance(
        IN      CCryptoParam& CryptoInfo, 
        IN OPT  bool bCreateNewAlways = true, 
        OUT OPT long* plErrCode = NULL
        );
    typedef ICryptoWrapperInterface* (*PFN_AFENCCryptoWrapInstance)(IN CCryptoParam&, IN OPT bool, OUT OPT long*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Static member method to create a instance for encryption and hash.
     * @Param  CryptoInfo       Input parameter to indicate behavior of instance.
     * @Param  CreateNewAlways  If create a new instance or reuse exist instance. Currently it will create a new always.
     * @Param  plErrCode        Error code for indicating error reason. Defined in D2DCryptoError.h
     * @Param  pKeyBLOBBuf      Specify key BLOB buffer. If this parameter is NUll, a new encryption key will be created,
     * @Param  dwKeyBLOBSize    Specify key BLOB buffer size. Only valid when pKeyBLOBBuf is specified,
     * @Param  eKeyBLOBType     Specify key BLOB type,
     * @Param  bInitAfterCreate If call initialize function after this instance is created.
     * @Return If succeed return the instance pointer created. If any error return NULL. 
     */
    CRYPTOWRAPPERDLL_API ICryptoWrapperInterface* AFENCCryptoWrapInstanceEx(
        IN      CCryptoParam&  CryptoInfo, 
        IN OPT  BYTE*          pKeyBLOBBuf = NULL,
        IN OPT  DWORD          dwKeyBLOBSize = 0,
        IN OPT  E_KEYBLOB_TYPE eKeyBLOBType = EKBT_UNKNOWN,
        IN OPT  bool           bCreateNewAlways = true, 
        OUT OPT long*          plErrCode = NULL,
        IN OPT  bool           bInitAfterCreate = true
        );
    typedef ICryptoWrapperInterface* (*PFN_AFENCCryptoWrapInstanceEx)(
        IN      CCryptoParam&,
        IN OPT  BYTE*,        
        IN OPT  DWORD,         
        IN OPT  E_KEYBLOB_TYPE,
        IN OPT  bool,          
        OUT OPT long*,         
        IN OPT  bool);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Static member method to release exist instance.
     * @Param  ppCryptoWrapInterface Pointer to instance pointer. After release this pointer is set to NULL
     * @Return void
     */
    CRYPTOWRAPPERDLL_API void AFENCReleaseCryptoWrapInstance(
        IN ICryptoWrapperInterface** ppCryptoWrapInterface
        );
    typedef void (*PFN_AFENCReleaseCryptoWrapInstance)(IN ICryptoWrapperInterface**);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Static member method to create a key file manager instance for managing key file.
     * @Param  pwzKeyFilePath    Specify full path of key file. If this parameter is NULL, you should specify a valid path in following calling.
     * @Param  pwzKeyFilePWD     Specify password to encrypt key file.
     * @Param  plErrCode         Error code for indicating error reason. Defined in D2DCryptoError.h
     * @Param  dwSaveControl     Specify save control option.Refer to E_SAVE_KEY_FILE_CTRL.
     * @Return If succeed return the instance pointer created. If any error return NULL. 
     */
    CRYPTOWRAPPERDLL_API IKeyFileMgrInterface* AFENCCryptoKeyFileMgrInstance(
        IN OPT  const WCHAR* pwzKeyFilePath = NULL, 
        IN OPT  const WCHAR* pwzKeyFilePWD = NULL,
        OUT OPT long*        plErrCode = NULL,
        IN OPT  DWORD        dwSaveControl = ESKFC_NONE
        );

    typedef IKeyFileMgrInterface* (*PFN_AFENCCryptoKeyFileMgrInstance)(IN OPT const WCHAR*, IN OPT const WCHAR*, OUT OPT long*, IN OPT DWORD);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Static member method to release exist key file manager instance.
     * @Param  ppCryptoKeyFileMgr Pointer to key file manager instance pointer. After release this pointer is set to NULL
     * @Return void
     */
     CRYPTOWRAPPERDLL_API void AFENCReleaseCryptoKeyFileMgrInstance(
        IN IKeyFileMgrInterface** ppCryptoKeyFileMgr
        );
     typedef void (*PFN_AFENCReleaseCryptoKeyFileMgrInstance)(IN IKeyFileMgrInterface**);
     //////////////////////////////////////////////////////////////////////////

    /**
     * Static member method to convert a unicode string to binary.
     * The unicode string should be as such format: base64(both contain header and not), HEX string.
     * @Param  pbBinary     Binary buffer for converted string, If equal to NULL, pdwBinSize return required size. 
     * @Param  pdwBinSize   Binary buffer size. Must NOT be NULL. if this size is not large enough, return required size.
     * @Param  pwzString    Pointer to unicode string to convert. Must NOT be NULL
     * @Param  dwStrSize    In CHARACTERS, not including terminating null character.
     * @Param  dwFlags      Flags for indicating how to convert, defined in enumerate type E_STR_OPT
     * @Param  pdwSkipSize  Return How many characters have been skipped to find real data. (skip header~)
     * @Param  pdwFlagsUsed Flags used for converting. if dwFlags is ESEO_BASE64_ANY or ESEO_HEX_ANY, this parameter will
     *                      return the flag used actually.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     * @see    E_STR_OPT
     */
    CRYPTOWRAPPERDLL_API long AFENCWString2Binary(
        OUT     PBYTE        pbBinary,
        IN OUT  DWORD*       pdwBinSize,
        IN      const WCHAR* pwzString,
        IN      DWORD        dwStrSize,
        IN OPT  DWORD        dwFlags = ESEO_BASE64_ANY,
        OUT OPT DWORD*       pdwSkipSize = NULL,
        OUT OPT DWORD*       pdwFlagsUsed = NULL
        );
    typedef long (* PFN_AFENCWString2Binary)(OUT PBYTE, IN OUT DWORD*, IN const WCHAR*, IN DWORD, IN OPT DWORD, OUT OPT DWORD*, OUT OPT DWORD*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Static member method to convert a binary to unicode string.
     * The unicode string should be as such format: base64(both contain header and not), HEX string.
     * @Param  pwzString  Pointer to unicode string after converting. If equal to NULL, pdwStrSize return required size.
     * @Param  pdwStrSize Must NOT be NULL. In CHARACTERS, including terminating null character, to indicate string length 
     *                    after converting. If it is not large enough, return D2DCRYPTO_E_MORE_DATA and pdwStrSize store required size.
     * @Param  pbBinary   Binary buffer to be converted, Must NOT be NULL. 
     * @Param  dwBinSize  Binary buffer size. Must NOT be zero.
     * @Param  dwFlags    Flags for indicating how to convert, defined in enumerate type E_STR_OPT
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     * @see    E_STR_OPT
     */
    CRYPTOWRAPPERDLL_API long AFENCBinary2StringW(
        OUT    WCHAR*      pwzString,
        IN OUT DWORD*      pdwStrSize,
        IN     const PBYTE pbBinary,
        IN     DWORD       dwBinSize,
        IN OPT DWORD       dwFlags = ESEO_BASE64
        );
    typedef long (*PFN_AFENCBinary2StringW)(OUT WCHAR*, IN OUT DWORD*, IN const PBYTE, IN DWORD, IN OPT DWORD);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Enumerate key container in specified CSP based on input encryption parameter.
     * @Param  vecContainerName Receive all key container names.
     * @Param  CryptoInfo       Input crypto information which decide the CSP.
     * @Param  eQueryCtrl       Which kind of container will be returned.
     * @Param  bSkipSelf        If skip the container name for current instance.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCEnumKeyContainerInCSP(
        OUT    std::vector<std::wstring>& vecContainerName,
        IN OPT E_QUERY_KEYSET_CTRL        eQueryCtrl = EQKC_D2D_ONLY,
        IN OPT bool                       bSkipSelf = true,
        IN OPT CCryptoParam*              pCryptoInfo = NULL
        );
    typedef long (*PFN_AFENCEnumKeyContainerInCSP)(OUT std::vector<std::wstring>&, E_QUERY_KEYSET_CTRL, IN OPT bool, IN OPT CCryptoParam*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Remove key container in specified CSP based on input vector or results searched.
     * @Param  pvecContainerName All container in vector will be remove, If it is NULL, all results searched will be removed.
     * @Param  CryptoInfo       Input crypto information which decide the CSP.
     * @Param  eQueryCtrl       Which kind of container will be returned.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCRemoveKeyContainerInCSP(
        IN OPT std::vector<std::wstring>* pvecContainerName = NULL,
        IN OPT E_QUERY_KEYSET_CTRL        eQueryCtrl = EQKC_D2D_ONLY,
        IN OPT CCryptoParam*              pCryptoInfo = NULL
        );
    typedef long (*PFN_AFENCRemoveKeyContainerInCSP)(IN OPT std::vector<std::wstring>*, IN OPT E_QUERY_KEYSET_CTRL, IN OPT CCryptoParam*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Create empty key container in specified CSP based on input encryption parameter.
     * @Param  pwzContainerName Container name will be created.
     * @Param  bTryRandomName   When it is unable to create specified key container, if try random name.
     * @Param  CryptoInfo       Input crypto information which decide the CSP.
     * @Param  pwsActualContianerName The actual container name created.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCCreateKeyContainerInCSP(
        IN OPT  const WCHAR*  pwzContainerName = NULL,
        IN OPT  bool          bTryRandomName = true,
        IN OPT  CCryptoParam* pCryptoInfo = NULL,
        OUT OPT wstring*      pwsActualContianerName = NULL);
    typedef long (*PFN_AFENCCreateKeyContainerInCSP)(IN OPT const WCHAR*, IN OPT bool, IN OPT CCryptoParam*, OUT OPT wstring*);
    //////////////////////////////////////////////////////////////////////////

    /** Encrypt data using input password The encrypted data, result may be a string in base64 format or binary.
     * @Param  pbBuffer2Enc   Data buffer to encrypt.
     * @Param  dwBuf2EncSize  Size of data buffer to encrypt.
     * @Param  pwzPassword    Password to encrypt data or private key.
     * @Param  pwsEncDataStr  A string to receive encrypted data after base64 coding.
     * @Param  pbEncDataBuf   Data buffer to receive data encrypted. If equal to NULL, pdwEncDataSize stores required size.
     * @Param  pdwEncDataSize Specify data buffer size to receive encrypt data. It can only be ignored and set to NULL when pwsEncDataStr is used.
     * @Param  dwFlags        Specify if data will be padding when encrypt to following CBC algorithm requirement.
     * @Return If valid key BLOB return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCEncryptData(
        IN         PBYTE        pbBuffer2Enc,
        IN         DWORD        dwBuf2EncSize,
        IN         const WCHAR* pwzPassword,
        OUT OPT    wstring*     pwsEncDataStr,
        OUT OPT    PBYTE        pbEncDataBuf = NULL,
        IN OUT OPT DWORD*       pdwEncDataSize = NULL,
        IN OPT     DWORD        dwFlags = ECF_PADDING_DATA);
    typedef long (*PFN_AFENCEncryptData)(
        IN PBYTE, IN DWORD, IN const WCHAR*, OUT OPT wstring*, OUT OPT PBYTE, IN OUT OPT DWORD*, IN OPT DWORD);
    //////////////////////////////////////////////////////////////////////////

    /** Decrypt data using input password. Input encrypted data may be a string in base64 format or a data buffer.
     * @Param  pbBuffer2Dec   Data buffer to decrypt.
     * @Param  dwBuf2DecSize  Size of data buffer size to decrypt.
     * @Param  pwsEncStr2Dec  A string in base64 format to decrypt.
     * @Param  pwzPassword    Password to decrypt data or saved private key.
     * @Param  pbDecDataBuf   Data buffer to receive decrypted data. If it is NULL, pdwDecDataSize stores required size.
     * @Param  pdwDecDataSize Size of buffer to receive decrypted data. It can only be ignored when pwsDecDataStr is not empty.
     * @Param  dwFlags        Specify if data will be padding when encrypt to following CBC algorithm requirement.
     * @Return If valid key BLOB return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCDecryptData(
        IN OPT     PBYTE        pbBuffer2Dec,
        IN OPT     DWORD        dwBuf2DecSize,
        IN OPT     const WCHAR* pwsEncStr2Dec,
        IN         const WCHAR* pwzPassword,
        OUT OPT    PBYTE        pbDecDataBuf = NULL,
        IN OUT OPT DWORD*       pdwDecDataSize = NULL,
        IN OPT     DWORD        dwFlags = ECF_PADDING_DATA);
    typedef long (*PFN_AFENCDecryptData)(
        IN OPT PBYTE, IN OPT DWORD, IN OPT const WCHAR*, IN const WCHAR*, OUT OPT PBYTE, IN OUT OPT DWORD*, IN OPT DWORD);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Get hash value for input data buffer.
     * The hash value will be convert to a string being composed of HEX characters. 
     * @Param  pPlainbataBuf      Input data buffer to be calculated for hash, Must NOT be NULL. 
     * @Param  dwPlainDataBufLen  Input data buffer size(in byte) .Must NOT be zero.
     * @Param  wsHashStr          Store hash value string.
     * @Param  dwHashAlgType      Reserved. Modify it only when you exactly know if CSP created support it.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCGetHashData(
        IN  const BYTE* pPlainbataBuf,
        IN  DWORD       dwPlainDataBufLen, 
        OUT wstring&    wsHashStr,
        IN OPT DWORD    dwHashAlgType = EHAT_SHA1);
    typedef long (*PFN_AFENCGetHashData)(IN const BYTE*, IN DWORD, OUT wstring&, IN OPT DWORD);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Save a data buffer into key file. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
     * @Param  pbDataBuffer   Specify data buffer to be encrypted and saved into key file path.
     * @Param  dwDataBufSize  Specify data buffer size, in bytes.
     * @Param  pwzKeyFilePath Specify a valid and accessible full path which used to store key and data.
     * @Param  pwzPassword    Specify a password to encrypt private key. If this paramter is empty. all data is plain text.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyFileSaveData(
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,         
        IN     const WCHAR* pwzKeyFilePath, 
        IN OPT const WCHAR* pwzPassword);
    typedef long (*PFN_AFENCKeyFileSaveData)(IN PBYTE, IN DWORD, IN const WCHAR*, IN OPT const WCHAR*);
    //////////////////////////////////////////////////////////////////////////

     /**
     * Save a data buffer into key file data buffer. This data will be encrypted by public key, while private key is encrypted by pwzPassword.
     * @Param  pbKeyFileData Specify key file data buffer object to receive key file data..
     * @Param  pbDataBuffer  Specify data buffer to be encrypted and saved into key file path.
     * @Param  dwDataBufSize Specify data buffer size, in bytes.
     * @Param  pwzPassword   Specify a password to encrypt private key. If this parameter is empty. all data is plain text.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyDataSaveData(
        IN OUT CByteBuf&    pbKeyFileData,
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,
        IN OPT const WCHAR* pwzPassword);
    typedef long (*PFN_AFENCKeyDataSaveData)(IN OUT CByteBuf&, IN PBYTE, IN DWORD, IN OPT const WCHAR*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Read decrypted data from key file. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
     * and dwDataBufSize will receive required buffer size.
     * @Param  pbDataBuffer   Specify data buffer which receives decrytped data. Set NULL to get required size.
     * @Param  dwDataBufSize  Specify data buffer size and recieve decryped size. Return required size if size is not enough or pbDataBuffer is NULL, 
     * @Param  pwzKeyFilePath Specify full path of key file. Caller should make sure this file can be read.
     * @Param  pwzPassword    Specify password used to decrypt orivate key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyFileReadData(
        OUT    PBYTE        pbDataBuffer,          
        OUT    DWORD&       dwDataBufSize,        
        IN     const WCHAR* pwzKeyFilePath, 
        IN OPT const WCHAR* pwzPassword);
    typedef long (*PFN_AFENCKeyFileReadData)(OUT PBYTE, OUT DWORD&, IN const WCHAR*, IN OPT const WCHAR*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Read decrypted data from key file data buffer. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
     * and dwDataBufSize will receive required buffer size.
     * @Param  pbDataBuf        Specify a key data buffer object to receive decrypted data.   
     * @Param  pbKeyDataBuf     Specify a key data buffer from where data will be read.
     * @Param  dwKeyDataBufSize Specify a key data buffer size to read.
     * @Param  pwzPassword      Specify password used to decrypt private key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyDataReadData(
        OUT    CByteBuf&    pbDataBuf,     
        IN     const PBYTE  pbKeyDataBuf,          
        IN     DWORD        dwKeyDataBufSize,
        IN OPT const WCHAR* pwzPassword);
    typedef long (*PFN_AFENCKeyDataReadData)(OUT CByteBuf&, IN const PBYTE, IN DWORD, IN OPT const WCHAR*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Read decrypted data from key file. When input buffer size is not enough or pbDatabuffer is NULL this API will return D2DCRYPTO_E_MORE_DATA.
     * Replace data saved in current key file. This data will be encrypted using public key stored in key file
     * @Param  pbDataBuffer   Specify data buffer to replace the data in key file.
     * @Param  dwDataBufSize  Specify data buffer size.
     * @Param  pwzKeyFilePath Specify full path of key file. Caller should make sure this file can be read.
     * @Param  pwzPassword    Not used now. should be set as NULL.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyFileUpdateData(
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,         
        IN     const WCHAR* pwzKeyFilePath,  
        IN OPT const WCHAR* pwzPassword);
    typedef long (*PFN_AFENCKeyFileUpdateData)(IN PBYTE, IN DWORD, IN const WCHAR*, IN OPT const WCHAR*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Encrypt data and replace it into key file data buffer 
     * Replace data saved in current key file. This data will be encrypted using public key stored in key file
     * @Param  pbKeyFileData    Specify a key file data buffer object which will be update by encrypted data.
     * @Param  pbDataBuffer     Specify data buffer to replace the data in key file data buffer.
     * @Param  dwDataBufSize    Specify data buffer size.
     * @Param  pwzPassword      Not used now. should be set as NULL.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyDataUpdateData(
        OUT    CByteBuf&    pbKeyFileData,
        IN     PBYTE        pbDataBuffer,          
        IN     DWORD        dwDataBufSize,         
        IN OPT const WCHAR* pwzPassword);
    typedef long (*PFN_AFENCKeyDataUpdateData)(OUT CByteBuf&, IN PBYTE, IN DWORD, IN OPT const WCHAR*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Decrypt data stored in key file using original password and encrypt them using new password. If current key file is not encrypted data will 
     * be encrypted. If new password is NULL or empty. the data will be decrupted and saved into key file in plain text format.
     * @Param  pwzKeyFilePath Specify full path of key file. Caller should make sure this file can be read. 
     * @Param  pwzNewPassword Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
     * @Param  pwzCurPassword Specify original password to decrypt private key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyFileUpdatePassword(
        IN     const WCHAR* pwzKeyFilePath, 
        IN OPT const WCHAR* pwzNewPassword, 
        IN OPT const WCHAR* pwzCurPassword);
    typedef long (*PFN_AFENCKeyFileUpdatePassword)(IN const WCHAR*, IN OPT const WCHAR*, IN OPT const WCHAR*);
    //////////////////////////////////////////////////////////////////////////

    /**
     * Decrypt data stored in key file data buffer using original password and encrypt them using new password. If current key file data buffer is not
     * encrypted data will be encrypted. If new password is NULL or empty. the data will be decrypted and saved into key file in plain text format.
     * @Param  pbKeyFileData    Specify a key data buffer object to which key file data wille be updated.
     * @Param  pwzNewPassword   Specify new password to encrypt private key. If this parameter is NULL or empty. private key will be plain text.
     * @Param  pwzCurPassword   Specify original password to decrypt private key.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h
     */
    CRYPTOWRAPPERDLL_API long AFENCKeyDataUpdatePassword(
        IN OUT CByteBuf&    pbKeyFileData,
        IN OPT const WCHAR* pwzNewPassword, 
        IN OPT const WCHAR* pwzCurPassword);
    typedef long (*PFN_AFENCKeyDataUpdatePassword)(IN OUT CByteBuf&, IN OPT const WCHAR*, IN OPT const WCHAR*);

    /**
     * Derive key data based on same algorithm as what CAPI and CNG use. 
     * @Param  pbKeyData       Specify buffer to store key data. It can be NULL when query key data size
     * @Param  pulKeyDataSize  Specify buffer size of key data, if the size is not big enough or pbKeyData is NULL, this value store required size.
     * @Param  pbBaseDataBuf   Specify base data to generate key data, usually it is something like password.
     * @Param  ulBaseDataSize  Specify base data size, in bytes.
     * @Param  ulHashType      Specify hash algorithm used to hash base data. Refer to E_HASHALG_TYPE.
     * @Param  ulAlgType       Specify encryption algorithm. Refer to E_ENCALG_TYPE.
     * @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h. If the key data buffer size is not big engouh
     *         D2DCRYPTO_E_MORE_DATA will be returned.
     */
    CRYPTOWRAPPERDLL_API long AFENCDeriveKey(
        OUT    PBYTE pbKeyData, 
        IN OUT ULONG* pulKeyDataSize, 
        IN     PBYTE pbBaseDataBuf, 
        IN     ULONG ulBaseDataSize, 
        IN     ULONG ulHashType,
        IN     ULONG ulAlgType);
    typedef long (*PFN_AFENCDeriveKey)(OUT PBYTE, IN OUT ULONG*, IN PBYTE, IN ULONG, IN ULONG, IN ULONG);

	/**
	* Convert exported key format from MS to X509 which is used by ETPKI.
	* @Param  pbOutKeyData       Specify buffer to store key data after being converted. It can be NULL when query key data size
	* @Param  pdwOutKeyDataSize  Specify buffer size of key data, if the size is not big enough or pbKeyData is NULL, this value store required size.
	* @Param  pbInKeyData        Specify original key to convert in MS format.
	* @Param  dwInKeyDataSize    Specify size of original key to convert in MS format.
	* @Param  eKeyBLOBType       Specify original key data type, refer to E_KEYBLOB_TYPE.
	* @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h. If the key data buffer size is not big enough
	*         D2DCRYPTO_E_MORE_DATA will be returned.
	*/
	CRYPTOWRAPPERDLL_API long AFENCConvertKeyFormatMSToX509(
		OUT    PBYTE  pbOutKeyData,
		IN OUT DWORD* pdwOutKeyDataSize,
		IN     PBYTE  pbInKeyData,
		IN     DWORD  dwInKeyDataSize,
		IN     E_KEYBLOB_TYPE eKeyBLOBType);
	typedef long(*PFN_AFENCConvertKeyFormatMSToX509)(OUT PBYTE, IN OUT DWORD*, IN PBYTE, IN DWORD, IN E_KEYBLOB_TYPE);

	/**
	* Convert exported key format from X509 which is used by ETPKI to MS.
	* @Param  pbOutKeyData       Specify buffer to store key data after being converted. It can be NULL when query key data size
	* @Param  pdwOutKeyDataSize  Specify buffer size of key data, if the size is not big enough or pbKeyData is NULL, this value store required size.
	* @Param  pbInKeyData        Specify original key to convert in X509 format.
	* @Param  dwInKeyDataSize    Specify size of original key to convert in X509 format.
	* @Param  eKeyBLOBType       Specify original key data type, refer to E_KEYBLOB_TYPE.
	* @Return If succeed return zero, while failed return error code defined in D2DCryptoError.h. If the key data buffer size is not big enough
	*         D2DCRYPTO_E_MORE_DATA will be returned.
	*/
	CRYPTOWRAPPERDLL_API long AFENCConvertKeyFormatX509ToMS(
		OUT    PBYTE  pbOutKeyData,
		IN OUT DWORD* pdwOutKeyDataSize,
		IN     PBYTE  pbInKeyData,
		IN     DWORD  dwInKeyDataSize,
		IN     E_KEYBLOB_TYPE eKeyBLOBType);
	typedef long(*PFN_AFENCConvertKeyFormatX509ToMS)(OUT PBYTE, IN OUT DWORD*, IN PBYTE, IN DWORD, IN E_KEYBLOB_TYPE);

#ifdef __cplusplus
}
#endif

