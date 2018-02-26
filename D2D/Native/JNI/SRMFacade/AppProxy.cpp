#include "stdafx.h"
#include "AppProxy.h"
#include "SPLogger.h"
#include "SRMAgent.h"
#include <cwctype>
#include <string>
#include <AFCoreAPIInterface.h>
#include <Windows.h>
#import "msxml6.dll"


const wstring AFCOREINTERFACE = L"AFCoreInterface.dll";
const wstring XML_NAMESPACE = L"AgentInfoProbe";
AppProxy* AppProxy::m_pSelf = NULL;

typedef DWORD (*FPAFReadAdminAccount)(std::wstring&, std::wstring&);


AppProxy* AppProxy::GetInstance()
{
    static AppProxy appProxy;
    return (m_pSelf = &appProxy);
}

AppProxy::AppProxy()
{
    // Exchange proxy modules
    m_vModuleFiles.push_back(L"Exchange2003Proxy.dll");
    m_vModuleFiles.push_back(L"Exchange2007LProxy.exe");

    // SQL Server proxy module
    m_vModuleFiles.push_back(L"SQLServerProxy.dll");
}

AppProxy::~AppProxy()
{}

void AppProxy::setCredentialInfo(const wstring& user, const wstring& password)
{
    wcsncpy_s(m_credential.password, _countof(m_credential.password), 
        password.c_str(), _TRUNCATE);
    size_t pos = -1;
    if ( (pos = user.find(L'\\')) > 0) { // user name with domain info
        wcsncpy_s(m_credential.domain, _countof(m_credential.domain),
            user.substr(0, pos -1).c_str(), _TRUNCATE);
    }

    wcsncpy_s(m_credential.userName, _countof(m_credential.userName),
        user.substr(pos+1, user.length() - pos -1).c_str(), _TRUNCATE);
}


void AppProxy::UpdateAppTrendingInfo(wstring& srcXML)
{
    vector<Application_Info> vAIarray;
    vector<wstring>::iterator it = m_vModuleFiles.begin();
    for (; it != m_vModuleFiles.end(); it++) {

        HKEY hKey = NULL;

        if(wcsicmp(it->c_str(), L"Exchange2003Proxy.dll") == 0 && !RegOpenKeyExW(HKEY_LOCAL_MACHINE, 
            L"SOFTWARE\\Microsoft\\Exchange\\Setup", 0, KEY_READ, &hKey) 
            == ERROR_SUCCESS) {
                continue;
        } else
            if( wcsicmp(it->c_str(), L"Exchange2007LProxy.exe") == 0 && !((RegOpenKeyExW(HKEY_LOCAL_MACHINE, 
                L"SOFTWARE\\Microsoft\\Exchange\\v8.0\\Setup", 0, KEY_READ, &hKey) 
                == ERROR_SUCCESS) || (RegOpenKeyExW(HKEY_LOCAL_MACHINE, 
                L"SOFTWARE\\Microsoft\\ExchangeServer\\v14", 0, KEY_READ, &hKey) 
                == ERROR_SUCCESS))){
                    DWORD errc = GetLastError();
                    continue;
            }
            CloseHandle(hKey);
            
            if (wcsicmp(it->c_str(), L"Exchange2007LProxy.exe") == 0) {
                CollectAppInfoByExe(*it, vAIarray);
            } else {
                CollectAppInfo(*it, vAIarray);
            }
    }

    CoInitializeEx(NULL, COINIT_MULTITHREADED);
    GenerateXML(srcXML, vAIarray);
    CoUninitialize();
}

BOOL AppProxy::LoadFunc(HMODULE& hMod, const wstring& fileName, PF_CollectAppInfo* ppFunc)
{
    hMod = LoadLibraryEx(fileName.c_str(), NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
    if (hMod == NULL) {
        DWORD errcode = GetLastError();
        LOG_INFOW(L"[LoadFuncFromDLL] Load DLL failed, DLL Name: %s, EC: %d", 
            fileName.c_str(), errcode);
        return FALSE;
    } else {
        *ppFunc = (PF_CollectAppInfo)GetProcAddress(hMod, "CollectAppInfo");
        if(*ppFunc == NULL) {
            LOG_INFO("[LoadFuncFromDLL] Get PF_CollectAppInfo from %s failed, EC: %d", 
                fileName.c_str(), GetLastError());
        }
        return (*ppFunc != NULL);
    }
}

BOOL AppProxy::CollectAppInfo(const wstring& moduleName, vector<Application_Info>& vAIarray)
{
    DWORD dwResult                     = EDGE_FAILED;
    HMODULE hMod                       = NULL;
    PF_CollectAppInfo pfCollectAppInfo = NULL;

    if (LoadFunc(hMod, moduleName, &pfCollectAppInfo)) {
        int appCount = 1;

        __try{

            PApplication_Info pAI = new Application_Info[1];
            memset(pAI, 0, sizeof(Application_Info));
            DWORD dwResult = pfCollectAppInfo(&m_credential, 
                sizeof(m_credential)/sizeof(Application_CredentialInfo),
                pAI, &appCount);

            if (dwResult == EDGE_SUCCEED) {
                vAIarray.push_back(pAI[0]);
            } else if (dwResult == EDGE_NO_ENOUGH_BUFFER ) {
                delete [] pAI;
                pAI = new Application_Info[appCount];
                if (pAI != NULL) {
                    dwResult = pfCollectAppInfo(&m_credential, 
                        sizeof(m_credential)/sizeof(Application_CredentialInfo),
                        pAI, &appCount);
                    if (dwResult == EDGE_SUCCEED) {
                        for (int i=0; i<appCount; i++) {
                            vAIarray.push_back(pAI[i]);
                        }
                    }
                }

            }
            delete [] pAI;

        } __finally{
            // nothing to do
        }
    }

    if (hMod != NULL) {
        FreeLibrary(hMod);
    }

    return (dwResult == EDGE_SUCCEED);
}

BOOL AppProxy::CollectAppInfoByExe(const wstring& moduleName, vector<Application_Info>& vAIarray) 
{
    // step1, retreive D2D user account info
    HMODULE hMod = LoadLibraryEx(AFCOREINTERFACE.c_str(), NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
    if (hMod == NULL) {
        LOG_INFOW(L"Load %s failed.", AFCOREINTERFACE.c_str());
        return FALSE;
    }

    FPAFReadAdminAccount fpAFReadAdminAccount = (FPAFReadAdminAccount)GetProcAddress(hMod, "AFReadAdminAccount");
    if (fpAFReadAdminAccount == NULL) {
        LOG_INFOW(L"Load function AFReadAdminAccount() failed");
        FreeLibrary(hMod);
        return FALSE;
    }


    std::wstring szAdminUser;
    std::wstring szAdminPwd;
    std::wstring szDomain;
    DWORD retval = fpAFReadAdminAccount(szAdminUser, szAdminPwd);
    if (retval != 0) {
        LOG_INFOW(L"Grant windows logon user credential information failed. EC=(%d)", GetLastError());
        FreeLibrary(hMod);
        return FALSE;
    }

    FreeLibrary(hMod);

    
    size_t pos = szAdminUser.find(L'\\');
    if (pos != string::npos) {
        szDomain.assign(szAdminUser.substr(0, pos));
        szAdminUser.assign(szAdminUser.substr(pos+1, szAdminUser.length()-pos-1));
    } else {
        szDomain.assign(L".");
    }


    // step2, login as D2D user and create a process under the user session
    HANDLE    hToken = NULL;
    if (!LogonUserW(szAdminUser.c_str(), szDomain.c_str(), szAdminPwd.c_str(), 
        LOGON32_LOGON_BATCH, 
        LOGON32_PROVIDER_DEFAULT, &hToken)) {
            LOG_INFOW(L"Log on as user %s failed, EC=(%d)", szAdminUser.c_str(), GetLastError());
            return FALSE;
    }


    SECURITY_DESCRIPTOR    sd;
    InitializeSecurityDescriptor(&sd, SECURITY_DESCRIPTOR_REVISION);
    SetSecurityDescriptorDacl(&sd, TRUE, (PACL)NULL, FALSE);

    SECURITY_ATTRIBUTES sa;
    sa.nLength = sizeof(SECURITY_ATTRIBUTES);
    sa.bInheritHandle = FALSE;
    sa.lpSecurityDescriptor = &sd;

    HANDLE hDupToken = NULL;
    if (!DuplicateTokenEx(hToken, GENERIC_ALL, &sa, 
            SecurityImpersonation, TokenPrimary, &hDupToken)) {
        LOG_INFOW(L"Duplicate token failed, EC=(%d)", GetLastError());
        CloseHandle(hToken);
        return FALSE;
    }

    CloseHandle(hToken);

    // allocate share memory for created process
    HANDLE hMapFile;
    hMapFile = CreateFileMapping(
        INVALID_HANDLE_VALUE,    // use paging file
        &sa,                    // default security 
        PAGE_READWRITE,          // read/write access
        0,                       // maximum object size (high-order DWORD) 
        DEF_SHARE_MEMORY_FOR_EXCHANGE_SIZE,// maximum object size (low-order DWORD)  
        NAME_SHARE_MEMORY_FOR_EXCHANGE);   // name of mapping object

    if (hMapFile == NULL) 
    { 
        DWORD errorCode = GetLastError();
        LOG_INFOW(L"Could not create file mapping object (%d).", errorCode);
        return FALSE;
    }


    STARTUPINFO si;
    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
	si.dwFlags = STARTF_USESHOWWINDOW;
	si.wShowWindow = SW_HIDE;

    PROCESS_INFORMATION pi;
    ZeroMemory( &pi, sizeof(pi) );

    wstring commandLine(moduleName);
    // Start the child process. 
    if( !CreateProcessAsUser( 
        hDupToken,
        NULL,   // No module name (use command line). 
        const_cast<wchar_t*>(commandLine.c_str()), // Command line. 
        NULL,             // Process handle not inheritable. 
        NULL,             // Thread handle not inheritable. 
        FALSE,            // Set handle inheritance to FALSE. 
        NULL,             // No creation flags. 
        NULL,             // Use parent's environment block. 
        NULL,             // Use parent's starting directory. 
        &si,              // Pointer to STARTUPINFO structure.
        &pi )             // Pointer to PROCESS_INFORMATION structure.
        ) {

            LOG_INFOW(L"Start %s Process failed (%d).", commandLine.c_str(), GetLastError());

            CloseHandle(hDupToken);
            return FALSE;

    } else {
        CloseHandle(hDupToken);

        // step3, wait the process end and populate the value
        LOG_INFOW(L"Start %s Process succeed.", commandLine.c_str());
        if (WaitForSingleObject(pi.hProcess, COLLECT_APP_INFO_TIME_OUT) != WAIT_OBJECT_0) {
            LOG_INFOW(L"Collect Exchange data size time out.");
            CloseHandle(hMapFile);
            return FALSE;
        }
        DWORD exitCode = 0;
        GetExitCodeProcess(pi.hProcess, &exitCode);
        LOG_INFOW(L"%s Process exit, return code=%d.", commandLine.c_str(), exitCode);

        
        CloseHandle( pi.hThread );
        CloseHandle( pi.hProcess );

        BYTE* pBuf = (BYTE*)MapViewOfFile(hMapFile,   // handle to map object
            FILE_MAP_ALL_ACCESS, // read/write permission
            0,                   
            0,                   
            DEF_SHARE_MEMORY_FOR_EXCHANGE_SIZE);           

        if (pBuf == NULL) 
        { 
            DWORD errorCode = GetLastError();
            LOG_INFOW(L"Could not map view of file (%d).", errorCode); 
            CloseHandle(hMapFile);
            return FALSE;
        }

        DWORD dwAiCount = 0;
        memcpy(&dwAiCount, pBuf, sizeof(dwAiCount));
        for (int i=0; i<(int)dwAiCount; i++) {

            Application_Info ai;
            memset(&ai, 0, sizeof(ai));
            memcpy(&ai, (pBuf + sizeof(dwAiCount) + i*sizeof(ai)), sizeof(ai));
            ai.size = ai.size >> 10; // change KB to MB
            vAIarray.push_back(ai);
   
        }

        // step4, release resource
        UnmapViewOfFile(pBuf);
        CloseHandle(hMapFile);
        return TRUE;
    }
}

void AppProxy::GenerateXML(wstring& srcXML, vector<Application_Info>& vAIarray)
{
    try {
        HRESULT hr = S_FALSE;
        MSXML2::IXMLDOMDocumentPtr doc;
        hr = doc.CreateInstance(__uuidof(MSXML2::DOMDocument));
        if ( FAILED(hr) ) {
            return ;
        }

        VARIANT_BOOL flag = doc->loadXML(_bstr_t(srcXML.c_str()));
        if (flag == VARIANT_FALSE) {
            return ;
        }
        
        MSXML2::IXMLDOMElementPtr pRoot = NULL;
        hr = doc->get_documentElement(&pRoot);
        if (FAILED(hr)) {
            return ;
        }

        // create node for each APP
        vector<Application_Info>::iterator it;
        for (it = vAIarray.begin(); it != vAIarray.end(); it++) {
            MSXML2::IXMLDOMNodePtr pNode = doc->createNode(NODE_ELEMENT, 
                _bstr_t(L"AppDataInfo"), _bstr_t(XML_NAMESPACE.c_str()));
            if (NULL == pNode) {
                continue;
            }

            // create child nodes
            if (it->dwCategoryID != 0) { // skip invalid instance info
                MSXML2::IXMLDOMNodePtr pNodeCatalogID = doc->createNode(NODE_ELEMENT, 
                    _bstr_t(L"CategoryID"), _bstr_t(XML_NAMESPACE.c_str()));
                if (NULL == pNodeCatalogID) {
                    continue;
                }
                wchar_t catalogIDStr[64] = {0};
                _itow_s((int)it->dwCategoryID, catalogIDStr, _countof(catalogIDStr), 10);
                pNodeCatalogID->put_text(_bstr_t(catalogIDStr));


                MSXML2::IXMLDOMNodePtr pNodeMaj = doc->createNode(NODE_ELEMENT, 
                    _bstr_t(L"MajorVersion"), _bstr_t(XML_NAMESPACE.c_str()));
                if (NULL == pNodeMaj) {
                    continue;
                }
                wchar_t versionStr[8] = {0};
                _itow_s((int)it->majorVersion, versionStr, _countof(versionStr), 10);
                pNodeMaj->put_text(_bstr_t(versionStr));


                MSXML2::IXMLDOMNodePtr pNodeMin = doc->createNode(NODE_ELEMENT, 
                    _bstr_t(L"MinorVersion"), _bstr_t(XML_NAMESPACE.c_str()));
                if (NULL == pNodeMin) {
                    continue;
                }
                _itow_s((int)it->minorVersion, versionStr, _countof(versionStr), 10);
                pNodeMin->put_text(_bstr_t(versionStr));


                MSXML2::IXMLDOMNodePtr pNodeInst = doc->createNode(NODE_ELEMENT, 
                    _bstr_t(L"InstanceName"), _bstr_t(XML_NAMESPACE.c_str()));
                if (NULL == pNodeInst || 
                    FAILED(pNodeInst->put_text(it->instanceName))) {
                    continue;
                }


                MSXML2::IXMLDOMNodePtr pNodeSize = doc->createNode(NODE_ELEMENT, 
                    _bstr_t(L"AppDataSizeMB"), _bstr_t(XML_NAMESPACE.c_str()));
                if (NULL == pNodeSize) {
                    continue;
                }
                wchar_t sizeStr[64] = {0};
                __int64 sizeMB = it->size >> 10; // KB to MB
                _i64tow_s(sizeMB, sizeStr, _countof(sizeStr), 10);
                pNodeSize->put_text(sizeStr);


                pNode->appendChild(pNodeCatalogID);
                pNode->appendChild(pNodeMaj);
                pNode->appendChild(pNodeMin);
                pNode->appendChild(pNodeInst);
                pNode->appendChild(pNodeSize);
                pRoot->appendChild(pNode);
            }
        }

        //wchar_t buffer[SRMAgent::c_default_xml_size] = {0};
        //wcsncpy_s(buffer, _countof(buffer), doc->Getxml(), _TRUNCATE);
        srcXML.assign(doc->Getxml());
        //return buffer;
    } catch(...) {
        LOG_INFO("[AppProxy::GenerateXML] exception happened, EC: %d", GetLastError());
        //return srcXML;
    }
}