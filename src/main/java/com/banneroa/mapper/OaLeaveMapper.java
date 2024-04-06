package com.banneroa.mapper;

import com.banneroa.dto.ReportLeaveDto;
import com.banneroa.pojo.OaLeave;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
public interface OaLeaveMapper extends BaseMapper<OaLeave> {

    @Select("select status from oa_leave where id = #{leaveId}")
    int getLeaveStatus(@Param("leaveId") String leaveId);

    @Select("select * from oa_leave order by create_time desc limit #{page},8")
    List<OaLeave> getLeaveWithPage(@Param("page") int page);

    @Select("select count(*) from oa_leave")
    Long countAllLeave();

    @Select("select begin_time,end_time from oa_leave where member_id=#{userId} and begin_time > #{beginTime} and begin_time < #{dateTime};")
    List<ReportLeaveDto> getLeaveforReport(String userId, LocalDateTime beginTime, LocalDateTime dateTime);
}
