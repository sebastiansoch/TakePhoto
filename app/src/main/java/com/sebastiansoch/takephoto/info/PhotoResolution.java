package com.sebastiansoch.takephoto.info;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Size;

public class PhotoResolution {

    private CameraManager cameraManager;
    private String cameraId;
    private int width = 640;
    private int height = 480;

    public PhotoResolution(CameraManager cameraManager, String cameraId) {
        this.cameraManager = cameraManager;
        this.cameraId = cameraId;
    }

    public void findHighestResolution () {
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            Size[] jpegSizes = null;

            if (cameraCharacteristics != null) {
                jpegSizes = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }

            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[jpegSizes.length - 1].getWidth();
                height = jpegSizes[jpegSizes.length - 1].getHeight();
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
