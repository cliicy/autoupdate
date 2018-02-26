#ifndef _AF_SESSLOCK_WRAPPER_H_
#define _AF_SESSLOCK_WRAPPER_H_
#include "afsessmgr.h"


enum 
{
    ERROR_AUTOSESSLOCK_ALREADYLOCKED    = 0xA10F0001,
};

class CAutoSessLock
{
public:
    explicit CAutoSessLock() 
        : m_p(NULL) 
        , m_bLocked(false)
    {
    };
    ~CAutoSessLock() 
    {
        UnLock();
        Release();
    };
public:
    DWORD Lock(PAFSESSLCK_PARAM pparam, vector<AFSESSLCK_ERR_ITEM> &vErrItem)
    {
        if (m_p == NULL)
            return E_INVALIDARG;

        if (m_bLocked)
            return ERROR_AUTOSESSLOCK_ALREADYLOCKED;

        DWORD dwRet = m_p->LckSess(pparam, vErrItem);
        if (dwRet == 0)
            m_bLocked = true;
        return dwRet;
    }

	DWORD LckFile(PAFLCK_FILE pLckFile, vector<AFSESSLCK_ERR_ITEM> &vErrItem)
	{
		if (m_p == NULL)
			return E_INVALIDARG;
		if (m_bLocked)
			return ERROR_AUTOSESSLOCK_ALREADYLOCKED;
		DWORD dwRet = m_p->LckFile(pLckFile, vErrItem);
		if (dwRet == 0)
			m_bLocked = true;
		return dwRet;
	}
    DWORD ExcludeLckFile(const wstring &strDest, const AF_FILE_ITEM &file)
    {
        if (m_p == NULL)
            return E_INVALIDARG;
        else
            return m_p->ExcludeLckFile(strDest, file);
    }

    void UnLock()
    {
        if (m_p && m_bLocked)
            m_p->UnlckSess();
        m_bLocked = false;
    }

    // The original will be unlocked and released
    void Attach(IAFSessLck* p) throw()
    {
        UnLock();
        Release();
        m_p = p;
    }

    IAFSessLck* Detach() throw()
    {
        IAFSessLck* pt = m_p;
        m_p = NULL;
        return pt;
    }
private:
    void Release()
    {
        if (m_p)
        {
            m_p->Release();
            m_p = NULL;
        }
    }
private:
    CAutoSessLock(const CAutoSessLock&); //forbidden
    CAutoSessLock& operator=(const CAutoSessLock&); //forbidden
private:
    IAFSessLck* m_p;
    bool        m_bLocked;
};

#endif //_AF_SESSLOCK_WRAPPER_H_
