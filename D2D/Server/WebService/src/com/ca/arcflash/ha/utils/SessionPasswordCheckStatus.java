/**
 * Created on Jan 23, 2013 4:03:04 PM
 */
package com.ca.arcflash.ha.utils;

/**
 * @author lijwe02
 * 
 */
public enum SessionPasswordCheckStatus {
	PLAIN, // The session is not encrypted
	VALID, // Find the valid password and updated
	INVALID, // The session is encrypted but no valid password
	UPDATED // The password already updated
}
