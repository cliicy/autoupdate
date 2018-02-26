#ifndef _API_NETWORK_THROTTLING_H_
#define _API_NETWORK_THROTTLING_H_

#ifndef UNIX
#include <Guiddef.h>
#else
#include <string.h>
typedef struct _GUID {
	char data1[8];
	char data2[4];
	char data3[4];
	char data4[4];
	char data5[12];

	_GUID()
	{
		memset(data1, 0, 8);
		memset(data2, 0, 4);
		memset(data3, 0, 4);
		memset(data4, 0, 4);
		memset(data5, 0, 12);
	}

	_GUID(const _GUID& guid) 
        {
		memcpy(data1, guid.data1, 8);
		memcpy(data2, guid.data2, 4);
		memcpy(data3, guid.data3, 4);
		memcpy(data4, guid.data4, 4);
		memcpy(data5, guid.data5, 12);
        }

	_GUID& operator = (const _GUID& guid)
        {
		if (this != &guid) {
		        memcpy(data1, guid.data1, 8);
			memcpy(data2, guid.data2, 4);
			memcpy(data3, guid.data3, 4);
			memcpy(data4, guid.data4, 4);
			memcpy(data5, guid.data5, 12);
		}
		return *this;
	}
} GUID;
#endif

#ifdef  NETWORK_THROTTLING_EXPORTS 
#define NETWORK_THROTTLING_DLL_API __declspec(dllexport)
#else
#define NETWORK_THROTTLING_DLL_API __declspec(dllimport)
#endif

typedef struct _tagNetworkThrottlingItem
{
	int nBandwidth;						// unit kbps, (1kB/s = 8kbps, 1MB/s = 1000kB/s = 8000kbps)
	int nStartHour;						// start hour, in 24hour mode
	int nStartMinute;					// start minute
	int nEndHour;						// end hour, in 24hour mode
	int nEndMinute;						// end minute
}NETWORK_THROTTLING_ITEM,*PNETWORK_THROTTLING_ITEM;

typedef struct _tagNetworkThrottlingDay
{
	int nItemCount;						// how many item in array pItemArray
	PNETWORK_THROTTLING_ITEM pItemArray;// throttling items array
}NETWORK_THROTTLING_DAY,*PNETWORK_THROTTLING_DAY;

typedef struct _tagNetworkThrottlingPolicy
{
	GUID GuidPolicy;					// identify (GUID) for policy
	NETWORK_THROTTLING_DAY aDayArray[7];// day settings. index: Sunday is 0, Monday is 1, ..., Saturday is 6
}NETWORK_THROTTLING_POLICY,*PNETWORK_THROTTLING_POLICY;

typedef enum _eNetworkThrottlingOperation
{
	ENTO_CREATE = 1,
	ENTO_MODIFY,
	ENTO_DELETE
}E_NETWORK_THROTTLING_OPERATION;

#ifdef __cplusplus 
extern "C" { 
#endif

#ifndef UNIX
	/// below three functions are provided for Java code.
	/// \fn bool NTP_AddPolicy( PNETWORK_THROTTLING_POLICY pPolicy );
	/// \brief add a new policy into system. If same policy(same GUID) exists, the function will be failed.
	/// \param point to policy.
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_AddPolicy( IN PNETWORK_THROTTLING_POLICY pPolicy );

	/// \fn bool NTP_UpdatePolicy( PNETWORK_THROTTLING_POLICY pPolicy );
	/// \brief update an existed policy in system. If the policy(same GUID) doesn't exist, the function will be failed. 
	/// \param point to policy.
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_UpdatePolicy( IN PNETWORK_THROTTLING_POLICY pPolicy );

	/// \fn int NTP_RemovePolicy( GUID* pGuid );
	/// \brief remove the existed policy from system. If the policy(same GUID) doesn't exist, the function will be failed. 
	/// \param point to GUID of policy. NOTE: the pGuid must not be empty (NULL).
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_RemovePolicy( IN GUID* pGuid );


	/// below five functions are provided for communication library.
	/// \fn int NTP_RegisterProcess( IN GUID* pGUID, char * pcRemoteServer );
	/// \brief which policy will be used in current process.
	/// \param pGUID policy GUID
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_RegisterProcess( IN GUID* pGUID, char * pcRemoteServer );

	/// \fn int NTP_UnregisterProcess();
	/// \brief the process is existing.
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_UnregisterProcess();

	/// \fn int NTP_RegisterConnection( IN void* pvObject, IN char * pcRemoteServer );
	/// \brief registry a connection into system.
	/// \param communication object, socket handle, or any unique value for a connection
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_RegisterConnection( IN void* pvObject, IN char * pcRemoteServer );

	/// \fn int NTP_UnregisterConnection( IN DWORD dwPid, IN void* pvObject );
	/// \brief unregistry a connection from system.
	/// \param current process ID
	/// \param communication object, socket handle, or any unique value for a connection that is used in NTP_RegistrySocket
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_UnregisterConnection( IN void* pvObject );

	/// \fn int NTP_UpdateStatus( IN DWORD dwPid, IN void* pvObject, IN __int64 nSentSize, IN __int64 nRecvSize, OUT int* pnBandwidth);
	/// \brief update sent/received data size and get current bandwidth.
	/// \param communication object, socket handle, or any unique value for a connection
	/// \param nSentSize sent data size
	/// \param nRecvSize received data size
	/// \param pnBandwidth current bandwidth. If it is <=0, no limitation.
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_UpdateStatus( IN void* pvObject, IN __int64 nSentSize, IN __int64 nRecvSize, OUT int* pnBandwidth );

	/// \fn int NTP_IncRawSendSize( IN void* pvObject, IN __int64 nSentSize );
	/// \brief increase send raw size.
	/// \param communication object, socket handle, or any unique value for a connection
	/// \param nSentSize sent data size
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_IncRawSendSize( IN void* pvObject, IN __int64 nSentSize );

	/// \fn int NTP_GetSendSize( OUT __int64* pnRealSize, OUT __int64* pnRawSize );
	/// \brief get sent data size.
	/// \param pnRealSize sent data size on wire that is updated by function NTP_UpdateStatus().
	/// \param pnRawSize sent data size from upper layer caller that is updated by function NTP_IncRawSendSize().
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_GetSendSize( OUT __int64* pnRealSize, OUT __int64* pnRawSize );

	/// \fn int NTP_ResetSendSize();
	/// \brief get sent data size.
	/// \param communication object, socket handle, or any unique value for a connection
	/// \param pnRealSize sent data size on wire that is updated by function NTP_UpdateStatus().
	/// \param pnRawSize sent data size from upper layer caller that is updated by function NTP_IncRawSendSize().
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_ResetSendSize();

	/// \fn int NTP_GetThrottling( OUT int* pnBandwidth);;
	/// \brief get current bandwidth of current process
	/// \param pnBandwidth current bandwidth. If it is <=0, no limitation.
	/// \return =0: success; else: error code
	NETWORK_THROTTLING_DLL_API int NTP_GetThrottling( OUT int* pnBandwidth );
#endif

#ifdef __cplusplus
}
#endif
#endif
