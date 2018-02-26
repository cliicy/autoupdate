#pragma once

class D2D_SQLLITE_API SqlLiteCommand
{

private:
	sqlite3_stmt*  m_BindStatement;
public:
	SqlLiteCommand(sqlite3_stmt*  statement);
	~SqlLiteCommand();
	int AddIntParam(int nIndex,int nValue);
	int AddInt64Param(int nIndex,LONGLONG llValue);
	int AddBooleanParam(int nIndex,BOOLEAN bValue);
	int AddTextParam(int nIndex,const void* szValue,int nTextSize);
	int AddBlobParam(int nIndex,const void* bValue,int nBlobSize);
	int ExecuteNonQuery();
	int Close();
};