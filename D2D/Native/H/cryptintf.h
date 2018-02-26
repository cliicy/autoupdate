/******************************************************************************
*        Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries. All rights
reserved.  Any third party trademarks or copyrights are the property of their
respective owners.				    *
******************************************************************************/


#if !defined(CRYPTO_INTERFACE_H )
#define CRYPTO_INTERFACE_H 

#define		GLOBAL_DIGEST	CI_SHA256
/* #define		GLOBAL_DIGEST	CI_SHA1 */


#ifdef __cplusplus
extern "C"{
#endif


typedef unsigned int CI_u_int32;
typedef unsigned short CI_u_int16;
typedef short CI_int16;
typedef int CI_int32;

#ifndef NULL
#define NULL 0
#endif
#define MAX_ERROR_SIZE 128

// chefr03, for Password Management
#ifdef R12_5_MANAGE_PASSWORD
#define MAX_PASSPHRASE_FILE_SIZE 50000
#endif	// R12_5_MANAGE_PASSWORD


typedef enum CI_BOOL_
{
        CI_FALSE = 0,
        CI_TRUE  = 1
} CI_BOOL;



typedef enum CI_ERRNO_
{
		CI_KEY_DENY_HARDCODEKEY = -10,
		CI_KEY_INCOMPATIBLE	 = -9,
		CI_NO_CHANGE		 = -8,
		CI_INVALID_SIGNATURE = -7,	
		CI_INVALID_BUFFERSIZE= -6,
		CI_EXT_LIB_LOAD_ERR = -5,
		CI_NOT_COMPLAINT    = -4,
		CI_NOT_SUPPORTED    = -3,
		CI_INV_ARGUMENT     = -2,
		CI_FAIL             = -1,
		CI_SUCCESS          = 0
		
// chefr03, for Password Management
#ifdef R12_5_MANAGE_PASSWORD
		,
		CI_OUT_OF_MEMORY	= -11,
		CI_NOT_INITIALIZED	= -12,
		CI_INVALID_METHOD	= -13
#endif	// R12_5_MANAGE_PASSWORD

} CI_ERRNO;

/******************************************************************************
        A common config file will contain the setting for FIPS compliance setting.
        These are the values
******************************************************************************/
typedef enum CI_Compliance_Mode_
{
	CI_NORMAL=0,
	CI_FIPS_2=1,
	CI_FIPS_COMP=2

} CI_Compliance_Mode;


/******************************************************************************
        Encrypt/Decrypt data 
******************************************************************************/
typedef enum CI_Crypt_Mode_
{
	CI_ENCODE=1,
	CI_DECODE=2
} CI_Crypt_Mode;


/******************************************************************************
        List of algorithms (Symmetric or Asymmetric) supported by the library
******************************************************************************/
typedef enum CI_Crypto_Algo
{
	CI_Crypto_Algo_DEFAULT                  =-2,   /* Externally specified configurable default scheme */
	CI_Crypto_Algo_NON                      =-1,
	CI_Crypto_Algo_ATG                      =0,    /* Proprietary algo. For backward compatibility */
	CI_Crypto_Algo_DES3_OLD                 =1,    /* Symmetric Algorithms */
	CI_Crypto_Algo_SIMPLE_XOR               =2,    /* Proprietary algo. For backward compatibility */
	CI_Crypto_Algo_TNG                      =3,    /* Proprietary algo. For backward compatibility */
	CI_Crypto_Algo_JOBSCRIPTS               =4,    /* For backward compatibility.[Jobscripts] */
	CI_Crypto_Algo_DOMAIN_AUTH              =5,    /* For backward compatibility.[domain auth server] */
	CI_Crypto_Algo_BAOF                     =6,    /* For backward compatibility.[domain auth server] */

	CI_Crypto_Algo_DES3_NEW                 =7,
	CI_Crypto_Algo_AES128                   =8,
	CI_Crypto_Algo_AES192                   =9,
	CI_Crypto_Algo_AES256                   =10,
	
	CI_Crypto_Algo_RSA_PKCS1_PADDING        =11   /* Asymmetric Algorithms */

} CI_Crypto_Algo;


/******************************************************************************
                List of HASH algorithms supported by the library
******************************************************************************/
typedef enum CI_Digest_Algo_
{
	CI_SHA1 = 1,
	CI_MD5  = 2,
	CI_MD4  = 3,
	CI_SHA256  = 4,
	CI_SHA384  = 5,
	CI_SHA512  = 6
} CI_Digest_Algo;


/******************************************************************************
                List of base encodings. 
******************************************************************************/
typedef enum CI_Encode_Base_
{
	CI_BASE10 = 1,
	CI_BASE16 = 2,
	CI_BASE32 = 3,
	CI_BASE64  = 4
} CI_Encode_Base;

/******************************************************************************
                List of encryption flags. 
******************************************************************************/
typedef enum CI_ENC_SCHEME_
{
#ifdef BAB_DYNAMIC_KEY
	CI_SCHEME_UNSPECIFY    = 0x0,
#endif
	CI_SCHEME_NO_PADDING   = 0x1,
	CI_SCHEME_USE_HARDKEY1 = 0x2,
	CI_SCHEME_USE_HARDKEY2 = 0x4,
	CI_SCHEME_USE_HARDKEY3 = 0x8
#ifdef BAB_DYNAMIC_KEY
	,
	CI_SCHEME_USE_DYNKEY   = 0x10
#endif

// chefr03, for Password Management
#ifdef R12_5_MANAGE_PASSWORD
	,
	CI_SCHEME_USE_PWD	   = 0x20,
	CI_SCHEME_USE_ADMK	   = 0x40
#endif	// R12_5_MANAGE_PASSWORD

}CI_ENC_SCHEME;

/******************************************************************************
  This structure contains all output parameters.

  @param [OUT]  algo -- The algorithm for creating the key.
  @param [OUT]  KeyData -- The generated Key contents.
  @param [OUT]  KeySize -- The size in bytes of the KeyData.
******************************************************************************/

typedef enum CI_KEY_TYPE_
{
  CI_PUBLIC =0,
  CI_PRIVATE=1,
	CI_SYMMETRIC =2
}CI_KEY_TYPE;


/******************************************************************************
  @param [OUT]  Error -- The generated error. Allocation by caller.
  @param [IN]   ErrorSize -- The size in bytes of the allocated error string.
******************************************************************************/
typedef struct _CI_ERROR_
{
	char Error[MAX_ERROR_SIZE+1];
} CI_ERROR;

/******************************************************************************
CryptIntf library working mode 
CI_KEY_NORMALUSE: working in normal encryption and decryption( load master key 
					file according configuration)
CI_KEY_MANAGEMENT: working in master key management (not load master key file)
CI_KEY_UPGRADING: working in master key grading (always try to load master key file)
******************************************************************************/
typedef enum KEY_OPERATION_TYPE
{
  CI_KEY_NORMALUSE =0,
  CI_KEY_MANAGEMENT=1,
  CI_KEY_UPGRADING =2
}CI_KEY_OPERATION_TYPE;

/******************************************************************************
        An opaque handle to encrypiton state.
******************************************************************************/
typedef void* CI_HANDLE;
typedef struct CI_KEY CI_KEY;
typedef struct CI_KEYGENINFO CI_KEYGENINFO;

/*define a utility structure that may be used by multiple applications*/
#define CI_MODE_UNINITIALIZED	0
#define CI_MODE_FAILED		-1
#define CI_MODE_NORMAL		1
#define CI_MODE_COMPATIBLE	2
typedef struct _security_info_
{
	int		workingMode;
	CI_Encode_Base	encodingType;
	CI_Crypto_Algo	encAlgo;
	unsigned int	encScheme;
	CI_Digest_Algo	digestAlgo;
	unsigned char   digestValue[256];	
	unsigned int 	digestValueLen;
}CI_SecurityInfo;

/* define module name here*/
#define	MODULE_JOBSCRIPT	"JobScriptEncryption"
#define MODULE_DOMAINAUTH	"DomainAuthEncryption"
#define MODULE_SANDATA		"SANDataEncryption"
#define MODULE_AGENTAUTH	"AgentAuthEncryption"
#define MODULE_AGENTDATA	"AgentDataEncryption"


/* Function interfaces follow below */

/******************************************************************************
  Pack a CI_KEYGENINFO structure.
  This funciton will allocated necessary memory and initialize the structure.
	@param [OUT] ppInfo -- The place holder to return allocated KEYGENINFO.
	@param [IN]  password -- password or NULL.
	@param [IN]  passwordSize -- Size of password.
	@param [IN]  salt -- Salt string or NULL.
	@param [IN]  saltSize -- size of salt.
	@param [IN]  module -- Module name.
	@param [IN]  iterationCount or zero.
	@param [IN]  extraProperty -- Used only for BAOF encryption. otherwise zero.
******************************************************************************/
CI_int32  CI_KeyGenInfoPack(CI_KEYGENINFO **ppInfo, 
	unsigned char *password, CI_u_int32 passwordSize, 
	unsigned char* salt, CI_u_int32 saltSize, 
	char* module, CI_u_int32 encryptScheme,
	CI_u_int32 iterationCount, CI_u_int32 extraProperty); 


/******************************************************************************
  UnPack a CI_KEYGENINFO structure.
  This funciton will deallocated memory associated with this stucture.
******************************************************************************/
CI_int32  CI_KeyGenInfoUnPack(CI_KEYGENINFO *pInfo);

 
/******************************************************************************
  Pack a CI_KEY structure.
  This funciton will allocated memory associated with this stucture.
******************************************************************************/
CI_int32  CI_KeyPack(CI_KEY **ppKey, CI_Crypto_Algo algo, CI_KEY_TYPE type, 
	unsigned char *KeyData, CI_u_int32 KeySize, CI_u_int32 encryptScheme);


/******************************************************************************
  UnPack a CI_KEY structure.
  This funciton will deallocated memory associated with this stucture.
******************************************************************************/
CI_int32  CI_KeyUnPack(CI_KEY *pKey);


/******************************************************************************
  Initializes the encryption library. This function should be called 
  at the initialization. It will internally read all from a config file 
  about all encryption libs to initialize ??
  Returns <0 in case of an error.
******************************************************************************/
CI_int32  CI_InitLibrary(CI_ERROR *err);


/******************************************************************************
  Terminates the encryption library. This function should be called in the
  end.
  Returns <0 in case of an error.
******************************************************************************/
CI_int32  CI_TerminateLibrary(CI_ERROR *err);
CI_int32  CI_UnloadLibrary(CI_ERROR *err);

/******************************************************************************
                        DATA ENCRYPTION FUNCTIONS                             *
******************************************************************************/

/******************************************************************************
  Initialize encryption/decryption for a data buffer. This should be called
  as a first step for encrypting or decryption of any data buffer.

  @param [OUT] handle -- The enc/dec handle, a reference to a internal state.
  @param [IN]  algo -- The encryption algorithm.
  @param [IN]  mode -- Encryption or Decryption.
  @param [IN]  pKey -- Key for encryption.
  @param [IN/OUT] err -- The error information.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_InitEncDecData(
	CI_KEY	*pKey,
	CI_Crypt_Mode	mode,
	CI_HANDLE		*handle,
	CI_ERROR *err
);


/******************************************************************************
  Encrypt or decrypt a data buffer.
  @param [IN] handle -- The encryption / decryption handle.
  @param [IN]  input -- The data pointer to encrypt/decrypt.
  @param [IN]  len -- The length of data to encrypt/decrypt.
  @param [IN]  output -- preallocated output buffer.
  @param [IN]  final -- 0:Update, 1:Final
  @param [IN/OUT] err -- The error information.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_EncDecData(
	CI_HANDLE		handle,
	unsigned char *input,
	CI_u_int32			input_len,
	unsigned char *output,
	CI_u_int32			*output_length,
	CI_u_int32			final,
	CI_ERROR		*err
);


/******************************************************************************
  Close the Encryption state for this buffer.
  @param [IN] handle -- The encryption / decryption handle.
  @param [IN/OUT] err -- The error information.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_CloseEncDecData(CI_HANDLE handle, CI_ERROR		*err);


/******************************************************************************
*                     BUFFER ENCRYPTION FUNCTIONS                             
******************************************************************************/


/******************************************************************************
  Encrypt or decrypt a data buffer. This is meant for non-data encryption.

  @param [IN]     algo          -- The encryption algorithm.
  @param [IN]     mode          -- Encryption or Decryption.
  @param [IN]     pInfo         --  Optional input params, NULL for hardcoded key.
  @param [IN]     input         -- The data pointer to encrypt/decrypt.
  @param [IN]     len           -- The length of data to encrypt/decrypt.
  @param [OUT]    output        -- preallocated output buffer.
  @param [OUT]    output_length -- preallocated output buffer.length
  @param [IN/OUT] err           -- The error information.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_EncDecBuffer( CI_Crypto_Algo algo, 
	CI_KEYGENINFO	*pInfo, CI_Crypt_Mode	mode, unsigned char	*input,  
	CI_u_int32 input_len, unsigned char	*output, CI_u_int32 *output_length,
	CI_ERROR *err);


/******************************************************************************
  Encrypt or decrypt a data buffer. This is meant for non-data encryption.

  @param [IN]     algo          -- The encryption algorithm.
  @param [IN]     mode          -- Encryption or Decryption.
  @param [IN]     pKey          --  Optional input params, NULL for hardcoded key.
  @param [IN]     input         -- The data pointer to encrypt/decrypt.
  @param [IN]     len           -- The length of data to encrypt/decrypt.
  @param [OUT]    output        -- preallocated output buffer.
  @param [OUT]    output_length -- preallocated output buffer.length
  @param [IN/OUT] err           -- The error information.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_EncDecBufferWithKey( CI_Crypto_Algo algo, 
	CI_KEY	*pKey, CI_Crypt_Mode	mode, unsigned char	*input,  
	CI_u_int32 input_len, unsigned char	*output, CI_u_int32 *output_length,
	CI_ERROR *err);

/******************************************************************************
  Encrypt a data buffer. This is meant for non-data encryption. The output buffer
  needs to be of enough size to contain an extra fixed size header of 16 byte length. 
  The internal header will contain a signature, version, algo & encryption scheme.

  @param [IN]     algo          -- Prefered algorithm.
  @param [IN]     input         -- The data pointer to encrypt/decrypt.
  @param [IN]     input_len           -- The length of data to encrypt/decrypt.
  @param [OUT]    output        -- preallocated output buffer.
  @param [OUT]    output_length -- preallocated output buffer.length
  @param [IN/OUT] err           -- The error information.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_EncryptBufferWithHeader(CI_Crypto_Algo algo, CI_u_int32 input_len, unsigned char *input,CI_u_int32 *output_length,
				 unsigned char	*output,CI_ERROR *err); 


/******************************************************************************
  Decrypt a data buffer. This is meant for non-data encryption. The input buffer
  should contain the 16 byte header mentioned above. IF that header is missing, 
  the function will try the 3DES encryption implemented for 11.5

  @param [IN]     input         -- The data pointer to encrypt/decrypt.
  @param [IN]     input_len           -- The length of data to encrypt/decrypt.
  @param [OUT]    output        -- preallocated output buffer.
  @param [OUT]    output_length -- preallocated output buffer.length
  @param [IN/OUT] err           -- The error information.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_DecryptBufferWithHeader(CI_u_int32 input_len, unsigned char *input,CI_u_int32 *output_length,
				 unsigned char	*output,CI_ERROR *err); 

/******************************************************************************
  Returns the length of actual encrypted data buffer for above functions. 
  The input buffer contains the 

  @param [IN]     input         -- The data pointer to encrypt/decrypt.
  @param [IN]     input_len     -- The length of data to encrypt/decrypt.
  @param [OUT]    buffer_length -- length of the encrypted data.
  Returns 0 on success, -1 on failure.
******************************************************************************/

CI_int32  CI_EncryptString(CI_Crypto_Algo input_algo, CI_Encode_Base base, unsigned char *input, CI_u_int32 input_len,
                                                        unsigned char   *output, CI_u_int32 *output_length, CI_ERROR *err);

CI_int32  CI_DecryptString(unsigned char *input, CI_u_int32 input_len,
                                                unsigned char   *output, CI_u_int32 *output_length, CI_ERROR *err);

CI_int32  CI_GetBufferLengthFromHeader(CI_u_int32 input_len, unsigned char *input,CI_u_int32 *buffer_length);

/******************************************************************************
  Returns the length of actual encryption header used by above functions. 
******************************************************************************/
CI_int32  CI_GetEncryptionHeaderSize();

/* This is meant for Upgrading the encryption if it has changed */
CI_int32  CI_MigrateEncryptionWithHeader(unsigned char *input, CI_u_int32 input_len,
						unsigned char	**output, CI_u_int32 *output_length, CI_ERROR *err);

/******************************************************************************
  Creates a Password Based key according to the given encryption alogrithm.

  @param [IN]  algo -- The algorithm for creating the key.
  @param [IN]  pInfo --  Optional input params 
  @param [OUT] Key1 -- The Generated key. For Symmetric algorithms, public key for PKI
  @param [OUT] Key2 -- Optional.The Generated Private key for ASymmetric algorithms.
  @param [IN/OUT] err -- The error information.
  Returns 0 for success, or -ve value in case of error.

	For Asymmetric algorithms, such as RSA, DSA, the pInfo parameter will be 
  ignored. Key1 will correspond to private key, Key2
  will correspond to public key.

******************************************************************************/

CI_int32  CI_GenerateKey(
	CI_Crypto_Algo algo,
	CI_KEYGENINFO *pInfo,
	CI_KEY *Key1,
	CI_KEY *Key2,
	CI_ERROR *err
);


/******************************************************************************
  Create a Message Digest(Hash) for given data.

  @param [IN]  algo -- Algorithm for digest, such as MD4, MD5, SHA1.
  @param [IN]  digest_data -- Input data.
  @param [IN]  digest_data_length -- Length of input data.
  @param [OUT] digest -- Preallocated buffer to store digest.
  @param [OUT] digest_length -- Preallocated buffer to store digest.
  @param [IN/OUT] err -- The error information.

  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_GenerateDigest(CI_Digest_Algo algo, 
	unsigned char *digest_data, CI_u_int32 digest_data_length,
	unsigned char *digest, CI_u_int32 *digest_length,
	CI_ERROR *err);





/******************************************************************************
  Utility function to creates a Password Based key according to the given 
  encryption alogrithm.

  @param [IN/OUT]  algo -- The favorable algorithm for creating the key.
	Notice final algorithm used may be different from one specified. and is returned
	in this argument.
  @param [IN]  pInfo --  input params, password, salt etc.
  @param [OUT] Key -- The Generated key, that should be used for encryption of data.
  @param [OUT] encVal -- Encrypted data for user validation.
	DES3_NEW, AES -- encrypted salt. 
	ATG -- Dont Care.
  @param [IN/OUT] encValSize -- Size of encVal.
	Specify the size of encVal1 buffer during call.
	Output returns correct size of the encVal

  @param [IN/OUT] err -- The error information.
  Returns 0 for success, or -ve value in case of error.

	For Asymmetric algorithms, such as RSA, DSA, the pInfo parameter will be 
  ignored. Key1 will correspond to private key, Key2
  will correspond to public key.

******************************************************************************/

CI_int32  CI_GenerateDataEncKey(
	CI_Crypto_Algo *algo,
	CI_KEYGENINFO *pInfo,
	CI_KEY **Key,
	unsigned char *encVal,
	CI_u_int32 *encvalSize,
	CI_ERROR *err
); /* DEPRECATED */

/******************************************************************************
  Utility function to creates a Password Based key according to the given 
  encryption alogrithm.

  @param [IN/OUT]  algo -- The favorable algorithm for creating the key.
	Notice final algorithm used may be different from one specified. and is returned
	in this argument.
  @param [IN]  pInfo --  input params, password, salt etc.
  @param [OUT] Key -- The Generated key, that should be used for encryption of data.
  @param [OUT] encSalt -- Encrypted data for user validation.
	DES3_NEW, AES -- encrypted salt. 
	ATG -- Dont Care.
  @param [IN/OUT] encSaltSize -- Size of encSalt.
	Specify the size of encSalt buffer during call.
	Output returns correct size of the encSalt

  @param [OUT] encSalt -- Encrypted data for user validation.
	DES3_NEW, AES -- encrypted salt. 
	ATG -- Dont Care.
  @param [IN/OUT] encSaltSize -- Size of encSalt.
	Specify the size of encSalt buffer during call.
	Output returns correct size of the encSalt
  @param [IN/OUT] err -- The error information.
  Returns 0 for success, or -ve value in case of error.

  @param [OUT] encRandomKey -- Encrypted random key for DES3_NEW,AES
	ATG -- Dont Care.
  @param [IN/OUT] encRandomKeySize -- Size of encRandomKey.
	Specify the size of encRandomKey buffer during call.
	Output returns correct size of the encRandomKey

  @param [IN/OUT] err -- The error information.
  Returns 0 for success, or -ve value in case of error.
	For Asymmetric algorithms, such as RSA, DSA, the pInfo parameter will be 
  ignored. Key1 will correspond to private key, Key2
  will correspond to public key.

******************************************************************************/

CI_int32  CI_GenerateDataEncKeyEx(
	CI_Crypto_Algo *algo,
	CI_KEYGENINFO *pInfo,
	CI_KEY **Key,
	unsigned char *encSalt,
	CI_u_int32 *encSaltSize,
	unsigned char *encRandomKey,
	CI_u_int32 *encRandomKeySize,

	CI_ERROR *err
);

/******************************************************************************
  Utility function to validates the Session Password, by matching the compare 
  values with decrypted
  compare value.

  @param [IN]  algo --  The Crypto algorithm
  @param [IN]  pInfo --  input params, password, salt etc.
  @param [IN]  cmpVal -- A value to be matched for authentication.
	DES3_OLD - The encKey1 will be supplied in this argument.
	DES3_NEW,AES - Encrypted Salt will be supplied in this argument.
	ATG - encrypted password will be supplied in this argument.
  @param [IN]  cmpValSize -- Size of cmpVal;
  @param [IN]  encCmpVal -- Encrpted value of cmpVal.
	DES3_OLD - The encKey2 will be supplied in this argument.
	DES3_NEW,AES - Dont care.
	ATG - Dont care.
  @param [IN]  encCmpValSize -- Size of encCmpVal;
  @param [OUT]  outkey -- The decrypted key.
  @param [IN/OUT] err -- The error information.
  Returns 0 for success, or -ve value in case of error.
******************************************************************************/
CI_int32  CI_ValidateSessionPassword(CI_Crypto_Algo algo, CI_KEYGENINFO* pInfo,
	unsigned char* cmpVal, CI_u_int32 cmpValSize, 
	unsigned char* encCmpVal,CI_u_int32 encCmpValSize, 
	CI_KEY **outkey, CI_ERROR *err);


/******************************************************************************
  Returns the Size of BAB key based upon the encryption algorithm.
  @param [IN]  algo -- The encryption algorithm.
  Returns the size of the key.
******************************************************************************/
CI_int32  CI_GetKeySize(CI_Crypto_Algo algo );


/******************************************************************************
  Returns the Size of BAB key based upon the encryption algorithm.
  @param [IN]  handle -- The encryption/decryption handle.
  Returns the size of the key.
******************************************************************************/
CI_int32  CI_GetKeySize_H(CI_HANDLE handle);


/******************************************************************************
  Returns the maximum size in bytes, by which the encrypted data can extend
  as compared to input data.
  @param [IN]  algo -- The encryption algorithm.
  Returns the maximum size extension.
******************************************************************************/
CI_int32  CI_GetMaxEncSizeExtension(CI_Crypto_Algo algo);


/******************************************************************************
  Returns the maximum size in bytes, by which the encrypted data can extend
  as compared to input data.
  @param [IN]  handle -- The encryption/decryption handle.
  Returns the maximum size extension.
******************************************************************************/
CI_int32  CI_GetMaxEncSizeExtension_H(CI_HANDLE handle);


/******************************************************************************
  Returns externally configured setting 
******************************************************************************/
CI_Compliance_Mode  CI_GetComplianceMode();

/******************************************************************************
  Sets the FIPS complaince mode switch. 
******************************************************************************/
CI_int32  CI_SetComplianceMode(CI_Compliance_Mode mode);

/******************************************************************************
  Returns most suitable algorithm available , if input algo is not available.
  if the input algo is available, it returns the same. 
******************************************************************************/
CI_Crypto_Algo  CI_GetSuitableAlgo(CI_Crypto_Algo algo, char *module);


/******************************************************************************
  Returns the name of algorithm for a given numeric algo. The output
  string should be allocated by the caller.
	The return value is the number of bytes needed or written to output.
******************************************************************************/
CI_int32  CI_EncAlogStringFormat(CI_Crypto_Algo algo, char *output);


/******************************************************************************
A common config file will allow users to configure encryption algorithms. A global
encryption scheme can be selected for all modules on a specific installation. This 
setting be selected for all modules calling the crypto API.
This file  may allow module specific encryption as a exception in future.
Such a module specific setting would override the global setting. 
******************************************************************************/
CI_int32  CI_GetDefaultCryptoAlgos( char* Module, 
	CI_Crypto_Algo *preferredSymmetricScheme, 
	CI_Digest_Algo	*preferredDigestScheme, 
	CI_Crypto_Algo *preferredPKIScheme);


/******************************************************************************
  Converts hex data buffer to string format. Internal encode mode would be base 64. 
  Should we allow encoding mode to be specified ? 

  @param [IN]  base -- The encode/decode base.
  @param [IN]  mode -- Whether to encode or decode the data
  @param [IN]  input -- The data pointer to encode/decode
  @param [IN]  input_len -- The length of data to encode/decode
  @param [OUT] output -- preallocated output buffer.
  @param [OUT] output_length -- The encrypted / decrypted data length.
  Returns 0 on success, -1 on failure.
******************************************************************************/
CI_int32  CI_EncodeDecodeData(CI_Encode_Base base, CI_Crypt_Mode mode, 
	unsigned char *input, CI_u_int32 input_length, 
	unsigned char *out_data, CI_u_int32 *out_data_len);



	
/******************************************************************************
  Returns  -1 on error
  @param [IN]  algo -- The encryption algorithm.
  @param [IN/OUT]  value -- CI_TRUE is set to value when inplace encryption
                                is supported else CI_FALSE is set
******************************************************************************/
CI_int32  CI_IsInplaceSupported(CI_Crypto_Algo algo, CI_BOOL *value);



/******************************************************************************
This will take a encryption algo as a param & return TRUE/FALSE if that is 
supported depending on FIPS mode.
@param [IN]  algo -- The encryption algorithm.
******************************************************************************/
CI_BOOL   CI_IsEncryptionAlgoAvailable(CI_Crypto_Algo algo);

	
CI_int32  CI_MapCryptoAlgoValue(char *name, CI_Crypto_Algo *pAlgo);
CI_int32  CI_MapDigestAlgoValue(char *name, CI_Digest_Algo *pAlgo);

CI_int32  CI_MapCryptoAlgoName(CI_Crypto_Algo algo, char *name);
CI_int32  CI_MapDigestAlgoName(CI_Digest_Algo algo, char *name);

#ifdef BAB_DYNAMIC_KEY_ASC
CI_int32 CI_KeyGetData(CI_KEY *pKey, unsigned char *pData, CI_u_int32 *pSize);

CI_KEY_TYPE CI_KeyGetType(CI_KEY *pKey);
#endif 

#ifdef BAB_DYNAMIC_KEY
CI_int32 CI_GenerateMasterKey(CI_KEY *pKey, char *pPwd, int nSize);

CI_int32 CI_EncryptKeyData(unsigned char *pInBuff, CI_int32 nInLen, unsigned char *pOutBuff, CI_int32 *nOutLen, char *pPwd, int nSize);
CI_int32 CI_DecryptKeyData(unsigned char *pInBuff, CI_int32 nInLen, unsigned char *pOutBuff, CI_int32 *nOutLen, char *pPwd, int nSize);

CI_int32 CI_GetHostName(char *pName, int nSize);
CI_int32 CI_GetKeyFileName(char *pName, int nSize);

/*
#define METHOD_PADDING_UNKNOWN	0
#define METHOD_NO_PADDING		1
#define METHOD_PADDING			2
*/
typedef	void*	EncryptionParamPtr;
typedef	struct EncryptionParam 
{
	int				nSize; /* The size of this structure. It¡¯s used to identify the structure¡¯s version*/
	CI_Crypto_Algo	algo;
	CI_ENC_SCHEME	encryptionScheme;
	//int				paddingScheme;
} CI_EncryptionParam;
CI_int32  CI_EncryptBufferWithHeaderEx(EncryptionParamPtr pEncParam, CI_u_int32 input_len, unsigned char *input,CI_u_int32 *output_length,
									 unsigned char	*output,CI_ERROR *err); 
CI_int32  CI_DecryptBufferWithHeaderEx(EncryptionParamPtr pEncParam, CI_u_int32 input_len, unsigned char *input,CI_u_int32 *output_length,
									 unsigned char	*output,CI_ERROR *err); 
/*
CI_int32  CI_EncDecBufferWithKey_S( CI_EncryptionParam encrPara,
								   const CI_KEY * basekey, CI_Crypt_Mode  mode, unsigned char *input,
								   CI_u_int32 input_length, unsigned char  *output, CI_u_int32 *output_length,
								   CI_ERROR *err);
*/

/******************************************************************************
The function used by configencr tool to implement key management.
Here we don't load dynamic key file 
******************************************************************************/
// upgrading fix 
CI_int32  CI_InitLibrary2(CI_ERROR *err,int);


typedef enum _POLICY_ID
{       
	ENC_KEY_POLICY_CHECK  = 1,
} POLICY_ID;

#define POLICY_CHECK_OK			0
#define POLICY_CHECK_FAIL		-1

/******************************************************************************
The function is used by auth client app layer to check whether encrypt data is 
agree policy
******************************************************************************/
CI_int32 CI_PolicyCheck(POLICY_ID id, void *pInBuff, CI_int32 nInLen);

#endif

// chefr03, for Password Management
//#ifdef R12_5_MANAGE_PASSWORD
/******************************************************************************
Get encrypt header from encrypted buffer
******************************************************************************/
CI_int32 CI_GetEncryptHeader(void* header, unsigned char* inBuff, size_t inBuffLen);

/******************************************************************************
Encrypt/Decrypt buffer with header by following:
1. Hard coded key 1
2. Default algorithm in configure file
******************************************************************************/
CI_int32 CI_EncryptBuffer(unsigned char* inBuff, size_t inBuffLen, unsigned char* outBuff, size_t* outBuffLen);
CI_int32 CI_DecryptBuffer(unsigned char* inBuff, size_t inBuffLen, unsigned char* outBuff, size_t* outBuffLen);

/******************************************************************************
Generate key by user provided password, the caller also should prepare pKey
1. pKey and pPwd should not be NULL
2. pPwd should not be empty string
3. nSize should larger than 0
4. pPwd is in plain text format

Return Value:
1. If succeed, it returns 0
2. Otherwise, failed
******************************************************************************/
CI_int32 CI_GenerateKeyByPwd(CI_KEY **pKey, unsigned char *pPwd, size_t nSize, CI_Crypto_Algo algo);
CI_int32 CI_GenerateRandomKey(CI_KEY** pKey, CI_Crypto_Algo algo);

/******************************************************************************
Encrypt/Decrypt buffer by user provided password
1. pwd, pwdLen, inBuff, inBufflen are input parameters
2. outBuff is output parameter, user should prepare the buffer
3. outBufflen is input/output parameter
4. pwd is in plain text format

Return Value:
1. If succeed, it returns 0
2. Otherwise, failed
******************************************************************************/
CI_int32  CI_EncryptWithHeaderByPwd(unsigned char* pwd,		size_t	pwdLen,
									unsigned char* inBuff,	size_t  inBuffLen,  
									unsigned char* outBuff,	size_t *outBuffLen);
CI_int32  CI_DecryptWithHeaderByPwd(unsigned char* pwd,		size_t  pwdLen,
									unsigned char* inBuff,	size_t  inBuffLen,
									unsigned char* outBuff, size_t *outBuffLen);

/******************************************************************************
Encrypt key with new password
Return Value:
1. If succeed, it returns 0
2. Otherwise, failed
3. Both oldPwd and newPwd are in plain text format
******************************************************************************/
CI_int32	CI_EncryptKeyByNewPwd(unsigned char* newPwd,	 size_t  newPwdLen,
								  unsigned char* inKeyBuff,  size_t  inKeyBuffLen,
								  unsigned char* outKeyBuff, size_t* outKeyBuffLen);

/******************************************************************************
Encrypt buffer with pwd and encrypted key
Return Value:
1. If succeed, it returns 0
2. Otherwise, failed
******************************************************************************/
CI_int32  CI_EncryptWithHeaderByKey(unsigned char* keyBuff,	size_t	keyBuffLen,
									unsigned char* inBuff,	size_t  inBuffLen,
									unsigned char* outBuff,	size_t *outBuffLen);

/******************************************************************************
Decrypt buffer with pwd and encrypted key
Return Value:
1. If succeed, it returns 0
2. Otherwise, failed
******************************************************************************/
CI_int32  CI_DecryptWithHeaderByKey(unsigned char* keyBuff,	size_t	keyBuffLen,
									unsigned char* inBuff,	size_t  inBuffLen,
									unsigned char* outBuff,	size_t *outBuffLen);

/* ADMK encryption method enum */
typedef	enum	_ADMK_PROTECTION_METHOD
{
	ADMK_METHOD_FAILED		= 0,	// Failed to get method, internal error
	ADMK_METHOD_ARCSERVE	= 1,	// CI_SCHEME_USE_HARDKEY1	Use hard coded key encryption		
	ADMK_METHOD_PASSPHRASE	= 2,	// CI_SCHEME_USE_ADMK		OR CI_SCHEME_USE_PWD			
	ADMK_METHOD_HDKEY2		= 3,	// CI_SCHEME_USE_HARDKEY2	Use hard coded key 2 encryption	
	ADMK_METHOD_HDKEY3		= 4,	// CI_SCHEME_USE_HARDKEY3	Use hard coded key 3 encryption
	ADMK_METHOD_NOPADDING	= 5,	// CI_SCHEME_NO_PADDING
	ADMK_METHOD_DYNAMIC		= 6,	// CI_SCHEME_USE_DYNKEY
	ADMK_METHOD_UNKNOWN		= 7		// CI_SCHEME_UNSPECIFY		OR Unknown encryption method found
} ADMK_PROTECTION_METHOD;

/******************************************************************************
Get the ADMK encryption method from its header
Return	Value:
	1. If succeed, it returns 0
	2. Otherwise, failed

Output parameter:
	protectionMethod:	See declaration of ADMK_PROTECTION_METHOD
******************************************************************************/
CI_int32  ADMK_GetProtectionMethod(/* [in] */		const unsigned char*	encryptedADMK,
								   /* [in] */		const size_t			encryptedADMKLen,
								   /* [out] */		ADMK_PROTECTION_METHOD*	protectionMethod);

#ifndef _ADMKHANDLE_DEF
#define	_ADMKHANDLE_DEF
typedef struct _ADMK_KEY_CACHE_ 
{
	unsigned char	pEncKey[512];
	size_t			pEncKeyLen;
	int				bInitialized;
} ADMK_KEY_CACHE, *ADMKHANDLE;
#endif

typedef enum	_ADMK_PASSPHRASE_FILE_VERSION
{
	ADMK_PASSPHRASE_FILE_VERSION_12_V = 12,
	ADMK_PASSPHRASE_FILE_VERSION_14	  = 14,
	ADMK_PASSPHRASE_FILE_VERSION_15	  = 15
} ADMK_PASSPHRASE_FILE_VERSION;

typedef	struct _PASSPHRASE_FILE_HEADER 
{
	unsigned int	signature;
	unsigned int	activePasspharseIndex;
	unsigned int	version;
} PASSPHRASE_HEADER, *PPASSPHRASE_HEADER;

typedef struct _PASSPHRASE_FILE_RECORD 
{
	size_t			buffLen;
	unsigned char	buff[256];		// Approximately, it can hold 128 bytes password
} PASSPHRASE_RECORD, *PPASSPHRASE_RECORD;

/******************************************************************************
Cache the hard coded key
******************************************************************************/
CI_int32	ADMK_ImportKey(/* [out] */			ADMKHANDLE*		phADMK,
						   /* [in] */			unsigned char*	keyBuff,
						   /* [in] */			size_t			keyBuffLen,
						   /* [in] */			unsigned char*	passphrase,
						   /* [in] */			size_t			passphraseLen);

/******************************************************************************
// ADMK_ExportKey: Get the password encrypted key buffer
// Please note that phADMK still contains hard coded key
// pwd is plain text password, it returns the key
// If pwd is NULL, the key is hard coded key encrypted
// Otherwise, it is encrypted by pwd
******************************************************************************/
CI_int32	ADMK_ExportKey(/* [in] */			ADMKHANDLE		phADMK,
						   /* [in]  */			unsigned char*	passphrase,
						   /* [in]  */			size_t			passphraseLen,
						   /* [out] */			unsigned char*	keyBuff,
						   /* [in/out] */		size_t*			keyBuffLen);

/******************************************************************************
// ADMK_CreateKey: Generate random key and cache it with hard coded
******************************************************************************/
CI_int32	ADMK_CreateKey(/* [out] */			ADMKHANDLE*		phADMK);

/******************************************************************************
// Destroy the cached key by handle
// Used to destroy the key generated by ADMK_CreateKey()
******************************************************************************/
CI_int32	ADMK_DestroyKey(/* [in/out] */		ADMKHANDLE*		phADMK);

/******************************************************************************
Encrypt/Decrypt buffer by cached key
******************************************************************************/
CI_int32	ADMK_EncryptBuffer(/* [in] */		ADMKHANDLE		hADMK,
							   /* [in] */		unsigned char*	inBuff,
							   /* [in] */		size_t			inBuffLen,
							   /* [out] */		unsigned char*	outBuff,
							   /* [in/out] */	size_t*			outBuffLen);
CI_int32	ADMK_DecryptBuffer(/* [in] */		ADMKHANDLE		hADMK,
							   /* [in] */		unsigned char*	inBuff,
							   /* [in] */		size_t			inBuffLen,
							   /* [out] */		unsigned char*	outBuff,
							   /* [in/out] */	size_t*			outBuffLen);


/******************************************************************************/

#ifndef	MAX_PATH
#define	MAX_PATH	260
#endif

/******************************************************************************
// Get the file list which should be skipped during backup
// Parameter:
//	[out] pBuffer:		  The buffer used to store the file list
//						  It can be NULL, the API returns the necessary size of
//						  the buffer instead in this case
//						  If this API succeeded, the buffer contains the files
//						  These files are separated by '\0', and ended by '\0\0'
//						  For example, "file1\0file2\0\file3\0\0"
// [in/out] pulBufferSize: The size of pBuffer
//						  If pBuffer is NULL or the buffer isn't enough,
//						  this API will set the necessary size of the buffer to
//						  this parameter and returns CI_INVALID_BUFFERSIZE
// Return Value:
//	CI_INVALID_BUFFERSIZE	The pulBufferSize isn't enough
//  CI_SUCCESS				Succeeded
//	Other					Failed
******************************************************************************/
CI_int32	ADMK_GetBackupSkippedFileList(/* [out] */	 wchar_t*	pBuffer,
										  /* [in/out] */ unsigned long* pulBufferSize);

/******************************************************************************
// Get the file list which critical for DR purpose
// Parameter:
//	[out] pBuffer:		  The buffer used to store the file list
//						  It can be NULL, the API returns the necessary size of
//						  the buffer instead in this case
//						  If this API succeeded, the buffer contains the files
//						  These files are separated by '|'
//						  For example, "file1|file2"
// [in/out] pulBufferSize: The size of pBuffer
//						  If pBuffer is NULL or the buffer isn't enough,
//						  this API will set the necessary size of the buffer to
//						  this parameter and returns CI_INVALID_BUFFERSIZE
// Return Value:
//	CI_INVALID_BUFFERSIZE	The pulBufferSize isn't enough
//  CI_SUCCESS				Succeeded
//	Other					Failed
******************************************************************************/
CI_int32	ADMK_DRGetAuthDBFiles(/* [out] */	 wchar_t*	pBuffer,
								  /* [in/out] */ unsigned long* pulBufferSize);

/******************************************************************************
// Get the ADMK cache file path name
******************************************************************************/
CI_int32	ADMK_GetPassPhraseFilePathW(/* [out] */	wchar_t*		filePath,
									    /* [in] */	unsigned long	filePathLen,
									    /* [out] */	int*			exists);

/******************************************************************************
// Save pass phrase into file: passPhrase is in plain text format
******************************************************************************/
CI_int32	ADMK_SavePassPhrase(/* [in] */	unsigned char*	passphrase,
								/* [in] */	size_t			passphraseLen);

/******************************************************************************
// Read pass phrase from file: passPhrase is in plain text format
******************************************************************************/
CI_int32	ADMK_LoadPassPhrase(/* [out] */		unsigned char*	passphrase,
								/* [in/out] */	size_t*			passphraseLen);

/******************************************************************************
// Read pass phrase list from file: all of them are in plain text format
******************************************************************************/
CI_int32	ADMK_LoadPassPhraseList(/* [out] */		PPASSPHRASE_RECORD	pRecordList,
									/* [in/out] */	unsigned int*		pRecordCount);

/******************************************************************************
// Compare the passPhrase with the one in configure file
******************************************************************************/
CI_int32	ADMK_IsPassPhraseMatch(/* [in] */	unsigned char*	passphrase,
								   /* [in] */	size_t			passphraseLen);

//#endif	// R12_5_MANAGE_PASSWORD

typedef enum	_ADMK_IMPORT_KEY_STATUS
{
	ADMK_IKS_DEFAULT				= 0X0,
	ADMK_IKS_HARDCODED_KEY			= 0X1,
	ADMK_IKS_PASSPHRASE_KEY			= 0X2,

	ADMK_IKS_NEED_SET_ADMK_TO_DB	= 0x1000,

	ADMK_IKS_NEED_SET_PASSPHRASE	= 0x2000,	// Internally used only
	ADMK_IKS_NEED_SET_CAROOT_PWD	= 0x4000	// Internally used only

} ADMK_IMPORT_KEY_STATUS;

/******************************************************************************
// Import ADMK with the help of passphrase data file and caroot password
// Return value:
//		CI_SUCCESS		Succeed
//		Others			Failed
// Note:
//		As this API succeed, you need invoke ADMK_DestroyKey() to release
//		phADMK at proper position
// pStatus and succeeding operations:
//		This API is used by DBENG currently, as the return value is CI_SUCCESS
//		we need check pStatus for succeeding operations:
//	pStatus & ADMK_IKS_HARDCODED_KEY:	Indicate value, needn't other operations
//	pStatus & ADMK_IKS_PASSPHRASE_KEY:	Indicate value, needn't other operations
//	pStatus & ADMK_IKS_NEED_SET_ADMK_TO_DB:
//			This value means that we get the passphrase, but it isn't equal to 
//			caroot password, so that we need set the new ADMK into DB
//			If newADMK specified and the buffer length is enough, it contains
//			the caroot password encrypted ADMK, you can use it as well
******************************************************************************/
CI_int32	ADMK_ImportKeyEx(/* out */		ADMKHANDLE*		phADMK,
							 /* in */		unsigned char*	encryptedADMK,
							 /* in */		size_t			encryptedADMKLen, 
							 /* in */		unsigned char*	carootPassword,
							 /* in */		size_t			carootPasswordLen, 
							 /* out */		unsigned char*	newADMK,
							 /* in/out */	size_t*			newADMKLen,
							 /* out */		ADMK_IMPORT_KEY_STATUS*	pStatus);

#ifdef __cplusplus
}
#endif

#endif /* CRYPTO_INTERFACE_H */

