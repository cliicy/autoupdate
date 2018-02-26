package com.ca.arcflash.ui.client.restore.mailboxexplorer;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.restore.RestoreValidator;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class MailboxExplorerPanel extends LayoutContainer implements RestoreValidator 
{
	private MailboxExplorerContext context;

	private MailboxListPanel mailboxListPanel;
	private FolderTreePanel folderTreePanel;
	private MailGridPanel mailGridPanel;
	
	private LabelField labelHeading;
		
	
	public MailboxExplorerPanel()
	{
		super();
	}	
	
	public MailboxExplorerPanel(Window w) 
	{
		
	}	

	@Override
	public boolean validate(AsyncCallback<Boolean> callback)
	{		
		// call the recovery points panel to valid the sources, because it has all the sources.
		if (context != null && context.getRecoveryPointsPanel() != null)
		{
			return context.getRecoveryPointsPanel().validateForExplorer(callback);
		}
		else
		{
			return true;
		}
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		// TODO Auto-generated method stub
		super.onRender(parent, index);
		
		RowLayout rl = new RowLayout();
		this.setLayout(rl);
		this.setHeight("100%");
		this.setScrollMode(Scroll.AUTOY);
		
		this.add(renderHeaderSection(), new RowData(1, -1));
		
		// heading
		labelHeading = new LabelField();
		this.add(labelHeading);
		updateHeading();
		
		// layout the panels
		LayoutContainer lc = new LayoutContainer();
		{
			BorderLayout layout = new BorderLayout();
			lc.setLayout(layout);

			// 1. west
			LayoutContainer west = new LayoutContainer();

			{
				// 1.1  mailbox list panel				
				BorderLayout layoutLeft = new BorderLayout();
				west.setLayout(layoutLeft);

				mailboxListPanel = new MailboxListPanel(this);
				
				BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
				centerData.setMargins(new Margins(0));
				
				west.add(mailboxListPanel, centerData);
				
				// 1.2 folder tree panel
				folderTreePanel = new FolderTreePanel(this);
				
				BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 255, 50, 400);									
				southData.setCollapsible(true);
				southData.setFloatable(true);
				southData.setHideCollapseTool(false);
				southData.setSplit(true);
				southData.setMargins(new Margins(5, 0, 0, 0));
				
				west.add(folderTreePanel, southData);
			}
			
			BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 250);
			westData.setSplit(true);
			westData.setCollapsible(true);
			westData.setHideCollapseTool(false);
			westData.setFloatable(true);
			westData.setMargins(new Margins(0, 5, 0, 0));
			
			lc.add(west, westData);

			// 2. mail grid panel
			mailGridPanel = new MailGridPanel(this);
			
			BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
			centerData.setMargins(new Margins(0));
			
			lc.add(mailGridPanel, centerData);
		}

		this.add(lc, new RowData(0.98, 1, new Margins(0, 0, 0, 0)));		
	}
	
	protected LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_browse_exchange_grt()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		if (context.isRestoreManager()) {
			label.setValue(UIContext.Constants.restoreBrowseExchangeGRTButton());
		} else {
			label.setValue(UIContext.Constants.manageRecoveryPoints());
		}
		label.setStyleName("restoreWizardTitle");
		container.add(label);
		return container;
	}
	
	protected void updateHeading()
	{
		String strHeading = "";
		GridTreeNode edbNode = context.getEdbNode();
		if (edbNode != null)
		{
			if (edbNode.getDisplayPath() != null)
			{
				strHeading = edbNode.getDisplayPath() + "\\" + edbNode.getDisplayName();
			}
		}
		
		labelHeading.setValue(strHeading);
		labelHeading.setTitle(strHeading);
	}
	
////////////////////////////////
	// called when item is selected
	
	public void onSelectedMailbox(GridTreeNode node)
	{
		if (folderTreePanel != null)
		{
			folderTreePanel.setParentNode(node);
		}
	}

	public void onSelectedFolder(GridTreeNode node)
	{
		if (mailGridPanel != null)
		{			
			mailGridPanel.setParentNode(node);
		}
	}
	
	////////////////////////////////
	// called when item's check box is checked
	
	public void onCheckedMailbox(GridTreeNode node, int selectState)
	{
		if (folderTreePanel != null)
		{
			folderTreePanel.onParentChecked(node, selectState);
		}		
	}
	
	public void onCheckedFolder(GridTreeNode node, int selectState)
	{
		if (mailGridPanel != null)
		{
			mailGridPanel.onParentChecked(node, selectState);
		}
	}
	
	public void checkFolderAndParent(GridTreeNode node, int selectState)
	{
		if (folderTreePanel != null)
		{
			folderTreePanel.checkNodeAndParent(node, selectState);
		}	
	}
	
	public void checkMailboxAndParent(GridTreeNode node, int selectState)
	{
		if (mailboxListPanel != null)
		{
			mailboxListPanel.checkNodeAndParent(node, selectState);
		}	
	}
	
	public MailboxExplorerContext getContext()
	{
		return context;
	}

	// set context of the mailbox explorer
	public void setContext(MailboxExplorerContext context)
	{
		boolean bContextEqual = true;	
		
		if (this.context != null && context != null)
		{
			bContextEqual = this.getContext().equals(context);	
		}		
		
		this.context = context;
		
		if (this.rendered)
		{
			if (!bContextEqual)
			{
				if (mailboxListPanel != null)
				{
					mailboxListPanel.setParentNode(context.getEdbNode());
					this.updateHeading();
				}
			}
			else
			{
				if (mailboxListPanel != null)
				{
					// update the check boxes' statuses
					mailboxListPanel.onParentChecked(context.getEdbNode(), context.getEdbCheckBox().getSelectedState());
				}
			}			
		}
	}
	
	// clear the panels
	// release all the catalog handles since there is no chance to release the last handles within the panel
	public void clearMailboxExplorerPanel()
	{	
		if (mailboxListPanel != null)
		{
			mailboxListPanel.clearPanel();
		}
		
		if (folderTreePanel != null)
		{
			folderTreePanel.clearPanel();
		}
		
		if (mailGridPanel != null)
		{
			mailGridPanel.clearPanel();
		}
	}
	
	// reload the mailbox explorer after the contents are cleared
	public void reloadMailboxExplorerIfNecessary()
	{
		if (this.rendered)
		{
			if (mailboxListPanel != null && mailboxListPanel.isCleared())
			{
				mailboxListPanel.setParentNode(context.getEdbNode());
				this.updateHeading();
			}
		}
	}
	
	public void updateNextButtonStatus(Integer size)
	{	
		AppEvent event = new AppEvent(RestoreWizardContainer.onRestoreDateChanged, size);
		event.setSource(RestoreWizardContainer.PAGE_EXCHANGE_GRT_MAIL_EXPLORER);
		fireEvent(RestoreWizardContainer.onRestoreDateChanged, event);
	}
}
