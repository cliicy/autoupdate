package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.google.gwt.user.client.ui.RootPanel;

public class GxtFactory {
	
	public static <M extends ModelData> Grid<M> createSingleSelectGrid(ListStore<M> store, ColumnModel cm) {
		Grid<M> grid = new Grid<M>(store, cm);
//shaji02: for adding debug id.
grid.ensureDebugId("8ba7bdc6-cd60-4608-ad59-febae98e201b");
		grid.ensureDebugId("797cd097-4081-4516-b6dd-670f712ee80d");
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		return grid;
	}
	
	public static <M extends ModelData> Grid<M> createMultiSelectGrid(ListStore<M> store, ColumnModel cm) {
		CheckBoxSelectionModel<M> selectionModel = new CheckBoxIncrementalSelectionModel<M>();		
		cm.getColumns().add(0, selectionModel.getColumn());		
		Grid<M> grid = new Grid<M>(store, cm);
		grid.ensureDebugId("b61dbe9b-ec93-4a2c-95e8-b946dbc0f13a");
		grid.setSelectionModel(selectionModel);
		grid.addPlugin(selectionModel);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		return grid;
	}
	
	public static <M extends ModelData> EditorGrid<M> createMultiEditSelectGrid(ListStore<M> store, ColumnModel cm) {
		CheckBoxSelectionModel<M> selectionModel = new CheckBoxIncrementalSelectionModel<M>();
		cm.getColumns().add(0, selectionModel.getColumn());
		
		EditorGrid<M> grid = new EditorGrid<M>(store, cm);
		grid.ensureDebugId("b61dbe9b-ec93-4a2c-95e8-b946dbc0f43a");
		grid.setSelectionModel(selectionModel);
		grid.addPlugin(selectionModel);
		return grid;
	}
	
	public static <M extends ModelData> Grid<M> createGrid(ListStore<M> store, ColumnModel cm, CheckBoxSelectionModel<M> selectionModel) {
		cm.getColumns().add(0, selectionModel.getColumn());
		
		Grid<M> grid = new Grid<M>(store, cm);
		grid.ensureDebugId("cb20bcc3-8d78-401c-b005-821e8542b831");
		grid.setSelectionModel(selectionModel);
		grid.addPlugin(selectionModel);
		return grid;
	}
	
	public static void displayInfo(String title, String text) {
		InfoConfig config = new InfoConfig(title, text);
		
		Info testInfo = new Info();
		testInfo.setPosition(-10000, -10000);
		testInfo.setWidth(config.width);
		testInfo.setAutoHeight(true);
		testInfo.setHeadingHtml(config.titleHtml);
		testInfo.addText(config.html);
		
		RootPanel.get().add(testInfo);
		int testHeight = testInfo.getHeight();
		config.height = config.height < testHeight ? testHeight : config.height;
		RootPanel.get().remove(testInfo);
		
		Info.display(config);
	}

	public static <M extends ModelData> TreeGrid<M> createTreeGrid(TreeStore<M> store, ColumnModel cm) {
		TreeGrid<M> grid = new TreeGrid<M>(store, cm);
		
		TreeGridView view = new TreeGridView() {
			
			@Override
	        protected void clean() {
				if (grid == null || !grid.isViewReady() || !isBufferEnabled()) {
					return;
				}
				
				super.clean();
	        }
		};
		
		view.setBufferEnabled(false);
	    grid.setView(view);
	    
	    grid.setTrackMouseOver(false);
	    grid.setColumnLines(true);
	    grid.setAutoExpandMax(Integer.MAX_VALUE);
	    
	    grid.getStyle().setNodeCloseIcon(null);
		grid.getStyle().setNodeOpenIcon(null);
		
		return grid;
	}

}