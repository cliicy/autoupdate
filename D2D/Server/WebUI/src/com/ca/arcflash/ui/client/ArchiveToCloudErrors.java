package com.ca.arcflash.ui.client;

import java.util.HashMap;
public class ArchiveToCloudErrors {
	
	private static final String ERROR_PREFIX = "Error_";
	private static final String AZURE_ERROR_PREFIX = "AzError_";
	private  static final long BUCKET_LENGTH_ERROR = 10000;//if length is null or if length is not in between 3 and 63
	private static final long BUCKET_ERROR = 10001;//if contains special chars or capital letters
	private static final long BUCKET_SEQUENCE_ERROR = 10002;//if contains . or - adjacent
	private static final long BUCKET_IPFORMAT_ERROR = 10003;//if it is in IP address format
	private static final long BUCKET_EXCEPTION = 10005;//in case of exception
	

	static HashMap<String,String> errorMap = new HashMap<String, String>();
	static {
		errorMap.put("Error_-555", UIContext.Constants.cloudGlErrCppException());
		errorMap.put("Error_16", UIContext.Constants
				.cloudGlErrPluginLibraryNotPresent());
		errorMap.put("Error_404", UIContext.Constants.cloudGlErrProxyError());
		errorMap.put("Error_406", UIContext.Constants.cloudGlErrHttpError());
		errorMap.put("Error_408", UIContext.Constants.cloudGlErrServiceUnavailable());
		errorMap.put("Error_15", UIContext.Constants.cloudGlErrProviderError());
		errorMap.put("Error_407", UIContext.Constants.cloudGlErrAccessDenied());
		errorMap.put("Error_410", UIContext.Constants.cloudGlErrInternalError());
		errorMap.put("Error_415", UIContext.Constants.cloudGlErrInvalidBucketName());
		errorMap.put("Error_403", UIContext.Constants.cloudGlErrInvalidAccessKeyId());
		errorMap.put("Error_420",UIContext.Constants.cloudSkewError());
		errorMap.put("Error_421",UIContext.Constants.cloudTooManyBuckets());
		errorMap.put("Error_4294967295",UIContext.Constants.cloudInitializationFailed());		
		errorMap.put("Error_1", UIContext.Constants.cloudGlErrInvalidSize());
		errorMap.put("Error_2", UIContext.Constants.cloudGlErrLibInitFailed());
		errorMap.put("Error_3", UIContext.Constants.cloudGlErrInvalidHandle());
		errorMap.put("Error_11", UIContext.Constants.cloudGlErrAddProviderFailed());
		errorMap.put("Error_12", UIContext.Constants.cloudGlErrInvalidProviderToken());
		errorMap.put("Error_13", UIContext.Constants.cloudGlErrSessionTokenInvalid());
		errorMap.put("Error_31",UIContext.Constants.cloudProviderInUse());
		errorMap.put("Error_32",UIContext.Constants.cloudConfigFileCorrupted());
		errorMap.put("Error_400",UIContext.Constants.cloudNoProviderAvailable());
		errorMap.put("Error_401",UIContext.Constants.cloudGlErrNoMoreProviderAvailable());
		errorMap.put("Error_402", UIContext.Constants.cloudGlErrProviderNotSupported());
		errorMap.put("Error_409",UIContext.Constants.cloudSoapError());
		errorMap.put("Error_412",UIContext.Constants.cloudGlErrHttpConnectionFailed());
		errorMap.put("Error_414", UIContext.Constants.cloudGlErrInvalidProviderType());
		errorMap.put("Error_417", UIContext.Constants.cloudGlErrInvalidDestinationPath());
		errorMap.put("Error_419",UIContext.Constants.cloudBucketExisted());
		errorMap.put("Error_430",UIContext.Constants.cloudGlErrInvalidArguments());
		errorMap.put("Error_-777",UIContext.Constants.cloudNotRegisteredWithCom());
		errorMap.put("Error_500",UIContext.Constants.cloudBucketExistedInSameAccount());
		errorMap.put("default",UIContext.Constants.cloudBucketDefaultMessage());
		
		
		errorMap.put("AzError_16", UIContext.Constants.cloudGlErrPluginLibraryNotPresent());
		errorMap.put("AzError_404", UIContext.Constants.cloudGlErrProxyError());
		errorMap.put("AzError_406", UIContext.Constants.cloudGlErrHttpError());
		errorMap.put("AzError_408", UIContext.Constants.cloudGlErrServiceUnavailable());
		errorMap.put("AzError_15", UIContext.Constants.cloudGlErrProviderError());
		errorMap.put("AzError_407", UIContext.Constants.cloudGlErrAccessDenied());
		errorMap.put("AzError_410", UIContext.Constants.cloudGlErrInternalError());
		errorMap.put("AzError_420",UIContext.Constants.cloudSkewError());
		errorMap.put("AzError_421",UIContext.Constants.cloudTooManyBuckets());
		errorMap.put("AzError_4294967295",UIContext.Constants.cloudInitializationFailed());		
		errorMap.put("AzError_1", UIContext.Constants.cloudGlErrInvalidSize());
		errorMap.put("AzError_2", UIContext.Constants.cloudGlErrLibInitFailed());
		errorMap.put("AzError_3", UIContext.Constants.cloudGlErrInvalidHandle());
		errorMap.put("AzError_11", UIContext.Constants.cloudGlErrAddProviderFailed());
		errorMap.put("AzError_12", UIContext.Constants.cloudGlErrInvalidProviderToken());
		errorMap.put("AzError_13", UIContext.Constants.cloudGlErrSessionTokenInvalid());
		errorMap.put("AzError_31",UIContext.Constants.cloudProviderInUse());
		errorMap.put("AzError_32",UIContext.Constants.cloudConfigFileCorrupted());
		errorMap.put("AzError_400",UIContext.Constants.cloudNoProviderAvailable());
		errorMap.put("AzError_401",UIContext.Constants.cloudGlErrNoMoreProviderAvailable());
		errorMap.put("AzError_402", UIContext.Constants.cloudGlErrProviderNotSupported());
		errorMap.put("AzError_409",UIContext.Constants.cloudSoapError());
		errorMap.put("AzError_412",UIContext.Constants.cloudGlErrHttpConnectionFailed());
		errorMap.put("AzError_414", UIContext.Constants.cloudGlErrInvalidProviderType());
		errorMap.put("AzError_417", UIContext.Constants.cloudGlErrInvalidDestinationPath());
		errorMap.put("AzError_419",UIContext.Constants.cloudContainerExisted());
		errorMap.put("AzError_-777",UIContext.Constants.cloudNotRegisteredWithCom());
		errorMap.put("AzError_415", UIContext.Constants.cloudGlErrInvalidContainerName());
		errorMap.put("AzError_403", UIContext.Constants.cloudGlErrInvalidAccessKeyId());
		errorMap.put("AzError_430",UIContext.Constants.cloudGlErrInvalidArguments());
		errorMap.put("AzError_500",UIContext.Constants.cloudContainerExistedInSameAccount());
		
		errorMap.put(ERROR_PREFIX+BUCKET_LENGTH_ERROR, UIContext.Constants.cloudBucketNameInvalidLength());
		errorMap.put(ERROR_PREFIX+BUCKET_ERROR, UIContext.Constants.cloudBucketNameInvalidSpecialChar());
		errorMap.put(ERROR_PREFIX+BUCKET_SEQUENCE_ERROR, UIContext.Constants.cloudBucketNameInvalidAdjPeriods());
		errorMap.put(ERROR_PREFIX+BUCKET_IPFORMAT_ERROR, UIContext.Constants.cloudBucketNameInvalidIPFormat());
		
		errorMap.put(AZURE_ERROR_PREFIX+BUCKET_LENGTH_ERROR, UIContext.Constants.azureBucketNameInvalidLength());
		errorMap.put(AZURE_ERROR_PREFIX+BUCKET_ERROR, UIContext.Constants.azureBucketNameInvalidSpecialChar());
		errorMap.put(AZURE_ERROR_PREFIX+BUCKET_SEQUENCE_ERROR, UIContext.Constants.azureBucketNameInvalidAdjPeriods());
		errorMap.put(AZURE_ERROR_PREFIX+BUCKET_IPFORMAT_ERROR, UIContext.Constants.azureBucketNameInvalidIPFormat());
		
	}
	
	static public String getMessage(String errorCode) {
		String errMsg = "";
		
		
		if(errorCode.startsWith(ERROR_PREFIX)) 
			errMsg = errorMap.get(errorCode);
		else if(errorCode.startsWith(AZURE_ERROR_PREFIX)) 
			errMsg =  errorMap.get(errorCode);
		else if(!errorCode.startsWith(ERROR_PREFIX))
			errMsg = errorMap.get(ERROR_PREFIX+errorCode);
		
		if(errMsg == null || errMsg.length()==0 )
			errMsg = errorMap.get("default")+" "+errorCode;
		
		return errMsg;
	}
	
}
