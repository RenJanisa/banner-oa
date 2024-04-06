package com.banneroa.pojo;

import com.banneroa.mapper.OaClassTimeMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rjj
 * @date 2024/3/22 - 19:57
 */
@Data
@NoArgsConstructor
public class OaClassTime {
    private Long id;
    private Long memberId;
    private String day;
    private String status;
    private Integer orders;

    public OaClassTime(Long memberId,String day,String status,Integer orders){
        this.memberId = memberId;
        this.day = day;
        this.status = status;
        this.orders = orders;
    }

}
