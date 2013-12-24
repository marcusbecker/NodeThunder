package br.com.mvbos.nodethunder.core;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;


/**
 * 
 * @author Marcus Becker
 * 
 */
public interface IConverter<T,V> {

	/**
	 * Convert object in a node acceptable value  
	 * @param source
	 * @return JCR property value
	 * @throws IllegalArgumentException
	 */
	public T toNode(Object source) throws IllegalArgumentException;

	/**
	 * Convert the node property in a Java object value
	 * @param property
	 * @return Java Object
	 * @throws ValueFormatException
	 * @throws RepositoryException
	 */
	public V toClass(Property property) throws ValueFormatException,
			RepositoryException;

}
