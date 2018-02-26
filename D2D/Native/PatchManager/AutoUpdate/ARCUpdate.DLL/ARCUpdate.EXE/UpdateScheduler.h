#pragma once
#include "XXmlNode.h"
//
// this is a simple scheduler
//
class CUpdateScheduler : public CThreadBase
{
public:
	CUpdateScheduler( DWORD dwProd );
	 
	~CUpdateScheduler( );

public:
	virtual DWORD	Main( );

	virtual DWORD	Stop();

protected:
	BOOL	IsCanceled( DWORD dwTimeOut=0 );

protected:
	CDbgLog				m_log;
	DWORD				m_dwProduct;			// D2D or CPM
	HANDLE				m_hStopEvent;
};

