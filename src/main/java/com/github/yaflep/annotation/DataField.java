package com.github.yaflep.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataField {
	int pos();
	int length();
	char paddingChar() default ' ';
	boolean trim() default false;
	/* TODO Revert to matching by fixed fields if multiple FixedLengthRecord exists
	 * for a pre-determined length */
	boolean fixed() default false;
}
