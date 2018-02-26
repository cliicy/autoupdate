/******************************************************************************
*
*        filename  :      HAV2PUtil.h
*        created   :      2010/12/04
*        Written by:      baide02
*        comment   :      Some commonly used API when do V2P for VCM.
                          Such as parsing CTF2, create VOL bitmap by disk bitmap.
*
******************************************************************************/
#ifndef _HA_V2P_UTILS_
#define _HA_V2P_UTILS_

#ifdef	HAUTILITY_EXPORTS
	#define	HaUtilityAPI	extern "C" __declspec(dllexport)	
#else
	#define	HaUtilityAPI	extern "C" __declspec(dllimport)	
#endif

#include "HAVmIoIntf.h"
#include "HACommonDef.h"
#include <string>

struct IDiskImageVirtual;

namespace HaUtility
{
//==============================================================================================
// structures and interfaces
//==============================================================================================
struct IDrVolCallback
{
protected:
    virtual ~IDrVolCallback() = 0 {};
public:
    virtual void Release() = 0; // destory self
    virtual int GetCurrDiskPathBySig(DWORD dwDiskSig, std::wstring& wstrVhdPath) = 0;
    virtual int CreateIVDiskDiff(DWORD dwDiskSig, HaVhdUtility::IVDiskDiff** ppDiskDiff, IDiskImageVirtual** pIDiskRead) = 0;
};

/*
* Used to pass ST_DR_VOL_ATTR between modules.
*/
struct IDRVolAttrWrapper
{
protected:
    virtual ~IDRVolAttrWrapper() = 0 {};
public:
    virtual void Release() = 0;

    // The pointer returned has the same lifetime with IDRVolAttrWrapper
    // Should not to free the returned pointer.
    virtual ST_DR_VOL_ATTR* GetRawData() = 0;
};

struct IHAIoStm
{
protected:
    virtual ~IHAIoStm() = 0 {};
public:
    virtual void Release() = 0; // Destroy self
    virtual int Open() = 0;
    virtual int Close() = 0;

    /**
    * Read
    * @lpBuffer - [out] Return the data read
    * @cbToRead - [in]  Bytes to read
    * @pcbRead  - [out] Bytes read
    * @llOffset - [in]  starting offset
    * return value:
    *   0 success, ERROR_HANDLE_EOF eof, others fail.
    * Remarks:
    *  if llOffset == -1, will read from internal position sequencely.
    *     If Read() is call on the first time with llStartingOffset == -1, read from beginning of stream(0)
    *  If llOffset != -1, will read from llOffset in the stream
    *
    *  return ERROR_HANDLE_EOF
    *  means reach the end of VDisk.
    */
    virtual int Read(void* lpBuffer, unsigned long cbToRead, 
                     unsigned long* pcbRead, __int64 llOffset) = 0;

    /**
    * Seek
    * @nMethod - FILE_BEGIN, FILE_CURRENT, FILE_END
    */
    virtual int Seek(__int64 llDisToMove, __int64* pllNewPos, int nMethod) = 0;

    /**
    * If this stream is valid. Will be valid after is Open(), invalid after is close().
    */
    virtual bool IsValid() = 0;
};


/**
* This is a common CTF2 reader used to read 2.ctf for DR, by parsing different 
* CTF2 stream format. 
* For example, for HyperV, the CTF2 is represented as a local file, Open() a stream 
* returned by CreateHAFileIoStm() is OK; for Esx, by input a self-implemented 
* IHAIoStm, this reader can parse a CTF2 file reside on remote ESX server.
*/
struct IHACtfReader
{
protected:
    virtual ~IHACtfReader() = 0 {};
public:
    virtual void Release() = 0; // Destroy self
    // Caller in charge of the free of pStm. Caller should IHACtfReader->Release() or IHACtfReader->close() 
    // before pStm->Release(). After the pStm is free, all methods of this class should not be 
    // called except Open()another IHAIoStm.
    // If want to read from file, use IHAIoStm returned by CreateHAFileIoStm().
    virtual int	Open(IHAIoStm* pStm) = 0;
	virtual int Close() = 0;
    virtual int GetVolInfo(const wchar_t* pwszVolGuid, /*out*/IDRVolAttrWrapper** ppAttrWrapper) = 0;
    virtual int GetVolBitmap(const wchar_t* pwszVolGuid, LARGE_INTEGER liStartingLcn, /*IN*/ int nBufLen, 
        /*OUT*/ PST_DR_VOL_BITMAP pBitmap) = 0;
};


//==============================================================================================
// functions
//==============================================================================================

/**
* Get volume bitmap and write it into file, which name is pwszVolBmpFile.
* @pwszAdrCfgFile       [in]    -
* @pwszVolGuid          [in]    -
* @pwszVolBmpFile       [out]   -
* @pCb                  [in]    -
*/
HaUtilityAPI
int GenerateVolBmpFile(const wchar_t* pwszVolGuid,
                       const wchar_t* pwszAdrCfgFile,
                       IDrVolCallback* pCb,
                       const wchar_t* pwszVolBmpFile);
typedef int (*PFN_GenerateVolBmpFile)(const wchar_t* pwszVolGuid,
                       const wchar_t* pwszAdrCfgFile,
                       IDrVolCallback* pCb,
                       const wchar_t* pwszVolBmpFile);

/**
* 
* Create the IVVolDiff by volume bitmap file (such the bitmap file returned by GenerateVolBmpFile)
* return 0 success, others fail
*/
HaUtilityAPI
int CreateVolDiffByBmpFile(const wchar_t* pwszBmpPath, HaVhdUtility::IVVolDiff** ppVolDiff);
typedef int (*PFN_CreateVolDiffByBmpFile)(const wchar_t* pwszBmpPath, HaVhdUtility::IVVolDiff** ppVolDiff);

/**
* Get vol info (volume name, option, UUID, disk info, etc) by adrconfig file.
* Commonly used by both HyperV v2p and Esx v2p.
*/
HaUtilityAPI
int GetVolInfo(/*in*/const wchar_t* pwszVolGuid,/*in*/const wchar_t* pwszAdrCfgFile, 
               /*in*/IDrVolCallback* pCb, /*out*/IDRVolAttrWrapper** ppAttrWrapper);
typedef int (*PFN_GetVolInfo)(/*in*/const wchar_t* pwszVolGuid,/*in*/const wchar_t* pwszAdrCfgFile, 
                              /*in*/IDrVolCallback* pCb,/*out*/IDRVolAttrWrapper** ppAttrWrapper);

/**
 This API will always success even if pwszPath is NULL,
 on that case, the IHAIoStm->Open will fail.
*/
HaUtilityAPI
IHAIoStm* CreateHAFileIoStm(const wchar_t* pwszPath, DWORD dwDesireAccess, DWORD dwShareMode);
typedef IHAIoStm* (*PFN_CreateHAFileIoStm)(const wchar_t* pwszPath, DWORD dwDesireAccess, DWORD dwShareMode);



/**
* The common read CTF2 logic, create a IHACtfReader to read the volume bitmap and volume info
*
* This API will always success.
*/
HaUtilityAPI
IHACtfReader* CreateCtf2Reader(void);

typedef IHACtfReader* (*PFN_CreateCtf2Reader)(void);

} //namespace HaUtility




#endif //_HA_V2P_UTILS_

