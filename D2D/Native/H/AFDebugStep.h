#pragma once

#define AFBACKUP_DLL_BACKUP_BASE							0
#define AFBACKUP_DLL_BACKUP_MERGE_MARKFULL_BEFORE			AFBACKUP_DLL_BACKUP_BASE+0
#define AFBACKUP_DLL_BACKUP_MERGE_MARKFULL_AFTER			AFBACKUP_DLL_BACKUP_BASE+1
#define AFBACKUP_DLL_BACKUP_MERGE_FINISHED					AFBACKUP_DLL_BACKUP_BASE+2

#define AFBACKUP_DLL_OFFHOST_MERGE_MARKFULL_BEFORE			AFBACKUP_DLL_BACKUP_BASE+3
#define AFBACKUP_DLL_OFFHOST_MERGE_MARKFULL_AFTER			AFBACKUP_DLL_BACKUP_BASE+4
#define AFBACKUP_DLL_OFFHOST_MERGE_FINISHED					AFBACKUP_DLL_BACKUP_BASE+5

#define AFSTOR_DLL_BASE										0
#define	AFSTOR_DLL_PURGE_MOVE_INDEX_AFTER					AFSTOR_DLL_BASE+0
#define AFSTOR_DLL_PURGE_DATA								AFSTOR_DLL_BASE+1
#define AFSTOR_DLL_PURGE_DATA_AFSTER						AFSTOR_DLL_BASE+2
#define AFSTOR_DLL_PURGE_MOVE_INCIDX_AFTER					AFSTOR_DLL_BASE+3
#define AFSTOR_DLL_PURGE_MOVE_FULLD2D_AFTER					AFSTOR_DLL_BASE+4
#define AFSTOR_DLL_PURGE_MOVE_INDEX2STEP2_BEFORE			AFSTOR_DLL_BASE+5
#define AFSTOR_DLL_PRUGE_PHASE1_AFTER						AFSTOR_DLL_BASE+6
#define AFSTOR_DLL_PRUGE_PHASE2_DELFULL_BEFORE				AFSTOR_DLL_BASE+7
#define AFSTOR_DLL_PRUGE_PHASE2_DELIDX_BEFORE				AFSTOR_DLL_BASE+8
#define AFSTOR_DLL_PURGE_UPDATEUUID_BEFORE					AFSTOR_DLL_BASE+9
#define AFSTOR_DLL_PURGE_UPDATEFOOTER_BEFORE				AFSTOR_DLL_BASE+10
#define AFSTOR_DLL_PURGE_DELPARENT_BEFORE					AFSTOR_DLL_BASE+11
#define AFSTOR_DLL_PURGE_START      						AFSTOR_DLL_BASE+12
#define AFSTOR_DLL_PURGE_END            					AFSTOR_DLL_BASE+13


#define AFGENGRTCAT_DLL_BASE								0
#define AFGENGRTCAT_DLL_VALIDSESSION_BEFORE					AFGENGRTCAT_DLL_BASE+0