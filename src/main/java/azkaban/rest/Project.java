package azkaban.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import azkaban.rest.Login.LoginRequest;
import azkaban.user.User;
import azkaban.webapp.AzkabanWebServer;

@Path("/project")
public class Project {
	private static Logger logger = Logger.getLogger(Project.class);
	
	private AzkabanWebServer getApp() {
		return AzkabanWebServer.getInstance();
	}
	
	@POST
	@Path("/deploy")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deploy(
		@Context HttpServletRequest request,
		DeployRequest deployRequest
	) {
		User user = deployRequest.getUserFromSession(request);
		if (user == null) {
			return RestUtils.unauthRequest("Login Required: User session is invalid or doesn't exist.");
		}
		
		if (deployRequest.getPackageUrl() == null) {
			return RestUtils.badRequest("Package location (packageUrl) not set.");
		}
		
		return null;
	}
	
	public static class DeployRequest extends SessionRequest {
		private String projectName;
		private String packageUrl;
		
		public String getProjectName() {
			return projectName;
		}
		
		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public String getPackageUrl() {
			return packageUrl;
		}

		public void setPackageUrl(String packageUrl) {
			this.packageUrl = packageUrl;
		}
		
	}
}