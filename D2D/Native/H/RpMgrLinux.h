#ifndef RECOVERY_POINT_MGR_LINUX_H
#define RECOVERY_POINT_MGR_LINUX_H
#include <windef.h>
enum UPDATE_NODE_TYPE{
	MERGE_SESSION = 1,
	REPLICATION_SESSION
};
extern "C" int CREAddNodeInfo(const wchar_t *pRootPath, DWORD dwSessNo, UPDATE_NODE_TYPE type);
extern "C" int CRERemoveNodeInfo(const wchar_t* pwcszDSPath, const wchar_t* pwcszNodeName);
#endif
