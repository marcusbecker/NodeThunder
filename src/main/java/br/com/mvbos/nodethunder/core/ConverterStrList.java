package br.com.mvbos.nodethunder.core;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

/**
 * 
 * @author Marcus Becker
 * 
 */
public class ConverterStrList implements IConverter<String, List<String>> {

	private static final String SEPARATOR = ";";

	public ConverterStrList() {
		super();
	}

	/**
	 * Expected a list of strings
	 * 
	 * @param source
	 * @return values separated by ;
	 */
	public String toNode(Object source) {

		if (source != null) {
			try {
				@SuppressWarnings("unchecked")
				List<String> lst = (List<String>) source;

				StringBuilder arr = new StringBuilder(100);

				for (String string : lst) {
					if (string != null && !string.isEmpty()) {
						arr.append(string).append(SEPARATOR);
					}
				}

				return arr.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return source.toString();
		}

		return null;
	}

	/**
	 * Expected a JCR node property. 
	 * Example: string;string;
	 * @param source
	 * @return a list of strings
	 */
	public List<String> toClass(Property source) throws ValueFormatException,
			RepositoryException {
		List<String> lst = null;

		if (source == null || source.getString() == null
				|| !source.getString().contains(SEPARATOR)) {

			return null;
		}

		try {
			String[] arr = source.getString().split(SEPARATOR);

			lst = new ArrayList<String>(arr.length);

			for (int i = 0; i < arr.length; i++) {
				lst.add(arr[i]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lst;
	}

}
