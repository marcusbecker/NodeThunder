package br.com.mvbos.nodethunder.core;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

public class BlankConvert implements IConverter<Object, Object>{

	public Object toNode(Object source) throws IllegalArgumentException {
		return null;
	}

	public Object toClass(Property property) throws ValueFormatException,
			RepositoryException {
		return null;
	}

}
