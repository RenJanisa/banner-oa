package com.banneroa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.banneroa.dto.LeaveDto;
import com.banneroa.dto.LeaveListDto;
import com.banneroa.dto.MemberListDto;
import com.banneroa.dto.PageResultDto;
import com.banneroa.mapper.OaMemberMapper;
import com.banneroa.pojo.OaLeave;
import com.banneroa.mapper.OaLeaveMapper;
import com.banneroa.pojo.OaMember;
import com.banneroa.service.OaLeaveService;
import com.banneroa.utils.AppHttpCodeEnum;
import com.banneroa.utils.BaseContext;
import com.banneroa.utils.ResponseResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.beancontext.BeanContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
@Service
public class OaLeaveServiceImpl extends ServiceImpl<OaLeaveMapper, OaLeave> implements OaLeaveService {


    @Resource
    private OaLeaveMapper oaLeaveMapper;
    @Resource
    private OaMemberMapper oaMemberMapper;


    @Override
    public ResponseResult add(LeaveDto leaveDto) {

        OaLeave oaLeave = BeanUtil.copyProperties(leaveDto, OaLeave.class);
        oaLeave.setMemberId(BaseContext.getMember().getId());
        oaLeave.setStatus(0);

        int insert = oaLeaveMapper.insert(oaLeave);

        return insert > 0 ? ResponseResult.okResult(200,"成功添加")
                : ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    @Override
    public ResponseResult getList(String currentPage,String id) {


        List<OaLeave> oaLeaves;
        Map<Long,MemberListDto> idAndMember =  new HashMap<>();
        int page = (Integer.parseInt(currentPage)-1)*8;
        Integer total;

        if (BaseContext.getMember().getFlag()==0){
            //todo 用户查询自己
            id = BaseContext.getMember().getId().toString();
            oaLeaves = oaLeaveMapper.selectList(Wrappers.<OaLeave>lambdaQuery()
                    .eq(OaLeave::getMemberId, id).orderByDesc(OaLeave::getCreateTime));
            total = oaLeaveMapper.selectCount(Wrappers.<OaLeave>lambdaQuery()
                    .eq(OaLeave::getMemberId, id));
        }else {
            if (StrUtil.isNotBlank(id)){
                //todo 管理员查询某个人
                oaLeaves = oaLeaveMapper.selectList(Wrappers.<OaLeave>lambdaQuery()
                        .eq(OaLeave::getMemberId, id).orderByDesc(OaLeave::getCreateTime));
                total = oaLeaveMapper.selectCount(Wrappers.<OaLeave>lambdaQuery()
                        .eq(OaLeave::getMemberId, id));
            }else {
                //todo 查询所有人
                oaLeaves = oaLeaveMapper.getLeaveWithPage(page);
                total = oaLeaveMapper.selectCount(null);
                //找出成员id,去重查询成员信息
                List<Long> memberIds = oaLeaves.stream().distinct().map(OaLeave::getMemberId).collect(Collectors.toList());
                for (Long memberId : memberIds) {
                    idAndMember.put(memberId,oaMemberMapper.getMemberById(memberId));
                }
            }
        }

        List<LeaveListDto> leaves = oaLeaves.stream().map((item) -> {
            LeaveListDto leaveListDto = BeanUtil.copyProperties(item, LeaveListDto.class);
            if (item.getStatus() == 0) leaveListDto.setLeaveStatus("未审批");
            else if(item.getStatus()==1) leaveListDto.setLeaveStatus("已通过");
            else if (item.getStatus() == 2) leaveListDto.setProof("未通过");
            if (!idAndMember.isEmpty()){
                MemberListDto memberListDto = idAndMember.get(item.getMemberId());
                leaveListDto.setLeaveCount(memberListDto.getLeaveCount());
                leaveListDto.setBname(memberListDto.getBname());
                leaveListDto.setDirection(memberListDto.getDirection());
                leaveListDto.setClassName(memberListDto.getClassName());
            }
            return leaveListDto;
        }).collect(Collectors.toList());

        return ResponseResult.okResult(new PageResultDto(leaves,total));
    }

    @Override
    public ResponseResult del(String leaveId,int flag) {

        int delete;
        if (flag == 0){
            delete = oaLeaveMapper.delete(Wrappers.<OaLeave>lambdaQuery()
                    .eq(OaLeave::getId, leaveId)
                    .eq(OaLeave::getMemberId, BaseContext.getMember().getId())
                    .eq(OaLeave::getStatus, 0));
        }else {
            delete = oaLeaveMapper.delete(Wrappers.<OaLeave>lambdaQuery()
                    .eq(OaLeave::getId, leaveId));
        }
        return delete > 0 ? ResponseResult.okResult(200,"成功删除")
                : ResponseResult.errorResult(500, "无法删除已审批假条");
    }

    @Override
    public ResponseResult handleLeave(String leaveId) {

        if (StrUtil.isBlank(leaveId)) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);

        int status = oaLeaveMapper.getLeaveStatus(leaveId);
        if (status == 0){
            //未通过审批,现通过
            boolean update = this.update(Wrappers.<OaLeave>lambdaUpdate().set(OaLeave::getStatus, 1).eq(OaLeave::getId, leaveId));
            return update ? ResponseResult.okResult(200,"已通过审批!")
                    : ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }else if (status == 1){
            //已通过,现取消
            boolean update = this.update(Wrappers.<OaLeave>lambdaUpdate().set(OaLeave::getStatus, 2).eq(OaLeave::getId, leaveId));
            return update ? ResponseResult.okResult(200,"已取消通过!")
                    : ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }else if (status == 2){
            boolean update = this.update(Wrappers.<OaLeave>lambdaUpdate().set(OaLeave::getStatus, 1).eq(OaLeave::getId, leaveId));
            return update ? ResponseResult.okResult(200,"已通过审批!")
                    : ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }else {
         return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
    }
}
