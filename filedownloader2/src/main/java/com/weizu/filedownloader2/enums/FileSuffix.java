package com.weizu.filedownloader2.enums;

/**
 * 下载支持的文件类型后缀
 * @author  梦否
 * @version 1.0
 * @since 1.0
 */
public enum FileSuffix{
    EXE("exe"),
    ZIP("zip"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    MP4("mp4"),
    MP3("mp3"),
    PDF("pdf");

    // 实际值
    private String value;
    FileSuffix(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
