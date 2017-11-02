package com.imatz.toto.util.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthFilter implements Filter {

	/**
	 * Injected through Spring
	 */
	private TotoAuthorizationCheck totoAuthorizationCheck_;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		if (!((HttpServletRequest) req).getMethod().equals("OPTIONS")) {
			
			TotoAuthorizationCheckResult authorizationCheckResult = totoAuthorizationCheck_.checkAuthorization((HttpServletRequest) req);
			
			if (authorizationCheckResult.failed()) {
				
				((HttpServletResponse) res).sendError(401);
				
				return;
			}
		}

		chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {}

	public void destroy() {}

	public TotoAuthorizationCheck getTotoAuthorizationCheck() {
		return totoAuthorizationCheck_;
	}

	public void setTotoAuthorizationCheck(TotoAuthorizationCheck totoAuthorizationCheck) {
		totoAuthorizationCheck_ = totoAuthorizationCheck;
	}

}
