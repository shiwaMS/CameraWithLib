package microsoft.prototype.broadcastsupport;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import microsoft.prototype.broadcastsupport.gles.EglCore;
import microsoft.prototype.broadcastsupport.gles.OffscreenSurface;

import static microsoft.prototype.broadcastsupport.gles.EglCore.FLAG_RECORDABLE;

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();
    private Context context;

    public static String setContext(Context context) {
        String result = "null";
        if (context != null) {
            Log.i(TAG, "set context to ImageLoader");
            result = context.getPackageName();
        }

        return result;
    }

    public static Bitmap loadBitmap(String fileName) {
        Log.i(TAG, "loadBitmap path: " + fileName);

        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        return bitmap;
    }

    public static Bitmap loadBitmapFromSystemDrawable() {
        Log.i(TAG, "loadBitmapFromSystemDrawable");

        // The size of sym_def_app_icon is 144*144
        Drawable drawable = Resources.getSystem().getDrawable(android.R.drawable.sym_def_app_icon);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getMinimumWidth(), drawable.getMinimumHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static int loadTextureId() {
        Log.d(TAG, "loadTextureId");

        Bitmap bitmap = loadBitmapFromSystemDrawable();
        return getTextureIdOfLoadedImage(bitmap);
    }

    public static int loadTextureId(String fileName) {
        Log.d(TAG, "loadTextureId path: " + fileName);

        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        return getTextureIdOfLoadedImage(bitmap);
    }

    public static String loadTextureIdStr(Context context, String fileName) {
        Log.d(TAG, " loadTextureIdStr loadTextureId path: " + fileName);
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);

        if (bitmap != null) {
            return Integer.toString(getTextureIdOfLoadedImage(bitmap));
        }

        return "null";
    }

    public static void writeBytesToFile(int[] intArray, String fileName) {
        FileOutputStream fos = null;
        byte[] bytes = intArrayToByteArray(intArray);

        if (bytes.length > 0) {
            try {
                String name = "/" + fileName + ".png";
                File file = new File(Environment.getExternalStorageDirectory(), name);
                fos = new FileOutputStream(file);
                fos.write(bytes);
                Log.d(TAG, "image saved in" + Environment.getExternalStorageDirectory() + name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static byte[] intArrayToByteArray(int[] Iarr) {
        byte[] bytes = new byte[Iarr.length];
        for (int i = 0; i < Iarr.length; i++) {
            bytes[i] = (byte) (Iarr[i] & 0xFF);
        }
        return bytes;
    }

    private static int getTextureIdOfLoadedImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "getTextureIdOfLoadedImage: invalid bitmap");
            return 0;
        }

        Log.i(TAG, "getTextureIdOfLoadedImage Image size: " + bitmap.getWidth() + " x " + bitmap.getHeight());

        EglCore eglCore = new EglCore(null, FLAG_RECORDABLE);
        OffscreenSurface offscreenSurface = new OffscreenSurface(eglCore, 10, 10);
        offscreenSurface.makeCurrent();
        int textures[] = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        Log.d(TAG, "getTextureIdOfLoadedImage textureId: " + textureId);
        return textureId;
    }
}
