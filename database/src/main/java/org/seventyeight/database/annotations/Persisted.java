package org.seventyeight.database.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author cwolfgang
 */
@Retention( RUNTIME )
@Target( { FIELD } )
public @interface Persisted {
    String fieldName() default "";
}
