package com.imatz.toto.util.auth;

import javax.servlet.http.HttpServletRequest;

import com.imatz.toto.util.auth.impl.ExternalAppAuthorizationCheck;
import com.imatz.toto.util.auth.impl.LocallyHostedMSAuthorizationCheck;
import com.imatz.toto.util.auth.impl.TotoWebappAuthorizationCheck;

/**
 * This authorization check checks the authorization of an external application
 * to call the Toto Microservices (API).
 * 
 * @author nicolas
 *
 */
public class TotoAuthorizationCheck {

	/**
	 * Contains the logic for the authorization of the Toto Webapp.
	 * 
	 * Injected through Spring XML file
	 */
	private TotoWebappAuthorizationCheck totoWebappAuthorizationCheck_;

	/**
	 * Contains the logic for the authorization of localhost microservices.
	 * 
	 * Injected through Spring XML file
	 */
	private LocallyHostedMSAuthorizationCheck locallyHostedMSAuthorizationCheck_;

	/**
	 * Contains the logic for the authorization of external apps.
	 * 
	 * Injected through Spring XML file
	 */
	private ExternalAppAuthorizationCheck externalAppAuthorizationCheck_;

	/**
	 * Checks the authorization of the caller from the received HTTP request.
	 * 
	 * @param req
	 *            the http request. Must be a {@link HttpServletRequest}
	 * 
	 * @return
	 */
	public TotoAuthorizationCheckResult checkAuthorization(HttpServletRequest req) {

		Caller caller = new Caller(req);

		if (caller.isTotoWebapp()) return totoWebappAuthorizationCheck_.checkAuthorization(caller, req);

		if (caller.isLocallyHostedMicroservice()) return locallyHostedMSAuthorizationCheck_.checkAuthorization(caller, req);

		if (caller.isExternalApplication()) return externalAppAuthorizationCheck_.checkAuthorization(caller, req);

		return TotoAuthorizationCheckResult.notAuthorized();
	}
	
	
	public ExternalAppAuthorizationCheck getExternalAppAuthorizationCheck() {
		return externalAppAuthorizationCheck_;
	}
	
	public void setExternalAppAuthorizationCheck(ExternalAppAuthorizationCheck externalAppAuthorizationCheck) {
		externalAppAuthorizationCheck_ = externalAppAuthorizationCheck;
	}
	
	public LocallyHostedMSAuthorizationCheck getLocallyHostedMSAuthorizationCheck() {
		return locallyHostedMSAuthorizationCheck_;
	}
	
	public void setLocallyHostedMSAuthorizationCheck(LocallyHostedMSAuthorizationCheck locallyHostedMSAuthorizationCheck) {
		locallyHostedMSAuthorizationCheck_ = locallyHostedMSAuthorizationCheck;
	}
	
	public TotoWebappAuthorizationCheck getTotoWebappAuthorizationCheck() {
		return totoWebappAuthorizationCheck_;
	}
	
	public void setTotoWebappAuthorizationCheck(TotoWebappAuthorizationCheck totoWebappAuthorizationCheck) {
		totoWebappAuthorizationCheck_ = totoWebappAuthorizationCheck;
	}
	

}
