//
// Error code for HAVhdUtility
//
#ifndef _HA_VHD_UTILITY_RC_INCLUDE_
#define _HA_VHD_UTILITY_RC_INCLUDE_

#define E_VHD_COV_SUCCESS                0
#define E_VHD_COV_FAILED                 0xFFFFFFFF

#define E_VHD_COV_MASK                   0xA00E0000
#define E_VHD_COV_BASE                   0xA00EA000
// #define VHD_COV_ERROR_CODE(err)   ((err) & 0xFFFF)

#define E_VHD_COV_INVALID_ARG            0xA00EA001
#define E_VHD_COV_CREATE_FILE            0xA00EA002
#define E_VHD_COV_READ_FOOTER            0xA00EA003
#define E_VHD_COV_INVALID_TYPE           0xA00EA004
#define E_VHD_COV_READ_HEADER            0xA00EA005
#define E_VHD_COV_CHECKSUM               0xA00EA006
#define E_VHD_COV_READBAT                0xA00EA007
#define E_VHD_COV_READBITMAP             0xA00EA008
#define E_VHD_COV_ERROR_MAGIC            0xA00EA009
#define E_VHD_COV_CREATE_IDXFILE         0xA00EA00A
#define E_VHD_COV_NEED_MORE_INPUTBUF     0xA00EA00B

#define E_VHD_COV_SETPPATH_INVALID_TYPE  0xA00EA00C //invalid parent path type
#define E_VHD_COV_GETPPATH_INVALID_TYPE  0xA00EA00D
#define E_VHD_COV_GETPPATH_NOTEXIST      0xA00EA00E //no parent path
#define E_VHD_COV_GETPPATH_NO_PARENT     0xA00EA00F //base vhd, no need to get parent path
#define E_VHD_COV_SETPPATH_NO_PARENT     0xA00EA010 //base vhd, no need to set parent path
#define E_VHD_COV_SETPTIME_NO_PARENT     0xA00EA011 //base vhd, no need to set parent time
#define E_VHD_COV_GETPTIME_NO_PARENT     0xA00EA012 //base vhd, no need to get parent time

#define E_VHD_COV_VHD2_NO_PTIME          0xA00EA013

#define E_VHD_COV_WRITEHEADER_FAILED     0xA00EA014 //write file header failed
#define E_VHD_COV_SETFILEPOS             0xA00EA015 //set file pos failed
#define E_VHD_COV_WRITEFOOTER_FAILED     0xA00EA016 //write file footer failed

#define E_VHD_COV_UNUSED_BAT_ENTRY       0xA00EA017
#define E_VHD_COV_NO_ENOUGH_MEM          0xA00EA018 //not enough memory
#define E_VHD_COV_FILE_SIZE_LIMITED      0xA00EA019 //idx size limited
#define E_VHD_COV_INTERNAL_ERROR         0xA00EA01A
#define E_VHD_COV_READ_DATA              0xA00EA01B //read data failed
#define E_VHD_COV_UPDATEMTA              0xA00EA01C //update MTA failed
#define E_VHD_COV_LOCKSESS_FAILED        0xA00EA01D //lock session failed

//this is a special error code. no session exist when replicate cattalog only job start.
#define E_VHD_COV_NOSESS_JOBSTART        0xA00EA01E 

#define E_VHD_COV_CATALOG_LOCKED_BY_REP_OUT        0xA00EA01F //replicating catalog job can't lock locked by replicating out job


#define E_VHD_COV_RANGE_MOREDATA         0xA00EA050 //Have more range.
#define E_VHD_COV_WEBSRV_AUTHENTICATION_FAILED  0xA00EA051 //<huvfe01>2013-12-3 log web service error
#define E_VHD_COV_WEBSRV_JOB_COUNT_EXCEED_LIMIT 0xA00EA052
#define E_VHD_COV_WEBSRV_JOB_PLAN_IS_PAUSED 0xA00EA053
#define E_VHD_COV_WEBSRV_JOB_PLAN_IS_PURGING 0xA00EA054
#define E_VHD_COV_WEBSRV_TIMEOUT_EXPIRED        0xA00EA060

#endif //_HA_VHD_UTILITY_RC_INCLUDE_
