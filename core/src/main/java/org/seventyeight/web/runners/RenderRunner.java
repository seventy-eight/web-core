package org.seventyeight.web.runners;

import java.io.IOException;

import org.seventyeight.web.model.CallContext;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.model.Runner;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

public class RenderRunner implements Runner {

	private Class<?> imposter;
	private Object object;
	private String method;
	
	private Request context;
	
	public RenderRunner(Object object, String method, Class<?> imposter) {
		this.object = object;
		this.method = method;
		this.imposter = imposter;
	}
	
	@Override
	public void run(Response response) {
		try {
			response.getWriter().print(context.getCore().getTemplateManager().getRenderer(context).renderClass( object, imposter, method + ".vm" ) );
		} catch(Exception e) {

		}
	}

	@Override
	public void injectContext(CallContext context) {
		if(!(context instanceof Request)) {
			throw new IllegalArgumentException("Not correct context, " + context);
		}
		
		this.context = (Request) context;
	}

}
