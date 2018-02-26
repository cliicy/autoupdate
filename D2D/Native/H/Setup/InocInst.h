////////////////////////////////////////////////////////////////
// InocInst.h
//

#ifndef __INOC_INST_DEFINED__
#define __INOC_INST_DEFINED__

#include <windows.h>

////////////////////////////////////////////////////////////////
// Return codes for Install_InoculateIT
//

#define INOCINST_OK						0
#define INOCINST_ALREADY_INSTALLED		0xFFFF
#define INOCINST_FAILED					0xFFFE


////////////////////////////////////////////////////////////////
// Function Prototypes
//

////////////////////////////////////////////////////////////////
// Debug message callback
//
typedef void (_cdecl *FPINXINSTINOCMSG)(const char* szFormat);


////////////////////////////////////////////////////////////////
// Install function
//
DWORD INX_Install_InoculateIT (	const char* szSourcePath,
								const char* szInstallPath,
								FPINXINSTINOCMSG fpDbg
								);


#endif __INOC_INST_DEFINED__