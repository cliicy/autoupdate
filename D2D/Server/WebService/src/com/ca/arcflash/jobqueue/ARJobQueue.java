package com.ca.arcflash.jobqueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcflash.assurerecovery.AssureRecoveryJobScript;
import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.instantvm.InstantVMNode;

public class ARJobQueue {
	private Map<String, AssureRecoveryJobScript> jobsMap = new HashMap<String, AssureRecoveryJobScript>();
	private String location;
	private static Logger log = Logger.getLogger(ARJobQueue.class);
	private static final String JOB_FILENAME_EXTENSION = "-arjob.xml";
	private static JAXBContext jobScriptContext = newJobScriptJAXBContext();
	private static JAXBContext ivmConfigContext = newIvmConfigJAXBContext();

	private static JAXBContext newJobScriptJAXBContext() {
		try {
			return JAXBContext.newInstance(AssureRecoveryJobScript.class);
		} catch (JAXBException e) {
			log.error("Fail to create JAXBContext for AssureRecoveryJobScript");
		}
		return null;
	}

	private static JAXBContext newIvmConfigJAXBContext() {
		try {
			return JAXBContext.newInstance(InstantVMConfig.class);
		} catch (JAXBException e) {
			log.error("Fail to create JAXBContext for AssureRecoveryJobScript");
		}
		return null;
	}

	public ARJobQueue(String location) {
		this.location = location;
		load();
	}

	private void load() {
		jobsMap.clear();
		try {
			File queueLocation = new File(this.location);
			if (!queueLocation.exists()) {
				queueLocation.mkdirs();
			}
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(JOB_FILENAME_EXTENSION);
				}
			};
			for (File file : queueLocation.listFiles(filter)) {
				AssureRecoveryJobScript jobScript = (AssureRecoveryJobScript) (jobScriptContext
						.createUnmarshaller().unmarshal(file));
				String id = jobScript.getId();
				jobsMap.put(id, jobScript);
			}
		} catch (Exception e) {
			log.error("Fail to load AR jobs.", e);
		}
	}

	private void store(AssureRecoveryJobScript jobScript) {
		FileOutputStream fos = null;
		try {
			File queueLocation = new File(location);
			if (!queueLocation.exists()) {
				queueLocation.mkdirs();
			}
			String name = jobScript.getId() + JOB_FILENAME_EXTENSION;
			fos = new FileOutputStream(new File(queueLocation, name));
			JAXB.marshal(jobScript, fos);
			fos.close();
		} catch (Exception e) {
			log.error("Fail to store AR job script.", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	private void delete(AssureRecoveryJobScript jobScript) {
		try {
			File queueLocation = new File(location);
			if (!queueLocation.exists()) {
				queueLocation.mkdirs();
			}
			String name = jobScript.getId() + JOB_FILENAME_EXTENSION;
			File file = new File(queueLocation, name);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			log.error("Fail to delete AR job script.", e);
		}
	}

	public synchronized void add(AssureRecoveryJobScript jobScript) {
		jobsMap.put(jobScript.getId(), jobScript);
		store(jobScript);
	}

	public synchronized void remove(AssureRecoveryJobScript jobScript) {
		jobsMap.remove(jobScript.getId());
		delete(jobScript);
	}

	public synchronized InstantVMConfig getInstantVMConfig(String afID) {
		for (AssureRecoveryJobScript jobScript : jobsMap.values()) {
			InstantVMConfig ivmConfig = jobScript.getInstantVMconfig();
			for (InstantVMNode node : ivmConfig.getInstantVMNodes()) {
				if (node.getNodeUUID().equalsIgnoreCase(afID))
					try {
						return cloneInstantVMConfig(ivmConfig);
					} catch (JAXBException e) {
						log.error("Fail to clone InstantVMConfig", e);
						return null;
					}
			}
		}
		return null;
	}

	private InstantVMConfig cloneInstantVMConfig(InstantVMConfig ivmConfig)
			throws JAXBException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		JAXB.marshal(ivmConfig, buffer);
		ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
		return (InstantVMConfig) ivmConfigContext.createUnmarshaller().unmarshal(input);
	}
}
