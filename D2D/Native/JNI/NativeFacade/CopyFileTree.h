#pragma once

#include <tchar.h>
#include <Windows.h>

HRESULT CopyFileTree(LPCTSTR pSourceDir, LPCTSTR pDestDir, LPCTSTR pFilePattern);