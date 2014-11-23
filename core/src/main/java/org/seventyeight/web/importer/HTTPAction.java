package org.seventyeight.web.importer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.gson.JsonObject;

public abstract class HTTPAction<A, R> {

	protected R result;
	protected CloseableHttpClient httpClient;
	protected Context context;
	
	public HTTPAction(CloseableHttpClient httpClient, Context context) {
		this.httpClient = httpClient;
		this.context = context;
	}
	
	public abstract R act(A argument) throws IOException, ImportException;
	
	public R getResult() {
		return result;
	}
	
	protected HttpPost getPostRequest(String urlPart, JsonObject json) throws UnsupportedEncodingException {
		HttpPost postRequest = new HttpPost(context.generateUrl(urlPart));
		StringEntity input = new StringEntity(json.toString(), "UTF-8");
		input.setContentType("application/json");
		postRequest.setEntity(input);
		
		return postRequest;
	}
	
	protected static JsonObject getJsonRequest() {
		JsonObject creds = new JsonObject();
    	creds.addProperty("username", "wolle");
    	creds.addProperty("password", "pass");
    	
    	JsonObject obj = new JsonObject();
    	
    	obj.add("credentials", creds);
    	
    	return obj;
	}
}