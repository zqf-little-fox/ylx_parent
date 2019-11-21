package cn.zqf.core.controller;

import cn.zqf.core.entity.Result;
import cn.zqf.core.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {
    //读取application.Properties配置文件内容
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        try{
            FastDFSClient fastDFS = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            String path = fastDFS.uploadFile(file.getBytes(),file.getOriginalFilename(),file.getSize());
            return new Result(true,FILE_SERVER_URL+path);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
