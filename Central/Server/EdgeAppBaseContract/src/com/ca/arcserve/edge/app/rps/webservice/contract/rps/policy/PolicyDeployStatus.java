/**
 * 
 */
package com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy;

/**
 * @author lijbi02
 * @version 1.0
 * Description : this class is used to describe RPS policy deploy status
 */
public enum PolicyDeployStatus {
	Undeploy, Deploying, Failed, Succeed, Removed;
	
	public static PolicyDeployStatus parseInt(int value) {
		switch (value) {
		case 0:
			return Undeploy;
		case 1:
			return Deploying;
		case 2:
			return Failed;
		case 3:
			return Succeed;
		case 4:
			return Removed;
		default:
			return Undeploy;
		}
	}
}
