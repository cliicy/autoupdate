#include "./../stdafx.h"
#include "TaskLicense.h"
#include "d2dproxyiDef.h"
#include "D2DIPCItem.h"
#include "Basecodec.h"
#include "./../JNIConv.h"
#include "chklicFuncSignature.h"
#include "D2DProxyImpl.h"

extern CDbgLog logObj;

#define SPAN_VALUE_EDGE_D2D 13

CTaskLicense::CTaskLicense(void)
{
	memset(&m_licEntry, 0, sizeof(D2D_CHKLIC_LISTENTRY) );
}

CTaskLicense::~CTaskLicense(void)
{
	if(0 < m_licEntry.listsize && m_licEntry.pvItemList)
	{
		CMDcodec::ReleaseLicList(&m_licEntry);
	}

}
CTaskLicense *  CTaskLicense::CreateInstance()
{
	CTaskLicense * pobjTask = new CTaskLicense;
	ITaskItem *pv;
	pobjTask->QueryInterface(__uuidof(ITaskItem), (void**)&pv);

	return pobjTask;
}
 
DWORD CTaskLicense::Init(PD2D_CHKLIC_LISTENTRY pLicEntry)
{
	if(NULL == pLicEntry)
	{
		return E_INVALIDARG;
	}

	memcpy(&m_licEntry, pLicEntry, sizeof(D2D_CHKLIC_LISTENTRY) );
	 
	if(m_licEntry.listsize >0)
	{
		m_licEntry.pvItemList = new D2D_CHKLIC_ITEM[m_licEntry.listsize];

		for(int i=0; i < m_licEntry.listsize; i++)
		{
			m_licEntry.pvItemList[i].CompntID  = pLicEntry->pvItemList[i].CompntID ;
			m_licEntry.pvItemList[i].isRevered = pLicEntry->pvItemList[i].isRevered;
			m_licEntry.pvItemList[i].retVal = pLicEntry->pvItemList[i].retVal;
		}
		 
	}
	  
	return 0;
}

HRESULT CTaskLicense::DoWork()
{
	HRESULT hr=0; 
	
	try
	{
		//hr = CheckLicense(); 
		hr = CheckLicenseEx();
	}
	catch(...)
	{
		logObj.LogW(LL_WAR, 0, L" TaskLicense::DoWork() Meet Exception...");
	}
	 
	return hr;
}

DWORD CTaskLicense::GetCheckReslut(PD2D_CHKLIC_LISTENTRY pLicEntry)
{
	if(NULL == pLicEntry)
	{
		return E_INVALIDARG;
	}

	if(pLicEntry->listsize != m_licEntry.listsize )
	{
		logObj.LogW(LL_WAR, 0, L"CTaskLicense::GetCheckReslut(): different item size for license check...");
		return E_INVALIDARG;
	}

	pLicEntry->flags = m_licEntry.flags;
	pLicEntry->LicID = m_licEntry.LicID;
	pLicEntry->processId = GetCurrentProcessId();

	for(int i=0; i < m_licEntry.listsize; i++)
	{
		pLicEntry->pvItemList[i].CompntID  = m_licEntry.pvItemList[i].CompntID;
		pLicEntry->pvItemList[i].isRevered = m_licEntry.pvItemList[i].isRevered;
		pLicEntry->pvItemList[i].retVal    = m_licEntry.pvItemList[i].retVal;
	}

	return 0;
}

DWORD CTaskLicense::CheckLicense()
{
	if(NULL==m_pJnienv)
	{
		return E_INVALIDARG;
	} 

	D2D_CHKLIC_LIST stchkLic= {0};
	D2D_CHKLIC_ITEM Items[3];

	stchkLic.size = sizeof(D2D_CHKLIC_LIST);
	stchkLic.version	= MAKEWORD(0,17);
	stchkLic.flags		= m_licEntry.flags;
	stchkLic.processXid = m_licEntry.processId;
	m_licEntry.flags	= 0;
	stchkLic.listsize   = m_licEntry.listsize;
	stchkLic.pvItemList = m_licEntry.pvItemList;

	DWORD dwSize = _countof(stchkLic.szComputerName)-1;
 	GetComputerNameEx(ComputerNameDnsHostname, stchkLic.szComputerName,  &dwSize);
	logObj.LogW(LL_INF, 0, L"CTaskLicense::CheckLicense start(%s) from process:%d...", stchkLic.szComputerName, m_licEntry.processId);

	HRESULT hr =0;
	jmethodID jFunID;
	jobject obj_licINFO;
 
	do 
	{   
		BOOL isUnderEdge = IsUnderCPM();
		if(FALSE== isUnderEdge )
		{
			logObj.LogW(LL_INF, 0, L"Doesn't under Edge...");
			for(int i=0; i < stchkLic.listsize; i++)
			{
				stchkLic.pvItemList[i].isRevered = FALSE;
				stchkLic.pvItemList[i].retVal = 0;
			}
		 
			break;
		}

		if( stchkLic.listsize > 0)
		{
			if(COMID_CHK_UNDER_EDGE == stchkLic.pvItemList[0].CompntID )
			{
				logObj.LogW(LL_INF, 0, L"The current D2D is under Edge...");
				stchkLic.pvItemList[0].isRevered = TRUE;
				stchkLic.pvItemList[0].retVal = 0;
				break;
			}

		}

		/////////////////////////////////////////
		logObj.LogW(LL_INF, 0, L"*********************Oolong new License not ready**************** ");
		for(int i=0; i < stchkLic.listsize; i++)
		{
			stchkLic.pvItemList[i].isRevered = 1;
			stchkLic.pvItemList[i].retVal = 0;
		}
		break;
		/////////////////////////////////////////
		
		//May. 30, 2014 will be the hard code license date for Beta <
		time_t      lToday = 0;
		struct tm   finalAlpah = { 0, 0, 0, 31, 4, 114 };      // New one:  31, May, 2014

		time(&lToday);
		time_t lAlpha = mktime( &finalAlpah );

		if( lAlpha < lToday)
		{
			logObj.LogW(LL_WAR, 0, L"***Beta Expired: Will not try to allocate license from CPM***" ) ;
			for(int i=0; i < stchkLic.listsize; i++)
			{
				stchkLic.pvItemList[i].isRevered = FALSE;
				stchkLic.pvItemList[i].retVal = 0;
			}
		 
			break;
			 
		}
		//Feb. 28, 2014 will be the hard code license date for Alpha >
	 
		DWORD dwCurPID = ::GetCurrentProcessId();
		if(dwCurPID == stchkLic.processXid )
		{
			//Set related information to flags; 
			 m_licEntry.flags = m_licEntry.flags | 0x0010;
		}
	  
		jclass _gclsEntry = NULL;
		jclass _gclsItem  = NULL;
		jclass _gclsCheck = NULL;
		////////////////////////////////////////////////////////////////////////// 
		{

			jclass clsItem;
			clsItem = m_pJnienv->FindClass(CLASSNAME_LIC_ITEM );
			if(NULL==clsItem)
			{
				logObj.LogA(LL_INF, 0, "failed to find class %s", CLASSNAME_LIC_ITEM);
				hr = E_FAIL;
				break;
			}
			_gclsItem = (jclass)m_pJnienv->NewGlobalRef((jobject) clsItem);

			{
				jclass clsEntry;
				clsEntry = m_pJnienv->FindClass(CLASSNAME_LIC_LICINFO );
				if(NULL==clsEntry)
				{
					logObj.LogA(LL_INF, 0, "failed to find class %s", CLASSNAME_LIC_LICINFO);
					hr = E_FAIL;
					break;
				}
				_gclsEntry = (jclass)m_pJnienv->NewGlobalRef((jobject) clsEntry);
			}
			{	
				jclass clsCheck;
				clsCheck = m_pJnienv->FindClass(CLASSNAME_CHKLIS );
				if(NULL==clsCheck)
				{
					logObj.LogA(LL_INF, 0, "failed to find class %s", CLASSNAME_CHKLIS);
					hr = E_FAIL;
					break;
				}
				
				_gclsCheck = (jclass)m_pJnienv->NewGlobalRef((jobject) clsCheck);
			}

		} 

		// Find related Java class
		//////////////////////////////////////////////////////////////////////////
		// create list object
		jFunID = NULL;
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsEntry, FUN_LICINFO_CREAET, SIG_LICINFO_CREAET);
		if(NULL==jFunID)
		{
			break;
		}

		obj_licINFO = m_pJnienv->NewObject(_gclsEntry, jFunID);

		if(NULL==obj_licINFO)
		{
			break;
		}
		 
		// set computer name
		{
			jFunID = NULL;
			jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsEntry, FUN_LICINFO_SETHOSTNAME, SIG_LICINFO_SETHOSTNAME);
			if(NULL==jFunID)
			{

				break;
			}

			m_pJnienv->CallObjectMethod(obj_licINFO,jFunID, WCHARToJString(m_pJnienv, stchkLic.szComputerName) );
		}
		 
		//Set process id
		{
			jFunID = NULL;
			jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsEntry, FUN_LICINFO_SETPID,  SIG_LICINFO_SETPID);
			if(NULL==jFunID)
			{

				break;
			}
			DWORD64 PID =  m_licEntry.processId;
			m_pJnienv->CallObjectMethod(obj_licINFO, jFunID, PID);
		}
			
		//Set Flags
		{
			jFunID = NULL;
			jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsEntry, FUN_LICINFO_SETFLAGS, SIG_LICINFO_SETFLAGS);
			if(NULL==jFunID)
			{

				break;
			}

			m_pJnienv->CallObjectMethod(obj_licINFO, jFunID,  m_licEntry.flags);
		}

		//Set Component list
		 
		BOOL isOK = TRUE;
		for (int i=0; i<stchkLic.listsize; i++)
		{
			jFunID = NULL;
			jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsItem, FUN_LICITEM_CREAET, SIG_LICITEM_CREAET);
			if(NULL==jFunID)
			{
				isOK = FALSE;
				break;
			}

			jobject objitem;
			objitem =m_pJnienv->NewObject(_gclsItem, jFunID);

			//set attribute
			jFunID = NULL;
			jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsItem, FUN_LICITEM_SETCOMID, SIG_LICITEM_SETCOMID);
			if(NULL==jFunID)
			{
				isOK = FALSE;
				break;
			}
			{
				jobject jobjError;
				DWORD64 dwcomID = (stchkLic.pvItemList[i].CompntID+ SPAN_VALUE_EDGE_D2D);
				logObj.LogA(LL_INF, 0, "	Set component id(%d)", dwcomID);

				jobjError = m_pJnienv->CallObjectMethod(objitem,jFunID, dwcomID);
			}

			// add it to license check list
			jFunID = NULL;
			jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsEntry, FUN_LICINFO_ADDCOMPONENT, SIG_LICINFO_ADDCOMPONENT);
			if(NULL==jFunID)
			{
				isOK = FALSE;
				break;
			}

			m_pJnienv->CallObjectMethod(obj_licINFO,jFunID, objitem);
		}

		if(FALSE==isOK)
		{
			break;
		}
		 
		//////////////////////////////////////////////////////////////////////////
		jFunID = NULL;
		jFunID=m_pJnienv->GetStaticMethodID(_gclsCheck,  FUN_CHKLIC_CHECK,  SIG_CHKLIC_CHECK);

		jlong jlRet;
		jlRet =(jlong) m_pJnienv->CallStaticLongMethod(_gclsCheck, jFunID,obj_licINFO);	
		if(1 !=jlRet) // 1: SUCCESS
		{
			logObj.LogA(LL_WAR, 0, "invoke %s failed:%d ", FUN_CHKLIC_CHECK, jlRet);
			break;
		}

		//////////////////////////////////////////////////////////////////////////
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsEntry, FUN_LICINFO_GETCOMLIST, SIG_LICINFO_GETCOMLIST);
		if(NULL==jFunID)
		{
			isOK = FALSE;
			break;
		}

		jobject object_file_list =m_pJnienv->CallObjectMethod(obj_licINFO,jFunID);

		jclass class_file_list = m_pJnienv->GetObjectClass(object_file_list);
		jmethodID methodid_list_size = m_pJnienv->GetMethodID(class_file_list,"size","()I");
		jint list_size = m_pJnienv->CallIntMethod(object_file_list,methodid_list_size);

		int nlist_size =list_size;
		logObj.LogW(LL_INF, 0, L"return %d result...", nlist_size);

		jmethodID methodid_list_get = m_pJnienv->GetMethodID(class_file_list,"get","(I)Ljava/lang/Object;");

		for(int index = 0 ; index < nlist_size; index++)
		{

			jobject object_file_item = m_pJnienv->CallObjectMethod(object_file_list,methodid_list_get,index);
			jclass class_file_item = m_pJnienv->GetObjectClass(object_file_item);

			jFunID = m_pJnienv->GetMethodID(class_file_item, FUN_LICITEM_ERSULT , SIG_LICITEM_ERSULT );
			jlong jResult =(jlong) m_pJnienv->CallObjectMethod(object_file_item,jFunID);

			jFunID = m_pJnienv->GetMethodID(class_file_item, FUN_LICITEM_RESERVED , SIG_LICITEM_RESERVED );
			jboolean jReserved =(jboolean) m_pJnienv->CallObjectMethod(object_file_item,jFunID);

			jFunID = m_pJnienv->GetMethodID(class_file_item, FUN_LICITEM_GETCOMID ,	SIG_LICITEM_GETCOMID );
			jlong jComID =(jlong) m_pJnienv->CallObjectMethod(object_file_item,jFunID);

			if(index > ( stchkLic.listsize) )
			{
				break;
			}

			stchkLic.pvItemList[index].retVal =jResult;
			stchkLic.pvItemList[index].isRevered =jReserved;
			stchkLic.pvItemList[index].CompntID =jComID - SPAN_VALUE_EDGE_D2D;
		}
		//////////////////////////////////////////////////////////////////////////
		 
	}while(0);

	logObj.LogW(LL_INF, 0, L"CTaskLicense::CheckLicense end():%d...",hr);

	return hr;
}

DWORD CTaskLicense::CheckLicenseEx()
{
	if(NULL==m_pJnienv)
	{
		return E_INVALIDARG;
	} 
	 
	D2D_CHKLIC_ITEM Items[2];    
	WCHAR szComputerName[256] = {0}; 
	DWORD dwSize = _countof(szComputerName)-1;
 	GetComputerNameEx(ComputerNameDnsHostname, szComputerName,  &dwSize);
	logObj.LogW(LL_INF, 0, L"CheckLicenseEx start(%s) from JID:%d PID:%d MASK:0x%08x...", szComputerName, m_licEntry.JobID, m_licEntry.processId,  m_licEntry.Mask);

	HRESULT hr =0;
	DWORD dwLicID =0;
	do 
	{
		jmethodID jFunID;
		BOOL isUnderEdge = IsUnderCPM();
		if(FALSE== isUnderEdge )
		{
			logObj.LogW(LL_INF, 0, L"Doesn't under CPM...");
			m_licEntry.LicID =0;
			break;
		}
		 
		if(COMID_CHK_UNDER_EDGE == m_licEntry.flags)
		{
			logObj.LogW(LL_INF, 0, L"The current D2D is under CPM...");
			m_licEntry.LicID =1;
			break;
		} 
		/////////////////////////////////////////
		/*
		logObj.LogW(LL_INF, 0, L"*********************Oolong new License not ready**************** ");
		m_licEntry.LicID =1;
		break;
		*/
		/////////////////////////////////////////

		jclass _gclsCheck = NULL;
		jclass _gclsMachine = NULL;
		jclass _gclsComponent =NULL;
		{	
			jclass clsCheck;
			clsCheck = m_pJnienv->FindClass(CLASSNAME_CHKLIS );
			if(NULL==clsCheck)
			{
				logObj.LogA(LL_INF, 0, "failed to find class %s", CLASSNAME_CHKLIS);
				hr = E_FAIL;
				break;
			}

			_gclsCheck = (jclass)m_pJnienv->NewGlobalRef((jobject) clsCheck);
		}

		{	
			jclass clsMachine;
			clsMachine = m_pJnienv->FindClass(CLASSNANME_MACHININFO );
			if(NULL==clsMachine)
			{
				logObj.LogA(LL_INF, 0, "failed to find class %s", CLASSNANME_MACHININFO);
				hr = E_FAIL;
				break;
			}

			_gclsMachine = (jclass)m_pJnienv->NewGlobalRef((jobject) clsMachine);
		}

		{	
			jclass clsCmpnt;
			clsCmpnt = m_pJnienv->FindClass(CLASSNAME_LIC_ITEM );
			if(NULL==clsCmpnt)
			{
				logObj.LogA(LL_INF, 0, "failed to find class %s", CLASSNAME_LIC_ITEM);
				hr = E_FAIL;
				break;
			}

			_gclsComponent = (jclass)m_pJnienv->NewGlobalRef((jobject) clsCmpnt);
		}
			 
		// set machine information
		jFunID = NULL;
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsMachine, FUN_MACHINEINFO_CREAET, SIG_MACHINEINFO_CREAET);
		if(NULL==jFunID)
		{
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_MACHINEINFO_CREAET, CLASSNANME_MACHININFO);
			hr = E_FAIL;
			break;
		} 

		jobject objMachine;
		objMachine =m_pJnienv->NewObject(_gclsMachine, jFunID);
		
		if(NULL ==objMachine)
		{
			logObj.LogA(LL_INF, 0, "failed to create instance of class %s", CLASSNANME_MACHININFO);
			hr = E_FAIL;
			break;
		}

		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsMachine, FUN_MACHINE_SETNAME, SIG_MACHINE_SETNAME);
		if(NULL==jFunID)
		{ 
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_MACHINE_SETNAME, CLASSNANME_MACHININFO);
			hr = E_FAIL;
			break;
		}

		m_pJnienv->CallObjectMethod(objMachine, jFunID, WCHARToJString(m_pJnienv, szComputerName) );

		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsMachine, FUN_MACHINE_SETSOCKETNUM, SIG_MACHINE_SETSOCKETNUM);
		if(NULL==jFunID)
		{ 
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_MACHINE_SETNAME, CLASSNANME_MACHININFO);
			hr = E_FAIL;
			break;
		}

		m_pJnienv->CallObjectMethod(objMachine, jFunID, m_licEntry.SocketNum);

		// set job id
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsMachine, FUN_LICINFO_SETJOBID, SIG_LICINFO_SETJOBID);
		if(NULL==jFunID)
		{ 
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_LICINFO_SETJOBID, CLASSNANME_MACHININFO); 
		}
		else
		{
			m_pJnienv->CallObjectMethod(objMachine, jFunID, (jlong)m_licEntry.JobID);
		}

		// set flags, including job type
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsMachine, FUN_LICINFO_SETJOBTYPE, SIG_LICINFO_SETJOBTYPE);
		if(NULL==jFunID)
		{ 
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_LICINFO_SETJOBTYPE, CLASSNANME_MACHININFO);		 
		}
		else
		{
			m_pJnienv->CallObjectMethod(objMachine, jFunID, (jlong)m_licEntry.flags);
		}
		 
		// check license from CPM 
		jobject objComponent = NULL; 
		jFunID = NULL;
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, _gclsComponent, FUN_LICITEM_CREAET, SIG_LICITEM_CREAET);
		if(NULL==jFunID)
		{
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_MACHINEINFO_CREAET, CLASSNAME_LIC_ITEM);
			hr = E_FAIL;
			break;
		}  

		objComponent =m_pJnienv->NewObject(_gclsComponent, jFunID);
		if(NULL ==objComponent)
		{
			logObj.LogA(LL_INF, 0, "failed to create instance of class %s", CLASSNAME_LIC_ITEM);
			hr = E_FAIL;
			break;
		}

		jFunID = NULL;
		jFunID=m_pJnienv->GetStaticMethodID(_gclsCheck,  FUN_CHKLIC_ALLOCATE,  SIG_CHKLIC_ALLOCATE);

		if(NULL==jFunID)
		{
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_CHKLIC_ALLOCATE, CLASSNAME_CHKLIS);
			hr = E_FAIL;
			break;
		}  

		jlong jlRet =(jlong) m_pJnienv->CallStaticLongMethod(_gclsCheck, jFunID, objMachine, (jlong)m_licEntry.Mask, objComponent );	
		if( (1 !=jlRet) || NULL == objComponent) // 0: SUCCESS
		{
			logObj.LogA(LL_WAR, 0, "invoke %s failed:%d %d", FUN_CHKLIC_ALLOCATE, jlRet, objComponent);
			hr = E_FAIL;
			break;
		}  
		// check result
		jclass jcsCom;
		jcsCom = m_pJnienv->GetObjectClass(objComponent);
		jFunID = m_pJnienv->GetMethodID(jcsCom, FUN_LICITEM_GETCOMID , SIG_LICITEM_GETCOMID );
		if(NULL==jFunID)
		{ 
			logObj.LogA(LL_INF, 0, "failed to find method %s of class %s", FUN_LICITEM_GETCOMID, CLASSNAME_LIC_ITEM);
			hr = E_FAIL;
			break;
		}
		 
		jlong jResult =(jlong) m_pJnienv->CallObjectMethod(objComponent,jFunID);
		dwLicID =jResult;
		 
		m_licEntry.LicID = dwLicID;
		 
	}while(0);

	logObj.LogW(LL_INF, 0, L"CheckLicenseEx end(LICID:%x):0x%08x...", dwLicID, hr);

	return hr;
}

BOOL CTaskLicense::IsUnderCPM()
{
	BOOL isUnder = FALSE;

	HRESULT hr =0;
	jclass jclsckhlic;
	jmethodID jFunID;

	do 
	{
		// find all class
		jclsckhlic = CHelper::FindJavaClass(m_pJnienv, CLASSNAME_CHKLIS);
		if(NULL==jclsckhlic)
		{
			logObj.LogA(LL_INF, 0, "failed to find class %s", CLASSNAME_CHKLIS);
			break;
		}
		 
		jobject jobjchk;

		jFunID = NULL;
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, jclsckhlic, FUN_CHKLIC_CREAET , SIG_CHKLIC_CREAET);

		if(NULL ==jFunID)
		{
			logObj.LogA(LL_INF, 0, "failed to find function %s of class %s", FUN_CHKLIC_CREAET, CLASSNAME_CHKLIS);
			break;
		}
 
		jobjchk = m_pJnienv->NewObject(jclsckhlic, jFunID);
		if(NULL ==jobjchk)
		{
			logObj.LogA(LL_INF, 0, "failed to create instance of class %s", CLASSNAME_CHKLIS);
			break;
		}

		jFunID = NULL;
		jFunID = CHelper::FindMethodInJniClass(m_pJnienv, jclsckhlic,FUN_IS_UNDER_EDGE, SIG_IS_UNDER_EDGE);

		if(NULL==jFunID)
		{
			logObj.LogA(LL_INF, 0, "failed to find function %s of class %s", FUN_IS_UNDER_EDGE, CLASSNAME_CHKLIS);
			break;
		}

		//1 under , 2 doesn't under edge
		jlong jRet = (jlong) m_pJnienv->CallObjectMethod(jobjchk, jFunID);

		switch(jRet)
		{
		case 1:
			{
				isUnder = TRUE;
			}
			break;
		case 2: //doesn't have the related file. we treat it as no managed by CPM
			{
			}
			break;
		case 3: // the related file exists, but can't connect to CPM.
			{
				logObj.LogA(LL_WAR, 0, "the related file exists, but can't connect to CPM...");
				// isUnder = TRUE;
			}
			break;
		case 0xFF: // the UUID is different as current in CPM side.
			{
				logObj.LogA(LL_WAR, 0, "The UUID is different as CPM side, it seems the CPM has been re-installed...");
				isUnder = 0;
			}
			break;
		case 0xFE: // FLAG_RELEASED_BY_EDGE .
			{
				logObj.LogA(LL_WAR, 0, "The Current Node isn't managed by CPM, should delete related identify file...");
				isUnder = 0;
			}
			break;
		default:
			{
				logObj.LogA(LL_WAR, 0, "unrecognized return value(0x%08x)...", jRet);
				isUnder = 0;
			}
		}

		if(1 ==jRet)
		{
			isUnder = TRUE;
		}
		  
	} while(0);

	return isUnder;
}