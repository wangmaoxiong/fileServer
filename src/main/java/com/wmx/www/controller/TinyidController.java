package com.wmx.www.controller;

import com.wmx.www.util.TinyidHelper;
import com.xiaoju.uemc.tinyid.client.utils.TinyId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/12/28 20:27
 */
@RestController
public class TinyidController {

    /**
     * http://127.0.0.1:9393/fileServer/tinyid/nextId
     * <p>
     * Long nextId(String bizType)：获取单个 id
     * List<Long> nextId(String bizType, Integer batchSize) ：批量获取 id
     * 1）bizType：表示业务类型 id，对应 tiny_id_info 表的 bizType 字段，数据库中必须事先配置好，否则 TinyIdSysException 异常
     * 2）如果 bizType 或者 token 没有在数据库中事先配置，则方法调用抛 TinyIdSysException 异常
     * 3）通常一个应用对应唯一的 bizType 与 token，所以都可以配置到配置文件中去，为了演示方便才暂时写死。
     *
     * @return
     */
    @GetMapping("tinyid/nextId")
    public String nextId() {
        Long test = TinyId.nextId("test");

        System.out.println("test=" + test);

        List<Long> longList = TinyId.nextId("test", 10);
        System.out.println("longList=" + longList);
        return longList.toString();
    }

    /**
     * http://127.0.0.1:9393/fileServer/tinyid/randomTinyid?size=10
     * 批量获取分布式id，且对 Tinyid 结果随机插入字母，防止被扫库
     *
     * @param size ：批量生成的个数
     * @return
     */
    @GetMapping("tinyid/randomTinyid")
    public List<String> randomTinyid(@RequestParam Integer size) {
        size = size == null ? 10 : size < 0 ? 10 : size;
        List<Long> longList = TinyId.nextId("test", size);
        System.out.println("longList=" + longList);
        List<String> randomTinyid = TinyidHelper.randomTinyid(longList);
        return randomTinyid;
    }
}
