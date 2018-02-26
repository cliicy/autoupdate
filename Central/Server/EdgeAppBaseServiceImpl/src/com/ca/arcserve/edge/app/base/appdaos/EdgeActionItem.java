package com.ca.arcserve.edge.app.base.appdaos;

import java.io.Serializable;
import java.util.Date;

public class EdgeActionItem implements Serializable
{
	private static final long serialVersionUID = 2082764660004938108L;

	private int id;
	private int actionCategory;
	private String categoryParam;
	private int action;
	private String description;
	private int severity;
	private Date creationDate;

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public int getActionCategory()
	{
		return actionCategory;
	}

	public void setActionCategory( int actionCategory )
	{
		this.actionCategory = actionCategory;
	}

	public String getCategoryParam()
	{
		return categoryParam;
	}

	public void setCategoryParam( String categoryParam )
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public int getSeverity()
	{
		return severity;
	}

	public void setSeverity( int severity )
	{
		this.severity = severity;
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
