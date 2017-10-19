package microsoft.prototype.camerawithlib;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public enum PermissionRequest {
        CAMERA_BROADCAST(1, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});

        int requestCode;
        String permissions[];

        PermissionRequest(int requestCode, String[] permissions) {
            this.requestCode = requestCode;
            this.permissions = permissions;
        }
    }

    private SurfaceView surfaceView;
    private Button switchCameraButton;
    private Button startStopButton;

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest captureRequest;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private Handler backgroundHandler;
    private HandlerThread backgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.surfaceView = (SurfaceView) findViewById(R.id.activity_main_surface_view);
        this.surfaceView.getHolder().addCallback(this.surfaceHolderCallback);

        this.startStopButton = (Button) findViewById(R.id.activity_main_start_stop_button);
        this.startStopButton.setText("Start Preview");
        this.startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceView.getVisibility() != View.VISIBLE) {
                    if (requestCameraPermissionIfNotGranted(PermissionRequest.CAMERA_BROADCAST)) {
                        surfaceView.setVisibility(View.VISIBLE);
                    }
                } else {
                    surfaceView.setVisibility(View.GONE);
                }
            }
        });

        this.switchCameraButton = (Button) findViewById(R.id.activity_main_switch_camera_button);
        this.switchCameraButton.setText("Switch Text");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionRequest.CAMERA_BROADCAST.requestCode) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Log.i(TAG, "onRequestPermissionsResult permission denied");
                    Toast.makeText(MainActivity.this, "Permission is not granted!", Toast.LENGTH_SHORT).show();
                    this.surfaceView.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean requestCameraPermissionIfNotGranted(PermissionRequest permissionRequest) {
        boolean granted = true;

        List<String> permissionsNotGranted = new ArrayList<>();
        for (String permission : permissionRequest.permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission);
                granted = false;
            }
        }

        if (!granted) {
            ActivityCompat.requestPermissions(this,
                    permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),
                    permissionRequest.requestCode);
        }

        return granted;
    }

    private void startBackgroundThread() {
        this.backgroundThread = new HandlerThread("CameraPreview");
        this.backgroundThread.start();
        this.backgroundHandler = new Handler(this.backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        this.backgroundThread.quitSafely();

        try {
            this.backgroundThread.join();
            this.backgroundThread = null;
            this.backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        Log.i(TAG, "openCamera");
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            this.cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(this.cameraId);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            this.imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void releaseCamera() {
        if (this.cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (this.imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    private void createCameraPreview() {
        try {
            Surface surface = this.surfaceView.getHolder().getSurface();
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    MainActivity.this.cameraCaptureSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "CameraCaptureSession.StateCallback onConfigureFailed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            this.cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera();
            startBackgroundThread();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopBackgroundThread();
            releaseCamera();
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
}
