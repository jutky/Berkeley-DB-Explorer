package utils;

import java.lang.reflect.Array;

/**
 * Some times we want to pass objects by reference. In order the called function would be able to 
 * change the value of the object it got as a parameter. So this simple class is a wrapper of the Object class.
 */
public class MyObject {
	/** The reference to the wrapped object*/
	Object obj;

	public MyObject(Object obj) {
		this.obj = obj;
	}

	public MyObject() {	}

	/**
	 * @return the obj
	 */
	public Object getObj() {
		return obj;
	}

	/**
	 * @param obj the obj to set
	 */
	public void setObj(Object obj) {
		this.obj = obj;
	}
}
