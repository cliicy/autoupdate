package com.ca.arcserve.edge.app.base.util;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SelfSignTrustManager  implements X509TrustManager{
	private X509TrustManager defaultTrustManager = null;

	public SelfSignTrustManager(KeyStore keystore)
			throws NoSuchAlgorithmException, KeyStoreException {
		super();
		TrustManagerFactory factory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		factory.init(keystore);
		TrustManager[] trustmanagers = factory.getTrustManagers();
		if (trustmanagers.length == 0) {
			throw new NoSuchAlgorithmException("no trust manager found");
		}
		this.defaultTrustManager = (X509TrustManager) trustmanagers[0];
	}
	/**
	 * we accept any certificate
	 */
	public void checkClientTrusted(X509Certificate[] certificates,
			String authType) throws CertificateException {

	}
	/**
	 * we accept any certificate
	 */
	public void checkServerTrusted(X509Certificate[] certificates,
			String authType) throws CertificateException {

	}

	public X509Certificate[] getAcceptedIssuers() {
		return this.defaultTrustManager.getAcceptedIssuers();
	}
}
