package azkaban.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import azkaban.user.User;
import azkaban.user.UserManager;
import azkaban.user.UserManagerException;
import azkaban.webapp.AzkabanWebServer;
import azkaban.webapp.session.Session;

@Path("/login")
public class Login {
	private static Logger logger = Logger.getLogger(Project.class);
	
	private AzkabanWebServer getApp() {
		return AzkabanWebServer.getInstance();
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(
			@Context HttpServletRequest request,
			LoginRequest loginRequest
	) throws UserManagerException, ServletException, IOException {
		String ip = request.getRemoteAddr();
		if (loginRequest == null) {
			logger.info("Login request is null from ip " + ip);
			return RestUtils.badRequest("Login request not well formed.");
		}
		
		logger.info("Login Request from " + loginRequest.username + " from " + ip);
		
		Session session = null;
		try {
			session = createSession(loginRequest.getUsername(), loginRequest.getPassword(), ip);
		} catch (UserManagerException e) {
			logger.error("Failed Login Request: " + loginRequest.getUsername(), e);
			return RestUtils.unauthRequest(e);
		}
		
		getApp().getSessionCache().addSession(session);
		HashMap<String,Object> response = new HashMap<String,Object>();
		response.put("session.id", session.getSessionId());
		logger.error("Login Succeeded: " + loginRequest.getUsername());
		return RestUtils.accepted(response);
	}

	private Session createSession(String username, String password, String ip) throws UserManagerException, ServletException {
		UserManager manager = getApp().getUserManager();
		User user = manager.getUser(username, password);

		String randomUID = UUID.randomUUID().toString();
		Session session = new Session(randomUID, user, ip);
		
		return session;
	}
	
	public static class LoginRequest {
		private String username;
		private String password;
		
		public LoginRequest() {
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
