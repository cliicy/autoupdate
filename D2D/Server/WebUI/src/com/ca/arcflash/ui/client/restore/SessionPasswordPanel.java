package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.EncryptedRecoveryPointModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Element;

public class SessionPasswordPanel extends Dialog {
	
	private static final String TIMECOL = "Time";

	private CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	private final int width = 480;
	private final int height = 250;
	
	private List<EncryptedRecoveryPointModel> sessions;
	
//	private Map<String, List<EncryptedRecoveryPointModel>> eSessions = 
//		new HashMap<String, List<EncryptedRecoveryPointModel>>();
	private Map<String, String> pwdsByHash = new HashMap<String, String>();
	
	private ListStore<EncryptedRecoveryPointModel>store ;
	private EditorGrid<EncryptedRecoveryPointModel> grid;
	
	private String backupDest;
	
	private String domain;
	
	private String userName;
	
	private String password;
	
	private Button okButton;
	
	private PanelCallback callback;
	
	public SessionPasswordPanel(String dest, String domain, String userName, String password, 
			List<RecoveryPointModel> recoveryPoints, PanelCallback cb) {
		sessions = this.covertToEncryptedRecoveryPointModel(recoveryPoints);
		this.setWidth(width);
		this.setHeight(height);
		this.setButtonAlign(HorizontalAlignment.CENTER);
		this.callback = cb; 
		this.backupDest = dest;
		this.domain = domain;
		this.userName = userName;
		this.password = password;
		
		this.getButtonBar().removeAll();
		
		okButton = new Button();
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				updatePasswordToBackend();
			}
		});
		okButton.setId(OK);
		okButton.setText(UIContext.Constants.ok());
		okButton.ensureDebugId("9B188B33-818D-4fff-AA3B-A960376AD6CB");
		okButton.setMinWidth(50);
		okButton.setAutoWidth(true);
		okButton.setStyleAttribute("margin-right", "5px");
		this.addButton(okButton);
		
		final Button cancelButton = new Button();
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide(cancelButton);
				if(callback != null) {
					callback.onCancelClicked();
				}
			}
		});
		cancelButton.setId(CANCEL);
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.ensureDebugId("9B188B33-818D-4fff-AA3B-A960376AD6CB");
		cancelButton.setMinWidth(50);
		cancelButton.setAutoWidth(true);
		this.addButton(cancelButton);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		this.setHeadingHtml(UIContext.productNameD2D);
		this.setLayout(new RowLayout());
		
		LabelField desc = new LabelField();
		desc.setValue(UIContext.Constants.restoreInputPassword());
		
		add(desc, new RowData(1, -1));
		Grid<EncryptedRecoveryPointModel> grid = addPasswordTable();
		add(grid, new RowData(1, -1));		
		
		this.setScrollMode(Scroll.AUTOY);
		this.setOnEsc(false);
		this.setClosable(false);	
		this.setModal(true);
	}
	
	private void updatePasswordToBackend() {		
		for(int i = 0; i < store.getCount(); i ++){
			//get the password text field and valid it
			TextField<String> text = (TextField<String>) grid.getView().getWidget(i, 2);
			if(text.getValue() == null || text.getValue().isEmpty()){
				text.validate();
				this.popUpError(UIContext.Constants.encryptionPasswordEmpty());
				return;
			}else {
				//the change listener may not work in some cases, especially for UI automation,
				//so set value again here.
				store.getAt(i).setSessionPwd(text.getValue());
				this.pwdsByHash.put(store.getAt(i).getEncryptPwdHashKey(), text.getValue());
			}
		}
		
		commonService.updateSessionPassword(backupDest, domain, userName, password, sessions, 
				new BaseAsyncCallback<List<RecoveryPointModel>>(){
					@Override
					public void onFailure(Throwable caught) {
						callback.onUpdatePasswordFailed(caught);
						hide(okButton);
					}

					@Override
					public void onSuccess(List<RecoveryPointModel> result) {
						if(result == null || result.size() == 0) {
							callback.onUpdatePasswordSuccessfull(pwdsByHash);	
							hide(okButton);
						}else {
							sessions.clear();
//							eSessions.clear();
							sessions = covertToEncryptedRecoveryPointModel(result);
							store.removeAll();
							store.add(sessions);
							popUpError(UIContext.Constants.restorePasswordWrong());
						}
					}
			
		});
	}
	
	private void popUpError(String errorMsg) {
		MessageBox msg = new MessageBox();
		msg.setIcon(MessageBox.ERROR);
		msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		msg.setMessage(errorMsg);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}	
	
	private Grid<EncryptedRecoveryPointModel> addPasswordTable() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
		column = new ColumnConfig();
		column.setId(TIMECOL);
		column.setHeaderHtml(UIContext.Constants.restoreTimeColumn());
		GridCellRenderer<EncryptedRecoveryPointModel> timeRenderer = new GridCellRenderer<EncryptedRecoveryPointModel>() {
			@Override
			public Object render(EncryptedRecoveryPointModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<EncryptedRecoveryPointModel> store,
					Grid<EncryptedRecoveryPointModel> grid) {
				// Recovery Point table - Modified time column.
				
				String time = "";
				Date datetime = model.getTime();
				if (datetime != null) {
					if (model.getTimeZoneOffset() != null) {
						TimeZone serverTZ = TimeZone.createTimeZone(model
								.getTimeZoneOffset()
								/ (-1000 * 60));
						//Show time and date of the session, added by wanqi06
						time = FormatUtil.getTimeDateFormat().format(datetime, serverTZ);
					}else
						time =  Utils.formatDateToServerTime(datetime);
				}
				
				return time;
			}
		};
		column.setRenderer(timeRenderer);
		column.setWidth(140);
		column.setMenuDisabled(true);
		configs.add(column);
	
		column = new ColumnConfig();
		column.setId("Name");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setMenuDisabled(true);
		column.setWidth(130);
		configs.add(column);
		
		TextField<String> editField = new TextField<String>();
		editField.setPassword(true);
		CellEditor editor = new CellEditor(editField);
		
		column = new ColumnConfig();
		column.setId("encryptPwd");
		column.setHeaderHtml(UIContext.Constants.restorePassword());
		column.setRenderer(new GridCellRenderer<EncryptedRecoveryPointModel>(){

			@Override
			public Object render(EncryptedRecoveryPointModel model,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<EncryptedRecoveryPointModel> store,
					Grid<EncryptedRecoveryPointModel> grid) {
				final EncryptedRecoveryPointModel emodel = model;
				final TextField<String> txtField = new TextField<String>();
				txtField.setAllowBlank(false);
				txtField.addListener(Events.Change, new Listener<BaseEvent>(){
					@Override
					public void handleEvent(BaseEvent be) {
//						List<EncryptedRecoveryPointModel> models = 
//							eSessions.get(emodel.getEncryptPwdHashKey());
//						for(EncryptedRecoveryPointModel m : models)
//							m.setSessionPwd(txtField.getValue());
						pwdsByHash.put(emodel.getEncryptPwdHashKey(), txtField.getValue());
						emodel.setSessionPwd(txtField.getValue());
					}
				});
				txtField.setWidth(85);
				txtField.setBorders(false);
				txtField.setPassword(true);
				return txtField;
			}
		});
		column.setMenuDisabled(true);
//		column.setEditor(editor);
		column.setWidth(140);
		configs.add(column);	

		ColumnModel columnModel = new ColumnModel(configs);	
		
		store = new ListStore<EncryptedRecoveryPointModel>();		
		store.setStoreSorter(new StoreSorter<EncryptedRecoveryPointModel>(){
			@Override
			 public int compare(Store<EncryptedRecoveryPointModel> store, EncryptedRecoveryPointModel m1, EncryptedRecoveryPointModel m2, String property) {
				if(TIMECOL.equalsIgnoreCase(property))
				{
					if(m1.getTime().getTime() > m2.getTime().getTime())
						return 1;
					else if(m1.getTime().getTime() < m2.getTime().getTime())
						return -1;
					else
						return 0;
				}
				else return super.compare(store, m1, m2, property);				
				}}
		);
		store.add(sessions);
		store.sort(TIMECOL, SortDir.DESC);
		
		grid = new EditorGrid<EncryptedRecoveryPointModel>(store, columnModel);
		grid.setAutoExpandColumn("Name");
		grid.setAutoExpandMax(5000);
		grid.setHeight(120);
		grid.setBorders(true);
		grid.setStripeRows(true);
		
		return grid;
	}
	
	private EncryptedRecoveryPointModel convertToEncryptedModel(RecoveryPointModel model) {
		EncryptedRecoveryPointModel eModel = new EncryptedRecoveryPointModel();
		eModel.setName(model.getName());
		eModel.setTime(model.getTime());
		eModel.setEncryptPwdHashKey(model.getEncryptPwdHashKey());
		eModel.setSessionGuid(model.getSessionGuid());
		eModel.setSessionID(model.getSessionID());
		eModel.setTimeZoneOffset(model.getTimeZoneOffset());
		return eModel;
	}
	
	private List<EncryptedRecoveryPointModel> covertToEncryptedRecoveryPointModel(
			List<RecoveryPointModel> models) {
		List<EncryptedRecoveryPointModel> eModels = 
			new ArrayList<EncryptedRecoveryPointModel>();
		
		for(RecoveryPointModel model : models) {
			String hashKey = model.getEncryptPwdHashKey();
			if(!(model instanceof EncryptedRecoveryPointModel)) {
				EncryptedRecoveryPointModel eModel = convertToEncryptedModel(model);
				if(pwdsByHash.get(eModel.getEncryptPwdHashKey()) == null) {
					eModels.add(eModel);
				}
			}else {
				eModels.add((EncryptedRecoveryPointModel)model);
			}
			
			pwdsByHash.put(hashKey, " ");
//			List<EncryptedRecoveryPointModel> pModels = eSessions.get(model.getEncryptPwdHashKey());
//			if(pModels == null) {
//				pModels = new ArrayList<EncryptedRecoveryPointModel>();
//				eSessions.put(model.getEncryptPwdHashKey(), pModels);
//				eModels.add(eModel);
//			}
//			pModels.add(eModel);
		}
		
		return eModels;
	}

	
	
	public static interface PanelCallback {
		void onCancelClicked();
		void onUpdatePasswordSuccessfull(Map<String, String> pwdsByHash);
		void onUpdatePasswordFailed(Throwable t);
	}
}
