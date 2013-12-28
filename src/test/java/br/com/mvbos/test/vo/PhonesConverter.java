package br.com.mvbos.test.vo;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import br.com.mvbos.nodethunder.core.IConverter;

public class PhonesConverter implements IConverter<String, List<String>> {

	private static final String SEPARATOR = ";";

	public String toNode(Object source) throws IllegalArgumentException {
		if (source != null) {
			@SuppressWarnings("unchecked")
			List<String> lst = (List<String>) source;

			String concat = "";

			for (String s : lst) {
				concat += s.concat(SEPARATOR);
			}

			return concat;
		}

		return null;
	}

	public List<String> toClass(Property property) throws ValueFormatException,
			RepositoryException {

		String val = property.getValue().getString();

		if (val != null && !val.isEmpty()) {
			String[] arr = val.split(SEPARATOR);
			
			return Arrays.asList(arr);
		}

		return null;
	}

}
