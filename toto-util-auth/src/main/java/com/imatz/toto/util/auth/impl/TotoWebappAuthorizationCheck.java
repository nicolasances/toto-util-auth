package com.imatz.toto.util.auth.impl;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.imatz.toto.util.auth.Caller;
import com.imatz.toto.util.auth.TotoAuthorizationCheckResult;
import com.imatz.toto.util.db.mongo.MongoDB;
import com.imatz.toto.util.db.mongo.MongoServer;
import com.mongodb.client.FindIterable;

/**
 * Checks that the Toto Webapp logged in user is authorized to access this
 * microservice.
 * 
 * Spring bean defined in the XML Spring configuration file.
 * 
 * @author nicolas
 *
 */
public class TotoWebappAuthorizationCheck {

	private static String GOOGLE_CLIENT_ID = "209706877536-4h516ud369nuaakag4gbtlvq4d735ag9.apps.googleusercontent.com";

	private String mongoHost_;
	private int mongoPort_;

	public TotoAuthorizationCheckResult checkAuthorization(Caller caller, HttpServletRequest req) {

		try {

			// 1. Get Google Token
			String googleIdToken = req.getHeader("GoogleIdToken");

			JacksonFactory jacksonFactory = new JacksonFactory();
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jacksonFactory).setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();

			// 2. Verify Google Token
			GoogleIdToken idToken = verifier.verify(googleIdToken);

			if (idToken == null) return TotoAuthorizationCheckResult.notAuthorized();

			// 3. Retrieve API that the user is calling
			String app = getAppNameFromURL(req.getRequestURL());

			if (app == null) return TotoAuthorizationCheckResult.notAuthorized();

			// 4. Retrieve user info
			Payload payload = idToken.getPayload();

			String email = payload.getEmail();
			String name = (String) payload.get("name");

			// 5. Check on the database
			if (isAuthorized(email, name, app)) return TotoAuthorizationCheckResult.authorized();

			return TotoAuthorizationCheckResult.notAuthorized();

		}
		catch (Exception e) {
			e.printStackTrace();

			return TotoAuthorizationCheckResult.notAuthorized();
		}
	}

	/**
	 * Checks on the DB if the provided Google user is authorized to access to
	 * the specified app
	 * 
	 * @param email
	 *            the email of the Google user
	 * @param name
	 *            the name of the Google user
	 * @param app
	 *            the app name
	 * @return
	 */
	private boolean isAuthorized(String email, String name, String app) {

		MongoServer server = new MongoServer(mongoHost_, mongoPort_);
		MongoDB database = server.getDatabase("login");

		try {

			FindIterable<Document> docs = database.getCollection("authorizedUsers").find();

			for (Document doc : docs) {

				String authorizedName = doc.getString("name");
				String authorizedEmail = doc.getString("email");

				@SuppressWarnings("unchecked")
				List<String> apps = (List<String>) doc.get("apps");

				if (email.equalsIgnoreCase(authorizedEmail) && name.equalsIgnoreCase(authorizedName)) {

					if (apps == null) return true;

					for (String authorizedApp : apps) {

						if (app.equalsIgnoreCase(authorizedApp)) return true;
					}

				}
			}

			return false;
		}
		catch (RuntimeException e) {
			e.printStackTrace();

			throw e;
		}
		finally {
			server.closeConnection();
		}
	}

	/**
	 * Retrieves the Toto App name from the provided URL
	 * 
	 * @param requestURL
	 * @return
	 */
	private String getAppNameFromURL(StringBuffer requestURL) {

		boolean https = requestURL.toString().contains("https://");

		String urlWithoutProtocol = https ? requestURL.substring("https://".length()) : requestURL.substring("http://".length());

		String[] parts = urlWithoutProtocol.split("/");

		if (parts.length >= 1) return parts[1];

		return null;
	}

	public String getMongoHost() {
		return mongoHost_;
	}

	public void setMongoHost(String mongoHost) {
		mongoHost_ = mongoHost;
	}

	public int getMongoPort() {
		return mongoPort_;
	}

	public void setMongoPort(int mongoPort) {
		mongoPort_ = mongoPort;
	}

}
