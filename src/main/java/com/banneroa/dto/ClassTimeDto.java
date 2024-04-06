package com.banneroa.dto;

import lombok.Data;
import sun.security.provider.Sun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author rjj
 * @date 2024/3/22 - 20:01
 */
@Data
public class ClassTimeDto {
    private List<String> mon;
    private List<String> tues;
    private List<String> wed;
    private List<String> thur;
    private List<String> fri;
    private List<String> sat;
    private List<String> sun;

    public ClassTimeDto(){
        this.mon = new ArrayList<>();
        this.tues = new ArrayList<>();
        this.wed = new ArrayList<>();
        this.thur = new ArrayList<>();
        this.fri = new ArrayList<>();
        this.sat = new ArrayList<>();
        this.sun = Arrays.asList("下午一","下午二","晚上一");

    }

}
