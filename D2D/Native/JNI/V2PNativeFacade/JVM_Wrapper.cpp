#include "stdafx.h"
#include "windows.h"
#include "jni.h"

jobject _makeBoolean (JNIEnv *jenv, int val) {
	jclass classobj = jenv->FindClass ("java/lang/Boolean");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(Z)V");
	return jenv->NewObject (classobj, constructor, (jboolean) val);
}

jobject _makeByte (JNIEnv *jenv, int val) {
	jclass classobj = jenv->FindClass ("java/lang/Byte");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(B)V");
	return jenv->NewObject (classobj, constructor, (jbyte) val);
}

jobject _makeShort (JNIEnv *jenv, int val) {
	jclass classobj = jenv->FindClass ("java/lang/Short");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(S)V");
	return jenv->NewObject (classobj, constructor, (jshort) val);
}

jobject _makeInteger (JNIEnv *jenv, int val) {
	jclass classobj = jenv->FindClass ("java/lang/Integer");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(I)V");
	return jenv->NewObject (classobj, constructor, (jint) val);
}

jobject _makeLong (JNIEnv *jenv, long val) {
	jclass classobj = jenv->FindClass ("java/lang/Long");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(J)V");
	return jenv->NewObject (classobj, constructor, (jlong) val);
}

jobject _makeFloat (JNIEnv *jenv, float val) {
	jclass classobj = jenv->FindClass ("java/lang/Float");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(F)V");
	return jenv->NewObject (classobj, constructor, (jfloat) val);
}

jobject _makeDouble (JNIEnv *jenv, double val) {
	jclass classobj = jenv->FindClass ("java/lang/Double");
	jmethodID constructor = 
		jenv->GetMethodID (classobj, "<init>", "(D)V");
	return jenv->NewObject (classobj, constructor, (jdouble) val);
}

jstring _makeWCHARToJString( JNIEnv * env, LPWSTR str)
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


jobjectArray _makeNativeObjectToJniStringArrayArray(JNIEnv *env, long size,wchar_t** objHandles)
{
	jobjectArray ret;
	if(size > 0 )
	{
		ret= (jobjectArray)env->NewObjectArray(size,env->FindClass("java/lang/String"),env->NewStringUTF(""));
		
		if(ret==NULL)
			return NULL;
		for(int i=0;i<size;i++) 
		{
			jstring tempstring = _makeWCHARToJString(env,(LPWSTR)objHandles[i]);
			if(tempstring==NULL)
				return NULL;
			env->SetObjectArrayElement(ret ,i,tempstring);
		}
	}
	else
	{
		size = 1; //sending dummy class to JAVA, since it wont be used if numDisks is 0.
		ret= (jobjectArray)env->NewObjectArray(size,env->FindClass("java/lang/String"),env->NewStringUTF(""));		
		if(ret==NULL)
			return NULL;
		jstring tempstring = _makeWCHARToJString(env,L"");
		if(tempstring==NULL)
			return NULL;
		env->SetObjectArrayElement(ret ,0,tempstring);
	}
	return(ret);
}
