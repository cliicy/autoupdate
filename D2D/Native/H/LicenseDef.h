/************************************************************************
  File  Name: licenseDef.h
     Version: 1.0
 	   brief: it's based on <Proposed UDP License Components_v0.2.xlsx>
 
  When checking license , client need send related information to CPM side. If CPM find favorable license , then return the license id (refer to section of license identify define )to client
	1, Client type: should one of UDP_CLIENT_TYPE
	2, Machine Host Name
	3, The sub-license items, client can send more than one sub-license by combining in one 4 byte parameters (32 bit DWORD)
 
     Author: wanmi12
 CreateTime: 2014-01-14
    History: 
			 1. 2014.2.12 modify by wanmi12 to syn with Proposed UDP License Components_v0.8

************************************************************************/
#ifndef ARCSERVE_D2D_8D11D636_9761_434D_BA3E_8340F5B61093_licenseDef_H
#define ARCSERVE_D2D_8D11D636_9761_434D_BA3E_8340F5B61093_licenseDef_H
#pragma once

///////////////////////////////////////////////////////////////////////////////////////////////////
// license identify define
#define ID_LIC_INVALID_LICENSE								0x00	// 00 Invalid License 
#define ID_LIC_STANDARD_Managed_Capacity					0x01	// 01 CA ARCserve UDP v5 Standard Edition - Managed Capacity
#define ID_LIC_ADVANCED_Managed_Capacity					0x02	// 02 CA ARCserve UDP v5 Advanced Edition - Managed Capacity
#define ID_LIC_PREMIUM_Managed_Capacity						0x03	// 03 CA ARCserve UDP v5 Premium Edition - Managed Capacity
#define ID_LIC_PREMIUM_Managed_Capacity_PLUS				0x04	// 04 CA ARCserve UDP v5 Premium Plus Edition - Managed Capacity
#define ID_LIC_STANDARD_Per_HOST							0x05	// 05 CA ARCserve UDP v5 Standard Per Host
#define ID_LIC_ADVANCED_Per_HOST							0x06	// 06 CA ARCserve UDP v5 Advanced Per Host
#define ID_LIC_PREMIUM_Per_HOST								0x07	// 07 CA ARCserve UDP v5 Premium Per Host
#define ID_LIC_PREMIUM_Per_HOST_PLUS						0x08	// 08 CA ARCserve UDP v5 Premium Plus Per Host
#define ID_LIC_STANDARD_Per_SOCKET							0x09	// 09 CA ARCserve UDP v5 Standard Edition - Virtual Hypervisor Socket
#define ID_LIC_ADVANCED_Per_SOCKET							0x0A	// 10 CA ARCserve UDP v5 Advanced Edition - Virtual Hypervisor Socket
#define ID_LIC_PREMIUM_Per_SOCKET							0x0B	// 11 CA ARCserve UDP v5 Premium Edition - Virtual Hypervisor Socket
#define ID_LIC_PREMIUM_Per_SOCKET_PLUS						0x0C	// 12 CA ARCserve UDP v5 Premium Plus Edition - Virtual Hypervisor Socket
#define ID_LIC_STANDARD_Per_SOCKET_Essentials				0x0D	// 13 CA ARCserve UDP v5 Standard Edition - Virtual Hypervisor Socket (6 Socket Essentials)
#define ID_LIC_ADVANCED_Per_SOCKET_Essentials				0x0E	// 14 CA ARCserve UDP v5 Advanced Edition - Virtual Hypervisor Socket (6 Socket Essentials)
#define ID_LIC_PREMIUM_Per_SOCKET_Essentials				0x0F	// 15 CA ARCserve UDP v5 Premium Edition - Virtual Hypervisor Socket (6 Socket Essentials)
#define ID_LIC_PREMIUM_Per_SOCKET_Essentials_PLUS			0x10	// 16 CA ARCserve UDP v5 Premium Plus Edition - Virtual Hypervisor Socket (6 Socket Essentials)
#define ID_LIC_Virtual_Machine								0x11	// 17 CA ARCserve UDP v5 Virtual Machine
#define ID_LIC_STANDARD_SERVER								0x12	// 18 CA ARCserve UDP v5 Standard Edition - Server
#define ID_LIC_ADVANCED_SERVER								0x13	// 19 CA ARCserve UDP v5 Advanced Edition - Server
#define ID_LIC_PREMIUM_SERVER								0x14	// 20 CA ARCserve UDP v5 Premium Edition - Server
#define ID_LIC_PREMIUM_SERVER_PLUS							0x15	// 21 CA ARCserve UDP v5 Premium Plus Edition - Server	
#define ID_LIC_BASIC										0x16	// 22 CA ARCserve UDP v5 Basic
#define ID_LIC_ADVANCED_SBS									0x17	// 23 CA ARCserve UDP v5 Advanced Edition - SBS-Essentials Server
#define ID_LIC_PREMIUM_SBS									0x18	// 24 CA ARCserve UDP v5 Premium Edition - SBS-Essentials Server
#define ID_LIC_STANDARD_WORKSTATION							0x19	// 25 CA ARCserve UDP v5 Standard Edition - Workstation
#define ID_LIC_TRIAL_LICENSE								0xFF	// trial License
#define ID_LIC_FREE_EDITION									0xFE	// Free Edition ,only for workstation

//Bundle license. will not use for assign operation
#define ID_LIC_BUNDLE__WORKSTATION_5		0x100
#define ID_LIC_BUNDLE__WORKSTATION_10		0x101		
#define ID_LIC_BUNDLE__WORKSTATION_25		0x102
#define ID_LIC_BUNDLE__WORKSTATION_50		0x103
#define ID_LIC_BUNDLE__WORKSTATION_100		0x104
#define ID_LIC_BUNDLE__WORKSTATION_250		0x105
#define ID_LIC_BUNDLE__WORKSTATION_500		0x106

///////////////////////////////////////////////////////////////////////////////////////////////////
//license sub item mask: each feature or os type has one related mask, application can use a 32 bit value to hold 32 types of sub-license
#define SUBLIC_ALL				0xFFFFFFFF
#define SUMLIC_EMPTY			0x00000000
		
#define SUBLIC_OS_PM			0x00000001	// Physical Machine
#define SUBLIC_OS_WORKSTATION	0x00000002	// Workstation OS
#define SUBLIC_OS_SBS			0x00000004	// SBS OS
#define SUBLIC_OS_SERVER		0x00000008  // Server OS
#define SUBLIC_OS_HYPERV		0x00000010	// Hypervisor Level Backup
//#define SUBLIC_OS_HYPERV_SOCKET 0x00000010  // Hypervisor Level Backup (Per Socket)  
//#define SUBLIC_OS_HYPERV_HOST	  0x00000010  // Hypervisor Level Backup (Per Host), use the same id as  SUBLIC_OS_HYPERV_SOCKET
#define SUBLIC_BLI				0x00000040  // BLI (I2)
#define SUBLIC_VSB				0x00000080  // Virtual Standby 
#define SUBLIC_APP_EXCHANGE		0x00000100  // Exchange DB level restore , Exchange mail level restore 
#define SUBLIC_App_SQL			0x00000200  // SQL DB Level Recovery
#define SUBLIC_NO_VMWare_Essential	0x00000400  // VMware Essential, only for HBBU

#define ALL_OS_LICENSE		SUBLIC_OS_PM|SUBLIC_OS_WORKSTATION|SUBLIC_OS_SBS|SUBLIC_OS_SERVER|SUBLIC_OS_HYPERV_SOCKET|SUBLIC_OS_HYPERV_HOST
#define ALL_APP_LICENSE		SUBLIC_APP_EXCHANGE|SUBLIC_App_SQL

// the all feature for standalone D2D
#define ALL_LIC_STANDALONE_AGENT		 SUBLIC_ALL&(~(SUBLIC_OS_HYPERV|SUBLIC_VSB) )
#define ALL_LIC_STANDALONE_AGENT_NO_PM   SUBLIC_ALL&(~(SUBLIC_OS_HYPERV|SUBLIC_VSB |SUBLIC_OS_PM))

///////////////////////////////////////////////////////////////////////////////////////////////////
// license function define
// Managed Capacity 4
#define MASK_LIC_STANDARD_Managed_Capacity		SUBLIC_ALL&(~(SUBLIC_APP_EXCHANGE|SUBLIC_App_SQL) )  // CA ARCserve UDP v5 Standard Edition - Managed Capacity
#define MASK_LIC_ADVANCED_Managed_Capacity		SUBLIC_ALL											 // CA ARCserve UDP v5 Advanced Edition - Managed Capacity
#define MASK_LIC_PREMIUM_Managed_Capacity		SUBLIC_ALL											 // CA ARCserve UDP v5 Premium Edition - Managed Capacity
#define MASK_LIC_PREMIUM_Managed_Capacity_PLUS	SUBLIC_ALL											 // CA ARCserve UDP v5 Premium Plus Edition - Managed Capacity
// HBBU per Host 4
#define MASK_LIC_STANDARD_Per_HOST				SUBLIC_ALL&(~(SUBLIC_APP_EXCHANGE|SUBLIC_App_SQL|SUBLIC_OS_PM) ) // CA ARCserve UDP v5 Standard Per Host
#define MASK_LIC_ADVANCED_Per_HOST				SUBLIC_ALL&(~(SUBLIC_OS_PM) )// CA ARCserve UDP v5 Advanced Per Host
#define MASK_LIC_PREMIUM_Per_HOST				SUBLIC_ALL&(~(SUBLIC_OS_PM) )// CA ARCserve UDP v5 Premium Per Host
#define MASK_LIC_PREMIUM_Per_HOST_PLUS			SUBLIC_ALL&(~(SUBLIC_OS_PM) )// CA ARCserve UDP v5 Premium Plus Per Host
// HBBU per Socket 8
#define MASK_LIC_STANDARD_Per_SOCKET			SUBLIC_ALL&(~(SUBLIC_APP_EXCHANGE|SUBLIC_App_SQL) ) // CA ARCserve UDP v5 Standard Edition - Virtual Hypervisor Socket
#define MASK_LIC_ADVANCED_Per_SOCKET			SUBLIC_ALL		// CA ARCserve UDP v5 Advanced Edition - Virtual Hypervisor Socket
#define MASK_LIC_PREMIUM_Per_SOCKET				SUBLIC_ALL		// CA ARCserve UDP v5 Premium Edition - Virtual Hypervisor Socket
#define MASK_LIC_PREMIUM_Per_SOCKET_PLUS		SUBLIC_ALL		// CA ARCserve UDP v5 Premium Plus Edition - Virtual Hypervisor Socket

#define MASK_LIC_STANDARD_Per_SOCKET_Essentials		SUBLIC_ALL&(~(SUBLIC_NO_VMWare_Essential|SUBLIC_APP_EXCHANGE|SUBLIC_App_SQL|SUBLIC_OS_SERVER) ) // CA ARCserve UDP v5 Standard Edition - Virtual Hypervisor Socket (6 Socket Essentials)
#define MASK_LIC_ADVANCED_Per_SOCKET_Essentials		SUBLIC_ALL&(~(SUBLIC_NO_VMWare_Essential|SUBLIC_OS_SERVER) ) // CA ARCserve UDP v5 Advanced Edition - Virtual Hypervisor Socket (6 Socket Essentials)
#define MASK_LIC_PREMIUM_Per_SOCKET_Essentials		SUBLIC_ALL&(~(SUBLIC_NO_VMWare_Essential|SUBLIC_OS_SERVER) ) // CA ARCserve UDP v5 Premium Edition - Virtual Hypervisor Socket (6 Socket Essentials)
#define MASK_LIC_PREMIUM_Per_SOCKET_Essentials_PLUS	SUBLIC_ALL&(~(SUBLIC_NO_VMWare_Essential|SUBLIC_OS_SERVER) ) // CA ARCserve UDP v5 Premium Plus Edition - Virtual Hypervisor Socket (6 Socket Essentials)

//Virtual Machine 1
#define MASK_LIC_Virtual_Machine			SUBLIC_ALL&(~(SUBLIC_OS_HYPERV|SUBLIC_OS_PM) ) // CA ARCserve UDP v5 Virtual Machine

//Server 4
#define MASK_LIC_STANDARD_SERVER			SUBLIC_ALL&(~(SUBLIC_APP_EXCHANGE|SUBLIC_App_SQL|SUBLIC_OS_HYPERV) ) //CA ARCserve UDP v5 Standard Edition - Server
#define MASK_LIC_ADVANCED_SERVER			SUBLIC_ALL&(~(SUBLIC_OS_HYPERV) )	// CA ARCserve UDP v5 Advanced Edition - Server
#define MASK_LIC_PREMIUM_SERVER				SUBLIC_ALL&(~(SUBLIC_OS_HYPERV) )	// CA ARCserve UDP v5 Premium Edition - Server
#define MASK_LIC_PREMIUM_SERVER_PLUS		SUBLIC_ALL&(~(SUBLIC_OS_HYPERV) )	// CA ARCserve UDP v5 Premium Plus Edition - Server

//Basic 1
#define MASK_LIC_BASIC						SUBLIC_OS_PM|SUBLIC_OS_WORKSTATION|SUBLIC_OS_SBS|SUBLIC_OS_SERVER				// CA ARCserve UDP v5 Basic

//SBS 2
#define MASK_LIC_ADVANCED_SBS				SUBLIC_ALL&(~(SUBLIC_OS_HYPERV|SUBLIC_OS_SERVER) ) // CA ARCserve UDP v5 Advanced Edition - SBS-Essentials Server
#define MASK_LIC_PREMIUM_SBS				SUBLIC_ALL&(~(SUBLIC_OS_HYPERV|SUBLIC_OS_SERVER) ) // CA ARCserve UDP v5 Premium Edition - SBS-Essentials Server

//Workstation 1
#define MASK_LIC_STANDARD_WORKSTATION       SUBLIC_OS_PM|SUBLIC_OS_WORKSTATION| SUBLIC_BLI| SUBLIC_App_SQL| SUBLIC_VSB // CA ARCserve UDP v5 Standard Edition - Workstation
 
//Free Edition
#define  MASK_LIC_FREE_EDITION				SUBLIC_ALL
///////////////////////////////////////////////////////////////////////////////////////////////////
#define IS_INCLUDING_LICENSE(x, y) ( ((x&y)?1:0 ) )

// check whether include sub-license 10
#define HAS_PM(license)				IS_INCLUDING_LICENSE(license, SUBLIC_OS_PM)
#define HAS_HyperV(license)		    IS_INCLUDING_LICENSE(license, SUBLIC_OS_HYPERV)
#define HAS_WORKSTATION(license)	IS_INCLUDING_LICENSE(license, SUBLIC_OS_WORKSTATION)|IS_INCLUDING_LICENSE(license, SUBLIC_OS_SBS)|IS_INCLUDING_LICENSE(license, SUBLIC_OS_SERVER)
#define HAS_SBS(license)			IS_INCLUDING_LICENSE(license, SUBLIC_OS_SBS)|IS_INCLUDING_LICENSE(license, SUBLIC_OS_SERVER)
#define HAS_SERVER(license)			IS_INCLUDING_LICENSE(license, SUBLIC_OS_SERVER)
#define HAS_BLI(license)			IS_INCLUDING_LICENSE(license, SUBLIC_BLI)
#define HAS_VSB(license)			IS_INCLUDING_LICENSE(license, SUBLIC_VSB)
#define HAS_EXCHANGE(license)		IS_INCLUDING_LICENSE(license, SUBLIC_APP_EXCHANGE)
#define HAS_SQL(license)			IS_INCLUDING_LICENSE(license, SUBLIC_App_SQL )


//#define HAS_HyperV_SOCKET(license)	IS_INCLUDING_LICENSE(license, SUBLIC_OS_HYPERV_SOCKET)
//#define HAS_HyperV_HOST(license)		IS_INCLUDING_LICENSE(license, SUBLIC_OS_HYPERV_HOST)

///////////////////////////////////////////////////////////////////////////////////////////////////
// client type
typedef enum _client_type
{
	UDP_CLIENT_UNKNOWN	= 0x00,		
	UDP_WINDOWS_AGENT	= 0x01,
	UDP_CLIENT_HBBU		= 0x02,
	UDP_CLIENT_RPS		= 0x04,
	UDP_LINUX_AGENT		= 0x08,
}UDP_CLIENT_TYPE, *PUDP_CLIENT_TYPE;

typedef struct _udplic_id2mask
{
	DWORD licID;
	DWORD licMask;
} LIC_ID2MASK, *PLIC_ID2MASK;

#define NUMBER_OF_LICENSE 25		// the total number of license
#define NUMBER_OF_LICENSE_ALL	NUMBER_OF_LICENSE+2 // + TRIAL + FREE
const LIC_ID2MASK lic_id2mask[NUMBER_OF_LICENSE_ALL] =
{  
	{ ID_LIC_STANDARD_Managed_Capacity			,MASK_LIC_STANDARD_Managed_Capacity			},
	{ ID_LIC_ADVANCED_Managed_Capacity			,MASK_LIC_ADVANCED_Managed_Capacity			},
	{ ID_LIC_PREMIUM_Managed_Capacity			,MASK_LIC_PREMIUM_Managed_Capacity			},
	{ ID_LIC_PREMIUM_Managed_Capacity_PLUS		,MASK_LIC_PREMIUM_Managed_Capacity_PLUS		},
	{ ID_LIC_STANDARD_Per_HOST					,MASK_LIC_STANDARD_Per_HOST					},
	{ ID_LIC_ADVANCED_Per_HOST					,MASK_LIC_ADVANCED_Per_HOST					},
	{ ID_LIC_PREMIUM_Per_HOST					,MASK_LIC_PREMIUM_Per_HOST					},
	{ ID_LIC_PREMIUM_Per_HOST_PLUS				,MASK_LIC_PREMIUM_Per_HOST_PLUS				},
	{ ID_LIC_STANDARD_Per_SOCKET				,MASK_LIC_STANDARD_Per_SOCKET				},
	{ ID_LIC_ADVANCED_Per_SOCKET				,MASK_LIC_ADVANCED_Per_SOCKET				},
	{ ID_LIC_PREMIUM_Per_SOCKET					,MASK_LIC_PREMIUM_Per_SOCKET				},
	{ ID_LIC_PREMIUM_Per_SOCKET_PLUS			,MASK_LIC_PREMIUM_Per_SOCKET_PLUS			},
	{ ID_LIC_STANDARD_Per_SOCKET_Essentials		,MASK_LIC_STANDARD_Per_SOCKET_Essentials	},
	{ ID_LIC_ADVANCED_Per_SOCKET_Essentials		,MASK_LIC_ADVANCED_Per_SOCKET_Essentials	},
	{ ID_LIC_PREMIUM_Per_SOCKET_Essentials		,MASK_LIC_PREMIUM_Per_SOCKET_Essentials		},
	{ ID_LIC_PREMIUM_Per_SOCKET_Essentials_PLUS	,MASK_LIC_PREMIUM_Per_SOCKET_Essentials_PLUS},
	{ ID_LIC_Virtual_Machine					,MASK_LIC_Virtual_Machine					},
	{ ID_LIC_STANDARD_SERVER					,MASK_LIC_STANDARD_SERVER					},
	{ ID_LIC_ADVANCED_SERVER					,MASK_LIC_ADVANCED_SERVER					},
	{ ID_LIC_PREMIUM_SERVER						,MASK_LIC_PREMIUM_SERVER					},
	{ ID_LIC_PREMIUM_SERVER_PLUS				,MASK_LIC_PREMIUM_SERVER_PLUS				},
	{ ID_LIC_BASIC								,MASK_LIC_BASIC								},
	{ ID_LIC_ADVANCED_SBS						,MASK_LIC_ADVANCED_SBS						},
	{ ID_LIC_PREMIUM_SBS						,MASK_LIC_PREMIUM_SBS						},
	{ ID_LIC_STANDARD_WORKSTATION				,MASK_LIC_STANDARD_WORKSTATION				},
	{ ID_LIC_TRIAL_LICENSE						,SUBLIC_ALL									},
	{ ID_LIC_FREE_EDITION						,MASK_LIC_FREE_EDITION						},
	  
};
 
///////////////////////////////////////////////////////////////////////////////////////////////////
#endif// ARCSERVE_D2D_8D11D636_9761_434D_BA3E_8340F5B61093_licenseDef_H