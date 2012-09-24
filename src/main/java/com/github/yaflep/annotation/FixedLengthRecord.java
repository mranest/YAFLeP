package com.github.yaflep.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FixedLengthRecord {
	int minLength() default 0;
	int maxLength() default 0;
	int length() default 0;
	char paddingChar() default ' ';
}
