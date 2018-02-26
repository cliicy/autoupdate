#include "./../stdafx.h"
#include "ClusterMonitor.h"
#include "CryptoDefine.h"
#include <string>
#include "brandname.h"
#include <istream>
#include <sstream>

#define	REG_K_MS_CLUSTER_ROOT		L"Cluster"
#define	REG_K_RESOURCES				L"Resources"
#define REG_K_MS_CLUSTER_RESOURCES	REG_K_MS_CLUSTER_ROOT L"\\" REG_K_RESOURCES

#define REG_K_UDP_CLUSTER_ROOT		L"SoftWare\\" CST_PRODUCT_REG_ROOT_L L"\\" REG_K_MS_CLUSTER_ROOT

#define REG_V_LAST_UPDATE_TIME		L"LastUpdateTime"
#define REG_V_LAST_BACKUP_TIME		L"LastBackupTime"
#define REG_V_DISK_STATUS			L"Status"
#define REG_V_DISK_ID_TYPE			L"DiskIdType"
#define REG_V_DISK_SIGNATURE		L"DiskSignature"
#define REG_V_DISK_GUID				L"DiskGUID"
#define REG_V_DISK_RESOURCE_NAME	L"ResourceName"

#define CLUSTER_TAG_WIN2008			L"PaxosTag"
#define CLUSTER_TAG_WIN2003			L"RegistrySequence"	// DWORD

static __int64 current_time_utc( )
{
	FILETIME ft;
	::GetSystemTimeAsFileTime( &ft );
	LARGE_INTEGER li;
	li.HighPart = ft.dwHighDateTime;
	li.LowPart = ft.dwLowDateTime;
	return li.QuadPart;
}

ClusterMonitor* ClusterMonitor::_instance = NULL;

ClusterMonitor* ClusterMonitor::CreateInstance()
{
	if( _instance==NULL )
		_instance = new ClusterMonitor( );
	return _instance;
}

void ClusterMonitor::DeleteInstance( )
{
	if( _instance != NULL )
	{
		delete _instance;
		_instance = NULL;
	}
}

ClusterMonitor::ClusterMonitor(void)
	: m_hThread( NULL )
	, m_hStopEvent( NULL )
	, m_bWin2003( FALSE )
{
	m_hStopEvent = ::CreateEvent( NULL, TRUE, FALSE, NULL );

	OSVERSIONINFOEX osVer;
	ZeroMemory(&osVer, sizeof(OSVERSIONINFOEX));
	osVer.dwOSVersionInfoSize = sizeof(OSVERSIONINFOEX);
	ZeroMemory(&osVer, sizeof(OSVERSIONINFOEX));
	osVer.dwOSVersionInfoSize = sizeof(OSVERSIONINFOEX);

	//
	// Try calling GetVersionEx using the OSVERSIONINFOEX structure.
	// If that fails, try using the OSVERSIONINFO structure.
	//
	if( !GetVersionExW ((OSVERSIONINFO *) &osVer) ){
		osVer.dwOSVersionInfoSize = sizeof (OSVERSIONINFO);
		if( GetVersionEx ( (OSVERSIONINFO *) &osVer) )
			m_bWin2003 = osVer.dwMajorVersion<6 ? TRUE : FALSE;
	}
	else{
		m_bWin2003 = osVer.dwMajorVersion<6 ? TRUE : FALSE;
	}
}


ClusterMonitor::~ClusterMonitor(void)
{
	if( m_hThread!=NULL )
		::CloseHandle( m_hThread );
	m_hThread = NULL;

	if( m_hStopEvent )
		::CloseHandle( m_hStopEvent );
	m_hStopEvent = NULL;
}

DWORD ClusterMonitor::EnumDiskResources( HCLUSTER hCluster, std::vector<CLUSTER_DISK_INFO>& vecDiskResources )
{
	HCLUSENUM	hResEnum = NULL;
	DWORD		dwErr = 0;
	do
	{
		hCluster = OpenCluster( NULL );
		if( hCluster==NULL )
		{
			dwErr = GetLastError();
			m_log.LogW(LL_ERR, dwErr, L"%s: Failed to open cluster handle.", __WFUNCTION__ );
			break;
		}

		hResEnum = ClusterOpenEnum( hCluster, CLUSTER_ENUM_RESOURCE );
		if( hResEnum==NULL )
		{
			dwErr = GetLastError();
			m_log.LogW(LL_ERR, dwErr, L"%s: Failed to open resource enum handle.", __WFUNCTION__ );
			break;
		}

		DWORD dwResult = ERROR_SUCCESS;
		DWORD dwIndex = 0;
		WCHAR szResName[1024] = {0};		// assume 1024 is enough
		DWORD dwType = CLUSTER_ENUM_RESOURCE;

		while( dwResult == ERROR_SUCCESS )
		{
			DWORD dwResNameSize = 1024;
			ZeroMemory( szResName, sizeof(szResName) );

			dwResult = ::ClusterEnum( hResEnum, dwIndex, &dwType, szResName, &dwResNameSize );
			if( dwResult!=ERROR_SUCCESS && dwResult!=ERROR_NO_MORE_ITEMS )
			{
				dwErr = dwResult;
				m_log.LogW(LL_ERR, dwErr, L"%s: Cluster enum returned %d. ERR=%d", __WFUNCTION__, dwResult, GetLastError() );
				break;
			}

			if( dwResult==ERROR_NO_MORE_ITEMS ) // enum end
				break;
			
			dwIndex++;
			if( wcslen(szResName)==0 )
				continue;

			CLUSTER_DISK_INFO diskInfo;
			if( getDiskInfoByResourceName( hCluster, szResName, diskInfo ) )
				vecDiskResources.push_back( diskInfo );
		}
	}while(0);

	if( hResEnum )
	{
		::ClusterCloseEnum( hResEnum );
		hResEnum = NULL;
	}
	return dwErr;
}

DWORD ClusterMonitor::UpdateDiskByResourceName( HCLUSTER hCluster, LPCWSTR pszResourceName )
{
	if( !pszResourceName || !(*pszResourceName) )
		return 0;

	CLUSTER_DISK_INFO diskInfo;
	if( getDiskInfoByResourceName( hCluster, pszResourceName, diskInfo ) )
		return UpdateSingleDiskResource( diskInfo );

	return 0;
}

DWORD ClusterMonitor::DeleteDiskByResourceName( LPCWSTR pszResourceName )
{
	if( !pszResourceName || !(*pszResourceName) )
		return 0;

	DWORD dwRet = 0;
	CRegistry udpReg;
	dwRet = udpReg.OpenWrite( REG_K_UDP_CLUSTER_ROOT );
	if( 0 != dwRet ){
		return 0;
	}

	std::vector<wstring> subKeys;
	std::vector<wstring>::iterator itKey;
	udpReg.EnumKeyNames( subKeys );
	
	for( itKey=subKeys.begin();itKey!=subKeys.end();itKey++ )
	{
		CLUSTER_DISK_INFO diskInfo;
		if( 0 != readDiskInfo( (*itKey), diskInfo ) )
			continue;

		if( 0 != _wcsicmp( pszResourceName, diskInfo.resourceName ) )
			continue;

		diskInfo.status = DS_REMOVED;
		diskInfo.lastUpdateTime = current_time_utc( );
		updateDiskInfo( diskInfo );
	}
	return 0;
}

DWORD ClusterMonitor::UpdateSingleDiskResource( const CLUSTER_DISK_INFO& diskRes )
{
	DWORD dwRet = 0;
	CRegistry udpReg;
	dwRet = udpReg.OpenWrite( REG_K_UDP_CLUSTER_ROOT );
	if( 0 != dwRet ){
		dwRet = udpReg.Create( REG_K_UDP_CLUSTER_ROOT );
		if( 0 != dwRet ){
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create registry %s", __WFUNCTION__, REG_K_UDP_CLUSTER_ROOT );
			return dwRet;
		}
	}
	
	std::vector<wstring> subKeys;
	udpReg.EnumKeyNames( subKeys );
	BOOL bExisting = FALSE;
	for( vector<wstring>::iterator itK=subKeys.begin();itK!=subKeys.end();itK++ ){
		CLUSTER_DISK_INFO diskInfo;
		if( 0 != readDiskInfo( (*itK), diskInfo ) )
			continue;

		if( !sameDisk( diskRes, diskInfo ) )
			continue;

		wcscpy_s( diskInfo.clusResourceID, _countof(diskInfo.clusResourceID), diskRes.clusResourceID );
		wcscpy_s( diskInfo.resourceName, _countof(diskInfo.resourceName), diskRes.resourceName );
		diskInfo.lastUpdateTime = current_time_utc();
		diskInfo.status = DS_CHANGED;
		updateDiskInfo( diskInfo );
		if( 0 != wcsicmp( diskRes.clusResourceID, (*itK).c_str() ) )
			udpReg.DeleteKey( (*itK).c_str() );

		bExisting = TRUE;
		break;
	}

	if( !bExisting )
	{
		CLUSTER_DISK_INFO diskInfo = diskRes;
		diskInfo.lastUpdateTime = current_time_utc();
		diskInfo.status = DS_CHANGED;
		updateDiskInfo( diskInfo );
	}

	udpReg.Close();
	return 0;
}

DWORD ClusterMonitor::CleanDiskResources( BOOL bCleanAll )
{
	DWORD dwRet = 0;
	CRegistry udpReg;
	dwRet = udpReg.OpenWrite( REG_K_UDP_CLUSTER_ROOT );
	if( 0 != dwRet ){
		return 0;
	}

	std::vector<wstring> subKeys;
	std::vector<wstring>::iterator itKey;
	udpReg.EnumKeyNames( subKeys );

	for( itKey=subKeys.begin();itKey!=subKeys.end();itKey++ )
	{
		CLUSTER_DISK_INFO diskInfo;
		if( 0 != readDiskInfo( (*itKey), diskInfo ) )
			continue;

		if( diskInfo.status==DS_REMOVED && diskInfo.lastBackupTime>diskInfo.lastUpdateTime ){
			udpReg.DeleteKey( (*itKey).c_str() );
			continue;
		}

		// if want to delete all disks resource, mark them as REMOVED
		if( bCleanAll && diskInfo.status!=DS_REMOVED ){
			diskInfo.status = DS_REMOVED;
			diskInfo.lastUpdateTime = current_time_utc();
			updateDiskInfo( diskInfo );
		}
	}
		
	if( bCleanAll )
	{
		subKeys.clear();
		subKeys.clear();
		udpReg.EnumKeyNames( subKeys );

		if( subKeys.empty() )
		{
			CRegistry regRoot;
			udpReg.OpenWrite( CST_PRODUCT_REG_ROOT_L );
			regRoot.DeleteKey( REG_K_MS_CLUSTER_ROOT );
			regRoot.Close();
		}
	}

	udpReg.Close();
	return 0;
}

DWORD ClusterMonitor::UpdateClusterTag( )
{
	DWORD dwRet = 0;
	wstring msClusterTag = L"", udpClusterTag=L"";
	readClusterTag( msClusterTag, udpClusterTag );

	if( !msClusterTag.empty() && wcscmp(msClusterTag.c_str(), udpClusterTag.c_str()) !=0 )
	{
		CRegistry udpReg;
		dwRet = udpReg.OpenWrite( REG_K_UDP_CLUSTER_ROOT);
		if( 0 != dwRet ){
			dwRet = udpReg.Create( REG_K_UDP_CLUSTER_ROOT, HKEY_LOCAL_MACHINE );
			if( 0 != dwRet ){
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create registry %s", __WFUNCTION__, REG_K_UDP_CLUSTER_ROOT );
				return dwRet;
			}
		}
		
		__int64 tNow = current_time_utc();
		udpReg.SetValue( REG_V_LAST_UPDATE_TIME, REG_QWORD, &tNow, sizeof(tNow) );
		udpReg.SetStringValue( m_bWin2003 ? CLUSTER_TAG_WIN2003 : CLUSTER_TAG_WIN2008, msClusterTag.c_str() );
		udpReg.Close();
	}
	return dwRet;
}

BOOL ClusterMonitor::getDiskInfoByResourceName( HCLUSTER hCluster, LPCWSTR szResName, CLUSTER_DISK_INFO& diskRes )
{
	HRESOURCE hClusRes = ::OpenClusterResource( hCluster, szResName );
	if( hClusRes==NULL )
	{
		m_log.LogW(LL_ERR, GetLastError(), L"%s: Failed to open cluster resource [%s]", __WFUNCTION__, szResName );
		return FALSE;
	}

	//
	// get the resource information
	//
	CLUS_RESOURCE_CLASS_INFO  resInfo; 
	ZeroMemory( &resInfo, sizeof(resInfo) );
	DWORD dwRet = ClusterResourceControl(hClusRes, 
		NULL, 
		CLUSCTL_RESOURCE_GET_CLASS_INFO,
		NULL,
		0,
		&resInfo,
		sizeof(resInfo),
		NULL );
	if( dwRet!=ERROR_SUCCESS )
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to get class info of resource [%s]", __WFUNCTION__, szResName );
		::CloseClusterResource( hClusRes );
		return FALSE;
	}
	else if(resInfo.rc!=CLUS_RESCLASS_STORAGE )
	{
		m_log.LogW(LL_DET, dwRet, L"%s: Resource [%s] is not a storage.", __WFUNCTION__, szResName );
		::CloseClusterResource( hClusRes );
		return FALSE;
	}

	//m_log.LogW(LL_DET, 0, L"%s: Found one storage resource [%s].", __WFUNCTION__, szResName );

	//
	// get GUID ID of this resource
	//
	WCHAR szResID[MAX_PATH] = {0};
	dwRet = ClusterResourceControl( hClusRes, 
		NULL, 
		CLUSCTL_RESOURCE_GET_ID,
		NULL,
		0,
		szResID,
		sizeof(szResID),
		NULL );
	if( dwRet!=ERROR_SUCCESS )
	{
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to cluster ID of resource [%s]", __WFUNCTION__, szResName );
		::CloseClusterResource( hClusRes );
		return FALSE;
	}
	::CloseClusterResource( hClusRes );

	//
	// read detailed disk information from registry
	//

	wcsncpy_s( diskRes.clusResourceID, _countof(diskRes.clusResourceID), szResID, _TRUNCATE );
	wcsncpy_s( diskRes.resourceName, _countof(diskRes.resourceName), szResName, _TRUNCATE );
	
	std::wstring strRegKey = REG_K_MS_CLUSTER_RESOURCES L"\\";
	strRegKey.append( diskRes.clusResourceID );
	strRegKey.append( L"\\Parameters" );

	if( m_bWin2003 )
	{
		diskRes.diskIdType = 0;
		CRegistry reg;
		dwRet = reg.Open( strRegKey.c_str() );
		if( dwRet!=0 )
		{
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to open registry [%s]. ERR=%d", __WFUNCTION__, strRegKey.c_str(), GetLastError() );
			return FALSE;
		}

		dwRet = reg.QueryDWORDValue( L"Signature", diskRes.diskSignature );
		if( dwRet !=0 )
		{
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed query value of [%s] under [%s]. ERR=%d", __WFUNCTION__, CLUSREG_NAME_PHYSDISK_DISKIDTYPE, strRegKey.c_str(), GetLastError() );
			reg.Close();
			return FALSE;
		}
	}
	else
	{
		CRegistry reg;
		dwRet = reg.Open( strRegKey.c_str() );
		if( dwRet!=0 )
		{
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to open registry [%s]. ERR=%d", __WFUNCTION__, strRegKey.c_str(), GetLastError() );
			return FALSE;
		}

		dwRet = reg.QueryDWORDValue( CLUSREG_NAME_PHYSDISK_DISKIDTYPE, diskRes.diskIdType );
		if( dwRet !=0 )
		{
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed query value of [%s] under [%s]. ERR=%d", __WFUNCTION__, CLUSREG_NAME_PHYSDISK_DISKIDTYPE, strRegKey.c_str(), GetLastError() );
			reg.Close();
			return FALSE;
		}

		if( diskRes.diskIdType==0 )
		{
			dwRet = reg.QueryDWORDValue( CLUSREG_NAME_PHYSDISK_DISKSIGNATURE, diskRes.diskSignature );
			if( dwRet !=0 )
			{
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed query value of [%s] under [%s]. ERR=%d", __WFUNCTION__, CLUSREG_NAME_PHYSDISK_DISKSIGNATURE, strRegKey.c_str(), GetLastError() );
				reg.Close();
				return FALSE;
			}
		}
		else
		{
			DWORD dwSizeInChars = sizeof(diskRes.diskGUID);
			dwRet = reg.QueryStringValue( CLUSREG_NAME_PHYSDISK_DISKIDGUID, diskRes.diskGUID, &dwSizeInChars );
			if( dwRet !=0 )
			{
				m_log.LogW(LL_ERR, dwRet, L"%s: Failed query value of [%s] under [%s]. ERR=%d", __WFUNCTION__, CLUSREG_NAME_PHYSDISK_DISKIDGUID, strRegKey.c_str(), GetLastError() );
				reg.Close();
				return FALSE;
			}
		}
	}
	return TRUE;
}

BOOL ClusterMonitor::isThreadStoped( DWORD dwWaitInMilSeconds )
{
	return WAIT_OBJECT_0 == ::WaitForSingleObject(m_hStopEvent, dwWaitInMilSeconds );
}

DWORD ClusterMonitor::MonitorDiskResources( HCLUSTER hCluster )
{
	//DWORD dwMyEvent =	CLUSTER_CHANGE_RESOURCE_STATE|		// resource state changed	
	//					CLUSTER_CHANGE_RESOURCE_ADDED|		// resource added
	//					CLUSTER_CHANGE_RESOURCE_DELETED|	// resource deleted						
	//					CLUSTER_CHANGE_CLUSTER_RECONNECT|	// cluster reconnected
	//					CLUSTER_CHANGE_CLUSTER_STATE;		// cluster become unavailble

	DWORD dwMyEvent =	CLUSTER_CHANGE_ALL;

	HCHANGE hChange = (HCHANGE)INVALID_HANDLE_VALUE;
	DWORD   dwFilterType = 0;
	DWORD   dwNotifyKey = 0;
	DWORD   dwStatus = 0;
	//
	// create a cluster notify port
	//
	hChange = CreateClusterNotifyPort( hChange, (HCLUSTER)hCluster, dwMyEvent, dwMyEvent );
	if( hChange==NULL )
	{
		DWORD dwErr = ::GetLastError();
		m_log.LogW(LL_ERR, dwErr, L"%s: Failed to call function CreateClusterNotifyPort", __WFUNCTION__ );
		return dwErr;
	}

	WCHAR szObjectName[1024] = {0};	
	DWORD dwLastTickcount = GetTickCount();
	while( !isThreadStoped(0) )
	{
		//
		// read the cluster notification
		//
		ZeroMemory( szObjectName, sizeof(szObjectName) );
		DWORD dwNameSize = _countof(szObjectName);
		dwStatus = GetClusterNotify(hChange,  
									(DWORD_PTR*)&dwNotifyKey,
									&dwFilterType,
									szObjectName,
									&dwNameSize,
									3000 );

		DWORD dw = GetTickCount();
		if( dw-dwLastTickcount > 30000 )
		{
			CleanDiskResources( FALSE );
			dwLastTickcount = dw;
		}

		if( dwStatus == WAIT_TIMEOUT )
			continue;

		m_log.LogW(LL_DET, 0, L"%s: GetClusterNotify notify restured %d", __WFUNCTION__, dwStatus );

		if( dwStatus != ERROR_SUCCESS )
			continue;

		UpdateClusterTag( );
		
		m_log.LogW(LL_DET, 0, L"%s: Received a cluster notification %d from resource %s", __WFUNCTION__, dwFilterType, szObjectName );

		if( dwFilterType == CLUSTER_CHANGE_CLUSTER_RECONNECT ){
			m_log.LogW(LL_DET, 0, L"%s: Received a cluster notification of reconnected", __WFUNCTION__ );
			break;
		}
		else if( dwFilterType == CLUSTER_CHANGE_CLUSTER_STATE ){
			m_log.LogW(LL_INF, 0, L"%s: Received a cluster notification that cluster become unavailable", __WFUNCTION__ );
			CleanDiskResources(TRUE);
			break;
		}
		else if( dwFilterType == CLUSTER_CHANGE_RESOURCE_STATE || 
			     dwFilterType == CLUSTER_CHANGE_RESOURCE_ADDED || 
				 dwFilterType == CLUSTER_CHANGE_RESOURCE_PROPERTY ){
			m_log.LogW(LL_INF, 0, L"%s: Received a cluster notification that resource [%s] changed", __WFUNCTION__, szObjectName );
			UpdateDiskByResourceName( hCluster, szObjectName );
			continue;
		}
		else if( dwFilterType == CLUSTER_CHANGE_RESOURCE_DELETED ){
			m_log.LogW(LL_INF, 0, L"%s: Received a cluster notification that resource [%s] deleted", __WFUNCTION__, szObjectName );
			DeleteDiskByResourceName( szObjectName );
			continue;
		}
	}
	return 0;
}

//
// call this function, when first time cluster become available
//
DWORD ClusterMonitor::InitDiskResources( HCLUSTER hClust )
{
	DWORD dwRet = 0;
	//
	// check if "PaxosTag" (for windows 2008) or "RegistrySequence" (for windows 2003) changed since last update time
	// if this value changed, mark all existing claster shared disk as "Changed", so that next time of backup will
	// be full or verify.
	//
	m_log.LogW(LL_INF, dwRet, L"%s: Start to initialize cluster shared disks.", __WFUNCTION__ );
	
	wstring msClusterTag = L"", udpClusterTag=L"";
	readClusterTag( msClusterTag, udpClusterTag );
	if( wcscmp(msClusterTag.c_str(), udpClusterTag.c_str()) ==0 ){		
		m_log.LogW(LL_INF, dwRet, L"%s: Cluster state was notchanged since last time. Do nothing.", __WFUNCTION__ );
		return 0;
	}

	UpdateClusterTag( );

	CRegistry udpReg;
	dwRet = udpReg.Open( REG_K_UDP_CLUSTER_ROOT);
	if( 0 != dwRet ){
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to open registry %s", __WFUNCTION__, REG_K_UDP_CLUSTER_ROOT );
		return dwRet;
	}
	//
	// merge current shared disks into UDP registry
	//
	std::vector<CLUSTER_DISK_INFO> vecDiskResources;	
	EnumDiskResources( hClust, vecDiskResources );

	std::vector<wstring> vecSubKeys;
	udpReg.EnumKeyNames( vecSubKeys );
	for( std::vector<wstring>::iterator itK=vecSubKeys.begin(); itK!=vecSubKeys.end(); itK++ )
	{
		CLUSTER_DISK_INFO diskInfo;
		if( 0 != readDiskInfo( (*itK), diskInfo ) )
			continue;

		BOOL bExisting = FALSE;
		for( std::vector<CLUSTER_DISK_INFO>::iterator itD=vecDiskResources.begin();itD!=vecDiskResources.end();itD++ )
		{
			if( sameDisk( diskInfo, (*itD) ) ){
				bExisting = TRUE;
		
				wcscpy_s( diskInfo.clusResourceID, (*itD).clusResourceID );
				wcscpy_s( diskInfo.resourceName, (*itD).resourceName );
				diskInfo.lastUpdateTime = current_time_utc( );
				diskInfo.status = DS_CHANGED;
				
				updateDiskInfo( diskInfo );
				if( _wcsicmp( (*itK).c_str(), (*itD).clusResourceID ) !=0 )
					udpReg.DeleteKey( (*itK).c_str() );
				
				vecDiskResources.erase( itD );
				break;
			}
		}

		if( !bExisting ){
			diskInfo.status = DS_REMOVED;
			diskInfo.lastUpdateTime = current_time_utc();
			updateDiskInfo( diskInfo );
		}
	}

	for( std::vector<CLUSTER_DISK_INFO>::iterator itD=vecDiskResources.begin();itD!=vecDiskResources.end();itD++ )
	{
		itD->lastUpdateTime = current_time_utc();
		itD->status = DS_CHANGED;
		updateDiskInfo( (*itD) );
	}

	return 0;
}

DWORD ClusterMonitor::Main( )
{	
	DWORD dwWaitTime = 0;
	while( !isThreadStoped(dwWaitTime) )
	{
		HCLUSTER hClust = ::OpenCluster( NULL );
		if( hClust==NULL )
		{
			// if no cluster installed or cluster service stopped, wait for 3 seconds
			dwWaitTime = 10000;
			CleanDiskResources(TRUE);
			continue;
		}

		dwWaitTime = 0;

		InitDiskResources( hClust );

		MonitorDiskResources( hClust );
		
		::CloseCluster( hClust );

		hClust = NULL;
	}

	return 0;
}

DWORD ClusterMonitor::StartMonitor( )
{
	m_log.LogW(LL_INF, 0, L"%s: Start cluster monitor thread....", __WFUNCTION__ );
	DWORD dwTid = 0;
	DWORD dwRet = 0;
	m_hThread = ::CreateThread( NULL, 0, _cluster_monitor_thread, this, CREATE_SUSPENDED, &dwTid );
	if( m_hThread==NULL )
	{
		dwRet = GetLastError();
		m_log.LogW(LL_INF, dwRet, L"%s: Failed to start cluster monitor thread", __WFUNCTION__ );
	}
	else
	{
		::ResumeThread( m_hThread );
		m_log.LogW(LL_INF, dwRet, L"%s: Cluster monitor thread started.", __WFUNCTION__ );
	}
	return dwRet;
}

DWORD ClusterMonitor::updateDiskInfo( const CLUSTER_DISK_INFO& diskInfo )
{
	DWORD dwRet = 0;
	CRegistry udpReg;
	dwRet = udpReg.OpenWrite( REG_K_UDP_CLUSTER_ROOT );
	if( 0 != dwRet ){
		dwRet = udpReg.Create( REG_K_UDP_CLUSTER_ROOT );
		if( 0 != dwRet ){
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create registry %s", __WFUNCTION__, REG_K_UDP_CLUSTER_ROOT );
			return dwRet;
		}
	}
	udpReg.Close();

	wstring strDiskKey = REG_K_UDP_CLUSTER_ROOT;
	strDiskKey.append(L"\\");
	strDiskKey.append(diskInfo.clusResourceID);

	CRegistry diskReg;
	dwRet = diskReg.OpenWrite( strDiskKey.c_str() );
	if( 0!=dwRet ){
		dwRet = diskReg.Create( strDiskKey.c_str() );
		if( 0 != dwRet ){
			m_log.LogW(LL_ERR, dwRet, L"%s: Failed to create registry %s", __WFUNCTION__, strDiskKey.c_str() );
			return dwRet;
		}
	}

	diskReg.SetDWORDValue(REG_V_DISK_ID_TYPE, diskInfo.diskIdType );
	diskReg.SetDWORDValue(REG_V_DISK_SIGNATURE, diskInfo.diskSignature );
	diskReg.SetDWORDValue(REG_V_DISK_STATUS, diskInfo.status );
	diskReg.SetStringValue(REG_V_DISK_GUID, diskInfo.diskGUID );
	diskReg.SetStringValue(REG_V_DISK_RESOURCE_NAME, diskInfo.resourceName );
	diskReg.SetQWORDValue(REG_V_LAST_UPDATE_TIME, diskInfo.lastUpdateTime );
	diskReg.SetQWORDValue(REG_V_LAST_BACKUP_TIME, diskInfo.lastBackupTime );
	diskReg.Close();
	return 0;
}

DWORD ClusterMonitor::readDiskInfo( const wstring& strResourceID, CLUSTER_DISK_INFO& diskInfo )
{
	DWORD dwRet = 0;
	wstring strDiskKey = REG_K_UDP_CLUSTER_ROOT;
	strDiskKey.append(L"\\");
	strDiskKey.append(strResourceID);

	CRegistry diskReg;
	dwRet = diskReg.Open( strDiskKey.c_str() );
	if( 0!=dwRet ){
		return dwRet;
	}

	wcsncpy_s( diskInfo.clusResourceID, _countof(diskInfo.clusResourceID), strResourceID.c_str(), _TRUNCATE );
	DWORD dwSize = sizeof(diskInfo.diskGUID);
	diskReg.QueryStringValue( REG_V_DISK_GUID, diskInfo.diskGUID, &dwSize );
	dwSize = sizeof(diskInfo.resourceName);
	diskReg.QueryStringValue( REG_V_DISK_RESOURCE_NAME, diskInfo.resourceName, &dwSize );
	diskReg.QueryDWORDValue(  REG_V_DISK_STATUS, diskInfo.status );
	diskReg.QueryDWORDValue(  REG_V_DISK_SIGNATURE, diskInfo.diskSignature );
	diskReg.QueryDWORDValue(  REG_V_DISK_ID_TYPE, diskInfo.diskIdType );
	diskReg.QueryQWORDValue(  REG_V_LAST_UPDATE_TIME, diskInfo.lastUpdateTime );
	diskReg.QueryQWORDValue(  REG_V_LAST_BACKUP_TIME, diskInfo.lastBackupTime );
	diskReg.Close();
	return 0;
}

BOOL ClusterMonitor::readClusterTag( wstring& strMsClusterTag, wstring& strUDPClusterTag )
{
	wstring strTagKey = CLUSTER_TAG_WIN2008;
	if( m_bWin2003 )
		strTagKey = CLUSTER_TAG_WIN2003;

	strMsClusterTag  = L"";
	strUDPClusterTag = L"";
	DWORD dwRet = 0;

	CRegistry cluReg;
	dwRet = cluReg.Open( REG_K_MS_CLUSTER_ROOT );
	if( 0 != dwRet ){
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to open registry %s", __WFUNCTION__, REG_K_MS_CLUSTER_ROOT );
	}
	else{
		if( m_bWin2003 ){
			DWORD dwTag = 0;
			cluReg.QueryDWORDValue( strTagKey.c_str(), dwTag );
			WCHAR szTag[MAX_PATH]={0}; swprintf_s( szTag, _countof(szTag), L"%d", dwTag );
			strMsClusterTag = szTag;
		}
		else{
			cluReg.QueryStringValue( strTagKey.c_str(), strMsClusterTag );
		}
		cluReg.Close();
	}
	
	
	CRegistry udpReg;
	dwRet = udpReg.Open( REG_K_UDP_CLUSTER_ROOT );
	if( 0 != dwRet ){
		m_log.LogW(LL_ERR, dwRet, L"%s: Failed to open registry %s", __WFUNCTION__, REG_K_MS_CLUSTER_ROOT );
	}
	else{
		udpReg.QueryStringValue( strTagKey.c_str(), strUDPClusterTag );
		udpReg.Close();
	}
	return TRUE;
}

BOOL ClusterMonitor::sameDisk( const CLUSTER_DISK_INFO& diskInfo1, const CLUSTER_DISK_INFO& diskInfo2 )
{
	if( diskInfo1.diskIdType != diskInfo2.diskIdType )
		return FALSE;

	if( diskInfo1.diskIdType ==0 )
		return diskInfo1.diskSignature==diskInfo2.diskSignature;
	else
		return wcsicmp( diskInfo1.diskGUID, diskInfo2.diskGUID ) == 0;
}

DWORD ClusterMonitor::StopMonitor( )
{
	if( m_hThread != NULL )
	{
		m_log.LogW(LL_INF, 0, L"%s: Stop cluster monitor thread....", __WFUNCTION__ );
		::SetEvent( m_hStopEvent );
		if( WAIT_OBJECT_0 != ::WaitForSingleObject( m_hThread, 15000 ) )
		{
			// if can not stop the monitor thread in 15 secondes, kill this thread
			m_log.LogW(LL_INF, 0, L"%s: Can not stop cluster monitor thread. Terminate it.", __WFUNCTION__ );
			::TerminateThread( m_hThread, 0 );
		}
		m_log.LogW(LL_INF, 0, L"%s: Cluster monitor thread stopped", __WFUNCTION__ );
	}
	return 0;
}

DWORD WINAPI _cluster_monitor_thread( LPVOID pArgs )
{
	ClusterMonitor* pMonitor = (ClusterMonitor*)pArgs;
	return pMonitor->Main( );
}

