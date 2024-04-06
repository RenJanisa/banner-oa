package com.banneroa.mapper;

import com.banneroa.pojo.OaSign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author rjj
 * @date 2023/12/22 - 15:16
 */
public interface OaSignMapper extends BaseMapper<OaSign> {
    @Select("select leave_time from oa_sign where id = #{id}")
    String getLeaveTime(String id);

    @Select("select * from oa_sign where member_id=#{objId} ORDER BY come_time DESC limit #{page},8")
    List<OaSign> getSgins(String objId, Integer page);

    @Select("select * from oa_sign where member_id=#{objId} and come_time like #{checkTime} ORDER BY come_time DESC limit #{page},8")
    List<OaSign> getSginsWithCondition(String objId, Integer page, String checkTime);

    @Select("select count(*) from oa_sign")
    Long countAllSign();

    @Select("select count(*) from oa_sign where come_time like #{now}")
    Long getNowDateSignCount(String now);

    @Select("select come_time from oa_sign where member_id=#{userId} and come_time > #{beginTime} and come_time < #{dateTime};")
    List<LocalDateTime> getSginsForReport(String userId, LocalDateTime beginTime, LocalDateTime dateTime);
}
