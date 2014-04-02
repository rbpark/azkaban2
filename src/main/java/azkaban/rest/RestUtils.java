/*
 * Copyright 2014 LinkedIn Corp.
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
package azkaban.rest;

import java.util.Map;
import java.util.HashMap;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import azkaban.utils.JSONUtils;

/**
 * Useful tools
 */
public class RestUtils {
	private RestUtils() {
	}
	
	public static Response response(Status status, Map<String,Object> jsonOutput) {
		return response(status, JSONUtils.toJSON(jsonOutput));
	}
	
	public static Response response(Status status, String output) {
		return Response.status(status).entity(output).build();
	}
	
	public static Response accepted(Map<String,Object> jsonOutput) {
		return response(Status.ACCEPTED, jsonOutput);
	}
	
	public static Response badResponse(Status badStatus, Exception e, boolean includeStackTrace) {
		HashMap<String,Object> errorMsg = new HashMap<String,Object>();
		errorMsg.put("error", e.getMessage());
		
		if (includeStackTrace) {
			StringBuffer buffer = new StringBuffer();
			for(StackTraceElement elem : e.getStackTrace()) {
				buffer.append(elem.toString());
				buffer.append("\n");
			}
			errorMsg.put("stacktrace", buffer.toString());
		}
		
		return response(badStatus, errorMsg);
	}
	
	public static Response badRequest(String error) {
		HashMap<String, Object> responseMap = new HashMap<String,Object>();
		responseMap.put("error", error);
		return response(Status.BAD_REQUEST, responseMap);
	}
	
	public static Response badRequest(Exception e) {
		return badRequest(e, false);
	}
	
	public static Response badRequest(Exception e, boolean includeStackTrace) {
		return badResponse(Status.BAD_REQUEST, e, includeStackTrace);
	}
	
	public static Response unauthRequest(Exception e) {
		return badResponse(Status.UNAUTHORIZED, e, false);
	}
	
	public static Response unauthRequest(Exception e, boolean includeStackTrace) {
		return badResponse(Status.UNAUTHORIZED, e, includeStackTrace);
	}
	
	public static Response unauthRequest(String message) {
		HashMap<String, Object> responseMap = new HashMap<String,Object>();
		responseMap.put("error", message);
		return response(Status.UNAUTHORIZED, responseMap);
	}
	
}
