package com.ca.arcserve.edge.app.base.webservice.contract.common;

public class DeployCommonUtil {
      
      public static String getVersionString( String v ){
            return v!= null && !v.equals("")  ? v : "0" ;
      }
      
      //reference ImportRpsNodeJob -> isNeedRemoteDeploy
      public static Boolean isFreshOrOldVersionD2D(String d2dMajorVersion ,String d2dMinorVersion , String d2dUpdateVestionNumber, String d2dBuildNumber, String patchVersion){
            // For D2D 16.0 or 16.5 upgrade, defect 98157
            if(getVersionString(d2dMajorVersion).equals("16")&& (getVersionString( d2dMinorVersion ).equals("5")||getVersionString( d2dMinorVersion ).equals("0"))){
                  return true;
            }
            //For oolong
            String installedVersion = getVersionString(d2dMajorVersion)+"."+ getVersionString( d2dMinorVersion ) +"." +
            		getVersionString(d2dBuildNumber)+"."+getVersionString(d2dUpdateVestionNumber);
            if( installedVersion.compareTo(patchVersion) >=0 ) {  //installed version is same or newer than patch version
                  return false;
            }
            return true;
      }
}
