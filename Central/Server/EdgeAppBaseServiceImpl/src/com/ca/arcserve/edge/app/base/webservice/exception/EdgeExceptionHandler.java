package com.ca.arcserve.edge.app.base.webservice.exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class EdgeExceptionHandler extends ExceptionHandler {
	
	private static Logger logger = Logger.getLogger(EdgeExceptionHandler.class);
	
	public EdgeExceptionHandler() {
		this(null);
	}

	public EdgeExceptionHandler(ExceptionHandler nextHandler) {
		super(nextHandler);
	}

	@Override
	protected boolean handleTargetException(Throwable exception) throws EdgeServiceFault {
		if (exception instanceof EdgeServiceFault) {
			throw (EdgeServiceFault) exception;
		}
		
		if (exception instanceof DaoException) {
			throw convert(exception, EdgeServiceErrorCode.Common_Service_Dao_Execption);
		}
		
		if(exception instanceof UndeclaredThrowableException){
			Throwable eThrowable = ((UndeclaredThrowableException) exception).getUndeclaredThrowable();
			if(eThrowable instanceof InvocationTargetException){
				Throwable eThrowable1 = ((InvocationTargetException) eThrowable).getTargetException();
				if(eThrowable1 instanceof EdgeServiceFault){
					throw (EdgeServiceFault)eThrowable1;
				}
			}
		}
		
		throw convert(exception, EdgeServiceErrorCode.Common_Service_General);
	}

	private EdgeServiceFault convert(Throwable t, String edgeServiceErrorCode) {
		String message = "EdgeExceptionHandler - " + t.getClass().getSimpleName() + " occurred, error message = " + t.getMessage();
		logger.error(message, t);
		return EdgeServiceFault.getFault(edgeServiceErrorCode, message);
	}
	
}
