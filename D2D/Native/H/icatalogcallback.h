#ifndef D2D_EXCHANGE_GRT_iCatalogCallback__h 
#define D2D_EXCHANGE_GRT_iCatalogCallback__h
#pragma  once//iCatalogCallback.h
#include "comdef.h"

// generating catalog callback interface
class __declspec(uuid("{108AE73F-F7ED-4b54-AE84-AE3230CD5577}")) IGRTCallBack;

class IGRTCallBack : public IUnknown
{
public:
	//////////////////////////////////////////////////////////////////////////
	/* Function: GCInit()
	// Brief:
	//  Initialize the environment, this function will be called when entering 
	//  the API of generating catalog file
	// Parameter: None
	// Return Value: None
	*/
	//////////////////////////////////////////////////////////////////////////
	//virtual void __stdcall GCInit()   =0;

	//////////////////////////////////////////////////////////////////////////
	/* Function: GCUnInit()
	// Brief:
	//  Unnitialize the environment, this function will be called when leaving 
	//  the API of generating catalog file
	// Parameter: None
	// Return Value: None
	*/
	//////////////////////////////////////////////////////////////////////////
	//virtual void __stdcall GCUnInit() =0;

	//////////////////////////////////////////////////////////////////////////
	/* Function: UpdateMonitor()
	// Brief: 
	//  Notify the caller the steps of generating catalog file, catalog module 
	//	will take different response by detecing the return value
	// Parameter:
	//	nEventID -[in] Specifies the event to be notified, all the events are defined in enum_catalog_event. 
	//  wParam 	 -[in] Specifies additional event-specific information. 
	//	lParam   -[in] Specifies additional event-specific information. 
	// Return Value:
	//	 S_OK 
	*/
	//////////////////////////////////////////////////////////////////////////
	virtual HRESULT __stdcall UpdateMonitor(int nEventID, WPARAM wParam = NULL, LPARAM lParam =NULL )=0;

	//////////////////////////////////////////////////////////////////////////
	/* Function: IsCancelJob()
	// Brief:
	//   whether cancel the current job of generating catalog file 
	// Parameter: None
	// Return Value: 
	//	TRUE - cancel the job
	//	FALSE - continue the job
	*/
	//////////////////////////////////////////////////////////////////////////
	virtual BOOL __stdcall IsCancelJob() =0;

	//////////////////////////////////////////////////////////////////////////
	// Universal callback function
	virtual DWORD __stdcall OnMessage(int nMsg, WPARAM wParam = NULL, LPARAM lParam =NULL) =0;
};

#define  GC_EVENT_BASE 0x800
// GCE stands generating catalog event 
enum enum_catalog_event
{ 
	GRT_EVENT_MOUNT			= 1,
	GRT_EVENT_ESTIMATING	= 2,
	GRT_EVENT_TOTALMAILBOX	= 3,
	GRT_EVENT_MAILBOXNAME	= 4,
	GRT_EVENT_FOLDERNAME	= 5,
	GRT_EVENT_DISMOUNT		= 6,
	GRT_EVENT_GENERATE_INDEX= 7,
	GRT_EVENT_DEFRAGMENT	= 8 
}; 

 
/*
GCE_BEGIN	
	nEventID -	GCE_BEGIN
	wParam 	 -	invalid
	lParam   - 	invalid

GCE_END	
	nEventID - GCE_END
	wParam 	 -	invalid
	lParam   - 	invalid

GCE_MOUNTDRIVER	
	nEventID -	GCE_MOUNTDRIVER
	wParam 	 -	invalid
	lParam   - 	invalid

GCE_DISMOUNTDRIVER	
	nEventID -	GCE_DISMOUNTDRIVER
	wParam 	 -	invalid
	lParam   - 	invalid

GCE_OPENEDB	
	nEventID -	GCE_OPENEDB
	wParam 	 -	invalid
	lParam   - 	invalid

GCE_CLOSEEDB		
	nEventID -	GCE_CLOSEEDB
	wParam 	 -	invalid
	lParam   - 	invalid

GCE_ESTIMATE	
	nEventID -	GCE_ESTIMATE
	wParam 	 -	the mailbox number (int)
	lParam   - 	invalid

GCE_TRACE_MAILBOX	
	nEventID -	GCE_TRACE_MAILBOX
	wParam 	 -	the mailbox name (WCHAR *)
	lParam   - 	the size of(wParam)

GCE_TRACE_FOLDER	
	nEventID -	GCE_TRACE_FOLDER
	wParam 	 -	the folder name (WCHAR *)
	lParam   - 	the size of(wParam)


GCE_TRACE_MAIL			 
	 nEventID -	GCE_TRACE_MAIL
	 wParam	  -	the mail item name (WCHAR *)
	 lParam   - the size of(wParam)
*/

#define GRT_MESSAGE_BASE					0x100
#define GRT_MSG_FAILED_TO_OPEN_DATABASE		GRT_MESSAGE_BASE + 0x01
#define GRT_MSG_FAILED_TO_ATTACH_DATABASE    GRT_MESSAGE_BASE + 0x02

/*
GRT_MSG_FAILED_TO_OPEN_DATABASE
wParam [in] the return value when open database
return value: depend on the wParam
*/


#endif//D2D_EXCHANGE_GRT_iCatalogCallback__h