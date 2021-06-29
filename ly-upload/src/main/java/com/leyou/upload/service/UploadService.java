package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.upload.config.OssConfig;
import com.leyou.upload.config.OssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UploadService {

    //读取属性
    @Value("${ly.upload.imagePath}")
    private String imagePath;//nginx文件保存目录

    @Value("${ly.upload.imageUrl}")
    private String imageUrl;//图片的访问路径

    @Autowired
    private OSS ossClient;
    @Autowired
    private OssProperties ossProps;




    public String uploadImage(MultipartFile file) {

        //判断该文件是否为图片类型
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                //该文件不是图片流
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }


        //需求：把图片保存到nginx目录下
        //使用UUID生成随机文件名称
        String uuid = UUID.randomUUID().toString();
        //获取文件原名
        String originalFilename = file.getOriginalFilename();
        //获取后缀名
        String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //最终的名称
        String fileName = uuid+extName;

        /**
         * 参数一：保存目录
         * 参数二：文件名称
         */
        try {
            file.transferTo(new File(imagePath,fileName));

            //返回图片的访问路径
            return imageUrl+fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

    }

    public Map<String, String> ossSignature() {
        try {
            long expireTime = ossProps.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, ossProps.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, ossProps.getDir());

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessId", ossProps.getAccessKeyId());//注意：返回的name和前端接收的name保持一致
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", ossProps.getDir());
            respMap.put("host", ossProps.getHost());
            respMap.put("expire", String.valueOf(expireEndTime));

            return respMap;
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_SIGN);
        } finally {
            ossClient.shutdown();
        }
    }
}

