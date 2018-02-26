#pragma once
#include "jni.h"
#include "wchar.h"
#include "io.h"
#include "windows.h"
#include <string>
#include <vector>


using namespace std;
#define  SAFE_FREE(p)                     { if (p) { free(p); p=NULL;} }

PCHAR	WINAPI UnicodeToAnsi(PWCHAR ws, PCHAR s, UINT limit);
PWCHAR	WINAPI AnsiToUnicode(PCHAR s, PWCHAR ws, UINT limit);

int  WINAPI UnicodeToUTF8(LPCWSTR sourceStr, string &destStr);
int WINAPI UTF8ToUnicode(LPCSTR sourceStr, wstring &destStr);
wchar_t* JStringToWCHAR(JNIEnv* env, jstring str);
char* JStringToChar(JNIEnv *env, jstring str);
jstring WCHARToJString( JNIEnv* env, const wchar_t* str);
jstring CharToJString(JNIEnv* env, const char* pszChar);
wstring JStringToWString( JNIEnv *env, jstring str);
wchar_t* GetStringFromField(JNIEnv *env, jobject* jObj, jfieldID field);
jobject VList2JList(JNIEnv *env, const vector<wstring> &vList);
__time64_t JStringToTime64(JNIEnv* env, jstring jstr);
void jList2Vector(JNIEnv *env, jobject jlist, std::vector<std::wstring> &vec);

