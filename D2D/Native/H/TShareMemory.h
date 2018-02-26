#pragma once
#include <string>

//
// For each share memory, it should have one mutex name and one share memory name.
// You can define them here
//
#define TSM_NAME_COMMON      L"EE178459-D13F-4891-B2BE-4F5FB2EDD51B"

//
// Used in activity log:
// Mutex Name:        Global\\6438CF1A-D714-4073-9546-6CE4A8528DB9-MUTEXT-<Process ID>
// Share Memory Name: Global\\6438CF1A-D714-4073-9546-6CE4A8528DB9-SMNAME-<Process ID>
//
#define TSM_NAME_ACTLOG      L"6438CF1A-D714-4073-9546-6CE4A8528DB9"


//
// Used in bmr job monitor
// Mutex Name:        Global\\F012DB97-FC44-43C2-88EB-534783C7BE5A-MUTEXT-<Job ID>
// Share Memory Name: Global\\F012DB97-FC44-43C2-88EB-534783C7BE5A-SMNAME-<Job ID>
//
#define TSM_NAME_BMRJOBMONITOR	L"F012DB97-FC44-43C2-88EB-534783C7BE5A"

// Used in BMR activity log
// Mutex Name:        Global\\0708AE13-AC12-40F3-9EF9-90F32DB97914-MUTEXT-0
// Share Memory Name: Global\\0708AE13-AC12-40F3-9EF9-90F32DB97914-SMNAME-0
//
#define TSM_NAME_BMR_ACTLOG		L"0708AE13-AC12-40F3-9EF9-90F32DB97914"

template<class _TData>
class CTShareMemory
{
public:
	CTShareMemory( LPCWSTR lpszName );

	virtual ~CTShareMemory( );

public:
	virtual DWORD CreateSM( DWORD dwSMId );

	virtual DWORD OpenSM( DWORD dwSMId );

	virtual void  CloseSM( );

	virtual DWORD UpdateSM( const _TData* pData );

	virtual DWORD GetData( _TData* pData );

protected:
	std::wstring     m_strName;	
	HANDLE		     m_hMutex;
	HANDLE           m_hSM;
};

template<class _TData>
CTShareMemory<_TData>::CTShareMemory( LPCWSTR lpszName )
	: m_hMutex( NULL )
	, m_hSM( NULL )
{
	if( !lpszName || !(*lpszName) )
		m_strName = TSM_NAME_COMMON;
	else
		m_strName = lpszName;
}

template<class _TData>
CTShareMemory<_TData>::~CTShareMemory()
{
}

template<class _TData>
DWORD CTShareMemory<_TData>::CreateSM( DWORD dwSMId )
{
	if( m_hMutex != NULL)
		return 0;

	DWORD dwRet = 0;
	//
	// create a mutex to protect the share memory
	//
	WCHAR szMutexName[MAX_PATH] = {0};
	swprintf_s( szMutexName, _ARRAYSIZE(szMutexName), L"Global\\%s-MUTEXT-%d", m_strName.c_str(), dwSMId );

	SECURITY_DESCRIPTOR sd1; 
	SECURITY_ATTRIBUTES sa1; 
	InitializeSecurityDescriptor(&sd1,SECURITY_DESCRIPTOR_REVISION); 
	SetSecurityDescriptorDacl(&sd1,TRUE,(PACL)NULL,FALSE); //set all the user can access the object      
	sa1.nLength=sizeof(SECURITY_ATTRIBUTES); 
	sa1.bInheritHandle=FALSE; 
	sa1.lpSecurityDescriptor=&sd1;

	m_hMutex = ::CreateMutex(  &sa1, FALSE, szMutexName );
	if( m_hMutex==NULL )
		return GetLastError();

	if( GetLastError()==ERROR_ALREADY_EXISTS )
		return 0;

	//
	// create the share memory
	//
	if( WAIT_OBJECT_0 != ::WaitForSingleObject( m_hMutex, INFINITE ) )
	{
		dwRet = GetLastError(); 
		::CloseHandle( m_hMutex );
		m_hMutex = NULL;
		return dwRet;
	}

	WCHAR szSMName[MAX_PATH] = {0};
	swprintf_s( szSMName, _ARRAYSIZE(szSMName), L"Global\\%s-SMNAME-%d", m_strName.c_str(), dwSMId );

	SECURITY_DESCRIPTOR sd2; 
	SECURITY_ATTRIBUTES sa2; 
	InitializeSecurityDescriptor(&sd2,SECURITY_DESCRIPTOR_REVISION); 
	SetSecurityDescriptorDacl(&sd2,TRUE,(PACL)NULL,FALSE); //set all the user can access the object      
	sa2.nLength=sizeof(SECURITY_ATTRIBUTES); 
	sa2.bInheritHandle=FALSE; 
	sa2.lpSecurityDescriptor=&sd2;

	DWORD dwSizeOfSM = sizeof(_TData);
	m_hSM = ::CreateFileMapping( INVALID_HANDLE_VALUE, &sa2, PAGE_READWRITE, 0, dwSizeOfSM, szSMName );
	if( m_hSM==NULL )
	{
		dwRet = GetLastError();
		
		::ReleaseMutex(m_hMutex);
		::CloseHandle( m_hMutex );
		m_hMutex = NULL;
		
		return dwRet;
	}

	::ReleaseMutex(m_hMutex);
	return 0;
}

template<class _TData>
DWORD CTShareMemory<_TData>::OpenSM( DWORD dwSMId )
{
	if( m_hSM!=NULL && m_hMutex!=NULL )
		return 0;

	if( m_hMutex==NULL )
	{
		WCHAR szMutexName[MAX_PATH] = {0};
		swprintf_s( szMutexName, _ARRAYSIZE(szMutexName), L"Global\\%s-MUTEXT-%d", m_strName.c_str(), dwSMId );
		m_hMutex = ::OpenMutex(  MUTEX_ALL_ACCESS, FALSE, szMutexName );
		if( m_hMutex==NULL )
			return GetLastError();
	}

	if( m_hSM==NULL )
	{
		WCHAR szSMName[MAX_PATH] = {0};
		swprintf_s( szSMName, _ARRAYSIZE(szSMName), L"Global\\%s-SMNAME-%d", m_strName.c_str(), dwSMId );
		m_hSM = ::OpenFileMapping( FILE_MAP_ALL_ACCESS, FALSE, szSMName );
		if( m_hSM==NULL )
			return GetLastError();
	}

	return 0;
}

template<class _TData>
void CTShareMemory<_TData>::CloseSM( )
{
	if( m_hSM )
	{
		::CloseHandle( m_hSM );
		m_hSM = NULL;
	}

	if( m_hMutex )
	{
		::CloseHandle( m_hMutex );
		m_hMutex = NULL;
	}
}

template<class _TData>
DWORD CTShareMemory<_TData>::UpdateSM( const _TData* pData )
{
	if( m_hSM==NULL || m_hMutex==NULL || pData==NULL )
		return (DWORD)-1;

	if( WAIT_OBJECT_0 != ::WaitForSingleObject( m_hMutex, INFINITE ) )
		return GetLastError();

	DWORD dwRet = 0;
	DWORD dwSizeOfSM = sizeof(_TData);
	LPVOID pBuf = ::MapViewOfFile( m_hSM, FILE_MAP_ALL_ACCESS, 0, 0, dwSizeOfSM );
	if( !pBuf )
	{
		dwRet = GetLastError();
		::ReleaseMutex( m_hMutex );
		return dwRet;
	}

	memcpy_s( pBuf, dwSizeOfSM, pData, dwSizeOfSM );

	::UnmapViewOfFile( pBuf );

	::ReleaseMutex( m_hMutex );

	return 0;
}

template<class _TData>
DWORD CTShareMemory<_TData>::GetData( _TData* pData )
{
	if( m_hSM==NULL || m_hMutex==NULL || pData==NULL )
		return (DWORD)-1;

	if( WAIT_OBJECT_0 != ::WaitForSingleObject( m_hMutex, INFINITE ) )
		return GetLastError();

	DWORD dwRet = 0;
	DWORD dwSizeOfSM = sizeof(_TData);
	LPVOID pBuf = ::MapViewOfFile( m_hSM, FILE_MAP_ALL_ACCESS, 0, 0, dwSizeOfSM );
	if( !pBuf )
	{
		dwRet = GetLastError();
		::ReleaseMutex( m_hMutex );
		return dwRet;
	}

	memcpy_s( pData, dwSizeOfSM, pBuf, dwSizeOfSM );

	::UnmapViewOfFile( pBuf );

	::ReleaseMutex( m_hMutex );

	return 0;
}