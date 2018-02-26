package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.google.gwt.user.client.Element;
import com.sencha.gxt.core.client.dom.XDOM;

public class AdsTimeField extends TriggerField<String>{
	
	private String debugID;
	private AdsTimeMenu menu;
	private Listener<BaseEvent> changeListener;
	
	public AdsTimeField(String id) {
		super();
		debugID = id;
		setTriggerStyle("x-form-date-trigger");
		newMenu();
		populateValue(menu.getTimePanel());
	}
	
	private void populateValue(AdsTimePanel panel) {	
		setValue(ScheduleUtils.formatTime(panel.getHour(), panel.getMinute()));
	}
	
	private void newMenu() {
		menu = new AdsTimeMenu(debugID);
		/*menu.getOKButton().addListener(Events.Select, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				populateValue(menu.getTimePanel());
				menu.hide();
		        el().blur();
			}
		});
		
		menu.getCancelButton().addListener(Events.Select, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				menu.hide();
		        el().blur();
			}
		});*/

		menu.addListener(Events.Hide, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent be) {
	          focus();
	        }
	    });
		
		menu.getTimePanel().setUpdate(new IUpdate() {
			@Override
			public void refresh() {
				populateValue(menu.getTimePanel());
			}
			
			@Override
			public void close() {
				menu.hide();
		        el().blur();
			}
		});
	}
	
	protected void expand() {
	    menu.show(el().dom, "tl-bl?");
	    if(XDOM.getTopZIndex()> com.extjs.gxt.ui.client.core.XDOM.getTopZIndex())
	    	menu.setZIndex(XDOM.getTopZIndex()+1);
	    menu.focus();
	    this.populateValue(menu.getTimePanel());
	  }

	  protected void onDown(FieldEvent fe) {
	    fe.cancelBubble();
	    if (menu == null || !menu.isAttached()) {
	      expand();
	    }
	  }

	  @Override
	  protected void onRender(Element target, int index) {
	    super.onRender(target, index);

	    new KeyNav<FieldEvent>(this) {
	      @Override
	      public void onDown(FieldEvent fe) {
	        
	      }

	      @Override
	      public void onEsc(FieldEvent fe) {
	        if (menu != null && menu.isAttached()) {
	          menu.hide();
	        }
	      }
	    };
	  }
	  
	  @Override
	  protected void onDetach() {
	    super.onDetach();
	    if (menu != null && menu.isAttached()) {
	        menu.hide();
	     }
	  }

	  @Override
	  protected void onTriggerClick(ComponentEvent ce) {
	    super.onTriggerClick(ce);
	    expand();

	    getInputEl().focus();
	  }

	  @Override
	  protected boolean validateBlur(DomEvent e, Element target) {
	    return menu == null || (menu != null && !menu.isVisible());
	  }
	  
	  public AdsTimePanel getTimePanel() {
		  return menu.getTimePanel();
	  }
	  
	  public void setValue(int hours, int minutes) {
		  menu.getTimePanel().setValue(hours, minutes);
		  this.populateValue(menu.getTimePanel());
	  }
	  
	  public DayTimeModel getTimeValue() {
		  return menu.getTimePanel().getTimeValue();
	  }
	  
	  public void setTimeValue(DayTimeModel model) {
		  DayTimeModel oldData = menu.getTimePanel().getTimeValue();
		  menu.getTimePanel().setValue(model.getHour(), model.getMinutes());
		  this.populateValue(menu.getTimePanel());
		  if(model.getHour() != oldData.getHour() || model.getMinutes() != oldData.getMinutes()) {
			  if(changeListener != null)
				  changeListener.handleEvent(new BaseEvent(Events.Change));
		  }
	  }
	  
	  /*public void addChangeListener(Listener<BaseEvent> listener) {
		  if(menu != null) {
			  menu.getOKButton().addListener(Events.Select, listener);
			  changeListener = listener;
		  }
	  }*/
}
