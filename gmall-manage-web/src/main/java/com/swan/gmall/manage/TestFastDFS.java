package com.swan.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.hibernate.validator.internal.constraintvalidators.hv.ParameterScriptAssertValidator;

import java.io.IOException;

public class TestFastDFS {

    public static void main(String[] args) {
        try {
            ClientGlobal.init("tracker.conf");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();

            StorageClient storageClient = new StorageClient(connection, null);

            // 上传文件
            String[] file = storageClient.upload_file("F:\\图片\\亲爱的\\a.png", "png", null);
            for (String s : file) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

    }

}
