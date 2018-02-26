package com.ca.arcflash.ui.client.restore;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;

public abstract class RestoreSummaryPanel extends LayoutContainer {

	protected abstract void updateDestinationLabel();

	protected abstract void updateOptionsLabel();

	protected abstract void updateRecvPointRestoreSource();

	protected abstract void updateSearchSource();
	
	public abstract void updateArchiveRestoreSource();
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setScrollMode(Scroll.AUTOY);
	}
}
