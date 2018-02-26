package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.ITransactionDao;
import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.sharefolder.ShareFolderDestinationInfo;

public interface ISharedFolderDao extends ITransactionDao {
	@StoredProcedure( name = "dbo.as_edge_sharefolderlist" )
	void as_edge_sharefolderlist( 
			int start_index,	
			int count,
			@In(jdbcType = Types.VARCHAR) String sort_column,
			int asc,
			int gatewayId,
			@Out int[] totalCount,
			@ResultSet List<ShareFolderDestinationInfo> sharedFolders );
	
	@StoredProcedure( name = "dbo.as_edge_sharefolder_addorupdate" )
	void as_edge_sharefolder_addorupdate(int gatewayId, String path, String username, @EncryptSave String password, @Out int[] destinationId );
	
	@StoredProcedure( name = "dbo.as_edge_sharefolderwithpassword" )
	void as_edge_sharefolderwithpassword( int destinationId, @ResultSet List<ShareFolderDestinationInfo> sharedFolder );
	
	@StoredProcedure( name = "dbo.as_edge_sharefolder_delete" )
	void as_edge_sharefolder_delete( int destinationId);
}
