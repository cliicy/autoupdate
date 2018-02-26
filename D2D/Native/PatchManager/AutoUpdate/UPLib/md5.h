#ifndef _MD5_H
#define _MD5_H

//
// ---------------------------------------------------------------------
//               Proprietary and Confidential Information
//
//Copyright (c) 2016 Arcserve, including its affiliates and subsidiaries.
//All rights reserved.  Any third party trademarks or copyrights are the
//property of their respective owners.
// ----------------------------------------------------------------------
//

#ifndef IN
#define IN
#endif

#ifndef OUT
#define OUT
#endif

/* POINTER defines a generic pointer type */
typedef unsigned char *POINTER;

/* UINT2 defines a two byte word */
typedef unsigned short int UINT2;

/* UINT4 defines a four byte word */
typedef unsigned long int UINT4;

/* MD5 context. */
typedef struct {
  UINT4 state[4];                                   /* state (ABCD) */
  UINT4 count[2];        /* number of bits, modulo 2^64 (lsb first) */
  unsigned char buffer[64];                         /* input buffer */
  unsigned char digest[16];                         /* digest */
} MD5_CTX;

#ifdef __cplusplus
#define CTYPE extern "C"
#else
#define CTYPE
#endif

CTYPE void MD5Init (IN MD5_CTX *);
CTYPE void MD5Update (IN MD5_CTX *, IN unsigned char *, IN unsigned int);
CTYPE void MD5Final (OUT unsigned char *, IN MD5_CTX *);

#endif //_MD5_H
