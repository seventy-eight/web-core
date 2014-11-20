package org.seventyeight.web.runners;

import java.io.IOException;

import org.seventyeight.web.model.CallContext;
import org.seventyeight.web.model.Runner;
import org.seventyeight.web.servlet.Response;

public class RedirectRunner implements Runner {

	private String url;
	
	public RedirectRunner(String url) {
		this.url = url;
	}
	
	@Override
	public void run(Response response) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void injectContext(CallContext context) {
		// TODO Auto-generated method stub
		
	}

}
