//<sonmi01>2014-7-15 ###???
#pragma once

#include <tchar.h>
#include <windows.h>
#include <stdlib.h>
#include <string.h>



namespace
{
	template<typename T>
	static VOID __DeepClone(T & x, CONST T y)
	{
		x = y;
	}

	template<typename T>
	static VOID __DeepFree(T & x)
	{
		x = 0;
	}

	template<>
	static VOID __DeepClone(LPTSTR & x, CONST LPTSTR y)
	{
		if (x)
		{
			free(x);
			x = NULL;
		}

		x = _wcsdup(y);
	}

	template<>
	static VOID __DeepFree(LPTSTR & x)
	{
		if (x)
		{
			free(x);
			x = NULL;
		}
	}
}


#define JS_CLONE(field)							__DeepClone((target).field, (source).field)

#define JS_CLONE_LIST(sss, count, plist)		if ((source).count && (source).plist) \
													{ \
															JS_CLONE(count); \
														  (target).plist = new sss[(target).count]; \
														  ZeroMemory((target).plist, sizeof(sss) * (target).count); \
														  for (ULONG ii = 0; ii < (ULONG)((target).count); ++ii) \
														  														  { \
															  sss::__Clone((target).plist[ii], (source).plist[ii]); \
														  														  } \
													}

#define JS_CLONE_POINTER(sss, pobj)				if((source).pobj) \
												{ \
													(target).pobj = new sss; \
													ZeroMemory((target).pobj, sizeof(sss)); \
													sss::__Clone((target).pobj[0], (source).pobj[0]); \
												}




#define JS_CLONE_OBJECT(sss, obj)				{ \
													ZeroMemory(&(target).obj, sizeof(sss)); \
													sss::__Clone((target).obj, (source).obj); \
												}


#define JS_FREE(field)							__DeepFree((target).field)

#define JS_FREE_LIST(sss, count, plist)			 if ((target).count && (target).plist) \
												  { \
													  for (ULONG ii = 0; ii < (ULONG)((target).count); ++ii) \
													  													  { \
														  sss::__Free((target).plist[ii]); \
													  													  } \
													  delete[] (target).plist; \
													  (target).plist = NULL; \
													   JS_FREE(count); \
												  }

#define JS_FREE_POINTER(sss, pobj)				if((target).pobj) \
												{ \
													sss::__Free((target).pobj[0]); \
													delete (target).pobj; \
													(target).pobj = NULL; \
												}


#define JS_FREE_OBJECT(sss, obj)				sss::__Free((target).obj);



#define JS_CLONE_FUNCTION(sss)						static VOID __Clone(sss & target, const sss & source)
#define JS_FREE_FUNCTION(sss)						static VOID __Free(sss & target)


/**************************************
JS_CLONE_FUNCTION(xxx)
{
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
	JS_CLONE(yyy);
}
**************************************/


