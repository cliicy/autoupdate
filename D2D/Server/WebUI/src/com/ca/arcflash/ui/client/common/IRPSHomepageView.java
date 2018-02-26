package com.ca.arcflash.ui.client.common;

public interface IRPSHomepageView
{
	public class OperationResults
	{
		public static final int Succeeded	= 1;
		public static final int Failed		= 2;
	}	
	
	void onAsyncOperationCompleted(int result, String msg);
}
