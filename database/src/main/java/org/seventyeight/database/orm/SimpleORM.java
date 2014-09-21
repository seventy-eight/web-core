package org.seventyeight.database.orm;

import org.seventyeight.database.annotations.Persisted;
import org.seventyeight.database.mongodb.MongoDocument;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
                //System.out.println( "--->" + f.getName() + ", " + f.getModifiers() + ", " + ( f.getModifiers() | 4096 ) );

            	if( !Modifier.isTransient( f.getModifiers() ) && !Modifier.isStatic( f.getModifiers() ) && !isSynthetic( f.getModifiers() )  ) {
                    System.out.println( "Setting " + f.getName() );
                    String name = f.getName();
                    if(document.contains(name)) {
                    	f.setAccessible( true );
                    	f.set( object, f.getType().cast( document.get( name ) ) );
                    }
	           }

                //System.out.println( f.getName() + " = " + f.get( object ) );
            }

            clazz = clazz.getSuperclass();
        }
    }

    public static void storeFromObject( Object object, MongoDocument document ) throws IllegalAccessException {

        Class<?> clazz = object.getClass();

        while( clazz != null && !clazz.equals( Object.class ) ) {

            Field[] fs = clazz.getDeclaredFields();

            for( Field f : fs ) {
                //System.out.println( "--->" + f.getName() + ", " + f.getModifiers() + ", " + ( f.getModifiers() | 4096 ) );
                //System.out.println( "--->" + f.getName() + ", " + f.getModifiers() + ", " + ( f.getModifiers() | 8192 ) );
                if( !Modifier.isTransient( f.getModifiers() ) && !Modifier.isStatic( f.getModifiers() ) && !isSynthetic( f.getModifiers() ) ) {
                    String name = f.getName();
                    f.setAccessible( true );
                    document.set( name, f.get( object ) );
                }
            }

            clazz = clazz.getSuperclass();
        }
    }


    public static boolean isSynthetic( int i ) {
        return ( i & 4096 ) == 4096;
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
