package com.github.yaflep;

import com.github.yaflep.annotation.DataField;

public interface TypeConverter {

    <T> T convertTo(Class<T> type, Object value, DataField dataField)
    throws UnsupportedConversionException;
	
    public class UnsupportedConversionException extends Exception {
		private static final long serialVersionUID = 1663266551779148656L;
		public UnsupportedConversionException() { };
		public UnsupportedConversionException(Throwable t) { 
			super(t);
		};
	}
	

}
