package com.mti.videodiary.utils;

import java.io.File;

/**
 * Created by Taras Matolinets on 21.03.15.
 */
public class Constants {
    /*menu activity*/
    public static final String IMAGE_HEADER = "com.video.dairy.image.header";
    public static final int UPDATE_VIDEO_ADAPTER = 22;
    public static final int UPDATE_NOTE_ADAPTER = 33;
    public static final int RESULT_LOAD_IMAGE = 133;

    /*video fragment*/
    public static final String UPDATE_ADAPTER_INTENT = "com.mti.video.dairy.update.adapter";
    public static final String KEY_POSITION = "com.mti.position.key";
    public static final String FILE_FORMAT = ".mp4";
    public static String VIDEO_FILE_NAME = File.separator + "video-dairy" + FILE_FORMAT;
    public static final String KEY_VIDEO_PATH = "com.mti.video-dairy.key-video-file-path";


    /*note fragment*/
    public static final String KEY_POSITION_NOTE = "com.mti.position.position.note";
    public static final String UPDATE_ADAPTER_NOTE = "com.video.dairy.note.update.adapter";

    /*note adapter*/
    public static final String KEY_POSITION_NOTE_ADAPTER = "com.mti.position.position.note.adapter";
}
