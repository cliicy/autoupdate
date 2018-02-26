#pragma once

#include "IVSSMgrInterface.h"

namespace vsswrapper
{
    class VSSWRAPPERDLL_API CCriticalSectionWrapper;
    class VSSWRAPPERDLL_API IVSSMgrFactory
    {
    public:
        static IVSSMgrFactory* GetFactory();
        static void ReleaseFactory();
        static CCriticalSectionWrapper g_csMutex;
        static IVSSMgrFactory*         g_pVSSMgrFactory;
        static long                    g_lRefCount;

    public:
        IVSSBackupMgrInterface* GetVSSBackupMgr();
        void ReleaseBackupMgr();
        IVSSRestoreMgrInterface* GetVSSRestoreMgr();
        void ReleaseRestoreMgr();
        IBrowseMgrInterface* GetBrowseMgr();
        void ReleaseBrowseMgr();
        IVSSWriterInfoMgrInterface* GetWriterInfoMgr();
        void ReleaseWriterInfoMgr();

    private:
        IVSSMgrFactory();
        ~IVSSMgrFactory();
    };
}