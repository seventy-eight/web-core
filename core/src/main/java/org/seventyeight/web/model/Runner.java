package org.seventyeight.web.model;

import org.seventyeight.web.servlet.Response;

public interface Runner {
	public void run(Response response);
	public void injectContext(CallContext context);
}
