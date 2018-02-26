package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * !! Should be consolidate with the Version class later. !!
 * 
 * @author Bo.Pang
 *
 */
public class EdgeSimpleVersion implements Comparable<EdgeSimpleVersion>, Serializable
{
	private static final long serialVersionUID = -9007507357491079759L;
	
	private int majorVersion = 0;
	private int minorVersion = 0;
	private int buildNumber = 0;
	private int updateNumber = 0;
	private int updateBuildNumber = 0;
	
	public void copy( EdgeSimpleVersion another )
	{
		this.majorVersion = another.majorVersion;
		this.minorVersion = another.minorVersion;
		this.buildNumber = another.buildNumber;
		this.updateNumber = another.updateNumber;
		this.updateBuildNumber = another.updateBuildNumber;
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

	public int getBuildNumber()
	{
		return buildNumber;
	}

	public void setBuildNumber( int buildNumber )
	{
		this.buildNumber = buildNumber;
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
	
	private int compareInt( int i1, int i2 )
	{
		if (i1 < i2) return -1;
		else if (i1 > i2) return 1;
		return 0;
	}

	@Override
	public int compareTo( EdgeSimpleVersion theOther )
	{
		int result;
		
		result = compareInt( this.majorVersion, theOther.majorVersion );
		if (result != 0)
			return result;
		
		result = compareInt( this.minorVersion, theOther.minorVersion );
		if (result != 0)
			return result;
		
		result = compareInt( this.buildNumber, theOther.buildNumber );
		if (result != 0)
			return result;
		
		result = compareInt( this.updateNumber, theOther.updateNumber );
		if (result != 0)
			return result;
		
		result = compareInt( this.updateBuildNumber, theOther.updateBuildNumber );
		if (result != 0)
			return result;

		return 0;
	}
	
	@Override
	public boolean equals( Object object )
	{
		if (!(object instanceof EdgeSimpleVersion))
			return false;
		
		return (this.compareTo( (EdgeSimpleVersion)object ) == 0);
	}
	
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 17 + majorVersion;
		hash = hash * 31 + minorVersion;
		hash = hash * 13 + buildNumber;
		hash = hash * 29 + updateNumber;
		hash = hash * 43 + updateBuildNumber;
		return hash;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName() + " { " );
		sb.append( "majorVersion = " + majorVersion );
		sb.append( ", minorVersion = " + minorVersion );
		sb.append( ", buildNumber = " + buildNumber );
		sb.append( ", updateNumber = " + updateNumber );
		sb.append( ", updateBuildNumber = " + updateBuildNumber );
		sb.append( " }" );
		return sb.toString();
	}
	
	public String toVersionString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( majorVersion );
		sb.append( "." );
		sb.append( minorVersion );
		sb.append( "." );
		sb.append( buildNumber );
		
		if (updateNumber > 0)
		{
			sb.append( " Update " );
			sb.append( updateNumber );
			sb.append( " Build " );
			sb.append( updateBuildNumber );
		}
		return sb.toString();
	}
	
	public static class IllegalVersionFormatException extends Exception
	{
		private static final long serialVersionUID = -6897613421155834843L;

		public IllegalVersionFormatException( String message )
		{
			super( message );
		}
	}
	
	public static EdgeSimpleVersion parseVersionString( String string ) throws IllegalVersionFormatException
	{
		assert string != null : "string cannot be null";
		if (string == null)
			throw new IllegalArgumentException( "string is null" );
		
		int majorVersion, minorVersion, buildNumber, updateNumber, updateBuildNumber;
		
		string = string.trim();
		
		// 7.1.1234
		// 7.1.1234 Update 2 Build 3245
		Pattern pattern = Pattern.compile( "^(\\d+)\\.(\\d+)\\.(\\d+)(\\s+Update\\s+(\\d+)\\s+Build\\s+(\\d+))?$" );
		Matcher matcher = pattern.matcher( string );
		if (!matcher.matches())
			throw new IllegalVersionFormatException( string );
		majorVersion = parseInt( matcher.group( 1 ) );
		minorVersion = parseInt( matcher.group( 2 ) );
		buildNumber = parseInt( matcher.group( 3 ) );
		updateNumber = parseInt( matcher.group( 5 ) );
		updateBuildNumber = parseInt( matcher.group( 6 ) );
		
		EdgeSimpleVersion pkgVersion = new EdgeSimpleVersion();
		pkgVersion.setMajorVersion( majorVersion );
		pkgVersion.setMinorVersion( minorVersion );
		pkgVersion.setBuildNumber( buildNumber );
		pkgVersion.setUpdateNumber( updateNumber );
		pkgVersion.setUpdateBuildNumber( updateBuildNumber );
		
		return pkgVersion;
	}
	
	private static int parseInt( String string )
	{
		return (string == null) ? 0 : Integer.parseInt( string );
	}
	
	/**
	 * This is a test for the class.
	 * 
	 * @param args
	 */
	public static void main( String[] args )
	{
		try
		{
			String versionString;
			EdgeSimpleVersion version;
			
			versionString = "7.1.1234";
			System.out.println( "versionString: '" + versionString + "'" );
			version = EdgeSimpleVersion.parseVersionString( versionString );
			System.out.println( "ProductPackageVersion: " + version.toVersionString() );
			
			versionString = "7.1.1234   Update  2     Build 3245";
			System.out.println( "versionString: '" + versionString + "'" );
			version = EdgeSimpleVersion.parseVersionString( versionString );
			System.out.println( "ProductPackageVersion: " + version.toVersionString() );
		}
		catch (Exception e)
		{
			System.out.println( e );
			e.printStackTrace();
		}
	}
}
