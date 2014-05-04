package org.seventyeight.web.utilities;

import org.junit.Test;
import org.mockito.Mockito;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.web.Core;
import org.seventyeight.web.CoreException;
import org.seventyeight.web.DummyCore;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.Searchable;

import java.io.File;

/**
 * @author cwolfgang
 */
public class QueryVisitorTest {

    @Test
    public void test01() throws CoreException {
        Core core = new DummyCore( new File( "" ), "" );
        Core spy = Mockito.spy(core);
        Mockito.doReturn( new TestSearchable() ).when( spy ).gets
        QueryVisitor visitor = new QueryVisitor();
    }

    class TestSearchable extends Searchable {

        @Override
        public Class<? extends Node> getClazz() {
            return null;  /* Implementation is a no op */
        }

        @Override
        public String getName() {
            return "Test searchable";
        }

        @Override
        public String getMethodName() {
            return "test";
        }

        @Override
        public MongoDBQuery search( String term ) {
            return new MongoDBQuery();
        }
    }
}
