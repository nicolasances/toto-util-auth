package com.imatz.toto.util.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * Wrapper class that extracts caller information from the request.
 * 
 * If the caller provides a GoogleIdToken header containing a valid Google Id
 * Token, then it is considered to be the Toto Webapp
 * 
 * @author nicolas
 *
 */
public class Caller {

	private boolean totoWebapp_ = false;
	private boolean localhostMS_ = false;
	private boolean externalApp_ = false;

	public Caller(HttpServletRequest req) {

		// 1. Check if Toto Web App
		String googleIdToken = req.getHeader("GoogleIdToken");

		if (googleIdToken != null && !googleIdToken.isEmpty()) {

			totoWebapp_ = true;

			return;
		}

		// 2. Check if internal web service
		boolean localhost = req.getRemoteHost().equals("127.0.0.1") || req.getRemoteHost().equals("localhost");

		if (localhost) {

			localhostMS_ = true;

			return;
		}

		// 3. At this point it's an external service
		externalApp_ = true;
	}

	/**
	 * Specifies if this caller is the toto webapp
	 * 
	 * @return
	 */
	public boolean isTotoWebapp() {
		return totoWebapp_;
	}

	/**
	 * Specifies if the caller is a localhost service or app
	 * 
	 * @return
	 */
	public boolean isLocallyHostedMicroservice() {
		return localhostMS_;
	}

	/**
	 * Specifies if the caller is an external app
	 * 
	 * @return
	 */
	public boolean isExternalApplication() {
		return externalApp_;
	}

}
