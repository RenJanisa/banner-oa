package com.banneroa.pojo;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author rjj
 * @since 2023-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OaLeave implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Long memberId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String reason;

    private String proof;

    /**
     * 0:未通过;1:通过
     */
    private Integer status;


}
