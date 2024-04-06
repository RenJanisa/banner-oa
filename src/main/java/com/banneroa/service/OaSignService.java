package com.banneroa.service;

import com.banneroa.dto.ClassTimeDto;
import com.banneroa.mapper.OaSignMapper;
import com.banneroa.pojo.OaSign;
import com.banneroa.utils.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author rjj
 * @date 2023/12/22 - 15:17
 */
public interface OaSignService extends IService<OaSign> {
    ResponseResult sendCode(String objId);


    ResponseResult getCode(String direction);

    ResponseResult doSign(String code);

    ResponseResult getSigns(String objId,Integer currentPage,String checkTime);

    ResponseResult doSignLeave(String id);

    ResponseResult delSign(String id);

    ResponseResult addClassTIme(ClassTimeDto classTimeDto);

    ResponseResult getClassTime(String userId);

    ResponseResult delClassTime(String userId);

    ResponseResult getSignReport(String userId);
}
