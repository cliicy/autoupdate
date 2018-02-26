/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

/**
 * @author Neo.Li
 *
 */
/**
 * #define _JMS_ACTIVE                     0
#define _JMS_FINISHED                   1
#define _JMS_CANCELLED                  2
#define _JMS_FAILED                     3
#define _JMS_INCOMPLETE                 4
#define _JMS_TARGET_ACTIVE              5 //for ASNW.
#define _JMS_TARGET_WAITING             6     //for ASNW
#define _JMS_TARGET_WAITING_COMPARE     7   //for ASNW
#define _JMS_TARGET_WAITING_VERIFY        8 //for ASNW
#define _JMS_CRASHED                    101
#define _JMS_RUN_FAILED                 102 
Zhang, Taiwen [2:54 PM]: 
#define _JMS_RUN_CANCELLING   500 
 */
public enum ASBUJobStatus {

	ACTIVE(0, "ACTIVE"), FINISHED(1, "FINISHED"), CANCELLED(2, "CANCELLED"), FAILED(3, "FAILED"),
	INCOMPLETE(4, "INCOMPLETE"), TARGET_ACTIVE(5, "TARGET_ACTIVE"), TARGET_WAITING(6, "TARGET_WAITING"),
	TARGET_WAITING_COMPARE(7, "TARGET_WAITING_COMPARE"), TARGET_WAITING_VERIFY(8, "TARGET_WAITING_VERIFY"),
	CRASHED(101, "CRASHED"), RUN_FAILED(102, "RUN_FAILED"), RUN_CANCELLING(500, "CANCELLING");

	private int value;
	private String name;

	ASBUJobStatus(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}

	public static ASBUJobStatus fromValue(String value) {
		if(value == null || value.equals(""))
			return null;
		for (ASBUJobStatus item : ASBUJobStatus.values()) {
			if (String.valueOf(item.value).equals(value))
				return item;
		}
		return null;
	}
}