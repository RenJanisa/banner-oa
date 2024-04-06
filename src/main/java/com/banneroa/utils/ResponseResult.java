package com.banneroa.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的结果返回类
 *
 * @param <T>
 */
@Data
public class ResponseResult<T> implements Serializable {
    private Integer code;

    private String msg;

    private T data;

    public ResponseResult() {
        this.code = 200;
    }

    public static ResponseResult errorResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static ResponseResult errorResult(AppHttpCodeEnum appHttpCodeEnum) {
        ResponseResult result = new ResponseResult();
        result.code = appHttpCodeEnum.getCode();
        result.data = null;
        result.msg = appHttpCodeEnum.getMessage();
        return result;
    }

    public static <T> ResponseResult<T> okResult(T data) {
        ResponseResult result = new ResponseResult();
        result.data = data;
        result.msg = null;
        return result;
    }

    public static ResponseResult okResult(Integer code,String msg) {
        ResponseResult result = new ResponseResult();
        result.data = null;
        result.msg = msg;
        return result;
    }


}
