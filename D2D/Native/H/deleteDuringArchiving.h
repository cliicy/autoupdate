#include <Windows.h>
#include "SQLiteConnection.h"
#include "SQLiteDataReader.h"
#include "SQLiteCommand.h"
#include "../AFArchiveDLL/ArchivePolicy.h"
#include "../AFArchiveDLL/AFDataSet.h"
#include <tchar.h>


#ifndef	ALIGNMENT
	#define ALIGNMENT(x, y) (((x) + (y) - 1) / (y) * (y))
#endif

struct databaseEntryStructure
{
	int rowID;
	LONGLONG jobID;
	FileNameRecord fileinfo;
	int   isDelete;
	LONGLONG   ErrorCode;
};
class deleteDuringArchiving
{
private:
	wstring m_dbName;
	DWORD m_dbPageSize;
	DWORD m_dbCacheSize;
	sqlite3_stmt *m_ADDInsertPartialStatement;
	SQLiteConnection* m_Dbconnection;
	CRITICAL_SECTION m_CriticalSection;

	DWORD SUCCESS(int dwError);
	

public:

	void Input();

	DWORD preFetch();
	DWORD write2Sqlite(int jobID, FileNameRecord *FNR);

	string CreateDeleteTableInfo;
	string ADDInsertPartialStatement;
	string sqlSelectItems;
	string commandCachetoUpdate;
	string  sqlEfficientQuery;


	SqlLiteCommand *m_Findstatment;
	sqlite3_stmt *m_QueryAllInformation;
	SqlLiteDataReader *m_ChunkReader;
	deleteDuringArchiving();
	~deleteDuringArchiving();
	DWORD updateEntry(int rowID, int isDelete, int ErrorCode);
	DWORD Init(PTCHAR szDBName, DWORD dwPageSize, DWORD dwCacheSize);
	void DeInit();


};