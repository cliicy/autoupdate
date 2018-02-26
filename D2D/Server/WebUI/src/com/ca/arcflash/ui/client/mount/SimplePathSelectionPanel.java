package com.ca.arcflash.ui.client.mount;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.restore.BrowseWindow;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class SimplePathSelectionPanel extends LayoutContainer{

	private TextField<String>  destinationPath;
	private Button			   browseButton;
	
	public SimplePathSelectionPanel(){
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		this.setLayout(tableLayout);
		
		destinationPath = new TextField<String>();
		destinationPath.ensureDebugId("7ed4fb48-1744-4c1e-bebc-bdfe2399d585");
		//destinationPath.setAllowBlank(false);
		destinationPath.setWidth(260); 
		//destinationPath.setMinLength(3);
		Utils.addToolTip(destinationPath, UIContext.Constants.destinationSelDestEditBoxTooltip());
		this.add(destinationPath);
		
		browseButton = new Button(UIContext.Constants.restoreBrowse());
		browseButton.setWidth(85);
		browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(isShareFolder()){
					showMessageBox( UIContext.Constants.mountPathInvalid());
					return;
				}
				
				showBrowseDialog(getDestinationPath());
			}
		});
		this.add(browseButton);
	}
	
	public String getDestinationPath(){
		if(destinationPath.getValue() == null)
			return "";
		
		return destinationPath.getValue().trim();
	}
	
	public boolean validate(){
		if(getDestinationPath().isEmpty()){
			showMessageBox(UIContext.Constants.mountPathEmpty());
			return false;
		}
		if(isShareFolder()){
			showMessageBox( UIContext.Constants.mountPathInvalid());
			return false;
		}
		return true;
	}
	
	private boolean isShareFolder() {
		String path = getDestinationPath();
		if(path==null){
			return false;
		}
		if((path.length()>2)&&(path.charAt(0)=='\\')&&(path.charAt(1)=='\\')){
			return true;
		}
		
		return false;
		
	}
	
	private void showMessageBox(String msgStr){
		MessageBox msg = new MessageBox();
		msg.setIcon(MessageBox.ERROR);
		msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(Utils.getProductName()));
		msg.setMessage(msgStr);
		msg.setModal(true);
		msg.show();
	}
	
	private void showBrowseDialog(String path) {
		String title = UIContext.Constants.mountVolLocation();
		final BrowseWindow browseDlg = new BrowseWindow(false, title);
		browseDlg.setMode(0);
		browseDlg.setUser("");
		browseDlg.setPassword("");
		browseDlg.setInputFolder(path);
		browseDlg.setDebugID("2E8EB315-3F0D-4219-985A-64AC76C1B98B", 
				"827BE22E-D2DD-4ce0-A125-BE3D57D81014");
		
		browseDlg.setModal(true);
		browseDlg.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (browseDlg.getLastClicked() != Dialog.CANCEL) {
					String newDest = browseDlg.getDestination() == null ? ""
							: browseDlg.getDestination();
					destinationPath.setValue(newDest);
				}
			}
		});

		browseDlg.show();
	}
}
