#ifndef _HA_REPOSIROTYKIT_H_
#define _HA_REPOSIROTYKIT_H_

#ifdef HAREPOSITORYKIT_EXPORTS
#define HASESSKIT_API extern "C" __declspec(dllexport)
#else
#define HASESSKIT_API extern "C" __declspec(dllimport)
#endif


// In hacommondef.h
struct _t_job_header;
struct _t_file_header;

namespace HADT
{

#define HA_SESSKIT_NEED_MORE_BUFFER     0xFFFF0001
#define HA_SESSKIT_FIND_NO_RESULT       0xFFFF0002

/*
Represent a session root
*/
typedef struct _t_sessroot 
{
    wchar_t wszPath[512];           // The root path
    wchar_t wszUsername[128];       // The username to access wszPath if it is a ShareFolder
    wchar_t wszPassword[128];       // The password to access wszPath if it is a ShareFolder
} ST_SESSROOT, *PST_SESSROOT;

class ISessRootManager
{
public:
    virtual void    Release() = 0;

    /*
    Update the root path to session repository
    return 0 success, others fail.
    */
    //virtual int     UpdateLatestRoot(const wchar_t* pwszNode, const wchar_t* pwszRoot) = 0;
    virtual int     UpdateLatestRoot(const wchar_t* pwszNode, const ST_SESSROOT* pstRoot) = 0;

    /*
    Get the Latest root path of the input product node.
    @pwszRootBuf - return a zero-terminated string
    @nBufLen     - if return value is NEED_MORE_BUFFER, return the needed buffer length in word. 

    return value:
    0                           - success
    NEED_MORE_BUFFER            - need more input buffer, the len in words needed is returned 
                                  in nBufLen
    HA_SESSKIT_FIND_NO_RESULT   - not found
    others fail
    */
    //virtual int     FetchLatestRoot(const wchar_t* pwszNode, wchar_t* pwszRootBuf, 
    //                                int& nBufLen) = 0;
    virtual int     FetchLatestRoot(const wchar_t* pwszNode, ST_SESSROOT* pstRoot) = 0;
};

class ISessBrowser
{
public:
    virtual void    Release() = 0;

    /*
    Call this to free buffer returned by this interface.
    */
    virtual void    FreeBuffer(void* p) = 0;

    /*
    Query all product nodes that has session be replicated on the Replica machine.
    if input ppwAllNode is NULL, nBufSizeInWord return the number of node.
    MUST call FreeBuffer() to free *ppwAllNode.
    @ppwAllNode [out]        - return the all node buffer. format is "node1\0node2\0node3\0\0". 
                               if no root, return NULL.
    @pnBufSizeInWord [out]   - return the buffer size in word.

    return value:
    0      success
    others fail
    */
    virtual int     QueryAllProductNode(wchar_t** ppwAllNode, int* pnBufSizeInWord) = 0;

    /*
    Giving a node name, query all it's root path, 
    Must call FreeBuffer() to free the *ppwAllRoot.
    @ppwAllRoot [out] - format is "root1\0root2\0root3\0\0". Call FreeBuffer() to free it.
    if success but not exist any root of the node, *ppwAllRoot is NULL and *pnBufSizeInWord is 0

    return value:
    0      success
    others fail
    */
    virtual int     QueryAllRootOfNode(const wchar_t* pwszNode, wchar_t** ppwAllRoot, 
                                       int* pnBufSizeInWord) = 0;

    /*
    Query the sessions' number under one root path. 
    @ppnSessNumBuf -[out] the array of session number. Call FreeBuffer() to free it. 
                          if no session, return NULL.
    @pnSessCnt     -[out] the item count in *ppnSessNumBuf.

    if input ppnSessNumBuf is NULL, *pnSessCnt return the count of session.

    return value:
    0      success
    others fail
    */
    virtual int     QueryAllSessNumUnderRoot(const wchar_t* pwszRoot, int** ppnSessNumBuf, 
                                             int* pnSessCnt) = 0;

    /*
    Query session count of one product node.
    return 0 success, others fail
    */
    virtual int     QuerySessCountOfNode(const wchar_t* pwszNode, int& nCount) = 0;


    /*
    Get session's path by product node name and session number.
    return value:

    0                           - success
    NEED_MORE_BUFFER            - need more input buffer, buffer need is return in nBufLen, in word.
    HA_SESSKIT_FIND_NO_RESULT   - does not find path of the session number
    others fail
    */
    virtual int     QueryPathByNum(const wchar_t* pwszNode, int nSessNum, 
                                   wchar_t* pwszPathBuf, int& nBufLen) = 0;

    /*
    Get session's root path by product node name and session number.
    return value:

    0                           - success
    NEED_MORE_BUFFER            - need more input buffer, buffer need is return in nBufLen, in word.
    HA_SESSKIT_FIND_NO_RESULT   - does not find root of the session number
    others fail
    */
    virtual int     QueryRootByNum(const wchar_t* pwszNode, int nSessNum, 
                                   wchar_t* pwszRootBuf, int& nBufLen) = 0;
};

class IHARepositoryKit
{
protected:
    IHARepositoryKit() {};
    virtual ~IHARepositoryKit() {};
    IHARepositoryKit(const IHARepositoryKit&);
    IHARepositoryKit& operator=(const IHARepositoryKit&);
public:
    virtual void    Release() = 0;

    virtual int     CreateSessRootManager(ISessRootManager** ppRootManager) = 0;

    virtual int     CreateSessBrowser(ISessBrowser** ppBrowserIntf) = 0;

    /*
    Generate the index file for all sessions under the root path.
    @pwszRootPath - the root path
    @pwszUsername - the username for root path if is a network share folder, can be NULL.
    @pwszPassword - the password for root path if is a network share folder, can be NULL.
    @bForceRegen  - if true, will force regenerate index file of all sessions under the root.
                    if false, only generate index for session which does not have index file.
    return value:
    0 success.
    others fail.
    */
    virtual int     GenerateIndexFile(const wchar_t* pwszRootPath, const wchar_t* pwszUsername,
                                      const wchar_t* pwszPassword, bool bForceRegen) = 0;

    /*
    Only nUpperBound sessions will be preserved. If this number is exceeded, the oldest 
    sessions will be merged into the newer session.
    For HA replica only store the sessions on replica server, does provide input 
    username and password ARGs.
    @pwszProductNode - the product node name
    @nUpperBound     - the max number of sessions we will preserved.
    return value:
    0 success
    others fail
    */
    virtual int     MergeSessWithUpperbound(const wchar_t* pwszProductNode, int nUpperBound) = 0;

    /*
    Merge may fail, call this to continue failed merge before start a new merge.
    return 0 success, others fail
    */
    virtual int     ContinueFailedMerge(const wchar_t* pwszProductNode) = 0;
};

/*
Create a IHARepositoryKit interface
@pwszHaCfgPath -[in ] server’s configuration folder
@pwszUsername  -[in ] the username for configuration folder
@pwszPassword  -[in ] the password for configuration folder.
@ppSessKit     -[out] kit interface returned 
return value:
0: success, others:fail
*/
HASESSKIT_API int CreateRepositoryKit(const wchar_t* pwszHaCfgPath, const wchar_t* pwszUsername, 
                                      const wchar_t* pwszPassword, IHARepositoryKit** ppSessKit);

typedef int (*PFN_CreateRepositoryKit)(const wchar_t* pwszHaCfgPath, const wchar_t* pwszUsername, 
                                       const wchar_t* pwszPassword, IHARepositoryKit** ppSessKit);

/*
Get the session path by session root path and session number.
Notes: root path and session number will identify a session uniquely.
*/
HASESSKIT_API int GetSessionPath(const wchar_t* pwszRoot, int nSessNum, 
                                 wchar_t* pwszpathBuf, int* pnBufLen);

typedef int (*PFN_GetSessionPath)(const wchar_t* pwszRoot, int nSessNum, 
                                  wchar_t* pwszpathBuf, int* pnBufLen);





//===============================================================================
// FOR OfflineCopyInfo.xml
//===============================================================================

typedef struct _t_offlinecopy_info_xml
{
    unsigned long ulVersion;  
    int           nSessNum;
    int           nStatus; //0 success, others fail
    wchar_t       wszSessGUID[64]; //source session GUID
    wchar_t       wszCurrRoot[260];
    wchar_t       wszSrcRoot[260];
}ST_COPYINFO_XML, *PST_COPYINFO_XML;

/*
write data a xml.
return 0 success, others fail
*/
HASESSKIT_API int WriteOfflineCopyInfoXml(const wchar_t* pwszXmlPath,
                                          const ST_COPYINFO_XML* pInfoXml);

typedef int (*PFN_WriteOfflineCopyInfoXml)(const wchar_t* pwszXmlPath,
                                           const ST_COPYINFO_XML* pInfoXml);

/*
Read data from xml.
return 0 success, others fail
If the XML does not exist, also return fail.
*/
HASESSKIT_API int ReadOfflineCopyInfoXml(const wchar_t* pwszXmlPath,
                                         ST_COPYINFO_XML* pInfoXml);

typedef int (*PFN_ReadOfflineCopyInfoXml)(const wchar_t* pwszXmlPath,
                                          ST_COPYINFO_XML* pInfoXml);



//===========================================================================
// parse the KvpDataItem
//===========================================================================

/**
* Represent class Msvm_KvpExchangeDataItem
*/
class IKvpItem
{
protected:
    IKvpItem() {};
    virtual ~IKvpItem() {};
    IKvpItem(const IKvpItem&);
    IKvpItem& operator=(const IKvpItem&);
public:
    virtual void Release() = 0;
    //Source field
    virtual unsigned short GetSource() = 0;
    //Name field, return value only valid during the lifetime of IKvpItem instance
    virtual const wchar_t* GetName() = 0;
    //Data field, return value only valid during the lifetime of IKvpItem instance
    virtual const wchar_t* GetData() = 0;
};

HASESSKIT_API int CreateKvpItemFromXml(const wchar_t* pwszXmlText,
                                       IKvpItem** ppItem);
typedef int (*PFN_CreateKvpItemFromXml)(const wchar_t* pwszXmlText,
                                       IKvpItem** ppItem);



//===========================================================================
// parse the job header, file header, etc.
//===========================================================================
static const char* FNNAME_SerializeJobHeader     = "SerializeJobHeader";
static const char* FNNAME_UnserializeJobHeader   = "UnserializeJobHeader";
static const char* FNNAME_SerializeFileHeader    = "SerializeFileHeader";
static const char* FNNAME_UnserializeFileHeader  = "UnserializeFileHeader";
static const char* FNNAME_FreeSerializedBuf      = "FreeSerializedBuf";

HASESSKIT_API int SerializeJobHeader(const struct _t_job_header* pstHeader, void** ppBuf, int* pnBufLen);
typedef int (*PFN_SerializeJobHeader)(const struct _t_job_header* pstHeader, void** ppBuf, int* pnBufLen);

HASESSKIT_API int UnserializeJobHeader(void* pBuf, int nBufLen, struct _t_job_header* pstHeader);
typedef int (*PFN_UnserializeJobHeader)(void* pBuf, int nBufLen, struct _t_job_header* pstHeader);

HASESSKIT_API int SerializeFileHeader(const struct _t_file_header* pstHeader, void** ppBuf, int* pnBufLen);
typedef int (*PFN_SerializeFileHeader)(const struct _t_file_header* pstHeader, void** ppBuf, int* pnBufLen);

HASESSKIT_API int UnserializeFileHeader(void* pBuf, int nBufLen, struct _t_file_header* pstHeader);
typedef int (*PFN_UnserializeFileHeader)(void* pBuf, int nBufLen, struct _t_file_header* pstHeader);


HASESSKIT_API void FreeSerializedBuf(void* pBuf);
typedef void (*PFN_FreeSerializedBuf)(void* pBuf);
} //HADT


#endif //_HA_REPOSIROTYKIT_H_
