
/******************************************************************************
 The header file descripts that the API and data struct are definded by Disaster
 Recovery module.

 Version Date: March 04, 2009
 *****************************************************************************/

#pragma once


/******************************************************************************
 *enum data type to define the const value, please avoid use macro to define 
 the const value
 *****************************************************************************/

//define the backup level, file level and data block level
typedef enum _BACKUP_LEVEL_
{
	BACKUP_LEVEL_UNKNOWN = 0,
	BACKUP_LEVEL_FILE,
	BACKUP_LEVEL_BLOCK
}BACKUP_LEVEL, *PBACKUP_LEVEL;

//define the backup method, such as full, partial
typedef enum _BACKUP_METHOD_
{
	BACKUP_METHOD_UNKNOWN = 0,
	BACKUP_METHOD_FULL,
   BACKUP_METHOD_INC,
	BACKUP_METHOD_PARTIAL
}BACKUP_METHOD, *PBACKUP_METHOD;



//define the error code when call DR API to collect DR information
typedef enum _DR_ERROR_
{
	DR_SUCCESS = 0,
	DR_EXCEPTION,
	DR_ERR_SYSTEM_INFO,
	DR_ERR_CLIENT_PRODUCT_INFO,
	DR_ERR_NIC_DRIVER,
	DR_ERR_HDC_DRIVER,
	DR_ERR_SCSI_DRIVER,
	DR_ERR_USB_DRIVER,
	DR_ERR_DISK_DRIVER,
	DR_ERR_DISK_GEOMETRY,
	DR_ERR_NET_CFG,
	DR_ERR_INIT_CFG,
	DR_ERR_SAVE_CFG,
	DR_ERR_COPY_DRIVER,
	DR_ERR_ASR_COLLECT,
	DR_ERR_SAVE_TEKEY,
	DR_ERR_DISK_LAYOUT

}DR_ERROR, *PDR_ERROR;


/******************************************************************************
 * define global data structure
 *****************************************************************************/
typedef struct _CLIENT_PARAM_
{
	BACKUP_LEVEL	BackupLevel;
	BACKUP_METHOD	BackupMethod;
	PVOID		pAsNode;
}CLIENT_PARAM, *PCLIENT_PARAM;


typedef struct _SERVER_PARAM_
{
	LPCWSTR lpszServerName;
	LPCWSTR lpszCARoot;
	LPCWSTR lpszPassword;
}SERVER_PARAM, *PSERVER_PARAM;


/*****************************************************************************
 * Collect DR informaion API
 *
 *****************************************************************************/


/*
Description: 
 The function can judage whether this backup can be take disater recovery

Param:
 @pSourceNode [IN], the job script pointer for one node, PASNODE.

Return Value:
 TRUE, this backup for the node can be took disater recovery 
 FALSE, this backup for the ndoe can't be took disater recovery

Remark:
 The function only can judge one node every time.
 */
 
 //need discuss with GUI/Agent
BOOL	AdrIsCanTakeDRForThisBackup(PVOID pSoureNode);//PASNODE


/*
Description:
 The function retrieves the error message by the error code.

Param:
 @DrError [IN], the DR module defines error code
 @lpErrMsg [IN], the buffer is used to store the error message. The message string is localized.
 @lpdwBufSize [IN], the buffer size, include the NULL
              [OUT], if the lpErrMsg buffer is not enough, it is the required buffer size; if the
			  lpErrMsg buffer is enough, it is the number of the character is copied to the buffer
Return Value:
 TRUE, the function run success
 FALSE, the function run failed, if the buffer is enough contain the error string, it will return FALSE.
 */
BOOL	AdrGetDrErrorMessageW(IN DR_ERROR DrError, IN LPWSTR lpErrMsg, IN OUT LPDWORD lpdwBufSize);


/*****************************************************************************
 * Collect client side DR informaion API
 *
 *****************************************************************************/

/*
Description:
 The function judages DR can be supported on the platform

Param:
 @BackupLevel [IN], the backup level

Return Value:
 TRUE, DR is supported on the platform
 FALSE, DR is not supported on the platform
 */
BOOL	AdrIsDRSupported(BACKUP_LEVEL BackupLevel = BACKUP_LEVEL_BLOCK);

/*
Description:
 The function collects the client side DR information.

Param:
 @lpPath [IN], the path is used to store the DR information which is collected
 @pClientParam [IN], the information is told to DR module how to collect the 
                     DR information
 @lpDRError [OUT], the collect DR information operation code, call AdrGetDrErrorMessageW
                   can get the error message. The message can be send to activity log

Return Value:
 0, call the function success.
 non-0, windows error code.

Remark:
 the caller should always call AdrGetDrErrorMessageW to get the logical error message during
 collect DR informaiton.
 */
DWORD	AdrCollectClientDRInfoW(IN PDRIF_PARAM pClientParam, OUT PDR_ERROR lpDrError);



/******************************************************************************
 * Collect server side DR informatio API
 *****************************************************************************/

/*
Description:
 The function collects the backup server side DR information

Param:
 @lpPath [IN], the path is used to store the DR information which is collected
 @pServerParam [IN], the information is told to DR module how to collect the 
                     server side DR information
 @lpDRError [OUT], the collect DR information operation code, call AdrGetDrErrorMessageW
                   can get the error message. The message can be send to activity log

Return Value:
 0, call the function success.
 non-0, windows error code.

Remark:
 the caller should always call AdrGetDrErrorMessageW to get the logical error message during
 collect DR informaiton.
 */
 
 //node name parameter
DWORD	AdrCollectServerDRInfoW(IN LPCWSTR lpPath,
							   IN PSERVER_PARAM pServerParam,
							   OUT PDR_ERROR lpDrError
							   );


/*
Description:
 The function collects the backup server side DR information

Param:
 @lpPath [IN], the path is used to store the DR information which is collected
 @pServerParam [IN], the information is told to DR module how to collect the 
                     server side DR information
 @pNodeName [IN], the name of the node which is backed up, it is format is hostname
                  or hostname(IP)
 @lpDRError [OUT], the collect DR information operation code, call AdrGetDrErrorMessageW
                   can get the error message. The message can be send to activity log

Return Value:
 0, call the function success.
 non-0, windows error code.

Remark:
 the caller should always call AdrGetDrErrorMessageW to get the logical error message during
 collect DR informaiton.
 */
DWORD	AdrUpdateSessionFileW(IN LPCWSTR lpPath,
							 IN PSERVER_PARAM pServerParam,
							 IN LPCWSTR lpNodeName,
							 OUT PDR_ERROR lpDrError
							 );


