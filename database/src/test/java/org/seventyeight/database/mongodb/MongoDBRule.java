package org.seventyeight.database.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.net.UnknownHostException;

/**
 * @author cwolfgang
 *         Date: 16-02-13
 *         Time: 20:44
 */
public class MongoDBRule implements TestRule {

    private MongoDBManager manager;
    private String databaseName;
    private MongoDatabase db;

    public MongoDBRule( String databaseName ) {
        this.databaseName = databaseName;
        try {
            manager = new MongoDBManager( databaseName );
        } catch( UnknownHostException e ) {
            throw new IllegalStateException( e );
        }
    }

    private void before() throws UnknownHostException {
        db = manager.getDatabase();
    }

    private void after() {
        db.remove();
        manager.close();
    }

    public MongoDBManager getManager() {
        return manager;
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    @Override
    public Statement apply( final Statement base, final Description description ) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                System.out.println( " ===== Setting up MongoDB =====" );

                try {
                    before();
                    base.evaluate();
                } catch( Exception e ) {
                    e.printStackTrace();
                } finally {
                    System.out.println( " ===== Tearing down MongoDB =====" );
                    after();
                }
            }
        };
    }
}
