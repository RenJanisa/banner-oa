package com.banneroa.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author rjj
 * @date 2023/12/22 - 15:14
 */
@Data
public class OaSign {

    private Long id;
    private Long memberId;
    private LocalDateTime comeTime;
    private LocalDateTime leaveTime;

}
