package com.ca.arcflash.ui.client.offlinecopy;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class OfflineCopyHomepage extends LayoutContainer {
	public void render(Element target, int index) {
		super.render(target, index);
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(2);
		tableLayout.setWidth("100%");
		setLayout(tableLayout);
		
		TableData tableData = new TableData();
		tableData.setColspan(2);
		tableData.setPadding(2);
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		
		ContentPanel panel = new ContentPanel();   
		panel.setHeadingHtml("Offline Copy");
		
		add(panel, tableData);
		
		
		tableData = new TableData();
		tableData.setPadding(2);
		tableData.setWidth("50%");
		tableData.setVerticalAlign(VerticalAlignment.TOP);
	}
}
