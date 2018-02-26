package com.ca.arcflash.webservice.replication;

/////////////////////////////////////////////////////////////////////////////
// New ReplicationJobCommand object instance will not be created each time a
// job runs, each node will have and only have one ReplicationJobCommand, and
// will be reused by each job of the node. Therefore, if multiple jobs run at
// the same time and they use same instance variable to save different value,
// there will be problems.
//
// For instance, a job is doing replication, and then a new job got launched
// and find that a job is running, then it do some process and exit.
// Unfortunately, when the second job doing its own process, it uses some
// instance variables the first job is using and change their values, then the
// first job will get wrong values and run not properly.
//
// To keep those variables that will be changed in different job, we same them
// in a job context object. Each job run will have a job context.
//
// Here, we just defined a base job context, for subclasses, they may need
// more specific variables, then, they should create new job context class
// that derived from this base one, and then override the createJobContext()
// method of BaseReplicationCommand class.
//
// Pang, Bo (panbo01)
// 2013-02-01

public class BaseReplicationJobContext
{
	private ManualConversionUtility manualConversionUtility = null;

	public ManualConversionUtility getManualConversionUtility()
	{
		return manualConversionUtility;
	}

	public void setManualConversionUtility(
		ManualConversionUtility manualConversionUtility )
	{
		this.manualConversionUtility = manualConversionUtility;
	}
}
