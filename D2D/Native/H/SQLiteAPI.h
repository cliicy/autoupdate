
#pragma once

#include <stdarg.h>     /* Needed for the definition of va_list */
#include "sqlite3.h"

#ifdef  D2D_SQLLITE_API_EXPORTS
#define D2D_SQLLITE_API __declspec(dllexport)
#else
#define D2D_SQLLITE_API __declspec(dllimport)
#endif


#define SQLITEDLL _T("sqlite3.dll")
#ifdef SQLITE_DYNMIC_LOADING
typedef SQLITE_API int (*fn_sqlite3_bind_blob)(sqlite3_stmt*, int, const void*, int n, void(*)(void*));
typedef SQLITE_API int (*fn_sqlite3_bind_double)(sqlite3_stmt*, int, double);
typedef SQLITE_API int (*fn_sqlite3_bind_int)(sqlite3_stmt*, int, int);
typedef SQLITE_API int (*fn_sqlite3_bind_int64)(sqlite3_stmt*, int, sqlite3_int64);
typedef SQLITE_API int (*fn_sqlite3_bind_null)(sqlite3_stmt*, int);
typedef SQLITE_API int (*fn_sqlite3_bind_text)(sqlite3_stmt*, int, const char*, int n, void(*)(void*));
typedef SQLITE_API int (*fn_sqlite3_bind_text16)(sqlite3_stmt*, int, const void*, int, void(*)(void*));
typedef SQLITE_API int (*fn_sqlite3_bind_value)(sqlite3_stmt*, int, const sqlite3_value*);
typedef SQLITE_API int (*fn_sqlite3_bind_zeroblob)(sqlite3_stmt*, int, int n);

typedef SQLITE_API const void* (*fn_sqlite3_column_blob)(sqlite3_stmt*, int iCol);
typedef SQLITE_API int (*fn_sqlite3_column_bytes)(sqlite3_stmt*, int iCol);
typedef SQLITE_API int (*fn_sqlite3_column_bytes16)(sqlite3_stmt*, int iCol);
typedef SQLITE_API double (*fn_sqlite3_column_double)(sqlite3_stmt*, int iCol);
typedef SQLITE_API int (*fn_sqlite3_column_int)(sqlite3_stmt*, int iCol);
typedef SQLITE_API sqlite3_int64 (*fn_sqlite3_column_int64)(sqlite3_stmt*, int iCol);
typedef SQLITE_API const unsigned char* (*fn_sqlite3_column_text)(sqlite3_stmt*, int iCol);
typedef SQLITE_API const void* (*fn_sqlite3_column_text16)(sqlite3_stmt*, int iCol);
typedef SQLITE_API int (*fn_sqlite3_column_type)(sqlite3_stmt*, int iCol);
typedef SQLITE_API sqlite3_value* (*fn_sqlite3_column_value)(sqlite3_stmt*, int iCol);


typedef SQLITE_API int (*fn_sqlite3_step)(sqlite3_stmt*);
typedef SQLITE_API int (*fn_sqlite3_reset)(sqlite3_stmt *pStmt);
typedef SQLITE_API sqlite3_int64 (*fn_sqlite3_last_insert_rowid)(sqlite3*);
typedef SQLITE_API int (*fn_sqlite3_column_count)(sqlite3_stmt *pStmt);
typedef SQLITE_API int (*fn_sqlite3_initialize)(void);
typedef SQLITE_API int (*fn_sqlite3_shutdown)(void);
typedef SQLITE_API int (*fn_sqlite3_close)(sqlite3 *);
typedef SQLITE_API int (*fn_sqlite3_finalize)(sqlite3_stmt *pStmt);
typedef SQLITE_API void (*fn_sqlite3_interrupt)(sqlite3*);

typedef SQLITE_API int (*fn_sqlite3_data_count)(sqlite3_stmt *pStmt);

typedef SQLITE_API int (*fn_sqlite3_exec)(
  sqlite3*,                                  /* An open database */
  const char *sql,                           /* SQL to be evaluated */
  int (*callback)(void*,int,char**,char**),  /* Callback function */
  void *,                                    /* 1st argument to callback */
  char **errmsg                              /* Error msg written here */
);

typedef int (*fn_sqlite3_open16)(
  const void *filename,   /* Database filename (UTF-16) */
  sqlite3 **ppDb          /* OUT: SQLite db handle */
);

typedef SQLITE_API int (*fn_sqlite3_prepare_v2)(
  sqlite3 *db,            /* Database handle */
  const char *zSql,       /* SQL statement, UTF-8 encoded */
  int nByte,              /* Maximum length of zSql in bytes. */
  sqlite3_stmt **ppStmt,  /* OUT: Statement handle */
  const char **pzTail     /* OUT: Pointer to unused portion of zSql */
);

typedef SQLITE_API int (*fn_sqlite3_busy_timeout)(sqlite3*, int ms);

typedef SQLITE_API int (*fn_sqlite3_clear_bindings)(sqlite3_stmt*);

typedef SQLITE_API int (*fn_sqlite3_blob_open)(
  sqlite3*,
  const char *zDb,
  const char *zTable,
  const char *zColumn,
  sqlite3_int64 iRow,
  int flags,
  sqlite3_blob **ppBlob
);

typedef SQLITE_API int (*fn_sqlite3_blob_close)(sqlite3_blob *);


typedef SQLITE_API int (*fn_sqlite3_blob_read)(sqlite3_blob *, void *Z, int N, int iOffset);

typedef SQLITE_API int (*fn_sqlite3_blob_write)(sqlite3_blob *, const void *z, int n, int iOffset);

typedef SQLITE_API int (*fn_sqlite3_blob_bytes)(sqlite3_blob *);

typedef SQLITE_API void (*fn_sqlite3_update_hook)(sqlite3*,void(*)(void *,int ,char const *,char const *,sqlite3_int64),void*);


extern fn_sqlite3_update_hook p_sqlite3_update_hook;

extern fn_sqlite3_bind_blob p_sqlite3_bind_blob;
extern fn_sqlite3_bind_double p_sqlite3_bind_double;
extern fn_sqlite3_bind_int p_sqlite3_bind_int;
extern fn_sqlite3_bind_int64 p_sqlite3_bind_int64; 
extern fn_sqlite3_bind_null p_sqlite3_bind_null;
extern fn_sqlite3_bind_text p_sqlite3_bind_text;
extern fn_sqlite3_bind_text16 p_sqlite3_bind_text16;
extern fn_sqlite3_bind_value  p_sqlite3_bind_value;
extern fn_sqlite3_bind_zeroblob p_sqlite3_bind_zeroblob;
extern fn_sqlite3_step p_sqlite3_step;
extern fn_sqlite3_reset p_sqlite3_reset;
extern fn_sqlite3_last_insert_rowid p_sqlite3_last_insert_rowid;
extern fn_sqlite3_column_count p_sqlite3_column_count;
extern fn_sqlite3_initialize p_sqlite3_initialize;
extern fn_sqlite3_shutdown   p_sqlite3_shutdown;
extern fn_sqlite3_close		p_sqlite3_close;
extern fn_sqlite3_exec			p_sqlite3_exec;
extern fn_sqlite3_finalize p_sqlite3_finalize ;
extern fn_sqlite3_interrupt p_sqlite3_interrupt ;
extern fn_sqlite3_open16 p_sqlite3_open ;
extern fn_sqlite3_prepare_v2 p_sqlite3_prepare_v2 ;
extern fn_sqlite3_busy_timeout p_sqlite3_busy_timeout;
extern fn_sqlite3_clear_bindings p_sqlite3_clear_bindings ;
extern fn_sqlite3_column_blob p_sqlite3_column_blob;
extern fn_sqlite3_column_bytes p_sqlite3_column_bytes;
extern fn_sqlite3_column_bytes16 p_sqlite3_column_bytes16;
extern fn_sqlite3_column_double p_sqlite3_column_double ;
extern fn_sqlite3_column_int p_sqlite3_column_int;
extern fn_sqlite3_column_int64 p_sqlite3_column_int64;
extern fn_sqlite3_column_text p_sqlite3_column_text;
extern fn_sqlite3_column_text16 p_sqlite3_column_text16 ;
extern fn_sqlite3_column_type p_sqlite3_column_type;
extern fn_sqlite3_column_value p_sqlite3_column_value;
extern fn_sqlite3_data_count p_sqlite3_data_count ;


extern fn_sqlite3_blob_close p_sqlite3_blob_close;
extern fn_sqlite3_blob_open p_sqlite3_blob_open;
extern fn_sqlite3_blob_read p_sqlite3_blob_read;
extern fn_sqlite3_blob_write p_sqlite3_blob_write;
extern fn_sqlite3_blob_bytes p_sqlite3_blob_bytes;
#else
#define p_sqlite3_bind_blob  sqlite3_bind_blob
#define p_sqlite3_bind_double sqlite3_bind_double
#define p_sqlite3_bind_int sqlite3_bind_int
#define p_sqlite3_bind_int64 sqlite3_bind_int64
#define p_sqlite3_bind_null sqlite3_bind_null
#define p_sqlite3_bind_text sqlite3_bind_text
#define p_sqlite3_bind_text16 sqlite3_bind_text16
#define p_sqlite3_bind_value sqlite3_bind_value
#define p_sqlite3_bind_zeroblob sqlite3_bind_zeroblob
#define p_sqlite3_step sqlite3_step
#define p_sqlite3_reset sqlite3_reset
#define p_sqlite3_last_insert_rowid sqlite3_last_insert_rowid
#define p_sqlite3_column_count sqlite3_column_count
#define p_sqlite3_initialize sqlite3_initialize
#define p_sqlite3_shutdown sqlite3_shutdown
#define p_sqlite3_close sqlite3_close
#define p_sqlite3_exec sqlite3_exec
#define p_sqlite3_finalize sqlite3_finalize
#define p_sqlite3_interrupt sqlite3_interrupt
#define p_sqlite3_open sqlite3_open16
#define p_sqlite3_prepare_v2 sqlite3_prepare_v2
#define p_sqlite3_busy_timeout sqlite3_busy_timeout
#define p_sqlite3_clear_bindings sqlite3_clear_bindings
#define p_sqlite3_column_blob sqlite3_column_blob
#define p_sqlite3_column_bytes  sqlite3_column_bytes
#define p_sqlite3_column_bytes16  sqlite3_column_bytes16
#define p_sqlite3_column_double sqlite3_column_double
#define p_sqlite3_column_int sqlite3_column_int
#define p_sqlite3_column_int64 sqlite3_column_int64
#define p_sqlite3_column_text sqlite3_column_text
#define p_sqlite3_column_text16 sqlite3_column_text16
#define p_sqlite3_column_type sqlite3_column_type
#define p_sqlite3_column_value sqlite3_column_value
#define p_sqlite3_data_count sqlite3_data_count
#define p_sqlite3_blob_close sqlite3_blob_close
#define p_sqlite3_blob_open sqlite3_blob_open
#define p_sqlite3_blob_read sqlite3_blob_read
#define p_sqlite3_blob_write sqlite3_blob_write
#define p_sqlite3_blob_bytes sqlite3_blob_bytes
#define p_sqlite3_update_hook sqlite3_update_hook
#endif
D2D_SQLLITE_API void DeInitSQLiteDLL();
D2D_SQLLITE_API DWORD InitSQLiteDLL(PTCHAR szDllPath);