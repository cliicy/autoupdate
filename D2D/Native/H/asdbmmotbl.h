/**          MMO schema header file                              **/
/**                                                              **/
/**                                                 asdbmmotbl.h **/
/**                                                   10/20/1999 **/
/**                                                              **/
#ifndef ASDBMMOTBL_H
#define ASDBMMOTBL_H

#pragma pack(1)

#define ASDB_MMO_MAX_GETRECLIST         50

#define ASDB_MMO_BYNAMEORID_ALL         0
#define ASDB_MMO_BYNAMEORID_NAME        1
#define ASDB_MMO_BYNAMEORID_ID          2

#define ASDB_MMO_ERR_NOT_ENOUGH_MEMORY  -51

//#define ASDB_MMO_VSLOT_ISEMPTY_NOT      0
//#define ASDB_MMO_VSLOT_ISEMPTY_YES      1

/// Tape export
#define LOCAL_CONNECTION						11
#define REMOTE_CONNECTION						12

//Defining error codes for vaulted tapes export status.
#define EXPORT_VAULTTAPE_SUCCESSFUL			  0
#define EXPORT_VAULTTAPE_NOT_SUCCESSFUL       1
#define EXPORT_VAULTTAPE_READY				  2
#define EXPORT_VAULTTAPE_NOT_FOUND_IN_CHANGER 3



typedef enum {
    MMO_SLOT_TYPE_ACTIVE=0,     
    MMO_SLOT_TYPE_FREE, 
    MMO_SLOT_TYPE_ALL 
} ENUM_MMO_SLOT_TYPE;

#define ASDB_MMO_VSLOT_ISEMPTY_NOT      0
#define ASDB_MMO_VSLOT_ISEMPTY_YES      1

#define MMO_ERROR_STARTVAULT			1000
#define MMO_ERROR_COMMITVAULT			1001
#define MMO_ERROR_REPORT				1002	
#define MMO_ERROR_VAULTSELECTION		1003	
#define MMO_ERROR_EXPORT_TAPE			1004	

#define MMO_WARNING_INIT				2000
#define MMO_WARNING_EMPTY				2001
#define MMO_WARNING_HASDATA				2002
#define MMO_WARNING_ASSIGN_DUPLICATE	2003
#define MMO_WARNING_ASSIGN_NO_SCHEDULE	2004
#define MMO_WARNING_ASSIGN_NO_ROTATION	2005
#define MMO_WARNING_ASSIGN_CHECKOUT		2006
#define MMO_WARNING_ASSIGN_RETIRED		2007
#define MMO_WARNING_ASSIGN_PERMANENT	2008
#define MMO_WARNING_ASSIGN_DESTROYED	2009
#define MMO_WARNING_NOTVALID_TAPE		2010
#define MMO_WARNING_1					2011	
#define MMO_WARNING_2					2012



#define VT_VAULT_OTHERAPP	5
#define VT_VAULT_OVERFLOW	2
#define VT_VAULT_TMSDB		1

#define ASDB_MMO_FIRST_VAULT_ID	100

typedef enum {
	MMO_VCD_BYMPOOL = 1,
	MMO_VCD_BYFILE,
	MMO_VCD_BYOAPP
} MMO_VCD_TYPE;
#define VT_VCD_DIRECTVAULT		99999

typedef enum {
    MMO_TYPE_NONE=0, 
    MMO_TYPE_CYCLE, 
    MMO_TYPE_COMMIT, 
    MMO_TYPE_LIST, 
    MMO_TYPE_UNVAULT,
    MMO_TYPE_UPDATE_RRC,
    MMO_TYPE_RESET, 
} MMO_STATUS_TYPE;

typedef enum {
	DB_ENUM_CHECKIN_TEMP=0,
	DB_ENUM_CHECKIN_MANUAL_AND_RETIRE,
	DB_ENUM_CHECKIN_MANUAL

} DB_ENUM_CHECKIN_TYPE;


typedef struct _tagASDB_MMOvscheduleRec 
{
   long  id;                                            //  1 Auto increment, PK__vschedule
   long  schedule_padding;
   char  schedule[80];                                  //  2
}ASDB_MMOvscheduleRec, * PASDB_MMOvscheduleRec;

typedef struct _tagASDB_MMOvcdRec 
{
    long    id;                                         //  1 Auto increment, PK__vcd
    long    scheduleid;                                 //  2 FK_vcdscheduleid
    char    schedule[80];                               //
    short   vcdtype;                                    //  3
    short   sepdsn;                                     //  4
    short   sepjob;                                     //  5
    short   abend;                                      //  6
    long    cdate;                                      //  7 datetime
    long    ldate;                                      //  8 datetime 
    char    cuserid[32];                                //  9
    char    luserid[32];                                // 10
    char    mediapool[32];                              // 11
    char    dbname[40];                                 // 12
    char    nodename[64];                               // 13
    char    filename[256];                              // 14
    char    cprogram[80];                               // 15
    char    reserved[16];                               //
}ASDB_MMOvcdRec, * PASDB_MMOvcdRec;

typedef struct _tagASDB_MMOvaultRec
{
    long    id;                                         //  1 Auto increment, PK__vault
	long    vault_padding;
    char    vaultname[24];                              //  2 not null
    char    descript[256];                              //  3
    long    blocal;                                     //  4
    long    maxslots;                                   //  5
    long    actvslots;                                  //  6
    long    freeslots;                                  //  7
    long    cdate;                                      //  8 datetime not null
    long    ldate;                                      //  9 datetime
    char    cuserid[32];                                // 10
    char    luserid[32];                                // 11
    char    reserved[16];                               //
}ASDB_MMOvaultRec, * PASDB_MMOvaultRec;

typedef struct _tagASDB_MMOvrotationRec
{
    long    id;                                         //  1 Auto increment, PK__vrotation
    long    rotation_padding;
    long    vaultid;                                    //  2 not null, FK_vrotationvaultid
    long    scheduleid;                                 //  3 not null, FK_vrotationscheduleid
    char    vaultname[20];
    char    schedule[80];
    char    descript[256];                              //  4
    char    cuserid[32];                                // 14
    char    luserid[32];                                // 15
    long    sequence;                                   //  5
    long    retholddays;                                //  6
    long    retcycle;                                   //  7
    long    retcreat;                                   //  8
    short   retperm;                                    //  9
    short   retexpir;                                   // 10
    long    retdate;                                    // 11 datetime
    long    cdate;                                      // 12 datetime not null
    long    ldate;                                      // 13 datetime
    char    reserved[16];                               // 16
}ASDB_MMOvrotationRec, * PASDB_MMOvrotationRec;

typedef struct _tagASDB_MMOvslotRec
{
    long    id;                                         //  1 Auto increment, PK__vslot
    long    vaultid;                                    //  2 not null, FK_vslotvaultid
    char    vaultname[24];
    long    previd;                                     //  3 
    long    prevvaultid;                                //  4
    char    prevvaultname[24];                          //  5
    long    empty;                                      //  6
    long    mediatype;                                  //  7 
    long    mediaclass;                                 //  8
    long    sequence;                                   //  9 
    short   status;                                     // 10
	short	bexport;
    long    expdt;                                      // 15 datetime
    long    tapeid;                                     // tape_id in tape_record;
    char    medianame[24];                              // 11 volset
    long    randomid;                                   // 12 
    long    seqnum;                                     // 13 
	long    raidid;                                     //    current raid id
    long    tapelastwrite;                              // tape_id in tape_record;
    long    tapelastread;                               // tape_id in tape_record;
    char    serialno[32];                               // 14 volser
    char    tapeuuid[24];
    char    dbname[40];                                 // 16 
    long    vcdid;                                      // 17 
    long    scheduleid;                                 // 18 FK_vslotscheduleid
    char    schedule[80];
    char    prevedmid[80];                              // 20
    long    cdate;                                      // 21 datetime not null
    long    ldate;                                      // 22 datetime
    char    cuserid[32];                                // 23 
    char    luserid[32];                                // 24
	int  	cycle;         
    long    rotationid;    
    long    exceptid;      
    char    HostName[64];
    char    reserved[16];                               //
	
}ASDB_MMOvslotRec, * PASDB_MMOvslotRec;

typedef struct _tagASDB_MMOvmovementRec
{
    long    scheduleid;                                 //  1
    long    timedate;
    char    schedule[80];
    long    fromvaultid;                                //  2 not null
    long    tovaultid;                                  //  3 not null
    long    fromrotation;                               //  4 not null
    long    torotation;                                 //  5 not null
    char    medianame[24];                              //  6 volset
    long    randomid;                                   //  7
    long    seqnum;                                     //  8
    long    raidid;                                     //    current raid id
    long    tapeid;                                     //    tape internal id
    char    serialno[32];                               //  9 volser
    char    tapeuuid[24];
    short   slotcnt;                                    // 10
	short   bexport; 
    short   mediaclass;                                 // 11
    short   mediatype;                                  // 12
    short   status;                                     // 13
    char    dbname[40];                                 // 14
	long    fromslotnum;     
    long    toslotnum;
    char    reserved[16];                               //
}ASDB_MMOvmovementRec, * PASDB_MMOvmovementRec;

typedef struct _tagASDB_MMOvstatusRec
{
    short   status;                                     //  1
    short   padding1;                                   //    padding field (4 byte alignment)
    long    padding2;                                   //    padding field (4 byte alignment)
    char    who[32];                                    //  2
    char    what[16];                                   //  3
    char    vwhere[64];                                 //  4
    long    whendate;                                   //  5 datetime
    long    finidate;                                   //  6 datetime
    char    lastdsn[80];                                //  7
    char    reserved[16];                               //
}ASDB_MMOvstatusRec, * PASDB_MMOvstatusRec;

typedef struct _tagASDB_VaultSelection
{
    long    VCDID; 
	short   vcdtype;
	char    vaultname[24];
    long    scheduleID;
    ASDB_TapeExRec media;
}ASDB_VaultSelection, * PASDB_VaultSelection;

typedef ASDB_MMOvslotRec ASDB_MMOvinventory;
typedef PASDB_MMOvslotRec PASDB_MMOvinventory;
/*
typedef struct _tagASDB_MMOvinventory
{
    long    vauldID;                                   
    ASDB_TapeExRec media;
}ASDB_MMOvinventory, * PASDB_MMOvinventory;
*/

typedef struct _tagASDB_MMOvreportname
{
    char filename[32];     //for shipping, SYYYYMMDDHHMMSS
                           //for receiving, RYYYYMMDDHHMMSS
}ASDB_MMOvreportname, * PASDB_MMOvreportname;

#pragma pack()

#endif    /* ASDBMMOTBL_H */

