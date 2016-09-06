package org.geoserver.egeosmanager.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
	String name() default "";
	String description() default "";
}
