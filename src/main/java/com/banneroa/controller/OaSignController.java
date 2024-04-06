package com.banneroa.controller;

import cn.hutool.json.JSONUtil;
import com.banneroa.dto.ClassTimeDto;
import com.banneroa.service.OaSignService;
import com.banneroa.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author rjj
 * @date 2023/12/22 - 15:20
 */
@Api(tags = "用户签到接口")
@RestController
@RequestMapping("/oa-sign")
public class OaSignController {

    @Resource
    private OaSignService oaSignService;


    @ApiOperation("发送验证码")
    @PostMapping("/send-code")
    public ResponseResult sendCode(String objId){
        return oaSignService.sendCode(objId);
    }

    @ApiOperation("查询验证码")
    @PostMapping("/get-code")
    public ResponseResult getCode(String direction){
        return oaSignService.getCode(direction);
    }

    @ApiOperation("签到")
    @PostMapping("/do-sign")
    public ResponseResult doSign(String code){
        return oaSignService.doSign(code);
    }

    @ApiOperation("签退")
    @PostMapping("/do-sign-leave")
    public ResponseResult doSignLeave(String id){
        return oaSignService.doSignLeave(id);
    }

    @ApiOperation("查看签到列表")
    @GetMapping("/get-signs")
    public ResponseResult getSigns(String objId, Integer currentPage, String checkTime){
        return oaSignService.getSigns(objId,currentPage,checkTime);
    }

    @ApiOperation("删除签到记录")
    @PostMapping("/del-sign")
    public ResponseResult delSign(String id,HttpServletRequest request){
        return oaSignService.delSign(id);
    }

    @ApiOperation("添加课表")
    @PostMapping("/add-class-time")
    public ResponseResult addClassTime(@RequestBody ClassTimeDto classTimeDto){
        return oaSignService.addClassTIme(classTimeDto);
    }

    @ApiOperation("查询课表")
    @GetMapping("/get-class-time")
    public ResponseResult getClassTime(String userId){
        return oaSignService.getClassTime(userId);
    }

    @ApiOperation("清空课表")
    @DeleteMapping("/del-class-time")
    public ResponseResult delClassTime(String userId){
        return oaSignService.delClassTime(userId);
    }

    @ApiOperation("/生成签到报告")
    @GetMapping("/get-sign-report")
    public ResponseResult getSignReport(String userId){
        return oaSignService.getSignReport(userId);
    }


}
