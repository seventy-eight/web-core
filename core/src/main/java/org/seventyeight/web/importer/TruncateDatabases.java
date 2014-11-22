package org.seventyeight.web.importer;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TruncateDatabases extends HTTPAction<String, Boolean> {

	private static Logger logger = LogManager.getLogger(TruncateDatabases.class);

	public TruncateDatabases(CloseableHttpClient httpClient) {
		super(httpClient);
	}
	
	@Override
	public Boolean act(String s) throws IOException {
		HttpDelete getRequest = new HttpDelete("http://localhost:8080/clear/");
		CloseableHttpResponse response = httpClient.execute(getRequest);
		if(response.getStatusLine().getStatusCode() == 200) {
			logger.info("Databases truncated");
		} else {
			logger.info("Databases WAS NOT truncated");
		}
		
		return true;
	}
	
}