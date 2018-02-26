package com.ca.arcflash.ui.client.common.gxtex;

import com.extjs.gxt.ui.client.widget.grid.Grid;


import com.extjs.gxt.ui.client.data.ModelData;

import com.extjs.gxt.ui.client.store.ListStore;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;



//Extend GXT2.3.1 Class to Fix grid popmenu style issue.
public class GridEx<M extends ModelData> extends Grid<M> {

  public GridEx(ListStore<M> store, ColumnModel cm) {
	  
	  super(store,cm);
	  this.view = new GridViewEx();
  }

  protected GridEx() {
	  super();
  }

}
