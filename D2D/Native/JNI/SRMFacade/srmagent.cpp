#include "stdafx.h"
#include "SRMAgent.h"
#include "Help.h"
#include "SPLogger.h"
#include "AppProxy.h"
#include <Windows.h>

extern wstring g_module_dir;
typedef DWORD (WINAPI * PF_GetSysHardwareInformation)(wchar_t*, ULONG *);
typedef DWORD (WINAPI * PF_GetSysSoftwareInformation)(wchar_t*, ULONG *);
typedef BOOL  (WINAPI * PF_GetServerPKI)(wchar_t *, ULONG *, int);

typedef int (* PF_SendSRMValidCommand)(int);
typedef int (* PF_CreateNewAlertConfigureFile)();
typedef int (* PF_SetPkiAlertPolicy)(PKIAlertStruct *, unsigned long);
typedef int (* PF_GetSRMValidation)(SRMValidation *);
typedef int (* PF_SetSRMValidation)(SRMValidation *);

typedef int (WINAPI *PF_GetAlertRecord)(int *alertType, PWCHAR *alertHeader, int alertHeaderSize, 
				   int *threshold, int *curUtil, int recordCount);


const wstring c_agtinfo_module_name  = L"AgtInfo.dll";
const wstring c_srmclient_module_name = L"SRMClient.dll";
const wstring c_arcua_module_name = L"UnivAgent.exe";
const wstring c_agpkimon_module_name = L"AgPkiMon.exe";
const wstring c_agpkialt_module_name = L"AgPkiAlt.dll";


SRMAgent::SRMAgent()
: m_hAgtInfoModule(NULL), m_hSRMClientModule(NULL), m_user(L""), m_password(L"")
{

}

SRMAgent::~SRMAgent()
{
    ReleaseDLL(m_hAgtInfoModule);
	ReleaseDLL(m_hSRMClientModule);
}

bool SRMAgent::LoadFuncFromDLL(HMODULE& hMod, const wstring fileName, const string funcName, void** ppFunc)
{
    if (hMod != NULL) {
        *ppFunc = GetProcAddress(hMod, funcName.c_str());
        if (*ppFunc != NULL) {
            return true;
        } else {
            FreeLibrary(hMod);
        }
    } 
    
    // wstring fullPath = hlpGetModuleFullPath(fileName);
    hMod = LoadLibraryEx(fileName.c_str(), NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
    if (hMod == NULL) {
        DWORD errcode = GetLastError();
        LOG_INFOW(L"[LoadFuncFromDLL] Load DLL failed, DLL Name: %s, EC: %d", fileName.c_str(), errcode);
        return false;
    } else {
        *ppFunc = GetProcAddress(hMod, funcName.c_str());
        if(*ppFunc == NULL) {
            LOG_INFO("[LoadFuncFromDLL] Get funcName: %s, EC: %d", funcName.c_str(), GetLastError());
        }
        return (*ppFunc != NULL);
    }
}

void SRMAgent::ReleaseDLL(HMODULE hMod)
{
    if (hMod != NULL) {
        FreeLibrary(hMod);
    }
}


bool SRMAgent::IsARCInstalled()
{
    // Find the ARCserve UA process and AgPkiMon.exe
    vector<ProcessMapItem> procList;
    ProcessMapItem uapmi;
    memset(&uapmi, 0, sizeof(uapmi));
    uapmi.procName = c_arcua_module_name;
    procList.push_back(uapmi);

    ProcessMapItem agPkiMonPmi;
    memset(&agPkiMonPmi, 0, sizeof(agPkiMonPmi));
    agPkiMonPmi.procName = c_agpkimon_module_name;
    procList.push_back(agPkiMonPmi);


    DWORD returnVal = hlpFindProcesses(procList);
    if (returnVal == 0) {
        return true;
    } else {
        LOG_INFOW(L"[IsARCInstalled] EC: %d", returnVal);
        return false;
    }
}

bool SRMAgent::IsPkiMonitorRunning()
{
    vector<ProcessMapItem> procList;
    ProcessMapItem agPkiMonPmi;
    memset(&agPkiMonPmi, 0, sizeof(agPkiMonPmi));
    agPkiMonPmi.procName = c_agpkimon_module_name;
    procList.push_back(agPkiMonPmi);


    DWORD returnVal = hlpFindProcesses(procList);
    if (returnVal == 0) {
        return true;
    } else {
        LOG_INFOW(L"[IsPkiMonitorRunning] EC: %d", returnVal);
        return false;
    }
}


bool SRMAgent::GetSoftwareInfo(wstring& xmlOutput, int* pSize)
{
    PF_GetSysSoftwareInformation pfFunc = NULL;
    if (LoadFuncFromDLL(m_hAgtInfoModule, c_agtinfo_module_name, 
        "GetSysSoftwareInformation", (void **)&pfFunc)) {

            wchar_t xmlBuf[c_default_xml_size] = {0};
            ULONG   xmlBufSize = sizeof(xmlBuf);

            DWORD returnVal = ((PF_GetSysSoftwareInformation)pfFunc)(xmlBuf, &xmlBufSize);
            if ( returnVal == 0) {
                xmlOutput.assign(xmlBuf);
                *pSize = (int)xmlBufSize;

                // Collect APP trending data
                AppProxy* pAppProxy = AppProxy::GetInstance();
                pAppProxy->setCredentialInfo(m_user, m_password);
                pAppProxy->UpdateAppTrendingInfo(xmlOutput);

                return true;
            } else {
                LOG_INFOW(L"[GetSoftwareInfo] failed, EC: %d",  returnVal);
            }

    }
    return false;
}



bool SRMAgent::GetHardwareInfo(wstring& xmlOutput, int* pSize)
{
    PF_GetSysHardwareInformation pfFunc = NULL;
    if (LoadFuncFromDLL(m_hAgtInfoModule, c_agtinfo_module_name, 
        "GetSysHardwareInformation", (void **)&pfFunc)) {

            wchar_t xmlBuf[c_default_xml_size] = {0};
            ULONG   xmlBufSize = sizeof(xmlBuf);
            DWORD returnVal = ((PF_GetSysHardwareInformation)pfFunc)(xmlBuf, &xmlBufSize);
            if ( returnVal == 0) {
                xmlOutput.assign(xmlBuf);
                *pSize = (int)xmlBufSize;
                return true;
            } else {
                LOG_INFOW(L"[GetHardwareInfo] failed, EC: %d",  returnVal);
            }
    } 

    return false;
}

bool SRMAgent::GetServerPkiInfo(wstring& xmlOutput, int* pSize, int intervalInHour)
{
    PF_GetServerPKI pfFunc = NULL;
    if (LoadFuncFromDLL(m_hAgtInfoModule, c_agtinfo_module_name,
        "GetServerPKI", (void **)&pfFunc)) {
            wchar_t xmlBuf[c_default_xml_size] = {0};
            ULONG   xmlBufSize = sizeof(xmlBuf);

            if (((PF_GetServerPKI)pfFunc)(xmlBuf, &xmlBufSize, intervalInHour)) {
                xmlOutput.assign(xmlBuf);
                *pSize = (int)xmlBufSize;
                return true;
            } else {
                LOG_INFOW(L"[GetServerPkiInfo] failed, EC: %d",  GetLastError());
            }
    }

    return false;
}


bool SRMAgent::StartPkiMonitor()
{
    //if (IsPkiMonitorRunning()) {
    //    return false;
    //} else {
        STARTUPINFO si;
        ZeroMemory( &si, sizeof(si) );
        si.cb = sizeof(si);
		si.dwFlags = STARTF_USESHOWWINDOW;
		si.wShowWindow = SW_HIDE;

        PROCESS_INFORMATION pi;
        ZeroMemory( &pi, sizeof(pi) );

		//Create Alert configuratio file: SrmCfg.xml
		CreateNewAlertConfigureFile();

        wstring commandLine; //= c_agpkimon_module_name + c_agpkimon_pki_only;
		GetAgPkiMonCommand(commandLine);

        // Start the child process. 
        if( !CreateProcess( NULL,   // No module name (use command line). 
            const_cast<wchar_t*>(commandLine.c_str()), // Command line. 
            NULL,             // Process handle not inheritable. 
            NULL,             // Thread handle not inheritable. 
            FALSE,            // Set handle inheritance to FALSE. 
            NULL,          // No creation flags. 
            NULL,             // Use parent's environment block. 
            NULL,             // Use parent's starting directory. 
            &si,              // Pointer to STARTUPINFO structure.
            &pi )             // Pointer to PROCESS_INFORMATION structure.
            ) {
                LOG_INFOW(L"Start AgPkiMon.exe Process failed (%d).", GetLastError());
                return false;
        } else {
            LOG_INFOW(L"Start AgPkiMon.exe Process succeed.");
            CloseHandle( pi.hProcess );
            CloseHandle( pi.hThread );
            return true;
        }
    //}
}


bool SRMAgent::StopPkiMonitor()
{
    //if (IsARCInstalled()) {
    //    return false;
    //} else {
        int returnVal = SendSRMValidCommand(SRM_TOTAL_DISABLE);
        if (returnVal == 0) {
            LOG_INFOW(L"Stop AgPkiMon.exe Process succeed.");
            return true;
        } else {
            LOG_INFOW(L"Stop AgPkiMon.exe Process failed (%d).", returnVal);
			return false;
        }
    //}
}

void SRMAgent::setCredentialInfo(wstring& user, wstring& password)
{
    m_user     = user;
    m_password = password;
}

int SRMAgent::CreateNewAlertConfigureFile()
{
    PF_CreateNewAlertConfigureFile pfFunc = NULL;
    if (LoadFuncFromDLL(m_hSRMClientModule, g_module_dir+c_srmclient_module_name, "CreateNewAlertConfigureFile", (void**)&pfFunc) )
	{
		int ret = pfFunc();
		if ( ret != 0 )
		{
		    LOG_WARNW(L"Failed to CreateNewAlertConfigureFile (%d)", ret);
		}
		return ret;
	}

	return -1;
}

int SRMAgent::SetPkiAlertPolicy(PKIAlertStruct *pstPkiAlertStruct, unsigned long ulServerUpdateTime)
{
	PF_SetPkiAlertPolicy pfFunc = NULL;
    if (LoadFuncFromDLL(m_hSRMClientModule, g_module_dir+c_srmclient_module_name, "SetPkiAlertPolicy", (void**)&pfFunc) )
	{
		int ret = pfFunc(pstPkiAlertStruct, ulServerUpdateTime);
		if ( ret != 0 )
		{
		    LOG_WARNW(L"Failed to SetPkiAlertPolicy (%d)", ret);
		}
		return ret;
	}

	return -1;
}

int SRMAgent::GetSRMValidation(SRMValidation *pstSRMValidStruct)
{
	PF_GetSRMValidation pfFunc = NULL;
    if (LoadFuncFromDLL(m_hSRMClientModule, g_module_dir+c_srmclient_module_name, "GetSRMValidation", (void**)&pfFunc) )
	{
	    int ret = pfFunc(pstSRMValidStruct);
		if ( ret != 0 )
		{
		    LOG_WARNW(L"Failed to GetSRMValidation (%d)", ret);
		}
		return ret;
	}

	return -1;
}

int SRMAgent::SetSRMValidation(SRMValidation *pstSRMValidStruct)
{
	PF_SetSRMValidation pfFunc = NULL;
    if (LoadFuncFromDLL(m_hSRMClientModule, g_module_dir+c_srmclient_module_name, "SetSRMValidation", (void**)&pfFunc) )
	{
		int ret = pfFunc(pstSRMValidStruct);
		if ( ret != 0 )
		{
		    LOG_WARNW(L"Failed to SetSRMValidation (%d)", ret);
		}
		return ret;
	}

	return -1;
}

int SRMAgent::SendSRMValidCommand(int command)
{
	PF_SendSRMValidCommand pfFunc = NULL;
    if (LoadFuncFromDLL(m_hSRMClientModule, g_module_dir+c_srmclient_module_name, "SendSRMValidCommand", (void**)&pfFunc) )
	{
	    int ret = pfFunc(command);
		if ( ret != 0 )
		{
		    LOG_WARNW(L"Failed to SendSRMValidCommand (command=%d, return value=%d)", command, ret);
		}
		return ret;
	}

	return -1;
}

int SRMAgent::savePkiAlertSetting(PKIAlertStruct *pstPkiAlertStruct, unsigned long ulServerUpdateTime, 
						SRMValidation *pstSRMValidStruct)
{
	int ret = -1;

	if ( pstPkiAlertStruct != NULL && ulServerUpdateTime >= 0 )
	{
		ret = SetPkiAlertPolicy(pstPkiAlertStruct, ulServerUpdateTime);
	}

	if ( pstSRMValidStruct != NULL )
	{
		ret += SetSRMValidation(pstSRMValidStruct);
		if ( pstSRMValidStruct->nSRMEnabled == 0 )
		{
			//StopPkiMonitor();
			ret += SendSRMValidCommand(SRM_ALERT_DISABLE);
			ret += SendSRMValidCommand(SRM_PKI_UTL_DISABLE);
			return ret;
		}

		if ( pstSRMValidStruct->nAlertEnabled == 1 )
		{
			ret += SendSRMValidCommand(SRM_ALERT_ENABLE);
		}
		else
		{
			ret += SendSRMValidCommand(SRM_ALERT_DISABLE);
		}
		
		if ( pstSRMValidStruct->nPKIUtlEnabled == 1 )
		{
			ret += SendSRMValidCommand(SRM_PKI_UTL_ENABLE);
		}
		else
		{
			ret += SendSRMValidCommand(SRM_PKI_UTL_DISABLE);
		}
	}

	return ret;
}

bool SRMAgent::IsSRMEnabled()
{
	SRMValidation srmValidation = {0};
	if ( this->GetSRMValidation(&srmValidation) == 0 && srmValidation.nSRMEnabled != 0 )
		return true;

	return false;
}

void SRMAgent::GetAgPkiMonCommand(wstring &command)
{
	SRMValidation srmValidation = {0};
	if ( this->GetSRMValidation(&srmValidation) != 0 )
	{
	    return;
	}

	command.assign( c_agpkimon_module_name );
	//compose agpkimon parameters
	if (srmValidation.nAlertEnabled==1 && srmValidation.nPKIUtlEnabled!=1)
	{
		//StartAgPkiMonProcess -- agpkimon running with Alert only
		// >> lijwe02
		//command.append(L" -a");
		command.append(L" -t"); // in order to retrieve pki information for RPS node, we change parameter here to work around, need to change back
		// << lijwe02
	}
	else if (srmValidation.nAlertEnabled!=1 && srmValidation.nPKIUtlEnabled==1)
	{
		//StartAgPkiMonProcess -- agpkimon running with PKI UTL only
		command.append(L" -p");
	}
	else if (srmValidation.nAlertEnabled==1 && srmValidation.nPKIUtlEnabled==1)
	{
		//StartAgPkiMonProcess -- agpkimon running with PKI UTL&Alert
		command.append(L" -t");
	}
	else if (srmValidation.nAlertEnabled!=1 && srmValidation.nPKIUtlEnabled!=1)
	{
		//StartAgPkiMonProcess -- agpkimon running without PKI UTL&Alert
		// >> lijwe02
		//command.append(L" -n");
		command.append(L" -p"); // in order to retrieve pki information for RPS node, we change parameter here to work around, need to change back
		// << lijwe02
	}

	return;
}

int SRMAgent::GetAlertRecords(int *alertType, PWCHAR *alertHeader, int alertHeaderSize, int *threshold, int *curUtil, int recordCount)
{
	int ret = -1;
    HMODULE hAgPkiAlt = NULL;
	PF_GetAlertRecord GetAlertRecord = NULL;
	if ( LoadFuncFromDLL(hAgPkiAlt, c_agpkialt_module_name, "GetAlertRecord", (void**)&GetAlertRecord) )
	{
	    ret = GetAlertRecord(alertType, alertHeader, alertHeaderSize, threshold, curUtil, recordCount);
	}

	ReleaseDLL(hAgPkiAlt);

	return ret;
}

BOOL SRMAgent::EnableAlert(BOOL enable)
{
	SRMValidation stSRMValidStruct = {0};
	int ret = -1;
	if ( GetSRMValidation(&stSRMValidStruct) == 0 )
	{
        stSRMValidStruct.nAlertEnabled = enable ? 1 : 0;
	    if ( SetSRMValidation(&stSRMValidStruct) == 0 )
		{
			if ( enable )
			{
		        ret = SendSRMValidCommand(SRM_ALERT_ENABLE);
			}
			else
			{
			    ret = SendSRMValidCommand(SRM_ALERT_DISABLE);
			}
		}
	}

	if ( ret == 0 )
		return TRUE;

	return FALSE;
}

BOOL SRMAgent::EnablePkiUtl(BOOL enable)
{
    SRMValidation stSRMValidStruct = {0};
    int ret = -1;
    if ( GetSRMValidation(&stSRMValidStruct) == 0 )
    {
        stSRMValidStruct.nPKIUtlEnabled = enable ? 1 : 0;
        if ( SetSRMValidation(&stSRMValidStruct) == 0 )
        {
            if ( enable )
            {
                ret = SendSRMValidCommand(SRM_PKI_UTL_ENABLE);
            }
            else
            {
                ret = SendSRMValidCommand(SRM_PKI_UTL_DISABLE);
            }
        }
    }

    if ( ret == 0 )
        return TRUE;

    return FALSE;
}
