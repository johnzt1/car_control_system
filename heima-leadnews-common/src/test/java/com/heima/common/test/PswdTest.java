package com.heima.common.test;

import com.heima.utils.common.BCrypt;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.DigestUtils;

public class PswdTest {

    public static void main(String[] args) {
        //md5加密
//        String pswd = DigestUtils.md5DigestAsHex("abc".getBytes());
//        System.out.println(pswd);//900150983cd24fb0d6963f7d28e17f72

        //手动加盐的方式  salt:随机字符串
//        String salt = RandomStringUtils.randomAlphanumeric(10);
//        System.out.println(salt);
        String salt = "123456";
        String password = "guest"+salt;
        String saltPswd = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(saltPswd);//476b6771b847f6fb6076556d89ace49f

        //生产盐
        /*String gensalt = BCrypt.gensalt();
        System.out.println(gensalt);
        //加密操作
        String saltPassword = BCrypt.hashpw("abc", gensalt);
        System.out.println(saltPassword);//$2a$10$ZLWvpXwyQDRk52xXvkiCpuLZUXiPmO0.XVhik6n76TZcvgtNJ4Na.

        //校验密码
        boolean abc = BCrypt.checkpw("abc", "$2a$10$ZLWvpXwyQDRk52xXvkiCpuLZUXiPmO0.XVhik6n76TZcvgtNJ4Na.");
        System.out.println(abc);*/

    }
}
