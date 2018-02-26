#pragma once

#include <windows.h>

#include <string>
#include <vector>
using namespace std;

static CONST TCHAR DNS_UPDATER_SECTION_NAME[] = TEXT("CommandLines");
static CONST TCHAR DNS_UPDATER_COMMAND_LINE_NAME[] = TEXT("CommandLine%u");

//<huvfe01>2012-12-20 for server type
enum E_DNS_SERVER_TYPE
{
    DNS_SERVER_TYPE_WINDOWS = 0,
    DNS_SERVER_TYPE_BIND,
    DNS_SERVER_TYPE_INVALID
};

#define DNS_UPDATER_CFG_ARG_COUNT 2
typedef struct tagDNSUpdaterCfg
{
    INT     nDnsSerserType;
    wstring strUpdaterCmdLine;
}DNS_UPDATER_CFG_S;

struct  DNSUpdaterCmdLine
{
	wstring	dns;		//-dns           DNS server(s)
	wstring	hostname;	//-hostname      Host fully qualified name (Local server name by default)  (The hostname must include the dns zone information, such as hostname.xx.com)
	wstring	hostip;		//-hostip        Host IP address (Local IP by default)
	INT		ttl;		//-ttl           Time to live for records to be added (60 by default)
	wstring	username;	//-username      User credentials for authenticating the DNS server
	wstring	password;	//-password      User credentials for authenticating the DNS server  (The username and password is used for dynamic updates when DNS is configured to secure only updates.)
	wstring	keyfile;	//-keyfile       the full path of the file containing the DNS secure key for the DNS servers which are not AD integrated.(This keyfile is used for BIND DNS Server, and is not used for Windows DNS.)
    INT     dnsServerType;   // 0 use the windows DNS, 1 use the Bind Server

    DNSUpdaterCmdLine() :
    ttl(-1),
    dnsServerType(-1)
    {
    }
};


//////////////////////////////////////////////////////////////////////////
HRESULT DNSUpdaterSaveBinToIni(LPCTSTR pExe, CONST vector<DNSUpdaterCmdLine> & vecBin, LPCTSTR pIni, BOOL bencrypted );
HRESULT DNSUpdaterLoadCommandLineFromIni(LPCTSTR pIni, OUT vector<DNS_UPDATER_CFG_S> & vecCfg, BOOL bEncrypted);
HRESULT DNSUpdaterExecCommands(CONST vector<DNS_UPDATER_CFG_S> & vecCfg);
HRESULT DNSUpdaterCovertP2E(CONST vector<wstring> & CmdLines, LPCTSTR pEncryptedIni);
HRESULT DNSUpdaterCovertE2P(CONST vector<wstring> & CmdLines, LPCTSTR pPlainIni);