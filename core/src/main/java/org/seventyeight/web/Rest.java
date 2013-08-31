package org.seventyeight.web;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.seventyeight.utils.StopWatch;
import org.seventyeight.web.authentication.AuthenticationException;
import org.seventyeight.web.model.HttpException;
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

    private static Logger logger = Logger.getLogger( Rest.class );

    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doRequest( request, response );
    }

    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doRequest( request, response );
    }

    public void doRequest( HttpServletRequest rqs, HttpServletResponse rsp ) throws ServletException, IOException {
        //PrintWriter out = response.getWriter();

        StopWatch sw = new StopWatch();
        sw.reset();

        sw.start( "preparing" );

        logger.debug( "Query  : " + rqs.getQueryString() );
        logger.debug( "URI    : " + rqs.getRequestURI() );
        logger.debug( "METHOD : " + rqs.getMethod() );

        /* Instantiating request */
        Request request = new Request( rqs );
        Response response = new Response( rsp );
        response.setCharacterEncoding( "UTF-8" );

        logger.debug( "[Parameters] " + rqs.getParameterMap() );

        /* Instantiating context */
        VelocityContext vc = new VelocityContext();
        vc.put( "title", "" );

        request.setContext( vc );
        request.getContext().put( "request", request );
        request.setRequestParts( rqs.getRequestURI().split( "/" ) );
        logger.debug( "------ " + Arrays.asList( request.getRequestParts() ) + " ------" );

        vc.put( "currentUrl", rqs.getRequestURI() );

        request.setUser( Core.getInstance().getAnonymousUser() );
        request.setTheme( Core.getInstance().getDefaultTheme() );

        try {
            logger.debug( "AUTHENTICATING" );
            Core.getInstance().getAuthentication().authenticate( request, response );
        } catch( AuthenticationException e ) {
            logger.warn( "Unable to authenticate", e );
        }

        try {
            Core.getInstance().render( request, response );
        } catch( CoreException e ) {
            e.printStackTrace();
            if( request.isPagedResponseType() ) {
                response.renderError( request, e );
            } else {
                response.sendError( e.getCode(), e.getMessage() );
            }
        } catch( Exception e ) {
            logger.error( "CAUGHT ERROR" );
            e.printStackTrace();
            generateException( request, rsp.getWriter(), e, e.getMessage() );
        }

        sw.stop();
        logger.info( sw.print( 1000 ) );
    }

    private void generateException( Request request, PrintWriter writer, Throwable e, String message ) {
        logger.error( "Generating error: " + e.getMessage() );
        try {
            VelocityContext vc = new VelocityContext();
            vc.put( "stacktrace", e.getStackTrace() );
            vc.put( "message", message );

            org.seventyeight.web.model.Error error = new org.seventyeight.web.model.Error( (Exception)e );

            request.getContext().put( "content", Core.getInstance().getTemplateManager().getRenderer( request ).setContext( vc ).renderObject( error, "view.vm" ) );
            request.getContext().put( "title", message );
            writer.print( Core.getInstance().getTemplateManager().getRenderer( request ).render( "org/seventyeight/web/main.vm" ) );
        } catch( Exception ec ) {
            request.getContext().put( "content", "Error while displaying exception" );
        }
    }

}
