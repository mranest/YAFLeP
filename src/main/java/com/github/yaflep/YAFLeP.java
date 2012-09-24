package com.github.yaflep;

import java.lang.reflect.Field;

import com.github.yaflep.TypeConverter.UnsupportedConversionException;
import com.github.yaflep.annotation.DataField;
import com.github.yaflep.annotation.FixedLengthRecord;

public class YAFLeP<T> {

//	private static final Logger LOGGER = LoggerFactory.getLogger(YAFLeP.class);
	
	private static final TypeConverter DEFAULT_TYPE_CONVERTER = new DefaultTypeConverter();
	
	public static <T> YAFLeP<T> newInstance(Class<T> clazz) {
		return new YAFLeP<T>(clazz);
	}
	
	private final Class<T> clazz;
	
	private final FixedLengthRecord flr; 
	
	private Field[] declaredFields = null;
	
	private Field[] getDeclaredFields() {
		if (declaredFields == null) {
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field each: declaredFields) {
				each.setAccessible(true);
			}
			
			this.declaredFields = declaredFields;
		}
		
		return declaredFields;
	}
	
	private Field getFixedValueDeclaredField() {
		for (Field each: getDeclaredFields()) {
			DataField df = each.getAnnotation(DataField.class);
			
			if (df == null) {
				continue;
			}
			
			if (df.fixed()) {
				return each;
			}
		}
		
		return null;
	}
	
	private String fixedValue = null;
	
	private String getFixedValue() throws InstantiationException, IllegalAccessException {
		if (fixedValue == null && getFixedValueDeclaredField() != null) {
			T o = clazz.newInstance();
			fixedValue = (String) getFixedValueDeclaredField().get(o);
		}
		
		return fixedValue;
	}
	
	private YAFLeP(Class<T> clazz) { 
		this.clazz = clazz;
		
		flr = clazz.getAnnotation(FixedLengthRecord.class);
		if (flr == null) {
			throw new IllegalArgumentException("Class must be annotated with @FixedLengthRecord");
		}
	}
	
	public T unmarshal(String line) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, UnsupportedConversionException {
		return unmarshal(line, DEFAULT_TYPE_CONVERTER);
	}
	
	public T unmarshal(String line, TypeConverter typeConverter) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, UnsupportedConversionException {
		T o = clazz.newInstance();
		
		for (Field each: getDeclaredFields()) {
			DataField df = each.getAnnotation(DataField.class);
			
			if (df == null) {
				continue;
			}
			
			int pos = df.pos();
			int length = df.length();
			boolean last = df.last();
			
			String valueAsString = last ?
					line.substring(pos-1) :
					line.substring(pos-1, pos+length-1);
					
			if (df.trimToNull()) {
				valueAsString = valueAsString.trim();
				if (valueAsString.length() == 0) {
					valueAsString = null;
				}
			}
			
			each.set(o, typeConverter.convertTo(
					each.getType(), 
					valueAsString,
					df));
		}
		
		return o;
	}
	
	public boolean matches(String line) throws InstantiationException, IllegalAccessException {
		int length = flr.length();
		int minLength = flr.minLength();
		int maxLength = flr.maxLength();
		
		if (length != 0 && flr.length() != line.length()) {
			return false;
		}
		
		if (	minLength != 0 && 
				maxLength != 0 &&
				(line.length() < minLength ||
				 line.length() > maxLength)) {
			return false;
		}
		
		if (getFixedValue() != null) {
			DataField df = getFixedValueDeclaredField().getAnnotation(DataField.class);
			if (line.substring(df.pos()-1, df.pos()+df.length()-1).equals(getFixedValue())) {
				return true;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
}
