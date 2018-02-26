/**************************************************************************
Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

Program Name:  Win32Agent Compression Encryption API Header
      Version:  Release 4.0, Rev 1.0
Version Date:  December 1, 1996
**************************************************************************/
#include "cryptintf.h"

#ifndef _ASDCEN_H
#define _ASDCEN_H

#pragma pack(1)
#ifdef __cplusplus
extern "C" {
#endif

#ifndef _ASDCEN_DLL_
#define ASDCENAPI _declspec(dllimport)
#else
#define ASDCENAPI _declspec(dllexport)  
#endif

#define NFUNC	4

#define INIT	0	// initialization
#define CALL1	1	// compression - encryption function
#define CALL2	2	// decompression - decryotion function
#define END	3	

#define ZIP_ANY 0
#define ZIP_PKW 1
#define ZIP_GNU 2

#define LZW_ANYSIZE 0x7fffffffL

#ifndef __ARGS__
#define MODERN
#	ifndef __COMPILER_KCC__
#		ifndef __GNUC__ /* f...d compiler! */
#			define __ARGS__(x) x
#		endif
#	endif
#endif
#ifndef __ARGS__
# define __ARGS__(x) ()
#endif

#define LZ_INP_TYPE char *
#define LZ_OUT_TYPE char *

#define DELTA_COMPRESS 2048
#define MAX_COMPRESS_SIZE (32768)
#define MAX_COMPRESS_SIZE_UNIX	65536
typedef int (*LPFNCALLFUNCTION)(void *);

// compression info 
typedef struct _COMPRESSION_INFO 
{
	unsigned long dwtype;	// type of compression
	unsigned long dwlevel;	// level of compression
	unsigned long padding[8];	// reserved for future use
}	COMPRESS_INFO, *PCOMPRESS_INFO;

#define COMPRESS_INFO_SIZE sizeof(COMPRESS_INFO)

// input parameter for compression functions
typedef struct _cstruct
{
	LZ_INP_TYPE		in_data;
	LZ_OUT_TYPE		out_data;

	unsigned long 	in_data_len;
	unsigned long 	out_data_len;

//	unsigned int	ctype, clevel;  /* deflate type and level */
//	unsigned int	unctype;		/* inflate type */
	
	COMPRESS_INFO		info;
	unsigned long 		crc;			/* crc returned after deflation */
	unsigned long		c_data_len;		/* the length of deflated data */
	unsigned long		len;			/* the length of inflated data
									input paramter for inflate function */
}	CSTRUCT, *LPCSTRUCT;

// defines for size of des key and salt
#define MAX_DESKEY_SIZE 24
#define MAX_SALT_SIZE 8


// Define for encryptoin/decryption mode
#define ASCSATG_MODE 0
#define ETPKI_ENCRYPTION_MODE 1	// Old 3DES encryption
#define ETPKI_DECRYPTION_MODE 2

// Define for data mode
#define B_DATA		1	// Beginning of data
//#define M_DATA		2	// Middle of data
#define E_DATA		4	// End of data

// input parameter for enryption functions
typedef struct _estruct
{
	char*			pKey;
	char*			pText;

	char*			StreamMemory;
	char*			pOutBuff;
	char*			pDesKey;
	char*			CryptState;
	
	unsigned long	nTextLen;
	unsigned long   nOutLen;
	unsigned long	nOutBuffSize;

	unsigned char	nKeyLen;
	char			mode; // ASCSATG_MODE, ETPKI_ENCRYPTION_MODE, ETPKI_DECRYPTION_MODE
	char			nDataFlag; // B_DATA for begining of data and E_DATA for last data to encrypt
	char			cPadding;

	char			cryptoMode;
	char			cryptoAlgo;
	char			cryptoFlags;
	unsigned int	cryptoScheme;
	int				bInplacement;

	int				nEncSaltLen;
	char*			pEncSalt;

	int				nEncRandomKeyLen;
	char*			pEncRandomKey;

	int				nEncDeltLen;	//used during decryption.
	CI_KEY			*bkKey;
	CI_HANDLE		handle;
}	ESTRUCT, *LPESTRUCT;


#ifdef BAB_DYNAMIC_KEY_ASC

typedef struct tagCSCQuery{
	CI_Crypto_Algo	algo;
	/* here maybe we need add other query condition */
}CSCQuery, *PCSCQuery;

typedef struct tagConnSecurityContext{
	unsigned char	keyData[32];
	CI_u_int32		keyDataLen;
	CI_Crypto_Algo	algo;
	CI_u_int32		scheme;
}ConnSecurityContext;

typedef LPVOID LPConnSecurityContext;

#define MAX_ENCRYPT_KEY_DATA_LENGTH	1024

typedef struct tagEncryptedSecurityContext{
	unsigned char encryptSessKeyData[MAX_ENCRYPT_KEY_DATA_LENGTH];
	int encryptLength;
	CI_Crypto_Algo algo;
	CI_u_int32	scheme;
}EncryptedSecurityContext;

#endif

#if 0 //CDR disable compress header
typedef struct _COMPRESS_HEADER
{
	unsigned int	level;		// level of compression
	unsigned long 	crc;		// crc returned after deflation
	unsigned long	len;		// original length of inflated data - 
								// input paramter for inflate function
	unsigned long	outlen;		// actual length of data
}	COMPRESS_HEADER, *PCOMPRESS_HEADER;
#endif

typedef enum _DLLCALL { init, call1, call2, end} DLLCALLTYPE;
typedef enum _OPTYPE {encr, compr} OPTYPE;

typedef struct _COMPRESS_ENCRYPT_DATA
{
	char	szDllName[MAX_PATH];
	char	pszFunctions[NFUNC][MAX_PATH];
	
	LPFNCALLFUNCTION lpfnFunctions[NFUNC];
	void* hDll;  // a HANDLE
	// call1 - compression or encryption
	// call2 - decompression or decryption
}	CE_DATA, *LPCE_DATA;

int  unzalloc __ARGS__((void));
int  unzopen  __ARGS__((LZ_INP_TYPE, int));
int  unzread  __ARGS__((char *, unsigned));
void unzfree  __ARGS__((void));
int  unzclose __ARGS__((void));

int  zipalloc __ARGS__((void));
int  zipcreat __ARGS__((LZ_OUT_TYPE, int, int, int));
int  zipwrite __ARGS__((char *, unsigned));
void zipfree  __ARGS__((void));
long zipclose __ARGS__((unsigned long *, unsigned long *, unsigned long *));


int  lzwalloc __ARGS__((int));
int  lzwcreat __ARGS__((LZ_OUT_TYPE, long, int));
int  lzwwrite __ARGS__((char *, unsigned));
void lzwfree  __ARGS__((void));
long lzwclose __ARGS__((void));

int  lzwmark  __ARGS__((int));
int  lzwopen  __ARGS__((LZ_INP_TYPE));
int  lzwread  __ARGS__((char *, unsigned));
void lzwrelease __ARGS__((void));

//int	GetErrnumber(void);
//int	GetoCount();
//int	GetoTotal();
//void AddTooTotal(int);

void crcbegin __ARGS__((void));
void updcrc   __ARGS__((unsigned char *, unsigned));
void addcrc   __ARGS__((unsigned char));
unsigned long getcrc __ARGS__((void));

extern int lzerror;
extern char *lzerrlist[];

#define ZNOPEN		0	/* Uninitialized zip processing */
#define ZNOMEM		1	/* Not enough memory */
#define ZMAGIC		2	/* Bad magic header */
#define ZUNSUP		3	/* Reserved field or compression method */
#define ZHDEOF		4	/* EOF while processing header */
#define ZMOULD		5	/* Invalid compressed data */
#define ZNOEOF		6	/* More data to process at close */
#define ZBADSZ		7	/* Real size differs from recorded */
#define BADCRC		8	/* Bad CRC */
#define ZWRITE		9	/* Error writing output file */
#define ZERROR		10	/* Generic/internal error */

#define ZCALLERROR	11
#define ZNOTCALL	12
#define ZEXCEPTION	13
#define ZCSATGERROR	14	/* ATG Encryption/Decryption error */
#define ZASTNGERROR	15	/* ASM TNG Encryption/Decryption error */
#define ZNOERROR	20	/* Successul operation */
#define ZINSUFFICIENTMEM 21
#define ZALGONOTAVAILABLE 22	//algo not available

// Define for ETPKI Encryption error
#define ETPKI_NOERROR				20
#define ETPKI_INITERR				21 // failed to initialize encryption
#define ETPKI_MEMERR				22 // failed to allocate memory
#define ETPKI_KEYGENERR				23 // unable to create key generator
#define ETPKI_KEYERR				24 // unable to generate 3 des key
#define ETPKI_INIT_LIB_ERR			25 // unable to initialize library
#define ETPKI_ENCRYPT_ESTIMATE_ERR	26 // Unable to estimate encryption
#define ETPKI_ENCRYPT_DATA_ERR		27 // failed to encrypt  data
#define ETPKI_PASSWORD_BASEKEY_ERR  28 // Failed to generate password-based key
#define ETPKI_END_ENCRYPTION		29 // Failed to end encryption/decryption
#define ETPKI_EXPORT_KEY			30 // Failed to export key
#define ETPKI_IMPORT_KEY			31 // Import key error





ASDCENAPI	int	ASDeflate (void * data);
ASDCENAPI	int	ASDeflateGNU (void * data);
ASDCENAPI	int ASInflate (void * data);

// Add by Steven Yan  5/25/2006
// Description: 
// >>
ASDCENAPI int ASDeflateEx( void *data );
ASDCENAPI int ASDeflateNS( void *data );
ASDCENAPI int ASInflateNS(void *data);
//<<  Add by Steven Yan  5/25/2006

#if 0  // maybe we will use them lather
ASDCENAPI	int	ASDeflatePKW (void * data);
ASDCENAPI	int ASInflatePKW (void * data);
ASDCENAPI	int ASInflateGNU (void * data);

ASDCENAPI	int ASInitCompression(void *p);
ASDCENAPI	int ASEndCompression(void *p);

ASDCENAPI	unsigned char*	ASEncryptPassword(unsigned char *pszPassword);
ASDCENAPI	unsigned char*	ASDecryptPassword(unsigned char *pszEncrypted);
ASDCENAPI	void ASEnableEncryption(unsigned char *pKey, int nLen);
ASDCENAPI	void ASDisableEncryption(void);
ASDCENAPI	void ASEncryptText(char *pText, unsigned long nLen);
ASDCENAPI	void ASDecryptText(char *pText, unsigned long nLen);

ASDCENAPI	int ASEncrypt (void *);
ASDCENAPI	int ASDecrypt (void *);

//stacker functions
ASDCENAPI int ASLZSInitCompression(void *p);
ASDCENAPI int ASLZSEndCompression(void *p);
ASDCENAPI int ASLZSCompress(void *data);
ASDCENAPI int ASLZSDecompress(void *data);
#endif

// ASCSATG functions (Shared by Etpki encryption)
ASDCENAPI int ASCSATGInitEncryption (PVOID pe);
ASDCENAPI int ASCSATGEndEncryption (PVOID pe);
ASDCENAPI int ASCSATGEncrypt (PVOID pe);
ASDCENAPI int ASCSATGDecrypt (PVOID pe);

int ASCSEncryptKeyUsePassword(char *password, char *salt, 
	const unsigned char* keydata, int keydata_size,int *encryptedKeyLength,
	char *encryptedKey);

int ASCSDecryptKeyUsePassword( char* password, char *salt, 
	unsigned char* data, int data_size,
	unsigned char* DecryptBuf, int *DecryptSize);

int ASCSConvertDataWithKey(int mode, char *data, int dataSize, char *key, int keySize, int *OutDataSize, char *OutData);

void ASCSGetKey(char *masterKey, char *salt);
void ASCSGetSalt(char *salt, int seed);
void ASCSSetLoggingFunc(void *pFunc, BOOL bDRRestore);

// ASNT ASM-TNG functions
ASDCENAPI int ASTNGEncrypt (PVOID pe);
ASDCENAPI int ASTNGDecrypt (PVOID pe);

// New 3DES Encryption and Decryption functions
ASDCENAPI int BABEncrypt(char *pInData, unsigned long InDataLen, char *pOutData, unsigned long *pOutDataBufSize);
ASDCENAPI int BABDecrypt(char *pInData, unsigned long InDataLen, char *pOutData, unsigned long *pOutDataBufSize);
ASDCENAPI int BABEncrypt_Ex(char *pInData, unsigned long InDataLen, char *pOutData, unsigned long *pOutDataBufSize);
ASDCENAPI int BABEncrypt_Ex2(unsigned long algo, char *pInData, unsigned long InDataLen, char *pOutData, unsigned long *pOutDataBufSize);
ASDCENAPI int BABDecrypt_Ex(char *pInData, unsigned long InDataLen, char *pOutData, unsigned long *pOutDataBufSize);
ASDCENAPI int BABGetDefaultCAROOT(char *pOutData, unsigned long OutDataBufSize);
ASDCENAPI int BABGetDefaultCAROOTPWD(char *pOutData, unsigned long OutDataBufSize);

// used for FIPSnon-data encryption/decryption

/*
 * This API is used to encrypt/decrypt data with a hardcoded key.
 *
 * input parameter: pe is a pointer to ESTRUCT structure.
 *
 * return 0 for success.
 *
 * Encrytion: 
 *		input:
 *			ESTRUCT::cryptoMode		- should be CI_ENCODE 
 *			ESTRUCT::cryptoAlgo		- encryption algorithm, specify CI_Crypto_Algo_DEFAULT to use default algorithm
 *			ESTRUCT::cryptoScheme	- could be 0 to use the default value
 *			ESTRUCT::pText			- point to the input data buffer
 *			ESTRUCT::nTextLen		- input data size
 *			ESTRUCT::pOutBuff		- point to the output data buffer that receives cipher text, for an inplace algorithm such as TNG or XOR enc, 
 *									-   it can be the same as input buffer
 *			ESTRUCT::nOutBuffSize	- output buffer size. In case of TNG/XOR enc or input buffer is 16 bytes aligned and scheme is 0, 
 *									-   the output buffer size can be the same input buffer size. Otherwise, it should be 16 bytes bigger than the input buffer size.
 *			
 *
 *		output:
 *			ESTRUCT::pOutBuff		- has the encrypted cipher text
 *			ESTRUCT::nOutLen		- the cipher text size
 *			ESTRUCT::cryptoAlgo		- the encryption algorithm used in case of CI_Crypto_Algo_DEFAULT as input
 *			ESTRUCT::cryptoScheme	- the encryption scheme flags used in case of 0 is specified as input
 *
 * Decryption:
 *		input:
 *			ESTRUCT::cryptoMode		- should be CI_DECODE 
 *			ESTRUCT::cryptoAlgo		- the encryption algorithm used during encryption
 *			ESTRUCT::cryptoScheme	- the scheme flag used dring encryption
 *			ESTRUCT::pText			- point to the cipher text buffer to be decrypted
 *			ESTRUCT::nTextLen		- the cipher buffer size
 *			ESTRUCT::pOutBuff		- point to the output data buffer that receives decrypted data.
 *									-   For TNG or XOR enc, the output buffer can be the same as input buffer.
 *			ESTRUCT::nOutBuffSize	- output buffer size
 *
 *		output:
 *			ESTRUCT::pOutBuff		- has the decrypted data
 *			ESTRUCT::nOutLen		- the decrypted data size
 *
 *
 */
ASDCENAPI int ASETPKICrypt(PVOID pe);
ASDCENAPI CI_Crypto_Algo ASETPKIGetSuitableAlgo(CI_Crypto_Algo algo);
ASDCENAPI BOOL ASETPKIIsCryptAlgoAvailable(CI_Crypto_Algo algo);

/*
 * This API is used to encrypt/decrypt data with encryption metadata embeded with the cipher text.
 *
 * input parameter: pe is a pointer to ESTRUCT structure.
 *
 * return 0 for success.
 *
 * Encrytion: 
 *		input:
 *			ESTRUCT::cryptoMode		- should be CI_ENCODE 
 *			ESTRUCT::pText			- point to the input data buffer
 *			ESTRUCT::nTextLen		- input data size
 *			ESTRUCT::pOutBuff		- point to the output data buffer that receives cipher text
 *			ESTRUCT::nOutBuffSize	- output buffer size, the output buffer should be at least 32 bytes bigger than the input buffer
 *			ESTRUCT::cryptoAlgo		- encryption algorithm, specify CI_Crypto_Algo_DEFAULT to use default algorithm
 *
 *		output:
 *			ESTRUCT::pOutBuff		- has the encrypted cipher text
 *			ESTRUCT::nOutLen		- the cipher text size
 *
 * Decryption:
 *		input:
 *			ESTRUCT::cryptoMode		- should be CI_DECODE 
 *			ESTRUCT::pText			- point to the cipher text buffer to be decrypted
 *			ESTRUCT::nTextLen		- the cipher buffer size
 *			ESTRUCT::pOutBuff		- point to the output data buffer that receives decrypted data
 *			ESTRUCT::nOutBuffSize	- output buffer size, the output buffer should be at least 32 bytes bigger than the input buffer
 *
 *		output:
 *			ESTRUCT::pOutBuff		- has the decrypted data
 *			ESTRUCT::nOutLen		- the decrypted data size
 *
 *
 */
ASDCENAPI int ASETPKICryptWithHeader(PVOID pe);
ASDCENAPI int ASETPKIGetBufferLengthFromHeader(char *input, unsigned long inputSize, unsigned long *bufferLength);
ASDCENAPI int ASETPKIGetEncryptionHeaderSize();
ASDCENAPI int ASETPKIGetMaxEncSizeExtension(CI_Crypto_Algo algo);

// used for FIPS data encryption/decryption
ASDCENAPI int ASETPKIGetEncryptionAlgo(PVOID pe);
ASDCENAPI int ASETPKIInitEncryption (PVOID pe, BOOL bUnixSession);
ASDCENAPI void ASETPKIGetSaltString( CI_Crypto_Algo algo, char* password,ULONG startTime, USHORT sessionType, char* rootDirectory, char* description, char* salt);
ASDCENAPI int ASETPKICreateEncryptionKey(char* password, char* pSalt, ESTRUCT *estruct);
ASDCENAPI int ASETPKIInitEncDecData(PVOID pe);
ASDCENAPI int ASETPKIEncrypt (PVOID pe);
ASDCENAPI int ASETPKICloseEncDecData(PVOID pe);
ASDCENAPI int ASETPKIEndEtpkiEncrypt(PVOID pe);
ASDCENAPI int ASETPKIGenerateRestoreKeyAndValidatePassword( PVOID pe, int size1, int size2, unsigned char *encKey1, 
					unsigned char *encKey2, unsigned char *password, int passwordSize, char* pSaltString, int nSaltStringSize);

#ifdef BAB_DYNAMIC_KEY_ASC
ASDCENAPI int ASETPKIGetSessionKey(CI_Crypto_Algo algo, CI_u_int32	scheme, unsigned char * keyData,int * keyDataLen);
ASDCENAPI LPConnSecurityContext ASETPKIGetSecurityContext(PCSCQuery query);
ASDCENAPI LPConnSecurityContext ASETPKICreateSecurityContext(CI_Crypto_Algo algo, unsigned char * keyData,int keyDataLen,CI_u_int32 encryptScheme);
ASDCENAPI int ASETPKICryptWithSeContext(LPVOID pe,LPConnSecurityContext seContext);
ASDCENAPI int ASETPKIKeyPairDataCrypt(CI_Crypto_Algo algo, CI_u_int32 scheme, int mode,
									  unsigned char *keyData,CI_u_int32 keyDataLen,
									  unsigned char *inData,CI_u_int32 inDataLen,
									  unsigned char *outData,CI_u_int32 * outDataLen);
ASDCENAPI int ASETPKICreateKeyPairData(CI_Crypto_Algo algo, 
									   unsigned char *pubKeyData,CI_u_int32 * pubKeyDataLen,
									   unsigned char *privKeyData,CI_u_int32 * privKeyDataLen);
ASDCENAPI int ASETPKIReleaseSecurityContext(LPConnSecurityContext lpSecurityContext);
ASDCENAPI LPConnSecurityContext ASETPKIDuplicateSecurityContext(LPConnSecurityContext lpSecurityContext);

ASDCENAPI int ASETPKIEncryptCSC(
							LPConnSecurityContext pConnSecurityContext,
							EncryptedSecurityContext * pEncryptSecurityContext);
ASDCENAPI int ASETPKIDecryptCSC(
							LPConnSecurityContext * pConnSecurityContext,
							EncryptedSecurityContext EncryptSecurityContext);
ASDCENAPI void ASETPKIDeinitialize(BOOL bUnloadLibrary);

typedef int (*LPFN_ASETPKIEncryptCSC)(LPConnSecurityContext pConnSecurityContext,
								   EncryptedSecurityContext * pEncryptSecurityContext);
typedef int (*LPFN_ASETPKIDecryptCSC)(LPConnSecurityContext * pConnSecurityContext,
								   EncryptedSecurityContext EncryptSecurityContext);
typedef int (*LPFN_ASETPKIReleaseSecurityContext)(LPConnSecurityContext lpSecurityContext);
typedef LPConnSecurityContext (*LPFN_ASETPKIDuplicateSecurityContext)(LPConnSecurityContext lpSecurityContext);

#endif;

#ifdef __cplusplus
}
#endif

#pragma pack()
#endif // _ASDCEN_H