package com.banneroa.controller;


import com.banneroa.dto.MemberLoginDto;
import com.banneroa.pojo.OaMember;
import com.banneroa.service.OaMemberService;
import com.banneroa.utils.AppHttpCodeEnum;
import com.banneroa.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
@Api(tags = "成员接口")
@RestController
@RequestMapping("/oa-member")
public class OaMemberController {

    @Resource
    private OaMemberService oaMemberService;


    @ApiOperation("登录")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody MemberLoginDto member) {
        return oaMemberService.login(member.getId(), member.getPassword());
    }

    @ApiOperation("注册")
    @PostMapping("/add")
    public ResponseResult add(@RequestBody @Validated OaMember oaMember){
        return oaMemberService.add(oaMember);
    }





}
