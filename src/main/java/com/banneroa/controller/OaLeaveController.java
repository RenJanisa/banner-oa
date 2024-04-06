package com.banneroa.controller;


import com.banneroa.dto.LeaveDto;
import com.banneroa.service.OaLeaveService;
import com.banneroa.utils.AppHttpCodeEnum;
import com.banneroa.utils.BaseContext;
import com.banneroa.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
@Api(tags = "请假接口")
@RestController
@RequestMapping("/oa-leave")
public class OaLeaveController {

    @Resource
    private OaLeaveService oaLeaveService;

    @ApiOperation("申请假条")
    @PostMapping("/add")
    public ResponseResult add(@RequestBody @Validated LeaveDto leaveDto) {
        return oaLeaveService.add(leaveDto);
    }

    @ApiOperation("查看已申请假条")
    @GetMapping("/get-list")
    public ResponseResult getList(){
        return oaLeaveService.getList("1",BaseContext.getMember().getId().toString());
    }

    @ApiOperation("删除假条")
    @DeleteMapping("/delete/{leaveId}")
    public ResponseResult del(@PathVariable("leaveId") String leaveId){
        return oaLeaveService.del(leaveId,BaseContext.getMember().getFlag());
    }

    @ApiOperation("审批假条")
    @PostMapping("/handle/{leaveId}")
    public ResponseResult handleLeave(@PathVariable("leaveId") String leaveId){
        return oaLeaveService.handleLeave(leaveId);
    }



}
