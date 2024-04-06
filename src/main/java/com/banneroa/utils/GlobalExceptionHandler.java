package com.banneroa.utils;

import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author rjj
 * @date 2023/10/18 - 17:08
 */
@RestControllerAdvice // 激活捕获controller层抛出的异常
public class GlobalExceptionHandler {

    //捕获JSR303异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult doMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        //校验错误信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //收集错误信息
        StringBuffer errors = new StringBuffer();
        fieldErrors.forEach((item)->{
            errors.append(item.getDefaultMessage()).append(',');
        });
        String em = errors.substring(0, errors.length() - 1);
        return ResponseResult.errorResult(500,em);
    }
}
