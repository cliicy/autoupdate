package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * Use this widget when the TextField is going to display a password,
 * a fake password will be set to the DOM, so that the browser's debugging tool cannot read it
 * (Refer to the macro FAKEPASSWORD in ARCserve Backup Manager) 
 * 
 * The usage is the same as TextField<String>
 */
public class PasswordTextField extends TextField<String>
{
	private final String fakePassword = "\u007f\u007f\u007f\u007f\u007f\u007f\u007f\u007f";
	private String realPassword;
	
	@Override
	public void setValue(String value)
	{
		if ( value == null || value.isEmpty())
		{
			super.setValue(value);
			realPassword = value;
		}
		else
		{
			// if it is not fake password again, set the real password
			if (!fakePassword.equals(value))
			{
				realPassword = value;
			}			
			
			super.setValue(fakePassword);
		}
		
	}

	@Override
	public String getValue()
	{
		String password = null;
		
		// not changed
		if (isFakePassword()) 
		{
			password = realPassword;
		}
		// changed
		else
		{
			password = super.getValue(); 
		}
		
		return password;
	}
	
	@Override
	protected boolean validateValue(String value)
	{
		if (isFakePassword())
		{
			return true;
		}
		else
		{
			return super.validateValue(value);
		}
	}
	
	private boolean isFakePassword()
	{
		boolean result = false;
		
		String temp = super.getValue();
		
		if (temp != null && fakePassword.equals(temp)) 
		{
			result = true;
		}
		
		return result;
	}
	
	// validate against the default length
	public boolean isMaxLengthExceeded()
	{
		return isMaxLengthExceeded(Utils.EncryptionPwdLen);
	}
	
	// validate with specified length
	public boolean isMaxLengthExceeded(int maxLength)
	{
		boolean result = false;
		
		if (!isFakePassword())
		{
			String temp = super.getValue();
			
			if(temp != null && temp.length() > maxLength)
			{
				result = true;
			}
		}
		
		return result;
	}
}
