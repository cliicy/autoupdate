//
// Notwork throttling for Replication job. 
// zhazh06.  Added 12/2011
//

// I want to use some functions after XP
#define _WIN32_WINNT  0x0502 

#include "stdafx.h"
#include "com.ca.arcflash.rps.jni.RPSWSJNI.h" // JNI header created from WSJNI.java
#include "../Common/CommonJNIConv.h"							  // Utility functions
#include "../Common/CommonUtils.h"
#include "drcommonlib.h"
#include "RPSCoreFunction.h"
// #include "network_throttling.h"
#include <msxml2.h>
#include <comdef.h>


class CDSLock {
	LPCRITICAL_SECTION m_cs;
public :
	CDSLock (LPCRITICAL_SECTION pcs ){
		m_cs = pcs;
		EnterCriticalSection (pcs);
	}
	~CDSLock (){
		LeaveCriticalSection (m_cs);
	}
};

struct HOUR_SETTING
{
	int hour;       // 0 - 23
	int throttling; // KB
};

struct WEEKDAY_SETTING
{
	WCHAR * weekday;
	int list_len;
	HOUR_SETTING * plist;
};

struct POLICY_SETTING
{
	WCHAR * guid;
	int list_len;
	WEEKDAY_SETTING * plist;
};

// Function types are copied from network_throttling.h
typedef bool (*FN_init)(const wchar_t*policy_prefix);
typedef void (*FN_uninit)();
typedef bool (*FN_update_policy)(const wchar_t *xml_buf);
typedef bool (*FN_remove_policy)(const wchar_t *xml_buf);
typedef bool (*FN_clean_policy)(const wchar_t *necessary_xml_buf);
typedef bool (*FN_set_permission_info)(const wchar_t* domain, const wchar_t * user_name, const wchar_t * password);

// function pointers
static FN_init					fn_init					= NULL;
static FN_uninit				fn_uninit				= NULL;
static FN_update_policy			fn_update_policy		= NULL;
static FN_remove_policy			fn_remove_policy		= NULL;
static FN_clean_policy			fn_clean_policy			= NULL;
static FN_set_permission_info	fn_set_permission_info	= NULL;

// Data store APIs need be called exclusively
static CRITICAL_SECTION _CriticalSection_ = { 0 };
static WCHAR			g_RPSHome [256];
static BOOL				g_bInitialized = FALSE;


static void appendPath(__out LPWSTR buffer, size_t _SizeInWords, LPWSTR rootPath, LPWSTR subPath ) 
{
	if ( rootPath[ wcslen( rootPath ) - 1 ] != L'\\' )    // Is last character a '\'?
		_snwprintf_s(buffer, _SizeInWords, _TRUNCATE, L"%s\\%s", rootPath, subPath);
	else
		_snwprintf_s(buffer, _SizeInWords, _TRUNCATE, L"%s%s", rootPath, subPath);
}

// note : the returned string need to be freed by free()
static WCHAR * _CreateQosPolicyXML( POLICY_SETTING * policy )
{
	int  i, j;
	wchar_t buff [512] ;

	HRESULT hr;
	IXMLDOMDocument * pXMLDoc;
	IXMLDOMNode		* pXDN, * pXDN0;
	IXMLDOMElement	* pXElem;
	IXMLDOMNode		* pXDNRoot;
	IXMLDOMNode		* pXDNPolicy;
	IXMLDOMNode		* pXDNWeeklist;
	IXMLDOMNode		* pXDNWeekday;
	IXMLDOMElement	* pXDNHourSetting;
    IXMLDOMProcessingInstruction * pIXMLDOMProcessingInstruction;
	

	hr = CoCreateInstance(CLSID_DOMDocument, NULL, CLSCTX_INPROC_SERVER, 
		   IID_IXMLDOMDocument, (void**)&pXMLDoc);

	// create processing instruction
	pXMLDoc->createProcessingInstruction (_bstr_t(L"xml"), _bstr_t(L"version='1.0'"),
		&pIXMLDOMProcessingInstruction);
	pXMLDoc->appendChild ( pIXMLDOMProcessingInstruction, &pXDN0);
	pXDN0->Release();
	pIXMLDOMProcessingInstruction->Release();

	// create one root node
	hr = pXMLDoc->createNode(_variant_t(NODE_ELEMENT), _bstr_t(L"Network_Throttling"), _bstr_t(L""), &pXDN);
	hr = pXMLDoc->appendChild (pXDN, &pXDNRoot);
	hr = pXDN->Release();

	hr = pXMLDoc->createNode(_variant_t(NODE_ELEMENT), _bstr_t(L"Policy"), _bstr_t(L""), &pXDN);
	hr = pXDNRoot->appendChild (pXDN, &pXDNPolicy);
	hr = pXDN->Release();
	hr = pXDNRoot->Release(); // release root
	
	// create config node for policy
	hr = pXMLDoc->createNode(_variant_t(NODE_ELEMENT), _bstr_t(L"config"), _bstr_t(L""), &pXDN);
	hr = pXDNPolicy->appendChild (pXDN, (IXMLDOMNode**)&pXDN0);
	hr = pXDN->Release();
	pXDN0->QueryInterface (IID_IXMLDOMElement, (void **)&pXElem);
	pXDN0->Release();
	wsprintf(buff, L"RPSReplication_%s.exe", policy->guid );
	hr = pXElem->setAttribute(_bstr_t(L"process_name"), _variant_t(buff));
	hr = pXElem->setAttribute(_bstr_t(L"bandwidth_unit"), _variant_t(L"KB"));
	hr = pXElem->Release(); // release config

	// creae week_list node for policy
	hr = pXMLDoc->createNode(_variant_t(NODE_ELEMENT), _bstr_t(L"week_list"), _bstr_t(L""), &pXDN);
	hr = pXDNPolicy->appendChild (pXDN, &pXDNWeeklist);
	hr = pXDN->Release();
	hr = pXDNPolicy->Release() ; // realease policy

	// create weekdays for week_list
	for (  i=0 ; i< policy->list_len ; i++)
	{
		hr = pXMLDoc->createNode(_variant_t(NODE_ELEMENT), _bstr_t(policy->plist[i].weekday), _bstr_t(L""), &pXDN);
		hr = pXDNWeeklist->appendChild (pXDN, &pXDNWeekday);
		hr = pXDN->Release();
		for ( j=0; j<policy->plist[i].list_len; j++)
		{
			hr = pXMLDoc->createNode(_variant_t(NODE_ELEMENT), _bstr_t(L"bandwidth") , _bstr_t(L""), &pXDN);
			hr = pXDNWeekday->appendChild (pXDN,  (IXMLDOMNode**)&pXDN0);
			hr = pXDN->Release();
			pXDN0->QueryInterface (IID_IXMLDOMElement, (void **)&pXDNHourSetting);
			pXDN0->Release();
			hr = pXDNHourSetting->setAttribute(_bstr_t(L"hour"), _variant_t(policy->plist[i].plist[j].hour));
			hr = pXDNHourSetting->setAttribute(_bstr_t(L"val"), _variant_t(policy->plist[i].plist[j].throttling));
			hr = pXDNHourSetting->Release();
		}
		// day config complete, release it
		pXDNWeekday->Release();
	}

	// we can release weeklist now
	pXDNWeeklist->Release();

	BSTR bstr ;
	hr = pXMLDoc->get_xml( &bstr);
	wchar_t * xml = _wcsdup ( _bstr_t(bstr, false) );

	pXMLDoc->Release();

	return xml;

}

#define RATIO_KBPS (1024.0f/60.0f)

static int _LoadRPSPolicySetting ( wchar_t * guid, POLICY_SETTING * pPolicy )
{
	WCHAR           policy_file [256] ; //= {L"d:\\RPSPolicy_a609f6ba-b27a-4bc9-bce5-f232f08b2602.xml"};
	WCHAR *         weekdays[] = { L"Monday", L"Tuesday", L"Wednesday", 
									  L"Thursday", L"Friday", L"Saturday", L"Sunday"};
	int             i, j, k;
	VARIANT         var0, var1, var2, var3;
	VARIANT_BOOL    var_bool;
	_variant_t		var;

	HRESULT hr;
	IXMLDOMDocument * pXMLDoc;
	IXMLDOMNode		* pXDN1;
	IXMLDOMNode		* pXDN2;
	IXMLDOMNode		* pXDN3;
	IXMLDOMElement	* pXElem;
	IXMLDOMElement	* pXElemThrottling;
	IXMLDOMElement	* pXElemHourSetting;

	// create the full path of policy file
	appendPath(policy_file, _countof(policy_file), g_RPSHome, L"Configuration\\Policy");
	wsprintf( policy_file, L"%s\\RPSPolicy_%s.xml", policy_file, guid);

	hr = CoCreateInstance(CLSID_DOMDocument, NULL, CLSCTX_INPROC_SERVER, 
		   IID_IXMLDOMDocument, (void**)&pXMLDoc);

	hr = pXMLDoc->load (_variant_t(policy_file), &var_bool);
	
	if ( !var_bool ) return -1;

	hr = pXMLDoc->selectSingleNode ( _bstr_t( L"//rpsPolicy/rpsSettings/rpsReplicationSettings/rpsBandWidthThrottlingSettings"), &pXDN1);
	if (!SUCCEEDED (hr)) // RPS policy doesn't have throttling settings
	{
		pXMLDoc->Release();
		return -1;
	}

	pXDN1->QueryInterface(IID_IXMLDOMElement, (void**)&pXElemThrottling);
	hr = pXElemThrottling->getAttribute (_bstr_t(L"enableAdvancedSetting"), &var0);
	pXDN1->Release();

	VarBoolFromStr ( var0.bstrVal, 0, 0, &var_bool);
	VariantClear(&var0);

	// THis is a simple throttling setting, there is no detailed throttling setting
	if (!var_bool )
	{
		long limit =0;
		
		pXElemThrottling->getAttribute (_bstr_t(L"simpleThrottling"), &var0);
		VarI4FromStr(var0.bstrVal, 0, 0, &limit);
		VariantClear(&var0);

		// There is no limitation!
		if ( limit == 0){
			pXMLDoc->Release();
			pXElemThrottling->Release();
			return 0;
		}

		limit = limit * RATIO_KBPS ;// GUI side sets this value as MB/MIN, need to convert to Kbps


		pPolicy->plist = (WEEKDAY_SETTING *) calloc (7, sizeof (WEEKDAY_SETTING));
		pPolicy->list_len = 7;

		for ( i=0; i<7; i++)
		{
			pPolicy->plist[i].plist    = (HOUR_SETTING*) calloc (24, sizeof(HOUR_SETTING));
			pPolicy->plist[i].list_len = 24;
			pPolicy->plist[i].weekday  = _wcsdup(weekdays[i]) ;
			for ( j=0; j<24; j++)
			{
				pPolicy->plist[i].plist[j].hour= j;
				pPolicy->plist[i].plist[j].throttling = limit;
			}
		}

		pXMLDoc->Release();
		pXElemThrottling->Release();
		return 0;
	}

	// Ensure it is a valide setting
	pXElemThrottling->hasChildNodes(&var_bool);
	if (!var_bool )// This is an empty element
	{
		pXMLDoc->Release();
		pXElemThrottling->Release();
		return -1;
	}

	i  = 0;
	j  = 0; // as index into pPolicy->plist
	IXMLDOMNodeList * pWeekdayList = NULL;
	IXMLDOMNodeList * pItemList    = NULL;

	pPolicy->plist = (WEEKDAY_SETTING *) calloc (7, sizeof (WEEKDAY_SETTING));
	pPolicy->list_len = 0;

	hr = pXElemThrottling->get_childNodes (&pWeekdayList);
	hr = pWeekdayList->nextNode(&pXDN1 ); // pXDN1 now point to a weekday setting
	while ( pXDN1 && i<7 )
	{
		hr = pXDN1->hasChildNodes( &var_bool); // the week day has setting items
		if ( var_bool )
		{
			pPolicy->plist[j].list_len = 0;
			pPolicy->plist[j].plist    = (HOUR_SETTING*) calloc (24, sizeof(HOUR_SETTING));
			// in RPS policy, there is no weekday name!
			// Make a duplication, don't forget to free it
			pPolicy->plist[j].weekday  = _wcsdup(weekdays[i]) ;


			hr = pXDN1->get_childNodes(&pItemList);
			hr = pItemList->nextNode (&pXDN2 ); // this is one setting item

			while ( pXDN2 )
			{
				long s, e, v;
				hr = pXDN2->QueryInterface(IID_IXMLDOMElement, (void**)&pXElemHourSetting);
				
				hr = pXElemHourSetting->selectSingleNode (_bstr_t(L"./startDayTime"), &pXDN3);
				hr = pXDN3->QueryInterface(IID_IXMLDOMElement, (void**)&pXElem);
				hr = pXDN3->Release();
				hr = pXElem->getAttribute(_bstr_t(L"hour"), &var1); // the start hour
				hr = pXElem->Release();
				hr = VarI4FromStr (var1.bstrVal, 0, 0, &s);

				hr = pXElemHourSetting->selectSingleNode (_bstr_t(L"./endDayTime"), &pXDN3);
				hr = pXDN3->QueryInterface(IID_IXMLDOMElement, (void**)&pXElem);
				hr = pXDN3->Release();
				hr = pXElem->getAttribute(_bstr_t(L"hour"), &var2); // the start hour
				hr = pXElem->Release();
				hr = VarI4FromStr (var2.bstrVal, 0, 0, &e);

				hr = pXElemHourSetting->getAttribute (_bstr_t(L"throttlingValue"),&var3);
				pXElemHourSetting->Release();
				hr = VarI4FromStr (var3.bstrVal, 0, 0, &v);
				
				v = v * RATIO_KBPS ;// GUI side sets this value as MB/MIN, need to convert to Kbps

				k = pPolicy->plist[j].list_len ; // last position in the list
				for ( ; s < e; s++,k++)
				{
					pPolicy->plist[j].plist[k].hour= s ;
					pPolicy->plist[j].plist[k].throttling = v;
				}
				pPolicy->plist[j].list_len = k;

				VariantClear(&var1);
				VariantClear(&var2);
				VariantClear(&var3);

				hr = pXDN2->Release();
				hr = pItemList->nextNode (&pXDN2 );
			}

			// this weekday has settings
			j++;
			pPolicy->list_len ++;

		} 
		
		i++;
		
		pXDN1->Release ();
		pWeekdayList->nextNode(&pXDN1 );
	}

	pXElemThrottling->Release ();
	return 0;


}

void _FreePolicy( POLICY_SETTING * pPolicy )
{
	int i;

	if ( pPolicy->guid)
		free ( pPolicy->guid);

	if ( pPolicy->plist )
	{
		for ( i=0; i< pPolicy->list_len; i++)
		{
			if( pPolicy->plist[i].plist )
				free (pPolicy->plist[i].plist);

			if (pPolicy->plist[i].weekday )
				free (pPolicy->plist[i].weekday);
		}

		free (pPolicy->plist );

	}
}


static int _InitThrottlingService ( CDbgLog* plogger)
{
	wstring user, pwd;
	bool    bret;
	BOOL    bRet;
	DWORD   dwRet;
	wchar_t dllpath [512];

	dwRet = RPSGetInstallPath(g_RPSHome, _countof(g_RPSHome));

	if ( dwRet == 0)
	{
		appendPath(dllpath, _countof(dllpath), g_RPSHome, L"BIN\\RPScomm");
		bRet = SetDllDirectory (dllpath );
	}

	HMODULE hMod = LoadLibrary(L"network_throttling");
	if ( hMod == NULL) return -1;


	fn_init					= (FN_init)GetProcAddress(hMod, "init");
	fn_uninit				= (FN_uninit)GetProcAddress(hMod, "uninit");
	fn_update_policy		= (FN_update_policy)GetProcAddress(hMod, "update_policy");
	fn_remove_policy		= (FN_remove_policy)GetProcAddress(hMod, "remove_policy");
	fn_clean_policy			= (FN_clean_policy)GetProcAddress(hMod, "clean_policy");
	fn_set_permission_info  = (FN_set_permission_info)GetProcAddress(hMod, "set_permission_info");


	if ( fn_init ) 
		bret = fn_init(L"");

	if ( !bret ) return -1;
	InitializeCriticalSection (&_CriticalSection_ );

	// set credential info
	dwRet = RPSGetAdminAccount(user, pwd);
	if (fn_set_permission_info) 
		bret = fn_set_permission_info(NULL,user.c_str(), pwd.c_str());
 
	// indicate initializaion ok
	g_bInitialized = TRUE;

	return 0;

}

static int _UninitThrottlingService ( CDbgLog* plogger)
{
	if ( g_bInitialized) fn_uninit();
	
	DeleteCriticalSection (&_CriticalSection_);

	return 0;
}

static int _CreateThrotlling ( wchar_t * guid, CDbgLog* plogger )
{
	CDSLock lock (&_CriticalSection_);
	CoInitialize(NULL);

	BOOL    bRet;
	wchar_t _src_file_ [256];
	wchar_t _des_file_ [256];
	POLICY_SETTING policy = {0};

	// copy the RPSReplication.exe
	appendPath(_src_file_, _countof(_src_file_), g_RPSHome, L"bin\\RPSReplication.exe");
	appendPath(_des_file_, _countof(_des_file_), g_RPSHome, L"bin");
	wsprintf(_des_file_, L"%s\\RPSReplication_%s.exe", _des_file_, guid );
	bRet = CopyFile (_src_file_, _des_file_, FALSE);

	// load throttling settings from RPS policy
	policy.guid = _wcsdup(guid);
	int iret = _LoadRPSPolicySetting ( guid, &policy );

	// creae the policy xml for QoS
	wchar_t * xml = _CreateQosPolicyXML ( &policy);

	bool bret = fn_update_policy ( xml );

	_FreePolicy ( &policy );
	free (xml);
	CoUninitialize();
	return 0;

}

static int _DeleteThrotlling ( wchar_t * guid, CDbgLog* plogger )
{
	CDSLock lock (&_CriticalSection_);
	CoInitialize(NULL);

	BOOL bRet;
	wchar_t _des_file_ [256];
	POLICY_SETTING policy = {0};
	// delete the copy of the RPSReplication.exe
	appendPath(_des_file_, _countof(_des_file_), g_RPSHome, L"bin");
	wsprintf(_des_file_, L"%s\\RPSReplication_%s.exe", _des_file_, guid );
	bRet = DeleteFile (_des_file_);

	policy.guid = _wcsdup(guid);
	wchar_t * xml = _CreateQosPolicyXML ( &policy);
	bool bret = fn_remove_policy ( xml );
	
	_FreePolicy ( &policy );
	free (xml);

	CoUninitialize();
	return 0;
}

static int _UpdateThrotlling ( wchar_t * guid, CDbgLog* plogger )
{
	CDSLock lock (&_CriticalSection_);

	BOOL bRet;
	bool b;
	wchar_t _src_file_ [256];
	wchar_t _des_file_ [256];
	POLICY_SETTING policy = {0};
	
	CoInitialize(NULL);

	appendPath(_src_file_, _countof(_src_file_), g_RPSHome, L"bin\\RPSReplication.exe");
	appendPath(_des_file_, _countof(_des_file_), g_RPSHome, L"bin");
	wsprintf(_des_file_, L"%s\\RPSReplication_%s.exe", _des_file_, guid );

	// load throttling settings from RPS policy
	policy.guid = _wcsdup(guid);
	int iret = _LoadRPSPolicySetting ( guid, &policy );

	// creae the policy xml for QoS
	wchar_t * xml = _CreateQosPolicyXML ( &policy);

	// call the communication API
	if ( policy.list_len == 0){ // RPS policy has no throttling setting
		bRet = DeleteFile (_des_file_);
		if ( !bRet ){
			plogger->LogW(LL_INF, 0, L"_UpdateThrotlling: delete file failed , file=%s.", _des_file_ );
		}

		b = fn_remove_policy(xml);
	}else{
		bRet = CopyFile (_src_file_, _des_file_, FALSE);
		if ( !bRet ){
			plogger->LogW(LL_INF, 0, L"_UpdateThrotlling: copy file failed , file=%s.", _des_file_ );
		}
		b = fn_update_policy ( xml );
	}

	_FreePolicy ( &policy );
	free (xml);

	CoUninitialize();

	return 0;
}

static int _OperateNetworkThrottling (wchar_t* operation, wchar_t * guid )
{
	// use the common logger
	CDbgLog logger(L"NativeFacade");

	logger.LogW(LL_INF, 0, L"%S enter, %s, %s...",__FUNCTION__, operation, guid );

	if ( wcscmp (operation, L"initialize") !=0 && !g_bInitialized ){
		return -1; // Not initialized successfully, can't do other things then initialization
	}

	if ( wcscmp (operation, L"initialize") ==0){
		return _InitThrottlingService ( &logger);
	}else if ( wcscmp (operation, L"uninitialize")==0){
		return _UninitThrottlingService(&logger);
	}else if ( wcscmp (operation, L"create")==0){
		return _CreateThrotlling (guid, &logger);
	}else if (wcscmp( operation, L"delete") == 0){
		return _DeleteThrotlling (guid, &logger);
	}else if ( wcscmp (operation, L"update")==0){
		return _UpdateThrotlling (guid, &logger);
	}else {
		return -1;
	}
}

// This is for UT
__declspec (dllexport) int OperateNetworkThrottling ( wchar_t* operation,  wchar_t * guid )
{
	return _OperateNetworkThrottling (operation, guid);
}
JNIEXPORT jlong JNICALL Java_com_ca_arcflash_rps_jni_RPSWSJNI_OperateNetworkThrottling
	(JNIEnv * env, jclass clz, jstring operation, jstring policyName, jstring policyGuid)
{
	wstring op = JStringToWString(env, operation); 
	wstring nm = JStringToWString(env, policyName); 
	wstring id = JStringToWString(env, policyGuid);

	return _OperateNetworkThrottling ((wchar_t*)op.c_str(), (wchar_t*)id.c_str());

}

