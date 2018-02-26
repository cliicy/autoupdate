#pragma once
#include <Windows.h>

#define UDP_RESOURCE_DLL_NAME			L"UDPRes.dll"
/*********************************************************************************
*		Header:     UDPMessage.h
*		Library:	Log.lib
*		DLL:		Log.dll
*********************************************************************************/

//
// Function: FormatMessageW
// Parameters:
//		[IN]	 lid:				 the language ID. If lid is zero, will use default local to format the message
//      [IN/OUT] pszBuffer:			 the buffer to receive formatted message
//		[IN]     dwSizeInCharacters: size of the buffer in characters
//      [IN]     dwMessageID:		 The message identifier
// Return value:
//		If the function succeeds, return 0; Otherwise, return the windows error
//
DWORD WINAPI UDPFormatMessageW( LANGID lid, LPWSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, ... );
typedef DWORD (WINAPI* PFUNC_UDPFormatMessageW)( LANGID lid, LPWSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, ... );

//
// Function: FormatMessageA
// Parameters:
//		[IN]	 lid:				 the language ID. If lid is zero, will use default local to format the message
//      [IN/OUT] pszBuffer:			 the buffer to receive formatted message
//		[IN]     dwSizeInCharacters: size of the buffer in characters
//      [IN]     dwMessageID:		 The message identifier
// Return value:
//		If the function succeeds, return 0; Otherwise, return the windows error
//
DWORD WINAPI UDPFormatMessageA( LANGID lid, LPSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, ... );
typedef DWORD (WINAPI* PFUNC_UDPFormatMessageA)( LANGID lid, LPSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, ... );

//
// Function: UDPFormatMessage2W
// Parameters:
//		[IN]	 lid:				 the language ID. If lid is zero, will use default local to format the message
//      [IN/OUT] pszBuffer:			 the buffer to receive formatted message
//		[IN]     dwSizeInCharacters: size of the buffer in characters
//      [IN]     dwMessageID:		 The message identifier
// Return value:
//		If the function succeeds, return 0; Otherwise, return the windows error
//
DWORD WINAPI UDPFormatMessage2W( LANGID lid, LPWSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, va_list* pArgs );
typedef DWORD (WINAPI* PFUNC_UDPFormatMessage2W)( LANGID lid, LPWSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, va_list* pArgList );

//
// Function: UDPFormatMessage2A
// Parameters:
//		[IN]	 lid:				 the language ID. If lid is zero, will use default local to format the message
//      [IN/OUT] pszBuffer:			 the buffer to receive formatted message
//		[IN]     dwSizeInCharacters: size of the buffer in characters
//      [IN]     dwMessageID:		 The message identifier
// Return value:
//		If the function succeeds, return 0; Otherwise, return the windows error
//
DWORD WINAPI UDPFormatMessage2A( LANGID lid, LPSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, va_list* pArgs );
typedef DWORD (WINAPI* PFUNC_UDPFormatMessage2A)( LANGID lid, LPSTR pszBuffer, DWORD dwSizeInCharacters, DWORD dwMessageID, va_list* pArgList );

//
// Function: UDPLoadMessageW
// Parameters:
//		[IN]	 lid:				 the language ID. If lid is zero, will use default local to format the message
//      [IN]     dwMessageID:		 The message identifier
//      [IN/OUT] pszBuffer:			 the buffer to receive formatted message
//		[IN]     dwSizeInCharacters: size of the buffer in characters
// Return value:
//		If the function succeeds, return 0; Otherwise, return the windows error
//
DWORD WINAPI UDPLoadMessageW( LANGID lid, DWORD dwMessageID, LPWSTR pszBuffer, DWORD dwSizeInCharacters );
typedef DWORD (WINAPI* PFUNC_UDPLoadMessageW)( LANGID lid, DWORD dwMessageID, LPWSTR pszBuffer, DWORD dwSizeInCharacters );

//
// Function: UDPLoadMessageW
// Parameters:
//		[IN]	 lid:				 the language ID. If lid is zero, will use default local to format the message
//      [IN]     dwMessageID:		 The message identifier
//      [IN/OUT] pszBuffer:			 the buffer to receive formatted message
//		[IN]     dwSizeInCharacters: size of the buffer in characters
// Return value:
//		If the function succeeds, return 0; Otherwise, return the windows error
//
DWORD WINAPI UDPLoadMessageA( LANGID lid, DWORD dwMessageID, LPSTR pszBuffer, DWORD dwSizeInCharacters );
typedef DWORD (WINAPI* PFUNC_UDPLoadMessageA)( LANGID lid, DWORD dwMessageID, LPSTR pszBuffer, DWORD dwSizeInCharacters );