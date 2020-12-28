package com.wmx.www.controller;

import com.wmx.www.config.MyWebMvcConfigurer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 文件上传、下载控制层
 *
 * @author wangMaoXiong
 * Created by Administrator on 2019/3/17 0017.
 */
@Controller
public class UploadFileController {

    private static Logger logger = LoggerFactory.getLogger(UploadFileController.class);
    /**
     * 请求 url 中的资源映射，不推荐写死在代码中，最好提供可配置，如 /uploadFiles/**
     */
    @Value("${uploadFile.resourceHandler}")
    private String resourceHandler;

    /**
     * 上传文件保存的本地目录，使用@Value获取全局配置文件中配置的属性值，如 E:/wmx/uploadFiles/
     */
    @Value("${uploadFile.location}")
    private String uploadFileLocation;

    /**
     * 文件上传，因为只是演示，所以使用 @ResponseBody 将结果直接返回给页面
     *
     * @param singleFile ：上传的文件对象
     * @param request    ：请求对象
     * @return ：返回的是文件名称，将来客户端可以用来下载
     * @throws IOException
     */
    @PostMapping("fastdfs/uploadFile")
    @ResponseBody
    public String uploadFile(MultipartFile singleFile, HttpServletRequest request) throws IOException {
        if (singleFile == null || singleFile.isEmpty()) {
            logger.debug("上传文件为空...");
            return "上传文件为空...";
        }
        //basePath拼接完成后，形如：http://192.168.1.20:8080/fileServer
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String fileName = singleFile.getOriginalFilename();
        String fileServerPath = basePath + resourceHandler.substring(0, resourceHandler.lastIndexOf("/") + 1) + fileName;
        logger.debug("文件访问路径：{}", fileServerPath);

        File saveFile = new File(uploadFileLocation, fileName);
        /**
         * 文件保存，注意目的文件夹必须事先存在，否则保存时报错
         * 在{@link MyWebMvcConfigurer}中已经处理了，如果不存在，自动新建存储目录
         */
        singleFile.transferTo(saveFile);
        logger.info("文件保存路径：{}", saveFile.getPath());
        return "<a target='_blank' href='" + fileServerPath + "'>" + fileServerPath + "</a>";
    }

    /**
     * 附件上传
     * http://192.168.3.102:9393/fileServer/fastdfs/upload
     *
     * @param file   ：文件
     * @param region ：区划
     * @return
     * @throws IOException
     */
    @PostMapping(value = "fastdfs/upload", produces = {"text/plain;charset=utf-8"})
    @ResponseBody
    public String uploadAffix(MultipartFile file, String region, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=utf-8");

        //允许所有域名的脚本访问该资源
        response.setHeader("Access-Control-Allow-Origin", "*");
        logger.info("fileName={},region={}", file.getName(), region);

        String fileName = file.getOriginalFilename().trim();
        String extName = fileName.substring(fileName.lastIndexOf("."));
        String id = UUID.randomUUID().toString().replace("-", "") + extName;
        File saveFile = new File(uploadFileLocation, id);
        file.transferTo(saveFile);
        logger.debug("文件上传保存路径：{}", saveFile.getPath());
        return id;
    }

    /**
     * 文件下载 · 使用 FileInputStream 读取文件内容
     * http://192.168.3.127:9393/fileServer/fastdfs/download1?id=f41e995ba7454a9da1bcd975c7460fcd.png
     *
     * @param response
     * @param id       ：文件名称，如 小白猫.png
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/fastdfs/download1"}, method = {RequestMethod.GET})
    public void download1(HttpServletResponse response, String id) {
        try {
            logger.debug("文件下载：{}", id);
            //构建文件输入流。推荐使用：org.apache.commons.io.FileUtils.readFileToByteArray
            File saveFile = new File(uploadFileLocation, id);
            if (!saveFile.exists()) {
                //设置头信息
                response.setContentType("text/html;charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write("【"+id + "】文件不存在！");
                logger.warn("【{} 】文件不存在！", id);
                writer.flush();
                writer.close();
                return;
            }
            InputStream inputStream = new FileInputStream(saveFile);
            //设置返回类型，必须对文件名称进行编码，否则中午容易乱码
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(id, "UTF-8"));

            //写入输出流返回给客户端
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            int length;
            byte[] bytes = new byte[1024];
            while ((length = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            logger.info("文件下载完成：{}", id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载 · 使用 FileUtils.readFileToByteArray 读取文件内容
     * http://192.168.3.127:9393/fileServer/fastdfs/download2?id=f41e995ba7454a9da1bcd975c7460fcd.png
     *
     * @param response
     * @param id       ：文件名称，如 123.xtx
     * @return
     */
    @RequestMapping(value = {"/fastdfs/download2"}, method = {RequestMethod.GET})
    public void download2(HttpServletResponse response, String id) {
        OutputStream outputStream = null;
        try {
            logger.debug("文件下载：{}", id);
            File saveFile = new File(uploadFileLocation, id);
            if(saveFile.exists()){
                //设置返回类型，必须对文件名称进行编码，否则中午容易乱码
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(id, "UTF-8"));

                //构建文件输入流。推荐使用：org.apache.commons.io.FileUtils.readFileToByteArray
                byte[] byteArray = FileUtils.readFileToByteArray(saveFile);

                //写入输出流返回给客户端
                outputStream = new BufferedOutputStream(response.getOutputStream());
                if (byteArray != null && byteArray.length > 0) {
                    outputStream.write(byteArray);
                }
            } else {
                //设置头信息
                response.setContentType("text/html;charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write("【"+id + "】文件不存在！");
                logger.warn("【{} 】文件不存在！", id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("文件下载完成：{}", id);
    }

}
