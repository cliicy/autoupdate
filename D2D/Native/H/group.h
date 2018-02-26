// crgroup.h : header file
#ifndef _GROUP_H_
#define _GROUP_H_

#ifdef __cplusplus
extern "C" {
#endif
BOOL	WINAPI GetCommonGroupPath	(OUT LPTSTR szCommonGroupPath);
BOOL	WINAPI GetASGroupName		(OUT LPTSTR szASGroupName);
BOOL	WINAPI CreateGroup			(IN LPTSTR szGroupName);
BOOL	WINAPI DeleteGroup			(IN LPTSTR szGroupName);
BOOL	WINAPI CreateShortcut		(IN LPTSTR szGroupName,
									 IN LPTSTR szDescription,
									 IN LPTSTR szTarget,
									 IN LPTSTR szArguments,
									 IN LPTSTR szWorkDir);
BOOL	WINAPI DeleteShortcut		(IN LPTSTR szGroupName,
									 IN LPTSTR szDescription);
BOOL	WINAPI DeleteGroupItems		(IN LPTSTR szGroupName);
BOOL	WINAPI IsUserLoggedOn		(void);
#ifdef __cplusplus
}
#endif

#define GROUP_NAME_LENGTH	128
#define	BUFFER_LENGTH		64
#endif //_GROUP_H_

