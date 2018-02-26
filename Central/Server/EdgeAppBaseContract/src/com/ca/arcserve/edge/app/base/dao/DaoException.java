package com.ca.arcserve.edge.app.base.dao;
/**
 * This Class is used to map SQLException and other Exception into a unchecked Exception.
 * @author gonro07
 *
 */
public class DaoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5909114414541906349L;


	public DaoException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DaoException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}


	

}
