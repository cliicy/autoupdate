package com.ca.arcflash.ui.client.coldstandby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.event.ReplicationJobFinishedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.ReplicationJobFinishedEventHandler;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.monitor.MonitorService;
import com.ca.arcflash.ui.client.monitor.MonitorServiceAsync;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelMarker;
import com.extjs.gxt.ui.client.data.BeanModelMarker.BEAN;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
public class ProvisionPointsPanel extends LayoutContainer {

	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	private final MonitorServiceAsync monitorService = GWT.create(MonitorService.class);
	private BeanModelFactory factory = BeanModelLookup.get().getFactory(VMSnapshotsInfo.class);
	private Grid<BeanModel> grid;
	private ColumnModel columnModel;
	private ListStore<BeanModel> store;
//	private GridCellRenderer<BeanModel> timeRenderer;
//	private GridCellRenderer<BeanModel> nameRenderer;
//	private List<ColumnConfig> configs;

	private BaseAsyncCallback<VMSnapshotsInfo[]> callback;
	private ContentPanel contentPanel;
	private static final String TimeColumn = "time";
	private static final String ProvisionColumn = "provision";
	private static final String timeRendererControl = "timeRendererControl";
	private static final String provisionRenderControl = "provisionRenderControl";
	
	private BeanModel currentHighligthedModel;
	private String currentHighligthedUUID;
	private static final int REFRESH_INTERVAL = 3000;
	private Timer timer;
	private LayoutContainer panel = new LayoutContainer();
	private boolean checkingFailoverStatus = false;
	
//	private static VMSnapshotsInfo[] snapshotsInfo;
	private RowGridView view;
	private boolean usingTCPIPSetting = false;
	
	public ProvisionPointsPanel() {
		setLayout(new FitLayout());
//		setScrollMode(Scroll.AUTO);
		constructContent();
	    
	    ColdStandbyManager.getInstance().registerEventHandler(new ReplicationJobFinishedEventHandler(){

			@Override
			public void onJobFinished(ReplicationJobFinishedEvent event) {
				//the following update is removed because Summary Panel will also fetch provision points and 
				//we update this panel when summary panel updates.
				//update();
			}
	    	
	    });
	    
	    callback = new BaseAsyncCallback<VMSnapshotsInfo[]>(){

			@Override
			public void onFailure(Throwable caught) {
//				snapshotsInfo = null;
				ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
				grid.getView().setEmptyText(UIContext.Constants.NA());
				store = new ListStore<BeanModel>();
				grid.reconfigure(store, columnModel);
			}

			@Override
			public void onSuccess(VMSnapshotsInfo[] result) {
//				snapshotsInfo = result;
				ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
				grid.getView().setEmptyText(UIContext.Constants.NA());
				
				if (result == null || result.length == 0){
					grid.getView().setEmptyText(UIContext.Constants.NA());
					store = new ListStore<BeanModel>();
					grid.reconfigure(store, columnModel);
					return;
				}
				
				Arrays.sort(result, new Comparator<VMSnapshotsInfo>(){
					@Override
					public int compare(VMSnapshotsInfo arg0, VMSnapshotsInfo arg1) {
						if (arg0.getTimestamp()>arg1.getTimestamp())
							return -1;
						else if (arg0.getTimestamp()<arg1.getTimestamp())
							return 1;
						else
							return 0;
					}
					
				});
				
				for (final VMSnapshotsInfo snapShot : result){
					BeanModel model = factory.createModel(snapShot);
					store.add(model);
				}
				
				grid.reconfigure(store, columnModel);
				
				view.renderRowStyle();
			}
			
		};
		
		timer = new Timer() {
			public void run() {
				highlightSnapshotWhenFailoverFinish();
			}			
		};
		
	}

	private void constructContent() {
		List<ColumnConfig> configs = getColumnConfig();
		
		store = new ListStore<BeanModel>();  
		columnModel = new ColumnModel(configs);
		
	    grid = new Grid<BeanModel>(store, columnModel);
//	    int height = 335;
//	    grid.setHeight(height);
//	    grid.setAutoWidth(true);
	    grid.setLoadMask(true);
	    grid.setAutoExpandColumn(ProvisionColumn);
		grid.setTrackMouseOver(false);
		view = new RowGridView();
		grid.setView(view);
		grid.getView().setEmptyText(UIContext.Constants.NA());
//		grid.getView().setForceFit(true);
		
		 panel = new LayoutContainer();
		 panel.setLayout(new FitLayout());
		 panel.add(grid);

	    contentPanel = new ContentPanel();
	    contentPanel.setCollapsible(true);
//	    contentPanel.setHeight(height);
	    contentPanel.setHeadingHtml(UIContext.Constants.provistionPointPanelTitle());
	    contentPanel.setLayout(new FitLayout());
//	    contentPanel.setScrollMode(Scroll.AUTO);
	    contentPanel.add(panel);
//	    contentPanel.setAutoWidth(true);
	    
	    add(contentPanel);
	}

	private List<ColumnConfig> getColumnConfig() {
//		nameRenderer = new GridCellRenderer<BeanModel>() {
//		@Override
//		public Object render(BeanModel model, String property,	ColumnData config, int rowIndex, int colIndex,
//				ListStore<BeanModel> store, Grid<BeanModel> grid) {
//			final VMSnapshotsInfo snapShot = (VMSnapshotsInfo)model.getBean();
//			return snapShot.getSessionName();
//		}
//	};
		
		GridCellRenderer<BeanModel> timeRenderer = new GridCellRenderer<BeanModel>() {

			@Override
			public Object render(BeanModel model, String property,	ColumnData config, int rowIndex, int colIndex,
					ListStore<BeanModel> store, Grid<BeanModel> grid) {
				Label label = new Label();
				model.set(timeRendererControl, label);
				final VMSnapshotsInfo snapShot = (VMSnapshotsInfo)model.getBean();
				String formatDateToServerTime = UIContext.Constants.NA();
				if(snapShot.getTimestamp() > 0)
					formatDateToServerTime = Utils.formatDateToServerTime(new Date(snapShot.getTimestamp()),
							snapShot.getTimeZoneOffset());
				label.setHtml(formatDateToServerTime);
				if(currentHighligthedUUID != null 
						&& (currentHighligthedUUID.equals(snapShot.getSnapGuid())
							|| currentHighligthedUUID.equals(snapShot.getBootableSnapGuid())) )
					label.setStyleAttribute("font-weight", "bold");
				return label;
			}
		};
		
		GridCellRenderer<BeanModel> provisionRender = new GridCellRenderer<BeanModel>() {

			@Override
			public Object render(BeanModel model, String property,	ColumnData config, int rowIndex, int colIndex,
					ListStore<BeanModel> listStore, Grid<BeanModel> grid) {
				
				final VMSnapshotsInfo snapShot = (VMSnapshotsInfo)model.getBean();
				
				final Anchor link = new Anchor();
				if(currentHighligthedUUID != null 
						&& (currentHighligthedUUID.equals(snapShot.getSnapGuid())
						    || currentHighligthedUUID.equals(snapShot.getBootableSnapGuid())) )
					link.setStyleName("provisionPointsActionColumnHighligth");
				else
					link.setStyleName("provisionPointsActionColumn");
				link.setText(UIContext.Constants.provistionPointColumnProvision());
				
				model.set(provisionRenderControl, link);
				
				final ARCFlashNode currentNode = ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode();

				ClickHandler handler = new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(!link.isEnabled())
							return;
						if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
							monitorService.getFailoverJobScript(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), new AsyncCallback<FailoverJobScript>() {

								@Override
								public void onFailure(Throwable caught) {
									usingTCPIPSetting = false;
									getPowerOnVMWarningDialog();
								}

								@Override
								public void onSuccess(FailoverJobScript result) {
									usingTCPIPSetting = result.isIPSettingsFromVCM();
									getPowerOnVMWarningDialog();;
								}
								
							});
						}
						else {
							service.getFailoverJobScript(ColdStandbyManager.getVMInstanceUUID(), new AsyncCallback<FailoverJobScript>() {

								@Override
								public void onFailure(Throwable caught) {
									usingTCPIPSetting = false;
									getPowerOnVMWarningDialog();
								}

								@Override
								public void onSuccess(FailoverJobScript result) {
									usingTCPIPSetting = result.isIPSettingsFromVCM();
									getPowerOnVMWarningDialog();
								}
								
							});
						}
					}
					
					private void getPowerOnVMWarningDialog() {
						final PowerOnVMWarningDialog warningDialog = new PowerOnVMWarningDialog();
						warningDialog.setMessage(getWarningMessage());
						warningDialog.setUsingIPConfigurationEnable(usingTCPIPSetting);
						warningDialog.addListener(Events.BeforeHide, new Listener<WindowEvent>() {
							@Override
							public void handleEvent(WindowEvent be) {
								if(warningDialog.isOKButtonClick()){
									snapShot.setPowerOnWithIPSettings(warningDialog.getUsingIPConfigurationEnable());
									startVMNow();
								} 
							}
						});
						warningDialog.setWidth(500);
						warningDialog.show();
					}
					
					private String getWarningMessage() {
						String msgStr = UIContext.Constants.coldStandbySnapshotWhetherToStart();
						if(currentHighligthedModel != null) { 
							if(((VMSnapshotsInfo)currentHighligthedModel.getBean()).getTimestamp() != snapShot.getTimestamp()) { 
								String onSnapshotBackupedTime =  Utils.formatDateToServerTime(new Date(((VMSnapshotsInfo)currentHighligthedModel.getBean()).getTimestamp()),
										((VMSnapshotsInfo)currentHighligthedModel.getBean()).getTimeZoneOffset());
								String toPowerOn = Utils.formatDateToServerTime(new Date(snapShot.getTimestamp()),
										snapShot.getTimeZoneOffset());
								msgStr = UIContext.Messages.virtualConvesionSnapshotPowerOnAnother(onSnapshotBackupedTime, toPowerOn);//"Do you want to power off the snapshot backuped on {0} and then power on the one backuped on {1}?";
							}
							else {
								msgStr = UIContext.Constants.coldStandbySnapshotRevertToSnapshot();
							}
						}
						else if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor() 
								|| (ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible() 
										&& ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerPhysical())) {
							msgStr =  UIContext.Constants.coldStandbySnapshotProductionServerRunningQuery();
						}
						else if(ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible() 
										&& !ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerPhysical()) {
							msgStr =  UIContext.Constants.coldStandbySnapshotRevertToSnapshot();
						}
						return msgStr;
					}
					
					private void startVMNow() {
						BaseAsyncCallback<Void> failoverCallback = new BaseAsyncCallback<Void>(){
							
							@Override
							public void onFailure(Throwable caught) {
								MessageBox msg = new MessageBox();
								msg.setIcon(MessageBox.ERROR);
								msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameVCM));
								msg.setMessage(UIContext.Constants.coldStandbySnapshotStartFailoverFailed());
								msg.setModal(true);
								Utils.setMessageBoxDebugId(msg);
								msg.show();
//									Window.alert(UIContext.Constants.coldStandbySnapshotStartFailoverFailed());
							}
							
							@Override
							public void onSuccess(Void result) {
								timer.scheduleRepeating(REFRESH_INTERVAL);
							}
							
						};
						
						if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
							String uuid = currentNode.getUuid();
							monitorService.startFailover(uuid, snapShot, failoverCallback);
						} else {
							String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
							service.startFailover(vmInstanceUUID, snapShot, failoverCallback);
						}
					}

					private void setButtonId(MessageBox messageBox, String buttonId, String id) {
						Button button = messageBox.getDialog().getButtonById(buttonId);
						if(button != null) {
							button.ensureDebugId(id);
						}
					}
				};
				link.addClickHandler(handler);
				
				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				lc.setLayout(layout);
				
				Image image = AbstractImagePrototype.create(UIContext.IconBundle.power_on_vm()).createImage();
				image.addClickHandler(handler);
				image.setStyleName("homepage_task_icon");
				
				TableData data = new TableData();
				data.setStyleName("vcm_provision_points_image");
				lc.add(image, data);
				
				lc.add(link);
				return lc;
			}
		};
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig(TimeColumn, UIContext.Constants.provistionPointColumnTime(), 150, timeRenderer));
		int length = 100;
		configs.add(Utils.createColumnConfig(ProvisionColumn, UIContext.Constants.provistionPointColumnAction() , length, provisionRender));
		return configs;
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		timer.scheduleRepeating(REFRESH_INTERVAL);
	}
	
	public void update() {
		
		clearProvisionPoints();
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			monitorService.getSnapshots(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), callback);
		}
		else {
			String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
			service.getSnapshots(vmInstanceUUID, callback);
		}
	}
	
	public void update(List<VMSnapshotsInfo> snapshotsList) {
		clearProvisionPoints();
		VMSnapshotsInfo[] provisionPoints = snapshotsList == null ? new VMSnapshotsInfo[0] 
		                                                           : snapshotsList.toArray(new VMSnapshotsInfo[0]);
		callback.onSuccess(provisionPoints);
		timer.scheduleRepeating(REFRESH_INTERVAL);
	}

	public void clearProvisionPoints() {
		grid.getView().setEmptyText(UIContext.Constants.loadingIndicatorText());
		store = new ListStore<BeanModel>();
		grid.reconfigure(store, columnModel);
	}
	
	@BEAN(com.ca.arcflash.ha.model.VMSnapshotsInfo.class)
	public interface VMSnapshotsInfoModel extends BeanModelMarker {
		
	}
	
	public class VMSnapshotsInfoTag implements BeanModelTag, Serializable {

		private static final long serialVersionUID = 5265279894144483588L;
		
	}
	
	void refreshHighLightedRunningSnapshot() {
		timer.scheduleRepeating(REFRESH_INTERVAL);
	}
	
	private void highlightCurrentSnapShot() {
		
		 final BaseAsyncCallback<String> callback = new BaseAsyncCallback<String>(){

				@Override
				public void onFailure(Throwable caught) {
					clearOldRunning();
					currentHighligthedUUID = null;
				}

				@Override
				public void onSuccess(String result) {
					
					clearOldRunning();
					currentHighligthedUUID = result; 
					
					if(result == null)
						return;
					
					int size = store.getCount();
					for (int i = 0; i < size; i++) {
						BeanModel model = store.getAt(i);
						VMSnapshotsInfo info = model.getBean();
						if(result.equals(info.getSnapGuid()) || result.equals(info.getBootableSnapGuid())){
							
							currentHighligthedModel = model;
							Label label = (Label)model.get(timeRendererControl);
							label.setStyleAttribute("font-weight", "bold");
							
							Anchor anchor = (Anchor)model.get(provisionRenderControl);
//							anchor.setEnabled(false);
							anchor.setStyleName("provisionPointsActionColumnHighligth");
						}
						
					}
				}

				private void clearOldRunning() {
					if(currentHighligthedModel != null) {
						Label label = (Label)currentHighligthedModel.get(timeRendererControl);
						label.setStyleAttribute("font-weight", "normal");
						
						Anchor anchor = (Anchor)currentHighligthedModel.get(provisionRenderControl);
						anchor.setStyleName("provisionPointsActionColumn");
						currentHighligthedModel = null;
					}
				}
		 };
		
		 if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
				monitorService.getCurrentRunningSnapShotGuid(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), callback);
			}
			else {
				String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
				service.getRunningSnapShotGuidForProduction(vmInstanceUUID, callback);
			}

	}
	
	private void highlightSnapshotWhenFailoverFinish() {
		if(checkingFailoverStatus)
			return;
		
		checkingFailoverStatus = true;
		
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			monitorService.isFailoverJobFinish(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), new BaseAsyncCallback<Boolean>(){

				@Override
				public void onFailure(Throwable caught) {
					checkingFailoverStatus = false;
				}

				@Override
				public void onSuccess(Boolean result) {
					checkingFailoverStatus = false;
					if(result) {
						if(timer != null) {
							timer.cancel();
							highlightCurrentSnapShot();
						}
					}
				}
			}
			);
			
		}
		else {
			String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
			service.isFailoverJobFinishOfProductServer(vmInstanceUUID, new BaseAsyncCallback<Boolean>(){

				@Override
				public void onFailure(Throwable caught) {
					checkingFailoverStatus = false;
				}

				@Override
				public void onSuccess(Boolean result) {
					checkingFailoverStatus = false;
					if(result) {
						if(timer != null) {
							timer.cancel();
							highlightCurrentSnapShot();
						}
					}
				}
			}
			);
		}
	}
	
	  @Override
	  protected void onUnload() {
		  super.onUnload();
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
	  }
	  
	  public void removeGrid() {
////		  if(snapshotsInfo != null) {
//			  contentPanel.remove(panel);
////		  }
	  }
	  
	  public void reconstructGrid() {
////			  constructContent();
//			  	List<ColumnConfig> configs = getColumnConfig();
//			  	
//				store = new ListStore<BeanModel>();  
//				columnModel = new ColumnModel(configs);
//				
//				grid = new Grid<BeanModel>(store, columnModel);
//				int height = 335;
//				grid.setHeight(height);
//				grid.setAutoWidth(true);
//				grid.setLoadMask(true);
//				grid.setAutoExpandColumn(ProvisionColumn);
//				grid.setTrackMouseOver(false);
//				view = new RowGridView();
//				grid.setView(view);
//				grid.getView().setEmptyText(UIContext.Constants.NA());
////				
////			  	Grid<BaseModelData> g1 = getTestGrid();
//				panel = new LayoutContainer();
//				panel.setLayout(new FitLayout());
//				contentPanel.add(panel);
//				panel.add(grid);
////				
//////	  		callback.onSuccess(snapshotsInfo);
//				if(snapshotsInfo != null) {
//					for (final VMSnapshotsInfo snapShot : snapshotsInfo){
//						BeanModel model = factory.createModel(snapShot);
//						store.add(model);
//					}
//				}
//				
//				grid.reconfigure(store, columnModel);
//				view.renderRowStyle();
//				setHeight(height);
//				
//				if(snapshotsInfo == null) {
//					clearProvisionPoints();
//				}
	  }
	  
//	  public void reconstructGridPanel() {
//		  callback.onSuccess(snapshotsInfo);
//	  }
	  
	  class RowGridView extends GridView {

			public void renderRowStyle() {
				if(store == null)
					return;
				
				int count = store.getCount();
				if(count > 1)
					for(int i = 1; i < count; i += 2) {
						com.google.gwt.dom.client.Element elem = getRow(i);
						addRowStyle(elem, "table_row_alternative");
					}
			}
			
		}
	  
	  public void refreshGrid() {
			DeferredCommand.addCommand(new Command()
			{
				@Override
				public void execute()
				{
					if (grid != null && store != null && columnModel != null)
					{
						grid.reconfigure(store, columnModel);
					}
				}
			});
	  }
}
