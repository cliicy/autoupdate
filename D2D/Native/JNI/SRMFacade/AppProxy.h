#pragma once
#include <CommonDef.h>
#include <ErrorCode.h>
#include <vector>
#include <cwctype>
#include <string>
using namespace std;

typedef DWORD (WINAPI *PF_CollectAppInfo)(const PApplication_CredentialInfo , 
                                          int ,
                                          PApplication_Info , 
                                          int* );

class AppProxy 
{
public:
    const static int COLLECT_APP_INFO_TIME_OUT = 1200000; // unit second

public:
    static AppProxy* GetInstance();
    void UpdateAppTrendingInfo(wstring& srcXML);
    void setCredentialInfo(const wstring& user, const wstring& password);

private:
    AppProxy();
    ~AppProxy();

    BOOL LoadFunc(HMODULE& hMod, const wstring& fileName, PF_CollectAppInfo* ppFunc);
    BOOL CollectAppInfo(const wstring& moduleName, vector<Application_Info>& vAIarray);
    BOOL CollectAppInfoByExe(const wstring& moduleName, vector<Application_Info>& vAIarray);
    void GenerateXML(wstring& srcXML, vector<Application_Info>& vAIarray);

private:
    static AppProxy* m_pSelf;
    vector<wstring> m_vModuleFiles;
    Application_CredentialInfo m_credential;
};