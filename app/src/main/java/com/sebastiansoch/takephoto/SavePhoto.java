package com.sebastiansoch.takephoto;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.widget.Toast;

import com.sebastiansoch.takephoto.info.CameraSettings;
import com.sebastiansoch.takephoto.info.PhotoResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavePhoto extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_REQ_CODE = 1;
    private File file;

    private CameraManager cameraManager;
    private PhotoResolution photoResolution;
    private CameraSettings cameraSettings;

    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            saveImage();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    private Handler backgroundHandler;
    private HandlerThread backgroundHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cameraSettings = (CameraSettings) bundle.getSerializable("CameraSettings");
        }
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        findCameraResolution();
        checkWriteStoragePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        openCamera();
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission successfully granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "App needs to save image to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Toast.makeText(this, "SS: DUPA nie ustawilo CameraDevice", Toast.LENGTH_SHORT).show();
                return;
            }
            cameraManager.openCamera(cameraSettings.getCameraId(), stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void saveImage() {
        if (cameraDevice == null) {
            return;
        }

        try {
            final ImageReader imageReader = ImageReader.newInstance(photoResolution.getWidth(), photoResolution.getHeight(), ImageFormat.JPEG, 1);
            List<Surface> outputSurface = new ArrayList<>(1);
            outputSurface.add(imageReader.getSurface());

            final CaptureRequest.Builder captureRequestBuilder;

            try {
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Toast.makeText(this, "TakePhoto app required access to camera", Toast.LENGTH_SHORT).show();
                return;
            }

            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, cameraSettings.getCameraOrientation());

            file = createPhotoFileName();

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    try {
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                    } finally {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                }
            };

            imageReader.setOnImageAvailableListener(readerListener, backgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }
            };


            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureRequestBuilder.build(), captureListener, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private File createPhotoFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = timestamp + ".jpg";
        String pathname = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        pathname = pathname.concat("/TakePhoto");
        File path = new File(pathname);
        if (!path.exists()) {
            path.mkdirs();
        }
        return new File(pathname + File.separator + filename);
    }

    private void findCameraResolution() {
        if (cameraManager != null && cameraSettings != null) {
            photoResolution = new PhotoResolution(cameraManager, cameraSettings.getCameraId());
            photoResolution.findHighestResolution();
        } else {
            Toast.makeText(this, "Could not find camera, check camera preview ", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }

    private void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("Save Photo");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundHandlerThread.quitSafely();
        try {
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
