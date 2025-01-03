package com.example.demo.model;

import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.exception.reflection.NotExistMethodName;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

public class MyModel {
    public static <T> void createMap (T t, Map<String, Method> getters) {
        String methodName = "not initialized";
        for(Field field : t.getClass().getDeclaredFields()){
            try {
                String filedName = field.getName();
                if(filedName.equals("getters"))
                    continue;
                methodName = String.format("get%s%s", filedName.substring(0, 1).toUpperCase(), filedName.substring(1));
                getters.put(filedName, t.getClass().getMethod(methodName));
            } catch (NoSuchMethodException e) {
                throw new NotExistMethodName(methodName);
            }
        }
    }

    public void checkNecessary(String necessarys[], Map<String, Method> getters) {
        for(String necess : necessarys){
            Method getter = getters.get(necess);

            try {
                if( ((String)getter.invoke(this)).isEmpty() )
                    throw new CustomException(CustomTitle.NO_NECESSARY_PARAMETER, CustomMessage.NO_NECESSARY_INPUT, necess);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    class pair{
        Type type;
        Method method;
    }
}
