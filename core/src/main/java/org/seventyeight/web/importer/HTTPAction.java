package org.seventyeight.web.importer;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;

public abstract class HTTPAction<A, R> {

	protected R result;
	protected CloseableHttpClient httpClient;
	
	public HTTPAction(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	public abstract R act(A argument) throws IOException;
	
	public R getResult() {
		return result;
	}
}