package utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.MyApp;
import db.SongDAOImpl;

/**
 * Created by panl on 15/7/22.
 */
public class FileUtils {

    final static String TAG = "===FileUtils===";


    private static String IMAGE_PATH = null;
    private static final String APP_PATH = "/OnceFm/images";

    public static String getPhotoDir() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + APP_PATH;
        } else {
            Toast.makeText(MyApp.getContext(), "请插入SD卡", Toast.LENGTH_SHORT).show();
        }
        return IMAGE_PATH;
    }

    public static String saveBitmap(Bitmap cropBitmap) throws FileNotFoundException {
        File chiShaDir = new File(getPhotoDir());
        if (!chiShaDir.exists())
            chiShaDir.mkdirs();

        File nomedia = new File(getPhotoDir() + "/.nomedia");
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String imgName = "IMG_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        File jpgFile = new File(getPhotoDir(), imgName);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        cropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpgFile.getAbsolutePath();
    }

    public static String saveBitmapWithName(Bitmap cropBitmap) throws FileNotFoundException {
        return saveBitmapWithName(cropBitmap, Utils.getRandomString());
    }

    public static String saveBitmapWithName(Bitmap bitmap, String name) throws FileNotFoundException {
        File chiShaDir = new File(getPhotoDir());
        if (!chiShaDir.exists()) {
            chiShaDir.mkdirs();
        }
        File nomedia = new File(getPhotoDir() + "/.nomedia");
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String imgName = "IMG_" + name + ".jpg";
        File jpgFile = new File(getPhotoDir(), imgName);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapUtils.recycle(bitmap);
        return jpgFile.getAbsolutePath();
    }


    public static String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(MyApp.getContext().getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String HandleBitmapToSquare(byte[] data, float scale) throws IOException {
        Bitmap croppedImage;
        //获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        int PHOTO_SIZE = options.outHeight > options.outWidth ? options.outWidth : options.outHeight;
        options.inJustDecodeBounds = false;

        Rect rect;
        if (options.outHeight > options.outWidth) {
            int height = (int) (options.outHeight * scale);
            rect = new Rect(0, height, PHOTO_SIZE, PHOTO_SIZE + height);
        } else {
            int left = (int) (options.outWidth * scale);
            rect = new Rect(left, 0, PHOTO_SIZE + left, PHOTO_SIZE);
        }
        try {
            croppedImage = BitmapUtils.decodeRegionCrop(data, rect);
        } catch (Exception e) {
            return null;
        }
        if (croppedImage == null) return null;
        String imgPath = saveBitmap(croppedImage);
        BitmapUtils.recycle(croppedImage);
        return imgPath;
    }


    public static Uri getOutputUri(File mFile) {
        return Uri.fromFile(mFile);
    }

    /**
     * 根据Uri获取到文件的真正filepath
     * 这个放在这里作为备选，如果Utils中的getRealPath不能使用大多数机型和android版本，就切换为这个试试
     * <p/>
     * <p/>
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(21)
    public static String getPath(final Context context, final Uri uri) {

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
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

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
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    public static String getCacheSize(File file) throws Exception {
        return getFormatSize(getFolderSize(file));
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    public static boolean existFile(int id) {
        File musicFile = new File(MyApp.DOWNLOAD_DIR, "m" + id + ".mp3");
        if (musicFile.exists()) {
            return true;
        } else {
            Log.e("sssss", TAG + "FILE NOT EXIST==" + Utils.getLineNumber(new Exception()));
            return false;
        }
    }


    public static void clearFiles(final SongDAOImpl songDAO) {

        new Thread() {
            @Override
            public void run() {

                File dir = new File(MyApp.DOWNLOAD_DIR);
                File[] files = dir.listFiles();
                List list = new ArrayList();
                if (files != null) {
                    for (File f : files) {
                        String name = f.getName();
                        if (name.endsWith(".mp3")) {
                            long id = Long.parseLong(name.replace(".mp3", "").replace("m", ""));
                            if (!songDAO.isExist(id) &&
                                    !MyApp.downloadingList.contains(id)) {
                                list.add(id);
                            }
                        }
                    }
                    for (Object num : list) {
                        long id = Long.parseLong(num == null ? "" : num.toString());
                        deleteFiles(id);
                    }
                }
            }
        }.start();
    }

    public static void deleteFiles(final long id) {

        new Thread() {
            @Override
            public void run() {

                File music = new File(MyApp.DOWNLOAD_DIR, "m" + id + ".mp3");
                if (music != null) {
                    music.delete();
                    Log.i("Test info file ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> on DELETE FILES on " + id);
                }
            }
        }.start();
    }

    private void downloadLastestVersion(int version) {

        HttpURLConnection connection = null;
        File newApk = null;
        InputStream inputStream = null;

        try {
            newApk = new File(MyApp.DOWNLOAD_DIR, "once" + version + ".apk");
            OutputStream outputStream = new FileOutputStream(newApk);
            URL url = new URL("http://yanwenzi.net/files/once" + (float) version / 10 + ".apk");
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                int len = 0;
                byte[] buffer = new byte[1024];
                inputStream = connection.getInputStream();
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }

    private void clearApk() {

        new Thread() {
            @Override
            public void run() {
                File dir = new File(MyApp.DOWNLOAD_DIR);
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.getName().endsWith(".apk")) {
                            f.delete();
                        }
                    }
                }

            }
        }.start();
    }

    private boolean existApk() {
        File dir = new File(MyApp.DOWNLOAD_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(".apk")) {
                    return true;
                }
            }
        }
        return false;
    }
}
