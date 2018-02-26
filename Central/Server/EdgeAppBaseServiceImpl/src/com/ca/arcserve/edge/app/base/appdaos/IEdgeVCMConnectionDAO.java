package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxHostMapInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.VCMConnectionInfo;

public interface IEdgeVCMConnectionDAO {
	
	int insert(@In(jdbcType = Types.NVARCHAR)String hostname, 
			@In(jdbcType = Types.NVARCHAR)String username,
			@EncryptSave @In(jdbcType = Types.NVARCHAR)String password,
			@EncryptSave @In(jdbcType = Types.NVARCHAR)String uuid,
			int protocol, int port);
	
	void update(int id,
			@In(jdbcType = Types.NVARCHAR)String hostname, 
			@In(jdbcType = Types.NVARCHAR)String username,
			@EncryptSave @In(jdbcType = Types.NVARCHAR)String password,
			@EncryptSave @In(jdbcType = Types.NVARCHAR)String uuid,
			int protocol, int port);
	
	void getVCMConnection(@ResultSet List<VCMConnectionInfo> connectionList);
	
	void deleteAll();
	
	void clearVCMVMMap();
	
	int isVCMExists(@In(jdbcType = Types.NVARCHAR)String hostname);
	
	void addVCMVMMap(int policyID, int vcmID);
	
	void getVMIDList(@ResultSet List<Integer> idList);
	
	void getImportedVMInstaceUUID(int id, @ResultSet List<String> instanceUUID);
}
