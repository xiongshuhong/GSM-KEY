package irene.com.framework.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Irene on 2015/6/5.
 */
public class ConvertUtil {

    public static HashMap<String, Object> objectToHashMap(Object obj){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        if(obj == null)
            return hashMap;
        try{
            Class clazz = obj.getClass();
            List<Class> clazzs = new ArrayList<Class>();
            do {
                clazzs.add(clazz);
                clazz = clazz.getSuperclass();
            } while (!clazz.equals(Object.class));

            for (Class iClazz : clazzs) {
                Field[] fields = iClazz.getDeclaredFields();
                for (Field field : fields) {
                    Object objVal = null;
                    field.setAccessible(true);
                    objVal = field.get(obj);
                    hashMap.put(field.getName(), objVal);
                }
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return hashMap;
    }

    public static Map<String, Object> objectToMap(Object obj){
        Map<String, Object> hashMap = new HashMap<String, Object>();
        if(obj == null)
            return hashMap;
        try{
            Class clazz = obj.getClass();
            List<Class> clazzs = new ArrayList<Class>();
            do {
                clazzs.add(clazz);
                clazz = clazz.getSuperclass();
            } while (!clazz.equals(Object.class));

            for (Class iClazz : clazzs) {
                Field[] fields = iClazz.getDeclaredFields();
                for (Field field : fields) {
                    Object objVal = null;
                    field.setAccessible(true);
                    objVal = field.get(obj);
                    hashMap.put(field.getName(), objVal);
                }
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return hashMap;
    }

    public static HashMap<String, Object> jsonToHashMap(JSONObject json) throws JSONException {
        HashMap<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    private static HashMap<String, Object> toMap(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
