package org.seventyeight.web;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.seventyeight.database.mongodb.MongoDatabase;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author cwolfgang
 */
public class WebCoreEnv implements TestRule {

    protected File path;
    protected String databaseName;
    protected MongoDatabase db;
    protected Core core;

    public WebCoreEnv( String databaseName ) {
        this.databaseName = databaseName;
    }

    protected void before( File path ) throws UnknownHostException {
        this.core = new Core( path, databaseName );
        db = core.getDatabase();

    }

    protected void after() {
        db.remove();
    }

    public Core getCore() {
        return core;
    }

    @Override
    public Statement apply( final Statement base, final Description description ) {
        try {
            path = File.createTempFile( "SEVENTYEIGHT", "web" );

            if( !path.delete() ) {
                System.out.println( path + " could not be deleted" );
            }

            if( !path.mkdir() ) {
                System.out.println( "DAMN!" );
            }
            System.out.println( "Path: " + path );
        } catch( IOException e ) {
            System.out.println( "Unable to create temporary path" );
        }

        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                Thread t = Thread.currentThread();
                String o = t.getName();
                t.setName( "Executing " + description.getDisplayName() );
                System.out.println( " ===== Setting up Seventy Eight Web Environment =====" );

                try {
                    before( path );
                    System.out.println( " ===== Running test: " + description.getDisplayName() + " =====" );
                    base.evaluate();
                } catch( Exception e ) {
                    //System.out.println( "Caught exception: " + e.getMessage() );
                    e.printStackTrace();
                } finally {
                    System.out.println( " ===== Tearing down Seventy Eight Web Environment =====" );
                    after();
                    t.setName( o );
                }
            }
        };
    }
}
