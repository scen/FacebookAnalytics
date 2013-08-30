package com.stanleycen.facebookanalytics;

/**
 * Created by scen on 8/29/13.
 */
public class FBAttachment {
    public String id;
    public int height;
    public int width;
    public String url;
    public String previewUrl;
    public String mimeType;
    public Type type;

    public String thread;
    public String message;

    enum Type {
        IMAGE,
        STICKER
    }
}
