package com.sebastiansoch.takephoto.info;

import java.io.Serializable;

public class CameraSettings implements Serializable {
    private String cameraId;
    private int cameraOrientation;

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraOrientation(int cameraOrientation) {
        this.cameraOrientation = cameraOrientation;
    }

    public int getCameraOrientation() {
        return cameraOrientation;
    }
}
