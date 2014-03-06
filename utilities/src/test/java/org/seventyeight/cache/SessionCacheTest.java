package org.seventyeight.cache;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class SessionCacheTest {
    class DB implements DBStrategy {

        private Map<String, Object> db = new HashMap<String, Object>(  );

        @Override
        public Object get( String id ) {
            return db.get( id );
        }

        @Override
        public void serialize( Object object ) {

        }

        @Override
        public Object deserialize( Object record ) {
            return record;
        }

        @Override
        public Object save( Object object, String id ) {
            db.put( id, object );
            return object;
        }

        @Override
        public String toString() {
            return db.toString();
        }
    }

    @Test
    public void test1() {
        SessionCache session = new SessionCache( 10, new DB() );

        String item1 = "FIRST ITEM";
        session.save( item1, "1" );

        String item2 = "SECOND ITEM";
        session.save( item2, "2" );

        String out = session.get( "1" );

        assertThat(out, is("FIRST ITEM"));
    }

    @Test
    public void test2() {
        DBStrategy db = new DB();
        SessionCache session = new SessionCache( 10, db );

        String item1 = "FIRST ITEM";
        session.save( item1, "1" );

        String item2 = "SECOND ITEM";
        session.save( item2, "2" );

        String out = session.get( "1" );

        assertThat(out, is("FIRST ITEM"));

        SessionCache session2 = new SessionCache( 10, db );

        System.out.println("DB: " + db);

        assertThat( (String) session2.get( "2" ), is("SECOND ITEM"));
    }
}
