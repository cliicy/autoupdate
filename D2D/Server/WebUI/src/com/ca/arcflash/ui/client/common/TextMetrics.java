package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;

public class TextMetrics {
	private static TextMetrics instance;

	  static {
	    instance = new TextMetrics();
	  }

	  /**
	   * Returns the singleton instance.
	   * 
	   * @return the text metrics instance
	   */
	  public static TextMetrics get() {
	    return instance;
	  }

	  private El el;

	  private TextMetrics() {
	    el = new El(DOM.createDiv());
	    DOM.appendChild(XDOM.getBody(), el.dom);
	    el.makePositionable(true);
	    el.setLeftTop(-10000, -10000);
	    el.setVisibility(false);
	  }

	  /**
	   * Binds this TextMetrics instance to an element from which to copy existing
	   * CSS styles that can affect the size of the rendered text.
	   * 
	   * @param el the element
	   */
	  public void bind() {
	    this.el.setStyleAttribute("fontSize", "11px");
	    //this.el.setStyleAttribute("fontWeight", el.getStyleAttribute("fontWeight"));
	    //this.el.setStyleAttribute("fontStyle", el.getStyleAttribute("fontStyle"));
	    this.el.setStyleAttribute("fontFamily", "arial, tahoma, helvetica, sans-serif;");
	    /*this.el.setStyleAttribute("lineHeight", el.getStyleAttribute("lineHeight"));
	    this.el.setStyleAttribute("textTransform", el.getStyleAttribute("textTransform"));
	    this.el.setStyleAttribute("letterSpacing", el.getStyleAttribute("letterSpacing"));*/
	  }

	  /**
	   * Returns the measured height of the specified text. For multiline text, be
	   * sure to call {@link #setFixedWidth} if necessary.
	   * 
	   * @param text the text to be measured
	   * @return the height in pixels
	   */
	  public int getHeight(String text) {
	    return getSize(text).height;
	  }

	  /**
	   * Returns the size of the specified text based on the internal element's
	   * style and width properties.
	   * 
	   * @param text the text to measure
	   * @return the size
	   */
	  public Size getSize(String text) {
	    el.dom.setInnerHTML(text);
	    Size size = el.getSize();
	    el.dom.setInnerHTML("");
	    return size;
	  }

	  /**
	   * Returns the measured width of the specified text.
	   * 
	   * @param text the text to measure
	   * @return the width in pixels
	   */
	  public int getWidth(String text) {
	    el.setStyleAttribute("width", "auto");
	    return getSize(text).width;
	  }

	  /**
	   * Sets a fixed width on the internal measurement element. If the text will be
	   * multiline, you have to set a fixed width in order to accurately measure the
	   * text height.
	   * 
	   * @param width the width to set on the element
	   */
	  public void setFixedWidth(int width) {
	    el.setWidth(width);
	  }
}

