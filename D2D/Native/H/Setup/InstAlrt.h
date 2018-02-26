////////////////////////////////////////////////////////////////////////////////////////////
// InstAlrt.h - Alert Installation DLL specific definitions
//
// 9/2/97	-	John Gargiulo
//
// 8/17/98	-	John Gargiulo : 
// 4/8/99	-	John Gargiulo : Added support for service start type.

#ifndef __ALERTINSTALLDATA__DEFINED__
#define __ALERTINSTALLDATA__DEFINED__

////////////////////////////////////////////////////////////////////////////////////////////
// ALERTINSTALLDATA
//
// A pointer to this structure is passed to the Install function providing the necessary
// installation information. Not all of the fields are required and should be set to NULL
// to specify that they are not in use. (Tip: memset the struct with 0 before using). All
// of the optional members in this structure can be configured by the user after installation
// via the Alert Manager (ALADMIN.EXE)
//

// Impersonation members (optional)
// .........................................................................................
// These members of the structure are used to specify the account the service will use when
// it needs to make a connection accross the network. Since the service is running on the 
// LocalSystem account it has admin access to the local machine but cannot connect to any
// remote resources. Before attempting to connect to a remote resource the service needs to 
// "impersonate" a user on the local machine, after which it can connect to a remote resource
// using any account that has access. This account needs local "Log on as a service" and, if
// using MS Exchange, "Local Administrator" rights.

// Exchange members (optional)
// .........................................................................................
// These members specify the mail box and server for the MS Exchange server. They are also
// used for the MS Mail mailbox name. When specifying a mailbox for Exchange it is to be noted
// that the account for the mailbox must match the Impersonation information specified in the 
// impersonation members, otherwise the service will not be able to login to the Exchange mailbox.

// Lotus Notes members (optional)
// .........................................................................................
// If the user want's to use a different ID file than the one used by default when they run the
// Notes Client, then all of these members need to be filled in. Otherwise only the path and
// password members need to be populated. The ID file should be specified without any path and
// the Alert Service will look in the "Notes"\Data directory for it. The MailServer member should
// be formated the same way as the MailServer key in the NOTES.INI file, as should the MailFile
// member.

// Unicenter/TNG members (optional)
// .........................................................................................
// The world view username and password only need to be specified if the world view machine
// is the same as the machine where Alert is being installed.

typedef struct _ALERTINSTALLDATA
{
	DWORD		dwVersion;					// Structure version (set to sizeof(ALERTINSTALLDATA))
	const char *szMachine;					// Name of machine where install is to take place (NULL == Local)
	const char *szInstallPath;				// Installation Path
	const char *szRegisteredUser;			// Registered Username
	const char *szRegisteredCompany;		// Registered Organization
	const char *szImpersonationDomain;		// Domain name for Impersonation Account		(optional)
	const char *szImpersonationUser;		// User name for Impersonation Account			(optional)
	const char *szImpersonationPassword;	// Password for Impersonation Account			(optional)
	const char *szExchangeServer;			// Name of the Exchange Server to use			(optional)
	const char *szExchangeMailbox;			// Name of the Mailbox on the Exchange Server	(optional)
	const char *szNotesInstallPath;			// Path where Lotus Notes is installed			(optional)
	const char *szNotesPassword;			// Password for Notes ID file being used		(optional)
	const char *szNotesIDFile;				// ID file to use if not default ID	file		(optional)
	const char *szNotesMailServer;			// Mail server to use if not default ID file	(optional)
	const char *szNotesMailFile;			// Mail file to use if not default ID file		(optional)
	const char *szTNGEventMachine;			// Machine running Event Management Console		(optional)
	const char *szTNGWorldViewMachine;		// Machine running TNG World View repository	(optional)
	const char *szTNGWorldViewUser;			// Username to access TNG World View repository	(optional)
	const char *szTNGWorldViewPassword;		// Password to access TNG World View repository	(optional)
	DWORD		dwAutoStartService;			// 0 == Manual Start, 1 == Automatic Start

} ALERTINSTALLDATA, *LPALERTINSTALLDATA;


////////////////////////////////////////////////////////////////////////////////////////////
// ALERTUNINSTALLDATA
//
// A pointer to this structure is passed to the UnInstallEx function providing the necessary
// uninstall information. (Tip: memset the struct with 0 before using).
//
typedef struct _ALERTUNINSTALLEXDATA
{
	DWORD		dwVersion;					// Structure version (set to sizeof(ALERTUNINSTALLEXDATA))
	const char *szMachine;					// Name of machine where uninstall is to take place (NULL == Local)
} ALERTUNINSTALLEXDATA, *LPALERTUNINSTALLEXDATA;


#endif // __ALERTINSTALLDATA__DEFINED__