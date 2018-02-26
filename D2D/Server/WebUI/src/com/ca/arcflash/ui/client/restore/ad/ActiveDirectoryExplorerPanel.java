package com.ca.arcflash.ui.client.restore.ad;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RecoveryPointItemModel;
import com.ca.arcflash.ui.client.restore.RestoreValidator;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ActiveDirectoryExplorerPanel extends LayoutContainer implements RestoreValidator 
{
	final LoginServiceAsync service = GWT.create(LoginService.class);
	CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	private RestoreWizardContainer wizard;
	
	private String destination;
	private String destUserName;
	private String destPassword;
	private String selectedSessionEncryptionKey;
	private long sessionNumber;
	private int subSessionID;
	public ADGridPanel gridPanel;
	public ADTreePanel treePanel;
	private boolean changed=false;
	
	public ActiveDirectoryExplorerPanel(RestoreWizardContainer restoreWizardContainer) 
	{
		this.wizard = restoreWizardContainer;
	}
	
	public void init(GridTreeNode gridTreeNode, RecoveryPointItemModel recoveryPointItemModel) {
		ADRecoveryPointsPanel previousPanel = wizard.getADRecoveryPointsPanel();
		destination = previousPanel.getSessionPath();
		destUserName = previousPanel.getUserName();
		destPassword = previousPanel.getPassword();
		sessionNumber = previousPanel.getSelectedSessionID();
//		sessionNumber = gridTreeNode.getSessionID();
		selectedSessionEncryptionKey = gridTreeNode.getEncryptedKey();
		subSessionID=recoveryPointItemModel.getSubSessionID().intValue();
	}
	
	@Override
	public boolean validate(AsyncCallback<Boolean> callback)
	{		
		List<GridTreeNode> selectedNodes = getSelectedTreeNodes();
		
		//Check 1: No selection
		if (selectedNodes.size() == 0) {
			final MessageBox errMessage = MessageBox.info(UIContext.Constants
					.restoreBrowseButton(), UIContext.Constants
					.restoreMustSelectFiles(),null);
			errMessage.setModal(true);
			errMessage.setIcon(MessageBox.ERROR);
			Utils.setMessageBoxDebugId(errMessage);
			errMessage.show();

			callback.onSuccess(Boolean.FALSE);
			return false;
		}
		
		callback.onSuccess(true);
		return true;
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		RowLayout rl = new RowLayout();
		this.setLayout(rl);
		this.setHeight("100%");
//		this.setScrollMode(Scroll.AUTOY);
		
		this.add(renderHeaderSection(), new RowData(1, -1));
		
		// heading
		LabelField labelHeading = new LabelField(UIContext.Constants.restoreADExplorerHead());
		this.add(labelHeading);
		//main browsing panel
//		this.add(renderMainSection(), new RowData(1, -1));
		this.add(renderMainSection(), new RowData(0.98, 1, new Margins(0, 0, 0, 0)));
	}
	
	private Widget renderMainSection() {
		LayoutContainer lc = new LayoutContainer();
		BorderLayout layout = new BorderLayout();
		lc.setLayout(layout);
		lc.setBorders(true);

		// 1. west tree panel
		treePanel = new ADTreePanel(this);

		BorderLayoutData leftData = new BorderLayoutData(LayoutRegion.WEST, 200);
		leftData.setSplit(true);
		leftData.setCollapsible(true);
		leftData.setHideCollapseTool(false);
		leftData.setFloatable(true);
		leftData.setMargins(new Margins(0, 0, 0, 0));

		lc.add(treePanel, leftData);

		// 2. mail grid panel
		gridPanel = new ADGridPanel(this);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));

		lc.add(gridPanel, centerData);

		return lc;
	}

	public void loadADNodes(final GridTreeNode current, final AsyncCallback<List<GridTreeNode>> callback){
		if(current != null){
			current.setBackupDestination(destination);
			current.setDestUser(destUserName);
			current.setDestPwd(destPassword);
			current.setSessionID(sessionNumber);
			current.setSubSessionID(subSessionID);
			current.setEncryptedKey(selectedSessionEncryptionKey);
			service.getADNodes(current, callback);
		}
	}
	
	public void loadADPagingNodes(final GridTreeNode parent, PagingLoadConfig loadConfig, String filter, final AsyncCallback<PagingLoadResult<GridTreeNode>> callback){
		if(parent != null){
			parent.setBackupDestination(destination);
			parent.setDestUser(destUserName);
			parent.setDestPwd(destPassword);
			parent.setSessionID(sessionNumber);
			parent.setSubSessionID(subSessionID);
			parent.setEncryptedKey(selectedSessionEncryptionKey);
			service.getADPagingNodes(parent, loadConfig, filter, callback);
		}
	}
	
	public void loadADAttributes(final GridTreeNode current, final AsyncCallback<List<GridTreeNode>> callback){
		if(current != null){
			current.setBackupDestination(destination);
			current.setDestUser(destUserName);
			current.setDestPwd(destPassword);
			current.setSessionID(sessionNumber);
			current.setSubSessionID(subSessionID);
			current.setEncryptedKey(selectedSessionEncryptionKey);
			service.getADAttributes(current, callback);
		}
	}
	
	
	protected LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");		

		LabelField label = new LabelField();
		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_browse_ad_grt()).createImage();
		container.add(image, td);
		label.setValue(UIContext.Constants.restoreBrowseADButton());
		label.setStyleName("restoreWizardTitle");
		container.add(label);
		return container;
	}
	
	public List<GridTreeNode> getSelectedTreeNodes() {
		return treePanel.getSelectedNodes();
	}
	
	private List<GridTreeNode> getSelectedGridNodes(){
		List<GridTreeNode> subNodes = gridPanel.getAllSelectedNodes();
		//filter dirty data. some selected node should be clear once their patent is none-selected.
		List<GridTreeNode> noneSelectedTreeNodes = treePanel.getNoneSelectedNodes();
		for(GridTreeNode parent : noneSelectedTreeNodes){
			ADRestoreUtils.removeChildren(parent, subNodes);
		}
		return subNodes;
	}
	
	public List<GridTreeNode> getSelectedNodes() {
		List<GridTreeNode> result= new ArrayList<GridTreeNode>();
		List<GridTreeNode> subNodes = getSelectedGridNodes();
		List<GridTreeNode> parents = getSelectedTreeNodes();
		for(GridTreeNode parent: parents){
			ADRestoreUtils.deduplicateNode(parent, subNodes);
			if (parent.getSelectionType() == GridTreeNode.SELECTION_TYPE_FULL) {
				result.add(parent);
				ADRestoreUtils.removeChildren(parent, subNodes);
			}
		}
		result.addAll(subNodes);
		ADRestoreUtils.mergSelectedList(result);
		return result;
	}
	
	public String getDestination() {
		return destination;
	}

	public long getSessionNumber() {
		return sessionNumber;
	}

	public void firePageMasked(Integer size) {
		AppEvent event = new AppEvent(RestoreWizardContainer.onRestoreDateChanged, size);       ///D2D Lite Integration
		event.setSource(RestoreWizardContainer.PAGE_AD_EXPLORER);
		fireEvent(RestoreWizardContainer.onRestoreDateChanged, event);	
	}
	
	public void clear(){
		if(treePanel!=null){
			treePanel.clear();
			gridPanel.clear();
		}
	}
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public void repaint() {
		super.repaint();
		if(treePanel!=null&&changed){
			treePanel.populateTree();
		}
	}
}
