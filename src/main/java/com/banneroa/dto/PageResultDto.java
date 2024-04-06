package com.banneroa.dto;

/**
 * @author rjj
 * @date 2023/11/30 - 21:37
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResultDto<T> {

    private T obj;
    private Integer total;

}
