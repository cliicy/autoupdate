/////////////////////////////////////////////////////////////////////////////
// App.h		CApplication Interface
// Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.

#ifndef _APP
#define _APP

#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CApplication
#else
class __declspec(dllimport) CApplication
#endif
{
private:
    CString m_sClassName;
    CString m_sMutexName;

    void Construct(LPCTSTR lpszClassName, LPCTSTR lpszMutex);

public:
    static const CString ARCserveManagerMutex;
    static const CString ARCserveAdminMutex;
    static const CString ARCserveCentralDBConfigMutex;
    static const CString ARCserveMonitorMutex;
    static const CString ARCserveDeviceConfigMutex;

    static const CString ARCserveManagerClass;
    static const CString ARCserverAdminClass;
    static const CString ARCserveCentralDBConfigClass;
    static const CString ARCserveMonitorClass;
    static const CString ARCserveDeviceConfigClass;

    enum Application{
        ARCserveManager,
        ARCserveAdmin,
        ARCserveDBConfig,
        ARCserveMonitor,
		ARCserveDeviceConfig
    };

    // Constructor
    CApplication(Application App);
    CApplication(LPCTSTR lpszClassName, LPCTSTR lpszMutex);

    // Attributes
    LPCTSTR GetClassName()  {return m_sClassName;}
    BOOL    IsRunning()       {return IsRunning(m_sMutexName);}
    BOOL    CloseWindow()   {return CloseWindow(m_sClassName);}

    static BOOL IsRunning(LPCTSTR lpszMutex);
    static BOOL CloseWindow(LPCTSTR lpszClassName);
    static HWND FindWindow(LPCTSTR lpszPartialTitle);
    static void CloseWindows(LPCTSTR lpszPartialTitle);

};
#endif
