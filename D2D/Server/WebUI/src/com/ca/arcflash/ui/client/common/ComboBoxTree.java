package com.ca.arcflash.ui.client.common;


import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

@SuppressWarnings("deprecation")
public class ComboBoxTree<D extends ModelData> extends TriggerField<D> implements SelectionProvider<D> {

  /**
   * ComboBox error messages.
   */
  public class ComboBoxMessages extends TextFieldMessages {

    private String loadingText = GXT.MESSAGES.loadMask_msg();
    private String valueNoutFoundText;

    /**
     * Returns the loading text.
     * 
     * @return the loading text
     */
    public String getLoadingText() {
      return loadingText;
    }

    /**
     * Returns the value not found error text.
     * 
     * @return the error text
     */
    public String getValueNoutFoundText() {
      return valueNoutFoundText;
    }

    /**
     * Sets the loading text.
     * 
     * @param loadingText the loading text
     */
    public void setLoadingText(String loadingText) {
      this.loadingText = loadingText;
    }

    /**
     * When using a name/value combo, if the value passed to setValue is not
     * found in the store, valueNotFoundText will be displayed as the field text
     * if defined.
     * 
     * @param valueNoutFoundText
     */
    public void setValueNoutFoundText(String valueNoutFoundText) {
      this.valueNoutFoundText = valueNoutFoundText;
    }

  }

  /**
   * TriggerAction enum.
   */
  public enum TriggerAction {
    ALL, QUERY;
  }

  protected boolean autoComplete = false;
  protected boolean delayedCheck;

  protected TreeStore<D> store;
  private BaseEventPreview eventPreview;
  private boolean expanded;
  private El footer;
  private boolean forceSelection;
  private InputElement hiddenInput;
  private String lastSelectionText;
  private boolean lazyRender = true, initialized;
  private LayoutContainer list;
  private String listAlign = "tl-bl?";
  private String listStyle = "x-combo-list";
  private TreePanel<D> treePanel;
  private int maxHeight = 300;
  private int minChars = 4;
  private int minListWidth = 70;
  private D selectedItem;
  private TriggerAction triggerAction = TriggerAction.QUERY;
  private boolean typeAhead;
  private String displayProp;
  private String valueField;

  /**
   * Creates a combo box.
   */
  @SuppressWarnings("unchecked")
  public ComboBoxTree(TreeStore<D> treeStore) {
    this.setStore(treeStore);
    this.messages = new ComboBoxMessages();
    treePanel = new TreePanel<D>(store);
    setPropertyEditor(new ListModelPropertyEditor<D>());
    monitorWindowResize = true;
    windowResizeDelay = 0;
    initComponent();
  }

  
  public void setDisplayProperty(String strDisplay) {
	displayProp = strDisplay;
    this.treePanel.setDisplayProperty(strDisplay);
  }

  public void addSelectionChangedListener(SelectionChangedListener<D> listener) {
    addListener(Events.SelectionChange, listener);
  }

  @Override
  public void clear() {
    getStore().clearFilters();
    boolean f = forceSelection;
    forceSelection = false;
    super.clear();
    forceSelection = f;
  }

  /**
   * Clears any text/value currently set in the field.
   */
  public void clearSelections() {
    setRawValue("");
    lastSelectionText = "";
    applyEmptyText();
    value = null;
  }

  /**
   * Hides the dropdown list if it is currently expanded. Fires the
   * <i>Collapse</i> event on completion.
   */
  public void collapse() {
    if (!expanded) {
      return;
    }
    eventPreview.remove();
    expanded = false;
    list.hide();

    RootPanel.get().remove(list);
    if (GXT.isAriaEnabled() && hasFocus) {
      // inspect 32 is keeping focus on hidden list item in dropdown
      input.blur();
      input.focus();
    }
    fireEvent(Events.Collapse, new FieldEvent(this));
  }

  /**
   * Execute a query to filter the dropdown list. Fires the BeforeQuery event
   * prior to performing the query allowing the query action to be canceled if
   * needed.
   * 
   * @param q the query
   * @param forceAll true to force the query to execute even if there are
   *          currently fewer characters in the field than the minimum specified
   *          by the minChars config option. It also clears any filter
   *          previously saved in the current store
   */
  public void doQuery(String q, boolean forceAll) {
    if (q == null) {
      q = "";
    }

    FieldEvent fe = new FieldEvent(this);
    fe.setValue(q);
    if (!fireEvent(Events.BeforeQuery, fe)) {
      return;
    }

    if (forceAll || q.length() >= minChars) {
      expand();
      selectedItem = null;
      onLoad(null);
    }
  }

  /**
   * Expands the dropdown list if it is currently hidden. Fires the
   * <i>expand</i> event on completion.
   */
  public void expand() {
    if (expanded || !hasFocus) {
      return;
    }

    if (!initialized) {
      createList(false);
    } else {
      RootPanel.get().add(list);
    }

    list.show();
    list.layout();
    list.el().updateZIndex(0);
    restrict();

    eventPreview.add();
    expanded = true;
    fireEvent(Events.Expand, new FieldEvent(this));
  }

  /**
   * Returns the combo's TreePanel view.
   * 
   * @return the view
   */
  public TreePanel<D> getTreePanel() {
    return treePanel;
  }

  /**
   * Returns the loading text.
   * 
   * @return the loading text
   */
  public String getLoadingText() {
    return getMessages().getLoadingText();
  }

  /**
   * Returns the dropdown list's max height.
   * 
   * @return the max height
   */
  public int getMaxHeight() {
    return maxHeight;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ComboBoxMessages getMessages() {
    return (ComboBoxMessages) messages;
  }

  /**
   * Returns the min characters used for autocompete and typeahead.
   * 
   * @return the minimum number of characters
   */
  public int getMinChars() {
    return minChars;
  }

  /**
   * Returns the dropdown list's min width.
   * 
   * @return the min width
   */
  public int getMinListWidth() {
    return minListWidth;
  }

  @Override
  public ListModelPropertyEditor<D> getPropertyEditor() {
    return (ListModelPropertyEditor<D>) propertyEditor;
  }

  public List<D> getSelection() {
    List<D> sel = new ArrayList<D>();
    D v = getValue();
    if (v != null) {
      sel.add(v);
    }
    return sel;
  }

  /**
   * Returns the combo's store.
   * 
   * @return the store
   */
  public TreeStore<D> getStore() {
    return store;
  }

  /**
   * Returns the trigger action.
   * 
   * @return the trigger action
   */
  public TriggerAction getTriggerAction() {
    return triggerAction;
  }

  @Override
  public D getValue() {
    if (!initialized) {
      return value;
    }
    if (store != null) {
      getPropertyEditor().setList(store.getModels());
    }

    doForce();
    return super.getValue();
  }

  /**
   * Returns the value field name.
   * 
   * @return the value field name
   */
  public String getValueField() {
    return valueField;
  }

  /**
   * Returns the combo's list view.
   * 
   * @return the view
   */
  public TreePanel<D> getView() {
    return treePanel;
  }

  /**
   * Returns <code>true</code> if the panel is expanded.
   * 
   * @return the expand state
   */
  public boolean isExpanded() {
    return expanded;
  }

  /**
   * Returns true if lazy rendering is enabled.
   * 
   * @return true of lazy rendering
   */
  public boolean isLazyRender() {
    return lazyRender;
  }

  /**
   * Returns true if type ahead is enabled.
   * 
   * @return the type ahead state
   */
  public boolean isTypeAhead() {
    return typeAhead;
  }

  public void removeSelectionListener(SelectionChangedListener<D> listener) {
    removeListener(Events.SelectionChange, listener);
  }

  @Override
  public void reset() {
    getStore().clearFilters();
    boolean f = forceSelection;
    forceSelection = false;
    super.reset();
    forceSelection = f;
  }

  public void select(D sel) {
    System.out.println("===select D===" + sel);
  }

  /**
   * Select an item in the dropdown list by its numeric index in the list. This
   * function does NOT cause the select event to fire. The list must expanded
   * for this function to work, otherwise use #setValue.
   * 
   * @param index
   *          the index of the item to select
   */
  public void select(int index) {
    // select(store.getAt(index));
    System.out.println("===select int===" + index);
  }

  /**
   * The underlying data field name to bind to this ComboBox (defaults to
   * 'text').
   * 
   * @param displayField the display field
   */
  public void setDisplayField(String displayField) {
    getPropertyEditor().setDisplayProperty(displayField);
  }

  /**
   * Sets the panel's expand state.
   * 
   * @param expand <code>true<code> true to expand
   */
  public void setExpanded(boolean expand) {
    this.expanded = expand;
    if (isRendered()) {
      if (expand) {
        expand();
      } else {
        collapse();
      }
    }
  }

  /**
   * Sets whether the combo's value is restricted to one of the values in the
   * list, false to allow the user to set arbitrary text into the field
   * (defaults to false).
   * 
   * @param forceSelection true to force selection
   */
  public void setForceSelection(boolean forceSelection) {
    this.forceSelection = forceSelection;
  }

  /**
   * True to lazily render the combo's drop down list (default to true,
   * pre-render).
   * 
   * @param lazyRender true to lazy render the drop down list
   */
  public void setLazyRender(boolean lazyRender) {
    this.lazyRender = lazyRender;
  }

  /**
   * Sets a valid anchor position value. See {@link El#alignTo} for details on
   * supported anchor positions (defaults to 'tl-bl?').
   * 
   * @param listAlign the new list align value
   */
  public void setListAlign(String listAlign) {
    this.listAlign = listAlign;
  }

  /**
   * Sets the style for the drop down list (defaults to 'x-combo-list');
   * 
   * @param listStyle the list style
   */
  public void setListStyle(String listStyle) {
    this.listStyle = listStyle;
  }

  /**
   * Sets the loading text.
   * 
   * @param loadingText the loading text
   */
  public void setLoadingText(String loadingText) {
    getMessages().setLoadingText(loadingText);
  }

  /**
   * Sets the maximum height in pixels of the dropdown list before scrollbars
   * are shown (defaults to 300).
   * 
   * @param maxHeight the max hieght
   */
  public void setMaxHeight(int maxHeight) {
    this.maxHeight = maxHeight;
  }

  /**
   * Sets the minimum number of characters the user must type before
   * autocomplete and typeahead active (defaults to 4 if remote, or 0 if local).
   * 
   * @param minChars
   */
  public void setMinChars(int minChars) {
    this.minChars = minChars;
  }

  /**
   * Sets the minimum width of the dropdown list in pixels (defaults to 70, will
   * be ignored if listWidth has a higher value).
   * 
   * @param minListWidth the min width
   */
  public void setMinListWidth(int minListWidth) {
    this.minListWidth = minListWidth;
  }

  @Override
  public void setPropertyEditor(PropertyEditor<D> propertyEditor) {
    assert propertyEditor instanceof ListModelPropertyEditor<?> : "PropertyEditor must be a ListModelPropertyEditor instance";
    super.setPropertyEditor(propertyEditor);
  }

  @Override
  public void setRawValue(String text) {
    if (rendered) {
      if (text == null) {
        String msg = getMessages().getValueNoutFoundText();
        text = msg != null ? msg : "";
      }
      getInputEl().setValue(text);
    }
  }

  public void setSelection(List<D> selection) {
    if (selection.size() > 0) {
      setValue(selection.get(0));
    } else {
      setValue(null);
    }
  }

  /**
   * Sets the combo's store.
   * 
   * @param store the store
   */
  public void setStore(TreeStore<D> store) {
    this.store = store;
  }

  /**
   * The action to execute when the trigger field is activated. Use
   * {@link TriggerAction#ALL} to run the query specified by the allQuery config
   * option (defaults to {@link TriggerAction#QUERY}).
   * 
   * @param triggerAction the trigger action
   */
  public void setTriggerAction(TriggerAction triggerAction) {
    this.triggerAction = triggerAction;
  }

  @Override
  public void setValue(D value) {
    D oldValue = this.value;
    super.setValue(value);
    updateHiddenValue();
    this.lastSelectionText = getRawValue();
    if (!Util.equalWithNull(oldValue, value)) {
      SelectionChangedEvent<D> se = new SelectionChangedEvent<D>(this, getSelection());
      fireEvent(Events.SelectionChange, se);
    }
  }

  /**
   * Sets the model field used to retrieve the "value" from the model. If
   * specified, a hidden form field will contain the value. The hidden form
   * field name will be the combo's field name plus "-hidden".
   * 
   * @param valueField the value field name
   */
  public void setValueField(String valueField) {
    this.valueField = valueField;
  }

  protected void collapseIf(PreviewEvent pe) {
    if (!list.el().isOrHasChild(pe.getTarget()) && !el().isOrHasChild(pe.getTarget())) {
      collapse();
    }
  }

  protected void doForce() {
    if (forceSelection) {
      boolean f = forceSelection;
      forceSelection = false;
      String rv = getRawValue();
      if (getAllowBlank() && (rv == null || rv.equals(""))) {
        forceSelection = f;
        return;
      }

      if (getValue() == null) {
        if (lastSelectionText != null && !"".equals(lastSelectionText)) {
          setRawValue(lastSelectionText);
        } else {
          applyEmptyText();
        }
      }
      forceSelection = f;
    }
  }

  protected D findModel(String property, String value) {
    if (value == null) return null;
    for (D model : store.getModels()) {
      if (value.equals(getPropertyEditor().getStringValue(model))) {
        return model;
      }
    }
    return null;
  }

  protected void fireKey(FieldEvent fe) {
    if (fe.isNavKeyPress() && !isExpanded() && !delayedCheck) {
      fireEvent(Events.SpecialKey, fe);
    }
  }

  protected Element getAlignElement() {
    return getElement();
  }

  @Override
  protected El getFocusEl() {
    return input;
  }

  protected boolean hasFocus() {
    return hasFocus || expanded;
  }

  @SuppressWarnings("rawtypes")
  protected void initComponent() {
    eventPreview = new BaseEventPreview() {
      @Override
      protected boolean onPreview(PreviewEvent pe) {
        switch (pe.getType().getEventCode()) {
        case Event.ONSCROLL:
        case Event.ONMOUSEWHEEL:
        case Event.ONMOUSEDOWN:
          collapseIf(pe);
          break;
        case Event.ONKEYPRESS:
          if (expanded && pe.getKeyCode() == KeyCodes.KEY_ENTER) {
            pe.stopEvent();
            onViewClick(pe, false);
          }
          break;
        }
        return true;
      }
    };
    eventPreview.setAutoHide(false);

    new KeyNav(this) {

      @Override
      public void onDown(ComponentEvent ce) {
        if (!isReadOnly()) {
          ce.cancelBubble();
          if (!isExpanded()) {
            onTriggerClick(ce);
          } else {
            selectNext();
          }
        }
      }

      @Override
      public void onEnter(ComponentEvent ce) {
        if (expanded) {
          ce.cancelBubble();
          onViewClick(ce, false);
          delayedCheck = true;
          unsetDelayCheck();
        }
      }

      @Override
      public void onEsc(ComponentEvent ce) {
        if (expanded) {
          ce.cancelBubble();
          collapse();
        }
      }

      @Override
      public void onTab(ComponentEvent ce) {
        if (expanded) {
          onViewClick(ce, false);
        }
      }

      @Override
      public void onUp(ComponentEvent ce) {
        if (expanded) {
          ce.cancelBubble();
          selectPrev();
        }
      }

    };
  }

  protected void initTree() {
    if (treePanel == null) {
    	treePanel = new TreePanel<D>(store);
    }
    String style = listStyle;
    treePanel.setStyleAttribute("overflowX", "hidden");
    treePanel.addStyleName(style + "-inner");
    treePanel.setStyleAttribute("padding", "0px");
    treePanel.setBorders(false);

    Listener<SelectionChangedEvent<D>> selectChangeListner = new Listener<SelectionChangedEvent<D>>() {
    	public void handleEvent(SelectionChangedEvent<D> se) {
            selectedItem = treePanel.getSelectionModel().getSelectedItem();
            if (GXT.isAriaEnabled()) {
              System.out.println("===SelectionChangedEvent===" + se);
            }
            if(selectedItem != null && displayProp != null && selectedItem.get(displayProp) != null) {
	            setRawValue(selectedItem.get(displayProp).toString());
	            fireEvent(Events.Change, new BaseEvent(selectedItem));
            } else {
            	if(selectedItem == null) {
            		System.out.println("selectedItem is NULL");
            	} else if(displayProp == null) {
            		System.out.println("displayProp is NULL");
            	} else {
            		System.out.println("displayProp is " + displayProp);
            		System.out.println("selectedItem.get(displayProp) is NULL");
            	}
            }
            list.hide();
        }
    };
    
    treePanel.getSelectionModel().addListener(Events.SelectionChange, selectChangeListner); 

    treePanel.addListener(Events.Select, new Listener<ListViewEvent<D>>() {
      public void handleEvent(ListViewEvent<D> le) {
        onViewClick(le, true);
      }
    });

    list = new LayoutContainer() {
      @Override
      protected void doAttachChildren() {
        super.doAttachChildren();
      }

      @Override
      protected void doDetachChildren() {
        super.doDetachChildren();
      }

      @Override
      protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        eventPreview.getIgnoreList().add(getElement());

        setAriaRole("presentation");
      }
    };
    list.setScrollMode(Scroll.NONE);
    list.setShim(true);
    list.setShadow(true);
    list.setBorders(true);
    list.setStyleName(style);
    list.hide();
    list.addStyleName("x-ignore");

    assert store != null : "ComboBox needs a store";

    list.add(treePanel);

    if (!lazyRender) {
      createList(true);
    }
  }

  protected void onBeforeLoad(StoreEvent<D> se) {
    if (!hasFocus()) {
      return;
    }
    if (expanded) {
      restrict();
    }
  }

  @Override
  protected void onDetach() {
    collapse();
    super.onDetach();
    if (eventPreview != null) {
      eventPreview.remove();
    }
  }

  protected void onEmptyResults() {
    collapse();
  }

  @Override
  protected void onKeyDown(FieldEvent fe) {
    if (fe.getKeyCode() == KeyCodes.KEY_TAB) {
      if (expanded) {
        onViewClick(fe, false);
      }
    }
    super.onKeyDown(fe);
  }

  protected void onLoad(StoreEvent<D> se) {
    if (!isAttached() || !hasFocus()) {
      return;
    }
    if (store.getAllItems().size() > 0) {
      if (expanded) {
        restrict();
      } else {
        expand();
      }

    } else {
      onEmptyResults();
    }
  }

  protected void onRender(Element parent, int index) {
    super.onRender(parent, index);
    initTree();

    if (!autoComplete) {
      getInputEl().dom.setAttribute("autocomplete", "off");
    }

    if (valueField != null) {
      hiddenInput = Document.get().createHiddenInputElement().cast();
      hiddenInput.setName(getName() + "-hidden");
      getElement().appendChild(hiddenInput);
    }

    eventPreview.getIgnoreList().add(getElement());

    setAriaState("aria-owns", treePanel.getId());
    setAriaRole("combobox");
  }

  protected void onSelect(D model, int index) {
    FieldEvent fe = new FieldEvent(this);
    if (fireEvent(Events.BeforeSelect, fe)) {
      setValue(model);
      collapse();
      fireEvent(Events.Select, fe);
    }
  }

  protected void onTriggerClick(ComponentEvent ce) {
    super.onTriggerClick(ce);
    if (expanded) {
      collapse();
    } else {
      onFocus(null);
      if (triggerAction != TriggerAction.ALL) {
        doQuery(getRawValue(), true);
      }
    }
    getInputEl().focus();
  }

  protected void onTypeAhead() {
    if (store.getAllItems().size() > 0) {
      D m = store.getChild(0);
      String newValue = propertyEditor.getStringValue(m);
      int len = newValue.length();
      int selStart = getRawValue().length();
      if (selStart != len) {
        setRawValue(newValue);
        select(selStart, newValue.length());
      }
    }
  }

  protected void onUpdate(StoreEvent<D> se) {
    // handle the case when the selected model's display property is updated
    if (!getRawValue().equals("") && getValue() == null && forceSelection) {
      setValue(null);
      store.clearFilters();
      setValue(se.getModel());
    }
  }

  protected void onViewClick(DomEvent de, boolean focus) {
   
  }

  protected void onWindowResize(int width, int height) {
    collapse();
  }

  protected void restrict() {
    list.el().setVisibility(false);
    treePanel.setHeight("auto");
    list.setHeight("auto");
    int w = Math.max(getWidth(), minListWidth);

    int fh = footer != null ? footer.getHeight() : 0;
    int fw = list.el().getFrameWidth("tb") + fh;

    int h = treePanel.getHeight() + fw;

    int mH = Math.min(maxHeight, Window.getClientHeight() - 10);
    h = Math.min(h, mH);
    list.setSize(w, h);
    list.el().alignTo(getAlignElement(), listAlign, null);

    h -= fw;

    int width = w - list.el().getFrameWidth("lr");
    treePanel.syncSize();
    treePanel.setSize(width, h);

    int y = list.el().getY();
    int b = y + h + fw;
    int vh = XDOM.getViewportSize().height + XDOM.getBodyScrollTop();
    if (b > vh) {
      y = y - (b - vh) - 5;
      list.el().setTop(y);
    }
    list.el().setVisibility(true);
  }

  @Override
  protected void triggerBlur(ComponentEvent ce) {
    doForce();
    collapse();
    super.triggerBlur(ce);
  }

  protected void unsetDelayCheck() {
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        delayedCheck = false;
      }
    });
  }

  @Override
  protected boolean validateBlur(DomEvent e, Element target) {
    return list == null || (list != null && !list.isVisible() && !list.getElement().isOrHasChild(target));
  }

  @Override
  protected boolean validateValue(String value) {
    if (forceSelection) {
      boolean f = forceSelection;
      forceSelection = false;
      if (getValue() == null) {
        forceSelection = f;
        String rv = getRawValue();
        if (getAllowBlank() && (rv == null || rv.equals(""))) {
          return true;
        }
        markInvalid(getMessages().getBlankText());
        return false;
      }
      forceSelection = f;
    }
    return super.validateValue(value);
  }

  private void createList(boolean remove) {
    RootPanel.get().add(list);
    initialized = true;
    if (remove) {
      RootPanel.get().remove(list);
    }
  }

  private void selectNext() {
    int count = store.getAllItems().size();
    if (count > 0) {
      int selectedIndex = store.indexOf(selectedItem);
      if (selectedIndex == -1) {
        select(0);
      } else if (selectedIndex < count - 1) {
        select(selectedIndex + 1);
      }
    }
  }

  private void selectPrev() {
    int count = store.getAllItems().size();
    if (count > 0) {
      int selectedIndex = store.indexOf(selectedItem);
      if (selectedIndex == -1) {
        select(0);
      } else if (selectedIndex != 0) {
        select(selectedIndex - 1);
      }
    }
  }

  private void updateHiddenValue() {
    if (hiddenInput != null) {
      String v = "";
      D val = getValue();
      if (val != null && val.get(valueField) != null) {
        v = ((Object) val.get(valueField)).toString();
      }
      hiddenInput.setValue(v);
    }
  }

}