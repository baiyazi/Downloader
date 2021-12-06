package com.weizu.filedownloader2.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 编码工具类，主要完成对字符串、文件、InputStream的md5值的得到，方便进行文件是否存在判断。
 * @author  梦否
 * @version 1.0.1
 * @since 1.0
 */
public class EncoderUtil {

    /**
     * 根据字符串生成MD5
     * @param param 下载url
     * @return hash码
     */
    public static String hashKeyFromUrl(String param){
        if (param == null || param.length() == 0) {
            return "";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(param.getBytes());
            return new BigInteger(1, md5.digest()).toString(16); // 返回16进制的MD5值的字符串表示
        }catch (NoSuchAlgorithmException e){  //找不到md5算法，或者加密过程出现异常
            return String.valueOf(param.hashCode());  // 返回哈希码的字符串表示
        }
    }

    /**
     * 根据流生成MD5
     * @param is 流
     * @return MD5
     */
    public static String getMD5(InputStream is) {
        String md5 = "";
        try {
            if (is == null) {
                return md5;
            }

            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            md5 = String.format("%32s", bigInt.toString(16).replace(' ', '0'));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 根据文件生成MD5
     * @param file 文件
     * @return MD5
     */
    public static String getMD5(File file) {
        String md5 = "";
        try {
            InputStream is = new FileInputStream(file);
            md5 = getMD5(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 检测文件的MD5
     * @param md5  MD5
     * @param file 文件
     * @return {@code true}:是<br>{@code false}:否
     */
    public static boolean checkMD5(String md5, File file) {
        if (TextUtils.isEmpty(md5) || file == null) {
            return false;
        }
        String fileMD5 = getMD5(file);
        return fileMD5 != null && fileMD5.equalsIgnoreCase(md5);
    }

    public static boolean checkMD5(String md5, InputStream is) {
        if (TextUtils.isEmpty(md5) || is == null) {
            return false;
        }
        String isMD5 = getMD5(is);
        return isMD5 != null && isMD5.equalsIgnoreCase(md5);
    }
}
