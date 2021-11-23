package com.weizu.mylibrary2.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 将url转化为一个较短的字符串表示
 * @auth 梦否
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 10:11:31
 */
public class EncoderUtils {

    /**
     * @param url 下载url
     * @return hash码
     */
    public static String hashKeyFromUrl(String url){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(url.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                String a = Integer.toHexString(aByte >> 4 & 0b00001111);
                String b = Integer.toHexString(aByte & 0b00001111);
                sb.append(a).append(b);
            }
            return sb.toString();  // 返回16进制的MD5值的字符串表示
        }catch (NoSuchAlgorithmException e){  //找不到md5算法，或者加密过程出现异常
            return String.valueOf(url.hashCode());  // 返回哈希码的字符串表示
        }
    }
}
