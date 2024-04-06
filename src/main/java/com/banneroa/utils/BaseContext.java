package com.banneroa.utils;

/**
 * @author rjj
 * @date 2022/9/16 - 9:03
 */


import com.banneroa.dto.MemberInfoDto;

/**
 * 基于ThreadLocal的封装工具类,用于保存和获取当前登陆用户信息
 */
public class BaseContext {
    private static ThreadLocal<MemberInfoDto> threadLocal = new ThreadLocal<>();

    //作用在同一线程范围内
    public static void setMember(MemberInfoDto memberLogin){
        threadLocal.set(memberLogin);
    }

    public static MemberInfoDto getMember(){
        return threadLocal.get();
    }

    public static void removeMember(){threadLocal.remove();}
}
