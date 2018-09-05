package com.klfront.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * Created by lujinlong on 2016/6/2.
 */

public class ImageLoaderUtils {
    public interface IQhImageLoadComplete {
        void onLoadingStarted(String s, View view);

        void OnComplete(String s, View view, Bitmap bitmap);

        void onLoadingFailed(String s, View view);
    }

    private DisplayImageOptions FDisplayOptions = null;

    private static ImageLoaderUtils instance = null;
    private Context FContext = null;

    public static ImageLoaderUtils getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoaderUtils(context);
        }
        return instance;
    }

    private ImageLoaderUtils(Context context) {
        FContext = context;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(800, 760).denyCacheImageMultipleSizesInMemory().memoryCache(new LRULimitedMemoryCache(4 * 1024 * 1024)).defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .discCacheSize(1 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
        FDisplayOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .considerExifParams(true)                        //启用EXIF和JPEG图像格式
                //.displayer(new RoundedBitmapDisplayer(5))       //图片圆角显示，值为整数 这样耗内存比较大
                .bitmapConfig(Bitmap.Config.RGB_565).build();

    }

    public Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
//        //取最短边做边长
//        if (width > height) {
//            r = height;
//        } else {
//            r = width;
//        }

        //取最短长做边长
        if (width > height) {
            r = width;
        } else {
            r = height;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);

        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    public Bitmap toRoundCornerBitmap(Bitmap bitmap, int radius) {
        //圆角图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);

        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, width, height);
        //通过制定的rect画一个圆角矩形
        canvas.drawRoundRect(rect, radius, radius, paint);

        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    /**
     * 加载图片 支持回调
     *
     * @param url      图片网路地址
     * @param callback
     */
    public void LoadImage(String url, final IQhImageLoadComplete callback) {
        if (NetUtils.IsNetworkConnected(FContext)) {
            File file = GetCacheFile(url);
            if (file.exists()) {
                LoadImageOnly(GetCacheUrl(url), callback);
            } else {
                LoadAndCacheImage(url, callback);
            }
        } else {
            String cacheurl = GetCacheUrl(url);
            LoadAndCacheImage(cacheurl, callback);
        }
    }

    public void LoadImage(final String url, int imageside, final IQhImageLoadComplete callback) {
        if (NetUtils.IsNetworkConnected(FContext)) {
            File file = GetCacheFile(url);
            if (file.exists()) {
                LoadImageOnly(GetCacheUrl(url), imageside, callback);
            } else {
                LoadAndCacheImage(url, imageside, callback);
            }
        } else {
            String cacheurl = GetCacheUrl(url);
            LoadAndCacheImage(cacheurl, imageside, callback);
        }
    }

    private void LoadImageOnly(final String url, final IQhImageLoadComplete callback) {
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                callback.OnComplete(s, view, bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private void LoadAndCacheImage(final String url, final IQhImageLoadComplete callback) {
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                callback.onLoadingFailed(s, view);
            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                callback.OnComplete(s, view, bitmap);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = GetCacheFile(url);
                        BitmapUtils.saveBitmap(bitmap, file);
                    }
                }).start();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private void LoadImageOnly(final String url, int imageside, final IQhImageLoadComplete callback) {
        ImageSize imageSize = new ImageSize(imageside, imageside, 0);
        ImageLoader.getInstance().loadImage(url, imageSize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                callback.OnComplete(s, view, bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private void LoadAndCacheImage(final String url, int imageside, final IQhImageLoadComplete callback) {
        ImageSize imageSize = new ImageSize(imageside, imageside, 0);
        ImageLoader.getInstance().loadImage(url, imageSize, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                callback.OnComplete(s, view, bitmap);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = GetCacheFile(url);
                        BitmapUtils.saveBitmap(bitmap, file);
                    }
                }).start();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }


    public void DisplayIcon(String url, ImageView imageview) {
        DisplayIcon(url, imageview, false);
    }

    public void DisplayIcon(String url, ImageView imageview, boolean isRound) {
        if (!url.endsWith("_128.png")) {
            url = url.replace(".png", "_128.png");
        }
        DisplayImage(url, imageview, isRound, FDisplayOptions);
    }

    public void DisplayIcon(String url, @DrawableRes int defaultimg, ImageView imageView) {
        DisplayIcon(url, defaultimg, imageView, false);
    }

    public void DisplayIcon(String url, @DrawableRes int defaultimg, ImageView imageview, boolean isRound) {
        if (!url.endsWith("_128.png")) {
            url = url.replace(".png", "_128.png");
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).showImageForEmptyUri(defaultimg).showImageOnFail(defaultimg).bitmapConfig(Bitmap.Config.RGB_565).build();
        DisplayImage(url, imageview, isRound, options);
    }


    public void DisplayImage(String url, ImageView imageview) {
        DisplayImage(url, imageview, false, FDisplayOptions);
    }

    public void DisplayImage(String url, ImageView imageview, boolean isRound) {
        DisplayImage(url, imageview, isRound, FDisplayOptions);
    }

    public void DisplayImage(String url, @DrawableRes int defaultimg, ImageView imageView) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).showImageForEmptyUri(defaultimg).showImageOnFail(defaultimg).bitmapConfig(Bitmap.Config.RGB_565).build();
        DisplayImage(url, imageView, false, options);
    }

    public void DisplayImage(String url, @DrawableRes int defaultimg, ImageView imageview, boolean isRound) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(false).showImageForEmptyUri(defaultimg).showImageOnFail(defaultimg).bitmapConfig(Bitmap.Config.RGB_565).build();
        DisplayImage(url, imageview, isRound, options);
    }

    /**
     * 根据网络状态显示图片
     *
     * @param url       图片文件地址或网络访问地址
     * @param imageview 要显示图片的控件
     * @param isRound   是否显示为圆形的图
     * @param options
     */
    private void DisplayImage(String url, ImageView imageview, boolean isRound, DisplayImageOptions options) {
        if (NetUtils.IsNetworkConnected(FContext)) {
            File file = GetCacheFile(url);
            if (file.exists()) {
                DisplayImageOnly(GetCacheUrl(url), imageview, isRound, options);
            } else {
                DisplayAndCacheImage(url, imageview, isRound, options);
            }
        } else {
            url = GetCacheUrl(url);
            DisplayImageOnly(url, imageview, isRound, options);
        }
    }

    /**
     * 显示图片,不缓存文件
     *
     * @param url       图片文件地址或网络访问地址
     * @param imageview 要显示图片的控件
     * @param isRound   是否显示为圆形的图
     * @param options
     */
    private void DisplayImageOnly(String url, ImageView imageview, boolean isRound, DisplayImageOptions options) {
        ImageLoader.getInstance().cancelDisplayTask(imageview);
        if (isRound) {
            ImageLoader.getInstance().displayImage(url, imageview, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap != null) {
                        ((ImageView) view).setImageBitmap(toRoundBitmap(bitmap));
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } else {
            ImageLoader.getInstance().displayImage(url, imageview, options);
        }
    }


    /**
     * 显示图片,不缓存文件
     *
     * @param url       图片文件地址或网络访问地址
     * @param imageview 要显示图片的控件
     * @param radius    圆角半径
     */
    public void DisplayCornerImage(String url, ImageView imageview, final int radius) {
        ImageLoader.getInstance().cancelDisplayTask(imageview);
        ImageLoader.getInstance().displayImage(url, imageview, FDisplayOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    ((ImageView) view).setImageBitmap(toRoundCornerBitmap(bitmap, radius));
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }
    public void DisplayCornerIcon(String url, ImageView imageview, final int radius) {
        if (!url.endsWith("_128.png")) {
            url = url.replace(".png", "_128.png");
        }
        DisplayCornerImage(url,imageview,radius);
    }

    /**
     * 显示图片并缓存文件
     *
     * @param url       图片文件地址或网络访问地址
     * @param imageview 要显示图片的控件
     * @param isRound   是否显示为圆形的图
     * @param options
     */
    private void DisplayAndCacheImage(final String url, ImageView imageview, final boolean isRound, DisplayImageOptions options) {
        ImageLoader.getInstance().cancelDisplayTask(imageview);
        ImageLoader.getInstance().displayImage(url, imageview, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                if (bitmap != null) {
                    if (isRound) {
                        ((ImageView) view).setImageBitmap(toRoundBitmap(bitmap));
                    } else {
                        ((ImageView) view).setImageBitmap(bitmap);
                    }

                    if (!url.startsWith("file://")) {//网络图片缓存
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File file = GetCacheFile(url);
                                BitmapUtils.saveBitmap(bitmap, file);
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }


    public File GetCacheFile(String netUrl) {
        String fileName = MD5Utils.getMD5(netUrl) + FileUtils.getExtensionName(netUrl);
        String fileurl = UriUtils.Combine(FContext.getExternalCacheDir().getAbsolutePath(), fileName);
        File file = new File(fileurl);
        return file;
    }

    public String GetCacheUrl(String netUrl) {
        String fileName = MD5Utils.getMD5(netUrl) + FileUtils.getExtensionName(netUrl);
        String fileurl = UriUtils.Combine(FContext.getExternalCacheDir().getAbsolutePath(), fileName);
        File file = new File(fileurl);
        return Uri.fromFile(file).toString();
    }

}
