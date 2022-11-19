package com.itheima.reggie.controller;

import com.itheima.reggie.commom.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/*文件上传和下载*/
@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    //文件上传
    @PostMapping("/upload")
    public R<String> uploda(MultipartFile file){
        String s1 = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //file是一个临时文件，要及时转存，否则本次请求后就会删除
        //获得原始文件名或使用UUid重新生成文件名，防止覆盖
        String s = UUID.randomUUID().toString()+s1;

        //判断目录存在
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        //转存
        try {
            file.transferTo(new File(basePath+s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info(file.toString());
        return R.success(s);
    }
    //文件下载
    @GetMapping("/download")
    public  void   download(String name, HttpServletResponse response){
        //构造输入流
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpge");

            int len=0;
            byte[] bytes = new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
            }
            fileInputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
