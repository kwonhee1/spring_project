package com.example.demo.controller;

import com.example.demo.Exception.HttpException.NoNecessayException;
import com.example.demo.Exception.NotExistMethodName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyController {
    private static final Logger log = LoggerFactory.getLogger(MyController.class);

    // necessarys[] 들이 실제 존재하는 함수들인지 확인 => 확인하지 않음 만약 에러 발생시 그냥 보이게
    // 해당 함수들 구해서 실행 결과값이 null이 존재하면 httpException 인자 없음
    public void checkInputParameter(String necessarys[], Object param) throws NotExistMethodName, NoNecessayException {
        String methodName = "not initialized";
        try {
            for (String necess : necessarys) {
                methodName = String.format("get%s%s", necess.substring(0, 1).toUpperCase(), necess.substring(1));
                Method method = param.getClass().getMethod(methodName);

                Object result = method.invoke(param);
                if(result == null) {
                    throw new NoNecessayException(necess);
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new NotExistMethodName(methodName);
        }
    }
}
