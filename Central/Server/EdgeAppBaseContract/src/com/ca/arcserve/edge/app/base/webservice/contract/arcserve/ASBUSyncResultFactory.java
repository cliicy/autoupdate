package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.util.List;
import java.util.Map;


public class ASBUSyncResultFactory {
	public static ASBUSyncResult createPrimaryRoleChangedAndHasPlanUseServerResult(String hostName, String[] planNames){
		ASBUSyncResult result = new PrimaryRoleChangedAndHasPlanUseServer(hostName, planNames);
		return result;
	}
	
	public static ASBUSyncResult createPrimaryRoleChangedAndNoPlanUseServerResult(String domain){
		ASBUSyncResult result = new PrimaryRoleChangedAndNoPlanUseServer(domain);
		return result;
	}
	
	public static ASBUSyncResult createMemberRoleChangedAndHasPlanUseServerResult(Map<String, String[]> serverPlanMap){
		ASBUSyncResult result = new MemberRoleChangedAndHasPlanUseServer(serverPlanMap);
		return result;
	}
	
	public static ASBUSyncResult createAsbuServerHasBeenControledByOtherConsoleResult(){
		ASBUSyncResult result = new AsbuServerHasBeenControledByOtherConsoleResult();
		return result;
	}
	
	public static ASBUSyncResult createSyncSuccessWithoutExceptionResult(){
		ASBUSyncResult result = new SyncSuccessWithoutException();
		return result;
	}
	
	public static ASBUSyncResult createSyncSuccessWithMemberServersDeleted(List<ASBUServerInfo> deletedServers){
		ASBUSyncResult result = new SyncSuccessWithMemberServersDeleted(deletedServers);
		return result;
	}
}
