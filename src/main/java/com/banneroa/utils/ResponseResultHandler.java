package com.banneroa.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author rjj
 * @date 2023/10/21 - 8:58
 */
//@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {

    //对符合条件的响应进行处理
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //查看是否有@ResponseBody注解,存在才进行处理
        if (returnType.getMethodAnnotation(ResponseBody.class) != null
                || AnnotationUtils.findAnnotation(returnType.getContainingClass(), ResponseBody.class) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof ResponseResult){
            //如果是统一结果类型不处理
            return body;
        }
        //不是统一类型,进行封装
        return ResponseResult.okResult(body);
    }
}
