package com.ca.arcflash.ui.client.restore.ad;

import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.grid.GridView;

public class ADGridView extends GridView {
	
	public ADGridView(){
		setPreventScrollToTopOnRefresh(true);
	}

	public boolean isPreventScrollToTopOnRefresh() {
		return preventScrollToTopOnRefresh;
	}

	public void setPreventScrollToTopOnRefresh(boolean preventScrollToTopOnRefresh) {
		this.preventScrollToTopOnRefresh = preventScrollToTopOnRefresh;
	}
	
	public void refresh(boolean headerToo) {
		Point p=getScrollState();
		super.refresh(headerToo);
		restoreScroll(p);
	}
}
