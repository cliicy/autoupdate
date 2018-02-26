package com.ca.arcflash.webservice.edge.datasync;

public class D2DSyncResourceID {
	public final static long AFRES_DATA_SYNC							= 	0x00003300; 
	public final static long AFRES_DATA_SYNC_GET_BK_DATA_FAILURE		=	AFRES_DATA_SYNC + 1;
	public final static long AFRES_DATA_SYNC_GET_VM_DATA_FAILURE		=	AFRES_DATA_SYNC + 2;
	public final static long AFRES_DATA_SYNC_GET_VCM_DATA_FAILURE		=	AFRES_DATA_SYNC + 3;
	public final static long AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE		=	AFRES_DATA_SYNC + 4;
	public final static long AFRES_DATA_SYNC_GET_FILECOPY_DATA_FAILURE	=	AFRES_DATA_SYNC + 5;
	public final static long AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE		=	AFRES_DATA_SYNC + 6;
	public final static long AFRES_DATA_SYNC_CONNECT_TO_EDGE_FAILURE	=	AFRES_DATA_SYNC + 7;
	public final static long AFRES_DATA_SYNC_EDGE_NOT_MATCH				=	AFRES_DATA_SYNC + 8;
	public final static long AFRES_DATA_SYNC_BK_TO_EDGE_FAILURE			=	AFRES_DATA_SYNC + 9;
	public final static long AFRES_DATA_SYNC_VM_TO_EDGE_FAILURE			=	AFRES_DATA_SYNC + 10;
	public final static long AFRES_DATA_SYNC_VCM_TO_EDGE_FAILURE		=	AFRES_DATA_SYNC + 11;
	public final static long AFRES_DATA_SYNC_LOG_TO_EDGE_FAILURE		=	AFRES_DATA_SYNC + 12;
	public final static long AFRES_DATA_SYNC_FILECOPY_TO_EDGE_FAILURE	=	AFRES_DATA_SYNC + 13;
	public final static long AFRES_DATA_SYNC_NOT_MANAGED				=	AFRES_DATA_SYNC + 14;
	public final static long AFRES_DATA_SYNC_UNKNOWN_ERROR				=	AFRES_DATA_SYNC + 15;
}
