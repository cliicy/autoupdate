/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

/**
 * @author lijwe02
 * 
 */
public enum VCMConverterType {
	/**
	 * Source Agent backup to share folder
	 */
	Agent,
	/**
	 * Source Agent backup to RPS Server
	 */
	Agent2RPSServer,
	/**
	 * Source HBBU Proxy backup to share folder
	 */
	HBBUProxy,
	/**
	 * Source HBBP Proxy backup to RPS Server
	 */
	HBBUProxy2RPSServer,
	/**
	 * The conversion source is replicate task, and the source is agent
	 */
	RPSServer2RPSServerForAgent,
	/**
	 * The conversion source is replicate task, and the source is HBBU Proxy
	 */
	RPSServer2RPSServerForHBBU,
	/**
	 * The conversion source is MSP Server replicate task
	 */
	RPSServer2MSPRPSServer,
	/**
	 * The conversion source is session folder which replicated by RHA
	 */
	RHASessionDestination,
	/**
	 * Unknown type
	 */
	Unknown;

	public static boolean isHbbuConverter(VCMConverterType converterType) {
		return (HBBUProxy == converterType || HBBUProxy2RPSServer == converterType || RPSServer2RPSServerForHBBU == converterType);
	}
}
