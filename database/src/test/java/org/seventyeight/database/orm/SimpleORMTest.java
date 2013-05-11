package org.seventyeight.database.orm;

import org.junit.Test;
import org.seventyeight.database.annotations.Persisted;
import org.seventyeight.database.mongodb.MongoDocument;

import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cwolfgang
 */
public class SimpleORMTest {

    @Test
    public void test01() {
        List<Field> fields = SimpleORM.getFields( new Testing() );

        System.out.println( fields );

        assertThat( fields.size(), is( 2 ) );
    }

    @Test
    public void test02() {
        List<Field> fields = SimpleORM.getFields( new SubTesting() );

        System.out.println( fields );

        assertThat( fields.size(), is( 3 ) );
    }

    @Test
    public void test03() throws IllegalAccessException {
        MongoDocument doc = new MongoDocument(  );
        doc.set( "bar", "1" );
        doc.set( "string", "2" );

        Testing t = new Testing();

        System.out.println( "t1=" + t );

        SimpleORM.bindToObject( t, doc );

        System.out.println( "t2=" + t );

        assertThat( t.bar, is( "1" ) );
        assertThat( t.string, is( "2" ) );
    }

    @Test
    public void test04() throws IllegalAccessException {
        MongoDocument doc = new MongoDocument(  );
        Testing t = new Testing();
        t.bar = "1";
        t.string = "2";

        System.out.println( "t1=" + doc );

        SimpleORM.storeFromObject( t, doc );

        System.out.println( "t2=" + doc );

        assertThat( (String)doc.get( "bar" ), is( "1" ) );
        assertThat( (String)doc.get( "string" ), is( "2" ) );
    }


    public class Testing {
        public String foo;

        @Persisted( fieldName = "bar" )
        public String bar;

        @Persisted( fieldName = "string" )
        private String string;

        @Override
        public String toString() {
            return "BAR=" + bar + ", STRING=" + string;
        }
    }

    public class SubTesting extends Testing {
        @Persisted
        public int myInt;
    }
}
