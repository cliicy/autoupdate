package com.ca.arcflash.webservice.service;

import javax.xml.ws.soap.SOAPFaultException;

public class ServiceException extends Exception{
	private static final long serialVersionUID = 6333180308590436968L;
	private String errorCode;
	private Object[] arguments=new Object[0];
	
	private SOAPFaultException webServiceCause = null;
	
	/**
	 * Constructs a new exception with the specified errorCode  
	 * <p>Note that this constructor is <i>only</i> compatible with the old edition
	 */
	@Deprecated
	public ServiceException(String errorCode) {
		super();
		this.errorCode = errorCode;
	}
	/**
	 * Constructs a new exception with the specified message and errorCode  
	 * <p>Note that this constructor is <i>only</i> compatible with the old edition
	 */
	public ServiceException(String message, String errorCode){
		super(message);
		this.arguments=new String[]{message};
		this.errorCode = errorCode;
	}
    /**
     * Constructs a new exception with the specified errorCode and messages.
     * <p>Note that the messages is a <i>Object array</i>.
     * If you use this constructor,you should use {@link #getMultipleMessages()} method.
     * The messages are <i>not</i> automatically incorporated in this exception's detail message,
     * you will not get enough message by {@link #getMessage()} method
     * @param  errorCode the error code (which is saved for later retrieval by the
     *         {@link #getErrorCode()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the code is nonexistent or
     *         unknown.)
     * @param  message the messages (which are saved for later retrieval
     *         by the {@link #getMultipleMessages()} method).
     * @author liuyu07
     */
    public ServiceException(String errorCode,Object[] arguments){
    	super();
    	this.errorCode=errorCode;
		this.arguments=new Object[arguments.length];
		System.arraycopy(arguments, 0, this.arguments, 0, arguments.length);
    }
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage(){
		if(this.arguments.length>0){
			return (String)this.arguments[0];
		}else{
			return super.getMessage();
		}
	}
	public Object[] getMultipleArguments(){
		return arguments;
	}
	public SOAPFaultException getWebServiceCause() {
		return webServiceCause;
	}
	public void setWebServiceCause(SOAPFaultException webServiceCause) {
		this.webServiceCause = webServiceCause;
	}
}
