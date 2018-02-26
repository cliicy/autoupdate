package com.ca.arcflash.ui.client.restore.mailboxexplorer;

import java.util.HashMap;

import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.restore.ExtEditorTreeGrid;

public class MailboxExplorerParameter
{
	public GridTreeNode edbNode;
	public FlashCheckBox edbCheckbox;
	public HashMap<GridTreeNode, MailboxExplorerContext> mailboxContextMap;
	public ExtEditorTreeGrid treeGrid;	
	
	public MailboxExplorerParameter()
	{
		this.edbNode = null;
		this.edbCheckbox = null;
		this.mailboxContextMap = null;
		this.treeGrid = null;
	}
	
	public boolean isValid()
	{
		boolean bValid = false;
		
		if (edbNode != null && edbCheckbox != null && mailboxContextMap != null && treeGrid != null)
		{
			bValid = true;
		}
		
		return bValid;
	}	
}
