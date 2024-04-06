package com.banneroa.service;

import com.banneroa.dto.LeaveDto;
import com.banneroa.pojo.OaLeave;
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
public interface OaLeaveService extends IService<OaLeave> {

    ResponseResult add(LeaveDto leaveDto);

    ResponseResult getList(String currentPage,String id);

    ResponseResult del(String leaveId,int flag);

    ResponseResult handleLeave(String leaveId);
}
