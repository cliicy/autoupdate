#include <windows.h>
#include <openssl/ssl.h>    
#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <winsock.h> 

#ifndef THUMBPRINT_LENGTH
#define THUMBPRINT_LENGTH 60
#endif

#ifndef VCB_MAX_HOST_NAME
#define VCB_MAX_HOST_NAME 256
#endif


extern int GetHostThumbprintInfo(const std::wstring &serverurl, int port, std::wstring &rthumbprint);


