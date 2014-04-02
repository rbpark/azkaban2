package azkaban.rest;

import javax.servlet.http.HttpServletRequest;

import azkaban.user.User;
import azkaban.webapp.AzkabanWebServer;
import azkaban.webapp.session.Session;

public class SessionRequest {
	private String sessionId;
	
	private AzkabanWebServer getApp() {
		return AzkabanWebServer.getInstance();
	}
	
	public SessionRequest() {
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public User getUserFromSession(HttpServletRequest req) {
		Session session = getSessionFromSessionId(req.getRemoteAddr());
		if (session == null) {
			return null;
		}
		
		return session.getUser();
	}
	
	public Session getSessionFromSessionId(String remoteIp) {
		if (sessionId == null) {
			return null;
		}
		
		Session session = getApp().getSessionCache().getSession(sessionId);
		// Check if the IP's are equal. If not, we invalidate the sesson.
		if (session == null || !remoteIp.equals(session.getIp())) {
			return null;
		}
		
		return session;
	}
}