package com.imatz.toto.util.auth.impl;

import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.bson.Document;

import com.imatz.toto.util.auth.Caller;
import com.imatz.toto.util.auth.TotoAuthorizationCheckResult;
import com.imatz.toto.util.db.mongo.MongoDB;
import com.imatz.toto.util.db.mongo.MongoServer;
import com.mongodb.client.FindIterable;

/**
 * Checks if the External Application is authorized to call Toto Microservices
 * (API).
 * 
 * Spring bean defined in the XML Spring configuration file.
 * 
 * @author nicolas
 *
 */
public class ExternalAppAuthorizationCheck {

	private String mongoHost_;
	private int mongoPort_;

	public TotoAuthorizationCheckResult checkAuthorization(Caller caller, HttpServletRequest req) {

		MongoServer server = new MongoServer(mongoHost_, mongoPort_);
		MongoDB database = server.getDatabase("login");

		try {

			// 1. Get Authorization Header
			String authorizationHeader = req.getHeader("Authorization");

			if (authorizationHeader == null || authorizationHeader.isEmpty()) return TotoAuthorizationCheckResult.notAuthorized();

			// 2. Check if Basic Authentication
			StringTokenizer st = new StringTokenizer(authorizationHeader);

			String type = st.nextToken();

			if (!type.equals("Basic")) return TotoAuthorizationCheckResult.notAuthorized();

			// 3. Retrieve credentials
			String credentials = new String(Base64.decodeBase64(st.nextToken().getBytes()), "UTF-8");

			int p = credentials.indexOf(":");

			if (p == -1) return TotoAuthorizationCheckResult.notAuthorized();

			String username = credentials.substring(0, p).trim();
			String password = credentials.substring(p + 1).trim();

			// 4. Get app name
			String app = getAppNameFromURL(req.getRequestURL());

			if (app == null) return TotoAuthorizationCheckResult.notAuthorized();

			// 5. Check credentials on DB
			if (!userCredentialsValid(username, password, database)) return TotoAuthorizationCheckResult.notAuthorized();

			// 6. Check user authorization on the app
			if (userAuthorizedToAccessApp(username, app, database)) return TotoAuthorizationCheckResult.authorized();

			return TotoAuthorizationCheckResult.notAuthorized();
		}
		catch (Exception e) {

			e.printStackTrace();

			return TotoAuthorizationCheckResult.notAuthorized();
		}
		finally {
			server.closeConnection();
		}
	}

	/**
	 * Checks if the specified technical user is authorized to access the
	 * specified app
	 * 
	 * @param username
	 * @param app
	 * @param database
	 * @return
	 */
	private boolean userAuthorizedToAccessApp(String username, String app, MongoDB database) {

		FindIterable<Document> docs = database.getCollection("authorizedTechnicalUsers").find(new Document("username", username));

		if (docs.first() == null) return false;

		@SuppressWarnings("unchecked")
		List<String> authorizedApps = (List<String>) docs.first().get("apps");

		if (authorizedApps == null || authorizedApps.isEmpty()) return true;

		for (String authorizedApp : authorizedApps) {

			if (authorizedApp.equalsIgnoreCase(app)) return true;
		}

		return false;
	}

	/**
	 * Checks if the provided credentials are valid
	 * 
	 * @param username
	 * @param password
	 * @param database
	 * @return
	 */
	private boolean userCredentialsValid(String username, String password, MongoDB database) {

		Document filter = new Document();
		filter.append("username", username);
		filter.append("password", password);

		FindIterable<Document> docs = database.getCollection("technicalUsers").find(filter);

		if (docs.first() == null) return false;

		return true;
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
