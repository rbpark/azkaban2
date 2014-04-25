/*
 * Copyright 2012 LinkedIn Corp.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import azkaban.executor.ExecutorManager;
import azkaban.project.ProjectManager;
import azkaban.scheduler.ScheduleManager;
import azkaban.user.Permission;
import azkaban.user.Role;
import azkaban.user.User;
import azkaban.user.UserManager;
import azkaban.webapp.AzkabanWebServer;
import azkaban.webapp.session.Session;

public class AdminServlet extends LoginAbstractAzkabanServlet {
	private static final long serialVersionUID = -3904230215801164381L;
	private UserManager userManager;
	private ProjectManager projectManager;
	private ExecutorManager executorManager;
	private ScheduleManager scheduleManager;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		AzkabanWebServer server = (AzkabanWebServer)getApplication();
		projectManager = server.getProjectManager();
		executorManager = server.getExecutorManager();
		scheduleManager = server.getScheduleManager();
		userManager = server.getUserManager();
	}
	
	@Override
	protected void handleGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws ServletException, IOException {
		
		handleAdminPage(req, resp, session);
	}

	@Override
	protected void handlePost(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws ServletException, IOException {

	}
	
	private void handleAdminPage(HttpServletRequest req, HttpServletResponse resp, Session session) throws IOException {
		Page page = newPage(req, resp, session, "azkaban/webapp/servlet/velocity/admin.vm");
		if(!hasAdminPermission(session.getUser())) {
			page.add("errorMsg", "User " + session.getUser().getUserId() + " has no permission.");
			page.render();
			return;
		}
		
		page.render();
	}
	
	protected boolean hasAdminPermission(User user) {	
		for(String roleName: user.getRoles()) {
			Role role = userManager.getRole(roleName);
			if (role.getPermission().isPermissionSet(Permission.Type.ADMIN)) {
				return true;
			}
		}
		
		return false;
	}
}