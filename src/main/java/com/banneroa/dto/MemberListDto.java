package com.banneroa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author rjj
 * @date 2023/10/16 - 20:51
 */
@Data
public class MemberListDto extends MemberSimpleDto{

    private String className;

    private Integer leaveCount;
}
