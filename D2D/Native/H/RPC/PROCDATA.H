/****************************************************************
	PROCDATA.H

		TIRPC code from SUN was written with global data, and it
		was assumed that each process would have its own instance
		of the data.  Since NLM's data is shared among all 
		processes which access it, we need to put all non constant
		globals and process statics into a process data structure

****************************************************************/

typedef struct 
{
	int NLMID;		// For setting the owner's ID on NLM cleanup

	void *MemTree;	// Memory tree for freeing
	void *ClientList; // List of all CLIENT structures

	// ***** From SVC_GENE.C
	struct xlist *xprtlist;

	// ***** From GETNETC.C
	FILE *nc_file;  /* for netconfig database */
	struct netconfig_info *ni;

	// **** From RPC_COMD.C
	rpc_fd_set svc_fdset;
	void (*_svc_getreqset_proc)();
	struct rpc_createerr rpc_createerr;
	struct rpc_err	rac_senderr;

	// **** From ND_COMD.C
	int _nderror_;

	// **** From SVC.C
	struct svc_callout *svc_head;
	struct _svcxprt *xports[RPC_FD_SETSIZE];
	int num_xports;
	int svc_interrupted;

} PROCDATA;

extern PROCDATA *GetDataPointer(void);

