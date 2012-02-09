package com.github.yaflep;

import java.math.BigDecimal;

/**
 * TODO Depend on PropertyEditor infrastructure
 * @author mranest
 */
public class DefaultTypeConverter implements TypeConverter {

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convertTo(Class<T> type, Object value) 
	throws UnsupportedConversionException {
		if (! (value instanceof String)) {
			throw new UnsupportedConversionException();
		}
		
		if (type.equals(String.class)) {
			return (T) value;
		} else if (type.isAssignableFrom(Integer.class)) {
			return (T) Integer.valueOf((String) value);
		} else if(type.equals(BigDecimal.class)) {
			return (T) new BigDecimal((String) value);
		}
		
		throw new UnsupportedConversionException();
	}
	
}
