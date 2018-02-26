#ifndef _NET_CMD_H_
#define _NET_CMD_H_



#pragma warning(disable: 4200)

//this command send to primary server to start the backup thread
#define PRIMARY_BACKUP_FILE				1

//this command send to member server to start the backup thread
#define MEMBER_BACKUP_FILE				2

//this command send to data server to start the backup thread
#define STORAGE_BACKUP_FILE				3


//INIT commands
#define PRIMARY_INIT_BACKUP             12
#define PRIMARY_INIT_BACKUP_RESP        13
#define PRIMARY_INIT_BACKUP_RESP_ERR    14

#define MEMBER_INIT_BACKUP              22
#define MEMBER_INIT_BACKUP_RESP         23
#define MEMBER_INIT_BACKUP_RESP_ERR     24

#define STORAGE_INIT_BACKUP             32
#define STORAGE_INIT_BACKUP_RESP        33
#define STORAGE_INIT_BACKUP_RESP_ERR    34

//this command send to member server
//it is used tansfer the hash key to member server
#define TRANS_BACKUP_HASH				100
#define TRANS_BACKUP_HASH_RESP			101
#define TRANS_BACKUP_HASH_RESP_ERR		102

//this command send to data server
//it is used tansfer the data to data server
#define TRANS_BACKUP_DATA				200
#define TRANS_BACKUP_DATA_RESP			201
#define TRANS_BACKUP_DATA_RESP_ERR		202

#define BACKUP_DATA_FLUSH				300
#define BACKUP_DATA_FLUSH_RESP			301
#define BACKUP_DATA_FLUSH_RESP_ERR		302

//this command send to the primary server 
//the backup job finished
#define PRIMARY_CLOSE_BACKUP			400
#define PRIMARY_CLOSE_BACKUP_RESP		401
#define PRIMARY_CLOSE_BACKUP_RESP_ERR	402

//this command send to the member server 
//the backup job finished
#define MEMBER_CLOSE_BACKUP			410
#define MEMBER_CLOSE_BACKUP_RESP		411
#define MEMBER_CLOSE_BACKUP_RESP_ERR	412

//this command send to the data server 
//the backup job finished
#define STORAGE_CLOSE_BACKUP			420
#define STORAGE_CLOSE_BACKUP_RESP		421
#define STORAGE_CLOSE_BACKUP_RESP_ERR	422



//// Restore commands - BEGIN
//this command send to primary server to start the restore thread
#define PRIMARY_RESTORE_FILE			1001
#define PRIMARY_RESTORE_FILE_RESP		1002
#define PRIMARY_RESTORE_FILE_RESP_ERR	1003

//this command send to member server to start the restore thread
#define MEMBER_RESTORE_FILE				1004
#define MEMBER_RESTORE_FILE_RESP		1005
#define MEMBER_RESTORE_FILE_RESP_ERR	1006

//this command send to data server to start the restore thread
#define STORAGE_RESTORE_FILE			1007
#define STORAGE_RESTORE_FILE_RESP		1008
#define STORAGE_RESTORE_FILE_RESP_ERR	1009

//INIT commands
#define PRIMARY_INIT_RESTORE            1012
#define PRIMARY_INIT_RESTORE_RESP       1013
#define PRIMARY_INIT_RESTORE_RESP_ERR   1014

#define PRIMARY_INIT_RESTORE_EX			1015
#define PRIMARY_INIT_RESTORE_EX_RESP	1016
#define PRIMARY_INIT_RESTORE_EX_RESP_ERR	1017

#define STORAGE_INIT_RESTORE            1032
#define STORAGE_INIT_RESTORE_RESP       1033
#define STORAGE_INIT_RESTORE_RESP_ERR   1034

//Send to primary server to seek the virtual file with given offset
#define PRIMARY_SEEK_FILE				1200
#define PRIMARY_SEEK_FILE_RESP			1201
#define PRIMARY_SEEK_FILE_RESP_ERR		1202

#define PRIMARY_SEEK_FILE_EX				1203
#define PRIMARY_SEEK_FILE_EX_RESP			1204
#define PRIMARY_SEEK_FILE_EX_RESP_ERR		1205

//Send to primary server to get array of real data
#define PRIMARY_READ_FILE				1300
#define PRIMARY_READ_FILE_RESP			1301
#define PRIMARY_READ_FILE_RESP_ERR		1302

#define PRIMARY_READ_FILE_EX				1303
#define PRIMARY_READ_FILE_EX_RESP			1304
#define PRIMARY_READ_FILE_EX_RESP_ERR		1305

#if GDD_ANSYNC_READ
#define PRIMARY_READ_FILES_WITH_SEQNO			1306
#define PRIMARY_READ_FILES_WITH_SEQNO_RESP		1307
#define PRIMARY_READ_FILES_WITH_SEQNO_RESP_ERR	1308
#endif //GDD_ANSYNC_READ

#ifdef GDD_REPLICATION_OPTIMIZATION
#define PRIMARY_READ_FILE_EX2				1309
#define PRIMARY_READ_FILE_EX2_RESP			1310
#define PRIMARY_READ_FILE_EX2_RESP_ERR		1311

#define PRIMARY_READ_FILE_WITH_OFFSET_EX2				1312
#define PRIMARY_READ_FILE_WITH_OFFSET_EX2_RESP			1313
#define PRIMARY_READ_FILE_WITH_OFFSET_EX2_RESP_ERR		1314
#endif //GDD_REPLICATION_OPTIMIZATION

#define PRIMARY_READ_FILE_WITH_OFFSET_EX				1315
#define PRIMARY_READ_FILE_WITH_OFFSET_EX_RESP			1316
#define PRIMARY_READ_FILE_WITH_OFFSET_EX_RESP_ERR		1317

//Send to storage server to read real data
#define STORAGE_READ_FILE				1400
#define STORAGE_READ_FILE_RESP			1401
#define STORAGE_READ_FILE_RESP_ERR		1402

#if GDD_ANSYNC_READ
#define STORAGE_READ_FILES_WITH_SEQNO				1403
#define STORAGE_READ_FILES_WITH_SEQNO_RESP			1404
#define STORAGE_READ_FILES_WITH_SEQNO_RESP_ERR		1405
#endif //GDD_ANSYNC_READ

#ifdef GDD_REPLICATION_OPTIMIZATION
#define STORAGE_USE_ASYNCREAD                 1406
#define STORAGE_USE_ASYNCREAD_RESP            1407
#define STORAGE_USE_ASYNCREAD_RESP_ERR        1408
//For replication optimization
#define STORAGE_ASYNCREAD_DATA                    1409
#define STORAGE_ASYNCREAD_DATA_RESP               1410
#define STORAGE_ASYNCREAD_DATA_RESP_ERR           1411
//For replication optimization
#define STORAGE_ASYNCREAD_RESULT                  1412
#define STORAGE_ASYNCREAD_RESULT_RESP             1413
#define STORAGE_ASYNCREAD_RESULT_RESP_ERR         1414
//For replication optimization
#define STORAGE_ASYNCREAD_FLUSH                   1415
#define STORAGE_ASYNCREAD_FLUSH_RESP              1416
#define STORAGE_ASYNCREAD_FLUSH_RESP_ERR          1417
#endif //GDD_REPLICATION_OPTIMIZATION

//hefzh01 ne define for async read 
#define NEW_INDEX_RESTORE_FILE				(1500)
#define NEW_INDEX_RESTORE_FILE_RESP			(NEW_INDEX_RESTORE_FILE + 1)
#define NEW_INDEX_RESTORE_FILE_RESP_ERR		(NEW_INDEX_RESTORE_FILE + 2)

#define NEW_INDEX_INIT_RESTORE				(NEW_INDEX_RESTORE_FILE + 10)
#define NEW_INDEX_INIT_RESTORE_RESP			(NEW_INDEX_INIT_RESTORE + 1)
#define NEW_INDEX_INIT_RESTORE_RESP_ERR		(NEW_INDEX_INIT_RESTORE + 2)

#define NEW_INDEX_DEINIT_RESTORE			(NEW_INDEX_RESTORE_FILE + 20)
#define NEW_INDEX_DEINIT_RESTORE_RESP		(NEW_INDEX_DEINIT_RESTORE + 1)
#define NEW_INDEX_DEINIT_RESTORE_RESP_ERR	(NEW_INDEX_DEINIT_RESTORE + 2)

#define NEW_INDEX_ROLE_READ					(NEW_INDEX_RESTORE_FILE + 30)
#define NEW_INDEX_ROLE_READ_RESP			(NEW_INDEX_ROLE_READ + 1)
#define NEW_INDEX_ROLE_READ_RESP_ERR		(NEW_INDEX_ROLE_READ + 2)

#define NEW_INDEX_ROLE_FLUSH				(NEW_INDEX_RESTORE_FILE + 40)
#define NEW_INDEX_ROLE_FLUSH_RESP			(NEW_INDEX_ROLE_FLUSH + 1)
#define NEW_INDEX_ROLE_FLUSH_RESP_ERR		(NEW_INDEX_ROLE_FLUSH + 2)


#define NEW_DATA_RESTORE_FILE				(1600)
#define NEW_DATA_RESTORE_FILE_RESP			(NEW_DATA_RESTORE_FILE + 1)
#define NEW_DATA_RESTORE_FILE_RESP_ERR		(NEW_DATA_RESTORE_FILE + 2)

#define NEW_DATA_INIT_RESTORE				(NEW_DATA_RESTORE_FILE + 10)
#define NEW_DATA_INIT_RESTORE_RESP			(NEW_DATA_INIT_RESTORE + 1)
#define NEW_DATA_INIT_RESTORE_RESP_ERR		(NEW_DATA_INIT_RESTORE + 2)

#define NEW_DATA_DEINIT_RESTORE				(NEW_DATA_RESTORE_FILE + 20)
#define NEW_DATA_DEINIT_RESTORE_RESP		(NEW_DATA_DEINIT_RESTORE + 1)
#define NEW_DATA_DEINIT_RESTORE_RESP_ERR	(NEW_DATA_DEINIT_RESTORE + 2)

#define NEW_DATA_ROLE_READ					(NEW_DATA_RESTORE_FILE + 30)
#define NEW_DATA_ROLE_READ_RESP				(NEW_DATA_ROLE_READ + 1)
#define NEW_DATA_ROLE_READ_RESP_ERR			(NEW_DATA_ROLE_READ + 2)

#define NEW_DATA_ROLE_FLUSH					(NEW_DATA_RESTORE_FILE + 40)
#define NEW_DATA_ROLE_FLUSH_RESP			(NEW_DATA_ROLE_FLUSH + 1)
#define NEW_DATA_ROLE_FLUSH_RESP_ERR		(NEW_DATA_ROLE_FLUSH + 2)

#define NEW_DATA_ROLE_ASYNCREAD_DATA			(NEW_DATA_RESTORE_FILE + 50)
#define NEW_DATA_ROLE_ASYNCREAD_DATA_RESP		(NEW_DATA_ROLE_ASYNCREAD_DATA + 1)
#define NEW_DATA_ROLE_ASYNCREAD_DATA_RESP_ERR	(NEW_DATA_ROLE_ASYNCREAD_DATA + 2)

#define NEW_DATA_ROLE_ASYNCREAD_RESULT			(NEW_DATA_RESTORE_FILE + 55)
#define NEW_DATA_ROLE_ASYNCREAD_RESULT_RESP		(NEW_DATA_ROLE_ASYNCREAD_RESULT + 1)
#define NEW_DATA_ROLE_ASYNCREAD_RESULT_RESP_ERR	(NEW_DATA_ROLE_ASYNCREAD_RESULT + 2)

#define NEW_DATA_ROLE_ASYNCREAD_FLUSH			(NEW_DATA_RESTORE_FILE + 60)
#define NEW_DATA_ROLE_ASYNCREAD_FLUSH_RESP		(NEW_DATA_ROLE_ASYNCREAD_FLUSH + 1)
#define NEW_DATA_ROLE_ASYNCREAD_FLUSH_RESP_ERR	(NEW_DATA_ROLE_ASYNCREAD_FLUSH + 2)

//DEINIT commands
#define PRIMARY_CLOSE_RESTORE			1912
#define PRIMARY_CLOSE_RESTORE_RESP		1913
#define PRIMARY_CLOSE_RESTORE_RESP_ERR	1914

#define PRIMARY_CLOSE_RESTORE_EX			1915
#define PRIMARY_CLOSE_RESTORE_EX_RESP		1916
#define PRIMARY_CLOSE_RESTORE_EX_RESP_ERR	1917

#define STORAGE_CLOSE_RESTORE			1932
#define STORAGE_CLOSE_RESTORE_RESP		1933
#define STORAGE_CLOSE_RESTORE_RESP_ERR	1934
//// Restore commands - END

#define DELETE_FILE						2000
#define DELETE_FILE_RESP				2001
#define DELETE_FILE_RESP_ERR			2002

#define SET_BLOCK_SIZE					2500
#define SET_BLOCK_SIZE_RESP				2501
#define SET_BLOCK_SIZE_RESP_ERR			2502


/*
#define FIND_FIRST_FILE					2100
#define FIND_FIRST_FILE_RESP			2101
#define FIND_FIRST_FILE_RESP_ERR		2102


#define FIND_NEXT_FILE					2200
#define FIND_NEXT_FILE_RESP				2201
#define FIND_NEXT_FILE_RESP_ERR			2202

#define FIND_CLOSE  					2300
#define FIND_CLOSE_RESP					2301
#define FIND_CLOSE_RESP_ERR				2302

#define CREATE_DIR						2400
#define CREATE_DIR_RESP					2401
#define CREATE_DIR_RESP_ERR				2402

#define REMOVE_DIR						2500
#define REMOVE_DIR_RESP					2501
#define REMOVE_DIR_RESP_ERR				2502
*/

//this command is used to reconnect the peer server when network down
#define CONTINUE_JOB					2600
#define CONTINUE_JOB_RESP				2601
#define CONTINUE_JOB_RESP_ERR			2602

//get the member server ip and port from primary server
#define GET_MEMBER_SERVER_INFO			2700
#define GET_MEMBER_SERVER_INFO_RESP		2701
#define GET_MEMBER_SERVER_INFO_RESP_ERR	2702

//get the data server ip and port from primary server
#define GET_DATA_SERVER_INFO			2800
#define GET_DATA_SERVER_INFO_RESP		2801
#define GET_DATA_SERVER_INFO_RESP_ERR	2802


//this command send to member server
//GDD client will get the duplicated hash metadata from the member server
#define GET_DUP_HASH_METADATA			2900
#define GET_DUP_HASH_METADATA_RESP		2901
#define GET_DUP_HASH_METADATA_RESP_ERR	2902

//this command send to the primary server
//it used to let the primary to append the index entry
#define TRANS_BACKUP_INDEX_ENTRY			3000
#define TRANS_BACKUP_INDEX_ENTRY_RESP		3001
#define TRANS_BACKUP_INDEX_ENTRY_RESP_ERR	3002

//this command send to the primary server
//it used to let the primary to append the ref file entry
#define TRANS_REF_META					3100
#define TRANS_REF_META_RESP				3101
#define TRANS_REF_META_RESP_ERR			3102

//this command send to the member server
//it used to let the member insert the unique hash key into memory
#define INSERT_HASH_KEY					3200
#define INSERT_HASH_KEY_RESP			3201
#define INSERT_HASH_KEY_RESP_ERR		3202

#define FLUSH_HASH_KEY					3210
#define FLUSH_HASH_KEY_RESP				3211
#define FLUSH_HASH_KEY_RESP_ERR			3212

//this command send to the primary server
//before client send TRANS_BACKUP_INDEX_ENTRY to primary server, 
//it is used to let the primary server know which member server the hash entry belong to
#define SEND_MEMBER_ID				3300
#define SEND_MEMBER_ID_RESP			3301
#define SEND_MEMBER_ID_RESP_ERR		3302


//this command send to the primary server
//before client send TRANS_BACKUP_INDEX_ENTRY to primary server, 
//it is used to let the primary server know which hash entry is duplicated, which hash entry is unique
#define MARK_SUSPEND_POINT				3403
#define MARK_SUSPEND_POINT_RESP			3404
#define MARK_SUSPEND_POINT_RESP_ERR		3405
//End adding

//for backup error handle command
#define ERR_HANDLE_PRIMARY_BACKUP_FILE			3500
#define ERR_HANDLE_HASH_BACKUP_FILE				3501
#define ERR_HANDLE_DATA_BACKUP_FILE				3502
#define ERR_HANDLE_PRIMARY_INIT_BACKUP			3503
#define ERR_HANDLE_PRIMARY_INIT_BACKUP2			3504
#define ERR_HANDLE_MEMBER_INIT_BACKUP			3505
//#define ERR_HANDLE_MEMBER_INIT_BACKUP2			3506
#define ERR_HANDLE_STORAGE_INIT_BACKUP			3507
#define ERR_HANDLE_STORAGE_INIT_BACKUP2			3508


#define ERR_HANDLE_PRIMARY_BACKUP_FILE_ERR			3509
#define ERR_HANDLE_PRIMARY_BACKUP_FILE_RESP			3510
#define ERR_HANDLE_HASH_BACKUP_FILE_ERR				3511
#define ERR_HANDLE_HASH_BACKUP_FILE_RESP			3512
#define ERR_HANDLE_DATA_BACKUP_FILE_ERR			    3513
#define ERR_HANDLE_DATA_BACKUP_FILE_RESP			3514
#define ERR_HANDLE_PRIMARY_INIT_BACKUP_ERR			3515
#define ERR_HANDLE_PRIMARY_INIT_BACKUP_RESP			3516

#define ERR_HANDLE_STORAGE_INIT_BACKUP_ERR			3517
#define ERR_HANDLE_STORAGE_INIT_BACKUP_RESP			3518

#define ERR_ERR_HANDLE_MEMBER_INIT_BACKUP_ERR		3519
#define ERR_ERR_HANDLE_MEMBER_INIT_BACKUP_RESP		3520

//End for error handle


//// Merge commands - BEGIN
//Client sends this command to primary service for it to start the merge thread.
#define PRIMARY_MERGE_FILE			    5001
#define PRIMARY_MERGE_FILE_RESP		    5002
#define PRIMARY_MERGE_FILE_RESP_ERR	    5003

//Client sends this command to primary service for it to prepare merge destination file.
#define PRIMARY_INIT_MERGE			    5011
#define PRIMARY_INIT_MERGE_RESP		    5012
#define PRIMARY_INIT_MERGE_RESP_ERR	    5013

//Client sends this command to primary service for it to prepare merge source files.
#define PRIMARY_MERGE_ASSIGN_SRC_FILES 			    5021
#define PRIMARY_MERGE_ASSIGN_SRC_FILES_RESP		    5022
#define PRIMARY_MERGE_ASSIGN_SRC_FILES_RESP_ERR	    5023

//Client sends this command and merging information to primary service for it to exchange index entries.
#define PRIMARY_MERGE_TRANS_OFFSET 			    5031
#define PRIMARY_MERGE_TRANS_OFFSET_RESP		    5032
#define PRIMARY_MERGE_TRANS_OFFSET_RESP_ERR	    5033

//Client sends this command to primary service to report errors.
#define PRIMARY_MERGE_REPORT_ERROR		        5041
#define PRIMARY_MERGE_REPORT_ERROR_RESP	        5042
#define PRIMARY_MERGE_REPORT_ERROR_RESP_ERR     5043

//Client sends this command to primary service to request the service to build new index file
#define  PRIMARY_MERGE_PRE_CLOSE				5051
#define  PRIMARY_MERGE_PRE_CLOSE_RESP			5052
#define  PRIMARY_MERGE_PRE_CLOSE_RESP_ERR		5053

#define   PRIMARY_MERGE_DELETE_FILE				5054
#define   PRIMARY_MERGE_DELETE_FILE_RESP		5055
#define   PRIMARY_MERGE_DELETE_FILE_RESP_ERR	5056


//Client sends this command to primary service for it to clean up resources.
#define PRIMARY_CLOSE_MERGE 			        5061
#define PRIMARY_CLOSE_MERGE_RESP		        5062
#define PRIMARY_CLOSE_MERGE_RESP_ERR	        5063

#define PRIMARY_MERGE_KEEP_ALIVE 			    5070
#define PRIMARY_MERGE_UNKNOW_CMD 			    5071


//// Merge commands - END
//// Fast Purge commands Start		<
#define PRIMARY_FAST_PURGE_BASE					6000

#define PRIMARY_FAST_PURGE_BEGIN				PRIMARY_FAST_PURGE_BASE +1
#define PRIMARY_FAST_PURGE_BEGIN_RESP			PRIMARY_FAST_PURGE_BASE +2
#define PRIMARY_FAST_PURGE_BEGIN_RESP_ERR	    PRIMARY_FAST_PURGE_BASE +3

#define PRIMARY_FAST_PURGE_ITEM					PRIMARY_FAST_PURGE_BASE +4
#define PRIMARY_FAST_PURGE_ITEM_RESP			PRIMARY_FAST_PURGE_BASE +5
#define PRIMARY_FAST_PURGE_ITEM_RESP_ERR	    PRIMARY_FAST_PURGE_BASE +6

#define PRIMARY_FAST_PURGE_END					PRIMARY_FAST_PURGE_BASE +7
#define PRIMARY_FAST_PURGE_END_RESP				PRIMARY_FAST_PURGE_BASE +8
#define PRIMARY_FAST_PURGE_END_RESP_ERR			PRIMARY_FAST_PURGE_BASE +9
//// Fast Purge commands End		<

#define REAL_PURGE_BASE							6100

#define REAL_PURGE_FILE							REAL_PURGE_BASE + 0
#define REAL_PURGE_FILE_RESP					REAL_PURGE_BASE + 1
#define REAL_PURGE_FILE_RESP_ERR				REAL_PURGE_BASE + 2

#define REAL_PURGE_SEND_INDEX_ENTRIES			REAL_PURGE_BASE + 3
#define REAL_PURGE_SEND_INDEX_ENTRIES_RESP		REAL_PURGE_BASE + 4
#define REAL_PURGE_SEND_INDEX_ENTRIES_RESP_ERR	REAL_PURGE_BASE + 5

#define REAL_PURGE_SEND_REF_ENTRIES				REAL_PURGE_BASE + 6
#define REAL_PURGE_SEND_REF_ENTRIES_RESP		REAL_PURGE_BASE + 7
#define REAL_PURGE_SEND_REF_ENTRIES_RESP_ERR	REAL_PURGE_BASE + 8

#define REAL_PURGE_GET_HOLES					REAL_PURGE_BASE + 9
#define REAL_PURGE_GET_HOLES_RESP				REAL_PURGE_BASE + 10
#define REAL_PURGE_GET_HOLES_RESP_ERR			REAL_PURGE_BASE + 11

#define REAL_PURGE_GET_HASH_STATUS				REAL_PURGE_BASE + 12
#define REAL_PURGE_GET_HASH_STATUS_RESP			REAL_PURGE_BASE + 13
#define REAL_PURGE_GET_HASH_STATUS_RESP_ERR		REAL_PURGE_BASE + 14

#define REAL_PURGE_SEND_SUSPEND_REF_ENTRIES				REAL_PURGE_BASE + 15
#define REAL_PURGE_SEND_SUSPEND_REF_ENTRIES_RESP		REAL_PURGE_BASE + 16
#define REAL_PURGE_SEND_SUSPEND_REF_ENTRIES_RESP_ERR	REAL_PURGE_BASE + 17

#define RECLAIM_BASE							6200

#define RECLAIM_FILE							RECLAIM_BASE + 0
#define RECLAIM_FILE_RESP						RECLAIM_BASE + 1
#define RECLAIM_FILE_RESP_ERR					RECLAIM_BASE + 2

#define RECLAIM_ITEM_START						RECLAIM_BASE + 3
#define RECLAIM_ITEM_START_RESP					RECLAIM_BASE + 4
#define RECLAIM_ITEM_START_RESP_ERR				RECLAIM_BASE + 5

#define RECLAIM_OPEN_TEMP_DATA_FILE				RECLAIM_BASE + 6
#define RECLAIM_OPEN_TEMP_DATA_FILE_RESP		RECLAIM_BASE + 7
#define RECLAIM_OPEN_TEMP_DATA_FILE_RESP_ERR	RECLAIM_BASE + 8

#define RECLAIM_WRITE_TEMP_DATA_FILE			RECLAIM_BASE + 9
#define RECLAIM_WRITE_TEMP_DATA_FILE_RESP		RECLAIM_BASE + 10
#define RECLAIM_WRITE_TEMP_DATA_FILE_RESP_ERR	RECLAIM_BASE + 11

#define RECLAIM_CLOSE_TEMP_DATA_FILE			RECLAIM_BASE + 12
#define RECLAIM_CLOSE_TEMP_DATA_FILE_RESP		RECLAIM_BASE + 13
#define RECLAIM_CLOSE_TEMP_DATA_FILE_RESP_ERR	RECLAIM_BASE + 14

#define RECLAIM_REMOVE_TEMP_DATA_FILE			RECLAIM_BASE + 15
#define RECLAIM_REMOVE_TEMP_DATA_FILE_RESP		RECLAIM_BASE + 16
#define RECLAIM_REMOVE_TEMP_DATA_FILE_RESP_ERR	RECLAIM_BASE + 17

#define RECLAIM_OVERWRITE_DATA_FILE				RECLAIM_BASE + 18
#define RECLAIM_OVERWRITE_DATA_FILE_RESP		RECLAIM_BASE + 19
#define RECLAIM_OVERWRITE_DATA_FILE_RESP_ERR	RECLAIM_BASE + 20

#define RECLAIM_DELETE_DATA_FILE				RECLAIM_BASE + 21
#define RECLAIM_DELETE_DATA_FILE_RESP			RECLAIM_BASE + 22
#define RECLAIM_DELETE_DATA_FILE_RESP_ERR		RECLAIM_BASE + 23

#define RECLAIM_ZERO_DATA_FILE					RECLAIM_BASE + 24
#define RECLAIM_ZERO_DATA_FILE_RESP				RECLAIM_BASE + 25
#define RECLAIM_ZERO_DATA_FILE_RESP_ERR			RECLAIM_BASE + 26

#define RECLAIM_ITEM_END						RECLAIM_BASE + 27
#define RECLAIM_ITEM_END_RESP					RECLAIM_BASE + 28
#define RECLAIM_ITEM_END_RESP_ERR				RECLAIM_BASE + 29

#define RECLAIM_QUERY_DATA_PATH_SUPPORT_SPARSE			RECLAIM_BASE + 30
#define RECLAIM_QUERY_DATA_PATH_SUPPORT_SPARSE_RESP		RECLAIM_BASE + 31
#define RECLAIM_QUERY_DATA_PATH_SUPPORT_SPARSE_ERROR	RECLAIM_BASE + 32


#define RECLAIM_RENAME_DATASLICE						RECLAIM_BASE + 33
#define RECLAIM_RENAME_DATASLICE_RESP					RECLAIM_BASE + 34
#define RECLAIM_RENAME_DATASLICE_RESP_ERROR				RECLAIM_BASE + 35



//////////////////////////////////////////////////////////////////////////////
//Purge/reclaim status query
#define PURGE_STATUS_BASE							6400

#define PURGE_STATUS_BEGIN							PURGE_STATUS_BASE + 0
#define PURGE_STATUS_BEGIN_RESP						PURGE_STATUS_BASE + 1
#define PURGE_STATUS_BEGIN_RESP_ERR					PURGE_STATUS_BASE + 2

#define PURGE_STATUS_END							PURGE_STATUS_BASE + 3
#define PURGE_STATUS_END_RESP						PURGE_STATUS_BASE + 4
#define PURGE_STATUS_END_RESP_ERR					PURGE_STATUS_BASE + 5

#define PURGE_STATUS_QUERY							PURGE_STATUS_BASE + 6
#define PURGE_STATUS_QUERY_RESP						PURGE_STATUS_BASE + 7
#define PURGE_STATUS_QUERY_RESP_ERR					PURGE_STATUS_BASE + 8

#define PURGE_STATUS_FORCESTART_RECLAIM				PURGE_STATUS_BASE + 9
#define PURGE_STATUS_FORCESTART_RECLAIM_RESP		PURGE_STATUS_BASE + 10
#define PURGE_STATUS_FORCESTART_RECLAIM_RESP_ERR	PURGE_STATUS_BASE + 11


//Role Management Commands -- Begin
#define PRIMARY_MANAGEMENT_BASE				    10000

#define PRIMARY_MANAGEMENT_BEGIN	            PRIMARY_MANAGEMENT_BASE + 1
#define PRIMARY_MANAGEMENT_BEGIN_RESP		    PRIMARY_MANAGEMENT_BASE + 2
#define PRIMARY_MANAGEMENT_BEGIN_RESP_ERR	    PRIMARY_MANAGEMENT_BASE + 3

#define PRIMARY_MANAGEMENT_INDEX_SPACE_USAGE			PRIMARY_MANAGEMENT_BASE + 4
#define PRIMARY_MANAGEMENT_INDEX_SPACE_USAGE_RESP		PRIMARY_MANAGEMENT_BASE + 5
#define PRIMARY_MANAGEMENT_INDEX_SPACE_USAGE_RESP_ERR	PRIMARY_MANAGEMENT_BASE + 6

#define PRIMARY_MANAGEMENT_GLB_DEDUPE_RATIO				PRIMARY_MANAGEMENT_BASE + 7
#define PRIMARY_MANAGEMENT_GLB_DEDUPE_RATIO_RESP		PRIMARY_MANAGEMENT_BASE + 8
#define PRIMARY_MANAGEMENT_GLB_DEDUPE_RATIO_RESP_ERR	PRIMARY_MANAGEMENT_BASE + 9

#define PRIMARY_MANAGEMENT_JOB_INFO						PRIMARY_MANAGEMENT_BASE + 10
#define PRIMARY_MANAGEMENT_JOB_INFO_RESP				PRIMARY_MANAGEMENT_BASE + 11
#define PRIMARY_MANAGEMENT_JOB_INFO_RESP_ERR			PRIMARY_MANAGEMENT_BASE + 12

#define PRIMARY_MANAGEMENT_SERVER_STATUS				PRIMARY_MANAGEMENT_BASE + 13
#define PRIMARY_MANAGEMENT_SERVER_STATUS_RESP			PRIMARY_MANAGEMENT_BASE + 14
#define PRIMARY_MANAGEMENT_SERVER_STATUS_RESP_ERR		PRIMARY_MANAGEMENT_BASE + 15

#define PRIMARY_MANAGEMENT_PREPARE_STOP_ROLE            PRIMARY_MANAGEMENT_BASE + 994
#define PRIMARY_MANAGEMENT_PREPARE_STOP_ROLE_RESP	    PRIMARY_MANAGEMENT_BASE + 995
#define PRIMARY_MANAGEMENT_PREPARE_STOP_ROLE_RESP_ERR   PRIMARY_MANAGEMENT_BASE + 996

#define PRIMARY_MANAGEMENT_END  	            PRIMARY_MANAGEMENT_BASE + 997
#define PRIMARY_MANAGEMENT_END_RESP 		    PRIMARY_MANAGEMENT_BASE + 998
#define PRIMARY_MANAGEMENT_END_RESP_ERR 	    PRIMARY_MANAGEMENT_BASE + 999


#define MEMBER_MANAGEMENT_BASE				    11000

#define MEMBER_MANAGEMENT_BEGIN	                MEMBER_MANAGEMENT_BASE + 1
#define MEMBER_MANAGEMENT_BEGIN_RESP		    MEMBER_MANAGEMENT_BASE + 2
#define MEMBER_MANAGEMENT_BEGIN_RESP_ERR	    MEMBER_MANAGEMENT_BASE + 3

#define MEMBER_MANAGEMENT_MEMORY_USAGE			MEMBER_MANAGEMENT_BASE + 4
#define MEMBER_MANAGEMENT_MEMORY_USAGE_RESP		MEMBER_MANAGEMENT_BASE + 5
#define MEMBER_MANAGEMENT_MEMORY_USAGE_RESP_ERR	MEMBER_MANAGEMENT_BASE + 6

#define MEMBER_MANAGEMENT_DISK_USAGE			MEMBER_MANAGEMENT_BASE + 7
#define MEMBER_MANAGEMENT_DISK_USAGE_RESP		MEMBER_MANAGEMENT_BASE + 8
#define MEMBER_MANAGEMENT_DISK_USAGE_RESP_ERR	MEMBER_MANAGEMENT_BASE + 9

#define MEMBER_MANAGEMENT_JOB_INFO				MEMBER_MANAGEMENT_BASE + 10
#define MEMBER_MANAGEMENT_JOB_INFO_RESP			MEMBER_MANAGEMENT_BASE + 11
#define MEMBER_MANAGEMENT_JOB_INFO_RESP_ERR		MEMBER_MANAGEMENT_BASE + 12

#define MEMBER_MANAGEMENT_SERVER_STATUS				MEMBER_MANAGEMENT_BASE + 13
#define MEMBER_MANAGEMENT_SERVER_STATUS_RESP		MEMBER_MANAGEMENT_BASE + 14
#define MEMBER_MANAGEMENT_SERVER_STATUS_RESP_ERR	MEMBER_MANAGEMENT_BASE + 15

//Begin for get/set hash information from/to active GDD server
#define MEMBER_MANAGEMENT_GET_HASH_INFO				MEMBER_MANAGEMENT_BASE + 100
#define MEMBER_MANAGEMENT_GET_HASH_INFO_RESP		MEMBER_MANAGEMENT_BASE + 101
#define MEMBER_MANAGEMENT_GET_HASH_INFO_RESP_ERR	MEMBER_MANAGEMENT_BASE + 102

#define MEMBER_MANAGEMENT_SET_HASH_INFO				MEMBER_MANAGEMENT_BASE + 103
#define MEMBER_MANAGEMENT_SET_HASH_INFO_RESP		MEMBER_MANAGEMENT_BASE + 104
#define MEMBER_MANAGEMENT_SET_HASH_INFO_RESP_ERR	MEMBER_MANAGEMENT_BASE + 105
//End

//Begin for get hash init process
#define MEMBER_MANAGEMENT_GET_INIT_PROC				MEMBER_MANAGEMENT_BASE + 110
#define MEMBER_MANAGEMENT_GET_INIT_PROC_RESP		MEMBER_MANAGEMENT_BASE + 111
#define MEMBER_MANAGEMENT_GET_INIT_PROC_RESP_ERR	MEMBER_MANAGEMENT_BASE + 112
//End

#define MEMBER_MANAGEMENT_PREPARE_STOP_ROLE             MEMBER_MANAGEMENT_BASE + 994
#define MEMBER_MANAGEMENT_PREPARE_STOP_ROLE_RESP	    MEMBER_MANAGEMENT_BASE + 995
#define MEMBER_MANAGEMENT_PREPARE_STOP_ROLE_RESP_ERR    MEMBER_MANAGEMENT_BASE + 996

#define MEMBER_MANAGEMENT_END  	                MEMBER_MANAGEMENT_BASE + 997
#define MEMBER_MANAGEMENT_END_RESP 		        MEMBER_MANAGEMENT_BASE + 998
#define MEMBER_MANAGEMENT_END_RESP_ERR 	        MEMBER_MANAGEMENT_BASE + 999


#define STORAGE_MANAGEMENT_BASE				    12000

#define STORAGE_MANAGEMENT_BEGIN	            STORAGE_MANAGEMENT_BASE + 1
#define STORAGE_MANAGEMENT_BEGIN_RESP		    STORAGE_MANAGEMENT_BASE + 2
#define STORAGE_MANAGEMENT_BEGIN_RESP_ERR	    STORAGE_MANAGEMENT_BASE + 3

#define STORAGE_MANAGEMENT_DATA_SPACE_USAGE		     STORAGE_MANAGEMENT_BASE + 4
#define STORAGE_MANAGEMENT_DATA_SPACE_USAGE_RESP     STORAGE_MANAGEMENT_BASE + 5
#define STORAGE_MANAGEMENT_DATA_SPACE_USAGE_RESP_ERR STORAGE_MANAGEMENT_BASE + 6

#define STORAGE_MANAGEMENT_JOB_INFO				STORAGE_MANAGEMENT_BASE + 7
#define STORAGE_MANAGEMENT_JOB_INFO_RESP		STORAGE_MANAGEMENT_BASE + 8 
#define STORAGE_MANAGEMENT_JOB_INFO_RESP_ERR	STORAGE_MANAGEMENT_BASE + 9 

#define STORAGE_MANAGEMENT_SERVER_STATUS					STORAGE_MANAGEMENT_BASE + 10
#define STORAGE_MANAGEMENT_SERVER_STATUS_RESP			STORAGE_MANAGEMENT_BASE + 11
#define STORAGE_MANAGEMENT_SERVER_STATUS_RESP_ERR		STORAGE_MANAGEMENT_BASE + 12

#define STORAGE_MANAGEMENT_PREPARE_STOP_ROLE            STORAGE_MANAGEMENT_BASE + 994
#define STORAGE_MANAGEMENT_PREPARE_STOP_ROLE_RESP	    STORAGE_MANAGEMENT_BASE + 995
#define STORAGE_MANAGEMENT_PREPARE_STOP_ROLE_RESP_ERR   STORAGE_MANAGEMENT_BASE + 996

#define STORAGE_MANAGEMENT_END  	            STORAGE_MANAGEMENT_BASE + 997
#define STORAGE_MANAGEMENT_END_RESP 		    STORAGE_MANAGEMENT_BASE + 998
#define STORAGE_MANAGEMENT_END_RESP_ERR 	    STORAGE_MANAGEMENT_BASE + 999
//Role Management Commands -- End

#pragma pack(1)
typedef struct PktHeader
{
	UINT32 PkgLen;
	UINT32 Cmd;
}PKTHEADER,*PPKTHEADER;

typedef struct Packet
{
	PKTHEADER Head;
	int DataLen;
	char Buffer[0];
}PACKET,*PPACKET;

typedef struct InitPkt
{	
	UINT16 nMajVer;
	UINT16 nMinVer;

	UINT32 nFileNo;
	UINT32 nThreadId;
	UINT32 nBlockSize;
	UINT32 nMaxEntryPerSlice;
    UINT64 nFileSize;
	UINT32 nJobID;
	SYSTEMTIME SysTime;
	char szHostName[128];
	//The sector dize of data server path
	UINT32 nDataSectorSize;
}INITPKT,*PINITPKT;

typedef struct _File_Info_
{
	UINT32 nFileNo;
	UINT64 nFileSize;

}FILEINFO,*PFILE_INFO;

typedef struct InitPktEx
{	
	USHORT nMajVer;
	USHORT nMinVer;

	UINT32 nBlockSize;
	
	UINT32 nJobID;
	SYSTEMTIME SysTime;
	char szHostName[128];

	UINT32 nFiles;
	FILEINFO FileInfo[0];

}INITPKTEX,*PINITPKTEX;

typedef struct InitPkt4NewRestore
{	
    USHORT nMajVer;
    USHORT nMinVer;

    ULONG nBlockSize;
    ULONG nNumOfBlocksPerIdxRead;
    CHAR  cReserved[16];

    ULONG nJobID;
    SYSTEMTIME SysTime;
    char szHostName[128];

    ULONG nFiles;
    FILEINFO FileInfo[0];

}INITPKT4NEWRESTORE,*PINITPKTEX4NEWRESTORE;

union IOControlInfo
{
    UINT64 Offset;            // for backup
    UINT64 llTotalDataLen;    // for restore
    struct MERGEINFO            // for merge
    {
        UINT32 ulNumOfDestOffsetPkt;
        UINT32 ulNumOfSrcOffsetPkt;
    }MergeInfo;
#ifdef GDD_REPLICATION_OPTIMIZATION
	struct REPINFO
	{
		int lRequestId;
		UINT32 ulTotalDataLen;
	}RepInfo;
#endif //GDD_REPLICATION_OPTIMIZATION
};

typedef struct IOPacket
{
	PKTHEADER Head;
	int DataLen;
    IOControlInfo IoControlInfo; 
	char Buffer[0];
}IOPACKET,*PIOPACKET;


typedef struct ServerInfoPkt
{
	UINT32 nServerNo;

	//the server IP(UINT32),port(UINT32), the total length will be nServerNo*2*sizeof(UINT32)
	UINT32 *pServerInfo;

}SERVERINFOPKT,*PSERVERINFOPKT;

typedef struct InitBucketPkt
{
	UINT16 ui16BeginBucketID;
	UINT16 ui16EndBucketID;
    UINT64 ui64InitHTSize;
    UINT64 ui64TotalHTSize;
}INITBUCKETPKT,*PINITBUCKETPKT;

typedef struct DataBlockInfoPkt
{
    int lDataLen;              // The actual length of block. If compression enabled, it's the length after compression.
    UINT32 ulFileNo;             // The data file which holds the block.
    //INT64 llOffset;          // Offset to beginning of block. Block size aligned.
	UINT32 uiSliceID;	
	UINT32 uiSliceOffset;
    //UINT32 ulOffsetInBlock;    // Offset to beginning of data inside block. If compression enabled, it's the offset before compression. Comment out since we don't support this case for now. 
    INT64 llFlag;
}DATABLOCKINFOPKT,*PDATABLOCKINFOPKT;

#if GDD_ANSYNC_READ
typedef struct _GDDBLOCKINFO_2
{
	UINT32 ui32BlockSeqNo;
	union{
		struct {
			UINT32 nFileNo;
			UINT64 ui64BlockNoInGDDFile;
		};
		struct {
			UINT32 ui32RefFileNumber;
			UINT64 ui64RefFileEntryIndex;
		};
		struct {
			UINT32 ui32DataFileNumber;
			UINT32 ui32DataSliceID;
			UINT32 ui32DataSliceOffset;
			UINT16 ui16DataLen;
			UINT32 ui32DataFlag;
		};
	};
}GDDBLOCKINFO_2, *P_GDDBLOCKINFO_2;
typedef struct _GDDBLOCKINFO_3
{
	UINT32 ui32BlockSeqNo;
	UINT32 ui32BlockOffsetInBuffer;
	UINT32 ui32DataFileNumber;
	UINT32 ui32DataSliceID;
	UINT32 ui32DataSliceOffset;
	UINT16 ui16DataLen;
	UINT32 ui32DataFlag;
}GDDBLOCKINFO_3, *P_GDDBLOCKINFO_3;
#endif //GDD_ANSYNC_READ

typedef struct _st_PurgeFileInfoReq
{
	INITPKT			initPara;
	UINT32			ui32Flag;
	UINT32			dwFileCount;
	UINT32			FileNoArray[0];
}PurgeFileInfoReq,*PPurgeFileInfoReq;

typedef struct _st_PurgeFileInfoResp
{
	INITPKT			initPara;
	UINT32			dwResult;
	UINT32			dwFileCount;
	UINT32			FileNoArray[0];
}PurgeFileInfoResp,*PPurgeFileInfoResp;

typedef struct MergeDestOffsetPkt
{
    //INT64   nOffset;
    //UINT64  nBytesToMerge;
	INT64	  nStart;			// -1 means beyond one of the session end.
	UINT64	  nCount;			// from the nStart, the nCount for continuous block number to be merge. this should be >0
}MERGEDESTOFFSETPKT,*PMERGEDESTOFFSETPKT;

typedef struct MergeSrcOffsetPkt
{
    UINT32   nFileNo;
   // INT64   nOffset;
   // UINT64  nBytesToMerge;
	INT64	  nStart;		// should be >=0;
	UINT64	  nCount;		// Should be >0;
}MERGESRCOFFSETPKT,*PMERGESRCOFFSETPKT;

enum
{
	MERGE_CMD_MODE_EXEXUTE = 1,
	MERGE_CMD_MODE_QUERY
};

typedef struct _st_MergeInfoHeader
{
	UINT32		dwCmd;		// indicate cmd on server or on client
	UINT32		dwReplyCmd;
	UINT32		dwMode;		// Mode for first time or ...
	UINT32		dwResult;
	UINT64	ullCmdSeq;
}MergeInfoHeader,*PMergeInfoHeader;

typedef struct _st_MergeInitReq
{
	MergeInfoHeader	header;
	INITPKT			initPara;
}MergeInitReq,*PMergeInitReq;

typedef struct _st_MergeInitResp
{
	MergeInfoHeader	header;
	INITPKT			initPara;
}MergeInitResp,*PMergeInitResp;



typedef struct _st_MergeFileInfoReq
{
	MergeInfoHeader	header;
	UINT32			dwFileCount;
	UINT32			FileNoArray[0];
}MergeFileInfoReq,*PMergeFileInfoReq;

typedef struct _st_MergeFileInfoResp
{
	MergeInfoHeader	header;
	UINT32			dwFileCount;
	UINT64		FileSizeArray[0];
}MergeFileInfoResp,*PMergeFileInfoResp;

typedef struct _st_MergeFileDataReq
{
	MergeInfoHeader	header;
	UINT32			dwDestPktNum;
	UINT32			dwSrcPktNum;
	char			PktBuf[0];
}MergeFileDataReq,*PMergeFileDataReq;

typedef struct _st_MergeFileDataResp
{
	MergeInfoHeader	header;
	UINT32			dwFileCount;
	UINT64		FileSizeArray[0];
}MergeFileDataResp,*PMergeFileDataResp;

typedef struct _st_MergeFilePreCloseReq
{
	MergeInfoHeader	header;
}MergeFilePreCloseReq,*PMergeFilePreCloseReq;

typedef struct _st_MergeFilePreCloseResp
{
	MergeInfoHeader	header;
}MergeFilePreCloseResp,*PMergeFilePreCloseResp;

typedef struct _st_MergeFileDeleteReq
{
	MergeInfoHeader	header;
	UINT32			dwFileCount;
	UINT32			FileNoArray[0];
}MergeFileDeleteReq,*PMergeFileDeleteReq;

typedef struct _st_MergeFileDeleteResp
{
	MergeInfoHeader	header;
}MergeFileDeleteResp,*PMergeFileDeleteResp;

typedef struct _st_MergeJobEndReq
{
	MergeInfoHeader	header;
}MergeJobEndReq,*PMergeJobEndReq;

typedef struct _st_MergeJobEndResp
{
	MergeInfoHeader	header;
}MergeJobEndResp,*PMergeJobEndResp;


typedef struct _st_ClientErrorReport
{
	MergeInfoHeader	header;
	int			lclientError;
}ClientErrorReport,*PClientErrorReport;

typedef struct _st_ClientErrorReportResp
{
	MergeInfoHeader	header;
}ClientErrorReportResp,*PClientErrorReportResp;

typedef struct _st_MergeKeepAlive
{
	MergeInfoHeader	header;
}MergeKeepAlive,*PMergeKeepAlive;

typedef struct _GLB_DEDUPE_RATIO_
{
	UINT64    g_ui64_toal_src_size;				// Total source data size backed up by GDD, unit:byte
	UINT64	  g_ui64_total_unique_size;			// Total unique data size backed up by GDD, unit:byte
	UINT64    g_ui64_total_compressed_size;		// Total Compressed data size backed up by GDD, unit:byte
}GLB_DEDUPE_RATION,*PGLB_DEDUPE_RATION;

typedef struct _INDEX_PATH_DISK_SPACE_USAGE_
{
	UINT64	 g_ui64_Index_path_size;			//used by GDD system
	UINT64   g_ui64_Index_path_free_size;		//lpTotalNumberOfFreeBytes
	UINT64   g_ui64_index_path_available_size;	//lpFreeBytesAvailable
	UINT64   g_ui64_index_path_Total_size;		//lpTotalNumberOfBytes
	wchar_t	 pszPath[MAX_PATH];					//Directory that is used to store GDD Index and ref file.	
	wchar_t  Disk[2];							//Disk that index directory reside.
}INDEX_PATH_DISK_SPACE_USAGE,*PINDEX_PATH_DISK_SPACE_USAGE;

typedef struct _DATA_PATH_DISK_SPACE_USAGE_
{
	UINT64	 g_ui64_Data_path_size;				//used by GDD system
	UINT64   g_ui64_Data_path_free_size;		//lpTotalNumberOfFreeBytes
	UINT64   g_ui64_Data_path_available_size;	//lpFreeBytesAvailable
	UINT64   g_ui64_Data_path_Total_size;		//lpTotalNumberOfBytes
	wchar_t	 pszPath[MAX_PATH];					//Directory that is used to store GDD data.
	wchar_t  Disk[2];							//Disk that Data directory reside.
}DATA_PATH_DISK_SPACE_USAGE,*PDATA_PATH_DISK_SPACE_USAGE;

typedef struct _HASH_MEMORY_USAGE_
{
	UINT64	 g_ui64_Hash_Working_Usage;
	UINT64   g_ui64_Total_Mem_Size;
	UINT64   g_ui64_Free_Mem_Size;
}HASH_MEMORY_USAGE,*PHASH_MEMORY_USAGE;

typedef struct _HASH_MAP_DISK_SPACE_USAGE_
{
	UINT64	 g_ui64_Hash_Map_path_size;				//used by GDD system
	UINT64   g_ui64_Hash_Map_path_free_size;		//lpTotalNumberOfFreeBytes
	UINT64   g_ui64_Hash_Map_path_available_size;	//lpFreeBytesAvailable
	UINT64   g_ui64_Hash_Map_path_Total_size;		//lpTotalNumberOfBytes
	wchar_t	 pszPath[MAX_PATH];						//Directory that is used to store GDD data.
	wchar_t  Disk[2];								//Disk that Data directory reside.
}HASH_MAP_DISK_SPACE_USAGE, *PHASH_MAP_DISK_SPACE_USAGE;

typedef struct _HASH_STATUS
{	
	UINT32  dwStoreMode;					//Memory or SSD mode

	INT64  i64MemoryTotalSize;			//Physical memory total size
	INT64  i64MemoryAvailSize;			//Physical memory avail size
	INT64  i64MemoryCurUsedSize;		//Current used memory size
	INT64  i64MemoryCurSettingSize;		//Current setting memory size
	INT64  i64MemoryMinRequireSize;		//The minimal require memory size during modify hash database parameter
	
	INT64  i64DiskTotalSize;			//Physical disk total size
	INT64  i64DiskAvailSize;			//Physical disk avail size
	INT64  i64DiskCurUsedSize;			//Current used disk size
	INT64  i64SSDCurSettingSize;		//Current setting SSD size
	INT64  i64SSDMinRequireSize;		//The minimal require SSD size during modify hash database parameter
}HASH_STATUS, *PHASH_STATUS;

typedef struct _GET_HASH_STATUS_RESP
{	
	UINT32  dwResult;
	HASH_STATUS hashStatus;
}GET_HASH_STATUS_RESP, *PGET_HASH_STATUS_RESP;

typedef struct _MODIFY_HASH_PARA_REQ
{	
	UINT32  dwStoreMode;					//Memory or SSD mode	
	INT64 i64MemorySize;
	INT64 i64SSDSize;
}MODIFY_HASH_PARA_REQ, *PMODIFY_HASH_PARA_REQ;


enum _HASH_ROLE_INIT_STATUS_ENUM
{
	HASH_ROLE_INIT_STATUS_INITIALIZING	= 1,
	HASH_ROLE_INIT_STATUS_FAILED		= 2,
	HASH_ROLE_INIT_STATUS_RUNNING		= 3
};

typedef struct _HASH_INIT_PROC_INFO
{	
	UINT32     dwPhase;					//1: initializing; 2: failed; 3: succeeded and running; 
	INT64  llToLoad;
	INT64  llLoaded;
	int	  lElapsedTime;				
	int	  lEstimatedRemainTime;
}HASH_INIT_PROC_INFO, *PHASH_INIT_PROC_INFO;

typedef struct _GDD_ROLE_JOB_INFO_
{
	UINT32  jobNum;
}GDD_ROLE_JOB_INFO, *PGDD_ROLE_JOB_INFO;

typedef struct _SEEK_FILE_EX
{
	UINT64 nOffset;
	UINT32 nFileNo;
}SEEK_FILE_EX, *PSEEK_FILE_EX;

typedef struct _READ_FILE_EX
{
	int nLength;
	UINT32 nFileNo;
}READ_FILE_EX, *PREAD_FILE_EX;

typedef struct _READ_FILE_WITH_OFFSET_EX
{
	UINT64 nOffset;
	int nLength;
	UINT32 nFileNo;
}READ_FILE_WITH_OFFSET_EX, *PREAD_FILE_WITH_OFFSET_EX;

typedef struct _SERVER_STATUS
{
	UINT32 nStatus;
	UINT32 nErrorCode;
}SERVER_STATUS, *PSERVER_STATUS;

#ifdef GDD_REPLICATION_OPTIMIZATION
#define KEY_LEN 20
#define GDDBLOCKDESC_SZ 64
typedef union tagGDDBLOCKDESC
{
	struct
	{
		UINT8 HashKey[KEY_LEN];
		int lDataLen;    //The actual length of block. If compression enabled, it's the length after compression.
		UINT32 ulFileNo;    // The data file which holds the block.
		UINT32 uiSliceID;
		UINT32 uiSliceOffset;
		INT64 llFlag;
	};
	BYTE Data[GDDBLOCKDESC_SZ];
}GDDBLOCKDESC, *PGDDBLOCKDESC;
#endif //GDD_REPLICATION_OPTIMIZATION

#pragma pack()


#define  MAX_DEDUPE_RATIO  100000


#endif
