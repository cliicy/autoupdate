package com.ca.arcserve.edge.app.base.webservice.gateway.clitools;

public class GatewayToolNative
{
	public static class LongValue
	{
		private long value;

		public long getValue()
		{
			return value;
		}

		public void setValue( long value )
		{
			this.value = value;
		}
	}
	
	public static class StringValue
	{
		private String value;

		public String getValue()
		{
			return value;
		}

		public void setValue( String value )
		{
			this.value = value;
		}
	}
	
	public static final int HKEY_LOCAL_MACHINE = 0x80000002;
	
	public static native int regOpenKey( long keyHandle, String subKey, LongValue resultHandle );
	public static native int regCloseKey( long keyHandle );
	public static native int regQueryLongValue( long keyHandle, String valueName, LongValue value );
	public static native int regQueryStringValue( long keyHandle, String valueName, StringValue value );
	public static native int regSetLongValue( long keyHandle, String valueName, long value );
	public static native int regSetStringValue( long keyHandle, String valueName, String value );
	public static native int regSetBinaryValue( long keyHandle, String valueName, byte[] value );
}
