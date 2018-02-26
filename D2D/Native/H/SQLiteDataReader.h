#pragma once


class D2D_SQLLITE_API SqlLiteDataReader
{
private:
	sqlite3_stmt*  m_CachedStatement;
public:
	SqlLiteDataReader(sqlite3_stmt*  statement);
	~SqlLiteDataReader(void);
	int Close();
	BOOLEAN GetBoolean(int index);
	SHORT GetInt16(int index);
	int GetInt(int index);
	LONGLONG GetInt64(int index);
	TCHAR* GetText(int index,int& nTextSizeInBytes);
	const void* GetBlob(int index,int& nTextSizeInBytes);
	int GetFieldCount();
	BOOL Read();
};