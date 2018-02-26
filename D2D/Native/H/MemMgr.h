#pragma once
#include <windows.h>
#include <string>

#include "AFCoreFunction.h"
using namespace std;

//////////////////////////////////////////////////////////////////////////
typedef enum
{
    EFO_READ = 0x01,               ///ZZ: Read only for file mapping
    EFO_WRITE = 0x02,              ///ZZ: Write only for file mapping
    EFO_READWRITE = 0x03,          ///ZZ: Read/Write access for file mapping
    EFO_OPEN_ALWAYS = 0x04,        ///ZZ: When file for file mapping, create it.
    EFO_OUTO_RELEASE = 0x08,       ///ZZ: File for file mapping is deleted when all handles closed.
    EFO_FIXED_MAP_FILE = 0x10      ///ZZ: Create file as specifies size when file created.
}E_FILEMAP_OPTION;

///ZZ: Wrapper of file mapping operation.
class CFileMapWrap
{
public:
    CFileMapWrap();
    CFileMapWrap(const WCHAR* pwzFilePath, ULONGLONG ullMapSize, DWORD dwOption, const WCHAR* pwzVirtualMemName = NULL);
    CFileMapWrap(HANDLE hFileHandle, ULONGLONG ullMapSize, DWORD dwOption, const WCHAR* pwzVirtualMemName = NULL);
    ~CFileMapWrap();

    long CreateVirtualMem(
        HANDLE hFileHandle = INVALID_HANDLE_VALUE, 
        const WCHAR* pwzFilePath = NULL,
        ULONGLONG ullMapSize = 0, 
        DWORD dwOption = 0, 
        const WCHAR* pwzVirtualMemName = NULL);

    long ReleaseVirtualMem();

protected:
    wstring       m_wsVirtualMemName;
    wstring       m_wsFilePath;
    HANDLE        m_hFileHandle;
    HANDLE        m_hFileMapping;
    bool          m_bExistMapping;
    PBYTE         m_pbMemView;
    LARGE_INTEGER m_ullMapSize;
    DWORD         m_dwOption;
};

//////////////////////////////////////////////////////////////////////////
typedef enum
{
    EMM_PMEM = 0x01,    ///ZZ: By function new on physical memory
    EMM_VMEM = 0x02,    ///ZZ: By VirtualAlloc on virtual memory
    EMM_FMAP = 0x04     ///ZZ: Simulate on file map
}E_MEMALLOC_METHOD;

///ZZ: Create memory for specified count of objects. For class, constructor will be called after memory created.
template<typename Element>
class CMemAlloc : public CFileMapWrap
{
public:
    CMemAlloc(ULONGLONG ullAllocSize = 0, DWORD dwAllocMethod = EMM_PMEM | EMM_VMEM | EMM_FMAP, bool bInitZero = true, const WCHAR* pwzTmpFolder = NULL); 
    ~CMemAlloc();

    operator Element*() const throw()  { return m_pbCurMem; }
    bool operator == (const Element* pInputPtr) { return (pInputPtr == m_pbCurMem); }
    bool operator == (const CMemAlloc<Element>& inputObj) { return (inputObj.m_pbCurMem == m_pbCurMem); }
    Element& operator * () const { return *m_pbCurMem; }
    Element** operator & () throw() { return &m_pbCurMem; }

    Element& operator [] (DWORD dwPos) { return m_pbCurMem[dwPos]; }
    Element& operator [] (int iPos) { return m_pbCurMem[iPos]; }
    Element& operator [] (ULONGLONG ullPos) { return m_pbCurMem[ullPos]; }

    Element* Ptr() { return m_pbCurMem; }
    bool     IsNULL() { return m_pbCurMem ? true : false; }

    PBYTE Allocate(ULONGLONG ullAllocSize, DWORD dwAllocMethod = EMM_PMEM | EMM_VMEM | EMM_FMAP, bool bInitZero = true, const WCHAR* pwzTmpFolder = NULL);
    void  Release();

private:
    Element*  m_pbCurMem;
    PBYTE     m_pbMemPool;
    DWORD     m_dwAllocType;
    ULONGLONG m_ullElementCnt;
    ULONGLONG m_ullMemUsedSize;
    ULONGLONG m_ullMemAllocSize;

    ///ZZ: Determine if Element is a a class. If yes, we will call placement new to initialize object.
    ///ZZ: SFINAE: WHen some template cannot be initialized, it will be ignored and initialize next available function instead of error when compilation.
private:
    ///ZZ: When Element is not a class, 0 cannot be converted to int TestType::* type pointer.Thus, it will be implemented as function return byte.
    template<typename TestType> static short IsClass(int TestType::*);      ///ZZ: Accept data struct can have member variable.
    template<typename TestType> static byte IsClass(...);                   ///ZZ: Accept any type input.
    enum { IS_CLASS=(sizeof(IsClass<Element>(0)) - sizeof(byte)) };
};


///////////////////////////////////////////////////////////////// /MemMgr.cpp /////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////
///ZZ: Merged from MemMgr.cpp
//////////////////////////////////////////////////////////////////////////
CFileMapWrap::CFileMapWrap()
: m_hFileHandle(INVALID_HANDLE_VALUE)
, m_hFileMapping(INVALID_HANDLE_VALUE)
, m_bExistMapping(false)
, m_pbMemView(NULL)
, m_dwOption(0)
{
}

CFileMapWrap::CFileMapWrap(const WCHAR* pwzFilePath, ULONGLONG ullMapSize, DWORD dwOption, const WCHAR* pwzVirtualMemName /*= NULL*/)
: m_hFileHandle(INVALID_HANDLE_VALUE)
, m_hFileMapping(INVALID_HANDLE_VALUE)
, m_bExistMapping(false)
, m_pbMemView(NULL)
, m_dwOption(dwOption)
{
    m_ullMapSize.QuadPart = ullMapSize;
    if (pwzFilePath)
        m_wsFilePath = pwzFilePath;

    if (pwzVirtualMemName)
        m_wsVirtualMemName = pwzVirtualMemName;
}

CFileMapWrap::CFileMapWrap(HANDLE hFileHandle, ULONGLONG ullMapSize, DWORD dwOption, const WCHAR* pwzVirtualMemName /*= NULL*/)
: m_hFileHandle(INVALID_HANDLE_VALUE)
, m_hFileMapping(INVALID_HANDLE_VALUE)
, m_bExistMapping(false)
, m_pbMemView(NULL)
, m_dwOption(dwOption)
{
    m_ullMapSize.QuadPart = ullMapSize;

    if (pwzVirtualMemName)
        m_wsVirtualMemName = pwzVirtualMemName;

    if (hFileHandle)
    {
        DuplicateHandle(
            GetCurrentProcess(), 
            hFileHandle, 
            GetCurrentProcess(),
            &m_hFileHandle, 
            0,
            FALSE,
            DUPLICATE_SAME_ACCESS);
    }
}

CFileMapWrap::~CFileMapWrap()
{
    ReleaseVirtualMem();
}

long CFileMapWrap::CreateVirtualMem(HANDLE hFileHandle /* = INVALID_HANDLE_VALUE */, 
                                    const WCHAR* pwzFilePath /* = NULL */, 
                                    ULONGLONG ullMapSize /* = 0 */, 
                                    DWORD dwOption /* = 0 */,
                                    const WCHAR* pwzVirtualMemName /* = NULL */)
{
    if (hFileHandle)
    {
        ReleaseVirtualMem();

        DuplicateHandle(
            GetCurrentProcess(), 
            hFileHandle, 
            GetCurrentProcess(),
            &m_hFileHandle, 
            0,
            FALSE,
            DUPLICATE_SAME_ACCESS);
    }

    if (pwzFilePath)
        m_wsFilePath = pwzFilePath;
    if (0 != ullMapSize)
        m_ullMapSize.QuadPart = ullMapSize;
    if (0 != dwOption)
        m_dwOption = dwOption;
    if (pwzVirtualMemName)
        m_wsVirtualMemName = pwzVirtualMemName;

    long lRetCode = 0;
    if ((INVALID_HANDLE_VALUE == m_hFileHandle) && !m_wsFilePath.empty())
    {
        DWORD dwDesiredAccess = 0;
        if (EFO_READ == (m_dwOption & EFO_READ))
            dwDesiredAccess |= GENERIC_READ;
        if (EFO_WRITE == (m_dwOption & EFO_WRITE))
            dwDesiredAccess |= GENERIC_WRITE;

        DWORD dwCreateFlag = OPEN_EXISTING;
        if (EFO_OPEN_ALWAYS == (m_dwOption & EFO_OPEN_ALWAYS))
            dwCreateFlag = OPEN_ALWAYS;

        DWORD dwFileFlag = FILE_ATTRIBUTE_NORMAL;
        if (EFO_OUTO_RELEASE == (m_dwOption & EFO_OUTO_RELEASE))
            dwFileFlag |= FILE_FLAG_DELETE_ON_CLOSE;

        m_hFileHandle = CreateFileW(
            m_wsFilePath.c_str(),
            dwDesiredAccess,
            FILE_SHARE_READ,
            NULL,
            dwCreateFlag,
            dwFileFlag,
            NULL);
        if (INVALID_HANDLE_VALUE == m_hFileHandle)
            lRetCode = GetLastError();
        else if (EFO_FIXED_MAP_FILE == (m_dwOption & EFO_FIXED_MAP_FILE))
        {
            LARGE_INTEGER ullNewPos = {0};
            if (!SetFilePointerEx(m_hFileHandle, m_ullMapSize, &ullNewPos, FILE_BEGIN))
                lRetCode = GetLastError();
        }
    }

    if (0 == m_ullMapSize.QuadPart)
    {
        if (INVALID_HANDLE_VALUE == m_hFileHandle)
            return E_INVALIDARG;
        else
        {
            if (!GetFileSizeEx(m_hFileHandle, &m_ullMapSize))
            {
                lRetCode = GetLastError();
                return lRetCode;
            }
        }
    }

    ///ZZ: Create security description.
    SECURITY_DESCRIPTOR stSecurityDesc; 
    SECURITY_ATTRIBUTES stSecurityAttrib; 
    InitializeSecurityDescriptor(&stSecurityDesc, SECURITY_DESCRIPTOR_REVISION); 
    SetSecurityDescriptorDacl(&stSecurityDesc, TRUE, (PACL)NULL,FALSE); //set all the user can access the object      
    stSecurityAttrib.nLength = sizeof(SECURITY_ATTRIBUTES); 
    stSecurityAttrib.bInheritHandle = FALSE; 
    stSecurityAttrib.lpSecurityDescriptor = &stSecurityDesc;

    do 
    {
        DWORD dwPageAccess = 0;
        if (EFO_READWRITE == (m_dwOption & EFO_READWRITE))
            dwPageAccess = PAGE_READWRITE;
        else
            dwPageAccess = PAGE_READONLY;

        m_hFileMapping = CreateFileMappingW(
            m_hFileHandle, 
            &stSecurityAttrib, 
            dwPageAccess, 
            m_ullMapSize.HighPart, 
            m_ullMapSize.LowPart, 
            m_wsVirtualMemName.c_str());

        lRetCode = GetLastError();
        if (NULL == m_hFileMapping)
            break;

        if (ERROR_ALREADY_EXISTS == lRetCode)
            m_bExistMapping = true;

        DWORD dwDesiredAccess = 0;
        if (EFO_READ == (m_dwOption & EFO_READ))
            dwDesiredAccess |= FILE_MAP_READ;
        if (EFO_WRITE == (m_dwOption & EFO_WRITE))
            dwDesiredAccess |= FILE_MAP_WRITE;

        m_pbMemView = (LPBYTE)MapViewOfFile(m_hFileMapping, dwDesiredAccess, 0, 0, 0);
        if (NULL == m_pbMemView)
        {
            lRetCode = GetLastError();
            break;;
        }
    } while (0);

    return lRetCode;
}

long CFileMapWrap::ReleaseVirtualMem()
{
    if (m_pbMemView)
    {
        FlushViewOfFile(m_pbMemView, 0);
        UnmapViewOfFile(m_pbMemView);
        m_pbMemView = NULL;
    }

    if (m_hFileMapping)
    {
        CloseHandle(m_hFileMapping);
        m_hFileMapping = NULL;
    }

    if (INVALID_HANDLE_VALUE != m_hFileHandle)
    {
        CloseHandle(m_hFileHandle);
        m_hFileHandle = INVALID_HANDLE_VALUE;
    }

    return 0;
}

//////////////////////////////////////////////////////////////////////////
///ZZ: implement for template class CMemAlloc.
/////////////////////////////////////////////////////////////////////////
template<typename Element>
CMemAlloc<Element>::CMemAlloc(ULONGLONG ullAllocSize /*= 0*/, DWORD dwAllocMethod /* = EMM_PMEM | EMM_VMEM | EMM_FMAP */, bool bInitZero /*= true*/, const WCHAR* pwzTmpFolder /*= NULL*/)
: m_pbCurMem(NULL)
, m_pbMemPool(NULL)
, m_dwAllocType(0)
, m_ullMemAllocSize(0)
, m_ullMemUsedSize(0)
, m_ullElementCnt(0)
{
    Allocate(ullAllocSize, dwAllocMethod, bInitZero, pwzTmpFolder);
}

template<typename Element>
CMemAlloc<Element>::~CMemAlloc()
{
    Release();
}

template<typename Element>
PBYTE CMemAlloc<Element>::Allocate(ULONGLONG ullAllocSize, DWORD dwAllocMethod /* = EMM_PMEM | EMM_VMEM | EMM_FMAP */, bool bInitZero /*= true*/, const WCHAR* pwzTmpFolder /*= NULL*/)
{
    if (0 == ullAllocSize)
        return NULL;

    if (0 == dwAllocMethod)
        dwAllocMethod = EMM_PMEM | EMM_VMEM | EMM_FMAP;

    ///ZZ: When call placement new to initialize object of class with destructor, there will be additional storage to save how many object initialized.
    ///ZZ: We will always allocate 4 more bytes to contain this case. The actual data is start from 4 byte after start address of memory allocated.
    ULONGLONG ullMemNeed = ullAllocSize * sizeof(Element) + sizeof(int);

    if (ullMemNeed > m_ullMemAllocSize)
    {
        Release();

        do 
        {
            long lRetCode = 0;
            if ((NULL == m_pbCurMem) && (EMM_PMEM == (dwAllocMethod & EMM_PMEM)))
            {
                const char* pzErrMsg = NULL;
#if 1
                try
                {
                    m_pbMemPool = new BYTE[(size_t)ullMemNeed];
                }
                catch(std::bad_alloc stExp)
                {
                    pzErrMsg = stExp.what();
                    m_pbMemPool = NULL;
                }
                catch(...)
                {
                    m_pbMemPool = NULL;
                }
#else        
                m_pbMemPool = new(nothrow) BYTE[m_ullMemAllocSize];
#endif

                if (m_pbMemPool)
                {
                    m_dwAllocType = EMM_PMEM;
                    break;
                }
            }

            ///ZZ: For virtual memory allocation, the size should be multiple of page size.
            SYSTEM_INFO stSysInfo = {0};
            GetSystemInfo(&stSysInfo);
            if (stSysInfo.dwPageSize && (ullMemNeed % stSysInfo.dwPageSize))
            {
                ULONGLONG ullMultiCnt = ullMemNeed / stSysInfo.dwPageSize + 1;
                ullMemNeed = ullMultiCnt * stSysInfo.dwPageSize;
            }

            if ((NULL == m_pbCurMem) && (EMM_VMEM == (dwAllocMethod & EMM_VMEM)))
            {
                m_pbMemPool = (LPBYTE)VirtualAlloc(NULL, (SIZE_T)ullMemNeed, MEM_RESERVE|MEM_COMMIT, PAGE_READWRITE);
                if (m_pbMemPool)
                {
                    m_dwAllocType = EMM_VMEM;
                    break;
                }
                else
                    lRetCode = GetLastError();
            }

            if ((NULL == m_pbCurMem) && (EMM_FMAP == (dwAllocMethod & EMM_FMAP)))
            {
                WCHAR wzTmpFilePath[MAX_PATH] = {0};
                GUID Guid = {0};
                WCHAR wzGUIDStr[128] = {0};
                CoCreateGuid(&Guid);
                StringFromGUID2(Guid, wzGUIDStr, _countof(wzGUIDStr));

                if (pwzTmpFolder && wcslen(pwzTmpFolder))
                {
                    if (L'\\' == pwzTmpFolder[wcslen(pwzTmpFolder) -1])
                        swprintf_s(wzTmpFilePath, _countof(wzTmpFilePath), L"%s%u_%s.mem", pwzTmpFolder, GetCurrentProcessId(), wzGUIDStr);
                    else
                        swprintf_s(wzTmpFilePath, _countof(wzTmpFilePath), L"%s\\%u_%s.mem", pwzTmpFolder, GetCurrentProcessId(), wzGUIDStr);
                }
                else
                {
                    WCHAR wzInstallPath[MAX_PATH] = {0};
                    lRetCode = AFGetInstallPath(wzInstallPath, _countof(wzInstallPath));
                    if (0 == lRetCode)
                        swprintf_s(wzTmpFilePath, _countof(wzTmpFilePath), L"%sBIN\\Temp\\%u_%s.mem", wzInstallPath, GetCurrentProcessId(), wzGUIDStr);
                    else
                    {
                        if (GetModuleFileNameW(NULL, wzInstallPath, _countof(wzInstallPath)))
                        {
                            WCHAR* pwzLastBS = wcsrchr(wzInstallPath, L'\\');
                            if (pwzLastBS)
                                *pwzLastBS = L'\0';
                            swprintf_s(wzTmpFilePath, _countof(wzTmpFilePath), L"%s\\%u_%s.mem", wzInstallPath, GetCurrentProcessId(), wzGUIDStr);
                        }
                        else
                            swprintf_s(wzTmpFilePath, _countof(wzTmpFilePath), L".\\%u_%s.mem", GetCurrentProcessId(), wzGUIDStr);
                    }
                }

                lRetCode = CreateVirtualMem(NULL, wzTmpFilePath, ullMemNeed, EFO_READWRITE | EFO_OPEN_ALWAYS | EFO_OUTO_RELEASE, NULL);
                if ((0 == lRetCode) && m_pbMemView)
                    m_pbMemPool = m_pbMemView;

                if (m_pbMemPool)
                {
                    m_dwAllocType = EMM_FMAP;
                    break;
                }
            }
        } while (0);

        if (m_pbMemPool)
            m_ullMemAllocSize = ullMemNeed;
    }

    if (m_pbMemPool)
    {
        m_pbCurMem = (Element*)m_pbMemPool;

        m_ullMemUsedSize = ullMemNeed;
        if (bInitZero)
            memset(m_pbMemPool, 0, (size_t)m_ullMemUsedSize);


        m_ullElementCnt = ullAllocSize;

        if (IS_CLASS)
            m_pbCurMem = new(m_pbMemPool) Element[(size_t)m_ullElementCnt];
    }

    return (PBYTE)m_pbCurMem; 
}

template<typename Element>
void CMemAlloc<Element>::Release()
{
    if (m_pbMemPool)
    {
        if (IS_CLASS && m_pbCurMem)
        {
            for (ULONGLONG ullIdx = 0; ullIdx < m_ullElementCnt; ullIdx++)
            {
                Element* pCurElement = (Element*)m_pbCurMem + ullIdx;
                ///ZZ: Explicitly call destructor to un-initialize class object.
                if (pCurElement)
                    pCurElement->~Element();
            }
        }

        switch (m_dwAllocType)
        {
        case EMM_PMEM:
            delete []m_pbMemPool;
            break;
        case EMM_VMEM:
            VirtualFree(m_pbMemPool, 0, MEM_RELEASE);
            break;
        case EMM_FMAP:
            ReleaseVirtualMem();
            break;
        }
    }

    m_pbMemPool = NULL;
    m_pbCurMem = NULL;
    m_ullMemAllocSize = 0;
    m_ullMemUsedSize = 0;
}
