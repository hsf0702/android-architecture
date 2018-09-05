package com.klfront.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2016/3/2.
 */
public class FileUtils {
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot);
            }
        }
        return filename;
    }

    public static String getFileName(String path) {
        String filename = path;
        if ((filename != null) && (filename.length() > 0)) {
            int separate = filename.lastIndexOf('/');
            if ((separate > -1) && (separate < (filename.length()))) {
                filename = filename.substring(separate + 1);
            }
        }
        return filename;
    }

    public static String getFileNameNoEx(String filename) {
        filename = getFileName(filename);
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static String GetPathByUri(Context context, Uri uri) {
        String path;
        if (!TextUtils.isEmpty(uri.getAuthority())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{
                    MediaStore.Images.Media.DATA
            }, null, null, null);
            if (null == cursor) {
                return "";
            }
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        } else {
            path = uri.getPath();
        }
        return path;
    }


    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String dirPath) {
        File dir = new File(dirPath);
        deleteDirWithFile(dir);
    }

    public static void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete(); // 删除所有文件
            }
            else if (file.isDirectory()) {
                deleteDirWithFile(file); // 递规的方式删除文件夹
            }
        }
        dir.delete();// 删除目录本身
    }

    public static boolean copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return false;
        }

        if (!fromFile.isFile()) {
            return false;
        }

        if (!fromFile.canRead()) {
            return false;
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }

        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

//    public static File GetFileFromFileName(Context context, String dirName, String fileName) {
//        File mediaStorageDir = null;
//        try {
//            String dir = ((IQhContext) context.getApplicationContext()).GetAppModulePath(dirName);
//            mediaStorageDir = new File(dir);
//            if (!mediaStorageDir.exists()) {
//                mediaStorageDir.mkdirs();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        File f = new File(mediaStorageDir.getPath() + File.separator + fileName);
//        return f;
//    }

    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        fi.close();
        return buffer;
    }

    public static void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
//        if (!type.equals("*")) {
//            //大类过滤
//            type = type.split("/")[0] + "/*";
//        }
        intent.setDataAndType(UriUtils.GetUriForFileByFileProvider(context, file), type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            //手机里面没装对应类型的查看器应用，就会报错。
            //打开文件失败
            //大类过滤
            type = type.split("/")[0] + "/*";
//            intent.setDataAndType(Uri.fromFile(file), type);
            Uri uri = UriUtils.GetUriForFileByFileProvider(context, file);
            intent.setDataAndType(uri, type);
        }
    }

    public static void shareImage(Context context, File file) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));//此处一定要用Uri.fromFile(file),其中file为File类型，否则附件无法发送成功。
        share.putExtra(Intent.EXTRA_STREAM, UriUtils.GetUriForFileByFileProvider(context, file));
        context.startActivity(Intent.createChooser(share, "分享图片"));

        //针对发送文件 与上同，只是setType如下：
        //share.setType("*/*");

        //针对 text代码如下：
//        Intent share=new Intent(Intent.ACTION_SEND);
//        share.setType("text/plain");
//        share.putExtra(Intent.EXTRA_TEXT,"I'm being sent!!");
//        context.startActivity(Intent.createChooser(share,"Share Text"));
    }

    public static void shareMultiFiles(Context context, List<File> files, String title) {
        String mimeType = "*/*";
        ArrayList<Uri> uris = new ArrayList<Uri>();
        for (File file : files) {
//            Uri u = Uri.fromFile(file);
            Uri u = UriUtils.GetUriForFileByFileProvider(context, file);
            mimeType = getMIMEType(file);
            uris.add(u);
        }
        boolean multiple = uris.size() > 1;
        Intent intent = new Intent(multiple ? Intent.ACTION_SEND_MULTIPLE
                : Intent.ACTION_SEND);
        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        }
        context.startActivity(Intent.createChooser(intent, title));
    }

    private static String getMIMEType(File file) {

        String type = "*";
        String ext = getExtensionName(file.getPath()).toLowerCase();
        if (ext == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (ext.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
                break;
            }
        }
        return type;
    }

    private static String getMIMEType(Uri uri) {

        String type = "*";
        String ext = getExtensionName(uri.getPath()).toLowerCase();
        if (ext == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (ext.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
                break;
            }
        }
        return type;
    }

    private final static String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", ""},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/*"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*"}
    };

    public static int getFileIconResId(Context context, String ext) {
        Resources res = context.getResources();
        String packageName = context.getPackageName();
        int imageResId = res.getIdentifier("file_unknown", "drawable", packageName);
        //        int imageResId = res.getIdentifier("ic_launcher", "drawable", packageName);
        if (ext != null) {
            ext = ext.toLowerCase();
        } else {
            ext = "";
        }
        if (ext.equals(".weike") || ext.equals(".note") || ext.equals("weike") || ext.equals("note")) {
            imageResId = res.getIdentifier("weike", "drawable", packageName);
        } else if (ext.equals(".doc") || ext.equals(".docx")) {
            imageResId = res.getIdentifier("file_word", "drawable", packageName);
        } else if (ext.equals(".xls") || ext.equals(".xlsx")) {
            imageResId = res.getIdentifier("file_xls", "drawable", packageName);
        } else if (ext.equals(".ppt") || ext.equals(".pptx")) {
            imageResId = res.getIdentifier("file_ppt", "drawable", packageName);
        } else if (ext.equals(".pdf")) {
            imageResId = res.getIdentifier("file_pdf", "drawable", packageName);
        } else if (ext.equals(".txt")) {
            imageResId = res.getIdentifier("file_txt", "drawable", packageName);
        } else if (ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg")) {
            imageResId = res.getIdentifier("file_pic", "drawable", packageName);
        } else if (ext.equals(".avi") || ext.equals(".asf") || ext.equals(".wmv") || ext.equals(".avs") || ext.equals(".flv") || ext.equals(".mkv") || ext.equals(".mov") || ext.equals(".3gp") || ext.equals(".mp4") || ext.equals(".mpg") || ext.equals(".mpeg") || ext.equals(".dat") || ext.equals(".ogm") || ext.equals(".vob") || ext.equals(".rm") || ext.equals(".rmvb") || ext.equals(".ts") || ext.equals(".tp") || ext.equals(".ifo") || ext.equals(".nsv")) {
            //常见的视频文件格式：.AVI;   .ASF;   .WMV;   .AVS;   .FLV;   .MKV;   .MOV;   .3GP;   .MP4;   .MPG;   .MPEG;   .DAT;   .OGM;   .VOB;   .RM;   .RMVB;   .TS;   .TP;   .IFO;   .NSV
            imageResId = res.getIdentifier("file_video", "drawable", packageName);
        } else if (ext.equals(".mp3") || ext.equals(".aac") || ext.equals(".wma") || ext.equals(".cda") || ext.equals(".flac") || ext.equals(".m4a") || ext.equals(".mid") || ext.equals(".mka") || ext.equals(".mp2") || ext.equals(".mpa") || ext.equals(".mpc") || ext.equals(".ape") || ext.equals(".ofr") || ext.equals(".ogg") || ext.equals(".ra") || ext.equals(".wv") || ext.equals(".tta") || ext.equals(".ac3") || ext.equals(".dts") || ext.equals(".amr") || ext.equals(".aif") || ext.equals(".asf")) {
            //常见的音频文件格式：.MP3;   .AAC;   .WAV;   .WMA;   .CDA;   .FLAC;   .M4A;   .MID;   .MKA;   .MP2;   .MPA;   .MPC;   .APE;   .OFR;   .OGG;   .RA;   .WV;   .TTA;   .AC3;   .DTS
            imageResId = res.getIdentifier("file_music", "drawable", packageName);
        }

        return imageResId;
    }

    public static String getFileSize(File file) {
        if (file.exists()) {
            return getFileSize(file.length());
        }
        return "";
    }

    public static String getFileSize(long length) {
        if (length >= 1024 * 1024 * 1024) {
            float size = length * 1.0f / (1024 * 1024 * 1024);
            return String.format("%sGB", Round2Hundredth(size));
        } else if (length >= 1024 * 1024) {
            float size = length * 1.0f / (1024 * 1024);
            return String.format("%sMB", Round2Hundredth(size));
        } else if (length >= 1024) {
            float size = length * 1.0f / (1024);
            return String.format("%sKB", Round2Hundredth(size));
        } else {
            return String.format("%sByte", Round2Hundredth(length));
        }
    }

    public static float Round2Hundredth(float size) {
        float result = (float) (Math.round(size * 100)) / 100;
        return result;
    }

    public static boolean IsMatchFilter(String filter, String name) {
        String[] fileTypes = filter.split(";");
        for (String s : fileTypes) {
            boolean res = name.toLowerCase().endsWith(s.toLowerCase().replace("*", ""));
            if(res)
            {
                return res;
            }
        }
        return false;
    }

    /**
     * 清除不需用到的历史文件
     *
     * @param keepDays 保留天数
     */
    public static void ClearOldFiles(String dirPath, int keepDays) {
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            String[] filelist = dir.list();
            for (int i = 0; i < filelist.length; i++) {
                String filePath = dirPath + "/" + filelist[i];
                File file = new File(filePath);
                Long time = file.lastModified();

                if (file.exists() && DateUtils.getDistanceDay(time, System.currentTimeMillis()) > keepDays) {
                    file.delete();
                }
            }
        }
    }

}
