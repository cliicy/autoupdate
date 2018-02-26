#pragma once


//Tags
#define SP_ROOT									L"SPGrtRestore"
#define SP_HEADER								L"header"
#define SP_VERSION								L"version"
#define SP_VERSION_VALUE						L"1.0"
#define SP_DEST									L"Destination"
#define SP_ACCOUNT								L"restoreaccount"
#define SP_PASSWD								L"password"
#define	SP_OWNERLOGIN							L"OwnerLogin"
#define	SP_OWNEREMAIL							L"OwnerEmail"
#define SP_TARGETSERVER							L"TargetServer"
#define SP_RESTOREITEMS							L"RestoredItems"
#define	SP_RESTOREITEM							L"Item"
#define SP_ITEMNAME								L"itemname"
#define SP_ITEMDATA								L"itemdata"
#define	SP_DESTTYPE								L"DestType"	 
#define	SP_ATTRVAL_DESTTYPE_DISK				L"Disk"
#define	SP_ATTRVAL_DESTTYPE_FARM				L"Farm"
#define	SP_ATTRVAL_DESTTYPE_ORIGINAL			L"Original_Farm"

#define	SP_ATTRVAL_INCVER_ALL       			L"All"
#define	SP_ATTRVAL_INCVER_CURRENT      			L"CurrentVersion"
#define	SP_ATTRVAL_INCVER_LASTMAJOR    			L"LastMajor"
#define	SP_ATTRVAL_INCVER_LASTMAJORMINOR		L"LastMajorAndMinor"

#define SP_RESTOREITEMS_ATTRIB_PATH				L"path"
#define SP_RESTOREITEMS_ATTRIB_NAMECOLLISION	L"NameCollision" // for name collision resolve option
#define SP_RESTOREITEMS_ATTRIB_PATH_NULL		L"0"
#define SP_RESTOREITEMS_ATTRIB_PATHTYPE			L"pathType"
#define SP_RESTOREITEMS_ATTRIB_ID				L"Id"
#define SP_RESTOREITEMS_ATTRIB_TYPE				L"Type"
#define SP_RESTOREITEMS_ATTRIB_INCVERSIONS		L"IncludeVersions"




//Logs
#define SP_CANTCREATEELEMENT		L"GenerateSPGRTXMLString:: Cannot create XML element [%s]."
#define SP_CANTADDCHILDELEMENT		L"GenerateSPGRTXMLString:: Cannot add XML child element [%s]."
#define SP_CANTCREATEATTRIB			L"GenerateSPGRTXMLString:: Cannot create XML attrib [%s]."
#define SP_CANTADDCHILDATTRIB		L"GenerateSPGRTXMLString:: Cannot add XML attrib [%s]."

