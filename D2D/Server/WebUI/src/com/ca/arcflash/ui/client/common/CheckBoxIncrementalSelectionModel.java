package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;

public class CheckBoxIncrementalSelectionModel<M extends ModelData> extends CheckBoxSelectionModel<M> {
	
	private abstract class IncrementalSelectionCommand implements IncrementalCommand {

		private int index;
		private int total;
		
		public IncrementalSelectionCommand() {
			total = grid.getStore().getCount();
		}
		
		protected abstract void doSelection(int start, int end);
		
		@Override
		public boolean execute() {
			if (index >= total) {
				grid.unmask();
				return false;
			}
			
			int end = index + incremental;
			if (end > total) {
				end = total;
			}
			
			doSelection(index, end);
			
			index = end;
			
			return true;
		}
		
	}
	
	private int incremental;
	
	public int getIncremental() {
		return incremental;
	}

	public void setIncremental(int incremental) {
		this.incremental = incremental;
	}

	public CheckBoxIncrementalSelectionModel() {
		incremental = 10;
	}
	
	private boolean enabled = true;
	
	
	@Override
	public void selectAll() {
		if(!enabled)
			return;
		IncrementalCommand command = new IncrementalSelectionCommand() {

			@Override
			protected void doSelection(int start, int end) {
				select(start, end, true);
			}
			
		};
		
		DeferredCommand.addCommand(command);
	}
	
	@Override
	public void deselectAll() {
		if(!enabled)
			return;
		IncrementalCommand command = new IncrementalSelectionCommand() {

			@Override
			protected void doSelection(int start, int end) {
				deselect(start, end);
			}
			
		};
		
		DeferredCommand.addCommand(command);
	}
	
	@Override
	 protected void onHeaderClick(GridEvent<M> e) {
		if(!enabled)
			return;
		 ColumnConfig c = grid.getColumnModel().getColumn(e.getColIndex());
		 if (c == config) {
			 grid.mask(UIContext.Constants.waiting());
			 super.onHeaderClick(e);
		 }
	}
	
	
	
	public void setEnabled(boolean isEnabled) {
		enabled = isEnabled;
	}

}
