#pragma  once //d2dproxyiDef.h

//define
#define  D2D_PROXY_OBJECT_COMM	L"Global\\D2D_PROXY_{10791D2E-0F53-4182-A962-C65532ADFB3F}"

#define  D2D_PROXY_TIMEOUT		1000*360		//360 second
#define  D2D_PROXY_BUF_SIZE		1024*1024		//<sonmi01>2014-9-23 #backend c++ and proxy java IPC without new jvm //1M //32k
//named pipe 
#define  D2D_PROXY_IPC_PIPE_NAME_BASE	L"\\\\.\\pipe\\D2D-PROXY-{680249D0-C60F-48a0-9577-4B2707B463A7}"
#define  D2D_PROXY_NP_INS_NUMBER	1

//command response
#define  CMD_RESINVALID_DATA	L"INVALID DATA"