#include "StdAfx.h"
#include "ApmFactory.h"
#include "EdgeAPM.h"
#include "ApmBackendStatus.h"
#include "StatusObserver.h"

const CStatusObserver& CApmFactory::GetEdgeStatusObserver(){
	static CStatusObserver observer(APM_EDGE_PROCESS_RUNNING_GUID, APM_EDGE_PROCESS_BUSY_GUID);
	return observer;
}