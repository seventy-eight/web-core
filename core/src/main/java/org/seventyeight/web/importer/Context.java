package org.seventyeight.web.importer;

import java.util.HashMap;
import java.util.Map;

public class Context {

	private String baseUrl;
	
	private Map<Integer, String> userMap = new HashMap<Integer, String>();
	private Map<Integer, String> groupMap = new HashMap<Integer, String>();

	public Context(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public Map<Integer, String> getUserMap() {
		return userMap;
	}

	public Map<Integer, String> getGroupMap() {
		return groupMap;
	}
	
	public String generateUrl(String urlPart) {
		return baseUrl + urlPart;
	}
}
