package com.seeyon.v3x.common.ajax.impl;

import static com.seeyon.v3x.util.Datetimes.dateStyle;
import static com.seeyon.v3x.util.Datetimes.datetimeStyle;
import static com.seeyon.v3x.util.Datetimes.datetimeWithoutSecondStyle;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.ajax.AJAXException;
import com.seeyon.v3x.common.ajax.AJAXParameter;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * AJAX Seriver parameter
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-22
 */
public class AJAXParameterImpl implements AJAXParameter {
	protected static final Log log = LogFactory.getLog(AJAXParameterImpl.class);

	/**
	 * ��������
	 */
	private Class className;

	/**
	 * ����ֵ
	 */
	private Object value;

	public AJAXParameterImpl(String typeName, String paramValue, String[] paramArrayValue, boolean isNeedEncoder)
			throws AJAXException {
		if (Strings.isBlank(typeName)) {
			throw new AJAXException("Parameter's Type must be not NULL.");
		}

		//****************** String 系列
		if (typeName.equalsIgnoreCase("String")) {
			className = String.class;
			if(isNeedEncoder){
				try {
					value = new String(paramValue.getBytes("8859_1"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					log.warn("文字转码失败", e);
				}
			}
			else{
				value = paramValue;
			}
		}
		else if (typeName.equalsIgnoreCase("String[]")) {
			className = String[].class;
			if(isNeedEncoder){
				String[] _value = new String[paramArrayValue.length];
				for (int i = 0; i < paramArrayValue.length; i++) {
					try {
						_value[i] = new String(paramArrayValue[i].getBytes("8859_1"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.warn("文字转码失败", e);
					}
				}
				
				value = _value;
			}
			else{
				value = paramArrayValue;
			}
		}
		//****************** long 系列
		else if (typeName.equals("long")) {
			className = long.class;
			value = new Long(paramValue).longValue();
		}
		else if (typeName.equals("long[]")) {
			className = long[].class;
			long[] _value = new long[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Long(paramArrayValue[i]).longValue();
			}
			
			value = _value;
		}
		else if (typeName.equals("Long")) {
			className = Long.class;
			value = StringUtils.isNotBlank(paramValue) ? new Long(paramValue) : null;
		}
		else if (typeName.equals("Long[]")) {
			className = Long[].class;
			Long[] _value = new Long[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Long(paramArrayValue[i]);
			}
			
			value = _value;
		}
		//****************** int 系列
		else if (typeName.equals("int")) {
			className = int.class;
			value = new Integer(paramValue).intValue();
		}
		else if (typeName.equals("int[]")) {
			className = int[].class;
			int[] _value = new int[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Integer(paramArrayValue[i]).intValue();
			}
			
			value = _value;
		}
		else if (typeName.equals("Integer")) {
			className = Integer.class;
			value = StringUtils.isNotBlank(paramValue) ? new Integer(paramValue) : null;
		}
		else if (typeName.equals("Integer[]")) {
			className = Integer[].class;
			Integer[] _value = new Integer[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Integer(paramArrayValue[i]);
			}
			
			value = _value;
		}
		//****************** boolean 系列
		else if (typeName.equals("boolean")) {
			className = boolean.class;
			value = new Boolean(paramValue).booleanValue();
		}
		else if (typeName.equals("boolean[]")) {
			className = boolean[].class;
			boolean[] _value = new boolean[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Boolean(paramArrayValue[i]).booleanValue();
			}
			
			value = _value;
		}
		else if (typeName.equals("Boolean")) {
			className = Boolean.class;
			value = StringUtils.isNotBlank(paramValue) ? new Boolean(paramValue) : null;
		}
		else if (typeName.equals("Boolean[]")) {
			className = Boolean[].class;
			Boolean[] _value = new Boolean[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Boolean(paramArrayValue[i]);
			}
			
			value = _value;
		}
		
		//****************** byte 系列
		else if (typeName.equals("byte")) {
			className = byte.class;
			value = new Byte(paramValue).byteValue();
		}
		else if (typeName.equals("byte[]")) {
			className = byte[].class;
			byte[] _value = new byte[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Byte(paramArrayValue[i]).byteValue();
			}
			
			value = _value;
		}
		else if (typeName.equals("Byte")) {
			className = Byte.class;
			value = StringUtils.isNotBlank(paramValue) ? new Byte(paramValue) : null;
		}
		else if (typeName.equals("Byte[]")) {
			className = Byte[].class;
			Byte[] _value = new Byte[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Byte(paramArrayValue[i]);
			}
			
			value = _value;
		}
		//****************** short 系列
		else if (typeName.equals("short")) {
			className = short.class;
			value = new Short(paramValue).shortValue();
		}
		else if (typeName.equals("short[]")) {
			className = short[].class;
			short[] _value = new short[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Short(paramArrayValue[i]).shortValue();
			}
			
			value = _value;
		}
		else if (typeName.equals("Short")) {
			className = Short.class;
			value = StringUtils.isNotBlank(paramValue) ? new Short(paramValue) : null;
		}
		else if (typeName.equals("Short[]")) {
			className = Short[].class;
			Short[] _value = new Short[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Short(paramArrayValue[i]);
			}
			
			value = _value;
		}

		//****************** double 系列
		else if (typeName.equals("double")) {
			className = double.class;
			value = new Double(paramValue).doubleValue();
		}
		else if (typeName.equals("double[]")) {
			className = Byte[].class;
			double[] _value = new double[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Double(paramArrayValue[i]).doubleValue();
			}
			
			value = _value;
		}
		else if (typeName.equals("Double")) {
			className = Double.class;
			value = StringUtils.isNotBlank(paramValue) ? new Double(paramValue) : null;
		}
		else if (typeName.equals("Double[]")) {
			className = Double[].class;
			Double[] _value = new Double[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Double(paramArrayValue[i]);
			}
			
			value = _value;
		}
		//****************** float 系列
		else if (typeName.equals("float")) {
			className = float.class;
			value = new Float(paramValue).floatValue();
		}
		else if (typeName.equals("float[]")) {
			className = Double[].class;
			float[] _value = new float[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Float(paramArrayValue[i]).floatValue();
			}
			
			value = _value;
		}
		else if (typeName.equals("Float")) {
			className = Float.class;
			value = StringUtils.isNotBlank(paramValue) ? new Float(paramValue) : null;
		}
		else if (typeName.equals("Float[]")) {
			className = Float[].class;
			Float[] _value = new Float[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = new Float(paramArrayValue[i]);
			}
			
			value = _value;
		}
		//****************** char 系列 
		else if (typeName.equals("char")) {
			className = char.class;
			if (paramValue.equals("")) {
				value = new Character('\u0000');
			}
			else {
				value = new Character(paramValue.charAt(0)).charValue();
			}
		}
		else if (typeName.equals("char[]")) {
			className = char[].class;
			char[] _value = new char[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				if (paramValue.equals("")) {
					_value[i] = new Character('\u0000');
				}
				else {
					_value[i] = new Character(paramArrayValue[i].charAt(0)).charValue();
				}
			}
			
			value = _value;
		}
		else if (typeName.equals("Character")) {
			className = Character.class;
			if (paramValue.equals("")) {
				value = new Character('\u0000');
			}
			else {
				value = new Character(paramValue.charAt(0));
			}
		}
		else if (typeName.equals("Character[]")) {
			className = Character[].class;
			Character[] _value = new Character[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				if (paramValue.equals("")) {
					_value[i] = new Character('\u0000');
				}
				else {
					_value[i] = new Character(paramArrayValue[i].charAt(0));
				}
			}
			
			value = _value;
		}
		//****************** date系列
		else if (typeName.equalsIgnoreCase("DATE")) {
			className = Date.class;
			value = parseDate(paramValue);

		}
		else if (typeName.equalsIgnoreCase("DATE[]")) {
			className = Date[].class;
			
			Date[] _value = new Date[paramArrayValue.length];
			for (int i = 0; i < paramArrayValue.length; i++) {
				_value[i] = parseDate(paramValue);
			}
			
			value = _value;
		}
		else {
			throw new AJAXException("Parameter type '" + typeName + "' is invalidity.");
		}
	}
	
	private static Date parseDate(String paramValue){
		Date date = null;
		if(StringUtils.isNumeric(paramValue)){
			try {
				long minute = Long.parseLong(paramValue);
				date = new Date(minute);
			}
			catch (Exception e) {
			}
		}
		else if (StringUtils.isNotBlank(paramValue)) {
			try {
				date = Datetimes.parse(paramValue);
			}
			catch (Exception e) {
			}
		}
		
		return date;
	}

	public Object getValue() {
		return value;
	}

	public Class getClassName() {
		return className;
	}

	public String toString() {
		return value + "\t" + className;
	}
	
}
