package com.ca.arcserve.edge.app.msp.webservice;

import javax.jws.WebService;

import com.ca.arcserve.edge.webservice.msp.IMsp4RpsService;
import com.ca.arcserve.edge.webservice.msp.IMspValidate;

@WebService(targetNamespace="http://webservice.edge.arcserve.ca.com/")
public interface IEdgeMsp4ClientService extends IMspValidate, IMspPlan4ClientService, IMsp4RpsService {
	
}
