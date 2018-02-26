package com.ca.arcserve.edge.app.base.db;

//import java.net.URL;
//import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.util.EdgeImpersonation;

public final class GDBCConnection {
	//private int sqlType;
//	private String url;
//	private String user;
//	private String passwd;
	private	Connection con = null;
	private	Exception exce = null;
    private static Exception testExec = null;
    private static AtomicBoolean asNativeLoaded =new  AtomicBoolean(false);
	private GDBCConnection() {}
	public  boolean  Execute(String cmd) throws Exception {
		if(con == null)
			throw new Exception();
		PreparedStatement st = null;
		try{
			st = con.prepareStatement(cmd);
			if(st.execute() && st.getResultSet().next())
				return true;
		}finally{
			if(st!=null){
				try{
					st.close();
				}catch(Exception e){}
			}
		}
		return false;
	}

	public Connection getRawConnection() {
		return con;
	}
	
	public  void close() {
		try {
			if (con != null)
				con.close();

		} catch (Exception e) {
		}
		con = null;
	}
	public synchronized static  boolean testToConnectWithImpersonate(final String url, final String user, @NotPrintAttribute final String password) throws Exception{
		testExec = null;
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {

				try {
					if (-1 != url.indexOf(IConfiguration.WINDOWS_AUTHENTICATE)) {
						if (!asNativeLoaded.get())
							try {
								System.loadLibrary("ASNative");
								asNativeLoaded.set(true);
							} catch (Throwable t) {

							}

						int i = EdgeImpersonation.getInstance().impersonate();
						if (i != 0) {
							System.err
									.println("GDBCConnection.getGDBCConnection failed to impersonate Edge User:"
											+ EdgeImpersonation.getInstance().getLastUsername());
							throw new Exception(
									"GDBCConnection.getGDBCConnection failed to impersonate Edge User:"
											+ EdgeImpersonation.getInstance().getLastUsername());
						}
						java.sql.DriverManager.getConnection(url);
					}else
					java.sql.DriverManager.getConnection(url,user,password);

				} catch (Exception e) {

					testExec = e;
				}

			}});
		t.start();
		try {
			t.join();
			if(null!=testExec) throw testExec;
			return true;
		} catch (InterruptedException e) {
			testExec = null;
			return false;
		}
	}
	public static GDBCConnection getGDBCConnection(final String url, final  String user, final  String passwd) throws Exception {
		final GDBCConnection  INSTANCE = new GDBCConnection();
		if(user == null || "".equals(user)) {
			Thread t = new Thread(new Runnable(){

				@Override
				public void run() {
					try {
						if(!asNativeLoaded.get())
						try{
						System.loadLibrary("ASNative");
						asNativeLoaded.set(true);
						}catch(Throwable t){

						}
						
						int i = EdgeImpersonation.getInstance().impersonate();
						if(i!=0){
							System.err.println("GDBCConnection.getGDBCConnection failed to impersonate Edge User:"+EdgeImpersonation.getInstance().getLastUsername());
							throw new ImpersonateException("GDBCConnection.getGDBCConnection failed to impersonate Edge User:"+EdgeImpersonation.getInstance().getLastUsername());
						}
						INSTANCE.con = java.sql.DriverManager.getConnection("jdbc:sqlserver://" + url + ";integratedSecurity=true;", user, passwd);
						INSTANCE.exce = null;
					} catch (Exception e) {
						INSTANCE.con = null;
						INSTANCE.exce = e;
					}

				}});
			t.start();
			t.join();

		} else {
			INSTANCE.con = java.sql.DriverManager.getConnection("jdbc:sqlserver://" + url + ";integratedSecurity=false;", user, passwd);
			INSTANCE.exce = null;
		}
		if(INSTANCE.con == null)
		{
			if(INSTANCE.exce!=null) throw INSTANCE.exce;
			else
				throw new Exception("Failed to get Connection:"+url);
		}


		return INSTANCE;
	}
	
	
	
	public static GDBCConnection getGDBCConnection_JAVADB(final String url, final  String user, final  String passwd) throws Exception {
		final GDBCConnection  INSTANCE = new GDBCConnection();
		{
			INSTANCE.con = java.sql.DriverManager.getConnection( url, user, passwd);
			INSTANCE.exce = null;
		}
		if(INSTANCE.con == null)
		{
			if(INSTANCE.exce!=null) throw INSTANCE.exce;
			else
				throw new Exception("Failed to get Connection:"+url);
		}


		return INSTANCE;
	}
}
