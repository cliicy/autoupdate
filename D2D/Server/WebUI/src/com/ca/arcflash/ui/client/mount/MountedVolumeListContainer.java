package com.ca.arcflash.ui.client.mount;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.MountedRecoveryPointItemModel;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;

public class MountedVolumeListContainer extends LayoutContainer {
	private final LoginServiceAsync service = GWT.create(LoginService.class);
	private ListStore<MountedRecoveryPointItemModel> mountItemStore;
	private Grid<MountedRecoveryPointItemModel> mountItemGrid;
	private MountWindow parentPanel;
	private ColumnModel columnModel;
	
	public MountedVolumeListContainer(MountWindow parentPanel){
		this.parentPanel = parentPanel;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setBorders(false);
		panel.setWidth(676);
	    panel.setLayout(new FitLayout());
	    panel.setHeight(100);
	    panel.add(getMountedVolumesGrid());
		this.add(panel);
		
		getAllMountedRecoveryPointItems();
	}
	
	private Grid<MountedRecoveryPointItemModel> getMountedVolumesGrid(){
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
		column.setId("action");
		column.setHeaderHtml(UIContext.Constants.disMountVolText());
		column.setMenuDisabled(true);
		column.setWidth(90);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {
			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				final String moutPath = model.getMountPath();
				final Integer mountDiskSignature = model.getMountDiskSignature();
				Label label = new Label(UIContext.Constants.disMountVolText());
				label.addStyleName("homepage_header_hyperlink_label");
				label.addStyleName("dismount_volume_label_icon");
				label.ensureDebugId("71FDAF2B-B011-44b0-8BA2-A1176482BA3A");
				//label.setTitle(UIContext.Constants.disMountVolTextToolTip());
				label.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Listener<MessageBoxEvent> callback = new Listener<MessageBoxEvent>(){
							@Override
							public void handleEvent(MessageBoxEvent be) {
								if(be.getButtonClicked().getItemId().equals(Dialog.YES)){
									dismountVolume(moutPath, mountDiskSignature);
								}
							}
						};
						
						showMessageBox(MessageBox.INFO, MessageBox.YESNO, 
								UIContext.Constants.dismountMessage(), callback);
					}					
				});
				
				LayoutContainer disMountContainer = new LayoutContainer();
				disMountContainer.setLayout(new TableLayout(1));
				disMountContainer.add(label);
				return disMountContainer;
			}
		});
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("mounthPath");
		column.setHeaderHtml(UIContext.Constants.mountVolPath());
		column.setMenuDisabled(true);
		column.setWidth(80);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {

			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				String path = model.getMountPath();
				return getGridRowLabel(path, path);
			}
		});
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("time");
		column.setHeaderHtml(UIContext.Constants.mountVolRecoveryPointDate());
		GridCellRenderer<MountedRecoveryPointItemModel> timeRenderer = new GridCellRenderer<MountedRecoveryPointItemModel>() {
			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				// Recovery Point table - Modified time column.
				if (model.getTime()!=null){
					String timeValue = Utils.formatDateToServerTime(model.getTime(), model
							.getTimeZoneOffset().longValue());
					return getGridRowLabel(timeValue, timeValue);
				}
				return "";
			}
		};
		column.setRenderer(timeRenderer);
		column.setWidth(140);
		column.setMenuDisabled(true);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("volumePath");
		column.setHeaderHtml(UIContext.Constants.mountVolSource());
		column.setMenuDisabled(true);
		column.setWidth(100);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {

			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				String path = model.getVolumePath();
				return getGridRowLabel(path, path);
			}
		});
		configs.add(column);		
		
		column = new ColumnConfig();
		column.setId("size");
		column.setHeaderHtml(UIContext.Constants.restoreSizeColumn());
		column.setMenuDisabled(true);
		column.setWidth(80);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {

			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				try {
					Long value = model.getVolumeSize();
					String formattedValue = Utils.bytes2String(value);
					return getGridRowLabel(formattedValue, formattedValue);

				} catch (Exception e) {

				}
				return "";
			}
			
		});
		configs.add(column);
		
		/*column = new ColumnConfig();
		column.setId("readOnly");
		column.setHeader(UIContext.Constants.mountVolReadOnly());
		column.setMenuDisabled(true);
		column.setWidth(80);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {

			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				if(model.getIsReadOnly()){
					String text = UIContext.Constants.mountVolReadOnlyYes();
					return getGridRowLabel(text, text);
				}
				else{
					String text =  UIContext.Constants.mountVolReadOnlyNo();
					return getGridRowLabel(text, text);
				}
			}
		});
		configs.add(column);*/
		
		column = new ColumnConfig();
		column.setId("recoveryPointPath");
		column.setHeaderHtml(UIContext.Constants.mountVolRecoveryPointPath());
		column.setMenuDisabled(true);
		column.setWidth(120);
		column.setRenderer(new GridCellRenderer<MountedRecoveryPointItemModel> () {

			@Override
			public Object render(MountedRecoveryPointItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MountedRecoveryPointItemModel> store,
					Grid<MountedRecoveryPointItemModel> grid) {
				String path = model.getRecoveryPointPath();
				if(MountUtils.isDestinationAccess(model.getMountFlag())){
					return getGridRowLabel(path, path);
				}
				else{
					String toolTip = UIContext.Messages.mountVolDestinationNotAccessible(path);
					LabelField label = getGridRowLabel(path, toolTip);
					label.addStyleName("mount_volume_dest_not_accessible_icon");
					return label;
				}
			}
		});
		configs.add(column);

		StoreSorter<MountedRecoveryPointItemModel> sorter = new StoreSorter<MountedRecoveryPointItemModel>(){
			
			private boolean isEmptyOrNull(String str){
				if((str == null) || (str.isEmpty())){
					return true;
				}
				else{
					return false;
				}
			}
			
			private int comparePath(String path1, String path2){
				if(isEmptyOrNull(path1)){
					return -1;
				}
				else if(isEmptyOrNull(path2)){
					return 1;
				}
				else{
					return path1.compareToIgnoreCase(path2);
				}
			}
			
			public int compare(Store<MountedRecoveryPointItemModel> store, MountedRecoveryPointItemModel m1,
					MountedRecoveryPointItemModel m2, String property) {
				if( property == null ){
					return comparePath(m1.getMountPath(), m2.getMountPath());
				}
				else if( property == "size" ){
					if(m1.getVolumeSize() < m2.getVolumeSize()){
						return -1;
					}
					else if(m1.getVolumeSize() == m2.getVolumeSize()){
						return 0;
					}
					else{
						return 1;
					}
				}
				else if( property == "time"){
					if( m1.getTime() == null)
					{
						if( m2.getTime() == null )
							return comparePath(m1.getMountPath(), m2.getMountPath());
						else
							return -1;
					}
					else
					{
						if( m2.getTime() == null )
							return 1;
						else
							return m1.getTime().compareTo(m2.getTime());
					}
				}
				else
					return super.compare(store, m1, m2, property);
			}
		};
		
		columnModel = new ColumnModel(configs);
		columnModel.addListener(Events.WidthChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				mountItemGrid.reconfigure(mountItemStore, columnModel);
			}
	    	
	    });
		
		
		mountItemStore = new ListStore<MountedRecoveryPointItemModel>();
		mountItemStore.setStoreSorter(sorter);
		mountItemGrid = new Grid<MountedRecoveryPointItemModel>(mountItemStore, columnModel);
		mountItemGrid.setAutoExpandColumn("recoveryPointPath");
		mountItemGrid.setAutoExpandMax(3000);
		mountItemGrid.setBorders(true);
		mountItemGrid.setStripeRows(true);
		mountItemGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		mountItemGrid.setLoadMask(true);
		mountItemGrid.mask(UIContext.Constants.loadingIndicatorText());
		mountItemGrid.setSize(676, 100);
		//mountItemGrid.addStyleName("mount_volume_grid");
		return mountItemGrid;
	}
	
	private LabelField getGridRowLabel(String text, String toolTip){
		LabelField label = new LabelField(text);
		label.addStyleName("mount_volume_label_ellipsis");
		Utils.addToolTip(label, toolTip);
		return label;
	}
	
	public void getAllMountedRecoveryPointItems(){
		service.getAllMountedRecoveryPointItems(new BaseAsyncCallback<List<MountedRecoveryPointItemModel>>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				parentPanel.updateRefreshStatus(WindowsMaskAdapter.MOUNTED_LIST_CONTAINER, true);
			}
			@Override
			public void onSuccess(List<MountedRecoveryPointItemModel> result) {
				mountItemStore.removeAll();
				mountItemStore.add(result);
				mountItemGrid.unmask();
				parentPanel.updateRefreshStatus(WindowsMaskAdapter.MOUNTED_LIST_CONTAINER, true);
			}
		});
	}
	
	public void dismountVolume(String mountPath, int mountDiskSignature){
		parentPanel.maskWindow();
		service.disMountRecoveryPointItem(mountPath,mountDiskSignature, new BaseAsyncCallback<Long>(){
			@Override
			public void onFailure(Throwable caught) {
				parentPanel.unMaskWindow();
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(Long result) {
				//refresh the UI
				parentPanel.refreshUI();
				//unmaskWindow();
			}
		});
	}
	
	private void showMessageBox(String icon, String buttons, String message, 
			Listener<MessageBoxEvent> callback) {
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(Utils.getProductName()));
		box.setIcon(icon);
		box.setButtons(buttons);
		box.setMessage(message);
		if(callback != null) {
			box.addCallback(callback);
		}		
		Utils.setMessageBoxDebugId(box);
		box.show();
	}
}
