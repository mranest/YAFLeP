package com.github.yaflep.camel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.spi.DataFormat;

import com.github.yaflep.TypeConverter;
import com.github.yaflep.YAFLeP;

public class YAFLePDataFormat implements DataFormat {

	private Set<YAFLeP<?>> yafleps;
	
	public void setClasses(Class<?>[] classes) {
		yafleps = new HashSet<YAFLeP<?>>();
		
		for (Class<?> each: classes) {
			yafleps.add(YAFLeP.newInstance(each));
		}
	}
	
	public YAFLePDataFormat() {
	}
	
	public YAFLePDataFormat(Class<?>... classes) {
		setClasses(classes);
	}
	
	@Override
	public void marshal(Exchange exchange, Object graph, OutputStream stream)
	throws Exception {
		throw new UnsupportedOperationException("marshal not yet implemented");
	}

	@Override
	public Object unmarshal(final Exchange exchange, InputStream stream)
	throws Exception {
		if (yafleps == null) {
			return null;
		}
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(stream));

		String line = null;
		Object singleResponse = null;
		List<Object> multipleResponses = null;
		while ((line = br.readLine()) != null) {
			Object newResponse = null;
			for (YAFLeP<?> each: yafleps) {
				if (each.matches(line)) {
					newResponse = each.unmarshall(line, new TypeConverter() {
						@Override
						public <T> T convertTo(Class<T> type, Object value)
						throws UnsupportedConversionException {
							try {
								return exchange.getContext().getTypeConverter().mandatoryConvertTo(type, value);
							} catch (NoTypeConversionAvailableException e) {
								throw new UnsupportedConversionException(e);
							}
						}
					});
				}
			}
			
			if (newResponse == null) {
				continue;
			}
			
			if (singleResponse == null) {
				singleResponse = newResponse;
			} else if (multipleResponses == null) {
				multipleResponses = new LinkedList<Object>();
				
				multipleResponses.add(singleResponse);
				multipleResponses.add(newResponse);
			} else {
				multipleResponses.add(newResponse);
			}
		}
		
		return multipleResponses == null ? singleResponse : multipleResponses;
	}

}
