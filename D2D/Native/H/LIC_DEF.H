/* *************************************************************************
 *
 *   $Workfile:   lic_def.h  $
 *     $Author:   MICHAELS  $
 *
 *   $Revision:   1.88  $
 *       $Date:   02 Mar 2001 15:14:04  $
 *
 *        $Log:   R:/as/my/h/LIC_DEF.HV_  $
 * 
 *    Rev 1.88   02 Mar 2001 15:14:04   MICHAELS
 * added PRODUCTTYPE_IBM3494_NT
 *
 *    Rev 1.87   27 Feb 2001 15:58:08   MICHAELS
 * added PRODUCTTYPE_MY_ETRUST_TSP_STR
 *
 *    Rev 1.86   27 Feb 2001 12:15:14   MICHAELS
 * changed defines:
 * PRODUCTTYPE_EMC_AGENT to PRODUCTTYPE_ORACLE_TIMEFINDER
 * PRODUCTTYPE_EMC_FASTRAX_ORACLE_AGENT to  PRODUCTTYPE_ORACLE_FASTRAX
 * PRODUCTTYPE_ORACLE_SERVERLESS_BK to PRODUCTTYPE_ORACLE_SERVERLESS
 *
 *    Rev 1.85   24 Jan 2001 11:00:28   MICHAELS
 * renamed PRODUCTTYPE_NAS to PRODUCTTYPE_CLIENTAGENT_NAS and
 * PRODUCTTYPE_EMC_SYMMETRIX to PRODUCTTYPE_CLIENTAGENT_SYMMETRIX
 *
 *    Rev 1.84   23 Jan 2001 14:10:08   MICHAELS
 * added PRODUCTTYPE_ARCSERVE_NT_XP
 *
 *    Rev 1.83   17 Jan 2001 16:52:34   MICHAELS
 * added PRODUCTTYPE_NAS and PRODUCTTYPE_EMC_SYMMETRIX
 *
 *    Rev 1.82   16 Jan 2001 18:03:34   MICHAELS
 * added PRODUCTTYPE_ORACLE_SERVERLESS_BK
 * and PRODUCTTYPE_EMC_FASTRAX_ORACLE_AGENT
 *
 *    Rev 1.81   05 Dec 2000 12:08:28   MICHAELS
 * added eTrust Suite SOHO Personal Edition and eTrust Suite SOHO Edition
 *
 *    Rev 1.80   29 Nov 2000 11:58:56   MICHAELS
 * added products:
 * - PRODUCTTYPE_MY_ETRUST_AV_PE,
 * - PRODUCTTYPE_MY_ETRUST_AV_SOHO,
 * - PRODUCTTYPE_MY_ETRUST_SB_PE,
 * - PRODUCTTYPE_MY_ETRUST_SB_SOHO,
 * - PRODUCTTYPE_MY_ETRUST_FW_PE,
 * - PRODUCTTYPE_MY_ETRUST_FW_SOHO.
 *
 *
 *    Rev 1.79   06 Nov 2000 16:00:56   MICHAELS
 * added:
 * PRODUCTTYPE_ETRUST_FW
 * PRODUCTTYPE_ETRUST_FW_PE
 * PRODUCTTYPE_ETRUST_CI_PE
 * PRODUCTTYPE_ETRUST_MW
 * PRODUCTTYPE_ETRUST_VPN
 * PRODUCTTYPE_ETRUST_ID
 * products
 *
 *    Rev 1.78   06 Sep 2000 16:38:18   MICHAELS
 * added PRODUCTTYPE_EMC_AGENT
 *
 *    Rev 1.77   31 Aug 2000 16:53:24   MICHAELS
 * added ARCserve options V7.0:
 * - RAID
 * - TLO
 * - DR
 * - GWA
 * - SAN
 *
 *    Rev 1.76   26 Jul 2000 15:37:48   MICHAELS
 * added products: Crossplatform 7.0, AS 7 for NW SB, SS, SE
 *
 *    Rev 1.75   30 Jun 2000 17:29:08   MICHAELS
 * added PRODUCTTYPE_ARCSERVE_70_NW
 *
 *    Rev 1.74   21 Jun 2000 15:35:00   MICHAELS
 * deleted PRODUCTTYPE_NWAGENT_70_NW, NW Agent 70 uses same
 * keycodes as 6.6
 *
 *    Rev 1.73   23 May 2000 11:54:38   MICHAELS
 * added PRODUCTTYPE_SERVERLESSBACKUP
 *
 *    Rev 1.72   22 May 2000 15:52:08   MICHAELS
 * added PRODUCTTYPE_NWAGENT_70_NW
 *
 *    Rev 1.71   06 Apr 2000 15:37:12   MICHAELS
 * added PRODUCTTYPE_ARCSERVE_66_NW_LITE
 *
 *    Rev 1.70   22 Feb 2000 11:12:04   MICHAELS
 * added PRODUCTTYPE_MCBSERVER
 *
 *
 *    Rev 1.69   16 Feb 2000 11:35:34   JOSE
 *
 *
 *    Rev 1.68   15 Feb 2000 12:47:56   MICHAELS
 * added PRODUCTTYPE_JASMINE
 *
 *
 *    Rev 1.67   21 May 1999 16:04:14   MICHAELS
 * added PRODUCTTYPE_SYMFOWARE_NT_STR
 *
 *    Rev 1.66   07 Apr 1999 15:19:54   MICHAELS
 * changed PRODUCTTYPE_FRITZ to PRODUCTTYPE_INFINIT
 * added RODUCTTYPE_DMAGENT_UNIX
 *
 *
 *
 *    Rev 1.65   Mar 23 1999 18:08:06   MICHAELS
 * merged with the NT group change
 * now PRODUCTTYPE_TNGFRAMEWORK_NT
 * and PRODUCTTYPE_FAXSERVE_EXCHANGE have the same code
 *
 *
 *    Rev 1.64   Mar 23 1999 16:25:12   MICHAELS
 * added PRODUCTTYPE_FAXSERVE_EXCHANGE
 * and PRODUCTTYPE_FRITZ
 *
 *    Rev 1.63   Oct 19 1998 17:03:12   MICHAELS
 * added product type PRODUCTTYPE_DMSTORAGES_NT
 *
 *    Rev 1.62   29 Sep 1998 16:56:44   PETERT
 * Add macro for checking AS6 NW SS
 *
 *    Rev 1.61   21 Sep 1998 12:50:40   MICHAELS
 * added product code PRODUCTTYPE_DMAGENT_NT
 *
 *    Rev 1.60   27 Aug 1998 18:17:12   PETERT
 * Change Cheyenne to CA
 *
 *    Rev 1.59   26 Aug 1998 12:22:02   MICHAELS
 * added prduct codes:
 * PRODUCTTYPE_ARCSERVE_66_NW_SS,
 * PRODUCTTYPE_TAPE_LIBRARY_66_NW,
 * PRODUCTTYPE_NWAGENT_66_NW;
 * changed:
 * PRODUCTTYPE_ARCSERVE_66_SB_NW to PRODUCTTYPE_ARCSERVE_66_NW_SB
 *
 *    Rev 1.58   Aug 21 1998 17:20:50   MICHAELS
 * added products PRODUCTTYPE_ARCSERVE_66_NW,
 * PRODUCTTYPE_ARCSERVE_66_SB_NW
 *
 *    Rev 1.58   Aug 21 1998 17:08:42   MICHAELS
 * added products PRODUCTTYPE_ARCSERVE_66_NW,
 * PRODUCTTYPE_ARCSERVE_66_NW
 *
 *    Rev 1.57   17 Aug 1998 11:17:20   HENRYD
 * Fixed licensing for Netware 5
 * site license should have the maximum # of available connections
 *
 *    Rev 1.56   11 Jun 1998 14:01:12   MICHAELS
 * added product Power Pack Option for Workgroup for Windows NT
 *
 *    Rev 1.56   11 Jun 1998 13:46:28   MICHAELS
 * added porduct Power Pack Option for Workgroup for Windows NT
 *
 *
 *    Rev 1.55   08 Jun 1998 17:27:16   MICHAELS
 * added Enterprise Library Option (NetWare)
 *
 *    Rev 1.54   05 Jun 1998 16:10:02   MICHAELS
 * added PRODUCTTYPE_SILO_NT,
 * changed codes for PRODUCTTYPE_ARCSERVE_UNIX and PRODUCTTYPE_UNIX_AGENT_NT
 *
 *    Rev 1.53   05 Jun 1998 12:24:42   MICHAELS
 * added ARCserve for UNIX and Client Agent for UNIX products
 *
 *    Rev 1.52   06 May 1998 10:48:00   MICHAELS
 * added PRODUCTSUBTYPE_...
 *
 *    Rev 1.51   14 Apr 1998 16:37:40   MICHAELS
 * removed ARCserve Replication (0x04E)
 *
 *    Rev 1.50   06 Apr 1998 15:13:18   CAREY
 * Merged changes from GO_SRVPAK5_FIX.
 *
 *    Rev 1.49   27 Mar 1998 18:44:50   MICHAELS
 * added BETA_GENERIC_LICENSE_STRING
 *
 *    Rev 1.48   27 Mar 1998 17:30:42   MICHAELS
 * added BETA_LICENCE
 *
 *    Rev 1.47   12 Mar 1998 18:24:20   MICHAELS
 * added PRODUCTTYPE_INOCULAN_INTERNET ptoduct code
 *
 *    Rev 1.46   11 Mar 1998 11:47:08   MICHAELS
 * added PRODUCTTYPE_EMAIL_GATEWAY_FAXSERVE product code
 *
 *    Rev 1.45   09 Mar 1998 15:52:56   MICHAELS
 * added PRODUCTTYPE_INGRES_NT
 *
 *    Rev 1.44   04 Mar 1998 18:26:52   MICHAELS
 * added stratification level USER_INOCULAN_NT_SB_5_UPG
 *
 *    Rev 1.43   27 Feb 1998 16:01:08   MICHAELS
 * added PRODUCTTYPE_OPTICAL_SERVER_UPG_10_15 product code
 *
 *    Rev 1.42   29 Jan 1998 14:37:46   MICHAELS
 * added USER_INOCULAN_NT_SB_25 staratification
 *
 *    Rev 1.41   27 Jan 1998 11:48:02   MICHAELS
 * added stratification code USER_INOCULAN_NT_SB
 *
 *    Rev 1.40   19 Jan 1998 15:57:18   MICHAELS
 * added Identica for ARCserve and
 * ARCserve for Windows NT - Small Buisness Server Edition
 * product types
 *
 *    Rev 1.39   08 Dec 1997 16:15:38   MICHAELS
 * merged PRODUCTTYPE_RP_32_NT_WORKSTATION and
 * PRODUCTTYPE_RP_32_WINDOWS_95 into PRODUCTTYPE_RP_32_WORKSTATION;
 * added PRODUCTTYPE_UNICENTER125_BACKUP and
 * Optical porduct codes
 *
 *    Rev 1.38   11 Nov 1997 17:54:24   MICHAELS
 * added Remotely Possible/32 Lite product code
 *
 *    Rev 1.37   10 Nov 1997 14:59:08   MICHAELS
 * added RP/32 product codes
 *
 *    Rev 1.36   04 Nov 1997 18:25:48   MICHAELS
 * deleted PRODUCTTYPE_DISASTER_REC_NT for John Santivasci,
 * PRODUCTTYPE_DM_OPTIONS_ARCSERVE_NT for Rohit Ghai
 *
 *    Rev 1.35   24 Oct 1997 15:52:56   MICHAELS
 * removed DM_..._AGENT, restored USER_012, 13, 14
 *
 *    Rev 1.34   10 Oct 1997 17:48:38   MICHAELS
 * added PRODUCTTYPE_INOCULAN_MANAGEWISE_BUNDLE product type
 *
 *    Rev 1.33   23 Sep 1997 14:28:50   MICHAELS
 * added PRODUCTTYPE_CLIENT_AGENT_WIN95, PRODUCTTYPE_DM_OPTIONS_ARCSERVE_NT product codes
 * changed USER_0012, 13, 14 to USER_DM_STORAGE, WORKSTATION, SERVER_AGENT
 * added SITE_FSERVE_LICENSE_STRING
 *
 *
 *    Rev 1.32   20 Aug 1997 17:22:46   gerardea
 * fix merging error.
 *
 *    Rev 1.31   20 Aug 1997 17:14:04   gerardea
 * Add upgrades for Single Server & Small Business
 *
 *    Rev 1.30   15 Aug 1997 18:30:24   MICHAELS
 * removed ARCSERVENT_SERVER for Mani Sinnappan
 *
 *    Rev 1.29   15 Aug 1997 18:07:42   MICHAELS
 * changed VERSION_4 to PSI_NDS, VERSION_5 to PSI_BINDERY.
 * added ARCserve Changer Option (Optical Jukebox) for Windows NT producr code.
 *
 *    Rev 1.28   08 Aug 1997 14:50:18   MICHAELS
 * added
 * DS Standard,
 * AuditWare,
 * Database Agent for Informix Online Server (Windows NT),
 * Database Agent for Tandem SWS (Windows NT),
 * Database Agent for Sybase Server (Windows NT)
 * product codes
 *
 *    Rev 1.27   31 Jul 1997 16:26:50   MICHAELS
 * added Replication Under NT product codes
 *
 *    Rev 1.26   11 Jul 1997 12:19:16   gerardea
 * fixed typo
 *
 *    Rev 1.25   11 Jul 1997 12:15:08   gerardea
 * Add ARCserve Single Server and Small Business product  types.
 *
 * *********************************************************************** */

#ifndef _LICENSE_DEFINITION
#define _LICENSE_DEFINITION

// userLevel Definitions
#define  USER_EVAL                              0x0000
#define  USER_LIVE                              0x0001
#define  USER_LIVE_60                           0x0002
#define  USER_LIVE_90                           0x0003
#define  USER_LIVE_180                          0x0004
#define  USER_SITE                              0x0005
#define  USER_3YEARS_NFR						0x0006

#define  USER_WORKGROUP                         0x000A
#define  USER_ENTERPRISE                        0x000B
#define  USER_UP_WORKGROUP_L25                  0x000C
#define  USER_UP_ENTERPRISE_L25                 0x000D
#define  USER_UP_ENTERPRISE_G25                 0x000E
#define  USER_UP_ENTERPRISE_WORKGROUP           0x000F
#define  USER_UP_TO_SINGLE_SERVER               0x0010
#define  USER_UP_TO_SMALL_BUSINESS              0x0011

#define  USER_0012                              0x0012
#define  USER_0013                              0x0013
#define  USER_0014                              0x0014
#define  USER_0015                              0x0015
#define  USER_0016                              0x0016
#define  USER_0017                              0x0017
#define  USER_0018                              0x0018
#define  USER_0019                              0x0019
#define  USER_001A                              0x001A
#define  USER_001B                              0x001B
#define  USER_001C                              0x001C
#define  USER_001D                              0x001D
#define  USER_001E                              0x001E
#define  USER_001F                              0x001F
#define  USER_0020                              0x0020
#define  USER_0021                              0x0021
#define  USER_0022                              0x0022
#define  USER_0023                              0x0023
#define  USER_0024                              0x0024
#define  USER_0025                              0x0025
#define  USER_0026                              0x0026
#define  USER_0027                              0x0027
#define  USER_0028                              0x0028
#define  USER_0029                              0x0029
#define  USER_002A                              0x002A
#define  USER_002B                              0x002B
#define  USER_002C                              0x002C
#define  USER_002D                              0x002D
#define  USER_002E                              0x002E
#define  USER_002F                              0x002F
#define  USER_0030                              0x0030


#define  USER_1                                 0x1200
#define  USER_5                                 0x12ab
#define  USER_10                                0x25ca
#define  USER_25                                0x780f
#define  USER_50                                0xef01
#define  USER_100                               0xee95
#define  USER_200                               0x50ee
#define  USER_250                               0x65ce
#define  USER_400                               0x51ca
#define  USER_500                               0x82ac
#define  USER_1000                              0xdee7
#define  USER_UNLIMITED                         0x4dda
#define  USER_FSERVE                            0xf2a9
#define  USER_INOCULAN_NT_SB                    0x0918
#define  USER_INOCULAN_NT_SB_25                 0x0908
#define  USER_INOCULAN_NT_SB_5_UPG              0x1228


// actual user number count for an unlimited license
#define  MAX_USER_COUNT    ULONG_MAX


// licenseVersion Definitions
#define  NORMAL_LICENSE                         0x0000
#define  VERSION_1                              0x0001
#define  VERSION_2                              0x0002
#define  VERSION_3                              0x0003
#define  PSI_NDS                                0x0004
#define  PSI_BINDERY                            0x0005
#define  VERSION_6                              0x0006
#define  VERSION_7                              0x0007
#define  VERSION_8                              0x0008
#define  VERSION_9                              0x0009
#define  VERSION_10                             0x000A
#define  BETA_LICENSE                           0x000B
#define  SITE_FSERVE_LICENSE                    0x7B55
#define  SITE_ARCSERVE_LICENSE                  0xB955
#define  BETA_ARCSERVE_LICENSE                  0xBBBB

// licenseVersionString Definitions (one char only)
#define  NORMAL_LICENSE_STRING                  "0"
#define  VERSION_1_STRING                       "1"
#define  VERSION_2_STRING                       "2"
#define  VERSION_3_STRING                       "3"
#define  PSI_NDS_STRING                         "4"
#define  PSI_BINDERY_STRING                     "5"
#define  VERSION_6_STRING                       "6"
#define  VERSION_7_STRING                       "7"
#define  VERSION_8_STRING                       "8"
#define  VERSION_9_STRING                       "9"
#define  VERSION_10_STRING                      "A"
#define  BETA_GENERIC_LICENSE_STRING            "B"
#define  SITE_FSERVE_LICENSE_STRING             "X"
#define  SITE_LICENSE_STRING                    "Y"
#define  BETA_LICENSE_STRING                    "Z"

// Definition of License Flags
#define LFLAG_VALIDATE_NLM_REQUIRED             0x00000001
#define LFLAG_PRIMARY_LICENSE_FILE              0x00000002
#define LFLAG_PRODUCT_SERIALNO_INVALID          0x00000004

// Definition of productTypeVersion Flags
#define LICENSE_CHEYENNE_VERSION                0x00000001
#define LICENSE_OEM_VERSION                     0x00000002
#define LICENSE_DEALER_EVAL_VERSION             0x00000004
#define LICENSE_LIVETRIAL_VERSION               0x00000008

//Definition of LanguageTypes
#define  LANGUAGE_ENGLISH                       0x00000000
#define  LANGUAGE_FRENCH                        0x00000001
#define  LANGUAGE_GERMAN                        0x00000002
#define  LANGUAGE_ITALIAN                       0x00000003
#define  LANGUAGE_SPANISH                       0x00000004
#define  LANGUAGE_JAPANESE                      0x00000005
#define  LANGUAGE_CHINESE                       0x00000006
#define  LANGUAGE_KOREAN                        0x00000007


//Definition of LanguageTypes
#define  LANGUAGE_ENGLISH_STR                   "0"
#define  LANGUAGE_FRENCH_STR                    "1"
#define  LANGUAGE_GERMAN_STR                    "2"
#define  LANGUAGE_ITALIAN_STR                   "3"
#define  LANGUAGE_SPANISH_STR                   "4"
#define  LANGUAGE_JAPANESE_STR                  "5"
#define  LANGUAGE_CHINESE_STR                   "6"
#define  LANGUAGE_KOREAN_STR                    "7"


// Definition of productTypes
#define PRODUCTTYPE_ARCSERVE                      0x00000001
#define PRODUCTTYPE_NWAGENT_NW                    0x00000002
#define PRODUCTTYPE_RAID_NW                       0x00000003
#define PRODUCTTYPE_CHANGER_NW                    0x00000004
#define PRODUCTTYPE_MACAGENT_NW_AS11_1            0x00000005
#define PRODUCTTYPE_NTAGENT_NW                    0x00000006
#define PRODUCTTYPE_SCOAGENT_NW                   0x00000007
#define PRODUCTTYPE_INTAGENT_NW                   0x00000008
#define PRODUCTTYPE_UWAGENT_NW                    0x00000009
#define PRODUCTTYPE_SUNAGENT_NW                   0x0000000A
#define PRODUCTTYPE_AIXAGENT_NW                   0x0000000B
#define PRODUCTTYPE_HPAGENT_NW                    0x0000000C
#define PRODUCTTYPE_DECAGENT_NW                   0x0000000D
#define PRODUCTTYPE_ANTIVIRUS_NW                  0x0000000E
#define PRODUCTTYPE_BTRIEVE_NW                    0x0000000F
#define PRODUCTTYPE_SQL_NW                        0x00000010
#define PRODUCTTYPE_ORACLE_NW                     0x00000011
#define PRODUCTTYPE_GUPTA_NW                      0x00000012
#define PRODUCTTYPE_SYBASE_NW                     0x00000013
#define PRODUCTTYPE_INOCULAN_NW                   0x00000014
#define PRODUCTTYPE_INOCULAN_W95                  0x00000015
#define PRODUCTTYPE_FAXSERVE_NW                   0x00000016
#define PRODUCTTYPE_JETSERVE                      0x00000017
#define PRODUCTTYPE_ARCSERVE_NT_EE                0x00000018
#define PRODUCTTYPE_ARCSERVE_NT_SS                0x00000019
#define PRODUCTTYPE_HSM_NW                        0x0000001A
#define PRODUCTTYPE_HSM_NT                        0x0000001B
#define PRODUCTTYPE_ANTIVIRUS_EXCHANGE_NT         0x0000001C
#define PRODUCTTYPE_ANTIVIRUS_LOTUSNOTES_NT       0x0000001D
#define PRODUCTTYPE_DISASTER_REC_NT               0x0000001E
#define PRODUCTTYPE_INOCULAN_NT                   0x0000001F
#define PRODUCTTYPE_ARCSERVE_NT_WS                0x00000020
#define PRODUCTTYPE_IMAGEBACKUP_NT                0x00000021
#define PRODUCTTYPE_RAID_NT                       0x00000022
#define PRODUCTTYPE_CHANGER_NT                    0x00000023
#define PRODUCTTYPE_SQL_NT                        0x00000025
#define PRODUCTTYPE_ORACLE_NT                     0x00000026
#define PRODUCTTYPE_SAPR3_NT                      0x00000027
#define PRODUCTTYPE_EXCHANGE_NT                   0x00000028
#define PRODUCTTYPE_LOTUSNOTES_NT                 0x00000029
#define PRODUCTTYPE_DISASTER_REC_NW               0x0000002A
#define PRODUCTTYPE_FAXSERVE_KAYAK                0x0000002B
#define PRODUCTTYPE_ANTIVIRUS_W95_CLIENT          0x0000002C
#define PRODUCTTYPE_JETSERVE_UPGRADE              0x0000002D
#define PRODUCTTYPE_CHEYENNE_DR_NT                0x0000002E
#define PRODUCTTYPE_OPENFILE_AGENT_NW             0x0000002F
#define PRODUCTTYPE_OPENFILE_AGENT_NT             0x00000030
#define PRODUCTTYPE_GROUPWISE_AGENT_NW            0x00000031
#define PRODUCTTYPE_ANTIVIRUS_AGENT_EXCHANGE      0x00000032
#define PRODUCTTYPE_FAXSERVE_NT                   0x00000033
#define PRODUCTTYPE_MBO_NT                        0x00000034
#define PRODUCTTYPE_ARCSERVE_NW_SS                0x00000035
#define PRODUCTTYPE_ARCSERVE_NW_SB                0x00000036
#define PRODUCTTYPE_REPLICATION_NT                0x00000037
#define PRODUCTTYPE_DS_STANDARD                   0x00000038
#define PRODUCTTYPE_AUDITWARE                     0x00000039
#define PRODUCTTYPE_INFORMIX_ONLINE_NT            0x0000003A
#define PRODUCTTYPE_TANDEM_SWS_NT                 0x0000003B
#define PRODUCTTYPE_SYBASE_NT                     0x0000003C
#define PRODUCTTYPE_CHANGER_OPTICAL_NT            0x0000003D
#define PRODUCTTYPE_CLIENT_AGENT_WIN95            0x0000003E
#define PRODUCTTYPE_INOCULAN_MANAGEWISE_BUNDLE    0x00000040
#define PRODUCTTYPE_DISTRIBUTED_OPTIONS_NT        0x00000041
#define PRODUCTTYPE_RP_32_NT_SERVER               0x00000042
#define PRODUCTTYPE_RP_32_WORKSTATION             0x00000043
#define PRODUCTTYPE_UNICENTER125_BACKUP           0x00000044
#define PRODUCTTYPE_RP_32_LITE                    0x00000045
#define PRODUCTTYPE_OPTICAL_SERVER                0x00000046
#define PRODUCTTYPE_CD_SERVER                     0x00000047
#define PRODUCTTYPE_FILE_JOCKEY                   0x00000048
#define PRODUCTTYPE_OPTICAL_CD_SERVER             0x00000049
#define PRODUCTTYPE_OPTICAL_SERVER_FILE_JOCKEY    0x0000004A
#define PRODUCTTYPE_CD_SERVER_FILE_JOCKEY         0x0000004B
#define PRODUCTTYPE_OPTICAL_CD_SERVER_FILE_JOCKEY 0x0000004C
#define PRODUCTTYPE_FILE_JOCKEY_UPG_10_20         0x0000004D
#define PRODUCTTYPE_ARCSERVE_NT_SB                0x0000004F
#define PRODUCTTYPE_OPTICAL_SERVER_UPG_10_15      0x00000050
#define PRODUCTTYPE_INGRES_NT                     0x00000051
#define PRODUCTTYPE_EMAIL_GATEWAY_FAXSERVE        0x00000052
#define PRODUCTTYPE_INOCULAN_INTERNET             0x00000053
#define PRODUCTTYPE_SILO_NT                       0x00000054
#define PRODUCTTYPE_ARCSERVE_UNIX                 0x00000055
#define PRODUCTTYPE_UNIX_AGENT_NT_AS11_1          0x00000056
#define PRODUCTTYPE_ENTERPRISE_LIBRARY_NW         0x00000057
#define PRODUCTTYPE_POWERPACK_WG_NT               0x00000058
#define PRODUCTTYPE_ARCSERVE_66_NW                0x00000059
#define PRODUCTTYPE_ARCSERVE_66_NW_SB             0x0000005A
#define PRODUCTTYPE_ARCSERVE_66_NW_SS             0x0000005B
#define PRODUCTTYPE_TAPE_LIBRARY_66_NW            0x0000005C
#define PRODUCTTYPE_NWAGENT_66_NW                 0x0000005D
#define PRODUCTTYPE_DMAGENT_NT                    0x0000005E
#define PRODUCTTYPE_DMSTORAGES_NT                 0x0000005F
#define PRODUCTTYPE_TNGFRAMEWORK_NT               0x00000060
#define PRODUCTTYPE_FAXSERVE_EXCHANGE             0x00000060
#define PRODUCTTYPE_INFINIT                       0x00000061
#define PRODUCTTYPE_DMAGENT_UNIX                  0x00000062
#define PRODUCTTYPE_SYMFOWARE_NT                  0x00000063
#define PRODUCTTYPE_JASMINE                       0x00000064
#define PRODUCTTYPE_MCBSERVER                     0x00000065
#define PRODUCTTYPE_ARCSERVE_66_NW_LITE           0x00000066
#define PRODUCTTYPE_CROSSPLATFORM_70              0x00000067
#define PRODUCTTYPE_SERVERLESSBACKUP              0x00000068
#define PRODUCTTYPE_ARCSERVE_70_NW                0x00000069
#define PRODUCTTYPE_ETRUST_FW                     0x0000006A
#define PRODUCTTYPE_ETRUST_FW_PE                  0x0000006B
#define PRODUCTTYPE_ETRUST_CI_PE                  0x0000006C
#define PRODUCTTYPE_ETRUST_MW                     0x0000006D
#define PRODUCTTYPE_ETRUST_VPN                    0x0000006E
#define PRODUCTTYPE_ETRUST_ID                     0x0000006F
#define PRODUCTTYPE_ARCSERVE_70_NW_SB             0x00000070
#define PRODUCTTYPE_ARCSERVE_70_NW_SS             0x00000071
#define PRODUCTTYPE_ARCSERVE_70_NW_SE             0x00000072
#define PRODUCTTYPE_GROUPWISE_AGENT_70_NW         0x00000073
#define PRODUCTTYPE_TAPE_LIBRARY_70_NW            0x00000074
#define PRODUCTTYPE_SAN_70_NW                     0x00000075
#define PRODUCTTYPE_RAID_70_NW                    0x00000076
#define PRODUCTTYPE_DISASTER_REC_70_NW            0x00000077
#define PRODUCTTYPE_ORACLE_TIMEFINDER             0x00000078
#define PRODUCTTYPE_MY_ETRUST_AV_PE               0x00000079
#define PRODUCTTYPE_MY_ETRUST_SUITE_PE            0x0000007A
#define PRODUCTTYPE_MY_ETRUST_SUITE_SOHO          0x0000007B
#define PRODUCTTYPE_ORACLE_SERVERLESS             0x0000007C
#define PRODUCTTYPE_ORACLE_FASTRAX                0x0000007D
#define PRODUCTTYPE_CLIENTAGENT_NAS_AS11_1        0x0000007E
#define PRODUCTTYPE_CLIENTAGENT_SYMMETRIX         0x0000007F
#define PRODUCTTYPE_MY_ETRUST_AV_SOHO             0x00000080
#define PRODUCTTYPE_MY_ETRUST_SB_PE               0x00000081
#define PRODUCTTYPE_MY_ETRUST_SB_SOHO             0x00000082
#define PRODUCTTYPE_MY_ETRUST_FW_PE               0x00000083
#define PRODUCTTYPE_MY_ETRUST_FW_SOHO             0x00000084
#define PRODUCTTYPE_ARCSERVE_NT_XP                0x00000085
#define PRODUCTTYPE_MY_ETRUST_TSP                 0x00000086
#define PRODUCTTYPE_IBM3494_NT                    0x00000087
#define PRODUCTTYPE_NWAGENT_90_NW				  0x00000088
#define PRODUCTTYPE_WINAGENT_90_NW				  0x00000089
#define PRODUCTTYPE_UNIXAGENT_90_NW				  0x0000008A
#define PRODUCTTYPE_GROUPWISE_AGENT_90_NW		  0x0000008B
#define PRODUCTTYPE_TAPE_LIBRARY_90_NW			  0x0000008C
#define PRODUCTTYPE_SAN_90_NW					  0x0000008D
#define PRODUCTTYPE_DISASTER_REC_90_NW			  0x0000008E
#define PRODUCTTYPE_IMAGE_90_NW					  0x0000008F
#define PRODUCTTYPE_ARCSERVE_90_NW				  0x00000090
#define PRODUCTTYPE_LINUX_AGENT_NT_AS11_1         0x00000091
#define PRODUCTTYPE_CLIENTAGENT_NW                0x00000092
#define PRODUCTTYPE_ARCSERVE_90_NW_SE			  0x00000093
#define PRODUCTTYPE_RAID_90_NW					  0x00000094
#define PRODUCTTYPE_OPENFILEAGENT_90_NW           0x00000095
#define PRODUCTTYPE_LINUXAGENT_90_NW			  0x00000096
#define PRODUCTTYPE_SQL_HITACHI_NT                0x00000097
#define PRODUCTTYPE_TIMEFINDER                    0x00000098
#define PRODUCTTYPE_SQL_HP_XP		              0x00000099
#define PRODUCTTYPE_EXCHANGE_ADDON_NT		      0x00000100
#define PRODUCTTYPE_VSS_HARDWARE_SNAPSHOT	      0x00000101
#define PRODUCTTYPE_ENTERPRISE_OPTION		      0x00000102
#define PRODUCTTYPE_VSS_AGENT_NT_AS11_1           0x00000110
#define PRODUCTTYPE_SHAREPOINT_NT				  0x00000118
#define PRODUCTTYPE_DISKSTAGING_NT				  0x00000119
#define PRODUCTTYPE_SHAREPOINT_AGENT			  0x00000120
#define PRODUCTTYPE_SCC_SERVER					  0x00000121
#define PRODUCTTYPE_CMO						      0x00000122
#define PRODUCTTYPE_VMWARE					      0x00000123
#define PRODUCTTYPE_SHAREPOINT_2007               0x00000124
#define PRODUCTTYPE_EXCHANGE					  0x00000125
#define PRODUCTTYPE_MSSQL						  0x00000126
#define PRODUCTTYPE_OPENFILE_AGENT_VM			  0x00000127		// Open File Agent for VMWare

#define PRODUCTTYPE_FILESERVER_SUITE_CLIENT	      0x00000200
#define PRODUCTTYPE_FILESERVER_SUITE_BASE	      0x00000201
#define PRODUCTTYPE_EMAILSERVER_SUITE	          0x00000202
#define PRODUCTTYPE_DBSERVER_SUITE	              0x00000203
#define PRODUCTTYPE_APPSERVER_SUITE	              0x00000204
#define PRODUCTTYPE_FILESERVER_SUITE_VM           0x00000205

#define PRODUCTTYPE_ARCSERVE_11_NW				  0x0000009A
#define PRODUCTTYPE_ARCSERVE_11_NW_SE			  0x0000009B
#define PRODUCTTYPE_DISASTER_REC_11_NW            0x0000009C
#define PRODUCTTYPE_RAID_11_NW                    0x0000009D
#define PRODUCTTYPE_IMAGE_11_NW					  0x0000009E
#define PRODUCTTYPE_TAPE_LIBRARY_11_NW			  0x0000009F
#define PRODUCTTYPE_SAN_11_NW					  0x00000104
#define PRODUCTTYPE_MYSQLAGENT_11_NW			  0x00000103
#define PRODUCTTYPE_OPENFILEAGENT_11_NW			  0x00000105
#define PRODUCTTYPE_GROUPWISE_AGENT_11_NW		  0x00000106
#define PRODUCTTYPE_WINAGENT_11_NW				  0x00000107
#define PRODUCTTYPE_NWAGENT_11_NW				  0x00000108
#define PRODUCTTYPE_UNIXAGENT_11_NW				  0x00000109
#define PRODUCTTYPE_LINUXAGENT_11_NW			  0x0000010A
#define PRODUCTTYPE_MACAGENT_11_NW				  0x0000010B

#define PRODUCTTYPE_NAS_UNIX					  0x0000010C
#define PRODUCTTYPE_ORACLE_UNIX					  0x0000010D
#define PRODUCTTYPE_SYBASE_UNIX					  0x0000010E
#define PRODUCTTYPE_INFORMIX_UNIX				  0x0000010F
#define PRODUCTTYPE_LOTUSNOTES_UNIX			      0x00000111
#define PRODUCTTYPE_DB2UDB_UNIX					  0x00000112
#define PRODUCTTYPE_INGRES_UNIX					  0x00000113
#define PRODUCTTYPE_APACHE_WEB_LINUX			  0x00000114
#define PRODUCTTYPE_MYSQL_LINUX					  0x00000115
#define PRODUCTTYPE_SAP_UNIX					  0x00000116
#define PRODUCTTYPE_CLIENTAGENT_AS400			  0x00000117

#define PRODUCTTYPE_CLIENTAGENT_NT_AS9			  0x00000400		// This product ID is for counting AS9 windows client agent only.
#define PRODUCTTYPE_UNIX_AGENT_NT_AS9			  0x00000401
#define PRODUCTTYPE_LINUX_AGENT_NT_AS9			  0x00000402

#define PRODUCTTYPE_CLIENTAGENT_NT                0x00000410		// This product ID is for counting AS12 windows client agent only.
#define PRODUCTTYPE_CLIENTAGENT_NT_AS11_5         0x00000403		// This product ID is for counting AS11.5 windows client agent only.
#define PRODUCTTYPE_CLIENTAGENT_NT_AS11_1         0x00000024		// This product ID is for counting AS11/AS11.1 windows client agent only.

#define PRODUCTTYPE_UNIX_AGENT_NT                 0x00000404
#define PRODUCTTYPE_LINUX_AGENT_NT                0x00000405
#define PRODUCTTYPE_MACAGENT_NW                   0x00000406
#define PRODUCTTYPE_VSS_AGENT_NT                  0x00000407
#define PRODUCTTYPE_CLIENTAGENT_NAS               0x00000408
#define PRODUCTTYPE_CLIENTAGENT_FREEBSD			  0x00000409
#define PRODUCTTYPE_DPM_AGENT_NT				  0x0000040A
#define PRODUCTTYPE_APM_NT						  0x00000500

#define PRODUCTTYPE_LIVETRIAL_NT                  0x000000FE
#define PRODUCTTYPE_LIVETRIAL_NW                  0x000000FF


// chets01: 10/9/2008
// Define new product ID for the r12.5 release server side licensing. For the future release such as r14, a new mapper should be defined.
// The define ID will be used in the following areas:
// 1. Counting mechanism: based upon the release version, it will use different product ID
// 2. Licensing checking: the new component codes should be added for the new product ID
// 3. License management dialog: it should management both r12.5 and r12 releases.
// 4. Server admin: it should display both r12.5 and r12 releases
// 5. License verification dialog, it should fill in the r12.5 product ID
// For the client side licensing such as BAOF, those components continue to use existing IDs, no change is required.


#define PRODUCTTYPE_MAPPER_12_5						0x00001000
#define PRODUCTTYPE_BRIGHTSTOR_PRODUCTS				0x00000FFF

#define PRODUCTTYPE_ARCSERVE_NT_EE_12_5				(PRODUCTTYPE_ARCSERVE_NT_EE | PRODUCTTYPE_MAPPER_12_5)	
#define PRODUCTTYPE_IMAGEBACKUP_NT_12_5				(PRODUCTTYPE_ARCSERVE_NT_EE | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_RAID_NT_12_5					(PRODUCTTYPE_RAID_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CHANGER_NT_12_5					(PRODUCTTYPE_CHANGER_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CLIENTAGENT_NT_12_5				(PRODUCTTYPE_CLIENTAGENT_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_SQL_NT_12_5						(PRODUCTTYPE_SQL_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_ORACLE_NT_12_5					(PRODUCTTYPE_ORACLE_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_SAPR3_NT_12_5					(PRODUCTTYPE_SAPR3_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_EXCHANGE_NT_12_5				(PRODUCTTYPE_EXCHANGE_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_LOTUSNOTES_NT_12_5				(PRODUCTTYPE_LOTUSNOTES_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_DISASTER_REC_NT_12_5			(PRODUCTTYPE_DISASTER_REC_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CHEYENNE_DR_NT_12_5				(PRODUCTTYPE_CHEYENNE_DR_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CHANGER_OPTICAL_NT_12_5			(PRODUCTTYPE_CHANGER_OPTICAL_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_INFORMIX_ONLINE_NT_12_5			(PRODUCTTYPE_INFORMIX_ONLINE_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_SYBASE_NT_12_5					(PRODUCTTYPE_SYBASE_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_DISTRIBUTED_OPTIONS_NT_12_5		(PRODUCTTYPE_DISTRIBUTED_OPTIONS_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_OPENFILE_AGENT_NT_12_5			(PRODUCTTYPE_OPENFILE_AGENT_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_OPENFILE_AGENT_VM_12_5			(PRODUCTTYPE_OPENFILE_AGENT_VM | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_UNIX_AGENT_NT_12_5				(PRODUCTTYPE_UNIX_AGENT_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_SERVERLESSBACKUP_12_5			(PRODUCTTYPE_SERVERLESSBACKUP | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_ARCSERVE_NT_XP_12_5				(PRODUCTTYPE_ARCSERVE_NT_XP | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CLIENTAGENT_NAS_12_5			(PRODUCTTYPE_CLIENTAGENT_NAS | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_IBM3494_NT_12_5					(PRODUCTTYPE_IBM3494_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_LINUX_AGENT_NT_12_5				(PRODUCTTYPE_LINUX_AGENT_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_EXCHANGE_ADDON_NT_12_5			(PRODUCTTYPE_EXCHANGE_ADDON_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_MACAGENT_NW_12_5				(PRODUCTTYPE_MACAGENT_NW | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_VSS_HARDWARE_SNAPSHOT_12_5		(PRODUCTTYPE_VSS_HARDWARE_SNAPSHOT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_ENTERPRISE_OPTION_12_5			(PRODUCTTYPE_ENTERPRISE_OPTION | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_VSS_AGENT_NT_12_5				(PRODUCTTYPE_VSS_AGENT_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_SHAREPOINT_NT_12_5				(PRODUCTTYPE_SHAREPOINT_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_DISKSTAGING_NT_12_5				(PRODUCTTYPE_DISKSTAGING_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_ORACLE_UNIX_12_5				(PRODUCTTYPE_ORACLE_UNIX | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CLIENTAGENT_AS400_12_5			(PRODUCTTYPE_CLIENTAGENT_AS400 | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CLIENTAGENT_FREEBSD_12_5		(PRODUCTTYPE_CLIENTAGENT_FREEBSD | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_DPM_AGENT_NT_12_5				(PRODUCTTYPE_DPM_AGENT_NT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_CMO_12_5						(PRODUCTTYPE_CMO | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_VMWARE_12_5						(PRODUCTTYPE_VMWARE | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_FILESERVER_SUITE_CLIENT_12_5	(PRODUCTTYPE_FILESERVER_SUITE_CLIENT | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_FILESERVER_SUITE_BASE_12_5		(PRODUCTTYPE_FILESERVER_SUITE_BASE | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_EMAILSERVER_SUITE_12_5			(PRODUCTTYPE_EMAILSERVER_SUITE | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_DBSERVER_SUITE_12_5				(PRODUCTTYPE_DBSERVER_SUITE | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_APPSERVER_SUITE_12_5			(PRODUCTTYPE_APPSERVER_SUITE | PRODUCTTYPE_MAPPER_12_5)
#define PRODUCTTYPE_FILESERVER_SUITE_VM_12_5		(PRODUCTTYPE_FILESERVER_SUITE_VM | PRODUCTTYPE_MAPPER_12_5)


// Definition of productTypeStrings
#define PRODUCTTYPE_ARCSERVE_NW_STR                   "001"
#define PRODUCTTYPE_NWAGENT_NW_STR                    "002"
#define PRODUCTTYPE_RAID_NW_STR                       "003"
#define PRODUCTTYPE_CHANGER_NW_STR                    "004"
#define PRODUCTTYPE_MACAGENT_NW_STR                   "005"
#define PRODUCTTYPE_NTAGENT_NW_STR                    "006"
#define PRODUCTTYPE_SCOAGENT_NW_STR                   "007"
#define PRODUCTTYPE_INTAGENT_NW_STR                   "008"
#define PRODUCTTYPE_UWAGENT_NW_STR                    "009"
#define PRODUCTTYPE_SUNAGENT_NW_STR                   "00A"
#define PRODUCTTYPE_AIXAGENT_NW_STR                   "00B"
#define PRODUCTTYPE_HPAGENT_NW_STR                    "00C"
#define PRODUCTTYPE_DECAGENT_NW_STR                   "00D"
#define PRODUCTTYPE_ANTIVIRUS_NW_STR                  "00E"
#define PRODUCTTYPE_BTRIEVE_NW_STR                    "00F"
#define PRODUCTTYPE_SQL_NW_STR                        "010"
#define PRODUCTTYPE_ORACLE_NW_STR                     "011"
#define PRODUCTTYPE_GUPTA_NW_STR                      "012"
#define PRODUCTTYPE_SYBASE_NW_STR                     "013"
#define PRODUCTTYPE_INOCULAN_NW_STR                   "014"
#define PRODUCTTYPE_INOCULAN_W95_STR                  "015"
#define PRODUCTTYPE_FAXSERVE_NW_STR                   "016"
#define PRODUCTTYPE_JETSERVE_STR                      "017"
#define PRODUCTTYPE_ARCSERVE_NT_EE_STR                "018"
#define PRODUCTTYPE_ARCSERVE_NT_SS_STR                "019"
#define PRODUCTTYPE_HSM_NW_STR                        "01A"
#define PRODUCTTYPE_HSM_NT_STR                        "01B"
#define PRODUCTTYPE_AV_EXCHANGE_NT_STR                "01C"
#define PRODUCTTYPE_AV_LOTUSNOTES_NT_STR              "01D"
#define PRODUCTTYPE_DISASTER_REC_NT_STR               "01E"
#define PRODUCTTYPE_INOCULAN_NT_STR                   "01F"
#define PRODUCTTYPE_ARCSERVE_NT_WS_STR                "020"
#define PRODUCTTYPE_IMAGEBACKUP_NT_STR                "021"
#define PRODUCTTYPE_RAID_NT_STR                       "022"
#define PRODUCTTYPE_CHANGER_NT_STR                    "023"
#define PRODUCTTYPE_CLIENTAGENT_NT_STR                "024"
#define PRODUCTTYPE_SQL_NT_STR                        "025"
#define PRODUCTTYPE_ORACLE_NT_STR                     "026"
#define PRODUCTTYPE_SAPR3_NT_STR                      "027"
#define PRODUCTTYPE_EXCHANGE_NT_STR                   "028"
#define PRODUCTTYPE_LOTUSNOTES_NT_STR                 "029"
#define PRODUCTTYPE_DISASTER_REC_NW_STR               "02A"
#define PRODUCTTYPE_FAXSERVE_KAYAK_STR                "02B"
#define PRODUCTTYPE_AV_W95_CLIENT_STR                 "02C"
#define PRODUCTTYPE_JETSERVE_UPGRADE_STR              "02D"
#define PRODUCTTYPE_CHEYENNE_DR_NT_STR                "02E"
#define PRODUCTTYPE_OPENFILE_AGENT_NW_STR             "02F"
#define PRODUCTTYPE_OPENFILE_AGENT_NT_STR             "030"
#define PRODUCTTYPE_GROUPWISE_AGENT_NW_STR            "031"
#define PRODUCTTYPE_ANTIVIRUS_EXCHANGE_STR            "032"
#define PRODUCTTYPE_FAXSERVE_NT_STR                   "033"
#define PRODUCTTYPE_MBO_NT_STR                        "034"
#define PRODUCTTYPE_ARCSERVE_NW_SS_STR                "035"
#define PRODUCTTYPE_ARCSERVE_NW_SB_STR                "036"
#define PRODUCTTYPE_REPLICATION_NT_STR                "037"
#define PRODUCTTYPE_DS_STANDARD_STR                   "038"
#define PRODUCTTYPE_AUDITWARE_STR                     "039"
#define PRODUCTTYPE_INFORMIX_ONLINE_NT_STR            "03A"
#define PRODUCTTYPE_TANDEM_SWS_NT_STR                 "03B"
#define PRODUCTTYPE_SYBASE_NT_STR                     "03C"
#define PRODUCTTYPE_CHANGER_OPTICAL_NT_STR            "03D"
#define PRODUCTTYPE_CLIENT_AGENT_WIN95_STR            "03E"
#define PRODUCTTYPE_INOCULAN_MANAGEWISE_BUNDLE_STR    "040"
#define PRODUCTTYPE_DISTRIBUTED_OPTIONS_NT_STR        "041"
#define PRODUCTTYPE_RP_32_NT_SERVER_STR               "042"
#define PRODUCTTYPE_RP_32_WORKSTATION_STR             "043"
#define PRODUCTTYPE_UNICENTER125_BACKUP_STR           "044"
#define PRODUCTTYPE_RP_32_LITE_STR                    "045"
#define PRODUCTTYPE_OPTICAL_SERVER_STR                "046"
#define PRODUCTTYPE_CD_SERVER_STR                     "047"
#define PRODUCTTYPE_FILE_JOCKEY_STR                   "048"
#define PRODUCTTYPE_OPTICAL_CD_SERVER_STR             "049"
#define PRODUCTTYPE_OPTICAL_SERVER_FILE_JOCKEY_STR    "04A"
#define PRODUCTTYPE_CD_SERVER_FILE_JOCKEY_STR         "04B"
#define PRODUCTTYPE_OPTICAL_CD_SERVER_FILE_JOCKEY_STR "04C"
#define PRODUCTTYPE_FILE_JOCKEY_UPG_10_20_STR         "04D"
#define PRODUCTTYPE_ARCSERVE_NT_SB_STR                "04F"
#define PRODUCTTYPE_OPTICAL_SERVER_UPG_10_15_STR      "050"
#define PRODUCTTYPE_INGRES_NT_STR                     "051"
#define PRODUCTTYPE_EMAIL_GATEWAY_FAXSERVE_STR        "052"
#define PRODUCTTYPE_INOCULAN_INTERNET_STR             "053"
#define PRODUCTTYPE_SILO_NT_STR                       "054"
#define PRODUCTTYPE_ARCSERVE_UNIX_STR                 "055"
#define PRODUCTTYPE_UNIX_AGENT_NT_STR                 "056"
#define PRODUCTTYPE_ENTERPRISE_LIBRARY_NW_STR         "057"
#define PRODUCTTYPE_POWERPACK_WG_NT_STR               "058"
#define PRODUCTTYPE_ARCSERVE_66_NW_STR                "059"
#define PRODUCTTYPE_ARCSERVE_66_NW_SB_STR             "05A"
#define PRODUCTTYPE_ARCSERVE_66_NW_SS_STR             "05B"
#define PRODUCTTYPE_TAPE_LIBRARY_66_NW_STR            "05C"
#define PRODUCTTYPE_NWAGENT_66_NW_STR                 "05D"
#define PRODUCTTYPE_DMAGENT_NT_STR                    "05E"
#define PRODUCTTYPE_DMSTORAGES_NT_STR                 "05F"
#define PRODUCTTYPE_TNGFRAMEWORK_NT_STR               "060"
#define PRODUCTTYPE_FAXSERVE_EXCHANGE_STR             "060"
#define PRODUCTTYPE_INFINIT_STR                       "061"
#define PRODUCTTYPE_DMAGENT_UNIX_STR                  "062"
#define PRODUCTTYPE_SYMFOWARE_NT_STR                  "063"
#define PRODUCTTYPE_JASMINE_STR                       "064"
#define PRODUCTTYPE_MCBSERVER_STR                     "065"
#define PRODUCTTYPE_ARCSERVE_66_NW_LITE_STR           "066"
#define PRODUCTTYPE_CROSSPLATFORM_70_STR              "067"
#define PRODUCTTYPE_SERVERLESSBACKUP_STR              "068"
#define PRODUCTTYPE_ARCSERVE_70_NW_STR                "069"
#define PRODUCTTYPE_ETRUST_FW_STR                     "06A"
#define PRODUCTTYPE_ETRUST_FW_PE_STR                  "06B"
#define PRODUCTTYPE_ETRUST_CI_PE_STR                  "06C"
#define PRODUCTTYPE_ETRUST_MW_STR                     "06D"
#define PRODUCTTYPE_ETRUST_VPN_STR                    "06E"
#define PRODUCTTYPE_ETRUST_ID_STR                     "06F"
#define PRODUCTTYPE_ARCSERVE_70_NW_SB_STR             "070"
#define PRODUCTTYPE_ARCSERVE_70_NW_SS_STR             "071"
#define PRODUCTTYPE_ARCSERVE_70_NW_SE_STR             "072"
#define PRODUCTTYPE_GROUPWISE_AGENT_70_NW_STR         "073"
#define PRODUCTTYPE_TAPE_LIBRARY_70_NW_STR            "074"
#define PRODUCTTYPE_SAN_70_NW_STR                     "075"
#define PRODUCTTYPE_RAID_70_NW_STR                    "076"
#define PRODUCTTYPE_DISASTER_REC_70_NW_STR            "077"
#define PRODUCTTYPE_ORACLE_TIMEFINDER_STR             "078"
#define PRODUCTTYPE_MY_ETRUST_AV_PE_STR               "079"
#define PRODUCTTYPE_MY_ETRUST_SUITE_PE_STR            "07A"
#define PRODUCTTYPE_MY_ETRUST_SUITE_SOHO_STR          "07B"
#define PRODUCTTYPE_ORACLE_SERVERLESS_STR             "07C"
#define PRODUCTTYPE_ORACLE_FASTRAX_STR                "07D"
#define PRODUCTTYPE_CLIENTAGENT_NAS_STR               "07E"
#define PRODUCTTYPE_CLIENTAGENT_SYMMETRIX_STR         "07F"
#define PRODUCTTYPE_MY_ETRUST_AV_SOHO_STR             "080"
#define PRODUCTTYPE_MY_ETRUST_SB_PE_STR               "081"
#define PRODUCTTYPE_MY_ETRUST_SB_SOHO_STR             "082"
#define PRODUCTTYPE_MY_ETRUST_FW_PE_STR               "083"
#define PRODUCTTYPE_MY_ETRUST_FW_SOHO_STR             "084"
#define PRODUCTTYPE_ARCSERVE_NT_XP_STR                "085"
#define PRODUCTTYPE_MY_ETRUST_TSP_STR                 "086"
#define PRODUCTTYPE_IBM3494_NT_STR                    "087"
#define PRODUCTTYPE_NWAGENT_90_NW_STR				  "088"
#define PRODUCTTYPE_WINAGENT_90_NW_STR				  "089"
#define PRODUCTTYPE_UNIXAGENT_90_NW_STR				  "08A"
#define PRODUCTTYPE_GROUPWISE_AGENT_90_NW_STR		  "08B"
#define PRODUCTTYPE_TAPE_LIBRARY_90_NW_STR			  "08C"
#define PRODUCTTYPE_SAN_90_NW_STR					  "08D"
#define PRODUCTTYPE_DISASTER_REC_90_NW_STR			  "08E"
#define PRODUCTTYPE_IMAGE_90_NW_STR					  "08F"
#define PRODUCTTYPE_ARCSERVE_90_NW_STR				  "090"
#define PRODUCTTYPE_LINUX_AGENT_NT_STR                "091"
#define PRODUCTTYPE_CLIENTAGENT_NW_STR                "092"
#define PRODUCTTYPE_ARCSERVE_90_NW_SE_STR			  "093"
#define PRODUCTTYPE_RAID_90_NW_STR					  "094"
#define PRODUCTTYPE_OPENFILEAGENT_90_NW_STR			  "095"
#define PRODUCTTYPE_LINUXAGENT_90_NW_STR			  "096"
#define PRODUCTTYPE_SQL_HITACHI_NT_STR                "097"
#define PRODUCTTYPE_TIMEFINDER_STR                    "098"
#define PRODUCTTYPE_SQL_HP_XP_STR		              "099"
#define PRODUCTTYPE_EXCHANGE_ADDON_NT_STR		      "100"
#define PRODUCTTYPE_ENTERPRISE_OPTION_STR		      "102"

#define PRODUCTTYPE_ARCSERVE_11_NW_STR				  "09A"
#define PRODUCTTYPE_ARCSERVE_11_NW_SE_STR			  "09B"
#define PRODUCTTYPE_DISASTER_REC_11_NW_STR            "09C"
#define PRODUCTTYPE_RAID_11_NW_STR                    "09D"
#define PRODUCTTYPE_IMAGE_11_NW_STR					  "09E"
#define PRODUCTTYPE_TAPE_LIBRARY_11_NW_STR			  "09F"
#define PRODUCTTYPE_SAN_11_NW_STR					  "104"
#define PRODUCTTYPE_MYSQLAGENT_11_NW_STR			  "103"
#define PRODUCTTYPE_OPENFILEAGENT_11_NW_STR			  "105"
#define PRODUCTTYPE_GROUPWISE_AGENT_11_NW_STR		  "106"
#define PRODUCTTYPE_WINAGENT_11_NW_STR				  "107"
#define PRODUCTTYPE_NWAGENT_11_NW_STR				  "108"
#define PRODUCTTYPE_UNIXAGENT_11_NW_STR				  "109"
#define PRODUCTTYPE_LINUXAGENT_11_NW_STR			  "10A"
#define PRODUCTTYPE_MACAGENT_11_NW_STR				  "10B"

#define PRODUCTTYPE_NAS_UNIX_STR					  "10C"
#define PRODUCTTYPE_ORACLE_UNIX_STR					  "10D"
#define PRODUCTTYPE_SYBASE_UNIX_STR					  "10E"
#define PRODUCTTYPE_INFORMIX_UNIX_STR				  "10F"
#define PRODUCTTYPE_SAP_UNIX_STR					  "110"
#define PRODUCTTYPE__LOTUSNOTES_UNIX_STR			  "111"
#define PRODUCTTYPE_DB2UDB_UNIX_STR					  "112"
#define PRODUCTTYPE_INGRES_UNIX_STR					  "113"
#define PRODUCTTYPE_APACHE_WEB_LINUX_STR			  "114"
#define PRODUCTTYPE_MYSQL_LINUX_STR					  "115"


#define PRODUCTTYPE_LIVETRIAL_NT_STR                  "0FE"
#define PRODUCTTYPE_LIVETRIAL_NW_STR                  "0FF"

// Product subtypes
#define PRODUCTSUBTYPE_TNG_ASM                  0x00
#define PRODUCTSUBTYPE_STD						0x00	// Standard Edition
#define PRODUCTSUBTYPE_TNG_ASO                  0x01
#define PRODUCTSUBTYPE_ENTERPRISE               0x02
#define PRODUCTSUBTYPE_WORKGROUP                0x03
#define PRODUCTSUBTYPE_ADVANCED                 0x04
#define PRODUCTSUBTYPE_SBS		                0x05	// Small Business Server Edition

// Definition of userLevelStrings
#define  USER_EVAL_STRING                       "00"
#define  USER_LIVE_STRING                       "01"
#define  USER_LIVE_60_STRING                    "02"
#define  USER_LIVE_90_STRING                    "03"
#define  USER_LIVE_180_STRING                   "04"
#define  USER_SITE_STRING                       "05"
#define  USER_3YEARS_NFR_STRING					"06"

#define  USER_WORKGROUP_STRING                  "0A"
#define  USER_ENTERPRISE_STRING                 "0B"
#define  USER_UP_WORKGROUP_L25_STR              "0C"
#define  USER_UP_ENTERPRISE_L25_STR             "0D"
#define  USER_UP_ENTERPRISE_G25_STR             "0E"
#define  USER_UP_ENTERPRISE_WORKGRP_STR         "0F"
#define  USER_UP_TO_SINGLE_SERVER_STR           "10"
#define  USER_UP_TO_SMALL_BUSINESS_STR          "11"

#define  USER_0012_STRING                       "12"
#define  USER_0013_STRING                       "13"
#define  USER_0014_STRING                       "14"
#define  USER_0015_STRING                       "15"
#define  USER_0016_STRING                       "16"
#define  USER_0017_STRING                       "17"
#define  USER_0018_STRING                       "18"
#define  USER_0019_STRING                       "19"
#define  USER_001A_STRING                       "1A"
#define  USER_001B_STRING                       "1B"
#define  USER_001C_STRING                       "1C"
#define  USER_001D_STRING                       "1D"
#define  USER_001E_STRING                       "1E"
#define  USER_001F_STRING                       "1F"
#define  USER_0020_STRING                       "20"
#define  USER_0021_STRING                       "21"
#define  USER_0022_STRING                       "22"
#define  USER_0023_STRING                       "23"
#define  USER_0024_STRING                       "24"
#define  USER_0025_STRING                       "25"
#define  USER_0026_STRING                       "26"
#define  USER_0027_STRING                       "27"
#define  USER_0028_STRING                       "28"
#define  USER_0029_STRING                       "29"
#define  USER_002A_STRING                       "2A"
#define  USER_002B_STRING                       "2B"
#define  USER_002C_STRING                       "2C"
#define  USER_002D_STRING                       "2D"
#define  USER_002E_STRING                       "2E"
#define  USER_002F_STRING                       "2F"
#define  USER_0030_STRING                       "30"


#define  USER_1_STRING                          "UF"
#define  USER_5_STRING                          "U0"
#define  USER_10_STRING                         "U1"
#define  USER_25_STRING                         "U2"
#define  USER_50_STRING                         "U3"
#define  USER_100_STRING                        "U4"
#define  USER_200_STRING                        "U5"
#define  USER_250_STRING                        "U6"
#define  USER_400_STRING                        "U7"
#define  USER_500_STRING                        "U8"
#define  USER_1000_STRING                       "U9"
#define  USER_UNLIMITED_STRING                  "UA"
#define  USER_INOCULAN_NT_SB_STRING             "I0"
#define  USER_INOCULAN_NT_SB_25_STRING          "I1"
#define  USER_INOCULAN_NT_SB_5_UPG_STRING       "I2"


// Definition of oemCodes
#define  OEM_CHEYENNE                           0x00000000
#define	OEM_CA											OEM_CHEYENNE
#define	OEM_COMPUTERASSOCIATES						OEM_CA
#define  OEM_COMPAQ                             0x00000001
#define  OEM_IBM                                0x00000002
#define  OEM_HP                                 0x00000003
#define  OEM_UNISYS                             0x00000004
#define  OEM_NEC                                0x00000005
#define  OEM_DEC                                0x00000006
#define  OEM_INTEL                              0x00000007
#define  OEM_NETFRAME                           0x00000008
#define  OEM_EMERALD                            0x00000009


// Definition of oemCodes
#define  OEM_CHEYENNE_STRING                    "CHEY"
#define	OEM_CA_STRING							OEM_CHEYENNE_STRING
#define	OEM_COMPUTERASSOCIATES_STRING			OEM_CA_STRING
#define  OEM_COMPAQ_STRING                      "CMPQ"
#define  OEM_IBM_STRING                         "IBM0"
#define  OEM_HP_STRING                          "HEWP"
#define  OEM_UNISYS_STRING                      "UNIS"
#define  OEM_NEC_STRING                         "NEC0"
#define  OEM_DEC_STRING                         "DEC0"
#define  OEM_INTEL_STRING                       "INTL"
#define  OEM_NETFRAME_STRING                    "NETF"
#define  OEM_EMERALD_STRING                     "EMER"

#ifdef _WINDOWS
#define SZ_OEM_CHEYENNE                         "Computer Associates Intl."
#define SZ_OEM_COMPAQ                           "Compaq Computer Corp."
#define SZ_OEM_IBM                              "IBM Corp."
#define SZ_OEM_HP                               "Hewlett-Packard Co."
#define SZ_OEM_UNISYS                           "Unisys Corp."
#define SZ_OEM_NEC                              "NEC America Inc."
#define SZ_OEM_DEC                              "Digital Equipment Corp."
#define SZ_OEM_INTEL                            "Intel Corp."
#define SZ_OEM_NETFRAME                         "NetFrame"
#define SZ_OEM_EMERALD                          "Emerald"
#endif

#define IsAS6xNWSingleServerProdCode(x)         ( ((x) == PRODUCTTYPE_ARCSERVE_66_NW_SS || (x) == PRODUCTTYPE_ARCSERVE_NW_SS) ? TRUE : FALSE)

#endif //_LICENSE_DEFINITION
