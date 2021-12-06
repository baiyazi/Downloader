package com.weizu.filedownloader2.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类，将文件的long类型的数据转换为可读的字符串表示的转换方法
 * @author  梦否
 * @version 1.0.1
 * @since 1.0.1
 */
public class FormatUtil {

    public static boolean checkNULL(String url) {
        return TextUtils.isEmpty(url) || url.equalsIgnoreCase("null");
    }

    /**
     * 判断是否含有中文字符
     *
     * @param text
     * @return {@code true}:是<br>{@code false}:否
     */
    public static boolean hasDoubleCharacter(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        char[] charArray = text.toCharArray();
        for (char ch : charArray) {
            if (ch >= 0x0391 && ch <= 0xFFE5) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换链接中中文字符
     *
     * @param url
     * @return
     */
    public static String convertUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        if (hasDoubleCharacter(url)) {
            String regex = "[^\\x00-\\xff]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);
            Set<String> strs = new HashSet<>();
            while (matcher.find()) {
                strs.add(matcher.group());
            }

            try {
                for (String str : strs) {
                    url = url.replaceAll(str, URLEncoder.encode(str, "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * 格式化文件大小
     *
     * @param fileSize
     * @return
     */
    public static String formatFileSize(long fileSize) {
        if (fileSize < 0) {
            return "0kb";
        }
        double kiloByte = fileSize / 1024;
        if (kiloByte < 1) {
            return fileSize + "b";
        }

        double megaByte = kiloByte / 1024;
        BigDecimal result;
        if (megaByte < 1) {
            result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "kb";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "mb";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "gb";
        }
        result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "tb";
    }
}
