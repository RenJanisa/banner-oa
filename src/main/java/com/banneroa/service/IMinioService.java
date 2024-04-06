package com.banneroa.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author rjj
 * @date 2023/11/28 - 15:25
 */
public interface IMinioService {

    String upload(MultipartFile multipartFile);

    boolean delete(String path);

}
