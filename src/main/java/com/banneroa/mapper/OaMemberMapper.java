package com.banneroa.mapper;

import com.banneroa.dto.MemberListDto;
import com.banneroa.dto.MemberInfoDto;
import com.banneroa.dto.MemberSimpleDto;
import com.banneroa.pojo.OaMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
public interface OaMemberMapper extends BaseMapper<OaMember> {

    @Select("select id,flag from oa_member where id=#{id} and password=#{password}")
    MemberInfoDto login(String id, String password);

    List<MemberListDto> getMemberList();

    List<MemberListDto> getMemberListWithDirection(@Param("direction") String direction);

    MemberListDto getMemberById(@Param("id") Long memberId);

    @Select("select id,bname,direction from oa_member where flag = 0")
    List<MemberSimpleDto> getMembersNameId();

    @Select("select qq from oa_member where id = #{objId}")
    String getMemberQQ(String objId);

    @Select("select qq from oa_member where flag = 1 and direction = #{direction}")
    String getHeadQQ(String direction);

    @Select("select bname from oa_member where id = #{id}")
    String getMemberNameById(Long id);

    @Select("select count(*) from oa_member where flag = 0")
    Long countAllMember();
}
