#pragma once

//message buffer size
#define MAX_MSG_LEN 2048

//error code

#define ERROR_IGNORE	20000

//base code
#define OC_BASE_CODE			1000


//operate code list
#define OC_BLI_DRIVER			(OC_BASE_CODE+1)
#define OC_KMDF_DRIVER			(OC_BASE_CODE+2)
#define OC_UMDF_DRIVER			(OC_BASE_CODE+3)
//end for operate code list

#ifdef __cplusplus
extern "C"{
#endif

typedef struct _SETUP_MSG__
	{
		DWORD  dwOperateCode;			//operate code, refer to operate code list
		DWORD  dwErrorCode;				//the utility return code
		WCHAR  szMsg[MAX_MSG_LEN];		//error message
	}SETUP_MSG, *PSETUP_MSG;


//API

/*get the language ID

******************************
LanguageID 		   Laguage
  1033   			  ENU 
  1031   			  DEU 
  1036   			  FRA
  1040   			  ITA
  1041   			  JPN 
  2052   			  CHS
  1028   			  CHT 
  1034   			  ESP  
  1046   			  PRB
******************************
*/
DWORD GetSetupLanguageID();


/*
report the error message to setup side.
return code:
  0                success 
  ERROR_IGNORE     ignore to report the message because this is not called by setup applicatoin
  other			   fail to report the message
*/
DWORD ReportSetupErrorMessage(PSETUP_MSG pSetupMsg);


/*
get the error message on setup side.
return code:
  0              success 
  other			 fail to get the message
*/
DWORD GetSetupErrorMessage(DWORD dwOperateCode,PSETUP_MSG pSetupMsg);


#ifdef __cplusplus
}
#endif

