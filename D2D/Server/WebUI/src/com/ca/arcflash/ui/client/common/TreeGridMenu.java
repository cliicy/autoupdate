package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;

public class TreeGridMenu<M extends ModelData> extends TreeGrid<M> {

	private Menu menu;

	public TreeGridMenu(TreeStore<M> store, ColumnModel cm) {
		super(store, cm);
		this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.setHideHeaders(true);
	} 
	
	public TreeGridMenu(TreeStore<M> store, ColumnModel cm, Menu menu) {
		super(store, cm);
		List<ColumnConfig> configs = cm.getColumns();
		List<ColumnConfig> configsNew = new ArrayList<ColumnConfig>();
		for (ColumnConfig config : configs) {
			config.setStyle("border: 0px;");
			configsNew.add(config);
		}
		configsNew.add(getColumnConfig());
		ColumnModel cmNew = new ColumnModel(configsNew);
		this.cm = cmNew;
		this.menu = menu;
		setView(new TreeGridMenuView());
		this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.setHideHeaders(true);

	}

	public ColumnConfig getColumnConfig() {
		GridCellRenderer<M> renderer = new GridCellRenderer<M>() {
			public Object render(final M model, String property,
					ColumnData config, final int rowIndex, final int colIndex,
					ListStore<M> store, Grid<M> grid) {
				if (model.get("showmenu") != null
						&& (Boolean) model.get("showmenu") == false) {
					return "";
				} else {
					Label label = new Label();
					label.setHtml(">>");
					label.addListener(Events.OnClick,
							new Listener<BoxComponentEvent>() {

								@Override
								public void handleEvent(BoxComponentEvent be) {
									if (menu != null) {
										menu.showAt(be.getClientX(), be
												.getClientY());
									}
								}

							});
					label.sinkEvents(Events.OnClick.getEventCode());
					return label;
				}
			}
		};
		ColumnConfig menuColumn = new ColumnConfig("menu", "", 20);
		menuColumn.setSortable(false);
		menuColumn.setMenuDisabled(true);
		menuColumn.setRenderer(renderer);
		menuColumn.setHidden(true);
		return menuColumn;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

}
