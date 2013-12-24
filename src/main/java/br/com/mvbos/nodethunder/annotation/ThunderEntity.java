package br.com.mvbos.nodethunder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Anota&ccedil;&atilde;o para configura&ccedil;&atilde;o da classe. 
 * @author Marcus Becker
 *
 */
public @interface ThunderEntity {

	/**
	 * Nome utilizado para salvar o N&oacute;. Caso o nome do n&oacute; seja
	 * din&acirc;mico (valor de um atributo) utilize:
	 * {@link br.com.fiat.agenciaclick.annotation.propertyName propertyName}
	 * 
	 * Caso o valor n&atilde;o seja setado, ser&aacute; utilizado o nome simples
	 * da classe.
	 * */
	String name() default "";

	/**
	 * Nome utilizado para salvar o Nó, que será o valor da propriedade setada
	 * no atributo da classe.
	 * 
	 * Caso o valor n&atilde;o seja setado, ser&aacute; utilizado o nome simples
	 * da classe.
	 */
	String propertyName() default "";

	/**
	 * Valor utilizado como Mixintype do N&oacute;. Observa&ccedil;&atilde;o:
	 * Caso o Mixintype n&atilde;o exista ocorrer&aacute; uma
	 * {@link javax.jcr.NamespaceException NamespaceException}
	 */
	String propertyType() default "";
}
