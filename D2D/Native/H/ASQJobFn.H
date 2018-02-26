/**************************************************************************
This program is an unpublished work fully protected by the United States
Copyright laws and is considered a trade secret belonging to Computer 
Associates International, Inc.

Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.

 Program Name:  ARCserveIT Queue Job API Header
      Version:  Release 6.7, Rev 1.0
 Version Date:  October 6, 1998
**************************************************************************/

#ifndef _INC_ASQJOBFN
#define _INC_ASQJOBFN

#include <rpc.h>
#include <asjmon.h>

#ifdef __cplusplus
extern "C" {
#endif

#if !defined boolean
typedef unsigned char boolean;
#endif
typedef boolean *PBOOLEAN;


#include <qsi.h>

ASRET _ASAddQueueJob(
  handle_t hMachID,
  ULONG   ulQueueID,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulJobID);

ASRET _ASAddQueueJobW(handle_t hMachID,
											ULONG   ulQueueID,
											USHORT  usLevel,
											PCHAR   pBuf,
											ULONG   ulBufSize,
											PULONG  pulJobID);

ASRET _ASAddQueueJobImage(
  handle_t hMachID,
  ULONG   ulQueueID,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulJobID);

ASRET ASAddQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulJobID);
// Oripin: UNICODE_JIS Support redbh03
ASRET ASAddQueueJobW(
					 PWSZ     pszServer,
					 PWSZ     pszQueue,
					 USHORT  usLevel,
					 PCHAR   pBuf,
					 ULONG   ulBufSize,
					 PULONG  pulJobID); 

//CUI
ASRET ASQAddQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulJobID);

//adding job with user authentication
ASRET ASAuthAddQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulJobID,
  PSZ    pszUserName,
  PSZ    pszPassword,
  PSZ    pszDomainName,
  ULONG  ulSecLevel
  );

//CUI
//adding job with user authentication
ASRET ASQAuthAddQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulJobID,
  PSZ    pszUserName,
  PSZ    pszPassword,
  PSZ    pszDomainName,
  ULONG  ulSecLevel
  );

ASRET ASAddQueueInterleavedJobs(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulJobID);

ASRET ASAuthAddQueueInterleavedJob(
  PSZ    pszServer,
  PSZ    pszQueue,
  USHORT  usLevel,
  PCHAR  pBuf,
  ULONG  ulBufSize,
  PULONG  pulJobID,
  PSZ    pszUserName,
  PSZ    pszPassword,
  PSZ    pszDomainName,
  ULONG  ulSecLevel);

ASRET _ASGetQueueJob(
  handle_t hMachID,
  ULONG   ulQueueID,
  ULONG   ulJobID,
  HFILE   hJobFile,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET _ASGetQueueJobW(
  handle_t hMachID,
  ULONG   ulQueueID,
  ULONG   ulJobID,
  HFILE   hJobFile,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET _ASGetQueueJobImage(
  handle_t hMachID,
  ULONG   ulQueueID,
  ULONG   ulJobID,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET ASGetQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID,
  HFILE   hJobFile,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET ASQGetQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID,
  HFILE   hJobFile,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize,
  UCHAR   cHostType);  //CUI

ASRET ASQGetQueueJobW(
					 PWSZ     pszServer,
					 PWSZ     pszQueue,
					 ULONG   ulJobID,
					 HFILE   hJobFile,
					 USHORT  usLevel,
					 USHORT  usDetail,
					 PCHAR   pBuf,
					 ULONG   ulBufSize,
					 PULONG  pulTotalSize,
					 UCHAR   cHostType);  //CUI // dayra01 translation lib api
ASRET ASGetQueueJobW(
  PWSZ    pszServer,
  PWSZ    pszQueue,
  ULONG   ulJobID,
  HFILE   hJobFile,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET _ASSetQueueJob(
  handle_t hMachID,
  ULONG   ulQueueID,
  ULONG   ulJobID,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  USHORT  usSetMember);

ASRET _ASSetQueueJobImage(
  handle_t hMachID,
  ULONG   ulQueueID,
  ULONG   ulJobID,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  USHORT  usSetMember);

//modifying job with user authentication
ASRET ASAuthSetQueueJob(
  PSZ     pszServer, 
  PSZ     pszQueue,
  ULONG   ulJobID,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf, 
  ULONG   ulBufSize, 
  USHORT  usSetMember,
  PSZ    pszUserName,
  PSZ    pszPassword,
  PSZ    pszDomainName,
  ULONG  ulSecLevel)  ;

// CUI start
ASRET ASQAuthSetQueueJob(
  PSZ     pszServer, 
  PSZ     pszQueue,
  ULONG   ulJobID,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf, 
  ULONG   ulBufSize, 
  USHORT  usSetMember,
  PSZ    pszUserName,
  PSZ    pszPassword,
  PSZ    pszDomainName,
  ULONG  ulSecLevel)  ;
// CUI end
// Oripin: UNICODE_JIS Support :redbh03
ASRET ASQAuthSetQueueJobW(
						 LPWSTR     pszServer, 
						 LPWSTR     pszQueue,
						 ULONG   ulJobID,
						 USHORT  usLevel,
						 USHORT  usDetail,
						 PCHAR   pBuf, 
						 ULONG   ulBufSize, 
						 USHORT  usSetMember,
						 LPWSTR    pszUserName,
						 LPWSTR    pszPassword,
						 LPWSTR    pszDomainName,
						 ULONG  ulSecLevel)  ;

ASRET ASSetQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  USHORT  usSetMember);

// CUI start
ASRET ASQSetQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  USHORT  usSetMember);
// CUI end

ASRET ASQSetQueueJobW(
					 LPWSTR     pszServer,
					 LPWSTR     pszQueue,
					 ULONG   ulJobID,
					 USHORT  usLevel,
					 USHORT  usDetail,
					 PCHAR   pBuf,
					 ULONG   ulBufSize,
					 USHORT  usSetMember);
ASRET _ASDeleteQueueJob(
  handle_t hMachID,
  ULONG   ulQueueID,
  ULONG   ulJobID,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize);

ASRET ASDeleteQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize);

//CUI
ASRET ASQDeleteQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  UCHAR   cHostType);   //CUI

ASRET ASQDeleteQueueJobW(
						PWSZ     pszServer,
						PWSZ     pszQueue,
						ULONG   ulJobID,
						USHORT  usLevel,
						PWCHAR   pBuf,
						ULONG   ulBufSize,
						UCHAR   cHostType);   //CUI
ASRET ASAuthDeleteQueueJob(
  PSZ    pszServer,
  PSZ    pszQueue,
  ULONG    ulJobID,
  USHORT  usLevel,
  PCHAR    pBuf,
  ULONG    ulBufSize,
  PSZ    pszUserName,
  PSZ    pszPassword,
  PSZ    pszDomainName,
  ULONG    ulSecLevel);

//CUI
//delete job with user authentication
ASRET ASQAuthDeleteQueueJob(
  PSZ    pszServer,
  PSZ    pszQueue,
  ULONG    ulJobID,
  USHORT  usLevel,
  PCHAR    pBuf,
  ULONG    ulBufSize,
  PSZ    pszUserName,
  PSZ    pszPassword,
  PSZ    pszDomainName,
  ULONG    ulSecLevel,
  UCHAR     cHostType) ; //CUI

ASRET ASQAuthDeleteQueueJobW(
							PWSZ    pszServer,
							PWSZ    pszQueue,
							ULONG    ulJobID,
							USHORT  usLevel,
							PWCHAR    pBuf,
							ULONG    ulBufSize,
							PWSZ    pszUserName,
							PWSZ    pszPassword,
							PWSZ    pszDomainName,
							ULONG    ulSecLevel,
							UCHAR     cHostType) ; //CUI // dayra01 translation lib api
ASRET _ASCancelQueueJob(
  handle_t hMachID,
  ULONG   ulQueueID,
  ULONG   ulJobID);

ASRET ASAuthCancelQueueJob(
  PSZ   pszServer,
  PSZ   pszQueue,
  ULONG ulJobID,
  PSZ  pszUserName,
  PSZ  pszPassword,
  PSZ  pszDomainName,
  ULONG  ulSecLevel);

//CUI
//cancel job with user authentication
ASRET ASQAuthCancelQueueJob(
  PSZ   pszServer,
  PSZ   pszQueue,
  ULONG ulJobID,
  PSZ  pszUserName,
  PSZ  pszPassword,
  PSZ  pszDomainName,
  ULONG  ulSecLevel,
  UCHAR  cHostType); //CUI

ASRET ASQAuthCancelQueueJobW(
							PWSZ   pszServer,
							PWSZ   pszQueue,
							ULONG ulJobID,
							PWSZ  pszUserName,
							PWSZ  pszPassword,
							PWSZ  pszDomainName,
							ULONG  ulSecLevel,
							UCHAR  cHostType); //CUI // dayra01 translation lib api
ASRET ASCancelQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID);

//CUI
ASRET ASQCancelQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  ULONG   ulJobID,
  UCHAR  cHostType) ; //CUI

ASRET ASEnumQueueJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  USHORT  usDetail,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalEntries,
  PULONG  pulTotalSize,
  PVOID   pvResumeKey);

ASRET ASSaveQueueJobToScript(
  PSZ     pszScriptName,
  PSZ     pszFileName,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize);

ASRET ASSaveQueueJobToScriptW(
							  LPWSTR    pszScriptName,
							  LPWSTR    pszFileName,
							  USHORT	usLevel,
							  PCHAR		pBuf,
							  ULONG		ulBufSize); // dayra01

ASRET ASLoadQueueJobFromScript(
  PSZ     pszScriptName,
  PSZ     pszFileName,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET ASLoadQueueJobFromScriptForGUI(
  PSZ     pszScriptName,
  PSZ     pszFileName,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET ASLoadQueueJobFromScriptForGUIW(
									  PWSZ     pszScriptName,
									  PWSZ     pszFileName,
									  USHORT  usLevel,
									  PWCHAR   pBuf,
									  ULONG   ulBufSize,
									  PULONG  pulTotalSize); // dayra01
ASRET ASLoadQueueJobFromScriptW(
  PWSZ    pszScriptName,
  PWSZ    pszFileName,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET ASLoadQueueJobFromScript2(
  PSZ     pszScriptName,
  PSZ     pszFileName,
  USHORT  usLevel,
  PCHAR   *pBuf,
  PULONG  pulTotalSize);

ASRET ASLoadQueueJobImage(
  PSZ     pszFileName,
  USHORT  usLevel,
  PCHAR   *pBuf,
  PULONG  pulTotalSize);

ASRET ASAuthAccess( 
  PSZ      pszServer,
  PSZ      pszUserName,
  PSZ      pszPassword,
  PSZ      pszDomainName,
  ULONG    ulSecLevel,
  ULONG    ulDesiredAccess,
  PBOOLEAN  pbReturn);

//CUI
ASRET ASQAuthAccess( 
  PSZ      pszServer,
  PSZ      pszUserName,
  PSZ      pszPassword,
  PSZ      pszDomainName,
  ULONG    ulSecLevel,
  ULONG    ulDesiredAccess,
  PBOOLEAN  pbReturn,
   UCHAR    cHostType); //CUI

//CUI
ASRET   ASQIsJobServiceActive( PSZ szComputerName, UCHAR cHostType);

//CUI
ASRET  ASQStartQueueMonitor(PSZ  pszServer, LPVOID hWindow, UCHAR cHostType, LPVOID pObject);
ASRET  ASQStartQueueMonitorW(PWSZ  pszServer, LPVOID hWindow, UCHAR cHostType, LPVOID pObject);
//CUI
ASRET  ASQStopQueueMonitor(PSZ  pszServer, UCHAR cHostType, LPVOID pObject);

//CUI
ASRET  ASQEnumQueueJobs(PSZ  pszServer, UCHAR cHostType, void *hJobWnd,int  *nQueueJobs);

ASRET  ASQEnumQueueJobsW(PWSZ  pszServer, UCHAR cHostType, void *hJobWnd,int  *nQueueJobs);
//CUI
ASRET  ASQCheckQueueRights(PSZ  pszServer, PSZ pszUser, PULONG pQRights, UCHAR cHostType );

ASRET  ASQCheckQueueRightsW(PWSZ  pszServer, PWSZ pszUser, PULONG pQRights, UCHAR cHostType ); // dayra01 translation lib api
//CUI
ASRET  ASQModifyJobStatusOrTime ( PSZ pszServer, ULONG ulJobID, ULONG ulExecTime, USHORT usJobStatus,
                  UCHAR  cHostType);

ASRET  ASQModifyJobStatusOrTimeW ( PWSZ pszServer, ULONG ulJobID, ULONG ulExecTime, USHORT usJobStatus,
								 UCHAR  cHostType);// Oripin: UNICODE_JIS Support kalsa03
//CUI
ASRET  ASQEnumJobScripts(PSZ  pszServer, UCHAR cHostType, void *hJobWnd);

//CUI
ASRET  ASQGetQueueStatus(PSZ  pszServer, UCHAR cHostType, PUSHORT pusQueueStatus);
//CUI
ASRET  ASQSetQueueStatus(PSZ  pszServer, UCHAR cHostType, USHORT usQueueStatus);

//CUI
ASRET  ASQGetSetName(PSZ  pszServer,  ULONG   ulJobID, UCHAR cHostType,  PSZ pszSetName);

ASRET  ASQGetSetNameW(PWSZ  pszServer,  ULONG   ulJobID, UCHAR cHostType,  PWSZ pszSetName); // dayra01 translation lib api
///////////////////////////////////////////////////////
//
// evereset; specialized queueu job api
//
ASRET ASAddCommandLineJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR  pszCommandline,
  USHORT usJobtype,  
  LONG   tTimeToExecute,
  PULONG pulJobID
);

//Oripin_JIS_Support: kumga04
ASRET ASAddCommandLineJobW(
						   PWSZ     pszServer,
						   PWSZ     pszQueue,
						   USHORT  usLevel,
						   PWCHAR  pszCommandline,
						   USHORT usJobtype,  
						   LONG   tTimeToExecute,
						   PULONG pulJobID
						   );

ASRET ASAddGenericTaskJob(
  PSZ     pszServer,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR  pszCommandline,
	PCHAR pszBeforeJobCmdln,
	PCHAR pszAfterJobCmdln,
  USHORT usJobtype,
	PASSCHED pschedule,
  PULONG pulJobID,
  PCHAR jobdescription
);

ASRET ASAddGenericTaskJobEx(
  PSZ     pszServerInfo,
  PSZ     pszQueue,
  USHORT  usLevel,
  PCHAR  pszCommandline,
	PCHAR		pszBeforeJobCmdln,
	PCHAR   pszAfterJobCmdln,
	USHORT usJobtype,
  PASSCHED pschedule,
  PULONG pulJobID,
	PCHAR  jobdescription,
	PSZ	  pszUser,
	PSZ	  pszPassword
);

ASRET ASAddGenericTaskJobExW(
							 PWSZ     pszServerInfo,
							 PWSZ     pszQueue,
							 USHORT  usLevel,
							 PWCHAR  pszCommandline,
							 PWCHAR		pszBeforeJobCmdln,
							 PWCHAR   pszAfterJobCmdln,
							 USHORT usJobtype,
							 PASSCHED pschedule,
							 PULONG pulJobID,
							 PWCHAR  jobdescription,
							 PWSZ	  pszUser,
							 PWSZ	  pszPassword
							 ); // dayr0a1 translation lib api
/*
//cheetah Dana, for Multiprocessing
//I do not understand why I have to put this here, 
//but I do not have time to fix it now
ASRET ASOpenJobMon(
	PSZ pszMachine, 
	ULONG ulJobMonID, 
    ULONG flags, 
	PASJMON pASJMon);

ASRET ASGetJobMonInfo(PSZ pszMachine, 
					  ULONG ulJobMonID, 
					  PASJMON pASJMon,
                      PASJOBMON pJobMonRet);

ASRET ASSetJobMonFlags( 
     PSZ      pszServer,
     ULONG	  ulJobMonID,
     BOOL	  bOn,
     ULONG	  flags);
*/

///////////////////////////////////////////////////////
ASRET ASQGetQJobItemList(PSZ pszMachine, ULONG ulQueueID, PQJOBITEM pQJobItemList, 
                          ULONG ulBufSize, PULONG pnJobItems, char *pszUserName, char *pszPassword, UCHAR cHostType);

ASRET ASQSetRunNOWJobFlags(
	PSZ			pszServer,
	PSZ			pszUserName,
	PSZ			pszPassword,
	ULONG		ulJobID,
	UCHAR		cType,
	ULONG		ulExecTime);
ASRET ASQSetRunNOWJobFlagsW(
						   PWSZ			pszServer,
						   PWSZ			pszUserName,
						   PWSZ			pszPassword,
						   ULONG		ulJobID,
						   UCHAR		cType,
						   ULONG		ulExecTime); // dayra01 translation lib api
	
//wanhe05, 2006-5-31, for Master Child Separation -- begin
ASRET ASQSaveNodeScript(PCHAR pszScriptFile, PASNODE pNode);
ASRET ASQSaveNodeScriptW(PWCHAR pszScriptFile, PASNODE pNode);
ASRET ASQGetNodeScript(PCHAR pszScriptFile, PASNODE *ppNode);
ASRET ASQGetNodeScriptW(PWCHAR pszScriptFile, PASNODE *ppNode);
ASRET ASQFreeNodeScript(PASNODE pNode);

//wanhe05, 2006-5-31, for Master Child Separation -- end
	
//wanhe05, 2006-6-30, for CJQ -- begin
ASRET _ASExecuteQueueJob(
  handle_t hMachID,
  JOBEXECPARM *pJobParm, 
  PULONG pulProcessID,
  PULONG pulProcessTime);
  
ASRET ASExecuteQueueJob(
  PSZ     pszServer,
  JOBEXECPARM *pJobParm, 
  PULONG pulProcessID,
  PULONG pulProcessTime);
//wanhe05, 2006-6-30, for CJQ -- end	

ASRET ASIsJobRun(
	PSZ pszMachine, 
	ULONG ulQueueID, 
	PULONG pnActiveJobCount);
// Oripin: UNICODE_JIS Support :redbh03
ASRET ASIsJobRunW(
				  wchar_t* pszMachine, 
				  ULONG ulQueueID, 
				  PULONG pnActiveJobCount);

//Check if there is SQL multi-striping job
ASRET ASIsSQLMultiStripJobInQueue(
								  PSZ pszMachine,
								  ULONG ulQueueuID,
								  BOOL* pbIsSQLMultiStripJOB);
	
//used to upgrade job package
BOOL UpgradeAgentJob(PASDDISK pUniDisk,PASNODE pNode);
BOOL UpgradeAgentJobW(PASDDISK pUniDisk,PASNODE pNode, DWORD dwAgentVersion);

ASRET ASLoadQueueJobFromScript_Ex(
  PSZ     pszScriptName,
  PSZ     pszFileName,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET ASLoadQueueJobFromScriptForGUI_Ex(
  PSZ     pszScriptName,
  PSZ     pszFileName,
  USHORT  usLevel,
  PCHAR   pBuf,
  ULONG   ulBufSize,
  PULONG  pulTotalSize);

ASRET ASLoadQueueJobFromScriptForGUI_ExW(
	PWSZ    pszScriptName,
	PWSZ    pszFileName,
	USHORT  usLevel,
	PCHAR   pBuf,
	ULONG   ulBufSize,
	PULONG  pulTotalSize);  //redbh03

ASRET ASAuthDeleteQueueJob_Ex(
							 PSZ		pszServer,
						   PSZ		pszQueue,
						   ULONG		ulJobID,
						   USHORT	usLevel,
						   PCHAR		pBuf,
						   ULONG		ulBufSize,
						   PSZ		pszUserName,
						   PSZ		pszPassword,
						   PSZ		pszDomainName,
						   ULONG		ulSecLevel,
						   PASJOBUPDATE  pAsJobUpdate);

ASRET ASAuthAddQueueJob_Ex(
						   PSZ		pszServer,
						   PSZ		pszQueue,
						   USHORT	usLevel,
						   PCHAR	pBuf,
						   ULONG	ulBufSize,
						   PULONG	pulJobID,
						   PSZ		pszUserName,
						   PSZ		pszPassword,
						   PSZ		pszDomainName,
						   ULONG	ulSecLevel,
						   PASJOBUPDATE  pAsJobUpdate);
						   
ASRET ASAuthSetQueueJob_Ex(
						PSZ     pszServer,
						PSZ     pszQueue,
						ULONG   ulJobID,
						USHORT  usLevel,
						USHORT  usDetail,
						PCHAR   pBuf,
						ULONG   ulBufSize,
						USHORT  usSetMember,
						PSZ		pszUserName,
						PSZ		pszPassword,
						PSZ		pszDomainName,
						ULONG	ulSecLevel,
						PASJOBUPDATE  pAsJobUpdate);
//UNICODE_SUPPORT_START
ASRET ASQAddQueueJobW(
                        PWSZ     pszServerW,
                        PWSZ     pszQueueW,
                        USHORT  usLevel,
                        PSZ   pBuf,
                        ULONG   ulBufSize,
                        PULONG  pulJobID);
ASRET ASQAuthAddQueueJobW(
	                    PWSZ		pszServerW, //IN
	                    PWSZ		pszQueueW, //IN
	                    USHORT	usLevel,
	                    PSZ	pBuf,
	                    ULONG	ulBufSize,
	                    PULONG	pulJobID,
	                    PWSZ		pszUserNameW, //IN
	                    PWSZ		pszPasswordW, //IN
	                    PWSZ		pszDomainNameW, //IN
	                    ULONG	ulSecLevel);
ASRET ASQAuthAccessW( 
	                     LPWSTR      pszServer,
	                     LPWSTR      pszUserName,
	                     LPWSTR      pszPassword,
	                     LPWSTR      pszDomainName,
	                     ULONG    ulSecLevel,
	                     ULONG    ulDesiredAccess,
	                     PBOOLEAN  pbReturn,
	                     UCHAR    cHostType);
ASRET ASQModifyJobStatusOrTimeW ( PWSZ pszServerW, 
                                 ULONG ulJobID, 
                                 ULONG ulExecTime, 
                                 USHORT usJobStatus,
	                             UCHAR	cHostType);
ASRET ASAuthAddQueueJobW(
						 PWCHAR     pszServerW, /*in*/
						 PWCHAR     pszQueueW, /*in*/
						 USHORT		usLevel,
						 PCHAR		pBuf, /*in*/
						 ULONG		ulBufSize,
						 PULONG		pulJobID,
						 PWSZ		pszUserNameW,
						 PWSZ		pszPasswordW,
						 PWSZ		pszDomainNameW,
						 ULONG	ulSecLevel);
ASRET ASSetQueueJobW(
					 PWSZ     pwszServer,
					 PWSZ     pwszQueue,
					 ULONG   ulJobID,
					 USHORT  usLevel,
					 USHORT  usDetail,
					 PCHAR   pBuf,
					 ULONG   ulBufSize,
					 USHORT  usSetMember);
ASRET ASAuthSetQueueJobW(
						 PWSZ    pwszServer,
						 PWSZ    pwszQueue,
						 ULONG   ulJobID,
						 USHORT  usLevel,
						 USHORT  usDetail,
						 PCHAR   pBuf,
						 ULONG   ulBufSize,
						 USHORT  usSetMember,
						 PWSZ	pwszUserName,
						 PWSZ	pwszPassword,
						 PWSZ	pwszDomainName,
						 ULONG	ulSecLevel);
ASRET ASAuthAddQueueJob_ExW(
							PWSZ		pwszServer,
							PWSZ		pwszQueue,
							USHORT	usLevel,
							PCHAR	pBuf,
							ULONG	ulBufSize,
							PULONG	pulJobID,
							PWSZ		pwszUserName,
							PWSZ		pwszPassword,
							PWSZ		pwszDomainName,
							ULONG	ulSecLevel,
							PASJOBUPDATE  pAsJobUpdate);

ASRET ASAuthSetQueueJob_ExW(
							PWSZ     pwszServer,
							PWSZ     pwszQueue,
							ULONG   ulJobID,
							USHORT  usLevel,
							USHORT  usDetail,
							PCHAR   pBuf,
							ULONG   ulBufSize,
							USHORT  usSetMember,
							PWSZ		pwszUserName,
							PWSZ		pwszPassword,
							PWSZ		pwszDomainName,
							ULONG	ulSecLevel,
							PASJOBUPDATE  pAsJobUpdate);


//UNICODE_SUPPORT_END
	
#ifdef __cplusplus
}
#endif

#endif  /* _INC_ASQJOBFN */


