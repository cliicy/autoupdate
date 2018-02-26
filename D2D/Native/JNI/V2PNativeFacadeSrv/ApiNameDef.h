#pragma once

#ifndef ApiNameDef

#define ApiNameDef(x) static LPCSTR ApiName_##x = #x
#define ApiName(x) ApiName_##x

#define ModuleApiNameDef(m, x) static LPCSTR ApiName_##m_##x = #x
#define ModuleApiName(m, x) ApiName_##m_##x

#endif