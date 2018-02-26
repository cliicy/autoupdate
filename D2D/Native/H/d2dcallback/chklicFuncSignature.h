#ifndef D2D_CHECK_LICENSE_chklicFuncSignature__h 
#define D2D_CHECK_LICENSE_chklicFuncSignature__h
#pragma once //chklicFuncSignature.h
//class for check license
#define  CLASSNAME_CHKLIS	"com/ca/arcflash/webservice/edge/licensing/LicenseUtils"


#define  FUN_CHKLIC_CREAET	"<init>"
#define  SIG_CHKLIC_CREAET	"()V"

#define FUN_CHKLIC_CHECK	"checkD2DLicenseFromEdge"
#define SIG_CHKLIC_CHECK	"(Lcom/ca/arcflash/webservice/edge/licensing/LicenseInfo;)J"

#define FUN_CHKLIC_ALLOCATE	"checkCentralLicenseFromEdge"
#define SIG_CHKLIC_ALLOCATE "(Lcom/ca/arcflash/webservice/edge/license/MachineInfo;JLcom/ca/arcflash/webservice/edge/licensing/ComponentInfo;)J"

#define  FUN_IS_UNDER_EDGE	"isManagedByEdge"
#define  SIG_IS_UNDER_EDGE	"()J"

 //class for license item
#define  CLASSNAME_LIC_ITEM	"com/ca/arcflash/webservice/edge/licensing/ComponentInfo"

#define  FUN_LICITEM_CREAET	"<init>"
#define  SIG_LICITEM_CREAET	"()V"

#define  FUN_LICITEM_RESERVED	"isReserved"
#define  SIG_LICITEM_RESERVED	"()Z"

#define  FUN_LICITEM_ERSULT	"getCheckResult"
#define  SIG_LICITEM_ERSULT	"()J"

#define  FUN_LICITEM_SETCOMID	"setComponentId"
#define  SIG_LICITEM_SETCOMID	"(J)V" 


#define  FUN_LICITEM_GETCOMID	"getComponentId"
#define  SIG_LICITEM_GETCOMID	"()J" 
			 
//class for license information
#define  CLASSNAME_LIC_LICINFO	"com/ca/arcflash/webservice/edge/licensing/LicenseInfo"

#define  FUN_LICINFO_CREAET	"<init>"
#define  SIG_LICINFO_CREAET	"()V"

#define  FUN_LICINFO_SETHOSTNAME	"setHostName"
#define  SIG_LICINFO_SETHOSTNAME	"(Ljava/lang/String;)V"

#define  FUN_LICINFO_SETPID			"setProcessId"
#define  SIG_LICINFO_SETPID			"(J)V"

#define  FUN_LICINFO_SETFLAGS		"setFlags"
#define  SIG_LICINFO_SETFLAGS		"(J)V"

#define  FUN_LICINFO_ADDCOMPONENT	"addComponentInfo"
#define  SIG_LICINFO_ADDCOMPONENT	"(Lcom/ca/arcflash/webservice/edge/licensing/ComponentInfo;)V"

#define  FUN_LICINFO_GETCOMLIST		"getComponentList"
#define  SIG_LICINFO_GETCOMLIST		"()Ljava/util/List;"

//////////////////////////////////////////////////////////////////////////////////////////

// machine information
#define CLASSNANME_MACHININFO		"com/ca/arcflash/webservice/edge/license/MachineInfo"

#define  FUN_MACHINEINFO_CREAET		"<init>"
#define  SIG_MACHINEINFO_CREAET		"()V"

#define  FUN_MACHINE_SETNAME		"setHostName"
#define  SIG_MACHINE_SETNAME		"(Ljava/lang/String;)V"

#define  FUN_MACHINE_SETSOCKETNUM	"setSocketCount"
#define  SIG_MACHINE_SETSOCKETNUM	"(I)V"


#define  FUN_LICINFO_SETJOBID		"setJobId"
#define  SIG_LICINFO_SETJOBID		"(J)V" 

#define  FUN_LICINFO_SETJOBTYPE		"setJobType"
#define  SIG_LICINFO_SETJOBTYPE		"(J)V"
//////////////////////////////////////////////////////////////////////////////////////////

#endif//D2D_CHECK_LICENSE_chklicFuncSignature__h
