package com.swan.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UploadFileUtil {

    public static String imgUpload(MultipartFile multipartFile){
        String imgUrl = "http://192.168.159.21";

        try {
            ClientGlobal.init("tracker.conf");

            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();

            StorageClient storageClient = new StorageClient(connection, null);

            // 获取文件的后缀格式名
            String filename = multipartFile.getOriginalFilename();
            String substring = filename.substring(filename.lastIndexOf(".") + 1);

            // 上传文件
            String[] file = storageClient.upload_file(multipartFile.getBytes(), substring, null);

            for (String s : file) {
                imgUrl = imgUrl + "/" +  s;
            }
            return imgUrl;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
            throw new RuntimeException("上传异常");
        }
        return null;
    }
}
