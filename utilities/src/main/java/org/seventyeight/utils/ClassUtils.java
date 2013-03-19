package org.seventyeight.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cwolfgang
 *         Date: 14-01-13
 *         Time: 22:26
 */
public class ClassUtils {
    private ClassUtils() {}

    public static Method getEnheritedMethod( Class<?> clazz, String method, Class<?>... args ) throws NoSuchMethodException {
        return getEnheritedMethod( clazz, method, null, args );
    }

    public static Method getEnheritedMethod( Class<?> clazz, String method, Class<? extends Annotation> annotationClass, Class<?>... args ) throws NoSuchMethodException {
        while( clazz != null ) {
            try {
                Method m = clazz.getDeclaredMethod( method, args );
                if( annotationClass != null ) {
                    Annotation a = m.getAnnotation( annotationClass );
                    if( a != null ) {
                        return m;
                    }
                } else {
                    return m;
                }
            } catch( NoSuchMethodException e ) {

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
