package com.banneroa.controller;

import cn.hutool.core.bean.BeanUtil;
import com.banneroa.dto.MemberListDto;
import com.banneroa.pojo.OaMember;
import com.banneroa.service.OaLeaveService;
import com.banneroa.service.OaMemberService;
import com.banneroa.utils.AppHttpCodeEnum;
import com.banneroa.utils.BaseContext;
import com.banneroa.utils.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author rjj
 * @date 2023/10/16 - 20:43
 */
@Api(tags = "管理员接口")
@RestController
@RequestMapping("/oa-admin")
public class OaAdminController {

    @Resource
    private OaMemberService oaMemberService;

    @Resource
    private OaLeaveService oaLeaveService;

    @ApiOperation("获取信息")
    @GetMapping("/self")
    public ResponseResult getAdminInfo() {
        OaMember member = oaMemberService.getById(BaseContext.getMember().getId());
        member.setPassword(null);
        return ResponseResult.okResult(member);
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/member-list")
    public ResponseResult getMemberList(String direction) {
        return oaMemberService.getMemberList(direction);
    }

    @ApiOperation("获取用户请假列表")
    @GetMapping("/leave-list")
    public ResponseResult getMemberLeaveList(String currentPage,String id) {
        return oaLeaveService.getList(currentPage,id);
    }

    @ApiOperation("删除成员")
    @DeleteMapping("/delete-member/{id}")
    public ResponseResult delMember(@PathVariable("id") String id) {
        return oaMemberService.removeById(id) ? ResponseResult.okResult(200, "删除成功")
                : ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
    }

    @ApiOperation("获取用户姓名与id")
    @GetMapping("/member-name-id")
    public ResponseResult getMembersNameId(){
        return oaMemberService.getMembersNameId();
    }


    @ApiOperation("获取系统统计信息")
    @PostMapping("/get-all-info")
    public ResponseResult getAllInfo(){
        return oaMemberService.getAllInfo();
    }

}
