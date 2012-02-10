package com.github.yaflep.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataField {
	int pos();
	int length();
	char paddingChar() default ' ';
	boolean trimToNull() default false;
	boolean fixed() default false;
	int decimalPoints() default 0;
}
