package com.heima.admin.test;

import org.apache.commons.codec.digest.DigestUtils;

public class PswdTest {

    public static void main(String[] args) {
        //md5
       /* String pwsd = DigestUtils.md5Hex("adc".getBytes());
        System.out.println(pwsd);*/
        String imageName = "group1/M00/00/00/wKjIgl892suAAHHxAACr_szTy3c449.jpg";
        int i = imageName.indexOf("/");
        String groupName = imageName.substring(0,i);
        String imagePath = imageName.substring(i+1);
        System.out.println(groupName);
        System.out.println(imagePath);
    }
}
