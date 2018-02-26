#pragma once
#include <Iphlpapi.h>
#include <NtDDNdis.h>
#include "id2dproxydef.h"
#include <list>
#include <vector>

class CHelper;
class CD2DProxyImpl:public ID2DProxyServer, IMSGCallback
{
	CD2DProxyImpl(void);
	virtual ~CD2DProxyImpl(void);
public:
	static CD2DProxyImpl * GetInstance();
	CA_RELEASE_UNDEL();
	CA_BEGIN_INTERFACE_MAP()
		CA_INTERFACE(ID2DProxyServer)
		CA_INTERFACE(IMSGCallback)
	CA_END_INTERFACE_MAP()
public:
	// ID2DProxyServer
	virtual HRESULT Init(JNIEnv *pJnienv);
	virtual HRESULT UnInit();
	virtual HRESULT Run();
	// IMSGCallback
	virtual HRESULT OnMessage( XIN BYTE * pinBUF, XIN DWORD ninSIZE, XOUT BYTE * poutBUF, XINOUT DWORD & noutSIZE);
protected:
	HRESULT ExcuteTask();
	HRESULT FinishTask(ITaskItem *);
	HRESULT AddTask(ITaskItem * pvTask);
	HRESULT OnSendMergeMail(PD2DIPC_MERGE_INFO pstInfo);
	HRESULT OnFailedToBackupVM(PD2DIPC_VMBACKUP_INFO pstInfo);

	HRESULT OnCheckLicense(PD2D_CHKLIC_LISTENTRY pListEntry);
	HRESULT OnSendChildJobsToProxy(PD2D_CPP_VM_JOB_CONTEXT_LIST pListEntry);
	DWORD GetCurProcessToken();
protected:
	JNIEnv * m_pvJEnv;
	HANDLE m_hQuitEvent;
	volatile BOOL m_isQuit;
	std::list<ITaskItem *> m_Task;
	CRITICAL_SECTION m_csTask;
	static CD2DProxyImpl g_objD2dPxy;
	HANDLE m_hSignal;
};


class CLockHelper
{
public:
	CLockHelper::CLockHelper(CRITICAL_SECTION * pCS)
	{
		m_pCS = pCS;
		EnterCriticalSection(m_pCS);
	}

	CLockHelper::~CLockHelper()
	{
		LeaveCriticalSection(m_pCS);
		m_pCS =NULL;
	}

protected:
	CRITICAL_SECTION * m_pCS;
};

class CHelper
{
public:
	static jclass FindJavaClass(JNIEnv *env, char * pClassName);
	static jmethodID FindMethodInJniClass(JNIEnv *env, jclass clsLicCHK,  char * methodName, char *funcSignature);
	static BOOL CHelper::DescribeException(JNIEnv *env);
};

class CTaskMergeMail :public ITaskItem
{
	CTaskMergeMail(void);
	virtual ~CTaskMergeMail(void);
public:
	static CTaskMergeMail *  CreateInstance();
	CA_COMCOUNTERIMPL();
	CA_BEGIN_INTERFACE_MAP()
		CA_INTERFACE(ITaskItem)
	CA_END_INTERFACE_MAP()

public:
	//ITaskItem
	virtual HRESULT DoWork();
public:
	HRESULT SetMergeInfo(PD2DIPC_MERGE_INFO pstInfo);
protected:
	D2DIPC_MERGE_INFO m_mergeInfo;

};

//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm #2 worker
class CTaskSendChildJobsToProxy :public ITaskItem
{
	CTaskSendChildJobsToProxy(void);
	virtual ~CTaskSendChildJobsToProxy(void);
public:
	static CTaskSendChildJobsToProxy *  CreateInstance();
	CA_COMCOUNTERIMPL();
	CA_BEGIN_INTERFACE_MAP()
		CA_INTERFACE(ITaskItem)
	CA_END_INTERFACE_MAP()

public:
	//ITaskItem
	virtual HRESULT DoWork();
public:
	HRESULT SetCTaskSendChildJobsToProxyInfo(PD2D_CPP_VM_JOB_CONTEXT_LIST pstInfo);
protected:
	D2D_CPP_VM_JOB_CONTEXT_LIST m_Info;


private: //<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm #2 worker
	static HRESULT NewObject(JNIEnv *env, jobject& JObjOut, const D2D_CPP_VM_JOB_CONTEXT_ITEM & item);
	static HRESULT NewList(JNIEnv *env, jobject& JObjOut, const D2D_CPP_VM_JOB_CONTEXT_LIST & items);
};

//
// class to monitor network adpater changes.
//
typedef struct _NIC_ADDR_
{
	wstring     ipAddr;
	wstring     ipMask;

	_NIC_ADDR_()
	{
		ipAddr = L"";
		ipMask = L"";
	}

	_NIC_ADDR_( const _NIC_ADDR_& other )
	{
		ipAddr = other.ipAddr;
		ipMask = other.ipMask;
	}

	_NIC_ADDR_& operator=(const _NIC_ADDR_ & other)
	{
		ipAddr = other.ipAddr;
		ipMask = other.ipMask;
		return (*this);
	}
}NIC_ADDR, *PNIC_ADDR;

typedef struct _NIC_INFO_
{
	wstring					adapterName;
	wstring					nicDescription;
	wstring					macAddr;
	std::vector<NIC_ADDR>	ipAddrList;
	std::vector<NIC_ADDR>	gatewayList;
	std::vector<NIC_ADDR>	dhcpServers;
	std::vector<NIC_ADDR>	primaryWinsServer;
	std::vector<NIC_ADDR>	secondWinsServer;
	BOOL bConnected;
	DWORD					dwSppeed; // Mbps
	BOOL					bDhcpEnabled;
	BOOL					bHaveWins;
	UINT					uType;

	_NIC_INFO_( )
	{
		adapterName = L"";
		nicDescription = L"";
		macAddr = L"";
		bConnected = FALSE;
		dwSppeed = 0;
		bDhcpEnabled = FALSE;
		bHaveWins= FALSE;
		uType = 0;;
		ipAddrList.clear();
		gatewayList.clear();
		dhcpServers.clear();
		primaryWinsServer.clear();
		secondWinsServer.clear();
	}

	_NIC_INFO_( const _NIC_INFO_& other )
	{
		adapterName = other.adapterName;
		nicDescription = other.nicDescription;
		macAddr = other.macAddr;
		bConnected = other.bConnected;
		dwSppeed = other.dwSppeed;
		bDhcpEnabled = other.bDhcpEnabled;
		bHaveWins= other.bHaveWins;
		uType = other.uType;
		ipAddrList.assign( other.ipAddrList.begin(), other.ipAddrList.end() );
		gatewayList.assign( other.gatewayList.begin(), other.gatewayList.end() );
		dhcpServers.assign( other.dhcpServers.begin(), other.dhcpServers.end() );
		primaryWinsServer.assign( other.primaryWinsServer.begin(), other.primaryWinsServer.end() );
		secondWinsServer.assign( other.secondWinsServer.begin(), other.secondWinsServer.end() );
	}

	_NIC_INFO_& operator= ( const _NIC_INFO_& other )
	{
		adapterName = other.adapterName;
		nicDescription = other.nicDescription;
		macAddr = other.macAddr;
		bConnected = other.bConnected;
		dwSppeed = other.dwSppeed;
		bDhcpEnabled = other.bDhcpEnabled;
		bHaveWins= other.bHaveWins;
		uType = other.uType;
		ipAddrList.assign( other.ipAddrList.begin(), other.ipAddrList.end() );
		gatewayList.assign( other.gatewayList.begin(), other.gatewayList.end() );
		dhcpServers.assign( other.dhcpServers.begin(), other.dhcpServers.end() );
		primaryWinsServer.assign( other.primaryWinsServer.begin(), other.primaryWinsServer.end() );
		secondWinsServer.assign( other.secondWinsServer.begin(), other.secondWinsServer.end() );

		return (*this);
	}
}NIC_INFO, *PNIC_INFO;

// Copy from ntddndis.h since _WIN32_WINNT is not new enough
#if ((NTDDI_VERSION < NTDDI_VISTA) && !NDIS_SUPPORT_NDIS6)

//
// Describes the large send offload version 1 capabilities
// or configuration of the NIC. Used in NDIS_OFFLOAD structure
//
typedef struct _NDIS_TCP_LARGE_SEND_OFFLOAD_V1
{

    struct
    {
        ULONG     Encapsulation;
        ULONG     MaxOffLoadSize;
        ULONG     MinSegmentCount;
        ULONG     TcpOptions:2;
        ULONG     IpOptions:2;
    } IPv4;

} NDIS_TCP_LARGE_SEND_OFFLOAD_V1, *PNDIS_TCP_LARGE_SEND_OFFLOAD_V1;


//
// Describes the checksum task offload capabilities or configuration 
// of the NIC. used in NDIS_OFFLOAD structure
//
typedef struct _NDIS_TCP_IP_CHECKSUM_OFFLOAD
{

    struct
    {
        ULONG       Encapsulation;
        ULONG       IpOptionsSupported:2;
        ULONG       TcpOptionsSupported:2;
        ULONG       TcpChecksum:2;
        ULONG       UdpChecksum:2;
        ULONG       IpChecksum:2;
    } IPv4Transmit;

    struct
    {
        ULONG       Encapsulation;
        ULONG       IpOptionsSupported:2;
        ULONG       TcpOptionsSupported:2;
        ULONG       TcpChecksum:2;
        ULONG       UdpChecksum:2;
        ULONG       IpChecksum:2;
    } IPv4Receive;


    struct
    {
        ULONG       Encapsulation;
        ULONG       IpExtensionHeadersSupported:2;
        ULONG       TcpOptionsSupported:2;
        ULONG       TcpChecksum:2;
        ULONG       UdpChecksum:2;

    } IPv6Transmit;

    struct
    {
        ULONG       Encapsulation;
        ULONG       IpExtensionHeadersSupported:2;
        ULONG       TcpOptionsSupported:2;
        ULONG       TcpChecksum:2;
        ULONG       UdpChecksum:2;

    } IPv6Receive;

} NDIS_TCP_IP_CHECKSUM_OFFLOAD, *PNDIS_TCP_IP_CHECKSUM_OFFLOAD;


//
// Describes the IPsec task offload version 1 capabilities 
// or configuration of the NIC. Used in NDIS_OFFLOAD structure
//
typedef struct _NDIS_IPSEC_OFFLOAD_V1
{
    struct
    {
        ULONG   Encapsulation;
        ULONG   AhEspCombined;
        ULONG   TransportTunnelCombined;
        ULONG   IPv4Options;
        ULONG   Flags;
    } Supported;

    struct
    {
        ULONG   Md5:2;
        ULONG   Sha_1:2;
        ULONG   Transport:2;
        ULONG   Tunnel:2;
        ULONG   Send:2;
        ULONG   Receive:2;
    } IPv4AH;

    struct
    {
        ULONG   Des:2;
        ULONG   Reserved:2;
        ULONG   TripleDes:2;
        ULONG   NullEsp:2;
        ULONG   Transport:2;
        ULONG   Tunnel:2;
        ULONG   Send:2;
        ULONG   Receive:2;
    } IPv4ESP;

} NDIS_IPSEC_OFFLOAD_V1, *PNDIS_IPSEC_OFFLOAD_V1;

//
// Encapsulation types that are used during offload in query and set
//
#define NDIS_ENCAPSULATION_NOT_SUPPORTED                0x00000000                         

//
// Describes the large send offload version 2 capabilities
// or configuration of the NIC. Used in NDIS_OFFLOAD structure
//
typedef struct _NDIS_TCP_LARGE_SEND_OFFLOAD_V2
{
    struct 
    {
        ULONG     Encapsulation;
        ULONG     MaxOffLoadSize;
        ULONG     MinSegmentCount;
    }IPv4;

    struct 
    {
        ULONG     Encapsulation;
        ULONG     MaxOffLoadSize;
        ULONG     MinSegmentCount;
        ULONG     IpExtensionHeadersSupported:2;
        ULONG     TcpOptionsSupported:2;
    }IPv6;

} NDIS_TCP_LARGE_SEND_OFFLOAD_V2, *PNDIS_TCP_LARGE_SEND_OFFLOAD_V2;

typedef struct _NDIS_OFFLOAD
{
    NDIS_OBJECT_HEADER                  Header;

    //
    // Checksum Offload information
    //
    NDIS_TCP_IP_CHECKSUM_OFFLOAD        Checksum;

    //
    // Large Send Offload information
    //
    NDIS_TCP_LARGE_SEND_OFFLOAD_V1      LsoV1;

    //
    // IPsec Offload Information
    //
    NDIS_IPSEC_OFFLOAD_V1               IPsecV1;
    //
    // Large Send Offload version 2Information
    //
    NDIS_TCP_LARGE_SEND_OFFLOAD_V2      LsoV2;

    ULONG                               Flags;

#if (NDIS_SUPPORT_NDIS61)
    //
    //IPsec offload V2
    //
    NDIS_IPSEC_OFFLOAD_V2               IPsecV2;
#endif // (NDIS_SUPPORT_NDIS61)


}NDIS_OFFLOAD, *PNDIS_OFFLOAD;
#endif

#define AFSTOR_KEY  CST_REG_ROOT_L L"\\AFstor" 
DWORD GetRegistryVal(TCHAR *szValName, DWORD *pdwValue, DWORD dwDefaultValue);

class CNICMonitor
{
protected:
	static CNICMonitor*   _instance_;

public:
	static CNICMonitor* CreateInstance( );

	static void         DeleteInstance( );

	static void         GetNicAdapters( std::vector<NIC_INFO>& vecNics, BOOL bDetails );

	static BOOL			IsAdapterConnected( const wstring& strAdapterName );

    static DWORD		GetAdapterSpeed( const wstring& strAdapterName );

	static BOOL			IsTSOEnabled( const wstring& strAdapterName );

	static void			OutputNICInfo( const NIC_INFO& nic );

    static DWORD		FilterOutE1000e( std::vector<NIC_INFO>& vecNics );

	static void			GetDnsSuffixes(std::vector<wstring>& vecNics);

protected:
	static DWORD WINAPI MonitorThreadFunc( LPVOID pArg );

public:
	DWORD        Start( );

	void         Stop( );

	void         OnNetworkChanges( );

	DWORD		 Run( );

protected:
	CNICMonitor( );

	~CNICMonitor( );

	void  Init( );

protected:
	OVERLAPPED               m_overlap;
	HANDLE					 m_hThread;
	HANDLE					 m_hStopEvent;
	BOOL                     m_bStopFlag;
	std::vector<NIC_INFO>	 m_vecNics;
};