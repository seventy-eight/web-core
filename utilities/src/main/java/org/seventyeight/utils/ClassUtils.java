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
                if( m.getAnnotation( PostMethod.class ) == null ) {
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
                return clazz.getDeclaredMethod( method, args );
            } catch( NoSuchMethodException e ) {
                /* Just carry on */
            }

            clazz = clazz.getSuperclass();
        }

        throw new NoSuchMethodException( method );
    }


    public static List<Class<?>> getInterfaces( Class<?> clazz ) {
        List<Class<?>> interfaces = new ArrayList<Class<?>>();
        interfaces.addAll( Arrays.asList( clazz.getInterfaces() ) );

        Class<?> s = clazz.getSuperclass();
        if( s != null ) {
            interfaces.addAll( getInterfaces( s ) );
        }

        return interfaces;
    }

}
