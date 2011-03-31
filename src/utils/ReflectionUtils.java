package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.sleepycat.persist.model.PrimaryKey;

/**
 * Different static utilities that uses reflection are implemented in this class.
 */
public class ReflectionUtils {
	/**
	 * Create an object of specified type
	 * @param entityClazz name of the class of the object that should be created
	 * @return the object of the desired class
	 */
	public static Class<?> getClass(String entityClazz){
		Class<?> result;
		try {
			result = Class.forName(entityClazz);
		} catch (ClassNotFoundException e) {
			MyLogger.error("Can't get the Class of the "+entityClazz);
			return null;
		}		
		return result;
	}
	
	/**
	 * Get primary key field of the entity class. The primary key field is the field that is 
	 * annotated with the {@link PrimaryKey} annotation.
	 * @param entityClass the entity class to find the primary key in.
	 * @return the primary key of the entity
	 */
	public static Field getPrimaryKeyField(Class<?> entityClass){
		Field[] fields = entityClass.getDeclaredFields();
		for(Field field:fields){
			Annotation pkAnnotation = field.getAnnotation(PrimaryKey.class);
			if(pkAnnotation instanceof PrimaryKey){
				return field;
			}
		}
		return null;
	}
		
	/**
	 * Wrapper for the {@link ReflectionUtils#getAllNonStaticFieds(Class)} function
	 * @param obj the class of this object would be queried for all non-static fields
	 */
	public static List<Field> getAllNonStaticFieds(Object obj){
		return getAllNonStaticFieds(obj.getClass());
	}
	/**
	 * Get list of all fields (includes fields that are inherited from superclass or interface)
	 * that are non-static.
	 * @param clazz the class find fields in
	 * @return list of all non-static fields
	 */
	public static List<Field> getAllNonStaticFieds(Class<?> clazz){
		List<Field> allFields = new LinkedList<Field>();
	    Class<?> tempClass = clazz;
	    while (tempClass != null) {
	    	Field[] f = tempClass.getDeclaredFields();
	    	for (int i = 0; i < f.length; i++) {
	    		if(!Modifier.isStatic(f[i].getModifiers()))
	    			allFields.add(f[i]);
	    	}
	    	tempClass = tempClass.getSuperclass();
	    }
	    Class<?>[] interfaces = clazz.getInterfaces();
	    for(Class<?> curInterface:interfaces){
	    	Field[] f = curInterface.getDeclaredFields();
	    	for (int i = 0; i < f.length; i++) {
	    		if(!Modifier.isStatic(f[i].getModifiers()))
	    			allFields.add(f[i]);
	    	}
	    }
	    return allFields;
	}
	
	/** Set of classes that are thought as primitive data types */
	static private HashSet<Class<?>> primitives = new HashSet<Class<?>>();
	static {
		primitives.add(Integer.class);
		primitives.add(int.class);
		primitives.add(Byte.class);
		primitives.add(byte.class);
		primitives.add(Float.class);
		primitives.add(float.class);
		primitives.add(Double.class);
		primitives.add(double.class);
		primitives.add(Long.class);
		primitives.add(long.class);
		primitives.add(String.class);
		primitives.add(boolean.class);
		primitives.add(Boolean.class);
	}
	public static boolean isPrimitive(Object obj){
		return isPrimitive(obj.getClass());
	}

	/**
	 * Check if the class is in the set of primitive data types, or is an enum.
	 * @param clazz
	 * @return true if the class is primitive or enum, false otherwise
	 */
	public static boolean isPrimitive(Class<?> clazz){
		if(clazz.isEnum())
			return true;
		return primitives.contains(clazz);
	}
	
	
	/**
	 * Check if the object is of the array type
	 * @param obj
	 * @return true if the object is of the array type, false otherwise
	 */
	public static boolean isArray(Object obj){
		return obj.getClass().isArray();
	};
	
	/**
	 * Create an object of specific type and initialize it's fields.
	 * @param clazz type of the class to create
	 * @param fieldValues string representation of the field values
	 * @return initialized object
	 */
	public static Object createObject(Class<?> clazz,String fieldValues[]){		
		Object obj = null;
		try {
			
			if(isPrimitive(clazz))
				return createPrimitiveObject(clazz, fieldValues[0]);
			
			// TODO: test that this works correctly for non primitive objects
			// TODO: test also for composite objects
			
			obj = clazz.newInstance();
			List<Field> fieldsList = ReflectionUtils.getAllNonStaticFieds(clazz);
			if(fieldsList.size()!=fieldValues.length){
				MyLogger.error("Can't initialize an object, number of fields doesn't suits");
				return null;
			}
			
			for(int i=0;i<fieldsList.size();i++){
				Field field = fieldsList.get(i);
				Object fieldObj = createPrimitiveObject(field.getType(), fieldValues[i]);
				field.set(obj, fieldObj);
			}
		} catch (NoSuchMethodException e) {
			MyLogger.error("The class "+clazz.getCanonicalName()+" has no empty constructor!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Create a class instance of primitive type with predefined value.
	 * @param clazz the type of class to create
	 * @param value string representation of the value this object would get
	 */
	public static Object createPrimitiveObject(Class<?> clazz,String value) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException{
		// TODO: convert other simple class types to theirs corresponding wrapper types too
		if(clazz == int.class)
			clazz = Integer.class;

		Object obj;
		Constructor<?> ctor = clazz.getConstructor(String.class);
		try{
			obj = ctor.newInstance(value);
		}catch (InvocationTargetException e) {
			return null;
		}
		return obj;
	}
}
