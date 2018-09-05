package com.klfront.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by L on 2017/4/7.
 */

public class UriUtils {

    /**
     * 使用FileProvider方式获取文件的Uri
     * 该方法适用情况： AndroidManifest声明的provider下android:authorities的值为包名.fileprovider
     * @param context
     * @param file 文件
     * @return
     */
    public static Uri GetUriForFileByFileProvider(Context context, File file) {
        String authority = context.getPackageName()+".fileprovider";
        return GetUriForFileByFileProvider(context,authority,file);
    }

    /**
     * 使用FileProvider方式获取文件的Uri
     * @param context
     * @param authority fileProvider授权：AndroidManifest中声明的provider下android:authorities的值。
     * @param file 文件
     * @return
     */
    public static Uri GetUriForFileByFileProvider(Context context, String authority,File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            // return address for android 7+ like huawei P10
            if (isHuaweiDiskUri(uri)) {
                String path = uri.getPath();
                path = path.substring("/root".length());
                return path;
            }
            try {
                return getDataColumn(context, uri, null, null);
            } catch (Exception e) {
                //return address for android 7+
                String path = uri.getPath();
                if(path.startsWith("/root")) {
                    path = path.substring("/root".length());
                }
                return path;
            }
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isHuaweiDiskUri(Uri uri) {
        return "com.huawei.hidisk.fileprovider".equals(uri.getAuthority());
    }

    public static String selectImage(Context context, Intent data) {
        Uri selectedImage = data.getData();
//      Log.e(TAG, selectedImage.toString());
        if (selectedImage != null) {
            String uriStr = selectedImage.toString();
            String path = uriStr.substring(10, uriStr.length());
            if (path.startsWith("com.sec.android.gallery3d")) {
                Log.e(TAG, "It's auto backup pic path:" + selectedImage.toString());
                return null;
            }
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    public static String Combine(String baseurl, String part)
    {
        if (baseurl.endsWith("/") && part.startsWith("/"))
        {
            return String.format("%s%s", baseurl, part.substring(1));
        }
        else if (!baseurl.endsWith("/") && !part.startsWith("/"))
        {
            return String.format("%s/%s", baseurl, part);
        }
        return String.format("%s%s", baseurl, part);
    }

    public static String Encode(String str) throws UnsupportedEncodingException {
        String zhPattern = "[\u4e00-\u9fa5]+";
        Pattern p = Pattern.compile(zhPattern);
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), "UTF-8"));
        }
        m.appendTail(b);
        return b.toString();
    }
}
