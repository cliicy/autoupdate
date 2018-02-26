package com.ca.arcflash.ui.client.vsphere.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.ProtectionInformationPanel;
import com.ca.arcflash.ui.client.model.ProtectionInformationModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;

public class VSphereProtectionInformationPanel extends ProtectionInformationPanel implements IRefreshable{
	
	public void render(Element target, int index) {
		lastBackupTimeRenderer = new GridCellRenderer<ProtectionInformationModel>() {

			@Override
			public Object render(ProtectionInformationModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProtectionInformationModel> store, Grid<ProtectionInformationModel> grid) {
				if (model.getLastBackupTime()!=null && model.getLastBackupTime()!=""){
					Date date = Utils.serverString2LocalDate(model.getLastBackupTime());
					return Utils.localDate2LocalString(date);
				}
				return "";
			}
			
		};
		
		super.render(target, index);
	}
	
	@Override
	public void refresh(Object data){	
		
			service.getVMProtectionInformation(UIContext.backupVM,new BaseAsyncCallback<ProtectionInformationModel[]>(){
				@Override
				public void onFailure(Throwable caught) {
					grid.unmask();
					super.onFailure(caught);
				}
				@Override
				public void onSuccess(ProtectionInformationModel[] result) {
					if(result==null || result.length == 0){
						/*if(!panel.isCollapsed())
							panel.collapse();*/
						return;
					}
					if(store==null){
						store = new ListStore<ProtectionInformationModel>();
					}
					boolean isNeedReconfig = store.getCount() > 0 ? false : true;
//					store.removeAll();
					for (int i = 0; i < result.length; i++){
						if(store.getCount() > i)
							store.update(result[i]);
						else
							store.add(result[i]);
//						store.add(result[i]);
					}
					
					if(!panel.isExpanded())
						panel.expand();
					
					if(isNeedReconfig)
						grid.reconfigure(store, cm);
					
					//To solve issue 19148598: when using IE6 and https protocol, this pane does't expand.
					//This issue can be solved by reloading the protection summary after the UI is loaded.
					//if we do not support IE6 or IE6 can expand. Remove the following if cause.
					String protocal = Location.getProtocol();
					if(Utils.checkIE6(com.extjs.gxt.ui.client.GXT.getUserAgent()) && protocal != null && protocal.toLowerCase().startsWith("https")){
						if(loadingTime <= 2) { 
							loadingTime++;
							if(loadingTime == 1) {
								final ProtectionInformationModel[] result1 = result;
								Timer timer1 = new Timer() {
									public void run() {
										if(loadingTime == 1) {
											store.removeAll();
											for (int i = 0; i < result1.length; i++){
												store.add(result1[i]);
											}
											grid.reconfigure(store, cm);
											panel.expand();
										}
									}
								};
								timer1.schedule(8000);
							}
						}
					}
					if(timer == null){
						timer = new Timer() {
							public void run() {
								updatePartialProtectionInfo();
							}
						};
						timer.schedule(3000);
						timer.scheduleRepeating(3000); 
					}
					
					grid.unmask();
				}
			});
	}
	
	private void updatePartialProtectionInfo(){
		
		if(store == null || store.getCount()==0)
			return;
		
		service.updateVMProtectionInformation(UIContext.backupVM,new BaseAsyncCallback<ProtectionInformationModel[]>(){
			@Override
			public void onSuccess(ProtectionInformationModel[] result) {
				if(result == null||result.length==0)
					return;
				for (int i = 0; i < result.length; i++)
				{
					ProtectionInformationModel model = store.getAt(i);
					model.setSchedule(result[i].getSchedule());
					model.setNextRunTime(result[i].getNextRunTime());
					model.setNextTimeZoneOffset(result[i].getNextTimeZoneOffset());
					store.update(model);
				}
				//grid.reconfigure(store, cm);
			}
		});
	}
}
