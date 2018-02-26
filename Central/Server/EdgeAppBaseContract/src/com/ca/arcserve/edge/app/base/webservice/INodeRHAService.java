/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.OffsiteVCMConverterInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAControlService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAScenario;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAParameters;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.ImportNodeFromRHAResult;

/**
 * @author lijwe02
 * 
 */
public interface INodeRHAService {

	/**
	 * Retrieve RHA Scenario list from control service
	 * 
	 * @param controlService
	 *            The control service to connect
	 * @return The RHA scenario list
	 * @throws EdgeServiceFault
	 *             failed to get scenario list
	 */
	List<RHAScenario> getScenarioList(RHAControlService controlService) throws EdgeServiceFault;

	/**
	 * Retrieve all source nodes from RHA Control Service
	 * 
	 * @param controlService
	 *            The control service information
	 * @return the source node list
	 * @throws EdgeServiceFault
	 *             failed to get scenario list
	 */
	List<RHASourceNode> getSourceNodeList(RHAControlService controlService) throws EdgeServiceFault;

	/**
	 * Insert source nodes to database
	 * 
	 * @param parameters
	 *            The RHA and nodes information
	 * @return Import result
	 * @throws EdgeServiceFault
	 *             Failed to import nodes to database
	 */
	ImportNodeFromRHAResult importNodeFromRHA(ImportNodeFromRHAParameters parameters) throws EdgeServiceFault;

	/**
	 * Retrieve all control service in the database
	 * 
	 * @param serverNamePrefix
	 *            The prefix of the server name
	 * 
	 * @return the cotnrol service list in the database
	 * @throws EdgeServiceFault
	 *             Failed to query the control service information
	 */
	List<RHAControlService> getControlServiceList(String serverNamePrefix) throws EdgeServiceFault;

	/**
	 * Retrieve the converter information by the host id
	 * 
	 * @param hostId
	 *            The host id to retrieve converter information
	 * @return The Converter information relate to the host
	 * @throws EdgeServiceFault
	 *             Failed to get the converter information
	 */
	OffsiteVCMConverterInfo getOffsiteVCMConverterInfoByHostId(int hostId) throws EdgeServiceFault;
}
