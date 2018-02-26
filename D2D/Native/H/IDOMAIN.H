//		This is for Alert 2.0
#ifndef __IDOMAIN_H
#define __IDOMAIN_H

#ifdef __cplusplus
extern "C" {
#endif

#define PRODUCT_NAME_SIZE		20

#pragma	pack(1)
//////// M.M
#define MAX_NUM_OF_ENTRYS				0xffffffff

#define DSCV_BROADCAST					0
#define DSCV_PING						1
#define DSCV_ARP						2
#define DSCV_ARP_FAST					3

typedef struct tagASRecord
{
	TCHAR  ServerName[MAX_COMPUTERNAME_LENGTH + 1];
	TCHAR  DomainName[MAX_COMPUTERNAME_LENGTH + 1];
	ULONG  ulTimeStamp;
	DWORD  BroadcastOwnerId;				//4
	ULONG  ulIP;										//4

}ASRecord;
typedef ASRecord* PASRecord;

// Oripin: UNICODE_JIS Support yelsu01

typedef struct tagASRecordW
{
	WCHAR  ServerName[MAX_COMPUTERNAME_LENGTH + 1];
	WCHAR  DomainName[MAX_COMPUTERNAME_LENGTH + 1];
	ULONG  ulTimeStamp;
	DWORD  BroadcastOwnerId;				//4
	ULONG  ulIP;										//4

}ASRecordW;
typedef ASRecordW* PASRecordW;
typedef struct tagASProdRecord
{
	TCHAR ProdName[PRODUCT_NAME_SIZE];
	DWORD dwProdId;			//4
	DWORD dwProdType;		//4
	DWORD dwProdVersion;	//4
	ULONG ulExpireTime;		//4
	DWORD dwUserLevel;		//4
	char  SerialNumber[9];	//9
	DWORD dwLicenceCRC;		//4				
	char  AgentInfo[16];	//16
	char  aReserv[3];			
							//72
}  ASProdRecord;
typedef  ASProdRecord* PasProdRecord;
// Oripin: UNICODE_JIS Support :yelsu01
typedef struct tagASProdRecordW
{
	WCHAR ProdName[PRODUCT_NAME_SIZE];
	DWORD dwProdId;			//4
	DWORD dwProdType;		//4
	DWORD dwProdVersion;	//4
	ULONG ulExpireTime;		//4
	DWORD dwUserLevel;		//4
	WCHAR  SerialNumber[9];	//9
	DWORD dwLicenceCRC;		//4	
	// Here AgentInfo contains IPADDRESS, and used in
	// BOOL UnPackIPXAddress (ASProdRecord *ProductInfo, LPWSTR  pszIPXAddress)
	// p =  ProductInfo->AgentInfo + 4; a1 = (p[0] << 8) + p[1];
	// _swprintf  (pszIPXAddress, "%04X%04X-%04X%04X%04X", a1, a2, n1, n2, n3);
	WCHAR  AgentInfo[16];	//16
	char  aReserv[3];			
	//72
}  ASProdRecordW;

typedef  ASProdRecordW* PasProdRecordW;
///// For EnumDomain and EnumMember
typedef struct
{
	char DomainName[48];
	char MasterServerName[48];
	char ServerName[48];
	DWORD OSVersion;
	DWORD InocuLANVersion;
	DWORD EngineVersion;
	DWORD EngineDate;
	DWORD SigntureVersion;
	DWORD SigntureDate;
	char  SerialNumber[8];
	DWORD MachineStatus;
} ServerData;

typedef struct tagAdvertize
{
	DWORD	dwQueryType;				// Level of required functionality (Query for Info, Advertize own set and etc.) 
	ULONG	ulDscvType;					// Broadcast type: ping, ARP cache, fast ARP cash
}	ASADVERTIZE;
typedef ASADVERTIZE* PASADVERTIZE;

#pragma	pack()

//event name to sync broadcasting

/////////////////////// Note: ///////////////////////////////
//	See lic_def.h for ProdId definitions.				   //
//	Stream Id is defined and maintained by each product.   //
//	It is must be unique in the boundaries of each product.// 
//	For example for ARCserveIT stream Id definitions see   //
//	admin.h												   //
/////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////
/////// API set to get  info from Discovery table			   ///////////
/////////////////////////////////////////////////////////////////////////

LONG	APIENTRY ASInitEnumServer(PHKEY phKey, ULONG* nTotal);
LONG	APIENTRY ASTblEnumServer(HKEY hKey, ULONG ulInd, PASRecord pas);
LONG	APIENTRY ASTblEnumServerW(HKEY hKey, ULONG ulInd, PASRecordW pas);
LONG	APIENTRY ASTblEnumServerByProductId(HKEY hSrvKey, ULONG* pulSrvInd, DWORD dwProdId, PASRecord pasRecord, PasProdRecord pProdInfo);
LONG	APIENTRY ASTblEnumServerByProductIdW(
	HKEY hSrvKey, ULONG* pulSrvInd, 
	DWORD dwProdId, OUT PASRecordW pasRecordW, 
	OUT PasProdRecordW pProdInfoW);
LONG	APIENTRY ASDeInitEnumServer(HKEY hKey);

LONG	APIENTRY ASTblEnumProd(HKEY hKeyProd, ULONG ulInd, PasProdRecord pas);
LONG	APIENTRY ASDeInitEnumProduct(HKEY hKeyProd);
LONG	APIENTRY ASInitEnumProduct(PTCHAR sHost,PHKEY phKeyProd, ULONG* nTotal);

LONG	APIENTRY ASTblGetServerInfoFlags(LPCTSTR lpszServerName, ULONG* Flags);

////////////////////////////////////////////////////////////////////////////
//////// API set to get/set Product Descriptor				////////////////
////////////////////////////////////////////////////////////////////////////
LONG	WINAPI	ASGetProductDescriptor(DWORD dwProdId, ASProduct* pASProduct);
LONG	WINAPI	ASSetProductDescriptor(const ASProduct* pASProduct);
LONG	WINAPI	ASDelProductDescriptor(DWORD dwProdId);

//M.M 12/21/99
LONG	WINAPI	ASGetProductDescriptorEx(LPCTSTR ServerName, DWORD dwProdId, ASProduct* pASProduct);
LONG	WINAPI	ASSetProductDescriptorEx(LPCTSTR ServerName, const ASProduct* pASProduct);

LONG	WINAPI	ASRemoveServerFromAllDT(LPCTSTR ServerName); 
/////////////////////////////////////////////////////////////////////////////
//////// API set to configure DS							/////////////////	
/////////////////////////////////////////////////////////////////////////////
DWORD	WINAPI	ASDSSetMailSlotReadInterval(LPCTSTR Server, DWORD interval);
DWORD 	WINAPI	ASDSGetMailSlotReadInterval(LPCTSTR Server);

////////////////////////////////////////////////////////////////////////////
//////// API set to compute/validate CRC					////////////////
////////////////////////////////////////////////////////////////////////////
DWORD WINAPI ASCalcCRC(ULONG ulSize, PVOID pBuf);

////////////////////////////////////////////////////////////////////////////
//////// API Start Chey Apps Diescovery						////////////////
////////////////////////////////////////////////////////////////////////////

DWORD WINAPI ASStartDiscovery(LPCTSTR sServerName, PVOID pParam);

////////////////////////////////////////////////////////////////////////////
//////// API set to load(read) Network targets tables		////////////////
////////////////////////////////////////////////////////////////////////////

LONG	WINAPI ASGetDomains(LPCTSTR ServerName, char *lpData, ULONG ulBufLen);
LONG	WINAPI ASGetIPNets(LPCTSTR ServerName, char *lpData, ULONG ulBufLen);
LONG	WINAPI ASGetIPMasks(LPCTSTR ServerName, char *lpData, ULONG ulBufLen);

////////////////////////////////////////////////////////////////////////////
//////// API set to save() Network targets tables		////////////////
////////////////////////////////////////////////////////////////////////////

LONG	WINAPI ASSetDomains(LPCTSTR ServerName, char *lpData, ULONG ulBufLen,  ULONG nRecords);
LONG	WINAPI ASSetIPNets(LPCTSTR ServerName, char *lpData, ULONG ulBufLen, ULONG nRecords);
LONG	WINAPI ASSetIPMasks(LPCTSTR ServerName, char *lpData, ULONG ulBufLen, ULONG nRecords);

////// Server Broadcast functions
//int 	APIENTRY AlertBroadCast(char *pBuf,unsigned long BufSize);
BOOL 	APIENTRY InocuLANQueryRunning();
UINT	WINAPI InitDiscovery( PVOID pMode );
UINT	WINAPI DeInitDiscovery( PVOID pMode );

/////////////////////////////////////////////////////////////////////////////////////////////
///////	API set to set/get Configuration info for Cheyenne Products		////////////////////
///////																	///////////////////	
/////// Return value for all functions: MS error code (see Winerror.h)	//////////////////
/////////////////////////////////////////////////////////////////////////////////////////
DWORD WINAPI ASSetProductConfigInfo(LPCTSTR lpszServerName, DWORD ProdId, LPBYTE pbsConfigInf, LPDWORD lpdwSize);
DWORD WINAPI ASGetProductConfigInfo(LPCTSTR lpszServerName, DWORD ProdId, LPBYTE pbsConfigInf, LPDWORD lpdwSize);


DWORD WINAPI ASGetProductStreamInfo(LPCTSTR lpszServerName, DWORD ProdId, DWORD StreamId, LPBYTE pbsStreamInf, LPDWORD lpdwSize); 
DWORD WINAPI ASSetProductStreamInfo(LPCTSTR lpszServerName, DWORD ProdId, DWORD StreamId, LPBYTE pbsStreamInf, LPDWORD lpdwSize); 
//------------------------------------------------------------------------------------------------
//	Synopsys : 	ASGetProductCfgInfoFromDT() gets particular stream specified by StreamId 
//				from the Discovery Table for particular host and specified product. Entire 
//				config info can be returned if dwStreamId = 0;
//-------------------------------------------------------------------------------------------------
DWORD WINAPI ASGetProductCfgInfoFromDT(LPCTSTR lpszServerName,
							 DWORD	dwProdId,			 
							 DWORD	dwStreamId,
							 LPBYTE	 pbsConfigInf,
							 LPDWORD lpdwSize);

///////////////////////////////////////////////////////////////////////////////////////////
///////	API set to get path to different registry locations		//////////////////
/////////////////////////////////////////////////////////////////////////////////////////	
ULONG	WINAPI ASGetRegBaseString(PTCHAR name, USHORT limit);
ULONG	WINAPI ASGetRegRootString(PTCHAR name, USHORT limit);
ULONG	WINAPI ASGetDiscoveryTblString(PTCHAR name, USHORT limit);
ULONG	WINAPI ASGetProductsString(PTCHAR name, USHORT limit);

///////////////////////////////////////////////////////////////////////////////////////////
///////	API set to get and set cleanup discovery table flag		//////////////////
/////////////////////////////////////////////////////////////////////////////////////////	
BOOL APIENTRY ASGetCleanupDiscoveryTableFlag(ULONG* bCleanupDiscoveryTableFlag);
BOOL APIENTRY ASSetCleanupDiscoveryTableFlag(ULONG bCleanupDiscoveryTableFlag);

//UNICODE_SUPPORT_START SHADA07
LONG	APIENTRY ASTblEnumProdW(HKEY hKeyProd, ULONG ulInd, PasProdRecordW pasw);
LONG APIENTRY ASTblGetServerInfoFlagsW(LPCWSTR lpszServerName, ULONG* Flags);
LONG APIENTRY ASInitEnumProductW(PWCHAR sHost, PHKEY phKeyProd, ULONG* pnTotal);
//UNICODE_SUPPORT_END SHADA07

#ifdef __cplusplus
}
#endif

#endif //__IDOMAIN_H
