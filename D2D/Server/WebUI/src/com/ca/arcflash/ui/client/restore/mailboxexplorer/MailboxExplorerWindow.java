package com.ca.arcflash.ui.client.restore.mailboxexplorer;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class MailboxExplorerWindow extends Window
{

	public static final int WINDOW_WIDTH = 798;
	public static final int WINDOW_HEIGHT = 500;

	private Window window;
	private boolean isCancelled = true;

	private MailboxExplorerPanel explorerPanel;

	public MailboxExplorerWindow(MailboxExplorerContext mbxExplorerContext)
	{
		this.window = this;

		String strHeading = "";
		GridTreeNode edbNode = mbxExplorerContext.getEdbNode();
		if (edbNode != null)
		{
			TreeStore<GridTreeNode> treeStore = mbxExplorerContext.getExtEditorTreeGrid().getTreeStore();
			if (treeStore != null)
			{
				StringBuilder sbPath = new StringBuilder(edbNode.getDisplayName());
				GridTreeNode curNode = treeStore.getParent(edbNode);

				for (int i = 0; i < 1 && (curNode != null); i++)
				{
					sbPath.insert(0, curNode.getDisplayName() + "\\");
					curNode = treeStore.getParent(curNode);
				}

				strHeading = UIContext.Constants.mailboxExplorerHeading() + " - " + sbPath.toString();
			}

		}
		this.setHeadingHtml(strHeading);
		this.setResizable(true);
		this.setMaximizable(true);
		this.setClosable(true);
		this.setHeight(WINDOW_HEIGHT);
		this.setWidth(WINDOW_WIDTH);
		this.setLayout(new FitLayout());

		// layout the panels
		explorerPanel = new MailboxExplorerPanel();
		explorerPanel.setContext(mbxExplorerContext);		

		this.add(explorerPanel);

		// buttons
		Button okBtn = new Button(UIContext.Constants.ok());
		okBtn.ensureDebugId("58FA0366-5EB1-46c8-B3C1-072E0B5BFEE8");
		okBtn.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				setCancelled(false);
				window.hide();
			}
		});

		this.addButton(okBtn);

		Button cancelBtn = new Button(UIContext.Constants.cancel());
		cancelBtn.ensureDebugId("16342ED5-63FD-4bc5-BA09-75E0029CD7DF");
		cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				setCancelled(true);
				window.hide();
			}
		});
		this.addButton(cancelBtn);
	}
	
	public void setCancelled(boolean isCancelled)
	{
		this.isCancelled = isCancelled;
	}

	public boolean isCancelled()
	{
		return isCancelled;
	}

	public MailboxExplorerContext getContext()
	{
		MailboxExplorerContext context = null;
		if (explorerPanel != null)
		{
			context = explorerPanel.getContext();
		}
		 
		return context;
	}	
}
