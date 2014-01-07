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

}
