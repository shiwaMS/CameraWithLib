package microsoft.prototype.broadcastsupport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;

import microsoft.prototype.broadcastsupport.gles.EglCore;
import microsoft.prototype.broadcastsupport.gles.OffscreenSurface;

import static microsoft.prototype.broadcastsupport.gles.EglCore.FLAG_RECORDABLE;

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();
    private Context context;

    public static String setContext(Context context){
        String result = "null";
        if(context != null){
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

    public static int loadTextureId(String fileName) {
        Log.d(TAG, "loadTextureId path: " + fileName);
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
//        Log.d(TAG, "Bitmap is: " + bitmap);
        Log.i(TAG, "Image loaded, size: " + bitmap.getWidth() + " x " + bitmap.getHeight());
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
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
        Log.d(TAG, "texture id returned: " + textureId);
        return textureId;
    }

    public static String loadTextureIdStr(Context context, String fileName) {
        Log.d(TAG, "loadTextureId path: " + fileName);
        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
//        Log.d(TAG, "Bitmap is: " + bitmap);

        if (bitmap != null) {
            EglCore eglCore = new EglCore(null, FLAG_RECORDABLE);
            OffscreenSurface offscreenSurface = new OffscreenSurface(eglCore, 10, 10);
            offscreenSurface.makeCurrent();
            Log.i(TAG, "Image loaded, size: " + bitmap.getWidth() + " x " + bitmap.getHeight());
            ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
            bitmap.copyPixelsToBuffer(buffer);
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

            Log.d(TAG, "texture id returned: " + textureId);
            return Integer.toString(textureId);
        }

        return "null";
    }
}
