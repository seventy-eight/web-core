package org.seventyeight.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author cwolfgang
 */
@Retention( RUNTIME )
@Target( { ElementType.TYPE } )
public @interface Installation {
}
