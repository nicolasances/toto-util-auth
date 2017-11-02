package com.imatz.toto.util.auth;

/**
 * Result of the Toto Authorization Check
 * 
 * @author nicolas
 *
 */
public class TotoAuthorizationCheckResult {

	private Boolean authorized_;

	public boolean failed() {
		return !authorized_;
	}

	/**
	 * Utility factory method for non authorized
	 * 
	 * @return
	 */
	public static TotoAuthorizationCheckResult notAuthorized() {

		TotoAuthorizationCheckResult result = new TotoAuthorizationCheckResult();
		result.authorized_ = false;

		return result;
	}

	/**
	 * Utility factory method for  authorized
	 * 
	 * @return
	 */
	public static TotoAuthorizationCheckResult authorized() {

		TotoAuthorizationCheckResult result = new TotoAuthorizationCheckResult();
		result.authorized_ = true;

		return result;
	}

}
