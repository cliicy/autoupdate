package com.arcserve.edge.rbac.webservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.arcserve.edge.rbac.model.Permission;
import com.arcserve.edge.rbac.model.Role;
import com.arcserve.edge.webservice.facade.IWSO2ServiceFacade;
import com.arcserve.edge.webservice.facade.WSO2ServiceFacadeClientFactory;
import com.arcserve.edge.webservice.facade.exception.WSO2FacadeException;
import com.arcserve.edge.webservice.facade.exception.WSO2FacadeExceptionCode;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

public class RBACServiceImpl implements IRBACService {
	private static final Logger logger = Logger.getLogger(RBACServiceImpl.class);
	private String hostname = "localhost";
	private int port = EdgeCommonUtil.getEdgeWebServicePort();
	private String protocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
	EdgeWebServiceImpl serviceImpl;
	public RBACServiceImpl(EdgeWebServiceImpl serviceImpl){
		this.serviceImpl = serviceImpl;
	}
	@Override
	public List<Permission> getUserAllPermissions(String username)
			throws EdgeServiceFault {
		List<Permission> permissions = new ArrayList<>();
		try{
			IWSO2ServiceFacade facade = WSO2ServiceFacadeClientFactory.getService(hostname, port, protocol);
			permissions = facade.getUserAllPermissions(username);
		} catch (WSO2FacadeException e){
			logger.error(e.getMessage(),e);
			if(e.getCode().equals(WSO2FacadeExceptionCode.WSO2_REMOTE_EXCEPTION.toString())){
				throw new EdgeServiceFault(new EdgeServiceFaultBean(EdgeServiceErrorCode.WSO2_RemoteException));
			}
			if(e.getCode().equals(WSO2FacadeExceptionCode.WSO2_USER_ADMIN_EXCEPTION.toString())){
				throw new EdgeServiceFault(new EdgeServiceFaultBean(EdgeServiceErrorCode.WSO2_UserAdminException));
			}
		} catch (ServerSOAPFaultException e){
			logger.error("[RBACServiceImpl] getUserAllPermissions(),username : "+ username +", ServerSOAPFaultException :" + e.getMessage(),e);
			throw new EdgeServiceFault(new EdgeServiceFaultBean(EdgeServiceErrorCode.WSO2_GetUserPermissionsFail));
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return permissions;
	}
	
	@Override
	public List<Role> getUserAllRoles(String username) throws EdgeServiceFault {
		List<Role> roles = new ArrayList<>();
		try{
			IWSO2ServiceFacade facade = WSO2ServiceFacadeClientFactory.getService(hostname, port, protocol);
			roles = facade.getRoles(username);
		} catch (WSO2FacadeException e){
			logger.error(e.getMessage(),e);
			if(e.getCode().equals(WSO2FacadeExceptionCode.WSO2_REMOTE_EXCEPTION.toString())){
				throw new EdgeServiceFault(new EdgeServiceFaultBean(EdgeServiceErrorCode.WSO2_RemoteException));
			}
			if(e.getCode().equals(WSO2FacadeExceptionCode.WSO2_USER_ADMIN_EXCEPTION.toString())){
				throw new EdgeServiceFault(new EdgeServiceFaultBean(EdgeServiceErrorCode.WSO2_UserAdminException));
			}
		} catch (ServerSOAPFaultException e){
			logger.error("[RBACServiceImpl] getUserAllRoles() ,username : "+ username +", ServerSOAPFaultException :" + e.getMessage(),e);
			throw new EdgeServiceFault(new EdgeServiceFaultBean(EdgeServiceErrorCode.Common_Service_Dao_Execption));
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		return roles;
	}
}
