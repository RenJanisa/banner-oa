package com.banneroa.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.banneroa.service.IMinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

/**
 * @author rjj
 * @date 2023/11/28 - 15:26
 */
@Service
@Slf4j
public class MinioServiceImpl implements IMinioService {

    @Value("${minio.bucket.files}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String address;

    @Resource
    private MinioClient minioClient;

    @Override
    public String upload(MultipartFile file) {

        //将文件字节输入到内存流中
        //获取文件类型
        String contentType = file.getContentType();
        try {
            String fileName = DigestUtil.md5Hex(file.getBytes())+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes());
            String path = "leaveProof/" + fileName;
            PutObjectArgs putObjectArgs =
                    PutObjectArgs.builder().bucket(bucket).object(path)
                            //-1 表示文件分片按 5M(不小于 5M,不大于 5T),分片数量最大10000
                            .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                            .contentType(contentType)
                            .build();
            minioClient.putObject(putObjectArgs);
            return address+"/"+bucket+"/"+path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(String pathUrl) {
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(pathUrl).build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("minio remove file error.  pathUrl:{}",pathUrl);
            e.printStackTrace();
        }
        return true;
    }
}
