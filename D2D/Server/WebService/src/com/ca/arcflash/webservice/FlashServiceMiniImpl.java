package com.ca.arcflash.webservice;

import javax.jws.WebService;

import com.ca.arcflash.webservice.mini.IFlashServiceMini;

@WebService(endpointInterface="com.ca.arcflash.webservice.mini.IFlashServiceMini")
public class FlashServiceMiniImpl extends FlashServiceImpl implements IFlashServiceMini{

}
