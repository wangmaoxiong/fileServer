package com.wmx.www;

import java.io.File;

/**
 * Created by Administrator on 2019/3/17 0017.
 */
public class Test {
    public static void main(String[] args) {
        File file = new File("E:/wmx/uploadFiles/009.jpg");
        System.out.println(file.getPath());
        file.delete();
    }
}
