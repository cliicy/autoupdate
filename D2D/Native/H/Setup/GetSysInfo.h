#ifndef _GETSYSINFO_H
#define _GETSYSINFO_H

#ifdef __cplusplus
extern "C" {
#endif  /* __cplusplus */

DWORD			ASetGetProgramFilesPath			(LPTSTR lpPath, LPDWORD lpdwSize, HKEY hHKEYLocalMachine /*= NULL*/);
typedef	DWORD	(*PFNASetGetProgramFilesPath)	(LPTSTR, LPDWORD, HKEY);

#ifdef __cplusplus
}
#endif

#endif  _GETSYSINFO_H