package soleilcode.onepicaday;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;

/**
 * Manages the Camera.
 *
 * TODO: Use Camera2.
 */
public class CameraManager extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraManager";

    private static final int PORTRAIT_ROTATION = 90;

    /** Callback called when a picture has been taken. */
    public interface CameraCallback {
        /**
         * Called when the picture has been taken.
         *
         * @param data The picture's bytes, as JPEG
         * @param width The picture's width, in pixels
         * @param height The pictures' height, in pixels
         */
        void onPictureTaken(byte[] data, int width, int height);
    }

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;

    public CameraManager(Context context, FrameLayout preview) {
        super(context);
        preview.addView(this);
    }

    public void start() {
        if (mCamera != null) {
            return;
        }

        mCamera = Camera.open();
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(PORTRAIT_ROTATION);
        mCamera.setParameters(params);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        startPreview();
    }

    public void stop() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}

    @Nullable
    public Camera.Size getCameraSize() {
        return mCamera == null ? null : mCamera.getParameters().getPictureSize();
    }

    /**
     * Takes the picture and calls the callback upon completion. Also saves the picture on
     * external memory.
     */
    public void takePhoto(final String photoshootId, final long multiframeId,
                          @Nullable final CameraCallback callback) {
        if (mCamera != null) {
            try {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (camera != null) {
                            // Immediately restart the preview for the next picture.
                            camera.startPreview();
                        }

                        // TODO: Save the picture.

                        // Call the callback if there was one.
                        if (callback != null) {
                            Camera.Size size = getCameraSize();
                            if (size != null) {
                                callback.onPictureTaken(data, size.width, size.height);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Could not take photo.", e);
            }
        } else {
            Log.e(TAG, "Camera not found");
        }
    }

    private void startPreview() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setDisplayOrientation(PORTRAIT_ROTATION);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Can't start camera preview: ", e);
        }
    }
}
