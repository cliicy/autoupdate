package com.ca.arcserve.edge.app.base.webservice.contract.actioncenter;

import java.io.Serializable;
import java.util.Date;

public class ActionItem implements Serializable
{
	private static final long serialVersionUID = 2082764660004938108L;

	private ActionItemId id;
	private ActionCategory actionCategory;
	private Object categoryParam; // category specific parameters
	private int action;
	private ActionSeverity severity;
	private String description;
	private Date creationDate;
	
	public ActionItemId getId()
	{
		return id;
	}
	
	public void setId( ActionItemId id )
	{
		this.id = id;
	}

	public ActionCategory getActionCategory()
	{
		return actionCategory;
	}

	public void setActionCategory( ActionCategory actionCategory )
	{
		this.actionCategory = actionCategory;
	}

	public Object getCategoryParam()
	{
		return categoryParam;
	}

	public void setCategoryParam( Object categoryParam )
	{
		this.categoryParam = categoryParam;
	}

	public int getAction()
	{
		return action;
	}

	public void setAction( int action )
	{
		this.action = action;
	}

	public ActionSeverity getSeverity()
	{
		return severity;
	}

	public void setSeverity( ActionSeverity severity )
	{
		this.severity = severity;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate( Date creationDate )
	{
		this.creationDate = creationDate;
	}
}
