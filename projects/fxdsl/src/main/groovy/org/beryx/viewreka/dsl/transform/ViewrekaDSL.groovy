package org.beryx.viewreka.dsl.transform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@java.lang.annotation.Documented
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(classes=ViewrekaDSLTransformation)
public @interface ViewrekaDSL {
	@SuppressWarnings("rawtypes")
	Class<? extends Keyword>[] keywords() default [];
}
