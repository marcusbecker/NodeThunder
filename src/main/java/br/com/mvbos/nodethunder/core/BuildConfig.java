package br.com.mvbos.nodethunder.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Marcus Becker
 *
 */

public class BuildConfig {

	private Map<String, Boolean> map;

	public BuildConfig() {
		map = new HashMap<String, Boolean>(2);
	}

	/**
	 * Nome do campo do atributo da classe para ser incluido, sobrescrevendo a
	 * configuracao da anotacao.
	 * 
	 * @param fieldName
	 * @return
	 */

	public BuildConfig include(String fieldName) {
		map.put(fieldName, Boolean.TRUE);
		return this;
	}

	/**
	 * Nome do campo do atributo da classe para ser excluido, sobrescrevendo a
	 * configuracao da anotacao.
	 * 
	 * @param fieldName
	 * @return
	 */
	public BuildConfig exclude(String fieldName) {
		map.put(fieldName, Boolean.FALSE);
		return this;
	}

	/**
	 * Verifica se o campo ja foi adicionado para ser incluido.
	 * 
	 * @param fieldName
	 * @return
	 */
	public boolean haveIncludeField(String fieldName) {
		return map.containsKey(fieldName) && map.get(fieldName);
	}

	/**
	 * Verifica se o campo ja foi adicionado para ser excluido.
	 * 
	 * @param fieldName
	 * @return
	 */
	public boolean haveExcludeField(String fieldName) {
		return map.containsKey(fieldName) && !map.get(fieldName);
	}

	/**
	 * Limpa os campos adicionados.
	 */
	public void clean() {
		map.clear();
	}
}
