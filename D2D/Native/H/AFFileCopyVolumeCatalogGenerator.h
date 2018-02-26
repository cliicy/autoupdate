#pragma once 

#include<vector>
#include<string>
using namespace std;
#include <windows.h>
#include <winioctl.h>
#include "IVolumeBitmap.h"
#include "FileCopyStreanInterface.h"
class D2D_ARCHIVE_JOB
{
public:
	INT						jobNumber;
	TCHAR*					pszSourceVolume;
	TCHAR*					pszSourceVolumeGuid;
	TCHAR*					pszSourceVolumeShadowCopyPath;
	TCHAR*					pszPathToImageMetaDataFiles;
	TCHAR*      			pszArchiveFListPath;
	BOOL					bDoArchive;
	TCHAR*					szVolumeType;
	PSHORT					p_isJobCancelled;
	BYTE					nDebugLevel;
	TCHAR*					pszHomeDirectoryPath;
	TCHAR*                  szHostName;
	IVolumeBitmap*			pChangedBitmapBuffer;
	vector<wstring>*		pListOfDirectories;
	FileCopyPolicyManager  *pCurrentFileCopyManager;
	ArchivePolicyManager   *pCurrentArchivePolicyManager;
	IFileCopyStream* StreamObject;
	D2D_ARCHIVE_JOB()
		:jobNumber(0), pszSourceVolume(NULL),pszSourceVolumeGuid(NULL),pszSourceVolumeShadowCopyPath(NULL),
		pszPathToImageMetaDataFiles(NULL), pszArchiveFListPath(NULL), bDoArchive(TRUE), szVolumeType(NULL),
		p_isJobCancelled(0), nDebugLevel(0), pszHomeDirectoryPath(NULL), pChangedBitmapBuffer(NULL),
		pListOfDirectories(NULL)
	{	
	}
};

typedef D2D_ARCHIVE_JOB* PD2D_ARCHIVE_JOB;
ULONG __declspec(dllexport) WINAPI GenerateFListFile(PD2D_ARCHIVE_JOB pArchiveJob);
typedef ULONG __declspec(dllexport)(WINAPI *PGENERATEFLISTFILE)(PD2D_ARCHIVE_JOB pArchiveJob);