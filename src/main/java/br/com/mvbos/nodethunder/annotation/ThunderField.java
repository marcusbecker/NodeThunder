package br.com.mvbos.nodethunder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.mvbos.nodethunder.core.IConverter;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * Anota&ccedil;&atilde;o para configura&ccedil;&atilde;o dos atributos. 
 * @author Marcus Becker
 *
 */
public @interface ThunderField {

	/**
	 * Nome da propriedade do N&oacute;. Caso nenhum valor seja setado,
	 * ser&aacute; utilizado o nome do atributo da classe.
	 */
	String name() default "";

	/**
	 * Por padr&atilde;o, objetos do tipo <code>List</code> n&atilde;o
	 * s&atilde;o carregados automaticamente durante o parse. Caso seja setado
	 * como <tt>false</tt>, a listagem ser&aacute; carregada durante o parse do
	 * objeto.
	 * 
	 */
	boolean lazy() default true;

	/**
	 * Por padr&atilde;o, objetos dentro de uma <code>List</code> n&atilde;o
	 * s&atilde;o salvos automaticamente. Caso seja setado como <tt>true</tt>,
	 * todos os objetos dentro da lista, ser&atilde;o persistidos como
	 * n&oacute;.
	 * 
	 */
	boolean cascade() default false;

	/**
	 * Por padr&atilde;o, objetos dentro de uma <code>List</code> s&atilde;o
	 * salvos abaixo do n&oacute; pai. 
	 * Utilize este atributo para especificar o n&oacute; <b>abaixo</b> do n&oacute; pai.  
	 * 
	 */
	String childPath() default "";

	/**
	 * Caso seja setado como <tt>true</tt>, durante o parse, o nome do n&oacute;
	 * ser&aacute; setado neste atributo.
	 * 
	 */
	boolean setPropertyName() default false;

	/**
	 * Caso seja setado como <tt>true</tt>, durante o parse, os valores do
	 * Mixintype do n&oacute; ser&aacute; setados neste atributo, caso o
	 * atributo seja do tipo String ou String[]
	 * 
	 */
	boolean setMixinType() default false;

	/**
	 * Objeto utilizado para convers&atilde;o do objeto da classe para o
	 * n&oacute; (<tt>toNode</tt>) e do n&oacute; para classe (<tt>toClass</tt>
	 * ).
	 * 
	 */
	Class<? extends IConverter> converter() default IConverter.class;

}