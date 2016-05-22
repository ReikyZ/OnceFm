package utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.freemp.malevich.ImageCache;
import org.freemp.malevich.Malevich;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import app.MyApp;

/**
 * Created by clownqiang on 15/7/15.
 */
public class BitmapUtils {
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private static Malevich malevich;

    /**
     * 配置ImageLoader
     *
     * @param context
     * @param cacheDir
     * @return
     */
    public static ImageLoaderConfiguration getImageLoaderConfig(Context context, File cacheDir) {
        return new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                //.memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache())
                .diskCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
                // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                //.writeDebugLogs() // Remove for release app
                .build();
    }

    private static ImageCache.ImageCacheParams getLoadBitmapParams() {
        ImageCache.ImageCacheParams loadBitmapParams = new ImageCache.ImageCacheParams(MyApp.getContext(), FileUtils.getPhotoDir() + "bitmaps");
        loadBitmapParams.memoryCacheEnabled = true;
        loadBitmapParams.setMemCacheSizePercent(0.4f);
        loadBitmapParams.compressQuality = 70; // Compress quality
        loadBitmapParams.compressFormat = Bitmap.CompressFormat.JPEG; // Compress format
        loadBitmapParams.diskCacheEnabled = true; // Use disk cache
        loadBitmapParams.diskCacheSize = 10485760; // Disk cache size
        return loadBitmapParams;
    }

    public static Malevich getInstanceMalevich() {
        if (malevich == null) {
            malevich = new Malevich.Builder(MyApp.getContext())
                    .debug(true)
                    .CacheParams(getLoadBitmapParams())
                    .build();
        }
        return malevich;
    }

    public static DisplayImageOptions avatarImageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(new ColorDrawable(Color.TRANSPARENT))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
            .build();

    public static DisplayImageOptions roundImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
            .displayer(new RoundedBitmapDisplayer(10))
            .build();

    public static DisplayImageOptions normalImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
            .build();

    /**
     * 显示头像
     *
     * @param imageUrl
     * @param avatarView
     */
    public static void displayAvatar(String imageUrl, ImageView avatarView) {
        imageLoader.displayImage(imageUrl, avatarView, avatarImageOptions);
    }

    /**
     * 展示圆角图片
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayRoundImage(String imageUrl, ImageView imageView) {
        imageLoader.displayImage(imageUrl, imageView, roundImageOptions);
    }

    /**
     * 展示图片
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayImage(String imageUrl, ImageView imageView) {
        imageLoader.displayImage(imageUrl, imageView, normalImageOptions);
    }

    public static void displayImageWithCallback(String imageUrl, SimpleImageLoadingListener listener) {
        imageLoader.loadImage(imageUrl, normalImageOptions, listener);
    }

    /**
     * 展示高斯模糊图片
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayImageWithBlur(String imageUrl, final ImageView imageView) {
        imageLoader.loadImage(imageUrl, normalImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
                if (loadedImage != null && !loadedImage.isRecycled())
                    FastBlur.applyBlur(imageView, 10);
            }
        });
    }

    public static void displayImageWithStrongBlur(String imageUrl, final ImageView imageView) {
        imageLoader.loadImage(imageUrl, normalImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.d("bitmap", "image load");
                imageView.setImageBitmap(loadedImage);
                if (loadedImage != null && !loadedImage.isRecycled())
                    FastBlur.applyBlur(imageView, 50);
            }
        });
    }

    public static void displayLocalImageWithStrongBlur(String imageUrl, final ImageView imageView) {
        imageLoader.loadImage("file:///" + imageUrl, normalImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
                if (loadedImage != null && !loadedImage.isRecycled())
                    FastBlur.applyBlur(imageView, 50);
            }
        });
    }

    /**
     * 图片加载完成之前显示随机颜色
     *
     * @param imageUrl
     * @param imageView
     * @param isLocal
     */
    public static void displayLocalImageWithRandomSrc(final String imageUrl, final ImageView imageView, final boolean isLocal) {
        final int color = Utils.getRandomColor();
        imageView.setTag(imageUrl);
        imageLoader.loadImage(imageUrl, normalImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                imageView.setImageDrawable(new ColorDrawable(color));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                imageView.setImageDrawable(new ColorDrawable(color));
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                if (imageView.getTag().equals(imageUri) || isLocal) {
                    Drawable oldDrawable = imageView.getDrawable();
                    if (oldDrawable != null) oldDrawable.setCallback(null);
                    Drawable oldBitmapDrawable = new ColorDrawable(color);
                    TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                            oldBitmapDrawable,
                            new BitmapDrawable(Resources.getSystem(), loadedImage)
                    });
                    imageView.setImageDrawable(td);
                    td.startTransition(400);
                } else {
                    BitmapUtils.recycle(loadedImage);
                }
            }
        });
    }

    public static void displayImageWithRandomSrc(final String imageUrl, final ImageView imageView) {
        displayLocalImageWithRandomSrc(imageUrl, imageView, false);
    }

    public static void displayImageWithRandomBg(String imageUrl, final ImageView imageView) {
        imageView.setBackgroundColor(Utils.getRandomColor());
        imageLoader.displayImage(imageUrl, imageView, normalImageOptions);
    }

    /**
     * 切换图片时过渡动画
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayImageWithAnim(String imageUrl, final ImageView imageView) {
        imageLoader.loadImage(imageUrl, normalImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Drawable oldDrawable = imageView.getDrawable();
                Drawable oldBitmapDrawable = null;
                if (oldDrawable == null) {
                    oldBitmapDrawable = new ColorDrawable(Color.TRANSPARENT);
                } else if (oldDrawable instanceof TransitionDrawable) {
                    oldBitmapDrawable = ((TransitionDrawable) oldDrawable).getDrawable(1);
                } else {
                    oldBitmapDrawable = oldDrawable;
                }
                TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                        oldBitmapDrawable,
                        new BitmapDrawable(Resources.getSystem(), loadedImage)
                });
                imageView.setImageDrawable(td);
                td.startTransition(500);
            }
        });
    }

    /**
     * 展示本地图片圆角效果
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayLocalRoundImage(String imageUrl, ImageView imageView) {
        imageLoader.displayImage("file:///" + imageUrl, imageView, roundImageOptions);
    }

    /**
     * 展示本地图片
     *
     * @param imageUrl
     * @param imageView
     */
    public static void displayLocalImage(String imageUrl, ImageView imageView) {
        imageLoader.displayImage("file:///" + imageUrl, imageView, normalImageOptions);
    }

    public static void clearCacheImage() {
        imageLoader.clearDiskCache();
    }

    /**
     * 将拍好的照片处理为正方形
     *
     * @param data
     * @param rect
     * @return
     */
    public static Bitmap decodeRegionCrop(byte[] data, Rect rect) {
        InputStream is = null;
        Bitmap croppedImage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, 1080, 1080);
        options.inJustDecodeBounds = false;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, true);
            try {
                croppedImage = decoder.decodeRegion(rect, options);
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Bitmap rotatedImage = rotateBitmap(croppedImage, 90);
        if (rotatedImage == null ||
                (!rotatedImage.equals(croppedImage))) {
            BitmapUtils.recycle(croppedImage);
        }
        return rotatedImage;
    }

    /**
     * 回收垃圾 recycle
     *
     * @throws
     */
    public static void recycle(Bitmap bitmap) {
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    /**
     * 根据控件大小计算bitmap缩放比例
     *
     * @param options
     * @param reqWidth  控件宽度（px）
     * @param reqHeight 控件高度 （px）
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > height) {
            if (height > reqWidth) {
                inSampleSize = Math.round((float) height / (float) reqWidth);
            }
        } else {
            if (width > reqWidth) {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 根据控件大小获得压缩后的bitmap
     *
     * @param imagePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getSmallBitmap(String imagePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return compressBitmap(BitmapFactory.decodeFile(imagePath, options));
    }

    /**
     * 根据旋转角度获得旋转后的bitmap
     *
     * @param bm                原bitmap
     * @param orientationDegree 旋转角度 (90 , 180 , 270)
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bm, final int orientationDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(orientationDegree);
        Bitmap rotatedBitmap = createBitmapSafely(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true, 1);
        if (rotatedBitmap == null || !rotatedBitmap.equals(bm)) {
            BitmapUtils.recycle(bm);
        }
        return rotatedBitmap;
    }

    /**
     * 裁剪图片，以较短的一边为边长，从图片中间裁剪出一个正方形图片
     *
     * @param imgPath 图片路径
     * @return
     */
    public static Bitmap cropCenterSquareBitmap(String imgPath, int width, int height) {
        Bitmap originBitmap = decodeSampledBitmapFromResource(imgPath, width, height);
        if (originBitmap == null) return null;

        int originWidth = originBitmap.getWidth();
        int originHeight = originBitmap.getHeight();
        int cropSize = Math.min(originWidth, originHeight);
        cropSize = Math.min(1080, cropSize);
        int offset = Math.abs(originWidth - originHeight) / 2;

        Bitmap cropBitmap;

        if (originWidth < originHeight) {
            cropBitmap = createBitmapSafely(originBitmap, 0, offset, cropSize, cropSize, 1);
        } else {
            cropBitmap = createBitmapSafely(originBitmap, offset, 0, cropSize, cropSize, 1);
        }
        if (cropBitmap == null || !originBitmap.equals(cropBitmap)) {
            BitmapUtils.recycle(originBitmap);
        }

        if (cropBitmap == null) return null;

        Matrix matrix = new Matrix();
        float scale = 1080 / cropSize;
        matrix.postScale(scale, scale);
        Bitmap resultBitmap = BitmapUtils.createBitmapSafely(cropBitmap, 0, 0, cropBitmap.getWidth(), cropBitmap.getHeight(), matrix, false, 1);
        if (resultBitmap == null || !cropBitmap.equals(resultBitmap)) {
            BitmapUtils.recycle(cropBitmap);
        }

        if (resultBitmap != null) {
            Log.d("clownqiang", "resultBitmap height--->" + resultBitmap.getHeight());
            Log.d("clownqiang", "resultBitmap width--->" + resultBitmap.getWidth());
        }
        return resultBitmap;
    }


    public static Bitmap decodeSampledBitmapFromResource(String imagePath, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return compressBitmap(decodeBitmapSafely(imagePath, options, 1));
    }

    /**
     * 压缩bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap decodeBitmapSafely(String pathName, BitmapFactory.Options options, int retryCount) {
        try {
            return BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                Log.d("clownqiang", "retry decode");
                System.gc();
                return decodeBitmapSafely(pathName, options, retryCount - 1);
            }
            return null;
        }
    }


    public static Bitmap createBitmapSafely(Bitmap source, int x, int y, int width, int height, int retryCount) {
        try {
            return Bitmap.createBitmap(source, x, y, width, height);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                Log.d("clownqiang", "retry bitmap 1");
                System.gc();
                return createBitmapSafely(source, x, y, width, height, retryCount - 1);
            }
            return null;
        }
    }

    public static Bitmap createBitmapSafely(Bitmap source, int x, int y, int width, int height, Matrix matrix, boolean filter, int retryCount) {
        try {
            return Bitmap.createBitmap(source, x, y, width, height, matrix, filter);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                Log.d("clownqiang", "retry bitmap 2");
                System.gc();
                return createBitmapSafely(source, x, y, width, height, matrix, filter, retryCount - 1);
            }
            return null;
        }
    }

    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                Log.d("clownqiang", "retry bitmap 3");
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
            return null;
        }
    }


    public static final Bitmap createRGBImage(Bitmap bitmap, int color) {
        int bitmap_w = bitmap.getWidth();
        int bitmap_h = bitmap.getHeight();
        int[] arrayColor = new int[bitmap_w * bitmap_h];
        int count = 0;
        for (int i = 0; i < bitmap_h; i++) {
            for (int j = 0; j < bitmap_w; j++) {
                int color1 = bitmap.getPixel(j, i);
                //这里也可以取出 R G B 可以扩展一下 做更多的处理，
                //暂时我只是要处理除了透明的颜色，改变其他的颜色
                if (color1 != 0) {
                    color1 = color;
                } else {
                }
                arrayColor[count] = color1;
                count++;
            }
        }
        bitmap = Bitmap.createBitmap(arrayColor, bitmap_w, bitmap_h, Bitmap.Config.ARGB_4444);
        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }


    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
// 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
    public static Bitmap loadBitMap(File file) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inSampleSize = 2;
        opt.inInputShareable = true;
        //获取资源图片
        try {
            InputStream is = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }




}
