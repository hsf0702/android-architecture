package com.klfront.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by L on 2015/11/24.
 */
public class BitmapUtils {
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int d = 0;
        if (width > height) {
            d = width;
        } else {
            d = height;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, d, d);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, d / 2, d / 2, paint);

        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    public static Bitmap toRoundCornerBitmap(Bitmap bitmap, int radius) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int d = 0;
        if (width > height) {
            d = width;
        } else {
            d = height;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, d, d);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, radius, radius, paint);

        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    public static Bitmap getBitmapByText(String text) {
        if (text.length() > 2) {
            text = text.substring(text.length() - 2, text.length());
        }
        int number = 0;
        try {
            byte[] bytes = text.getBytes("UTF-8");
            for (byte b : bytes) {
                int i = b & 0xff;
                number = number ^ i;
            }
        } catch (Exception e) {
        }
        return getBitmapByText(text, Colors.GetColor(number));
    }

    public static Bitmap getBitmapByText(String text, boolean isRound) {
        if (isRound) {
            return toRoundBitmap(getBitmapByText(text));
        } else {
            return getBitmapByText(text);
        }
    }

    public static Bitmap getBitmapByColor(int color) {
        return getBitmapByText("", color);
    }

    public static Bitmap getRoundBitmapByColor(int color) {
        return toRoundBitmap(getBitmapByColor(color));
    }

    public static Bitmap getBitmapByText(String text, int color) {
        return getBitmapByText(text, 36, 120, 120, color);
    }

    public static Bitmap getBitmapByText(String text, int bgColor, int textColor) {
        return getBitmapByText(text, 36, 120, 120, bgColor, textColor);
    }


    public static Bitmap getBitmapByText(String text, float textSize, int width, int height, int bgColor) {
        return getBitmapByText(text, textSize, width, height, bgColor, Color.WHITE);
    }

    public static Bitmap getBitmapByText(String text, float textSize, int width, int height, int bgColor, int textColor) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//创建一个宽度和高度都是400、32位ARGB图
        Canvas canvas = new Canvas(bitmap);//使用bitmap初始化画布
        canvas.drawColor(bgColor);  //图层的背景色

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//创建画笔
        paint.setTextSize(textSize);    //设置文字的大小
        //paint.setTypeface(Typeface.DEFAULT_BOLD);//文字的样式(加粗)
        paint.setColor(textColor);     //文字的颜色

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        canvas.drawText(text, width / 2 - bounds.width() / 2, baseline, paint);//将文字写入。x、y参数表示文字初始位置
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存所有图层
        canvas.restore();
        return bitmap;
    }


    /* 关于图片压缩
    1.质量压缩:可以把一个file转成bitmap再转成file，或者直接将一个bitmap转成file，这个最终的file是被压缩过的。但bigmap在内存中的大小是按像素计算的，也就是width * height，质量压缩不会改变图片的像素，所以就算质量被压缩了，但是bitmap在内存的占有率还是没变小。
      比方说, 你的图片是300K的, 1280*700像素的, 经过该方法压缩后, File形式的图片是在100以下, 以方便上传服务器, 但是你BitmapFactory.decodeFile到内存中,变成Bitmap时,它的像素仍然是1280*700, 计算图片像素的方法是 bitmap.getWidth()和bitmap.getHeight(), 图片是由像素组成的。 每个像素又包含色相,明度和饱和度等信息，质量压缩可能使图像的位深(即色深)和每个像素的透明度发生变化。
    2.尺寸压缩：通过设置采样率, 减少图片的像素, 达到对内存中的Bitmap进行压缩。而尺寸压缩由于是减小了图片的像素，所以它直接对bitmap产生了影响，当然最终的file也是相对的变小了。
    */

    //图片压缩前对图片的方向进行校正
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            // MmsLog.e(ISMS_TAG, "getExifOrientation():", ex);
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90://已经逆时针旋转了270度，通过再旋转90度进行矫正
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180://已经逆时针旋转了180度，通过再旋转180度进行矫正
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270://已经逆时针旋转了90度，通过再旋转270度进行矫正
                        degree = 270;
                        break;
                    default:
                        break;
                }
            }
        }

        return degree;
    }


    /**
     * 取值2的次方数,向上取整 （例如：3取4，15取16 ）
     * @param inSampleSize
     * @return
     */
    public static int formatInSampleSize(int inSampleSize) {
        if (inSampleSize <= 0) {
            return 1;
        }
        int i = 0;
        int result = 1;
        while (result < inSampleSize) {
            i++;
            result = (int) Math.pow(2, i);
        }
        return result;
    }

    /**
     * 根据采样率从文件获取位图图片
     *
     * @param srcPath      文件路径
     * @param inSampleSize 采样率 图片的采样率: 数值越高，图片像素越低。
     *                     取值只能是2的次方数(例如:inSampleSize=15,实际取值为8;inSampleSize=17,实际取值为16;实际取值会往2的次方结算)
     * @return
     */
    public static Bitmap getBitmap(String srcPath, int inSampleSize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = formatInSampleSize(inSampleSize);
        opts.inPurgeable = true;// 同时设置才会有效
        opts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
        Bitmap resizeBmp = BitmapFactory.decodeFile(srcPath, opts);
        return resizeBmp;
    }

    /**
     * 从文件读取大图片，适当压缩
     * 1M左右的图片文件 读取到bitmap可能占用内存60M ，应根据图片长宽信息使用适当的采样率读取
     *
     * @param file
     * @param maxBitmapSize 允许分配给Bitmap的最大内存
     * @return
     */
    public static Bitmap getBitmap(File file, long maxBitmapSize) {
        if (!file.exists()) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 开启只加载属性模式，以便获取宽高信息
            options.inJustDecodeBounds = true;
            InputStream inStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inStream, null, options);
            long imgsize = options.outHeight * options.outWidth * 4;
            int inSampleSize = (int) Math.ceil(Math.sqrt(imgsize / maxBitmapSize));
            options.inSampleSize = formatInSampleSize(inSampleSize);

            // 关闭只加载属性模式, 并重新加载的时候传入自定义的options对象
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;
            options.inPurgeable = true;// 同时设置才会有效
            options.inInputShareable = true;//。当系统内存不够时候图片自动被回收
            try {//decodeStream之后位置已发生变化，因此重新初始化流
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap resizeBmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            return resizeBmp;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static Bitmap getBitmap(File file) {
        return getBitmap(file, 4 * 1024 * 1024);
    }

    public static Bitmap getSmallBitmap(File file) {
        return getBitmap(file, 2 * 1024 * 1024);
    }

    /**
     * 获取压缩过的图片作为资源文件
     *
     * @param srcImagePath
     * @return
     */
    public static String getSmallRotatedResPath(String srcImagePath, int rotatedDegree) {
        //根据采样率读取压缩的图片资源
        File file = new File(srcImagePath);
        Bitmap temp = BitmapUtils.getSmallBitmap(file);
        Bitmap result = temp;
        int degree = BitmapUtils.getExifOrientation(srcImagePath);
        degree = degree + rotatedDegree;
        if (degree == -90) {
            degree = 270;
        }
        if (degree == 90 || degree == 180 || degree == 270) {//旋转图片使得图片的顶部始终朝上
            //Roate preview icon according to exif orientation
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            result = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
        }
        String filePath = srcImagePath.replace(FileUtils.getExtensionName(srcImagePath), "_thumb.jpg");
        return BitmapUtils.saveBitmap(result, filePath);
    }


    /**
     * 保存Bitmap对象到文件
     * @param bitmap
     * @param f
     * @return
     */
    private static boolean saveBitmapToFile(Bitmap bitmap, File f) {
        if (f.exists()) {
            f.delete();
        }
        try {
            boolean isPng = f.getName().toLowerCase().endsWith(".png");
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(isPng?Bitmap.CompressFormat.PNG:Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存图片到文件
     */
    public static String saveBitmap(Bitmap bitmap, File f) {
        boolean success = saveBitmapToFile(bitmap,f );
        return success?f.getAbsolutePath():"";
    }

    /**
     * 保存图片到文件
     */
    public static String saveBitmap(Bitmap bitmap, String filePath) {
        File f = new File(filePath);
        return saveBitmap(bitmap,f);
    }

//    /**
//     * 保存图片到文件
//     */
//    public static String saveBitmap(Context context, Bitmap bitmap, String dirName, String fileName) {
//        File f = QhFileUtils.GetFileFromFileName(context, dirName, fileName);
//        return saveBitmap(bitmap, f);
//    }

    /**
     * 质量压缩 压缩文件存储尺寸 降低图片的质量，像素不会减少。
     * 应用于文件上传等场景
     *
     * @param bmp
     * @param file
     * @param quality 质量参数 0-100 100为不压缩
     *                指定jpeg情况下，即使质量指定100，也会有很大的压缩效果（RGB_565 1.7M的图能压缩到300K）。
     *                一般情况下,压缩率在80%以上就行，继续往下，文件减少的幅度大幅下降，失真度却大幅上升
     */
    public static void saveCompressedBitmap(Bitmap bmp, File file, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 参数一指定png 参数quality就都为100了，所以要压缩质量，要指定为jpeg
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存图片到相册
     */
    public static void saveImageToGallery(Context context,String authority, Bitmap bmp, String dirName, String fileName) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.GetUriForFileByFileProvider(context,authority, file)));
    }

    /**
     * 保存图片文件到相册
     */
    public static void saveImageToGallery(Context context,String authority, File file) {
        //其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.GetUriForFileByFileProvider(context,authority, file)));
    }

    public static void saveImageToGallery(Context context,File file) {
        String authority = context.getPackageName()+".fileprovider";
        saveImageToGallery(context,authority,file);
    }
    /**
     * 根据最大限定值压缩图片
     * 保持宽高比和原bitmap比率一致，压缩后减小占用空间
     *
     * @param maxSize 图片允许最大空间 单位：Byte。
     */
    public static Bitmap CompressImg(int maxSize, Bitmap bitmap) {
        if (bitmap.getByteCount() > maxSize) {
            double i = bitmap.getByteCount() / maxSize;//压缩倍数
            bitmap = ZoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i), bitmap.getHeight() / Math.sqrt(i));//开始压缩N倍 则将宽度和高度压缩N的平方根倍
        }
        return bitmap;
    }

    /**
     * 将图片缩放到新的宽高用于显示
     *
     * @param bgimage
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap ZoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth(); // 获取这个图片的宽和高
        float height = bgimage.getHeight();

        float scaleWidth = ((float) newWidth) / width;// 计算宽高缩放率
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();  // 创建操作图片的matrix对象
        matrix.postScale(scaleWidth, scaleHeight); // 缩放图片动作

        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 裁剪并缩放图片，使图片宽度和高度不超过指定值
     *
     * @param maxWidth  宽度大于该尺寸则压缩
     * @param srcBitmap 原图
     * @return
     */
    public static Bitmap CutAndZoomBitmap(int maxWidth, Bitmap srcBitmap) {
        float scale = 1;
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();

        //高度超过宽度，则按宽度截取需要的高度
        if (height > width) {
            height = width;
        }

        //图片宽度尺寸过大，则保持宽高比例 缩小到最大宽度
        if (width > maxWidth) {
            scale = ((float) maxWidth) / width;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap tempBmp = Bitmap.createBitmap(srcBitmap, 0, (srcBitmap.getHeight() - height) / 2, width, height, matrix, true);

        return tempBmp;
    }

    public static Bitmap CropBackgroudImage(Context context, Bitmap bmp, int outHeight) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int outWidth = wm.getDefaultDisplay().getWidth();
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        //以背景区域的宽高比例，计算要截取图片的大小（需要保持图片不变形）
        if (width / height > outWidth / outHeight) {
            width = (int) ((float) outWidth / (float) outHeight * height);
        } else {
            height = (int) ((float) outHeight / (float) outWidth * width);
        }

        //截取图片并缩放到背景区域大小 （缩放解决 图片尺寸超过屏幕时的报错：Failed to allocate a 31961100 byte allocation with 4194304 free bytes and 27MB until OOM）
        float scaleWidth = ((float) outWidth) / width;
        float scaleHeight = ((float) outHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        int startx = 0;
        int starty = 0;
        try {
            if (bmp.getHeight() >= height) {
                if (bmp.getWidth() > width) {
                    startx = (bmp.getWidth() - width) / 2;
                }
                if (bmp.getHeight() > height) {
                    starty = (bmp.getHeight() - height) / 2;
                }
                Bitmap img = Bitmap.createBitmap(bmp, startx, starty, width, height, matrix, true);
                return img;
            }
        } catch (Exception e) {
            Log.e("LoadBgImage", e.getMessage());
        }
        return bmp;
    }
}
