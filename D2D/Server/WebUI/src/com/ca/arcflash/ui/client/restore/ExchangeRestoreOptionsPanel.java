package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.ExchVersion;
import com.ca.arcflash.ui.client.model.ExchangeOptionModel;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ExchangeRestoreOptionsPanel extends RestoreOptionsPanel {
	final LoginServiceAsync service = GWT.create(LoginService.class);
	private Radio origLocRd;
	private Radio restore2RSGRd;
	private Radio dumpFileRd;
	private CheckBox replayLogCb;
	private Radio restore2RDBRd;
	private RadioGroup locationGroup;

	private HTML orginalLocationLabel;
	
	private BrowseWindow destinationDialog;

	private TextField<String> dumpLocTF;
	private Button btnDumpLocBrowse;

	private TextField<String> rdbTF;
	private CheckBox dismountAndMountCb;

	private ExchangeRestoreOptionsPanel thisPanel;
	private static int MIN_WIDTH = 90;
	private LabelField noteLabel;
	private LayoutContainer replicaOriginalWarning;

	public ExchangeRestoreOptionsPanel(RestoreWizardContainer restoreWizardWindow) {     ///D2D Lite Integration
		super(restoreWizardWindow);
		thisPanel = this;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setStyleAttribute("margin", "5px");
		TableLayout tl = new TableLayout();
		tl.setWidth("98%");
		tl.setColumns(3);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		this.setLayout(tl);

		TableData td = new TableData();
		td.setColspan(3);

		this.add(renderHeaderSection(), td);
		createDest(this, td);

		dismountAndMountCb = new CheckBox();
		dismountAndMountCb.ensureDebugId("1496F403-D1A3-4c7a-BF21-70A3007109AA");
		dismountAndMountCb.setBoxLabel(UIContext.Constants.restoreDismountMountDatabase());
		dismountAndMountCb.addStyleName("restoreWizardLeftSpacing");
		dismountAndMountCb.setValue(true);
		this.add(dismountAndMountCb, td);

		noteLabel = new LabelField();
		// noteLabel.addStyleName("restoreWizardLeftSpacing");
		noteLabel.setVisible(false);
		this.add(noteLabel, td);
		
		this.createReplicaOriginalWarning(this, td);

		setDefaults();
	}	
	
	private void createDest(LayoutContainer cp, TableData td) {
		Label label = new Label(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		cp.add(label, td);

		label = new Label(UIContext.Constants.restoreDestinationDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		cp.add(label, td);

		origLocRd = new Radio();
		origLocRd.ensureDebugId("B3654094-464E-42e5-8348-A7F8299D074D");
		origLocRd.setBoxLabel(UIContext.Constants.restoreToOriginalLocation());
		origLocRd.addStyleName("restoreWizardLeftSpacing");
		cp.add(origLocRd, td);
		
		orginalLocationLabel = new HTML(UIContext.Messages.restoreToOriginalLocationForVSphere(UIContext.productNameD2D));
		orginalLocationLabel.addStyleName("restoreWizardSubItemDescription");
		orginalLocationLabel.setVisible(false);
		cp.add(orginalLocationLabel,td);

		dumpFileRd = new Radio();
		dumpFileRd.ensureDebugId("AB33C828-C8D3-4b11-9C84-C16210B47E28");
		dumpLocTF = new TextField<String>();
		dumpLocTF.ensureDebugId("1EB841EF-2875-4153-AB3A-FB7C963F7DF4");
		dumpLocTF.setStyleAttribute("margin-right", "15px");
		dumpLocTF.setWidth(325);
		btnDumpLocBrowse = getBrowseButton(dumpLocTF);
		addRdTFBrowse(dumpFileRd, UIContext.Constants.restoreDumpFileOnly(),
				dumpLocTF, btnDumpLocBrowse, cp, td);

		replayLogCb = new CheckBox();
		replayLogCb.ensureDebugId("5FC94934-AD56-4b9b-A59E-993037610B0B");
		replayLogCb.setBoxLabel(UIContext.Constants
				.restoreReplayLogOnDatabase());
		replayLogCb.addStyleName("restoreWizardLeftSpacing");
		replayLogCb.setStyleAttribute("margin-left", "20px");
		cp.add(replayLogCb, td);

		restore2RSGRd = new Radio();
		restore2RSGRd.ensureDebugId("C1E45680-4E05-49d9-98E2-79A4F6AEAE0E");
		restore2RSGRd.setBoxLabel(UIContext.Constants.restoretoRSG());
		restore2RSGRd.addStyleName("restoreWizardLeftSpacing");
		cp.add(restore2RSGRd, td);

		restore2RDBRd = new Radio();
		restore2RDBRd.ensureDebugId("59186A9B-4B26-49eb-AAF8-C72F552F0F32");
		TableData rdbRdTd = new TableData();
		rdbRdTd.setWidth("40%");
		restore2RDBRd.setBoxLabel(UIContext.Constants.restoretoRDB());
		restore2RDBRd.addStyleName("restoreWizardLeftSpacing");
		cp.add(restore2RDBRd, rdbRdTd);

		rdbTF = new TextField<String>();
		rdbTF.ensureDebugId("EF4AFCB8-25F4-4836-A4B9-36381B6DFB4B");
		rdbTF.setEmptyText(UIContext.Constants.restoreRDBNameOrGUID());
		rdbTF.setAllowBlank(false);
		rdbTF.setWidth("80%");
		TableData rdbTFTd = new TableData();
		rdbTFTd.setColspan(2);
		rdbTFTd.setWidth("60%");
		cp.add(rdbTF, rdbTFTd);

		cp.add(pwdPane, td);
		
		locationGroup = new RadioGroup();
		locationGroup.add(origLocRd);
		locationGroup.add(dumpFileRd);
		locationGroup.add(restore2RSGRd);
		locationGroup.add(restore2RDBRd);

		locationGroup.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				Radio rd = locationGroup.getValue();
				ExchVersion exchVersion = RestoreContext.getExchVersion();
				noteLabel.setVisible(false);
				replicaOriginalWarning.setVisible(false);
				clearAndDisable();
				if (rd == origLocRd || rd == restore2RSGRd) {
					dismountAndMountCb.setEnabled(true);
					if (rd == restore2RSGRd) {
						showRSGNote();
					}
					if(rd == origLocRd){
						showReplicaOriginalWarning();
					}
				} else if (rd == dumpFileRd) {
					dumpLocTF.setEnabled(true);
					btnDumpLocBrowse.setEnabled(true);
					if (exchVersion != null
							&& exchVersion.getVersion() == ExchVersion.Exch2003
									.getVersion()) {
						thisPanel.replayLogCb.setEnabled(false);
					} else {
						thisPanel.replayLogCb.setEnabled(true);
					}
				} else if (rd == restore2RDBRd) {
					rdbTF.setEnabled(true);
					dismountAndMountCb.setEnabled(true);
				}
				
				if(replayLogCb.getValue())
					replayLogCb.setValue(false);
			}
		});
	}

	private void createReplicaOriginalWarning(LayoutContainer cp, TableData td) {
		replicaOriginalWarning = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setWidth("100%");
		replicaOriginalWarning.setLayout(tl);
		
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		replicaOriginalWarning.add(warningImage, tableData);
		
		LabelField label = new LabelField(UIContext.Constants.restoreReplicaOriginalWarning());		
		label.addStyleName("labelTextWrap");
		tableData = new TableData();
		replicaOriginalWarning.add(label, tableData);
		
		cp.add(replicaOriginalWarning, td);
	}
	
	private void setRDBVisible(boolean isVisible) {
		rdbTF.setVisible(isVisible);
		restore2RDBRd.setVisible(isVisible);
	}

	private void addRdTFBrowse(final Radio radio, final String radioLabel,
			final TextField<String> input, final Button btnbrowse,
			LayoutContainer cp, TableData td) {
		radio.setBoxLabel(radioLabel);
		radio.addStyleName("restoreWizardLeftSpacing");
		TableData rdTd = new TableData();
		rdTd.setWidth("40%");
		cp.add(radio, rdTd);

		input.setAllowBlank(false);
		input.setRegex(AbsoluteDirReg);
		input.getMessages().setRegexText(
				UIContext.Constants.restoreInvalidPath());

		// show error tip for UNC path
		input.setValidator(new Validator()
		{
			@Override
			public String validate(Field<?> field, String value)
			{
				if (Utils.isValidRemotePath(value))
				{
					String productName = UIContext.productNameD2D;
					if (UIContext.uiType == 1)
					{
						productName = UIContext.productNamevSphere;
					}

					return UIContext.Messages.restoreExchangeWriterToRemoteDisk(productName);
				}
				
				return null;
			}
			
		});

		TableData inputTd = new TableData();
		inputTd.setHorizontalAlign(HorizontalAlignment.RIGHT);
		inputTd.setWidth("55%");
		cp.add(input, inputTd);

		TableData brtd = new TableData();
		brtd.setHorizontalAlign(HorizontalAlignment.LEFT);
		brtd.setWidth("20%");
		cp.add(btnbrowse, brtd);
	}

	private Button getBrowseButton(final TextField<String> buttonFor) {
		return getBrowseButton(buttonFor, -1, -1);
	}

	private Button getBrowseButton(final TextField<String> buttonFor,
			final int rowIndex, final int colIndex) {
		Button browse = new Button();
		browse.ensureDebugId("ABE81473-CFF5-4696-95CB-DF610381B887");
		browse.setText(UIContext.Constants.restoreBrowse());
		browse.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				String dumpPath = dumpLocTF.getValue();
				
				// show error message for UNC path
				if (Utils.isValidRemotePath(dumpPath))
				{
					String productName = UIContext.productNameD2D;
					if (UIContext.uiType == 1)
					{
						productName = UIContext.productNamevSphere;
					}

					MessageBox msg = new MessageBox();
					msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
					msg.setMessage(UIContext.Messages.restoreExchangeWriterToRemoteDisk(productName));
					msg.setIcon(MessageBox.ERROR);
					Utils.setMessageBoxDebugId(msg);
					msg.show();
					
					return;
				}
				
				destinationDialog = new BrowseWindow(false, UIContext.Constants
						.restoreDestinationTitle());
				destinationDialog.setResizable(false);
				destinationDialog.setModal(true);
					
				if (dumpPath != null && dumpPath.trim().length() > 0)
				{
					destinationDialog.setInputFolder(dumpPath);
				}
					
				destinationDialog.show();

				destinationDialog.addWindowListener(new WindowListener() {
					public void windowHide(WindowEvent we) {
						if (destinationDialog.getLastClicked() == Dialog.CANCEL) {
							// Canceled
						} else {
							buttonFor.setValue(destinationDialog
									.getDestination());
						}
					}
				});
			}

		});
		browse.setMinWidth(MIN_WIDTH);
		return browse;
	}

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreOptions());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}

	protected void setDefaultValue(){
		
		RecoveryPointModel rp = restoreWizardWindow.getSelectedRecoveryPoint();
		if(rp.getVMHypervisor()==1){ //1= hyperv. From hyperv vm session we only support restore to dump
			origLocRd.disable();
			dumpFileRd.setValue(true);
			restore2RSGRd.disable();
			setRDBVisible(false);
			replayLogCb.setEnabled(false);
		}
		
		else{
			origLocRd.setValue(true);
			setRDBVisible(false);
			restore2RSGRd.setVisible(false);
			noteLabel.setVisible(false);
			replicaOriginalWarning.setVisible(false);
		}
	}
	
	protected void setVSphereDefaultValue(){
		RecoveryPointModel rp = restoreWizardWindow.getSelectedRecoveryPoint();
		if(rp.getVMHypervisor()==1){ //1= hyperv. From hyperv vm session we only support restore to dump
			origLocRd.disable();
			dumpFileRd.setValue(true);
			restore2RSGRd.disable();
			setRDBVisible(false);
			replayLogCb.setEnabled(false);
		}
		else{
			origLocRd.disable();
			origLocRd.setBoxLabel(UIContext.Constants
					.restoreToOriginalLocation()+" ("+UIContext.Constants.disable()+")");
			orginalLocationLabel.setVisible(true);
			dumpFileRd.setValue(true);
			restore2RDBRd.disable(); //restore to recovery database should be disabled for vmware hbbu exchange sessions
			locationGroup.fireEvent(Events.Change);
		}
	}

	@Override
	public int processOptions() {
		RestoreJobModel model = RestoreContext.getRestoreModel();
		model.exchangeOption = new ExchangeOptionModel();
		model.setDestinationPath("");
		model.setRDBName("");
		model.exchangeOption.setDisMoundAndMountDB(dismountAndMountCb
				.getValue());
		model.exchangeOption.setReplayLogOnDB(null);
		model.setEncryptPassword(pwdPane.getPassword());
		
		if (origLocRd.getValue()) {
			model.setDestType(DestType.OrigLoc.getValue());
		} else if (dumpFileRd.getValue()) {
			model.setDestType(DestType.DumpFile.getValue());
			model.setDestinationPath(Utils.getNormalizedPath(dumpLocTF.getValue()));
			model.exchangeOption.setReplayLogOnDB(replayLogCb.getValue());
			model.exchangeOption.setDisMoundAndMountDB(null);
		} else if (restore2RSGRd.getValue()) {
			model.setDestType(DestType.ExchRestore2RSG.getValue());
		} else if (restore2RDBRd.getValue()) {
			model.setDestType(DestType.ExchRestore2RDB.getValue());
			model.setRDBName(rdbTF.getValue());
		}
		return 0;
	}

	private boolean nodeIsExchWriter(GridTreeNode node) {
		Integer nodeType = node.getType();
		if (nodeType != null && nodeType == CatalogModelType.OT_VSS_EXCH_WRITER) {
			return true;
		}
		return false;
	}

	@Override
	public void repaint() {
		super.repaint();

		ExchVersion exchVersion = RestoreContext.getExchVersion();
		if (exchVersion == null)
			return;
		
		RecoveryPointModel rp = restoreWizardWindow.getSelectedRecoveryPoint();

		//To FIX TFS bug765180
		if(rp.getAgentBackupType()!=null&&rp.getAgentBackupType()==2){
			if(rp.getVMHypervisor()==1) //1 = hyperv. From hyperv vm session we only support restore to dump
				return;
			
			if(rp.getVMHypervisor()==0 && !restore2RDBRd.isEnabled()) //restore2RDBRd will be disabled similar as restore to original location disable when try restore from HBBU PRoxy UI.
				return;
		}

		Boolean isRestore2RSG = restore2RSGRd.getValue();
		Boolean isRestore2RDB = restore2RDBRd.getValue();
		Boolean isDumpSelected = dumpFileRd.getValue();
		boolean isRDBVisible = false;
		switch (exchVersion) {
		case Exch2003:
		case Exch2007: {
			setRDBVisible(false);
			restore2RSGRd.setVisible(true);
			restore2RSGRd.enable();
			replayLogCb.enable();
			if (Boolean.TRUE.equals(isRestore2RDB)
					|| Boolean.TRUE.equals(origLocRd.getValue())
					|| Boolean.TRUE.equals(isDumpSelected)) {
				locationGroup.setValue(restore2RSGRd);
			}
			break;
		}
		case Exch2010: 
		case Exch2013:
		case Exch2016:{
			isRDBVisible = true;
			setRDBVisible(isRDBVisible);
			restore2RDBRd.enable();
			replayLogCb.enable();
			if (Boolean.TRUE.equals(isRestore2RSG)
					|| Boolean.TRUE.equals(origLocRd.getValue())
					|| Boolean.TRUE.equals(dumpFileRd.getValue())) {
				locationGroup.setValue(restore2RDBRd);
				rdbTF.enable();
			}
			restore2RSGRd.setVisible(false);
			break;
		}
		}

		if (exchVersion.getVersion() == ExchVersion.Exch2003.getVersion()) {
			replayLogCb.setValue(false);
			replayLogCb.disable();
			restore2RSGRd.disable();
			if (Boolean.TRUE.equals(restore2RSGRd.getValue())) {
				if (Boolean.TRUE.equals(isDumpSelected)) {
					dumpFileRd.setValue(true);
				} else {
					origLocRd.setValue(true);
				}
			}
		}
		

		List<GridTreeNode> selectedNodes = RestoreContext
				.getRestoreRecvPointSources();
		TreeStore<GridTreeNode> treeStore = RestoreContext
				.getRestoreRecvPointTreeStore();

		boolean isWriterSelected = false;
		for (GridTreeNode item : selectedNodes) {
			isWriterSelected = nodeIsExchWriter(item);
			if (isWriterSelected) {
				break;
			}
		}

		if (restore2RSGRd.isVisible()) {// exch2k7 or exch2010
			// For Exchange, only support to select such levels for restore:
			// whole writer, storage group(only exist on exchange 2k3 and 2k7)
			// and database.
			// For Exchange 2k3
			// a) No RSG
			// For Exchange 2k7,
			// b)only support select one storage group or
			// c) databases in one storage group in one job,
			// For Exchange 2010
			// d) only support select one database to RDB in one restore job.
			// e) Public folder can�t be restore to RSG or RDB.
			// f) Whole writer selected, can�t be restore to RSG or RDB.

			Map<String, GridTreeNode> mapRSGSelf = new FastMap<GridTreeNode>();
			Map<String, GridTreeNode> mapRSGParent = new FastMap<GridTreeNode>();
			boolean isPubSelected = false;
			for (GridTreeNode item : selectedNodes) {
				if (CatalogModelType.OT_VSS_EXCH_LOGICALPATH == item.getType()) {
					mapRSGSelf.put(item.getName(), item);
				} else {
					GridTreeNode pItem = item;
					while ((pItem = treeStore.getParent(pItem)) != null
							&& (CatalogModelType.OT_VSS_EXCH_LOGICALPATH != pItem
									.getType()))
						;
					if (pItem != null) {
						mapRSGParent.put(pItem.getName(), pItem);
					}
				}
				if (CatalogModelType.OT_VSS_EXCH_COMPONENT_PUBLIC == item.getType()
						|| CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS == item.getType()) {
					isPubSelected = true;
				}
			}
			if (mapRSGSelf.keySet().size() + mapRSGParent.keySet().size() > 1
					|| isPubSelected || isWriterSelected) {
				restore2RSGRd.disable();
				if (Boolean.TRUE.equals(restore2RSGRd.getValue())) {
					if (Boolean.TRUE.equals(isDumpSelected)) {
						dumpFileRd.setValue(true);
					} else {
						origLocRd.setValue(true);
					}
				}
			}
		} else if (isRDBVisible) {
			boolean isPubSelected = false;
			Map<String, GridTreeNode> mapRDBSelf = new FastMap<GridTreeNode>();
			for (GridTreeNode item : selectedNodes) {
				GridTreeNode pItem = treeStore.getParent(item);
				
				if (pItem != null
						&& (CatalogModelType.OT_VSS_EXCH_COMPONENT_SELECTABLE == item.getType() 
								||CatalogModelType.OT_GRT_EXCH_MBSDB == item.getType() 
								|| CatalogModelType.OT_VSS_EXCH_COMPONENT == item.getType())) {
					mapRDBSelf.put(item.getName(), item);
				}
				if (CatalogModelType.OT_VSS_EXCH_COMPONENT_PUBLIC == item.getType()
					|| CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS == item.getType()
					/*added since EX GRT changed this type*/) {
					isPubSelected = true;
				}
			}
			if (mapRDBSelf.keySet().size() > 1 || isPubSelected
					|| isWriterSelected) {
				restore2RDBRd.disable();
				rdbTF.disable();
				if (Boolean.TRUE.equals(restore2RDBRd.getValue())) {
					if (Boolean.TRUE.equals(isDumpSelected)) {
						dumpFileRd.setValue(true);
					} else {
						origLocRd.setValue(true);
					}
				}
			}
		}		
		locationGroup.fireEvent(Events.Change);
		showRSGNote();
	}
	
	private void showReplicaOriginalWarning() {
		replicaOriginalWarning.setVisible(false);
		List<GridTreeNode> selectedNodes = RestoreContext
				.getRestoreRecvPointSources();
		TreeStore<GridTreeNode> treeStore = RestoreContext
				.getRestoreRecvPointTreeStore();
		
		for(GridTreeNode node : selectedNodes) {
			if(isReplicaWriterNode(node, treeStore)){
				replicaOriginalWarning.setVisible(true);
				break;
			}
		}
	}
	
	private boolean isReplicaWriterNode(GridTreeNode node, TreeStore<GridTreeNode> treeStore) {
		while(node != null) {
			if(node.getType() == CatalogModelType.OT_VSS_EXCH_REPLICA
					|| node.getType() == CatalogModelType.OT_VSS_EXCH_WRITER && node.getPath() != null
					&& node.getPath().startsWith(RecoveryPointsPanel.GUID_EXCHANGE_REPLICA_WRITER) ){
				return true;
			}else if(RecoveryPointsPanel.GUID_EXCHANGE_2013.equals(node.getDisplayName()) && node.getSelectionType()!=null && node.getSelectionType() == FlashCheckBox.FULL){
				if((node.isHasReplicaDB()!=null && node.isHasReplicaDB() == true)){
					return true;
				}
			}
			if(treeStore == null)
				return false;
			else
				node = treeStore.getParent(node);
		}
		return false;
	}
	
	private void showRSGNote() {
		if (!restore2RSGRd.isVisible() || !restore2RSGRd.isEnabled()
				|| Boolean.FALSE.equals(restore2RSGRd.getValue())) {
			noteLabel.setVisible(false);
			return;
		}

		boolean isShowNote = false;
		ExchVersion exchVersion = RestoreContext.getExchVersion();

		List<GridTreeNode> selectedNodes = RestoreContext
				.getRestoreRecvPointSources();

		TreeStore<GridTreeNode> treeStore = RestoreContext
				.getRestoreRecvPointTreeStore();

		List<GridTreeNode> publicFolderDbs = new ArrayList<GridTreeNode>();

		if (selectedNodes != null) {
			if (selectedNodes.size() == 1) {
				isShowNote = nodeIsRSG(selectedNodes.get(0))
						&& (exchVersion != null && exchVersion == ExchVersion.Exch2007);
				if (isShowNote) {
					isShowNote = false;
					List<GridTreeNode> children = treeStore
							.getChildren(selectedNodes.get(0));

					if (children != null && children.size() > 0) {
						for (GridTreeNode item : children) {
							if (CatalogModelType.OT_VSS_EXCH_COMPONENT_PUBLIC == item.getType()
									|| CatalogModelType.OT_GRT_EXCH_PUBLIC_FOLDERS == item.getType()) {
								publicFolderDbs.add(item);
							}
						}
					}
					if (publicFolderDbs.size() > 0) {
						isShowNote = true;
					}
				}
			}
		}

		if (isShowNote) {
			StringBuffer sb = new StringBuffer();
			int indx = 0;
			for (GridTreeNode item : publicFolderDbs) {
				sb.append(item.getDisplayName());
				if (++indx < publicFolderDbs.size()) {
					sb.append(",");
				}
			}
			noteLabel.setValue(UIContext.Messages.restoreRSGNote(sb.toString()));
			noteLabel.setVisible(true);
		} else {
			noteLabel.setVisible(false);
		}
	}

	private boolean nodeIsRSG(GridTreeNode node) {
		Integer nodeType = node.getType();
		if (nodeType != null
				&& CatalogModelType.OT_VSS_EXCH_LOGICALPATH == nodeType) {
			return true;
		}
		return false;
	}

	private void clearAndDisable() {
		replayLogCb.setEnabled(false);
		dismountAndMountCb.setEnabled(false);
		dumpLocTF.clearInvalid();
		dumpLocTF.setEnabled(false);
		rdbTF.clearInvalid();
		rdbTF.setEnabled(false);
		btnDumpLocBrowse.setEnabled(false);
	}

	@Override
	public boolean validate(AsyncCallback<Boolean> callback) {
		boolean isValid = true;
		if (dumpFileRd.getValue()) {
			isValid = dumpLocTF.validate();
		} else if (restore2RDBRd.getValue()) {
			isValid = rdbTF.validate();
		}
		
		if(isValid)
			checkSessionPassword(callback);
		else
			callback.onSuccess(false);

		return isValid;
	}
}
