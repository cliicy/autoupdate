#pragma once

#include "AFSessMgr.h"
#include "ICatalogMgrInterface.h"
#include "D2DErrorCode.h"

#define __USING_NEW_SESSION_LOCK__

//ZZ: By default we will wait session lock for 10 minutes            
#define DEFAULT_SESSION_LOCK_WAIT_TIME               60000
#define DEFAULT_SESSION_LOCK_WAIT_INTERVAL           60000
#define DEFAULT_SESSION_LOCK_RETRY_TIMES             10  
#define MERGE_SESSION_LOCK_RETRY_TIMES               3 

typedef std::vector<AFSESSLCK_ERR_ITEM> LockErrVector;

typedef enum
{
    ELT_UNLOCK = 0,
    ELT_SESS_LOCK,
    ELT_FILE_LOCK
}E_LOCK_TYPE;

class CLockComm
{
public:
    static wstring SessLockParamStr(PAFSESSLCK_PARAM pSessLckParam)
    {
        if (NULL == pSessLckParam)
            return L"";

        WCHAR wzTempBuf[MAX_PATH * 3] = {0};
        swprintf_s(wzTempBuf, L"Method=[%u], BKDest=[%s], Desc=[%s], WaitTime=[%u ms]",
            pSessLckParam->dwBakMethod,
            WS_S(pSessLckParam->strDest), 
            WS_S(pSessLckParam->strDesc), 
            pSessLckParam->dwWait);

        wstring wsParamStr = wzTempBuf;

        if (0 != pSessLckParam->vSessNo.size())
        {
            wsParamStr += L", SessNumList=[";
            WCHAR wzSessNumStr[MAX_PATH] = {0};
            for (UINT uiIdx = 0; uiIdx < pSessLckParam->vSessNo.size(); uiIdx++)
            {
                DWORD dwSessNum = pSessLckParam->vSessNo[uiIdx];
                if (0 != _ultow_s(dwSessNum, wzSessNumStr, 10))
                {
                    long lRetCode = GetLastError();    
                }
                else
                {
                    if (0 != uiIdx)
                        wsParamStr += L",";
                    wsParamStr += wzSessNumStr;
                }
            }
            wsParamStr += L"]";
        }

        return wsParamStr;
    }

    static wstring FileLockParamStr(PAFLCK_FILE pFileLckParam)
    {
        if (NULL == pFileLckParam)
            return L"";

        WCHAR wzTempBuf[MAX_PATH * 3] = {0};
        swprintf_s(wzTempBuf, L"Method=[%u], BKDest=[%s], Desc=[%s], WaitTime=[%u ms], ReserveTime=[%u ms]",
            pFileLckParam->dwBakMethod,
            WS_S(pFileLckParam->strDest), 
            WS_S(pFileLckParam->strDesc),
            pFileLckParam->dwWait, 
            pFileLckParam->dwReserveTime);

        wstring wsParamStr = wzTempBuf;

        for (UINT uiIdx = 0; uiIdx < pFileLckParam->vFile.size(); uiIdx++)
        {
            AF_FILE_ITEM& FileItem = pFileLckParam->vFile[uiIdx];
            wsParamStr += L", [";
            wsParamStr += FileItem.strFile;
            wsParamStr += L"]";
        }

        return wsParamStr;
    }
};

//
// This class is used for callback in lock function. But the class name is improper. :(
//
class ILockLogCallBack
{
public:
	virtual long LockLog(int iLoglevel, DWORD dwRetCode, const WCHAR* pwzLogFormat, ...)
	{
		// do nothing
		return 0;		
	}


	//
	// the function used to cancel lock process. 
	// This is to avoid lock function return in a long time even user canceld job from UI
	//
	virtual BOOL CancelLock( )
	{
		return FALSE;
	}
};

template<AF_OP_TYPE eOPType>
class CSessLockWrap
{
public:
    CSessLockWrap(bool bUnlockGuard = true, ILockLogCallBack* pLockLogCB = NULL)
        : m_pAFSessLock(NULL), 
          m_bUnlockGuard(bUnlockGuard), 
          m_eLockType(ELT_UNLOCK), 
          m_pLockLogCB(pLockLogCB)
    {
        if (NULL == m_pAFSessLock)
        {
            long lRetCode = CreateIAFSessLck(eOPType, &m_pAFSessLock);        
            if (0 != lRetCode)
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_ERR, lRetCode, L"Failed to create session lock object. (Type=[%u])", eOPType);
            }
            else
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_INF, 0, L"Succeed to create session lock object. (Type=[%u])", eOPType);
            }
        }
    }

    ~CSessLockWrap()
    {
        if (ELT_UNLOCK != m_eLockType)
            UnLock();

        if (m_pAFSessLock)
        {
            m_pAFSessLock->Release();
            m_pAFSessLock = NULL;
        }
    }

public:
    long LockFile(PAFLCK_FILE pLockParam, 
        bool bUnLockExist = true, 
        DWORD dwRetryTime = DEFAULT_SESSION_LOCK_RETRY_TIMES,
        DWORD dwRettryInterval = DEFAULT_SESSION_LOCK_WAIT_INTERVAL)
    {
        if (NULL == m_pAFSessLock)
            return D2DCOMM_E_POINTER_OBJECT_NOT_INIT;

        if (IsLocked())
        {
            if (!bUnLockExist || (ELT_FILE_LOCK != m_eLockType))
                return D2DCOMM_E_LOCK_USED_FOR_ANOTHER_SESS;

            UnLock();
        }

        wstring wsLockParam = CLockComm::FileLockParamStr(pLockParam);

        DWORD dwLockTimes = 1 + dwRetryTime;

        long lRetCode = 0;
        for (UINT uiIdx = 0; uiIdx < dwLockTimes; uiIdx++)
        {
			if( m_pLockLogCB && m_pLockLogCB->CancelLock() )
			{
				lRetCode = D2DCOMM_E_LOCK_CANCELED_BY_USER;
				m_pLockLogCB->LockLog(LL_WAR, 0, L"Lock canceled by user." );
				break;
			}

            vector<AFSESSLCK_ERR_ITEM> vecErrItem;
            lRetCode = m_pAFSessLock->LckFile(pLockParam, vecErrItem);
            if (0 != lRetCode)
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(lRetCode, L"Failed to lock file. (%s)", WS_S(wsLockParam));

                for (UINT uiIndex = 0; uiIndex < vecErrItem.size(); uiIndex++)
                {
                    AFSESSLCK_ERR_ITEM& ErrItem = vecErrItem[uiIndex];
                    if (m_pLockLogCB)
                    {
                        m_pLockLogCB->LockLog(LL_ERR, lRetCode, L"Process locking this file: (ProcID=[%u], Host=[%s\\%s], Usr=[%s], Desc=[%s], File=[%s])",
                            ErrItem.dwProcessId, WS_S(ErrItem.strDomain), WS_S(ErrItem.strComputer), 
                            WS_S(ErrItem.strUser), WS_S(ErrItem.strDesc), WS_S(ErrItem.strFile));
                    }
                }
            }
            else
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_INF, 0, L"Succeed to lock file. (%s)", WS_S(wsLockParam));

                m_eLockType = ELT_FILE_LOCK;
                break;
            }

            if ((0 == pLockParam->dwWait) && (0 != dwRettryInterval))
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_WAR, 0, L"Wait for %u ms to retry lock file.", dwRettryInterval);

				if( m_pLockLogCB && m_pLockLogCB->CancelLock() )
				{
					lRetCode = D2DCOMM_E_LOCK_CANCELED_BY_USER;
					m_pLockLogCB->LockLog(LL_WAR, 0, L"Lock canceled by user." );
					break;
				}

                Sleep(dwRettryInterval);

                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_WAR, 0, L"Begin to retry lock file.", dwRettryInterval);
            }
        }

        return lRetCode;
    }

	long LockSessEx(PAFSESSLCK_PARAM pLockParam, 
		vector<AFSESSLCK_ERR_ITEM>& vecErrItem,
		long& lExError,
        bool bUnLockExist = true, 
        DWORD dwRetryTime = DEFAULT_SESSION_LOCK_RETRY_TIMES,
        DWORD dwRettryInterval = DEFAULT_SESSION_LOCK_WAIT_INTERVAL)
	{
		lExError = 0;
		if (NULL == m_pAFSessLock)
            return D2DCOMM_E_POINTER_OBJECT_NOT_INIT;

        if (IsLocked())
        {
            if (!bUnLockExist || (ELT_SESS_LOCK != m_eLockType))
                return D2DCOMM_E_LOCK_USED_FOR_ANOTHER_SESS;

            UnLock();
        }

        wstring wsLockParam = CLockComm::SessLockParamStr(pLockParam);

        DWORD dwLockTimes = 1 + dwRetryTime;

        long lRetCode = 0;
		UINT uiIdx = 0;
		BOOL bLogActOnce = FALSE;
		BOOL bDeleteLck = FALSE;
        for (; uiIdx < dwLockTimes; uiIdx++)
        {
			if( m_pLockLogCB && m_pLockLogCB->CancelLock() )
			{
				lRetCode = D2DCOMM_E_LOCK_CANCELED_BY_USER;
				m_pLockLogCB->LockLog(LL_WAR, 0, L"Lock canceled by user." );
				break;
			}

			vecErrItem.clear();
            lRetCode = m_pAFSessLock->LckSess(pLockParam, vecErrItem);
            if (0 != lRetCode)
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_ERR, lRetCode, L"Failed to lock session. (%s)", WS_S(wsLockParam));

                bool bIsBKDestBroken = false;
                if (ERROR_LOCK_FAILED != lRetCode)
                {
                    if (m_pLockLogCB)
                        m_pLockLogCB->LockLog(LL_ERR, lRetCode, L"Some backup destination in chain is unavailable. (%s)", WS_S(wsLockParam));
                    bIsBKDestBroken = true;
                }

				typedef DWORD (*JOBLOADSTRING)( DWORD ResouceID, TCHAR* szBuf, DWORD dwBufCount);
				typedef DWORD (*LOGACTIVITY)(DWORD Flags, DWORD ResouceID, ...);
				HMODULE hMode = NULL;
				LOGACTIVITY pEActLog = NULL;
				JOBLOADSTRING pLoadStr = NULL;
				if(!bLogActOnce)
				{
					hMode = LoadLibrary(L"Log.dll");
					if(hMode)
					{
						pEActLog = (LOGACTIVITY)GetProcAddress(hMode, "LogActivity");
						pLoadStr = (JOBLOADSTRING)GetProcAddress(hMode, "JobLoadString");
					}
				}

				for (UINT uiIndex = 0; uiIndex < vecErrItem.size(); uiIndex++)
				{
					AFSESSLCK_ERR_ITEM& ErrItem = vecErrItem[uiIndex];
					if (m_pLockLogCB)
					{
						if (!bIsBKDestBroken)
						{
							m_pLockLogCB->LockLog(LL_ERR, lRetCode, L"Process locking this session: (ProcID=[%u], Host=[%s\\%s], Usr=[%s], Desc=[%s], File=[%s])",
								ErrItem.dwProcessId, WS_S(ErrItem.strDomain), WS_S(ErrItem.strComputer), 
								WS_S(ErrItem.strUser), WS_S(ErrItem.strDesc), WS_S(ErrItem.strFile));
						}
						else
							m_pLockLogCB->LockLog(LL_ERR, lRetCode, L"Backup destination is unavailable: %s", WS_S(ErrItem.strFile));
					}

					if(pEActLog&&pLoadStr)
					{
						DWORD dwResId;
						if(Operation2ResourceId(ErrItem.dwOp, dwResId))
						{
							WCHAR szMsg[512] = {0};
							DWORD dwRetE = pLoadStr(dwResId, szMsg, ARRAYSIZE(szMsg));
							if (dwRetE == 0 && !bLogActOnce)
							{
								pEActLog(AFWARNING, AFRES_COMMON_ERR_LOCK_FAILED, pLockParam->strDest.c_str(), ErrItem.strFile.c_str(), szMsg, ErrItem.strComputer.c_str(), ErrItem.dwProcessId);
								bLogActOnce = TRUE;
							}
						}
					}

					// if delete lock, just return, no need to retry 
					if (D2DCOMM_E_DEST_OR_SESS_UNDER_REMOVING == ErrItem.dwErrCode)
					{
						bDeleteLck = TRUE;
						break;
					}
				}

				if(hMode)
				{
					FreeLibrary(hMode);
				}

				if (bDeleteLck)
				{
					m_pLockLogCB->LockLog(LL_INF, 0, L"*****Delete lock exist, no retry need[%d]", uiIdx);
					break;
				}

				if (ERROR_LOGON_FAILURE == lRetCode)
				{
					// Incorrent Credential Information, No need to retry
                    if (m_pLockLogCB)
                    {
                        m_pLockLogCB->LockLog(LL_ERR, lRetCode, L"Incorrect credential information, no need to retry.");
					}
					break;
				}
            }
            else
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_INF, 0, L"Succeed to lock session. (%s)", WS_S(wsLockParam));

                m_eLockType = ELT_SESS_LOCK;
                break;
            }

            if ((0 == pLockParam->dwWait) && (0 != dwRettryInterval))
            {
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_WAR, 0, L"Wait for %u ms to retry lock session.", dwRettryInterval);

				if( m_pLockLogCB && m_pLockLogCB->CancelLock() )
				{
					lRetCode = D2DCOMM_E_LOCK_CANCELED_BY_USER;
					m_pLockLogCB->LockLog(LL_WAR, 0, L"Lock canceled by user." );
					break;
				}

                Sleep(dwRettryInterval);
                if (m_pLockLogCB)
                    m_pLockLogCB->LockLog(LL_WAR, 0, L"Begin to retry lock session.", dwRettryInterval);
            }
        }

		if(uiIdx == dwLockTimes && lRetCode!=D2DCOMM_E_LOCK_CANCELED_BY_USER )
		{
			lExError = ERROR_TIMEOUT;
		}
        return lRetCode;
	}

    long LockSess(PAFSESSLCK_PARAM pLockParam, 
        bool bUnLockExist = true, 
        DWORD dwRetryTime = DEFAULT_SESSION_LOCK_RETRY_TIMES,
        DWORD dwRettryInterval = DEFAULT_SESSION_LOCK_WAIT_INTERVAL)
    {
		vector<AFSESSLCK_ERR_ITEM> vecErrItem;
		long lExError = 0;
		return LockSessEx(pLockParam, vecErrItem, lExError, bUnLockExist, dwRetryTime, dwRettryInterval);
    }

    void UnLock()
    {
        if (IsLocked())
        {
            if (m_pAFSessLock)
                m_pAFSessLock->UnlckSess();

            m_eLockType = ELT_UNLOCK;
        }
    }

    void           SetUnLockGuard(bool bEnableGuard) { m_bUnlockGuard = bEnableGuard; }
    bool           IsLocked() const { return (ELT_UNLOCK != m_eLockType); }

private:
    bool              m_bUnlockGuard;
    IAFSessLck*       m_pAFSessLock;
    E_LOCK_TYPE       m_eLockType;
    ILockLogCallBack* m_pLockLogCB;
};

