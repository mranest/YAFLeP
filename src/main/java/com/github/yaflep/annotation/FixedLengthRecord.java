package com.github.yaflep.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FixedLengthRecord {
	int length();
	char paddingChar() default ' ';
}
