package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

/**
 * This class is designed to be common class for all ARCserve products, include
 * Central, D2D, RHA and ASBU.
 * 
 * @author panbo01
 * @time 2014-06-25
 */
public class Version implements Serializable, Comparable<Version>
{
	private static final long serialVersionUID = 1L;

	private int majorVersion;
	private int minorVersion;
	private int privateNumber;
	private String buildNumber;
	private int updateNumber;
	private int updateBuildNumber;
	
	public Version()
	{
		this( 0, 0 );
	}
	
	public Version( int majorVersion, int minorVersion )
	{
		this( majorVersion, minorVersion, 0, "0" );
	}
	
	public Version( int majorVersion, int minorVersion, int privateNumber, String buildNumber )
	{
		this( majorVersion, minorVersion, privateNumber, buildNumber, 0, 0 );
	}
	
	public Version( int majorVersion, int minorVersion, int privateNumber, String buildNumber,
		int updateNumber, int updateBuildNumber )
	{
		this.majorVersion		= majorVersion;
		this.minorVersion		= minorVersion;
		this.privateNumber		= privateNumber;
		this.buildNumber		= buildNumber;
		this.updateNumber		= updateNumber;
		this.updateBuildNumber	= updateBuildNumber;
	}

	public int getMajorVersion()
	{
		return majorVersion;
	}

	public void setMajorVersion( int majorVersion )
	{
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion()
	{
		return minorVersion;
	}

	public void setMinorVersion( int minorVersion )
	{
		this.minorVersion = minorVersion;
	}

	public int getPrivateNumber()
	{
		return privateNumber;
	}

	public void setPrivateNumber( int privateNumber )
	{
		this.privateNumber = privateNumber;
	}

	public static Version fromString( String versionString )
	{
		Version version = new Version();
		
		if (versionString == null)
			return version;
		
		versionString = versionString.trim();
		
		if (versionString.length() == 0)
			return version;
		
		String[] parts = versionString.split( "\\." );
		
		if ((parts.length >= 1) && (parts[0] != null))
			version.setMajorVersion( Integer.parseInt( parts[0] ) );
		
		if ((parts.length >= 2) && (parts[1] != null))
			version.setMinorVersion( Integer.parseInt( parts[1] ) );
		
		if ((parts.length >= 3) && (parts[2] != null))
			version.setPrivateNumber( Integer.parseInt( parts[2] ) );
		
		if ((parts.length >= 4) && (parts[3] != null))
			version.setBuildNumber(parts[3]);
		
		if ((parts.length >= 5) && (parts[4] != null))
			version.setBuildNumber(version.getBuildNumber() + "." + parts[4]);
		
		return version;
	}
	
	public int getUpdateNumber()
	{
		return updateNumber;
	}

	public void setUpdateNumber( int updateNumber )
	{
		this.updateNumber = updateNumber;
	}

	public int getUpdateBuildNumber()
	{
		return updateBuildNumber;
	}
	
	public void setUpdateBuildNumber( int updateBuildNumber )
	{
		this.updateBuildNumber = updateBuildNumber;
	}

	public void setUpdateInfo( int updateNumber, int updateBuildNumber )
	{
		this.updateNumber = updateNumber;
		this.updateBuildNumber = updateBuildNumber;
	}

	@Override
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append( this.majorVersion );
		strBuilder.append( "." );
		strBuilder.append( this.minorVersion );
		strBuilder.append( "." );
		strBuilder.append( this.privateNumber );
		strBuilder.append( "." );
		strBuilder.append( this.buildNumber );
		
		if (this.updateNumber > 0)
		{
			strBuilder.append( " Update " );
			strBuilder.append( this.updateNumber );
			strBuilder.append( "." );
			strBuilder.append( this.updateBuildNumber );
		}
		
		return strBuilder.toString();
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	@Override
	public int compareTo( Version version )
	{
		final int ARRAY_SIZE = 5;
		
		int[] numbers1 = new int[ARRAY_SIZE];
		numbers1[0] = this.majorVersion;
		numbers1[1] = this.minorVersion;
		numbers1[2] = parseBuildNumber( this.buildNumber );
		numbers1[3] = this.updateNumber;
		numbers1[4] = this.updateBuildNumber;
		
		int[] numbers2 = new int[ARRAY_SIZE];
		numbers2[0] = version.majorVersion;
		numbers2[1] = version.minorVersion;
		numbers2[2] = parseBuildNumber( version.buildNumber );
		numbers2[3] = version.updateNumber;
		numbers2[4] = version.updateBuildNumber;
		
		for (int i = 0; i < numbers1.length; i ++)
		{
			if (numbers1[i] < numbers2[i])
				return -1;
			
			if (numbers1[i] > numbers2[i])
				return 1;
		}
		
		return 0;
	}
	
	/**
	 * Some codes wrongly merges build number and update build number into
	 * build number like 1897.567, we have to change the data type of build
	 * number to string. Here, we want to get the real build number, so this
	 * function will parse the string and return the number before decimal
	 * point.
	 * 
	 * @param	buildNumber
	 * @return
	 */
	public static int parseBuildNumber( String buildNumber )
	{
		String[] parts = buildNumber.split( "\\." );
		
		if ((parts.length >= 1) && (parts[0] != null))
			return Integer.parseInt( parts[0] );
		
		return 0;
	}
}

