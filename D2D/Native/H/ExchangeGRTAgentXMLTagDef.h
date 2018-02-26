////////////////////////////////////////////////////////////////////////////////
// <XUVNE01> Created @ 2010-04-23
// Used for Exchange GRT Agent XML string tag defines.
////////////////////////////////////////////////////////////////////////////////

#pragma once


//Tags
#define EXGT_ROOT					L"ExGrtRestore"
#define EXGT_HEADER					L"header"
#define EXGT_VERSION				L"version"
#define EXGT_VERSION_VALUE			L"1.0"
#define EXGT_DEST					L"Destination"
#define EXGT_ACCOUNT				L"restoreaccount"
#define EXGT_PASSWD					L"password"
#define EXGT_TARGETSERVER			L"TargetServer"
#define EXGT_RESTOREITEMS			L"RestoredItems"
#define EXGT_RESTOREITEMS_ATTRIB_PATH			L"path"
#define EXGT_RESTOREITEMS_ATTRIB_NAMECOLLISION	L"NameCollision" // for name collision resolve option
#define EXGT_RESTOREITEMS_ATTRIB_PATH_NULL		L"0"
#define EXGT_RESTOREITEMS_ATTRIB_PATHTYPE		L"pathType"
#define EXGT_RESTOREITEMS_ATTRIB_PATHTYPE_DISK		L"disk"
#define EXGT_RESTOREITEMS_ATTRIB_PATHTYPE_SERVER	L"server"
#define EXGT_RESTOREITEMS_ATTRIB_PATHTYPE_ORIGINAL	L"original_server"
#define EXGT_RESTOREITEMS_ATTRIB_ISPUB			L"IsPub"
#define EXGT_RESTOREITEMS_ATTRIB_ISPUB_TRUE			L"TRUE"
#define EXGT_RESTOREITEMS_ATTRIB_ISPUB_FALSE		L"FALSE"
#define EXGT_ITEM					L"Item"
#define EXGT_ITEMTYPE				L"itemType"
#define EXGT_ITEMNAME				L"itemname"
#define EXGT_MAILBOXNAME			L"MailboxName"
#define EXGT_EXCHOBJIDS				L"ExchangeObjectIDs"
#define EXGT_DESC					L"description"
#define EXGT_SERVERVERSION			L"ServerVersion" // will be 2003/2007/2010

//Logs
#define EXGL_CANTCREATEELEMENT		L"GenerateExchGRTXML:: Cannot create XML element [%s]."
#define EXGL_CANTADDCHILDELEMENT	L"GenerateExchGRTXML:: Cannot add XML child element [%s]."
#define EXGL_CANTCREATEATTRIB		L"GenerateExchGRTXML:: Cannot create XML attrib [%s]."
#define EXGL_CANTADDCHILDATTRIB		L"GenerateExchGRTXML:: Cannot add XML attrib [%s]."

