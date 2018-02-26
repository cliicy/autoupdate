#ifndef CA_AD_RESOTE_ESEDATABASE_2009422__CaAdUnknown__H
#define CA_AD_RESOTE_ESEDATABASE_2009422__CaAdUnknown__H
//CaAdUnknown.h 

#define  _CAPUREM    =0   // pure method
#define  CASTDMETHOD(method)		virtual HRESULT	 __stdcall method
#define  CASTDMETHOD_(type,method)  virtual type __stdcall method
#define  CAMETHOD_(type,method)		type  method


#define CA_DEFINE_GUID(name, l, w1, w2, b1, b2, b3, b4, b5, b6, b7, b8) \
	EXTERN_C const GUID DECLSPEC_SELECTANY name \
	= { l, w1, w2, { b1, b2,  b3,  b4,  b5,  b6,  b7,  b8 } }

#define CA_DEFINE_IID(iface, uuid_string)	class __declspec(uuid(uuid_string)) iface

#define ca_uuidof(iface)	__uuidof(iface)
 
class CRefCounter
{
public:
	CRefCounter(void)
	{
		m_dwCount=0;
	};
	virtual ~CRefCounter(void){};
public:
	volatile LONG m_dwCount;
};
#define  CA_COMCOUNTERIMPL()	\
public:							\
	CRefCounter m_objCounter;	\
virtual ULONG __stdcall AddRef( void)	\
{										\
	ULONG ulRef =0;						\
	ulRef= InterlockedIncrement(&m_objCounter.m_dwCount);			  \
	return ulRef;	}					\
virtual ULONG __stdcall Release( void)	\
{										\
	ULONG ulRef =0;	  \
	ulRef =InterlockedDecrement(&m_objCounter.m_dwCount);			  \
	if(0==ulRef)						\
	{									\
		delete this;					\
		return 0;						\
	}									\
	return ulRef;}	

#define  CA_RELEASE_UNDEL() \
public:						\
virtual ULONG __stdcall Release( void){return S_OK;};\
virtual ULONG __stdcall AddRef( void){return S_OK;}	

#define CA_BEGIN_INTERFACE_MAP()					\
	HRESULT STDMETHODCALLTYPE						\
	QueryInterface(REFIID riid, void **ppv)			\
{													\
	if( riid == IID_IUnknown)						\
{													\
	*ppv = this;									\
	AddRef();										\
	return S_OK;									\
}

#define CA_INTERFACE(InterfaceName)					\
	else if( riid == ca_uuidof(InterfaceName) )		\
{													\
	*ppv = static_cast<InterfaceName*>(this);		\
	AddRef();										\
	return S_OK;									\
}

#define CA_END_INTERFACE_MAP()						\
	return E_NOINTERFACE;							\
}

//////////////////////////////////////////////////////////////////////////


 
#endif//CA_AD_RESOTE_ESEDATABASE_2009422__CaAdUnknown__H