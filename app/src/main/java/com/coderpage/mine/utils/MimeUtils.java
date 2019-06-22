package com.coderpage.mine.utils;

import java.io.File;

/**
 * @author lc. 2019-06-20 18:05
 * @since 0.7.0
 *
 * 通过文件后缀获取 MIME
 */
public class MimeUtils {

    public static String getMimeBySuffix(File file) {
        if (file == null) {
            return getMimeBySuffix("");
        }

        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex < 0) {
            return getMimeBySuffix("");
        }

        String suffix = name.substring(dotIndex, name.length()).toLowerCase();
        return getMimeBySuffix(suffix);
    }

    public static String getMimeBySuffix(String suffix) {
        switch (suffix) {
            case ".m3u":
                return "audio/x-mpegurl";
            case ".jpeg":
            case ".jpg":
                return "image/jpeg";
            case ".wma":
                return "audio/x-ms-wma";
            case ".bmp":
                return "image/bmp";
            case ".gif":
                return "image/gif";
            case ".tgz":
                return "application/x-compressed";
            case ".avi":
                return "video/x-msvideo";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".m4v":
                return "video/x-m4v";
            case ".jar":
                return "application/java-archive";
            case ".zip":
                return "application/x-zip-compressed";
            case ".wps":
                return "application/vnd.ms-works";
            case ".wav":
                return "audio/x-wav";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".m4a":
            case ".m4b":
            case ".m4p":
                return "audio/mp4a-latm";
            case ".msg":
                return "application/vnd.ms-outlook";
            case ".rmvb":
                return "audio/x-pn-realaudio";
            case ".c":
            case ".conf":
            case ".cpp":
            case ".h":
            case ".java":
            case ".log":
            case ".prop":
            case ".rc":
            case ".sh":
            case ".txt":
            case ".xml":
                return "text/plain";
            case ".gz":
                return "application/x-gzip";
            case ".mp2":
            case ".mp3":
                return "audio/x-mpeg";
            case ".png":
                return "image/png";
            case ".bin":
            case ".class":
            case ".exe":
                return "application/octet-stream";
            case ".doc":
                return "application/msword";
            case ".pdf":
                return "application/pdf";
            case ".z":
                return "application/x-compress";
            case ".asf":
                return "video/x-ms-asf";
            case ".3gp":
                return "video/3gpp";
            case ".js":
                return "application/x-javascript";
            case ".m4u":
                return "video/vnd.mpegurl";
            case ".mpc":
                return "application/vnd.mpohun.certificate";
            case ".pps":
            case ".ppt":
                return "application/vnd.ms-powerpoint";
            case ".apk":
                return "application/vnd.android.package-archive";
            case ".mpga":
                return "audio/mpeg";
            case ".ogg":
                return "audio/ogg";
            case ".wmv":
                return "audio/x-ms-wmv";
            case ".rtf":
                return "application/rtf";
            case ".tar":
                return "application/x-tar";
            case ".htm":
            case ".html":
                return "text/html";
            case ".mpe":
            case ".mpeg":
            case ".mpg":
                return "video/mpeg";
            case ".pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".gtar":
                return "application/x-gtar";
            case ".mov":
                return "video/quicktime";
            case ".mp4":
            case ".mpg4":
                return "video/mp4";

            default:
                return "*/*";
        }
    }
}
