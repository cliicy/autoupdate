#pragma once

extern "C" DWORD GetARCFlashHomeDir(PTCHAR szARCFlashDir);
extern "C" DWORD GetFileCopyCatalogPath(PTCHAR szMachineName, DWORD dwProductType, PTCHAR szCatalogPath);
extern "C" DWORD GetARCFlashHomeDirByProductType(int nProductType, PTCHAR szARCFlashDir);