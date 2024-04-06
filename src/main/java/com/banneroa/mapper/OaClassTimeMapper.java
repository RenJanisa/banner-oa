package com.banneroa.mapper;

import com.banneroa.pojo.OaClassTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author rjj
 * @date 2024/3/23 - 10:07
 */
public interface OaClassTimeMapper extends BaseMapper<OaClassTime> {

    @Select("select COUNT(*) from oa_class_time where member_id=#{userId} and day=#{day}")
    Integer isExist(Long userId, String day);

    @Update("update oa_class_time set status = #{status} where member_id = #{userId} and day = #{day}")
    void updateClassTime(Long userId, String day, String status);
}
