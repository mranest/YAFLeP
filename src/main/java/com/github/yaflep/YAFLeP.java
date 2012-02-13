package com.github.yaflep;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yaflep.TypeConverter.UnsupportedConversionException;
import com.github.yaflep.annotation.DataField;
import com.github.yaflep.annotation.FixedLengthRecord;

public class YAFLeP<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(YAFLeP.class);
	
	private static final TypeConverter DEFAULT_TYPE_CONVERTER = new DefaultTypeConverter();
	
	public static <T> YAFLeP<T> newInstance(Class<T> clazz) {
		return new YAFLeP<T>(clazz);
	}
	
	private final Class<T> clazz;
	
	private final FixedLengthRecord flr; 
	
	private Map<Class<?>, Field[]> declaredFieldsMap = new HashMap<Class<?>, Field[]>();
	
	public Field[] getDeclaredFields(Class<?> clazz) {
		if (! declaredFieldsMap.containsKey(clazz)) {
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field each: declaredFields) {
				each.setAccessible(true);
			}
			
			declaredFieldsMap.put(clazz, declaredFields);
		}
		
		return declaredFieldsMap.get(clazz);
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
		return unmarshalObject(clazz, line, typeConverter);
	}
	
	public <O> O unmarshalObject(Class<O> objectClass, String line, TypeConverter typeConverter) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, UnsupportedConversionException {
		O o = objectClass.newInstance();
		
		for (Field each: getDeclaredFields(objectClass)) {
			DataField df = each.getAnnotation(DataField.class);
			
			if (df == null) {
				continue;
			}
			
			int pos = df.pos();
			int length = df.length();
			
			String valueAsString = line.substring(pos-1, pos+length-1);
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
	
	public boolean matches(String line) {
		if (flr.length() != line.length()) {
			return false;
		}
		
		// TODO Matched fixed fields
		
		return true;
	}
	
}
