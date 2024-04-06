package com.banneroa.pojo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class OaMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学号")
    @NotNull(message = "学号不为空!")
    private Long id;

    @ApiModelProperty(value = "姓名")
    @NotBlank(message = "姓名不为空!")
    private String bname;

    @ApiModelProperty(value = "班级")
    @NotBlank(message = "班级不为空!")
    private String className;

    @ApiModelProperty(value = "方向")
    @NotBlank(message = "方向不为空!")
    private String direction;

    @NotBlank(message = "密码不为空!")
    private String password;

    @NotBlank(message = "qq号不为空")
    private String qq;

    /**
     * 1:管理员;0:普通成员
     */
    @ApiModelProperty(hidden = true)
    private int flag;


}
