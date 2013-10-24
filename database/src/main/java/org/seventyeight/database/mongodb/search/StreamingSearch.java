package org.seventyeight.database.mongodb.search;

import org.seventyeight.database.mongodb.MongoDBQuery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author cwolfgang
 */
public class StreamingSearch {

    public static class Result {
        String id;
        int score;
    }

    public static abstract class Query {
        private MongoDBQuery query;

        protected Query( MongoDBQuery query ) {
            this.query = query;
        }

        public List<Result> fetch( int offset, int number ) {
            return fetch( offset, number, null );
        }

        public abstract List<Result> fetch( int offset, int number, List<Result> previousResult );
    }

    private List<Query> queries = new ArrayList<Query>(  );

    private int number = 10;
    private int offset = 0;
    private Queue<Result> current = new LinkedList<Result>();

    public StreamingSearch addQuery( Query query ) {
        queries.add( query );
        return this;
    }

    /**
     * Fetch the next id
     */
    public String fetch() {
        Result r = current.remove();
        return r.id;
    }

    public boolean hasMore() {
        if( current.isEmpty() ) {
            current.addAll( fetch( offset ) );
        }

        return !current.isEmpty();
    }

    private List<Result> fetch( int offset ) {
        /* First query */
        List<Result> results = queries.get( 0 ).fetch( offset, number );
        offset += number;

        for( int i = 1 ; i < queries.size() ; i++ ) {
            results = queries.get( i ).fetch( offset, number, results );
        }

        return results;
    }
}
