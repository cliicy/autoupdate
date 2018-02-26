package com.ca.arcflash.webservice.edge.d2dreg;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.jni.NativeFacadeImpl;

public class D2DEdgeRegistration extends BaseEdgeRegistration {
	private static final Logger logger = Logger.getLogger(D2DEdgeRegistration.class);
	protected void removeConfig() {
		PlanUtil.cleanPlan();
	}
	

	@Override
	protected String encrypt(String toEnc) {		
		return new NativeFacadeImpl().encrypt(toEnc);
	}

	@Override
	protected String decrypt(String toDec) {		
		return new NativeFacadeImpl().decrypt(toDec);
	}

}
