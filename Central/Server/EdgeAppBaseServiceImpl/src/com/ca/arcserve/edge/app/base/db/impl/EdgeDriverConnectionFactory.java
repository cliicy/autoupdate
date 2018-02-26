package com.ca.arcserve.edge.app.base.db.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.db.IConfiguration;

public class EdgeDriverConnectionFactory  implements ConnectionFactory {
		protected boolean windowsAuth = false;
		protected String windowUser = null;
		@NotPrintAttribute
		protected String windowPass = null;

	   public boolean isWindowsAuth() {
			return windowsAuth;
		}

		public void setWindowsAuth(boolean windowsAuth) {
			this.windowsAuth = windowsAuth;
		}

		public String getWindowUser() {
			return windowUser;
		}

		public void setWindowUser(String windowUser) {
			this.windowUser = windowUser;
		}

		public String getWindowPass() {
			return windowPass;
		}

		public void setWindowPass(String windowPass) {
			this.windowPass = windowPass;
		}

		public Driver get_driver() {
			return _driver;
		}

		public void set_driver(Driver driver) {
			_driver = driver;
		}

		public String get_connectUri() {
			return _connectUri;
		}

		public void set_connectUri(String connectUri) {
			_connectUri = connectUri;
		}

		public Properties get_props() {
			return _props;
		}

		public void set_props(Properties props) {
			_props = props;
		}

	public EdgeDriverConnectionFactory(Driver driver, String connectUri, Properties props) {
	        _driver = driver;
	        _connectUri = connectUri;
	        _props = props;
	        if(-1!= _connectUri.indexOf(IConfiguration.WINDOWS_AUTHENTICATE))
				windowsAuth = true;
	        if(windowsAuth){
	        	windowUser = _props.get("user")==null? null: _props.get("user").toString();
	        	_props.remove("user");
	        	windowPass = _props.get("password")==null?null:_props.get("password").toString();
	        	_props.remove("password");
	        }
	    }

	    public Connection createConnection() throws SQLException {
			if(windowsAuth){
				 ConnectionResult connection = ConnectionManagerUtil.getConnection(this);
				 if(connection.getE()!=null){
					 if(connection.getE() instanceof SQLException)
						 throw (SQLException)connection.getE();
					 else
						 throw new SQLException(connection.getE().getMessage(),connection.getE());
				 }else
					 return connection.getCon();

			}else  return _driver.connect(_connectUri,_props);
	    }

	    protected Driver _driver = null;
	    protected String _connectUri = null;
	    protected Properties _props = null;

	    public String toString() {
	        return this.getClass().getName() + " [" + String.valueOf(_driver) + ";" + String.valueOf(_connectUri) + ";"  + String.valueOf(_props) + "]";
	    }


}
