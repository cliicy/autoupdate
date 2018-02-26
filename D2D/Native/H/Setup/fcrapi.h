/********************************************************************************
*																				*
*	File			fcrapi.h 													*
*																				*
*   Purpose:		Defines data structures and function prototypes used by		*
*					the CA File Change Recorder (FCR) API to access the FCR		*
*					transaction database.										*
*																				*
*********************************************************************************
*								Revision History	                            *
*********************************************************************************
*    Date    |  Name | Version | Comments                                       *
*********************************************************************************
* 28-04-1999 |  MC   |  1      | File created									*
* 24-05-1999 |  MC   |  2      | Add event mask definitions previously held	in	*
*			 |		 |		   | dbase.h										*
* 03-08-1999 |  MC   |  4      | Set packing for history structure to 8 bytes	*
* 27-09-1999 |  MC   |  5      | Added EVENT_DELETE_PENDING						*
* 12-11-1999 |  MC   |  6      | Added EVENT_TRUNCATE							*
* 16/02/00   |  MC   |  7      | Add client IDs									*
* 16/02/00   |  MC   |  8      | Added type definition for FCR_CLIENTID_TYPE	*
********************************************************************************/

#ifndef __FCRAPI_H_
#define __FCRAPI_H_


/* All API functions return FCRSTATUS codes */
typedef long FCRSTATUS;

/* Define the type used as a client ID */
typedef UINT32 FCR_CLIENTID_TYPE;


/* The FCR_FILE_HANDLE is returned by a call to FCRGetFile and FCRGetFileByName and
   is used in subsequent calls to locate the file's record. As far as the FCR client
   user is concerned, the contents of this structure are unimportant as long as 
   they allocate sufficient memory for the structure and set the contents to zero 
   on the first call */
   
typedef struct 
{
	unsigned hashTableIndex,
			 listEntry;
} FCR_FILE_HANDLE, *PFCR_FILE_HANDLE;




/* The FCR_BLOCK_HISTORY record is used to maintain a list of blocks that were
   modified for a given file. This record is also common between NT and 95 implementations
   so should be protected from default compiler structure packing options in the same manner
   as the MCBCTM_DATABASE_RECORD (see comments in dbase.h).

   Note that the block history record is used for two purposes. Initially, it will be allocated
   and chained to the history field of the file information record and the next field
   contains a pointer to the next item (if any) in the list. The second role is used to read 
   and write records to the database file. When the record is used for this purpose, the 
   location field stores the physical location of the record in the database file.

   A list of these structures may also be returned to the FCR client following a call to FCRGetFile.
   Each of the entries in the lists corresponds to a segment of the file and multiple entries are
   chained together using the next member 
*/


#pragma pack (push, blockHistory, 8)

typedef struct blockHistory
{
	unsigned		next,				// pointer to next record
					location,			// physical location in database file
					bitmap;				// File modification bitmap. Don't change type without changing
										// BLOCK_BITMAP_SIZE
	LARGE_INTEGER	segment;			// Offset of file where modification occurred 
} FCR_MODIFIED_BLOCK_HISTORY, *PFCR_MODIFIED_BLOCK_HISTORY;


#pragma pack (pop, blockHistory)


typedef enum
{
	CONFIG_TYPE_NONE=0,
	CONFIG_BLOCK_SIZE,
	CONFIG_DATABASE_PATH,
	CONFIG_END_MARKER
} FCR_CONFIG_TYPE;



/* Bit values used for event flags. Each bit in the mask represents a file modification */

#define EVENT_NONE				0x00000000	// the file is currently unmodified
#define EVENT_CREATE			0x00000001
#define	EVENT_MODIFY			0x00000002	// the file's data was modified or a new file was created
#define EVENT_DELETE			0x00000004	// the file has been deleted
#define EVENT_RENAME			0x00000008	// the file was renamed
#define EVENT_ATTRIBUTES		0x00000010	// A change was made to the file's attributes
#define EVENT_EA				0x00000020	// A change was made to the file's extended attributes
#define EVENT_ACL				0x00000040	// A change was made to the file's security permissions
#define EVENT_DELETE_PENDING	0x00000080	// A file has been deleted but the database hasn't yet been updated.
#define EVENT_TRUNCATE			0x00000100  // The file has been truncated


/* Client ID values */

#define FCR_CLIENT_ID_MCB			0x00000001	// MCB
#define FCR_CLIENT_ID_NOTES_AGENT	0x00000002	// Lotus Notes database agent


#endif
