package com.banneroa.service;

import com.banneroa.pojo.OaMember;
import com.banneroa.utils.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
public interface OaMemberService extends IService<OaMember> {

    ResponseResult login(String id, String password);

    ResponseResult add(OaMember oaMember);

    ResponseResult getMemberList(String direction);

    ResponseResult getMembersNameId();

    ResponseResult getAllInfo();
}
