package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * In this class there are definitions of many variables that are used across the project.
 * </p>
 * The values of the variables could be changed from the external file (that it's name is 
 * saved in {@link Settings#settingsFileName}) with out recompiling the entire project and even in
 * runtime,by calling the static function {@link Settings#loadSettings()} like it is done in {@link KPSServiceServer#refreshSettings()}
 */
public class Settings {
	/** Folder where the BDB files would be saved */
	public static String BDBFilesPath="D:/Work/eclipse_ws/SearchApplication/bdb_files";
	/** path of the log4j configuration file*/
	public static final String Log4jConfigurationFile = "src/log4j.xml";
	/** The file name of the settings values that would reload the default values 
	 *  of the variables in this class.<p>
	 *  The default value is "setting.txt" */
	public static String settingsFileName = "settings.txt";

	public static float defaultNodeWeight = 1;

	static{
		loadSettings(Settings.class);
	}

	/**
	 * This function reads the values from the of the parameters from the file
	 * named {@link Settings#settingsFileName} and overrides the the default
	 * values of the parameters with those from that file.</p> Note: that is no
	 * way to overload values of parameters of complex types, like arrays or
	 * hashmaps to do so you have to edit code of this class and recompile the
	 * project
	 * 
	 * @param settingsClass 
	 *            the class the settings would be loaded to, if you use the
	 *            original Settings class then you should pass here
	 *            Setting.class, but if you extend the Settings class the you
	 *            should pass yoursSettingClass.class
	 */
	public static void loadSettings(Class<?> settingsClass) {
		MyLogger.info("Loading settings from the "+settingsFileName+" file...");
		File file = new File(settingsFileName);
		if(!file.canRead()){
			MyLogger.warn("Can't read settings file:"+file.getAbsolutePath()+", so continue to run with default settings.");
			return;
		}
		Properties settings = new Properties();	
		try {
			// parse the settings file
			settings.load(new FileInputStream(new File(settingsFileName)));
			// for every parameter name
			for(Object settingName : settings.keySet()){
				String settingValue = ((String) settings.get(settingName)).trim();
				// check it there is such field in this class
				try{
					// check what is type of the field and override it's value
					Field field = settingsClass.getField(settingName.toString());
					Class<?> fieldType = field.getType();
					// override the default value of that field with one from the settings file
					// according to the type of the field
					if(fieldType == String.class){
						field.set(null, String.valueOf(settingValue));
					}else if(fieldType == int.class || fieldType == Integer.class ){
						field.set(null, Integer.valueOf(String.valueOf(settingValue)));
					}else if(fieldType == boolean.class || fieldType == Boolean.class){
						field.set(null, Boolean.valueOf(String.valueOf(settingValue)));
					}else if(fieldType == float.class || fieldType == Float.class){
						field.set(null, Float.valueOf(String.valueOf(settingValue)));						
					}else if(fieldType == HashMap.class){ 
						// handle the case when the field is of the HashMap type
						
						// get the types of the Key and of the Value of the Hash
						ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
						Type keyValueType[] = parametrizedType.getActualTypeArguments();
						Class<?> keyClass = (Class<?>) keyValueType[0];
						Class<?> valueClass = (Class<?>) keyValueType[1];

						MyObject keyObject = new MyObject();
						MyObject valueObject = new MyObject();
						
						HashMap<Object, Object> hash = new HashMap<Object, Object>(); 

						// parse the value of the Hash key1 => value1 , key2 => value2 , ....						
						StringTokenizer st = new StringTokenizer(settingValue.toString().trim(),",");
						while(st.hasMoreTokens()){
							String keyValuePair = st.nextToken();
							int arrowIndex = keyValuePair.indexOf("=>");
							String key = keyValuePair.substring(0,arrowIndex).trim();
							String value = keyValuePair.substring(arrowIndex+2).trim();
							setAnObjectByType(keyClass, keyObject, key);
							setAnObjectByType(valueClass, valueObject, value);
							hash.put(keyObject.getObj(), valueObject.getObj());
						}
						field.set(null,hash);
					}else if(fieldType == List.class || fieldType == ArrayList.class || fieldType == LinkedList.class || fieldType == Vector.class || fieldType == HashSet.class){
						// handle the case when the field is of the Collection type
						ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
						Class<?> elementClass = (Class<?>) parametrizedType.getActualTypeArguments()[0];
						// if the type of the field is an interface then decide to use one of it's implementations 
						if(fieldType == List.class)
							fieldType = LinkedList.class;
						Constructor<Collection<Object>> ctor = (Constructor<Collection<Object>>) fieldType.getConstructor();	
						Collection<Object> collection = ctor.newInstance();;
						// parse the value of the List "element1 , element2, ...						
						MyObject elementObject = new MyObject();
						StringTokenizer st = new StringTokenizer(settingValue.toString().trim(),",");
						while(st.hasMoreTokens()){
							String element = st.nextToken().trim();
							setAnObjectByType(elementClass, elementObject, element);							
							collection.add(elementObject.getObj());
						}
						field.set(null, collection);					
					}else if(fieldType.isEnum()){
							Object[] enumValues = fieldType.getEnumConstants();
							Object enumValue=null;
							for(Object curValue:enumValues)
								if(String.valueOf(curValue).equalsIgnoreCase(settingValue)){
									enumValue=curValue;
									break;
								}
							if(enumValue==null)
								MyLogger.warn("can't assign a vlaue \""+settingValue+"\" to the enum variable "+settingName+" of a type "+fieldType);
							else{
								field.set(null, enumValue);
							}
					}else {
						MyLogger.warn("I don't know how to init the property of the type:"+fieldType);
					}
				}
				// if there is no such field then do nothing
				catch (NoSuchFieldException e) {
					MyLogger.warn("Can't init the field: "+e.getMessage());
				} 
			}
		} catch (IOException e) {
			MyLogger.error("Can't find properties file", e);
		} catch (Exception e) {
			MyLogger.error("I have a problem",e);
		}
	}

	/**
	 * Sets value of the object according to the object's type
	 * @param objClass the type of the object
	 * @param obj the object itself
	 * @param value the value to set
	 */
	private static void setAnObjectByType(Class<?> objClass, MyObject obj, String value){
		if(objClass == String.class){
			obj.setObj(value);
		}else if(objClass == Integer.class){
			obj.setObj(Integer.valueOf(value));
		}else if(objClass == Float.class){
			obj.setObj(Float.valueOf(value));
		}else if (objClass == Boolean.class){
			obj.setObj(Boolean.valueOf(value));
		}
	}
}
