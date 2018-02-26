typedef DWORD    (*PFNCHECKCURRENTSTATEXML)( LPSTR, LPSTR, LPSTR, BOOL, BOOL);
typedef DWORD    (*PISPREREQPATCHINSTALLED)( LPSTR, LPSTR );
typedef DWORD    (*PISCURENTPATCHINSTALLED)( LPSTR, LPSTR );

//////////////////////////////////////////////////////////////////////////////////////
// Returning value of CheckCurrentStateXML()
//////////////////////////////////////////////////////////////////////////////////////
#define ERROR_XML_SUCCESS				0	
#define ERROR_XML_NOT_FOUND				1	// Cannot find the XML file
#define ERROR_XML_LOAD_FAILED			2	// Cannot load the XML file
#define ERROR_XML_PREREQ_NOT_INSTALLED	3	// The prerequiste is not instaleld
#define ERROR_XML_PATCH_INSTALLED		4	// The currrent patch is installed

BOOL CheckTraceFlag();

