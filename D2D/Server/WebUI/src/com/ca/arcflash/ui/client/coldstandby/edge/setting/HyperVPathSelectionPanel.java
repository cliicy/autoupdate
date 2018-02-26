package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.restore.BrowseWindow;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class HyperVPathSelectionPanel extends LayoutContainer{

	private final ColdStandbyServiceAsync	service = GWT.create(ColdStandbyService.class);
	private TextField<String> 			  	hyperVPath;
	private boolean							isForEdge;
	private static final int 				MAX_INPUT_LENGTH = 260;
	
	public HyperVPathSelectionPanel(int width, boolean isForEdge, String debugID){
		this.isForEdge = isForEdge;
		
		hyperVPath = new TextField<String>();
		hyperVPath.ensureDebugId(debugID);
		hyperVPath.setAllowBlank(false);
		hyperVPath.setWidth(width); //150
		hyperVPath.setMaxLength(MAX_INPUT_LENGTH);
		hyperVPath.setMinLength(3);
		
		doInit();
	}
	
	public HyperVPathSelectionPanel(TextField<String> selectPath, boolean isForEdge){
		this.isForEdge = isForEdge;
		this.hyperVPath = selectPath;
		
		if(hyperVPath!=null){
			doInit();
		}
	}
	
	public void setHyperVPath(String path){
		if(hyperVPath!=null){
			hyperVPath.setValue(path);
		}
	}
	
	public String getHyperVPath(){
		if(hyperVPath==null){
			return null;
		}
		
		return hyperVPath.getValue().trim();
	}
	
	public void setHyperVPathToolTip(String tip){
		if(hyperVPath!=null){
			Utils.addToolTip(hyperVPath, tip);
		}
	}
	
	public void setIsForEdge(boolean isForEdge){
		this.isForEdge = isForEdge;
	}
	
	public boolean validate(){
		if(hyperVPath!=null){
			return hyperVPath.validate();
		}
		else{
			return true;
		}
	}
	
	protected int doInit(){
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		this.setLayout(layout);
		
		final Button button = new Button(UIContext.Constants.coldStandbySettingHypervPathBrowse());
		button.setWidth(80);
		button.setStyleAttribute("padding-left", "4px");

		button.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						String replicationPath=hyperVPath.getValue();
						if((replicationPath==null)||(replicationPath.isEmpty())){
							showBrowseDialog(hyperVPath);
						}
						else{
							if(!isShareFolder(replicationPath))
							{
								button.disable();
								service.vcmValidateSource(hyperVPath.getValue(), "", "", "", true, new BaseAsyncCallback<Long>(){

									@Override
									public void onFailure(Throwable caught) {
										button.enable();
										//super.onFailure(caught);
										showBrowseDialog(hyperVPath);
									}

									@Override
									public void onSuccess(Long result) {
										button.enable();
										showBrowseDialog(hyperVPath);
									}
									
								});
							}
						}
						
					}
				});
		
		if(!isForEdge){
			button.disable();
			hyperVPath.disable();
		}
			
		
		//setDefaultHyperVPath(hyperVPath, diskID);
		this.add(hyperVPath);
		this.add(button);
		
		return 0;
	}
	
	protected boolean isShareFolder(String path) {
		if(path==null){
			return false;
		}
		if((path.length()>2)&&(path.charAt(0)=='\\')&&(path.charAt(1)=='\\')){
			String strMsg=UIContext.Constants.coldStandbySettingHypervPathInvalid();
			WizardContext.getWizardContext().showMessageBox("{90E8FBB6-C96D-470c-A687-7E7DB7CBDE0C}",strMsg);
			return true;
		}
		
		return false;
		
	}
	
	protected void showBrowseDialog(final TextField<String> textFieldPath) {

		String title = UIContext.Constants.coldStandbySettingHypervLocation();
		String path = textFieldPath.getValue();
		final BrowseWindow browseDlg = new BrowseWindow(false, title);
		browseDlg.setMode(0);
		browseDlg.setUser(WizardContext.getWizardContext().getMonitorUsername());
		browseDlg.setPassword(WizardContext.getWizardContext().getMonitorPassword());
		browseDlg.setInputFolder(path);
		browseDlg.setBrowseClient(1);
		
		browseDlg.setModal(true);
		browseDlg.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (browseDlg.getLastClicked() != Dialog.CANCEL) {
					String newDest = browseDlg.getDestination() == null ? ""
							: browseDlg.getDestination();
					textFieldPath.setValue(newDest);
				}
			}
		});

		browseDlg.show();
	}
}
