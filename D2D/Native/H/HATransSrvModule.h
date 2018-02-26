//
// The socket recv and process logic at server side
// baide02 2010-05-20
//

#ifndef _HATRANS_SRVMODULE_H_
#define _HATRANS_SRVMODULE_H_
#include <WinSock2.h>
#include <Unknwn.h>

#ifdef HATRANSSRVMODULE_EXPORTS
#define HATRANSSRVMODULE_API extern "C" __declspec(dllexport)
#else
#define HATRANSSRVMODULE_API extern "C" __declspec(dllimport)
#endif


struct _t_package_header; // in HACommonDef.h
struct _t_job_header;

struct ISrvCmdHandler : public IUnknown
{
    virtual int     SetSocket(SOCKET sock) = 0;
    virtual int     NewJob(struct _t_job_header* pstJobHeader, unsigned long* pnStatus) = 0;
    virtual int     JobReconnectBegin(void) = 0;
    virtual int     JobReconnectEnd(void) = 0;
    virtual int     ProcessCmd(struct _t_package_header* pstPackHeader, bool& bJobEnd) = 0;
    virtual int     LoopRecvAndProcess(void) = 0;

    virtual int     GetJobUUID(wchar_t* pwszGuidBuf, int nBufCnt) = 0;
    virtual int     GetProductNode(wchar_t* pwszNodeName, int nBufCnt) = 0;
    virtual int     MarkStopFlag(bool bStop) = 0;
    virtual int     GetLastReceiveTime(__time64_t& ltLastRecv) = 0;
};

HATRANSSRVMODULE_API int HASrv_CreateSrvCmdHandler(ISrvCmdHandler** ppHandler);
typedef int (*PFN_HASrv_CreateSrvCmdHandler)(ISrvCmdHandler** ppHandler);


#endif //_HATRANS_SRVMODULE_H_
