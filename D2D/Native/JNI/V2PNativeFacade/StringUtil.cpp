#include "stdafx.h"
#include "StringUtil.h"
#include <time.h>

using namespace std;

PWCHAR WINAPI AnsiToUnicode(PCHAR s, PWCHAR ws, UINT limit)
{
	UINT   n = 0;
	PWCHAR p = ws;

	if (!ws)
		return ws;                          // Return NULL

	if (!s)
	{
		*ws = 0;                            // Return nul string
		return ws;
	}

	if (limit == 0)
	{                     // Use length of string
		if (limit = (UINT)strlen(s))
			limit++;                          // Incrememt for nul
		else
		{
			*ws = 0;                          // Return nul string
			return ws;
		}
	}

	//#ifdef _JAPANESE
	if (MultiByteToWideChar(CP_ACP, MB_PRECOMPOSED, s, -1, ws, limit))
		//#else
		//	if (mbstowcs(ws,s,limit) > 0)
		//#endif
		return ws;

	while ((n < limit) && (*s))
	{
		*p++ = *s++;
		n++;
	}
	*p = 0;
	return ws;
}

PCHAR  WINAPI UnicodeToAnsi(PWCHAR ws, PCHAR s, UINT limit)
{
	UINT  n = 0;
	//	PCHAR p = s;
	//	PCHAR p1;
	DWORD rc;

	if (!s)
		return s;                           // Return NULL

	if (!ws)
	{
		*s = 0;                             // Return nul string
		return s;
	}

	if (limit == 0)						// Use length of string
	{
		if (limit = (UINT)wcslen(ws))
			limit = limit*2 +1;
		else
		{
			*s = 0;                           // Return nul string
			return s;
		}
	}
	//	else
	//		limit--;

	if (WideCharToMultiByte(CP_ACP, WC_COMPOSITECHECK | WC_SEPCHARS,
		ws, -1, s, limit, NULL, NULL))
		return s;
	else
		rc = GetLastError();

	//	while ((n < limit) && (*ws))
	//	{
	//		p1 = (PCHAR)ws;
	//		*p++ = *p1;
	//		ws++;
	//		n++;
	//	}
	//	*p = '\0';
	return s;
}

int WINAPI UTF8ToUnicode(LPCSTR sourceStr, wstring &destStr)
{
	if(!sourceStr)
		return -1;

	int ret=0;
	wchar_t *strUnicode = NULL; 
	int  unicodeLen = MultiByteToWideChar(CP_UTF8,0,sourceStr,-1,NULL,0); 
	if(unicodeLen>0)
	{
		strUnicode = new wchar_t[unicodeLen+1];
		memset(strUnicode,0,(unicodeLen+1)*sizeof(wchar_t));
		if(MultiByteToWideChar(CP_UTF8,0,sourceStr,-1,strUnicode,unicodeLen)>0)
		{
			destStr = strUnicode;
		}
		else
			ret = GetLastError();
	}
	else
		ret = GetLastError();
	
	if(strUnicode)
		delete[] strUnicode;

	return ret;
}

int  WINAPI UnicodeToUTF8(LPCWSTR sourceStr, string &destStr)
{
	if(!sourceStr)
		return -1;
	
	int ret=0;
	int iTextLen;
	char *strUTF8 =NULL;
	iTextLen = WideCharToMultiByte(CP_UTF8,0,sourceStr,-1,NULL,0,NULL,NULL);
	if(iTextLen>0)
	{
		strUTF8  = new char[iTextLen+1];
		memset(strUTF8, 0, iTextLen + 1);
		if(WideCharToMultiByte(CP_UTF8,0,sourceStr,-1,strUTF8,iTextLen,NULL,NULL)>0)
		{
			destStr = strUTF8;
		}
		else
			ret = GetLastError();
	}
	else
		ret = GetLastError();

	if(strUTF8)
		delete[] strUTF8;

	return ret;
}
wchar_t* JStringToWCHAR( JNIEnv *env, jstring str){
	if (str == NULL) return NULL;

	const jchar* jchs = env->GetStringChars(str, NULL);// UCS-2 to Unicode;

	if (jchs == NULL) return NULL;

	size_t len = (wcslen((wchar_t*)jchs)+1) * sizeof(wchar_t);
	wchar_t *tmp = (wchar_t *) malloc(len);
	if(tmp)
	{
		memset(tmp, 0, len );
		wcscpy_s(tmp, len / sizeof(wchar_t), (wchar_t*)jchs);
	}

	env->ReleaseStringChars(str, jchs);

	return tmp;
}

wstring JStringToWString( JNIEnv *env, jstring str)
{
	if (str == NULL) return L"";

	const jchar* jchs = env->GetStringChars(str, NULL);// UCS-2 to Unicode;

	if (jchs == NULL) return L"";

	wstring wstr((wchar_t*)jchs);

	env->ReleaseStringChars(str, jchs);

	return wstr;

}

char* JStringToChar(JNIEnv *env, jstring str)
{
	if(str == NULL)
		return NULL;

	wchar_t* tmp = JStringToWCHAR(env, str);
	if(tmp == NULL)
		return NULL;

	size_t len = wcslen(tmp) + 1;

	char* ret = (char*)malloc(len);
	memset(ret,'0',len);

	if (WideCharToMultiByte(CP_ACP, WC_COMPOSITECHECK | WC_SEPCHARS,
		tmp, -1, ret , len, NULL, NULL))
	{
		if(tmp != NULL)
			free(tmp);
		return ret;
	}
	else
	{
		return NULL;
	}
}

jstring WCHARToJString( JNIEnv * env, const wchar_t* str)
{
	jstring rtn = 0;

	if (!env) 
		return 0;

	if (str == NULL) {
		char sTemp[2];
		strcpy_s(sTemp, "");
		rtn = env->NewStringUTF(sTemp);
		return rtn;
	}

	int slen = wcslen(str);

	rtn = env->NewString((jchar*)str, slen);

	return rtn;
}

#if 1
// str is UTF8 char string, do not use this function if str is not UTF8
jstring CharToJString(JNIEnv* env, const char* str)
{
	jstring rtn = 0;

	if (!env) 
		return 0;

	if (str == NULL) {
		char sTemp[2];
		strcpy_s(sTemp, "");
		rtn = env->NewStringUTF(sTemp);
		return rtn;
	}

	int slen = strlen(str);
	rtn = env->NewStringUTF(str);

	return rtn;
}
#else
// do not use
// the conversion uses default code page, may not be expected
jstring CharToJString(JNIEnv* env, const char* pszChar)
{
	jstring pJStr = NULL;
	wchar_t* pwszStr = NULL;

	do
	{
		if (NULL == env)
		{
			break;
		}

		if (NULL == pszChar)
		{
			pJStr = env->NewString((jchar*)L"", 0);
			break;
		}

		int ccWChar = MultiByteToWideChar(CP_ACP, 0, pszChar, -1, NULL, 0);
		pwszStr = (wchar_t*)malloc(sizeof(wchar_t)*(ccWChar + 1));
		if (NULL == pwszStr)
		{
			break;
		}
		ccWChar = MultiByteToWideChar(CP_ACP, 0, pszChar, -1, pwszStr, ccWChar+1);
		ccWChar = wcslen(pwszStr);
		pJStr = env->NewString((jchar*)pwszStr, ccWChar);
	} while (0);
	if (NULL != pwszStr)
	{
		free(pwszStr);
	}

	return pJStr;
}
#endif // #if 1

wchar_t* GetStringFromField(JNIEnv *env, jobject* jObj, jfieldID field)
{
	if (jObj == NULL) return NULL;

	jstring jstr = (jstring)env->GetObjectField(*jObj, field);

	wchar_t* tmp = JStringToWCHAR(env, jstr);
	if (tmp != NULL) {		
		size_t len =(wcslen(tmp)+1) * sizeof(wchar_t);
		wchar_t* destination = (wchar_t *) malloc(len);
		wcscpy_s(destination, len/sizeof(wchar_t), tmp);
		free(tmp);
		return destination;
	}
	return NULL;
}
jobject VList2JList(JNIEnv *env, const vector<wstring> &vList)
{
	jclass  class_ArrayList = env->FindClass("java/util/ArrayList");
	jmethodID arrayList_constructor = env->GetMethodID(class_ArrayList, "<init>", "()V");
	jmethodID id_ArrayList_add = env->GetMethodID(class_ArrayList, "add", "(Ljava/lang/Object;)Z");

	jobject result = env->NewObject(class_ArrayList, arrayList_constructor);		
	
	for(vector<wstring>::const_iterator itr = vList.begin(); itr != vList.end(); itr++)
	{
		jstring jstr = WCHARToJString(env, (wchar_t*)itr->c_str());

		env->CallBooleanMethod(result, id_ArrayList_add, jstr);

		if ( jstr!=NULL) env->DeleteLocalRef(jstr);
	}

	if (class_ArrayList!=NULL) env->DeleteLocalRef(class_ArrayList);

	return result;
}

__time64_t JStringToTime64(JNIEnv* env, jstring jstr)
{
	__time64_t tTime = 0;

	const jchar* pjchar = NULL;
	do
	{
		if (NULL == env || NULL == jstr)
		{
			break;
		}

		pjchar = env->GetStringChars(jstr, NULL);
		if (NULL == pjchar)
		{
			break;
		}

		struct tm tmTime = { 0 };
		int nItems = swscanf_s((wchar_t*)pjchar, L"%d-%d-%dT%d:%d:%dZ",
			&tmTime.tm_year, &tmTime.tm_mon, &tmTime.tm_mday,
			&tmTime.tm_hour, &tmTime.tm_min, &tmTime.tm_sec);
		if (6 != nItems)
		{
			break;
		}
		tmTime.tm_isdst = -1;
		tmTime.tm_year -= 1900;
		tmTime.tm_mon--;
		tTime = _mktime64(&tmTime);
	} while (0);

	if (NULL != env && NULL != jstr && NULL != pjchar)
	{
		env->ReleaseStringChars(jstr, pjchar);
	}
	return tTime;
}

void jList2Vector(JNIEnv *env, jobject jlist, std::vector<std::wstring> &vec)
{
	jclass Class_ArrayList = env->GetObjectClass(jlist);
	jmethodID mid_size = env->GetMethodID(Class_ArrayList, "size", "()I");
	jmethodID mid_get = env->GetMethodID(Class_ArrayList, "get", "(I)Ljava/lang/Object;");

	jint size = env->CallIntMethod(jlist, mid_size);
	for (jint i = 0; i < size; i++)
	{
		jstring jstr = (jstring) env->CallObjectMethod(jlist, mid_get, i);
		wchar_t* pvol = JStringToWCHAR(env, jstr);
		if (pvol != NULL)
		{
			std::wstring str = pvol;
			vec.push_back(str);
			free(pvol);
		}
		if (jstr != NULL){
			env->DeleteLocalRef(jstr);
		}
	}

	if (Class_ArrayList != NULL) env->DeleteLocalRef(Class_ArrayList);
}


