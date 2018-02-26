#include "stdafx.h"
#include "afdefine.h"
#include "drcore.h"
#include "afdefine.h"
#include "shprovd.tlh"
#include "XXmlNode.h"


#define CST_XML_FC_SETTINGS_ROOT     CST_PRODUCT_REG_ROOT_T L"\\FileCopy"



DWORD buildTreeXML(wstring nameXmlFile, CXXmlNode* pRes, wstring& inputValue, wstring& path);
void split_str(const std::wstring& str, wchar_t chSpliter, std::vector<std::wstring>& vecStrings);
DWORD getValuefromXML(wstring nameXmlFile, wstring completePath, wstring& outputValue);
DWORD setValueforXML(wstring nameXmlFile, wstring completePath, wstring inputValue);
DWORD GetDebugParamVal(PTCHAR szMachineName, PTCHAR szModuleName, PTCHAR szKeyName, PDWORD pdwValue, DWORD dwDefaultValue, BOOL bCreateKey = FALSE);
DWORD GetDebugParamString(PTCHAR szMachineName, PTCHAR szModuleName, PTCHAR szKeyName, PTCHAR pdwValue, PTCHAR dwDefaultValue, DWORD dwSize, BOOL bCreateKey = FALSE);
DWORD GetParamValFromConfigFile(PTCHAR szMachineName, PTCHAR szTaskId, PTCHAR szModuleName, PTCHAR szKeyName, PDWORD pdwValue, DWORD dwDefaultValue);
DWORD SetStringParamValInConfigFile(PTCHAR szMachineName, PTCHAR szTaskId, PTCHAR szModuleName, PTCHAR szKeyName, wstring szValue);

DWORD SetParamValInConfigFile(PTCHAR szMachineName, PTCHAR szTaskId, PTCHAR szModuleName, PTCHAR szKeyName, DWORD pdwValue);


