package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.google.gwt.user.client.Event;

public class FixedDatePicker extends DatePicker {
	
	private DateWrapper selectedDate = new DateWrapper();

	private int mpSelMonth = -1;
	
	private int mpSelYear = -1;
	@Override
	public void onComponentEvent(ComponentEvent ce) {
		super.onComponentEvent(ce);
		if (ce.getEventTypeInt() == Event.ONMOUSEUP) {
			ce.stopEvent();
			El target = ce.getTargetEl();
			String cls = target.getStyleName();
			if (cls.equals(" x-icon-btn x-nodrag x-date-left-icon x-date-left-icon-over")) {
				selectedDate = selectedDate.addMonths(-1);
				this.setValue(selectedDate.asDate());
			} else if (cls.equals(" x-icon-btn x-nodrag x-date-right-icon x-date-right-icon-over")) {
				selectedDate = selectedDate.addMonths(1);
				this.setValue(selectedDate.asDate());

			}
		}

	}
    @Override
	protected void onClick(ComponentEvent be) {
		super.onClick(be);
		be.stopEvent();
		El target = be.getTargetEl();
		El pn = null;
		if ((pn = target.findParent("td.x-date-mp-month", 2)) != null) {
			mpSelMonth = pn.dom.getPropertyInt("xmonth");
		} else if ((pn = target.findParent("td.x-date-mp-year", 2)) != null) {
			mpSelYear = pn.dom.getPropertyInt("xyear");
		} else if (target.is("button.x-date-mp-ok")) {
			DateWrapper wrap = new DateWrapper(getValue());
			if (mpSelYear == -1)
				mpSelYear = wrap.getFullYear();
			if (mpSelMonth == -1)
				mpSelMonth = wrap.getMonth();
			selectedDate = new DateWrapper(mpSelYear, mpSelMonth, selectedDate
					.getDate());
			this.setValue(selectedDate.asDate());
			mpSelYear = -1;
			mpSelMonth = -1;
		}
	}

}
