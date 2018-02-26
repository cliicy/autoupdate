package  com.ca.arcserve.edge.app.base.webservice.appliance;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class ReadStream implements Runnable{
	private Logger logger = Logger.getLogger(this.getClass());
	private String name;
	private InputStream is;  
	
	public ReadStream(String name, InputStream is) {
		this.name = name;
		this.is = is;
	}
	
	public void run () {
		try {
			InputStreamReader isr = new InputStreamReader (is);
			BufferedReader br = new BufferedReader (isr);   
			while (true) {
				String s = br.readLine ();
				if (s == null) break;
				logger.info("[ReadStream]: [" + name + "] " + s);
			}
			is.close ();
		} catch (Exception ex) {
			logger.error("[ReadStream]: Read stream failed." + ex);
		}
	}
}
