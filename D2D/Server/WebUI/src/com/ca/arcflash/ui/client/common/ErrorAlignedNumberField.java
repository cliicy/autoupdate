package com.ca.arcflash.ui.client.common;

import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.validator.MaxNumberValidator;
import com.sencha.gxt.widget.core.client.form.validator.MinNumberValidator;

/**
 * The class is used to fix the issue that the error icon of 
 * the numberfield validator overlap the unit or the text after 
 * the field. The error icon is put after the sibling element of 
 * the numberfield's parent element.
 * @author zhawe03
 *
 */

public class ErrorAlignedNumberField extends IntegerField {
	private MaxNumberValidator<Integer> maxValidator=new MaxNumberValidator<Integer>(Integer.MAX_VALUE);
	private MinNumberValidator<Integer> minValidator=new MinNumberValidator<Integer>(0);
	
	public ErrorAlignedNumberField() {
		addValidator(maxValidator);
		addValidator(minValidator);
	}
	public void setMaxValue(Integer num) {
		maxValidator.setMaxNumber(num);
	}
	
	public void setMinValue(Integer num){
		minValidator.setMinNumber(num);
	}
	
	public Integer getMaxValue() {
		return (Integer)maxValidator.getMaxNumber();
	}

	public Integer getMinValue() {
		return (Integer)minValidator.getMinNumber();
	}

//	@Override
//	protected void alignErrorIcon() {
//		final Element	alignElement = (Element) this.getElement().getParentElement().getNextSiblingElement();
//		
//		 DeferredCommand.addCommand(new Command() {
//		      public void execute() {
//		        errorIcon.el().alignTo(alignElement,
//		        		"tl-tr", new int[] {0, 3});
//		      }
//		 });
//	}
}
