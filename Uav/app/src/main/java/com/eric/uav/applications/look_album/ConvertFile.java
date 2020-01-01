package com.eric.uav.applications.look_album;

import android.graphics.Bitmap;

import java.io.File;

public class ConvertFile {
    private Bitmap bitmap;
    private File file;

    public ConvertFile(File file) {
        this.file = file;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
