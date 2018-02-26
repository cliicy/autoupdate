package com.ca.arcflash.ui.client.restore.ad;

import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class ADGridCell extends LayoutContainer{
	
	private FlashCheckBox fcb;
	private IconButton image;
	private LabelField text;

	public ADGridCell(GridTreeNode node){
		fcb = new FlashCheckBox();
		initContainer(node);
	}
	
	public ADGridCell(GridTreeNode node, FlashCheckBox fcb){
		this.fcb = fcb;
		initContainer(node);
	}

	private void initContainer(GridTreeNode node) {
		TableLayout layout = new TableLayout();
		layout.setColumns(3);
		this.setLayout(layout);
		updateSelectedState(node);
		this.add(fcb);
		
		image = ADRestoreUtils.getNodeIcon(node);
		if(image != null)
			this.add(image);

		text = new LabelField();
		text.setValue(node.getDisplayName().replace(" ", "&nbsp;"));
		text.setStyleAttribute("word-wrap", "break-word");
		text.setStyleAttribute("word-break", "break-all");
		this.add(text);
		
//		if(GXT.isIE) {
//		Label lf = new Label();
//		lf.setText(node.getDisplayName());
//		lf.setStyleName("x-form-label");
//		this.add(lf);
//		}else {
//			LabelField lf = new LabelField();
//			lf.setText(node.getDisplayName().replace(" ", "&nbsp;"));
//			this.add(lf);
//		}
	}
	

	
	public void updateSelectedState(GridTreeNode node){
		if (node.getSelectable() != null && node.getSelectable() == false) {					
			fcb.setEnabled(false);
		} else {
			if(node.getSelectionType()==null){
				fcb.setSelectedState(FlashCheckBox.NONE);
			}else{
				fcb.setSelectedState(node.getSelectionType());
			}
		}
	}
	
	public FlashCheckBox getFlashCheckBox() {
		return fcb;
	}
	
	public void addClickListener(Listener<? extends BaseEvent> listener) {
		 if(image!=null){
			 image.addListener(Events.OnMouseDown, listener);
		 }
		 if(text!=null){
			 text.addListener(Events.OnMouseDown, listener);
		 }
	}

}
