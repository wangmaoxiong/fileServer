package com.wmx.www.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Tinyid 辅助类，将原本的 long 型 id转为字符串类型，处理方式是往结果中随机插入字符。
 * 本文是随机插入大小写字母，如果想更加复杂，则可以继续修改，生成随机字母使用的是 {@link RandomStringUtils#randomAlphabetic(int)}
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/12/29 15:31
 */
public class TinyidHelper {

    /**
     * 将 Long 类型的值转为 String 类型，同时往其中插入3个随机字母((a-z, A-Z))
     *
     * @param tinyid ：待转换的数据，如果为 null，则返回空
     * @return
     */
    public static String randomTinyid(Long tinyid) {
        return randomTinyid(tinyid, 3);
    }

    /**
     * 将 Long 类型的值转为 String 类型，同时往其中插入随机字母((a-z, A-Z))，批量转换
     *
     * @param tinyid ：待转换的数据，如果为 null 或为空，则返回空
     * @return
     */
    public static List<String> randomTinyid(List<Long> tinyid) {
        return randomTinyid(tinyid, 3);
    }

    /**
     * 将 Long 类型的值转为 String 类型，同时往其中插入随机字母((a-z, A-Z))，批量转换
     *
     * @param tinyid ：待转换的数据，如果为 null 或为空，则返回空
     * @return
     */
    public static List<String> randomTinyid(List<Long> tinyid, int alphSize) {
        if (tinyid == null || tinyid.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> randomTinyidList = new ArrayList<>(tinyid.size());
        tinyid.stream().forEach(item -> randomTinyidList.add(randomTinyid(item, alphSize)));
        return randomTinyidList;
    }

    /**
     * 将 Long 类型的值转为 String 类型，同时往其中插入随机字母((a-z, A-Z))
     *
     * @param tinyid   ：待转换的数据，如果为 null，则返回空
     * @param alphSize ：随机插入字母的个数
     * @return
     */
    public static String randomTinyid(Long tinyid, int alphSize) {
        if (tinyid == null) {
            return "";
        }
        alphSize = alphSize < 0 ? 3 : alphSize;
        StringBuffer buffer = new StringBuffer(tinyid.toString());
        /**
         * 1、生成指定长度的随机字符串，字符从 (a-z, A-Z) 中选择
         * 2、被插入的字母是随机生成的，每次插入的单个字母的位置是随机的
         */
        String randomAlphabetic = RandomStringUtils.randomAlphabetic(alphSize);
        for (int i = 0; i < randomAlphabetic.length(); i++) {
            int length = buffer.length();
            //）nextInt(int bound)：生成的是 [0,bound)之间的随机数，StringBuffer 插入时可以包含 上限(length)
            int nextInt = new Random().nextInt(length + 1);
            //）往目标字符串中的随机位置插入随机字符
            buffer.insert(nextInt, randomAlphabetic.charAt(i));
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        System.out.println("------测试1------");

        System.out.println(randomTinyid(-1L));
        System.out.println(randomTinyid(-21L));
        System.out.println(randomTinyid(-241L));
        System.out.println(randomTinyid(0L));
        System.out.println(randomTinyid(10L));
        System.out.println(randomTinyid(104L));

        System.out.println("------测试2------");

        System.out.println(randomTinyid(-1L, 1));
        System.out.println(randomTinyid(-21L, 0));
        System.out.println(randomTinyid(-241L, 3));
        System.out.println(randomTinyid(0L, -4));
        System.out.println(randomTinyid(10L, 5));
        System.out.println(randomTinyid(104L, 6));

        System.out.println("------测试3------");
        System.out.println(randomTinyid(Arrays.asList(new Long[]{-123L, -23L, 0L, 1L, 23L, 44L})));
        System.out.println(randomTinyid(Arrays.asList(new Long[]{-34323L, -3L, 0L, 231L, 231233L, 4231234L})));

        System.out.println("------测试4------");
        System.out.println(randomTinyid(Arrays.asList(new Long[]{-123L, -23L, 0L, 1L, 23L, 44L}), 1));
        System.out.println(randomTinyid(Arrays.asList(new Long[]{-34323L, -3L, 0L, 231L, 231233L, 4231234L}), 6));
    }
}
