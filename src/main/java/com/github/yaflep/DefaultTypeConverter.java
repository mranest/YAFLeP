package com.github.yaflep;

import org.apache.commons.beanutils.ConvertUtils;

import com.github.yaflep.annotation.DataField;

/**
 * TODO Depend on PropertyEditor infrastructure
 * @author mranest
 */
public class DefaultTypeConverter implements TypeConverter {

	@Override
	public <T> T convertTo(Class<T> type, Object value, DataField dataField) 
	throws UnsupportedConversionException {
		if (value == null) {
			return null;
		}
		
		T t = convertTo(type, value);
		
		if (t == null) {		
			throw new UnsupportedConversionException();
		}
		
		return t;
	}

	@SuppressWarnings("unchecked")
	protected <T> T convertTo(Class<T> type, Object value) 
	throws UnsupportedConversionException {
		return (T) ConvertUtils.convert(value, type);
	}
	
}
