#ifndef	__CA_RPS_DATA_STORE_I_DEF_H__
#define	__CA_RPS_DATA_STORE_I_DEF_H__ 
#pragma  once 
//////////////////////////////////////////////////////////////////////////
//File Name: 
//	GDDManageridef.h 
//Brief Introduction
//	the gdd sever exports some API to let the client the manage the GDD system
//Create Time:
//	2011-Sep-09   by wanmi12
//Modify History:
//  
//////////////////////////////////////////////////////////////////////////
#ifdef DATASTOREMANAGER_EXPORTS
#define DSEXPORT_API __declspec(dllexport)
#else
#define DSEXPORT_API __declspec(dllimport)
#endif

#include "Data_Store_Mgr_s_def.h"

//API
extern "C" 
{	
	/*
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreInitialize();

	/*
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreUninitialize();
	/*
	Parameters:
		pInstance	[in]: the instance 
		pInsINFO	[in]: the attribute of the instance identified by pInstance
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreAddInstance(PDATASTORE_INSTANCE_ITEM pInstance, PDATASTORE_INSTANCE_INFO pInsStatus);


    DSEXPORT_API HRESULT WINAPI DataStoreAddInstanceOnExistingPath(PDATASTORE_INSTANCE_ITEM pInstance, PDATASTORE_INSTANCE_INFO pInsATTr, PDATASTORE_INSTANCE_INFO pOrgInstConf);

	/*
	Brief:			add a new instance in current datastore system.
	Parameters:
	pInstance	[in]: the instance 
	pInsATTr	[in]: the status of the instance identified by pInstance
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreModifyInstance(PDATASTORE_INSTANCE_ITEM pInstance, PDATASTORE_INSTANCE_INFO pInsATTr);


	/*
	Brief:			add a new instance in current datastore system.
	Parameters:
	pInstance	[in]: the instance 
	pInsATTr	[out]: the attribute of the instance identified by pInstance, please release it after use it by using 
				API GDDReleaseAttribute
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreGetInstance(PDATASTORE_INSTANCE_ITEM pInstance, PDATASTORE_INSTANCE_INFO pInsATTr);

	DSEXPORT_API HRESULT WINAPI DataStoreReleaseAttribute( PDATASTORE_INSTANCE_INFO pInsATTr);
 

	/*
	Brief:		   remove a instance from current datastore system.
	Parameters:
		pInstance	[in]: the instance to be removed
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreRemoveInstance(PDATASTORE_INSTANCE_ITEM pInstance);

	/*
	Brief:		   get the status of the instance identified by pInstance
	Parameters:
		pInstance	[in]: the identify of the instance
		pInsStatus	[out]: the status of current instance, please release it after use it by using 
		API GDDReleaseStatus
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreGetInstanceStatus(PDATASTORE_INSTANCE_ITEM pInstance, PDATASTORE_INSTANCE_STATUS pInsStatus);
	DSEXPORT_API HRESULT WINAPI DataStoreInstRuningStatus(PDATASTORE_INSTANCE_ITEM pInstance, PDATASTORE_INSTANCE_STATUS pInsStatus);

	DSEXPORT_API HRESULT WINAPI DataStoreReleaseStatus( PDATASTORE_INSTANCE_STATUS pInsStatus);

	/*
	Brief:		   start one instance identified by pInstance
	Parameters:
		pInstance	[in]: the identify of the instance
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreStartInstance(PDATASTORE_INSTANCE_ITEM pInstance);

	/*
	Brief:		   start one dedupe data store to read only status. For non-dedupe, just start it. 
	Parameters:
	pInstance	[in]: the identify of the data store instance
	return value: 0 Success otherwise failed
	*/
	DSEXPORT_API HRESULT WINAPI DataStoreStartInstanceReadonly(PDATASTORE_INSTANCE_ITEM pInstance);

	/*
	Brief:		   stop one instance identified by pInstance
	Parameters:
		pInstance	[in]: the identify of the instance
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreStopInstance(PDATASTORE_INSTANCE_ITEM pInstance);

	/*
	Brief:		   get all the instances in current data store system
	Parameters:
		pInsList	[out]: the instance list. when client doesn't need this list, it must be 
					released by invoking API GDDReleaseInstanceList
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreGetInstanceList(PDATASTORE_INSTANCE_LIST pInsList);

	/*
	Function Name: GDDReleaseInstanceList()
	Brief:		   release the instance list
	Parameters:
	pInsList	[out]: the instance list.  
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreReleaseInstanceList(PDATASTORE_INSTANCE_LIST pInsList);
	DSEXPORT_API HRESULT WINAPI DataStoreGetHashMachineConf(PDATA_STORE_HASH_DETECT_INFO pHASH_Machine_Config);
	DSEXPORT_API HRESULT WINAPI DataStoreReleaseHashMachineConf(PDATA_STORE_HASH_DETECT_INFO pHASH_Machine_Config);

	/*
	Brief:		   get all the nodes in current data store
	Parameters:
		pInstance: [in] data store uuid.
		pNodeList: [out] the node list.
	return value: 0 Success otherwise failed
	*/ 
	DSEXPORT_API HRESULT WINAPI DataStoreGetNodeList(PDATASTORE_INSTANCE_ITEM pInstance, pDATASTORE_NODE_LIST pNodeList);
	DSEXPORT_API HRESULT WINAPI DataStoreReleaseNodeList(pDATASTORE_NODE_LIST pNodeList);


};

#endif//__CA_RPS_DATA_STORE_I_DEF_H__
