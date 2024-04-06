package com.banneroa.controller;

import cn.hutool.core.util.StrUtil;
import com.banneroa.service.IMinioService;
import com.banneroa.utils.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author rjj
 * @date 2023/12/2 - 15:17
 */
@RestController
public class CommonController {

    @Resource
    private IMinioService minioService;

    @RequestMapping("/upload")
    public ResponseResult upload(MultipartFile file) {
        String url = minioService.upload(file);
        return StrUtil.isNotBlank(url) ? ResponseResult.okResult(url)
                : ResponseResult.errorResult(500, "上传失败");
    }

}
