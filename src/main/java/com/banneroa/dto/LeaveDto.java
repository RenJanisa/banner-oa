package com.banneroa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author rjj
 * @date 2023/10/15 - 10:47
 */
@Data
public class LeaveDto {

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "请假理由")
    @NotBlank(message = "申请原因不可为空!")
    private String reason;

    @NotBlank(message = "证据截图不为空")
    private String proof;

}
