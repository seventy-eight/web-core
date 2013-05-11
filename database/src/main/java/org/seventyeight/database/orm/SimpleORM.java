package org.seventyeight.database.orm;

import org.seventyeight.database.annotations.Persisted;
import org.seventyeight.database.mongodb.MongoDocument;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwolfgang
 */
public class SimpleORM {

    public void build( Object object, MongoDocument document ) {
        List<Field> fields = getFields( object );
    }

    public static void bindToObject( Object object, MongoDocument document ) throws IllegalAccessException {

        Class<?> clazz = object.getClass();

        while( clazz != null && !clazz.equals( Object.class ) ) {

            Field[] fs = clazz.getDeclaredFields();

            for( Field f : fs ) {
                Persisted p = f.getAnnotation( Persisted.class );
                if( p != null ) {
                    String name = p.fieldName().isEmpty() ? f.getName() : p.fieldName();
                    f.setAccessible( true );
                    f.set( object, f.getType().cast( document.get( name ) ) );
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static void storeFromObject( Object object, MongoDocument document ) throws IllegalAccessException {

        Class<?> clazz = object.getClass();

        while( clazz != null && !clazz.equals( Object.class ) ) {

            Field[] fs = clazz.getDeclaredFields();

            for( Field f : fs ) {
                Persisted p = f.getAnnotation( Persisted.class );
                if( p != null ) {
                    String name = p.fieldName().isEmpty() ? f.getName() : p.fieldName();
                    f.setAccessible( true );
                    document.set( name, f.get( object ) );
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static List<Field> getFields( Object object ) {

        List<Field> fields = new ArrayList<Field>(  );

        Class<?> clazz = object.getClass();

        while( clazz != null && !clazz.equals( Object.class ) ) {

            Field[] fs = clazz.getDeclaredFields();

            for( Field f : fs ) {
                if( f.getAnnotation( Persisted.class ) != null ) {
                    fields.add( f );
                }
            }

            clazz = clazz.getSuperclass();
        }

        return fields;
    }
}
