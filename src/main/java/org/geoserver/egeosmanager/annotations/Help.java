package org.geoserver.egeosmanager.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
	String text() default "";
	Parameter[] requires() default {};
	Parameter[] optionals() default {};
}
