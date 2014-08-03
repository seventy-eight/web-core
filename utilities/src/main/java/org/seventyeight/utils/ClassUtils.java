package org.seventyeight.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cwolfgang
 */
public class ClassUtils {

    private ClassUtils() {}

    public static Method getInheritedMethod( Class<?> clazz, String method, Class<?>... args ) throws NoSuchMethodException {
        while( clazz != null ) {
            try {
                Method m = clazz.getDeclaredMethod( method, args );
                /* If null, the method is NOT annotated as a post method */
                if( m.getAnnotation( PostMethod.class ) == null && m.getAnnotation( PutMethod.class ) == null ) {
                    return m;
                }
            } catch( NoSuchMethodException e ) {
                /* Just carry on */
            }

            clazz = clazz.getSuperclass();
        }

        throw new NoSuchMethodException( method );
    }

    public static Method getInheritedPostMethod( Class<?> clazz, String method, Class<?>... args ) throws NoSuchMethodException {
        while( clazz != null ) {
            try {
                Method m = clazz.getDeclaredMethod( method, args );
                if( m.getAnnotation( PostMethod.class ) != null ) {
                    return m;
                }
            } catch( NoSuchMethodException e ) {
                /* Just carry on */
            }

            clazz = clazz.getSuperclass();
        }

        throw new NoSuchMethodException( method );
    }

    public static Method getInheritedPutMethod( Class<?> clazz, String method, Class<?>... args ) throws NoSuchMethodException {
        while( clazz != null ) {
            try {
                Method m = clazz.getDeclaredMethod( method, args );
                if( m.getAnnotation( PutMethod.class ) != null ) {
                    return m;
                }
            } catch( NoSuchMethodException e ) {
                /* Just carry on */
            }

            clazz = clazz.getSuperclass();
        }

        throw new NoSuchMethodException( method );
    }

    public static Method getInheritedDeleteMethod( Class<?> clazz, String method, Class<?>... args ) throws NoSuchMethodException {
        while( clazz != null ) {
            try {
                Method m = clazz.getDeclaredMethod( method, args );
                if( m.getAnnotation( DeleteMethod.class ) != null ) {
                    return m;
                }
            } catch( NoSuchMethodException e ) {
                /* Just carry on */
            }

            clazz = clazz.getSuperclass();
        }

        throw new NoSuchMethodException( method );
    }


    public static List<Class<?>> getInterfaces( Class<?> clazz ) {
        //System.out.println( "[CLASS=" + clazz + "]" );
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.addAll( Arrays.asList( clazz.getInterfaces() ) );

        Class<?> s = clazz.getSuperclass();
        if( s != null ) {
            interfaces.addAll( getInterfaces( s ) );
        }

        return interfaces;
    }

    public static List<Class<?>> getClasses( Class<?> clazz ) {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        while( clazz != null && !clazz.equals( Object.class ) ) {
            classes.add( clazz );
            clazz = clazz.getSuperclass();
        }

        return classes;
    }

}
