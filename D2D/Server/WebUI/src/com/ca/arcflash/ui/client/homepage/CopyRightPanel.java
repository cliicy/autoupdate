package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class CopyRightPanel extends LayoutContainer {

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setColumns(1);
		tableLayout.setWidth("100%");
		setLayout(tableLayout);
		

		Html htmlControl = new Html("<HR>");		
		htmlControl.setHeight("5px");
		
		TableData td = new TableData();
		td.setWidth("100%");		
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		td.setHorizontalAlign(HorizontalAlignment.CENTER);		
		add(htmlControl, td);
		
		td = new TableData();
		td.setWidth("92%");
		
		Label label = new Label();
//		label.setHtml(UIContext.Constants.aboutWindowCopyRight()+UIContext.Constants.aboutWindowCopyRight2());
		label.setHtml(UIContext.Constants.aboutWindowCopyRightFull());
		label.addStyleName("copyright");
		add(label, td);
	}

}
