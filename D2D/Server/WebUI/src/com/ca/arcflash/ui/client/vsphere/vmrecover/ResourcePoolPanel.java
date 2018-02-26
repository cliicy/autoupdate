package com.ca.arcflash.ui.client.vsphere.vmrecover;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class ResourcePoolPanel extends LayoutContainer {
	
	private TextField<String> poolTF = new TextField<String>();
	private Button browseButton = new Button();
//	private ResourcePoolPanel thisPanel;
	private VirtualCenterModel vcModel;
	private ESXServerModel esxServerModel;
	private String poolMoref;
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		// cannot add css for PathSelectionPanel, because it's a base panel, many places may be using it.
//		setStyleAttribute("margin", "2px, 2px, 2px, 10px");
		
		TableLayout tl = new TableLayout();
		//tl.setWidth("100%");
		tl.setColumns(2);
		//tl.setCellPadding(2);
		//tl.setCellSpacing(2);
		setLayout(tl);
		
		//TableData td = new TableData();
		//td.setWidth("50%");
		
		//poolTF.setWidth(200);
		poolTF.setReadOnly(true);
		poolTF.mask();   //In order to lose the focus event
		add(poolTF);
		
		
		if(browseButton == null)
			browseButton = new Button();
		browseButton.ensureDebugId("505ED5A3-FAF5-432d-89BE-EF09C5E76B9D");
		browseButton.setText(UIContext.Constants.browseResourcePool());
		
		browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showBrowseDialog();
			}			
		});
		
		//td = new TableData();
		//td.setWidth("50%");
		//td.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData data = new TableData();
		data.setStyle("padding-left: 8px");
		add(browseButton, data);
	}
	
	private void showBrowseDialog(){
		final BrowseResourcePool browseDlg = new BrowseResourcePool(vcModel,esxServerModel);
		browseDlg.setResizable(false);
		browseDlg.setModal(true);
		browseDlg.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				String poolPath = browseDlg.getPoolPath();
				poolTF.setValue(poolPath);
				poolMoref = browseDlg.getPoolMoref();
			}
		});
		browseDlg.show();
	}

	public void setPoolWidth(int width){
		poolTF.setWidth(width);
	}
	
	public String getPoolMoref(){
		return poolMoref;
	}
	
	public void setPoolMoref(String poolMoref){
		this.poolMoref = poolMoref;
	}
	
	public String getPoolName() {
		return poolTF.getValue();
	}
	
	public void setPoolName(String poolName){
		poolTF.setValue(poolName);
	}
	
	public void setPoolNameTip(String poolNameTip){
		Utils.addToolTip(poolTF, poolNameTip);
	}

	public VirtualCenterModel getVcModel() {
		return vcModel;
	}

	public void setVcModel(VirtualCenterModel vcModel) {
		this.vcModel = vcModel;
	}

	public ESXServerModel getEsxServerModel() {
		return esxServerModel;
	}

	public void setEsxServerModel(ESXServerModel esxServerModel) {
		this.esxServerModel = esxServerModel;
	}
	
	
}
