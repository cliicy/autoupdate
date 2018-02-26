#include "stdafx.h"
#include "NativeClassIntf.h"
#include "NativeClass.h"
#include "NativeClassRPC.h"

//methods for Native class client side
INativeClass* CNativeClassFactory::CreateInstanceNativeClassRPC()
{
	return new NativeClassRPC();
}


//methods for Native class
INativeClass* CNativeClassFactory::CreateInstanceNativeClass()
{
	return new NativeClass();
}

void CNativeClassFactory::SetJVM(INativeClass * pClass, void *pJVM)
{
	NativeClass * pNativeClass = dynamic_cast<NativeClass *>(pClass);
	if (NULL != pNativeClass)
	{
		pNativeClass->jvm = (JavaVM *)pJVM;
	}
}

void* CNativeClassFactory::GetJVM(INativeClass * pClass)
{
	void * pJvm = NULL;
	NativeClass * pNativeClass = dynamic_cast<NativeClass *>(pClass);
	if (NULL != pNativeClass)
	{
		pJvm = (void *)pNativeClass->jvm;
	}

	return pJvm;
}