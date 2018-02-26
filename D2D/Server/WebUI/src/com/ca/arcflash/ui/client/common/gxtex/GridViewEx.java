package com.ca.arcflash.ui.client.common.gxtex;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.grid.GridView;

/**
 * This class encapsulates the user interface of an {@link Grid}. Methods of
 * this class may be used to access user interface elements to enable special
 * display effects. Do not change the DOM structure of the user interface. </p>
 * <p />
 * This class does not provide ways to manipulate the underlying data. The data
 * model of a Grid is held in an {@link ListStore}.
 */
public class GridViewEx extends GridView {

	@Override
	protected Menu createContextMenu(final int colIndex) {
		final Menu menu = new Menu();

		if (cm.isSortable(colIndex)) {
			MenuItem item = new MenuItemEx();
			item.setHtml(GXT.MESSAGES.gridView_sortAscText());
			item.setIcon(getImages().getSortAsc());
			item.addSelectionListener(new SelectionListener<MenuEvent>() {
				public void componentSelected(MenuEvent ce) {
					doSort(colIndex, SortDir.ASC);
				}

			});
			menu.add(item);

			item = new MenuItemEx();
			item.setHtml(GXT.MESSAGES.gridView_sortDescText());
			item.setIcon(getImages().getSortDesc());
			item.addSelectionListener(new SelectionListener<MenuEvent>() {
				public void componentSelected(MenuEvent ce) {
					doSort(colIndex, SortDir.DESC);
				}
			});
			menu.add(item);
		}

		MenuItem columns = new MenuItemEx();
		columns.setHtml(GXT.MESSAGES.gridView_columnsText());
		columns.setIcon(getImages().getColumns());
		columns.setData("gxt-columns", "true");

		final Menu columnMenu = new Menu();

		int cols = cm.getColumnCount();
		for (int i = 0; i < cols; i++) {
			if (shouldNotCount(i, false)) {
				continue;
			}
			final int fcol = i;
			final CheckMenuItemEx check = new CheckMenuItemEx();
			check.setHideOnClick(false);
			check.setHtml(cm.getColumnHeader(i));
			check.setChecked(!cm.isHidden(i));
			check.addSelectionListener(new SelectionListener<MenuEvent>() {
				public void componentSelected(MenuEvent ce) {
					cm.setHidden(fcol, !cm.isHidden(fcol));
					restrictMenu(columnMenu);
				}
			});
			columnMenu.add(check);
		}

		restrictMenu(columnMenu);
		columns.setEnabled(columnMenu.getItemCount() > 0);
		columns.setSubMenu(columnMenu);
		menu.add(columns);
		return menu;
	}

	private void restrictMenu(Menu columns) {
		int count = 0;
		for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
			if (!shouldNotCount(i, true)) {
				count++;
			}
		}

		if (count == 1) {
			for (Component item : columns.getItems()) {
				CheckMenuItemEx ci = (CheckMenuItemEx) item;
				if (ci.isChecked()) {
					ci.disable();
				}
			}
		} else {
			for (Component item : columns.getItems()) {
				item.enable();
			}
		}
	}

	private boolean shouldNotCount(int columnIndex, boolean includeHidden) {
		return cm.getColumnHeader(columnIndex) == null
				|| cm.getColumnHeader(columnIndex).equals("")
				|| (includeHidden && cm.isHidden(columnIndex))
				|| cm.isFixed(columnIndex);
	}

}
