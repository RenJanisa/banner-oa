package com.banneroa.utils;

/**
 * @author rjj
 * @date 2023/12/22 - 21:14
 */
public enum DirectionEnum {
    JAVA("java"),
    PYTHON("python"),
    UNITY("unity"),
    GO("go"),
    VIEW("前端");
    String values;

    DirectionEnum(String values){
        this.values=values;
    }

    public String getValues(){
        return values;
    }
}
