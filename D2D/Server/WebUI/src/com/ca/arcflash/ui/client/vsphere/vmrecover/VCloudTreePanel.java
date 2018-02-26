package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.VCloudDirectorModel;
import com.ca.arcflash.ui.client.model.VCloudOrgnizationModel;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class VCloudTreePanel extends LayoutContainer {
	private final LoginServiceAsync service = GWT.create(LoginService.class);

	private TreeGrid<BaseModelData> vCloudTreeGrid;
	private TreeStore<BaseModelData> vCloudTreeStore = new TreeStore<BaseModelData>();
	
	private BaseAsyncCallback<BaseModelData> callback;
	private VCloudDirectorModel inputVCloudModel;

	private FlexTable descriptionTable;
	private LabelField verifyDescriptionLabel;
	private Image verifyStatusImg;
	
	private TextField<String> userTextField;
	private PasswordTextField passwordField;
	private NumberField portNumberField = new NumberField();
	private RadioGroup protocolGroup;
	private Radio httpProtocol;
	private Radio httpsProtocol;
	
	private LabelField returnLabel;
	private Button verifyButton;
	
	private static int LABEL_WIDTH = 65;
	private static int FIELD_WIDTH = 170;
	private String maskMsgLeft = null;
	private String maskMsgTop = null;
	private VCloudVirtualDataCenterModel selectedVDCModel;

	public VCloudTreePanel(BaseAsyncCallback<BaseModelData> callback) {
		this.callback = callback;
		createContent();
	}
	
	public void setVCloudDirectorModel(VCloudDirectorModel vCloudModel) {
		this.inputVCloudModel = vCloudModel;
		initVCloudInfo();
		
		if (isRendered()) {
			getVCloudOrganizations();
		}
	}
	
	public LabelField getReturnButton() {
		return returnLabel;
	}
	
	private void initVCloudInfo() {
		String password = inputVCloudModel.getPassword();
		if (password == null) {
			password = "";
		}
		inputVCloudModel.setPassword(password);
	}
	
	private void createContent() {
		this.setWidth(600);
		this.setHeight(420);
		this.setLayout(new RowLayout());
		
		ColumnConfig nameColumnConf = new ColumnConfig("name", UIContext.Constants.vAppRestoreVCloudTreeNameHeader(), 230);
		nameColumnConf.setRenderer(new WidgetTreeGridCellRenderer<BaseModelData>() {
			@Override
			public Widget getWidget(BaseModelData model, String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				String name = model.get(property);
				LabelField label = new LabelField(name);
				if (!Utils.isEmptyOrNull(name)) {
					label.setToolTip(name);
				}
				return label;
			}
		});
						
		ColumnConfig desColumnConf =  Utils.createColumnConfig("description", UIContext.Constants.vAppRestoreVCloudTreeDescriptionHeader(), 200,
				new GridCellRenderer<BaseModelData>() {
					@Override
					public Object render(BaseModelData model, String property, ColumnData config, int rowIndex,
							int colIndex, ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
						String description = "";
						if (model instanceof VCloudDirectorModel) {
							description = UIContext.Constants.vAppRestoreVCloudDirector();
						} else if (model instanceof VCloudOrgnizationModel) {
							description = UIContext.Constants.vAppRestoreVCloudOrganization();
						} else if (model instanceof VCloudVirtualDataCenterModel) {
							description = UIContext.Constants.vAppRestoreVCloudVDC();
							if (((VCloudVirtualDataCenterModel) model).getIsMatchedOriginal()) {
								description = UIContext.Constants.vAppRestoreVCloudOriginalVDC();
							}
						}
						LabelField label = new LabelField(description);
						if (!Utils.isEmptyOrNull(description)) {
							label.setToolTip(description);
						}
						return label;
					}
				});
		List<ColumnConfig> columnConfList = new ArrayList<>();
		columnConfList.add(nameColumnConf);
		columnConfList.add(desColumnConf);

		vCloudTreeGrid = new TreeGrid<BaseModelData>(vCloudTreeStore,  new ColumnModel(columnConfList));
		vCloudTreeGrid.setAutoLoad(true);
		vCloudTreeGrid.setAutoWidth(true);
		vCloudTreeGrid.setHeight(245);
		vCloudTreeGrid.setColumnLines(true);
		vCloudTreeGrid.setAutoExpand(true);
		vCloudTreeGrid.setAutoExpandColumn("name");
		vCloudTreeGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		vCloudTreeGrid.getStyle().setNodeOpenIcon(AbstractImagePrototype.create(UIContext.IconBundle.vsphere_vdc()));
		vCloudTreeGrid.getStyle().setNodeCloseIcon(AbstractImagePrototype.create(UIContext.IconBundle.vsphere_vdc()));
		vCloudTreeGrid.setIconProvider(new ModelIconProvider<BaseModelData>() {
			@Override
			public AbstractImagePrototype getIcon(BaseModelData model) {
				ImageResource icon = null;
				if (model instanceof VCloudDirectorModel) {
					icon = UIContext.IconBundle.vsphere_vcloud();
				} else if (model instanceof VCloudOrgnizationModel) {
					icon = UIContext.IconBundle.vsphere_organization();
				} else if (model instanceof VCloudVirtualDataCenterModel) {
					icon = UIContext.IconBundle.vsphere_vdc();
				} else {
					icon = UIContext.IconBundle.vsphere_vdc();
				}
				return AbstractImagePrototype.create(icon);
			}
		});
		vCloudTreeGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<BaseModelData>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModelData> se) {
				verifyButton.setEnabled(false);
				if (se != null) {
					GridSelectionModel<BaseModelData> selections = vCloudTreeGrid.getSelectionModel();
					if (selections != null) {
						BaseModelData selectedItem = selections.getSelectedItem();
						if (selectedItem instanceof VCloudVirtualDataCenterModel) {
							VCloudVirtualDataCenterModel vDCModel = (VCloudVirtualDataCenterModel) selectedItem;
							List<VirtualCenterModel> vcList = vDCModel.getVCenters();
							if (vcList == null || vcList.size() == 0) {
								SpecifyDestinationDialog.showErrorMessage(UIContext.Constants.vAppRestoreVCloudTreeNoVCError());
								if (selectedVDCModel != null) {
									verifyButton.setEnabled(true);
								}
								return;
							}

							selectedVDCModel = vDCModel;
							VirtualCenterModel vcModel = vcList.get(0);
							setVerifyingDescriptions(VCVerifyingStatus.REFESH, vcModel.getVcName());
							userTextField.setValue(vcModel.getUsername());
							portNumberField.setValue(vcModel.getPort());
							if ("HTTP".equalsIgnoreCase(vcModel.getProtocol())) {
								httpsProtocol.setValue(Boolean.FALSE);
								httpProtocol.setValue(Boolean.TRUE);
							} else {
								httpsProtocol.setValue(Boolean.TRUE);
								httpProtocol.setValue(Boolean.FALSE);
							}
						} else {
							vCloudTreeGrid.getSelectionModel().deselectAll();
							setVerifyingDescriptions(VCVerifyingStatus.NOVC, null);
							return;
						}
					}
				}
				if (selectedVDCModel != null) {
					verifyButton.setEnabled(true);
				}
			}
		});
		new QuickTip(vCloudTreeGrid);
		
		LayoutContainer window = new LayoutContainer();
		window.ensureDebugId("678990fb-0937-4696-a548-1454323c45a5");
		window.setStyleName("x-window-mc");
		window.setBorders(true);
		window.setScrollMode(Scroll.NONE);
		window.setLayout(new FlowLayout());
		window.add(vCloudTreeGrid, new FlowData(10));
		add(window, new RowData(1, 1, new Margins(10)));
		
		FlexTable bottomFlexTable = new FlexTable();
		bottomFlexTable.setCellPadding(5);
		bottomFlexTable.setCellSpacing(5);
		bottomFlexTable.ensureDebugId("7f3f878a-3f27-4834-bf4f-9affa26942f5");
		window.add(bottomFlexTable, new RowData(1, -1, new Margins(10)));
		
		descriptionTable = new FlexTable();
		bottomFlexTable.getFlexCellFormatter().setColSpan(0, 0, 6);
		bottomFlexTable.setWidget(0, 0, descriptionTable);
		verifyDescriptionLabel = new LabelField();
		verifyDescriptionLabel.ensureDebugId("cb2803aa-db98-48fb-b438-9bc5539176d5");
		descriptionTable.setWidget(0, 0, verifyDescriptionLabel);
		setVerifyingDescriptions(VCVerifyingStatus.NOVC, null);
		
		LabelField userLabel = new LabelField(UIContext.Constants.vAppRestoreVCloudUserLabel());
		userLabel.ensureDebugId("3871278a-7ede-46fe-97ac-8c6966cb7d1f");
		userLabel.setWidth(LABEL_WIDTH);
		bottomFlexTable.setWidget(1, 0, userLabel);
		userTextField = new TextField<>();
		userTextField.ensureDebugId("0d4f39af-7044-4bac-b3d2-b80da60bd29a");
		userTextField.setAllowBlank(false);
		userTextField.setValidateOnBlur(true);
		userTextField.setWidth(FIELD_WIDTH);
		bottomFlexTable.setWidget(1, 1, userTextField);
		
		LabelField emptyLabel = new LabelField("");
		emptyLabel.setWidth(LABEL_WIDTH / 2);
		bottomFlexTable.setWidget(1, 2, emptyLabel);
		
		LabelField passwordLabel = new LabelField(UIContext.Constants.vAppRestoreVCloudPasswordLabel());
		passwordLabel.ensureDebugId("1360241f-1fb6-4798-ada7-5b163e14696f");
		passwordLabel.setWidth(LABEL_WIDTH);
		bottomFlexTable.setWidget(1, 4, passwordLabel);
		passwordField = new PasswordTextField();
		passwordField.ensureDebugId("d42bf5c9-f9bd-4beb-965b-48e0deac4265");
		passwordField.setWidth(FIELD_WIDTH);
		passwordField.setPassword(true);
		bottomFlexTable.setWidget(1, 5, passwordField);
		
		LabelField portLabel = new LabelField(UIContext.Constants.vAppRestoreVCloudPortLabel());
		portLabel.ensureDebugId("16c8ff39-f8f5-487d-b07e-b6d526e006f1");
		portLabel.setWidth(LABEL_WIDTH);
		bottomFlexTable.setWidget(2, 0, portLabel);
		portNumberField = new NumberField();
		portNumberField.ensureDebugId("948c4d57-acbd-4eb0-90e3-04af34113542");
		portNumberField.setAllowBlank(false);
		portNumberField.setValidateOnBlur(true);
		portNumberField.setWidth(FIELD_WIDTH/2);
		bottomFlexTable.setWidget(2, 1, portNumberField);
		
		LabelField protocolLabel = new LabelField("Protocol");
		protocolLabel.ensureDebugId("ec3785fc-13a0-4aab-9f08-8b9ce2ba5611");
		protocolLabel.setWidth(LABEL_WIDTH);
		bottomFlexTable.setWidget(2, 4, protocolLabel);
		FlexTable protocolFlexTable = new FlexTable();
		protocolFlexTable.ensureDebugId("3b75694f-4ed8-4638-b240-c0fa70cd545a");
		bottomFlexTable.setWidget(2, 5, protocolFlexTable);
		
		httpsProtocol = new Radio();
		httpsProtocol.ensureDebugId("2de8642f-92cf-4ad3-992a-d8550a139640");
		httpsProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttps());
		httpsProtocol.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (httpsProtocol.getValue() == true) {
					portNumberField.setValue(443);
				}
			}
		});
		httpsProtocol.setValue(Boolean.TRUE);
		protocolFlexTable.setWidget(0, 0, httpsProtocol);

		httpProtocol = new Radio();
		httpProtocol.ensureDebugId("ecca990b-d494-4319-84c7-e4bf8caaa96d");
		httpProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttp());
		httpProtocol.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (httpProtocol.getValue() == true) {
					portNumberField.setValue(80);
				}
			}
		});
		protocolFlexTable.setWidget(0, 1, httpProtocol);

		protocolGroup = new RadioGroup();
		protocolGroup.add(httpProtocol);
		protocolGroup.add(httpsProtocol);
		
		returnLabel = new LabelField(" < " + UIContext.Constants.vAppRestoreVCloudConLogin()) {
			@Override
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
				Style style = getElement().getStyle();
				style.setVerticalAlign(VerticalAlign.MIDDLE);
				style.setTextDecoration(TextDecoration.UNDERLINE);
				style.setColor("blue");
				style.setCursor(Cursor.POINTER);
			}
		};
		returnLabel.ensureDebugId("70cfdc34-1985-4d38-82ff-f3b7a3b9b8bd");
		bottomFlexTable.setWidget(3, 0, returnLabel);
		
		verifyButton = new Button(UIContext.Constants.vAppRestoreVCloudTreeVerify());
		verifyButton.ensureDebugId("9d39c28c-573c-43fc-a21b-bb5b8852d856");
		verifyButton.setEnabled(false);
		bottomFlexTable.getFlexCellFormatter().setHorizontalAlignment(3, 5, HorizontalAlignmentConstant.startOf(Direction.RTL));
		bottomFlexTable.setWidget(3, 5, verifyButton);
		
		verifyButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				validateVC();
			}
		});

		vCloudTreeStore.setKeyProvider(new ModelKeyProvider<BaseModelData>() {
			@Override
			public String getKey(BaseModelData model) {
				return model.get("id");
			}
		});
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		getVCloudOrganizations();
	}
	
	private void validateVC() {
		if (selectedVDCModel == null) {
			SpecifyDestinationDialog.showErrorMessage(UIContext.Constants.vAppRestoreVCloudTreeNoVDCSelectedError());
			verifyButton.setEnabled(false);
			return;
		}
		
		if (userTextField.isValid()) {
			userTextField.clearInvalid();
		} else {
			return;
		}
		
		if (portNumberField.isValid()) {
			portNumberField.clearInvalid();
		} else {
			return;
		}
		
		final String serverName = selectedVDCModel.getVCenters().get(0).getVcName();
		
		final VirtualCenterModel vcModel = new VirtualCenterModel();
		vcModel.setVcName(serverName);
		vcModel.setUsername(userTextField.getRawValue());
		String password = passwordField.getRawValue();
		vcModel.setPassword(password == null ? "" : password);
		vcModel.setPort(portNumberField.getValue().intValue());
		vcModel.setProtocol(httpProtocol.getValue() == true ? "HTTP" : "HTTPS");
		
		mask(UIContext.Constants.vAppRestoreVCloudTreeVerifyMask());
		service.validateVC(vcModel, new BaseAsyncCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				if (result != null && result == 0) {
					setVerifyingDescriptions(VCVerifyingStatus.OK, serverName);
					selectedVDCModel.setVCenters(Arrays.asList(vcModel));
					callback.onSuccess(selectedVDCModel);
					unmask();
				}else{
					setVerifyingDescriptions(VCVerifyingStatus.ERROR, serverName);
					showErrorMessage(UIContext.Messages.vAppRestoreVCloudTreeConnectError(serverName));
					callback.onFailure(null);
					unmask();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				setVerifyingDescriptions(VCVerifyingStatus.ERROR, serverName);
				super.onFailure(caught);
				unmask();
			}
		});
	}
	
	private void setVerifyingDescriptions(VCVerifyingStatus status, String vcName) {
		if (VCVerifyingStatus.REFESH == status) {
			verifyDescriptionLabel.setValue(UIContext.Messages.vAppRestoreVCloudTreeVerifyDescription(vcName));
			verifyStatusImg = new Image();
			verifyStatusImg.setVisible(false);
			verifyStatusImg.setSize("16px", "16px");
			descriptionTable.setWidget(0, 1, verifyStatusImg);
		} else if (VCVerifyingStatus.OK == status) {
			verifyDescriptionLabel.setValue(UIContext.Messages.vAppRestoreVCloudTreeVerifiedDescription(vcName));
			verifyStatusImg = new Image(UIContext.IconBundle.status_okay_16x16());
			verifyStatusImg.setVisible(true);
			descriptionTable.setWidget(0, 1, verifyStatusImg);
		} else if (VCVerifyingStatus.ERROR == status) {
			verifyDescriptionLabel.setValue(UIContext.Messages.vAppRestoreVCloudTreeFailedToVerify(vcName));
			verifyStatusImg = new Image(UIContext.IconBundle.status_failed_16x16());
			verifyStatusImg.setVisible(true);
			descriptionTable.setWidget(0, 1, verifyStatusImg);
		} else {
			verifyDescriptionLabel.setValue(UIContext.Constants.vAppRestoreVCloudTreeVerifyDescription());
			verifyStatusImg = new Image();
			verifyStatusImg.setVisible(false);
			verifyStatusImg.setSize("16px", "16px");
			descriptionTable.setWidget(0, 1, verifyStatusImg);
		}
	}
	
	private void getVCloudOrganizations() {
		final String maskMsg = UIContext.Constants.vAppRestoreLoading();
		vCloudTreeStore.removeAll();
		setMaskMsgLeftAndTop(mask(maskMsg));
		
		service.getVCloudOrganizations(inputVCloudModel, new BaseAsyncCallback<List<VCloudOrgnizationModel>>() {
			@Override
			public void onSuccess(List<VCloudOrgnizationModel> orgModelList) {
				if (orgModelList == null || orgModelList.isEmpty()) {
					VAppRecoveryOptionsWizard.showErrorMessage(UIContext.Constants.vAppRestoreVCloudTreeNoOrg());
					extractMaskMsgLeftAndTop(mask(maskMsg));
					unmask();
					super.onFailure(null);
				} else {
					inputVCloudModel.setOrganizations(orgModelList);
					fillVCloudTreeStore();
					extractMaskMsgLeftAndTop(mask(maskMsg));
					unmask();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				VAppRecoveryOptionsWizard.showErrorMessage(UIContext.Constants.vAppRestoreVCloudTreeFailedToGetOrg());
				unmask();
				super.onFailure(null);
			}
		});
	}
	
	private void extractMaskMsgLeftAndTop(El maskEl) {
		if (maskEl != null) {
			Element maskMsgEl = maskEl.nextSibling();
			if (maskMsgEl != null) {
				Style msgStyle = maskMsgEl.getStyle();
				if (msgStyle != null) {
					maskMsgLeft = msgStyle.getLeft();
					maskMsgTop = msgStyle.getTop();
				}
			}
		}
	}
	
	private void setMaskMsgLeftAndTop(El maskEl) {
		if (maskEl != null && maskMsgLeft != null && maskMsgTop != null) {
			Element maskMsgEl = maskEl.nextSibling();
			if (maskMsgEl != null) {
				Style msgStyle = maskMsgEl.getStyle();
				if (msgStyle != null) {
					msgStyle.setProperty("left", maskMsgLeft);
					msgStyle.setProperty("top", maskMsgTop);
				}
			}
		}
	}
	
	private void fillVCloudTreeStore() {
		vCloudTreeStore.removeAll();

		if (inputVCloudModel == null) {
			return;
		}
		vCloudTreeStore.add(inputVCloudModel, false);

		List<VCloudOrgnizationModel> orgList = inputVCloudModel.getOrganizations();
		if (orgList == null || orgList.isEmpty()) {
			return;
		}
		
		VCloudVirtualDataCenterModel matchedVDCById = null;
		VCloudVirtualDataCenterModel matchedVDCByName = null;
		String originalVDCId = null;
		String originalVDCName = null;
		VCloudVirtualDataCenterModel originalVDC = inputVCloudModel.getOriginalVDC();
		if (originalVDC != null) {
			originalVDCId = originalVDC.getId();
			originalVDCName = originalVDC.getName();
		}
		
		for (VCloudOrgnizationModel org : orgList) {
			vCloudTreeStore.add(inputVCloudModel, org, false);

			List<VCloudVirtualDataCenterModel> vDCList = org.getVitrualDataCenters();
			if (vDCList == null || vDCList.isEmpty()) {
				continue;
			}
			for (VCloudVirtualDataCenterModel vDC : vDCList) {
				vCloudTreeStore.add(org, vDC, false);
				vDC.setIsMatchedOriginal(Boolean.FALSE);
				
				if (matchedVDCById == null && isMatchedVDCById(originalVDCId, vDC)) {
					matchedVDCById = vDC;
				}
				if (matchedVDCByName == null && isMatchedVDCByName(originalVDCName, vDC)) {
					matchedVDCByName = vDC;
				}
			}
		}
		
		VCloudVirtualDataCenterModel matchedVDC = null;
		if (matchedVDCById != null) {
			matchedVDCById.setIsMatchedOriginal(Boolean.TRUE);
			matchedVDC = matchedVDCById;
		} else if (matchedVDCByName != null) {
			matchedVDCByName.setIsMatchedOriginal(Boolean.TRUE);
			matchedVDC = matchedVDCByName;
		}
		
		vCloudTreeGrid.expandAll();
		if (matchedVDC != null) {
			vCloudTreeGrid.getSelectionModel().select(matchedVDC, false);
		}
	}
	
	private boolean isMatchedVDCById(String originalVDCId, VCloudVirtualDataCenterModel vDCModel) {
		if (originalVDCId == null) {
			return false;
		}
		
		return originalVDCId.equalsIgnoreCase(vDCModel.getId());
	}
	
	private boolean isMatchedVDCByName(String originalVDCName, VCloudVirtualDataCenterModel vDCModel) {
		if (originalVDCName == null) {
			return false;
		}
		
		return originalVDCName.equals(vDCModel.getId());
	}
	
	private enum VCVerifyingStatus {
		NOVC, REFESH, OK, ERROR
	}
}
