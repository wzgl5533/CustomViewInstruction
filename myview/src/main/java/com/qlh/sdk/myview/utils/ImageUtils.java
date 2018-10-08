package com.qlh.sdk.myview.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.qlh.sdk.myview.camera.CameraView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 作者：QLH on 2018/10/8 10:09
 * 描述：图片处理
 */
public class ImageUtils {

    private final static String TAG = "ImageUtils";

    /**
     * Save the bitmap.
     *
     * @param src    The source of bitmap.
     * @param file   The file.
     * @param format The format of the image.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src, final File file, final Bitmap.CompressFormat format) {
        return save(src, file, format, false);
    }

    /**
     * Save the bitmap.
     *
     * @param src     The source of bitmap.
     * @param file    The file.
     * @param format  The format of the image.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final File file,
                               final Bitmap.CompressFormat format,
                               final boolean recycle) {
        if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) return false;
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled()) src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    private static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) return false;
        if (file.exists() && !file.delete()) return false;
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
     */
    public static int getOrientation(byte[] jpeg) {
        if (jpeg == null) {
            return 0;
        }

        int offset = 0;
        int length = 0;

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
            int marker = jpeg[offset] & 0xFF;

            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                break;
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false);
            if (length < 2 || offset + length > jpeg.length) {
                Log.e(TAG, "Invalid length");
                return 0;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                break;
            }

            // Skip other markers.
            offset += length;
            length = 0;
        }

        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.
            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.e(TAG, "Invalid byte order");
                return 0;
            }
            boolean littleEndian = (tag == 0x49492A00);

            // Get the offset and check if it is reasonable.
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                Log.e(TAG, "Invalid offset");
                return 0;
            }
            offset += count;
            length -= count;

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian);
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    // We do not really care about type and count, do we?
                    int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                            return 0;
                        case 3:
                            return 180;
                        case 6:
                            return 90;
                        case 8:
                            return 270;
                    }
                    Log.i(TAG, "Unsupported orientation");
                    return 0;
                }
                offset += 12;
                length -= 12;
            }
        }

        Log.i(TAG, "Orientation not found");
        return 0;
    }

    private static int pack(byte[] bytes, int offset, int length,
                            boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }

        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes[offset] & 0xFF);
            offset += step;
        }
        return value;
    }

    /**
     * 图片镜像翻转
     **/
    public static Bitmap mirrorRotate(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);// 镜像水平翻转
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return rotateBitmap;
    }

    /**
     *主要是矫正图片（如某些三星等手机会有图片旋转），前置摄像头镜像矫正（看需求）
     *
     */
    public static Bitmap adjustBitmap(byte[] data,boolean isMirrorRotate,int face){

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        int rotation = ImageUtils.getOrientation(data);
        if (rotation != 0){
            Matrix m = new Matrix();
            m.setRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        //前置摄像头且需要镜像旋转
        if (isMirrorRotate && face == CameraView.FACING_FRONT){
            bitmap = mirrorRotate(bitmap);
        }
        return bitmap;
    }
}
