#include "stdafx.h"
#include "windows.h"
#include "jni.h"


jobject _makeBoolean (JNIEnv *jenv, int val);
jobject _makeByte (JNIEnv *jenv, int val);
jobject _makeShort (JNIEnv *jenv, int val);
jobject _makeInteger (JNIEnv *jenv, int val);
jobject _makeLong (JNIEnv *jenv, long val);
jobject _makeFloat (JNIEnv *jenv, float val);
jobject _makeDouble (JNIEnv *jenv, double val);
jstring _makeWCHARToJString( JNIEnv * env, LPWSTR str);
jobjectArray _makeNativeObjectToJniStringArrayArray(JNIEnv *env, long size, wchar_t** objHandles);

#define SafeDeleteLocalRef(env, x) if (NULL != x) { env->DeleteLocalRef(x); }
