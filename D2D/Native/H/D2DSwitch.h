/**
 * @file 
 * The header file provides APIs to get switch value.
 * @author xuvji01
 */

#pragma once
#include "SwitchDefines.h"
#include <string>
#include <vector>
using namespace std;

/** @defgroup SwitchAPI SwichAPI
 * Provide APIs to get switch value.
 * @{
 */

/**
 * @brief Get switch int value from the config ini file.
 * @param lpAppName The name of the section in the initialization file. Such as "MergeMgrDll.DLL".
 * @param lpKeyName The name of the key whose value is to be retrieved. Such as "Common.NewConsole".
 * @param nDefault A default value. If the function failed, the switch value is the default value.
 * @param lpConfFullPath The full path of the config ini file. If it is NULL, the file is "$D2D_InstallPath\Configuration\Switch.ini"
 * @return The switch int value.
 */
UINT WINAPI GetSwitchIntFromIni(LPCWSTR lpAppName, LPCWSTR lpKeyName, INT nDefault = 0, LPCWSTR lpConfFullPath = NULL);


/**
 * @brief Get switch string value from the config ini file.
 * @param lpAppName The name of the section in the initialization file. Such as "MergeMgrDll.DLL".
 * @param lpKeyName The name of the key whose value is to be retrieved. Such as "Common.NewConsole".
 * @param lpReturnedString A pointer to the buffer that receives the retrieved string.
 * @param nSize The size of the buffer pointed to by the lpReturnedString parameter, in characters.
 * @param lpDefault A default string. If the function failed, the switch value is the default value.
 * @param lpConfFullPath The full path of the config ini file. If it is NULL, the file is "$D2D_InstallPath\Configuration\Switch.ini"
 * @return 0-success, others-error.
 */
DWORD WINAPI GetSwitchStringFromIni(LPCWSTR lpAppName, LPCWSTR lpKeyName, LPWSTR lpReturnedString, DWORD nSize, LPCWSTR lpDefault = NULL, LPCWSTR lpConfFullPath = NULL);

/**
 * @brief Get switch DWORD value from register.
 * @param pszValueName The name of the registry value.
 * @param dwValue The data of the the registry value.
 * @param pszSubKeyName The name of the registry key. This key must be a subkey of the key "HKEY_LOCAL_MACHINE". It may be NULL or like: CST_REG_ROOT_L L"\\Debug",CST_REG_ROOT_L L"\\VssWrap". If it is NULL, it is CST_REG_ROOT_L.
 * @param dwDefault: A default value. If the function failed, the switch value is the default value.
 * @return 0-success, others-error.
 */
DWORD WINAPI GetSwitchDWORDFromReg(LPCWSTR pszValueName, DWORD& dwValue, LPCWSTR pszSubKeyName = NULL, DWORD dwDefault = 0);

/**
 * @brief Get switch string value from register.
 * @param pszValueName The name of the registry value.
 * @param pszValue A pointer to the buffer that receives the retrieved string.
 * @param pnChars The size of the buffer pointed to by the pszValue parameter, in characters.
 * @param pszSubKeyName The name of the registry key. This key must be a subkey of the key "HKEY_LOCAL_MACHINE". It may be NULL or like: CST_REG_ROOT_L L"\\Debug",CST_REG_ROOT_L L"\\VssWrap". If it is NULL, it is CST_REG_ROOT_L.
 * @param lpDefault A default string. If the function failed, the switch value is the default value.
 * @return 0-success, others-error.
 */
DWORD WINAPI GetSwitchStringFromReg(LPCWSTR pszValueName, LPWSTR pszValue, ULONG* pnChars, LPCWSTR pszSubKeyName = NULL, LPCWSTR lpDefault = NULL);

/**
 * @brief Get switch multiple string value from register.
 * @param pszValueName The name of the registry value.
 * @param vStr The vector receives the retrieved multiple strings.
 * @param pszSubKeyName The name of the registry key. This key must be a subkey of the key "HKEY_LOCAL_MACHINE". It may be NULL or like: CST_REG_ROOT_L L"\\Debug",CST_REG_ROOT_L L"\\VssWrap". If it is NULL, it is CST_REG_ROOT_L.
 * @return 0-success, others-error.
 */
DWORD WINAPI GetSwitchMultiStringFromReg(LPCWSTR pszValueName,vector<wstring> &vStr,LPCWSTR pszSubKeyName = NULL);


/**
 * @brief get the full path of the default config ini file: "$D2D_InstallPath\Configuration\Switch.ini".
 * @param pConfigPath A pointer to the buffer that receives the path.
 * @param dwLen The size of the buffer pointed to by the pConfigPath parameter.
 * @return 0-success, others-error.
 */
DWORD WINAPI GetDefaultConfigPathForD2D(wchar_t *pConfigPath, DWORD dwLen);

/** @} */