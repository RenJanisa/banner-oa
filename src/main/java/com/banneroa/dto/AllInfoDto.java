package com.banneroa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rjj
 * @date 2024/3/13 - 21:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllInfoDto {

    private String userCount;
    private String leaveCount;
    private String signCount;
    private String rate;

}
