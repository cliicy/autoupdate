package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator;

import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;

public interface ITaskValidator
{
	public static class ValidationError
	{
		private String errorCode;
		private Object[] errorParams;
		private String errorMessageForLog;
		
		public ValidationError( String errorCode, Object[] errorParams, String errorMessageForLog )
		{
			this.errorCode = errorCode;
			this.errorParams = errorParams;
			this.errorMessageForLog = errorMessageForLog;
		}

		public String getErrorCode()
		{
			return errorCode;
		}

		public Object[] getErrorParams()
		{
			return errorParams;
		}

		public String getErrorMessageForLog()
		{
			return errorMessageForLog;
		}
	}
	
	/**
	 * Validate configurations in the specified policy of a task.
	 * 
	 * @param policy
	 * @param taskIndex
	 * @return null if pass the validation, otherwise returns the error information.
	 */
	ValidationError validate( UnifiedPolicy policy, TaskType taskType, int taskIndex,
		ValidationSession validationSession );
}
