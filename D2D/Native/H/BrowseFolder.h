#pragma once

#include "AFCoreAPIInterface.h"

#if 0 //<sonmi01>2013-3-22 #rps job monitor and eliminate the redefinitions
//typedef struct _FILE_INFO
//{
//   DWORD dwFileAttributes; //file attributes. defined in WIN32_FIND_DATAW
//   DWORD nFileSizeHigh;    //file size, defined in WIN32_FIND_DATAW
//   DWORD nFileSizeLow;     //file size, defined in WIN32_FIND_DATAW
//   FILETIME ftCreationTime; //file created time, defined in WIN32_FIND_DATAW
//   FILETIME ftLastAccessTime; //file access time, defined in WIN32_FIND_DATAW
//   FILETIME ftLastWriteTime;  //file modify time, defined in WIN32_FIND_DATAW
//   std::wstring strName;          //file name without path.
//   std::wstring strPath;          //file full path.
//
//}FILE_INFO, *PFILE_INFO;
//
//
//#define	FILELIST_BOTH			0
//#define	FILELIST_FOLDER_ONLY	1
//#define	FILELIST_FILE_ONLY		2
//
//class IFileListHandler
//{
//public:
//
//   virtual void Release() = 0;
//
//   /*
//   *Purpose: Get file list for specified folder.
//
//   *@vList: [output] container for file or folder information.
//
//   *@iType: [input] indicate what to retrieve. 0 means both files / folder; 1 means folder only; 2 mean file only
//
//   *@iNum: [input output] for input, it defines the number of files or folders which will be got.
//                          for input, if iNum is negative, all files and folders in specified folder will be returned,
//                          and iNum contains the number of total files and folders.
//                          for output, it returns the number of files or folders which are acctually got.
//
//   @strDir: [input] folder to traverse. If strDir is NULL, former folder will be used to continue traverse.
//
//   *Return: Zero for success. If fails, windows standard error code will be returned.
//
//   *Remarks: If return value is Zero, iNum contains the number of files or folders in specified folder.
//             If the specified folder doesn't contain files or folders any more after you call this function, 
//             and when you call this function again, ERROR_NO_MORE_ITEMS will be returned and iNum will be Zero.
//   */
//   virtual DWORD GetFileList(std::vector<FILE_INFO> &vList, int &iNum, const std::wstring &strDir) = 0;
//
//   virtual DWORD GetFileListEx(std::vector<FILE_INFO> &vList, int iType, int &iNum, const std::wstring &strDir) = 0;
//
//   virtual DWORD GetFileList(std::vector<FILE_INFO> &vList, int &iNum, const NET_CONN_INFO &info) = 0;
//
//   virtual DWORD GetFileListEx(std::vector<FILE_INFO> &vList, int iType, int &iNum, const NET_CONN_INFO &info) = 0;
//
//   /*
//   *@strDir: [input] Destination folder.
//   *Return: If folder contains backup data, TRUE will be returned. Otherwise, FALSE will be returned.
//   */
//   virtual BOOL CheckFolderContainBackups(const std::wstring &strDir) = 0;
//   
//   virtual BOOL CheckFolderIsSubFolderOfBackups(const std::wstring &strDir) = 0;
//};
//
//
//
//DWORD CreateIFileListHandler(IFileListHandler **ppIFileList);
#endif //<sonmi01>2013-3-22 #rps job monitor and eliminate the redefinitions