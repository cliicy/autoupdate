#pragma once
#include "AvailableBIUpdateDll.h"


#define return_if_canceled(ret) \
	if( isUpdateCanceled() )\
				{\
		return  ret;\
				}\

#define break_if_canceled(ret) \
	if( isUpdateCanceled() )\
				{\
		lLastError = ret;\
		break;\
				}\

#define break_if( b ) \
	if( b )\
		break;\

class CUDPBIUpdateJob //: public CBaseUpdateJob
{
public:
	CUDPBIUpdateJob();

	virtual ~CUDPBIUpdateJob(void);

public:
	virtual LONG		Run( );

public:
	//
	// select the update server to download the latest update
	//
	virtual	LONG	selectUpdateServer();

	//
	// detect the update server
	//
	virtual	LONG	detectUpdateServer(IDownloader* pDownloader, ARCUPDATE_SERVER_INFO* pSvrInfo, CAvailableBIUpdateDll** ppUpdateInfo);

	//
	// download the update
	//
	virtual LONG	downloadUpdate();

	//
	// download file from specified server 
	//
	virtual LONG	downloadFile(UP_FILE_INFO* pFileToDownload);

	//
	// download file from specified server 
	//
	virtual LONG	downloadFile(IDownloader* pDownloader, const wstring& strUrlOfFile, const wstring& strDstFile);

	//
	// run actions post download
	//
	virtual LONG	postDownload();

	//
	// generate the status.xml file
	//
	virtual LONG	generateStatusFile();

	//
	// detect if the update is downloaded already
	//
	virtual LONG	isUpdateDownloaded(CAvailableBIUpdateDll* pUpdateInfo);

	//
	// validate if the update is ok
	//
	virtual LONG	validateUpdateInfo(CAvailableBIUpdateDll* pUpdateInfo);

	//
	// create alert mail if necessary
	//
	//virtual void	crerate_MailAlert(BOOL bSucceed =TRUE);

	//
	// handle http errors
	//
	virtual void	handleHttpError(LONG lLastError, ARCUPDATE_SERVER_INFO* pSvrInfo);

	virtual LONG	handleErrors(LONG lLastError);

	//
	// safe delete file
	//
	virtual BOOL	safeDeleteFile(const wstring& strFile);

	//
	// safe move file
	//
	virtual BOOL	safeMoveFile(const wstring& strSrcFile, const wstring& strDstFile);

	//
	// move files / sub folders to target folder
	//
	virtual DWORD	moveFolder(const wstring& strSrcFolder, const wstring& strDstFolder, bool bCleanDestination = false);

	//
	// test if ther server is availble.
	//
	virtual LONG	testServerConnection(IDownloader* pDownloader, const wstring& strUrl);

	//
	// check if the file is well signed
	//
	virtual BOOL	isFileSignatureValid(const wstring& strFile);


	//
	// run a specified action
	//
	virtual LONG	runAction(UP_POST_ACTION* pAction);

	//
	// init the job script
	//
	virtual	LONG	initJobScript();

	//
	// detect if job canceled.
	//
	virtual	BOOL	isUpdateCanceled();

	//
	// clean a folder
	//
	virtual LONG	cleanFolder(const wstring& strFolder, BOOL bIncludeSelf = FALSE);

protected:
	CAvailableBIUpdateDll*	m_pUpdateInfo;

protected:
	CDbgLog					m_log;
	UDP_UPDATE_SETTINGS     m_upSettings;
	
};
