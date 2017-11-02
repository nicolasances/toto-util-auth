package com.imatz.toto.util.auth.impl;

import javax.servlet.http.HttpServletRequest;

import com.imatz.toto.util.auth.Caller;
import com.imatz.toto.util.auth.TotoAuthorizationCheckResult;

/**
 * Checks the authorization of a locally hosted service or app calling the Toto
 * microservices (API).
 * 
 * Spring bean defined in the XML Spring configuration file.
 * 
 * @author nicolas
 *
 */
public class LocallyHostedMSAuthorizationCheck {

	public TotoAuthorizationCheckResult checkAuthorization(Caller caller, HttpServletRequest req) {
		
		return TotoAuthorizationCheckResult.authorized();
	}

}
