package com.banneroa.dto;

import com.banneroa.pojo.OaLeave;
import lombok.Data;

import java.util.List;

/**
 * @author rjj
 * @date 2023/10/16 - 17:30
 */
@Data
public class LeaveListDto extends OaLeave{

    private String bname;

    private String className;

    private String direction;

    private Integer leaveCount;

    private String leaveStatus;

}
