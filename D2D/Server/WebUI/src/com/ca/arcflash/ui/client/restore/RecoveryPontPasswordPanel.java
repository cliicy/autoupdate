package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint.VerifyStatus;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;

public class RecoveryPontPasswordPanel extends LayoutContainer {
	
	private ListStore<EncrypedRecoveryPoint> store = new ListStore<EncrypedRecoveryPoint>();
	private ColumnModel columnModel;
	private Grid<EncrypedRecoveryPoint> grid;
	private Map<String, EncrypedRecoveryPoint> encrypedRecoveryPoint;
	private CommonServiceAsync service = GWT.create(CommonService.class);
	private String[] gettingPasswordArray;

	@Override
	public void render(Element target, int index) {
		super.render(target, index);
		
		store = new ListStore<EncrypedRecoveryPoint>();  
		
		GridCellRenderer<EncrypedRecoveryPoint> timeRenderer = new GridCellRenderer<EncrypedRecoveryPoint>() {

			@Override
			public Object render(EncrypedRecoveryPoint model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<EncrypedRecoveryPoint> store, Grid<EncrypedRecoveryPoint> grid) {
				Date datetime = model.getBackupDate();
				return Utils.formatDateToServerTime(datetime, model.getBackupTimeZoneOffset() != null?
						model.getBackupTimeZoneOffset() : 0);
			}
			
		};
		
		GridCellRenderer<EncrypedRecoveryPoint> nameRenderer = new GridCellRenderer<EncrypedRecoveryPoint>() {

			@Override
			public Object render(EncrypedRecoveryPoint model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<EncrypedRecoveryPoint> store, Grid<EncrypedRecoveryPoint> grid) {
				return model.getBackupJobName()==null?"":model.getBackupJobName();
			}
			
		};
		
		GridCellRenderer<EncrypedRecoveryPoint> iconRenderer = new GridCellRenderer<EncrypedRecoveryPoint>() {

			@Override
			public Object render(final EncrypedRecoveryPoint model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<EncrypedRecoveryPoint> store, final Grid<EncrypedRecoveryPoint> grid) {
				Text label = new Text();
				label.setStyleAttribute("color", "green");
				if( model.getPasswordVerified() == null || model.getPasswordVerified() == VerifyStatus.VERIFYING){
					label.setText(UIContext.Constants.loadingPassword());
					return label;
				}else if (model.getPasswordVerified() == VerifyStatus.SUCCESS_VERIFIED){
					label.setText(UIContext.Constants.restoreSessionPasswordPassed());
					return label;
				}else{
					Anchor link = new Anchor(UIContext.Constants.restoreSessionPasswordInput());
					link.addClickHandler(new ClickHandler(){

						@Override
						public void onClick(ClickEvent event) {
							final RecoveryPointPasswordWindow window = new RecoveryPointPasswordWindow(model);
							window.setModal(true);
							window.show();
							
						}
						
					});
					return link;
				}
			}
			
		};
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("Time", UIContext.Constants.restoreTimeColumn(), 120, timeRenderer));
		configs.add(Utils.createColumnConfig("Name", UIContext.Constants.restoreNameColumn(), 115, nameRenderer));
		configs.add(Utils.createColumnConfig("Time", UIContext.Constants.settingsLabelEncyrptionPassword(), 100, iconRenderer));
		
		columnModel = new ColumnModel(configs);
	    grid = new Grid<EncrypedRecoveryPoint>(store, columnModel);
	    grid.setLoadMask(true);
	    grid.setHeight(125);
	    grid.setAutoExpandColumn("Name");
	    
	    Label encrptlabel = new Label(UIContext.Constants.encryptionPassword());
		encrptlabel.addStyleName("restoreWizardSubItem");
		add(encrptlabel);
		
		encrptlabel = new Label(UIContext.Constants.recoveryPointsNeedSessionPassword());
		encrptlabel.addStyleName("restoreWizardSubItemDescription");
		
		add(encrptlabel);
	    add(grid);
	}


	public void setModel(Map<String, EncrypedRecoveryPoint> encrypedRecoveryPoint) {
		this.encrypedRecoveryPoint = encrypedRecoveryPoint;
		store.removeAll();
		store.setMonitorChanges(true);  
		Collection<EncrypedRecoveryPoint> points = encrypedRecoveryPoint.values();
		ArrayList<String> guidArray = new ArrayList<String>();
		
		for(EncrypedRecoveryPoint point: points){
			store.add(point);
			if( point.getPasswordVerified() == null || point.getPasswordVerified() == VerifyStatus.VERIFYING ){
				guidArray.add(point.getSessionGuid());
			}
		}
		
		if( !guidArray.isEmpty() ){
			gettingPasswordArray = guidArray.toArray(new String[0]);
			service.getSessionPasswordBySessionGuid(gettingPasswordArray, new BaseAsyncCallback<String[]>(){

				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					grid.reconfigure(store, columnModel);
				}

				@Override
				public void onSuccess(String[] result) {
					super.onSuccess(result);
					for (int i = 0; i < gettingPasswordArray.length; i++) {
						VerifyStatus status;
						
						// If result is 0 length (e.g. the session is not generated by local D2D),
						// accessing it will cause exception.
						if(result.length <= 0 || result[i] == null )
							status = VerifyStatus.FAIL_VERIFIED;
						else
							status = VerifyStatus.SUCCESS_VERIFIED;
						for(EncrypedRecoveryPoint point: store.getModels()){
							if( point.getSessionGuid() == gettingPasswordArray[i] ){
								point.setPasswordVerified(status);
								
								if (result.length > i)
								{
									point.setPassword(result[i]);
								}
							}
						}
					grid.reconfigure(store, columnModel);
					}
				}
			});
		}else{
			grid.reconfigure(store, columnModel);
		}
	}
	
	public Map<String, EncrypedRecoveryPoint> getModel(){
		return encrypedRecoveryPoint;
	}

	public boolean validate(){
		List<EncrypedRecoveryPoint> points = store.getModels();
		for (EncrypedRecoveryPoint point : points)
			if (point.getPasswordVerified()==null || point.getPasswordVerified()!=VerifyStatus.SUCCESS_VERIFIED){
				return false;
			}
			
		return true;
	}
}
