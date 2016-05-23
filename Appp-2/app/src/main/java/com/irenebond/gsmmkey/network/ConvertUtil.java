package com.irenebond.gsmmkey.network;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Irene on 2016/3/3.
 */
public class ConvertUtil {

    public static LinkedHashMap<String, Object> objectToHashMap(Object obj){
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
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
}
