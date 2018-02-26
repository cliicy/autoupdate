#pragma once

#include <map>
#include <iostream>
#include "SQLiteAPI.h"
using namespace std;

typedef map<const char*, sqlite3_stmt*> StatementCache;

class D2D_SQLLITE_API SQLiteConnection
{

private:
	StatementCache  m_cachedStatements;
	sqlite3  *m_sqliteconnection;
	int StepStatement(sqlite3_stmt *stmt);
	int InitializeDataBase(DWORD dwCacheSize,DWORD dwPageSize);
	BOOL m_TransactionInProgress;
	
public:
	SQLiteConnection(void);
	~SQLiteConnection(void);
	int Close();
	int Open(const wchar_t *filename,DWORD dwDatabaseCreationOptions,
						   DWORD dwCacheSize,DWORD dwPageSize);
	sqlite3_stmt* GetCachedStatement(const char* sql);
	void Interrupt();
	LONGLONG LastInsertedId();
	int Execute(const char* sql);
	int BeginTransaction();
	int CommitTransaction();
	sqlite3* GetConnecttion() {return m_sqliteconnection;}
};

