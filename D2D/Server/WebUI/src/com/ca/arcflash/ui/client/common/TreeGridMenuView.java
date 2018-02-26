package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

public class TreeGridMenuView extends TreeGridView {
	
	public TreeGridMenuView() {
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void handleComponentEvent(GridEvent ge) {
	    switch (ge.getEventTypeInt()) {
	      case Event.ONMOUSEMOVE:
	        Element row = getRow(ge.getRowIndex());
	        if (overRow != null && row == null) {
	          onRowOut(overRow);
	        } else if (row != null && overRow != row) {
	          if (overRow != null) {
	            onRowOut(overRow,ge);
	          }
	          onRowOver(row,ge);
	        }
	        break;
	      case Event.ONMOUSEDOWN:
	        onMouseDown(ge);
	        break;
	      case Event.ONSCROLL:
	        if (scroller.isOrHasChild(ge.getTarget())) {
	          syncScroll();
	        }
	        break;
	    }
	  }
	
	@SuppressWarnings("unchecked")
	protected void onRowOver(Element row,GridEvent ge) {
		super.onRowOver(row);
		cm.getColumnById("menu").setHidden(false);
		
		//this.refreshRow(ge.getRowIndex());
		refreshRow(ge.getRowIndex());
	}
	
	@SuppressWarnings("unchecked")
	protected void onRowOut(Element row,GridEvent ge) {
	    super.onRowOut(row);
	    cm.getColumnById("menu").setHidden(true);
	    refresh(false);
	}

}
