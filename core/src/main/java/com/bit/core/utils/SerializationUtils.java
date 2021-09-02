package com.bit.core.utils;

import com.google.gson.Gson;

public class SerializationUtils {
	public static String serializeObjectByGson(Object o) {
	    Gson gson = new Gson();
	    String serializedObject = gson.toJson(o);
	    return serializedObject;
	}


	public static Object unserializeObjectByGson(String s, Object o){
	    Gson gson = new Gson();
	    Object object = gson.fromJson(s, o.getClass());
	    return object;
	}

	public static Object cloneObjectByGson(Object o){
	    String s = serializeObjectByGson(o);
	    Object object = unserializeObjectByGson(s,o);
	    return object;
	}
}
