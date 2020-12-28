package com.wmx.www.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author wangMaoXiong
 * Created by Administrator on 2019/3/17 0017.
 * <p>
 * WebMvc 扩展配置类，应用一启动，本类就会执行
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(MyWebMvcConfigurer.class);

    /**
     * 请求 url 中的资源映射，不推荐写死在代码中，最好提供可配置，如 /uploadFiles/**
     */
    @Value("${uploadFile.resourceHandler}")
    private String resourceHandler;

    /**
     * 上传文件保存的本地目录，使用@Value获取全局配置文件中配置的属性值，如 E:/wmx/uploadFiles/
     */
    @Value("${uploadFile.location}")
    private String location;

    /**
     * 如果上传文件保存的本地目录不存在，则创建，否则后期保存文件时，容易出现找不到路径的错误
     */
    @PostConstruct
    public void init() {
        File file = new File(location);
        if (!file.exists()) {
            file.mkdirs();
            logger.debug("服务器文件存在目录={}，已经不存在，进行新建。", location);
        } else {
            logger.debug("服务器存储目录已经存在={}", location);
        }
    }

    /**
     * 配置静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //就是说 url 中出现 resourceHandler 匹配时，则映射到 location 中去,location 相当于虚拟路径
        //映射本地文件时，开头必须是 file:/// 开头，表示协议
        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///" + location);
    }
}
