package org.seventyeight.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.seventyeight.utils.StopWatch;
import org.seventyeight.web.authentication.AuthenticationException;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.Autonomous;
import org.seventyeight.web.model.Runner;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@WebServlet( asyncSupported = true )
public class Rest extends HttpServlet {

    private static Logger logger = LogManager.getLogger( Rest.class );



    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doRequest( request, response );
    }

    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doRequest( request, response );
    }
    
    @Override
    public void doDelete( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doRequest( request, response );
    }
    
    @Override
    public void doPut( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doRequest( request, response );
    }

    public void doRequest( HttpServletRequest rqs, HttpServletResponse rsp ) throws ServletException, IOException {
        //PrintWriter out = response.getWriter();

        StopWatch sw = new StopWatch();
        sw.reset();

        sw.start( rqs.getRequestURI() );

        Core core = (Core) getServletContext().getAttribute( "core" );

        logger.debug( "Query  : {]", rqs.getQueryString() );
        logger.debug( "URI    : {}", rqs.getRequestURI() );
        logger.debug( "METHOD : {}", rqs.getMethod() );
        logger.debug( "CORE   : {}", core );

        /* Instantiating request and response */
        Request request = new Request( rqs, core.getDefaultTemplate() );
        request.setLocaleFromCookie( "language" );

        Response response = new Response( rsp );
        response.setCharacterEncoding( "UTF-8" );

        request.setStopWatch( sw );

        logger.debug( "[Parameters] {}", rqs.getParameterMap() );

        /* Instantiating context */
        VelocityContext vc = new VelocityContext();
        vc.put( "title", "" );

        request.setContext( vc );
        request.getContext().put( "request", request );
        request.setRequestParts( rqs.getRequestURI().split( "/" ) );
        logger.debug( "------ {} -----", Arrays.asList( request.getRequestParts() ) );

        vc.put( "currentUrl", rqs.getRequestURI() );

        request.setUser( core.getAnonymousUser() );
        request.setTheme( core.getDefaultTheme() );

        request.getStopWatch().stop( rqs.getRequestURI() );

        if(request.getRequestParts().length > 0 && request.getRequestParts()[0].equalsIgnoreCase("static")) {
        	try {
				((Autonomous)core.getRoot().getChild("static")).autonomize(request, response);
			} catch (Throwable e) {
				throw new ServletException(e);
			}
        } else {
            request.getStopWatch().start( "Authentication" );

            logger.debug( "THE USER: {}", request.getUser() );
            try {
                logger.debug( "AUTHENTICATING" );
                core.getAuthentication().authenticate( request, response );
            } catch( AuthenticationException e ) {
                logger.warn( "Unable to authenticate", e );
            }
		
		    logger.debug( "THE USER: {}", request.getUser() );
		    request.getStopWatch().stop( "Getting user" );
		
		    request.getStopWatch().stop( "Authentication" );
		    request.getStopWatch().start( "Render page" );
		
		    try {
		        // Render the page
		        Runner runner = core.render( request );
		        runner.injectContext(request);
		        runner.run(response);
		        request.getUser().setSeen();
		    } catch( CoreException e ) {
		        e.printStackTrace();
		        if( response.isRenderingMain() ) {
		            response.renderError( request, e );
		        } else {
		            response.sendError( e.getCode(), e.getMessage() );
		        }
		    } catch( Throwable e ) {
		        logger.error( "CAUGHT ERROR" );
		        e.printStackTrace();
		        generateException( request, rsp.getWriter(), e, e.getMessage() );
		    }
        }

        sw.stop();
        logger.info( "Request response: {}", response.getResponse() );

        // Render the bottom
        /*
        if( response.isRenderingMain() ) {
            try {
                vc.put( "seconds", sw.getSeconds() );
                response.getWriter().print( core.getTemplateManager().getRenderer( request ).render( "org/seventyeight/web/bottomPage.vm" ) );
            } catch( TemplateException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        */

        //logger.info( sw.print( 1000 ) );
        System.out.println( sw.print( 10000 ) );
    }

    private void generateException( Request request, PrintWriter writer, Throwable e, String message ) {
        logger.error( "Generating error: {}", e.getMessage() );
        try {
            VelocityContext vc = new VelocityContext();
            vc.put( "stacktrace", e.getStackTrace() );
            vc.put( "message", message );

            Core core = (Core) request.getContext().get( "core" );

            org.seventyeight.web.model.Error error = new org.seventyeight.web.model.Error( (Exception)e );

            request.getContext().put( "content", core.getTemplateManager().getRenderer( request ).setContext( vc ).renderObject( error, "view.vm" ) );
            request.getContext().put( "title", message );
            writer.print( core.getTemplateManager().getRenderer( request ).render( "org/seventyeight/web/main.vm" ) );
        } catch( Exception ec ) {
            request.getContext().put( "content", "Error while displaying exception" );
        }
    }

}
