/* ---------------------------------------------------------------------- */
/*               Proprietary and Confidential Information                 */
/*                                                                        */
/* Copyright (C) 2016 Arcserve, including its affiliates and subsidiaries.
All rights reserved.  Any third party trademarks or copyrights are the
property of their respective owners.                                      */
/*                                                                        */
/* ---------------------------------------------------------------------- */
/*  SCCS Module Control Stamp                                             */
/* ---------------------------------------------------------------------- */
/* This is the exposed api from the license / registration dll            */
/* ---------------------------------------------------------------------- */




#ifndef _LICREGAPI_H_
#define _LICREGAPI_H_

#ifdef		LICREG_EXPORTS
#define	LICREG_API __declspec(dllexport)
#else
#define	LICREG_API __declspec(dllimport)
#endif

#define SILENT_REGISTER_INI		TEXT("SilntReg.ini")
#define REMOTE_LIC_INI			TEXT("LicReg.ini")
#define INI_COMP_CODES			TEXT("ComponentCodes")
#define INI_VERSION				TEXT("Version")
#define INI_INSTALLTYPE			TEXT("InstallType")
#define INI_UPGRADE				TEXT("UpgradeCodes")
#define INI_LICENSE_KEYS		TEXT("LicenseKeys")
#define INI_REG_ID				TEXT("RegistrationIDs")
#define INI_GENERAL				TEXT("General")





// Return codes to be used for license check and registration

typedef enum {
	REG_ALREADY_LICENSED=5300,		// Component already licensed
	REG_ALREADY_REGISTERED=5301,	// Component already registered
	REG_BAD_CHECKSUM=5302,			// Invalid Checksum
	REG_BAD_KEY=5303,				// Invalid license key
	REG_BAD_REGID=5304,				// Invalid regid
	REG_CANCEL=5305,				// User Cancellation
	REG_CANT_OPEN=5306,				// Corrupt license/data file
	REG_CANT_READ=5307,				// Can't read license/data file
	REG_CANT_WRITE=5308,			// Can't write license file
	REG_DONT_ASK_AGAIN=5309,		// Never ask user to register
	REG_EXPIRED=5310,				// Component licensed, but expired
	REG_FAILURE=5311,				// Unexpected Error
	REG_FREQUENCY_ELAPSED=5312,		// Ask user to register
	REG_FREQUENCY_NOT_ELAPSED=5313,	// Don't ask user to register
	REG_NO_LICENSE_FILE=5314,		// Component not licensed
	REG_NO_MATCH=5315,				// License key string/vendor 
									// identifier not defined in
									// data file
	REG_NO_UPGRADE_LICENSE=5316,	// Upgrade component not licensed
	REG_PROXY_UNAVAILABLE=5317,		// Can't access proxy
	REG_REGISTRY_FAILURE=5318,		// Unable to access registry
	REG_SUCCESS=5319,				// Successfully created license
	REG_TRIAL_INSTALL=5320,			// Component installed as trial

	REG_INVALID_PARAMETER_1=5321,	//An invalid value as the first parameter
	REG_INVALID_PARAMETER_2=5322,	//An invalid value as the Second parameter
	REG_INVALID_PARAMETER_3=5323,	//An invalid value as the third parameter
	REG_INVALID_PARAMETER_4=5324,	//An invalid value as the four parameter
	REG_INVALID_PARAMETER_5=5326,	//An invalid value as the fifth parameter
	REG_INVALID_PARAMETER_6=5327,	//An invalid value as the sixth parameter
	REG_PROMPT_FOR_REG=5400,			//The user need to be prompted to register
	REG_COMP_REGISTRATION_FAILED= 5401,	//One of the components has failed to be registered correctly
	REG_KEY_IN_USE = 5402			//One of the keys that you are trying to use has already been used (INI File)
} REG_RETURN;


/*************************************************************************
ca_license_validation
Functions:	Detect presence of existing License File,
			Validate RegID & License Key,
			Create Feature Line(s) in OLF,
			and Update Registration Registry Keys

Outputs:	Licensed Product (continue install),
			Trial Mode (continue install) or 
			Cancellation (cancel install / continue running in Trial Mode)
***************************************************************************/
#ifdef __cplusplus
extern "C" {
#endif
LICREG_API  REG_RETURN ca_license_validation
(
 	//Components that need licensing
	IN OPTIONAL const char*						ComponentCodes,

	//Product version in Major.minor format
	IN OPTIONAL const char*						ProductVersion,
	
	//Components to upgrade a comma separated list of 
	//components to upgrade from
	IN OPTIONAL const char*						UpgradeComponentCodes
);





/***********************************************************************
ca_register_component
Functions:	Show the user the "Do you wish to register screen"
		if now then show the registration information entry screen

***********************************************************************/
LICREG_API  REG_RETURN ca_register_component
(
	//one or more component codes for the products being registered
	IN const char*	ComponentCodes,
	//Product version in Major.minor format
	IN const char*	ProductVersion
	);


/***********************************************************************
ca_register_now
Shows the user information entry screen.
return either REG_SUCCESS or REG_CANCEL
***********************************************************************/
LICREG_API  REG_RETURN ca_register_now
(
	//one or more component codes for the products being registered
	IN const char*	ComponentCodes,
	//Product version in Major.minor format
	IN const char*	ProductVersion
);


/***********************************************************************
ca_check_registered
Check to see if the product is licensed.
if the user selected never to register returns REG_DONT_ASK_AGAIN
if has the number of days since last prompted has expired returns REG_PROMPT_FOR_REG
else REG_SUCCESS
***********************************************************************/
LICREG_API  REG_RETURN ca_check_registered
(
	//one or more component codes for the products being registered
	IN const char* ComponentCodes,

	//Product version in Major.minor format
	IN const char*	ProductVersion
);



/***********************************************************************
ca_registration_set_never
For each component and children
set the registration option to "NEVER", if not already set
to 
***********************************************************************/
LICREG_API REG_RETURN ca_registration_set_never
(

	//one or more component codes for the products being registered
	IN const char* ComponentCodes,

	//Product version in Major.minor format
	IN const char* ProductVersion
);


/***********************************************************************
create_user_key
***********************************************************************/
LICREG_API REG_RETURN create_user_key
(
	//one for the products being registered
	IN	const char* CompCode,
	//Number of users for the license
	IN	long Users,
	//Detect the machine type
	IN	int UseDetectedModel
);


/***********************************************************************
create_license_ini
***********************************************************************/
LICREG_API REG_RETURN create_license_ini
(
 	//Components that need licensing
	IN const char*						ComponentCodes,

	//Product version in Major.minor format
	IN const char*						ProductVersion,
	
	//Where the ini file is to be written to
	IN const char*						strIniFileFolder,

	//Components to upgrade 
	IN OPTIONAL const char*				UpgradeComponentCodes
);


/***********************************************************************
use_license_ini
***********************************************************************/
LICREG_API REG_RETURN use_license_ini
(
 	
	//Where the ini file is to be read from
	IN const char*						strIniFileFolder,

	//is this to be done without prompting the user
	IN const BOOL						bSilent
);


/***********************************************************************
cmd_line_license
This is used from the small exe this parses the OLF file to get the
list of licensed components against these components the license keys
can be entered. Or they can add new ones There is no upgrade done the 
license keys are just checked for validity
***********************************************************************/
LICREG_API REG_RETURN cmd_line_license
(
);


/***********************************************************************
cmd_line_register
This is used from the small exe this parses the OLF file to get the
list of licensed components. It then scan the registry to find all the 
keys that have not been registered for these components. The list 
will show all components that have at least one key not registered.
***********************************************************************/
LICREG_API REG_RETURN cmd_line_register
(
);



/***********************************************************************
ca_license_remove
This delete the passed in components from this mahcine. It delete the
registry keys and the entries in the OLF file.
***********************************************************************/
LICREG_API REG_RETURN ca_license_remove
(
 	//Components that need Removing from the machine
	IN const char*						ComponentCodes
);



/************************************************************************************************************
ca_license_decode
·	LicenseKey = the 29 character RegIT 9 License
·	CreateLicenses = we will generate a license if set to non-zero, if 0, no license is created.
·	ProductVersion = Product version string in xxx.yyyy format like in ca_license_validation
·	IsUpgradeKey = return 1 if the LicenseKey is an upgrade key, 0 otherwise.
·	DataFilePath = absolute path of lic_comp_codes.dat - if Null, we try in InstallPathNew
·	ComponentCodes = pass in ptr to allocated memory (128 bytes is good), return list of components for this key.
·	Reserved = set to NULL

Return Values:
1. If success, return REG_SUCCESS
2. Else, return an intuitve REG_RETURN value
3. note: if CreateLicenses is set and the key is an upgrade key REG_INVALID_PARAMETER_2 will return as you cannot create licenses for an upgrade key here.

*************************************************************************************************************
| USER	        |  VERSION		|                COMMENT															|
*************************************************************************************************************
|smine02/lutge01|		2		| Request of BAB 11 Group															|
*************************************************************************************************************/
LICREG_API	REG_RETURN ca_license_decode
(	
	IN const char*	LicenseKey,
	IN int CreateLicense,
	IN char* ProductVersion,
    IN int* IsUpgradeKey,
    IN const char* DataFilePath,
    IN char* ComponentCodes,
    IN void* reserved
);

#ifdef __cplusplus
}
#endif

#endif //_LICREGAPI_H_
