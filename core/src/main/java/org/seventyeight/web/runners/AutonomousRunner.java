package org.seventyeight.web.runners;

import java.io.IOException;

import org.seventyeight.web.model.Autonomous;
import org.seventyeight.web.model.CallContext;
import org.seventyeight.web.model.Runner;
import org.seventyeight.web.servlet.Response;

public class AutonomousRunner implements Runner {

	private Autonomous a;
	private CallContext context;
	
	public AutonomousRunner(Autonomous a) {
		this.a = a;
	}
	
	@Override
	public void run(Response response) {
		try {
			a.autonomize(context, response);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void injectContext(CallContext context) {
		this.context = context;
	}

}
