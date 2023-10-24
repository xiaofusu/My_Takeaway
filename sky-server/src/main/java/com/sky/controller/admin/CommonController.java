package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @Author wzy
 * @Date 2023/10/23 18:22
 * @description: 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api("通用注解")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        //file是一个临时文件 需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        String filePath = null;
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            //使用uuid重新生成文件名，防止文件名重复造成文件覆盖
            String fileName = UUID.randomUUID().toString()+suffix;

            filePath = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{}",e);
        }
        return null;
    }
//    /**
//     * 文件下载
//     * @param name
//     * @param response
//     */
//    @GetMapping("/download")
//    public void downLoad(String name, HttpServletResponse response){
//
//        try {
//            //输入流，通过输入流读取文件
//            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
//            //输出流，通过输出流将文件写回浏览器，在浏览器显示图片
//            ServletOutputStream outputStream = response.getOutputStream();
//            response.setContentType("image/jpg");
//            int length = 0;
//            byte[] bytes = new byte[1024];
//            while((length=fileInputStream.read(bytes))!=-1){
//                outputStream.write(bytes,0,length);
//                outputStream.flush();
//            }
//            outputStream.close();
//            fileInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
