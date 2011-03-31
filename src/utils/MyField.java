package utils;

import gui.EntitiesBrowser;

import java.lang.reflect.Array;

import org.apache.commons.lang.StringEscapeUtils;
/**
 * Represents one field that is shown in the tree of the {@link EntitiesBrowser} panel.
 * The main purpose is return beautiful HTML that show the information about this field in the <tt>toString()</tt> function. 
 */
public class MyField {
	// name of the represented field
	String fieldName;
	// value of the represented field
	Object fieldValue;
	// if this field is part of the collection or an array, then store here the index of current field.
	int inex=-1;
	
	public MyField(String fieldName, Object fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public MyField(String fieldName, Object fieldValue, int indexInArray) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.inex = indexInArray;
	}

	public String toHTMLString() {
		String fieldValueString = fieldValueToHTMLString();
		StringBuilder result = new StringBuilder();
		result.append("<html>");
		if(inex!=-1){
			result.append("[");
			result.append(inex);
			result.append("]: ");
		}
		if(fieldName==null){
			result.append(fieldValueString);
		}else{
			result.append(fieldName);
			if(fieldValue==null) 
				result.append(" = ");
			else if(ReflectionUtils.isPrimitive(fieldValue))
				result.append(" = ");
			else
				result.append(' ');
			result.append(fieldValueString);
		}
		result.append("</html>");
		return result.toString();
	}
	
	public String fieldValueToHTMLString() {
		if(fieldValue == null)
			return "null";
		if(ReflectionUtils.isPrimitive(fieldValue)){
			String className = fieldValue.getClass().getName();
			className=className.substring(className.lastIndexOf('.')+1);
			return "<font color=\"blue\">"+StringEscapeUtils.escapeHtml(fieldValue.toString())+"</font> <font color=\"gray\"><i>"+className+"</i></font>";
		}
		if(ReflectionUtils.isArray(fieldValue)){
			int size = Array.getLength(fieldValue);
			Class<?> arrElem = fieldValue.getClass().getComponentType();
			return "<font color=\"gray\"><i>"+arrElem.getName()+"["+size+"]</i></font>";
		}
		return "<font color=\"gray\"><i>"+fieldValue.getClass().getName()+"</i></font>";
	}
	
	@Override
	public String toString() {
		return toHTMLString();
	}
	
	public String getFieldName() {
		return fieldName;
	}


	public Object getFieldValue() {
		return fieldValue;
	}

}
