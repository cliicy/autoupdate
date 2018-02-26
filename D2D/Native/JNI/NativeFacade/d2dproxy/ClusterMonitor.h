#pragma once
#include <ClusAPI.h>                   // Cluster API.
#include <ResApi.h>                    // Resource API.
#include <vector>
#pragma comment( lib, "ClusAPI.lib" )  // Cluster API library.
#pragma comment( lib, "ResUtils.lib" ) // Utility Library.
//
// ClusterMonitor:
// This class is used to monitor cluster storage devices, and update 
// their status into registry:
// "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine\Cluster"
//
//

enum CLUSTER_DISK_STATUS
{
	DS_NO_CHANGE = 0,
	DS_CHANGED	 = 1,
	DS_REMOVED	 = 2
};
//
// The information of cluster disk information
//
typedef struct _CLUSTER_DISK_INFO_
{
	DWORD		diskIdType;						// 0 - MBR disk; 1 - GPT Disk
	DWORD		diskSignature;					// the disk signature if this is a MBR disk	
	DWORD		status;							// status of cluster disk. 0-not changed; 1-changed; 2-removed;
	WCHAR		resourceName[512];				// the resource name of this disk
	WCHAR		clusResourceID[64];				// the GUID that identifies the resource	
	WCHAR		diskGUID[64];					// the disk GUID if this is a GPT disk
	__int64		lastBackupTime;					// the last successfull backup time (UTC)
	__int64		lastUpdateTime;					// the last update time (UTC)

	_CLUSTER_DISK_INFO_( ){
		ZeroMemory( clusResourceID, sizeof(clusResourceID) );
		ZeroMemory( diskGUID, sizeof(diskGUID) );
		ZeroMemory( resourceName, sizeof(resourceName) );
		status = DS_NO_CHANGE;
		diskIdType = 0;
		diskSignature = 0;
		lastBackupTime = 0;
		lastUpdateTime = 0;
	}

	_CLUSTER_DISK_INFO_( const _CLUSTER_DISK_INFO_& other){
		diskIdType = other.diskIdType;
		diskSignature = other.diskSignature;
		memcpy_s( resourceName, sizeof(resourceName), other.resourceName, sizeof(resourceName) );
		memcpy_s( clusResourceID, sizeof(clusResourceID), other.clusResourceID, sizeof(clusResourceID) );
		memcpy_s( diskGUID, sizeof(diskGUID), other.diskGUID, sizeof(diskGUID) );
		status = other.status;
		lastBackupTime = other.lastBackupTime;
		lastUpdateTime = other.lastUpdateTime;
	}

	_CLUSTER_DISK_INFO_& operator = ( const _CLUSTER_DISK_INFO_& other){
		diskIdType = other.diskIdType;
		diskSignature = other.diskSignature;
		memcpy_s( resourceName, sizeof(resourceName), other.resourceName, sizeof(resourceName) );
		memcpy_s( clusResourceID, sizeof(clusResourceID), other.clusResourceID, sizeof(clusResourceID) );
		memcpy_s( diskGUID, sizeof(diskGUID), other.diskGUID, sizeof(diskGUID) );
		status = other.status;
		lastBackupTime = other.lastBackupTime;
		lastUpdateTime = other.lastUpdateTime;
		return (*this);
	}

}CLUSTER_DISK_INFO, *PCLUSTER_DISK_INFO;

class ClusterMonitor
{
public:
	static ClusterMonitor* CreateInstance( );

	static void	DeleteInstance( );

protected:
	ClusterMonitor(void);

	virtual ~ClusterMonitor(void);

	static ClusterMonitor* _instance;

protected:
	//
	// enumerate all of the cluster shared disks
	//
	DWORD	EnumDiskResources( HCLUSTER hCluster, std::vector<CLUSTER_DISK_INFO>& vecDiskResources );

	//
	// update one single disk information into UDP registry "Cluster"
	//
	DWORD	UpdateSingleDiskResource( const CLUSTER_DISK_INFO& diskRes );

	//
	// update one sing disk information into UDP registry "Cluster".
	// if the specified resource is not a disk, ignore it.
	//
	DWORD	UpdateDiskByResourceName( HCLUSTER hCluster, LPCWSTR pszResourceName );

	//
	// update one sing disk information into UDP registry "Cluster".
	// if the specified resource is not a disk, ignore it.
	//
	DWORD	DeleteDiskByResourceName( LPCWSTR pszResourceName );

	//
	// update cluster tag into UDP registry "Cluster"
	//
	DWORD	UpdateClusterTag( );

	//
	// clean all disk resources. call this function when cluster service stopped
	//
	DWORD	CleanDiskResources( BOOL bCleanAll );

	DWORD	MonitorDiskResources( HCLUSTER hClust );

	//
	// init the cluster disk information. call this function when cluster service become availbe the first time
	//
	DWORD	InitDiskResources( HCLUSTER hClust );

public:
	DWORD	StartMonitor( );

	DWORD	StopMonitor( );

	DWORD	Main( );

protected:
	DWORD	readDiskInfoFromClusterRegistry( CLUSTER_DISK_INFO& diskRes );

	BOOL	getDiskInfoByResourceName( HCLUSTER hCluster, LPCWSTR pszResourceName, CLUSTER_DISK_INFO& diskRes );

	BOOL	isThreadStoped( DWORD dwWaitInMilSeconds );

	DWORD	updateDiskInfo( const CLUSTER_DISK_INFO& diskInfo );

	DWORD	readDiskInfo( const wstring& strResourceID, CLUSTER_DISK_INFO& diskInfo );

	BOOL	sameDisk( const CLUSTER_DISK_INFO& diskInfo1, const CLUSTER_DISK_INFO& diskInfo2 );

	BOOL	readClusterTag( wstring& strMsClusterTag, wstring& strUDPClusterTag );
protected:
	HANDLE	    m_hThread;
	HANDLE		m_hStopEvent;
	BOOL		m_bWin2003;
protected:
	CDbgLog		m_log;
};

DWORD WINAPI _cluster_monitor_thread( LPVOID pArgs );
