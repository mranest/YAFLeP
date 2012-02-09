package com.github.yaflep;

public interface TypeConverter {

    <T> T convertTo(Class<T> type, Object value)
    throws UnsupportedConversionException;
	
    public class UnsupportedConversionException extends Exception {
		private static final long serialVersionUID = 1663266551779148656L;
		public UnsupportedConversionException() { };
		public UnsupportedConversionException(Throwable t) { 
			super(t);
		};
	}
	

}
