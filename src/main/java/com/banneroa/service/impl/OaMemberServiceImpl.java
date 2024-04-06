package com.banneroa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.banneroa.dto.AllInfoDto;
import com.banneroa.dto.MemberListDto;
import com.banneroa.dto.MemberInfoDto;
import com.banneroa.dto.MemberSimpleDto;
import com.banneroa.mapper.OaLeaveMapper;
import com.banneroa.mapper.OaSignMapper;
import com.banneroa.pojo.OaMember;
import com.banneroa.mapper.OaMemberMapper;
import com.banneroa.service.OaLeaveService;
import com.banneroa.service.OaMemberService;
import com.banneroa.service.OaSignService;
import com.banneroa.utils.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
@Service
public class OaMemberServiceImpl extends ServiceImpl<OaMemberMapper, OaMember> implements OaMemberService {

    @Resource
    private OaMemberMapper oaMemberMapper;

    @Override
    public ResponseResult login(String id, String password) {

        MemberInfoDto memberInfoDto = oaMemberMapper.login(id, password);
        if (memberInfoDto == null) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        String token = AppJwtUtil.getToken(memberInfoDto.getId(), memberInfoDto.getFlag());
        List<String> r = new ArrayList<>();
        r.add(token);
        r.add(memberInfoDto.getFlag() + "");
        return ResponseResult.okResult(r);
    }

    @Override
    public ResponseResult add(OaMember oaMember) {
        if (oaMemberMapper.selectById(oaMember.getId()) != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        int insert = oaMemberMapper.insert(oaMember);
        return insert > 0 ? ResponseResult.okResult(200, "成功添加")
                : ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    @Override
    public ResponseResult getMemberList(String direction) {

        if (BaseContext.getMember().getFlag() != 1) {
            return ResponseResult.errorResult(500, "缺少权限");
        }
        List<MemberListDto> memberListDtos;
        if (StrUtil.isBlank(direction)) {
            memberListDtos = oaMemberMapper.getMemberList();
        } else {
            memberListDtos = oaMemberMapper.getMemberListWithDirection(direction);
        }
        return ResponseResult.okResult(memberListDtos);
    }

    @Override
    public ResponseResult getMembersNameId() {

        if (BaseContext.getMember().getFlag() != 1) {
            return ResponseResult.errorResult(500, "缺少权限");
        }

        List<List<MemberSimpleDto>> r = new ArrayList<>();
        List<MemberSimpleDto> javaer = new ArrayList<>();
        List<MemberSimpleDto> pythoner = new ArrayList<>();
        List<MemberSimpleDto> unityer = new ArrayList<>();
        List<MemberSimpleDto> viewer = new ArrayList<>();
        List<MemberSimpleDto> goer = new ArrayList<>();

        List<MemberSimpleDto> memberSimpleDtos = oaMemberMapper.getMembersNameId();
        memberSimpleDtos.forEach(i -> {
            if (DirectionEnum.JAVA.getValues().equals(i.getDirection())) {
                javaer.add(i);
            } else if (DirectionEnum.PYTHON.getValues().equals(i.getDirection())) {
                pythoner.add(i);
            } else if (DirectionEnum.GO.getValues().equals(i.getDirection())) {
                goer.add(i);
            } else if (DirectionEnum.UNITY.getValues().equals(i.getDirection())) {
                unityer.add(i);
            } else {
                viewer.add(i);
            }
        });

        r.add(javaer);
        r.add(pythoner);
        r.add(goer);
        r.add(unityer);
        r.add(viewer);

        return ResponseResult.okResult(r);
    }

    @Resource
    private ExecutorService executorService;
    @Resource
    private OaLeaveMapper oaLeaveMapper;
    @Resource
    private OaSignMapper oaSignMapper;

    @Override
    public ResponseResult getAllInfo() {

        Future<Long> userCount = executorService.submit(() -> {
            return oaMemberMapper.countAllMember();
        });

        Future<Long> leaveCount = executorService.submit(() -> {
            return oaLeaveMapper.countAllLeave();
        });
        Future<Long> signCount = executorService.submit(() -> {
            return oaSignMapper.countAllSign();
        });

        AllInfoDto allInfoDto;
        try {
            int rate = Math.round((float) oaSignMapper.getNowDateSignCount(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "%") / (userCount.get() * 3) * 100);

            allInfoDto = new AllInfoDto(userCount.get().toString(), leaveCount.get().toString(), signCount.get().toString(), rate+"%");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return ResponseResult.okResult(allInfoDto);
    }
}
