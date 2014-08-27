package br.com.mvbos.nodethunder.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import br.com.mvbos.nodethunder.annotation.ThunderEntity;
import br.com.mvbos.nodethunder.annotation.ThunderField;

/**
 * 
 * @author Marcus Becker
 * 
 */

public class NodeThunder {

	private static final String JCR_SQL2 = Query.JCR_SQL2;
	
	private static final String CHILD_PATH_SLASH = "/";

	private static final String BT_NODE_TYPE = NodeThunder.class
			.getSimpleName() + "Type";

	private static final boolean useGenericType = false;

	private boolean updateMode = false;

	private boolean inTransaction = false;

	public enum AnnotationMode {
		ON, OFF, ONLY_FIELD, ONLY_ENTITY
	}

	public enum NodeTypeValidation {
		VALID, INVALID, NOT_FOUND
	}

	private Session session;
	private Node root;
	private AnnotationMode mode = AnnotationMode.ON;
	private boolean subscribe = false;
	private boolean ignoreStaticField = true;
	private boolean autoSetCustomProperty = false;

	private BuildConfig buildConfig = new BuildConfig();

	private NodeThunder(Session session, Node root) {
		this.session = session;
		this.root = root;
	}

	public static NodeThunder create(Session session, Node root) {
		NodeThunder bt = new NodeThunder(session, root);
		return bt;
	}

	public static NodeThunder create(Session session) {
		NodeThunder bt = new NodeThunder(session, null);
		return bt;
	}

	public static NodeThunder createEmpty() {
		NodeThunder bt = new NodeThunder(null, null);
		return bt;
	}

	public void update(Object vo) throws Exception {
		updateMode = true;
		save(vo);
	}

	public Node save(Object vo) throws Exception {

		String entityName = getEntityName(vo);

		return save(entityName, vo);
	}

	public Node save(String nodeName, Object vo) throws Exception {

		if (vo == null || empty(nodeName)) {
			throw new IllegalArgumentException(
					"'VO class' and 'Node Name' are expected. Recivied: " + vo
							+ " - " + nodeName);
		}

		Node node;

		if (root.getName().equals(nodeName)) {
			node = root;

		} else if (root.hasNode(nodeName)) {
			node = root.getNode(nodeName);

		} else {
			node = root.addNode(nodeName);
		}

		createNode(vo, node);

		if (!inTransaction) {
			session.save();
		}

		return node;
	}

	public <T> T populeByField(String fieldName, String value, Class<T> vo)
			throws Exception {

		T newVo = null;

		if (fieldName == null || value == null) {
			throw new IllegalArgumentException(
					"'fieldName' e 'value' devem ser preenchidos.");
		}

		NodeIterator it = root.getNodes();

		while (it.hasNext()) {

			Node node = it.nextNode();

			if (node.hasProperty(fieldName)
					&& node.getProperty(fieldName).getString().equals(value)) {

				newVo = popule(node, vo);

				break;
			}
		}

		return newVo;
	}

	public <T> T popule(Class<T> vo) throws Exception {
		return popule(root, vo);
	}

	public <T> List<T> list(Class<T> vo) throws Exception {
		return list(root, vo, false);
	}

	public <T> List<T> list(Node root, Class<T> vo) throws Exception {
		return list(root, vo, false);
	}

	public <T> T popule(Node node, Class<T> vo) throws Exception {
		T newVo = vo.newInstance();

		validateClassPropertyValue(vo, node);

		for (Field field : vo.getDeclaredFields()) {

			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}

			if (ignoreStaticField && Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			boolean lazy = false;

			boolean isPropertyName = false;

			IConverter<?, ?> converter = null;

			boolean isMixinType = false;

			String fieldName = field.getName();
			
			String childPath = "";

			if (field.isAnnotationPresent(ThunderField.class)) {
				ThunderField annotation = field
						.getAnnotation(ThunderField.class);

				lazy = annotation.lazy();

				isPropertyName = annotation.setPropertyName();

				isMixinType = annotation.setMixinType();
				
				childPath = annotation.childPath();

				if (annotation.converter() != BlankConvert.class) {
					converter = annotation.converter().newInstance();
				}

				// Verify if use the name of Field or Annotation to value
				// key;
				if ((mode == AnnotationMode.ON || mode == AnnotationMode.ONLY_FIELD)) {
					fieldName = notEmpty(annotation.name()) ? annotation.name()
							: fieldName;
				}

			}

			if (notEmpty(fieldName)) {

				if (buildConfig.haveExcludeField(fieldName)) {
					continue;
				}

				field.setAccessible(true);

				if (isPropertyName && field.getType().equals(String.class)) {
					field.set(newVo, node.getName());

				} else if (converter != null && node.hasProperty(fieldName)) {
					Object objReturn = converter.toClass(node
							.getProperty(fieldName));

					field.set(newVo, objReturn);

				} else if (isMixinType) {
					NodeType[] mixinNodeTypes = node.getMixinNodeTypes();

					String[] arr = new String[mixinNodeTypes.length];

					for (int i = 0; i < mixinNodeTypes.length; i++) {
						NodeType nodeType = mixinNodeTypes[i];
						arr[i] = nodeType.getName();
					}

					if (field.getType().equals(String.class)) {
						field.set(newVo, arr.toString());

					} else if (field.getType().equals(String[].class)) {
						field.set(newVo, arr);
					}

				} else if (isSimpleField(field) && node.hasProperty(fieldName)) {

					if (useGenericType) {
						String nodeValue = node.getProperty(fieldName)
								.getString();

						if (empty(nodeValue)) {
							continue;
						}

						field.set(newVo, getConvertedValue(field, nodeValue));

					} else {
						Object nodeValue = Util.getConvertedValue(field,
								node.getProperty(fieldName));
						
						field.set(newVo, nodeValue);
					}
					
				} else if (isArrayField(field) && node.hasProperty(fieldName)) {

					if (!useGenericType) {
						Object nodeValue = Util.getConvertedArrayValue(field, node.getProperty(fieldName));
						field.set(newVo, nodeValue);
					}
					
				} else if ((!lazy || buildConfig.haveIncludeField(fieldName))
						&& isListField(field)) {

					NodeIterator subIt = node.getNodes();

					List<Object> lstReturn = null;

					Class<?> listType = Util.getGenericListType(field);

					String propertyType = getPropertyType(listType);

					while (subIt.hasNext()) {
						Node subNode = subIt.nextNode();

						if (field.getName().equals(subNode.getName()) || field.getName().equals(childPath) ) {

							NodeIterator internal = subNode.getNodes();

							while (internal.hasNext()) {
								Node nInternal = internal.nextNode();

								if (lstReturn == null) {
									lstReturn = new ArrayList<Object>(10);
								}

								Object objReturn = popule(nInternal, listType);

								lstReturn.add(objReturn);
							}

						} else if (filterByMixinType(propertyType, subNode)
								|| (autoSetCustomProperty && filterByClassType(
										subNode, listType) == NodeTypeValidation.INVALID)) {

							if (lstReturn == null) {
								lstReturn = new ArrayList<Object>(10);
							}

							Object objReturn = popule(subNode, listType);

							lstReturn.add(objReturn);
							
						//} else if (listType.isAnnotationPresent(ThunderEntity.class)) {
						} else if (CHILD_PATH_SLASH.equals(childPath)) {
							//TODO teste
							if (lstReturn == null) {
								lstReturn = new ArrayList<Object>(10);
							}

							Object objReturn = popule(subNode, listType);

							lstReturn.add(objReturn);
						}

					}

					field.set(newVo, lstReturn);
				}
			}
		}// end for

		return newVo;
	}

	private String getPropertyType(Class<?> vo) {
		String propertyType = null;

		if (vo.isAnnotationPresent(ThunderEntity.class)) {
			ThunderEntity annotation = vo.getAnnotation(ThunderEntity.class);

			if (notEmpty(annotation.propertyType())) {
				propertyType = annotation.propertyType();
			}

		}
		return propertyType;
	}

	private boolean filterByMixinType(String propertyType, Node node) {

		if (!empty(propertyType)) {
			try {
				NodeType[] mixinNodeTypes = node.getMixinNodeTypes();

				for (NodeType type : mixinNodeTypes) {
					if (propertyType.equals(type.getName())) {
						return true;
					}
				}

			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * 
	 * @param root
	 * @param vo
	 * @param filterByNodeType
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> list(Node root, Class<T> vo, boolean filterByNodeType)
			throws Exception {

		if (root == null) {
			System.err.println("Error: No node was selected.");

			return null;
		}

		NodeIterator it = root.getNodes();

		return list(it, vo, filterByNodeType);
	}

	/**
	 * 
	 * @param root
	 * @param vo
	 * @param filterByNodeType
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> list(NodeIterator it, Class<T> vo,
			boolean filterByNodeType) throws Exception {

		List<T> lst = new ArrayList<T>((int) it.getSize());

		while (it.hasNext()) {

			Node node = it.nextNode();

			if (filterByNodeType
					&& filterByClassType(node, vo) != NodeTypeValidation.VALID) {

				continue;
			}

			if (isValidMixinType(node, vo) == NodeTypeValidation.INVALID) {
				continue;
			}

			T newVo = popule(node, vo);

			lst.add(newVo);

		}

		return lst;
	}

	public <T> T query(String sql, Class<T> vo) throws Exception {
		List<T> lst = queryList(sql, vo);

		if (lst != null && !lst.isEmpty()) {
			return lst.get(0);
		}

		return null;
	}

	public <T> List<T> queryList(String sql, Class<T> vo) throws Exception {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(sql, JCR_SQL2);

		QueryResult result = query.execute();

		return list(result.getNodes(), vo, false);
	}

	public <T> List<T> queryList(String sql, Class<T> vo, String... params)
			throws Exception {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		Query query = queryManager.createQuery(sql, JCR_SQL2);

		QueryResult result = query.execute();

		return list(result.getNodes(), vo, false);
	}

	private NodeTypeValidation isValidMixinType(Node node, Class<?> type)
			throws RepositoryException {
		String propertyType = getPropertyType(type);

		if (!empty(propertyType)) {
			NodeType[] types = node.getMixinNodeTypes();

			for (NodeType nodeType : types) {
				if (propertyType.equals(nodeType.getName())) {
					return NodeTypeValidation.VALID;
				}
			}

			return NodeTypeValidation.INVALID;
		}

		return NodeTypeValidation.NOT_FOUND;
	}

	private NodeTypeValidation filterByClassType(Node node, Class<?> type)
			throws RepositoryException {

		if (node.hasProperty(BT_NODE_TYPE)) {
			String className = node.getProperty(BT_NODE_TYPE).getString();

			return notEmpty(className)
					&& className.equals(type.getSimpleName()) ? NodeTypeValidation.VALID
					: NodeTypeValidation.INVALID;

		}

		return NodeTypeValidation.NOT_FOUND;
	}

	private void validateClassPropertyValue(Class<?> vo, Node node)
			throws RepositoryException {

		if (filterByClassType(node, vo) == NodeTypeValidation.INVALID) {

			String className = node.getProperty(BT_NODE_TYPE).getString();

			throw new IllegalArgumentException("The class '"
					+ vo.getSimpleName()
					+ "' is not compatible with declared class '" + className
					+ "' ");

		}
	}

	private boolean isListField(Field field) {
		if (field.getType().equals(List.class)) {
			return true;
		}

		return false;
	}

	public static boolean isSimpleField(Field field) {

		Class<?> type = field.getType();

		if (type.equals(String.class) || type.equals(Boolean.class)
				|| type.equals(boolean.class) || type.equals(Integer.class)
				|| type.equals(int.class) || type.equals(Long.class)
				|| type.equals(long.class) || type.equals(Float.class)
				|| type.equals(float.class) || type.equals(Double.class)
				|| type.equals(double.class) || type.equals(Date.class)
				|| type.equals(Calendar.class) || type.equals(BigDecimal.class)
				|| type.equals(Binary.class) || type.equals(byte[].class)) {

			return true;

		}

		return false;
	}

	public static boolean isArrayField(Field field) {
		
		Class<?> type = field.getType();
		
		if (type.equals(String[].class) || type.equals(Boolean[].class)
				|| type.equals(boolean[].class) || type.equals(Integer[].class)
				|| type.equals(int[].class) || type.equals(Long[].class)
				|| type.equals(long[].class) || type.equals(Float[].class)
				|| type.equals(float[].class) || type.equals(Double[].class)
				|| type.equals(double[].class) || type.equals(Date[].class)
				|| type.equals(Calendar[].class) || type.equals(BigDecimal[].class)
				|| type.equals(Binary[].class) || type.equals(byte[].class)) {
			
			return true;
			
		}
		
		return false;
	}
	
	public static Object getConvertedValue(Field field, String value) {
		Object valueReturn = value;

		Class<?> type = field.getType();

		if (type.equals(String.class)) {
			valueReturn = value;

		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			valueReturn = Boolean.parseBoolean(value);

		} else if (type.equals(Integer.class) || type.equals(int.class)
				|| type.equals(Long.class) || type.equals(long.class)) {

			valueReturn = Util.getIntOrLong(value, field);

		} else if (type.equals(Float.class) || type.equals(float.class)
				|| type.equals(Double.class) || type.equals(double.class)) {

			valueReturn = Util.getFloatOrDouble(value, field);

		}

		return valueReturn;
	}
	

	private void createNode(Object vo, Node node) throws Exception {

		Class<?> c = vo.getClass();

		if (autoSetCustomProperty) {
			node.setProperty(BT_NODE_TYPE, c.getSimpleName());
		}

		String propertyType = getPropertyType(c);

		if (notEmpty(propertyType) && node.canAddMixin(propertyType)) {
			node.addMixin(propertyType);
		}

		for (Field field : c.getDeclaredFields()) {

			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}

			if (ignoreStaticField && Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			boolean cascade = false;

			boolean isPropertyName = false;

			IConverter<?, ?> converter = null;

			String key = field.getName();

			Object value = null;

			Class<?> type = field.getType();

			String childPath = "";

			if (mode == AnnotationMode.ON || mode == AnnotationMode.ONLY_FIELD) {
				if (!field.isAnnotationPresent(ThunderField.class)) {
					continue;
				}
			}

			if (field.isAnnotationPresent(ThunderField.class)) {
				ThunderField annotation = field
						.getAnnotation(ThunderField.class);

				cascade = annotation.cascade();

				isPropertyName = annotation.setPropertyName();

				childPath = annotation.childPath();

				if (annotation.converter() != BlankConvert.class) {
					converter = annotation.converter().newInstance();
				}

				// Verify if use the name of Field or Annotation to value key;
				if ((mode == AnnotationMode.ON || mode == AnnotationMode.ONLY_FIELD)) {
					key = notEmpty(annotation.name()) ? annotation.name() : key;
				}

			}

			field.setAccessible(true);

			if (isPropertyName) {
				// ignore property name

			} else if (converter != null) {
				Object objReturn = converter.toNode(field.get(vo));

				if (objReturn != null) {
					type = objReturn.getClass();
					value = objReturn;
				}

			} else if (cascade && field.getType().equals(List.class)) {

				Class<?> sub = Util.getGenericListType(field);

				if (sub != null && field.get(vo) != null) {

					List<?> list = (List<?>) field.get(vo);

					Node childNode = node;

					if (notEmpty(childPath) && !childPath.equals(CHILD_PATH_SLASH)) {
						if (node.hasNode(childPath)) {
							childNode = node.getNode(childPath);
						} else {
							childNode = node.addNode(childPath);
						}
					}

					for (Object obj : list) {

						Node subNode;

						if (updateMode && childNode.hasNode(getEntityName(obj))) {
							subNode = childNode.getNode(getEntityName(obj));

						} else {
							subNode = childNode.addNode(getEntityName(obj));
						}

						createNode(obj, subNode);
					}

				}

			} else if (!cascade && field.getType().equals(List.class)) {
				continue;

			} else {
				value = field.get(vo);
			}

			if (notEmpty(key) && value != null) {
				if (useGenericType) {
					node.setProperty(key, String.valueOf(value));

				} else if (converter == null && isSimpleField(field) || isArrayField(field)) {
					if (isSimpleField(field)) {
						node.setProperty(key, Util.getConvertedValue(type, value));

					} else if (isArrayField(field)) {
						node.setProperty(key, Util.getConvertedArrayValue(type, value));
					}

				} else {
					node.setProperty(key, Util.getConvertedValue(type, value));
				}

			} else if (subscribe) {
				node.setProperty(key, "");
			}

		}

	}

	private String getEntityName(Object vo) throws NoSuchFieldException,
			IllegalAccessException {

		Class<?> c = vo.getClass();

		String entityName = c.getSimpleName();

		if (mode == AnnotationMode.ON || mode == AnnotationMode.ONLY_ENTITY) {
			if (c.isAnnotationPresent(ThunderEntity.class)) {
				ThunderEntity ann = c.getAnnotation(ThunderEntity.class);

				if (notEmpty(ann.name())) {
					entityName = ann.name();

				} else if (notEmpty(ann.propertyName())) {
					Field f = c.getDeclaredField(ann.propertyName().trim());

					f.setAccessible(true);

					entityName = String.valueOf(f.get(vo));

					if (empty(entityName)) {
						throw new NoSuchFieldException(
								"Incorrect propertyName value.");
					}
				}
			}
		}

		return entityName;
	}

	private boolean empty(String name) {
		return !notEmpty(name);
	}

	private boolean notEmpty(String name) {
		return name != null && !name.trim().isEmpty();
	}

	/**
	 * Por padr&atilde;o o parse &eacute; feito atrav&eacute;s de classes e
	 * atributos anotados. Para ignorar anota&ccedil;&otilde;es de classe
	 * utilize:<tt>AnnotationMode.ONLY_FIELD</tt>. Para ignorar
	 * anota&ccedil;&otilde;es de atributos utilize:
	 * <tt>AnnotationMode.ONLY_ENTITY</tt>. Para ignorar todas as
	 * anota&ccedil;&otilde;es utilize: <tt>AnnotationMode.OFF</tt>.
	 * 
	 * @param mode
	 * @return
	 */
	public NodeThunder setAnnotationMode(AnnotationMode mode) {
		this.mode = mode;

		return this;

	}

	/**
	 * Seta o nome da classe no atributo do n&oacute;.
	 * 
	 * @param autoSetCustomProperty
	 */

	public void setAutoSetCustomProperty(boolean autoSetCustomProperty) {
		this.autoSetCustomProperty = autoSetCustomProperty;
	}

	public BuildConfig getBuildConfig() {
		return buildConfig;
	}

	@Override
	public String toString() {
		return "BlackThunder [session=" + session + ", root=" + root
				+ ", mode=" + mode + ", subscribe=" + subscribe
				+ ", autoSetCustomProperty=" + autoSetCustomProperty + "]";
	}

	public NodeThunder relocate(Node noRel) {
		this.root = noRel;

		return this;
	}

	public void beginTransaction() throws Exception {
		inTransaction = true;
	}

	public void commitTransaction() throws Exception {
		session.save();
		inTransaction = false;
	}
}