package br.com.mvbos.nodethunder.core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Binary;
import javax.jcr.Property;
import javax.jcr.Value;

import org.apache.jackrabbit.value.BinaryValue;
import org.apache.jackrabbit.value.BooleanValue;
import org.apache.jackrabbit.value.DateValue;
import org.apache.jackrabbit.value.DecimalValue;
import org.apache.jackrabbit.value.DoubleValue;
import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.StringValue;

/**
 * 
 * @author Marcus Becker
 * 
 */

public class Util {

	public static Class<?> getGenericListType(Field field) {
		Type genericType = field.getGenericType();

		if (genericType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericType;

			return (Class<?>) parameterizedType.getActualTypeArguments()[0];
		}

		return null;
	}

	public static Object getIntOrLong(String value, Field field) {
		Class<?> type = field.getType();

		if (value == null || value.isEmpty() || value.equals("null")) {
			return null;
		}

		try {
			if (type.equals(Integer.class) || type.equals(int.class)) {
				return Integer.valueOf(value);
			} else {
				return Long.valueOf(value);
			}

		} catch (NumberFormatException n) {
			Exception e = new Exception("Error to converter " + field.getName()
					+ " for type " + type + " with value " + value, n);
			e.printStackTrace();
		}

		return null;
	}

	public static Object getFloatOrDouble(String value, Field field) {
		Class<?> type = field.getType();

		if (value == null || value.isEmpty() || value.equals("null")) {
			return null;
		}

		try {
			if (type.equals(Float.class) || type.equals(float.class)) {
				return Float.valueOf(value);
			} else {
				return Double.valueOf(value);
			}

		} catch (NumberFormatException n) {
			Exception e = new Exception("Erro to converter " + field.getName()
					+ " para tipo " + type + " com valor " + value, n);
			e.printStackTrace();
		}

		return null;
	}

	public static Value getConvertedValue(Class<?> type, Object value) {
		if (type == int.class || type == Integer.class || type == long.class
				|| type == Long.class) {

			return new LongValue(Long.parseLong(String.valueOf(value)));

		} else if (type == float.class || type == Float.class
				|| type == double.class || type == Double.class) {

			return new DoubleValue(Double.parseDouble(String.valueOf(value)));

		} else if (type == boolean.class || type == Boolean.class) {
			return new BooleanValue((Boolean) value);

		} else if (type == Date.class || type == Calendar.class) {
			Calendar c = null;
			if (type == Date.class) {
				c = Calendar.getInstance();
				c.setTime((Date) value);

			} else {
				c = (Calendar) value;
			}

			return new DateValue(c);

		} else if (type == BigDecimal.class) {
			return new DecimalValue((BigDecimal) value);

		} else if (type == Binary.class) {
			return new BinaryValue((Binary) value);

		} else if (type == byte[].class) {
			return new BinaryValue((byte[]) value);

		} else {
			return new StringValue(String.valueOf(value));
		}
	}

	public static Value[] getConvertedArrayValue(Class<?> type, Object[] value) {
		Value[] arr = new Value[value.length];
		
		int i = 0;
		if (type == String[].class) {
			for (Object o : value) {
				arr[i++] = new StringValue(String.valueOf(o));
			}			
			
		}else if (type == int[].class || type == Integer[].class || type == long[].class
				|| type == Long[].class) {
			
			for (Object o : value) {
				arr[i++] = new LongValue(Long.parseLong(String.valueOf(o)));
			}

		} else if (type == float[].class || type == Float[].class
				|| type == double[].class || type == Double[].class) {

			for (Object o : value) {
				arr[i++] = new DoubleValue(Double.parseDouble(String.valueOf(o)));
			}	

		} else if (type == boolean[].class || type == Boolean[].class) {
			for (Object o : value) {
				arr[i++] = new BooleanValue((Boolean) o);
			}				

		} else if (type == Date[].class || type == Calendar[].class) {
			for (Object o : value) {
							
				Calendar c = null;
				if (type == Date[].class) {
					c = Calendar.getInstance();
					c.setTime((Date) o);
	
				} else {
					c = (Calendar) o;
				}
				
				arr[i++] = new DateValue(c);
			}

		} else if (type == BigDecimal[].class) {
			for (Object o : value) {
				arr[i++] = new DecimalValue((BigDecimal) o);
			}				

		} else if (type == Binary[].class) {
			for (Object o : value) {
				arr[i++] = new BinaryValue((Binary) o);
			}			

		//TODO test
		/*} else if (type == byte[].class) {
			return new BinaryValue((byte[]) value);*/

		}
		
		return arr;
	}
	
	public static Object getConvertedValue(Field field, Property property) {
		Object valueReturn = null;

		Class<?> type = field.getType();

		try {
			if (type.equals(String.class)) {
				valueReturn = property.getString();

			} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
				valueReturn = property.getBoolean();

			} else if (type.equals(Integer.class) || type.equals(int.class)) {
				valueReturn = (int) property.getLong();

			} else if (type.equals(Long.class) || type.equals(long.class)) {
				valueReturn = property.getLong();

			} else if (type.equals(Float.class) || type.equals(float.class)) {
				valueReturn = (float) property.getDouble();

			} else if (type.equals(Double.class) || type.equals(double.class)) {
				valueReturn = property.getDouble();

			} else if (type.equals(Date.class) || type.equals(Calendar.class)) {
				valueReturn = type.equals(Calendar.class) ? property.getDate()
						: property.getDate().getTime();

			} else if (type.equals(BigDecimal.class)) {
				valueReturn = property.getDecimal();

			} else if (type.equals(Binary.class)) {
				valueReturn = property.getBinary();

			} else if (type.equals(byte[].class)) {
				byte[] arr = new byte[0];
				property.getBinary().read(arr, property.getBinary().getSize());

				valueReturn = arr;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return valueReturn;
	}
	
	public static Object getConvertedArrayValue(Field field, Property property) {
		Object[] valueReturn = null;
		
		Class<?> type = field.getType();
		
		try {
			
			Value[] values = property.getValues();
			
			if (values.length == 0) {
				return valueReturn;
			}
			 
			int i = 0;
			
			if (type.equals(String[].class)) {
				valueReturn = new String[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = v.getString();
				}					
				
				
			} else if (type.equals(Boolean[].class) || type.equals(boolean[].class)) {
				valueReturn = new Boolean[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = v.getBoolean();
				}					
				
			} else if (type.equals(Integer[].class) || type.equals(int[].class)) {
				valueReturn = new Integer[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = (int) v.getLong();
				}				
				
			} else if (type.equals(Long[].class) || type.equals(long[].class)) {
				valueReturn = new Long[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = v.getLong();
				}				
				
			} else if (type.equals(Float[].class) || type.equals(float[].class)) {
				valueReturn = new Float[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = (float) v.getDouble();
				}				
				
			} else if (type.equals(Double[].class) || type.equals(double[].class)) {
				valueReturn = new Double[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = v.getDouble();
				}				
				
			} else if (type.equals(Date[].class) || type.equals(Calendar[].class)) {
				valueReturn = type.equals(Calendar[].class) ? new Calendar[values.length]
						: new Date[values.length];
			
				for (Value v : values) {
					valueReturn[i++] = type.equals(Calendar[].class) ? v.getDate() : v.getDate().getTime();
				}				
				
			} else if (type.equals(BigDecimal[].class)) {
				valueReturn = new BigDecimal[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = v.getDecimal();
				}				
				
			} else if (type.equals(Binary[].class)) {
				valueReturn = new Binary[values.length];
				
				for (Value v : values) {
					valueReturn[i++] = v.getBinary();
				}				
				
			} //TODO execute tests
			/*else if (type.equals(byte[].class)) {
				byte[] arr = new byte[0];
				property.getBinary().read(arr, property.getBinary().getSize());
				
				valueReturn = arr;
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return valueReturn;
	}

}
