package com.ca.arcserve.edge.app.base.webservice.actioncenter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeActionItem;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeActionCenterDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions.InvalidCategoryParamException;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions.NoActionOwnerException;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionCategory;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItem;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItemId;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionSeverity;

public class ActionCenter implements IActionCenter
{
	private static Logger logger = Logger.getLogger( ActionCenter.class );
	private static ActionCenter instance = new ActionCenter();
	
	private IEdgeActionCenterDao actionCenterDao = DaoFactory.getDao( IEdgeActionCenterDao.class );
	
	private ActionCenter()
	{
	}
	
	public static ActionCenter getInstance()
	{
		return instance;
	}
	
	private String getLogPrefix()
	{
		return this.getClass().getSimpleName() + "." +
			Thread.currentThread().getStackTrace()[2].getMethodName() + "(): ";
	}
	
	private IActionOwner getActionOwner( ActionCategory actionCategory )
	{
		return ActionOwnerRegistry.getInstance().getActionOwner( actionCategory );
	}

	@Override
	public ActionItemId addActionItem( ActionCategory actionCategory,
		int action, ActionSeverity severity, String description, Object categoryParam )
		throws NoActionOwnerException, InvalidCategoryParamException
	{
		if (actionCategory == null)
			throw new IllegalArgumentException( "actionCategory is null" );
		
		IActionOwner actionOwner = this.getActionOwner( actionCategory );
		if (actionOwner == null)
			throw new NoActionOwnerException( actionCategory );
		
		int[] newIds = new int[1];
		this.actionCenterDao.addActionItem(
			actionCategory.getValue(),
			actionOwner.convertCategoryParamToString( categoryParam ),
			action, severity.getValue(), description, newIds );
		
		return new ActionItemId( newIds[0] );
	}

	@Override
	public void deleteActionItem( ActionItemId id )
	{
		if (id == null)
			throw new IllegalArgumentException( "id is null" );
		
		this.actionCenterDao.deleteActionItem( id.getRecordId() );
	}

	@Override
	public List<ActionItem> getActionItems( ActionCategory actionCategory, Object categoryParam )
		throws NoActionOwnerException, InvalidCategoryParamException
	{
		if (actionCategory == null)
			throw new IllegalArgumentException( "actionCategory is null" );
		
		IActionOwner actionOwner = this.getActionOwner( actionCategory );
		if (actionOwner == null)
			throw new NoActionOwnerException( actionCategory );
		
		List<EdgeActionItem> daoItemList = new ArrayList<>();
		this.actionCenterDao.getActionItems(
			actionCategory.getValue(),
			actionOwner.convertCategoryParamToString( categoryParam ),
			daoItemList );
		
		List<ActionItem> itemList = new ArrayList<>();
		for (EdgeActionItem daoItem : daoItemList)
			itemList.add( this.convertDaoActionItem( daoItem ) );
		
		return itemList;
	}

	@Override
	public void deleteActionItems( ActionCategory actionCategory, Object categoryParam )
		throws NoActionOwnerException, InvalidCategoryParamException
	{
		if (actionCategory == null)
			throw new IllegalArgumentException( "actionCategory is null" );
		
		IActionOwner actionOwner = this.getActionOwner( actionCategory );
		if (actionOwner == null)
			throw new NoActionOwnerException( actionCategory );
		
		this.actionCenterDao.deleteActionItems(
			actionCategory.getValue(),
			actionOwner.convertCategoryParamToString( categoryParam ) );
	}

	@Override
	public void deleteActionItem(
		ActionCategory actionCategory, Object categoryParam, int action )
		throws NoActionOwnerException, InvalidCategoryParamException
	{
		if (actionCategory == null)
			throw new IllegalArgumentException( "actionCategory is null" );
		
		IActionOwner actionOwner = this.getActionOwner( actionCategory );
		if (actionOwner == null)
			throw new NoActionOwnerException( actionCategory );
		
		this.actionCenterDao.deleteActionItemByAction(
			actionCategory.getValue(),
			actionOwner.convertCategoryParamToString( categoryParam ),
			action );
	}

	@Override
	public List<ActionItem> getAllActionItems()
		throws NoActionOwnerException, InvalidCategoryParamException
	{
		List<EdgeActionItem> daoItemList = new ArrayList<>();
		this.actionCenterDao.getAllActionItems( daoItemList );
		
		List<ActionItem> itemList = new ArrayList<>();
		for (EdgeActionItem daoItem : daoItemList)
			itemList.add( this.convertDaoActionItem( daoItem ) );
		
		return itemList;
	}
	
	private ActionItem convertDaoActionItem( EdgeActionItem daoActionItem )
		throws NoActionOwnerException, InvalidCategoryParamException
	{
		if (daoActionItem == null)
			throw new IllegalArgumentException( "daoActionItem is null" );
		
		ActionCategory actionCategory = ActionCategory.fromValue( daoActionItem.getActionCategory() );
		IActionOwner actionOwner = this.getActionOwner( actionCategory );
		if (actionOwner == null)
			throw new NoActionOwnerException( actionCategory );
		
		ActionItem actionItem = new ActionItem();
		actionItem.setId( new ActionItemId( daoActionItem.getId() ) );
		actionItem.setActionCategory( ActionCategory.fromValue( daoActionItem.getActionCategory() ) );
		actionItem.setCategoryParam( actionOwner.parseCategoryParamString( daoActionItem.getCategoryParam() ) );
		actionItem.setAction( daoActionItem.getAction() );
		actionItem.setSeverity( ActionSeverity.fromValue( daoActionItem.getSeverity() ) );
		actionItem.setDescription( daoActionItem.getDescription() );
		actionItem.setCreationDate( daoActionItem.getCreationDate() );
		
		return actionItem;
	}

}
