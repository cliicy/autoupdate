#pragma once
#include "DbgLog.h"
#include "vmdkimg.h"

#ifdef AFBITMAPMANAGER_EXPORTS
#define AFBITMAPMGRAPI __declspec(dllexport)
#else
#define AFBITMAPMGRAPI __declspec(dllimport)
#endif

#define FREE_BITMAP(p)			{ if(p) { free(p);		p=NULL; } }
#define RELEASE_BITMAP(p)		{ if(p) { p->Release(); p=NULL; } }

#define ALIGNMENTD(x, y) (((x) + (y) - 1) / (y) * (y))
/*
*  The base class of Volume Bitmap
*  Normally the volume bitmap is saved in memory. But on 32bit system if the volume bitmap is very
*  larger, it might be saved in file. 
*  Every time when a PVOLUME_BITMAP_BUFFER is requried, the caller can call GetBitmap to get it, and 
*  after finish using the bitmap, it can call Reclaim reclain this bitmap
*/
class IVolumeBitmap
{
public:
	virtual void		Release( ) = 0;

	//
	// Create a blank bitmap. llSize is the bitmap size in bits
	//
	virtual BOOL		CreateBlankBitmap( LONGLONG llSize ) = 0;

	//
	// retrieve volume bitmap by handle
	//
	virtual BOOL		GetVolumeBitmapByHandle(HANDLE hVolume) = 0;

	//
	// retrieve driver bitmap by handle
	// 
	virtual BOOL		GetDriverBitmapByHandle(HANDLE hVolume, LONGLONG llDiskLengthInfoVCB, DWORD dwDriverBlockSize) = 0;

	// 
	// read bitmap information from a file.
	//
	virtual DWORD		ReadBitmapFromFile(LPCWSTR lpszFile) = 0;

	//
	// set each bit of this bitmap to 1
	//
	virtual BOOL		SetToFullBitmap( ) = 0;

	//
	// get the bitmap size in bits
	//
	virtual LONGLONG	GetBitmapSize( ) = 0;

	//
	// get the bitmap buffer. To reduce memory usage, you must call "ReclaimBitmap"
	//
	virtual PVOLUME_BITMAP_BUFFER GetBitmap( ) = 0;

	//
	// reclaim the bitmap
	//
	virtual void		ReclaimBitmap( ) = 0;

	//
	// get a segment of bitmap buffer
	// 
	virtual PBYTE		GetBitmapBuffer( DWORD dwOffet, DWORD dwSizeToGet ) = 0;

	//
	// get single byte from the bitmap buffer.
	//
	virtual BOOL		GetByteOfBitmapBuffer(DWORD dwOffset, PBYTE pResult) = 0;

	//
	// set single byte into the bitmap buffer
	//
	virtual BOOL		SetByteOfBitmapBuffer(DWORD dwOffset, BYTE bData) = 0;

	//
	// get the used clusters. ( the total clusters that bit is 1 )
	//
	virtual LONGLONG	GetUsedClusters() = 0;

	//
	// get the last used cluster. Used to calclate the minimum volume size
	//
	virtual LONGLONG	GetLastUsedCluster( ) = 0;

	//
	// find used clusters
	//
	virtual BOOL		FindUsedClusters( DWORD dwStartCluster, DWORD dwMaxCountToFind,	LPDWORD pdwStartPos, LPDWORD pdwEndPos) = 0;
	
	//
	// find continuous used clusters
	//
	virtual BOOL		FindUsedContinuouslyClusters( DWORD dwStartCluster, DWORD dwMaxCountToFind, LPDWORD pdwStartPos, LPDWORD pdwEndPos, BOOL bLimiteSize = FALSE) = 0;

	//
	// get specified bit 
	//
	virtual BOOL		GetBit(LARGE_INTEGER i64BitIndex, LPBOOL pbResult) = 0;

	//
	// set specified bit 
	//
	virtual BOOL		SetBit(LARGE_INTEGER i64BitIndex, BOOL bResult) = 0;

	//
	// AND bitmap with others
	//
	virtual BOOL		AndBitmap( IVolumeBitmap* pBitmap2) = 0;

	//
	// OR bitmap with others
	//
	virtual BOOL		ORBitmap( IVolumeBitmap* pBitmap2) = 0;

	//
	// Fill the driver bitmap according to the volume bitmap
	//	
	virtual BOOL		ConvertToDriverBitmap( IVolumeBitmap* pVolumeBitmap, DWORD dwDriverBlockSize, DWORD dwVolumeClusterSize ) = 0;

	//
	// Fill the volume bitmap according to the driver bitmap
	//	
	virtual BOOL		ConvertFromDriverBitmap( IVolumeBitmap* pDriverBitmap, DWORD dwDriverBlockSize, DWORD dwVolumeClusterSize ) = 0;

	//
	// save bitmap to a file
	//
	virtual INT			SaveToFile( LPCWSTR szFilePath ) = 0;
};

inline DWORD BitmapHeaderSize()
{
	VOLUME_BITMAP_BUFFER bitmap={0};
	return (DWORD)((LPBYTE)(&(bitmap.Buffer)) - (LPBYTE)(&bitmap));
}

inline DWORD BitmapSizeInBytes( LONGLONG llSizeInBits )
{
	DWORD dwSize = (DWORD)(ALIGNMENTD(llSizeInBits, 8)/8);
	dwSize += BitmapHeaderSize();
	return dwSize;
}

inline DWORD CountOneByte(BYTE byteValue)
{
	DWORD llCount = 0;
	if (0 == byteValue)
		llCount = 0;
	else if (0xff == byteValue)
		llCount = 8;
	else
	{
		while (byteValue)
		{
			++ llCount;
			byteValue &= (byteValue - 1);
		}
	}
	return llCount;
}

extern "C" AFBITMAPMGRAPI IVolumeBitmap* CreateVolumeBitmapObject(BOOL bInMemory = TRUE);

