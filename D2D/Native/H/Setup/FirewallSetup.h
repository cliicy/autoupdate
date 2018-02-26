/////////////////////////////////////////////////////////////////////////////
// Firewall.h		CFirewall Interface
// Copyright (C) 2007 CA


#ifndef _Firewall
#define _Firewall

#include <Windows.h>

//Merge the code from rpcsal.h

typedef 
enum NET_FW_EDGE_TRAVERSAL_TYPE_
{	NET_FW_EDGE_TRAVERSAL_TYPE_DENY	= 0,
NET_FW_EDGE_TRAVERSAL_TYPE_ALLOW	= ( NET_FW_EDGE_TRAVERSAL_TYPE_DENY + 1 ) ,
NET_FW_EDGE_TRAVERSAL_TYPE_DEFER_TO_APP	= ( NET_FW_EDGE_TRAVERSAL_TYPE_ALLOW + 1 ) ,
NET_FW_EDGE_TRAVERSAL_TYPE_DEFER_TO_USER	= ( NET_FW_EDGE_TRAVERSAL_TYPE_DEFER_TO_APP + 1 ) 
} 	NET_FW_EDGE_TRAVERSAL_TYPE;


#pragma once

#include <specstrings.h>

#ifndef __RPCSAL_H_VERSION__
#define __RPCSAL_H_VERSION__        ( 100 )
#endif // __RPCSAL_H_VERSION__

#ifdef __REQUIRED_RPCSAL_H_VERSION__
#if ( __RPCSAL_H_VERSION__ < __REQUIRED_RPCSAL_H_VERSION__ )
#error incorrect <rpcsal.h> version. Use the header that matches with the MIDL compiler.
#endif
#endif


#ifdef  __cplusplus
extern "C" {
#endif  // #ifdef __cplusplus

#if (_MSC_VER >= 1000) && !defined(__midl) && defined(_PREFAST_)


	// [in]
#define __RPC__in                                   __pre __valid
#define __RPC__in_string                            __RPC__in   __pre __nullterminated
#define __RPC__in_ecount(size)                      __RPC__in __pre __elem_readableTo(size)
#define __RPC__in_ecount_full(size)                 __RPC__in_ecount(size)
#define __RPC__in_ecount_full_string(size)          __RPC__in_ecount_full(size) __pre __nullterminated
#define __RPC__in_ecount_part(size, length)         __RPC__in_ecount(length) __pre __elem_writableTo(size)
#define __RPC__in_ecount_full_opt(size)             __RPC__in_ecount_full(size) __pre __exceptthat  __maybenull
#define __RPC__in_ecount_full_opt_string(size)      __RPC__in_ecount_full_opt(size) __pre __nullterminated
#define __RPC__in_ecount_part_opt(size, length)     __RPC__in_ecount_part(size, length) __pre __exceptthat __maybenull
#define __RPC__in_xcount(size)                      __RPC__in __pre __elem_readableTo(size)
#define __RPC__in_xcount_full(size)                 __RPC__in_ecount(size)
#define __RPC__in_xcount_full_string(size)          __RPC__in_ecount_full(size) __pre __nullterminated
#define __RPC__in_xcount_part(size, length)         __RPC__in_ecount(length) __pre __elem_writableTo(size)
#define __RPC__in_xcount_full_opt(size)             __RPC__in_ecount_full(size) __pre __exceptthat  __maybenull
#define __RPC__in_xcount_full_opt_string(size)      __RPC__in_ecount_full_opt(size) __pre __nullterminated
#define __RPC__in_xcount_part_opt(size, length)     __RPC__in_ecount_part(size, length) __pre __exceptthat __maybenull


#define __RPC__deref_in                             __RPC__in __deref __notnull 
#define __RPC__deref_in_string                      __RPC__in   __pre __deref __nullterminated
#define __RPC__deref_in_opt                         __RPC__deref_in __deref __exceptthat __maybenull
#define __RPC__deref_in_opt_string                  __RPC__deref_in_opt __pre __deref __nullterminated
#define __RPC__deref_opt_in                         __RPC__in __exceptthat __maybenull 
#define __RPC__deref_opt_in_string                  __RPC__deref_opt_in __pre __deref __nullterminated 
#define __RPC__deref_opt_in_opt                     __RPC__deref_opt_in  __pre __deref __exceptthat __maybenull
#define __RPC__deref_opt_in_opt_string              __RPC__deref_opt_in_opt  __pre __deref __nullterminated
#define __RPC__deref_in_ecount(size)                __RPC__in __pre __deref __elem_readableTo(size)
#define __RPC__deref_in_ecount_part(size, length)   __RPC__deref_in_ecount(size)  __pre __deref __elem_readableTo(length)
#define __RPC__deref_in_ecount_full(size)           __RPC__deref_in_ecount_part(size, size)
#define __RPC__deref_in_ecount_full_opt(size)       __RPC__deref_in_ecount_full(size) __pre __deref __exceptthat __maybenull
#define __RPC__deref_in_ecount_full_opt_string(size) __RPC__deref_in_ecount_full_opt(size) __pre __deref __nullterminated
#define __RPC__deref_in_ecount_full_string(size)    __RPC__deref_in_ecount_full(size) __pre __deref __nullterminated
#define __RPC__deref_in_ecount_opt(size)            __RPC__deref_in_ecount(size) __pre __deref __exceptthat __maybenull
#define __RPC__deref_in_ecount_opt_string(size)     __RPC__deref_in_ecount_opt(size) __pre __deref __nullterminated
#define __RPC__deref_in_ecount_part_opt(size, length) __RPC__deref_in_ecount_opt(size) __pre __deref __elem_readableTo(length)
#define __RPC__deref_in_xcount(size)                __RPC__in __pre __deref __elem_readableTo(size)
#define __RPC__deref_in_xcount_part(size, length)   __RPC__deref_in_ecount(size)  __pre __deref __elem_readableTo(length)
#define __RPC__deref_in_xcount_full(size)           __RPC__deref_in_ecount_part(size, size)
#define __RPC__deref_in_xcount_full_opt(size)       __RPC__deref_in_ecount_full(size) __pre __deref __exceptthat __maybenull
#define __RPC__deref_in_xcount_full_opt_string(size) __RPC__deref_in_ecount_full_opt(size) __pre __deref __nullterminated
#define __RPC__deref_in_xcount_full_string(size)    __RPC__deref_in_ecount_full(size) __pre __deref __nullterminated
#define __RPC__deref_in_xcount_opt(size)            __RPC__deref_in_ecount(size) __pre __deref __exceptthat __maybenull
#define __RPC__deref_in_xcount_opt_string(size)     __RPC__deref_in_ecount_opt(size) __pre __deref __nullterminated
#define __RPC__deref_in_xcount_part_opt(size, length) __RPC__deref_in_ecount_opt(size) __pre __deref __elem_readableTo(length)

	// [out]
#define __RPC__out                                  __out
#define __RPC__out_ecount(size)                     __out_ecount(size)  __post  __elem_writableTo(size)
#define __RPC__out_ecount_string(size)              __RPC__out_ecount(size) __post __nullterminated
#define __RPC__out_ecount_part(size, length)        __RPC__out_ecount(size)  __post  __elem_readableTo(length)
#define __RPC__out_ecount_full(size)                __RPC__out_ecount_part(size, size)
#define __RPC__out_ecount_full_string(size)         __RPC__out_ecount_full(size) __post  __nullterminated
#define __RPC__out_xcount(size)                     __out
#define __RPC__out_xcount_string(size)              __RPC__out __post __nullterminated
#define __RPC__out_xcount_part(size, length)        __RPC__out
#define __RPC__out_xcount_full(size)                __RPC__out
#define __RPC__out_xcount_full_string(size)         __RPC__out __post __nullterminated

	// [in,out] 
#define __RPC__inout                                __inout
#define __RPC__inout_string                         __RPC__inout  __pre __nullterminated __post __nullterminated
#define __RPC__inout_ecount(size)                   __inout_ecount(size)
#define __RPC__inout_ecount_part(size, length)      __inout_ecount_part(size, length)
#define __RPC__inout_ecount_full(size)              __RPC__inout_ecount_part(size, size)
#define __RPC__inout_ecount_full_string(size)       __RPC__inout_ecount_full(size) __pre __nullterminated __post __nullterminated
#define __RPC__inout_xcount(size)                   __inout
#define __RPC__inout_xcount_part(size, length)      __inout
#define __RPC__inout_xcount_full(size)              __RPC__inout
#define __RPC__inout_xcount_full_string(size)       __RPC__inout __pre __nullterminated __post __nullterminated

	// [in,unique] 
#define __RPC__in_opt                               __RPC__in __pre __exceptthat __maybenull
#define __RPC__in_opt_string                        __RPC__in_opt   __pre __nullterminated
#define __RPC__in_ecount_opt(size)                  __RPC__in_ecount(size) __pre __exceptthat __maybenull
#define __RPC__in_ecount_opt_string(size)           __RPC__in_ecount_opt(size) __pre __nullterminated
#define __RPC__in_xcount_opt(size)                  __RPC__in_ecount(size) __pre __exceptthat __maybenull
#define __RPC__in_xcount_opt_string(size)           __RPC__in_ecount_opt(size) __pre __nullterminated

	// [in,out,unique] 
#define __RPC__inout_opt                            __inout_opt
#define __RPC__inout_opt_string                     __RPC__inout_opt  __pre __nullterminated
#define __RPC__inout_ecount_opt(size)               __inout_ecount_opt(size)
#define __RPC__inout_ecount_part_opt(size, length)  __inout_ecount_part_opt(size, length)
#define __RPC__inout_ecount_full_opt(size)          __RPC__inout_ecount_part_opt(size, size)
#define __RPC__inout_ecount_full_opt_string(size)   __RPC__inout_ecount_full_opt(size)  __pre __nullterminated __post __nullterminated
#define __RPC__inout_xcount_opt(size)               __inout_opt
#define __RPC__inout_xcount_part_opt(size, length)  __inout_opt
#define __RPC__inout_xcount_full_opt(size)          __RPC__inout_opt
#define __RPC__inout_xcount_full_opt_string(size)   __RPC__inout_opt __pre __nullterminated __post __nullterminated

	// [out] **
#define __RPC__deref_out                            __deref_out
#define __RPC__deref_out_string                     __RPC__deref_out    __post __deref __nullterminated
	// Removed "__post __deref __exceptthat __maybenull" so return values from QueryInterface and the like can be trusted without an explicit NULL check.
	// This is a temporary fix until midl.exe can be rev'd to produce more accurate annotations.
#define __RPC__deref_out_opt                        __RPC__deref_out
#define __RPC__deref_out_opt_string                 __RPC__deref_out_opt  __post __deref __nullterminated __pre __deref __null
#define __RPC__deref_out_ecount(size)               __deref_out_ecount(size) __post __deref __elem_writableTo(size)
#define __RPC__deref_out_ecount_part(size, length)  __RPC__deref_out_ecount(size) __post __deref __elem_readableTo(length)
#define __RPC__deref_out_ecount_full(size)          __RPC__deref_out_ecount_part(size,size)
#define __RPC__deref_out_ecount_full_string(size)   __RPC__deref_out_ecount_full(size) __post __deref __nullterminated
#define __RPC__deref_out_xcount(size)               __deref_out __post __deref
#define __RPC__deref_out_xcount_part(size, length)  __RPC__deref_out __post __deref
#define __RPC__deref_out_xcount_full(size)          __RPC__deref_out
#define __RPC__deref_out_xcount_full_string(size)   __RPC__deref_out __post __deref __nullterminated

	// [in,out] **, second pointer decoration. 
#define __RPC__deref_inout                          __deref_inout
#define __RPC__deref_inout_string                   __RPC__deref_inout __pre __deref __nullterminated __post __deref __nullterminated
#define __RPC__deref_inout_opt                      __deref_inout_opt
#define __RPC__deref_inout_opt_string               __RPC__deref_inout_opt __deref __nullterminated 
#define __RPC__deref_inout_ecount_opt(size)         __deref_inout_ecount_opt(size)
#define __RPC__deref_inout_ecount_part_opt(size, length) __deref_inout_ecount_part_opt(size , length)
#define __RPC__deref_inout_ecount_full_opt(size)    __RPC__deref_inout_ecount_part_opt(size, size)
#define __RPC__deref_inout_ecount_full(size)        __deref_inout_ecount_full(size)
#define __RPC__deref_inout_ecount_full_string(size) __RPC__deref_inout_ecount_full(size) __post __deref __nullterminated
#define __RPC__deref_inout_ecount_full_opt_string(size) __RPC__deref_inout_ecount_full_opt(size) __pre __deref __nullterminated __post __deref __nullterminated
#define __RPC__deref_inout_xcount_opt(size)         __deref_inout_opt
#define __RPC__deref_inout_xcount_part_opt(size, length) __deref_inout_opt
#define __RPC__deref_inout_xcount_full_opt(size)    __RPC__deref_inout_opt
#define __RPC__deref_inout_xcount_full(size)        __deref_inout
#define __RPC__deref_inout_xcount_full_string(size) __RPC__deref_inout __post __deref __nullterminated
#define __RPC__deref_inout_xcount_full_opt_string(size) __RPC__deref_inout_opt __pre __deref __nullterminated __post __deref __nullterminated


	// #define __RPC_out_opt    out_opt is not allowed in rpc

	// [in,out,unique] 
#define __RPC__deref_opt_inout                          __deref_opt_inout
#define __RPC__deref_opt_inout_ecount(size)             __deref_opt_inout_ecount(size)
#define __RPC__deref_opt_inout_string                   __RPC__deref_opt_inout __pre __deref __nullterminated __post __deref __nullterminated
#define __RPC__deref_opt_inout_ecount_part(size, length) __deref_opt_inout_ecount_part(size, length)
#define __RPC__deref_opt_inout_ecount_full(size)        __deref_opt_inout_ecount_full(size)
#define __RPC__deref_opt_inout_ecount_full_string(size)  __RPC__deref_opt_inout_ecount_full(size) __pre __deref __nullterminated __post __deref __nullterminated
#define __RPC__deref_opt_inout_xcount_part(size, length) __deref_opt_inout
#define __RPC__deref_opt_inout_xcount_full(size)        __deref_opt_inout
#define __RPC__deref_opt_inout_xcount_full_string(size)  __RPC__deref_opt_inout __pre __deref __nullterminated __post __deref __nullterminated


	// We don't need to specify __pre __deref __exceptthat __maybenull : this is default behavior. While this might not hold in SAL 1.1 syntax, SAL team 
	// believes it's OK. We can revisit if SAL 1.1 can survive. 
#define __RPC__deref_out_ecount_opt(size)               __RPC__out_ecount(size) __post __deref __exceptthat __maybenull __pre __deref __null 
#define __RPC__deref_out_ecount_part_opt(size, length)  __RPC__deref_out_ecount_part(size, length) __post __deref __exceptthat __maybenull __pre __deref __null
#define __RPC__deref_out_ecount_full_opt(size)          __RPC__deref_out_ecount_part_opt(size, size) __pre __deref __null
#define __RPC__deref_out_ecount_full_opt_string(size)   __RPC__deref_out_ecount_part_opt(size, size) __post __deref __nullterminated __pre __deref __null
#define __RPC__deref_out_xcount_opt(size)               __RPC__out __post __deref __exceptthat __maybenull __pre __deref __null 
#define __RPC__deref_out_xcount_part_opt(size, length)  __RPC__deref_out __post __deref __exceptthat __maybenull __pre __deref __null
#define __RPC__deref_out_xcount_full_opt(size)          __RPC__deref_out_opt __pre __deref __null
#define __RPC__deref_out_xcount_full_opt_string(size)   __RPC__deref_out_opt __post __deref __nullterminated __pre __deref __null

#define __RPC__deref_opt_inout_opt                      __deref_opt_inout_opt
#define __RPC__deref_opt_inout_opt_string               __RPC__deref_opt_inout_opt __pre __deref __nullterminated  __post __deref __nullterminated
#define __RPC__deref_opt_inout_ecount_opt(size)         __deref_opt_inout_ecount_opt(size)  
#define __RPC__deref_opt_inout_ecount_part_opt(size, length) __deref_opt_inout_ecount_part_opt(size, length)
#define __RPC__deref_opt_inout_ecount_full_opt(size)    __RPC__deref_opt_inout_ecount_part_opt(size, size)
#define __RPC__deref_opt_inout_ecount_full_opt_string(size)  __RPC__deref_opt_inout_ecount_full_opt(size) __pre __deref __nullterminated __post __deref __nullterminated
#define __RPC__deref_opt_inout_xcount_opt(size)         __deref_opt_inout_opt  
#define __RPC__deref_opt_inout_xcount_part_opt(size, length) __deref_opt_inout_opt
#define __RPC__deref_opt_inout_xcount_full_opt(size)    __RPC__deref_opt_inout_opt
#define __RPC__deref_opt_inout_xcount_full_opt_string(size)  __RPC__deref_opt_inout_opt __pre __deref __nullterminated __post __deref __nullterminated

#define __RPC_full_pointer                              __maybenull 
#define __RPC_unique_pointer                            __maybenull
#define __RPC_ref_pointer                               __notnull
#define __RPC_string                                    __nullterminated

#define __RPC__range(min,max)                           __range(min,max)
#define __RPC__in_range(min,max)                        __in_range(min,max)

#else   // not prefast

#define __RPC__range(min,max)
#define __RPC__in_range(min,max)

#define __RPC__in           
#define __RPC__in_string
#define __RPC__in_opt_string
#define __RPC__in_ecount(size) 
#define __RPC__in_ecount_full(size)
#define __RPC__in_ecount_full_string(size)
#define __RPC__in_ecount_part(size, length)
#define __RPC__in_ecount_full_opt(size)
#define __RPC__in_ecount_full_opt_string(size)
#define __RPC__inout_ecount_full_opt_string(size)
#define __RPC__in_ecount_part_opt(size, length)
#define __RPC__in_xcount(size) 
#define __RPC__in_xcount_full(size)
#define __RPC__in_xcount_full_string(size)
#define __RPC__in_xcount_part(size, length)
#define __RPC__in_xcount_full_opt(size)
#define __RPC__in_xcount_full_opt_string(size)
#define __RPC__inout_xcount_full_opt_string(size)
#define __RPC__in_xcount_part_opt(size, length)

#define __RPC__deref_in 
#define __RPC__deref_in_string
#define __RPC__deref_in_opt
#define __RPC__deref_in_opt_string
#define __RPC__deref_opt_in
#define __RPC__deref_opt_in_string
#define __RPC__deref_opt_in_opt
#define __RPC__deref_opt_in_opt_string
#define __RPC__deref_in_ecount(size) 
#define __RPC__deref_in_ecount_part(size, length) 
#define __RPC__deref_in_ecount_full(size) 
#define __RPC__deref_in_ecount_full_opt(size)
#define __RPC__deref_in_ecount_full_string(size)
#define __RPC__deref_in_ecount_full_opt_string(size)
#define __RPC__deref_in_ecount_opt(size) 
#define __RPC__deref_in_ecount_opt_string(size)
#define __RPC__deref_in_ecount_part_opt(size, length) 
#define __RPC__deref_in_xcount(size) 
#define __RPC__deref_in_xcount_part(size, length) 
#define __RPC__deref_in_xcount_full(size) 
#define __RPC__deref_in_xcount_full_opt(size)
#define __RPC__deref_in_xcount_full_string(size)
#define __RPC__deref_in_xcount_full_opt_string(size)
#define __RPC__deref_in_xcount_opt(size) 
#define __RPC__deref_in_xcount_opt_string(size)
#define __RPC__deref_in_xcount_part_opt(size, length) 

	// [out]
#define __RPC__out     
#define __RPC__out_ecount(size) 
#define __RPC__out_ecount_part(size, length) 
#define __RPC__out_ecount_full(size)
#define __RPC__out_ecount_full_string(size)
#define __RPC__out_xcount(size) 
#define __RPC__out_xcount_part(size, length) 
#define __RPC__out_xcount_full(size)
#define __RPC__out_xcount_full_string(size)

	// [in,out] 
#define __RPC__inout                                   
#define __RPC__inout_string
#define __RPC__opt_inout
#define __RPC__inout_ecount(size)                     
#define __RPC__inout_ecount_part(size, length)    
#define __RPC__inout_ecount_full(size)          
#define __RPC__inout_ecount_full_string(size)          
#define __RPC__inout_xcount(size)                     
#define __RPC__inout_xcount_part(size, length)    
#define __RPC__inout_xcount_full(size)          
#define __RPC__inout_xcount_full_string(size)          

	// [in,unique] 
#define __RPC__in_opt       
#define __RPC__in_ecount_opt(size)   
#define __RPC__in_xcount_opt(size)   


	// [in,out,unique] 
#define __RPC__inout_opt    
#define __RPC__inout_opt_string    
#define __RPC__inout_ecount_opt(size)  
#define __RPC__inout_ecount_part_opt(size, length) 
#define __RPC__inout_ecount_full_opt(size)     
#define __RPC__inout_ecount_full_string(size)
#define __RPC__inout_xcount_opt(size)  
#define __RPC__inout_xcount_part_opt(size, length) 
#define __RPC__inout_xcount_full_opt(size)     
#define __RPC__inout_xcount_full_string(size)

	// [out] **
#define __RPC__deref_out   
#define __RPC__deref_out_string
#define __RPC__deref_out_opt 
#define __RPC__deref_out_opt_string
#define __RPC__deref_out_ecount(size) 
#define __RPC__deref_out_ecount_part(size, length) 
#define __RPC__deref_out_ecount_full(size)  
#define __RPC__deref_out_ecount_full_string(size)
#define __RPC__deref_out_xcount(size) 
#define __RPC__deref_out_xcount_part(size, length) 
#define __RPC__deref_out_xcount_full(size)  
#define __RPC__deref_out_xcount_full_string(size)


	// [in,out] **, second pointer decoration. 
#define __RPC__deref_inout    
#define __RPC__deref_inout_string
#define __RPC__deref_inout_opt 
#define __RPC__deref_inout_opt_string
#define __RPC__deref_inout_ecount_full(size)
#define __RPC__deref_inout_ecount_full_string(size)
#define __RPC__deref_inout_ecount_opt(size) 
#define __RPC__deref_inout_ecount_part_opt(size, length) 
#define __RPC__deref_inout_ecount_full_opt(size) 
#define __RPC__deref_inout_ecount_full_opt_string(size) 
#define __RPC__deref_inout_xcount_full(size)
#define __RPC__deref_inout_xcount_full_string(size)
#define __RPC__deref_inout_xcount_opt(size) 
#define __RPC__deref_inout_xcount_part_opt(size, length) 
#define __RPC__deref_inout_xcount_full_opt(size) 
#define __RPC__deref_inout_xcount_full_opt_string(size) 

	// #define __RPC_out_opt    out_opt is not allowed in rpc

	// [in,out,unique] 
#define __RPC__deref_opt_inout  
#define __RPC__deref_opt_inout_string
#define __RPC__deref_opt_inout_ecount(size)     
#define __RPC__deref_opt_inout_ecount_part(size, length) 
#define __RPC__deref_opt_inout_ecount_full(size) 
#define __RPC__deref_opt_inout_ecount_full_string(size)
#define __RPC__deref_opt_inout_xcount(size)     
#define __RPC__deref_opt_inout_xcount_part(size, length) 
#define __RPC__deref_opt_inout_xcount_full(size) 
#define __RPC__deref_opt_inout_xcount_full_string(size)

#define __RPC__deref_out_ecount_opt(size) 
#define __RPC__deref_out_ecount_part_opt(size, length) 
#define __RPC__deref_out_ecount_full_opt(size) 
#define __RPC__deref_out_ecount_full_opt_string(size)
#define __RPC__deref_out_xcount_opt(size) 
#define __RPC__deref_out_xcount_part_opt(size, length) 
#define __RPC__deref_out_xcount_full_opt(size) 
#define __RPC__deref_out_xcount_full_opt_string(size)

#define __RPC__deref_opt_inout_opt      
#define __RPC__deref_opt_inout_opt_string
#define __RPC__deref_opt_inout_ecount_opt(size)   
#define __RPC__deref_opt_inout_ecount_part_opt(size, length) 
#define __RPC__deref_opt_inout_ecount_full_opt(size) 
#define __RPC__deref_opt_inout_ecount_full_opt_string(size) 
#define __RPC__deref_opt_inout_xcount_opt(size)   
#define __RPC__deref_opt_inout_xcount_part_opt(size, length) 
#define __RPC__deref_opt_inout_xcount_full_opt(size) 
#define __RPC__deref_opt_inout_xcount_full_opt_string(size) 

#define __RPC_full_pointer  
#define __RPC_unique_pointer
#define __RPC_ref_pointer
#define __RPC_string                               


#endif

#ifdef  __cplusplus
}
#endif



//end merge

//merge the Firewall code from MS NetFW.h

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 500
#endif

/* verify that the <rpcsal.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCSAL_H_VERSION__
#define __REQUIRED_RPCSAL_H_VERSION__ 100
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __netfw_h__
#define __netfw_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __INetFwRemoteAdminSettings_FWD_DEFINED__
#define __INetFwRemoteAdminSettings_FWD_DEFINED__
typedef interface INetFwRemoteAdminSettings INetFwRemoteAdminSettings;
#endif 	/* __INetFwRemoteAdminSettings_FWD_DEFINED__ */


#ifndef __INetFwIcmpSettings_FWD_DEFINED__
#define __INetFwIcmpSettings_FWD_DEFINED__
typedef interface INetFwIcmpSettings INetFwIcmpSettings;
#endif 	/* __INetFwIcmpSettings_FWD_DEFINED__ */


#ifndef __INetFwOpenPort_FWD_DEFINED__
#define __INetFwOpenPort_FWD_DEFINED__
typedef interface INetFwOpenPort INetFwOpenPort;
#endif 	/* __INetFwOpenPort_FWD_DEFINED__ */


#ifndef __INetFwOpenPorts_FWD_DEFINED__
#define __INetFwOpenPorts_FWD_DEFINED__
typedef interface INetFwOpenPorts INetFwOpenPorts;
#endif 	/* __INetFwOpenPorts_FWD_DEFINED__ */


#ifndef __INetFwService_FWD_DEFINED__
#define __INetFwService_FWD_DEFINED__
typedef interface INetFwService INetFwService;
#endif 	/* __INetFwService_FWD_DEFINED__ */


#ifndef __INetFwServices_FWD_DEFINED__
#define __INetFwServices_FWD_DEFINED__
typedef interface INetFwServices INetFwServices;
#endif 	/* __INetFwServices_FWD_DEFINED__ */


#ifndef __INetFwAuthorizedApplication_FWD_DEFINED__
#define __INetFwAuthorizedApplication_FWD_DEFINED__
typedef interface INetFwAuthorizedApplication INetFwAuthorizedApplication;
#endif 	/* __INetFwAuthorizedApplication_FWD_DEFINED__ */


#ifndef __INetFwAuthorizedApplications_FWD_DEFINED__
#define __INetFwAuthorizedApplications_FWD_DEFINED__
typedef interface INetFwAuthorizedApplications INetFwAuthorizedApplications;
#endif 	/* __INetFwAuthorizedApplications_FWD_DEFINED__ */


#ifndef __INetFwRule_FWD_DEFINED__
#define __INetFwRule_FWD_DEFINED__
typedef interface INetFwRule INetFwRule;
#endif 	/* __INetFwRule_FWD_DEFINED__ */


#ifndef __INetFwRule2_FWD_DEFINED__
#define __INetFwRule2_FWD_DEFINED__
typedef interface INetFwRule2 INetFwRule2;
#endif 	/* __INetFwRule2_FWD_DEFINED__ */


#ifndef __INetFwRules_FWD_DEFINED__
#define __INetFwRules_FWD_DEFINED__
typedef interface INetFwRules INetFwRules;
#endif 	/* __INetFwRules_FWD_DEFINED__ */


#ifndef __INetFwServiceRestriction_FWD_DEFINED__
#define __INetFwServiceRestriction_FWD_DEFINED__
typedef interface INetFwServiceRestriction INetFwServiceRestriction;
#endif 	/* __INetFwServiceRestriction_FWD_DEFINED__ */


#ifndef __INetFwProfile_FWD_DEFINED__
#define __INetFwProfile_FWD_DEFINED__
typedef interface INetFwProfile INetFwProfile;
#endif 	/* __INetFwProfile_FWD_DEFINED__ */


#ifndef __INetFwPolicy_FWD_DEFINED__
#define __INetFwPolicy_FWD_DEFINED__
typedef interface INetFwPolicy INetFwPolicy;
#endif 	/* __INetFwPolicy_FWD_DEFINED__ */


#ifndef __INetFwPolicy2_FWD_DEFINED__
#define __INetFwPolicy2_FWD_DEFINED__
typedef interface INetFwPolicy2 INetFwPolicy2;
#endif 	/* __INetFwPolicy2_FWD_DEFINED__ */


#ifndef __INetFwMgr_FWD_DEFINED__
#define __INetFwMgr_FWD_DEFINED__
typedef interface INetFwMgr INetFwMgr;
#endif 	/* __INetFwMgr_FWD_DEFINED__ */


#ifndef __INetFwProduct_FWD_DEFINED__
#define __INetFwProduct_FWD_DEFINED__
typedef interface INetFwProduct INetFwProduct;
#endif 	/* __INetFwProduct_FWD_DEFINED__ */


#ifndef __INetFwProducts_FWD_DEFINED__
#define __INetFwProducts_FWD_DEFINED__
typedef interface INetFwProducts INetFwProducts;
#endif 	/* __INetFwProducts_FWD_DEFINED__ */


#ifndef __INetFwRemoteAdminSettings_FWD_DEFINED__
#define __INetFwRemoteAdminSettings_FWD_DEFINED__
typedef interface INetFwRemoteAdminSettings INetFwRemoteAdminSettings;
#endif 	/* __INetFwRemoteAdminSettings_FWD_DEFINED__ */


#ifndef __INetFwIcmpSettings_FWD_DEFINED__
#define __INetFwIcmpSettings_FWD_DEFINED__
typedef interface INetFwIcmpSettings INetFwIcmpSettings;
#endif 	/* __INetFwIcmpSettings_FWD_DEFINED__ */


#ifndef __INetFwOpenPort_FWD_DEFINED__
#define __INetFwOpenPort_FWD_DEFINED__
typedef interface INetFwOpenPort INetFwOpenPort;
#endif 	/* __INetFwOpenPort_FWD_DEFINED__ */


#ifndef __INetFwOpenPorts_FWD_DEFINED__
#define __INetFwOpenPorts_FWD_DEFINED__
typedef interface INetFwOpenPorts INetFwOpenPorts;
#endif 	/* __INetFwOpenPorts_FWD_DEFINED__ */


#ifndef __INetFwService_FWD_DEFINED__
#define __INetFwService_FWD_DEFINED__
typedef interface INetFwService INetFwService;
#endif 	/* __INetFwService_FWD_DEFINED__ */


#ifndef __INetFwServices_FWD_DEFINED__
#define __INetFwServices_FWD_DEFINED__
typedef interface INetFwServices INetFwServices;
#endif 	/* __INetFwServices_FWD_DEFINED__ */


#ifndef __INetFwAuthorizedApplication_FWD_DEFINED__
#define __INetFwAuthorizedApplication_FWD_DEFINED__
typedef interface INetFwAuthorizedApplication INetFwAuthorizedApplication;
#endif 	/* __INetFwAuthorizedApplication_FWD_DEFINED__ */


#ifndef __INetFwAuthorizedApplications_FWD_DEFINED__
#define __INetFwAuthorizedApplications_FWD_DEFINED__
typedef interface INetFwAuthorizedApplications INetFwAuthorizedApplications;
#endif 	/* __INetFwAuthorizedApplications_FWD_DEFINED__ */


#ifndef __INetFwServiceRestriction_FWD_DEFINED__
#define __INetFwServiceRestriction_FWD_DEFINED__
typedef interface INetFwServiceRestriction INetFwServiceRestriction;
#endif 	/* __INetFwServiceRestriction_FWD_DEFINED__ */


#ifndef __INetFwRule_FWD_DEFINED__
#define __INetFwRule_FWD_DEFINED__
typedef interface INetFwRule INetFwRule;
#endif 	/* __INetFwRule_FWD_DEFINED__ */


#ifndef __INetFwRules_FWD_DEFINED__
#define __INetFwRules_FWD_DEFINED__
typedef interface INetFwRules INetFwRules;
#endif 	/* __INetFwRules_FWD_DEFINED__ */


#ifndef __INetFwProfile_FWD_DEFINED__
#define __INetFwProfile_FWD_DEFINED__
typedef interface INetFwProfile INetFwProfile;
#endif 	/* __INetFwProfile_FWD_DEFINED__ */


#ifndef __INetFwPolicy_FWD_DEFINED__
#define __INetFwPolicy_FWD_DEFINED__
typedef interface INetFwPolicy INetFwPolicy;
#endif 	/* __INetFwPolicy_FWD_DEFINED__ */


#ifndef __INetFwPolicy2_FWD_DEFINED__
#define __INetFwPolicy2_FWD_DEFINED__
typedef interface INetFwPolicy2 INetFwPolicy2;
#endif 	/* __INetFwPolicy2_FWD_DEFINED__ */


#ifndef __INetFwMgr_FWD_DEFINED__
#define __INetFwMgr_FWD_DEFINED__
typedef interface INetFwMgr INetFwMgr;
#endif 	/* __INetFwMgr_FWD_DEFINED__ */


#ifndef __INetFwProduct_FWD_DEFINED__
#define __INetFwProduct_FWD_DEFINED__
typedef interface INetFwProduct INetFwProduct;
#endif 	/* __INetFwProduct_FWD_DEFINED__ */


#ifndef __INetFwProducts_FWD_DEFINED__
#define __INetFwProducts_FWD_DEFINED__
typedef interface INetFwProducts INetFwProducts;
#endif 	/* __INetFwProducts_FWD_DEFINED__ */


#ifndef __NetFwRule_FWD_DEFINED__
#define __NetFwRule_FWD_DEFINED__

#ifdef __cplusplus
typedef class NetFwRule NetFwRule;
#else
typedef struct NetFwRule NetFwRule;
#endif /* __cplusplus */

#endif 	/* __NetFwRule_FWD_DEFINED__ */


#ifndef __NetFwOpenPort_FWD_DEFINED__
#define __NetFwOpenPort_FWD_DEFINED__

#ifdef __cplusplus
typedef class NetFwOpenPort NetFwOpenPort;
#else
typedef struct NetFwOpenPort NetFwOpenPort;
#endif /* __cplusplus */

#endif 	/* __NetFwOpenPort_FWD_DEFINED__ */


#ifndef __NetFwAuthorizedApplication_FWD_DEFINED__
#define __NetFwAuthorizedApplication_FWD_DEFINED__

#ifdef __cplusplus
typedef class NetFwAuthorizedApplication NetFwAuthorizedApplication;
#else
typedef struct NetFwAuthorizedApplication NetFwAuthorizedApplication;
#endif /* __cplusplus */

#endif 	/* __NetFwAuthorizedApplication_FWD_DEFINED__ */


#ifndef __NetFwPolicy2_FWD_DEFINED__
#define __NetFwPolicy2_FWD_DEFINED__

#ifdef __cplusplus
typedef class NetFwPolicy2 NetFwPolicy2;
#else
typedef struct NetFwPolicy2 NetFwPolicy2;
#endif /* __cplusplus */

#endif 	/* __NetFwPolicy2_FWD_DEFINED__ */


#ifndef __NetFwProduct_FWD_DEFINED__
#define __NetFwProduct_FWD_DEFINED__

#ifdef __cplusplus
typedef class NetFwProduct NetFwProduct;
#else
typedef struct NetFwProduct NetFwProduct;
#endif /* __cplusplus */

#endif 	/* __NetFwProduct_FWD_DEFINED__ */


#ifndef __NetFwProducts_FWD_DEFINED__
#define __NetFwProducts_FWD_DEFINED__

#ifdef __cplusplus
typedef class NetFwProducts NetFwProducts;
#else
typedef struct NetFwProducts NetFwProducts;
#endif /* __cplusplus */

#endif 	/* __NetFwProducts_FWD_DEFINED__ */


#ifndef __NetFwMgr_FWD_DEFINED__
#define __NetFwMgr_FWD_DEFINED__

#ifdef __cplusplus
typedef class NetFwMgr NetFwMgr;
#else
typedef struct NetFwMgr NetFwMgr;
#endif /* __cplusplus */

#endif 	/* __NetFwMgr_FWD_DEFINED__ */


/* header files for imported files */
//#include "icftypes.h"
//merge code from MS "icftypes.h"


/* this ALWAYS GENERATED file contains the definitions for the interfaces */


/* File created by MIDL compiler version 7.00.0499 */
/* Compiler settings for icftypes.idl:
Oicf, W1, Zp8, env=Win32 (32b run)
protocol : dce , ms_ext, c_ext, robust
error checks: allocation ref bounds_check enum stub_data 
VC __declspec() decoration level: 
__declspec(uuid()), __declspec(selectany), __declspec(novtable)
DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 500
#endif

/* verify that the <rpcsal.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCSAL_H_VERSION__
#define __REQUIRED_RPCSAL_H_VERSION__ 100
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__


#ifndef __icftypes_h__
#define __icftypes_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

/* header files for imported files */
#include "wtypes.h"

#ifdef __cplusplus
extern "C"{
#endif 


	/* interface __MIDL_itf_icftypes_0000_0000 */
	/* [local] */ 

	typedef 
		enum NET_FW_POLICY_TYPE_
	{	NET_FW_POLICY_GROUP	= 0,
	NET_FW_POLICY_LOCAL	= ( NET_FW_POLICY_GROUP + 1 ) ,
	NET_FW_POLICY_EFFECTIVE	= ( NET_FW_POLICY_LOCAL + 1 ) ,
	NET_FW_POLICY_TYPE_MAX	= ( NET_FW_POLICY_EFFECTIVE + 1 ) 
	} 	NET_FW_POLICY_TYPE;

	typedef 
		enum NET_FW_PROFILE_TYPE_
	{	NET_FW_PROFILE_DOMAIN	= 0,
	NET_FW_PROFILE_STANDARD	= ( NET_FW_PROFILE_DOMAIN + 1 ) ,
	NET_FW_PROFILE_CURRENT	= ( NET_FW_PROFILE_STANDARD + 1 ) ,
	NET_FW_PROFILE_TYPE_MAX	= ( NET_FW_PROFILE_CURRENT + 1 ) 
	} 	NET_FW_PROFILE_TYPE;

	typedef 
		enum NET_FW_PROFILE_TYPE2_
	{	NET_FW_PROFILE2_DOMAIN	= 0x1,
	NET_FW_PROFILE2_PRIVATE	= 0x2,
	NET_FW_PROFILE2_PUBLIC	= 0x4,
	NET_FW_PROFILE2_ALL	= 0x7fffffff
	} 	NET_FW_PROFILE_TYPE2;

	typedef 
		enum NET_FW_IP_VERSION_
	{	NET_FW_IP_VERSION_V4	= 0,
	NET_FW_IP_VERSION_V6	= ( NET_FW_IP_VERSION_V4 + 1 ) ,
	NET_FW_IP_VERSION_ANY	= ( NET_FW_IP_VERSION_V6 + 1 ) ,
	NET_FW_IP_VERSION_MAX	= ( NET_FW_IP_VERSION_ANY + 1 ) 
	} 	NET_FW_IP_VERSION;

	typedef 
		enum NET_FW_SCOPE_
	{	NET_FW_SCOPE_ALL	= 0,
	NET_FW_SCOPE_LOCAL_SUBNET	= ( NET_FW_SCOPE_ALL + 1 ) ,
	NET_FW_SCOPE_CUSTOM	= ( NET_FW_SCOPE_LOCAL_SUBNET + 1 ) ,
	NET_FW_SCOPE_MAX	= ( NET_FW_SCOPE_CUSTOM + 1 ) 
	} 	NET_FW_SCOPE;

	typedef 
		enum NET_FW_IP_PROTOCOL_
	{	NET_FW_IP_PROTOCOL_TCP	= 6,
	NET_FW_IP_PROTOCOL_UDP	= 17,
	NET_FW_IP_PROTOCOL_ANY	= 256
	} 	NET_FW_IP_PROTOCOL;

	typedef 
		enum NET_FW_SERVICE_TYPE_
	{	NET_FW_SERVICE_FILE_AND_PRINT	= 0,
	NET_FW_SERVICE_UPNP	= ( NET_FW_SERVICE_FILE_AND_PRINT + 1 ) ,
	NET_FW_SERVICE_REMOTE_DESKTOP	= ( NET_FW_SERVICE_UPNP + 1 ) ,
	NET_FW_SERVICE_NONE	= ( NET_FW_SERVICE_REMOTE_DESKTOP + 1 ) ,
	NET_FW_SERVICE_TYPE_MAX	= ( NET_FW_SERVICE_NONE + 1 ) 
	} 	NET_FW_SERVICE_TYPE;

	typedef 
		enum NET_FW_RULE_DIRECTION_
	{	NET_FW_RULE_DIR_IN	= 1,
	NET_FW_RULE_DIR_OUT	= ( NET_FW_RULE_DIR_IN + 1 ) ,
	NET_FW_RULE_DIR_MAX	= ( NET_FW_RULE_DIR_OUT + 1 ) 
	} 	NET_FW_RULE_DIRECTION;

	typedef 
		enum NET_FW_ACTION_
	{	NET_FW_ACTION_BLOCK	= 0,
	NET_FW_ACTION_ALLOW	= ( NET_FW_ACTION_BLOCK + 1 ) ,
	NET_FW_ACTION_MAX	= ( NET_FW_ACTION_ALLOW + 1 ) 
	} 	NET_FW_ACTION;

	typedef 
		enum NET_FW_MODIFY_STATE_
	{	NET_FW_MODIFY_STATE_OK	= 0,
	NET_FW_MODIFY_STATE_GP_OVERRIDE	= ( NET_FW_MODIFY_STATE_OK + 1 ) ,
	NET_FW_MODIFY_STATE_INBOUND_BLOCKED	= ( NET_FW_MODIFY_STATE_GP_OVERRIDE + 1 ) 
	} 	NET_FW_MODIFY_STATE;



	extern RPC_IF_HANDLE __MIDL_itf_icftypes_0000_0000_v0_0_c_ifspec;
	extern RPC_IF_HANDLE __MIDL_itf_icftypes_0000_0000_v0_0_s_ifspec;

	/* Additional Prototypes for ALL interfaces */

	/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif

//end icftypes.h

//merge code from MS "oaidl.h"


/* this ALWAYS GENERATED file contains the definitions for the interfaces */


/* File created by MIDL compiler version 6.00.0366 */
/* Compiler settings for oaidl.idl:
Oicf, W1, Zp8, env=Win32 (32b run)
protocol : dce , ms_ext, c_ext, robust
error checks: allocation ref bounds_check enum stub_data 
VC __declspec() decoration level: 
__declspec(uuid()), __declspec(selectany), __declspec(novtable)
DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 475
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __oaidl_h__
#define __oaidl_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __ICreateTypeInfo_FWD_DEFINED__
#define __ICreateTypeInfo_FWD_DEFINED__
typedef interface ICreateTypeInfo ICreateTypeInfo;
#endif 	/* __ICreateTypeInfo_FWD_DEFINED__ */


#ifndef __ICreateTypeInfo2_FWD_DEFINED__
#define __ICreateTypeInfo2_FWD_DEFINED__
typedef interface ICreateTypeInfo2 ICreateTypeInfo2;
#endif 	/* __ICreateTypeInfo2_FWD_DEFINED__ */


#ifndef __ICreateTypeLib_FWD_DEFINED__
#define __ICreateTypeLib_FWD_DEFINED__
typedef interface ICreateTypeLib ICreateTypeLib;
#endif 	/* __ICreateTypeLib_FWD_DEFINED__ */


#ifndef __ICreateTypeLib2_FWD_DEFINED__
#define __ICreateTypeLib2_FWD_DEFINED__
typedef interface ICreateTypeLib2 ICreateTypeLib2;
#endif 	/* __ICreateTypeLib2_FWD_DEFINED__ */


#ifndef __IDispatch_FWD_DEFINED__
#define __IDispatch_FWD_DEFINED__
typedef interface IDispatch IDispatch;
#endif 	/* __IDispatch_FWD_DEFINED__ */


#ifndef __IEnumVARIANT_FWD_DEFINED__
#define __IEnumVARIANT_FWD_DEFINED__
typedef interface IEnumVARIANT IEnumVARIANT;
#endif 	/* __IEnumVARIANT_FWD_DEFINED__ */


#ifndef __ITypeComp_FWD_DEFINED__
#define __ITypeComp_FWD_DEFINED__
typedef interface ITypeComp ITypeComp;
#endif 	/* __ITypeComp_FWD_DEFINED__ */


#ifndef __ITypeInfo_FWD_DEFINED__
#define __ITypeInfo_FWD_DEFINED__
typedef interface ITypeInfo ITypeInfo;
#endif 	/* __ITypeInfo_FWD_DEFINED__ */


#ifndef __ITypeInfo2_FWD_DEFINED__
#define __ITypeInfo2_FWD_DEFINED__
typedef interface ITypeInfo2 ITypeInfo2;
#endif 	/* __ITypeInfo2_FWD_DEFINED__ */


#ifndef __ITypeLib_FWD_DEFINED__
#define __ITypeLib_FWD_DEFINED__
typedef interface ITypeLib ITypeLib;
#endif 	/* __ITypeLib_FWD_DEFINED__ */


#ifndef __ITypeLib2_FWD_DEFINED__
#define __ITypeLib2_FWD_DEFINED__
typedef interface ITypeLib2 ITypeLib2;
#endif 	/* __ITypeLib2_FWD_DEFINED__ */


#ifndef __ITypeChangeEvents_FWD_DEFINED__
#define __ITypeChangeEvents_FWD_DEFINED__
typedef interface ITypeChangeEvents ITypeChangeEvents;
#endif 	/* __ITypeChangeEvents_FWD_DEFINED__ */


#ifndef __IErrorInfo_FWD_DEFINED__
#define __IErrorInfo_FWD_DEFINED__
typedef interface IErrorInfo IErrorInfo;
#endif 	/* __IErrorInfo_FWD_DEFINED__ */


#ifndef __ICreateErrorInfo_FWD_DEFINED__
#define __ICreateErrorInfo_FWD_DEFINED__
typedef interface ICreateErrorInfo ICreateErrorInfo;
#endif 	/* __ICreateErrorInfo_FWD_DEFINED__ */


#ifndef __ISupportErrorInfo_FWD_DEFINED__
#define __ISupportErrorInfo_FWD_DEFINED__
typedef interface ISupportErrorInfo ISupportErrorInfo;
#endif 	/* __ISupportErrorInfo_FWD_DEFINED__ */


#ifndef __ITypeFactory_FWD_DEFINED__
#define __ITypeFactory_FWD_DEFINED__
typedef interface ITypeFactory ITypeFactory;
#endif 	/* __ITypeFactory_FWD_DEFINED__ */


#ifndef __ITypeMarshal_FWD_DEFINED__
#define __ITypeMarshal_FWD_DEFINED__
typedef interface ITypeMarshal ITypeMarshal;
#endif 	/* __ITypeMarshal_FWD_DEFINED__ */


#ifndef __IRecordInfo_FWD_DEFINED__
#define __IRecordInfo_FWD_DEFINED__
typedef interface IRecordInfo IRecordInfo;
#endif 	/* __IRecordInfo_FWD_DEFINED__ */


#ifndef __IErrorLog_FWD_DEFINED__
#define __IErrorLog_FWD_DEFINED__
typedef interface IErrorLog IErrorLog;
#endif 	/* __IErrorLog_FWD_DEFINED__ */


#ifndef __IPropertyBag_FWD_DEFINED__
#define __IPropertyBag_FWD_DEFINED__
typedef interface IPropertyBag IPropertyBag;
#endif 	/* __IPropertyBag_FWD_DEFINED__ */


/* header files for imported files */
#include "objidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

	void * __RPC_USER MIDL_user_allocate(size_t);
	void __RPC_USER MIDL_user_free( void * ); 

	/* interface __MIDL_itf_oaidl_0000 */
	/* [local] */ 

	//+-------------------------------------------------------------------------
	//
	//  Microsoft Windows
	//  Copyright (c) Microsoft Corporation. All rights reserved.
	//
	//--------------------------------------------------------------------------
#if ( _MSC_VER >= 800 )
#if _MSC_VER >= 1200
#pragma warning(push)
#endif
#pragma warning(disable:4201)    /* Nameless struct/union */
#endif
#if ( _MSC_VER >= 1020 )
#pragma once
#endif




















	extern RPC_IF_HANDLE __MIDL_itf_oaidl_0000_v0_0_c_ifspec;
	extern RPC_IF_HANDLE __MIDL_itf_oaidl_0000_v0_0_s_ifspec;

#ifndef __IOleAutomationTypes_INTERFACE_DEFINED__
#define __IOleAutomationTypes_INTERFACE_DEFINED__

	/* interface IOleAutomationTypes */
	/* [auto_handle][unique][version] */ 

	typedef CY CURRENCY;

	typedef struct tagSAFEARRAYBOUND
	{
		ULONG cElements;
		LONG lLbound;
	} 	SAFEARRAYBOUND;

	typedef struct tagSAFEARRAYBOUND *LPSAFEARRAYBOUND;

	/* the following is what MIDL knows how to remote */
	typedef /* [unique] */ struct _wireVARIANT *wireVARIANT;

	typedef /* [unique] */ struct _wireBRECORD *wireBRECORD;

	typedef struct _wireSAFEARR_BSTR
	{
		ULONG Size;
		/* [ref][size_is] */ wireBSTR *aBstr;
	} 	SAFEARR_BSTR;

	typedef struct _wireSAFEARR_UNKNOWN
	{
		ULONG Size;
		/* [ref][size_is] */ IUnknown **apUnknown;
	} 	SAFEARR_UNKNOWN;

	typedef struct _wireSAFEARR_DISPATCH
	{
		ULONG Size;
		/* [ref][size_is] */ IDispatch **apDispatch;
	} 	SAFEARR_DISPATCH;

	typedef struct _wireSAFEARR_VARIANT
	{
		ULONG Size;
		/* [ref][size_is] */ wireVARIANT *aVariant;
	} 	SAFEARR_VARIANT;

	typedef struct _wireSAFEARR_BRECORD
	{
		ULONG Size;
		/* [ref][size_is] */ wireBRECORD *aRecord;
	} 	SAFEARR_BRECORD;

	typedef struct _wireSAFEARR_HAVEIID
	{
		ULONG Size;
		/* [ref][size_is] */ IUnknown **apUnknown;
		IID iid;
	} 	SAFEARR_HAVEIID;

	typedef /* [v1_enum] */ 
		enum tagSF_TYPE
	{	SF_ERROR	= VT_ERROR,
	SF_I1	= VT_I1,
	SF_I2	= VT_I2,
	SF_I4	= VT_I4,
	SF_I8	= VT_I8,
	SF_BSTR	= VT_BSTR,
	SF_UNKNOWN	= VT_UNKNOWN,
	SF_DISPATCH	= VT_DISPATCH,
	SF_VARIANT	= VT_VARIANT,
	SF_RECORD	= VT_RECORD,
	SF_HAVEIID	= VT_UNKNOWN | VT_RESERVED
	} 	SF_TYPE;

	typedef struct _wireSAFEARRAY_UNION
	{
		ULONG sfType;
		/* [switch_is] */ /* [switch_type] */ union __MIDL_IOleAutomationTypes_0001
		{
			/* [case()] */ SAFEARR_BSTR BstrStr;
			/* [case()] */ SAFEARR_UNKNOWN UnknownStr;
			/* [case()] */ SAFEARR_DISPATCH DispatchStr;
			/* [case()] */ SAFEARR_VARIANT VariantStr;
			/* [case()] */ SAFEARR_BRECORD RecordStr;
			/* [case()] */ SAFEARR_HAVEIID HaveIidStr;
			/* [case()] */ BYTE_SIZEDARR ByteStr;
			/* [case()] */ WORD_SIZEDARR WordStr;
			/* [case()] */ DWORD_SIZEDARR LongStr;
			/* [case()] */ HYPER_SIZEDARR HyperStr;
		} 	u;
	} 	SAFEARRAYUNION;

	typedef /* [unique] */ struct _wireSAFEARRAY
	{
		USHORT cDims;
		USHORT fFeatures;
		ULONG cbElements;
		ULONG cLocks;
		SAFEARRAYUNION uArrayStructs;
		/* [size_is] */ SAFEARRAYBOUND rgsabound[ 1 ];
	} 	*wireSAFEARRAY;

	typedef /* [unique] */ wireSAFEARRAY *wirePSAFEARRAY;

	typedef struct tagSAFEARRAY
	{
		USHORT cDims;
		USHORT fFeatures;
		ULONG cbElements;
		ULONG cLocks;
		PVOID pvData;
		SAFEARRAYBOUND rgsabound[ 1 ];
	} 	SAFEARRAY;

	typedef /* [wire_marshal] */ SAFEARRAY *LPSAFEARRAY;

#define	FADF_AUTO	( 0x1 )

#define	FADF_STATIC	( 0x2 )

#define	FADF_EMBEDDED	( 0x4 )

#define	FADF_FIXEDSIZE	( 0x10 )

#define	FADF_RECORD	( 0x20 )

#define	FADF_HAVEIID	( 0x40 )

#define	FADF_HAVEVARTYPE	( 0x80 )

#define	FADF_BSTR	( 0x100 )

#define	FADF_UNKNOWN	( 0x200 )

#define	FADF_DISPATCH	( 0x400 )

#define	FADF_VARIANT	( 0x800 )

#define	FADF_RESERVED	( 0xf008 )

	/* VARIANT STRUCTURE
	*
	*  VARTYPE vt;
	*  WORD wReserved1;
	*  WORD wReserved2;
	*  WORD wReserved3;
	*  union {
	*    LONGLONG       VT_I8
	*    LONG           VT_I4
	*    BYTE           VT_UI1
	*    SHORT          VT_I2
	*    FLOAT          VT_R4
	*    DOUBLE         VT_R8
	*    VARIANT_BOOL   VT_BOOL
	*    SCODE          VT_ERROR
	*    CY             VT_CY
	*    DATE           VT_DATE
	*    BSTR           VT_BSTR
	*    IUnknown *     VT_UNKNOWN
	*    IDispatch *    VT_DISPATCH
	*    SAFEARRAY *    VT_ARRAY
	*    BYTE *         VT_BYREF|VT_UI1
	*    SHORT *        VT_BYREF|VT_I2
	*    LONG *         VT_BYREF|VT_I4
	*    LONGLONG *     VT_BYREF|VT_I8
	*    FLOAT *        VT_BYREF|VT_R4
	*    DOUBLE *       VT_BYREF|VT_R8
	*    VARIANT_BOOL * VT_BYREF|VT_BOOL
	*    SCODE *        VT_BYREF|VT_ERROR
	*    CY *           VT_BYREF|VT_CY
	*    DATE *         VT_BYREF|VT_DATE
	*    BSTR *         VT_BYREF|VT_BSTR
	*    IUnknown **    VT_BYREF|VT_UNKNOWN
	*    IDispatch **   VT_BYREF|VT_DISPATCH
	*    SAFEARRAY **   VT_BYREF|VT_ARRAY
	*    VARIANT *      VT_BYREF|VT_VARIANT
	*    PVOID          VT_BYREF (Generic ByRef)
	*    CHAR           VT_I1
	*    USHORT         VT_UI2
	*    ULONG          VT_UI4
	*    ULONGLONG      VT_UI8
	*    INT            VT_INT
	*    UINT           VT_UINT
	*    DECIMAL *      VT_BYREF|VT_DECIMAL
	*    CHAR *         VT_BYREF|VT_I1
	*    USHORT *       VT_BYREF|VT_UI2
	*    ULONG *        VT_BYREF|VT_UI4
	*    ULONGLONG *    VT_BYREF|VT_UI8
	*    INT *          VT_BYREF|VT_INT
	*    UINT *         VT_BYREF|VT_UINT
	*  }
	*/
#if (__STDC__ && !defined(_FORCENAMELESSUNION)) || defined(NONAMELESSUNION)
#define __VARIANT_NAME_1 n1
#define __VARIANT_NAME_2 n2
#define __VARIANT_NAME_3 n3
#define __VARIANT_NAME_4 brecVal
#else
#define __tagVARIANT
#define __VARIANT_NAME_1
#define __VARIANT_NAME_2
#define __VARIANT_NAME_3
#define __tagBRECORD
#define __VARIANT_NAME_4
#endif
	typedef /* [wire_marshal] */ struct tagVARIANT VARIANT;

	struct tagVARIANT
	{
		union 
		{
			struct __tagVARIANT
			{
				VARTYPE vt;
				WORD wReserved1;
				WORD wReserved2;
				WORD wReserved3;
				union 
				{
					LONGLONG llVal;
					LONG lVal;
					BYTE bVal;
					SHORT iVal;
					FLOAT fltVal;
					DOUBLE dblVal;
					VARIANT_BOOL boolVal;
					_VARIANT_BOOL bool;
					SCODE scode;
					CY cyVal;
					DATE date;
					BSTR bstrVal;
					IUnknown *punkVal;
					IDispatch *pdispVal;
					SAFEARRAY *parray;
					BYTE *pbVal;
					SHORT *piVal;
					LONG *plVal;
					LONGLONG *pllVal;
					FLOAT *pfltVal;
					DOUBLE *pdblVal;
					VARIANT_BOOL *pboolVal;
					_VARIANT_BOOL *pbool;
					SCODE *pscode;
					CY *pcyVal;
					DATE *pdate;
					BSTR *pbstrVal;
					IUnknown **ppunkVal;
					IDispatch **ppdispVal;
					SAFEARRAY **pparray;
					VARIANT *pvarVal;
					PVOID byref;
					CHAR cVal;
					USHORT uiVal;
					ULONG ulVal;
					ULONGLONG ullVal;
					INT intVal;
					UINT uintVal;
					DECIMAL *pdecVal;
					CHAR *pcVal;
					USHORT *puiVal;
					ULONG *pulVal;
					ULONGLONG *pullVal;
					INT *pintVal;
					UINT *puintVal;
					struct __tagBRECORD
					{
						PVOID pvRecord;
						IRecordInfo *pRecInfo;
					} 	__VARIANT_NAME_4;
				} 	__VARIANT_NAME_3;
			} 	__VARIANT_NAME_2;
			DECIMAL decVal;
		} 	__VARIANT_NAME_1;
	} ;
	typedef VARIANT *LPVARIANT;

	typedef VARIANT VARIANTARG;

	typedef VARIANT *LPVARIANTARG;

	/* the following is what MIDL knows how to remote */
	struct _wireBRECORD
	{
		ULONG fFlags;
		ULONG clSize;
		IRecordInfo *pRecInfo;
		/* [size_is] */ byte *pRecord;
	} ;
	struct _wireVARIANT
	{
		DWORD clSize;
		DWORD rpcReserved;
		USHORT vt;
		USHORT wReserved1;
		USHORT wReserved2;
		USHORT wReserved3;
		/* [switch_is][switch_type] */ union 
		{
			/* [case()] */ LONGLONG llVal;
			/* [case()] */ LONG lVal;
			/* [case()] */ BYTE bVal;
			/* [case()] */ SHORT iVal;
			/* [case()] */ FLOAT fltVal;
			/* [case()] */ DOUBLE dblVal;
			/* [case()] */ VARIANT_BOOL boolVal;
			/* [case()] */ SCODE scode;
			/* [case()] */ CY cyVal;
			/* [case()] */ DATE date;
			/* [case()] */ wireBSTR bstrVal;
			/* [case()] */ IUnknown *punkVal;
			/* [case()] */ IDispatch *pdispVal;
			/* [case()] */ wirePSAFEARRAY parray;
			/* [case()] */ wireBRECORD brecVal;
			/* [case()] */ BYTE *pbVal;
			/* [case()] */ SHORT *piVal;
			/* [case()] */ LONG *plVal;
			/* [case()] */ LONGLONG *pllVal;
			/* [case()] */ FLOAT *pfltVal;
			/* [case()] */ DOUBLE *pdblVal;
			/* [case()] */ VARIANT_BOOL *pboolVal;
			/* [case()] */ SCODE *pscode;
			/* [case()] */ CY *pcyVal;
			/* [case()] */ DATE *pdate;
			/* [case()] */ wireBSTR *pbstrVal;
			/* [case()] */ IUnknown **ppunkVal;
			/* [case()] */ IDispatch **ppdispVal;
			/* [case()] */ wirePSAFEARRAY *pparray;
			/* [case()] */ wireVARIANT *pvarVal;
			/* [case()] */ CHAR cVal;
			/* [case()] */ USHORT uiVal;
			/* [case()] */ ULONG ulVal;
			/* [case()] */ ULONGLONG ullVal;
			/* [case()] */ INT intVal;
			/* [case()] */ UINT uintVal;
			/* [case()] */ DECIMAL decVal;
			/* [case()] */ DECIMAL *pdecVal;
			/* [case()] */ CHAR *pcVal;
			/* [case()] */ USHORT *puiVal;
			/* [case()] */ ULONG *pulVal;
			/* [case()] */ ULONGLONG *pullVal;
			/* [case()] */ INT *pintVal;
			/* [case()] */ UINT *puintVal;
			/* [case()] */  /* Empty union arm */ 
			/* [case()] */  /* Empty union arm */ 
		} 	;
	} ;
	typedef LONG DISPID;

	typedef DISPID MEMBERID;

	typedef DWORD HREFTYPE;

	typedef /* [v1_enum] */ 
		enum tagTYPEKIND
	{	TKIND_ENUM	= 0,
	TKIND_RECORD	= TKIND_ENUM + 1,
	TKIND_MODULE	= TKIND_RECORD + 1,
	TKIND_INTERFACE	= TKIND_MODULE + 1,
	TKIND_DISPATCH	= TKIND_INTERFACE + 1,
	TKIND_COCLASS	= TKIND_DISPATCH + 1,
	TKIND_ALIAS	= TKIND_COCLASS + 1,
	TKIND_UNION	= TKIND_ALIAS + 1,
	TKIND_MAX	= TKIND_UNION + 1
	} 	TYPEKIND;

	typedef struct tagTYPEDESC
	{
		/* [switch_is][switch_type] */ union 
		{
			/* [case()] */ struct tagTYPEDESC *lptdesc;
			/* [case()] */ struct tagARRAYDESC *lpadesc;
			/* [case()] */ HREFTYPE hreftype;
			/* [default] */  /* Empty union arm */ 
		} 	;
		VARTYPE vt;
	} 	TYPEDESC;

	typedef struct tagARRAYDESC
	{
		TYPEDESC tdescElem;
		USHORT cDims;
		/* [size_is] */ SAFEARRAYBOUND rgbounds[ 1 ];
	} 	ARRAYDESC;

	typedef struct tagPARAMDESCEX
	{
		ULONG cBytes;
		VARIANTARG varDefaultValue;
	} 	PARAMDESCEX;

	typedef struct tagPARAMDESCEX *LPPARAMDESCEX;

	typedef struct tagPARAMDESC
	{
		LPPARAMDESCEX pparamdescex;
		USHORT wParamFlags;
	} 	PARAMDESC;

	typedef struct tagPARAMDESC *LPPARAMDESC;

#define	PARAMFLAG_NONE	( 0 )

#define	PARAMFLAG_FIN	( 0x1 )

#define	PARAMFLAG_FOUT	( 0x2 )

#define	PARAMFLAG_FLCID	( 0x4 )

#define	PARAMFLAG_FRETVAL	( 0x8 )

#define	PARAMFLAG_FOPT	( 0x10 )

#define	PARAMFLAG_FHASDEFAULT	( 0x20 )

#define	PARAMFLAG_FHASCUSTDATA	( 0x40 )

	typedef struct tagIDLDESC
	{
		ULONG_PTR dwReserved;
		USHORT wIDLFlags;
	} 	IDLDESC;

	typedef struct tagIDLDESC *LPIDLDESC;

#define	IDLFLAG_NONE	( PARAMFLAG_NONE )

#define	IDLFLAG_FIN	( PARAMFLAG_FIN )

#define	IDLFLAG_FOUT	( PARAMFLAG_FOUT )

#define	IDLFLAG_FLCID	( PARAMFLAG_FLCID )

#define	IDLFLAG_FRETVAL	( PARAMFLAG_FRETVAL )

	//;begin_internal
#if 0
	/* the following is what MIDL knows how to remote */
	typedef struct tagELEMDESC
	{
		TYPEDESC tdesc;
		PARAMDESC paramdesc;
	} 	ELEMDESC;

#else /* 0 */
	//;end_internal
	typedef struct tagELEMDESC {
		TYPEDESC tdesc;             /* the type of the element */
		union {
			IDLDESC idldesc;        /* info for remoting the element */
			PARAMDESC paramdesc;    /* info about the parameter */
		};
	} ELEMDESC, * LPELEMDESC;
	//;begin_internal
#endif /* 0 */
	//;end_internal
	typedef struct tagTYPEATTR
	{
		GUID guid;
		LCID lcid;
		DWORD dwReserved;
		MEMBERID memidConstructor;
		MEMBERID memidDestructor;
		LPOLESTR lpstrSchema;
		ULONG cbSizeInstance;
		TYPEKIND typekind;
		WORD cFuncs;
		WORD cVars;
		WORD cImplTypes;
		WORD cbSizeVft;
		WORD cbAlignment;
		WORD wTypeFlags;
		WORD wMajorVerNum;
		WORD wMinorVerNum;
		TYPEDESC tdescAlias;
		IDLDESC idldescType;
	} 	TYPEATTR;

	typedef struct tagTYPEATTR *LPTYPEATTR;

	typedef struct tagDISPPARAMS
	{
		/* [size_is] */ VARIANTARG *rgvarg;
		/* [size_is] */ DISPID *rgdispidNamedArgs;
		UINT cArgs;
		UINT cNamedArgs;
	} 	DISPPARAMS;

	//;begin_internal
#if 0
	/* the following is what MIDL knows how to remote */

	typedef struct tagEXCEPINFO
	{
		WORD wCode;
		WORD wReserved;
		BSTR bstrSource;
		BSTR bstrDescription;
		BSTR bstrHelpFile;
		DWORD dwHelpContext;
		ULONG_PTR pvReserved;
		ULONG_PTR pfnDeferredFillIn;
		SCODE scode;
	} 	EXCEPINFO;

#else /* 0 */
	//;end_internal
	typedef struct tagEXCEPINFO {
		WORD  wCode;
		WORD  wReserved;
		BSTR  bstrSource;
		BSTR  bstrDescription;
		BSTR  bstrHelpFile;
		DWORD dwHelpContext;
		PVOID pvReserved;
		HRESULT (__stdcall *pfnDeferredFillIn)(struct tagEXCEPINFO *);
		SCODE scode;
	} EXCEPINFO, * LPEXCEPINFO;
	//;begin_internal
#endif /* 0 */
	//;end_internal
	typedef /* [v1_enum] */ 
		enum tagCALLCONV
	{	CC_FASTCALL	= 0,
	CC_CDECL	= 1,
	CC_MSCPASCAL	= CC_CDECL + 1,
	CC_PASCAL	= CC_MSCPASCAL,
	CC_MACPASCAL	= CC_PASCAL + 1,
	CC_STDCALL	= CC_MACPASCAL + 1,
	CC_FPFASTCALL	= CC_STDCALL + 1,
	CC_SYSCALL	= CC_FPFASTCALL + 1,
	CC_MPWCDECL	= CC_SYSCALL + 1,
	CC_MPWPASCAL	= CC_MPWCDECL + 1,
	CC_MAX	= CC_MPWPASCAL + 1
	} 	CALLCONV;

	typedef /* [v1_enum] */ 
		enum tagFUNCKIND
	{	FUNC_VIRTUAL	= 0,
	FUNC_PUREVIRTUAL	= FUNC_VIRTUAL + 1,
	FUNC_NONVIRTUAL	= FUNC_PUREVIRTUAL + 1,
	FUNC_STATIC	= FUNC_NONVIRTUAL + 1,
	FUNC_DISPATCH	= FUNC_STATIC + 1
	} 	FUNCKIND;

	typedef /* [v1_enum] */ 
		enum tagINVOKEKIND
	{	INVOKE_FUNC	= 1,
	INVOKE_PROPERTYGET	= 2,
	INVOKE_PROPERTYPUT	= 4,
	INVOKE_PROPERTYPUTREF	= 8
	} 	INVOKEKIND;

	typedef struct tagFUNCDESC
	{
		MEMBERID memid;
		/* [size_is] */ SCODE *lprgscode;
		/* [size_is] */ ELEMDESC *lprgelemdescParam;
		FUNCKIND funckind;
		INVOKEKIND invkind;
		CALLCONV callconv;
		SHORT cParams;
		SHORT cParamsOpt;
		SHORT oVft;
		SHORT cScodes;
		ELEMDESC elemdescFunc;
		WORD wFuncFlags;
	} 	FUNCDESC;

	typedef struct tagFUNCDESC *LPFUNCDESC;

	typedef /* [v1_enum] */ 
		enum tagVARKIND
	{	VAR_PERINSTANCE	= 0,
	VAR_STATIC	= VAR_PERINSTANCE + 1,
	VAR_CONST	= VAR_STATIC + 1,
	VAR_DISPATCH	= VAR_CONST + 1
	} 	VARKIND;

#define	IMPLTYPEFLAG_FDEFAULT	( 0x1 )

#define	IMPLTYPEFLAG_FSOURCE	( 0x2 )

#define	IMPLTYPEFLAG_FRESTRICTED	( 0x4 )

#define	IMPLTYPEFLAG_FDEFAULTVTABLE	( 0x8 )

	typedef struct tagVARDESC
	{
		MEMBERID memid;
		LPOLESTR lpstrSchema;
		/* [switch_is][switch_type] */ union 
		{
			/* [case()] */ ULONG oInst;
			/* [case()] */ VARIANT *lpvarValue;
		} 	;
		ELEMDESC elemdescVar;
		WORD wVarFlags;
		VARKIND varkind;
	} 	VARDESC;

	typedef struct tagVARDESC *LPVARDESC;

	typedef 
		enum tagTYPEFLAGS
	{	TYPEFLAG_FAPPOBJECT	= 0x1,
	TYPEFLAG_FCANCREATE	= 0x2,
	TYPEFLAG_FLICENSED	= 0x4,
	TYPEFLAG_FPREDECLID	= 0x8,
	TYPEFLAG_FHIDDEN	= 0x10,
	TYPEFLAG_FCONTROL	= 0x20,
	TYPEFLAG_FDUAL	= 0x40,
	TYPEFLAG_FNONEXTENSIBLE	= 0x80,
	TYPEFLAG_FOLEAUTOMATION	= 0x100,
	TYPEFLAG_FRESTRICTED	= 0x200,
	TYPEFLAG_FAGGREGATABLE	= 0x400,
	TYPEFLAG_FREPLACEABLE	= 0x800,
	TYPEFLAG_FDISPATCHABLE	= 0x1000,
	TYPEFLAG_FREVERSEBIND	= 0x2000,
	TYPEFLAG_FPROXY	= 0x4000
	} 	TYPEFLAGS;

	typedef 
		enum tagFUNCFLAGS
	{	FUNCFLAG_FRESTRICTED	= 0x1,
	FUNCFLAG_FSOURCE	= 0x2,
	FUNCFLAG_FBINDABLE	= 0x4,
	FUNCFLAG_FREQUESTEDIT	= 0x8,
	FUNCFLAG_FDISPLAYBIND	= 0x10,
	FUNCFLAG_FDEFAULTBIND	= 0x20,
	FUNCFLAG_FHIDDEN	= 0x40,
	FUNCFLAG_FUSESGETLASTERROR	= 0x80,
	FUNCFLAG_FDEFAULTCOLLELEM	= 0x100,
	FUNCFLAG_FUIDEFAULT	= 0x200,
	FUNCFLAG_FNONBROWSABLE	= 0x400,
	FUNCFLAG_FREPLACEABLE	= 0x800,
	FUNCFLAG_FIMMEDIATEBIND	= 0x1000
	} 	FUNCFLAGS;

	typedef 
		enum tagVARFLAGS
	{	VARFLAG_FREADONLY	= 0x1,
	VARFLAG_FSOURCE	= 0x2,
	VARFLAG_FBINDABLE	= 0x4,
	VARFLAG_FREQUESTEDIT	= 0x8,
	VARFLAG_FDISPLAYBIND	= 0x10,
	VARFLAG_FDEFAULTBIND	= 0x20,
	VARFLAG_FHIDDEN	= 0x40,
	VARFLAG_FRESTRICTED	= 0x80,
	VARFLAG_FDEFAULTCOLLELEM	= 0x100,
	VARFLAG_FUIDEFAULT	= 0x200,
	VARFLAG_FNONBROWSABLE	= 0x400,
	VARFLAG_FREPLACEABLE	= 0x800,
	VARFLAG_FIMMEDIATEBIND	= 0x1000
	} 	VARFLAGS;

	typedef /* [wire_marshal] */ struct tagCLEANLOCALSTORAGE
	{
		IUnknown *pInterface;
		PVOID pStorage;
		DWORD flags;
	} 	CLEANLOCALSTORAGE;

	typedef struct tagCUSTDATAITEM
	{
		GUID guid;
		VARIANTARG varValue;
	} 	CUSTDATAITEM;

	typedef struct tagCUSTDATAITEM *LPCUSTDATAITEM;

	typedef struct tagCUSTDATA
	{
		DWORD cCustData;
		/* [size_is] */ LPCUSTDATAITEM prgCustData;
	} 	CUSTDATA;

	typedef struct tagCUSTDATA *LPCUSTDATA;



	extern RPC_IF_HANDLE IOleAutomationTypes_v1_0_c_ifspec;
	extern RPC_IF_HANDLE IOleAutomationTypes_v1_0_s_ifspec;
#endif /* __IOleAutomationTypes_INTERFACE_DEFINED__ */

#ifndef __ICreateTypeInfo_INTERFACE_DEFINED__
#define __ICreateTypeInfo_INTERFACE_DEFINED__

	/* interface ICreateTypeInfo */
	/* [local][unique][uuid][object] */ 

	typedef /* [unique] */ ICreateTypeInfo *LPCREATETYPEINFO;


	EXTERN_C const IID IID_ICreateTypeInfo;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020405-0000-0000-C000-000000000046")
ICreateTypeInfo : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE SetGuid( 
			/* [in] */ REFGUID guid) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetTypeFlags( 
			/* [in] */ UINT uTypeFlags) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetDocString( 
			/* [in] */ LPOLESTR pStrDoc) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpContext( 
			/* [in] */ DWORD dwHelpContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetVersion( 
			/* [in] */ WORD wMajorVerNum,
			/* [in] */ WORD wMinorVerNum) = 0;

		virtual HRESULT STDMETHODCALLTYPE AddRefTypeInfo( 
			/* [in] */ ITypeInfo *pTInfo,
			/* [in] */ HREFTYPE *phRefType) = 0;

		virtual HRESULT STDMETHODCALLTYPE AddFuncDesc( 
			/* [in] */ UINT index,
			/* [in] */ FUNCDESC *pFuncDesc) = 0;

		virtual HRESULT STDMETHODCALLTYPE AddImplType( 
			/* [in] */ UINT index,
			/* [in] */ HREFTYPE hRefType) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetImplTypeFlags( 
			/* [in] */ UINT index,
			/* [in] */ INT implTypeFlags) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetAlignment( 
			/* [in] */ WORD cbAlignment) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetSchema( 
			/* [in] */ LPOLESTR pStrSchema) = 0;

		virtual HRESULT STDMETHODCALLTYPE AddVarDesc( 
			/* [in] */ UINT index,
			/* [in] */ VARDESC *pVarDesc) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetFuncAndParamNames( 
			/* [in] */ UINT index,
			/* [in][size_is][in] */ LPOLESTR *rgszNames,
			/* [in] */ UINT cNames) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetVarName( 
			/* [in] */ UINT index,
			/* [in] */ LPOLESTR szName) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetTypeDescAlias( 
			/* [in] */ TYPEDESC *pTDescAlias) = 0;

		virtual HRESULT STDMETHODCALLTYPE DefineFuncAsDllEntry( 
			/* [in] */ UINT index,
			/* [in] */ LPOLESTR szDllName,
			/* [in] */ LPOLESTR szProcName) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetFuncDocString( 
			/* [in] */ UINT index,
			/* [in] */ LPOLESTR szDocString) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetVarDocString( 
			/* [in] */ UINT index,
			/* [in] */ LPOLESTR szDocString) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetFuncHelpContext( 
			/* [in] */ UINT index,
			/* [in] */ DWORD dwHelpContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetVarHelpContext( 
			/* [in] */ UINT index,
			/* [in] */ DWORD dwHelpContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetMops( 
			/* [in] */ UINT index,
			/* [in] */ BSTR bstrMops) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetTypeIdldesc( 
			/* [in] */ IDLDESC *pIdlDesc) = 0;

		virtual HRESULT STDMETHODCALLTYPE LayOut( void) = 0;

	};

#else 	/* C style interface */

	typedef struct ICreateTypeInfoVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ICreateTypeInfo * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ICreateTypeInfo * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ICreateTypeInfo * This);

			HRESULT ( STDMETHODCALLTYPE *SetGuid )( 
				ICreateTypeInfo * This,
				/* [in] */ REFGUID guid);

			HRESULT ( STDMETHODCALLTYPE *SetTypeFlags )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT uTypeFlags);

			HRESULT ( STDMETHODCALLTYPE *SetDocString )( 
				ICreateTypeInfo * This,
				/* [in] */ LPOLESTR pStrDoc);

			HRESULT ( STDMETHODCALLTYPE *SetHelpContext )( 
				ICreateTypeInfo * This,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetVersion )( 
				ICreateTypeInfo * This,
				/* [in] */ WORD wMajorVerNum,
				/* [in] */ WORD wMinorVerNum);

			HRESULT ( STDMETHODCALLTYPE *AddRefTypeInfo )( 
				ICreateTypeInfo * This,
				/* [in] */ ITypeInfo *pTInfo,
				/* [in] */ HREFTYPE *phRefType);

			HRESULT ( STDMETHODCALLTYPE *AddFuncDesc )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ FUNCDESC *pFuncDesc);

			HRESULT ( STDMETHODCALLTYPE *AddImplType )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ HREFTYPE hRefType);

			HRESULT ( STDMETHODCALLTYPE *SetImplTypeFlags )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ INT implTypeFlags);

			HRESULT ( STDMETHODCALLTYPE *SetAlignment )( 
				ICreateTypeInfo * This,
				/* [in] */ WORD cbAlignment);

			HRESULT ( STDMETHODCALLTYPE *SetSchema )( 
				ICreateTypeInfo * This,
				/* [in] */ LPOLESTR pStrSchema);

			HRESULT ( STDMETHODCALLTYPE *AddVarDesc )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ VARDESC *pVarDesc);

			HRESULT ( STDMETHODCALLTYPE *SetFuncAndParamNames )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in][size_is][in] */ LPOLESTR *rgszNames,
				/* [in] */ UINT cNames);

			HRESULT ( STDMETHODCALLTYPE *SetVarName )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szName);

			HRESULT ( STDMETHODCALLTYPE *SetTypeDescAlias )( 
				ICreateTypeInfo * This,
				/* [in] */ TYPEDESC *pTDescAlias);

			HRESULT ( STDMETHODCALLTYPE *DefineFuncAsDllEntry )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szDllName,
				/* [in] */ LPOLESTR szProcName);

			HRESULT ( STDMETHODCALLTYPE *SetFuncDocString )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szDocString);

			HRESULT ( STDMETHODCALLTYPE *SetVarDocString )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szDocString);

			HRESULT ( STDMETHODCALLTYPE *SetFuncHelpContext )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetVarHelpContext )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetMops )( 
				ICreateTypeInfo * This,
				/* [in] */ UINT index,
				/* [in] */ BSTR bstrMops);

			HRESULT ( STDMETHODCALLTYPE *SetTypeIdldesc )( 
				ICreateTypeInfo * This,
				/* [in] */ IDLDESC *pIdlDesc);

			HRESULT ( STDMETHODCALLTYPE *LayOut )( 
				ICreateTypeInfo * This);

		END_INTERFACE
	} ICreateTypeInfoVtbl;

	interface ICreateTypeInfo
	{
		CONST_VTBL struct ICreateTypeInfoVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ICreateTypeInfo_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ICreateTypeInfo_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ICreateTypeInfo_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ICreateTypeInfo_SetGuid(This,guid)	\
	(This)->lpVtbl -> SetGuid(This,guid)

#define ICreateTypeInfo_SetTypeFlags(This,uTypeFlags)	\
	(This)->lpVtbl -> SetTypeFlags(This,uTypeFlags)

#define ICreateTypeInfo_SetDocString(This,pStrDoc)	\
	(This)->lpVtbl -> SetDocString(This,pStrDoc)

#define ICreateTypeInfo_SetHelpContext(This,dwHelpContext)	\
	(This)->lpVtbl -> SetHelpContext(This,dwHelpContext)

#define ICreateTypeInfo_SetVersion(This,wMajorVerNum,wMinorVerNum)	\
	(This)->lpVtbl -> SetVersion(This,wMajorVerNum,wMinorVerNum)

#define ICreateTypeInfo_AddRefTypeInfo(This,pTInfo,phRefType)	\
	(This)->lpVtbl -> AddRefTypeInfo(This,pTInfo,phRefType)

#define ICreateTypeInfo_AddFuncDesc(This,index,pFuncDesc)	\
	(This)->lpVtbl -> AddFuncDesc(This,index,pFuncDesc)

#define ICreateTypeInfo_AddImplType(This,index,hRefType)	\
	(This)->lpVtbl -> AddImplType(This,index,hRefType)

#define ICreateTypeInfo_SetImplTypeFlags(This,index,implTypeFlags)	\
	(This)->lpVtbl -> SetImplTypeFlags(This,index,implTypeFlags)

#define ICreateTypeInfo_SetAlignment(This,cbAlignment)	\
	(This)->lpVtbl -> SetAlignment(This,cbAlignment)

#define ICreateTypeInfo_SetSchema(This,pStrSchema)	\
	(This)->lpVtbl -> SetSchema(This,pStrSchema)

#define ICreateTypeInfo_AddVarDesc(This,index,pVarDesc)	\
	(This)->lpVtbl -> AddVarDesc(This,index,pVarDesc)

#define ICreateTypeInfo_SetFuncAndParamNames(This,index,rgszNames,cNames)	\
	(This)->lpVtbl -> SetFuncAndParamNames(This,index,rgszNames,cNames)

#define ICreateTypeInfo_SetVarName(This,index,szName)	\
	(This)->lpVtbl -> SetVarName(This,index,szName)

#define ICreateTypeInfo_SetTypeDescAlias(This,pTDescAlias)	\
	(This)->lpVtbl -> SetTypeDescAlias(This,pTDescAlias)

#define ICreateTypeInfo_DefineFuncAsDllEntry(This,index,szDllName,szProcName)	\
	(This)->lpVtbl -> DefineFuncAsDllEntry(This,index,szDllName,szProcName)

#define ICreateTypeInfo_SetFuncDocString(This,index,szDocString)	\
	(This)->lpVtbl -> SetFuncDocString(This,index,szDocString)

#define ICreateTypeInfo_SetVarDocString(This,index,szDocString)	\
	(This)->lpVtbl -> SetVarDocString(This,index,szDocString)

#define ICreateTypeInfo_SetFuncHelpContext(This,index,dwHelpContext)	\
	(This)->lpVtbl -> SetFuncHelpContext(This,index,dwHelpContext)

#define ICreateTypeInfo_SetVarHelpContext(This,index,dwHelpContext)	\
	(This)->lpVtbl -> SetVarHelpContext(This,index,dwHelpContext)

#define ICreateTypeInfo_SetMops(This,index,bstrMops)	\
	(This)->lpVtbl -> SetMops(This,index,bstrMops)

#define ICreateTypeInfo_SetTypeIdldesc(This,pIdlDesc)	\
	(This)->lpVtbl -> SetTypeIdldesc(This,pIdlDesc)

#define ICreateTypeInfo_LayOut(This)	\
	(This)->lpVtbl -> LayOut(This)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetGuid_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ REFGUID guid);


	void __RPC_STUB ICreateTypeInfo_SetGuid_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetTypeFlags_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT uTypeFlags);


	void __RPC_STUB ICreateTypeInfo_SetTypeFlags_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetDocString_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ LPOLESTR pStrDoc);


	void __RPC_STUB ICreateTypeInfo_SetDocString_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetHelpContext_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ DWORD dwHelpContext);


	void __RPC_STUB ICreateTypeInfo_SetHelpContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetVersion_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ WORD wMajorVerNum,
		/* [in] */ WORD wMinorVerNum);


	void __RPC_STUB ICreateTypeInfo_SetVersion_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_AddRefTypeInfo_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ ITypeInfo *pTInfo,
		/* [in] */ HREFTYPE *phRefType);


	void __RPC_STUB ICreateTypeInfo_AddRefTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_AddFuncDesc_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ FUNCDESC *pFuncDesc);


	void __RPC_STUB ICreateTypeInfo_AddFuncDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_AddImplType_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ HREFTYPE hRefType);


	void __RPC_STUB ICreateTypeInfo_AddImplType_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetImplTypeFlags_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ INT implTypeFlags);


	void __RPC_STUB ICreateTypeInfo_SetImplTypeFlags_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetAlignment_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ WORD cbAlignment);


	void __RPC_STUB ICreateTypeInfo_SetAlignment_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetSchema_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ LPOLESTR pStrSchema);


	void __RPC_STUB ICreateTypeInfo_SetSchema_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_AddVarDesc_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ VARDESC *pVarDesc);


	void __RPC_STUB ICreateTypeInfo_AddVarDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetFuncAndParamNames_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in][size_is][in] */ LPOLESTR *rgszNames,
		/* [in] */ UINT cNames);


	void __RPC_STUB ICreateTypeInfo_SetFuncAndParamNames_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetVarName_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ LPOLESTR szName);


	void __RPC_STUB ICreateTypeInfo_SetVarName_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetTypeDescAlias_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ TYPEDESC *pTDescAlias);


	void __RPC_STUB ICreateTypeInfo_SetTypeDescAlias_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_DefineFuncAsDllEntry_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ LPOLESTR szDllName,
		/* [in] */ LPOLESTR szProcName);


	void __RPC_STUB ICreateTypeInfo_DefineFuncAsDllEntry_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetFuncDocString_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ LPOLESTR szDocString);


	void __RPC_STUB ICreateTypeInfo_SetFuncDocString_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetVarDocString_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ LPOLESTR szDocString);


	void __RPC_STUB ICreateTypeInfo_SetVarDocString_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetFuncHelpContext_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ DWORD dwHelpContext);


	void __RPC_STUB ICreateTypeInfo_SetFuncHelpContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetVarHelpContext_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ DWORD dwHelpContext);


	void __RPC_STUB ICreateTypeInfo_SetVarHelpContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetMops_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ UINT index,
		/* [in] */ BSTR bstrMops);


	void __RPC_STUB ICreateTypeInfo_SetMops_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_SetTypeIdldesc_Proxy( 
		ICreateTypeInfo * This,
		/* [in] */ IDLDESC *pIdlDesc);


	void __RPC_STUB ICreateTypeInfo_SetTypeIdldesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo_LayOut_Proxy( 
		ICreateTypeInfo * This);


	void __RPC_STUB ICreateTypeInfo_LayOut_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ICreateTypeInfo_INTERFACE_DEFINED__ */


#ifndef __ICreateTypeInfo2_INTERFACE_DEFINED__
#define __ICreateTypeInfo2_INTERFACE_DEFINED__

	/* interface ICreateTypeInfo2 */
	/* [local][unique][uuid][object] */ 

	typedef /* [unique] */ ICreateTypeInfo2 *LPCREATETYPEINFO2;


	EXTERN_C const IID IID_ICreateTypeInfo2;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("0002040E-0000-0000-C000-000000000046")
ICreateTypeInfo2 : public ICreateTypeInfo
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE DeleteFuncDesc( 
			/* [in] */ UINT index) = 0;

		virtual HRESULT STDMETHODCALLTYPE DeleteFuncDescByMemId( 
			/* [in] */ MEMBERID memid,
			/* [in] */ INVOKEKIND invKind) = 0;

		virtual HRESULT STDMETHODCALLTYPE DeleteVarDesc( 
			/* [in] */ UINT index) = 0;

		virtual HRESULT STDMETHODCALLTYPE DeleteVarDescByMemId( 
			/* [in] */ MEMBERID memid) = 0;

		virtual HRESULT STDMETHODCALLTYPE DeleteImplType( 
			/* [in] */ UINT index) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetCustData( 
			/* [in] */ REFGUID guid,
			/* [in] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetFuncCustData( 
			/* [in] */ UINT index,
			/* [in] */ REFGUID guid,
			/* [in] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetParamCustData( 
			/* [in] */ UINT indexFunc,
			/* [in] */ UINT indexParam,
			/* [in] */ REFGUID guid,
			/* [in] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetVarCustData( 
			/* [in] */ UINT index,
			/* [in] */ REFGUID guid,
			/* [in] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetImplTypeCustData( 
			/* [in] */ UINT index,
			/* [in] */ REFGUID guid,
			/* [in] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpStringContext( 
			/* [in] */ ULONG dwHelpStringContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetFuncHelpStringContext( 
			/* [in] */ UINT index,
			/* [in] */ ULONG dwHelpStringContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetVarHelpStringContext( 
			/* [in] */ UINT index,
			/* [in] */ ULONG dwHelpStringContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE Invalidate( void) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetName( 
			/* [in] */ LPOLESTR szName) = 0;

	};

#else 	/* C style interface */

	typedef struct ICreateTypeInfo2Vtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ICreateTypeInfo2 * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ICreateTypeInfo2 * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ICreateTypeInfo2 * This);

			HRESULT ( STDMETHODCALLTYPE *SetGuid )( 
				ICreateTypeInfo2 * This,
				/* [in] */ REFGUID guid);

			HRESULT ( STDMETHODCALLTYPE *SetTypeFlags )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT uTypeFlags);

			HRESULT ( STDMETHODCALLTYPE *SetDocString )( 
				ICreateTypeInfo2 * This,
				/* [in] */ LPOLESTR pStrDoc);

			HRESULT ( STDMETHODCALLTYPE *SetHelpContext )( 
				ICreateTypeInfo2 * This,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetVersion )( 
				ICreateTypeInfo2 * This,
				/* [in] */ WORD wMajorVerNum,
				/* [in] */ WORD wMinorVerNum);

			HRESULT ( STDMETHODCALLTYPE *AddRefTypeInfo )( 
				ICreateTypeInfo2 * This,
				/* [in] */ ITypeInfo *pTInfo,
				/* [in] */ HREFTYPE *phRefType);

			HRESULT ( STDMETHODCALLTYPE *AddFuncDesc )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ FUNCDESC *pFuncDesc);

			HRESULT ( STDMETHODCALLTYPE *AddImplType )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ HREFTYPE hRefType);

			HRESULT ( STDMETHODCALLTYPE *SetImplTypeFlags )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ INT implTypeFlags);

			HRESULT ( STDMETHODCALLTYPE *SetAlignment )( 
				ICreateTypeInfo2 * This,
				/* [in] */ WORD cbAlignment);

			HRESULT ( STDMETHODCALLTYPE *SetSchema )( 
				ICreateTypeInfo2 * This,
				/* [in] */ LPOLESTR pStrSchema);

			HRESULT ( STDMETHODCALLTYPE *AddVarDesc )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ VARDESC *pVarDesc);

			HRESULT ( STDMETHODCALLTYPE *SetFuncAndParamNames )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in][size_is][in] */ LPOLESTR *rgszNames,
				/* [in] */ UINT cNames);

			HRESULT ( STDMETHODCALLTYPE *SetVarName )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szName);

			HRESULT ( STDMETHODCALLTYPE *SetTypeDescAlias )( 
				ICreateTypeInfo2 * This,
				/* [in] */ TYPEDESC *pTDescAlias);

			HRESULT ( STDMETHODCALLTYPE *DefineFuncAsDllEntry )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szDllName,
				/* [in] */ LPOLESTR szProcName);

			HRESULT ( STDMETHODCALLTYPE *SetFuncDocString )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szDocString);

			HRESULT ( STDMETHODCALLTYPE *SetVarDocString )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ LPOLESTR szDocString);

			HRESULT ( STDMETHODCALLTYPE *SetFuncHelpContext )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetVarHelpContext )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetMops )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ BSTR bstrMops);

			HRESULT ( STDMETHODCALLTYPE *SetTypeIdldesc )( 
				ICreateTypeInfo2 * This,
				/* [in] */ IDLDESC *pIdlDesc);

			HRESULT ( STDMETHODCALLTYPE *LayOut )( 
				ICreateTypeInfo2 * This);

			HRESULT ( STDMETHODCALLTYPE *DeleteFuncDesc )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index);

			HRESULT ( STDMETHODCALLTYPE *DeleteFuncDescByMemId )( 
				ICreateTypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [in] */ INVOKEKIND invKind);

			HRESULT ( STDMETHODCALLTYPE *DeleteVarDesc )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index);

			HRESULT ( STDMETHODCALLTYPE *DeleteVarDescByMemId )( 
				ICreateTypeInfo2 * This,
				/* [in] */ MEMBERID memid);

			HRESULT ( STDMETHODCALLTYPE *DeleteImplType )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index);

			HRESULT ( STDMETHODCALLTYPE *SetCustData )( 
				ICreateTypeInfo2 * This,
				/* [in] */ REFGUID guid,
				/* [in] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *SetFuncCustData )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ REFGUID guid,
				/* [in] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *SetParamCustData )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT indexFunc,
				/* [in] */ UINT indexParam,
				/* [in] */ REFGUID guid,
				/* [in] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *SetVarCustData )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ REFGUID guid,
				/* [in] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *SetImplTypeCustData )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ REFGUID guid,
				/* [in] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *SetHelpStringContext )( 
				ICreateTypeInfo2 * This,
				/* [in] */ ULONG dwHelpStringContext);

			HRESULT ( STDMETHODCALLTYPE *SetFuncHelpStringContext )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ ULONG dwHelpStringContext);

			HRESULT ( STDMETHODCALLTYPE *SetVarHelpStringContext )( 
				ICreateTypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ ULONG dwHelpStringContext);

			HRESULT ( STDMETHODCALLTYPE *Invalidate )( 
				ICreateTypeInfo2 * This);

			HRESULT ( STDMETHODCALLTYPE *SetName )( 
				ICreateTypeInfo2 * This,
				/* [in] */ LPOLESTR szName);

		END_INTERFACE
	} ICreateTypeInfo2Vtbl;

	interface ICreateTypeInfo2
	{
		CONST_VTBL struct ICreateTypeInfo2Vtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ICreateTypeInfo2_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ICreateTypeInfo2_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ICreateTypeInfo2_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ICreateTypeInfo2_SetGuid(This,guid)	\
	(This)->lpVtbl -> SetGuid(This,guid)

#define ICreateTypeInfo2_SetTypeFlags(This,uTypeFlags)	\
	(This)->lpVtbl -> SetTypeFlags(This,uTypeFlags)

#define ICreateTypeInfo2_SetDocString(This,pStrDoc)	\
	(This)->lpVtbl -> SetDocString(This,pStrDoc)

#define ICreateTypeInfo2_SetHelpContext(This,dwHelpContext)	\
	(This)->lpVtbl -> SetHelpContext(This,dwHelpContext)

#define ICreateTypeInfo2_SetVersion(This,wMajorVerNum,wMinorVerNum)	\
	(This)->lpVtbl -> SetVersion(This,wMajorVerNum,wMinorVerNum)

#define ICreateTypeInfo2_AddRefTypeInfo(This,pTInfo,phRefType)	\
	(This)->lpVtbl -> AddRefTypeInfo(This,pTInfo,phRefType)

#define ICreateTypeInfo2_AddFuncDesc(This,index,pFuncDesc)	\
	(This)->lpVtbl -> AddFuncDesc(This,index,pFuncDesc)

#define ICreateTypeInfo2_AddImplType(This,index,hRefType)	\
	(This)->lpVtbl -> AddImplType(This,index,hRefType)

#define ICreateTypeInfo2_SetImplTypeFlags(This,index,implTypeFlags)	\
	(This)->lpVtbl -> SetImplTypeFlags(This,index,implTypeFlags)

#define ICreateTypeInfo2_SetAlignment(This,cbAlignment)	\
	(This)->lpVtbl -> SetAlignment(This,cbAlignment)

#define ICreateTypeInfo2_SetSchema(This,pStrSchema)	\
	(This)->lpVtbl -> SetSchema(This,pStrSchema)

#define ICreateTypeInfo2_AddVarDesc(This,index,pVarDesc)	\
	(This)->lpVtbl -> AddVarDesc(This,index,pVarDesc)

#define ICreateTypeInfo2_SetFuncAndParamNames(This,index,rgszNames,cNames)	\
	(This)->lpVtbl -> SetFuncAndParamNames(This,index,rgszNames,cNames)

#define ICreateTypeInfo2_SetVarName(This,index,szName)	\
	(This)->lpVtbl -> SetVarName(This,index,szName)

#define ICreateTypeInfo2_SetTypeDescAlias(This,pTDescAlias)	\
	(This)->lpVtbl -> SetTypeDescAlias(This,pTDescAlias)

#define ICreateTypeInfo2_DefineFuncAsDllEntry(This,index,szDllName,szProcName)	\
	(This)->lpVtbl -> DefineFuncAsDllEntry(This,index,szDllName,szProcName)

#define ICreateTypeInfo2_SetFuncDocString(This,index,szDocString)	\
	(This)->lpVtbl -> SetFuncDocString(This,index,szDocString)

#define ICreateTypeInfo2_SetVarDocString(This,index,szDocString)	\
	(This)->lpVtbl -> SetVarDocString(This,index,szDocString)

#define ICreateTypeInfo2_SetFuncHelpContext(This,index,dwHelpContext)	\
	(This)->lpVtbl -> SetFuncHelpContext(This,index,dwHelpContext)

#define ICreateTypeInfo2_SetVarHelpContext(This,index,dwHelpContext)	\
	(This)->lpVtbl -> SetVarHelpContext(This,index,dwHelpContext)

#define ICreateTypeInfo2_SetMops(This,index,bstrMops)	\
	(This)->lpVtbl -> SetMops(This,index,bstrMops)

#define ICreateTypeInfo2_SetTypeIdldesc(This,pIdlDesc)	\
	(This)->lpVtbl -> SetTypeIdldesc(This,pIdlDesc)

#define ICreateTypeInfo2_LayOut(This)	\
	(This)->lpVtbl -> LayOut(This)


#define ICreateTypeInfo2_DeleteFuncDesc(This,index)	\
	(This)->lpVtbl -> DeleteFuncDesc(This,index)

#define ICreateTypeInfo2_DeleteFuncDescByMemId(This,memid,invKind)	\
	(This)->lpVtbl -> DeleteFuncDescByMemId(This,memid,invKind)

#define ICreateTypeInfo2_DeleteVarDesc(This,index)	\
	(This)->lpVtbl -> DeleteVarDesc(This,index)

#define ICreateTypeInfo2_DeleteVarDescByMemId(This,memid)	\
	(This)->lpVtbl -> DeleteVarDescByMemId(This,memid)

#define ICreateTypeInfo2_DeleteImplType(This,index)	\
	(This)->lpVtbl -> DeleteImplType(This,index)

#define ICreateTypeInfo2_SetCustData(This,guid,pVarVal)	\
	(This)->lpVtbl -> SetCustData(This,guid,pVarVal)

#define ICreateTypeInfo2_SetFuncCustData(This,index,guid,pVarVal)	\
	(This)->lpVtbl -> SetFuncCustData(This,index,guid,pVarVal)

#define ICreateTypeInfo2_SetParamCustData(This,indexFunc,indexParam,guid,pVarVal)	\
	(This)->lpVtbl -> SetParamCustData(This,indexFunc,indexParam,guid,pVarVal)

#define ICreateTypeInfo2_SetVarCustData(This,index,guid,pVarVal)	\
	(This)->lpVtbl -> SetVarCustData(This,index,guid,pVarVal)

#define ICreateTypeInfo2_SetImplTypeCustData(This,index,guid,pVarVal)	\
	(This)->lpVtbl -> SetImplTypeCustData(This,index,guid,pVarVal)

#define ICreateTypeInfo2_SetHelpStringContext(This,dwHelpStringContext)	\
	(This)->lpVtbl -> SetHelpStringContext(This,dwHelpStringContext)

#define ICreateTypeInfo2_SetFuncHelpStringContext(This,index,dwHelpStringContext)	\
	(This)->lpVtbl -> SetFuncHelpStringContext(This,index,dwHelpStringContext)

#define ICreateTypeInfo2_SetVarHelpStringContext(This,index,dwHelpStringContext)	\
	(This)->lpVtbl -> SetVarHelpStringContext(This,index,dwHelpStringContext)

#define ICreateTypeInfo2_Invalidate(This)	\
	(This)->lpVtbl -> Invalidate(This)

#define ICreateTypeInfo2_SetName(This,szName)	\
	(This)->lpVtbl -> SetName(This,szName)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_DeleteFuncDesc_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index);


	void __RPC_STUB ICreateTypeInfo2_DeleteFuncDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_DeleteFuncDescByMemId_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ INVOKEKIND invKind);


	void __RPC_STUB ICreateTypeInfo2_DeleteFuncDescByMemId_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_DeleteVarDesc_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index);


	void __RPC_STUB ICreateTypeInfo2_DeleteVarDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_DeleteVarDescByMemId_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ MEMBERID memid);


	void __RPC_STUB ICreateTypeInfo2_DeleteVarDescByMemId_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_DeleteImplType_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index);


	void __RPC_STUB ICreateTypeInfo2_DeleteImplType_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetCustData_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ REFGUID guid,
		/* [in] */ VARIANT *pVarVal);


	void __RPC_STUB ICreateTypeInfo2_SetCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetFuncCustData_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ REFGUID guid,
		/* [in] */ VARIANT *pVarVal);


	void __RPC_STUB ICreateTypeInfo2_SetFuncCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetParamCustData_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT indexFunc,
		/* [in] */ UINT indexParam,
		/* [in] */ REFGUID guid,
		/* [in] */ VARIANT *pVarVal);


	void __RPC_STUB ICreateTypeInfo2_SetParamCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetVarCustData_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ REFGUID guid,
		/* [in] */ VARIANT *pVarVal);


	void __RPC_STUB ICreateTypeInfo2_SetVarCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetImplTypeCustData_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ REFGUID guid,
		/* [in] */ VARIANT *pVarVal);


	void __RPC_STUB ICreateTypeInfo2_SetImplTypeCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetHelpStringContext_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ ULONG dwHelpStringContext);


	void __RPC_STUB ICreateTypeInfo2_SetHelpStringContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetFuncHelpStringContext_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ ULONG dwHelpStringContext);


	void __RPC_STUB ICreateTypeInfo2_SetFuncHelpStringContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetVarHelpStringContext_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ ULONG dwHelpStringContext);


	void __RPC_STUB ICreateTypeInfo2_SetVarHelpStringContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_Invalidate_Proxy( 
		ICreateTypeInfo2 * This);


	void __RPC_STUB ICreateTypeInfo2_Invalidate_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeInfo2_SetName_Proxy( 
		ICreateTypeInfo2 * This,
		/* [in] */ LPOLESTR szName);


	void __RPC_STUB ICreateTypeInfo2_SetName_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ICreateTypeInfo2_INTERFACE_DEFINED__ */


#ifndef __ICreateTypeLib_INTERFACE_DEFINED__
#define __ICreateTypeLib_INTERFACE_DEFINED__

	/* interface ICreateTypeLib */
	/* [local][unique][uuid][object] */ 

	typedef /* [unique] */ ICreateTypeLib *LPCREATETYPELIB;


	EXTERN_C const IID IID_ICreateTypeLib;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020406-0000-0000-C000-000000000046")
ICreateTypeLib : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE CreateTypeInfo( 
			/* [in] */ LPOLESTR szName,
			/* [in] */ TYPEKIND tkind,
			/* [out] */ ICreateTypeInfo **ppCTInfo) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetName( 
			/* [in] */ LPOLESTR szName) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetVersion( 
			/* [in] */ WORD wMajorVerNum,
			/* [in] */ WORD wMinorVerNum) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetGuid( 
			/* [in] */ REFGUID guid) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetDocString( 
			/* [in] */ LPOLESTR szDoc) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpFileName( 
			/* [in] */ LPOLESTR szHelpFileName) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpContext( 
			/* [in] */ DWORD dwHelpContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetLcid( 
			/* [in] */ LCID lcid) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetLibFlags( 
			/* [in] */ UINT uLibFlags) = 0;

		virtual HRESULT STDMETHODCALLTYPE SaveAllChanges( void) = 0;

	};

#else 	/* C style interface */

	typedef struct ICreateTypeLibVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ICreateTypeLib * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ICreateTypeLib * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ICreateTypeLib * This);

			HRESULT ( STDMETHODCALLTYPE *CreateTypeInfo )( 
				ICreateTypeLib * This,
				/* [in] */ LPOLESTR szName,
				/* [in] */ TYPEKIND tkind,
				/* [out] */ ICreateTypeInfo **ppCTInfo);

			HRESULT ( STDMETHODCALLTYPE *SetName )( 
				ICreateTypeLib * This,
				/* [in] */ LPOLESTR szName);

			HRESULT ( STDMETHODCALLTYPE *SetVersion )( 
				ICreateTypeLib * This,
				/* [in] */ WORD wMajorVerNum,
				/* [in] */ WORD wMinorVerNum);

			HRESULT ( STDMETHODCALLTYPE *SetGuid )( 
				ICreateTypeLib * This,
				/* [in] */ REFGUID guid);

			HRESULT ( STDMETHODCALLTYPE *SetDocString )( 
				ICreateTypeLib * This,
				/* [in] */ LPOLESTR szDoc);

			HRESULT ( STDMETHODCALLTYPE *SetHelpFileName )( 
				ICreateTypeLib * This,
				/* [in] */ LPOLESTR szHelpFileName);

			HRESULT ( STDMETHODCALLTYPE *SetHelpContext )( 
				ICreateTypeLib * This,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetLcid )( 
				ICreateTypeLib * This,
				/* [in] */ LCID lcid);

			HRESULT ( STDMETHODCALLTYPE *SetLibFlags )( 
				ICreateTypeLib * This,
				/* [in] */ UINT uLibFlags);

			HRESULT ( STDMETHODCALLTYPE *SaveAllChanges )( 
				ICreateTypeLib * This);

		END_INTERFACE
	} ICreateTypeLibVtbl;

	interface ICreateTypeLib
	{
		CONST_VTBL struct ICreateTypeLibVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ICreateTypeLib_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ICreateTypeLib_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ICreateTypeLib_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ICreateTypeLib_CreateTypeInfo(This,szName,tkind,ppCTInfo)	\
	(This)->lpVtbl -> CreateTypeInfo(This,szName,tkind,ppCTInfo)

#define ICreateTypeLib_SetName(This,szName)	\
	(This)->lpVtbl -> SetName(This,szName)

#define ICreateTypeLib_SetVersion(This,wMajorVerNum,wMinorVerNum)	\
	(This)->lpVtbl -> SetVersion(This,wMajorVerNum,wMinorVerNum)

#define ICreateTypeLib_SetGuid(This,guid)	\
	(This)->lpVtbl -> SetGuid(This,guid)

#define ICreateTypeLib_SetDocString(This,szDoc)	\
	(This)->lpVtbl -> SetDocString(This,szDoc)

#define ICreateTypeLib_SetHelpFileName(This,szHelpFileName)	\
	(This)->lpVtbl -> SetHelpFileName(This,szHelpFileName)

#define ICreateTypeLib_SetHelpContext(This,dwHelpContext)	\
	(This)->lpVtbl -> SetHelpContext(This,dwHelpContext)

#define ICreateTypeLib_SetLcid(This,lcid)	\
	(This)->lpVtbl -> SetLcid(This,lcid)

#define ICreateTypeLib_SetLibFlags(This,uLibFlags)	\
	(This)->lpVtbl -> SetLibFlags(This,uLibFlags)

#define ICreateTypeLib_SaveAllChanges(This)	\
	(This)->lpVtbl -> SaveAllChanges(This)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ICreateTypeLib_CreateTypeInfo_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ LPOLESTR szName,
		/* [in] */ TYPEKIND tkind,
		/* [out] */ ICreateTypeInfo **ppCTInfo);


	void __RPC_STUB ICreateTypeLib_CreateTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetName_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ LPOLESTR szName);


	void __RPC_STUB ICreateTypeLib_SetName_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetVersion_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ WORD wMajorVerNum,
		/* [in] */ WORD wMinorVerNum);


	void __RPC_STUB ICreateTypeLib_SetVersion_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetGuid_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ REFGUID guid);


	void __RPC_STUB ICreateTypeLib_SetGuid_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetDocString_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ LPOLESTR szDoc);


	void __RPC_STUB ICreateTypeLib_SetDocString_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetHelpFileName_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ LPOLESTR szHelpFileName);


	void __RPC_STUB ICreateTypeLib_SetHelpFileName_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetHelpContext_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ DWORD dwHelpContext);


	void __RPC_STUB ICreateTypeLib_SetHelpContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetLcid_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ LCID lcid);


	void __RPC_STUB ICreateTypeLib_SetLcid_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SetLibFlags_Proxy( 
		ICreateTypeLib * This,
		/* [in] */ UINT uLibFlags);


	void __RPC_STUB ICreateTypeLib_SetLibFlags_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib_SaveAllChanges_Proxy( 
		ICreateTypeLib * This);


	void __RPC_STUB ICreateTypeLib_SaveAllChanges_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ICreateTypeLib_INTERFACE_DEFINED__ */


#ifndef __ICreateTypeLib2_INTERFACE_DEFINED__
#define __ICreateTypeLib2_INTERFACE_DEFINED__

	/* interface ICreateTypeLib2 */
	/* [local][unique][uuid][object] */ 

	typedef /* [unique] */ ICreateTypeLib2 *LPCREATETYPELIB2;


	EXTERN_C const IID IID_ICreateTypeLib2;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("0002040F-0000-0000-C000-000000000046")
ICreateTypeLib2 : public ICreateTypeLib
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE DeleteTypeInfo( 
			/* [in] */ LPOLESTR szName) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetCustData( 
			/* [in] */ REFGUID guid,
			/* [in] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpStringContext( 
			/* [in] */ ULONG dwHelpStringContext) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpStringDll( 
			/* [in] */ LPOLESTR szFileName) = 0;

	};

#else 	/* C style interface */

	typedef struct ICreateTypeLib2Vtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ICreateTypeLib2 * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ICreateTypeLib2 * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ICreateTypeLib2 * This);

			HRESULT ( STDMETHODCALLTYPE *CreateTypeInfo )( 
				ICreateTypeLib2 * This,
				/* [in] */ LPOLESTR szName,
				/* [in] */ TYPEKIND tkind,
				/* [out] */ ICreateTypeInfo **ppCTInfo);

			HRESULT ( STDMETHODCALLTYPE *SetName )( 
				ICreateTypeLib2 * This,
				/* [in] */ LPOLESTR szName);

			HRESULT ( STDMETHODCALLTYPE *SetVersion )( 
				ICreateTypeLib2 * This,
				/* [in] */ WORD wMajorVerNum,
				/* [in] */ WORD wMinorVerNum);

			HRESULT ( STDMETHODCALLTYPE *SetGuid )( 
				ICreateTypeLib2 * This,
				/* [in] */ REFGUID guid);

			HRESULT ( STDMETHODCALLTYPE *SetDocString )( 
				ICreateTypeLib2 * This,
				/* [in] */ LPOLESTR szDoc);

			HRESULT ( STDMETHODCALLTYPE *SetHelpFileName )( 
				ICreateTypeLib2 * This,
				/* [in] */ LPOLESTR szHelpFileName);

			HRESULT ( STDMETHODCALLTYPE *SetHelpContext )( 
				ICreateTypeLib2 * This,
				/* [in] */ DWORD dwHelpContext);

			HRESULT ( STDMETHODCALLTYPE *SetLcid )( 
				ICreateTypeLib2 * This,
				/* [in] */ LCID lcid);

			HRESULT ( STDMETHODCALLTYPE *SetLibFlags )( 
				ICreateTypeLib2 * This,
				/* [in] */ UINT uLibFlags);

			HRESULT ( STDMETHODCALLTYPE *SaveAllChanges )( 
				ICreateTypeLib2 * This);

			HRESULT ( STDMETHODCALLTYPE *DeleteTypeInfo )( 
				ICreateTypeLib2 * This,
				/* [in] */ LPOLESTR szName);

			HRESULT ( STDMETHODCALLTYPE *SetCustData )( 
				ICreateTypeLib2 * This,
				/* [in] */ REFGUID guid,
				/* [in] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *SetHelpStringContext )( 
				ICreateTypeLib2 * This,
				/* [in] */ ULONG dwHelpStringContext);

			HRESULT ( STDMETHODCALLTYPE *SetHelpStringDll )( 
				ICreateTypeLib2 * This,
				/* [in] */ LPOLESTR szFileName);

		END_INTERFACE
	} ICreateTypeLib2Vtbl;

	interface ICreateTypeLib2
	{
		CONST_VTBL struct ICreateTypeLib2Vtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ICreateTypeLib2_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ICreateTypeLib2_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ICreateTypeLib2_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ICreateTypeLib2_CreateTypeInfo(This,szName,tkind,ppCTInfo)	\
	(This)->lpVtbl -> CreateTypeInfo(This,szName,tkind,ppCTInfo)

#define ICreateTypeLib2_SetName(This,szName)	\
	(This)->lpVtbl -> SetName(This,szName)

#define ICreateTypeLib2_SetVersion(This,wMajorVerNum,wMinorVerNum)	\
	(This)->lpVtbl -> SetVersion(This,wMajorVerNum,wMinorVerNum)

#define ICreateTypeLib2_SetGuid(This,guid)	\
	(This)->lpVtbl -> SetGuid(This,guid)

#define ICreateTypeLib2_SetDocString(This,szDoc)	\
	(This)->lpVtbl -> SetDocString(This,szDoc)

#define ICreateTypeLib2_SetHelpFileName(This,szHelpFileName)	\
	(This)->lpVtbl -> SetHelpFileName(This,szHelpFileName)

#define ICreateTypeLib2_SetHelpContext(This,dwHelpContext)	\
	(This)->lpVtbl -> SetHelpContext(This,dwHelpContext)

#define ICreateTypeLib2_SetLcid(This,lcid)	\
	(This)->lpVtbl -> SetLcid(This,lcid)

#define ICreateTypeLib2_SetLibFlags(This,uLibFlags)	\
	(This)->lpVtbl -> SetLibFlags(This,uLibFlags)

#define ICreateTypeLib2_SaveAllChanges(This)	\
	(This)->lpVtbl -> SaveAllChanges(This)


#define ICreateTypeLib2_DeleteTypeInfo(This,szName)	\
	(This)->lpVtbl -> DeleteTypeInfo(This,szName)

#define ICreateTypeLib2_SetCustData(This,guid,pVarVal)	\
	(This)->lpVtbl -> SetCustData(This,guid,pVarVal)

#define ICreateTypeLib2_SetHelpStringContext(This,dwHelpStringContext)	\
	(This)->lpVtbl -> SetHelpStringContext(This,dwHelpStringContext)

#define ICreateTypeLib2_SetHelpStringDll(This,szFileName)	\
	(This)->lpVtbl -> SetHelpStringDll(This,szFileName)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ICreateTypeLib2_DeleteTypeInfo_Proxy( 
		ICreateTypeLib2 * This,
		/* [in] */ LPOLESTR szName);


	void __RPC_STUB ICreateTypeLib2_DeleteTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib2_SetCustData_Proxy( 
		ICreateTypeLib2 * This,
		/* [in] */ REFGUID guid,
		/* [in] */ VARIANT *pVarVal);


	void __RPC_STUB ICreateTypeLib2_SetCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib2_SetHelpStringContext_Proxy( 
		ICreateTypeLib2 * This,
		/* [in] */ ULONG dwHelpStringContext);


	void __RPC_STUB ICreateTypeLib2_SetHelpStringContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateTypeLib2_SetHelpStringDll_Proxy( 
		ICreateTypeLib2 * This,
		/* [in] */ LPOLESTR szFileName);


	void __RPC_STUB ICreateTypeLib2_SetHelpStringDll_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ICreateTypeLib2_INTERFACE_DEFINED__ */


#ifndef __IDispatch_INTERFACE_DEFINED__
#define __IDispatch_INTERFACE_DEFINED__

	/* interface IDispatch */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ IDispatch *LPDISPATCH;

	/* DISPID reserved to indicate an "unknown" name */
	/* only reserved for data members (properties); reused as a method dispid below */
#define	DISPID_UNKNOWN	( -1 )

	/* DISPID reserved for the "value" property */
#define	DISPID_VALUE	( 0 )

	/* The following DISPID is reserved to indicate the param
	* that is the right-hand-side (or "put" value) of a PropertyPut
	*/
#define	DISPID_PROPERTYPUT	( -3 )

	/* DISPID reserved for the standard "NewEnum" method */
#define	DISPID_NEWENUM	( -4 )

	/* DISPID reserved for the standard "Evaluate" method */
#define	DISPID_EVALUATE	( -5 )

#define	DISPID_CONSTRUCTOR	( -6 )

#define	DISPID_DESTRUCTOR	( -7 )

#define	DISPID_COLLECT	( -8 )

	/* The range -500 through -999 is reserved for Controls */
	/* The range 0x80010000 through 0x8001FFFF is reserved for Controls */
	/* The range -5000 through -5499 is reserved for ActiveX Accessability */
	/* The range -2000 through -2499 is reserved for VB5 */
	/* The range -3900 through -3999 is reserved for Forms */
	/* The range -5500 through -5550 is reserved for Forms */
	/* The remainder of the negative DISPIDs are reserved for future use */

	EXTERN_C const IID IID_IDispatch;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020400-0000-0000-C000-000000000046")
IDispatch : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE GetTypeInfoCount( 
			/* [out] */ UINT *pctinfo) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeInfo( 
			/* [in] */ UINT iTInfo,
			/* [in] */ LCID lcid,
			/* [out] */ ITypeInfo **ppTInfo) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetIDsOfNames( 
			/* [in] */ REFIID riid,
			/* [size_is][in] */ LPOLESTR *rgszNames,
			/* [in] */ UINT cNames,
			/* [in] */ LCID lcid,
			/* [size_is][out] */ DISPID *rgDispId) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE Invoke( 
			/* [in] */ DISPID dispIdMember,
			/* [in] */ REFIID riid,
			/* [in] */ LCID lcid,
			/* [in] */ WORD wFlags,
			/* [out][in] */ DISPPARAMS *pDispParams,
			/* [out] */ VARIANT *pVarResult,
			/* [out] */ EXCEPINFO *pExcepInfo,
			/* [out] */ UINT *puArgErr) = 0;

	};

#else 	/* C style interface */

	typedef struct IDispatchVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			IDispatch * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				IDispatch * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				IDispatch * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				IDispatch * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				IDispatch * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				IDispatch * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				IDispatch * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

		END_INTERFACE
	} IDispatchVtbl;

	interface IDispatch
	{
		CONST_VTBL struct IDispatchVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define IDispatch_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IDispatch_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define IDispatch_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define IDispatch_GetTypeInfoCount(This,pctinfo)	\
	(This)->lpVtbl -> GetTypeInfoCount(This,pctinfo)

#define IDispatch_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	(This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo)

#define IDispatch_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	(This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)

#define IDispatch_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	(This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE IDispatch_GetTypeInfoCount_Proxy( 
		IDispatch * This,
		/* [out] */ UINT *pctinfo);


	void __RPC_STUB IDispatch_GetTypeInfoCount_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IDispatch_GetTypeInfo_Proxy( 
		IDispatch * This,
		/* [in] */ UINT iTInfo,
		/* [in] */ LCID lcid,
		/* [out] */ ITypeInfo **ppTInfo);


	void __RPC_STUB IDispatch_GetTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IDispatch_GetIDsOfNames_Proxy( 
		IDispatch * This,
		/* [in] */ REFIID riid,
		/* [size_is][in] */ LPOLESTR *rgszNames,
		/* [in] */ UINT cNames,
		/* [in] */ LCID lcid,
		/* [size_is][out] */ DISPID *rgDispId);


	void __RPC_STUB IDispatch_GetIDsOfNames_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE IDispatch_RemoteInvoke_Proxy( 
		IDispatch * This,
		/* [in] */ DISPID dispIdMember,
		/* [in] */ REFIID riid,
		/* [in] */ LCID lcid,
		/* [in] */ DWORD dwFlags,
		/* [in] */ DISPPARAMS *pDispParams,
		/* [out] */ VARIANT *pVarResult,
		/* [out] */ EXCEPINFO *pExcepInfo,
		/* [out] */ UINT *pArgErr,
		/* [in] */ UINT cVarRef,
		/* [size_is][in] */ UINT *rgVarRefIdx,
		/* [size_is][out][in] */ VARIANTARG *rgVarRef);


	void __RPC_STUB IDispatch_RemoteInvoke_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __IDispatch_INTERFACE_DEFINED__ */


#ifndef __IEnumVARIANT_INTERFACE_DEFINED__
#define __IEnumVARIANT_INTERFACE_DEFINED__

	/* interface IEnumVARIANT */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ IEnumVARIANT *LPENUMVARIANT;


	EXTERN_C const IID IID_IEnumVARIANT;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020404-0000-0000-C000-000000000046")
IEnumVARIANT : public IUnknown
	{
	public:
		virtual /* [local] */ HRESULT STDMETHODCALLTYPE Next( 
			/* [in] */ ULONG celt,
			/* [length_is][size_is][out] */ VARIANT *rgVar,
			/* [out] */ ULONG *pCeltFetched) = 0;

		virtual HRESULT STDMETHODCALLTYPE Skip( 
			/* [in] */ ULONG celt) = 0;

		virtual HRESULT STDMETHODCALLTYPE Reset( void) = 0;

		virtual HRESULT STDMETHODCALLTYPE Clone( 
			/* [out] */ IEnumVARIANT **ppEnum) = 0;

	};

#else 	/* C style interface */

	typedef struct IEnumVARIANTVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			IEnumVARIANT * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				IEnumVARIANT * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				IEnumVARIANT * This);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Next )( 
				IEnumVARIANT * This,
				/* [in] */ ULONG celt,
				/* [length_is][size_is][out] */ VARIANT *rgVar,
				/* [out] */ ULONG *pCeltFetched);

			HRESULT ( STDMETHODCALLTYPE *Skip )( 
				IEnumVARIANT * This,
				/* [in] */ ULONG celt);

			HRESULT ( STDMETHODCALLTYPE *Reset )( 
				IEnumVARIANT * This);

			HRESULT ( STDMETHODCALLTYPE *Clone )( 
				IEnumVARIANT * This,
				/* [out] */ IEnumVARIANT **ppEnum);

		END_INTERFACE
	} IEnumVARIANTVtbl;

	interface IEnumVARIANT
	{
		CONST_VTBL struct IEnumVARIANTVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define IEnumVARIANT_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IEnumVARIANT_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define IEnumVARIANT_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define IEnumVARIANT_Next(This,celt,rgVar,pCeltFetched)	\
	(This)->lpVtbl -> Next(This,celt,rgVar,pCeltFetched)

#define IEnumVARIANT_Skip(This,celt)	\
	(This)->lpVtbl -> Skip(This,celt)

#define IEnumVARIANT_Reset(This)	\
	(This)->lpVtbl -> Reset(This)

#define IEnumVARIANT_Clone(This,ppEnum)	\
	(This)->lpVtbl -> Clone(This,ppEnum)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	/* [call_as] */ HRESULT STDMETHODCALLTYPE IEnumVARIANT_RemoteNext_Proxy( 
		IEnumVARIANT * This,
		/* [in] */ ULONG celt,
		/* [length_is][size_is][out] */ VARIANT *rgVar,
		/* [out] */ ULONG *pCeltFetched);


	void __RPC_STUB IEnumVARIANT_RemoteNext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IEnumVARIANT_Skip_Proxy( 
		IEnumVARIANT * This,
		/* [in] */ ULONG celt);


	void __RPC_STUB IEnumVARIANT_Skip_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IEnumVARIANT_Reset_Proxy( 
		IEnumVARIANT * This);


	void __RPC_STUB IEnumVARIANT_Reset_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IEnumVARIANT_Clone_Proxy( 
		IEnumVARIANT * This,
		/* [out] */ IEnumVARIANT **ppEnum);


	void __RPC_STUB IEnumVARIANT_Clone_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __IEnumVARIANT_INTERFACE_DEFINED__ */


#ifndef __ITypeComp_INTERFACE_DEFINED__
#define __ITypeComp_INTERFACE_DEFINED__

	/* interface ITypeComp */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ ITypeComp *LPTYPECOMP;

	typedef /* [v1_enum] */ 
		enum tagDESCKIND
	{	DESCKIND_NONE	= 0,
	DESCKIND_FUNCDESC	= DESCKIND_NONE + 1,
	DESCKIND_VARDESC	= DESCKIND_FUNCDESC + 1,
	DESCKIND_TYPECOMP	= DESCKIND_VARDESC + 1,
	DESCKIND_IMPLICITAPPOBJ	= DESCKIND_TYPECOMP + 1,
	DESCKIND_MAX	= DESCKIND_IMPLICITAPPOBJ + 1
	} 	DESCKIND;

	typedef union tagBINDPTR
	{
		FUNCDESC *lpfuncdesc;
		VARDESC *lpvardesc;
		ITypeComp *lptcomp;
	} 	BINDPTR;

	typedef union tagBINDPTR *LPBINDPTR;


	EXTERN_C const IID IID_ITypeComp;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020403-0000-0000-C000-000000000046")
ITypeComp : public IUnknown
	{
	public:
		virtual /* [local] */ HRESULT STDMETHODCALLTYPE Bind( 
			/* [in] */ LPOLESTR szName,
			/* [in] */ ULONG lHashVal,
			/* [in] */ WORD wFlags,
			/* [out] */ ITypeInfo **ppTInfo,
			/* [out] */ DESCKIND *pDescKind,
			/* [out] */ BINDPTR *pBindPtr) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE BindType( 
			/* [in] */ LPOLESTR szName,
			/* [in] */ ULONG lHashVal,
			/* [out] */ ITypeInfo **ppTInfo,
			/* [out] */ ITypeComp **ppTComp) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeCompVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeComp * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeComp * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeComp * This);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Bind )( 
				ITypeComp * This,
				/* [in] */ LPOLESTR szName,
				/* [in] */ ULONG lHashVal,
				/* [in] */ WORD wFlags,
				/* [out] */ ITypeInfo **ppTInfo,
				/* [out] */ DESCKIND *pDescKind,
				/* [out] */ BINDPTR *pBindPtr);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *BindType )( 
				ITypeComp * This,
				/* [in] */ LPOLESTR szName,
				/* [in] */ ULONG lHashVal,
				/* [out] */ ITypeInfo **ppTInfo,
				/* [out] */ ITypeComp **ppTComp);

		END_INTERFACE
	} ITypeCompVtbl;

	interface ITypeComp
	{
		CONST_VTBL struct ITypeCompVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeComp_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeComp_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeComp_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeComp_Bind(This,szName,lHashVal,wFlags,ppTInfo,pDescKind,pBindPtr)	\
	(This)->lpVtbl -> Bind(This,szName,lHashVal,wFlags,ppTInfo,pDescKind,pBindPtr)

#define ITypeComp_BindType(This,szName,lHashVal,ppTInfo,ppTComp)	\
	(This)->lpVtbl -> BindType(This,szName,lHashVal,ppTInfo,ppTComp)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeComp_RemoteBind_Proxy( 
		ITypeComp * This,
		/* [in] */ LPOLESTR szName,
		/* [in] */ ULONG lHashVal,
		/* [in] */ WORD wFlags,
		/* [out] */ ITypeInfo **ppTInfo,
		/* [out] */ DESCKIND *pDescKind,
		/* [out] */ LPFUNCDESC *ppFuncDesc,
		/* [out] */ LPVARDESC *ppVarDesc,
		/* [out] */ ITypeComp **ppTypeComp,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);


	void __RPC_STUB ITypeComp_RemoteBind_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeComp_RemoteBindType_Proxy( 
		ITypeComp * This,
		/* [in] */ LPOLESTR szName,
		/* [in] */ ULONG lHashVal,
		/* [out] */ ITypeInfo **ppTInfo);


	void __RPC_STUB ITypeComp_RemoteBindType_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeComp_INTERFACE_DEFINED__ */


#ifndef __ITypeInfo_INTERFACE_DEFINED__
#define __ITypeInfo_INTERFACE_DEFINED__

	/* interface ITypeInfo */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ ITypeInfo *LPTYPEINFO;


	EXTERN_C const IID IID_ITypeInfo;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020401-0000-0000-C000-000000000046")
ITypeInfo : public IUnknown
	{
	public:
		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetTypeAttr( 
			/* [out] */ TYPEATTR **ppTypeAttr) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeComp( 
			/* [out] */ ITypeComp **ppTComp) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetFuncDesc( 
			/* [in] */ UINT index,
			/* [out] */ FUNCDESC **ppFuncDesc) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetVarDesc( 
			/* [in] */ UINT index,
			/* [out] */ VARDESC **ppVarDesc) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetNames( 
			/* [in] */ MEMBERID memid,
			/* [length_is][size_is][out] */ BSTR *rgBstrNames,
			/* [in] */ UINT cMaxNames,
			/* [out] */ UINT *pcNames) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetRefTypeOfImplType( 
			/* [in] */ UINT index,
			/* [out] */ HREFTYPE *pRefType) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetImplTypeFlags( 
			/* [in] */ UINT index,
			/* [out] */ INT *pImplTypeFlags) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetIDsOfNames( 
			/* [size_is][in] */ LPOLESTR *rgszNames,
			/* [in] */ UINT cNames,
			/* [size_is][out] */ MEMBERID *pMemId) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE Invoke( 
			/* [in] */ PVOID pvInstance,
			/* [in] */ MEMBERID memid,
			/* [in] */ WORD wFlags,
			/* [out][in] */ DISPPARAMS *pDispParams,
			/* [out] */ VARIANT *pVarResult,
			/* [out] */ EXCEPINFO *pExcepInfo,
			/* [out] */ UINT *puArgErr) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetDocumentation( 
			/* [in] */ MEMBERID memid,
			/* [out] */ BSTR *pBstrName,
			/* [out] */ BSTR *pBstrDocString,
			/* [out] */ DWORD *pdwHelpContext,
			/* [out] */ BSTR *pBstrHelpFile) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetDllEntry( 
			/* [in] */ MEMBERID memid,
			/* [in] */ INVOKEKIND invKind,
			/* [out] */ BSTR *pBstrDllName,
			/* [out] */ BSTR *pBstrName,
			/* [out] */ WORD *pwOrdinal) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetRefTypeInfo( 
			/* [in] */ HREFTYPE hRefType,
			/* [out] */ ITypeInfo **ppTInfo) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE AddressOfMember( 
			/* [in] */ MEMBERID memid,
			/* [in] */ INVOKEKIND invKind,
			/* [out] */ PVOID *ppv) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE CreateInstance( 
			/* [in] */ IUnknown *pUnkOuter,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ PVOID *ppvObj) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetMops( 
			/* [in] */ MEMBERID memid,
			/* [out] */ BSTR *pBstrMops) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetContainingTypeLib( 
			/* [out] */ ITypeLib **ppTLib,
			/* [out] */ UINT *pIndex) = 0;

		virtual /* [local] */ void STDMETHODCALLTYPE ReleaseTypeAttr( 
			/* [in] */ TYPEATTR *pTypeAttr) = 0;

		virtual /* [local] */ void STDMETHODCALLTYPE ReleaseFuncDesc( 
			/* [in] */ FUNCDESC *pFuncDesc) = 0;

		virtual /* [local] */ void STDMETHODCALLTYPE ReleaseVarDesc( 
			/* [in] */ VARDESC *pVarDesc) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeInfoVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeInfo * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeInfo * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeInfo * This);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetTypeAttr )( 
				ITypeInfo * This,
				/* [out] */ TYPEATTR **ppTypeAttr);

			HRESULT ( STDMETHODCALLTYPE *GetTypeComp )( 
				ITypeInfo * This,
				/* [out] */ ITypeComp **ppTComp);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetFuncDesc )( 
				ITypeInfo * This,
				/* [in] */ UINT index,
				/* [out] */ FUNCDESC **ppFuncDesc);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetVarDesc )( 
				ITypeInfo * This,
				/* [in] */ UINT index,
				/* [out] */ VARDESC **ppVarDesc);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetNames )( 
				ITypeInfo * This,
				/* [in] */ MEMBERID memid,
				/* [length_is][size_is][out] */ BSTR *rgBstrNames,
				/* [in] */ UINT cMaxNames,
				/* [out] */ UINT *pcNames);

			HRESULT ( STDMETHODCALLTYPE *GetRefTypeOfImplType )( 
				ITypeInfo * This,
				/* [in] */ UINT index,
				/* [out] */ HREFTYPE *pRefType);

			HRESULT ( STDMETHODCALLTYPE *GetImplTypeFlags )( 
				ITypeInfo * This,
				/* [in] */ UINT index,
				/* [out] */ INT *pImplTypeFlags);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				ITypeInfo * This,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [in] */ UINT cNames,
				/* [size_is][out] */ MEMBERID *pMemId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				ITypeInfo * This,
				/* [in] */ PVOID pvInstance,
				/* [in] */ MEMBERID memid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDocumentation )( 
				ITypeInfo * This,
				/* [in] */ MEMBERID memid,
				/* [out] */ BSTR *pBstrName,
				/* [out] */ BSTR *pBstrDocString,
				/* [out] */ DWORD *pdwHelpContext,
				/* [out] */ BSTR *pBstrHelpFile);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDllEntry )( 
				ITypeInfo * This,
				/* [in] */ MEMBERID memid,
				/* [in] */ INVOKEKIND invKind,
				/* [out] */ BSTR *pBstrDllName,
				/* [out] */ BSTR *pBstrName,
				/* [out] */ WORD *pwOrdinal);

			HRESULT ( STDMETHODCALLTYPE *GetRefTypeInfo )( 
				ITypeInfo * This,
				/* [in] */ HREFTYPE hRefType,
				/* [out] */ ITypeInfo **ppTInfo);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *AddressOfMember )( 
				ITypeInfo * This,
				/* [in] */ MEMBERID memid,
				/* [in] */ INVOKEKIND invKind,
				/* [out] */ PVOID *ppv);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *CreateInstance )( 
				ITypeInfo * This,
				/* [in] */ IUnknown *pUnkOuter,
				/* [in] */ REFIID riid,
				/* [iid_is][out] */ PVOID *ppvObj);

			HRESULT ( STDMETHODCALLTYPE *GetMops )( 
				ITypeInfo * This,
				/* [in] */ MEMBERID memid,
				/* [out] */ BSTR *pBstrMops);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetContainingTypeLib )( 
				ITypeInfo * This,
				/* [out] */ ITypeLib **ppTLib,
				/* [out] */ UINT *pIndex);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseTypeAttr )( 
				ITypeInfo * This,
				/* [in] */ TYPEATTR *pTypeAttr);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseFuncDesc )( 
				ITypeInfo * This,
				/* [in] */ FUNCDESC *pFuncDesc);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseVarDesc )( 
				ITypeInfo * This,
				/* [in] */ VARDESC *pVarDesc);

		END_INTERFACE
	} ITypeInfoVtbl;

	interface ITypeInfo
	{
		CONST_VTBL struct ITypeInfoVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeInfo_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeInfo_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeInfo_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeInfo_GetTypeAttr(This,ppTypeAttr)	\
	(This)->lpVtbl -> GetTypeAttr(This,ppTypeAttr)

#define ITypeInfo_GetTypeComp(This,ppTComp)	\
	(This)->lpVtbl -> GetTypeComp(This,ppTComp)

#define ITypeInfo_GetFuncDesc(This,index,ppFuncDesc)	\
	(This)->lpVtbl -> GetFuncDesc(This,index,ppFuncDesc)

#define ITypeInfo_GetVarDesc(This,index,ppVarDesc)	\
	(This)->lpVtbl -> GetVarDesc(This,index,ppVarDesc)

#define ITypeInfo_GetNames(This,memid,rgBstrNames,cMaxNames,pcNames)	\
	(This)->lpVtbl -> GetNames(This,memid,rgBstrNames,cMaxNames,pcNames)

#define ITypeInfo_GetRefTypeOfImplType(This,index,pRefType)	\
	(This)->lpVtbl -> GetRefTypeOfImplType(This,index,pRefType)

#define ITypeInfo_GetImplTypeFlags(This,index,pImplTypeFlags)	\
	(This)->lpVtbl -> GetImplTypeFlags(This,index,pImplTypeFlags)

#define ITypeInfo_GetIDsOfNames(This,rgszNames,cNames,pMemId)	\
	(This)->lpVtbl -> GetIDsOfNames(This,rgszNames,cNames,pMemId)

#define ITypeInfo_Invoke(This,pvInstance,memid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	(This)->lpVtbl -> Invoke(This,pvInstance,memid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)

#define ITypeInfo_GetDocumentation(This,memid,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)	\
	(This)->lpVtbl -> GetDocumentation(This,memid,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)

#define ITypeInfo_GetDllEntry(This,memid,invKind,pBstrDllName,pBstrName,pwOrdinal)	\
	(This)->lpVtbl -> GetDllEntry(This,memid,invKind,pBstrDllName,pBstrName,pwOrdinal)

#define ITypeInfo_GetRefTypeInfo(This,hRefType,ppTInfo)	\
	(This)->lpVtbl -> GetRefTypeInfo(This,hRefType,ppTInfo)

#define ITypeInfo_AddressOfMember(This,memid,invKind,ppv)	\
	(This)->lpVtbl -> AddressOfMember(This,memid,invKind,ppv)

#define ITypeInfo_CreateInstance(This,pUnkOuter,riid,ppvObj)	\
	(This)->lpVtbl -> CreateInstance(This,pUnkOuter,riid,ppvObj)

#define ITypeInfo_GetMops(This,memid,pBstrMops)	\
	(This)->lpVtbl -> GetMops(This,memid,pBstrMops)

#define ITypeInfo_GetContainingTypeLib(This,ppTLib,pIndex)	\
	(This)->lpVtbl -> GetContainingTypeLib(This,ppTLib,pIndex)

#define ITypeInfo_ReleaseTypeAttr(This,pTypeAttr)	\
	(This)->lpVtbl -> ReleaseTypeAttr(This,pTypeAttr)

#define ITypeInfo_ReleaseFuncDesc(This,pFuncDesc)	\
	(This)->lpVtbl -> ReleaseFuncDesc(This,pFuncDesc)

#define ITypeInfo_ReleaseVarDesc(This,pVarDesc)	\
	(This)->lpVtbl -> ReleaseVarDesc(This,pVarDesc)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteGetTypeAttr_Proxy( 
		ITypeInfo * This,
		/* [out] */ LPTYPEATTR *ppTypeAttr,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);


	void __RPC_STUB ITypeInfo_RemoteGetTypeAttr_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo_GetTypeComp_Proxy( 
		ITypeInfo * This,
		/* [out] */ ITypeComp **ppTComp);


	void __RPC_STUB ITypeInfo_GetTypeComp_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteGetFuncDesc_Proxy( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ LPFUNCDESC *ppFuncDesc,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);


	void __RPC_STUB ITypeInfo_RemoteGetFuncDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteGetVarDesc_Proxy( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ LPVARDESC *ppVarDesc,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);


	void __RPC_STUB ITypeInfo_RemoteGetVarDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteGetNames_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [length_is][size_is][out] */ BSTR *rgBstrNames,
		/* [in] */ UINT cMaxNames,
		/* [out] */ UINT *pcNames);


	void __RPC_STUB ITypeInfo_RemoteGetNames_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo_GetRefTypeOfImplType_Proxy( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ HREFTYPE *pRefType);


	void __RPC_STUB ITypeInfo_GetRefTypeOfImplType_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo_GetImplTypeFlags_Proxy( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ INT *pImplTypeFlags);


	void __RPC_STUB ITypeInfo_GetImplTypeFlags_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_LocalGetIDsOfNames_Proxy( 
		ITypeInfo * This);


	void __RPC_STUB ITypeInfo_LocalGetIDsOfNames_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_LocalInvoke_Proxy( 
		ITypeInfo * This);


	void __RPC_STUB ITypeInfo_LocalInvoke_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteGetDocumentation_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ BSTR *pBstrDocString,
		/* [out] */ DWORD *pdwHelpContext,
		/* [out] */ BSTR *pBstrHelpFile);


	void __RPC_STUB ITypeInfo_RemoteGetDocumentation_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteGetDllEntry_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ INVOKEKIND invKind,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pBstrDllName,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ WORD *pwOrdinal);


	void __RPC_STUB ITypeInfo_RemoteGetDllEntry_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo_GetRefTypeInfo_Proxy( 
		ITypeInfo * This,
		/* [in] */ HREFTYPE hRefType,
		/* [out] */ ITypeInfo **ppTInfo);


	void __RPC_STUB ITypeInfo_GetRefTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_LocalAddressOfMember_Proxy( 
		ITypeInfo * This);


	void __RPC_STUB ITypeInfo_LocalAddressOfMember_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteCreateInstance_Proxy( 
		ITypeInfo * This,
		/* [in] */ REFIID riid,
		/* [iid_is][out] */ IUnknown **ppvObj);


	void __RPC_STUB ITypeInfo_RemoteCreateInstance_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo_GetMops_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [out] */ BSTR *pBstrMops);


	void __RPC_STUB ITypeInfo_GetMops_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_RemoteGetContainingTypeLib_Proxy( 
		ITypeInfo * This,
		/* [out] */ ITypeLib **ppTLib,
		/* [out] */ UINT *pIndex);


	void __RPC_STUB ITypeInfo_RemoteGetContainingTypeLib_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_LocalReleaseTypeAttr_Proxy( 
		ITypeInfo * This);


	void __RPC_STUB ITypeInfo_LocalReleaseTypeAttr_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_LocalReleaseFuncDesc_Proxy( 
		ITypeInfo * This);


	void __RPC_STUB ITypeInfo_LocalReleaseFuncDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_LocalReleaseVarDesc_Proxy( 
		ITypeInfo * This);


	void __RPC_STUB ITypeInfo_LocalReleaseVarDesc_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeInfo_INTERFACE_DEFINED__ */


#ifndef __ITypeInfo2_INTERFACE_DEFINED__
#define __ITypeInfo2_INTERFACE_DEFINED__

	/* interface ITypeInfo2 */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ ITypeInfo2 *LPTYPEINFO2;


	EXTERN_C const IID IID_ITypeInfo2;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020412-0000-0000-C000-000000000046")
ITypeInfo2 : public ITypeInfo
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE GetTypeKind( 
			/* [out] */ TYPEKIND *pTypeKind) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeFlags( 
			/* [out] */ ULONG *pTypeFlags) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetFuncIndexOfMemId( 
			/* [in] */ MEMBERID memid,
			/* [in] */ INVOKEKIND invKind,
			/* [out] */ UINT *pFuncIndex) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetVarIndexOfMemId( 
			/* [in] */ MEMBERID memid,
			/* [out] */ UINT *pVarIndex) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetCustData( 
			/* [in] */ REFGUID guid,
			/* [out] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetFuncCustData( 
			/* [in] */ UINT index,
			/* [in] */ REFGUID guid,
			/* [out] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetParamCustData( 
			/* [in] */ UINT indexFunc,
			/* [in] */ UINT indexParam,
			/* [in] */ REFGUID guid,
			/* [out] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetVarCustData( 
			/* [in] */ UINT index,
			/* [in] */ REFGUID guid,
			/* [out] */ VARIANT *pVarVal) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetImplTypeCustData( 
			/* [in] */ UINT index,
			/* [in] */ REFGUID guid,
			/* [out] */ VARIANT *pVarVal) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetDocumentation2( 
			/* [in] */ MEMBERID memid,
			/* [in] */ LCID lcid,
			/* [out] */ BSTR *pbstrHelpString,
			/* [out] */ DWORD *pdwHelpStringContext,
			/* [out] */ BSTR *pbstrHelpStringDll) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetAllCustData( 
			/* [out] */ CUSTDATA *pCustData) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetAllFuncCustData( 
			/* [in] */ UINT index,
			/* [out] */ CUSTDATA *pCustData) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetAllParamCustData( 
			/* [in] */ UINT indexFunc,
			/* [in] */ UINT indexParam,
			/* [out] */ CUSTDATA *pCustData) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetAllVarCustData( 
			/* [in] */ UINT index,
			/* [out] */ CUSTDATA *pCustData) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetAllImplTypeCustData( 
			/* [in] */ UINT index,
			/* [out] */ CUSTDATA *pCustData) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeInfo2Vtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeInfo2 * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeInfo2 * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeInfo2 * This);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetTypeAttr )( 
				ITypeInfo2 * This,
				/* [out] */ TYPEATTR **ppTypeAttr);

			HRESULT ( STDMETHODCALLTYPE *GetTypeComp )( 
				ITypeInfo2 * This,
				/* [out] */ ITypeComp **ppTComp);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetFuncDesc )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [out] */ FUNCDESC **ppFuncDesc);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetVarDesc )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [out] */ VARDESC **ppVarDesc);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetNames )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [length_is][size_is][out] */ BSTR *rgBstrNames,
				/* [in] */ UINT cMaxNames,
				/* [out] */ UINT *pcNames);

			HRESULT ( STDMETHODCALLTYPE *GetRefTypeOfImplType )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [out] */ HREFTYPE *pRefType);

			HRESULT ( STDMETHODCALLTYPE *GetImplTypeFlags )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [out] */ INT *pImplTypeFlags);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				ITypeInfo2 * This,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [in] */ UINT cNames,
				/* [size_is][out] */ MEMBERID *pMemId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				ITypeInfo2 * This,
				/* [in] */ PVOID pvInstance,
				/* [in] */ MEMBERID memid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDocumentation )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [out] */ BSTR *pBstrName,
				/* [out] */ BSTR *pBstrDocString,
				/* [out] */ DWORD *pdwHelpContext,
				/* [out] */ BSTR *pBstrHelpFile);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDllEntry )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [in] */ INVOKEKIND invKind,
				/* [out] */ BSTR *pBstrDllName,
				/* [out] */ BSTR *pBstrName,
				/* [out] */ WORD *pwOrdinal);

			HRESULT ( STDMETHODCALLTYPE *GetRefTypeInfo )( 
				ITypeInfo2 * This,
				/* [in] */ HREFTYPE hRefType,
				/* [out] */ ITypeInfo **ppTInfo);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *AddressOfMember )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [in] */ INVOKEKIND invKind,
				/* [out] */ PVOID *ppv);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *CreateInstance )( 
				ITypeInfo2 * This,
				/* [in] */ IUnknown *pUnkOuter,
				/* [in] */ REFIID riid,
				/* [iid_is][out] */ PVOID *ppvObj);

			HRESULT ( STDMETHODCALLTYPE *GetMops )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [out] */ BSTR *pBstrMops);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetContainingTypeLib )( 
				ITypeInfo2 * This,
				/* [out] */ ITypeLib **ppTLib,
				/* [out] */ UINT *pIndex);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseTypeAttr )( 
				ITypeInfo2 * This,
				/* [in] */ TYPEATTR *pTypeAttr);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseFuncDesc )( 
				ITypeInfo2 * This,
				/* [in] */ FUNCDESC *pFuncDesc);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseVarDesc )( 
				ITypeInfo2 * This,
				/* [in] */ VARDESC *pVarDesc);

			HRESULT ( STDMETHODCALLTYPE *GetTypeKind )( 
				ITypeInfo2 * This,
				/* [out] */ TYPEKIND *pTypeKind);

			HRESULT ( STDMETHODCALLTYPE *GetTypeFlags )( 
				ITypeInfo2 * This,
				/* [out] */ ULONG *pTypeFlags);

			HRESULT ( STDMETHODCALLTYPE *GetFuncIndexOfMemId )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [in] */ INVOKEKIND invKind,
				/* [out] */ UINT *pFuncIndex);

			HRESULT ( STDMETHODCALLTYPE *GetVarIndexOfMemId )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [out] */ UINT *pVarIndex);

			HRESULT ( STDMETHODCALLTYPE *GetCustData )( 
				ITypeInfo2 * This,
				/* [in] */ REFGUID guid,
				/* [out] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *GetFuncCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ REFGUID guid,
				/* [out] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *GetParamCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT indexFunc,
				/* [in] */ UINT indexParam,
				/* [in] */ REFGUID guid,
				/* [out] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *GetVarCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ REFGUID guid,
				/* [out] */ VARIANT *pVarVal);

			HRESULT ( STDMETHODCALLTYPE *GetImplTypeCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [in] */ REFGUID guid,
				/* [out] */ VARIANT *pVarVal);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDocumentation2 )( 
				ITypeInfo2 * This,
				/* [in] */ MEMBERID memid,
				/* [in] */ LCID lcid,
				/* [out] */ BSTR *pbstrHelpString,
				/* [out] */ DWORD *pdwHelpStringContext,
				/* [out] */ BSTR *pbstrHelpStringDll);

			HRESULT ( STDMETHODCALLTYPE *GetAllCustData )( 
				ITypeInfo2 * This,
				/* [out] */ CUSTDATA *pCustData);

			HRESULT ( STDMETHODCALLTYPE *GetAllFuncCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [out] */ CUSTDATA *pCustData);

			HRESULT ( STDMETHODCALLTYPE *GetAllParamCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT indexFunc,
				/* [in] */ UINT indexParam,
				/* [out] */ CUSTDATA *pCustData);

			HRESULT ( STDMETHODCALLTYPE *GetAllVarCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [out] */ CUSTDATA *pCustData);

			HRESULT ( STDMETHODCALLTYPE *GetAllImplTypeCustData )( 
				ITypeInfo2 * This,
				/* [in] */ UINT index,
				/* [out] */ CUSTDATA *pCustData);

		END_INTERFACE
	} ITypeInfo2Vtbl;

	interface ITypeInfo2
	{
		CONST_VTBL struct ITypeInfo2Vtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeInfo2_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeInfo2_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeInfo2_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeInfo2_GetTypeAttr(This,ppTypeAttr)	\
	(This)->lpVtbl -> GetTypeAttr(This,ppTypeAttr)

#define ITypeInfo2_GetTypeComp(This,ppTComp)	\
	(This)->lpVtbl -> GetTypeComp(This,ppTComp)

#define ITypeInfo2_GetFuncDesc(This,index,ppFuncDesc)	\
	(This)->lpVtbl -> GetFuncDesc(This,index,ppFuncDesc)

#define ITypeInfo2_GetVarDesc(This,index,ppVarDesc)	\
	(This)->lpVtbl -> GetVarDesc(This,index,ppVarDesc)

#define ITypeInfo2_GetNames(This,memid,rgBstrNames,cMaxNames,pcNames)	\
	(This)->lpVtbl -> GetNames(This,memid,rgBstrNames,cMaxNames,pcNames)

#define ITypeInfo2_GetRefTypeOfImplType(This,index,pRefType)	\
	(This)->lpVtbl -> GetRefTypeOfImplType(This,index,pRefType)

#define ITypeInfo2_GetImplTypeFlags(This,index,pImplTypeFlags)	\
	(This)->lpVtbl -> GetImplTypeFlags(This,index,pImplTypeFlags)

#define ITypeInfo2_GetIDsOfNames(This,rgszNames,cNames,pMemId)	\
	(This)->lpVtbl -> GetIDsOfNames(This,rgszNames,cNames,pMemId)

#define ITypeInfo2_Invoke(This,pvInstance,memid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	(This)->lpVtbl -> Invoke(This,pvInstance,memid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)

#define ITypeInfo2_GetDocumentation(This,memid,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)	\
	(This)->lpVtbl -> GetDocumentation(This,memid,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)

#define ITypeInfo2_GetDllEntry(This,memid,invKind,pBstrDllName,pBstrName,pwOrdinal)	\
	(This)->lpVtbl -> GetDllEntry(This,memid,invKind,pBstrDllName,pBstrName,pwOrdinal)

#define ITypeInfo2_GetRefTypeInfo(This,hRefType,ppTInfo)	\
	(This)->lpVtbl -> GetRefTypeInfo(This,hRefType,ppTInfo)

#define ITypeInfo2_AddressOfMember(This,memid,invKind,ppv)	\
	(This)->lpVtbl -> AddressOfMember(This,memid,invKind,ppv)

#define ITypeInfo2_CreateInstance(This,pUnkOuter,riid,ppvObj)	\
	(This)->lpVtbl -> CreateInstance(This,pUnkOuter,riid,ppvObj)

#define ITypeInfo2_GetMops(This,memid,pBstrMops)	\
	(This)->lpVtbl -> GetMops(This,memid,pBstrMops)

#define ITypeInfo2_GetContainingTypeLib(This,ppTLib,pIndex)	\
	(This)->lpVtbl -> GetContainingTypeLib(This,ppTLib,pIndex)

#define ITypeInfo2_ReleaseTypeAttr(This,pTypeAttr)	\
	(This)->lpVtbl -> ReleaseTypeAttr(This,pTypeAttr)

#define ITypeInfo2_ReleaseFuncDesc(This,pFuncDesc)	\
	(This)->lpVtbl -> ReleaseFuncDesc(This,pFuncDesc)

#define ITypeInfo2_ReleaseVarDesc(This,pVarDesc)	\
	(This)->lpVtbl -> ReleaseVarDesc(This,pVarDesc)


#define ITypeInfo2_GetTypeKind(This,pTypeKind)	\
	(This)->lpVtbl -> GetTypeKind(This,pTypeKind)

#define ITypeInfo2_GetTypeFlags(This,pTypeFlags)	\
	(This)->lpVtbl -> GetTypeFlags(This,pTypeFlags)

#define ITypeInfo2_GetFuncIndexOfMemId(This,memid,invKind,pFuncIndex)	\
	(This)->lpVtbl -> GetFuncIndexOfMemId(This,memid,invKind,pFuncIndex)

#define ITypeInfo2_GetVarIndexOfMemId(This,memid,pVarIndex)	\
	(This)->lpVtbl -> GetVarIndexOfMemId(This,memid,pVarIndex)

#define ITypeInfo2_GetCustData(This,guid,pVarVal)	\
	(This)->lpVtbl -> GetCustData(This,guid,pVarVal)

#define ITypeInfo2_GetFuncCustData(This,index,guid,pVarVal)	\
	(This)->lpVtbl -> GetFuncCustData(This,index,guid,pVarVal)

#define ITypeInfo2_GetParamCustData(This,indexFunc,indexParam,guid,pVarVal)	\
	(This)->lpVtbl -> GetParamCustData(This,indexFunc,indexParam,guid,pVarVal)

#define ITypeInfo2_GetVarCustData(This,index,guid,pVarVal)	\
	(This)->lpVtbl -> GetVarCustData(This,index,guid,pVarVal)

#define ITypeInfo2_GetImplTypeCustData(This,index,guid,pVarVal)	\
	(This)->lpVtbl -> GetImplTypeCustData(This,index,guid,pVarVal)

#define ITypeInfo2_GetDocumentation2(This,memid,lcid,pbstrHelpString,pdwHelpStringContext,pbstrHelpStringDll)	\
	(This)->lpVtbl -> GetDocumentation2(This,memid,lcid,pbstrHelpString,pdwHelpStringContext,pbstrHelpStringDll)

#define ITypeInfo2_GetAllCustData(This,pCustData)	\
	(This)->lpVtbl -> GetAllCustData(This,pCustData)

#define ITypeInfo2_GetAllFuncCustData(This,index,pCustData)	\
	(This)->lpVtbl -> GetAllFuncCustData(This,index,pCustData)

#define ITypeInfo2_GetAllParamCustData(This,indexFunc,indexParam,pCustData)	\
	(This)->lpVtbl -> GetAllParamCustData(This,indexFunc,indexParam,pCustData)

#define ITypeInfo2_GetAllVarCustData(This,index,pCustData)	\
	(This)->lpVtbl -> GetAllVarCustData(This,index,pCustData)

#define ITypeInfo2_GetAllImplTypeCustData(This,index,pCustData)	\
	(This)->lpVtbl -> GetAllImplTypeCustData(This,index,pCustData)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetTypeKind_Proxy( 
		ITypeInfo2 * This,
		/* [out] */ TYPEKIND *pTypeKind);


	void __RPC_STUB ITypeInfo2_GetTypeKind_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetTypeFlags_Proxy( 
		ITypeInfo2 * This,
		/* [out] */ ULONG *pTypeFlags);


	void __RPC_STUB ITypeInfo2_GetTypeFlags_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetFuncIndexOfMemId_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ INVOKEKIND invKind,
		/* [out] */ UINT *pFuncIndex);


	void __RPC_STUB ITypeInfo2_GetFuncIndexOfMemId_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetVarIndexOfMemId_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ MEMBERID memid,
		/* [out] */ UINT *pVarIndex);


	void __RPC_STUB ITypeInfo2_GetVarIndexOfMemId_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ REFGUID guid,
		/* [out] */ VARIANT *pVarVal);


	void __RPC_STUB ITypeInfo2_GetCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetFuncCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ REFGUID guid,
		/* [out] */ VARIANT *pVarVal);


	void __RPC_STUB ITypeInfo2_GetFuncCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetParamCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT indexFunc,
		/* [in] */ UINT indexParam,
		/* [in] */ REFGUID guid,
		/* [out] */ VARIANT *pVarVal);


	void __RPC_STUB ITypeInfo2_GetParamCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetVarCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ REFGUID guid,
		/* [out] */ VARIANT *pVarVal);


	void __RPC_STUB ITypeInfo2_GetVarCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetImplTypeCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT index,
		/* [in] */ REFGUID guid,
		/* [out] */ VARIANT *pVarVal);


	void __RPC_STUB ITypeInfo2_GetImplTypeCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo2_RemoteGetDocumentation2_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ LCID lcid,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pbstrHelpString,
		/* [out] */ DWORD *pdwHelpStringContext,
		/* [out] */ BSTR *pbstrHelpStringDll);


	void __RPC_STUB ITypeInfo2_RemoteGetDocumentation2_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetAllCustData_Proxy( 
		ITypeInfo2 * This,
		/* [out] */ CUSTDATA *pCustData);


	void __RPC_STUB ITypeInfo2_GetAllCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetAllFuncCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT index,
		/* [out] */ CUSTDATA *pCustData);


	void __RPC_STUB ITypeInfo2_GetAllFuncCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetAllParamCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT indexFunc,
		/* [in] */ UINT indexParam,
		/* [out] */ CUSTDATA *pCustData);


	void __RPC_STUB ITypeInfo2_GetAllParamCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetAllVarCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT index,
		/* [out] */ CUSTDATA *pCustData);


	void __RPC_STUB ITypeInfo2_GetAllVarCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeInfo2_GetAllImplTypeCustData_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ UINT index,
		/* [out] */ CUSTDATA *pCustData);


	void __RPC_STUB ITypeInfo2_GetAllImplTypeCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeInfo2_INTERFACE_DEFINED__ */


#ifndef __ITypeLib_INTERFACE_DEFINED__
#define __ITypeLib_INTERFACE_DEFINED__

	/* interface ITypeLib */
	/* [unique][uuid][object] */ 

	typedef /* [v1_enum] */ 
		enum tagSYSKIND
	{	SYS_WIN16	= 0,
	SYS_WIN32	= SYS_WIN16 + 1,
	SYS_MAC	= SYS_WIN32 + 1,
	SYS_WIN64	= SYS_MAC + 1
	} 	SYSKIND;

	typedef /* [v1_enum] */ 
		enum tagLIBFLAGS
	{	LIBFLAG_FRESTRICTED	= 0x1,
	LIBFLAG_FCONTROL	= 0x2,
	LIBFLAG_FHIDDEN	= 0x4,
	LIBFLAG_FHASDISKIMAGE	= 0x8
	} 	LIBFLAGS;

	typedef /* [unique] */ ITypeLib *LPTYPELIB;

	typedef struct tagTLIBATTR
	{
		GUID guid;
		LCID lcid;
		SYSKIND syskind;
		WORD wMajorVerNum;
		WORD wMinorVerNum;
		WORD wLibFlags;
	} 	TLIBATTR;

	typedef struct tagTLIBATTR *LPTLIBATTR;


	EXTERN_C const IID IID_ITypeLib;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020402-0000-0000-C000-000000000046")
ITypeLib : public IUnknown
	{
	public:
		virtual /* [local] */ UINT STDMETHODCALLTYPE GetTypeInfoCount( void) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeInfo( 
			/* [in] */ UINT index,
			/* [out] */ ITypeInfo **ppTInfo) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeInfoType( 
			/* [in] */ UINT index,
			/* [out] */ TYPEKIND *pTKind) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeInfoOfGuid( 
			/* [in] */ REFGUID guid,
			/* [out] */ ITypeInfo **ppTinfo) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetLibAttr( 
			/* [out] */ TLIBATTR **ppTLibAttr) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeComp( 
			/* [out] */ ITypeComp **ppTComp) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetDocumentation( 
			/* [in] */ INT index,
			/* [out] */ BSTR *pBstrName,
			/* [out] */ BSTR *pBstrDocString,
			/* [out] */ DWORD *pdwHelpContext,
			/* [out] */ BSTR *pBstrHelpFile) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE IsName( 
			/* [out][in] */ LPOLESTR szNameBuf,
			/* [in] */ ULONG lHashVal,
			/* [out] */ BOOL *pfName) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE FindName( 
			/* [out][in] */ LPOLESTR szNameBuf,
			/* [in] */ ULONG lHashVal,
			/* [length_is][size_is][out] */ ITypeInfo **ppTInfo,
			/* [length_is][size_is][out] */ MEMBERID *rgMemId,
			/* [out][in] */ USHORT *pcFound) = 0;

		virtual /* [local] */ void STDMETHODCALLTYPE ReleaseTLibAttr( 
			/* [in] */ TLIBATTR *pTLibAttr) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeLibVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeLib * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeLib * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeLib * This);

			/* [local] */ UINT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				ITypeLib * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				ITypeLib * This,
				/* [in] */ UINT index,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoType )( 
				ITypeLib * This,
				/* [in] */ UINT index,
				/* [out] */ TYPEKIND *pTKind);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoOfGuid )( 
				ITypeLib * This,
				/* [in] */ REFGUID guid,
				/* [out] */ ITypeInfo **ppTinfo);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetLibAttr )( 
				ITypeLib * This,
				/* [out] */ TLIBATTR **ppTLibAttr);

			HRESULT ( STDMETHODCALLTYPE *GetTypeComp )( 
				ITypeLib * This,
				/* [out] */ ITypeComp **ppTComp);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDocumentation )( 
				ITypeLib * This,
				/* [in] */ INT index,
				/* [out] */ BSTR *pBstrName,
				/* [out] */ BSTR *pBstrDocString,
				/* [out] */ DWORD *pdwHelpContext,
				/* [out] */ BSTR *pBstrHelpFile);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *IsName )( 
				ITypeLib * This,
				/* [out][in] */ LPOLESTR szNameBuf,
				/* [in] */ ULONG lHashVal,
				/* [out] */ BOOL *pfName);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *FindName )( 
				ITypeLib * This,
				/* [out][in] */ LPOLESTR szNameBuf,
				/* [in] */ ULONG lHashVal,
				/* [length_is][size_is][out] */ ITypeInfo **ppTInfo,
				/* [length_is][size_is][out] */ MEMBERID *rgMemId,
				/* [out][in] */ USHORT *pcFound);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseTLibAttr )( 
				ITypeLib * This,
				/* [in] */ TLIBATTR *pTLibAttr);

		END_INTERFACE
	} ITypeLibVtbl;

	interface ITypeLib
	{
		CONST_VTBL struct ITypeLibVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeLib_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeLib_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeLib_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeLib_GetTypeInfoCount(This)	\
	(This)->lpVtbl -> GetTypeInfoCount(This)

#define ITypeLib_GetTypeInfo(This,index,ppTInfo)	\
	(This)->lpVtbl -> GetTypeInfo(This,index,ppTInfo)

#define ITypeLib_GetTypeInfoType(This,index,pTKind)	\
	(This)->lpVtbl -> GetTypeInfoType(This,index,pTKind)

#define ITypeLib_GetTypeInfoOfGuid(This,guid,ppTinfo)	\
	(This)->lpVtbl -> GetTypeInfoOfGuid(This,guid,ppTinfo)

#define ITypeLib_GetLibAttr(This,ppTLibAttr)	\
	(This)->lpVtbl -> GetLibAttr(This,ppTLibAttr)

#define ITypeLib_GetTypeComp(This,ppTComp)	\
	(This)->lpVtbl -> GetTypeComp(This,ppTComp)

#define ITypeLib_GetDocumentation(This,index,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)	\
	(This)->lpVtbl -> GetDocumentation(This,index,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)

#define ITypeLib_IsName(This,szNameBuf,lHashVal,pfName)	\
	(This)->lpVtbl -> IsName(This,szNameBuf,lHashVal,pfName)

#define ITypeLib_FindName(This,szNameBuf,lHashVal,ppTInfo,rgMemId,pcFound)	\
	(This)->lpVtbl -> FindName(This,szNameBuf,lHashVal,ppTInfo,rgMemId,pcFound)

#define ITypeLib_ReleaseTLibAttr(This,pTLibAttr)	\
	(This)->lpVtbl -> ReleaseTLibAttr(This,pTLibAttr)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_RemoteGetTypeInfoCount_Proxy( 
		ITypeLib * This,
		/* [out] */ UINT *pcTInfo);


	void __RPC_STUB ITypeLib_RemoteGetTypeInfoCount_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeLib_GetTypeInfo_Proxy( 
		ITypeLib * This,
		/* [in] */ UINT index,
		/* [out] */ ITypeInfo **ppTInfo);


	void __RPC_STUB ITypeLib_GetTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeLib_GetTypeInfoType_Proxy( 
		ITypeLib * This,
		/* [in] */ UINT index,
		/* [out] */ TYPEKIND *pTKind);


	void __RPC_STUB ITypeLib_GetTypeInfoType_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeLib_GetTypeInfoOfGuid_Proxy( 
		ITypeLib * This,
		/* [in] */ REFGUID guid,
		/* [out] */ ITypeInfo **ppTinfo);


	void __RPC_STUB ITypeLib_GetTypeInfoOfGuid_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_RemoteGetLibAttr_Proxy( 
		ITypeLib * This,
		/* [out] */ LPTLIBATTR *ppTLibAttr,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);


	void __RPC_STUB ITypeLib_RemoteGetLibAttr_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeLib_GetTypeComp_Proxy( 
		ITypeLib * This,
		/* [out] */ ITypeComp **ppTComp);


	void __RPC_STUB ITypeLib_GetTypeComp_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_RemoteGetDocumentation_Proxy( 
		ITypeLib * This,
		/* [in] */ INT index,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ BSTR *pBstrDocString,
		/* [out] */ DWORD *pdwHelpContext,
		/* [out] */ BSTR *pBstrHelpFile);


	void __RPC_STUB ITypeLib_RemoteGetDocumentation_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_RemoteIsName_Proxy( 
		ITypeLib * This,
		/* [in] */ LPOLESTR szNameBuf,
		/* [in] */ ULONG lHashVal,
		/* [out] */ BOOL *pfName,
		/* [out] */ BSTR *pBstrLibName);


	void __RPC_STUB ITypeLib_RemoteIsName_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_RemoteFindName_Proxy( 
		ITypeLib * This,
		/* [in] */ LPOLESTR szNameBuf,
		/* [in] */ ULONG lHashVal,
		/* [length_is][size_is][out] */ ITypeInfo **ppTInfo,
		/* [length_is][size_is][out] */ MEMBERID *rgMemId,
		/* [out][in] */ USHORT *pcFound,
		/* [out] */ BSTR *pBstrLibName);


	void __RPC_STUB ITypeLib_RemoteFindName_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_LocalReleaseTLibAttr_Proxy( 
		ITypeLib * This);


	void __RPC_STUB ITypeLib_LocalReleaseTLibAttr_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeLib_INTERFACE_DEFINED__ */


#ifndef __ITypeLib2_INTERFACE_DEFINED__
#define __ITypeLib2_INTERFACE_DEFINED__

	/* interface ITypeLib2 */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ ITypeLib2 *LPTYPELIB2;


	EXTERN_C const IID IID_ITypeLib2;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020411-0000-0000-C000-000000000046")
ITypeLib2 : public ITypeLib
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE GetCustData( 
			/* [in] */ REFGUID guid,
			/* [out] */ VARIANT *pVarVal) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetLibStatistics( 
			/* [out] */ ULONG *pcUniqueNames,
			/* [out] */ ULONG *pcchUniqueNames) = 0;

		virtual /* [local] */ HRESULT STDMETHODCALLTYPE GetDocumentation2( 
			/* [in] */ INT index,
			/* [in] */ LCID lcid,
			/* [out] */ BSTR *pbstrHelpString,
			/* [out] */ DWORD *pdwHelpStringContext,
			/* [out] */ BSTR *pbstrHelpStringDll) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetAllCustData( 
			/* [out] */ CUSTDATA *pCustData) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeLib2Vtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeLib2 * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeLib2 * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeLib2 * This);

			/* [local] */ UINT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				ITypeLib2 * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				ITypeLib2 * This,
				/* [in] */ UINT index,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoType )( 
				ITypeLib2 * This,
				/* [in] */ UINT index,
				/* [out] */ TYPEKIND *pTKind);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoOfGuid )( 
				ITypeLib2 * This,
				/* [in] */ REFGUID guid,
				/* [out] */ ITypeInfo **ppTinfo);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetLibAttr )( 
				ITypeLib2 * This,
				/* [out] */ TLIBATTR **ppTLibAttr);

			HRESULT ( STDMETHODCALLTYPE *GetTypeComp )( 
				ITypeLib2 * This,
				/* [out] */ ITypeComp **ppTComp);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDocumentation )( 
				ITypeLib2 * This,
				/* [in] */ INT index,
				/* [out] */ BSTR *pBstrName,
				/* [out] */ BSTR *pBstrDocString,
				/* [out] */ DWORD *pdwHelpContext,
				/* [out] */ BSTR *pBstrHelpFile);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *IsName )( 
				ITypeLib2 * This,
				/* [out][in] */ LPOLESTR szNameBuf,
				/* [in] */ ULONG lHashVal,
				/* [out] */ BOOL *pfName);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *FindName )( 
				ITypeLib2 * This,
				/* [out][in] */ LPOLESTR szNameBuf,
				/* [in] */ ULONG lHashVal,
				/* [length_is][size_is][out] */ ITypeInfo **ppTInfo,
				/* [length_is][size_is][out] */ MEMBERID *rgMemId,
				/* [out][in] */ USHORT *pcFound);

			/* [local] */ void ( STDMETHODCALLTYPE *ReleaseTLibAttr )( 
				ITypeLib2 * This,
				/* [in] */ TLIBATTR *pTLibAttr);

			HRESULT ( STDMETHODCALLTYPE *GetCustData )( 
				ITypeLib2 * This,
				/* [in] */ REFGUID guid,
				/* [out] */ VARIANT *pVarVal);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetLibStatistics )( 
				ITypeLib2 * This,
				/* [out] */ ULONG *pcUniqueNames,
				/* [out] */ ULONG *pcchUniqueNames);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *GetDocumentation2 )( 
				ITypeLib2 * This,
				/* [in] */ INT index,
				/* [in] */ LCID lcid,
				/* [out] */ BSTR *pbstrHelpString,
				/* [out] */ DWORD *pdwHelpStringContext,
				/* [out] */ BSTR *pbstrHelpStringDll);

			HRESULT ( STDMETHODCALLTYPE *GetAllCustData )( 
				ITypeLib2 * This,
				/* [out] */ CUSTDATA *pCustData);

		END_INTERFACE
	} ITypeLib2Vtbl;

	interface ITypeLib2
	{
		CONST_VTBL struct ITypeLib2Vtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeLib2_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeLib2_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeLib2_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeLib2_GetTypeInfoCount(This)	\
	(This)->lpVtbl -> GetTypeInfoCount(This)

#define ITypeLib2_GetTypeInfo(This,index,ppTInfo)	\
	(This)->lpVtbl -> GetTypeInfo(This,index,ppTInfo)

#define ITypeLib2_GetTypeInfoType(This,index,pTKind)	\
	(This)->lpVtbl -> GetTypeInfoType(This,index,pTKind)

#define ITypeLib2_GetTypeInfoOfGuid(This,guid,ppTinfo)	\
	(This)->lpVtbl -> GetTypeInfoOfGuid(This,guid,ppTinfo)

#define ITypeLib2_GetLibAttr(This,ppTLibAttr)	\
	(This)->lpVtbl -> GetLibAttr(This,ppTLibAttr)

#define ITypeLib2_GetTypeComp(This,ppTComp)	\
	(This)->lpVtbl -> GetTypeComp(This,ppTComp)

#define ITypeLib2_GetDocumentation(This,index,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)	\
	(This)->lpVtbl -> GetDocumentation(This,index,pBstrName,pBstrDocString,pdwHelpContext,pBstrHelpFile)

#define ITypeLib2_IsName(This,szNameBuf,lHashVal,pfName)	\
	(This)->lpVtbl -> IsName(This,szNameBuf,lHashVal,pfName)

#define ITypeLib2_FindName(This,szNameBuf,lHashVal,ppTInfo,rgMemId,pcFound)	\
	(This)->lpVtbl -> FindName(This,szNameBuf,lHashVal,ppTInfo,rgMemId,pcFound)

#define ITypeLib2_ReleaseTLibAttr(This,pTLibAttr)	\
	(This)->lpVtbl -> ReleaseTLibAttr(This,pTLibAttr)


#define ITypeLib2_GetCustData(This,guid,pVarVal)	\
	(This)->lpVtbl -> GetCustData(This,guid,pVarVal)

#define ITypeLib2_GetLibStatistics(This,pcUniqueNames,pcchUniqueNames)	\
	(This)->lpVtbl -> GetLibStatistics(This,pcUniqueNames,pcchUniqueNames)

#define ITypeLib2_GetDocumentation2(This,index,lcid,pbstrHelpString,pdwHelpStringContext,pbstrHelpStringDll)	\
	(This)->lpVtbl -> GetDocumentation2(This,index,lcid,pbstrHelpString,pdwHelpStringContext,pbstrHelpStringDll)

#define ITypeLib2_GetAllCustData(This,pCustData)	\
	(This)->lpVtbl -> GetAllCustData(This,pCustData)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ITypeLib2_GetCustData_Proxy( 
		ITypeLib2 * This,
		/* [in] */ REFGUID guid,
		/* [out] */ VARIANT *pVarVal);


	void __RPC_STUB ITypeLib2_GetCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib2_RemoteGetLibStatistics_Proxy( 
		ITypeLib2 * This,
		/* [out] */ ULONG *pcUniqueNames,
		/* [out] */ ULONG *pcchUniqueNames);


	void __RPC_STUB ITypeLib2_RemoteGetLibStatistics_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib2_RemoteGetDocumentation2_Proxy( 
		ITypeLib2 * This,
		/* [in] */ INT index,
		/* [in] */ LCID lcid,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pbstrHelpString,
		/* [out] */ DWORD *pdwHelpStringContext,
		/* [out] */ BSTR *pbstrHelpStringDll);


	void __RPC_STUB ITypeLib2_RemoteGetDocumentation2_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeLib2_GetAllCustData_Proxy( 
		ITypeLib2 * This,
		/* [out] */ CUSTDATA *pCustData);


	void __RPC_STUB ITypeLib2_GetAllCustData_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeLib2_INTERFACE_DEFINED__ */


#ifndef __ITypeChangeEvents_INTERFACE_DEFINED__
#define __ITypeChangeEvents_INTERFACE_DEFINED__

	/* interface ITypeChangeEvents */
	/* [local][unique][uuid][object] */ 

	typedef /* [unique] */ ITypeChangeEvents *LPTYPECHANGEEVENTS;

	typedef 
		enum tagCHANGEKIND
	{	CHANGEKIND_ADDMEMBER	= 0,
	CHANGEKIND_DELETEMEMBER	= CHANGEKIND_ADDMEMBER + 1,
	CHANGEKIND_SETNAMES	= CHANGEKIND_DELETEMEMBER + 1,
	CHANGEKIND_SETDOCUMENTATION	= CHANGEKIND_SETNAMES + 1,
	CHANGEKIND_GENERAL	= CHANGEKIND_SETDOCUMENTATION + 1,
	CHANGEKIND_INVALIDATE	= CHANGEKIND_GENERAL + 1,
	CHANGEKIND_CHANGEFAILED	= CHANGEKIND_INVALIDATE + 1,
	CHANGEKIND_MAX	= CHANGEKIND_CHANGEFAILED + 1
	} 	CHANGEKIND;


	EXTERN_C const IID IID_ITypeChangeEvents;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("00020410-0000-0000-C000-000000000046")
ITypeChangeEvents : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE RequestTypeChange( 
			/* [in] */ CHANGEKIND changeKind,
			/* [in] */ ITypeInfo *pTInfoBefore,
			/* [in] */ LPOLESTR pStrName,
			/* [out] */ INT *pfCancel) = 0;

		virtual HRESULT STDMETHODCALLTYPE AfterTypeChange( 
			/* [in] */ CHANGEKIND changeKind,
			/* [in] */ ITypeInfo *pTInfoAfter,
			/* [in] */ LPOLESTR pStrName) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeChangeEventsVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeChangeEvents * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeChangeEvents * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeChangeEvents * This);

			HRESULT ( STDMETHODCALLTYPE *RequestTypeChange )( 
				ITypeChangeEvents * This,
				/* [in] */ CHANGEKIND changeKind,
				/* [in] */ ITypeInfo *pTInfoBefore,
				/* [in] */ LPOLESTR pStrName,
				/* [out] */ INT *pfCancel);

			HRESULT ( STDMETHODCALLTYPE *AfterTypeChange )( 
				ITypeChangeEvents * This,
				/* [in] */ CHANGEKIND changeKind,
				/* [in] */ ITypeInfo *pTInfoAfter,
				/* [in] */ LPOLESTR pStrName);

		END_INTERFACE
	} ITypeChangeEventsVtbl;

	interface ITypeChangeEvents
	{
		CONST_VTBL struct ITypeChangeEventsVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeChangeEvents_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeChangeEvents_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeChangeEvents_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeChangeEvents_RequestTypeChange(This,changeKind,pTInfoBefore,pStrName,pfCancel)	\
	(This)->lpVtbl -> RequestTypeChange(This,changeKind,pTInfoBefore,pStrName,pfCancel)

#define ITypeChangeEvents_AfterTypeChange(This,changeKind,pTInfoAfter,pStrName)	\
	(This)->lpVtbl -> AfterTypeChange(This,changeKind,pTInfoAfter,pStrName)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ITypeChangeEvents_RequestTypeChange_Proxy( 
		ITypeChangeEvents * This,
		/* [in] */ CHANGEKIND changeKind,
		/* [in] */ ITypeInfo *pTInfoBefore,
		/* [in] */ LPOLESTR pStrName,
		/* [out] */ INT *pfCancel);


	void __RPC_STUB ITypeChangeEvents_RequestTypeChange_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeChangeEvents_AfterTypeChange_Proxy( 
		ITypeChangeEvents * This,
		/* [in] */ CHANGEKIND changeKind,
		/* [in] */ ITypeInfo *pTInfoAfter,
		/* [in] */ LPOLESTR pStrName);


	void __RPC_STUB ITypeChangeEvents_AfterTypeChange_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeChangeEvents_INTERFACE_DEFINED__ */


#ifndef __IErrorInfo_INTERFACE_DEFINED__
#define __IErrorInfo_INTERFACE_DEFINED__

	/* interface IErrorInfo */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ IErrorInfo *LPERRORINFO;


	EXTERN_C const IID IID_IErrorInfo;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("1CF2B120-547D-101B-8E65-08002B2BD119")
IErrorInfo : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE GetGUID( 
			/* [out] */ GUID *pGUID) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetSource( 
			/* [out] */ BSTR *pBstrSource) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetDescription( 
			/* [out] */ BSTR *pBstrDescription) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetHelpFile( 
			/* [out] */ BSTR *pBstrHelpFile) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetHelpContext( 
			/* [out] */ DWORD *pdwHelpContext) = 0;

	};

#else 	/* C style interface */

	typedef struct IErrorInfoVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			IErrorInfo * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				IErrorInfo * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				IErrorInfo * This);

			HRESULT ( STDMETHODCALLTYPE *GetGUID )( 
				IErrorInfo * This,
				/* [out] */ GUID *pGUID);

			HRESULT ( STDMETHODCALLTYPE *GetSource )( 
				IErrorInfo * This,
				/* [out] */ BSTR *pBstrSource);

			HRESULT ( STDMETHODCALLTYPE *GetDescription )( 
				IErrorInfo * This,
				/* [out] */ BSTR *pBstrDescription);

			HRESULT ( STDMETHODCALLTYPE *GetHelpFile )( 
				IErrorInfo * This,
				/* [out] */ BSTR *pBstrHelpFile);

			HRESULT ( STDMETHODCALLTYPE *GetHelpContext )( 
				IErrorInfo * This,
				/* [out] */ DWORD *pdwHelpContext);

		END_INTERFACE
	} IErrorInfoVtbl;

	interface IErrorInfo
	{
		CONST_VTBL struct IErrorInfoVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define IErrorInfo_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IErrorInfo_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define IErrorInfo_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define IErrorInfo_GetGUID(This,pGUID)	\
	(This)->lpVtbl -> GetGUID(This,pGUID)

#define IErrorInfo_GetSource(This,pBstrSource)	\
	(This)->lpVtbl -> GetSource(This,pBstrSource)

#define IErrorInfo_GetDescription(This,pBstrDescription)	\
	(This)->lpVtbl -> GetDescription(This,pBstrDescription)

#define IErrorInfo_GetHelpFile(This,pBstrHelpFile)	\
	(This)->lpVtbl -> GetHelpFile(This,pBstrHelpFile)

#define IErrorInfo_GetHelpContext(This,pdwHelpContext)	\
	(This)->lpVtbl -> GetHelpContext(This,pdwHelpContext)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE IErrorInfo_GetGUID_Proxy( 
		IErrorInfo * This,
		/* [out] */ GUID *pGUID);


	void __RPC_STUB IErrorInfo_GetGUID_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IErrorInfo_GetSource_Proxy( 
		IErrorInfo * This,
		/* [out] */ BSTR *pBstrSource);


	void __RPC_STUB IErrorInfo_GetSource_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IErrorInfo_GetDescription_Proxy( 
		IErrorInfo * This,
		/* [out] */ BSTR *pBstrDescription);


	void __RPC_STUB IErrorInfo_GetDescription_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IErrorInfo_GetHelpFile_Proxy( 
		IErrorInfo * This,
		/* [out] */ BSTR *pBstrHelpFile);


	void __RPC_STUB IErrorInfo_GetHelpFile_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IErrorInfo_GetHelpContext_Proxy( 
		IErrorInfo * This,
		/* [out] */ DWORD *pdwHelpContext);


	void __RPC_STUB IErrorInfo_GetHelpContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __IErrorInfo_INTERFACE_DEFINED__ */


#ifndef __ICreateErrorInfo_INTERFACE_DEFINED__
#define __ICreateErrorInfo_INTERFACE_DEFINED__

	/* interface ICreateErrorInfo */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ ICreateErrorInfo *LPCREATEERRORINFO;


	EXTERN_C const IID IID_ICreateErrorInfo;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("22F03340-547D-101B-8E65-08002B2BD119")
ICreateErrorInfo : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE SetGUID( 
			/* [in] */ REFGUID rguid) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetSource( 
			/* [in] */ LPOLESTR szSource) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetDescription( 
			/* [in] */ LPOLESTR szDescription) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpFile( 
			/* [in] */ LPOLESTR szHelpFile) = 0;

		virtual HRESULT STDMETHODCALLTYPE SetHelpContext( 
			/* [in] */ DWORD dwHelpContext) = 0;

	};

#else 	/* C style interface */

	typedef struct ICreateErrorInfoVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ICreateErrorInfo * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ICreateErrorInfo * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ICreateErrorInfo * This);

			HRESULT ( STDMETHODCALLTYPE *SetGUID )( 
				ICreateErrorInfo * This,
				/* [in] */ REFGUID rguid);

			HRESULT ( STDMETHODCALLTYPE *SetSource )( 
				ICreateErrorInfo * This,
				/* [in] */ LPOLESTR szSource);

			HRESULT ( STDMETHODCALLTYPE *SetDescription )( 
				ICreateErrorInfo * This,
				/* [in] */ LPOLESTR szDescription);

			HRESULT ( STDMETHODCALLTYPE *SetHelpFile )( 
				ICreateErrorInfo * This,
				/* [in] */ LPOLESTR szHelpFile);

			HRESULT ( STDMETHODCALLTYPE *SetHelpContext )( 
				ICreateErrorInfo * This,
				/* [in] */ DWORD dwHelpContext);

		END_INTERFACE
	} ICreateErrorInfoVtbl;

	interface ICreateErrorInfo
	{
		CONST_VTBL struct ICreateErrorInfoVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ICreateErrorInfo_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ICreateErrorInfo_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ICreateErrorInfo_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ICreateErrorInfo_SetGUID(This,rguid)	\
	(This)->lpVtbl -> SetGUID(This,rguid)

#define ICreateErrorInfo_SetSource(This,szSource)	\
	(This)->lpVtbl -> SetSource(This,szSource)

#define ICreateErrorInfo_SetDescription(This,szDescription)	\
	(This)->lpVtbl -> SetDescription(This,szDescription)

#define ICreateErrorInfo_SetHelpFile(This,szHelpFile)	\
	(This)->lpVtbl -> SetHelpFile(This,szHelpFile)

#define ICreateErrorInfo_SetHelpContext(This,dwHelpContext)	\
	(This)->lpVtbl -> SetHelpContext(This,dwHelpContext)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ICreateErrorInfo_SetGUID_Proxy( 
		ICreateErrorInfo * This,
		/* [in] */ REFGUID rguid);


	void __RPC_STUB ICreateErrorInfo_SetGUID_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateErrorInfo_SetSource_Proxy( 
		ICreateErrorInfo * This,
		/* [in] */ LPOLESTR szSource);


	void __RPC_STUB ICreateErrorInfo_SetSource_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateErrorInfo_SetDescription_Proxy( 
		ICreateErrorInfo * This,
		/* [in] */ LPOLESTR szDescription);


	void __RPC_STUB ICreateErrorInfo_SetDescription_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateErrorInfo_SetHelpFile_Proxy( 
		ICreateErrorInfo * This,
		/* [in] */ LPOLESTR szHelpFile);


	void __RPC_STUB ICreateErrorInfo_SetHelpFile_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ICreateErrorInfo_SetHelpContext_Proxy( 
		ICreateErrorInfo * This,
		/* [in] */ DWORD dwHelpContext);


	void __RPC_STUB ICreateErrorInfo_SetHelpContext_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ICreateErrorInfo_INTERFACE_DEFINED__ */


#ifndef __ISupportErrorInfo_INTERFACE_DEFINED__
#define __ISupportErrorInfo_INTERFACE_DEFINED__

	/* interface ISupportErrorInfo */
	/* [unique][uuid][object] */ 

	typedef /* [unique] */ ISupportErrorInfo *LPSUPPORTERRORINFO;


	EXTERN_C const IID IID_ISupportErrorInfo;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("DF0B3D60-548F-101B-8E65-08002B2BD119")
ISupportErrorInfo : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE InterfaceSupportsErrorInfo( 
			/* [in] */ REFIID riid) = 0;

	};

#else 	/* C style interface */

	typedef struct ISupportErrorInfoVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ISupportErrorInfo * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ISupportErrorInfo * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ISupportErrorInfo * This);

			HRESULT ( STDMETHODCALLTYPE *InterfaceSupportsErrorInfo )( 
				ISupportErrorInfo * This,
				/* [in] */ REFIID riid);

		END_INTERFACE
	} ISupportErrorInfoVtbl;

	interface ISupportErrorInfo
	{
		CONST_VTBL struct ISupportErrorInfoVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ISupportErrorInfo_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ISupportErrorInfo_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ISupportErrorInfo_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ISupportErrorInfo_InterfaceSupportsErrorInfo(This,riid)	\
	(This)->lpVtbl -> InterfaceSupportsErrorInfo(This,riid)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ISupportErrorInfo_InterfaceSupportsErrorInfo_Proxy( 
		ISupportErrorInfo * This,
		/* [in] */ REFIID riid);


	void __RPC_STUB ISupportErrorInfo_InterfaceSupportsErrorInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ISupportErrorInfo_INTERFACE_DEFINED__ */


#ifndef __ITypeFactory_INTERFACE_DEFINED__
#define __ITypeFactory_INTERFACE_DEFINED__

	/* interface ITypeFactory */
	/* [uuid][object] */ 


	EXTERN_C const IID IID_ITypeFactory;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("0000002E-0000-0000-C000-000000000046")
ITypeFactory : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE CreateFromTypeInfo( 
			/* [in] */ ITypeInfo *pTypeInfo,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ IUnknown **ppv) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeFactoryVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeFactory * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeFactory * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeFactory * This);

			HRESULT ( STDMETHODCALLTYPE *CreateFromTypeInfo )( 
				ITypeFactory * This,
				/* [in] */ ITypeInfo *pTypeInfo,
				/* [in] */ REFIID riid,
				/* [iid_is][out] */ IUnknown **ppv);

		END_INTERFACE
	} ITypeFactoryVtbl;

	interface ITypeFactory
	{
		CONST_VTBL struct ITypeFactoryVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeFactory_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeFactory_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeFactory_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeFactory_CreateFromTypeInfo(This,pTypeInfo,riid,ppv)	\
	(This)->lpVtbl -> CreateFromTypeInfo(This,pTypeInfo,riid,ppv)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ITypeFactory_CreateFromTypeInfo_Proxy( 
		ITypeFactory * This,
		/* [in] */ ITypeInfo *pTypeInfo,
		/* [in] */ REFIID riid,
		/* [iid_is][out] */ IUnknown **ppv);


	void __RPC_STUB ITypeFactory_CreateFromTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeFactory_INTERFACE_DEFINED__ */


#ifndef __ITypeMarshal_INTERFACE_DEFINED__
#define __ITypeMarshal_INTERFACE_DEFINED__

	/* interface ITypeMarshal */
	/* [uuid][object][local] */ 


	EXTERN_C const IID IID_ITypeMarshal;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("0000002D-0000-0000-C000-000000000046")
ITypeMarshal : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE Size( 
			/* [in] */ PVOID pvType,
			/* [in] */ DWORD dwDestContext,
			/* [in] */ PVOID pvDestContext,
			/* [out] */ ULONG *pSize) = 0;

		virtual HRESULT STDMETHODCALLTYPE Marshal( 
			/* [in] */ PVOID pvType,
			/* [in] */ DWORD dwDestContext,
			/* [in] */ PVOID pvDestContext,
			/* [in] */ ULONG cbBufferLength,
			/* [out] */ BYTE *pBuffer,
			/* [out] */ ULONG *pcbWritten) = 0;

		virtual HRESULT STDMETHODCALLTYPE Unmarshal( 
			/* [out] */ PVOID pvType,
			/* [in] */ DWORD dwFlags,
			/* [in] */ ULONG cbBufferLength,
			/* [in] */ BYTE *pBuffer,
			/* [out] */ ULONG *pcbRead) = 0;

		virtual HRESULT STDMETHODCALLTYPE Free( 
			/* [in] */ PVOID pvType) = 0;

	};

#else 	/* C style interface */

	typedef struct ITypeMarshalVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			ITypeMarshal * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				ITypeMarshal * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				ITypeMarshal * This);

			HRESULT ( STDMETHODCALLTYPE *Size )( 
				ITypeMarshal * This,
				/* [in] */ PVOID pvType,
				/* [in] */ DWORD dwDestContext,
				/* [in] */ PVOID pvDestContext,
				/* [out] */ ULONG *pSize);

			HRESULT ( STDMETHODCALLTYPE *Marshal )( 
				ITypeMarshal * This,
				/* [in] */ PVOID pvType,
				/* [in] */ DWORD dwDestContext,
				/* [in] */ PVOID pvDestContext,
				/* [in] */ ULONG cbBufferLength,
				/* [out] */ BYTE *pBuffer,
				/* [out] */ ULONG *pcbWritten);

			HRESULT ( STDMETHODCALLTYPE *Unmarshal )( 
				ITypeMarshal * This,
				/* [out] */ PVOID pvType,
				/* [in] */ DWORD dwFlags,
				/* [in] */ ULONG cbBufferLength,
				/* [in] */ BYTE *pBuffer,
				/* [out] */ ULONG *pcbRead);

			HRESULT ( STDMETHODCALLTYPE *Free )( 
				ITypeMarshal * This,
				/* [in] */ PVOID pvType);

		END_INTERFACE
	} ITypeMarshalVtbl;

	interface ITypeMarshal
	{
		CONST_VTBL struct ITypeMarshalVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define ITypeMarshal_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define ITypeMarshal_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define ITypeMarshal_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define ITypeMarshal_Size(This,pvType,dwDestContext,pvDestContext,pSize)	\
	(This)->lpVtbl -> Size(This,pvType,dwDestContext,pvDestContext,pSize)

#define ITypeMarshal_Marshal(This,pvType,dwDestContext,pvDestContext,cbBufferLength,pBuffer,pcbWritten)	\
	(This)->lpVtbl -> Marshal(This,pvType,dwDestContext,pvDestContext,cbBufferLength,pBuffer,pcbWritten)

#define ITypeMarshal_Unmarshal(This,pvType,dwFlags,cbBufferLength,pBuffer,pcbRead)	\
	(This)->lpVtbl -> Unmarshal(This,pvType,dwFlags,cbBufferLength,pBuffer,pcbRead)

#define ITypeMarshal_Free(This,pvType)	\
	(This)->lpVtbl -> Free(This,pvType)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE ITypeMarshal_Size_Proxy( 
		ITypeMarshal * This,
		/* [in] */ PVOID pvType,
		/* [in] */ DWORD dwDestContext,
		/* [in] */ PVOID pvDestContext,
		/* [out] */ ULONG *pSize);


	void __RPC_STUB ITypeMarshal_Size_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeMarshal_Marshal_Proxy( 
		ITypeMarshal * This,
		/* [in] */ PVOID pvType,
		/* [in] */ DWORD dwDestContext,
		/* [in] */ PVOID pvDestContext,
		/* [in] */ ULONG cbBufferLength,
		/* [out] */ BYTE *pBuffer,
		/* [out] */ ULONG *pcbWritten);


	void __RPC_STUB ITypeMarshal_Marshal_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeMarshal_Unmarshal_Proxy( 
		ITypeMarshal * This,
		/* [out] */ PVOID pvType,
		/* [in] */ DWORD dwFlags,
		/* [in] */ ULONG cbBufferLength,
		/* [in] */ BYTE *pBuffer,
		/* [out] */ ULONG *pcbRead);


	void __RPC_STUB ITypeMarshal_Unmarshal_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE ITypeMarshal_Free_Proxy( 
		ITypeMarshal * This,
		/* [in] */ PVOID pvType);


	void __RPC_STUB ITypeMarshal_Free_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __ITypeMarshal_INTERFACE_DEFINED__ */


#ifndef __IRecordInfo_INTERFACE_DEFINED__
#define __IRecordInfo_INTERFACE_DEFINED__

	/* interface IRecordInfo */
	/* [uuid][object][local] */ 

	typedef /* [unique] */ IRecordInfo *LPRECORDINFO;


	EXTERN_C const IID IID_IRecordInfo;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("0000002F-0000-0000-C000-000000000046")
IRecordInfo : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE RecordInit( 
			/* [out] */ PVOID pvNew) = 0;

		virtual HRESULT STDMETHODCALLTYPE RecordClear( 
			/* [in] */ PVOID pvExisting) = 0;

		virtual HRESULT STDMETHODCALLTYPE RecordCopy( 
			/* [in] */ PVOID pvExisting,
			/* [out] */ PVOID pvNew) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetGuid( 
			/* [out] */ GUID *pguid) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetName( 
			/* [out] */ BSTR *pbstrName) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetSize( 
			/* [out] */ ULONG *pcbSize) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetTypeInfo( 
			/* [out] */ ITypeInfo **ppTypeInfo) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetField( 
			/* [in] */ PVOID pvData,
			/* [in] */ LPCOLESTR szFieldName,
			/* [out] */ VARIANT *pvarField) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetFieldNoCopy( 
			/* [in] */ PVOID pvData,
			/* [in] */ LPCOLESTR szFieldName,
			/* [out] */ VARIANT *pvarField,
			/* [out] */ PVOID *ppvDataCArray) = 0;

		virtual HRESULT STDMETHODCALLTYPE PutField( 
			/* [in] */ ULONG wFlags,
			/* [out][in] */ PVOID pvData,
			/* [in] */ LPCOLESTR szFieldName,
			/* [in] */ VARIANT *pvarField) = 0;

		virtual HRESULT STDMETHODCALLTYPE PutFieldNoCopy( 
			/* [in] */ ULONG wFlags,
			/* [out][in] */ PVOID pvData,
			/* [in] */ LPCOLESTR szFieldName,
			/* [in] */ VARIANT *pvarField) = 0;

		virtual HRESULT STDMETHODCALLTYPE GetFieldNames( 
			/* [out][in] */ ULONG *pcNames,
			/* [length_is][size_is][out] */ BSTR *rgBstrNames) = 0;

		virtual BOOL STDMETHODCALLTYPE IsMatchingType( 
			/* [in] */ IRecordInfo *pRecordInfo) = 0;

		virtual PVOID STDMETHODCALLTYPE RecordCreate( void) = 0;

		virtual HRESULT STDMETHODCALLTYPE RecordCreateCopy( 
			/* [in] */ PVOID pvSource,
			/* [out] */ PVOID *ppvDest) = 0;

		virtual HRESULT STDMETHODCALLTYPE RecordDestroy( 
			/* [in] */ PVOID pvRecord) = 0;

	};

#else 	/* C style interface */

	typedef struct IRecordInfoVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			IRecordInfo * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				IRecordInfo * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				IRecordInfo * This);

			HRESULT ( STDMETHODCALLTYPE *RecordInit )( 
				IRecordInfo * This,
				/* [out] */ PVOID pvNew);

			HRESULT ( STDMETHODCALLTYPE *RecordClear )( 
				IRecordInfo * This,
				/* [in] */ PVOID pvExisting);

			HRESULT ( STDMETHODCALLTYPE *RecordCopy )( 
				IRecordInfo * This,
				/* [in] */ PVOID pvExisting,
				/* [out] */ PVOID pvNew);

			HRESULT ( STDMETHODCALLTYPE *GetGuid )( 
				IRecordInfo * This,
				/* [out] */ GUID *pguid);

			HRESULT ( STDMETHODCALLTYPE *GetName )( 
				IRecordInfo * This,
				/* [out] */ BSTR *pbstrName);

			HRESULT ( STDMETHODCALLTYPE *GetSize )( 
				IRecordInfo * This,
				/* [out] */ ULONG *pcbSize);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				IRecordInfo * This,
				/* [out] */ ITypeInfo **ppTypeInfo);

			HRESULT ( STDMETHODCALLTYPE *GetField )( 
				IRecordInfo * This,
				/* [in] */ PVOID pvData,
				/* [in] */ LPCOLESTR szFieldName,
				/* [out] */ VARIANT *pvarField);

			HRESULT ( STDMETHODCALLTYPE *GetFieldNoCopy )( 
				IRecordInfo * This,
				/* [in] */ PVOID pvData,
				/* [in] */ LPCOLESTR szFieldName,
				/* [out] */ VARIANT *pvarField,
				/* [out] */ PVOID *ppvDataCArray);

			HRESULT ( STDMETHODCALLTYPE *PutField )( 
				IRecordInfo * This,
				/* [in] */ ULONG wFlags,
				/* [out][in] */ PVOID pvData,
				/* [in] */ LPCOLESTR szFieldName,
				/* [in] */ VARIANT *pvarField);

			HRESULT ( STDMETHODCALLTYPE *PutFieldNoCopy )( 
				IRecordInfo * This,
				/* [in] */ ULONG wFlags,
				/* [out][in] */ PVOID pvData,
				/* [in] */ LPCOLESTR szFieldName,
				/* [in] */ VARIANT *pvarField);

			HRESULT ( STDMETHODCALLTYPE *GetFieldNames )( 
				IRecordInfo * This,
				/* [out][in] */ ULONG *pcNames,
				/* [length_is][size_is][out] */ BSTR *rgBstrNames);

			BOOL ( STDMETHODCALLTYPE *IsMatchingType )( 
				IRecordInfo * This,
				/* [in] */ IRecordInfo *pRecordInfo);

			PVOID ( STDMETHODCALLTYPE *RecordCreate )( 
				IRecordInfo * This);

			HRESULT ( STDMETHODCALLTYPE *RecordCreateCopy )( 
				IRecordInfo * This,
				/* [in] */ PVOID pvSource,
				/* [out] */ PVOID *ppvDest);

			HRESULT ( STDMETHODCALLTYPE *RecordDestroy )( 
				IRecordInfo * This,
				/* [in] */ PVOID pvRecord);

		END_INTERFACE
	} IRecordInfoVtbl;

	interface IRecordInfo
	{
		CONST_VTBL struct IRecordInfoVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define IRecordInfo_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IRecordInfo_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define IRecordInfo_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define IRecordInfo_RecordInit(This,pvNew)	\
	(This)->lpVtbl -> RecordInit(This,pvNew)

#define IRecordInfo_RecordClear(This,pvExisting)	\
	(This)->lpVtbl -> RecordClear(This,pvExisting)

#define IRecordInfo_RecordCopy(This,pvExisting,pvNew)	\
	(This)->lpVtbl -> RecordCopy(This,pvExisting,pvNew)

#define IRecordInfo_GetGuid(This,pguid)	\
	(This)->lpVtbl -> GetGuid(This,pguid)

#define IRecordInfo_GetName(This,pbstrName)	\
	(This)->lpVtbl -> GetName(This,pbstrName)

#define IRecordInfo_GetSize(This,pcbSize)	\
	(This)->lpVtbl -> GetSize(This,pcbSize)

#define IRecordInfo_GetTypeInfo(This,ppTypeInfo)	\
	(This)->lpVtbl -> GetTypeInfo(This,ppTypeInfo)

#define IRecordInfo_GetField(This,pvData,szFieldName,pvarField)	\
	(This)->lpVtbl -> GetField(This,pvData,szFieldName,pvarField)

#define IRecordInfo_GetFieldNoCopy(This,pvData,szFieldName,pvarField,ppvDataCArray)	\
	(This)->lpVtbl -> GetFieldNoCopy(This,pvData,szFieldName,pvarField,ppvDataCArray)

#define IRecordInfo_PutField(This,wFlags,pvData,szFieldName,pvarField)	\
	(This)->lpVtbl -> PutField(This,wFlags,pvData,szFieldName,pvarField)

#define IRecordInfo_PutFieldNoCopy(This,wFlags,pvData,szFieldName,pvarField)	\
	(This)->lpVtbl -> PutFieldNoCopy(This,wFlags,pvData,szFieldName,pvarField)

#define IRecordInfo_GetFieldNames(This,pcNames,rgBstrNames)	\
	(This)->lpVtbl -> GetFieldNames(This,pcNames,rgBstrNames)

#define IRecordInfo_IsMatchingType(This,pRecordInfo)	\
	(This)->lpVtbl -> IsMatchingType(This,pRecordInfo)

#define IRecordInfo_RecordCreate(This)	\
	(This)->lpVtbl -> RecordCreate(This)

#define IRecordInfo_RecordCreateCopy(This,pvSource,ppvDest)	\
	(This)->lpVtbl -> RecordCreateCopy(This,pvSource,ppvDest)

#define IRecordInfo_RecordDestroy(This,pvRecord)	\
	(This)->lpVtbl -> RecordDestroy(This,pvRecord)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE IRecordInfo_RecordInit_Proxy( 
		IRecordInfo * This,
		/* [out] */ PVOID pvNew);


	void __RPC_STUB IRecordInfo_RecordInit_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_RecordClear_Proxy( 
		IRecordInfo * This,
		/* [in] */ PVOID pvExisting);


	void __RPC_STUB IRecordInfo_RecordClear_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_RecordCopy_Proxy( 
		IRecordInfo * This,
		/* [in] */ PVOID pvExisting,
		/* [out] */ PVOID pvNew);


	void __RPC_STUB IRecordInfo_RecordCopy_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_GetGuid_Proxy( 
		IRecordInfo * This,
		/* [out] */ GUID *pguid);


	void __RPC_STUB IRecordInfo_GetGuid_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_GetName_Proxy( 
		IRecordInfo * This,
		/* [out] */ BSTR *pbstrName);


	void __RPC_STUB IRecordInfo_GetName_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_GetSize_Proxy( 
		IRecordInfo * This,
		/* [out] */ ULONG *pcbSize);


	void __RPC_STUB IRecordInfo_GetSize_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_GetTypeInfo_Proxy( 
		IRecordInfo * This,
		/* [out] */ ITypeInfo **ppTypeInfo);


	void __RPC_STUB IRecordInfo_GetTypeInfo_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_GetField_Proxy( 
		IRecordInfo * This,
		/* [in] */ PVOID pvData,
		/* [in] */ LPCOLESTR szFieldName,
		/* [out] */ VARIANT *pvarField);


	void __RPC_STUB IRecordInfo_GetField_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_GetFieldNoCopy_Proxy( 
		IRecordInfo * This,
		/* [in] */ PVOID pvData,
		/* [in] */ LPCOLESTR szFieldName,
		/* [out] */ VARIANT *pvarField,
		/* [out] */ PVOID *ppvDataCArray);


	void __RPC_STUB IRecordInfo_GetFieldNoCopy_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_PutField_Proxy( 
		IRecordInfo * This,
		/* [in] */ ULONG wFlags,
		/* [out][in] */ PVOID pvData,
		/* [in] */ LPCOLESTR szFieldName,
		/* [in] */ VARIANT *pvarField);


	void __RPC_STUB IRecordInfo_PutField_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_PutFieldNoCopy_Proxy( 
		IRecordInfo * This,
		/* [in] */ ULONG wFlags,
		/* [out][in] */ PVOID pvData,
		/* [in] */ LPCOLESTR szFieldName,
		/* [in] */ VARIANT *pvarField);


	void __RPC_STUB IRecordInfo_PutFieldNoCopy_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_GetFieldNames_Proxy( 
		IRecordInfo * This,
		/* [out][in] */ ULONG *pcNames,
		/* [length_is][size_is][out] */ BSTR *rgBstrNames);


	void __RPC_STUB IRecordInfo_GetFieldNames_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	BOOL STDMETHODCALLTYPE IRecordInfo_IsMatchingType_Proxy( 
		IRecordInfo * This,
		/* [in] */ IRecordInfo *pRecordInfo);


	void __RPC_STUB IRecordInfo_IsMatchingType_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	PVOID STDMETHODCALLTYPE IRecordInfo_RecordCreate_Proxy( 
		IRecordInfo * This);


	void __RPC_STUB IRecordInfo_RecordCreate_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_RecordCreateCopy_Proxy( 
		IRecordInfo * This,
		/* [in] */ PVOID pvSource,
		/* [out] */ PVOID *ppvDest);


	void __RPC_STUB IRecordInfo_RecordCreateCopy_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IRecordInfo_RecordDestroy_Proxy( 
		IRecordInfo * This,
		/* [in] */ PVOID pvRecord);


	void __RPC_STUB IRecordInfo_RecordDestroy_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __IRecordInfo_INTERFACE_DEFINED__ */


#ifndef __IErrorLog_INTERFACE_DEFINED__
#define __IErrorLog_INTERFACE_DEFINED__

	/* interface IErrorLog */
	/* [unique][uuid][object] */ 

	typedef IErrorLog *LPERRORLOG;


	EXTERN_C const IID IID_IErrorLog;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("3127CA40-446E-11CE-8135-00AA004BB851")
IErrorLog : public IUnknown
	{
	public:
		virtual HRESULT STDMETHODCALLTYPE AddError( 
			/* [in] */ LPCOLESTR pszPropName,
			/* [in] */ EXCEPINFO *pExcepInfo) = 0;

	};

#else 	/* C style interface */

	typedef struct IErrorLogVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			IErrorLog * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				IErrorLog * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				IErrorLog * This);

			HRESULT ( STDMETHODCALLTYPE *AddError )( 
				IErrorLog * This,
				/* [in] */ LPCOLESTR pszPropName,
				/* [in] */ EXCEPINFO *pExcepInfo);

		END_INTERFACE
	} IErrorLogVtbl;

	interface IErrorLog
	{
		CONST_VTBL struct IErrorLogVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define IErrorLog_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IErrorLog_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define IErrorLog_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define IErrorLog_AddError(This,pszPropName,pExcepInfo)	\
	(This)->lpVtbl -> AddError(This,pszPropName,pExcepInfo)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	HRESULT STDMETHODCALLTYPE IErrorLog_AddError_Proxy( 
		IErrorLog * This,
		/* [in] */ LPCOLESTR pszPropName,
		/* [in] */ EXCEPINFO *pExcepInfo);


	void __RPC_STUB IErrorLog_AddError_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __IErrorLog_INTERFACE_DEFINED__ */


#ifndef __IPropertyBag_INTERFACE_DEFINED__
#define __IPropertyBag_INTERFACE_DEFINED__

	/* interface IPropertyBag */
	/* [unique][uuid][object] */ 

	typedef IPropertyBag *LPPROPERTYBAG;


	EXTERN_C const IID IID_IPropertyBag;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("55272A00-42CB-11CE-8135-00AA004BB851")
IPropertyBag : public IUnknown
	{
	public:
		virtual /* [local] */ HRESULT STDMETHODCALLTYPE Read( 
			/* [in] */ LPCOLESTR pszPropName,
			/* [out][in] */ VARIANT *pVar,
			/* [in] */ IErrorLog *pErrorLog) = 0;

		virtual HRESULT STDMETHODCALLTYPE Write( 
			/* [in] */ LPCOLESTR pszPropName,
			/* [in] */ VARIANT *pVar) = 0;

	};

#else 	/* C style interface */

	typedef struct IPropertyBagVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			IPropertyBag * This,
			/* [in] */ REFIID riid,
			/* [iid_is][out] */ void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				IPropertyBag * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				IPropertyBag * This);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Read )( 
				IPropertyBag * This,
				/* [in] */ LPCOLESTR pszPropName,
				/* [out][in] */ VARIANT *pVar,
				/* [in] */ IErrorLog *pErrorLog);

			HRESULT ( STDMETHODCALLTYPE *Write )( 
				IPropertyBag * This,
				/* [in] */ LPCOLESTR pszPropName,
				/* [in] */ VARIANT *pVar);

		END_INTERFACE
	} IPropertyBagVtbl;

	interface IPropertyBag
	{
		CONST_VTBL struct IPropertyBagVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define IPropertyBag_QueryInterface(This,riid,ppvObject)	\
	(This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IPropertyBag_AddRef(This)	\
	(This)->lpVtbl -> AddRef(This)

#define IPropertyBag_Release(This)	\
	(This)->lpVtbl -> Release(This)


#define IPropertyBag_Read(This,pszPropName,pVar,pErrorLog)	\
	(This)->lpVtbl -> Read(This,pszPropName,pVar,pErrorLog)

#define IPropertyBag_Write(This,pszPropName,pVar)	\
	(This)->lpVtbl -> Write(This,pszPropName,pVar)

#endif /* COBJMACROS */


#endif 	/* C style interface */



	/* [call_as] */ HRESULT STDMETHODCALLTYPE IPropertyBag_RemoteRead_Proxy( 
		IPropertyBag * This,
		/* [in] */ LPCOLESTR pszPropName,
		/* [out] */ VARIANT *pVar,
		/* [in] */ IErrorLog *pErrorLog,
		/* [in] */ DWORD varType,
		/* [in] */ IUnknown *pUnkObj);


	void __RPC_STUB IPropertyBag_RemoteRead_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);


	HRESULT STDMETHODCALLTYPE IPropertyBag_Write_Proxy( 
		IPropertyBag * This,
		/* [in] */ LPCOLESTR pszPropName,
		/* [in] */ VARIANT *pVar);


	void __RPC_STUB IPropertyBag_Write_Stub(
		IRpcStubBuffer *This,
		IRpcChannelBuffer *_pRpcChannelBuffer,
		PRPC_MESSAGE _pRpcMessage,
		DWORD *_pdwStubPhase);



#endif 	/* __IPropertyBag_INTERFACE_DEFINED__ */


	/* interface __MIDL_itf_oaidl_0114 */
	/* [local] */ 

#if ( _MSC_VER >= 800 )
#if _MSC_VER >= 1200
#pragma warning(pop)
#else
#pragma warning(default:4201) /* Nameless struct/union */
#endif
#endif


	extern RPC_IF_HANDLE __MIDL_itf_oaidl_0114_v0_0_c_ifspec;
	extern RPC_IF_HANDLE __MIDL_itf_oaidl_0114_v0_0_s_ifspec;

	/* Additional Prototypes for ALL interfaces */

	unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
	unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
	unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
	void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

	unsigned long             __RPC_USER  CLEANLOCALSTORAGE_UserSize(     unsigned long *, unsigned long            , CLEANLOCALSTORAGE * ); 
	unsigned char * __RPC_USER  CLEANLOCALSTORAGE_UserMarshal(  unsigned long *, unsigned char *, CLEANLOCALSTORAGE * ); 
	unsigned char * __RPC_USER  CLEANLOCALSTORAGE_UserUnmarshal(unsigned long *, unsigned char *, CLEANLOCALSTORAGE * ); 
	void                      __RPC_USER  CLEANLOCALSTORAGE_UserFree(     unsigned long *, CLEANLOCALSTORAGE * ); 

	unsigned long             __RPC_USER  VARIANT_UserSize(     unsigned long *, unsigned long            , VARIANT * ); 
	unsigned char * __RPC_USER  VARIANT_UserMarshal(  unsigned long *, unsigned char *, VARIANT * ); 
	unsigned char * __RPC_USER  VARIANT_UserUnmarshal(unsigned long *, unsigned char *, VARIANT * ); 
	void                      __RPC_USER  VARIANT_UserFree(     unsigned long *, VARIANT * ); 

	/* [local] */ HRESULT STDMETHODCALLTYPE IDispatch_Invoke_Proxy( 
		IDispatch * This,
		/* [in] */ DISPID dispIdMember,
		/* [in] */ REFIID riid,
		/* [in] */ LCID lcid,
		/* [in] */ WORD wFlags,
		/* [out][in] */ DISPPARAMS *pDispParams,
		/* [out] */ VARIANT *pVarResult,
		/* [out] */ EXCEPINFO *pExcepInfo,
		/* [out] */ UINT *puArgErr);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE IDispatch_Invoke_Stub( 
		IDispatch * This,
		/* [in] */ DISPID dispIdMember,
		/* [in] */ REFIID riid,
		/* [in] */ LCID lcid,
		/* [in] */ DWORD dwFlags,
		/* [in] */ DISPPARAMS *pDispParams,
		/* [out] */ VARIANT *pVarResult,
		/* [out] */ EXCEPINFO *pExcepInfo,
		/* [out] */ UINT *pArgErr,
		/* [in] */ UINT cVarRef,
		/* [size_is][in] */ UINT *rgVarRefIdx,
		/* [size_is][out][in] */ VARIANTARG *rgVarRef);

	/* [local] */ HRESULT STDMETHODCALLTYPE IEnumVARIANT_Next_Proxy( 
		IEnumVARIANT * This,
		/* [in] */ ULONG celt,
		/* [length_is][size_is][out] */ VARIANT *rgVar,
		/* [out] */ ULONG *pCeltFetched);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE IEnumVARIANT_Next_Stub( 
		IEnumVARIANT * This,
		/* [in] */ ULONG celt,
		/* [length_is][size_is][out] */ VARIANT *rgVar,
		/* [out] */ ULONG *pCeltFetched);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeComp_Bind_Proxy( 
		ITypeComp * This,
		/* [in] */ LPOLESTR szName,
		/* [in] */ ULONG lHashVal,
		/* [in] */ WORD wFlags,
		/* [out] */ ITypeInfo **ppTInfo,
		/* [out] */ DESCKIND *pDescKind,
		/* [out] */ BINDPTR *pBindPtr);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeComp_Bind_Stub( 
		ITypeComp * This,
		/* [in] */ LPOLESTR szName,
		/* [in] */ ULONG lHashVal,
		/* [in] */ WORD wFlags,
		/* [out] */ ITypeInfo **ppTInfo,
		/* [out] */ DESCKIND *pDescKind,
		/* [out] */ LPFUNCDESC *ppFuncDesc,
		/* [out] */ LPVARDESC *ppVarDesc,
		/* [out] */ ITypeComp **ppTypeComp,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeComp_BindType_Proxy( 
		ITypeComp * This,
		/* [in] */ LPOLESTR szName,
		/* [in] */ ULONG lHashVal,
		/* [out] */ ITypeInfo **ppTInfo,
		/* [out] */ ITypeComp **ppTComp);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeComp_BindType_Stub( 
		ITypeComp * This,
		/* [in] */ LPOLESTR szName,
		/* [in] */ ULONG lHashVal,
		/* [out] */ ITypeInfo **ppTInfo);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetTypeAttr_Proxy( 
		ITypeInfo * This,
		/* [out] */ TYPEATTR **ppTypeAttr);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetTypeAttr_Stub( 
		ITypeInfo * This,
		/* [out] */ LPTYPEATTR *ppTypeAttr,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetFuncDesc_Proxy( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ FUNCDESC **ppFuncDesc);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetFuncDesc_Stub( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ LPFUNCDESC *ppFuncDesc,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetVarDesc_Proxy( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ VARDESC **ppVarDesc);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetVarDesc_Stub( 
		ITypeInfo * This,
		/* [in] */ UINT index,
		/* [out] */ LPVARDESC *ppVarDesc,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetNames_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [length_is][size_is][out] */ BSTR *rgBstrNames,
		/* [in] */ UINT cMaxNames,
		/* [out] */ UINT *pcNames);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetNames_Stub( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [length_is][size_is][out] */ BSTR *rgBstrNames,
		/* [in] */ UINT cMaxNames,
		/* [out] */ UINT *pcNames);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetIDsOfNames_Proxy( 
		ITypeInfo * This,
		/* [size_is][in] */ LPOLESTR *rgszNames,
		/* [in] */ UINT cNames,
		/* [size_is][out] */ MEMBERID *pMemId);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetIDsOfNames_Stub( 
		ITypeInfo * This);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_Invoke_Proxy( 
		ITypeInfo * This,
		/* [in] */ PVOID pvInstance,
		/* [in] */ MEMBERID memid,
		/* [in] */ WORD wFlags,
		/* [out][in] */ DISPPARAMS *pDispParams,
		/* [out] */ VARIANT *pVarResult,
		/* [out] */ EXCEPINFO *pExcepInfo,
		/* [out] */ UINT *puArgErr);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_Invoke_Stub( 
		ITypeInfo * This);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetDocumentation_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ BSTR *pBstrDocString,
		/* [out] */ DWORD *pdwHelpContext,
		/* [out] */ BSTR *pBstrHelpFile);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetDocumentation_Stub( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ BSTR *pBstrDocString,
		/* [out] */ DWORD *pdwHelpContext,
		/* [out] */ BSTR *pBstrHelpFile);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetDllEntry_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ INVOKEKIND invKind,
		/* [out] */ BSTR *pBstrDllName,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ WORD *pwOrdinal);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetDllEntry_Stub( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ INVOKEKIND invKind,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pBstrDllName,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ WORD *pwOrdinal);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_AddressOfMember_Proxy( 
		ITypeInfo * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ INVOKEKIND invKind,
		/* [out] */ PVOID *ppv);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_AddressOfMember_Stub( 
		ITypeInfo * This);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_CreateInstance_Proxy( 
		ITypeInfo * This,
		/* [in] */ IUnknown *pUnkOuter,
		/* [in] */ REFIID riid,
		/* [iid_is][out] */ PVOID *ppvObj);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_CreateInstance_Stub( 
		ITypeInfo * This,
		/* [in] */ REFIID riid,
		/* [iid_is][out] */ IUnknown **ppvObj);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetContainingTypeLib_Proxy( 
		ITypeInfo * This,
		/* [out] */ ITypeLib **ppTLib,
		/* [out] */ UINT *pIndex);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_GetContainingTypeLib_Stub( 
		ITypeInfo * This,
		/* [out] */ ITypeLib **ppTLib,
		/* [out] */ UINT *pIndex);

	/* [local] */ void STDMETHODCALLTYPE ITypeInfo_ReleaseTypeAttr_Proxy( 
		ITypeInfo * This,
		/* [in] */ TYPEATTR *pTypeAttr);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_ReleaseTypeAttr_Stub( 
		ITypeInfo * This);

	/* [local] */ void STDMETHODCALLTYPE ITypeInfo_ReleaseFuncDesc_Proxy( 
		ITypeInfo * This,
		/* [in] */ FUNCDESC *pFuncDesc);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_ReleaseFuncDesc_Stub( 
		ITypeInfo * This);

	/* [local] */ void STDMETHODCALLTYPE ITypeInfo_ReleaseVarDesc_Proxy( 
		ITypeInfo * This,
		/* [in] */ VARDESC *pVarDesc);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo_ReleaseVarDesc_Stub( 
		ITypeInfo * This);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeInfo2_GetDocumentation2_Proxy( 
		ITypeInfo2 * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ LCID lcid,
		/* [out] */ BSTR *pbstrHelpString,
		/* [out] */ DWORD *pdwHelpStringContext,
		/* [out] */ BSTR *pbstrHelpStringDll);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeInfo2_GetDocumentation2_Stub( 
		ITypeInfo2 * This,
		/* [in] */ MEMBERID memid,
		/* [in] */ LCID lcid,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pbstrHelpString,
		/* [out] */ DWORD *pdwHelpStringContext,
		/* [out] */ BSTR *pbstrHelpStringDll);

	/* [local] */ UINT STDMETHODCALLTYPE ITypeLib_GetTypeInfoCount_Proxy( 
		ITypeLib * This);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_GetTypeInfoCount_Stub( 
		ITypeLib * This,
		/* [out] */ UINT *pcTInfo);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeLib_GetLibAttr_Proxy( 
		ITypeLib * This,
		/* [out] */ TLIBATTR **ppTLibAttr);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_GetLibAttr_Stub( 
		ITypeLib * This,
		/* [out] */ LPTLIBATTR *ppTLibAttr,
		/* [out] */ CLEANLOCALSTORAGE *pDummy);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeLib_GetDocumentation_Proxy( 
		ITypeLib * This,
		/* [in] */ INT index,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ BSTR *pBstrDocString,
		/* [out] */ DWORD *pdwHelpContext,
		/* [out] */ BSTR *pBstrHelpFile);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_GetDocumentation_Stub( 
		ITypeLib * This,
		/* [in] */ INT index,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pBstrName,
		/* [out] */ BSTR *pBstrDocString,
		/* [out] */ DWORD *pdwHelpContext,
		/* [out] */ BSTR *pBstrHelpFile);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeLib_IsName_Proxy( 
		ITypeLib * This,
		/* [out][in] */ LPOLESTR szNameBuf,
		/* [in] */ ULONG lHashVal,
		/* [out] */ BOOL *pfName);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_IsName_Stub( 
		ITypeLib * This,
		/* [in] */ LPOLESTR szNameBuf,
		/* [in] */ ULONG lHashVal,
		/* [out] */ BOOL *pfName,
		/* [out] */ BSTR *pBstrLibName);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeLib_FindName_Proxy( 
		ITypeLib * This,
		/* [out][in] */ LPOLESTR szNameBuf,
		/* [in] */ ULONG lHashVal,
		/* [length_is][size_is][out] */ ITypeInfo **ppTInfo,
		/* [length_is][size_is][out] */ MEMBERID *rgMemId,
		/* [out][in] */ USHORT *pcFound);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_FindName_Stub( 
		ITypeLib * This,
		/* [in] */ LPOLESTR szNameBuf,
		/* [in] */ ULONG lHashVal,
		/* [length_is][size_is][out] */ ITypeInfo **ppTInfo,
		/* [length_is][size_is][out] */ MEMBERID *rgMemId,
		/* [out][in] */ USHORT *pcFound,
		/* [out] */ BSTR *pBstrLibName);

	/* [local] */ void STDMETHODCALLTYPE ITypeLib_ReleaseTLibAttr_Proxy( 
		ITypeLib * This,
		/* [in] */ TLIBATTR *pTLibAttr);


	/* [nocode][call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib_ReleaseTLibAttr_Stub( 
		ITypeLib * This);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeLib2_GetLibStatistics_Proxy( 
		ITypeLib2 * This,
		/* [out] */ ULONG *pcUniqueNames,
		/* [out] */ ULONG *pcchUniqueNames);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib2_GetLibStatistics_Stub( 
		ITypeLib2 * This,
		/* [out] */ ULONG *pcUniqueNames,
		/* [out] */ ULONG *pcchUniqueNames);

	/* [local] */ HRESULT STDMETHODCALLTYPE ITypeLib2_GetDocumentation2_Proxy( 
		ITypeLib2 * This,
		/* [in] */ INT index,
		/* [in] */ LCID lcid,
		/* [out] */ BSTR *pbstrHelpString,
		/* [out] */ DWORD *pdwHelpStringContext,
		/* [out] */ BSTR *pbstrHelpStringDll);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE ITypeLib2_GetDocumentation2_Stub( 
		ITypeLib2 * This,
		/* [in] */ INT index,
		/* [in] */ LCID lcid,
		/* [in] */ DWORD refPtrFlags,
		/* [out] */ BSTR *pbstrHelpString,
		/* [out] */ DWORD *pdwHelpStringContext,
		/* [out] */ BSTR *pbstrHelpStringDll);

	/* [local] */ HRESULT STDMETHODCALLTYPE IPropertyBag_Read_Proxy( 
		IPropertyBag * This,
		/* [in] */ LPCOLESTR pszPropName,
		/* [out][in] */ VARIANT *pVar,
		/* [in] */ IErrorLog *pErrorLog);


	/* [call_as] */ HRESULT STDMETHODCALLTYPE IPropertyBag_Read_Stub( 
		IPropertyBag * This,
		/* [in] */ LPCOLESTR pszPropName,
		/* [out] */ VARIANT *pVar,
		/* [in] */ IErrorLog *pErrorLog,
		/* [in] */ DWORD varType,
		/* [in] */ IUnknown *pUnkObj);



	/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif



//end
//#include "oaidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

#ifndef __INetFwRemoteAdminSettings_INTERFACE_DEFINED__
#define __INetFwRemoteAdminSettings_INTERFACE_DEFINED__

	/* interface INetFwRemoteAdminSettings */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwRemoteAdminSettings;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("D4BECDDF-6F73-4A83-B832-9C66874CD20E")
INetFwRemoteAdminSettings : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_IpVersion( 
			/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_IpVersion( 
			/* [in] */ NET_FW_IP_VERSION ipVersion) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Scope( 
			/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Scope( 
			/* [in] */ NET_FW_SCOPE scope) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RemoteAddresses( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_RemoteAddresses( 
			/* [in] */ __RPC__in BSTR remoteAddrs) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Enabled( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Enabled( 
			/* [in] */ VARIANT_BOOL enabled) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwRemoteAdminSettingsVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwRemoteAdminSettings * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwRemoteAdminSettings * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwRemoteAdminSettings * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwRemoteAdminSettings * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IpVersion )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_IpVersion )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [in] */ NET_FW_IP_VERSION ipVersion);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Scope )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Scope )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [in] */ NET_FW_SCOPE scope);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemoteAddresses )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemoteAddresses )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [in] */ __RPC__in BSTR remoteAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Enabled )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Enabled )( 
				__RPC__in INetFwRemoteAdminSettings * This,
				/* [in] */ VARIANT_BOOL enabled);

		END_INTERFACE
	} INetFwRemoteAdminSettingsVtbl;

	interface INetFwRemoteAdminSettings
	{
		CONST_VTBL struct INetFwRemoteAdminSettingsVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwRemoteAdminSettings_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwRemoteAdminSettings_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwRemoteAdminSettings_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwRemoteAdminSettings_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwRemoteAdminSettings_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwRemoteAdminSettings_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwRemoteAdminSettings_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwRemoteAdminSettings_get_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> get_IpVersion(This,ipVersion) ) 

#define INetFwRemoteAdminSettings_put_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> put_IpVersion(This,ipVersion) ) 

#define INetFwRemoteAdminSettings_get_Scope(This,scope)	\
	( (This)->lpVtbl -> get_Scope(This,scope) ) 

#define INetFwRemoteAdminSettings_put_Scope(This,scope)	\
	( (This)->lpVtbl -> put_Scope(This,scope) ) 

#define INetFwRemoteAdminSettings_get_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> get_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwRemoteAdminSettings_put_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> put_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwRemoteAdminSettings_get_Enabled(This,enabled)	\
	( (This)->lpVtbl -> get_Enabled(This,enabled) ) 

#define INetFwRemoteAdminSettings_put_Enabled(This,enabled)	\
	( (This)->lpVtbl -> put_Enabled(This,enabled) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwRemoteAdminSettings_INTERFACE_DEFINED__ */


#ifndef __INetFwIcmpSettings_INTERFACE_DEFINED__
#define __INetFwIcmpSettings_INTERFACE_DEFINED__

	/* interface INetFwIcmpSettings */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwIcmpSettings;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("A6207B2E-7CDD-426A-951E-5E1CBC5AFEAD")
INetFwIcmpSettings : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowOutboundDestinationUnreachable( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowOutboundDestinationUnreachable( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowRedirect( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowRedirect( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowInboundEchoRequest( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowInboundEchoRequest( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowOutboundTimeExceeded( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowOutboundTimeExceeded( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowOutboundParameterProblem( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowOutboundParameterProblem( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowOutboundSourceQuench( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowOutboundSourceQuench( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowInboundRouterRequest( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowInboundRouterRequest( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowInboundTimestampRequest( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowInboundTimestampRequest( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowInboundMaskRequest( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowInboundMaskRequest( 
			/* [in] */ VARIANT_BOOL allow) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AllowOutboundPacketTooBig( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *allow) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_AllowOutboundPacketTooBig( 
			/* [in] */ VARIANT_BOOL allow) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwIcmpSettingsVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwIcmpSettings * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwIcmpSettings * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwIcmpSettings * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwIcmpSettings * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowOutboundDestinationUnreachable )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowOutboundDestinationUnreachable )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowRedirect )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowRedirect )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowInboundEchoRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowInboundEchoRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowOutboundTimeExceeded )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowOutboundTimeExceeded )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowOutboundParameterProblem )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowOutboundParameterProblem )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowOutboundSourceQuench )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowOutboundSourceQuench )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowInboundRouterRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowInboundRouterRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowInboundTimestampRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowInboundTimestampRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowInboundMaskRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowInboundMaskRequest )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AllowOutboundPacketTooBig )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *allow);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_AllowOutboundPacketTooBig )( 
				__RPC__in INetFwIcmpSettings * This,
				/* [in] */ VARIANT_BOOL allow);

		END_INTERFACE
	} INetFwIcmpSettingsVtbl;

	interface INetFwIcmpSettings
	{
		CONST_VTBL struct INetFwIcmpSettingsVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwIcmpSettings_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwIcmpSettings_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwIcmpSettings_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwIcmpSettings_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwIcmpSettings_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwIcmpSettings_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwIcmpSettings_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwIcmpSettings_get_AllowOutboundDestinationUnreachable(This,allow)	\
	( (This)->lpVtbl -> get_AllowOutboundDestinationUnreachable(This,allow) ) 

#define INetFwIcmpSettings_put_AllowOutboundDestinationUnreachable(This,allow)	\
	( (This)->lpVtbl -> put_AllowOutboundDestinationUnreachable(This,allow) ) 

#define INetFwIcmpSettings_get_AllowRedirect(This,allow)	\
	( (This)->lpVtbl -> get_AllowRedirect(This,allow) ) 

#define INetFwIcmpSettings_put_AllowRedirect(This,allow)	\
	( (This)->lpVtbl -> put_AllowRedirect(This,allow) ) 

#define INetFwIcmpSettings_get_AllowInboundEchoRequest(This,allow)	\
	( (This)->lpVtbl -> get_AllowInboundEchoRequest(This,allow) ) 

#define INetFwIcmpSettings_put_AllowInboundEchoRequest(This,allow)	\
	( (This)->lpVtbl -> put_AllowInboundEchoRequest(This,allow) ) 

#define INetFwIcmpSettings_get_AllowOutboundTimeExceeded(This,allow)	\
	( (This)->lpVtbl -> get_AllowOutboundTimeExceeded(This,allow) ) 

#define INetFwIcmpSettings_put_AllowOutboundTimeExceeded(This,allow)	\
	( (This)->lpVtbl -> put_AllowOutboundTimeExceeded(This,allow) ) 

#define INetFwIcmpSettings_get_AllowOutboundParameterProblem(This,allow)	\
	( (This)->lpVtbl -> get_AllowOutboundParameterProblem(This,allow) ) 

#define INetFwIcmpSettings_put_AllowOutboundParameterProblem(This,allow)	\
	( (This)->lpVtbl -> put_AllowOutboundParameterProblem(This,allow) ) 

#define INetFwIcmpSettings_get_AllowOutboundSourceQuench(This,allow)	\
	( (This)->lpVtbl -> get_AllowOutboundSourceQuench(This,allow) ) 

#define INetFwIcmpSettings_put_AllowOutboundSourceQuench(This,allow)	\
	( (This)->lpVtbl -> put_AllowOutboundSourceQuench(This,allow) ) 

#define INetFwIcmpSettings_get_AllowInboundRouterRequest(This,allow)	\
	( (This)->lpVtbl -> get_AllowInboundRouterRequest(This,allow) ) 

#define INetFwIcmpSettings_put_AllowInboundRouterRequest(This,allow)	\
	( (This)->lpVtbl -> put_AllowInboundRouterRequest(This,allow) ) 

#define INetFwIcmpSettings_get_AllowInboundTimestampRequest(This,allow)	\
	( (This)->lpVtbl -> get_AllowInboundTimestampRequest(This,allow) ) 

#define INetFwIcmpSettings_put_AllowInboundTimestampRequest(This,allow)	\
	( (This)->lpVtbl -> put_AllowInboundTimestampRequest(This,allow) ) 

#define INetFwIcmpSettings_get_AllowInboundMaskRequest(This,allow)	\
	( (This)->lpVtbl -> get_AllowInboundMaskRequest(This,allow) ) 

#define INetFwIcmpSettings_put_AllowInboundMaskRequest(This,allow)	\
	( (This)->lpVtbl -> put_AllowInboundMaskRequest(This,allow) ) 

#define INetFwIcmpSettings_get_AllowOutboundPacketTooBig(This,allow)	\
	( (This)->lpVtbl -> get_AllowOutboundPacketTooBig(This,allow) ) 

#define INetFwIcmpSettings_put_AllowOutboundPacketTooBig(This,allow)	\
	( (This)->lpVtbl -> put_AllowOutboundPacketTooBig(This,allow) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwIcmpSettings_INTERFACE_DEFINED__ */


#ifndef __INetFwOpenPort_INTERFACE_DEFINED__
#define __INetFwOpenPort_INTERFACE_DEFINED__

	/* interface INetFwOpenPort */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwOpenPort;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("E0483BA0-47FF-4D9C-A6D6-7741D0B195F7")
INetFwOpenPort : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Name( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *name) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Name( 
			/* [in] */ __RPC__in BSTR name) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_IpVersion( 
			/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_IpVersion( 
			/* [in] */ NET_FW_IP_VERSION ipVersion) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Protocol( 
			/* [retval][out] */ __RPC__out NET_FW_IP_PROTOCOL *ipProtocol) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Protocol( 
			/* [in] */ NET_FW_IP_PROTOCOL ipProtocol) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Port( 
			/* [retval][out] */ __RPC__out LONG *portNumber) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Port( 
			/* [in] */ LONG portNumber) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Scope( 
			/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Scope( 
			/* [in] */ NET_FW_SCOPE scope) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RemoteAddresses( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_RemoteAddresses( 
			/* [in] */ __RPC__in BSTR remoteAddrs) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Enabled( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Enabled( 
			/* [in] */ VARIANT_BOOL enabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_BuiltIn( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *builtIn) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwOpenPortVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwOpenPort * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwOpenPort * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwOpenPort * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwOpenPort * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwOpenPort * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Name )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *name);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Name )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ __RPC__in BSTR name);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IpVersion )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_IpVersion )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ NET_FW_IP_VERSION ipVersion);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Protocol )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__out NET_FW_IP_PROTOCOL *ipProtocol);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Protocol )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ NET_FW_IP_PROTOCOL ipProtocol);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Port )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__out LONG *portNumber);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Port )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ LONG portNumber);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Scope )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Scope )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ NET_FW_SCOPE scope);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemoteAddresses )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemoteAddresses )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ __RPC__in BSTR remoteAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Enabled )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Enabled )( 
				__RPC__in INetFwOpenPort * This,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_BuiltIn )( 
				__RPC__in INetFwOpenPort * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *builtIn);

		END_INTERFACE
	} INetFwOpenPortVtbl;

	interface INetFwOpenPort
	{
		CONST_VTBL struct INetFwOpenPortVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwOpenPort_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwOpenPort_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwOpenPort_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwOpenPort_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwOpenPort_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwOpenPort_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwOpenPort_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwOpenPort_get_Name(This,name)	\
	( (This)->lpVtbl -> get_Name(This,name) ) 

#define INetFwOpenPort_put_Name(This,name)	\
	( (This)->lpVtbl -> put_Name(This,name) ) 

#define INetFwOpenPort_get_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> get_IpVersion(This,ipVersion) ) 

#define INetFwOpenPort_put_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> put_IpVersion(This,ipVersion) ) 

#define INetFwOpenPort_get_Protocol(This,ipProtocol)	\
	( (This)->lpVtbl -> get_Protocol(This,ipProtocol) ) 

#define INetFwOpenPort_put_Protocol(This,ipProtocol)	\
	( (This)->lpVtbl -> put_Protocol(This,ipProtocol) ) 

#define INetFwOpenPort_get_Port(This,portNumber)	\
	( (This)->lpVtbl -> get_Port(This,portNumber) ) 

#define INetFwOpenPort_put_Port(This,portNumber)	\
	( (This)->lpVtbl -> put_Port(This,portNumber) ) 

#define INetFwOpenPort_get_Scope(This,scope)	\
	( (This)->lpVtbl -> get_Scope(This,scope) ) 

#define INetFwOpenPort_put_Scope(This,scope)	\
	( (This)->lpVtbl -> put_Scope(This,scope) ) 

#define INetFwOpenPort_get_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> get_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwOpenPort_put_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> put_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwOpenPort_get_Enabled(This,enabled)	\
	( (This)->lpVtbl -> get_Enabled(This,enabled) ) 

#define INetFwOpenPort_put_Enabled(This,enabled)	\
	( (This)->lpVtbl -> put_Enabled(This,enabled) ) 

#define INetFwOpenPort_get_BuiltIn(This,builtIn)	\
	( (This)->lpVtbl -> get_BuiltIn(This,builtIn) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwOpenPort_INTERFACE_DEFINED__ */


#ifndef __INetFwOpenPorts_INTERFACE_DEFINED__
#define __INetFwOpenPorts_INTERFACE_DEFINED__

	/* interface INetFwOpenPorts */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwOpenPorts;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("C0E9D7FA-E07E-430A-B19A-090CE82D92E2")
INetFwOpenPorts : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Count( 
			/* [retval][out] */ __RPC__out long *count) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Add( 
			/* [in] */ __RPC__in_opt INetFwOpenPort *port) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Remove( 
			/* [in] */ LONG portNumber,
			/* [in] */ NET_FW_IP_PROTOCOL ipProtocol) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Item( 
			/* [in] */ LONG portNumber,
			/* [in] */ NET_FW_IP_PROTOCOL ipProtocol,
			/* [retval][out] */ __RPC__deref_out_opt INetFwOpenPort **openPort) = 0;

		virtual /* [restricted][propget][id] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
			/* [retval][out] */ __RPC__deref_out_opt IUnknown **newEnum) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwOpenPortsVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwOpenPorts * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwOpenPorts * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwOpenPorts * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwOpenPorts * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwOpenPorts * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwOpenPorts * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwOpenPorts * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
				__RPC__in INetFwOpenPorts * This,
				/* [retval][out] */ __RPC__out long *count);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
				__RPC__in INetFwOpenPorts * This,
				/* [in] */ __RPC__in_opt INetFwOpenPort *port);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
				__RPC__in INetFwOpenPorts * This,
				/* [in] */ LONG portNumber,
				/* [in] */ NET_FW_IP_PROTOCOL ipProtocol);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
				__RPC__in INetFwOpenPorts * This,
				/* [in] */ LONG portNumber,
				/* [in] */ NET_FW_IP_PROTOCOL ipProtocol,
				/* [retval][out] */ __RPC__deref_out_opt INetFwOpenPort **openPort);

			/* [restricted][propget][id] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
				__RPC__in INetFwOpenPorts * This,
				/* [retval][out] */ __RPC__deref_out_opt IUnknown **newEnum);

		END_INTERFACE
	} INetFwOpenPortsVtbl;

	interface INetFwOpenPorts
	{
		CONST_VTBL struct INetFwOpenPortsVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwOpenPorts_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwOpenPorts_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwOpenPorts_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwOpenPorts_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwOpenPorts_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwOpenPorts_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwOpenPorts_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwOpenPorts_get_Count(This,count)	\
	( (This)->lpVtbl -> get_Count(This,count) ) 

#define INetFwOpenPorts_Add(This,port)	\
	( (This)->lpVtbl -> Add(This,port) ) 

#define INetFwOpenPorts_Remove(This,portNumber,ipProtocol)	\
	( (This)->lpVtbl -> Remove(This,portNumber,ipProtocol) ) 

#define INetFwOpenPorts_Item(This,portNumber,ipProtocol,openPort)	\
	( (This)->lpVtbl -> Item(This,portNumber,ipProtocol,openPort) ) 

#define INetFwOpenPorts_get__NewEnum(This,newEnum)	\
	( (This)->lpVtbl -> get__NewEnum(This,newEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwOpenPorts_INTERFACE_DEFINED__ */


#ifndef __INetFwService_INTERFACE_DEFINED__
#define __INetFwService_INTERFACE_DEFINED__

	/* interface INetFwService */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwService;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("79FD57C8-908E-4A36-9888-D5B3F0A444CF")
INetFwService : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Name( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *name) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Type( 
			/* [retval][out] */ __RPC__out NET_FW_SERVICE_TYPE *type) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Customized( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *customized) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_IpVersion( 
			/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_IpVersion( 
			/* [in] */ NET_FW_IP_VERSION ipVersion) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Scope( 
			/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Scope( 
			/* [in] */ NET_FW_SCOPE scope) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RemoteAddresses( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_RemoteAddresses( 
			/* [in] */ __RPC__in BSTR remoteAddrs) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Enabled( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Enabled( 
			/* [in] */ VARIANT_BOOL enabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_GloballyOpenPorts( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwOpenPorts **openPorts) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwServiceVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwService * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwService * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwService * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwService * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwService * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwService * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwService * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Name )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *name);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Type )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__out NET_FW_SERVICE_TYPE *type);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Customized )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *customized);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IpVersion )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_IpVersion )( 
				__RPC__in INetFwService * This,
				/* [in] */ NET_FW_IP_VERSION ipVersion);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Scope )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Scope )( 
				__RPC__in INetFwService * This,
				/* [in] */ NET_FW_SCOPE scope);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemoteAddresses )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemoteAddresses )( 
				__RPC__in INetFwService * This,
				/* [in] */ __RPC__in BSTR remoteAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Enabled )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Enabled )( 
				__RPC__in INetFwService * This,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_GloballyOpenPorts )( 
				__RPC__in INetFwService * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwOpenPorts **openPorts);

		END_INTERFACE
	} INetFwServiceVtbl;

	interface INetFwService
	{
		CONST_VTBL struct INetFwServiceVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwService_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwService_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwService_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwService_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwService_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwService_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwService_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwService_get_Name(This,name)	\
	( (This)->lpVtbl -> get_Name(This,name) ) 

#define INetFwService_get_Type(This,type)	\
	( (This)->lpVtbl -> get_Type(This,type) ) 

#define INetFwService_get_Customized(This,customized)	\
	( (This)->lpVtbl -> get_Customized(This,customized) ) 

#define INetFwService_get_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> get_IpVersion(This,ipVersion) ) 

#define INetFwService_put_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> put_IpVersion(This,ipVersion) ) 

#define INetFwService_get_Scope(This,scope)	\
	( (This)->lpVtbl -> get_Scope(This,scope) ) 

#define INetFwService_put_Scope(This,scope)	\
	( (This)->lpVtbl -> put_Scope(This,scope) ) 

#define INetFwService_get_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> get_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwService_put_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> put_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwService_get_Enabled(This,enabled)	\
	( (This)->lpVtbl -> get_Enabled(This,enabled) ) 

#define INetFwService_put_Enabled(This,enabled)	\
	( (This)->lpVtbl -> put_Enabled(This,enabled) ) 

#define INetFwService_get_GloballyOpenPorts(This,openPorts)	\
	( (This)->lpVtbl -> get_GloballyOpenPorts(This,openPorts) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwService_INTERFACE_DEFINED__ */


#ifndef __INetFwServices_INTERFACE_DEFINED__
#define __INetFwServices_INTERFACE_DEFINED__

	/* interface INetFwServices */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwServices;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("79649BB4-903E-421B-94C9-79848E79F6EE")
INetFwServices : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Count( 
			/* [retval][out] */ __RPC__out long *count) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Item( 
			/* [in] */ NET_FW_SERVICE_TYPE svcType,
			/* [retval][out] */ __RPC__deref_out_opt INetFwService **service) = 0;

		virtual /* [restricted][propget][id] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
			/* [retval][out] */ __RPC__deref_out_opt IUnknown **newEnum) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwServicesVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwServices * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwServices * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwServices * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwServices * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwServices * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwServices * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwServices * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
				__RPC__in INetFwServices * This,
				/* [retval][out] */ __RPC__out long *count);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
				__RPC__in INetFwServices * This,
				/* [in] */ NET_FW_SERVICE_TYPE svcType,
				/* [retval][out] */ __RPC__deref_out_opt INetFwService **service);

			/* [restricted][propget][id] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
				__RPC__in INetFwServices * This,
				/* [retval][out] */ __RPC__deref_out_opt IUnknown **newEnum);

		END_INTERFACE
	} INetFwServicesVtbl;

	interface INetFwServices
	{
		CONST_VTBL struct INetFwServicesVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwServices_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwServices_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwServices_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwServices_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwServices_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwServices_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwServices_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwServices_get_Count(This,count)	\
	( (This)->lpVtbl -> get_Count(This,count) ) 

#define INetFwServices_Item(This,svcType,service)	\
	( (This)->lpVtbl -> Item(This,svcType,service) ) 

#define INetFwServices_get__NewEnum(This,newEnum)	\
	( (This)->lpVtbl -> get__NewEnum(This,newEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwServices_INTERFACE_DEFINED__ */


#ifndef __INetFwAuthorizedApplication_INTERFACE_DEFINED__
#define __INetFwAuthorizedApplication_INTERFACE_DEFINED__

	/* interface INetFwAuthorizedApplication */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwAuthorizedApplication;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("B5E64FFA-C2C5-444E-A301-FB5E00018050")
INetFwAuthorizedApplication : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Name( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *name) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Name( 
			/* [in] */ __RPC__in BSTR name) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ProcessImageFileName( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *imageFileName) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_ProcessImageFileName( 
			/* [in] */ __RPC__in BSTR imageFileName) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_IpVersion( 
			/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_IpVersion( 
			/* [in] */ NET_FW_IP_VERSION ipVersion) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Scope( 
			/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Scope( 
			/* [in] */ NET_FW_SCOPE scope) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RemoteAddresses( 
			/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_RemoteAddresses( 
			/* [in] */ __RPC__in BSTR remoteAddrs) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Enabled( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Enabled( 
			/* [in] */ VARIANT_BOOL enabled) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwAuthorizedApplicationVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwAuthorizedApplication * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwAuthorizedApplication * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwAuthorizedApplication * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwAuthorizedApplication * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Name )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *name);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Name )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ __RPC__in BSTR name);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ProcessImageFileName )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *imageFileName);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_ProcessImageFileName )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ __RPC__in BSTR imageFileName);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IpVersion )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [retval][out] */ __RPC__out NET_FW_IP_VERSION *ipVersion);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_IpVersion )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ NET_FW_IP_VERSION ipVersion);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Scope )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [retval][out] */ __RPC__out NET_FW_SCOPE *scope);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Scope )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ NET_FW_SCOPE scope);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemoteAddresses )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [retval][out] */ __RPC__deref_out_opt BSTR *remoteAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemoteAddresses )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ __RPC__in BSTR remoteAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Enabled )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Enabled )( 
				__RPC__in INetFwAuthorizedApplication * This,
				/* [in] */ VARIANT_BOOL enabled);

		END_INTERFACE
	} INetFwAuthorizedApplicationVtbl;

	interface INetFwAuthorizedApplication
	{
		CONST_VTBL struct INetFwAuthorizedApplicationVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwAuthorizedApplication_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwAuthorizedApplication_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwAuthorizedApplication_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwAuthorizedApplication_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwAuthorizedApplication_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwAuthorizedApplication_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwAuthorizedApplication_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwAuthorizedApplication_get_Name(This,name)	\
	( (This)->lpVtbl -> get_Name(This,name) ) 

#define INetFwAuthorizedApplication_put_Name(This,name)	\
	( (This)->lpVtbl -> put_Name(This,name) ) 

#define INetFwAuthorizedApplication_get_ProcessImageFileName(This,imageFileName)	\
	( (This)->lpVtbl -> get_ProcessImageFileName(This,imageFileName) ) 

#define INetFwAuthorizedApplication_put_ProcessImageFileName(This,imageFileName)	\
	( (This)->lpVtbl -> put_ProcessImageFileName(This,imageFileName) ) 

#define INetFwAuthorizedApplication_get_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> get_IpVersion(This,ipVersion) ) 

#define INetFwAuthorizedApplication_put_IpVersion(This,ipVersion)	\
	( (This)->lpVtbl -> put_IpVersion(This,ipVersion) ) 

#define INetFwAuthorizedApplication_get_Scope(This,scope)	\
	( (This)->lpVtbl -> get_Scope(This,scope) ) 

#define INetFwAuthorizedApplication_put_Scope(This,scope)	\
	( (This)->lpVtbl -> put_Scope(This,scope) ) 

#define INetFwAuthorizedApplication_get_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> get_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwAuthorizedApplication_put_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> put_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwAuthorizedApplication_get_Enabled(This,enabled)	\
	( (This)->lpVtbl -> get_Enabled(This,enabled) ) 

#define INetFwAuthorizedApplication_put_Enabled(This,enabled)	\
	( (This)->lpVtbl -> put_Enabled(This,enabled) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwAuthorizedApplication_INTERFACE_DEFINED__ */


#ifndef __INetFwAuthorizedApplications_INTERFACE_DEFINED__
#define __INetFwAuthorizedApplications_INTERFACE_DEFINED__

	/* interface INetFwAuthorizedApplications */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwAuthorizedApplications;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("644EFD52-CCF9-486C-97A2-39F352570B30")
INetFwAuthorizedApplications : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Count( 
			/* [retval][out] */ __RPC__out long *count) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Add( 
			/* [in] */ __RPC__in_opt INetFwAuthorizedApplication *app) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Remove( 
			/* [in] */ __RPC__in BSTR imageFileName) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Item( 
			/* [in] */ __RPC__in BSTR imageFileName,
			/* [retval][out] */ __RPC__deref_out_opt INetFwAuthorizedApplication **app) = 0;

		virtual /* [restricted][propget][id] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
			/* [retval][out] */ __RPC__deref_out_opt IUnknown **newEnum) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwAuthorizedApplicationsVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwAuthorizedApplications * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwAuthorizedApplications * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwAuthorizedApplications * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwAuthorizedApplications * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [retval][out] */ __RPC__out long *count);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [in] */ __RPC__in_opt INetFwAuthorizedApplication *app);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [in] */ __RPC__in BSTR imageFileName);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [in] */ __RPC__in BSTR imageFileName,
				/* [retval][out] */ __RPC__deref_out_opt INetFwAuthorizedApplication **app);

			/* [restricted][propget][id] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
				__RPC__in INetFwAuthorizedApplications * This,
				/* [retval][out] */ __RPC__deref_out_opt IUnknown **newEnum);

		END_INTERFACE
	} INetFwAuthorizedApplicationsVtbl;

	interface INetFwAuthorizedApplications
	{
		CONST_VTBL struct INetFwAuthorizedApplicationsVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwAuthorizedApplications_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwAuthorizedApplications_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwAuthorizedApplications_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwAuthorizedApplications_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwAuthorizedApplications_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwAuthorizedApplications_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwAuthorizedApplications_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwAuthorizedApplications_get_Count(This,count)	\
	( (This)->lpVtbl -> get_Count(This,count) ) 

#define INetFwAuthorizedApplications_Add(This,app)	\
	( (This)->lpVtbl -> Add(This,app) ) 

#define INetFwAuthorizedApplications_Remove(This,imageFileName)	\
	( (This)->lpVtbl -> Remove(This,imageFileName) ) 

#define INetFwAuthorizedApplications_Item(This,imageFileName,app)	\
	( (This)->lpVtbl -> Item(This,imageFileName,app) ) 

#define INetFwAuthorizedApplications_get__NewEnum(This,newEnum)	\
	( (This)->lpVtbl -> get__NewEnum(This,newEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwAuthorizedApplications_INTERFACE_DEFINED__ */


#ifndef __INetFwRule_INTERFACE_DEFINED__
#define __INetFwRule_INTERFACE_DEFINED__

	/* interface INetFwRule */
	/* [dual][uuid][object][local] */ 


	EXTERN_C const IID IID_INetFwRule;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("AF230D27-BABA-4E42-ACED-F524F22CFCE2")
INetFwRule : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Name( 
			/* [retval][out] */ BSTR *name) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Name( 
			/* [in] */ BSTR name) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Description( 
			/* [retval][out] */ BSTR *desc) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Description( 
			/* [in] */ BSTR desc) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ApplicationName( 
			/* [retval][out] */ BSTR *imageFileName) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_ApplicationName( 
			/* [in] */ BSTR imageFileName) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ServiceName( 
			/* [retval][out] */ BSTR *serviceName) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_ServiceName( 
			/* [in] */ BSTR serviceName) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Protocol( 
			/* [retval][out] */ LONG *protocol) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Protocol( 
			/* [in] */ LONG protocol) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_LocalPorts( 
			/* [retval][out] */ BSTR *portNumbers) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_LocalPorts( 
			/* [in] */ BSTR portNumbers) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RemotePorts( 
			/* [retval][out] */ BSTR *portNumbers) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_RemotePorts( 
			/* [in] */ BSTR portNumbers) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_LocalAddresses( 
			/* [retval][out] */ BSTR *localAddrs) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_LocalAddresses( 
			/* [in] */ BSTR localAddrs) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RemoteAddresses( 
			/* [retval][out] */ BSTR *remoteAddrs) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_RemoteAddresses( 
			/* [in] */ BSTR remoteAddrs) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_IcmpTypesAndCodes( 
			/* [retval][out] */ BSTR *icmpTypesAndCodes) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_IcmpTypesAndCodes( 
			/* [in] */ BSTR icmpTypesAndCodes) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Direction( 
			/* [retval][out] */ NET_FW_RULE_DIRECTION *dir) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Direction( 
			/* [in] */ NET_FW_RULE_DIRECTION dir) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Interfaces( 
			/* [retval][out] */ VARIANT *interfaces) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Interfaces( 
			/* [in] */ VARIANT interfaces) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_InterfaceTypes( 
			/* [retval][out] */ BSTR *interfaceTypes) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_InterfaceTypes( 
			/* [in] */ BSTR interfaceTypes) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Enabled( 
			/* [retval][out] */ VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Enabled( 
			/* [in] */ VARIANT_BOOL enabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Grouping( 
			/* [retval][out] */ BSTR *context) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Grouping( 
			/* [in] */ BSTR context) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Profiles( 
			/* [retval][out] */ long *profileTypesBitmask) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Profiles( 
			/* [in] */ long profileTypesBitmask) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_EdgeTraversal( 
			/* [retval][out] */ VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_EdgeTraversal( 
			/* [in] */ VARIANT_BOOL enabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Action( 
			/* [retval][out] */ NET_FW_ACTION *action) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Action( 
			/* [in] */ NET_FW_ACTION action) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwRuleVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			INetFwRule * This,
			/* [in] */ REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				INetFwRule * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				INetFwRule * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				INetFwRule * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				INetFwRule * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				INetFwRule * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [range][in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwRule * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Name )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *name);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Name )( 
				INetFwRule * This,
				/* [in] */ BSTR name);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Description )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *desc);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Description )( 
				INetFwRule * This,
				/* [in] */ BSTR desc);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ApplicationName )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *imageFileName);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_ApplicationName )( 
				INetFwRule * This,
				/* [in] */ BSTR imageFileName);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ServiceName )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *serviceName);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_ServiceName )( 
				INetFwRule * This,
				/* [in] */ BSTR serviceName);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Protocol )( 
				INetFwRule * This,
				/* [retval][out] */ LONG *protocol);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Protocol )( 
				INetFwRule * This,
				/* [in] */ LONG protocol);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_LocalPorts )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *portNumbers);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_LocalPorts )( 
				INetFwRule * This,
				/* [in] */ BSTR portNumbers);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemotePorts )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *portNumbers);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemotePorts )( 
				INetFwRule * This,
				/* [in] */ BSTR portNumbers);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_LocalAddresses )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *localAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_LocalAddresses )( 
				INetFwRule * This,
				/* [in] */ BSTR localAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemoteAddresses )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *remoteAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemoteAddresses )( 
				INetFwRule * This,
				/* [in] */ BSTR remoteAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IcmpTypesAndCodes )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *icmpTypesAndCodes);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_IcmpTypesAndCodes )( 
				INetFwRule * This,
				/* [in] */ BSTR icmpTypesAndCodes);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Direction )( 
				INetFwRule * This,
				/* [retval][out] */ NET_FW_RULE_DIRECTION *dir);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Direction )( 
				INetFwRule * This,
				/* [in] */ NET_FW_RULE_DIRECTION dir);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Interfaces )( 
				INetFwRule * This,
				/* [retval][out] */ VARIANT *interfaces);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Interfaces )( 
				INetFwRule * This,
				/* [in] */ VARIANT interfaces);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_InterfaceTypes )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *interfaceTypes);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_InterfaceTypes )( 
				INetFwRule * This,
				/* [in] */ BSTR interfaceTypes);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Enabled )( 
				INetFwRule * This,
				/* [retval][out] */ VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Enabled )( 
				INetFwRule * This,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Grouping )( 
				INetFwRule * This,
				/* [retval][out] */ BSTR *context);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Grouping )( 
				INetFwRule * This,
				/* [in] */ BSTR context);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Profiles )( 
				INetFwRule * This,
				/* [retval][out] */ long *profileTypesBitmask);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Profiles )( 
				INetFwRule * This,
				/* [in] */ long profileTypesBitmask);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_EdgeTraversal )( 
				INetFwRule * This,
				/* [retval][out] */ VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_EdgeTraversal )( 
				INetFwRule * This,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Action )( 
				INetFwRule * This,
				/* [retval][out] */ NET_FW_ACTION *action);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Action )( 
				INetFwRule * This,
				/* [in] */ NET_FW_ACTION action);

		END_INTERFACE
	} INetFwRuleVtbl;

	interface INetFwRule
	{
		CONST_VTBL struct INetFwRuleVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwRule_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwRule_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwRule_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwRule_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwRule_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwRule_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwRule_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwRule_get_Name(This,name)	\
	( (This)->lpVtbl -> get_Name(This,name) ) 

#define INetFwRule_put_Name(This,name)	\
	( (This)->lpVtbl -> put_Name(This,name) ) 

#define INetFwRule_get_Description(This,desc)	\
	( (This)->lpVtbl -> get_Description(This,desc) ) 

#define INetFwRule_put_Description(This,desc)	\
	( (This)->lpVtbl -> put_Description(This,desc) ) 

#define INetFwRule_get_ApplicationName(This,imageFileName)	\
	( (This)->lpVtbl -> get_ApplicationName(This,imageFileName) ) 

#define INetFwRule_put_ApplicationName(This,imageFileName)	\
	( (This)->lpVtbl -> put_ApplicationName(This,imageFileName) ) 

#define INetFwRule_get_ServiceName(This,serviceName)	\
	( (This)->lpVtbl -> get_ServiceName(This,serviceName) ) 

#define INetFwRule_put_ServiceName(This,serviceName)	\
	( (This)->lpVtbl -> put_ServiceName(This,serviceName) ) 

#define INetFwRule_get_Protocol(This,protocol)	\
	( (This)->lpVtbl -> get_Protocol(This,protocol) ) 

#define INetFwRule_put_Protocol(This,protocol)	\
	( (This)->lpVtbl -> put_Protocol(This,protocol) ) 

#define INetFwRule_get_LocalPorts(This,portNumbers)	\
	( (This)->lpVtbl -> get_LocalPorts(This,portNumbers) ) 

#define INetFwRule_put_LocalPorts(This,portNumbers)	\
	( (This)->lpVtbl -> put_LocalPorts(This,portNumbers) ) 

#define INetFwRule_get_RemotePorts(This,portNumbers)	\
	( (This)->lpVtbl -> get_RemotePorts(This,portNumbers) ) 

#define INetFwRule_put_RemotePorts(This,portNumbers)	\
	( (This)->lpVtbl -> put_RemotePorts(This,portNumbers) ) 

#define INetFwRule_get_LocalAddresses(This,localAddrs)	\
	( (This)->lpVtbl -> get_LocalAddresses(This,localAddrs) ) 

#define INetFwRule_put_LocalAddresses(This,localAddrs)	\
	( (This)->lpVtbl -> put_LocalAddresses(This,localAddrs) ) 

#define INetFwRule_get_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> get_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwRule_put_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> put_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwRule_get_IcmpTypesAndCodes(This,icmpTypesAndCodes)	\
	( (This)->lpVtbl -> get_IcmpTypesAndCodes(This,icmpTypesAndCodes) ) 

#define INetFwRule_put_IcmpTypesAndCodes(This,icmpTypesAndCodes)	\
	( (This)->lpVtbl -> put_IcmpTypesAndCodes(This,icmpTypesAndCodes) ) 

#define INetFwRule_get_Direction(This,dir)	\
	( (This)->lpVtbl -> get_Direction(This,dir) ) 

#define INetFwRule_put_Direction(This,dir)	\
	( (This)->lpVtbl -> put_Direction(This,dir) ) 

#define INetFwRule_get_Interfaces(This,interfaces)	\
	( (This)->lpVtbl -> get_Interfaces(This,interfaces) ) 

#define INetFwRule_put_Interfaces(This,interfaces)	\
	( (This)->lpVtbl -> put_Interfaces(This,interfaces) ) 

#define INetFwRule_get_InterfaceTypes(This,interfaceTypes)	\
	( (This)->lpVtbl -> get_InterfaceTypes(This,interfaceTypes) ) 

#define INetFwRule_put_InterfaceTypes(This,interfaceTypes)	\
	( (This)->lpVtbl -> put_InterfaceTypes(This,interfaceTypes) ) 

#define INetFwRule_get_Enabled(This,enabled)	\
	( (This)->lpVtbl -> get_Enabled(This,enabled) ) 

#define INetFwRule_put_Enabled(This,enabled)	\
	( (This)->lpVtbl -> put_Enabled(This,enabled) ) 

#define INetFwRule_get_Grouping(This,context)	\
	( (This)->lpVtbl -> get_Grouping(This,context) ) 

#define INetFwRule_put_Grouping(This,context)	\
	( (This)->lpVtbl -> put_Grouping(This,context) ) 

#define INetFwRule_get_Profiles(This,profileTypesBitmask)	\
	( (This)->lpVtbl -> get_Profiles(This,profileTypesBitmask) ) 

#define INetFwRule_put_Profiles(This,profileTypesBitmask)	\
	( (This)->lpVtbl -> put_Profiles(This,profileTypesBitmask) ) 

#define INetFwRule_get_EdgeTraversal(This,enabled)	\
	( (This)->lpVtbl -> get_EdgeTraversal(This,enabled) ) 

#define INetFwRule_put_EdgeTraversal(This,enabled)	\
	( (This)->lpVtbl -> put_EdgeTraversal(This,enabled) ) 

#define INetFwRule_get_Action(This,action)	\
	( (This)->lpVtbl -> get_Action(This,action) ) 

#define INetFwRule_put_Action(This,action)	\
	( (This)->lpVtbl -> put_Action(This,action) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwRule_INTERFACE_DEFINED__ */


#ifndef __INetFwRule2_INTERFACE_DEFINED__
#define __INetFwRule2_INTERFACE_DEFINED__

	/* interface INetFwRule2 */
	/* [dual][uuid][object][local] */ 


	EXTERN_C const IID IID_INetFwRule2;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("9C27C8DA-189B-4DDE-89F7-8B39A316782C")
INetFwRule2 : public INetFwRule
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_EdgeTraversalOptions( 
			/* [retval][out] */ long *lOptions) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_EdgeTraversalOptions( 
			/* [in] */ long lOptions) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwRule2Vtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			INetFwRule2 * This,
			/* [in] */ REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				INetFwRule2 * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				INetFwRule2 * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				INetFwRule2 * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				INetFwRule2 * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				INetFwRule2 * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [range][in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwRule2 * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Name )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *name);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Name )( 
				INetFwRule2 * This,
				/* [in] */ BSTR name);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Description )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *desc);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Description )( 
				INetFwRule2 * This,
				/* [in] */ BSTR desc);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ApplicationName )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *imageFileName);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_ApplicationName )( 
				INetFwRule2 * This,
				/* [in] */ BSTR imageFileName);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ServiceName )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *serviceName);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_ServiceName )( 
				INetFwRule2 * This,
				/* [in] */ BSTR serviceName);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Protocol )( 
				INetFwRule2 * This,
				/* [retval][out] */ LONG *protocol);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Protocol )( 
				INetFwRule2 * This,
				/* [in] */ LONG protocol);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_LocalPorts )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *portNumbers);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_LocalPorts )( 
				INetFwRule2 * This,
				/* [in] */ BSTR portNumbers);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemotePorts )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *portNumbers);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemotePorts )( 
				INetFwRule2 * This,
				/* [in] */ BSTR portNumbers);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_LocalAddresses )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *localAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_LocalAddresses )( 
				INetFwRule2 * This,
				/* [in] */ BSTR localAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemoteAddresses )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *remoteAddrs);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RemoteAddresses )( 
				INetFwRule2 * This,
				/* [in] */ BSTR remoteAddrs);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IcmpTypesAndCodes )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *icmpTypesAndCodes);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_IcmpTypesAndCodes )( 
				INetFwRule2 * This,
				/* [in] */ BSTR icmpTypesAndCodes);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Direction )( 
				INetFwRule2 * This,
				/* [retval][out] */ NET_FW_RULE_DIRECTION *dir);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Direction )( 
				INetFwRule2 * This,
				/* [in] */ NET_FW_RULE_DIRECTION dir);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Interfaces )( 
				INetFwRule2 * This,
				/* [retval][out] */ VARIANT *interfaces);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Interfaces )( 
				INetFwRule2 * This,
				/* [in] */ VARIANT interfaces);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_InterfaceTypes )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *interfaceTypes);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_InterfaceTypes )( 
				INetFwRule2 * This,
				/* [in] */ BSTR interfaceTypes);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Enabled )( 
				INetFwRule2 * This,
				/* [retval][out] */ VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Enabled )( 
				INetFwRule2 * This,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Grouping )( 
				INetFwRule2 * This,
				/* [retval][out] */ BSTR *context);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Grouping )( 
				INetFwRule2 * This,
				/* [in] */ BSTR context);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Profiles )( 
				INetFwRule2 * This,
				/* [retval][out] */ long *profileTypesBitmask);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Profiles )( 
				INetFwRule2 * This,
				/* [in] */ long profileTypesBitmask);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_EdgeTraversal )( 
				INetFwRule2 * This,
				/* [retval][out] */ VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_EdgeTraversal )( 
				INetFwRule2 * This,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Action )( 
				INetFwRule2 * This,
				/* [retval][out] */ NET_FW_ACTION *action);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Action )( 
				INetFwRule2 * This,
				/* [in] */ NET_FW_ACTION action);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_EdgeTraversalOptions )( 
				INetFwRule2 * This,
				/* [retval][out] */ long *lOptions);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_EdgeTraversalOptions )( 
				INetFwRule2 * This,
				/* [in] */ long lOptions);

		END_INTERFACE
	} INetFwRule2Vtbl;

	interface INetFwRule2
	{
		CONST_VTBL struct INetFwRule2Vtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwRule2_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwRule2_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwRule2_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwRule2_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwRule2_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwRule2_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwRule2_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwRule2_get_Name(This,name)	\
	( (This)->lpVtbl -> get_Name(This,name) ) 

#define INetFwRule2_put_Name(This,name)	\
	( (This)->lpVtbl -> put_Name(This,name) ) 

#define INetFwRule2_get_Description(This,desc)	\
	( (This)->lpVtbl -> get_Description(This,desc) ) 

#define INetFwRule2_put_Description(This,desc)	\
	( (This)->lpVtbl -> put_Description(This,desc) ) 

#define INetFwRule2_get_ApplicationName(This,imageFileName)	\
	( (This)->lpVtbl -> get_ApplicationName(This,imageFileName) ) 

#define INetFwRule2_put_ApplicationName(This,imageFileName)	\
	( (This)->lpVtbl -> put_ApplicationName(This,imageFileName) ) 

#define INetFwRule2_get_ServiceName(This,serviceName)	\
	( (This)->lpVtbl -> get_ServiceName(This,serviceName) ) 

#define INetFwRule2_put_ServiceName(This,serviceName)	\
	( (This)->lpVtbl -> put_ServiceName(This,serviceName) ) 

#define INetFwRule2_get_Protocol(This,protocol)	\
	( (This)->lpVtbl -> get_Protocol(This,protocol) ) 

#define INetFwRule2_put_Protocol(This,protocol)	\
	( (This)->lpVtbl -> put_Protocol(This,protocol) ) 

#define INetFwRule2_get_LocalPorts(This,portNumbers)	\
	( (This)->lpVtbl -> get_LocalPorts(This,portNumbers) ) 

#define INetFwRule2_put_LocalPorts(This,portNumbers)	\
	( (This)->lpVtbl -> put_LocalPorts(This,portNumbers) ) 

#define INetFwRule2_get_RemotePorts(This,portNumbers)	\
	( (This)->lpVtbl -> get_RemotePorts(This,portNumbers) ) 

#define INetFwRule2_put_RemotePorts(This,portNumbers)	\
	( (This)->lpVtbl -> put_RemotePorts(This,portNumbers) ) 

#define INetFwRule2_get_LocalAddresses(This,localAddrs)	\
	( (This)->lpVtbl -> get_LocalAddresses(This,localAddrs) ) 

#define INetFwRule2_put_LocalAddresses(This,localAddrs)	\
	( (This)->lpVtbl -> put_LocalAddresses(This,localAddrs) ) 

#define INetFwRule2_get_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> get_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwRule2_put_RemoteAddresses(This,remoteAddrs)	\
	( (This)->lpVtbl -> put_RemoteAddresses(This,remoteAddrs) ) 

#define INetFwRule2_get_IcmpTypesAndCodes(This,icmpTypesAndCodes)	\
	( (This)->lpVtbl -> get_IcmpTypesAndCodes(This,icmpTypesAndCodes) ) 

#define INetFwRule2_put_IcmpTypesAndCodes(This,icmpTypesAndCodes)	\
	( (This)->lpVtbl -> put_IcmpTypesAndCodes(This,icmpTypesAndCodes) ) 

#define INetFwRule2_get_Direction(This,dir)	\
	( (This)->lpVtbl -> get_Direction(This,dir) ) 

#define INetFwRule2_put_Direction(This,dir)	\
	( (This)->lpVtbl -> put_Direction(This,dir) ) 

#define INetFwRule2_get_Interfaces(This,interfaces)	\
	( (This)->lpVtbl -> get_Interfaces(This,interfaces) ) 

#define INetFwRule2_put_Interfaces(This,interfaces)	\
	( (This)->lpVtbl -> put_Interfaces(This,interfaces) ) 

#define INetFwRule2_get_InterfaceTypes(This,interfaceTypes)	\
	( (This)->lpVtbl -> get_InterfaceTypes(This,interfaceTypes) ) 

#define INetFwRule2_put_InterfaceTypes(This,interfaceTypes)	\
	( (This)->lpVtbl -> put_InterfaceTypes(This,interfaceTypes) ) 

#define INetFwRule2_get_Enabled(This,enabled)	\
	( (This)->lpVtbl -> get_Enabled(This,enabled) ) 

#define INetFwRule2_put_Enabled(This,enabled)	\
	( (This)->lpVtbl -> put_Enabled(This,enabled) ) 

#define INetFwRule2_get_Grouping(This,context)	\
	( (This)->lpVtbl -> get_Grouping(This,context) ) 

#define INetFwRule2_put_Grouping(This,context)	\
	( (This)->lpVtbl -> put_Grouping(This,context) ) 

#define INetFwRule2_get_Profiles(This,profileTypesBitmask)	\
	( (This)->lpVtbl -> get_Profiles(This,profileTypesBitmask) ) 

#define INetFwRule2_put_Profiles(This,profileTypesBitmask)	\
	( (This)->lpVtbl -> put_Profiles(This,profileTypesBitmask) ) 

#define INetFwRule2_get_EdgeTraversal(This,enabled)	\
	( (This)->lpVtbl -> get_EdgeTraversal(This,enabled) ) 

#define INetFwRule2_put_EdgeTraversal(This,enabled)	\
	( (This)->lpVtbl -> put_EdgeTraversal(This,enabled) ) 

#define INetFwRule2_get_Action(This,action)	\
	( (This)->lpVtbl -> get_Action(This,action) ) 

#define INetFwRule2_put_Action(This,action)	\
	( (This)->lpVtbl -> put_Action(This,action) ) 


#define INetFwRule2_get_EdgeTraversalOptions(This,lOptions)	\
	( (This)->lpVtbl -> get_EdgeTraversalOptions(This,lOptions) ) 

#define INetFwRule2_put_EdgeTraversalOptions(This,lOptions)	\
	( (This)->lpVtbl -> put_EdgeTraversalOptions(This,lOptions) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwRule2_INTERFACE_DEFINED__ */


#ifndef __INetFwRules_INTERFACE_DEFINED__
#define __INetFwRules_INTERFACE_DEFINED__

	/* interface INetFwRules */
	/* [dual][uuid][object][local] */ 


	EXTERN_C const IID IID_INetFwRules;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("9C4C6277-5027-441E-AFAE-CA1F542DA009")
INetFwRules : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Count( 
			/* [retval][out] */ long *count) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Add( 
			/* [in] */ INetFwRule *rule) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Remove( 
			/* [in] */ BSTR name) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Item( 
			/* [in] */ BSTR name,
			/* [retval][out] */ INetFwRule **rule) = 0;

		virtual /* [restricted][propget][id] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
			/* [retval][out] */ IUnknown **newEnum) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwRulesVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			INetFwRules * This,
			/* [in] */ REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				INetFwRules * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				INetFwRules * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				INetFwRules * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				INetFwRules * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				INetFwRules * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [range][in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwRules * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
				INetFwRules * This,
				/* [retval][out] */ long *count);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
				INetFwRules * This,
				/* [in] */ INetFwRule *rule);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
				INetFwRules * This,
				/* [in] */ BSTR name);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
				INetFwRules * This,
				/* [in] */ BSTR name,
				/* [retval][out] */ INetFwRule **rule);

			/* [restricted][propget][id] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
				INetFwRules * This,
				/* [retval][out] */ IUnknown **newEnum);

		END_INTERFACE
	} INetFwRulesVtbl;

	interface INetFwRules
	{
		CONST_VTBL struct INetFwRulesVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwRules_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwRules_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwRules_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwRules_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwRules_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwRules_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwRules_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwRules_get_Count(This,count)	\
	( (This)->lpVtbl -> get_Count(This,count) ) 

#define INetFwRules_Add(This,rule)	\
	( (This)->lpVtbl -> Add(This,rule) ) 

#define INetFwRules_Remove(This,name)	\
	( (This)->lpVtbl -> Remove(This,name) ) 

#define INetFwRules_Item(This,name,rule)	\
	( (This)->lpVtbl -> Item(This,name,rule) ) 

#define INetFwRules_get__NewEnum(This,newEnum)	\
	( (This)->lpVtbl -> get__NewEnum(This,newEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwRules_INTERFACE_DEFINED__ */


#ifndef __INetFwServiceRestriction_INTERFACE_DEFINED__
#define __INetFwServiceRestriction_INTERFACE_DEFINED__

	/* interface INetFwServiceRestriction */
	/* [dual][uuid][object][local] */ 


	EXTERN_C const IID IID_INetFwServiceRestriction;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("8267BBE3-F890-491C-B7B6-2DB1EF0E5D2B")
INetFwServiceRestriction : public IDispatch
	{
	public:
		virtual /* [id] */ HRESULT STDMETHODCALLTYPE RestrictService( 
			/* [in] */ BSTR serviceName,
			/* [in] */ BSTR appName,
			/* [in] */ VARIANT_BOOL restrictService,
			/* [in] */ VARIANT_BOOL serviceSidRestricted) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE ServiceRestricted( 
			/* [in] */ BSTR serviceName,
			/* [in] */ BSTR appName,
			/* [retval][out] */ VARIANT_BOOL *serviceRestricted) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Rules( 
			/* [retval][out] */ INetFwRules **rules) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwServiceRestrictionVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			INetFwServiceRestriction * This,
			/* [in] */ REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				INetFwServiceRestriction * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				INetFwServiceRestriction * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				INetFwServiceRestriction * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				INetFwServiceRestriction * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				INetFwServiceRestriction * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [range][in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwServiceRestriction * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *RestrictService )( 
				INetFwServiceRestriction * This,
				/* [in] */ BSTR serviceName,
				/* [in] */ BSTR appName,
				/* [in] */ VARIANT_BOOL restrictService,
				/* [in] */ VARIANT_BOOL serviceSidRestricted);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *ServiceRestricted )( 
				INetFwServiceRestriction * This,
				/* [in] */ BSTR serviceName,
				/* [in] */ BSTR appName,
				/* [retval][out] */ VARIANT_BOOL *serviceRestricted);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Rules )( 
				INetFwServiceRestriction * This,
				/* [retval][out] */ INetFwRules **rules);

		END_INTERFACE
	} INetFwServiceRestrictionVtbl;

	interface INetFwServiceRestriction
	{
		CONST_VTBL struct INetFwServiceRestrictionVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwServiceRestriction_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwServiceRestriction_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwServiceRestriction_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwServiceRestriction_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwServiceRestriction_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwServiceRestriction_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwServiceRestriction_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwServiceRestriction_RestrictService(This,serviceName,appName,restrictService,serviceSidRestricted)	\
	( (This)->lpVtbl -> RestrictService(This,serviceName,appName,restrictService,serviceSidRestricted) ) 

#define INetFwServiceRestriction_ServiceRestricted(This,serviceName,appName,serviceRestricted)	\
	( (This)->lpVtbl -> ServiceRestricted(This,serviceName,appName,serviceRestricted) ) 

#define INetFwServiceRestriction_get_Rules(This,rules)	\
	( (This)->lpVtbl -> get_Rules(This,rules) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwServiceRestriction_INTERFACE_DEFINED__ */


#ifndef __INetFwProfile_INTERFACE_DEFINED__
#define __INetFwProfile_INTERFACE_DEFINED__

	/* interface INetFwProfile */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwProfile;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("174A0DDA-E9F9-449D-993B-21AB667CA456")
INetFwProfile : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Type( 
			/* [retval][out] */ __RPC__out NET_FW_PROFILE_TYPE *type) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_FirewallEnabled( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_FirewallEnabled( 
			/* [in] */ VARIANT_BOOL enabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ExceptionsNotAllowed( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *notAllowed) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_ExceptionsNotAllowed( 
			/* [in] */ VARIANT_BOOL notAllowed) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_NotificationsDisabled( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *disabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_NotificationsDisabled( 
			/* [in] */ VARIANT_BOOL disabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_UnicastResponsesToMulticastBroadcastDisabled( 
			/* [retval][out] */ __RPC__out VARIANT_BOOL *disabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_UnicastResponsesToMulticastBroadcastDisabled( 
			/* [in] */ VARIANT_BOOL disabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RemoteAdminSettings( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwRemoteAdminSettings **remoteAdminSettings) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_IcmpSettings( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwIcmpSettings **icmpSettings) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_GloballyOpenPorts( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwOpenPorts **openPorts) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Services( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwServices **services) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_AuthorizedApplications( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwAuthorizedApplications **apps) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwProfileVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwProfile * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwProfile * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwProfile * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwProfile * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwProfile * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwProfile * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwProfile * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Type )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__out NET_FW_PROFILE_TYPE *type);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_FirewallEnabled )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_FirewallEnabled )( 
				__RPC__in INetFwProfile * This,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ExceptionsNotAllowed )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *notAllowed);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_ExceptionsNotAllowed )( 
				__RPC__in INetFwProfile * This,
				/* [in] */ VARIANT_BOOL notAllowed);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_NotificationsDisabled )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *disabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_NotificationsDisabled )( 
				__RPC__in INetFwProfile * This,
				/* [in] */ VARIANT_BOOL disabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_UnicastResponsesToMulticastBroadcastDisabled )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__out VARIANT_BOOL *disabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_UnicastResponsesToMulticastBroadcastDisabled )( 
				__RPC__in INetFwProfile * This,
				/* [in] */ VARIANT_BOOL disabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RemoteAdminSettings )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwRemoteAdminSettings **remoteAdminSettings);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IcmpSettings )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwIcmpSettings **icmpSettings);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_GloballyOpenPorts )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwOpenPorts **openPorts);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Services )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwServices **services);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_AuthorizedApplications )( 
				__RPC__in INetFwProfile * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwAuthorizedApplications **apps);

		END_INTERFACE
	} INetFwProfileVtbl;

	interface INetFwProfile
	{
		CONST_VTBL struct INetFwProfileVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwProfile_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwProfile_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwProfile_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwProfile_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwProfile_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwProfile_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwProfile_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwProfile_get_Type(This,type)	\
	( (This)->lpVtbl -> get_Type(This,type) ) 

#define INetFwProfile_get_FirewallEnabled(This,enabled)	\
	( (This)->lpVtbl -> get_FirewallEnabled(This,enabled) ) 

#define INetFwProfile_put_FirewallEnabled(This,enabled)	\
	( (This)->lpVtbl -> put_FirewallEnabled(This,enabled) ) 

#define INetFwProfile_get_ExceptionsNotAllowed(This,notAllowed)	\
	( (This)->lpVtbl -> get_ExceptionsNotAllowed(This,notAllowed) ) 

#define INetFwProfile_put_ExceptionsNotAllowed(This,notAllowed)	\
	( (This)->lpVtbl -> put_ExceptionsNotAllowed(This,notAllowed) ) 

#define INetFwProfile_get_NotificationsDisabled(This,disabled)	\
	( (This)->lpVtbl -> get_NotificationsDisabled(This,disabled) ) 

#define INetFwProfile_put_NotificationsDisabled(This,disabled)	\
	( (This)->lpVtbl -> put_NotificationsDisabled(This,disabled) ) 

#define INetFwProfile_get_UnicastResponsesToMulticastBroadcastDisabled(This,disabled)	\
	( (This)->lpVtbl -> get_UnicastResponsesToMulticastBroadcastDisabled(This,disabled) ) 

#define INetFwProfile_put_UnicastResponsesToMulticastBroadcastDisabled(This,disabled)	\
	( (This)->lpVtbl -> put_UnicastResponsesToMulticastBroadcastDisabled(This,disabled) ) 

#define INetFwProfile_get_RemoteAdminSettings(This,remoteAdminSettings)	\
	( (This)->lpVtbl -> get_RemoteAdminSettings(This,remoteAdminSettings) ) 

#define INetFwProfile_get_IcmpSettings(This,icmpSettings)	\
	( (This)->lpVtbl -> get_IcmpSettings(This,icmpSettings) ) 

#define INetFwProfile_get_GloballyOpenPorts(This,openPorts)	\
	( (This)->lpVtbl -> get_GloballyOpenPorts(This,openPorts) ) 

#define INetFwProfile_get_Services(This,services)	\
	( (This)->lpVtbl -> get_Services(This,services) ) 

#define INetFwProfile_get_AuthorizedApplications(This,apps)	\
	( (This)->lpVtbl -> get_AuthorizedApplications(This,apps) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwProfile_INTERFACE_DEFINED__ */


#ifndef __INetFwPolicy_INTERFACE_DEFINED__
#define __INetFwPolicy_INTERFACE_DEFINED__

	/* interface INetFwPolicy */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwPolicy;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("D46D2478-9AC9-4008-9DC7-5563CE5536CC")
INetFwPolicy : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_CurrentProfile( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwProfile **profile) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE GetProfileByType( 
			/* [in] */ NET_FW_PROFILE_TYPE profileType,
			/* [retval][out] */ __RPC__deref_out_opt INetFwProfile **profile) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwPolicyVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwPolicy * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwPolicy * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwPolicy * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwPolicy * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwPolicy * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwPolicy * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwPolicy * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_CurrentProfile )( 
				__RPC__in INetFwPolicy * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwProfile **profile);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *GetProfileByType )( 
				__RPC__in INetFwPolicy * This,
				/* [in] */ NET_FW_PROFILE_TYPE profileType,
				/* [retval][out] */ __RPC__deref_out_opt INetFwProfile **profile);

		END_INTERFACE
	} INetFwPolicyVtbl;

	interface INetFwPolicy
	{
		CONST_VTBL struct INetFwPolicyVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwPolicy_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwPolicy_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwPolicy_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwPolicy_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwPolicy_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwPolicy_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwPolicy_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwPolicy_get_CurrentProfile(This,profile)	\
	( (This)->lpVtbl -> get_CurrentProfile(This,profile) ) 

#define INetFwPolicy_GetProfileByType(This,profileType,profile)	\
	( (This)->lpVtbl -> GetProfileByType(This,profileType,profile) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwPolicy_INTERFACE_DEFINED__ */


#ifndef __INetFwPolicy2_INTERFACE_DEFINED__
#define __INetFwPolicy2_INTERFACE_DEFINED__

	/* interface INetFwPolicy2 */
	/* [dual][uuid][object][local] */ 


	EXTERN_C const IID IID_INetFwPolicy2;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("98325047-C671-4174-8D81-DEFCD3F03186")
INetFwPolicy2 : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_CurrentProfileTypes( 
			/* [retval][out] */ long *profileTypesBitmask) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_FirewallEnabled( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [retval][out] */ VARIANT_BOOL *enabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_FirewallEnabled( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [in] */ VARIANT_BOOL enabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ExcludedInterfaces( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [retval][out] */ VARIANT *interfaces) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_ExcludedInterfaces( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [in] */ VARIANT interfaces) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_BlockAllInboundTraffic( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [retval][out] */ VARIANT_BOOL *Block) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_BlockAllInboundTraffic( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [in] */ VARIANT_BOOL Block) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_NotificationsDisabled( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [retval][out] */ VARIANT_BOOL *disabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_NotificationsDisabled( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [in] */ VARIANT_BOOL disabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_UnicastResponsesToMulticastBroadcastDisabled( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [retval][out] */ VARIANT_BOOL *disabled) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_UnicastResponsesToMulticastBroadcastDisabled( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [in] */ VARIANT_BOOL disabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Rules( 
			/* [retval][out] */ INetFwRules **rules) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ServiceRestriction( 
			/* [retval][out] */ INetFwServiceRestriction **ServiceRestriction) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE EnableRuleGroup( 
			/* [in] */ long profileTypesBitmask,
			/* [in] */ BSTR group,
			/* [in] */ VARIANT_BOOL enable) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE IsRuleGroupEnabled( 
			/* [in] */ long profileTypesBitmask,
			/* [in] */ BSTR group,
			/* [retval][out] */ VARIANT_BOOL *enabled) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE RestoreLocalFirewallDefaults( void) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_DefaultInboundAction( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [retval][out] */ NET_FW_ACTION *action) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_DefaultInboundAction( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [in] */ NET_FW_ACTION action) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_DefaultOutboundAction( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [retval][out] */ NET_FW_ACTION *action) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_DefaultOutboundAction( 
			/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
			/* [in] */ NET_FW_ACTION action) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_IsRuleGroupCurrentlyEnabled( 
			/* [in] */ BSTR group,
			/* [retval][out] */ VARIANT_BOOL *enabled) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_LocalPolicyModifyState( 
			/* [retval][out] */ NET_FW_MODIFY_STATE *modifyState) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwPolicy2Vtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			INetFwPolicy2 * This,
			/* [in] */ REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				INetFwPolicy2 * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				INetFwPolicy2 * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				INetFwPolicy2 * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				INetFwPolicy2 * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				INetFwPolicy2 * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [range][in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwPolicy2 * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_CurrentProfileTypes )( 
				INetFwPolicy2 * This,
				/* [retval][out] */ long *profileTypesBitmask);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_FirewallEnabled )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [retval][out] */ VARIANT_BOOL *enabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_FirewallEnabled )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [in] */ VARIANT_BOOL enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ExcludedInterfaces )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [retval][out] */ VARIANT *interfaces);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_ExcludedInterfaces )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [in] */ VARIANT interfaces);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_BlockAllInboundTraffic )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [retval][out] */ VARIANT_BOOL *Block);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_BlockAllInboundTraffic )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [in] */ VARIANT_BOOL Block);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_NotificationsDisabled )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [retval][out] */ VARIANT_BOOL *disabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_NotificationsDisabled )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [in] */ VARIANT_BOOL disabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_UnicastResponsesToMulticastBroadcastDisabled )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [retval][out] */ VARIANT_BOOL *disabled);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_UnicastResponsesToMulticastBroadcastDisabled )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [in] */ VARIANT_BOOL disabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Rules )( 
				INetFwPolicy2 * This,
				/* [retval][out] */ INetFwRules **rules);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_ServiceRestriction )( 
				INetFwPolicy2 * This,
				/* [retval][out] */ INetFwServiceRestriction **ServiceRestriction);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *EnableRuleGroup )( 
				INetFwPolicy2 * This,
				/* [in] */ long profileTypesBitmask,
				/* [in] */ BSTR group,
				/* [in] */ VARIANT_BOOL enable);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *IsRuleGroupEnabled )( 
				INetFwPolicy2 * This,
				/* [in] */ long profileTypesBitmask,
				/* [in] */ BSTR group,
				/* [retval][out] */ VARIANT_BOOL *enabled);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *RestoreLocalFirewallDefaults )( 
				INetFwPolicy2 * This);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_DefaultInboundAction )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [retval][out] */ NET_FW_ACTION *action);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_DefaultInboundAction )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [in] */ NET_FW_ACTION action);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_DefaultOutboundAction )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [retval][out] */ NET_FW_ACTION *action);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_DefaultOutboundAction )( 
				INetFwPolicy2 * This,
				/* [in] */ NET_FW_PROFILE_TYPE2 profileType,
				/* [in] */ NET_FW_ACTION action);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_IsRuleGroupCurrentlyEnabled )( 
				INetFwPolicy2 * This,
				/* [in] */ BSTR group,
				/* [retval][out] */ VARIANT_BOOL *enabled);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_LocalPolicyModifyState )( 
				INetFwPolicy2 * This,
				/* [retval][out] */ NET_FW_MODIFY_STATE *modifyState);

		END_INTERFACE
	} INetFwPolicy2Vtbl;

	interface INetFwPolicy2
	{
		CONST_VTBL struct INetFwPolicy2Vtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwPolicy2_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwPolicy2_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwPolicy2_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwPolicy2_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwPolicy2_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwPolicy2_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwPolicy2_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwPolicy2_get_CurrentProfileTypes(This,profileTypesBitmask)	\
	( (This)->lpVtbl -> get_CurrentProfileTypes(This,profileTypesBitmask) ) 

#define INetFwPolicy2_get_FirewallEnabled(This,profileType,enabled)	\
	( (This)->lpVtbl -> get_FirewallEnabled(This,profileType,enabled) ) 

#define INetFwPolicy2_put_FirewallEnabled(This,profileType,enabled)	\
	( (This)->lpVtbl -> put_FirewallEnabled(This,profileType,enabled) ) 

#define INetFwPolicy2_get_ExcludedInterfaces(This,profileType,interfaces)	\
	( (This)->lpVtbl -> get_ExcludedInterfaces(This,profileType,interfaces) ) 

#define INetFwPolicy2_put_ExcludedInterfaces(This,profileType,interfaces)	\
	( (This)->lpVtbl -> put_ExcludedInterfaces(This,profileType,interfaces) ) 

#define INetFwPolicy2_get_BlockAllInboundTraffic(This,profileType,Block)	\
	( (This)->lpVtbl -> get_BlockAllInboundTraffic(This,profileType,Block) ) 

#define INetFwPolicy2_put_BlockAllInboundTraffic(This,profileType,Block)	\
	( (This)->lpVtbl -> put_BlockAllInboundTraffic(This,profileType,Block) ) 

#define INetFwPolicy2_get_NotificationsDisabled(This,profileType,disabled)	\
	( (This)->lpVtbl -> get_NotificationsDisabled(This,profileType,disabled) ) 

#define INetFwPolicy2_put_NotificationsDisabled(This,profileType,disabled)	\
	( (This)->lpVtbl -> put_NotificationsDisabled(This,profileType,disabled) ) 

#define INetFwPolicy2_get_UnicastResponsesToMulticastBroadcastDisabled(This,profileType,disabled)	\
	( (This)->lpVtbl -> get_UnicastResponsesToMulticastBroadcastDisabled(This,profileType,disabled) ) 

#define INetFwPolicy2_put_UnicastResponsesToMulticastBroadcastDisabled(This,profileType,disabled)	\
	( (This)->lpVtbl -> put_UnicastResponsesToMulticastBroadcastDisabled(This,profileType,disabled) ) 

#define INetFwPolicy2_get_Rules(This,rules)	\
	( (This)->lpVtbl -> get_Rules(This,rules) ) 

#define INetFwPolicy2_get_ServiceRestriction(This,ServiceRestriction)	\
	( (This)->lpVtbl -> get_ServiceRestriction(This,ServiceRestriction) ) 

#define INetFwPolicy2_EnableRuleGroup(This,profileTypesBitmask,group,enable)	\
	( (This)->lpVtbl -> EnableRuleGroup(This,profileTypesBitmask,group,enable) ) 

#define INetFwPolicy2_IsRuleGroupEnabled(This,profileTypesBitmask,group,enabled)	\
	( (This)->lpVtbl -> IsRuleGroupEnabled(This,profileTypesBitmask,group,enabled) ) 

#define INetFwPolicy2_RestoreLocalFirewallDefaults(This)	\
	( (This)->lpVtbl -> RestoreLocalFirewallDefaults(This) ) 

#define INetFwPolicy2_get_DefaultInboundAction(This,profileType,action)	\
	( (This)->lpVtbl -> get_DefaultInboundAction(This,profileType,action) ) 

#define INetFwPolicy2_put_DefaultInboundAction(This,profileType,action)	\
	( (This)->lpVtbl -> put_DefaultInboundAction(This,profileType,action) ) 

#define INetFwPolicy2_get_DefaultOutboundAction(This,profileType,action)	\
	( (This)->lpVtbl -> get_DefaultOutboundAction(This,profileType,action) ) 

#define INetFwPolicy2_put_DefaultOutboundAction(This,profileType,action)	\
	( (This)->lpVtbl -> put_DefaultOutboundAction(This,profileType,action) ) 

#define INetFwPolicy2_get_IsRuleGroupCurrentlyEnabled(This,group,enabled)	\
	( (This)->lpVtbl -> get_IsRuleGroupCurrentlyEnabled(This,group,enabled) ) 

#define INetFwPolicy2_get_LocalPolicyModifyState(This,modifyState)	\
	( (This)->lpVtbl -> get_LocalPolicyModifyState(This,modifyState) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwPolicy2_INTERFACE_DEFINED__ */


#ifndef __INetFwMgr_INTERFACE_DEFINED__
#define __INetFwMgr_INTERFACE_DEFINED__

	/* interface INetFwMgr */
	/* [dual][uuid][object] */ 


	EXTERN_C const IID IID_INetFwMgr;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("F7898AF5-CAC4-4632-A2EC-DA06E5111AF2")
INetFwMgr : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_LocalPolicy( 
			/* [retval][out] */ __RPC__deref_out_opt INetFwPolicy **localPolicy) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_CurrentProfileType( 
			/* [retval][out] */ __RPC__out NET_FW_PROFILE_TYPE *profileType) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE RestoreDefaults( void) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE IsPortAllowed( 
			/* [in] */ __RPC__in BSTR imageFileName,
			/* [in] */ NET_FW_IP_VERSION ipVersion,
			/* [in] */ LONG portNumber,
			/* [in] */ __RPC__in BSTR localAddress,
			/* [in] */ NET_FW_IP_PROTOCOL ipProtocol,
			/* [out] */ __RPC__out VARIANT *allowed,
			/* [out] */ __RPC__out VARIANT *restricted) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE IsIcmpTypeAllowed( 
			/* [in] */ NET_FW_IP_VERSION ipVersion,
			/* [in] */ __RPC__in BSTR localAddress,
			/* [in] */ BYTE type,
			/* [out] */ __RPC__out VARIANT *allowed,
			/* [out] */ __RPC__out VARIANT *restricted) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwMgrVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			__RPC__in INetFwMgr * This,
			/* [in] */ __RPC__in REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				__RPC__in INetFwMgr * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				__RPC__in INetFwMgr * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				__RPC__in INetFwMgr * This,
				/* [out] */ __RPC__out UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				__RPC__in INetFwMgr * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ __RPC__deref_out_opt ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				__RPC__in INetFwMgr * This,
				/* [in] */ __RPC__in REFIID riid,
				/* [size_is][in] */ __RPC__in_ecount_full(cNames) LPOLESTR *rgszNames,
				/* [range][in] */ __RPC__in_range(0,16384) UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ __RPC__out_ecount_full(cNames) DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwMgr * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_LocalPolicy )( 
				__RPC__in INetFwMgr * This,
				/* [retval][out] */ __RPC__deref_out_opt INetFwPolicy **localPolicy);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_CurrentProfileType )( 
				__RPC__in INetFwMgr * This,
				/* [retval][out] */ __RPC__out NET_FW_PROFILE_TYPE *profileType);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *RestoreDefaults )( 
				__RPC__in INetFwMgr * This);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *IsPortAllowed )( 
				__RPC__in INetFwMgr * This,
				/* [in] */ __RPC__in BSTR imageFileName,
				/* [in] */ NET_FW_IP_VERSION ipVersion,
				/* [in] */ LONG portNumber,
				/* [in] */ __RPC__in BSTR localAddress,
				/* [in] */ NET_FW_IP_PROTOCOL ipProtocol,
				/* [out] */ __RPC__out VARIANT *allowed,
				/* [out] */ __RPC__out VARIANT *restricted);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *IsIcmpTypeAllowed )( 
				__RPC__in INetFwMgr * This,
				/* [in] */ NET_FW_IP_VERSION ipVersion,
				/* [in] */ __RPC__in BSTR localAddress,
				/* [in] */ BYTE type,
				/* [out] */ __RPC__out VARIANT *allowed,
				/* [out] */ __RPC__out VARIANT *restricted);

		END_INTERFACE
	} INetFwMgrVtbl;

	interface INetFwMgr
	{
		CONST_VTBL struct INetFwMgrVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwMgr_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwMgr_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwMgr_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwMgr_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwMgr_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwMgr_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwMgr_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwMgr_get_LocalPolicy(This,localPolicy)	\
	( (This)->lpVtbl -> get_LocalPolicy(This,localPolicy) ) 

#define INetFwMgr_get_CurrentProfileType(This,profileType)	\
	( (This)->lpVtbl -> get_CurrentProfileType(This,profileType) ) 

#define INetFwMgr_RestoreDefaults(This)	\
	( (This)->lpVtbl -> RestoreDefaults(This) ) 

#define INetFwMgr_IsPortAllowed(This,imageFileName,ipVersion,portNumber,localAddress,ipProtocol,allowed,restricted)	\
	( (This)->lpVtbl -> IsPortAllowed(This,imageFileName,ipVersion,portNumber,localAddress,ipProtocol,allowed,restricted) ) 

#define INetFwMgr_IsIcmpTypeAllowed(This,ipVersion,localAddress,type,allowed,restricted)	\
	( (This)->lpVtbl -> IsIcmpTypeAllowed(This,ipVersion,localAddress,type,allowed,restricted) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwMgr_INTERFACE_DEFINED__ */


#ifndef __INetFwProduct_INTERFACE_DEFINED__
#define __INetFwProduct_INTERFACE_DEFINED__

	/* interface INetFwProduct */
	/* [dual][uuid][object][local] */ 


	EXTERN_C const IID IID_INetFwProduct;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("71881699-18f4-458b-b892-3ffce5e07f75")
INetFwProduct : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_RuleCategories( 
			/* [retval][out] */ VARIANT *ruleCategories) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_RuleCategories( 
			/* [in] */ VARIANT ruleCategories) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_DisplayName( 
			/* [retval][out] */ BSTR *displayName) = 0;

		virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_DisplayName( 
			/* [in] */ BSTR displayName) = 0;

		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_PathToSignedProductExe( 
			/* [retval][out] */ BSTR *path) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwProductVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			INetFwProduct * This,
			/* [in] */ REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				INetFwProduct * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				INetFwProduct * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				INetFwProduct * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				INetFwProduct * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				INetFwProduct * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [range][in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwProduct * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_RuleCategories )( 
				INetFwProduct * This,
				/* [retval][out] */ VARIANT *ruleCategories);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_RuleCategories )( 
				INetFwProduct * This,
				/* [in] */ VARIANT ruleCategories);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_DisplayName )( 
				INetFwProduct * This,
				/* [retval][out] */ BSTR *displayName);

			/* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_DisplayName )( 
				INetFwProduct * This,
				/* [in] */ BSTR displayName);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_PathToSignedProductExe )( 
				INetFwProduct * This,
				/* [retval][out] */ BSTR *path);

		END_INTERFACE
	} INetFwProductVtbl;

	interface INetFwProduct
	{
		CONST_VTBL struct INetFwProductVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwProduct_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwProduct_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwProduct_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwProduct_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwProduct_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwProduct_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwProduct_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwProduct_get_RuleCategories(This,ruleCategories)	\
	( (This)->lpVtbl -> get_RuleCategories(This,ruleCategories) ) 

#define INetFwProduct_put_RuleCategories(This,ruleCategories)	\
	( (This)->lpVtbl -> put_RuleCategories(This,ruleCategories) ) 

#define INetFwProduct_get_DisplayName(This,displayName)	\
	( (This)->lpVtbl -> get_DisplayName(This,displayName) ) 

#define INetFwProduct_put_DisplayName(This,displayName)	\
	( (This)->lpVtbl -> put_DisplayName(This,displayName) ) 

#define INetFwProduct_get_PathToSignedProductExe(This,path)	\
	( (This)->lpVtbl -> get_PathToSignedProductExe(This,path) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwProduct_INTERFACE_DEFINED__ */


#ifndef __INetFwProducts_INTERFACE_DEFINED__
#define __INetFwProducts_INTERFACE_DEFINED__

	/* interface INetFwProducts */
	/* [dual][uuid][object][local] */ 


	EXTERN_C const IID IID_INetFwProducts;

#if defined(__cplusplus) && !defined(CINTERFACE)

	MIDL_INTERFACE("39EB36E0-2097-40BD-8AF2-63A13B525362")
INetFwProducts : public IDispatch
	{
	public:
		virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Count( 
			/* [retval][out] */ long *count) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Register( 
			/* [in] */ INetFwProduct *product,
			/* [retval][out] */ IUnknown **registration) = 0;

		virtual /* [id] */ HRESULT STDMETHODCALLTYPE Item( 
			/* [in] */ long index,
			/* [retval][out] */ INetFwProduct **product) = 0;

		virtual /* [restricted][propget][id] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
			/* [retval][out] */ IUnknown **newEnum) = 0;

	};

#else 	/* C style interface */

	typedef struct INetFwProductsVtbl
	{
		BEGIN_INTERFACE

			HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
			INetFwProducts * This,
			/* [in] */ REFIID riid,
			/* [annotation][iid_is][out] */ 
			__RPC__deref_out  void **ppvObject);

			ULONG ( STDMETHODCALLTYPE *AddRef )( 
				INetFwProducts * This);

			ULONG ( STDMETHODCALLTYPE *Release )( 
				INetFwProducts * This);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
				INetFwProducts * This,
				/* [out] */ UINT *pctinfo);

			HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
				INetFwProducts * This,
				/* [in] */ UINT iTInfo,
				/* [in] */ LCID lcid,
				/* [out] */ ITypeInfo **ppTInfo);

			HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
				INetFwProducts * This,
				/* [in] */ REFIID riid,
				/* [size_is][in] */ LPOLESTR *rgszNames,
				/* [range][in] */ UINT cNames,
				/* [in] */ LCID lcid,
				/* [size_is][out] */ DISPID *rgDispId);

			/* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
				INetFwProducts * This,
				/* [in] */ DISPID dispIdMember,
				/* [in] */ REFIID riid,
				/* [in] */ LCID lcid,
				/* [in] */ WORD wFlags,
				/* [out][in] */ DISPPARAMS *pDispParams,
				/* [out] */ VARIANT *pVarResult,
				/* [out] */ EXCEPINFO *pExcepInfo,
				/* [out] */ UINT *puArgErr);

			/* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
				INetFwProducts * This,
				/* [retval][out] */ long *count);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Register )( 
				INetFwProducts * This,
				/* [in] */ INetFwProduct *product,
				/* [retval][out] */ IUnknown **registration);

			/* [id] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
				INetFwProducts * This,
				/* [in] */ long index,
				/* [retval][out] */ INetFwProduct **product);

			/* [restricted][propget][id] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
				INetFwProducts * This,
				/* [retval][out] */ IUnknown **newEnum);

		END_INTERFACE
	} INetFwProductsVtbl;

	interface INetFwProducts
	{
		CONST_VTBL struct INetFwProductsVtbl *lpVtbl;
	};



#ifdef COBJMACROS


#define INetFwProducts_QueryInterface(This,riid,ppvObject)	\
	( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define INetFwProducts_AddRef(This)	\
	( (This)->lpVtbl -> AddRef(This) ) 

#define INetFwProducts_Release(This)	\
	( (This)->lpVtbl -> Release(This) ) 


#define INetFwProducts_GetTypeInfoCount(This,pctinfo)	\
	( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define INetFwProducts_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
	( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define INetFwProducts_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
	( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define INetFwProducts_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
	( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define INetFwProducts_get_Count(This,count)	\
	( (This)->lpVtbl -> get_Count(This,count) ) 

#define INetFwProducts_Register(This,product,registration)	\
	( (This)->lpVtbl -> Register(This,product,registration) ) 

#define INetFwProducts_Item(This,index,product)	\
	( (This)->lpVtbl -> Item(This,index,product) ) 

#define INetFwProducts_get__NewEnum(This,newEnum)	\
	( (This)->lpVtbl -> get__NewEnum(This,newEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __INetFwProducts_INTERFACE_DEFINED__ */



#ifndef __NetFwPublicTypeLib_LIBRARY_DEFINED__
#define __NetFwPublicTypeLib_LIBRARY_DEFINED__

	/* library NetFwPublicTypeLib */
	/* [version][uuid] */ 



















	EXTERN_C const IID LIBID_NetFwPublicTypeLib;

	EXTERN_C const CLSID CLSID_NetFwRule;

#ifdef __cplusplus

	class DECLSPEC_UUID("2C5BC43E-3369-4C33-AB0C-BE9469677AF4")
		NetFwRule;
#endif

	EXTERN_C const CLSID CLSID_NetFwOpenPort;

#ifdef __cplusplus

	class DECLSPEC_UUID("0CA545C6-37AD-4A6C-BF92-9F7610067EF5")
		NetFwOpenPort;
#endif

	EXTERN_C const CLSID CLSID_NetFwAuthorizedApplication;

#ifdef __cplusplus

	class DECLSPEC_UUID("EC9846B3-2762-4A6B-A214-6ACB603462D2")
		NetFwAuthorizedApplication;
#endif

	EXTERN_C const CLSID CLSID_NetFwPolicy2;

#ifdef __cplusplus

	class DECLSPEC_UUID("E2B3C97F-6AE1-41AC-817A-F6F92166D7DD")
		NetFwPolicy2;
#endif

	EXTERN_C const CLSID CLSID_NetFwProduct;

#ifdef __cplusplus

	class DECLSPEC_UUID("9D745ED8-C514-4D1D-BF42-751FED2D5AC7")
		NetFwProduct;
#endif

	EXTERN_C const CLSID CLSID_NetFwProducts;

#ifdef __cplusplus

	class DECLSPEC_UUID("CC19079B-8272-4D73-BB70-CDB533527B61")
		NetFwProducts;
#endif

	EXTERN_C const CLSID CLSID_NetFwMgr;

#ifdef __cplusplus

	class DECLSPEC_UUID("304CE942-6E39-40D8-943A-B913C40C9CD4")
		NetFwMgr;
#endif
#endif /* __NetFwPublicTypeLib_LIBRARY_DEFINED__ */

	/* Additional Prototypes for ALL interfaces */

	unsigned long             __RPC_USER  BSTR_UserSize(     __RPC__in unsigned long *, unsigned long            , __RPC__in BSTR * ); 
	unsigned char * __RPC_USER  BSTR_UserMarshal(  __RPC__in unsigned long *, __RPC__inout_xcount(0) unsigned char *, __RPC__in BSTR * ); 
	unsigned char * __RPC_USER  BSTR_UserUnmarshal(__RPC__in unsigned long *, __RPC__in_xcount(0) unsigned char *, __RPC__out BSTR * ); 
	void                      __RPC_USER  BSTR_UserFree(     __RPC__in unsigned long *, __RPC__in BSTR * ); 

	unsigned long             __RPC_USER  VARIANT_UserSize(     __RPC__in unsigned long *, unsigned long            , __RPC__in VARIANT * ); 
	unsigned char * __RPC_USER  VARIANT_UserMarshal(  __RPC__in unsigned long *, __RPC__inout_xcount(0) unsigned char *, __RPC__in VARIANT * ); 
	unsigned char * __RPC_USER  VARIANT_UserUnmarshal(__RPC__in unsigned long *, __RPC__in_xcount(0) unsigned char *, __RPC__out VARIANT * ); 
	void                      __RPC_USER  VARIANT_UserFree(     __RPC__in unsigned long *, __RPC__in VARIANT * ); 

	unsigned long             __RPC_USER  BSTR_UserSize64(     __RPC__in unsigned long *, unsigned long            , __RPC__in BSTR * ); 
	unsigned char * __RPC_USER  BSTR_UserMarshal64(  __RPC__in unsigned long *, __RPC__inout_xcount(0) unsigned char *, __RPC__in BSTR * ); 
	unsigned char * __RPC_USER  BSTR_UserUnmarshal64(__RPC__in unsigned long *, __RPC__in_xcount(0) unsigned char *, __RPC__out BSTR * ); 
	void                      __RPC_USER  BSTR_UserFree64(     __RPC__in unsigned long *, __RPC__in BSTR * ); 

	unsigned long             __RPC_USER  VARIANT_UserSize64(     __RPC__in unsigned long *, unsigned long            , __RPC__in VARIANT * ); 
	unsigned char * __RPC_USER  VARIANT_UserMarshal64(  __RPC__in unsigned long *, __RPC__inout_xcount(0) unsigned char *, __RPC__in VARIANT * ); 
	unsigned char * __RPC_USER  VARIANT_UserUnmarshal64(__RPC__in unsigned long *, __RPC__in_xcount(0) unsigned char *, __RPC__out VARIANT * ); 
	void                      __RPC_USER  VARIANT_UserFree64(     __RPC__in unsigned long *, __RPC__in VARIANT * ); 

	/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif




//end for merge Firewall


#ifdef _SETUPCLS_DLL 
class __declspec(dllexport) CFirewall
#else
class __declspec(dllimport) CFirewall
#endif
{
public:
	CFirewall();
	~CFirewall();

	//get the error message
	void GetErrorMessage(LPTSTR lpMsg,DWORD ccBuffer);

	//Initialize the firewall profile currently in effect.
	HRESULT WindowsFirewallInitialize();

	//Get the current state of the firewall.
	HRESULT WindowsFirewallIsOn(BOOL &bOn);

	//Add the authorized application collection.
	HRESULT WindowsFirewallAddApp(CString strAppFile,CString strName = _T(""));

	//Add port to list of globally open ports.
	HRESULT WindowsFirewallAddPort(UINT nPort,CString strName = _T(""), NET_FW_IP_PROTOCOL ipProtocol = NET_FW_IP_PROTOCOL_TCP);

	//Remove the authorized application collection.
	HRESULT WindowsFirewallRemoveApp(CString strAppFile);

	//Remove port to list of globally open ports.
	HRESULT WindowsFirewallRemovePort(UINT nPort, NET_FW_IP_PROTOCOL ipProtocol = NET_FW_IP_PROTOCOL_TCP);

	//check if the application is added
	HRESULT WindowsFirewallAppIsEnabled(CString strAppFile,BOOL &bEnabled);

	//check if the port is added
	HRESULT WindowsFirewallPortIsEnabled(UINT nPort, NET_FW_IP_PROTOCOL ipProtocol,BOOL &bEnabled);

	//add the following API for windows7 and Windows 2008 R2 above

	//check if the WFCOM support
	BOOL IsWFCOMPermission();

	struct ProfileMapElement 
	{
		NET_FW_PROFILE_TYPE2 Id;
		LPCWSTR Name;
	};
	//Initialize the firewall profile currently in effect.

	HRESULT WFCOMInitialize();

	//Add the authorized application collection.
	HRESULT WFCOMAddAppFirewallRule(CString strAppFile,CString strName,long lProfileTypesBitmask = NET_FW_PROFILE2_PRIVATE,CString strDescription = _T(""));

	//check if the rule enable
	HRESULT WFCOMAppFirewallRuleEnabled(CString strAppFile,CString strName,BOOL& bEnabled,long lProfileTypesBitmask = NET_FW_PROFILE2_PRIVATE);

	//check if the rule exist
	BOOL IsWFCOMAppFirewallRuleExist(CString strAppFile,CString strName);

	//Remove the authorized application collection.
	HRESULT WFCOMRemoveAppFirewallRule(CString strAppFile,CString strName);
	
	HRESULT WFCOMGetCurrentProfile(long &CurrentProfilesType);
	
	//end new API

private:
	CString m_strErrMsg;
	HRESULT m_hr;
	HRESULT m_comInit;
	INetFwProfile* m_fwProfile;	

	//add new items for windows7 and Windows 2008 R2 above
	INetFwPolicy2* m_pNetFwPolicy2;
	INetFwRule* m_pNetFwRule;
	//end new items

};

#endif