//-------------------------------------------------------------------------/
// This program is an unpublished work fully protected by the United States
// Copyright laws and is considered a trade secret belonging to CA, Inc.
// Copyright 2005    CA, Inc.
//-------------------------------------------------------------------------/

//----------Revise history-------------------------------------------------//
//	Date[mm/dd/yy]		[Who does this]		[What your do.]
//
//
//
//-------------------------------------------------------------------------//
package com.ca.arcflash.ha.webservice;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CAX509TrustManager implements X509TrustManager {

	private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

	/**
	 * @return X509Certificate[]
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 */
	public X509Certificate[] getAcceptedIssuers() {
		return _AcceptedIssuers;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws CertificateException
	 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],
	 *      java.lang.String)
	 */
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {

	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws CertificateException
	 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],
	 *      java.lang.String)
	 */
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {

	}
}
