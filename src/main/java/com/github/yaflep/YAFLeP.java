package com.github.yaflep;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	
	private final Map<DataField, Field> dataFieldMap;
	
	private YAFLeP(Class<T> clazz) { 
		this.clazz = clazz;
		
		flr = clazz.getAnnotation(FixedLengthRecord.class);
		if (flr == null) {
			throw new IllegalArgumentException("Class must be annotated with @FixedLengthRecord");
		}

		dataFieldMap = new HashMap<DataField, Field>();
		
		for (Field each: clazz.getDeclaredFields()) {
			each.setAccessible(true);
			
			DataField df = each.getAnnotation(DataField.class);
			if (df != null) {
				dataFieldMap.put(df, each);
				
				LOGGER.info("DataField added for {}", clazz.getName());
			}
		}
	}
	
	public T unmarshall(String line) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, UnsupportedConversionException {
		return unmarshall(line, DEFAULT_TYPE_CONVERTER);
	}
	
	public T unmarshall(String line, TypeConverter typeConverter) 
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, UnsupportedConversionException {
		T t = clazz.newInstance();
		
		for (Entry<DataField, Field> each: dataFieldMap.entrySet()) {
			int pos = each.getKey().pos();
			int length = each.getKey().length();
			
			String valueAsString = line.substring(pos-1, pos+length-1);
			if (each.getKey().trim()) {
				valueAsString = valueAsString.trim();
			}
			
			each.getValue().set(t, typeConverter.convertTo(
					each.getValue().getType(), 
					valueAsString));
		}
		
		return t;
	}
	
	public boolean matches(String line) {
		if (flr.length() != line.length()) {
			return false;
		}
		
		// TODO Matched fixed fields
		
		return true;
	}
	
}
