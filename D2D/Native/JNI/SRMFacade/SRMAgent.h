#pragma once
#ifndef _SRMAGENT_H_
#define _SRMAGENT_H_
#include <cwctype>
#include <string>
#include "AgPkiStruct.h"
using namespace std;

class SRMAgent
{
public:
    SRMAgent();
    ~SRMAgent(); // If sombebody want to inherit this class, please change this function to virtual

    bool IsARCInstalled();
	bool IsSRMEnabled();
    bool GetHardwareInfo(wstring& xmlOutput, int* pSize);
    bool GetSoftwareInfo(wstring& xmlOutput, int* pSize);
    bool GetServerPkiInfo(wstring& xmlOutput, int* psize, int intervalInHour);
    bool StartPkiMonitor();
    bool StopPkiMonitor();
    void setCredentialInfo(wstring& user, wstring& password);
	int savePkiAlertSetting(PKIAlertStruct *pstPkiAlertStruct, unsigned long ulServerUpdateTime, SRMValidation *pstSRMValidStruct);
	/*
	 * Parameters: alertType - in/out, array used to store the alert types.
	 *			   alertHeader - in/out, array used to store the alert headers.
	 *			   alertHeaderSize - in, specifies the buffer size of each element of array alertHeader
	 *		 	   threshold - in/out, array used to store the thresholds.
	 *			   curUtil - in/out, array used to store current utilization.
	 *			   recordCount - in, specifies the array size.
	*/
	int GetAlertRecords(int *alertType, PWCHAR *alertHeader, int alertHeaderSize, int *threshold, int *curUtil, int recordCount);
	BOOL EnableAlert(BOOL enable);
    BOOL EnablePkiUtl(BOOL enable);
	
public:
    static const int c_default_xml_size = 64 * 1024; // 64KB

private:
    bool LoadFuncFromDLL(HMODULE& hMod, const wstring fileName, const string funcName,void** ppFunc);
    void ReleaseDLL(HMODULE hMod);
    bool IsPkiMonitorRunning();
	int CreateNewAlertConfigureFile();
	int SetPkiAlertPolicy(PKIAlertStruct *pstPkiAlertStruct, unsigned long ulServerUpdateTime);
	int GetSRMValidation(SRMValidation *pstSRMValidStruct);
	int SetSRMValidation(SRMValidation *pstSRMValidStruct);
	int SendSRMValidCommand(int command);
	void GetAgPkiMonCommand(wstring &command);

private:
    HMODULE m_hAgtInfoModule;
	HMODULE m_hSRMClientModule;

    wstring m_user;
    wstring m_password;
    
};
#endif